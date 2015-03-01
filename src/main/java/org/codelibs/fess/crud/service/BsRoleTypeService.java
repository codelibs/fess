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

package org.codelibs.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.RoleTypeCB;
import org.codelibs.fess.db.exbhv.RoleTypeBhv;
import org.codelibs.fess.db.exentity.RoleType;
import org.codelibs.fess.pager.RoleTypePager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsRoleTypeService {

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    public BsRoleTypeService() {
        super();
    }

    public List<RoleType> getRoleTypeList(final RoleTypePager roleTypePager) {

        final PagingResultBean<RoleType> roleTypeList = roleTypeBhv
                .selectPage(cb -> {
                    cb.paging(roleTypePager.getPageSize(),
                            roleTypePager.getCurrentPageNumber());
                    setupListCondition(cb, roleTypePager);
                });

        // update pager
        Beans.copy(roleTypeList, roleTypePager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        roleTypePager.setPageNumberList(roleTypeList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return roleTypeList;
    }

    public RoleType getRoleType(final Map<String, String> keys) {
        final RoleType roleType = roleTypeBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (roleType == null) {
            // TODO exception?
            return null;
        }

        return roleType;
    }

    public void store(final RoleType roleType) throws CrudMessageException {
        setupStoreCondition(roleType);

        roleTypeBhv.insertOrUpdate(roleType);

    }

    public void delete(final RoleType roleType) throws CrudMessageException {
        setupDeleteCondition(roleType);

        roleTypeBhv.delete(roleType);

    }

    protected void setupListCondition(final RoleTypeCB cb,
            final RoleTypePager roleTypePager) {

        if (roleTypePager.id != null) {
            cb.query().setId_Equal(Long.parseLong(roleTypePager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final RoleTypeCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final RoleType roleType) {
    }

    protected void setupDeleteCondition(final RoleType roleType) {
    }
}