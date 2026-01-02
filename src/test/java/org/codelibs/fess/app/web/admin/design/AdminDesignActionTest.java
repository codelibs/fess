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
package org.codelibs.fess.app.web.admin.design;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class AdminDesignActionTest extends UnitFessTestCase {

    private AdminDesignAction action;
    private Path tempDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        action = new AdminDesignAction();
        tempDir = Files.createTempDirectory("fess_test");
    }

    @Override
    public void tearDown() throws Exception {
        // Clean up temp directory
        if (tempDir != null) {
            deleteRecursively(tempDir.toFile());
        }
        super.tearDown();
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    // ===================================================================================
    //                                                                           JSP Codec
    //                                                                           =========
    public void test_decodeJsp() {
        assertEquals("&lt;% a %&gt;", AdminDesignAction.decodeJsp("<% a %>"));
        assertEquals("&lt;%= a %&gt;", AdminDesignAction.decodeJsp("<%= a %>"));
        assertEquals("&lt;% a\nb %&gt;", AdminDesignAction.decodeJsp("<% a\nb %>"));
        assertEquals("&lt;%= a\nb %&gt;", AdminDesignAction.decodeJsp("<%= a\nb %>"));
        assertEquals("<% a", AdminDesignAction.decodeJsp("<% a"));
        assertEquals("<%= a", AdminDesignAction.decodeJsp("<%= a"));
        assertEquals("<% try{ %>", AdminDesignAction.decodeJsp("<!--TRY-->"));
        assertEquals("<% }catch(Exception e){session.invalidate();} %>",
                AdminDesignAction.decodeJsp("<!--CACHE_AND_SESSION_INVALIDATE-->"));
        assertEquals("&lt;% a %&gt; %>", AdminDesignAction.decodeJsp("<% a %> %>"));
        assertEquals("&lt;% a %&gt; <%", AdminDesignAction.decodeJsp("<% a %> <%"));
        assertEquals("&lt;% <% a %&gt;", AdminDesignAction.decodeJsp("<% <% a %>"));
        assertEquals("%> &lt;% a %&gt;", AdminDesignAction.decodeJsp("%> <% a %>"));
    }

    public void test_encodeJsp() {
        assertEquals("<!--TRY-->", AdminDesignAction.encodeJsp("<% try{ %>"));
        assertEquals("<!--CACHE_AND_SESSION_INVALIDATE-->",
                AdminDesignAction.encodeJsp("<% }catch(Exception e){session.invalidate();} %>"));
    }

    // ===================================================================================
    //                                                                Path Traversal Tests
    //                                                                ====================

    public void test_isValidUploadPath_validPath() throws Exception {
        // Create test directory structure
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File validFile = new File(baseDir, "test.png");

        Boolean result = invokeIsValidUploadPath(validFile, baseDir);
        assertTrue("Valid path within base directory should be allowed", result);
    }

    public void test_isValidUploadPath_validSubdirectory() throws Exception {
        // Create test directory structure with subdirectory
        File baseDir = new File(tempDir.toFile(), "images");
        File subDir = new File(baseDir, "icons");
        subDir.mkdirs();
        File validFile = new File(subDir, "icon.png");

        Boolean result = invokeIsValidUploadPath(validFile, baseDir);
        assertTrue("Valid path in subdirectory should be allowed", result);
    }

    public void test_isValidUploadPath_pathTraversal_simple() throws Exception {
        // Test simple path traversal attack
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File maliciousFile = new File(baseDir, "../../../etc/passwd");

        Boolean result = invokeIsValidUploadPath(maliciousFile, baseDir);
        assertFalse("Path traversal with ../ should be blocked", result);
    }

    public void test_isValidUploadPath_pathTraversal_encoded() throws Exception {
        // Test path traversal that would be decoded
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File maliciousFile = new File(baseDir, "test/../../../etc/passwd");

        Boolean result = invokeIsValidUploadPath(maliciousFile, baseDir);
        assertFalse("Encoded path traversal should be blocked", result);
    }

    public void test_isValidUploadPath_pathTraversal_outsideBase() throws Exception {
        // Test file outside base directory
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File outsideFile = new File(tempDir.toFile(), "css/style.css");

        Boolean result = invokeIsValidUploadPath(outsideFile, baseDir);
        assertFalse("File outside base directory should be blocked", result);
    }

    public void test_isValidUploadPath_pathTraversal_absolutePath() throws Exception {
        // Test absolute path outside base
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File absoluteFile = new File("/etc/passwd");

        Boolean result = invokeIsValidUploadPath(absoluteFile, baseDir);
        assertFalse("Absolute path outside base should be blocked", result);
    }

    public void test_isValidUploadPath_pathTraversal_multipleTraversals() throws Exception {
        // Test multiple path traversal sequences
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File maliciousFile = new File(baseDir, "a/b/c/../../../../../tmp/evil");

        Boolean result = invokeIsValidUploadPath(maliciousFile, baseDir);
        assertFalse("Multiple path traversals should be blocked", result);
    }

    public void test_isValidUploadPath_edgeCase_sameDirectory() throws Exception {
        // Test file directly in base directory
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File directFile = new File(baseDir, "direct.png");

        Boolean result = invokeIsValidUploadPath(directFile, baseDir);
        assertTrue("File directly in base directory should be allowed", result);
    }

    public void test_isValidUploadPath_edgeCase_similarPrefix() throws Exception {
        // Test directory with similar prefix (images vs images2)
        File baseDir = new File(tempDir.toFile(), "images");
        baseDir.mkdirs();
        File similarDir = new File(tempDir.toFile(), "images2");
        similarDir.mkdirs();
        File fileInSimilar = new File(similarDir, "test.png");

        Boolean result = invokeIsValidUploadPath(fileInSimilar, baseDir);
        assertFalse("File in directory with similar prefix should be blocked", result);
    }

    // ===================================================================================
    //                                                          Virtual Host Path Tests
    //                                                          ========================

    public void test_isValidVirtualHostPath_emptyPath() throws Exception {
        setupVirtualHostHelper();

        Boolean result = invokeIsValidVirtualHostPath("");
        assertTrue("Empty path should be valid (default host)", result);
    }

    public void test_isValidVirtualHostPath_nullPath() throws Exception {
        setupVirtualHostHelper();

        Boolean result = invokeIsValidVirtualHostPath(null);
        assertTrue("Null path should be valid (default host)", result);
    }

    public void test_isValidVirtualHostPath_blankPath() throws Exception {
        setupVirtualHostHelper();

        Boolean result = invokeIsValidVirtualHostPath("   ");
        assertTrue("Blank path should be valid (default host)", result);
    }

    public void test_isValidVirtualHostPath_slashPath() throws Exception {
        setupVirtualHostHelper();

        Boolean result = invokeIsValidVirtualHostPath("/");
        assertTrue("Root path '/' should be valid", result);
    }

    public void test_isValidVirtualHostPath_validConfiguredPath() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/site1");
        assertTrue("Configured virtual host path should be valid", result);
    }

    public void test_isValidVirtualHostPath_anotherValidConfiguredPath() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/site2");
        assertTrue("Another configured virtual host path should be valid", result);
    }

    public void test_isValidVirtualHostPath_pathTraversal() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/../../../etc");
        assertFalse("Path traversal should be blocked", result);
    }

    public void test_isValidVirtualHostPath_pathTraversal_encoded() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/site1/../../../etc");
        assertFalse("Encoded path traversal should be blocked", result);
    }

    public void test_isValidVirtualHostPath_unconfiguredPath() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/site3");
        assertFalse("Unconfigured path should be blocked", result);
    }

    public void test_isValidVirtualHostPath_randomMaliciousPath() throws Exception {
        setupVirtualHostHelper("/site1", "/site2");

        Boolean result = invokeIsValidVirtualHostPath("/..%2f..%2f..%2fetc");
        assertFalse("Malicious encoded path should be blocked", result);
    }

    public void test_isValidVirtualHostPath_emptyVirtualHosts() throws Exception {
        // No virtual hosts configured
        setupVirtualHostHelper();

        Boolean result = invokeIsValidVirtualHostPath("/anypath");
        assertFalse("Any non-empty/non-root path should be blocked when no virtual hosts configured", result);
    }

    public void test_isValidVirtualHostPath_caseInsensitive() throws Exception {
        setupVirtualHostHelper("/Site1", "/SITE2");

        // The current implementation is case-sensitive
        Boolean result = invokeIsValidVirtualHostPath("/site1");
        assertFalse("Case mismatch should be blocked (implementation is case-sensitive)", result);
    }

    // ===================================================================================
    //                                                                      Helper Methods
    //                                                                      ==============

    private Boolean invokeIsValidUploadPath(File file, File baseDir) throws Exception {
        Method method = AdminDesignAction.class.getDeclaredMethod("isValidUploadPath", File.class, File.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(action, file, baseDir);
    }

    private Boolean invokeIsValidVirtualHostPath(String path) throws Exception {
        Method method = AdminDesignAction.class.getDeclaredMethod("isValidVirtualHostPath", String.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(action, path);
    }

    private void setupVirtualHostHelper(String... paths) {
        // Register VirtualHostHelper in ComponentUtil
        VirtualHostHelper virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String[] getVirtualHostPaths() {
                return paths;
            }
        };
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");

        // Also set up FessConfig if needed
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                Tuple3<String, String, String>[] result = new Tuple3[paths.length];
                for (int i = 0; i < paths.length; i++) {
                    // Remove leading slash for the tuple value
                    String path = paths[i].startsWith("/") ? paths[i].substring(1) : paths[i];
                    result[i] = new Tuple3<>("Host", "host" + i + ".example.com", path);
                }
                return result;
            }
        });
    }
}
