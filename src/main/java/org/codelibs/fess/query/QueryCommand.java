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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.lang.Character.UnicodeBlock;

import org.apache.lucene.search.Query;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.DisMaxQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * Abstract base class for query command implementations.
 * Provides common functionality for processing and executing search queries.
 */
public abstract class QueryCommand {

    /**
     * Default constructor for QueryCommand.
     * Creates a new instance of the query command with default settings.
     */
    public QueryCommand() {
        // Default constructor
    }

    /**
     * Executes the query command and returns a QueryBuilder.
     * @param context The query context containing search parameters.
     * @param query The Lucene query to execute.
     * @param boost The boost factor to apply.
     * @return The executed QueryBuilder.
     */
    public abstract QueryBuilder execute(final QueryContext context, final Query query, final float boost);

    /**
     * Gets the class name of the query this command handles.
     * @return The query class name.
     */
    protected abstract String getQueryClassName();

    /**
     * Registers this query command with the query processor.
     * Associates this command with its query class name in the processor.
     */
    public void register() {
        ComponentUtil.getQueryProcessor().add(getQueryClassName(), this);
    }

    /**
     * Gets the query field configuration.
     * @return The query field configuration instance.
     */
    protected QueryFieldConfig getQueryFieldConfig() {
        return ComponentUtil.getQueryFieldConfig();
    }

    /**
     * Gets the query processor instance.
     * @return The query processor instance.
     */
    protected QueryProcessor getQueryProcessor() {
        return ComponentUtil.getQueryProcessor();
    }

    /**
     * Creates a sort builder for the specified field and order.
     * @param field The field name to sort by.
     * @param order The sort order (ascending or descending).
     * @return The appropriate sort builder for the field.
     */
    protected SortBuilder<?> createFieldSortBuilder(final String field, final SortOrder order) {
        if (QueryFieldConfig.SCORE_FIELD.equals(field) || QueryFieldConfig.DOC_SCORE_FIELD.equals(field)) {
            return SortBuilders.scoreSort().order(order);
        }
        return SortBuilders.fieldSort(field).order(order);
    }

    /**
     * Checks if the specified field is a search field.
     * @param field The field name to check.
     * @return True if the field is a search field, false otherwise.
     */
    protected boolean isSearchField(final String field) {
        for (final String searchField : getQueryFieldConfig().searchFields) {
            if (searchField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the query languages from the current request.
     * @return An optional containing the query languages array, or empty if not available.
     */
    protected OptionalThing<String[]> getQueryLanguages() {
        return LaRequestUtil.getOptionalRequest()
                .map(request -> ComponentUtil.getFessConfig()
                        .getQueryLanguages(request.getLocales(), (String[]) request.getAttribute(Constants.REQUEST_LANGUAGES)));
    }

    /**
     * Builds a default query builder with configured fields and boost values.
     * @param fessConfig The Fess configuration.
     * @param context The query context.
     * @param builder The function to build individual field queries.
     * @return The constructed default query builder.
     */
    protected DefaultQueryBuilder buildDefaultQueryBuilder(final FessConfig fessConfig, final QueryContext context,
            final DefaultQueryBuilderFunction builder) {
        final DefaultQueryBuilder defaultQuery = createDefaultQueryBuilder();
        defaultQuery.add(builder.apply(fessConfig.getIndexFieldTitle(), fessConfig.getQueryBoostTitleAsDecimal().floatValue()));
        defaultQuery.add(builder.apply(fessConfig.getIndexFieldContent(), fessConfig.getQueryBoostContentAsDecimal().floatValue()));
        final float importantContentBoost = fessConfig.getQueryBoostImportantContentAsDecimal().floatValue();
        if (importantContentBoost >= 0.0f) {
            defaultQuery.add(builder.apply(fessConfig.getIndexFieldImportantContent(), importantContentBoost));
        }
        final float importantContantLangBoost = fessConfig.getQueryBoostImportantContentLangAsDecimal().floatValue();
        getQueryLanguages().ifPresent(langs -> stream(langs).of(stream -> stream.forEach(lang -> {
            defaultQuery.add(
                    builder.apply(fessConfig.getIndexFieldTitle() + "_" + lang, fessConfig.getQueryBoostTitleLangAsDecimal().floatValue()));
            defaultQuery.add(builder.apply(fessConfig.getIndexFieldContent() + "_" + lang,
                    fessConfig.getQueryBoostContentLangAsDecimal().floatValue()));
            if (importantContantLangBoost >= 0.0f) {
                defaultQuery.add(builder.apply(fessConfig.getIndexFieldImportantContent() + "_" + lang, importantContantLangBoost));
            }
        })));
        getQueryFieldConfig().additionalDefaultList.stream().forEach(f -> {
            final QueryBuilder query = builder.apply(f.getFirst(), f.getSecond());
            defaultQuery.add(query);
        });
        return defaultQuery;
    }

    /**
     * Creates a default query builder based on the configured query type.
     * @return The default query builder (either dismax or bool query).
     */
    protected DefaultQueryBuilder createDefaultQueryBuilder() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if ("dismax".equals(fessConfig.getQueryDefaultQueryType())) {
            final DisMaxQueryBuilder disMaxQuery = QueryBuilders.disMaxQuery();
            disMaxQuery.tieBreaker(fessConfig.getQueryDismaxTieBreakerAsDecimal().floatValue());
            return new DefaultQueryBuilder(disMaxQuery);
        }

        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        final String minimumShouldMatch = fessConfig.getQueryBoolMinimumShouldMatch();
        if (StringUtil.isNotBlank(minimumShouldMatch)) {
            boolQuery.minimumShouldMatch(minimumShouldMatch);
        }
        return new DefaultQueryBuilder(boolQuery);
    }

    /**
     * Builds a match phrase query, with special handling for single CJK characters.
     * For single CJK characters in title or content fields, uses prefix query instead.
     * @param f The field name.
     * @param text The text to search for.
     * @return The appropriate query builder.
     */
    protected QueryBuilder buildMatchPhraseQuery(final String f, final String text) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (text == null || text.length() != 1
                || !fessConfig.getIndexFieldTitle().equals(f) && !fessConfig.getIndexFieldContent().equals(f)) {
            return QueryBuilders.matchPhraseQuery(f, text);
        }

        final UnicodeBlock block = UnicodeBlock.of(text.codePointAt(0));
        if (block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //
                || block == UnicodeBlock.HIRAGANA //
                || block == UnicodeBlock.KATAKANA //
                || block == UnicodeBlock.HANGUL_SYLLABLES //
        ) {
            return QueryBuilders.prefixQuery(f, text);
        }
        return QueryBuilders.matchPhraseQuery(f, text);
    }

    /**
     * Gets the actual search field, replacing default field placeholder if needed.
     * @param defaultField The default field to use if field is the default placeholder.
     * @param field The field name to check.
     * @return The actual field name to use for searching.
     */
    protected String getSearchField(final String defaultField, final String field) {
        if (Constants.DEFAULT_FIELD.equals(field) && defaultField != null) {
            return defaultField;
        }
        return field;
    }

    /**
     * Functional interface for building query builders with field and boost parameters.
     */
    protected interface DefaultQueryBuilderFunction {
        /**
         * Applies the function to create a query builder for the specified field and boost.
         * @param field The field name.
         * @param boost The boost value.
         * @return The created query builder.
         */
        QueryBuilder apply(String field, float boost);
    }
}
