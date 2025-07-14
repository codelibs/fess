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
import org.codelibs.fess.app.pager.FileAuthPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.FileAuthenticationCB;
import org.codelibs.fess.opensearch.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exentity.FileAuthentication;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing file authentication configurations.
 * This service provides operations for retrieving, storing, and deleting
 * file authentication settings used by the Fess search engine.
 */
public class FileAuthenticationService {

    /**
     * Behavior class for file authentication database operations.
     */
    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    /**
     * Configuration settings for the Fess application.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of file authentication configurations.
     *
     * @param fileAuthenticationPager the pager containing pagination settings and search criteria
     * @return a list of file authentication configurations for the current page
     */
    public List<FileAuthentication> getFileAuthenticationList(final FileAuthPager fileAuthenticationPager) {

        final PagingResultBean<FileAuthentication> fileAuthenticationList = fileAuthenticationBhv.selectPage(cb -> {
            cb.paging(fileAuthenticationPager.getPageSize(), fileAuthenticationPager.getCurrentPageNumber());
            setupListCondition(cb, fileAuthenticationPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileAuthenticationList, fileAuthenticationPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileAuthenticationPager.setPageNumberList(fileAuthenticationList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return fileAuthenticationList;
    }

    /**
     * Retrieves a specific file authentication configuration by its ID.
     *
     * @param id the unique identifier of the file authentication configuration
     * @return an OptionalEntity containing the file authentication if found, empty otherwise
     */
    public OptionalEntity<FileAuthentication> getFileAuthentication(final String id) {
        return fileAuthenticationBhv.selectByPK(id);
    }

    /**
     * Stores a file authentication configuration.
     * The parameters are encrypted before storage for security.
     *
     * @param fileAuthentication the file authentication configuration to store
     */
    public void store(final FileAuthentication fileAuthentication) {
        fileAuthentication.setParameters(ParameterUtil.encrypt(fileAuthentication.getParameters()));
        fileAuthenticationBhv.insertOrUpdate(fileAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a file authentication configuration from the system.
     *
     * @param fileAuthentication the file authentication configuration to delete
     */
    public void delete(final FileAuthentication fileAuthentication) {

        fileAuthenticationBhv.delete(fileAuthentication, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the search conditions for retrieving file authentication configurations.
     * This method configures the query conditions and ordering for the database query.
     *
     * @param cb the condition bean for building the query
     * @param fileAuthenticationPager the pager containing search criteria
     */
    protected void setupListCondition(final FileAuthenticationCB cb, final FileAuthPager fileAuthenticationPager) {
        if (fileAuthenticationPager.id != null) {
            cb.query().docMeta().setId_Equal(fileAuthenticationPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Hostname_Asc();

        // search

    }

    /**
     * Retrieves all file authentication configurations associated with a specific file configuration.
     *
     * @param fileConfigId the ID of the file configuration to retrieve authentications for
     * @return a list of file authentication configurations for the specified file configuration
     */
    public List<FileAuthentication> getFileAuthenticationList(final String fileConfigId) {
        return fileAuthenticationBhv.selectList(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
            cb.fetchFirst(fessConfig.getPageFileAuthMaxFetchSizeAsInteger());
        });
    }
}
