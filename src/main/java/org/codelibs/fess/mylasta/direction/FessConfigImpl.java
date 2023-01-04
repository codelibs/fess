/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.mylasta.direction;

import java.util.concurrent.ExecutionException;

import org.codelibs.fess.Constants;
import org.dbflute.helper.jprop.ObjectiveProperties;
import org.lastaflute.core.direction.PropertyFilter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class FessConfigImpl extends FessConfig.SimpleImpl {

    private static final long serialVersionUID = 1L;

    @Override
    protected ObjectiveProperties newObjectiveProperties(final String resourcePath, final PropertyFilter propertyFilter) {
        return new ObjectiveProperties(resourcePath) { // for e.g. checking existence and filtering value
            Cache<String, String> cache = CacheBuilder.newBuilder().build();

            @Override
            public String get(final String propertyKey) {
                final String plainValue = getFromCache(propertyKey);
                final String filteredValue = propertyFilter.filter(propertyKey, plainValue);
                verifyPropertyValue(propertyKey, filteredValue); // null checked
                return filterPropertyAsDefault(filteredValue); // not null here
            }

            private String getFromCache(final String propertyKey) {
                try {
                    return cache.get(propertyKey,
                            () -> System.getProperty(Constants.FESS_CONFIG_PREFIX + propertyKey, super.get(propertyKey)));
                } catch (final ExecutionException e) {
                    return super.get(propertyKey);
                }
            }
        };
    }
}
