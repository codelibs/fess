/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class StreamUtil {
    @SafeVarargs
    public static <T> Stream<T> of(final T... values) {
        if (values != null) {
            return Arrays.stream(values);
        } else {
            return Collections.<T> emptyList().stream();
        }
    }

    public static Stream<String> splitOf(final String value, final String regex) {
        if (value != null) {
            return Arrays.stream(value.split(regex));
        } else {
            return Collections.<String> emptyList().stream();
        }
    }

    public static <K, V> Stream<Map.Entry<K, V>> of(final Map<K, V> map) {
        if (map != null) {
            return map.entrySet().stream();
        } else {
            return Collections.<K, V> emptyMap().entrySet().stream();
        }
    }

}
