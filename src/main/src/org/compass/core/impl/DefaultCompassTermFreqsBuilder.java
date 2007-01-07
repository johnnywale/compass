/*
 * Copyright 2004-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.core.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.compass.core.CompassTermFreq;
import org.compass.core.CompassTermFreqsBuilder;
import org.compass.core.engine.SearchEngineInternalSearch;
import org.compass.core.engine.SearchEngineTermFrequencies;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.spi.InternalCompassSession;

/**
 * @author kimchy
 */
public class DefaultCompassTermFreqsBuilder implements CompassTermFreqsBuilder {

    private InternalCompassSession session;

    private String[] propertyNames;

    private int size;

    private String[] aliases;

    private String[] subIndexes;

    private int minNorm = -1;

    private int maxNorm = -1;

    private Sort sort = Sort.FREQ;

    public DefaultCompassTermFreqsBuilder(InternalCompassSession session, String[] names) {
        this.session = session;
        this.propertyNames = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            CompassMapping.ResourcePropertyLookup lookup = session.getMapping().getResourcePropertyLookup(names[i]);
            this.propertyNames[i] = lookup.getPath();
        }
        this.size = 10;
    }

    public CompassTermFreqsBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public CompassTermFreqsBuilder setAliases(String[] aliases) {
        this.aliases = aliases;
        return this;
    }

    public CompassTermFreqsBuilder setSubIndexes(String[] subIndexes) {
        this.subIndexes = subIndexes;
        return this;
    }

    public CompassTermFreqsBuilder normalize(int min, int max) {
        this.minNorm = min;
        this.maxNorm = max;
        return this;
    }

    public CompassTermFreqsBuilder setSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public CompassTermFreq[] toTermFreqs() {
        SearchEngineInternalSearch internalSearch = session.getSearchEngine().internalSearch(subIndexes, aliases);
        SearchEngineTermFrequencies seTermFreqs = session.getSearchEngine().termFreq(propertyNames, size, internalSearch);
        CompassTermFreq[] termFreqs = seTermFreqs.getTerms();
        if (sort == Sort.TERM) {
            List list = Arrays.asList(termFreqs);
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((CompassTermFreq) o1).getTerm().compareTo(((CompassTermFreq) o2).getTerm());
                }
            });
            termFreqs = (CompassTermFreq[]) list.toArray(new CompassTermFreq[list.size()]);
        }
        if (minNorm != -1 && maxNorm != -1) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < termFreqs.length; i++) {
                if (termFreqs[i].getFreq() < min) {
                    min = (int) termFreqs[i].getFreq();
                }
                if (termFreqs[i].getFreq() > max) {
                    max = (int) termFreqs[i].getFreq();
                }
            }
            for (int i = 0; i < termFreqs.length; i++) {
                float freq;
                if ((int) termFreqs[i].getFreq() == min) {
                    freq = minNorm;
                } else if ((int) termFreqs[i].getFreq() == max) {
                    freq = maxNorm;
                } else {
                    freq = minNorm + (termFreqs[i].getFreq() - min) / (max - min) * (maxNorm - minNorm);
                }
                ((DefaultCompassTermFreq) termFreqs[i]).setFreq(freq);
            }
        }
        return termFreqs;
    }
}
