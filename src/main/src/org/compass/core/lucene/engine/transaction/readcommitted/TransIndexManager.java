/*
 * Copyright 2004-2008 the original author or authors.
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

package org.compass.core.lucene.engine.transaction.readcommitted;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.compass.core.CompassException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.lucene.engine.LuceneSearchEngineFactory;
import org.compass.core.spi.ResourceKey;

/**
 * @author kimchy
 */
public class TransIndexManager implements CompassConfigurable {

    private final boolean concurrent;

    private final LuceneSearchEngineFactory searchEngineFactory;

    private CompassSettings settings;

    private final Map<String, TransIndex> transIndexMap;

    public TransIndexManager(LuceneSearchEngineFactory searchEngineFactory, boolean concurrent) {
        this.searchEngineFactory = searchEngineFactory;
        this.concurrent = concurrent;
        if (concurrent) {
            transIndexMap = new ConcurrentHashMap<String, TransIndex>();
        } else {
            transIndexMap = new HashMap<String, TransIndex>();
        }
    }

    public void configure(CompassSettings settings) throws CompassException {
        this.settings = settings;
    }

    public void create(Document document, ResourceKey resourceKey, Analyzer analyzer) throws IOException {
        getTransIndex(resourceKey.getSubIndex()).create(document, analyzer);
    }

    public void update(Document document, ResourceKey resourceKey, Analyzer analyzer) throws IOException {
        getTransIndex(resourceKey.getSubIndex()).update(document, resourceKey, analyzer);
    }

    public void delete(ResourceKey resourceKey) throws IOException {
        // no need to delete anything if we don't have a transactional index
        if (!transIndexMap.containsKey(resourceKey.getSubIndex())) {
            return;
        }
        getTransIndex(resourceKey.getSubIndex()).delete(resourceKey);
    }

    public void delete(Query query, String subIndex) throws IOException {
        // no need to delete anything if we don't have a transactional index
        if (!transIndexMap.containsKey(subIndex)) {
            return;
        }
        getTransIndex(subIndex).delete(query);
    }

    public IndexReader getReader(String subIndex) throws IOException {
        return transIndexMap.get(subIndex).getReader();
    }

    public IndexSearcher getSearcher(String subIndex) throws IOException {
        return transIndexMap.get(subIndex).getSearcher();
    }

    public Directory getDirectory(String subIndex) {
        return transIndexMap.get(subIndex).getDirectory();
    }

    public boolean hasTransactions() {
        return !transIndexMap.isEmpty();
    }

    public boolean hasTransIndex(String subIndex) {
        return transIndexMap.containsKey(subIndex);
    }

    public void commit() throws IOException {
        for (TransIndex transIndex : transIndexMap.values()) {
            transIndex.commit();
        }
    }

    public void commit(String subIndex) throws IOException {
        transIndexMap.get(subIndex).commit();
    }

    public void close(String subIndex) throws IOException {
        TransIndex transIndex = transIndexMap.remove(subIndex);
        if (transIndex != null) {
            transIndex.close();
        }
    }

    private TransIndex getTransIndex(String subIndex) {
        TransIndex transIndex = transIndexMap.get(subIndex);
        if (transIndex == null) {
            if (concurrent) {
                synchronized (transIndexMap) {
                    transIndex = transIndexMap.get(subIndex);
                    if (transIndex == null) {
                        transIndex = new TransIndex(searchEngineFactory, subIndex, concurrent);
                        transIndex.configure(settings);
                        transIndexMap.put(subIndex, transIndex);
                    }
                }
            } else {
                transIndex = new TransIndex(searchEngineFactory, subIndex, concurrent);
                transIndex.configure(settings);
                transIndexMap.put(subIndex, transIndex);
            }
        }
        return transIndex;
    }
}
