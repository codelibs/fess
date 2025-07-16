/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.codelibs.core.beans.util.BeanUtil;
import org.dbflute.Entity;
import org.lastaflute.web.response.render.RenderData;

/**
 * Utility class for managing render data in web responses.
 * This class provides methods to register values in RenderData objects with proper handling
 * of Entity objects and Collections that may contain Entity objects.
 *
 */
public class RenderDataUtil {

    /**
     * Default constructor.
     * This constructor is provided for utility class instantiation, though this class
     * is designed to be used statically.
     */
    public RenderDataUtil() {
        // Default constructor
    }

    /**
     * Registers a value in the render data with the specified key.
     * If the value is an Entity object, it will be converted to a Map using BeanUtil.
     * If the value is a Collection containing Entity objects, each Entity will be converted to a Map.
     * For other types of values, they are registered directly.
     *
     * @param data the RenderData object to register the value in
     * @param key the key to associate with the value
     * @param value the value to register; can be null, Entity, Collection, or any other object
     */
    public static void register(final RenderData data, final String key, final Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof Entity) {
            data.register(key, BeanUtil.copyBeanToNewMap(value));
        } else {
            if (value instanceof final Collection<?> coll && !coll.isEmpty()) {
                // care performance for List that the most frequent pattern
                final Object first = coll instanceof List<?> ? ((List<?>) coll).get(0) : coll.iterator().next();
                if (first instanceof Entity) {
                    data.register(key, coll.stream().map(BeanUtil::copyBeanToNewMap).collect(Collectors.toList()));
                    return;
                }
            }
            data.register(key, value);
        }
    }

}
