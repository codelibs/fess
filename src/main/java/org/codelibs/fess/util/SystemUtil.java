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

import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;

/**
 * This class provides system-related utility methods.
 * It extends {@link org.codelibs.core.lang.SystemUtil} and adds
 * methods specific to the Fess application.
 */
public class SystemUtil extends org.codelibs.core.lang.SystemUtil {

    private static final String DEFAULT_SENSITIVE_PATTERN = ".*password.*|.*secret.*|.*key.*|.*token.*|.*credential.*|.*auth.*|.*private.*";

    private static volatile Pattern sensitivePattern;

    /**
     * Private constructor to prevent instantiation.
     */
    private SystemUtil() {
    }

    /**
     * Gets the HTTP address of the search engine.
     *
     * @return The search engine HTTP address.
     */
    public static String getSearchEngineHttpAddress() {
        return System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
    }

    /**
     * Determines whether this JVM is the main webapp process, as opposed to one of the short-lived
     * child processes (crawler, thumbnail, suggest and chunk) that also boot the same container to
     * run a single job and exit. Each child process is started with its own
     * {@code -Dfess.<type>.process=true} system property; the webapp process sets none of them.
     *
     * @return {@code true} when none of the job-process markers is set
     */
    public static boolean isWebappProcess() {
        return !Stream
                .of(Constants.EXECUTE_TYPE_CRAWLER, Constants.EXECUTE_TYPE_THUMBNAIL, Constants.EXECUTE_TYPE_SUGGEST,
                        Constants.EXECUTE_TYPE_CHUNK)
                .anyMatch(type -> Constants.TRUE.equalsIgnoreCase(System.getProperty("fess." + type + ".process")));
    }

    /**
     * Gets the compiled pattern for matching sensitive property/environment variable keys.
     * The pattern is read from the system property 'app.log.sensitive.property.pattern'.
     * If not set, a default pattern matching common sensitive key names is used.
     *
     * @return The compiled Pattern for sensitive key matching
     */
    private static Pattern getSensitivePattern() {
        if (sensitivePattern == null) {
            synchronized (SystemUtil.class) {
                if (sensitivePattern == null) {
                    final String patternStr = System.getProperty(FessConfig.APP_LOG_SENSITIVE_PROPERTY_PATTERN, DEFAULT_SENSITIVE_PATTERN);
                    sensitivePattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                }
            }
        }
        return sensitivePattern;
    }

    /**
     * Masks sensitive values for logging purposes.
     * Keys matching the pattern defined in 'app.log.sensitive.property.pattern' system property
     * will have their values replaced with "********".
     *
     * @param key The key name to check
     * @param value The value to potentially mask
     * @return The masked value if the key matches a sensitive pattern, otherwise the original value
     */
    public static String maskSensitiveValue(final String key, final String value) {
        if (key == null || value == null) {
            return value;
        }
        if (getSensitivePattern().matcher(key).matches()) {
            return "********";
        }
        return value;
    }
}
