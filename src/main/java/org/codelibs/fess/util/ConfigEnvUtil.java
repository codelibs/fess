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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;

import org.codelibs.fess.Constants;

/**
 * Reads a setting that is given as {@code -Dfess.config.<key>} or {@code -Dfess.system.<key>} from an
 * environment variable as well.
 *
 * <p>A JVM option is part of the command line that {@code ps} and {@code docker top} print, and Fess
 * repeats the {@code -Dfess.config.*} and {@code -Dfess.system.*} options on the command line of
 * every crawler, suggest, thumbnail and chunk process it starts. The environment of a process is not
 * shown there, and a child process inherits it, so a secret is given by the variable.
 *
 * <p>The name of the variable is made from the key: the prefix {@code FESS_CONFIG_} or {@code FESS_SYSTEM_},
 * then the key in upper case with every character other than a letter or a digit replaced by an underscore,
 * so {@code search_engine.password} is {@code FESS_CONFIG_SEARCH_ENGINE_PASSWORD} and
 * {@code storage.secretkey} is {@code FESS_SYSTEM_STORAGE_SECRETKEY}. An empty variable counts as unset.
 *
 * <p>This class has no dependency on the web application or on the container, because the crawler,
 * suggest, thumbnail and chunk processes load the classes that use it and run without the servlet API.
 */
public class ConfigEnvUtil {

    /** The prefix of the variable of a {@code fess_config.properties} key. */
    public static final String CONFIG_ENV_PREFIX = "FESS_CONFIG_";

    /** The prefix of the variable of a {@code system.properties} key. */
    public static final String SYSTEM_ENV_PREFIX = "FESS_SYSTEM_";

    private static volatile UnaryOperator<String> envReader = System::getenv;

    /** The value of the variable of a key ({@link #NO_VALUE} when unset): the environment of a running JVM does not change. */
    private static final ConcurrentMap<String, String> ENV_CACHE = new ConcurrentHashMap<>();

    private static final String NO_VALUE = "";

    private ConfigEnvUtil() {
    }

    /**
     * Replaces the reader of the environment, for a test.
     *
     * @param reader the reader, or null for the environment of the process
     */
    static void setEnvReader(final UnaryOperator<String> reader) {
        envReader = reader != null ? reader : System::getenv;
        ENV_CACHE.clear();
    }

    /**
     * Gets the name of the variable of a key.
     *
     * @param jvmPrefix {@link Constants#FESS_CONFIG_PREFIX} or {@link Constants#SYSTEM_PROP_PREFIX}
     * @param key the key of the setting
     * @return the name of the variable
     */
    public static String toEnvName(final String jvmPrefix, final String key) {
        final String envPrefix = Constants.SYSTEM_PROP_PREFIX.equals(jvmPrefix) ? SYSTEM_ENV_PREFIX : CONFIG_ENV_PREFIX;
        return envPrefix + key.replaceAll("[^A-Za-z0-9]", "_").toUpperCase(Locale.ROOT);
    }

    /**
     * Gets the value of the variable of a key.
     *
     * @param jvmPrefix {@link Constants#FESS_CONFIG_PREFIX} or {@link Constants#SYSTEM_PROP_PREFIX}
     * @param key the key of the setting
     * @return the value, or null when the variable is not set or is empty
     */
    public static String getEnv(final String jvmPrefix, final String key) {
        final String value = ENV_CACHE.computeIfAbsent(jvmPrefix + key, k -> {
            final String v = envReader.apply(toEnvName(jvmPrefix, key));
            return v == null || v.isEmpty() ? NO_VALUE : v;
        });
        return value.isEmpty() ? null : value;
    }

    /**
     * Gets the value that is given to a setting from outside the configuration files: the
     * {@code -D<jvmPrefix><key>} option when it has a value, otherwise the variable of the key. An empty option
     * does not hide the variable (a compose file that passes {@code -D...api.key=${KEY:-}} gives one when KEY is unset); with
     * no variable it stays empty, so that it still overrides the file.
     *
     * @param jvmPrefix {@link Constants#FESS_CONFIG_PREFIX} or {@link Constants#SYSTEM_PROP_PREFIX}
     * @param key the key of the setting
     * @return the value, or null when neither is given
     */
    public static String getOverride(final String jvmPrefix, final String key) {
        final String value = System.getProperty(jvmPrefix + key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        final String env = getEnv(jvmPrefix, key);
        return env != null ? env : value;
    }

    /**
     * Tells whether a name is the variable of a setting, whose value is not shown.
     *
     * @param name the name of an environment variable
     * @return true if it starts with {@code FESS_CONFIG_} or {@code FESS_SYSTEM_}
     */
    public static boolean isConfigEnvName(final String name) {
        return name != null && (name.startsWith(CONFIG_ENV_PREFIX) || name.startsWith(SYSTEM_ENV_PREFIX));
    }

    /**
     * Describes each {@code -Dfess.config.*} and {@code -Dfess.system.*} system property that carries a
     * secret, so that it can be warned about. The value is not part of the message. An SSO client id is hidden
     * in System Info, like the client secret, but it is not a secret: it is sent in the URL of the login redirect.
     *
     * @return one message per such property, empty when there is none
     */
    public static List<String> secretsOnCommandLine() {
        final List<String> messages = new ArrayList<>();
        for (final String jvmPrefix : new String[] { Constants.FESS_CONFIG_PREFIX, Constants.SYSTEM_PROP_PREFIX }) {
            System.getProperties().stringPropertyNames().stream().sorted().filter(name -> name.startsWith(jvmPrefix)).forEach(name -> {
                final String key = name.substring(jvmPrefix.length());
                if (SecretKeyUtil.isSecretKey(key) && !key.endsWith(".client.id") && !System.getProperty(name, "").isEmpty()) {
                    messages.add("The secret " + key + " is given as the JVM option -D" + name
                            + ", which the process list shows (ps, docker top) and which is repeated on the command line of the"
                            + " crawler, suggest, thumbnail and chunk processes. Set the environment variable " + toEnvName(jvmPrefix, key)
                            + " instead and remove the option.");
                }
            });
        }
        return messages;
    }
}
