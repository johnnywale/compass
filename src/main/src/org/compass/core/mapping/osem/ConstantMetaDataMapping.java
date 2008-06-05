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

package org.compass.core.mapping.osem;

import java.util.ArrayList;
import java.util.Iterator;

import org.compass.core.mapping.Mapping;
import org.compass.core.mapping.OverrideByNameMapping;
import org.compass.core.mapping.support.AbstractResourcePropertyMapping;

/**
 * 
 * @author kimchy
 * 
 */

public class ConstantMetaDataMapping extends AbstractResourcePropertyMapping
        implements OverrideByNameMapping, OsemMapping {

    private ArrayList metaDataValues = new ArrayList();
    
    private boolean overrideByName;

    public Mapping copy() {
        ConstantMetaDataMapping copy = new ConstantMetaDataMapping();
        super.copy(copy);
        copy.setOverrideByName(isOverrideByName());
        for (Iterator it = metaDataValuesIt(); it.hasNext();) {
            copy.addMetaDataValue((String) it.next());
        }
        return copy;
    }

    public boolean hasAccessors() {
        return false;
    }

    public void addMetaDataValue(String value) {
        metaDataValues.add(value);
    }

    public Iterator metaDataValuesIt() {
        return metaDataValues.iterator();
    }

    public boolean isOverrideByName() {
        return overrideByName;
    }

    public void setOverrideByName(boolean overrideByName) {
        this.overrideByName = overrideByName;
    }
}
