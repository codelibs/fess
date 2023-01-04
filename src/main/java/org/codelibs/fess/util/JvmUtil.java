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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JvmUtil {
    private static final Pattern VERSION_PREFIX_PATTERN = Pattern.compile("([0-9]+)(\\-?):(.*)");

    private JvmUtil() {
        // nothing
    }

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
