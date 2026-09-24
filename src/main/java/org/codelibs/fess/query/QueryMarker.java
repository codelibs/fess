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

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

/**
 * Marks nodes of a parsed query so that they are handled by this marker instead of the
 * {@link QueryCommand} for their query class.
 *
 * <p>After {@link org.codelibs.fess.query.parser.QueryParser} parses a query string, every leaf
 * of the tree is offered to the registered markers in priority order, together with how it is
 * combined into its parent ({@link Occur}). The first marker whose {@link #matches} returns true
 * wraps the leaf in a {@link MarkedQuery}; the leaf is not offered to the other markers. A marked
 * leaf is then converted by {@link #execute} each time the query is built. To act on a marked
 * query outside the query itself (for example to redirect the search), register a
 * {@link org.codelibs.fess.helper.SearchHelper.SearchRequestParamsRewriter} that parses the query
 * with the query parser and finds the marker's leaves with {@link MarkedQuery#collect}.</p>
 *
 * <p>A query is parsed and built several times per request (facets, each rank fusion searcher,
 * the semantic query splitter), so {@link #matches} and {@link #execute} must be free of side
 * effects.</p>
 *
 * <p>The classic query parser folds {@code !x}, {@code -x} and {@code NOT x} into the same
 * {@link Occur#MUST_NOT} clause, so a marker cannot tell them apart.</p>
 */
public abstract class QueryMarker {

    /** Priority of this marker (lower numbers = higher priority). */
    protected int priority = 99;

    /**
     * Default constructor.
     */
    public QueryMarker() {
        // Default constructor
    }

    /**
     * Gets the priority of this marker. Lower numbers indicate higher priority.
     *
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this marker. Lower numbers indicate higher priority.
     *
     * @param priority the priority value
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Registers this marker with the query parser.
     */
    public void register() {
        ComponentUtil.getQueryParser().addMarker(this);
    }

    /**
     * Returns whether this marker takes the given leaf of a parsed query.
     *
     * @param occur how the leaf is combined into its parent ({@link Occur#MUST} at the top level)
     * @param query the leaf query
     * @return true to mark the leaf
     */
    public abstract boolean matches(Occur occur, Query query);

    /**
     * Converts a leaf this marker marked. The default drops the clause.
     *
     * @param context the query context
     * @param query the marked leaf
     * @param boost the boost value
     * @return the converted query builder, or null to drop the clause
     */
    public QueryBuilder execute(final QueryContext context, final MarkedQuery query, final float boost) {
        return null;
    }
}
