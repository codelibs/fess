/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.DuplicateHostPager;
import org.codelibs.fess.es.config.cbean.DuplicateHostCB;
import org.codelibs.fess.es.config.exbhv.DuplicateHostBhv;
import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class DuplicateHostService extends FessAppService {

    @Resource
    protected DuplicateHostBhv duplicateHostBhv;

    @Resource
    protected FessConfig fessConfig;

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

    public OptionalEntity<DuplicateHost> getDuplicateHost(final String id) {
        return duplicateHostBhv.selectByPK(id);
    }

    public void store(final DuplicateHost duplicateHost) {

        duplicateHostBhv.insertOrUpdate(duplicateHost, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final DuplicateHost duplicateHost) {

        duplicateHostBhv.delete(duplicateHost, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public List<DuplicateHost> getDuplicateHostList() {

        return duplicateHostBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_RegularName_Asc();
            cb.query().addOrderBy_DuplicateHostName_Asc();
            cb.fetchFirst(fessConfig.getPageDuplicateHostMaxFetchSizeAsInteger());
        });
    }

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
