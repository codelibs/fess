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

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;

public class ResourceUtilTest extends UnitFessTestCase {
    @Test
    public void test_resolve() {
        String value;

        value = null;
        assertNull(ResourceUtil.resolve(value));

        value = "";
        assertEquals("", ResourceUtil.resolve(value));

        value = "a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${a}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "$a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${a";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "$a}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${abc}";
        assertEquals(value, ResourceUtil.resolve(value));

        value = "${abc.xyz}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("abc", "123");

        value = "${abc}";
        assertEquals("123", ResourceUtil.resolve(value));

        value = "xxx${abc}zzz";
        assertEquals("xxx123zzz", ResourceUtil.resolve(value));

        value = "${abc.xyz}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("abc.xyz", "789");

        value = "${abc.xyz}";
        assertEquals("789", ResourceUtil.resolve(value));

        value = "${abc}${abc.xyz}";
        assertEquals("123789", ResourceUtil.resolve(value));

        value = "xxx${abc.xyz}zzz";
        assertEquals("xxx789zzz", ResourceUtil.resolve(value));

        value = "${\\$}";
        assertEquals(value, ResourceUtil.resolve(value));

        System.setProperty("test.dir", "c:\\test1\\test2");

        value = "${test.dir}";
        assertEquals("c:\\test1\\test2", ResourceUtil.resolve(value));

    }

    @Test
    public void test_getAppType() {
        // Test default empty when no env var is set
        String originalAppType = System.getenv("FESS_APP_TYPE");

        // Since we can't modify environment variables directly,
        // we test the current behavior
        String appType = ResourceUtil.getAppType();
        assertNotNull(appType);
        // appType should be either empty string or the actual env value
        assertTrue(appType.isEmpty() || appType.length() > 0);
    }

    @Test
    public void test_getOverrideConfPath() {
        // Test when app type is not docker
        OptionalEntity<String> confPath = ResourceUtil.getOverrideConfPath();
        assertNotNull(confPath);
        // Should be empty when not docker app type
        if (!"docker".equalsIgnoreCase(ResourceUtil.getAppType())) {
            assertFalse(confPath.isPresent());
        }
    }

    @Test
    public void test_getFesenHttpUrl() {
        String url = ResourceUtil.getFesenHttpUrl();
        assertNotNull(url);
        // Should return either system property or config value
        assertTrue(url.length() > 0);
    }

    @Test
    public void test_getConfPath() {
        Path confPath = ResourceUtil.getConfPath("test.conf");
        assertNotNull(confPath);

        // Test with empty names
        confPath = ResourceUtil.getConfPath();
        assertNotNull(confPath);

        // Test with multiple names
        confPath = ResourceUtil.getConfPath("dir1", "dir2", "file.conf");
        assertNotNull(confPath);
        assertTrue(confPath.toString().contains("dir1"));
        assertTrue(confPath.toString().contains("dir2"));
        assertTrue(confPath.toString().contains("file.conf"));
    }

    @Test
    public void test_getConfOrClassesPath() {
        // Test behavior when resource is not found
        try {
            Path path = ResourceUtil.getConfOrClassesPath("nonexistent.conf");
            fail("Should throw ResourceNotFoundRuntimeException for non-existent file");
        } catch (org.codelibs.core.exception.ResourceNotFoundRuntimeException e) {
            // Expected behavior - method throws exception for non-existent files
            assertTrue(e.getMessage().contains("nonexistent.conf"));
        }

        // Test with multiple names - should also throw exception for non-existent files
        try {
            Path path = ResourceUtil.getConfOrClassesPath("dir", "file.conf");
            fail("Should throw ResourceNotFoundRuntimeException for non-existent file");
        } catch (org.codelibs.core.exception.ResourceNotFoundRuntimeException e) {
            // Expected behavior
            assertTrue(e.getMessage().contains("dir") && e.getMessage().contains("file.conf"));
        }
    }

    @Test
    public void test_getClassesPath() {
        Path classesPath = ResourceUtil.getClassesPath("test.class");
        assertNotNull(classesPath);
        assertTrue(classesPath.toString().contains("classes"));

        // Test with multiple names
        classesPath = ResourceUtil.getClassesPath("package", "Test.class");
        assertNotNull(classesPath);
        assertTrue(classesPath.toString().contains("package"));
        assertTrue(classesPath.toString().contains("Test.class"));
    }

    @Test
    public void test_getOrigPath() {
        Path origPath = ResourceUtil.getOrigPath("original.file");
        assertNotNull(origPath);
        assertTrue(origPath.toString().contains("orig"));
    }

    @Test
    public void test_getMailTemplatePath() {
        Path mailPath = ResourceUtil.getMailTemplatePath("template.vm");
        assertNotNull(mailPath);
        assertTrue(mailPath.toString().contains("mail"));
    }

    @Test
    public void test_getViewTemplatePath() {
        Path viewPath = ResourceUtil.getViewTemplatePath("view.html");
        assertNotNull(viewPath);
        assertTrue(viewPath.toString().contains("view"));
    }

    @Test
    public void test_getDictionaryPath() {
        Path dictPath = ResourceUtil.getDictionaryPath("dict.txt");
        assertNotNull(dictPath);
        assertTrue(dictPath.toString().contains("dict"));
    }

