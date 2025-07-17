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
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ReqHeaderPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.RequestHeaderCB;
import org.codelibs.fess.opensearch.config.exbhv.RequestHeaderBhv;
import org.codelibs.fess.opensearch.config.exentity.RequestHeader;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing request headers used in web crawling configurations.
 * This service provides CRUD operations for request headers that are applied
 * during web crawling to configure HTTP request behavior.
 *
 */
public class RequestHeaderService {

    /**
     * Behavior for request header database operations.
     */
    @Resource
    protected RequestHeaderBhv requestHeaderBhv;

    /**
     * Fess configuration settings.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor for RequestHeaderService.
     * Initializes the service with dependency injection.
     */
    public RequestHeaderService() {
        // Default constructor
    }

    /**
     * Retrieves a paginated list of request headers based on the provided pager criteria.
     *
     * @param requestHeaderPager the pager containing pagination and search criteria
     * @return a list of request headers matching the specified criteria
     */
    public List<RequestHeader> getRequestHeaderList(final ReqHeaderPager requestHeaderPager) {

        final PagingResultBean<RequestHeader> requestHeaderList = requestHeaderBhv.selectPage(cb -> {
            cb.paging(requestHeaderPager.getPageSize(), requestHeaderPager.getCurrentPageNumber());
            setupListCondition(cb, requestHeaderPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(requestHeaderList, requestHeaderPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        requestHeaderPager.setPageNumberList(requestHeaderList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return requestHeaderList;
    }

    /**
     * Retrieves a specific request header by its ID.
     *
     * @param id the unique identifier of the request header
     * @return an OptionalEntity containing the request header if found, empty otherwise
     */
    public OptionalEntity<RequestHeader> getRequestHeader(final String id) {
        return requestHeaderBhv.selectByPK(id);
    }

    /**
     * Stores a request header configuration to the database.
     * This method performs either insert or update based on whether the request header already exists.
     *
     * @param requestHeader the request header configuration to store
     */
    public void store(final RequestHeader requestHeader) {

        requestHeaderBhv.insertOrUpdate(requestHeader, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a request header configuration from the database.
     *
     * @param requestHeader the request header configuration to delete
     */
    public void delete(final RequestHeader requestHeader) {

        requestHeaderBhv.delete(requestHeader, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the database query conditions for retrieving request headers based on pager criteria.
     *
     * @param cb the condition bean for building the database query
     * @param requestHeaderPager the pager containing search and filter criteria
     */
    protected void setupListCondition(final RequestHeaderCB cb, final ReqHeaderPager requestHeaderPager) {
        if (requestHeaderPager.id != null) {
            cb.query().docMeta().setId_Equal(requestHeaderPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    /**
     * Retrieves all request headers associated with a specific web configuration.
     *
     * @param webConfigId the unique identifier of the web configuration
     * @return a list of request headers associated with the specified web configuration
     */
    public List<RequestHeader> getRequestHeaderList(final String webConfigId) {
        return requestHeaderBhv.selectList(cb -> {
            cb.query().setWebConfigId_Equal(webConfigId);
            cb.fetchFirst(fessConfig.getPageRequestHeaderMaxFetchSizeAsInteger());
        });
    }

}
