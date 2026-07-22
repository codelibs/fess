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

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.apache.lucene.search.Query;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.index.query.AbstractQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryShardContext;

/**
 * Client-side {@link QueryBuilder} for the OpenSearch k-NN plugin's {@code knn}
 * query (approximate nearest-neighbor search over a {@code knn_vector} field
 * indexed with an ANN method such as HNSW). The transport client has no builder
 * for plugin-provided queries, so this class only serializes the query body —
 * mirroring the {@code NeuralQueryBuilder} pattern in fess-webapp-semantic-search;
 * {@link #doToQuery(QueryShardContext)} is unsupported because the query is
 * evaluated server-side.
 */
public class KnnQueryBuilder extends AbstractQueryBuilder<KnnQueryBuilder> {

    private static final String NAME = "knn";

    /** The target knn_vector field name. */
    protected final String fieldName;

    /** The query vector. */
    protected final float[] vector;

    /** The number of nearest neighbors to return per shard. */
    protected final int k;

    /** Optional filter applied during the ANN search (efficient filtering). */
    protected QueryBuilder filter;

    /** Optional HNSW ef_search override, emitted as method_parameters.ef_search. */
    protected Integer efSearch;

    /**
     * Creates a knn query.
     *
     * @param fieldName the knn_vector field name
     * @param vector the query vector
     * @param k the number of nearest neighbors to return per shard
     */
    public KnnQueryBuilder(final String fieldName, final float[] vector, final int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be positive: " + k);
        }
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
        this.vector = Objects.requireNonNull(vector, "vector").clone();
        this.k = k;
    }

    /**
     * Deserializes a knn query from a stream.
     *
     * @param in the stream input
     * @throws IOException on read failure
     */
    public KnnQueryBuilder(final StreamInput in) throws IOException {
        super(in);
        fieldName = in.readString();
        vector = in.readFloatArray();
        k = in.readVInt();
        filter = in.readOptionalNamedWriteable(QueryBuilder.class);
        efSearch = in.readOptionalVInt();
    }

    /**
     * Sets the filter applied during the ANN search.
     *
     * @param filter the filter query
     * @return this builder
     */
    public KnnQueryBuilder filter(final QueryBuilder filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Sets the HNSW ef_search parameter for this query.
     *
     * @param efSearch the ef_search value
     * @return this builder
     */
    public KnnQueryBuilder efSearch(final Integer efSearch) {
        this.efSearch = efSearch;
        return this;
    }

    @Override
    protected void doWriteTo(final StreamOutput out) throws IOException {
        out.writeString(fieldName);
        out.writeFloatArray(vector);
        out.writeVInt(k);
        out.writeOptionalNamedWriteable(filter);
        out.writeOptionalVInt(efSearch);
    }

    @Override
    protected void doXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject(NAME);
        builder.startObject(fieldName);
        builder.startArray("vector");
        for (final float v : vector) {
            builder.value(v);
        }
        builder.endArray();
        builder.field("k", k);
        if (filter != null) {
            builder.field("filter", filter);
        }
        if (efSearch != null) {
            builder.startObject("method_parameters");
            builder.field("ef_search", efSearch.intValue());
            builder.endObject();
        }
        printBoostAndQueryName(builder);
        builder.endObject();
        builder.endObject();
    }

    @Override
    protected Query doToQuery(final QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("doToQuery is not supported.");
    }

    @Override
    protected boolean doEquals(final KnnQueryBuilder other) {
        return Objects.equals(fieldName, other.fieldName) && Arrays.equals(vector, other.vector) && k == other.k
                && Objects.equals(filter, other.filter) && Objects.equals(efSearch, other.efSearch);
    }

    @Override
    protected int doHashCode() {
        return Objects.hash(fieldName, Arrays.hashCode(vector), k, filter, efSearch);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }
}
