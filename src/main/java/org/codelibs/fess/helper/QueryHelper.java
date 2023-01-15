/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

public class QueryHelper {
    private static final Logger logger = LogManager.getLogger(QueryHelper.class);

    protected static final String PREFERENCE_QUERY = "_query";

    protected String sortPrefix = "sort:";

    protected String additionalQuery;

    protected SortBuilder<?>[] defaultSortBuilders;

    protected String highlightPrefix = "hl_";

    protected FacetInfo defaultFacetInfo;

    protected GeoInfo defaultGeoInfo;

    protected Map<String, String> fieldBoostMap = new HashMap<>();

    protected List<FilterFunctionBuilder> boostFunctionList = new ArrayList<>();

    protected List<QueryRescorer> queryRescorerList = new ArrayList<>();

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

    protected void buildRoleQuery(final QueryContext queryContext, final SearchRequestType searchRequestType) {
        if (queryContext.roleQueryEnabled()) {
            final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
            if (!roleSet.isEmpty()) {
                queryContext.addQuery(boolQuery -> buildRoleQuery(roleSet, boolQuery));
            }
        }
    }

    public void buildRoleQuery(final Set<String> roleSet, final BoolQueryBuilder boolQuery) {
        final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String roleField = fessConfig.getIndexFieldRole();
        roleSet.stream().forEach(name -> roleQuery.should(QueryBuilders.termQuery(roleField, name)));
        final String deniedPrefix = fessConfig.getRoleSearchDeniedPrefix();
        roleSet.stream().forEach(name -> roleQuery.mustNot(QueryBuilders.termQuery(roleField, deniedPrefix + name)));
        boolQuery.filter(roleQuery);
    }

    protected void buildBoostQuery(final QueryContext queryContext) {
        queryContext.addFunctionScore(list -> {
            list.add(new FilterFunctionBuilder(
                    ScoreFunctionBuilders.fieldValueFactorFunction(ComponentUtil.getFessConfig().getIndexFieldBoost())));
            ComponentUtil.getKeyMatchHelper().buildQuery(queryContext.getDefaultKeyword(), list);
            list.addAll(boostFunctionList);
        });
    }

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

    protected QueryParser getQueryParser() {
        return ComponentUtil.getQueryParser();
    }

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
     * @return the sortPrefix
     */
    public String getSortPrefix() {
        return sortPrefix;
    }

    /**
     * @param sortPrefix the sortPrefix to set
     */
    public void setSortPrefix(final String sortPrefix) {
        this.sortPrefix = sortPrefix;
    }

    /**
     * @return the additionalQuery
     */
    public String getAdditionalQuery() {
        return additionalQuery;
    }

    /**
     * @param additionalQuery the additionalQuery to set
     */
    public void setAdditionalQuery(final String additionalQuery) {
        this.additionalQuery = additionalQuery;
    }

    public void addDefaultSort(final String fieldName, final String order) {
        final List<SortBuilder<?>> list = new ArrayList<>();
        if (defaultSortBuilders != null) {
            stream(defaultSortBuilders).of(stream -> stream.forEach(builder -> list.add(builder)));
        }
        list.add(createFieldSortBuilder(fieldName, SortOrder.DESC.toString().equalsIgnoreCase(order) ? SortOrder.DESC : SortOrder.ASC));
        defaultSortBuilders = list.toArray(new SortBuilder[list.size()]);
    }

    protected SortBuilder<?> createFieldSortBuilder(final String field, final SortOrder order) {
        if (QueryFieldConfig.SCORE_FIELD.equals(field) || QueryFieldConfig.DOC_SCORE_FIELD.equals(field)) {
            return SortBuilders.scoreSort().order(order);
        }
        return SortBuilders.fieldSort(field).order(order);
    }

    public void setHighlightPrefix(final String highlightPrefix) {
        this.highlightPrefix = highlightPrefix;
    }

    public String getHighlightPrefix() {
        return highlightPrefix;
    }

    public FacetInfo getDefaultFacetInfo() {
        return defaultFacetInfo;
    }

    public void setDefaultFacetInfo(final FacetInfo defaultFacetInfo) {
        this.defaultFacetInfo = defaultFacetInfo;
    }

    public GeoInfo getDefaultGeoInfo() {
        return defaultGeoInfo;
    }

    public void setDefaultGeoInfo(final GeoInfo defaultGeoInfo) {
        this.defaultGeoInfo = defaultGeoInfo;
    }

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    public void addBoostFunction(final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(scoreFunction));
    }

    public void addBoostFunction(final QueryBuilder filter, final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(filter, scoreFunction));
    }

    public RescorerBuilder<?>[] getRescorers(final Map<String, Object> params) {
        return queryRescorerList.stream().map(r -> r.evaluate(params)).filter(b -> b != null).toArray(n -> new RescorerBuilder<?>[n]);
    }

    public void addQueryRescorer(final QueryRescorer rescorer) {
        queryRescorerList.add(rescorer);
    }
}
