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
package org.codelibs.fess.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.codelibs.core.beans.util.BeanUtil;
import org.dbflute.Entity;
import org.lastaflute.web.response.render.RenderData;

public class RenderDataUtil {

    public static void register(final RenderData data, final String key, final Object value) {
        if (value == null) {
            return;
        }

        if (value instanceof Entity) {
            data.register(key, BeanUtil.copyBeanToNewMap(value));
        } else {
            if ((value instanceof final Collection<?> coll) && !coll.isEmpty()) {
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
