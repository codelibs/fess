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

import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.opensearch.common.io.stream.BytesStreamOutput;
import org.opensearch.core.common.io.stream.NamedWriteableAwareStreamInput;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;

public class KnnQueryBuilderTest extends UnitFessTestCase {

    /** Only the query types Fess actually puts into the knn filter need to be resolvable. */
    private static final NamedWriteableRegistry REGISTRY = new NamedWriteableRegistry(
            List.of(new NamedWriteableRegistry.Entry(QueryBuilder.class, TermQueryBuilder.NAME, TermQueryBuilder::new)));

    @Test
    public void test_name() {
        assertEquals("knn", KnnQueryBuilder.NAME);
        assertEquals("knn", new KnnQueryBuilder("v", new float[] { 0.1f }, 1).getWriteableName());
    }

    @Test
    public void test_doXContent_minimalShape() {
        final String json =
                new KnnQueryBuilder("content_chunk_vector.vector", new float[] { 0.1f, 0.2f }, 100).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"knn\":{\"content_chunk_vector.vector\":{"), json);
        assertTrue(json.contains("\"vector\":[0.1,0.2]"), json);
        assertTrue(json.contains("\"k\":100"), json);
        assertFalse(json.contains("\"filter\""), json);
        assertFalse(json.contains("method_parameters"), json);
    }

    @Test
    public void test_doXContent_withFilterAndEfSearch() {
        final KnnQueryBuilder builder = new KnnQueryBuilder("content_chunk_vector.vector", new float[] { 0.1f }, 10);
        builder.filter(QueryBuilders.termQuery("role", "Rguest"));
        builder.efSearch(512);
        builder.boost(2.0f);
        builder.queryName("semantic");
        final String json = builder.toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"filter\":{\"term\":{\"role\":{\"value\":\"Rguest\""), json);
        assertTrue(json.contains("\"method_parameters\":{\"ef_search\":512}"), json);
        assertTrue(json.contains("\"boost\":2.0"), json);
        assertTrue(json.contains("\"_name\":\"semantic\""), json);
    }

    @Test
    public void test_constructor_rejectsNonPositiveK() {
        // k <= 0 is rejected by the OpenSearch k-NN plugin server-side; failing fast keeps the
        // error on the caller rather than surfacing as a search error
        try {
            new KnnQueryBuilder("v", new float[] { 0.1f }, 0);
            fail("k=0 must be rejected");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("k must be positive"), e.getMessage());
        }
        try {
            new KnnQueryBuilder("v", new float[] { 0.1f }, -1);
            fail("a negative k must be rejected");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("k must be positive"), e.getMessage());
        }
    }

    @Test
    public void test_constructor_rejectsNulls() {
        try {
            new KnnQueryBuilder(null, new float[] { 0.1f }, 1);
            fail("a null field name must be rejected");
        } catch (final NullPointerException e) {
            assertEquals("fieldName", e.getMessage());
        }
        try {
            new KnnQueryBuilder("v", null, 1);
            fail("a null vector must be rejected");
        } catch (final NullPointerException e) {
            assertEquals("vector", e.getMessage());
        }
    }

    @Test
    public void test_constructor_copiesTheVector() {
        final float[] vector = { 0.1f, 0.2f };
        final KnnQueryBuilder builder = new KnnQueryBuilder("v", vector, 1);
        vector[0] = 9.9f;
        assertTrue(builder.toString().replaceAll("\\s", "").contains("\"vector\":[0.1,0.2]"), builder.toString());
    }

    @Test
    public void test_streamRoundTrip_withoutFilter() throws Exception {
        final KnnQueryBuilder original = new KnnQueryBuilder("content_chunk_vector.vector", new float[] { 0.1f, -0.5f }, 7);
        final KnnQueryBuilder copy = roundTrip(original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.toString(), copy.toString());
    }

    @Test
    public void test_streamRoundTrip_withFilterAndEfSearch() throws Exception {
        final KnnQueryBuilder original = new KnnQueryBuilder("content_chunk_vector.vector", new float[] { 0.1f }, 7);
        original.filter(QueryBuilders.termQuery("role", "Rguest"));
        original.efSearch(256);
        original.boost(3.0f);
        original.queryName("named");
        final KnnQueryBuilder copy = roundTrip(original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.toString(), copy.toString());
    }

    @Test
    public void test_equalsAndHashCode_coverEveryField() {
        final KnnQueryBuilder base = new KnnQueryBuilder("v", new float[] { 0.1f }, 5);
        assertEquals(base, new KnnQueryBuilder("v", new float[] { 0.1f }, 5));
        assertEquals(base.hashCode(), new KnnQueryBuilder("v", new float[] { 0.1f }, 5).hashCode());
        assertFalse(base.equals(new KnnQueryBuilder("other", new float[] { 0.1f }, 5)));
        assertFalse(base.equals(new KnnQueryBuilder("v", new float[] { 0.2f }, 5)));
        assertFalse(base.equals(new KnnQueryBuilder("v", new float[] { 0.1f }, 6)));
        assertFalse(base.equals(new KnnQueryBuilder("v", new float[] { 0.1f }, 5).efSearch(128)));
        assertFalse(base.equals(new KnnQueryBuilder("v", new float[] { 0.1f }, 5).filter(QueryBuilders.termQuery("role", "Rguest"))));
        // a different filter must not compare equal: the filter is part of the query identity
        final KnnQueryBuilder filtered = new KnnQueryBuilder("v", new float[] { 0.1f }, 5).filter(QueryBuilders.termQuery("role", "a"));
        assertFalse(filtered.equals(new KnnQueryBuilder("v", new float[] { 0.1f }, 5).filter(QueryBuilders.termQuery("role", "b"))));
        assertEquals(filtered, new KnnQueryBuilder("v", new float[] { 0.1f }, 5).filter(QueryBuilders.termQuery("role", "a")));
    }

    @Test
    public void test_doToQuery_isUnsupported() throws Exception {
        // the knn query is a plugin query evaluated server-side; this builder only serializes it
        try {
            new KnnQueryBuilder("v", new float[] { 0.1f }, 1).doToQuery(null);
            fail("doToQuery must not be supported");
        } catch (final UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("doToQuery"), e.getMessage());
        }
    }

    private KnnQueryBuilder roundTrip(final KnnQueryBuilder original) throws Exception {
        try (BytesStreamOutput out = new BytesStreamOutput()) {
            original.writeTo(out);
            try (StreamInput in = new NamedWriteableAwareStreamInput(out.bytes().streamInput(), REGISTRY)) {
                return new KnnQueryBuilder(in);
            }
        }
    }
}
