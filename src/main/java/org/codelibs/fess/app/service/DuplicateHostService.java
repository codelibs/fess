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
import org.codelibs.fess.app.pager.DuplicateHostPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.DuplicateHostCB;
import org.codelibs.fess.opensearch.config.exbhv.DuplicateHostBhv;
import org.codelibs.fess.opensearch.config.exentity.DuplicateHost;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing duplicate host configuration CRUD operations.
 * This service provides functionality to create, read, update, and delete
 * duplicate host configurations used by the Fess crawler system.
 *
 * <p>Duplicate host configurations allow administrators to define hostname patterns
 * that should be treated as equivalent during crawling. This helps avoid indexing
 * duplicate content from the same logical site that may be accessible via different
 * hostnames (e.g., www.example.com and example.com).</p>
 */
public class DuplicateHostService extends FessAppService {

    /**
     * DBFlute behavior for duplicate host operations.
     * Provides database access methods for DuplicateHost entities.
     */
    @Resource
    protected DuplicateHostBhv duplicateHostBhv;

    /**
     * Fess configuration containing application settings.
     * Used to retrieve paging and other configuration parameters.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Creates a new instance of DuplicateHostService.
     * This constructor initializes the service for managing duplicate host configuration operations
     * including CRUD operations and search functionality.
     */
    public DuplicateHostService() {
        super();
    }

    /**
     * Retrieves a paginated list of duplicate host configurations based on search criteria.
     *
     * <p>This method performs a paginated search through all duplicate host configurations,
     * applying any search filters specified in the pager. The results are sorted
     * by sort order, creation time, regular name, and duplicate hostname.</p>
     *
     * @param duplicateHostPager the pager containing search criteria and pagination settings
     * @return a list of DuplicateHost entities matching the search criteria
     * @throws IllegalArgumentException if duplicateHostPager is null
     */
    public List<DuplicateHost> getDuplicateHostList(final DuplicateHostPager duplicateHostPager) {

        final PagingResultBean<DuplicateHost> duplicateHostList = duplicateHostBhv.selectPage(cb -> {
            cb.paging(duplicateHostPager.getPageSize(), duplicateHostPager.getCurrentPageNumber());
            setupListCondition(cb, duplicateHostPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(duplicateHostList, duplicateHostPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        duplicateHostPager.setPageNumberList(duplicateHostList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return duplicateHostList;
    }

    /**
     * Retrieves a duplicate host configuration by its unique identifier.
     *
     * @param id the unique identifier of the duplicate host configuration
     * @return an OptionalEntity containing the DuplicateHost if found, empty otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    public OptionalEntity<DuplicateHost> getDuplicateHost(final String id) {
        return duplicateHostBhv.selectByPK(id);
    }

    /**
     * Stores (inserts or updates) a duplicate host configuration.
     *
     * <p>This method immediately refreshes the index to ensure the change is visible.
     * If the configuration already exists (based on ID), it will be updated;
     * otherwise, a new configuration will be created.</p>
     *
     * @param duplicateHost the duplicate host configuration to store
     * @throws IllegalArgumentException if duplicateHost is null
     */
    public void store(final DuplicateHost duplicateHost) {

        duplicateHostBhv.insertOrUpdate(duplicateHost, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes the specified duplicate host configuration from the system.
     *
     * <p>This operation permanently removes the duplicate host configuration and
     * immediately refreshes the index to ensure the change is visible.</p>
     *
     * @param duplicateHost the duplicate host configuration to delete
     * @throws IllegalArgumentException if duplicateHost is null
     * @throws org.dbflute.exception.EntityAlreadyDeletedException if the entity has already been deleted
     */
    public void delete(final DuplicateHost duplicateHost) {

        duplicateHostBhv.delete(duplicateHost, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Retrieves all duplicate host configurations without pagination.
     *
     * <p>This method returns all duplicate host configurations in the system,
     * ordered by sort order, regular name, and duplicate hostname. The results
     * are limited by the configured maximum fetch size to prevent memory issues.</p>
     *
     * @return a list of all DuplicateHost entities
     */
    public List<DuplicateHost> getDuplicateHostList() {

        return duplicateHostBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_RegularName_Asc();
            cb.query().addOrderBy_DuplicateHostName_Asc();
            cb.fetchFirst(fessConfig.getPageDuplicateHostMaxFetchSizeAsInteger());
        });
    }

    /**
     * Sets up the search conditions for listing duplicate host configurations.
     *
     * <p>This method configures the condition bean with search criteria from the pager,
     * including regular name wildcards and duplicate hostname wildcards.
     * Results are ordered by sort order and creation time in ascending order.</p>
     *
     * @param cb the condition bean to configure
     * @param duplicateHostPager the pager containing search criteria
     */
    protected void setupListCondition(final DuplicateHostCB cb, final DuplicateHostPager duplicateHostPager) {
        if (StringUtil.isNotBlank(duplicateHostPager.regularName)) {
            cb.query().setRegularName_Wildcard(wrapQuery(duplicateHostPager.regularName));
        }
        if (StringUtil.isNotBlank(duplicateHostPager.duplicateHostName)) {
            cb.query().setDuplicateHostName_Wildcard(wrapQuery(duplicateHostPager.duplicateHostName));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}
