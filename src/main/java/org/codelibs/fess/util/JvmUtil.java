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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for JVM-related operations.
 * This class provides methods for handling JVM options and version detection.
 */
public final class JvmUtil {
    private static final Pattern VERSION_PREFIX_PATTERN = Pattern.compile("([0-9]+)(\\-?):(.*)");

    private JvmUtil() {
        // nothing
    }

    /**
     * Filters JVM options based on the current Java version.
     * Options can be prefixed with version numbers to specify compatibility.
     * Format: "version:option" or "version-:option" (for version and above).
     *
     * @param values the array of JVM options to filter
     * @return the filtered array of JVM options applicable to the current Java version
     */
    public static String[] filterJvmOptions(final String[] values) {
        final int version = getJavaVersion();
        return Arrays.stream(values).map(s -> {
            final Matcher matcher = VERSION_PREFIX_PATTERN.matcher(s);
            if (!matcher.matches()) {
                return s;
            }
            final int v = Integer.parseInt(matcher.group(1));
            if ("-".equals(matcher.group(2))) {
                if (version >= v) {
                    return matcher.group(3);
                }
            } else if (v == version) {
                return matcher.group(3);
            }
            return null;
        }).filter(s -> s != null).toArray(n -> new String[n]);
    }

    /**
     * Gets the major version number of the current Java runtime.
     * For Java 8 and below, returns the minor version (e.g., 8 for Java 1.8).
     * For Java 9 and above, returns the major version (e.g., 11 for Java 11).
     *
     * @return the Java version number, defaults to 8 if version cannot be determined
     */
    public static int getJavaVersion() {
        final String javaVersion = System.getProperty("java.version");
        int version = 8;
        if (javaVersion != null) {
            final String[] split = javaVersion.split("[\\._]");
            if (split.length > 0) {
                version = Integer.parseInt(split[0]);
                if (version == 1 && split.length > 1) {
                    version = Integer.parseInt(split[1]);
                }
            }
        }
        return version;
    }
}
