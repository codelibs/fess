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
import org.codelibs.fess.app.pager.RelatedContentPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.RelatedContentCB;
import org.codelibs.fess.opensearch.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing related content entities.
 * This service provides CRUD operations for related content, including
 * retrieval, storage, deletion, and search functionality.
 */
public class RelatedContentService extends FessAppService {

    /**
     * Default constructor.
     * Creates a new instance of RelatedContentService.
     */
    public RelatedContentService() {
        super();
    }

    /**
     * Behavior class for RelatedContent entity operations.
     * Provides database access methods for related content management.
     */
    @Resource
    protected RelatedContentBhv relatedContentBhv;

    /**
     * Configuration settings for Fess application.
     * Contains various configuration parameters used throughout the application.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of related content entities.
     *
     * @param relatedContentPager the pager object containing pagination and search parameters
     * @return a list of RelatedContent entities matching the specified criteria
     */
    public List<RelatedContent> getRelatedContentList(final RelatedContentPager relatedContentPager) {

        final PagingResultBean<RelatedContent> relatedContentList = relatedContentBhv.selectPage(cb -> {
            cb.paging(relatedContentPager.getPageSize(), relatedContentPager.getCurrentPageNumber());
            setupListCondition(cb, relatedContentPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(relatedContentList, relatedContentPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        relatedContentPager.setPageNumberList(
                relatedContentList.pageRange(op -> op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger())).createPageNumberList());

        return relatedContentList;
    }

    /**
     * Retrieves a specific related content entity by its ID.
     *
     * @param id the unique identifier of the related content
     * @return an OptionalEntity containing the RelatedContent if found, empty otherwise
     */
    public OptionalEntity<RelatedContent> getRelatedContent(final String id) {
        return relatedContentBhv.selectByPK(id);
    }

    /**
     * Stores (inserts or updates) a related content entity.
     * After storing, updates the related content helper to refresh the cache.
     *
     * @param relatedContent the RelatedContent entity to store
     */
    public void store(final RelatedContent relatedContent) {

        relatedContentBhv.insertOrUpdate(relatedContent, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedContentHelper().update();
    }

    /**
     * Deletes a related content entity from the database.
     * After deletion, updates the related content helper to refresh the cache.
     *
     * @param relatedContent the RelatedContent entity to delete
     */
    public void delete(final RelatedContent relatedContent) {

        relatedContentBhv.delete(relatedContent, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedContentHelper().update();
    }

    /**
     * Sets up the search conditions for listing related content entities.
     * Configures wildcard searches for term and content fields, and sets up ordering.
     *
     * @param cb the condition bean for building the query
     * @param relatedContentPager the pager containing search parameters
     */
    protected void setupListCondition(final RelatedContentCB cb, final RelatedContentPager relatedContentPager) {
        if (StringUtil.isNotBlank(relatedContentPager.term)) {
            cb.query().setTerm_Wildcard(wrapQuery(relatedContentPager.term));
        }
        if (StringUtil.isNotBlank(relatedContentPager.content)) {
            cb.query().setContent_Wildcard(wrapQuery(relatedContentPager.content));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Term_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

    /**
     * Retrieves a list of all available related content entities.
     * Results are ordered by term and limited by the configured maximum fetch size.
     *
     * @return a list of all available RelatedContent entities
     */
    public List<RelatedContent> getAvailableRelatedContentList() {
        return relatedContentBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(fessConfig.getPageDocboostMaxFetchSizeAsInteger());
        });
    }

}