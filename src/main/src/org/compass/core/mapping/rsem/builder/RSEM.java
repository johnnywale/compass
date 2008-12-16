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

package org.compass.core.mapping.rsem.builder;

/**
 * Static builder allowing to construct RSEM (Resource to Search Engine Mapping)
 * definitions.
 *
 * <p>Here is an exmaple how it can be used:
 *
 * <p><pre>
 * import static org.compass.core.mapping.rsem.builder.RSEM.*;
 *
 *
 * conf.addResourceMapping(
 *          resource("a")
 *              .add(id("id"))
 *              .add(property("value1"))
 *              .add(property("value2").store(Property.Store.YES).index(Property.Index.ANALYZED))
 *              .add(property("value3").store(Property.Store.COMPRESS).index(Property.Index.ANALYZED))
 *              .add(property("value4").store(Property.Store.YES).index(Property.Index.NOT_ANALYZED))
 *              .add(property("value5").store(Property.Store.YES).index(Property.Index.ANALYZED).converter("mydate"))
 *              .add(property("value6"))
 * );
 * </pre>
 *
 * @author kimchy
 */
public abstract class RSEM {

    private RSEM() {

    }

    public static ResourceContractMappingBuilder contract(String alias) {
        return new ResourceContractMappingBuilder(alias);
    }

    /**
     * Constructs a new resource based mapping for the specific alias. Note, at least one
     * id mapping must be added to the resource mapping.
     */
    public static ResourceMappingBuilder resource(String alias) {
        return new ResourceMappingBuilder(alias);
    }

    public static ResourceIdMappingBuilder id(String name) {
        return new ResourceIdMappingBuilder(name);
    }

    public static ResourcePropertyMappingBuilder property(String name) {
        return new ResourcePropertyMappingBuilder(name);
    }

    public static ResourceAnalyzerMappingBuilder analyzer(String name) {
        return new ResourceAnalyzerMappingBuilder(name);
    }

    public static ResourceBoostMappingBuilder boost(String name) {
        return new ResourceBoostMappingBuilder(name);
    }

    public static ResourceAllMappingBuilder all() {
        return new ResourceAllMappingBuilder();
    }
}
