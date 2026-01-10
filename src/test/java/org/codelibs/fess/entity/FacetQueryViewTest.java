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
package org.codelibs.fess.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FacetQueryViewTest extends UnitFessTestCase {

    private FacetQueryView facetQueryView;
    private TestFacetInfo testFacetInfo;
    private TestFileTypeHelper testFileTypeHelper;
    private TestFessConfig testFessConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        facetQueryView = new FacetQueryView();
        testFacetInfo = new TestFacetInfo();
        testFileTypeHelper = new TestFileTypeHelper();
        testFessConfig = new TestFessConfig();

        ComponentUtil.register(testFacetInfo, "facetInfo");
        ComponentUtil.register(testFileTypeHelper, "fileTypeHelper");
        ComponentUtil.setFessConfig(testFessConfig);
    }

    @Override
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test constructor
    @Test
    public void test_constructor() {
        FacetQueryView view = new FacetQueryView();
        assertNotNull(view);
        assertNotNull(view.getQueryMap());
        assertTrue(view.getQueryMap().isEmpty());
        assertNull(view.getTitle());
    }

    // Test title getter and setter
    @Test
    public void test_getTitle() {
        assertNull(facetQueryView.getTitle());

        String testTitle = "Test Title";
        facetQueryView.setTitle(testTitle);
        assertEquals(testTitle, facetQueryView.getTitle());
    }

    @Test
    public void test_setTitle() {
        String title1 = "Title 1";
        facetQueryView.setTitle(title1);
        assertEquals(title1, facetQueryView.getTitle());

        String title2 = "Different Title";
        facetQueryView.setTitle(title2);
        assertEquals(title2, facetQueryView.getTitle());

        facetQueryView.setTitle(null);
        assertNull(facetQueryView.getTitle());
    }

    // Test query map operations
    @Test
    public void test_getQueryMap() {
        Map<String, String> queryMap = facetQueryView.getQueryMap();
        assertNotNull(queryMap);
        assertTrue(queryMap instanceof LinkedHashMap);
        assertTrue(queryMap.isEmpty());
    }

    @Test
    public void test_addQuery() {
        assertTrue(facetQueryView.getQueryMap().isEmpty());

        facetQueryView.addQuery("key1", "query1");
        assertEquals(1, facetQueryView.getQueryMap().size());
        assertEquals("query1", facetQueryView.getQueryMap().get("key1"));

        facetQueryView.addQuery("key2", "query2");
        assertEquals(2, facetQueryView.getQueryMap().size());
        assertEquals("query2", facetQueryView.getQueryMap().get("key2"));

        // Test overwriting existing key
        facetQueryView.addQuery("key1", "query1_updated");
        assertEquals(2, facetQueryView.getQueryMap().size());
        assertEquals("query1_updated", facetQueryView.getQueryMap().get("key1"));
    }

    // Test init() method without file type queries
    @Test
    public void test_init_withoutFileTypeQueries() {
        facetQueryView.addQuery("label1", "field1:value1");
        facetQueryView.addQuery("label2", "field2:value2");

        facetQueryView.init();

        // Should not modify query map when no filetype queries present
        assertEquals(2, facetQueryView.getQueryMap().size());
        assertEquals("field1:value1", facetQueryView.getQueryMap().get("label1"));
        assertEquals("field2:value2", facetQueryView.getQueryMap().get("label2"));

        // Check that queries were added to FacetInfo
        assertEquals(2, testFacetInfo.getAddedQueries().size());
        assertTrue(testFacetInfo.getAddedQueries().contains("field1:value1"));
        assertTrue(testFacetInfo.getAddedQueries().contains("field2:value2"));
    }

    // Test init() method with file type queries
    @Test
    public void test_init_withFileTypeQueries() {
        testFileTypeHelper.setTypes(new String[] { "pdf", "doc", "txt" });

        facetQueryView.addQuery("label1", "filetype:html");
        facetQueryView.addQuery("label2", "field2:value2");

        facetQueryView.init();

        Map<String, String> queryMap = facetQueryView.getQueryMap();

        // Should have original queries
        assertEquals("filetype:html", queryMap.get("label1"));
        assertEquals("field2:value2", queryMap.get("label2"));

        // Should add file type queries since there's a filetype query
        assertTrue(queryMap.size() > 2);
        // Keys might be labels.facet_filetype_* or uppercase file type
        assertTrue(queryMap.containsKey("labels.facet_filetype_pdf") || queryMap.containsKey("PDF"));
        assertTrue(queryMap.containsKey("DOC") || queryMap.containsKey("labels.facet_filetype_doc"));
        assertTrue(queryMap.containsKey("labels.facet_filetype_txt") || queryMap.containsKey("TXT"));

        // Check values
        if (queryMap.containsKey("labels.facet_filetype_pdf")) {
            assertEquals("filetype:pdf", queryMap.get("labels.facet_filetype_pdf"));
        } else {
            assertEquals("filetype:pdf", queryMap.get("PDF"));
        }

        if (queryMap.containsKey("DOC")) {
            assertEquals("filetype:doc", queryMap.get("DOC"));
        } else {
            assertEquals("filetype:doc", queryMap.get("labels.facet_filetype_doc"));
        }

        if (queryMap.containsKey("labels.facet_filetype_txt")) {
            assertEquals("filetype:txt", queryMap.get("labels.facet_filetype_txt"));
        } else {
            assertEquals("filetype:txt", queryMap.get("TXT"));
        }

        // Should have 'others' at the end
        assertEquals("filetype:others", queryMap.get("labels.facet_filetype_others"));

        // Check all unique queries were added to FacetInfo
        assertTrue(testFacetInfo.getAddedQueries().contains("filetype:html"));
        assertTrue(testFacetInfo.getAddedQueries().contains("field2:value2"));
        assertTrue(testFacetInfo.getAddedQueries().contains("filetype:pdf"));
        assertTrue(testFacetInfo.getAddedQueries().contains("filetype:doc"));
        assertTrue(testFacetInfo.getAddedQueries().contains("filetype:txt"));
        assertTrue(testFacetInfo.getAddedQueries().contains("filetype:others"));
    }

    // Test init() with duplicate file type values
    @Test
    public void test_init_withDuplicateFileTypeValues() {
        testFileTypeHelper.setTypes(new String[] { "pdf", "doc" });

        // Add existing file type query
        facetQueryView.addQuery("existing_pdf", "filetype:pdf");
        facetQueryView.addQuery("label1", "filetype:html");

        facetQueryView.init();

        Map<String, String> queryMap = facetQueryView.getQueryMap();

        // Should not duplicate existing pdf query
        assertEquals("filetype:pdf", queryMap.get("existing_pdf"));
        // Should add doc since it's not present
        assertEquals("filetype:doc", queryMap.get("DOC"));

        // Check no duplicate queries added to FacetInfo
        List<String> addedQueries = testFacetInfo.getAddedQueries();
        long pdfCount = addedQueries.stream().filter(q -> q.equals("filetype:pdf")).count();
        assertEquals(1, pdfCount);
    }

    // Test init() with empty file types
    @Test
    public void test_init_withEmptyFileTypes() {
        testFileTypeHelper.setTypes(new String[] {});

        facetQueryView.addQuery("label1", "filetype:html");

        facetQueryView.init();

        Map<String, String> queryMap = facetQueryView.getQueryMap();

        // Should only have original query and others
        assertEquals(2, queryMap.size());
        assertEquals("filetype:html", queryMap.get("label1"));
        assertEquals("filetype:others", queryMap.get("labels.facet_filetype_others"));
    }

    // Test toString() method
    @Test
    public void test_toString() {
        facetQueryView.setTitle("Test Title");
        facetQueryView.addQuery("key1", "query1");
        facetQueryView.addQuery("key2", "query2");

        String result = facetQueryView.toString();
        assertNotNull(result);
        assertTrue(result.contains("FacetQueryView"));
        assertTrue(result.contains("title=Test Title"));
        assertTrue(result.contains("queryMap="));
        assertTrue(result.contains("key1=query1"));
        assertTrue(result.contains("key2=query2"));
    }

    @Test
    public void test_toString_withNullTitle() {
        facetQueryView.addQuery("key1", "query1");

        String result = facetQueryView.toString();
        assertNotNull(result);
        assertTrue(result.contains("title=null"));
        assertTrue(result.contains("key1=query1"));
    }

    @Test
    public void test_toString_withEmptyQueryMap() {
        facetQueryView.setTitle("Empty Map Test");

        String result = facetQueryView.toString();
        assertNotNull(result);
        assertTrue(result.contains("title=Empty Map Test"));
        assertTrue(result.contains("queryMap={}"));
    }

    // Test edge cases
    @Test
    public void test_addQuery_withNullValues() {
        facetQueryView.addQuery(null, "query1");
        assertEquals("query1", facetQueryView.getQueryMap().get(null));

        facetQueryView.addQuery("key1", null);
        assertNull(facetQueryView.getQueryMap().get("key1"));
    }

    @Test
    public void test_init_multipleCallsShouldNotDuplicate() {
        testFileTypeHelper.setTypes(new String[] { "pdf" });
        facetQueryView.addQuery("label1", "filetype:html");

        facetQueryView.init();
        int firstSize = facetQueryView.getQueryMap().size();
        int firstQueriesCount = testFacetInfo.getAddedQueries().size();

        // Call init again
        facetQueryView.init();

        // Size should remain the same
        assertEquals(firstSize, facetQueryView.getQueryMap().size());
        // But queries would be added again to FacetInfo (distinct handles duplicates)
        assertTrue(testFacetInfo.getAddedQueries().size() >= firstQueriesCount);
    }

    // Test complex scenarios
    @Test
    public void test_init_withMixedQueries() {
        testFileTypeHelper.setTypes(new String[] { "pdf", "doc", "xls" });

        facetQueryView.addQuery("label1", "filetype:html");
        facetQueryView.addQuery("label2", "field:value");
        facetQueryView.addQuery("label3", "anotherfield:anothervalue");
        facetQueryView.addQuery("existing_doc", "filetype:doc");

        facetQueryView.init();

        Map<String, String> queryMap = facetQueryView.getQueryMap();

        // Should have original non-filetype queries
        assertEquals("field:value", queryMap.get("label2"));
        assertEquals("anotherfield:anothervalue", queryMap.get("label3"));

        // Should have filetype queries
        assertEquals("filetype:html", queryMap.get("label1"));
        assertEquals("filetype:doc", queryMap.get("existing_doc"));

        // Check for PDF - could be either key format
        if (queryMap.containsKey("labels.facet_filetype_pdf")) {
            assertEquals("filetype:pdf", queryMap.get("labels.facet_filetype_pdf"));
        } else {
            assertEquals("filetype:pdf", queryMap.get("PDF"));
        }

        // Check for XLS - could be either key format
        if (queryMap.containsKey("labels.facet_filetype_xls")) {
            assertEquals("filetype:xls", queryMap.get("labels.facet_filetype_xls"));
        } else {
            assertEquals("filetype:xls", queryMap.get("XLS"));
        }

        // DOC type won't be duplicated since it already exists
        assertFalse(queryMap.containsKey("DOC"));
        assertFalse(queryMap.containsKey("labels.facet_filetype_doc"));
        assertEquals("filetype:others", queryMap.get("labels.facet_filetype_others"));

        // Verify all unique values added to FacetInfo
        List<String> uniqueQueries = new ArrayList<>();
        uniqueQueries.add("filetype:html");
        uniqueQueries.add("field:value");
        uniqueQueries.add("anotherfield:anothervalue");
        uniqueQueries.add("filetype:doc");
        uniqueQueries.add("filetype:pdf");
        uniqueQueries.add("filetype:xls");
        uniqueQueries.add("filetype:others");

        for (String query : uniqueQueries) {
            assertTrue(testFacetInfo.getAddedQueries().contains(query));
        }
    }

    // Helper classes for testing
    private static class TestFacetInfo extends FacetInfo {
        private final List<String> addedQueries = new ArrayList<>();

        @Override
        public void addQuery(String s) {
            addedQueries.add(s);
        }

        public List<String> getAddedQueries() {
            return addedQueries;
        }
    }

    private static class TestFileTypeHelper extends FileTypeHelper {
        private String[] types = new String[0];

        @Override
        public String[] getTypes() {
            return types;
        }

        public void setTypes(String[] types) {
            this.types = types;
        }
    }

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String getIndexFieldFiletype() {
            return "filetype";
        }

        @Override
        public String getPasswordInvalidAdminPasswords() {
            return "admin,password,123456";
        }

        @Override
        public String getPluginVersionFilter() {
            return "";
        }

        @Override
        public String getPluginRepositories() {
            return "";
        }

        @Override
        public String getStorageMaxItemsInPage() {
            return "25";
        }

        @Override
        public Integer getStorageMaxItemsInPageAsInteger() {
            return 25;
        }

        @Override
        public String getLdapAttrHomeDirectory() {
            return "homeDirectory";
        }

        @Override
        public String getLdapAttrGidNumber() {
            return "gidNumber";
        }

        @Override
        public String getLdapAttrUidNumber() {
            return "uidNumber";
        }

        @Override
        public String getLdapAttrDepartmentNumber() {
            return "departmentNumber";
        }

        @Override
        public String getLdapAttrPreferredLanguage() {
            return "preferredLanguage";
        }

        @Override
        public String getLdapAttrDisplayName() {
            return "displayName";
        }

        @Override
        public String getLdapAttrRegisteredAddress() {
            return "registeredAddress";
        }

        @Override
        public String getLdapAttrMail() {
            return "mail";
        }

        @Override
        public String getLdapAttrGivenName() {
            return "givenName";
        }

        @Override
        public String getLdapAttrSurname() {
            return "sn";
        }

        @Override
        public String getLdapAttrBusinessCategory() {
            return "businessCategory";
        }

        @Override
        public Integer getPluginVersionFilterAsInteger() {
            return null;
        }

        @Override
        public String getLdapAttrTeletexTerminalIdentifier() {
            return "teletexTerminalIdentifier";
        }

        @Override
        public String getLdapAttrCity() {
            return "l";
        }

        @Override
        public String getLdapAttrPostalAddress() {
            return "postalAddress";
        }

        @Override
        public String getLdapAttrMobile() {
            return "mobile";
        }

        @Override
        public String getLdapAttrCarLicense() {
            return "carLicense";
        }

        @Override
        public String getLdapAttrInitials() {
            return "initials";
        }

        @Override
        public String getLdapAttrPostOfficeBox() {
            return "postOfficeBox";
        }

        @Override
        public String getLdapAttrFacsimileTelephoneNumber() {
            return "facsimileTelephoneNumber";
        }

        @Override
        public String getLdapAttrEmployeeType() {
            return "employeeType";
        }

        @Override
        public String getLdapAttrState() {
            return "st";
        }

        @Override
        public String getLdapAttrInternationaliSDNNumber() {
            return "internationaliSDNNumber";
        }

        @Override
        public String getLdapAttrDestinationIndicator() {
            return "destinationIndicator";
        }

        @Override
        public String getLdapAttrPhysicalDeliveryOfficeName() {
            return "physicalDeliveryOfficeName";
        }

        @Override
        public String getLdapAttrPostalCode() {
            return "postalCode";
        }

        @Override
        public String getLdapAttrStreet() {
            return "street";
        }

        @Override
        public String getLdapAttrPager() {
            return "pager";
        }

        @Override
        public String getLdapAttrTitle() {
            return "title";
        }

        @Override
        public String getLdapAttrDescription() {
            return "description";
        }

        @Override
        public String getLdapAttrRoomNumber() {
            return "roomNumber";
        }
    }
}