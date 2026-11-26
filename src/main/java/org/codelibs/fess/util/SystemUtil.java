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

import java.util.regex.Matcher;
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
     * A {@code -D<name>=<value>} JVM option inside a value such as {@code FESS_JAVA_OPTS} or {@code JDK_JAVA_OPTIONS}.
     * The first alternative is an option inside quotes ({@code "-Dname=a b"}): groups 1 (the quote), 2 (the name),
     * 3 (the value) and 4 (the closing quote, empty when there is none). The second is an option whose value
     * may hold quoted parts ({@code -Dname="a b"}): groups 5 (the name) and 6 (the value). A quote that is not
     * closed runs to the end of the text, so the rest of the text is taken as the value.
     */
    private static final Pattern JVM_PROPERTY_OPTION = Pattern.compile(
            "([\"'])-D([^=\\s\"']+)=(.*?)(\\1|\\z)|-D([^=\\s\"']+)=((?:\"[^\"]*(?:\"|\\z)|'[^']*(?:'|\\z)|[^\\s\"'])*)", Pattern.DOTALL);

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
        if (ConfigEnvUtil.isConfigEnvName(key) || getSensitivePattern().matcher(key).matches()) {
            return "********";
        }
        return maskSensitiveJvmOptions(value);
    }

    /**
     * Masks the value of one JVM argument when it is a sensitive {@code -D<name>=<value>} option: everything
     * after the first {@code =} is replaced, so a value that holds spaces is masked whole. An option is
     * sensitive when its name matches the same pattern as {@link #maskSensitiveValue(String, String)}.
     *
     * @param arg one JVM argument, as {@code RuntimeMXBean#getInputArguments()} returns it, may be {@code null}
     * @return the argument with the value replaced with "********" when the option is sensitive and not empty
     */
    public static String maskSensitiveJvmArgument(final String arg) {
        if (arg == null || !arg.startsWith("-D")) {
            return arg;
        }
        final int pos = arg.indexOf('=');
        if (pos < 0 || pos == arg.length() - 1) {
            return arg;
        }
        final String name = arg.substring(2, pos);
        if (!getSensitivePattern().matcher(name).matches()) {
            return arg;
        }
        return "-D" + name + "=********";
    }

    /**
     * Masks the values of the sensitive {@code -D<name>=<value>} options in a value that carries JVM options,
     * such as the environment variable {@code FESS_JAVA_OPTS}; the options are taken as separated by white
     * space, as the start script splits the variable. A value in quotes, or an option in quotes, is masked up
     * to its closing quote, since a value such as {@code JDK_JAVA_OPTIONS} may hold a quoted value with spaces; a
     * quote that is not closed masks the rest of the text. An option is
     * sensitive when its name matches the same pattern as {@link #maskSensitiveValue(String, String)}.
     * The DEBUG dump of a child process prints its environment; without this a secret given as
     * {@code -Dfess.config.<key>} is printed there although its property line is masked.
     *
     * @param text a value holding JVM options, may be {@code null}
     * @return the text with the value of every sensitive option replaced with "********"
     */
    public static String maskSensitiveJvmOptions(final String text) {
        if (text == null || !text.contains("-D")) {
            return text;
        }
        final Matcher matcher = JVM_PROPERTY_OPTION.matcher(text);
        final StringBuilder buf = new StringBuilder(text.length());
        while (matcher.find()) {
            final boolean quoted = matcher.group(1) != null;
            final String name = quoted ? matcher.group(2) : matcher.group(5);
            final String value = quoted ? matcher.group(3) : matcher.group(6);
            if (value.isEmpty()) {
                matcher.appendReplacement(buf, Matcher.quoteReplacement(matcher.group()));
            } else if (!getSensitivePattern().matcher(name).matches()) {
                // A quote that is not closed (it's) takes the rest of the text as the value, so the options in it are masked too.
                final String inner = maskSensitiveJvmOptions(value);
                matcher.appendReplacement(buf, Matcher.quoteReplacement(
                        quoted ? matcher.group(1) + "-D" + name + "=" + inner + matcher.group(4) : "-D" + name + "=" + inner));
            } else if (quoted) {
                matcher.appendReplacement(buf, Matcher.quoteReplacement(matcher.group(1) + "-D" + name + "=********" + matcher.group(4)));
            } else {
                matcher.appendReplacement(buf, Matcher.quoteReplacement("-D" + name + "=********"));
            }
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
}
