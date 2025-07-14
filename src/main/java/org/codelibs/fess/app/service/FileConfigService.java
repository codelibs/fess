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
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.FileConfigCB;
import org.codelibs.fess.opensearch.config.exbhv.FileAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing file configuration operations.
 * This service provides CRUD operations for file crawler configurations,
 * including retrieval, storage, deletion, and search functionality.
 * It handles pagination and integrates with the file authentication system.
 */
public class FileConfigService extends FessAppService {

    /**
     * Behavior class for file configuration database operations.
     * Provides access to the file configuration entity operations.
     */
    @Resource
    protected FileConfigBhv fileConfigBhv;

    /**
     * Behavior class for file authentication database operations.
     * Manages authentication configurations associated with file configurations.
     */
    @Resource
    protected FileAuthenticationBhv fileAuthenticationBhv;

    /**
     * Fess configuration object providing access to application settings.
     * Used for retrieving pagination and other configuration parameters.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of file configurations based on the provided pager criteria.
     * This method applies search conditions from the pager and updates the pager with
     * pagination information including page numbers and result counts.
     *
     * @param fileConfigPager the pager containing search criteria and pagination settings
     * @return a list of file configurations matching the criteria
     */
    public List<FileConfig> getFileConfigList(final FileConfigPager fileConfigPager) {

        final PagingResultBean<FileConfig> fileConfigList = fileConfigBhv.selectPage(cb -> {
            cb.paging(fileConfigPager.getPageSize(), fileConfigPager.getCurrentPageNumber());
            setupListCondition(cb, fileConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(fileConfigList, fileConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        fileConfigPager.setPageNumberList(fileConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return fileConfigList;
    }

    /**
     * Deletes a file configuration and its associated authentication records.
     * This method removes the file configuration from the database and also
     * deletes all related file authentication entries.
     *
     * @param fileConfig the file configuration to be deleted
     */
    public void delete(final FileConfig fileConfig) {

        final String fileConfigId = fileConfig.getId();

        fileConfigBhv.delete(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        fileAuthenticationBhv.queryDelete(cb -> {
            cb.query().setFileConfigId_Equal(fileConfigId);
        });
    }

    /**
     * Retrieves a file configuration by its unique identifier.
     *
     * @param id the unique identifier of the file configuration
     * @return an OptionalEntity containing the file configuration if found, empty otherwise
     */
    public OptionalEntity<FileConfig> getFileConfig(final String id) {
        return fileConfigBhv.selectByPK(id);
    }

    /**
     * Retrieves a file configuration by its name.
     * If multiple configurations exist with the same name, returns the first one
     * ordered by sort order.
     *
     * @param name the name of the file configuration to retrieve
     * @return an OptionalEntity containing the file configuration if found, empty otherwise
     */
    public OptionalEntity<FileConfig> getFileConfigByName(final String name) {
        final ListResultBean<FileConfig> list = fileConfigBhv.selectList(cb -> {
            cb.query().setName_Equal(name);
            cb.query().addOrderBy_SortOrder_Asc();
        });
        if (list.isEmpty()) {
            return OptionalEntity.empty();
        }
        return OptionalEntity.of(list.get(0));
    }

    /**
     * Stores a file configuration in the database.
     * This method encrypts the configuration parameters before saving and
     * performs an insert or update operation based on whether the configuration exists.
     *
     * @param fileConfig the file configuration to be stored
     */
    public void store(final FileConfig fileConfig) {
        fileConfig.setConfigParameter(ParameterUtil.encrypt(fileConfig.getConfigParameter()));
        fileConfigBhv.insertOrUpdate(fileConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    /**
     * Sets up search conditions for the file configuration list query.
     * This method applies various filter conditions based on the pager parameters
     * including name, paths, and description filters with wildcard and phrase matching.
     *
     * @param cb the condition bean for building the database query
     * @param fileConfigPager the pager containing search filter criteria
     */
    protected void setupListCondition(final FileConfigCB cb, final FileConfigPager fileConfigPager) {
        if (StringUtil.isNotBlank(fileConfigPager.name)) {
            cb.query().setName_Wildcard(wrapQuery(fileConfigPager.name));
        }
        if (StringUtil.isNotBlank(fileConfigPager.paths)) {
            cb.query().setPaths_Wildcard(wrapQuery(fileConfigPager.paths));
        }
        if (StringUtil.isNotBlank(fileConfigPager.description)) {
            if (fileConfigPager.description.startsWith("*")) {
                cb.query().setDescription_Wildcard(fileConfigPager.description);
            } else if (fileConfigPager.description.endsWith("*")) {
                cb.query().setDescription_Prefix(fileConfigPager.description.replaceAll("\\*$", StringUtil.EMPTY));
            } else {
                cb.query().setDescription_MatchPhrase(fileConfigPager.description);
            }
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
