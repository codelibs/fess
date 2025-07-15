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

import java.util.Collection;
import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.PathMapPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.PathMappingCB;
import org.codelibs.fess.opensearch.config.exbhv.PathMappingBhv;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service for path mapping operations.
 */
public class PathMappingService extends FessAppService {

    /**
     * Default constructor.
     */
    public PathMappingService() {
        // Default constructor
    }

    /** Path mapping behavior. */
    @Resource
    protected PathMappingBhv pathMappingBhv;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Gets the path mapping list with paging.
     *
     * @param pathMappingPager the path mapping pager
     * @return the path mapping list
     */
    public List<PathMapping> getPathMappingList(final PathMapPager pathMappingPager) {

        final PagingResultBean<PathMapping> pathMappingList = pathMappingBhv.selectPage(cb -> {
            cb.paging(pathMappingPager.getPageSize(), pathMappingPager.getCurrentPageNumber());
            setupListCondition(cb, pathMappingPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(pathMappingList, pathMappingPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        pathMappingPager.setPageNumberList(pathMappingList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return pathMappingList;
    }

    /**
     * Gets a path mapping by ID.
     *
     * @param id the path mapping ID
     * @return the path mapping
     */
    public OptionalEntity<PathMapping> getPathMapping(final String id) {
        return pathMappingBhv.selectByPK(id);
    }

    /**
     * Stores a path mapping.
     *
     * @param pathMapping the path mapping to store
     */
    public void store(final PathMapping pathMapping) {

        pathMappingBhv.insertOrUpdate(pathMapping, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        ComponentUtil.getPathMappingHelper().init();
    }

    /**
     * Deletes a path mapping.
     *
     * @param pathMapping the path mapping to delete
     */
    public void delete(final PathMapping pathMapping) {

        pathMappingBhv.delete(pathMapping, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        ComponentUtil.getPathMappingHelper().init();
    }

    /**
     * Gets the path mapping list by process types.
     *
     * @param processTypeList the process type list
     * @return the path mapping list
     */
    public List<PathMapping> getPathMappingList(final Collection<String> processTypeList) {

        return pathMappingBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().setProcessType_InScope(processTypeList);
            cb.fetchFirst(fessConfig.getPagePathMappingMaxFetchSizeAsInteger());
        });
    }

    /**
     * Sets up list condition for path mapping search.
     *
     * @param cb the condition bean
     * @param pathMappingPager the path mapping pager
     */
    protected void setupListCondition(final PathMappingCB cb, final PathMapPager pathMappingPager) {
        if (StringUtil.isNotBlank(pathMappingPager.regex)) {
            cb.query().setRegex_Wildcard(wrapQuery(pathMappingPager.regex));
        }
        if (StringUtil.isNotBlank(pathMappingPager.replacement)) {
            cb.query().setReplacement_Wildcard(wrapQuery(pathMappingPager.replacement));
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_CreatedTime_Asc();

        // search

    }

}
