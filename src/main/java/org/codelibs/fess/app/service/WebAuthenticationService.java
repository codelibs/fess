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
import org.codelibs.fess.app.pager.WebAuthPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.WebAuthenticationCB;
import org.codelibs.fess.opensearch.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing web authentication configurations.
 * Provides CRUD operations for web authentication settings including
 * listing, retrieving, storing, and deleting authentication configurations.
 */
public class WebAuthenticationService {

    /**
     * Default constructor.
     */
    public WebAuthenticationService() {
        // Default constructor
    }

    /**
     * Behavior class for web authentication operations.
     */
    @Resource
    protected WebAuthenticationBhv webAuthenticationBhv;

    /**
     * Fess configuration settings.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Gets a paginated list of web authentications based on the provided pager.
     *
     * @param webAuthenticationPager The pager containing pagination and search criteria
     * @return List of web authentication configurations
     */
    public List<WebAuthentication> getWebAuthenticationList(final WebAuthPager webAuthenticationPager) {

        final PagingResultBean<WebAuthentication> webAuthenticationList = webAuthenticationBhv.selectPage(cb -> {
            cb.paging(webAuthenticationPager.getPageSize(), webAuthenticationPager.getCurrentPageNumber());
            setupListCondition(cb, webAuthenticationPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(webAuthenticationList, webAuthenticationPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        webAuthenticationPager.setPageNumberList(webAuthenticationList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return webAuthenticationList;
    }

    /**
     * Gets a web authentication configuration by its ID.
     *
     * @param id The ID of the web authentication configuration
     * @return Optional containing the web authentication if found
     */
    public OptionalEntity<WebAuthentication> getWebAuthentication(final String id) {
        return webAuthenticationBhv.selectByPK(id);
    }

    /**
     * Stores a web authentication configuration.
     * Parameters are encrypted before storage.
     *
     * @param webAuthentication The web authentication configuration to store
     */
    public void store(final WebAuthentication webAuthentication) {
        webAuthentication.setParameters(ParameterUtil.encrypt(webAuthentication.getParameters()));
        webAuthenticationBhv.insertOrUpdate(webAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a web authentication configuration.
     *
     * @param webAuthentication The web authentication configuration to delete
     */
    public void delete(final WebAuthentication webAuthentication) {

        webAuthenticationBhv.delete(webAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the list condition for querying web authentications.
     *
     * @param cb The condition bean for the query
     * @param webAuthenticationPager The pager containing search criteria
     */
    protected void setupListCondition(final WebAuthenticationCB cb, final WebAuthPager webAuthenticationPager) {
        if (webAuthenticationPager.id != null) {
            cb.query().docMeta().setId_Equal(webAuthenticationPager.id);
        }
        // TODO Long, Integer, String supported only.
        // setup condition
        cb.query().addOrderBy_Hostname_Asc();
        cb.query().addOrderBy_WebConfigId_Asc();

        // search

    }

    /**
     * Gets a list of web authentications for a specific web configuration.
     *
     * @param webConfigId The ID of the web configuration
     * @return List of web authentication configurations for the specified web config
     */
    public List<WebAuthentication> getWebAuthenticationList(final String webConfigId) {
        return webAuthenticationBhv.selectList(cb -> {
            cb.query().setWebConfigId_Equal(webConfigId);
            cb.fetchFirst(fessConfig.getPageWebAuthMaxFetchSizeAsInteger());
        });
    }

}
