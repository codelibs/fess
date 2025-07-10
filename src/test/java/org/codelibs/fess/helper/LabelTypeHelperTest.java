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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypePattern;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class LabelTypeHelperTest extends UnitFessTestCase {

    private LabelTypeHelper labelTypeHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        labelTypeHelper = new LabelTypeHelper();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new MockLabelTypeService(), LabelTypeService.class.getCanonicalName());
        ComponentUtil.register(new MockVirtualHostHelper(), "virtualHostHelper");
        ComponentUtil.register(new MockRoleQueryHelper(), "roleQueryHelper");
    }

    public void test_init() {
        try {
            labelTypeHelper.init();
        } catch (Exception e) {
            fail("init() should not throw an exception: " + e.getMessage());
        }
    }

    public void test_load() {
        int result = labelTypeHelper.load();
        assertTrue(result >= 0);
    }

    public void test_refresh() {
        List<LabelType> labelTypeList = createTestLabelTypeList();

        try {
            labelTypeHelper.refresh(labelTypeList);
        } catch (Exception e) {
            fail("refresh() should not throw an exception: " + e.getMessage());
        }
    }

    public void test_matchLocale() {
        assertFalse(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.JAPANESE));
        assertFalse(labelTypeHelper.matchLocale(Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE));

        assertTrue(labelTypeHelper.matchLocale(null, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ROOT, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.ENGLISH));
        assertTrue(labelTypeHelper.matchLocale(Locale.JAPAN, Locale.JAPANESE));
        assertTrue(labelTypeHelper.matchLocale(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE));
        assertTrue(labelTypeHelper.matchLocale(Locale.TRADITIONAL_CHINESE, Locale.CHINESE));
    }

    public void test_matchLocale_edgeCases() {
        // Test with null request locale
        assertTrue(labelTypeHelper.matchLocale(null, Locale.ROOT));
        assertFalse(labelTypeHelper.matchLocale(null, Locale.ENGLISH));

        // Test with different countries
        assertFalse(labelTypeHelper.matchLocale(Locale.US, Locale.UK));

        // Test with same language but different country
        Locale enUS = new Locale("en", "US");
        Locale enGB = new Locale("en", "GB");
        assertFalse(labelTypeHelper.matchLocale(enUS, enGB));

        // Test with same language, target has no country
        Locale en = new Locale("en");
        assertTrue(labelTypeHelper.matchLocale(enUS, en));
    }

    public void test_getLabelTypeItemList_searchRequestType() {
        List<Map<String, String>> result = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH);
        assertNotNull(result);
    }

    public void test_getLabelTypeItemList_searchRequestTypeAndLocale() {
        List<Map<String, String>> result = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH, Locale.ENGLISH);
        assertNotNull(result);
    }

    public void test_getLabelTypeItemList_nullLabelTypeItemList() {
        // Test when labelTypeItemList is null (triggers init)
        List<Map<String, String>> result = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH, Locale.ROOT);
        assertNotNull(result);
    }

    public void test_getMatchedLabelValueSet() {
        String path = "/test/path";
        Set<String> result = labelTypeHelper.getMatchedLabelValueSet(path);
        assertNotNull(result);
    }

    public void test_getMatchedLabelValueSet_emptyPatternList() {
        // Test with empty pattern list
        Set<String> result = labelTypeHelper.getMatchedLabelValueSet("/nonexistent/path");
        assertNotNull(result);
    }

    public void test_getMatchedLabelValueSet_nullPath() {
        try {
            labelTypeHelper.getMatchedLabelValueSet(null);
            fail("Should handle null path gracefully or throw appropriate exception");
        } catch (Exception e) {
            // Expected behavior for null path
            assertNotNull(e);
        }
    }

    public void test_buildLabelTypeItems() {
        List<LabelType> labelTypeList = createTestLabelTypeList();

        try {
            labelTypeHelper.refresh(labelTypeList);
            // Method is protected, so we test it indirectly through refresh
        } catch (Exception e) {
            fail("buildLabelTypeItems() should not throw an exception: " + e.getMessage());
        }
    }

    public void test_buildLabelTypePatternList() {
        List<LabelType> labelTypeList = createTestLabelTypeList();

        try {
            labelTypeHelper.refresh(labelTypeList);
            // Method is protected, so we test it indirectly through refresh
        } catch (Exception e) {
            fail("buildLabelTypePatternList() should not throw an exception: " + e.getMessage());
        }
    }

    public void test_labelTypePattern_constructor() {
        try {
            LabelTypePattern pattern = new LabelTypePattern("test", "/test.*", "/exclude.*");
            assertNotNull(pattern);
            assertEquals("test", pattern.getValue());
        } catch (Exception e) {
            fail("LabelTypePattern constructor should not throw an exception: " + e.getMessage());
        }
    }

    public void test_labelTypePattern_constructor_nullPaths() {
        try {
            LabelTypePattern pattern = new LabelTypePattern("test", null, null);
            assertNotNull(pattern);
            assertEquals("test", pattern.getValue());
        } catch (Exception e) {
            fail("LabelTypePattern constructor with null paths should not throw an exception: " + e.getMessage());
        }
    }

    public void test_labelTypePattern_constructor_emptyPaths() {
        try {
            LabelTypePattern pattern = new LabelTypePattern("test", "", "");
            assertNotNull(pattern);
            assertEquals("test", pattern.getValue());
        } catch (Exception e) {
            fail("LabelTypePattern constructor with empty paths should not throw an exception: " + e.getMessage());
        }
    }

    public void test_labelTypePattern_match() {
        LabelTypePattern pattern = new LabelTypePattern("test", "/test.*", "/exclude.*");

        // Should match included path
        assertTrue(pattern.match("/test/path"));

        // Should not match excluded path
        assertFalse(pattern.match("/exclude/path"));

        // Should not match non-included path
        assertFalse(pattern.match("/other/path"));
    }

    public void test_labelTypePattern_match_onlyIncluded() {
        LabelTypePattern pattern = new LabelTypePattern("test", "/test.*", null);

        // Should match included path
        assertTrue(pattern.match("/test/path"));

        // Should not match non-included path
        assertFalse(pattern.match("/other/path"));
    }

    public void test_labelTypePattern_match_onlyExcluded() {
        LabelTypePattern pattern = new LabelTypePattern("test", null, "/exclude.*");

        // Should match anything except excluded
        assertTrue(pattern.match("/test/path"));

        // Should not match excluded path
        assertFalse(pattern.match("/exclude/path"));
    }

    public void test_labelTypePattern_match_noPatterns() {
        LabelTypePattern pattern = new LabelTypePattern("test", null, null);

        // Should match everything when no patterns
        assertTrue(pattern.match("/any/path"));
        assertTrue(pattern.match("/test/path"));
    }

    public void test_labelTypePattern_match_multilinePaths() {
        LabelTypePattern pattern = new LabelTypePattern("test", "/test.*\n/another.*", "/exclude.*\n/deny.*");

        // Should match first included path
        assertTrue(pattern.match("/test/path"));

        // Should match second included path
        assertTrue(pattern.match("/another/path"));

        // Should not match first excluded path
        assertFalse(pattern.match("/exclude/path"));

        // Should not match second excluded path
        assertFalse(pattern.match("/deny/path"));
    }

    public void test_labelTypeHelper_inheritance() {
        assertTrue(labelTypeHelper instanceof AbstractConfigHelper);
    }

    public void test_load_returnValue() {
        int result = labelTypeHelper.load();
        assertEquals(2, result); // Based on mock data
    }

    public void test_getMatchedLabelValueSet_multipleMatches() {
        // Load some test data first
        labelTypeHelper.load();

        Set<String> result = labelTypeHelper.getMatchedLabelValueSet("/test/path");
        assertNotNull(result);
        // Result depends on test data configuration
    }

    // Helper method to create test data
    private List<LabelType> createTestLabelTypeList() {
        List<LabelType> labelTypeList = new ArrayList<>();

        LabelType labelType1 = new LabelType();
        labelType1.setName("Test Label 1");
        labelType1.setValue("test1");
        labelType1.setPermissions(new String[0]);
        labelType1.setVirtualHost("");
        // Locale is derived from value, not set directly
        labelType1.setIncludedPaths("/test.*");
        labelType1.setExcludedPaths("/exclude.*");
        labelTypeList.add(labelType1);

        LabelType labelType2 = new LabelType();
        labelType2.setName("Test Label 2");
        labelType2.setValue("test2");
        labelType2.setPermissions(new String[] { "role1", "role2" });
        labelType2.setVirtualHost("example.com");
        // Locale is derived from value, not set directly
        labelType2.setIncludedPaths("/admin.*");
        labelType2.setExcludedPaths("");
        labelTypeList.add(labelType2);

        return labelTypeList;
    }

    // Mock classes
    private static class MockLabelTypeService extends LabelTypeService {
        @Override
        public List<LabelType> getLabelTypeList() {
            List<LabelType> labelTypeList = new ArrayList<>();

            LabelType labelType1 = new LabelType();
            labelType1.setName("Test Label 1");
            labelType1.setValue("test1");
            labelType1.setPermissions(new String[0]);
            labelType1.setVirtualHost("");
            // Locale is derived from value, not set directly
            labelType1.setIncludedPaths("/test.*");
            labelType1.setExcludedPaths("/exclude.*");
            labelTypeList.add(labelType1);

            LabelType labelType2 = new LabelType();
            labelType2.setName("Test Label 2");
            labelType2.setValue("test2");
            labelType2.setPermissions(new String[] { "role1" });
            labelType2.setVirtualHost("");
            // Locale is derived from value, not set directly
            labelType2.setIncludedPaths("/admin.*");
            labelType2.setExcludedPaths("");
            labelTypeList.add(labelType2);

            return labelTypeList;
        }
    }

    private static class MockVirtualHostHelper extends VirtualHostHelper {
        @Override
        public String getVirtualHostKey() {
            return ""; // Return empty string for testing
        }
    }

    private static class MockRoleQueryHelper extends RoleQueryHelper {
        @Override
        public Set<String> build(SearchRequestType searchRequestType) {
            // Return empty set for no roles
            return new HashSet<>();
        }
    }
}
