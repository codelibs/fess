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
package org.codelibs.fess.mylasta.direction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.dbflute.helper.jprop.ObjectiveProperties;
import org.lastaflute.core.direction.PropertyFilter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class FessConfigImpl extends FessConfig.SimpleImpl {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(FessConfigImpl.class);

    /**
     * The configuration defaults this build ships. The installed fess_config.properties keeps
     * whatever the installation put there - the packages treat it as a configuration file and do
     * not replace it on upgrade - so it can lack keys a newer version reads. These are the answer
     * for those keys. The name differs from fess_config.properties on purpose, so that this copy
     * survives the packaging rules that hold the installed file back.
     */
    protected static final String DEFAULT_CONFIG_PATH = "fess_config_default.properties";

    private static class KeyNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;

        private KeyNotFoundException(final String key) {
            super(key);
        }
    }

    /** Reads {@link #DEFAULT_CONFIG_PATH} once, on the first configuration property that needs it. */
    private static final class DefaultConfigHolder {

        private static final Properties DEFAULT_CONFIG = loadDefaultConfig();

        private static Properties loadDefaultConfig() {
            final Properties props = new Properties();
            try (InputStream in = FessConfigImpl.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH)) {
                if (in == null) {
                    logger.warn("{} is not on the classpath. A configuration property missing from"
                            + " fess_config.properties will fail instead of falling back to its default.", DEFAULT_CONFIG_PATH);
                    return props;
                }
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            } catch (final IOException e) {
                logger.warn("Failed to read {}.", DEFAULT_CONFIG_PATH, e);
            }
            return props;
        }

        private DefaultConfigHolder() {
        }
    }

    @Override
    protected ObjectiveProperties newObjectiveProperties(final String resourcePath, final PropertyFilter propertyFilter) {
        return new ObjectiveProperties(resourcePath) { // for e.g. checking existence and filtering value
            Cache<String, String> cache = CacheBuilder.newBuilder().build();

            @Override
            public String get(final String propertyKey) {
                final String plainValue = getFromCache(propertyKey);
                final String filteredValue = propertyFilter.filter(propertyKey, plainValue);
                verifyPropertyValue(propertyKey, filteredValue); // null checked
                return filterPropertyAsDefault(filteredValue); // not null here
            }

            private String getFromCache(final String propertyKey) {
                try {
                    return cache.get(propertyKey, () -> {
                        final String value = System.getProperty(Constants.FESS_CONFIG_PREFIX + propertyKey, super.get(propertyKey));
                        if (value != null) {
                            return value;
                        }
                        final String defaultValue = DefaultConfigHolder.DEFAULT_CONFIG.getProperty(propertyKey);
                        if (defaultValue == null) {
                            throw new KeyNotFoundException(propertyKey);
                        }
                        // Reached when the installed fess_config.properties predates this build, which
                        // is what an upgrade leaves behind whenever the file was not merged. The loading
                        // cache does not run this again, so the warning appears once per key. The value
                        // stays out of the log because a default can be a credential.
                        logger.warn("{} is not in fess_config.properties, so the default this version ships is used."
                                + " Merge the fess_config.properties of this version into the installed one.", propertyKey);
                        return defaultValue;
                    });
                } catch (final ExecutionException e) {
                    if (e.getCause() instanceof KeyNotFoundException) {
                        return null;
                    }
                    return super.get(propertyKey);
                }
            }
        };
    }
}
