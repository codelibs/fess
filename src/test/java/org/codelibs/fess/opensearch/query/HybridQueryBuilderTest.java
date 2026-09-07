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
package org.codelibs.fess.opensearch.query;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.opensearch.index.query.QueryBuilders;

public class HybridQueryBuilderTest extends UnitFessTestCase {

    @Test
    public void test_toXContent_shape() {
        final String json = new HybridQueryBuilder()//
                .add(QueryBuilders.matchQuery("content", "opensearch").queryName("default"))//
                .add(QueryBuilders.termQuery("label", "news").queryName("semantic_chunk"))//
                .paginationDepth(Integer.valueOf(200))//
                .toString()
                .replaceAll("\\s", "");
        assertTrue(json.startsWith("{\"hybrid\":{"), json);
        assertTrue(json.contains("\"pagination_depth\":200"), json);
        assertTrue(json.contains("\"queries\":["), json);
        assertTrue(json.contains("\"_name\":\"default\""), json);
        assertTrue(json.contains("\"_name\":\"semantic_chunk\""), json);
    }

    @Test
    public void test_toXContent_omitsPaginationDepthWhenUnset() {
        // the engine only requires it past the first page, and sending it always would bound
        // the ranked set for every search
        final String json = new HybridQueryBuilder().add(QueryBuilders.matchAllQuery()).toString().replaceAll("\\s", "");
        assertFalse(json.contains("pagination_depth"), json);
    }

    @Test
    public void test_toXContent_neverEmitsFilter() {
        // A top-level filter reads as "applies to every branch", but against a nested subquery
        // the engine applies it outside the nested clause, where it can only discard documents
        // the approximate search already chose. This assertion is the regression guard.
        final String json = new HybridQueryBuilder().add(QueryBuilders.matchAllQuery()).toString();
        assertFalse(json.contains("filter"), json);
    }

    @Test
    public void test_filter_isRefusedRatherThanDropped() {
        final HybridQueryBuilder builder = new HybridQueryBuilder().add(QueryBuilders.matchAllQuery());
        try {
            builder.filter(QueryBuilders.termQuery("role", "guest"));
            fail("a filter must be refused, not silently dropped");
        } catch (final UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("subquery"), e.getMessage());
        }
    }

    @Test
    public void test_add_rejectsMoreThanTheEngineAccepts() {
        final HybridQueryBuilder builder = new HybridQueryBuilder();
        for (int i = 0; i < HybridQueryBuilder.MAX_SUB_QUERIES; i++) {
            builder.add(QueryBuilders.matchAllQuery());
        }
        assertEquals(HybridQueryBuilder.MAX_SUB_QUERIES, builder.size());
        try {
            builder.add(QueryBuilders.matchAllQuery());
            fail("the engine accepts at most " + HybridQueryBuilder.MAX_SUB_QUERIES + " subqueries");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(String.valueOf(HybridQueryBuilder.MAX_SUB_QUERIES)), e.getMessage());
        }
    }

    @Test
    public void test_toXContent_rejectsAnEmptyQuery() {
        try {
            new HybridQueryBuilder().toString();
            fail("a hybrid query with no branch is not a query");
        } catch (final IllegalStateException e) {
            assertTrue(e.getMessage().contains("subquery"), e.getMessage());
        }
    }

    @Test
    public void test_getWriteableName() {
        assertEquals("hybrid", new HybridQueryBuilder().getWriteableName());
    }
}
