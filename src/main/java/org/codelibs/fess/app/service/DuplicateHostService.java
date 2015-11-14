/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.DuplicateHostPager;
import org.codelibs.fess.es.config.cbean.DuplicateHostCB;
import org.codelibs.fess.es.config.exbhv.DuplicateHostBhv;
import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class DuplicateHostService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected DuplicateHostBhv duplicateHostBhv;

    public DuplicateHostService() {
        super();
    }

    public List<DuplicateHost> getDuplicateHostList(final DuplicateHostPager duplicateHostPager) {

        final PagingResultBean<DuplicateHost> duplicateHostList = duplicateHostBhv.selectPage(cb -> {
            cb.paging(duplicateHostPager.getPageSize(), duplicateHostPager.getCurrentPageNumber());
            setupListCondition(cb, duplicateHostPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(duplicateHostList, duplicateHostPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        duplicateHostPager.setPageNumberList(duplicateHostList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return duplicateHostList;
    }

    public OptionalEntity<DuplicateHost> getDuplicateHost(final String id) {
        return duplicateHostBhv.selectByPK(id);
    }

    public void store(final DuplicateHost duplicateHost) {
        setupStoreCondition(duplicateHost);

        duplicateHostBhv.insertOrUpdate(duplicateHost, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final DuplicateHost duplicateHost) {
        setupDeleteCondition(duplicateHost);

        duplicateHostBhv.delete(duplicateHost, op -> {
            op.setRefresh(true);
        });

    }

    public List<DuplicateHost> getDuplicateHostList() {

        return duplicateHostBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_RegularName_Asc();
            cb.query().addOrderBy_DuplicateHostName_Asc();
        });
    }

    protected void setupListCondition(final DuplicateHostCB cb, final DuplicateHostPager duplicateHostPager) {
        if (duplicateHostPager.id != null) {
            cb.query().docMeta().setId_Equal(duplicateHostPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();

        // search

    }

    protected void setupEntityCondition(final DuplicateHostCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final DuplicateHost duplicateHost) {

        // setup condition

    }

    protected void setupDeleteCondition(final DuplicateHost duplicateHost) {

        // setup condition

    }

}
