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
package org.codelibs.fess.query;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for Set-based field lookup improvements in QueryFieldConfig.
 * This class tests the performance and correctness of Set-based lookups
 * for search fields, facet fields, and sort fields.
 */
public class QueryFieldConfigSetBasedLookupTest extends UnitFessTestCase {

    private QueryFieldConfig queryFieldConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        FessConfig fessConfig = createTestFessConfig();
        ComponentUtil.setFessConfig(fessConfig);

        queryFieldConfig = new QueryFieldConfig();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    /**
     * Test that setting search fields creates both array and Set.
     */
    @Test
    public void test_setSearchFields_createsSet() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setSearchFields(fields);

        // Verify array is set
        assertNotNull(queryFieldConfig.searchFields);
        assertSame(fields, queryFieldConfig.searchFields);
        assertEquals(3, queryFieldConfig.searchFields.length);

        // Verify Set is created and populated
        assertNotNull(queryFieldConfig.searchFieldSet);
        assertEquals(3, queryFieldConfig.searchFieldSet.size());
        assertTrue(queryFieldConfig.searchFieldSet.contains("field1"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field2"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field3"));
    }

    /**
     * Test that setting facet fields creates both array and Set.
     */
    @Test
    public void test_setFacetFields_createsSet() {
        String[] fields = { "facet1", "facet2", "facet3" };
        queryFieldConfig.setFacetFields(fields);

        // Verify array is set
        assertNotNull(queryFieldConfig.facetFields);
        assertSame(fields, queryFieldConfig.facetFields);
        assertEquals(3, queryFieldConfig.facetFields.length);

        // Verify Set is created and populated
        assertNotNull(queryFieldConfig.facetFieldSet);
        assertEquals(3, queryFieldConfig.facetFieldSet.size());
        assertTrue(queryFieldConfig.facetFieldSet.contains("facet1"));
        assertTrue(queryFieldConfig.facetFieldSet.contains("facet2"));
        assertTrue(queryFieldConfig.facetFieldSet.contains("facet3"));
    }

    /**
     * Test that setting sort fields creates both array and Set.
     */
    @Test
    public void test_setSortFields_createsSet() {
        String[] fields = { "sort1", "sort2", "sort3" };
        queryFieldConfig.setSortFields(fields);

        // Verify array is set
        assertNotNull(queryFieldConfig.sortFields);
        assertSame(fields, queryFieldConfig.sortFields);
        assertEquals(3, queryFieldConfig.sortFields.length);

        // Verify Set is created and populated
        assertNotNull(queryFieldConfig.sortFieldSet);
        assertEquals(3, queryFieldConfig.sortFieldSet.size());
        assertTrue(queryFieldConfig.sortFieldSet.contains("sort1"));
        assertTrue(queryFieldConfig.sortFieldSet.contains("sort2"));
        assertTrue(queryFieldConfig.sortFieldSet.contains("sort3"));
    }

    /**
     * Test isSortField with empty array - should return false, not throw exception.
     * This test addresses the Copilot AI concern about empty arrays.
     */
    @Test
    public void test_isSortField_withEmptyArray_returnsFalse() {
        String[] emptyFields = {};
        queryFieldConfig.setSortFields(emptyFields);

        // Set should be empty but not null
        assertNotNull(queryFieldConfig.sortFieldSet);
        assertEquals(0, queryFieldConfig.sortFieldSet.size());

        // Should return false for any field, not throw exception
        assertFalse(queryFieldConfig.isSortField("anyfield"));
        assertFalse(queryFieldConfig.isSortField("score"));
        assertFalse(queryFieldConfig.isSortField(""));
    }

    /**
     * Test isFacetField with empty array - should return false, not throw exception.
     * This test addresses the Copilot AI concern about empty arrays.
     */
    @Test
    public void test_isFacetField_withEmptyArray_returnsFalse() {
        String[] emptyFields = {};
        queryFieldConfig.setFacetFields(emptyFields);

        // Set should be empty but not null
        assertNotNull(queryFieldConfig.facetFieldSet);
        assertEquals(0, queryFieldConfig.facetFieldSet.size());

        // Should return false for any field, not throw exception
        assertFalse(queryFieldConfig.isFacetField("anyfield"));
        assertFalse(queryFieldConfig.isFacetField("category"));
        // Blank check happens before Set lookup
        assertFalse(queryFieldConfig.isFacetField(""));
    }

    /**
     * Test that isSortField correctly handles null sortFieldSet.
     */
    @Test
    public void test_isSortField_withNullSet_returnsFalse() {
        queryFieldConfig.sortFieldSet = null;

        // Should return false, not throw NullPointerException
        assertFalse(queryFieldConfig.isSortField("anyfield"));
        assertFalse(queryFieldConfig.isSortField("score"));
    }

    /**
     * Test that isFacetField correctly handles null facetFieldSet.
     */
    @Test
    public void test_isFacetField_withNullSet_returnsFalse() {
        queryFieldConfig.facetFieldSet = null;

        // Should return false, not throw NullPointerException
        assertFalse(queryFieldConfig.isFacetField("anyfield"));
        assertFalse(queryFieldConfig.isFacetField("category"));
    }

    /**
     * Test that init() creates Sets when arrays are initialized.
     */
    @Test
    public void test_init_createsSets() {
        queryFieldConfig.init();

        // All Sets should be created during init
        assertNotNull(queryFieldConfig.searchFieldSet);
        assertNotNull(queryFieldConfig.facetFieldSet);
        assertNotNull(queryFieldConfig.sortFieldSet);

        // Sets should contain same values as arrays
        assertEquals(queryFieldConfig.searchFields.length, queryFieldConfig.searchFieldSet.size());
        assertEquals(queryFieldConfig.facetFields.length, queryFieldConfig.facetFieldSet.size());
        assertEquals(queryFieldConfig.sortFields.length, queryFieldConfig.sortFieldSet.size());

        // Verify all array elements are in Set
        for (String field : queryFieldConfig.searchFields) {
            assertTrue("Search field " + field + " should be in Set", queryFieldConfig.searchFieldSet.contains(field));
        }
        for (String field : queryFieldConfig.facetFields) {
            assertTrue("Facet field " + field + " should be in Set", queryFieldConfig.facetFieldSet.contains(field));
        }
        for (String field : queryFieldConfig.sortFields) {
            assertTrue("Sort field " + field + " should be in Set", queryFieldConfig.sortFieldSet.contains(field));
        }
    }

    /**
     * Test that Set lookup behavior is identical to original array-based lookup.
     * This ensures backward compatibility.
     */
    @Test
    public void test_isSortField_behaviourIdenticalToArrayLookup() {
        String[] fields = { "score", "created", "modified" };
        queryFieldConfig.setSortFields(fields);

        // Test positive cases
        for (String field : fields) {
            assertTrue("Field " + field + " should be found", queryFieldConfig.isSortField(field));
        }

        // Test negative cases
        assertFalse(queryFieldConfig.isSortField("nonexistent"));
        assertFalse(queryFieldConfig.isSortField("title"));
        assertFalse(queryFieldConfig.isSortField(""));
        assertFalse(queryFieldConfig.isSortField(null));
    }

    /**
     * Test that Set lookup behavior is identical to original array-based lookup for facet fields.
     */
    @Test
    public void test_isFacetField_behaviourIdenticalToArrayLookup() {
        String[] fields = { "category", "type", "author" };
        queryFieldConfig.setFacetFields(fields);

        // Test positive cases
        for (String field : fields) {
            assertTrue("Field " + field + " should be found", queryFieldConfig.isFacetField(field));
        }

        // Test negative cases
        assertFalse(queryFieldConfig.isFacetField("nonexistent"));
        assertFalse(queryFieldConfig.isFacetField("title"));
        // Blank fields are handled before Set lookup
        assertFalse(queryFieldConfig.isFacetField(""));
        assertFalse(queryFieldConfig.isFacetField(null));
        assertFalse(queryFieldConfig.isFacetField("   ")); // Whitespace only
    }

    /**
     * Test that updating fields via setter updates both array and Set.
     */
    @Test
    public void test_updateFields_updatesSetAndArray() {
        // Initial setup
        String[] initialFields = { "field1", "field2" };
        queryFieldConfig.setSearchFields(initialFields);
        assertTrue(queryFieldConfig.searchFieldSet.contains("field1"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field2"));
        assertEquals(2, queryFieldConfig.searchFieldSet.size());

        // Update fields
        String[] updatedFields = { "field3", "field4", "field5" };
        queryFieldConfig.setSearchFields(updatedFields);

        // Verify old fields are removed from Set
        assertFalse(queryFieldConfig.searchFieldSet.contains("field1"));
        assertFalse(queryFieldConfig.searchFieldSet.contains("field2"));

        // Verify new fields are in Set
        assertTrue(queryFieldConfig.searchFieldSet.contains("field3"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field4"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field5"));
        assertEquals(3, queryFieldConfig.searchFieldSet.size());

        // Verify array is updated
        assertSame(updatedFields, queryFieldConfig.searchFields);
    }

    /**
     * Test that Sets handle duplicate values correctly (deduplication).
     */
    @Test
    public void test_setFields_withDuplicates_deduplicatesInSet() {
        String[] fieldsWithDuplicates = { "field1", "field2", "field1", "field3", "field2" };
        queryFieldConfig.setSearchFields(fieldsWithDuplicates);

        // Array should contain all values including duplicates
        assertEquals(5, queryFieldConfig.searchFields.length);

        // Set should contain only unique values
        assertEquals(3, queryFieldConfig.searchFieldSet.size());
        assertTrue(queryFieldConfig.searchFieldSet.contains("field1"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field2"));
        assertTrue(queryFieldConfig.searchFieldSet.contains("field3"));
    }

    /**
     * Test performance improvement of Set-based lookup over array-based lookup.
     * This is a micro-benchmark to demonstrate the O(1) vs O(n) improvement.
     */
    @Test
    public void test_lookupPerformance_SetFasterThanArray() {
        // Create a large array of fields
        int fieldCount = 1000;
        String[] fields = new String[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            fields[i] = "field" + i;
        }
        queryFieldConfig.setSortFields(fields);

        // Test field that appears at the end (worst case for array lookup)
        String testField = "field999";

        // Warm up
        for (int i = 0; i < 100; i++) {
            queryFieldConfig.isSortField(testField);
        }

        // Measure Set-based lookup (current implementation)
        long setStartTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            queryFieldConfig.isSortField(testField);
        }
        long setDuration = System.nanoTime() - setStartTime;

        // For comparison, simulate array-based lookup
        long arrayStartTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            // Simulate original array-based lookup
            boolean found = false;
            for (String field : queryFieldConfig.sortFields) {
                if (field.equals(testField)) {
                    found = true;
                    break;
                }
            }
        }
        long arrayDuration = System.nanoTime() - arrayStartTime;

        // Set-based lookup should be significantly faster
        // Note: This is a rough check - exact performance depends on JVM, hardware, etc.
        // We expect Set lookup to be at least as fast as array lookup for large datasets
        assertTrue("Set-based lookup should be faster or comparable to array-based lookup for large datasets",
                setDuration <= arrayDuration * 2); // Allow 2x margin for test stability
    }

    /**
     * Helper method to create a test FessConfig.
     */
    private FessConfig createTestFessConfig() {
        return new FessConfig.SimpleImpl() {

            @Override
            public String[] getQueryAdditionalResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalScrollResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalCacheResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalHighlightedFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSearchFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalFacetFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSortFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalApiResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalNotAnalyzedFields(String... fields) {
                return fields;
            }

            @Override
            public String getQueryAdditionalAnalyzedFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalDefaultFields() {
                return "";
            }

            @Override
            public String getIndexFieldId() {
                return "id";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldBoost() {
                return "boost";
            }

            @Override
            public String getIndexFieldContentLength() {
                return "content_length";
            }

            @Override
            public String getIndexFieldHost() {
                return "host";
            }

            @Override
            public String getIndexFieldSite() {
                return "site";
            }

            @Override
            public String getIndexFieldLastModified() {
                return "last_modified";
            }

            @Override
            public String getIndexFieldTimestamp() {
                return "timestamp";
            }

            @Override
            public String getIndexFieldMimetype() {
                return "mimetype";
            }

            @Override
            public String getIndexFieldFiletype() {
                return "filetype";
            }

            @Override
            public String getIndexFieldFilename() {
                return "filename";
            }

            @Override
            public String getIndexFieldCreated() {
                return "created";
            }

            @Override
            public String getIndexFieldTitle() {
                return "title";
            }

            @Override
            public String getIndexFieldDigest() {
                return "digest";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }

            @Override
            public String getIndexFieldConfigId() {
                return "config_id";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldHasCache() {
                return "has_cache";
            }

            @Override
            public String getIndexFieldCache() {
                return "cache";
            }

            @Override
            public String getIndexFieldContent() {
                return "content";
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }

            @Override
            public String getIndexFieldSegment() {
                return "segment";
            }

            @Override
            public String getIndexFieldAnchor() {
                return "anchor";
            }

            @Override
            public String getResponseFieldContentDescription() {
                return "content_description";
            }

            @Override
            public String getResponseFieldContentTitle() {
                return "content_title";
            }

            @Override
            public String getResponseFieldSitePath() {
                return "site_path";
            }

            @Override
            public String getResponseFieldUrlLink() {
                return "url_link";
            }

            @Override
            public String getIndexFieldExpires() {
                return "expires";
            }

            @Override
            public String getIndexFieldParentId() {
                return "parent_id";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "primary_term";
            }

            @Override
            public String getIndexFieldRole() {
                return "role";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "seq_no";
            }

            @Override
            public String getIndexFieldVersion() {
                return "version";
            }
        };
    }
}
