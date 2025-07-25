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

import java.io.IOException;
import java.util.Objects;

import org.apache.lucene.search.Query;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.DisMaxQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilderVisitor;
import org.opensearch.index.query.QueryRewriteContext;
import org.opensearch.index.query.QueryShardContext;

/**
 * Default implementation of QueryBuilder that wraps other QueryBuilder instances
 * and provides additional functionality for adding inner queries dynamically.
 * Supports both BoolQueryBuilder and DisMaxQueryBuilder as underlying implementations.
 */
public class DefaultQueryBuilder implements QueryBuilder {

    /** The underlying query builder being wrapped. */
    private final QueryBuilder queryBuilder;

    /** The type of the underlying query builder. */
    private final QueryType queryType;

    /**
     * Creates a new DefaultQueryBuilder wrapping the specified QueryBuilder.
     *
     * @param queryBuilder the query builder to wrap (must be BoolQueryBuilder or DisMaxQueryBuilder)
     * @throws IllegalArgumentException if the query builder type is not supported
     */
    public DefaultQueryBuilder(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        if (queryBuilder instanceof BoolQueryBuilder) {
            queryType = QueryType.BOOL;
        } else if (queryBuilder instanceof DisMaxQueryBuilder) {
            queryType = QueryType.DISMAX;
        } else {
            throw new IllegalArgumentException("Unknown query builder: " + queryBuilder);
        }
    }

    /**
     * Adds an inner query builder to the wrapped query.
     * For BoolQueryBuilder, adds as a should clause.
     * For DisMaxQueryBuilder, adds as a query.
     *
     * @param innerQueryBuilder the query builder to add
     * @return this instance for method chaining
     */
    public DefaultQueryBuilder add(final QueryBuilder innerQueryBuilder) {
        switch (queryType) {
        case BOOL:
            ((BoolQueryBuilder) queryBuilder).should(innerQueryBuilder);
            break;
        case DISMAX:
            ((DisMaxQueryBuilder) queryBuilder).add(innerQueryBuilder);
            break;
        default:
            break;
        }
        return this;
    }

    /**
     * Enumeration of supported query types.
     */
    enum QueryType {
        /** Boolean query type. */
        BOOL,
        /** DisMax query type. */
        DISMAX;
    }

    /**
     * Returns the writeable name of the wrapped query builder.
     *
     * @return the writeable name
     */
    @Override
    public String getWriteableName() {
        return queryBuilder.getWriteableName();
    }

    /**
     * Creates a Lucene Query from this query builder.
     *
     * @param context the query shard context
     * @return the Lucene Query
     * @throws IOException if an I/O error occurs
     */
    @Override
    public Query toQuery(final QueryShardContext context) throws IOException {
        return queryBuilder.toQuery(context);
    }

    /**
     * Returns whether this query builder is a fragment.
     *
     * @return true if this is a fragment, false otherwise
     */
    @Override
    public boolean isFragment() {
        return queryBuilder.isFragment();
    }

    /**
     * Sets the query name.
     *
     * @param queryName the query name to set
     * @return the query builder
     */
    @Override
    public QueryBuilder queryName(final String queryName) {
        return queryBuilder.queryName(queryName);
    }

    /**
     * Returns the query name.
     *
     * @return the query name
     */
    @Override
    public String queryName() {
        return queryBuilder.queryName();
    }

    /**
     * Returns the boost value.
     *
     * @return the boost value
     */
    @Override
    public float boost() {
        return queryBuilder.boost();
    }

    /**
     * Sets the boost value.
     *
     * @param boost the boost value to set
     * @return the query builder
     */
    @Override
    public QueryBuilder boost(final float boost) {
        return queryBuilder.boost(boost);
    }

    /**
     * Applies a filter to the query.
     *
     * @param filter the filter query builder
     * @return the query builder
     */
    @Override
    public QueryBuilder filter(QueryBuilder filter) {
        return queryBuilder.filter(filter);
    }

    /**
     * Returns the name of the query.
     *
     * @return the query name
     */
    @Override
    public String getName() {
        return queryBuilder.getName();
    }

    /**
     * Rewrites the query using the provided rewrite context.
     *
     * @param queryShardContext the query rewrite context
     * @return the rewritten query builder
     * @throws IOException if an I/O error occurs during rewriting
     */
    @Override
    public QueryBuilder rewrite(final QueryRewriteContext queryShardContext) throws IOException {
        return queryBuilder.rewrite(queryShardContext);
    }

    @Override
    public void visit(final QueryBuilderVisitor visitor) {
        queryBuilder.visit(visitor);
    }

    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        return queryBuilder.toXContent(builder, params);
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        queryBuilder.writeTo(out);
    }

    @Override
    public int hashCode() {
        return queryBuilder.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DefaultQueryBuilder other = (DefaultQueryBuilder) obj;
        return Objects.equals(queryBuilder, other.queryBuilder);
    }

    @Override
    public String toString() {
        return queryBuilder.toString();
    }

}
