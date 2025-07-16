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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.QueryParseException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.score.QueryRescorer;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.util.LaRequestUtil;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.opensearch.search.rescore.RescorerBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * QueryHelper is responsible for building and managing OpenSearch queries for Fess search functionality.
 * It handles query construction, role-based access control, boost functions, sorting, and search preferences.
 * This class serves as the central component for translating user search requests into properly formatted
 * OpenSearch queries with appropriate filters and scoring mechanisms.
 */
public class QueryHelper {

    /**
     * Default constructor.
     */
    public QueryHelper() {
        // Default constructor
    }

    /** Logger for this class */
    private static final Logger logger = LogManager.getLogger(QueryHelper.class);

    /** Constant used to indicate that query-based preference should be used for search routing */
    protected static final String PREFERENCE_QUERY = "_query";

    /** Prefix used to identify sort parameters in search queries */
    protected String sortPrefix = "sort:";

    /** Additional query string to be appended to all search queries */
    protected String additionalQuery;

    /** Default sort builders to be applied when no explicit sorting is specified */
    protected SortBuilder<?>[] defaultSortBuilders;

    /** Prefix used for highlight field names in search results */
    protected String highlightPrefix = "hl_";

    /** Default facet information configuration for search results */
    protected FacetInfo defaultFacetInfo;

    /** Default geographic information configuration for location-based searches */
    protected GeoInfo defaultGeoInfo;

    /** Map containing field-specific boost values for search scoring */
    protected Map<String, String> fieldBoostMap = new HashMap<>();

    /** List of boost functions to be applied to search queries for custom scoring */
    protected List<FilterFunctionBuilder> boostFunctionList = new ArrayList<>();

    /** List of query rescorers for post-processing search results */
    protected List<QueryRescorer> queryRescorerList = new ArrayList<>();

    /**
     * Builds a complete QueryContext for search operations, applying all necessary filters,
     * boosts, and role-based access controls.
     *
     * @param searchRequestType the type of search request (e.g., regular search, admin search)
     * @param query the user's search query string
     * @param context a consumer that allows additional customization of the query context
     * @return a fully constructed QueryContext ready for OpenSearch execution
     */
    public QueryContext build(final SearchRequestType searchRequestType, final String query, final Consumer<QueryContext> context) {
        String q;
        if (additionalQuery != null && StringUtil.isNotBlank(query)) {
            q = query + " " + additionalQuery;
        } else {
            q = query;
        }

        final QueryContext queryContext = new QueryContext(q, true);
        buildBaseQuery(queryContext, context);
        buildBoostQuery(queryContext);
        buildRoleQuery(queryContext, searchRequestType);
        buildVirtualHostQuery(queryContext, searchRequestType);

        if (!queryContext.hasSorts() && defaultSortBuilders != null) {
            queryContext.addSorts(defaultSortBuilders);
        }
        return queryContext;
    }

