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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.search.Query;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.index.query.AbstractQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryShardContext;

/**
 * Client-side {@link QueryBuilder} for the OpenSearch Neural Search plugin's {@code hybrid}
 * query, which runs several subqueries and hands their per-shard results to a search pipeline
 * that normalizes and combines them. The transport client has no builder for plugin-provided
 * queries, so this class only serializes the query body - following the same
 * serialization-only pattern as {@link KnnQueryBuilder} and {@link StoredLtrQueryBuilder};
 * {@link #doToQuery(QueryShardContext)} is unsupported because the query is evaluated
 * server-side.
 *
 * <p>Two things about this query are easy to get wrong and fail silently:</p>
 *
 * <ul>
 * <li>It has to be the top-level query. Wrapping it in another compound query does not
 * necessarily raise an error; the subqueries stop being collected separately, the pipeline never
 * runs, and the response comes back looking plausible with only the first subquery's hits and
 * raw scores.</li>
 * <li>It only means anything with a normalization or score-ranker processor attached. Sent
 * without one it returns duplicated hits and large negative sentinel scores - again with no
 * error - so whatever builds this query has to attach the pipeline in the same breath.</li>
 * </ul>
 *
 * <p>This builder deliberately does not support the query's {@code filter} parameter; see
 * {@link #filter(QueryBuilder)}.</p>
 */
public class HybridQueryBuilder extends AbstractQueryBuilder<HybridQueryBuilder> {

    /** The query name registered by the OpenSearch Neural Search plugin. */
    public static final String NAME = "hybrid";

    /** The maximum number of subqueries the plugin accepts. */
    public static final int MAX_SUB_QUERIES = 5;

    /** The subqueries whose results are fused. */
    protected final List<QueryBuilder> queries = new ArrayList<>(MAX_SUB_QUERIES);

    /** Maximum number of results each subquery returns per shard; required when from is above zero. */
    protected Integer paginationDepth;

    /**
     * Creates an empty hybrid query.
     */
    public HybridQueryBuilder() {
    }

    /**
     * Deserializes a hybrid query from a stream.
     *
     * @param in the stream input
     * @throws IOException on read failure
     */
    public HybridQueryBuilder(final StreamInput in) throws IOException {
        super(in);
        queries.addAll(in.readNamedWriteableList(QueryBuilder.class));
        paginationDepth = in.readOptionalVInt();
    }

    /**
     * Adds a subquery.
     *
     * @param query the subquery
     * @return this builder
     */
    public HybridQueryBuilder add(final QueryBuilder query) {
        Objects.requireNonNull(query, "query");
        if (queries.size() >= MAX_SUB_QUERIES) {
            throw new IllegalArgumentException("hybrid accepts at most " + MAX_SUB_QUERIES + " subqueries.");
        }
        queries.add(query);
        return this;
    }

    /**
     * Sets the pagination depth, which bounds how many results each subquery returns per shard.
     * OpenSearch rejects a hybrid query that pages beyond the first page without it.
     *
     * @param paginationDepth the maximum number of results per shard per subquery
     * @return this builder
     */
    public HybridQueryBuilder paginationDepth(final Integer paginationDepth) {
        this.paginationDepth = paginationDepth;
        return this;
    }

    /**
     * Returns the number of subqueries added so far.
     *
     * @return the subquery count
     */
    public int size() {
        return queries.size();
    }

    /**
     * Always throws: a hybrid query built by Fess must not carry a top-level filter.
     *
     * <p>OpenSearch documents this parameter as applying to every subquery, and for a bare
     * {@code knn} subquery it does - it becomes that query's own filter, evaluated during the
     * approximate search. Fess wraps its {@code knn} in a {@code nested} query, and against a
     * nested subquery the filter lands outside the nested clause instead, where it can only
     * discard documents the approximate search has already chosen. A user whose roles cover a
     * small slice of the corpus then gets nothing from the vector branch, with no error and a
     * response that looks fine.</p>
     *
     * <p>So the constraint belongs inside each subquery, where the caller can put it in the place
     * that actually restricts retrieval. This method refuses rather than silently accepting a
     * filter it would have to drop.</p>
     *
     * @param filter ignored
     * @return never returns
     */
    @Override
    public QueryBuilder filter(final QueryBuilder filter) {
        throw new UnsupportedOperationException("A hybrid query must not carry a top-level filter: against a nested subquery "
                + "it degrades into a filter over results the approximate search has already chosen. "
                + "Apply the constraint inside each subquery instead.");
    }

    @Override
    protected void doWriteTo(final StreamOutput out) throws IOException {
        out.writeNamedWriteableList(queries);
        out.writeOptionalVInt(paginationDepth);
    }

    @Override
    protected void doXContent(final XContentBuilder builder, final Params params) throws IOException {
        if (queries.isEmpty()) {
            throw new IllegalStateException("hybrid requires at least one subquery.");
        }
        builder.startObject(NAME);
        if (paginationDepth != null) {
            builder.field("pagination_depth", paginationDepth.intValue());
        }
        builder.startArray("queries");
        for (final QueryBuilder query : queries) {
            query.toXContent(builder, params);
        }
        builder.endArray();
        printBoostAndQueryName(builder);
        builder.endObject();
    }

    @Override
    protected Query doToQuery(final QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("doToQuery is not supported.");
    }

    @Override
    protected boolean doEquals(final HybridQueryBuilder other) {
        return Objects.equals(queries, other.queries) && Objects.equals(paginationDepth, other.paginationDepth);
    }

    @Override
    protected int doHashCode() {
        return Objects.hash(queries, paginationDepth);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }
}
