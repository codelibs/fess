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
package org.codelibs.fess.app.web.base;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessAdminActionTest extends UnitFessTestCase {

    private Path tempDir;
    private Path varDir;
    private Path webappDir;
    private Path confDir;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        tempDir = Files.createTempDirectory("fess_admin_test");
        varDir = Files.createDirectories(tempDir.resolve("var"));
        webappDir = Files.createDirectories(tempDir.resolve("webapp"));
        confDir = Files.createDirectories(tempDir.resolve("conf"));
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        System.clearProperty("fess.var.path");
        System.clearProperty("fess.webapp.path");
        System.clearProperty("fess.conf.path");
        if (tempDir != null) {
            deleteRecursively(tempDir.toFile());
        }
        super.tearDown(testInfo);
    }

    private void deleteRecursively(final File file) {
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    // ===================================================================================
    //                                                                    Blank/Null Path
    //                                                                    ================

    @Test
    public void test_validateFilePath_blankPath() {
        final FessAdminAction action = createAction();
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(""));
    }

    @Test
    public void test_validateFilePath_nullPath() {
        final FessAdminAction action = createAction();
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(null));
    }

    @Test
    public void test_validateFilePath_whitespaceOnlyPath() {
        final FessAdminAction action = createAction();
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath("   "));
    }

    // ===================================================================================
    //                                                                  Path Traversal
    //                                                                  ================

    @Test
    public void test_validateFilePath_pathTraversal_dotdot() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(varDir.toString() + "/../../../etc/passwd"));
    }

    @Test
    public void test_validateFilePath_pathTraversal_middleDotdot() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(varDir.toString() + "/subdir/../../.."));
    }

    // ===================================================================================
    //                                                              fess.var.path Tests
    //                                                              ====================

    @Test
    public void test_validateFilePath_validPathUnderVarPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        final File testFile = new File(varDir.toFile(), "data/test.txt");
        testFile.getParentFile().mkdirs();
        testFile.createNewFile();

        // Should not throw
        action.validateFilePath(testFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_pathOutsideVarPath() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        final File outsideFile = new File(tempDir.toFile(), "outside/test.txt");

        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(outsideFile.getAbsolutePath()));
    }

    // ===================================================================================
    //                                                            fess.webapp.path Tests
    //                                                            ========================

    @Test
    public void test_validateFilePath_validPathUnderWebappPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.webapp.path", webappDir.toString());
        final File testFile = new File(webappDir.toFile(), "WEB-INF/view/test.jsp");
        testFile.getParentFile().mkdirs();
        testFile.createNewFile();

        // Should not throw
        action.validateFilePath(testFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_pathOutsideWebappPath() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.webapp.path", webappDir.toString());
        final File outsideFile = new File(tempDir.toFile(), "outside/test.txt");

        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(outsideFile.getAbsolutePath()));
    }

    // ===================================================================================
    //                                                             fess.conf.path Tests
    //                                                             =======================

    @Test
    public void test_validateFilePath_validPathUnderConfPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.conf.path", confDir.toString());
        final File testFile = new File(confDir.toFile(), "fess_config.properties");
        testFile.createNewFile();

        // Should not throw
        action.validateFilePath(testFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_pathOutsideConfPath() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.conf.path", confDir.toString());
        final File outsideFile = new File(tempDir.toFile(), "outside/test.txt");

        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(outsideFile.getAbsolutePath()));
    }

    // ===================================================================================
    //                                                           Multiple Paths Tests
    //                                                           =======================

    @Test
    public void test_validateFilePath_validPathUnderSecondAllowedPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        System.setProperty("fess.webapp.path", webappDir.toString());
        System.setProperty("fess.conf.path", confDir.toString());

        // Path under webapp (second property) should be valid
        final File testFile = new File(webappDir.toFile(), "test.html");
        testFile.createNewFile();

        action.validateFilePath(testFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_validPathUnderThirdAllowedPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        System.setProperty("fess.webapp.path", webappDir.toString());
        System.setProperty("fess.conf.path", confDir.toString());

        // Path under conf (third property) should be valid
        final File testFile = new File(confDir.toFile(), "app.properties");
        testFile.createNewFile();

        action.validateFilePath(testFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_pathOutsideAllAllowedPaths() {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        System.setProperty("fess.webapp.path", webappDir.toString());
        System.setProperty("fess.conf.path", confDir.toString());

        final File outsideFile = new File(tempDir.toFile(), "outside/test.txt");

        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(outsideFile.getAbsolutePath()));
    }

    @Test
    public void test_validateFilePath_onlyVarPathSet_webappPathValid() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        // fess.webapp.path and fess.conf.path not set

        final File webappFile = new File(webappDir.toFile(), "test.html");
        webappFile.getParentFile().mkdirs();
        webappFile.createNewFile();

        // Should throw because only fess.var.path is set and the file is not under it
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(webappFile.getAbsolutePath()));
    }

    @Test
    public void test_validateFilePath_onlyWebappPathSet_varPathValid() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.webapp.path", webappDir.toString());
        // fess.var.path and fess.conf.path not set

        final File varFile = new File(varDir.toFile(), "test.txt");
        varFile.getParentFile().mkdirs();
        varFile.createNewFile();

        // Should throw because only fess.webapp.path is set and the file is not under it
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(varFile.getAbsolutePath()));
    }

    // ===================================================================================
    //                                                         No Properties Set Tests
    //                                                         =========================

    @Test
    public void test_validateFilePath_noPropertiesSet() throws Exception {
        final FessAdminAction action = createAction();
        // No system properties set - should pass without checking

        final File anyFile = new File(tempDir.toFile(), "any/test.txt");
        anyFile.getParentFile().mkdirs();
        anyFile.createNewFile();

        // Should not throw when no allowed paths are configured
        action.validateFilePath(anyFile.getAbsolutePath());
    }

    // ===================================================================================
    //                                                             Error Message Tests
    //                                                             =======================

    @Test
    public void test_validateFilePath_errorMessageContainsAllAllowedPaths() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        System.setProperty("fess.webapp.path", webappDir.toString());
        System.setProperty("fess.conf.path", confDir.toString());

        final File outsideFile = new File(tempDir.toFile(), "outside/test.txt");
        outsideFile.getParentFile().mkdirs();
        outsideFile.createNewFile();

        try {
            action.validateFilePath(outsideFile.getAbsolutePath());
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            final String message = e.getMessage();
            assertTrue("Error message should contain var path: " + message, message.contains(varDir.toFile().getCanonicalPath()));
            assertTrue("Error message should contain webapp path: " + message, message.contains(webappDir.toFile().getCanonicalPath()));
            assertTrue("Error message should contain conf path: " + message, message.contains(confDir.toFile().getCanonicalPath()));
        }
    }

    @Test
    public void test_validateFilePath_errorMessageForBlankPath() {
        final FessAdminAction action = createAction();
        try {
            action.validateFilePath("");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            assertTrue("Error message should indicate blank path: " + e.getMessage(), e.getMessage().contains("blank"));
        }
    }

    // ===================================================================================
    //                                                             Subdirectory Tests
    //                                                             =======================

    @Test
    public void test_validateFilePath_deeplyNestedPathUnderVarPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        final File deepFile = new File(varDir.toFile(), "a/b/c/d/e/test.txt");
        deepFile.getParentFile().mkdirs();
        deepFile.createNewFile();

        // Should not throw
        action.validateFilePath(deepFile.getAbsolutePath());
    }

    @Test
    public void test_validateFilePath_directFileUnderVarPath() throws Exception {
        final FessAdminAction action = createAction();
        System.setProperty("fess.var.path", varDir.toString());
        final File directFile = new File(varDir.toFile(), "test.txt");
        directFile.createNewFile();

        // Should not throw
        action.validateFilePath(directFile.getAbsolutePath());
    }

    // ===================================================================================
    //                                                          Similar Prefix Tests
    //                                                          ========================

    @Test
    public void test_validateFilePath_similarPrefixDirectory() throws Exception {
        final FessAdminAction action = createAction();
        // Use a path with a distinct name to test non-matching paths
        final Path baseDir = Files.createDirectories(tempDir.resolve("vardata"));
        System.setProperty("fess.var.path", baseDir.toString());

        // A directory that does NOT share the prefix
        final Path otherDir = Files.createDirectories(tempDir.resolve("other"));
        final File fileInOther = new File(otherDir.toFile(), "test.txt");
        fileInOther.createNewFile();

        // Should throw because "other" is not under "vardata"
        assertThrows(IllegalArgumentException.class, () -> action.validateFilePath(fileInOther.getAbsolutePath()));
    }

    // ===================================================================================
    //                                                                      Helper Methods
    //                                                                      ==============

    private FessAdminAction createAction() {
        return new FessAdminAction() {
            @Override
            protected String getActionRole() {
                return "admin-test";
            }
        };
    }

    private static void assertThrows(final Class<? extends Throwable> expectedType, final Runnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected " + expectedType.getSimpleName() + " to be thrown");
        } catch (final Throwable t) {
            if (!expectedType.isInstance(t)) {
                throw new AssertionError("Expected " + expectedType.getSimpleName() + " but got " + t.getClass().getSimpleName(), t);
            }
        }
    }
}