    /**
     * Builds virtual host query filters to restrict search results to the current virtual host.
     * This method adds filters based on the virtual host key, except for admin searches.
     *
     * @param queryContext the query context to modify
     * @param searchRequestType the type of search request to determine if virtual host filtering should be applied
     */
    protected void buildVirtualHostQuery(final QueryContext queryContext, final SearchRequestType searchRequestType) {
        switch (searchRequestType) {
        case ADMIN_SEARCH:
            // nothing to do
            break;
        default:
            final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(key)) {
                queryContext.addQuery(boolQuery -> {
                    boolQuery.filter(QueryBuilders.termQuery(ComponentUtil.getFessConfig().getIndexFieldVirtualHost(), key));
                });
            }
            break;
        }
    }

    /**
     * Builds role-based access control query filters to restrict search results based on user roles.
     * This method applies role-based filtering to ensure users only see documents they have access to.
     *
     * @param queryContext the query context to modify
     * @param searchRequestType the type of search request to determine role filtering requirements
     */
    protected void buildRoleQuery(final QueryContext queryContext, final SearchRequestType searchRequestType) {
        if (queryContext.roleQueryEnabled()) {
            final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
            if (!roleSet.isEmpty()) {
                queryContext.addQuery(boolQuery -> buildRoleQuery(roleSet, boolQuery));
            }
        }
    }

    /**
     * Builds role-based query filters using the provided role set.
     * This method adds should clauses for allowed roles and must-not clauses for denied roles.
     *
     * @param roleSet the set of roles to use for filtering
     * @param boolQuery the boolean query builder to add role filters to
     */
    public void buildRoleQuery(final Set<String> roleSet, final BoolQueryBuilder boolQuery) {
        final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String roleField = fessConfig.getIndexFieldRole();
        roleSet.stream().forEach(name -> roleQuery.should(QueryBuilders.termQuery(roleField, name)));
        final String deniedPrefix = fessConfig.getRoleSearchDeniedPrefix();
        roleSet.stream().forEach(name -> roleQuery.mustNot(QueryBuilders.termQuery(roleField, deniedPrefix + name)));
        boolQuery.filter(roleQuery);
    }

    /**
     * Builds boost query functions to modify document scoring based on various factors.
     * This method adds field value factors, key matching boosts, and custom boost functions.
     *
     * @param queryContext the query context to add boost functions to
     */
    protected void buildBoostQuery(final QueryContext queryContext) {
        queryContext.addFunctionScore(list -> {
            list.add(new FilterFunctionBuilder(
                    ScoreFunctionBuilders.fieldValueFactorFunction(ComponentUtil.getFessConfig().getIndexFieldBoost())));
            ComponentUtil.getKeyMatchHelper().buildQuery(queryContext.getDefaultKeyword(), list);
            list.addAll(boostFunctionList);
        });
    }

    /**
     * Builds the base query from the user's search string using the configured query parser.
     * This method parses the query string, processes it, and applies any additional customizations.
     *
     * @param queryContext the query context containing the query string
     * @param context a consumer for additional query context customization
     * @throws InvalidQueryException if the query string cannot be parsed
     */
    public void buildBaseQuery(final QueryContext queryContext, final Consumer<QueryContext> context) {
        try {
            final Query query = getQueryParser().parse(queryContext.getQueryString());
            final QueryBuilder queryBuilder = ComponentUtil.getQueryProcessor().execute(queryContext, query, 1.0f);
            if (queryBuilder != null) {
                queryContext.setQueryBuilder(queryBuilder);
            } else {
                queryContext.setQueryBuilder(QueryBuilders.matchAllQuery());
            }
            // TODO options query
            context.accept(queryContext);
        } catch (final QueryParseException e) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                    "Invalid query: " + queryContext.getQueryString(), e);
        }
    }

    /**
     * Gets the query parser instance for parsing search query strings.
     *
     * @return the configured query parser
     */
    protected QueryParser getQueryParser() {
        return ComponentUtil.getQueryParser();
    }

    /**
     * Processes and sets search preferences for routing search requests to appropriate OpenSearch shards.
     * This method determines the preference value based on user roles, session information, or request parameters.
     *
     * @param searchRequestBuilder the search request builder to configure
     * @param userBean the optional user bean containing user information
     * @param query the search query string
     */
    public void processSearchPreference(final SearchRequestBuilder searchRequestBuilder, final OptionalThing<FessUserBean> userBean,
            final String query) {
        userBean.map(user -> {
            if (user.hasRoles(ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray())) {
                return Constants.SEARCH_PREFERENCE_LOCAL;
            }
            return user.getUserId();
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)).orElse(() -> LaRequestUtil.getOptionalRequest().map(r -> {
            final HttpSession session = r.getSession(false);
            if (session != null) {
                return session.getId();
            }
            final String preference = r.getParameter("preference");
            if (preference != null) {
                return Integer.toString(preference.hashCode());
            }
            final Object accessType = r.getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
            if (Constants.SEARCH_LOG_ACCESS_TYPE_JSON.equals(accessType)) {
                return processJsonSearchPreference(r, query);
            }
            if (Constants.SEARCH_LOG_ACCESS_TYPE_GSA.equals(accessType)) {
                return processGsaSearchPreference(r, query);
            }
            return null;
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)));
    }

    /**
     * Processes search preferences specifically for JSON API requests.
     * This method determines the preference value based on configuration and query content.
     *
     * @param req the HTTP servlet request
     * @param query the search query string
     * @return the preference value for JSON search requests, or null if not applicable
     */
    protected String processJsonSearchPreference(final HttpServletRequest req, final String query) {
        final String pref = ComponentUtil.getFessConfig().getQueryJsonDefaultPreference();
        if (PREFERENCE_QUERY.equals(pref)) {
            return Integer.toString(query.hashCode());
        }
        if (StringUtil.isNotBlank(pref)) {
            return pref;
        }
        return null;
    }

    /**
     * Processes search preferences specifically for GSA (Google Search Appliance) compatible requests.
     * This method determines the preference value based on configuration and query content.
     *
     * @param req the HTTP servlet request
     * @param query the search query string
     * @return the preference value for GSA search requests, or null if not applicable
     */
    protected String processGsaSearchPreference(final HttpServletRequest req, final String query) {
        final String pref = ComponentUtil.getFessConfig().getQueryGsaDefaultPreference();
        if (PREFERENCE_QUERY.equals(pref)) {
            return Integer.toString(query.hashCode());
        }
        if (StringUtil.isNotBlank(pref)) {
            return pref;
        }
        return null;
    }

    /**
     * Gets the sort prefix used for identifying sort parameters in search queries.
     *
     * @return the sortPrefix
     */
    public String getSortPrefix() {
        return sortPrefix;
    }

    /**
     * Sets the sort prefix used for identifying sort parameters in search queries.
     *
     * @param sortPrefix the sortPrefix to set
     */
    public void setSortPrefix(final String sortPrefix) {
        this.sortPrefix = sortPrefix;
    }

    /**
     * Gets the additional query string that is appended to all search queries.
     *
     * @return the additionalQuery
     */
    public String getAdditionalQuery() {
        return additionalQuery;
    }

    /**
     * Sets the additional query string that is appended to all search queries.
     *
     * @param additionalQuery the additionalQuery to set
     */
    public void setAdditionalQuery(final String additionalQuery) {
        this.additionalQuery = additionalQuery;
    }

    /**
     * Adds a default sort configuration to be applied when no explicit sorting is specified.
     * This method appends the new sort to existing default sorts.
     *
     * @param fieldName the field name to sort by
     * @param order the sort order ("ASC" or "DESC")
     */
    public void addDefaultSort(final String fieldName, final String order) {
        final List<SortBuilder<?>> list = new ArrayList<>();
        if (defaultSortBuilders != null) {
            stream(defaultSortBuilders).of(stream -> stream.forEach(builder -> list.add(builder)));
        }
        list.add(createFieldSortBuilder(fieldName, SortOrder.DESC.toString().equalsIgnoreCase(order) ? SortOrder.DESC : SortOrder.ASC));
        defaultSortBuilders = list.toArray(new SortBuilder[list.size()]);
    }

    /**
     * Creates a sort builder for the specified field and order.
     * This method handles special cases for score fields and regular field sorting.
     *
     * @param field the field name to sort by
     * @param order the sort order (ASC or DESC)
     * @return a configured sort builder
     */
    protected SortBuilder<?> createFieldSortBuilder(final String field, final SortOrder order) {
        if (QueryFieldConfig.SCORE_FIELD.equals(field) || QueryFieldConfig.DOC_SCORE_FIELD.equals(field)) {
            return SortBuilders.scoreSort().order(order);
        }
        return SortBuilders.fieldSort(field).order(order);
    }

    /**
     * Sets the prefix used for highlight field names in search results.
     *
     * @param highlightPrefix the prefix string to use for highlight fields
     */
    public void setHighlightPrefix(final String highlightPrefix) {
        this.highlightPrefix = highlightPrefix;
    }

    /**
     * Gets the current highlight prefix used for highlight field names.
     *
     * @return the current highlight prefix
     */
    public String getHighlightPrefix() {
        return highlightPrefix;
    }

    /**
     * Gets the default facet information configuration.
     *
     * @return the default facet information, or null if not configured
     */
    public FacetInfo getDefaultFacetInfo() {
        return defaultFacetInfo;
    }

    /**
     * Sets the default facet information configuration for search results.
     *
     * @param defaultFacetInfo the facet information to use as default
     */
    public void setDefaultFacetInfo(final FacetInfo defaultFacetInfo) {
        this.defaultFacetInfo = defaultFacetInfo;
    }

    /**
     * Gets the default geographic information configuration.
     *
     * @return the default geographic information, or null if not configured
     */
    public GeoInfo getDefaultGeoInfo() {
        return defaultGeoInfo;
    }

    /**
     * Sets the default geographic information configuration for location-based searches.
     *
     * @param defaultGeoInfo the geographic information to use as default
     */
    public void setDefaultGeoInfo(final GeoInfo defaultGeoInfo) {
        this.defaultGeoInfo = defaultGeoInfo;
    }

    /**
     * Generates a unique identifier string by creating a UUID and removing hyphens.
     *
     * @return a unique identifier string
     */
    public String generateId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    /**
     * Adds a boost function to modify document scoring during search.
     * This method adds a boost function that applies to all documents.
     *
     * @param scoreFunction the score function to add for boosting
     */
    public void addBoostFunction(final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(scoreFunction));
    }

    /**
     * Adds a boost function with a filter to modify document scoring during search.
     * This method adds a boost function that applies only to documents matching the filter.
     *
     * @param filter the query filter to determine which documents the boost applies to
     * @param scoreFunction the score function to add for boosting
     */
    public void addBoostFunction(final QueryBuilder filter, final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(filter, scoreFunction));
    }

    /**
     * Gets an array of rescorer builders for post-processing search results.
     * This method evaluates all configured query rescorers with the provided parameters.
     *
     * @param params parameters to pass to the rescorers during evaluation
     * @return an array of rescorer builders, filtered to exclude null values
     */
    public RescorerBuilder<?>[] getRescorers(final Map<String, Object> params) {
        return queryRescorerList.stream().map(r -> r.evaluate(params)).filter(b -> b != null).toArray(n -> new RescorerBuilder<?>[n]);
    }

    /**
     * Adds a query rescorer for post-processing search results.
     * Query rescorers allow modification of search scores after the initial query execution.
     *
     * @param rescorer the query rescorer to add
     */
    public void addQueryRescorer(final QueryRescorer rescorer) {
        queryRescorerList.add(rescorer);
    }
}
