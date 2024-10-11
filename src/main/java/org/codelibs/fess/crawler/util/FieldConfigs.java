/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.util;

import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.dbflute.optional.OptionalThing;

public class FieldConfigs {

    private final Map<String, String> params;

    public FieldConfigs(final Map<String, String> params) {
        this.params = params;
    }

    public OptionalThing<Config> getConfig(final String fieldName) {
        final String value = params.get(fieldName);
        if (StringUtil.isNotBlank(value)) {
            return OptionalThing.of(new Config(value));
        }
        return OptionalThing.empty();
    }

    public static class Config {

        private final String[] values;

        public Config(final String value) {
            values = StreamUtil.split(value, Pattern.quote("|")).get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
        }

        public boolean isCache() {
            for (final String value : values) {
                if ("cache".equalsIgnoreCase(value)) {
                    return true;
                }
            }
            // backward compatibility
            if (values.length == 1 && Constants.TRUE.equalsIgnoreCase(values[0])) {
                return true;
            }
            return false;
        }

        public boolean isOverwrite() {
            for (final String value : values) {
                if ("overwrite".equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        public String[] getValues() {
            return values;
        }
    }
}
