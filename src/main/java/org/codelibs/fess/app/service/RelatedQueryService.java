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
package org.codelibs.fess.app.service;

import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RelatedQueryPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.RelatedQueryCB;
import org.codelibs.fess.opensearch.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing related query entities.
 * This service provides operations to retrieve, store, and delete related queries,
 * which are used to suggest alternative search terms to users.
 */
public class RelatedQueryService extends FessAppService {

    /**
     * Default constructor for RelatedQueryService.
     * This constructor is used by the DI container to create an instance of the service.
     */
    public RelatedQueryService() {
        // Default constructor
    }

    /**
     * Behavior class for accessing related query data in the database.
     * This provides database operations for RelatedQuery entities.
     */
    @Resource
    protected RelatedQueryBhv relatedQueryBhv;

    /**
     * Configuration properties for Fess application.
     * Used to access various configuration settings like paging parameters.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of related queries based on the provided pager parameters.
     * This method performs a database query with pagination and updates the pager with result information.
     *
     * @param relatedQueryPager the pager containing pagination parameters and search conditions
     * @return a list of RelatedQuery entities matching the search criteria
     */
    public List<RelatedQuery> getRelatedQueryList(final RelatedQueryPager relatedQueryPager) {

        final PagingResultBean<RelatedQuery> relatedQueryList = relatedQueryBhv.selectPage(cb -> {
            cb.paging(relatedQueryPager.getPageSize(), relatedQueryPager.getCurrentPageNumber());
            setupListCondition(cb, relatedQueryPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(relatedQueryList, relatedQueryPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        relatedQueryPager.setPageNumberList(
                relatedQueryList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return relatedQueryList;
    }

    /**
     * Retrieves a specific related query by its unique identifier.
     *
     * @param id the unique identifier of the related query to retrieve
     * @return an OptionalEntity containing the RelatedQuery if found, or empty if not found
     */
    public OptionalEntity<RelatedQuery> getRelatedQuery(final String id) {
        return relatedQueryBhv.selectByPK(id);
    }

    /**
     * Stores (inserts or updates) a related query in the database.
     * After storing, the related query helper is updated to refresh the cache.
     *
     * @param relatedQuery the RelatedQuery entity to store
     */
    public void store(final RelatedQuery relatedQuery) {

        relatedQueryBhv.insertOrUpdate(relatedQuery, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedQueryHelper().update();
    }

    /**
     * Deletes a related query from the database.
     * After deletion, the related query helper is updated to refresh the cache.
     *
     * @param relatedQuery the RelatedQuery entity to delete
     */
    public void delete(final RelatedQuery relatedQuery) {

        relatedQueryBhv.delete(relatedQuery, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedQueryHelper().update();
    }

    /**
     * Sets up the search conditions for the related query list based on the pager parameters.
     * This method configures wildcard searches for term and queries fields, and sets up ordering.
     *
     * @param cb the condition bean for building the query
     * @param relatedQueryPager the pager containing search parameters
     */
    protected void setupListCondition(final RelatedQueryCB cb, final RelatedQueryPager relatedQueryPager) {
        if (StringUtil.isNotBlank(relatedQueryPager.term)) {
            cb.query().setTerm_Wildcard(wrapQuery(relatedQueryPager.term));
        }
        if (StringUtil.isNotBlank(relatedQueryPager.queries)) {
            cb.query().setQueries_Wildcard(wrapQuery(relatedQueryPager.queries));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Term_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}