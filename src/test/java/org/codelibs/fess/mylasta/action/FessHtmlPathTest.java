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
package org.codelibs.fess.mylasta.action;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.next.HtmlNext;

public class FessHtmlPathTest extends UnitFessTestCase {

    @Test
    public void test_allPathsInitialized() throws Exception {
        // Get all public static final fields of HtmlNext type
        Field[] fields = FessHtmlPath.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                if (field.getType().equals(HtmlNext.class)) {
                    Object value = field.get(null);
                    assertNotNull(value, "Field " + field.getName() + " should not be null");
                    assertTrue(value instanceof HtmlNext, "Field " + field.getName() + " should be HtmlNext instance");
                }
            }
        }
    }

    @Test
    public void test_pathValues() throws Exception {
        // Test specific path values to ensure correct JSP paths
        assertEquals("/admin/accesstoken/admin_accesstoken.jsp", FessHtmlPath.path_AdminAccesstoken_AdminAccesstokenJsp.getRoutingPath());
        assertEquals("/admin/accesstoken/admin_accesstoken_details.jsp",
                FessHtmlPath.path_AdminAccesstoken_AdminAccesstokenDetailsJsp.getRoutingPath());
        assertEquals("/admin/accesstoken/admin_accesstoken_edit.jsp",
                FessHtmlPath.path_AdminAccesstoken_AdminAccesstokenEditJsp.getRoutingPath());
        assertEquals("/admin/backup/admin_backup.jsp", FessHtmlPath.path_AdminBackup_AdminBackupJsp.getRoutingPath());
        assertEquals("/admin/badword/admin_badword.jsp", FessHtmlPath.path_AdminBadword_AdminBadwordJsp.getRoutingPath());
        assertEquals("/admin/dashboard/admin_dashboard.jsp", FessHtmlPath.path_AdminDashboard_AdminDashboardJsp.getRoutingPath());
        assertEquals("/admin/general/admin_general.jsp", FessHtmlPath.path_AdminGeneral_AdminGeneralJsp.getRoutingPath());
        assertEquals("/admin/wizard/admin_wizard.jsp", FessHtmlPath.path_AdminWizard_AdminWizardJsp.getRoutingPath());
        assertEquals("/admin/login/index.jsp", FessHtmlPath.path_AdminLogin_IndexJsp.getRoutingPath());
    }

    @Test
    public void test_uniquePaths() throws Exception {
        // Verify all paths are unique
        Set<String> paths = new HashSet<>();
        Field[] fields = FessHtmlPath.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                if (field.getType().equals(HtmlNext.class)) {
                    HtmlNext htmlNext = (HtmlNext) field.get(null);
                    String path = htmlNext.getRoutingPath();
                    assertFalse("Duplicate path found: " + path + " in field " + field.getName(), paths.contains(path));
                    paths.add(path);
                }
            }
        }
        assertTrue("Should have unique paths", paths.size() > 0);
    }

    @Test
    public void test_pathNamingConvention() throws Exception {
        // Test field naming follows convention
        Field[] fields = FessHtmlPath.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                if (field.getType().equals(HtmlNext.class)) {
                    String fieldName = field.getName();
                    assertTrue("Field name should start with 'path_': " + fieldName, fieldName.startsWith("path_"));

                    HtmlNext htmlNext = (HtmlNext) field.get(null);
                    String path = htmlNext.getRoutingPath();
                    assertTrue("Path should end with .jsp: " + path, path.endsWith(".jsp"));
                }
            }
        }
    }

    @Test
    public void test_adminPaths() throws Exception {
        // Test admin paths structure
        Set<String> adminPaths = new HashSet<>();
        Field[] fields = FessHtmlPath.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                if (field.getType().equals(HtmlNext.class)) {
                    String fieldName = field.getName();
                    if (fieldName.contains("Admin")) {
                        HtmlNext htmlNext = (HtmlNext) field.get(null);
                        String path = htmlNext.getRoutingPath();
                        assertTrue("Admin field should have /admin/ path: " + fieldName + " -> " + path, path.startsWith("/admin/"));
                        adminPaths.add(path);
                    }
                }
            }
        }
        assertTrue("Should have admin paths", adminPaths.size() > 0);
    }

    @Test
    public void test_dictPaths() throws Exception {
        // Test dictionary paths structure
        assertEquals("/admin/dict/admin_dict.jsp", FessHtmlPath.path_AdminDict_AdminDictJsp.getRoutingPath());
        assertEquals("/admin/dict/kuromoji/admin_dict_kuromoji.jsp",
                FessHtmlPath.path_AdminDictKuromoji_AdminDictKuromojiJsp.getRoutingPath());
        assertEquals("/admin/dict/synonym/admin_dict_synonym.jsp", FessHtmlPath.path_AdminDictSynonym_AdminDictSynonymJsp.getRoutingPath());
        assertEquals("/admin/dict/stopwords/admin_dict_stopwords.jsp",
                FessHtmlPath.path_AdminDictStopwords_AdminDictStopwordsJsp.getRoutingPath());
        assertEquals("/admin/dict/mapping/admin_dict_mapping.jsp", FessHtmlPath.path_AdminDictMapping_AdminDictMappingJsp.getRoutingPath());
        assertEquals("/admin/dict/protwords/admin_dict_protwords.jsp",
                FessHtmlPath.path_AdminDictProtwords_AdminDictProtwordsJsp.getRoutingPath());
        assertEquals("/admin/dict/stemmeroverride/admin_dict_stemmeroverride.jsp",
                FessHtmlPath.path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp.getRoutingPath());
    }

    @Test
    public void test_loginPaths() throws Exception {
        // The login pages are rendered from the admin view tree; their URL stays /login/.
        assertEquals("/admin/login/index.jsp", FessHtmlPath.path_AdminLogin_IndexJsp.getRoutingPath());
        assertEquals("/admin/login/newpassword.jsp", FessHtmlPath.path_AdminLogin_NewpasswordJsp.getRoutingPath());
    }

    @Test
    public void test_configPaths() throws Exception {
        // Test configuration paths
        assertEquals("/admin/webconfig/admin_webconfig.jsp", FessHtmlPath.path_AdminWebconfig_AdminWebconfigJsp.getRoutingPath());
        assertEquals("/admin/fileconfig/admin_fileconfig.jsp", FessHtmlPath.path_AdminFileconfig_AdminFileconfigJsp.getRoutingPath());
        assertEquals("/admin/dataconfig/admin_dataconfig.jsp", FessHtmlPath.path_AdminDataconfig_AdminDataconfigJsp.getRoutingPath());
    }

    @Test
    public void test_pathCount() throws Exception {
        // Count total number of paths
        int pathCount = 0;
        Field[] fields = FessHtmlPath.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                if (field.getType().equals(HtmlNext.class)) {
                    pathCount++;
                }
            }
        }
        // Verify minimum expected number of paths
        assertTrue("Should have at least 100 paths, found: " + pathCount, pathCount >= 100);
    }

    @Test
    public void test_detailsEditPattern() throws Exception {
        // Test common pattern: base, details, edit JSPs
        verifyPathPattern("AdminAccesstoken", "/admin/accesstoken/");
        verifyPathPattern("AdminBadword", "/admin/badword/");
        verifyPathPattern("AdminBoostdoc", "/admin/boostdoc/");
        verifyPathPattern("AdminDataconfig", "/admin/dataconfig/");
        verifyPathPattern("AdminDuplicatehost", "/admin/duplicatehost/");
        verifyPathPattern("AdminElevateword", "/admin/elevateword/");
        verifyPathPattern("AdminFileauth", "/admin/fileauth/");
        verifyPathPattern("AdminFileconfig", "/admin/fileconfig/");
        verifyPathPattern("AdminGroup", "/admin/group/");
        verifyPathPattern("AdminKeymatch", "/admin/keymatch/");
        verifyPathPattern("AdminLabeltype", "/admin/labeltype/");
        verifyPathPattern("AdminPathmap", "/admin/pathmap/");
        verifyPathPattern("AdminRelatedcontent", "/admin/relatedcontent/");
        verifyPathPattern("AdminRelatedquery", "/admin/relatedquery/");
        verifyPathPattern("AdminReqheader", "/admin/reqheader/");
        verifyPathPattern("AdminRole", "/admin/role/");
        verifyPathPattern("AdminScheduler", "/admin/scheduler/");
        verifyPathPattern("AdminUser", "/admin/user/");
        verifyPathPattern("AdminWebauth", "/admin/webauth/");
        verifyPathPattern("AdminWebconfig", "/admin/webconfig/");
    }

    @Test
    public void test_everyPathNamesAnExistingJsp() throws Exception {
        // FessHtmlPath is edited by hand (freegen does not run on JDK 21) and a JSP is not compiled
        // by the build, so a constant left pointing at a moved or deleted page is only found when
        // a user opens it. Every constant must name a file under WEB-INF/view.
        final List<String> missing = new ArrayList<>();
        int checked = 0;
        for (final Field field : FessHtmlPath.class.getDeclaredFields()) {
            if (field.getType().equals(HtmlNext.class)) {
                final String path = ((HtmlNext) field.get(null)).getRoutingPath();
                if (!Files.isRegularFile(Paths.get("src/main/webapp/WEB-INF/view" + path))) {
                    missing.add(field.getName() + " -> " + path);
                }
                checked++;
            }
        }
        assertTrue("checked only " + checked + " paths", checked >= 100);
        assertEquals("every FessHtmlPath constant must name an existing JSP: " + missing, 0, missing.size());
    }

    private void verifyPathPattern(String prefix, String basePath) throws Exception {
        // Helper method to verify common pattern
        String baseFieldName = "path_" + prefix + "_" + prefix + "Jsp";
        String detailsFieldName = "path_" + prefix + "_" + prefix + "DetailsJsp";
        String editFieldName = "path_" + prefix + "_" + prefix + "EditJsp";

        Field baseField = getFieldIfExists(baseFieldName);
        Field detailsField = getFieldIfExists(detailsFieldName);
        Field editField = getFieldIfExists(editFieldName);

        if (baseField != null) {
            HtmlNext htmlNext = (HtmlNext) baseField.get(null);
            assertTrue("Base path should match pattern", htmlNext.getRoutingPath().startsWith(basePath));
        }

        if (detailsField != null) {
            HtmlNext htmlNext = (HtmlNext) detailsField.get(null);
            assertTrue("Details path should match pattern", htmlNext.getRoutingPath().contains("_details.jsp"));
        }

        if (editField != null) {
            HtmlNext htmlNext = (HtmlNext) editField.get(null);
            assertTrue("Edit path should match pattern", htmlNext.getRoutingPath().contains("_edit.jsp"));
        }
    }

    private Field getFieldIfExists(String fieldName) {
        try {
            return FessHtmlPath.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}