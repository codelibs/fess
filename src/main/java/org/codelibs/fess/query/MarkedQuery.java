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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryVisitor;

/**
 * A leaf of a parsed query that a {@link QueryMarker} took. It keeps the original leaf and how
 * it was combined into its parent, and prints as the original leaf.
 */
public final class MarkedQuery extends Query {

    private final QueryMarker marker;

    private final Occur occur;

    private final Query query;

    /**
     * Creates a marked leaf.
     *
     * @param marker the marker that took the leaf
     * @param occur how the leaf is combined into its parent
     * @param query the original leaf
     */
    public MarkedQuery(final QueryMarker marker, final Occur occur, final Query query) {
        this.marker = Objects.requireNonNull(marker, "marker");
        this.occur = Objects.requireNonNull(occur, "occur");
        this.query = Objects.requireNonNull(query, "query");
    }

    /**
     * Gets the marker that took the leaf.
     *
     * @return the marker
     */
    public QueryMarker getMarker() {
        return marker;
    }

    /**
     * Gets how the leaf is combined into its parent.
     *
     * @return the occur
     */
    public Occur getOccur() {
        return occur;
    }

    /**
     * Gets the original leaf.
     *
     * @return the original query
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Collects the marked leaves of a parsed query in query order.
     *
     * @param query the parsed query
     * @return the marked leaves
     */
    public static List<MarkedQuery> collect(final Query query) {
        final List<MarkedQuery> list = new ArrayList<>();
        collect(query, list);
        return list;
    }

    private static void collect(final Query query, final List<MarkedQuery> list) {
        if (query instanceof final MarkedQuery markedQuery) {
            list.add(markedQuery);
        } else if (query instanceof final BooleanQuery booleanQuery) {
            for (final BooleanClause clause : booleanQuery.clauses()) {
                collect(clause.query(), list);
            }
        } else if (query instanceof final BoostQuery boostQuery) {
            collect(boostQuery.getQuery(), list);
        }
    }

    @Override
    public String toString(final String field) {
        return query.toString(field);
    }

    @Override
    public void visit(final QueryVisitor visitor) {
        query.visit(visitor);
    }

    @Override
    public boolean equals(final Object other) {
        return sameClassAs(other) && marker == ((MarkedQuery) other).marker && occur == ((MarkedQuery) other).occur
                && query.equals(((MarkedQuery) other).query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classHash(), System.identityHashCode(marker), occur, query);
    }
}
