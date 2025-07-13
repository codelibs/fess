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
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.DataConfigCB;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing data configuration CRUD operations.
 * This service provides functionality to create, read, update, and delete
 * data configurations used by the Fess crawler system.
 *
 * <p>Data configurations define how the crawler should access and process
 * various data sources such as databases, CSV files, or other structured data.</p>
 */
public class DataConfigService extends FessAppService {

    /**
     * DBFlute behavior for data configuration operations.
     * Provides database access methods for DataConfig entities.
     */
    @Resource
    protected DataConfigBhv dataConfigBhv;

    /**
     * Fess configuration containing application settings.
     * Used to retrieve paging and other configuration parameters.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Retrieves a paginated list of data configurations based on search criteria.
     *
     * <p>This method performs a paginated search through all data configurations,
     * applying any search filters specified in the pager. The results are sorted
     * by sort order and name.</p>
     *
     * @param dataConfigPager the pager containing search criteria and pagination settings
     * @return a list of DataConfig entities matching the search criteria
     * @throws IllegalArgumentException if dataConfigPager is null
     */
    public List<DataConfig> getDataConfigList(final DataConfigPager dataConfigPager) {

        final PagingResultBean<DataConfig> dataConfigList = dataConfigBhv.selectPage(cb -> {
            cb.paging(dataConfigPager.getPageSize(), dataConfigPager.getCurrentPageNumber());

            setupListCondition(cb, dataConfigPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(dataConfigList, dataConfigPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        dataConfigPager.setPageNumberList(dataConfigList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return dataConfigList;
    }

    /**
     * Deletes the specified data configuration from the system.
     *
     * <p>This operation permanently removes the data configuration and
     * immediately refreshes the index to ensure the change is visible.</p>
     *
     * @param dataConfig the data configuration to delete
     * @throws IllegalArgumentException if dataConfig is null
     * @throws org.dbflute.exception.EntityAlreadyDeletedException if the entity has already been deleted
     */
    public void delete(final DataConfig dataConfig) {
        dataConfigBhv.delete(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    /**
     * Retrieves a data configuration by its unique identifier.
     *
     * @param id the unique identifier of the data configuration
     * @return an OptionalEntity containing the DataConfig if found, empty otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    public OptionalEntity<DataConfig> getDataConfig(final String id) {
        return dataConfigBhv.selectByPK(id);
    }

    /**
     * Retrieves a data configuration by its name.
     *
     * <p>If multiple configurations exist with the same name, returns the first one
     * ordered by sort order ascending.</p>
     *
     * @param name the name of the data configuration to retrieve
     * @return an OptionalEntity containing the DataConfig if found, empty otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    public OptionalEntity<DataConfig> getDataConfigByName(final String name) {
        final ListResultBean<DataConfig> list = dataConfigBhv.selectList(cb -> {
            cb.query().setName_Equal(name);
            cb.query().addOrderBy_SortOrder_Asc();
        });
        if (list.isEmpty()) {
            return OptionalEntity.empty();
        }
        return OptionalEntity.of(list.get(0));
    }

    /**
     * Stores (inserts or updates) a data configuration.
     *
     * <p>This method encrypts sensitive handler parameters before storing
     * and immediately refreshes the index to ensure the change is visible.
     * If the configuration already exists (based on ID), it will be updated;
     * otherwise, a new configuration will be created.</p>
     *
     * @param dataConfig the data configuration to store
     * @throws IllegalArgumentException if dataConfig is null
     */
    public void store(final DataConfig dataConfig) {
        dataConfig.setHandlerParameter(ParameterUtil.encrypt(dataConfig.getHandlerParameter()));
        dataConfigBhv.insertOrUpdate(dataConfig, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the search conditions for listing data configurations.
     *
     * <p>This method configures the condition bean with search criteria from the pager,
     * including name wildcards, handler name wildcards, and description matching.
     * Results are ordered by sort order and name in ascending order.</p>
     *
     * <p>Description matching supports:</p>
     * <ul>
     *   <li>Wildcard matching (if starts or ends with *)</li>
     *   <li>Prefix matching (if ends with *)</li>
     *   <li>Exact phrase matching (otherwise)</li>
     * </ul>
     *
     * @param cb the condition bean to configure
     * @param dataConfigPager the pager containing search criteria
     */
    protected void setupListCondition(final DataConfigCB cb, final DataConfigPager dataConfigPager) {
        if (StringUtil.isNotBlank(dataConfigPager.name)) {
            cb.query().setName_Wildcard(dataConfigPager.name);
        }
        if (StringUtil.isNotBlank(dataConfigPager.handlerName)) {
            cb.query().setHandlerName_Wildcard(wrapQuery(dataConfigPager.handlerName));
        }
        if (StringUtil.isNotBlank(dataConfigPager.description)) {
            if (dataConfigPager.description.startsWith("*")) {
                cb.query().setDescription_Wildcard(dataConfigPager.description);
            } else if (dataConfigPager.description.endsWith("*")) {
                cb.query().setDescription_Prefix(dataConfigPager.description.replaceAll("\\*$", StringUtil.EMPTY));
            } else {
                cb.query().setDescription_MatchPhrase(dataConfigPager.description);
            }
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

}
