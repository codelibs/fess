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

import java.beans.Beans;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.OverlappingHostCB;
import org.codelibs.fess.es.exbhv.OverlappingHostBhv;
import org.codelibs.fess.es.exentity.OverlappingHost;
import org.codelibs.fess.pager.OverlappingHostPager;
import org.dbflute.cbean.result.PagingResultBean;

public class OverlappingHostService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected OverlappingHostBhv overlappingHostBhv;

    public OverlappingHostService() {
        super();
    }

    public List<OverlappingHost> getOverlappingHostList(final OverlappingHostPager overlappingHostPager) {

        final PagingResultBean<OverlappingHost> overlappingHostList = overlappingHostBhv.selectPage(cb -> {
            cb.paging(overlappingHostPager.getPageSize(), overlappingHostPager.getCurrentPageNumber());
            setupListCondition(cb, overlappingHostPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(overlappingHostList, overlappingHostPager, option -> option.include(CommonConstants.PAGER_CONVERSION_RULE));
        overlappingHostPager.setPageNumberList(overlappingHostList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return overlappingHostList;
    }

    public OverlappingHost getOverlappingHost(final Map<String, String> keys) {
        final OverlappingHost overlappingHost = overlappingHostBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (overlappingHost == null) {
            // TODO exception?
            return null;
        }

        return overlappingHost;
    }

    public void store(final OverlappingHost overlappingHost) throws CrudMessageException {
        setupStoreCondition(overlappingHost);

        overlappingHostBhv.insertOrUpdate(overlappingHost, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final OverlappingHost overlappingHost) throws CrudMessageException {
        setupDeleteCondition(overlappingHost);

        overlappingHostBhv.delete(overlappingHost, op -> {
            op.setRefresh(true);
        });

    }

    public List<OverlappingHost> getOverlappingHostList() {

        return overlappingHostBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_RegularName_Asc();
            cb.query().addOrderBy_OverlappingName_Asc();
        });
    }

    protected void setupListCondition(final OverlappingHostCB cb, final OverlappingHostPager overlappingHostPager) {
        if (overlappingHostPager.id != null) {
            cb.query().docMeta().setId_Equal(overlappingHostPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final OverlappingHostCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final OverlappingHost overlappingHost) {

        // setup condition

    }

    protected void setupDeleteCondition(final OverlappingHost overlappingHost) {

        // setup condition

    }

}
