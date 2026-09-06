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
package org.codelibs.fess.rank.fusion;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.dbflute.optional.OptionalThing;
import org.opensearch.index.query.QueryBuilder;

/**
 * Abstract base class for rank fusion searchers in the Fess search system.
 * Rank fusion searchers are responsible for executing search queries and
 * can be combined to implement advanced ranking strategies.
 */
public abstract class RankFusionSearcher {
    /** The name of this searcher, lazily initialized. */
    protected String name;

    /**
     * Default constructor for creating a new rank fusion searcher instance.
     * This constructor initializes the searcher with default values.
     * The searcher name will be lazily initialized when first accessed.
     */
    public RankFusionSearcher() {
        // Default constructor - name will be initialized lazily
    }

    /**
     * Returns the name of this searcher.
     * The name is derived from the class name by converting it to lowercase
     * and removing the "Searcher" suffix.
     *
     * @return the searcher name
     */
    public String getName() {
        if (name == null) {
            name = StringUtil.decamelize(this.getClass().getSimpleName().replace("Searcher", StringUtil.EMPTY)).toLowerCase(Locale.ENGLISH);
        }
        return name;
    }

    /**
     * Executes a search operation with the specified parameters.
     * This method must be implemented by concrete searcher classes.
     *
     * @param query the search query string
     * @param params the search request parameters including pagination, filters, etc.
     * @param userBean the optional user bean for access control and personalization
     * @return the search result containing matched documents and metadata
     */
    protected abstract SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean);

    /**
     * Returns this searcher's contribution to a request that fuses several searchers in the search
     * engine, or empty when it cannot contribute to one.
     *
     * <p>Fusing in the engine means one request whose top-level query carries every searcher's
     * query as a subquery, so that facets, total hits and highlighting describe the fused result
     * set instead of one branch of it. A searcher that can be a branch of such a request returns
     * its query here; the default is empty, so a searcher that only knows how to run its own
     * request keeps working unchanged and the caller falls back to fusing in Fess.</p>
     *
     * <p>The query returned has to carry its own constraints - permissions above all. Nothing
     * outside it will add them: the top-level query is not a place where a filter can be applied
     * to every branch (see {@code HybridQueryBuilder#filter}).</p>
     *
     * @param query the search query string
     * @param params the search request parameters
     * @param userBean the optional user bean for access control
     * @return this searcher's query, or empty when it cannot take part
     */
    protected Optional<QueryBuilder> buildSubQuery(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        return Optional.empty();
    }

    /**
     * Runs a single request that fuses this searcher's own query with the given subqueries, or
     * returns empty when it cannot.
     *
     * <p>This is the other half of {@link #buildSubQuery}: that one says "I can be fused", this
     * one says "I can do the fusing". Returning empty rather than ignoring the subqueries is what
     * keeps a searcher that cannot fuse from silently dropping the other branches - the caller
     * sees the refusal and falls back to fusing in Fess.</p>
     *
     * @param query the search query string
     * @param params the search request parameters
     * @param userBean the optional user bean for access control
     * @param subQueries the other searchers' queries, never empty
     * @return the fused search result, or empty when this searcher cannot fuse
     */
    protected Optional<SearchResult> searchWithSubQueries(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean, final List<QueryBuilder> subQueries) {
        return Optional.empty();
    }

}