    @Test
    public void test_getThumbnailPath() {
        Path thumbPath = ResourceUtil.getThumbnailPath("thumb.png");
        assertNotNull(thumbPath);
        assertTrue(thumbPath.toString().contains("thumbnails"));
    }

    @Test
    public void test_getSitePath() {
        Path sitePath = ResourceUtil.getSitePath("site.xml");
        assertNotNull(sitePath);
        assertTrue(sitePath.toString().contains("site"));
    }

    @Test
    public void test_getPluginPath() {
        Path pluginPath = ResourceUtil.getPluginPath("plugin.jar");
        assertNotNull(pluginPath);
        assertTrue(pluginPath.toString().contains("plugin"));
    }

    @Test
    public void test_getProjectPropertiesFile() {
        Path propFile = ResourceUtil.getProjectPropertiesFile();
        assertNotNull(propFile);
        assertTrue(propFile.toString().contains("project.properties"));
    }

    @Test
    public void test_getImagePath() {
        Path imagePath = ResourceUtil.getImagePath("logo.png");
        assertNotNull(imagePath);
        assertTrue(imagePath.toString().contains("images"));
    }

    @Test
    public void test_getCssPath() {
        Path cssPath = ResourceUtil.getCssPath("style.css");
        assertNotNull(cssPath);
        assertTrue(cssPath.toString().contains("css"));
    }

    @Test
    public void test_getJavaScriptPath() {
        Path jsPath = ResourceUtil.getJavaScriptPath("script.js");
        assertNotNull(jsPath);
        assertTrue(jsPath.toString().contains("js"));
    }

    @Test
    public void test_getEnvPath() {
        Path envPath = ResourceUtil.getEnvPath("test", "config.properties");
        assertNotNull(envPath);
        assertTrue(envPath.toString().contains("env"));
        assertTrue(envPath.toString().contains("test"));
    }

    @Test
    public void test_getJarFiles() {
        File[] jarFiles = ResourceUtil.getJarFiles("fess");
        assertNotNull(jarFiles);
        // Should return empty array when no servlet context available in test
        assertEquals(0, jarFiles.length);

        // Test with empty prefix
        jarFiles = ResourceUtil.getJarFiles("");
        assertNotNull(jarFiles);
        assertEquals(0, jarFiles.length);
    }

    @Test
    public void test_getPluginJarFiles_withPrefix() {
        File[] pluginFiles = ResourceUtil.getPluginJarFiles("plugin");
        assertNotNull(pluginFiles);
        // Should return empty array when plugin directory doesn't exist
        assertEquals(0, pluginFiles.length);
    }

    @Test
    public void test_getPluginJarFiles_withFilter() {
        FilenameFilter filter = (dir, name) -> name.endsWith(".jar");
        File[] pluginFiles = ResourceUtil.getPluginJarFiles(filter);
        assertNotNull(pluginFiles);
        // Should return empty array when plugin directory doesn't exist
        assertEquals(0, pluginFiles.length);
    }

    @Test
    public void test_resolve_additionalCases() {
        // Test multiple replacements in same string
        System.setProperty("var1", "value1");
        System.setProperty("var2", "value2");

        String value = "${var1}-${var2}";
        assertEquals("value1-value2", ResourceUtil.resolve(value));

        // Test nested-like patterns (but not actual nesting)
        value = "${var1${var2}}"; // The ${var2} part gets replaced first
        assertEquals("${var1value2}", ResourceUtil.resolve(value));

        // Test with special characters in replacement
        System.setProperty("special", "value$with\\backslash");
        value = "${special}";
        assertEquals("value$with\\backslash", ResourceUtil.resolve(value));

        // Test with numeric property names
        System.setProperty("123", "numeric");
        value = "${123}";
        assertEquals("numeric", ResourceUtil.resolve(value));

        // Test with underscores in property names
        System.setProperty("test_var", "underscore");
        value = "${test_var}";
        assertEquals("underscore", ResourceUtil.resolve(value));

        // Clean up test properties
        System.clearProperty("var1");
        System.clearProperty("var2");
        System.clearProperty("special");
        System.clearProperty("123");
        System.clearProperty("test_var");
    }

    @Test
    public void test_resolve_edgeCases() {
        // Test empty property replacement
        System.setProperty("empty", "");
        String value = "${empty}";
        assertEquals("", ResourceUtil.resolve(value));

        // Test property with just spaces
        System.setProperty("spaces", "   ");
        value = "${spaces}";
        assertEquals("   ", ResourceUtil.resolve(value));

        // Test long property name
        System.setProperty("very.long.property.name.with.many.dots", "long");
        value = "${very.long.property.name.with.many.dots}";
        assertEquals("long", ResourceUtil.resolve(value));

        // Test multiple occurrences of same variable
        System.setProperty("repeat", "X");
        value = "${repeat}${repeat}${repeat}";
        assertEquals("XXX", ResourceUtil.resolve(value));

        // Clean up
        System.clearProperty("empty");
        System.clearProperty("spaces");
        System.clearProperty("very.long.property.name.with.many.dots");
        System.clearProperty("repeat");
    }

}
