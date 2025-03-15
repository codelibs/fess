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

public class DefaultQueryBuilder implements QueryBuilder {

    private final QueryBuilder queryBuilder;

    private final QueryType queryType;

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

    enum QueryType {
        BOOL, DISMAX;
    }

    @Override
    public String getWriteableName() {
        return queryBuilder.getWriteableName();
    }

    @Override
    public Query toQuery(final QueryShardContext context) throws IOException {
        return queryBuilder.toQuery(context);
    }

    @Override
    public boolean isFragment() {
        return queryBuilder.isFragment();
    }

    @Override
    public QueryBuilder queryName(final String queryName) {
        return queryBuilder.queryName(queryName);
    }

    @Override
    public String queryName() {
        return queryBuilder.queryName();
    }

    @Override
    public float boost() {
        return queryBuilder.boost();
    }

    @Override
    public QueryBuilder boost(final float boost) {
        return queryBuilder.boost(boost);
    }

    @Override
    public String getName() {
        return queryBuilder.getName();
    }

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
