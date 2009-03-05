/*
 * Copyright 2004-2009 the original author or authors.
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

package org.compass.core.test.boost.property;

import org.compass.core.config.CompassConfiguration;
import static org.compass.core.mapping.osem.builder.OSEM.*;

/**
 * @author kimchy
 */
public class BoostPropertyBuilderTests extends BoostPropertyTests {

    @Override
    protected String[] getMappings() {
        return new String[0];
    }

    @Override
    protected void addExtraConf(CompassConfiguration conf) {
        conf.addMapping(
                searchable(A.class).alias("a1")
                        .add(id("id").accessor("field"))
                        .add(boost("value1").accessor("field"))
                        .add(property("value1").accessor("field").add(metadata("value1")))
                        .add(property("value2").accessor("field").add(metadata("value2")))
        );
    }
}