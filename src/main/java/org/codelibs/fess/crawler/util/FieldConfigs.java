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
package org.codelibs.fess.crawler.util;

import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.dbflute.optional.OptionalThing;

/**
 * Utility class for managing field configurations with parameter mappings.
 * This class provides functionality to retrieve and manage field-specific configurations
 * from a parameter map.
 */
public class FieldConfigs {

    /**
     * Map containing field names as keys and their corresponding configuration values as values.
     */
    private final Map<String, String> params;

    /**
     * Constructs a new FieldConfigs instance with the specified parameter map.
     *
     * @param params the map containing field names as keys and configuration values as values
     */
    public FieldConfigs(final Map<String, String> params) {
        this.params = params;
    }

    /**
     * Retrieves the configuration for the specified field name.
     *
     * @param fieldName the name of the field to get configuration for
     * @return an OptionalThing containing the Config if the field exists and has a non-blank value,
     *         otherwise an empty OptionalThing
     */
    public OptionalThing<Config> getConfig(final String fieldName) {
        final String value = params.get(fieldName);
        if (StringUtil.isNotBlank(value)) {
            return OptionalThing.of(new Config(value));
        }
        return OptionalThing.empty();
    }

    /**
     * Configuration class that holds parsed configuration values for a field.
     * This class parses pipe-separated configuration values and provides methods
     * to check for specific configuration options.
     */
    public static class Config {

        /**
         * Array of parsed configuration values split by pipe character and trimmed.
         */
        private final String[] values;

        /**
         * Constructs a new Config instance by parsing the provided configuration value.
         * The value is split by pipe character (|) and each part is trimmed.
         *
         * @param value the configuration value string to parse
         */
        public Config(final String value) {
            values = StreamUtil.split(value, Pattern.quote("|")).get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
        }

        /**
         * Checks if the cache option is enabled in the configuration.
         * Returns true if "cache" is present in the values or if there's a single "true" value
         * for backward compatibility.
         *
         * @return true if caching is enabled, false otherwise
         */
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

        /**
         * Checks if the overwrite option is enabled in the configuration.
         * Returns true if "overwrite" is present in the values.
         *
         * @return true if overwriting is enabled, false otherwise
         */
        public boolean isOverwrite() {
            for (final String value : values) {
                if ("overwrite".equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the array of parsed configuration values.
         *
         * @return the array of configuration values
         */
        public String[] getValues() {
            return values;
        }
    }
}
