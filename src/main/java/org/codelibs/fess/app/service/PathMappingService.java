/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.PathMappingPager;
import org.codelibs.fess.es.cbean.PathMappingCB;
import org.codelibs.fess.es.exbhv.PathMappingBhv;
import org.codelibs.fess.es.exentity.PathMapping;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class PathMappingService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected PathMappingBhv pathMappingBhv;

    public List<PathMapping> getPathMappingList(final PathMappingPager pathMappingPager) {

        final PagingResultBean<PathMapping> pathMappingList = pathMappingBhv.selectPage(cb -> {
            cb.paging(pathMappingPager.getPageSize(), pathMappingPager.getCurrentPageNumber());
            setupListCondition(cb, pathMappingPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(pathMappingList, pathMappingPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        pathMappingPager.setPageNumberList(pathMappingList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return pathMappingList;
    }

    public OptionalEntity<PathMapping> getPathMapping(final String id) {
        return pathMappingBhv.selectByPK(id);
    }

    public void store(final PathMapping pathMapping) {
        setupStoreCondition(pathMapping);

        pathMappingBhv.insertOrUpdate(pathMapping, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final PathMapping pathMapping) {
        setupDeleteCondition(pathMapping);

        pathMappingBhv.delete(pathMapping, op -> {
            op.setRefresh(true);
        });

    }

    public List<PathMapping> getPathMappingList(final Collection<String> processTypeList) {

        return pathMappingBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().setProcessType_InScope(processTypeList);
        });
    }

    protected void setupListCondition(final PathMappingCB cb, final PathMappingPager pathMappingPager) {
        if (pathMappingPager.id != null) {
            cb.query().docMeta().setId_Equal(pathMappingPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final PathMappingCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final PathMapping pathMapping) {

        // setup condition

    }

    protected void setupDeleteCondition(final PathMapping pathMapping) {

        // setup condition

    }

}
