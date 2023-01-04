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
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RoleTypePager;
import org.codelibs.fess.es.config.cbean.RoleTypeCB;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exentity.RoleType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class RoleTypeService {

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<RoleType> getRoleTypeList(final RoleTypePager roleTypePager) {

        final PagingResultBean<RoleType> roleTypeList = roleTypeBhv.selectPage(cb -> {
            cb.paging(roleTypePager.getPageSize(), roleTypePager.getCurrentPageNumber());
            setupListCondition(cb, roleTypePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(roleTypeList, roleTypePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        roleTypePager.setPageNumberList(roleTypeList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return roleTypeList;
    }

    public OptionalEntity<RoleType> getRoleType(final String id) {
        return roleTypeBhv.selectByPK(id);
    }

    public void store(final RoleType roleType) {

        roleTypeBhv.insertOrUpdate(roleType, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final RoleType roleType) {

        roleTypeBhv.delete(roleType, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final RoleTypeCB cb, final RoleTypePager roleTypePager) {
        if (roleTypePager.id != null) {
            cb.query().docMeta().setId_Equal(roleTypePager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        // search

    }

    public List<RoleType> getRoleTypeList() {
        return roleTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageRoletypeMaxFetchSizeAsInteger(), 1);
        });
    }

}
