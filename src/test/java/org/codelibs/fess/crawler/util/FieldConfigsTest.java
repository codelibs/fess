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

import java.util.Collections;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.codelibs.fess.unit.UnitFessTestCase;

public class FieldConfigsTest extends UnitFessTestCase {
    public void test_empty() {
        final FieldConfigs fieldConfigs = new FieldConfigs(Collections.emptyMap());
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
    }

    public void test_values() {
        final Map<String, String> params = Maps.of("foo", "bar");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
        assertFalse(fieldConfigs.getConfig("foo").isEmpty());
        assertFalse(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isCache).orElse(false));
        assertFalse(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isOverwrite).orElse(false));
        assertEquals("bar", fieldConfigs.getConfig("foo").map(FieldConfigs.Config::getValues).orElse(new String[0])[0]);
    }

    public void test_cache_true() {
        final Map<String, String> params = Maps.of("foo", "true");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
        assertTrue(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isCache).orElse(false));
        assertFalse(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isOverwrite).orElse(false));
    }

    public void test_cache() {
        final Map<String, String> params = Maps.of("foo", "cache");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
        assertTrue(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isCache).orElse(false));
        assertFalse(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isOverwrite).orElse(false));
    }

    public void test_overwrite() {
        final Map<String, String> params = Maps.of("foo", "overwrite");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
        assertFalse(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isCache).orElse(false));
        assertTrue(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isOverwrite).orElse(false));
    }

    public void test_cache_overwrite() {
        final Map<String, String> params = Maps.of("foo", "cache|overwrite");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        assertTrue(fieldConfigs.getConfig("test").isEmpty());
        assertTrue(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isCache).orElse(false));
        assertTrue(fieldConfigs.getConfig("foo").map(FieldConfigs.Config::isOverwrite).orElse(false));
    }
}
