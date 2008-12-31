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

package org.compass.core.util.proxy.extractor;

import org.compass.core.CompassException;
import org.compass.core.config.CompassSettings;
import org.springframework.aop.support.AopUtils;

/**
 * Uses {@link org.springframework.aop.support.AopUtils#getTargetClass(Object)} in order to get the
 * wrapped class from the object.
 *
 * @author kimchy
 */
public class SpringProxyExtractor implements ProxyExtractor {

    public void configure(CompassSettings settings) throws CompassException {
        // do nothing
    }

    public Class getTargetClass(Object obj) {
        return AopUtils.getTargetClass(obj);
    }

    public void initalizeProxy(Object obj) {
        // nothing to do
    }
}
