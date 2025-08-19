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
package org.codelibs.fess.unit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.codelibs.core.misc.DynamicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test implementation of system properties that loads from classpath
 */
public class TestSystemProperties extends DynamicProperties {

    private static final Logger logger = LoggerFactory.getLogger(TestSystemProperties.class);

    public TestSystemProperties() {
        // Create a temporary file to satisfy DynamicProperties constructor
        super(createTempFile());
        loadTestProperties();
    }

    private static File createTempFile() {
        try {
            File tempFile = File.createTempFile("test-system", ".properties");
            tempFile.deleteOnExit();
            return tempFile;
        } catch (IOException e) {
            logger.warn("Failed to create temp file, using null", e);
            return null;
        }
    }

    private void loadTestProperties() {
        // Load system.properties from test classpath
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("system.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                props.forEach((key, value) -> setProperty(String.valueOf(key), String.valueOf(value)));
                logger.debug("Loaded test system properties");
            } else {
                logger.warn("system.properties not found in test classpath, using defaults");
                setDefaultProperties();
            }
        } catch (IOException e) {
            logger.warn("Failed to load system.properties, using defaults", e);
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        // Set default test properties
        setProperty("fess.version", "99.99.99");
    }
}