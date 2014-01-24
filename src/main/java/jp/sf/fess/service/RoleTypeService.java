/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jp.sf.fess.crud.service.BsRoleTypeService;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.pager.RoleTypePager;

public class RoleTypeService extends BsRoleTypeService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final RoleTypeCB cb,
            final RoleTypePager roleTypePager) {
        super.setupListCondition(cb, roleTypePager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        // search

    }

    @Override
    protected void setupEntityCondition(final RoleTypeCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final RoleType roleType) {
        super.setupStoreCondition(roleType);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final RoleType roleType) {
        super.setupDeleteCondition(roleType);

        // setup condition

    }

    public List<RoleType> getRoleTypeList() {
        final RoleTypeCB cb = new RoleTypeCB();
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();
        return roleTypeBhv.selectList(cb);
    }

}
