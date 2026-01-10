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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.search.aggregations.BucketOrder;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FacetInfoTest extends UnitFessTestCase {

    private FacetInfo facetInfo;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        facetInfo = new FacetInfo();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Test default constructor
    @Test
    public void test_constructor() {
        FacetInfo info = new FacetInfo();
        assertNull(info.field);
        assertNull(info.query);
        assertNull(info.size);
        assertNull(info.minDocCount);
        assertNull(info.sort);
        assertNull(info.missing);
    }

    // Test init method with various configurations
    @Test
    public void test_init_withAllFields() {
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryFacetFields() {
                return "field1, field2, field3";
            }

            @Override
            public String getQueryFacetFieldsSize() {
                return "10";
            }

            @Override
            public Integer getQueryFacetFieldsSizeAsInteger() {
                return 10;
            }

            @Override
            public String getQueryFacetFieldsMinDocCount() {
                return "5";
            }

            @Override
            public String getQueryFacetFieldsSort() {
                return "count.desc";
            }

            @Override
            public String getQueryFacetFieldsMissing() {
                return "unknown";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        facetInfo.init();

        assertNotNull(facetInfo.field);
        assertEquals(3, facetInfo.field.length);
        assertEquals("field1", facetInfo.field[0]);
        assertEquals("field2", facetInfo.field[1]);
        assertEquals("field3", facetInfo.field[2]);
        assertEquals(Integer.valueOf(10), facetInfo.size);
        assertEquals(Long.valueOf(5), facetInfo.minDocCount);
        assertEquals("count.desc", facetInfo.sort);
        assertEquals("unknown", facetInfo.missing);
    }

    // Test init with empty fields
    @Test
    public void test_init_withEmptyFields() {
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryFacetFields() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsSize() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsMinDocCount() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsSort() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsMissing() {
                return "";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        facetInfo.init();

        assertNull(facetInfo.field);
        assertNull(facetInfo.size);
        assertNull(facetInfo.minDocCount);
        assertNull(facetInfo.sort);
        assertNull(facetInfo.missing);
    }

    // Test init with duplicate and empty field names
    @Test
    public void test_init_withDuplicateAndEmptyFieldNames() {
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryFacetFields() {
                return "field1, field2, field1, , field3, field2";
            }

            @Override
            public String getQueryFacetFieldsSize() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsMinDocCount() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsSort() {
                return "";
            }

            @Override
            public String getQueryFacetFieldsMissing() {
                return "";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        facetInfo.init();

        assertNotNull(facetInfo.field);
        assertEquals(3, facetInfo.field.length);
        assertEquals("field1", facetInfo.field[0]);
        assertEquals("field2", facetInfo.field[1]);
        assertEquals("field3", facetInfo.field[2]);
    }

    // Test getBucketOrder with count.desc
    @Test
    public void test_getBucketOrder_countDesc() {
        facetInfo.sort = "count.desc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(false), order);
    }

    // Test getBucketOrder with count.asc
    @Test
    public void test_getBucketOrder_countAsc() {
        facetInfo.sort = "count.asc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(true), order);
    }

    // Test getBucketOrder with term.desc
    @Test
    public void test_getBucketOrder_termDesc() {
        facetInfo.sort = "term.desc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(false), order);
    }

    // Test getBucketOrder with term.asc
    @Test
    public void test_getBucketOrder_termAsc() {
        facetInfo.sort = "term.asc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(true), order);
    }

    // Test getBucketOrder with key.desc
    @Test
    public void test_getBucketOrder_keyDesc() {
        facetInfo.sort = "key.desc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(false), order);
    }

    // Test getBucketOrder with key.asc
    @Test
    public void test_getBucketOrder_keyAsc() {
        facetInfo.sort = "key.asc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(true), order);
    }

    // Test getBucketOrder with only sort type (no order)
    @Test
    public void test_getBucketOrder_onlySortType() {
        facetInfo.sort = "count";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(true), order);
    }

    // Test getBucketOrder with only term type (no order)
    @Test
    public void test_getBucketOrder_onlyTermType() {
        facetInfo.sort = "term";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(true), order);
    }

    // Test getBucketOrder with only key type (no order)
    @Test
    public void test_getBucketOrder_onlyKeyType() {
        facetInfo.sort = "key";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(true), order);
    }

    // Test getBucketOrder with invalid sort value
    @Test
    public void test_getBucketOrder_invalidSort() {
        facetInfo.sort = "invalid.desc";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(false), order); // default
    }

    // Test getBucketOrder with null sort
    @Test
    public void test_getBucketOrder_nullSort() {
        facetInfo.sort = null;
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(false), order); // default
    }

    // Test getBucketOrder with empty sort
    @Test
    public void test_getBucketOrder_emptySort() {
        facetInfo.sort = "";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(false), order); // default
    }

    // Test getBucketOrder with case variations
    @Test
    public void test_getBucketOrder_caseVariations() {
        facetInfo.sort = "COUNT.DESC";
        BucketOrder order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.count(false), order);

        facetInfo.sort = "term.DESC";
        order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(false), order);

        facetInfo.sort = "key.ASC";
        order = facetInfo.getBucketOrder();
        assertNotNull(order);
        assertEquals(BucketOrder.key(true), order);
    }

    // Test addQuery with null query array
    @Test
    public void test_addQuery_nullArray() {
        assertNull(facetInfo.query);
        facetInfo.addQuery("query1");
        assertNotNull(facetInfo.query);
        assertEquals(1, facetInfo.query.length);
        assertEquals("query1", facetInfo.query[0]);
    }

    // Test addQuery with existing queries
    @Test
    public void test_addQuery_existingQueries() {
        facetInfo.query = new String[] { "query1", "query2" };
        facetInfo.addQuery("query3");
        assertNotNull(facetInfo.query);
        assertEquals(3, facetInfo.query.length);
        assertEquals("query1", facetInfo.query[0]);
        assertEquals("query2", facetInfo.query[1]);
        assertEquals("query3", facetInfo.query[2]);
    }

    // Test addQuery multiple times
    @Test
    public void test_addQuery_multiple() {
        facetInfo.addQuery("query1");
        facetInfo.addQuery("query2");
        facetInfo.addQuery("query3");
        assertNotNull(facetInfo.query);
        assertEquals(3, facetInfo.query.length);
        assertEquals("query1", facetInfo.query[0]);
        assertEquals("query2", facetInfo.query[1]);
        assertEquals("query3", facetInfo.query[2]);
    }

    // Test addQuery with empty string
    @Test
    public void test_addQuery_emptyString() {
        facetInfo.addQuery("");
        assertNotNull(facetInfo.query);
        assertEquals(1, facetInfo.query.length);
        assertEquals("", facetInfo.query[0]);
    }

    // Test addQuery with null string
    @Test
    public void test_addQuery_nullString() {
        facetInfo.addQuery(null);
        assertNotNull(facetInfo.query);
        assertEquals(1, facetInfo.query.length);
        assertNull(facetInfo.query[0]);
    }

    // Test toString method with all fields populated
    @Test
    public void test_toString_allFieldsPopulated() {
        facetInfo.field = new String[] { "field1", "field2" };
        facetInfo.query = new String[] { "query1", "query2" };
        facetInfo.size = 10;
        facetInfo.minDocCount = 5L;
        facetInfo.sort = "count.desc";
        facetInfo.missing = "unknown";

        String result = facetInfo.toString();
        assertEquals("FacetInfo [field=[field1, field2], query=[query1, query2], size=10, minDocCount=5, sort=count.desc, missing=unknown]",
                result);
    }

    // Test toString method with null fields
    @Test
    public void test_toString_nullFields() {
        String result = facetInfo.toString();
        assertEquals("FacetInfo [field=null, query=null, size=null, minDocCount=null, sort=null, missing=null]", result);
    }

    // Test toString method with empty arrays
    @Test
    public void test_toString_emptyArrays() {
        facetInfo.field = new String[0];
        facetInfo.query = new String[0];

        String result = facetInfo.toString();
        assertEquals("FacetInfo [field=[], query=[], size=null, minDocCount=null, sort=null, missing=null]", result);
    }

    // Test toString method with partially populated fields
    @Test
    public void test_toString_partiallyPopulated() {
        facetInfo.field = new String[] { "field1" };
        facetInfo.size = 20;
        facetInfo.sort = "term.asc";

        String result = facetInfo.toString();
        assertEquals("FacetInfo [field=[field1], query=null, size=20, minDocCount=null, sort=term.asc, missing=null]", result);
    }

    // Test field assignment
    @Test
    public void test_fieldAssignment() {
        String[] fields = new String[] { "field1", "field2", "field3" };
        facetInfo.field = fields;
        assertNotNull(facetInfo.field);
        assertEquals(3, facetInfo.field.length);
        assertSame(fields, facetInfo.field);
    }

    // Test size assignment
    @Test
    public void test_sizeAssignment() {
        facetInfo.size = 100;
        assertEquals(Integer.valueOf(100), facetInfo.size);
    }

    // Test minDocCount assignment
    @Test
    public void test_minDocCountAssignment() {
        facetInfo.minDocCount = 10L;
        assertEquals(Long.valueOf(10), facetInfo.minDocCount);
    }

    // Test missing assignment
    @Test
    public void test_missingAssignment() {
        facetInfo.missing = "N/A";
        assertEquals("N/A", facetInfo.missing);
    }
}