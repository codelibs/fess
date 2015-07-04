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

package org.codelibs.fess.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.PathMappingCB;
import org.codelibs.fess.es.exbhv.PathMappingBhv;
import org.codelibs.fess.es.exentity.PathMapping;
import org.codelibs.fess.pager.PathMappingPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

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
        Beans.copy(pathMappingList, pathMappingPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        pathMappingPager.setPageNumberList(pathMappingList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return pathMappingList;
    }

    public PathMapping getPathMapping(final Map<String, String> keys) {
        final PathMapping pathMapping = pathMappingBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            cb.request().setVersion(true);
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (pathMapping == null) {
            // TODO exception?
            return null;
        }

        return pathMapping;
    }

    public void store(final PathMapping pathMapping) throws CrudMessageException {
        setupStoreCondition(pathMapping);

        pathMappingBhv.insertOrUpdate(pathMapping, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final PathMapping pathMapping) throws CrudMessageException {
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
