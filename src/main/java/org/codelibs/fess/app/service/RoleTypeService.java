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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RoleTypePager;
import org.codelibs.fess.es.cbean.RoleTypeCB;
import org.codelibs.fess.es.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.exentity.RoleType;
import org.dbflute.cbean.result.PagingResultBean;

public class RoleTypeService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    public RoleTypeService() {
        super();
    }

    public List<RoleType> getRoleTypeList(final RoleTypePager roleTypePager) {

        final PagingResultBean<RoleType> roleTypeList = roleTypeBhv.selectPage(cb -> {
            cb.paging(roleTypePager.getPageSize(), roleTypePager.getCurrentPageNumber());
            setupListCondition(cb, roleTypePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(roleTypeList, roleTypePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        roleTypePager.setPageNumberList(roleTypeList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return roleTypeList;
    }

    public RoleType getRoleType(final Map<String, String> keys) {
        final RoleType roleType = roleTypeBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (roleType == null) {
            // TODO exception?
            return null;
        }

        return roleType;
    }

    public void store(final RoleType roleType) {
        setupStoreCondition(roleType);

        roleTypeBhv.insertOrUpdate(roleType, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final RoleType roleType) {
        setupDeleteCondition(roleType);

        roleTypeBhv.delete(roleType, op -> {
            op.setRefresh(true);
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

    protected void setupEntityCondition(final RoleTypeCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final RoleType roleType) {

        // setup condition

    }

    protected void setupDeleteCondition(final RoleType roleType) {

        // setup condition

    }

    public List<RoleType> getRoleTypeList() {
        return roleTypeBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

}
