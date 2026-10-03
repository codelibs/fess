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
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.util.LaRequestUtil;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.DisMaxQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchNoneQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.sort.SortBuilder;
import org.codelibs.fesen.opensearch.search.sort.SortBuilders;
import org.codelibs.fesen.opensearch.search.sort.SortOrder;

/**
 * Abstract base class for query command implementations.
 * Provides common functionality for processing and executing search queries.
 */
public abstract class QueryCommand {
    private static final Logger logger = LogManager.getLogger(QueryCommand.class);

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
     * Uses O(1) Set lookup for improved performance.
     * @param field The field name to check.
     * @return True if the field is a search field, false otherwise.
     */
    protected boolean isSearchField(final String field) {
        final QueryFieldConfig config = getQueryFieldConfig();
        return config.searchFieldSet != null && config.searchFieldSet.contains(field);
    }

    /**
     * Checks if a condition on the field must be limited to the tags that the caller can see. The {@code tag} field
     * holds the values of every tag of a document, including tags whose label permissions do not include the caller,
     * so a condition on it is restricted for every search except the admin search.
     * @param context The query context.
     * @param field The field name.
     * @return True if the field is the tag field and the condition must be restricted.
     */
    protected boolean isRestrictedTagField(final QueryContext context, final String field) {
        return field != null && field.equals(ComponentUtil.getFessConfig().getIndexFieldTag())
                && !SearchRequestType.ADMIN_SEARCH.equals(context.getSearchRequestType());
    }

    /**
     * Checks if the caller can see a tag. The visible tags are resolved once per query context.
     * @param context The query context.
     * @param value The tag value.
     * @return True if the tag is visible to the caller.
     */
    protected boolean isVisibleTag(final QueryContext context, final String value) {
        Set<String> valueSet = context.getVisibleTagValueSet();
        if (valueSet == null) {
            valueSet = getVisibleTagValueSet(context);
            context.setVisibleTagValueSet(valueSet);
        }
        return valueSet.contains(value);
    }

    /**
     * Resolves the values of the tags that the caller can see.
     * @param context The query context.
     * @return The visible tag values, or an empty set if they cannot be resolved.
     */
    protected Set<String> getVisibleTagValueSet(final QueryContext context) {
        try {
            final SearchRequestType searchRequestType =
                    context.getSearchRequestType() != null ? context.getSearchRequestType() : SearchRequestType.JSON;
            final Locale locale = LaRequestUtil.getOptionalRequest().map(request -> request.getLocale()).orElse(null);
            return ComponentUtil.getLabelTypeHelper().getTagValueSet(searchRequestType, locale != null ? locale : Locale.ROOT);
        } catch (final RuntimeException e) {
            logger.debug("Failed to resolve the visible tags; no tag condition matches.", e);
            return Collections.emptySet();
        }
    }

    /**
     * Builds the query for a condition on the tag field that the caller may not use: it matches no document, so the
     * condition does not reveal which documents carry a tag that the caller cannot see.
     * @param context The query context.
     * @param field The field name.
     * @param text The query text.
     * @return A query that matches no document.
     */
    protected QueryBuilder buildHiddenTagQuery(final QueryContext context, final String field, final String text) {
        context.addFieldLog(field, text);
        return new MatchNoneQueryBuilder();
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

    /**
     * Functional interface for building field-specific query builders.
     */
    protected interface FieldQueryBuilder {
        /**
         * Builds a query builder for the specified field and text.
         * @param field The field name.
         * @param text The query text.
         * @param boost The boost value.
         * @return The created query builder.
         */
        QueryBuilder buildQuery(String field, String text, float boost);
    }

    /**
     * Template method that handles the common pattern of query conversion:
     * 1. Check if field is DEFAULT_FIELD and apply default query builder
     * 2. Check if field is a search field and apply field-specific query
     * 3. Fall back to default query builder for unsupported fields
     *
     * This reduces code duplication across query command implementations.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param field the field name
     * @param text the query text
     * @param boost the boost value
     * @param defaultBuilder function to build default queries
     * @param fieldBuilder function to build field-specific queries
     * @return the constructed query builder
     */
    protected QueryBuilder convertWithFieldCheck(final FessConfig fessConfig, final QueryContext context, final String field,
            final String text, final float boost, final DefaultQueryBuilderFunction defaultBuilder, final FieldQueryBuilder fieldBuilder) {

        context.addFieldLog(field, text);
        context.addHighlightedQuery(text);

        if (Constants.DEFAULT_FIELD.equals(field)) {
            return buildDefaultQueryBuilder(fessConfig, context, defaultBuilder);
        }

        if (isSearchField(field)) {
            return fieldBuilder.buildQuery(field, text, boost);
        }

        // Fallback: treat as default field query
        context.addFieldLog(Constants.DEFAULT_FIELD, text);
        return buildDefaultQueryBuilder(fessConfig, context, defaultBuilder);
    }
}
