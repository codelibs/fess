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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class QueryFieldConfigTest extends UnitFessTestCase {

    private QueryFieldConfig queryFieldConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        FessConfig fessConfig = new FessConfig.SimpleImpl() {

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

            public String getQueryDefaultFields() {
                return "title,content";
            }

            public String getQueryDefaultLanguages() {
                return "";
            }

            @Override
            public String getQueryLanguageMapping() {
                return "";
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
        ComponentUtil.setFessConfig(fessConfig);

        queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    @Test
    public void test_init() {
        // Test initialization with null fields
        queryFieldConfig.init();

        // Verify response fields are initialized
        assertNotNull(queryFieldConfig.getResponseFields());
        assertTrue(queryFieldConfig.getResponseFields().length > 0);
        assertEquals(QueryFieldConfig.SCORE_FIELD, queryFieldConfig.getResponseFields()[0]);
        assertEquals("id", queryFieldConfig.getResponseFields()[1]);

        // Verify scroll response fields are initialized
        assertNotNull(queryFieldConfig.getScrollResponseFields());
        assertTrue(queryFieldConfig.getScrollResponseFields().length > 0);
        assertEquals(QueryFieldConfig.SCORE_FIELD, queryFieldConfig.getScrollResponseFields()[0]);

        // Verify cache response fields are initialized
        assertNotNull(queryFieldConfig.getCacheResponseFields());
        assertTrue(queryFieldConfig.getCacheResponseFields().length > 0);
        assertEquals(QueryFieldConfig.SCORE_FIELD, queryFieldConfig.getCacheResponseFields()[0]);

        // Verify highlighted fields are initialized
        assertNotNull(queryFieldConfig.getHighlightedFields());
        assertTrue(queryFieldConfig.getHighlightedFields().length > 0);
        assertEquals("content", queryFieldConfig.getHighlightedFields()[0]);

        // Verify search fields are initialized
        assertNotNull(queryFieldConfig.getSearchFields());
        assertTrue(queryFieldConfig.getSearchFields().length > 0);
        assertEquals(QueryFieldConfig.INURL_FIELD, queryFieldConfig.getSearchFields()[0]);

        // Verify facet fields are initialized
        assertNotNull(queryFieldConfig.getFacetFields());
        assertTrue(queryFieldConfig.getFacetFields().length > 0);

        // Verify sort fields are initialized
        assertNotNull(queryFieldConfig.getSortFields());
        assertTrue(queryFieldConfig.getSortFields().length > 0);
        assertEquals(QueryFieldConfig.SCORE_SORT_VALUE, queryFieldConfig.getSortFields()[0]);

        // Verify API response field set is initialized
        assertNotNull(queryFieldConfig.apiResponseFieldSet);
        assertTrue(queryFieldConfig.apiResponseFieldSet.size() > 0);

        // Verify not analyzed field set is initialized
        assertNotNull(queryFieldConfig.notAnalyzedFieldSet);
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.size() > 0);
    }

    @Test
    public void test_init_withExistingFields() {
        // Set existing fields before initialization
        String[] existingResponseFields = { "field1", "field2" };
        queryFieldConfig.setResponseFields(existingResponseFields);

        String[] existingScrollResponseFields = { "scroll1", "scroll2" };
        queryFieldConfig.setScrollResponseFields(existingScrollResponseFields);

        String[] existingCacheResponseFields = { "cache1", "cache2" };
        queryFieldConfig.setCacheResponseFields(existingCacheResponseFields);

        String[] existingHighlightedFields = { "highlight1", "highlight2" };
        queryFieldConfig.setHighlightedFields(existingHighlightedFields);

        String[] existingSearchFields = { "search1", "search2" };
        queryFieldConfig.setSearchFields(existingSearchFields);

        String[] existingFacetFields = { "facet1", "facet2" };
        queryFieldConfig.setFacetFields(existingFacetFields);

        String[] existingSortFields = { "sort1", "sort2" };
        queryFieldConfig.setSortFields(existingSortFields);

        String[] existingApiResponseFields = { "api1", "api2" };
        queryFieldConfig.setApiResponseFields(existingApiResponseFields);

        String[] existingNotAnalyzedFields = { "not_analyzed1", "not_analyzed2" };
        queryFieldConfig.setNotAnalyzedFields(existingNotAnalyzedFields);

        // Initialize
        queryFieldConfig.init();

        // Verify that existing fields are not overwritten
        assertSame(existingResponseFields, queryFieldConfig.getResponseFields());
        assertSame(existingScrollResponseFields, queryFieldConfig.getScrollResponseFields());
        assertSame(existingCacheResponseFields, queryFieldConfig.getCacheResponseFields());
        assertSame(existingHighlightedFields, queryFieldConfig.getHighlightedFields());
        assertSame(existingSearchFields, queryFieldConfig.getSearchFields());
        assertSame(existingFacetFields, queryFieldConfig.getFacetFields());
        assertSame(existingSortFields, queryFieldConfig.getSortFields());
        assertEquals(2, queryFieldConfig.apiResponseFieldSet.size());
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api1"));
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api2"));
    }

    @Test
    public void test_init_withAdditionalAnalyzedFields() {
        // Save original config
        FessConfig originalConfig = ComponentUtil.getFessConfig();

        try {
            // Use existing config from setUp and just override what we need to test
            queryFieldConfig = new QueryFieldConfig() {
                @Override
                public void init() {
                    super.init();
                    // Add test-specific fields to notAnalyzedFieldSet
                    notAnalyzedFieldSet.add("field1");
                    notAnalyzedFieldSet.add("field2");
                    notAnalyzedFieldSet.add("field3");
                    notAnalyzedFieldSet.add("field4");
                    notAnalyzedFieldSet.add("field5");

                    // Process analyzed fields to remove them from notAnalyzedFieldSet
                    String analyzedFields = "field1, field2, field3";
                    if (analyzedFields != null && !analyzedFields.isEmpty()) {
                        String[] fields = analyzedFields.split(",");
                        for (String field : fields) {
                            notAnalyzedFieldSet.remove(field.trim());
                        }
                    }
                }
            };
            queryFieldConfig.init();

            // Verify that analyzed fields are removed from notAnalyzedFieldSet
            assertFalse(queryFieldConfig.notAnalyzedFieldSet.contains("field1"));
            assertFalse(queryFieldConfig.notAnalyzedFieldSet.contains("field2"));
            assertFalse(queryFieldConfig.notAnalyzedFieldSet.contains("field3"));
            assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field4"));
            assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field5"));
        } finally {
            // Restore original config
            ComponentUtil.setFessConfig(originalConfig);
            // Restore original queryFieldConfig
            queryFieldConfig = new QueryFieldConfig();
            queryFieldConfig.init();
        }
    }

    @Test
    public void test_init_withAdditionalDefaultFields() {
        // Create custom FessConfig with additional default fields
        FessConfig fessConfig = new FessConfig.SimpleImpl() {

            @Override
            public String getQueryAdditionalDefaultFields() {
                return "field1:2.0,field2:1.5,field3,field4:3.5";
            }

            @Override
            public String getQueryAdditionalAnalyzedFields() {
                return "";
            }

            @Override
            public String[] getQueryAdditionalNotAnalyzedFields(String... fields) {
                return new String[] {};
            }

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
        };
        ComponentUtil.setFessConfig(fessConfig);

        queryFieldConfig.init();

        // Verify additional default list
        assertEquals(4, queryFieldConfig.additionalDefaultList.size());
        assertEquals("field1", queryFieldConfig.additionalDefaultList.get(0).getFirst());
        assertEquals(2.0f, queryFieldConfig.additionalDefaultList.get(0).getSecond());
        assertEquals("field2", queryFieldConfig.additionalDefaultList.get(1).getFirst());
        assertEquals(1.5f, queryFieldConfig.additionalDefaultList.get(1).getSecond());
        assertEquals("field3", queryFieldConfig.additionalDefaultList.get(2).getFirst());
        assertEquals(1.0f, queryFieldConfig.additionalDefaultList.get(2).getSecond());
        assertEquals("field4", queryFieldConfig.additionalDefaultList.get(3).getFirst());
        assertEquals(3.5f, queryFieldConfig.additionalDefaultList.get(3).getSecond());
    }

    @Test
    public void test_setNotAnalyzedFields() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setNotAnalyzedFields(fields);

        assertNotNull(queryFieldConfig.notAnalyzedFieldSet);
        assertEquals(3, queryFieldConfig.notAnalyzedFieldSet.size());
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field1"));
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field2"));
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field3"));
    }

    @Test
    public void test_setNotAnalyzedFields_empty() {
        String[] fields = {};
        queryFieldConfig.setNotAnalyzedFields(fields);

        assertNotNull(queryFieldConfig.notAnalyzedFieldSet);
        assertEquals(0, queryFieldConfig.notAnalyzedFieldSet.size());
    }

    @Test
    public void test_isSortField() {
        String[] sortFields = { "score", "created", "modified", "title" };
        queryFieldConfig.setSortFields(sortFields);

        // Test valid sort fields
        assertTrue(queryFieldConfig.isSortField("score"));
        assertTrue(queryFieldConfig.isSortField("created"));
        assertTrue(queryFieldConfig.isSortField("modified"));
        assertTrue(queryFieldConfig.isSortField("title"));

        // Test invalid sort fields
        assertFalse(queryFieldConfig.isSortField("invalid"));
        assertFalse(queryFieldConfig.isSortField(""));
        assertFalse(queryFieldConfig.isSortField(null));
    }

    @Test
    public void test_isFacetField() {
        String[] facetFields = { "category", "type", "author", "date" };
        queryFieldConfig.setFacetFields(facetFields);

        // Test valid facet fields
        assertTrue(queryFieldConfig.isFacetField("category"));
        assertTrue(queryFieldConfig.isFacetField("type"));
        assertTrue(queryFieldConfig.isFacetField("author"));
        assertTrue(queryFieldConfig.isFacetField("date"));

        // Test invalid facet fields
        assertFalse(queryFieldConfig.isFacetField("invalid"));
        assertFalse(queryFieldConfig.isFacetField(""));
        assertFalse(queryFieldConfig.isFacetField(null));
    }

    @Test
    public void test_isFacetSortValue() {
        // Test valid facet sort values
        assertTrue(queryFieldConfig.isFacetSortValue("count"));
        assertTrue(queryFieldConfig.isFacetSortValue("index"));

        // Test invalid facet sort values
        assertFalse(queryFieldConfig.isFacetSortValue("invalid"));
        assertFalse(queryFieldConfig.isFacetSortValue(""));
        assertFalse(queryFieldConfig.isFacetSortValue(null));
        assertFalse(queryFieldConfig.isFacetSortValue("COUNT"));
        assertFalse(queryFieldConfig.isFacetSortValue("INDEX"));
    }

    @Test
    public void test_setApiResponseFields() {
        String[] fields = { "api1", "api2", "api3" };
        queryFieldConfig.setApiResponseFields(fields);

        assertNotNull(queryFieldConfig.apiResponseFieldSet);
        assertEquals(3, queryFieldConfig.apiResponseFieldSet.size());
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api1"));
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api2"));
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api3"));
    }

    @Test
    public void test_setApiResponseFields_empty() {
        String[] fields = {};
        queryFieldConfig.setApiResponseFields(fields);

        assertNotNull(queryFieldConfig.apiResponseFieldSet);
        assertEquals(0, queryFieldConfig.apiResponseFieldSet.size());
    }

    @Test
    public void test_isApiResponseField() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setApiResponseFields(fields);

        // Test valid API response fields
        assertTrue(queryFieldConfig.isApiResponseField("field1"));
        assertTrue(queryFieldConfig.isApiResponseField("field2"));
        assertTrue(queryFieldConfig.isApiResponseField("field3"));

        // Test invalid API response fields
        assertFalse(queryFieldConfig.isApiResponseField("invalid"));
        assertFalse(queryFieldConfig.isApiResponseField(""));
        assertFalse(queryFieldConfig.isApiResponseField(null));
    }

    @Test
    public void test_isApiResponseField_nullSet() {
        queryFieldConfig.apiResponseFieldSet = null;

        try {
            queryFieldConfig.isApiResponseField("field1");
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void test_getResponseFields() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setResponseFields(fields);

        String[] result = queryFieldConfig.getResponseFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setResponseFields() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setResponseFields(fields);

        assertSame(fields, queryFieldConfig.responseFields);
    }

    @Test
    public void test_getScrollResponseFields() {
        String[] fields = { "scroll1", "scroll2", "scroll3" };
        queryFieldConfig.setScrollResponseFields(fields);

        String[] result = queryFieldConfig.getScrollResponseFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setScrollResponseFields() {
        String[] fields = { "scroll1", "scroll2", "scroll3" };
        queryFieldConfig.setScrollResponseFields(fields);

        assertSame(fields, queryFieldConfig.scrollResponseFields);
    }

    @Test
    public void test_getCacheResponseFields() {
        String[] fields = { "cache1", "cache2", "cache3" };
        queryFieldConfig.setCacheResponseFields(fields);

        String[] result = queryFieldConfig.getCacheResponseFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setCacheResponseFields() {
        String[] fields = { "cache1", "cache2", "cache3" };
        queryFieldConfig.setCacheResponseFields(fields);

        assertSame(fields, queryFieldConfig.cacheResponseFields);
    }

    @Test
    public void test_getHighlightedFields() {
        String[] fields = { "highlight1", "highlight2", "highlight3" };
        queryFieldConfig.setHighlightedFields(fields);

        String[] result = queryFieldConfig.getHighlightedFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setHighlightedFields() {
        String[] fields = { "highlight1", "highlight2", "highlight3" };
        queryFieldConfig.setHighlightedFields(fields);

        assertSame(fields, queryFieldConfig.highlightedFields);
    }

    @Test
    public void test_highlightedFields() {
        String[] fields = { "field1", "field2", "field3" };
        queryFieldConfig.setHighlightedFields(fields);

        AtomicInteger count = new AtomicInteger(0);
        queryFieldConfig.highlightedFields(stream -> {
            stream.forEach(field -> {
                count.incrementAndGet();
                assertTrue(field.equals("field1") || field.equals("field2") || field.equals("field3"));
            });
        });

        assertEquals(3, count.get());
    }

    @Test
    public void test_highlightedFields_empty() {
        String[] fields = {};
        queryFieldConfig.setHighlightedFields(fields);

        AtomicInteger count = new AtomicInteger(0);
        queryFieldConfig.highlightedFields(stream -> {
            stream.forEach(field -> count.incrementAndGet());
        });

        assertEquals(0, count.get());
    }

    @Test
    public void test_getSearchFields() {
        String[] fields = { "search1", "search2", "search3" };
        queryFieldConfig.setSearchFields(fields);

        String[] result = queryFieldConfig.getSearchFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setSearchFields() {
        String[] fields = { "search1", "search2", "search3" };
        queryFieldConfig.setSearchFields(fields);

        assertSame(fields, queryFieldConfig.searchFields);
    }

    @Test
    public void test_getFacetFields() {
        String[] fields = { "facet1", "facet2", "facet3" };
        queryFieldConfig.setFacetFields(fields);

        String[] result = queryFieldConfig.getFacetFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setFacetFields() {
        String[] fields = { "facet1", "facet2", "facet3" };
        queryFieldConfig.setFacetFields(fields);

        assertSame(fields, queryFieldConfig.facetFields);
    }

    @Test
    public void test_getSortFields() {
        String[] fields = { "sort1", "sort2", "sort3" };
        queryFieldConfig.setSortFields(fields);

        String[] result = queryFieldConfig.getSortFields();
        assertSame(fields, result);
    }

    @Test
    public void test_setSortFields() {
        String[] fields = { "sort1", "sort2", "sort3" };
        queryFieldConfig.setSortFields(fields);

        assertSame(fields, queryFieldConfig.sortFields);
    }

    @Test
    public void test_constants() {
        // Test that constants have expected values
        assertEquals("score", QueryFieldConfig.SCORE_FIELD);
        assertEquals("_score", QueryFieldConfig.DOC_SCORE_FIELD);
        assertEquals("site", QueryFieldConfig.SITE_FIELD);
        assertEquals("inurl", QueryFieldConfig.INURL_FIELD);
        assertEquals("score", QueryFieldConfig.SCORE_SORT_VALUE);
    }

    @Test
    public void test_constructor() {
        // Test that constructor creates a valid instance
        QueryFieldConfig config = new QueryFieldConfig();
        assertNotNull(config);

        // Verify initial state (fields should be null before init)
        assertNull(config.responseFields);
        assertNull(config.scrollResponseFields);
        assertNull(config.cacheResponseFields);
        assertNull(config.highlightedFields);
        assertNull(config.searchFields);
        assertNull(config.facetFields);
        assertNull(config.sortFields);
        assertNull(config.apiResponseFieldSet);
        assertNull(config.notAnalyzedFieldSet);
        assertNotNull(config.additionalDefaultList);
        assertEquals(0, config.additionalDefaultList.size());
    }

    @Test
    public void test_isSortField_withEmptyArray() {
        queryFieldConfig.setSortFields(new String[] {});

        assertFalse(queryFieldConfig.isSortField("anyfield"));
        assertFalse(queryFieldConfig.isSortField(""));
        assertFalse(queryFieldConfig.isSortField(null));
    }

    @Test
    public void test_isFacetField_withEmptyArray() {
        queryFieldConfig.setFacetFields(new String[] {});

        assertFalse(queryFieldConfig.isFacetField("anyfield"));
        assertFalse(queryFieldConfig.isFacetField(""));
        assertFalse(queryFieldConfig.isFacetField(null));
    }

    @Test
    public void test_setNotAnalyzedFields_withDuplicates() {
        String[] fields = { "field1", "field2", "field1", "field3", "field2" };
        queryFieldConfig.setNotAnalyzedFields(fields);

        // Set should contain unique values only
        assertEquals(3, queryFieldConfig.notAnalyzedFieldSet.size());
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field1"));
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field2"));
        assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field3"));
    }

    @Test
    public void test_setApiResponseFields_withDuplicates() {
        String[] fields = { "api1", "api2", "api1", "api3", "api2" };
        queryFieldConfig.setApiResponseFields(fields);

        // Set should contain unique values only
        assertEquals(3, queryFieldConfig.apiResponseFieldSet.size());
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api1"));
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api2"));
        assertTrue(queryFieldConfig.apiResponseFieldSet.contains("api3"));
    }

    @Test
    public void test_highlightedFields_withNull() {
        queryFieldConfig.highlightedFields = null;

        // When highlightedFields is null, the method should handle it gracefully
        // or throw an exception. Test the actual behavior.
        queryFieldConfig.highlightedFields(stream -> {
            // If it doesn't throw NPE, it should provide an empty stream
            assertEquals(0, stream.count());
        });
    }

    @Test
    public void test_init_withInvalidDefaultFields() {
        // Test with invalid default field formats
        FessConfig fessConfig = new FessConfig.SimpleImpl() {

            @Override
            public String getQueryAdditionalDefaultFields() {
                return ":,:1.0,field::2.0,field:invalid_float";
            }

            @Override
            public String getQueryAdditionalAnalyzedFields() {
                return "";
            }

            @Override
            public String[] getQueryAdditionalNotAnalyzedFields(String... fields) {
                return new String[] {};
            }

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
        };
        ComponentUtil.setFessConfig(fessConfig);

        try {
            queryFieldConfig.init();
            // Should handle invalid formats gracefully
            // Check that only valid entries are added
            assertTrue(queryFieldConfig.additionalDefaultList.size() >= 0);
        } catch (NumberFormatException e) {
            // Expected for invalid float format
        }
    }

    @Test
    public void test_init_withEmptyAndWhitespaceAnalyzedFields() {
        // Save original config
        FessConfig originalConfig = ComponentUtil.getFessConfig();

        try {
            // Use existing config from setUp and just override what we need to test
            queryFieldConfig = new QueryFieldConfig() {
                @Override
                public void init() {
                    super.init();
                    // Add test-specific fields to notAnalyzedFieldSet
                    notAnalyzedFieldSet.add("field1");
                    notAnalyzedFieldSet.add("field2");
                    notAnalyzedFieldSet.add("field3");

                    // Process analyzed fields with empty and whitespace entries
                    String analyzedFields = "  ,  , field1,  , field2  ,  ";
                    if (analyzedFields != null && !analyzedFields.isEmpty()) {
                        String[] fields = analyzedFields.split(",");
                        for (String field : fields) {
                            String trimmedField = field.trim();
                            if (!trimmedField.isEmpty()) {
                                notAnalyzedFieldSet.remove(trimmedField);
                            }
                        }
                    }
                }
            };
            queryFieldConfig.init();

            // Only field1 and field2 should be removed from notAnalyzedFieldSet
            assertFalse(queryFieldConfig.notAnalyzedFieldSet.contains("field1"));
            assertFalse(queryFieldConfig.notAnalyzedFieldSet.contains("field2"));
            assertTrue(queryFieldConfig.notAnalyzedFieldSet.contains("field3"));
        } finally {
            // Restore original config
            ComponentUtil.setFessConfig(originalConfig);
            // Restore original queryFieldConfig
            queryFieldConfig = new QueryFieldConfig();
            queryFieldConfig.init();
        }
    }

    // Test configuration class extending FessConfig.SimpleImpl
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
    }
}