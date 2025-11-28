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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.fess.Constants.DEFAULT_FIELD;
import static org.codelibs.fess.query.QueryFieldConfig.INURL_FIELD;
import static org.codelibs.fess.query.QueryFieldConfig.SITE_FIELD;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.message.UserMessages;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * Command class for handling term query execution and conversion.
 * This class processes Lucene TermQuery objects and converts them to OpenSearch QueryBuilder instances.
 */
public class TermQueryCommand extends QueryCommand {
    private static final Logger logger = LogManager.getLogger(TermQueryCommand.class);

    /**
     * Default constructor for TermQueryCommand.
     */
    public TermQueryCommand() {
        super();
    }

    private static final String SORT_FIELD = "sort";

    @Override
    protected String getQueryClassName() {
        return TermQuery.class.getSimpleName();
    }

    @Override
    public QueryBuilder execute(final QueryContext context, final Query query, final float boost) {
        if (query instanceof final TermQuery termQuery) {
            if (logger.isDebugEnabled()) {
                logger.debug("TermQuery: query={}, boost={}", query, boost);
            }
            return convertTermQuery(context, termQuery, boost);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                "Unknown q: " + query.getClass() + " => " + query);
    }

    /**
     * Converts a TermQuery to a QueryBuilder with the given boost value.
     *
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertTermQuery(final QueryContext context, final TermQuery termQuery, final float boost) {
        final String field = getSearchField(context.getDefaultField(), termQuery.getTerm().field());
        final String text = termQuery.getTerm().text();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return convertTermQuery(fessConfig, context, termQuery, boost, field, text);
    }

    /**
     * Converts a TermQuery to a QueryBuilder with field-specific handling.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertTermQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        if (fessConfig.getQueryReplaceTermWithPrefixQueryAsBoolean() && text.length() > 1 && text.endsWith("*")) {
            return convertPrefixQuery(fessConfig, context, termQuery, boost, field, text);
        }
        if (DEFAULT_FIELD.equals(field)) {
            return convertDefaultTermQuery(fessConfig, context, termQuery, boost, field, text);
        }
        if (SORT_FIELD.equals(field)) {
            return convertSortQuery(fessConfig, context, termQuery, boost, field, text);
        }
        if (SITE_FIELD.equals(field)) {
            return convertSiteQuery(fessConfig, context, termQuery, boost, field, text);
        }
        if (INURL_FIELD.equals(field)
                || StringUtil.equals(field, context.getDefaultField()) && fessConfig.getIndexFieldUrl().equals(context.getDefaultField())) {
            return convertWildcardQuery(fessConfig, context, termQuery, boost, field, text);
        }
        if (!isSearchField(field)) {
            final String origQuery = termQuery.toString();
            return convertDefaultTermQuery(fessConfig, context, termQuery, boost, DEFAULT_FIELD, origQuery);
        }
        if (getQueryFieldConfig().notAnalyzedFieldSet.contains(field)) {
            return convertKeywordQuery(fessConfig, context, termQuery, boost, field, text);
        }
        return convertTextQuery(fessConfig, context, termQuery, boost, field, text);
    }

    /**
     * Converts a term query to a text-based match phrase query.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertTextQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        context.addFieldLog(field, text);
        context.addHighlightedQuery(text);
        return buildMatchPhraseQuery(field, text).boost(boost);
    }

    /**
     * Converts a term query to a keyword-based exact term query.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertKeywordQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        context.addFieldLog(field, text);
        context.addHighlightedQuery(text);
        return QueryBuilders.termQuery(field, text).boost(boost);
    }

    /**
     * Converts a term query to a wildcard query for URL field matching.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertWildcardQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        final String urlField = fessConfig.getIndexFieldUrl();
        final String queryString = "*" + text + "*";
        context.addFieldLog(urlField, queryString);
        context.addHighlightedQuery(text);
        return QueryBuilders.wildcardQuery(urlField, queryString).boost(boost);
    }

    /**
     * Converts a term query ending with asterisk to a prefix query.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertPrefixQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        return getQueryProcessor().execute(context, new PrefixQuery(new Term(field, text.substring(0, text.length() - 1))), boost);
    }

    /**
     * Converts a sort field query to add sort criteria to the context.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return null as this method only adds sort criteria
     */
    protected QueryBuilder convertSortQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        split(text, ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(t -> {
            final String[] values = t.split("\\.");
            if (values.length > 2) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQuerySortValue(UserMessages.GLOBAL_PROPERTY_KEY, text),
                        "Invalid sort field: " + termQuery);
            }
            final String sortField = values[0];
            if (!getQueryFieldConfig().isSortField(sortField)) {
                throw new InvalidQueryException(
                        messages -> messages.addErrorsInvalidQueryUnsupportedSortField(UserMessages.GLOBAL_PROPERTY_KEY, sortField),
                        "Unsupported sort field: " + termQuery);
            }
            final SortOrder sortOrder;
            if (values.length == 2) {
                if (SortOrder.DESC.toString().equalsIgnoreCase(values[1])) {
                    sortOrder = SortOrder.DESC;
                } else if (SortOrder.ASC.toString().equalsIgnoreCase(values[1])) {
                    sortOrder = SortOrder.ASC;
                } else {
                    throw new InvalidQueryException(
                            messages -> messages.addErrorsInvalidQueryUnsupportedSortOrder(UserMessages.GLOBAL_PROPERTY_KEY, values[1]),
                            "Invalid sort order: " + termQuery);
                }
            } else {
                sortOrder = SortOrder.ASC;
            }
            context.addSorts(createFieldSortBuilder(sortField, sortOrder));
        }));
        return null;
    }

    /**
     * Converts a term query for the default field with fuzzy matching support.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertDefaultTermQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        context.addFieldLog(field, text);
        context.addHighlightedQuery(text);
        final DefaultQueryBuilder defaultQuery =
                buildDefaultQueryBuilder(fessConfig, context, (f, b) -> buildMatchPhraseQuery(f, text).boost(b * boost));
        final Integer fuzzyMinLength = fessConfig.getQueryBoostFuzzyMinLengthAsInteger();
        if (fuzzyMinLength >= 0 && text.length() >= fuzzyMinLength) {
            defaultQuery.add(QueryBuilders.fuzzyQuery(fessConfig.getIndexFieldTitle(), text)
                    .boost(fessConfig.getQueryBoostFuzzyTitleAsDecimal().floatValue())
                    .prefixLength(fessConfig.getQueryBoostFuzzyTitlePrefixLengthAsInteger())
                    .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryBoostFuzzyTitleTranspositions()))
                    .fuzziness(Fuzziness.build(fessConfig.getQueryBoostFuzzyTitleFuzziness()))
                    .maxExpansions(fessConfig.getQueryBoostFuzzyTitleExpansionsAsInteger()));
            defaultQuery.add(QueryBuilders.fuzzyQuery(fessConfig.getIndexFieldContent(), text)
                    .prefixLength(fessConfig.getQueryBoostFuzzyContentPrefixLengthAsInteger())
                    .transpositions(Constants.TRUE.equalsIgnoreCase(fessConfig.getQueryBoostFuzzyContentTranspositions()))
                    .boost(fessConfig.getQueryBoostFuzzyContentAsDecimal().floatValue())
                    .fuzziness(Fuzziness.build(fessConfig.getQueryBoostFuzzyContentFuzziness()))
                    .maxExpansions(fessConfig.getQueryBoostFuzzyContentExpansionsAsInteger()));
        }
        return defaultQuery;
    }

    /**
     * Converts a site field query to a prefix query for site filtering.
     *
     * @param fessConfig the Fess configuration
     * @param context the query context
     * @param termQuery the term query to convert
     * @param boost the boost value to apply
     * @param field the field name
     * @param text the query text
     * @return the converted QueryBuilder
     */
    protected QueryBuilder convertSiteQuery(final FessConfig fessConfig, final QueryContext context, final TermQuery termQuery,
            final float boost, final String field, final String text) {
        final String siteField = fessConfig.getIndexFieldSite();
        context.addFieldLog(siteField, text + "*");
        context.addHighlightedQuery(text);
        return QueryBuilders.prefixQuery(siteField, text).boost(boost);
    }
}
