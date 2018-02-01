/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.config.bsentity.BsLabelType;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

/**
 * @author FreeGen
 */
public class LabelType extends BsLabelType {

    private static final long serialVersionUID = 1L;
    private String[] roleTypeIds;

    private volatile List<RoleType> roleTypeList;

    public String[] getRoleTypeIds() {
        if (roleTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return roleTypeIds;
    }

    public void setRoleTypeIds(final String[] roleTypeIds) {
        this.roleTypeIds = roleTypeIds;
    }

    public List<RoleType> getRoleTypeList() {
        if (roleTypeList == null) {
            synchronized (this) {
                if (roleTypeList == null) {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final LabelToRoleBhv labelToRoleBhv = ComponentUtil.getComponent(LabelToRoleBhv.class);
                    final ListResultBean<LabelToRole> mappingList = labelToRoleBhv.selectList(cb -> {
                        cb.query().setLabelTypeId_Equal(getId());
                        cb.specify().columnRoleTypeId();
                        cb.paging(fessConfig.getPageRoletypeMaxFetchSizeAsInteger().intValue(), 1);
                    });
                    final List<String> roleIdList = new ArrayList<>();
                    for (final LabelToRole mapping : mappingList) {
                        roleIdList.add(mapping.getRoleTypeId());
                    }
                    final RoleTypeBhv roleTypeBhv = ComponentUtil.getComponent(RoleTypeBhv.class);
                    roleTypeList = roleIdList.isEmpty() ? Collections.emptyList() : roleTypeBhv.selectList(cb -> {
                        cb.query().setId_InScope(roleIdList);
                        cb.query().addOrderBy_SortOrder_Asc();
                        cb.fetchFirst(fessConfig.getPageRoletypeMaxFetchSizeAsInteger());
                    });
                }
            }
        }
        return roleTypeList;
    }

    public List<String> getRoleValueList() {
        final List<RoleType> list = getRoleTypeList();
        final List<String> roleValueList = new ArrayList<>(list.size());
        for (final RoleType roleType : list) {
            roleValueList.add(roleType.getValue());
        }
        return roleValueList;
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "LabelType [roleTypeIds=" + Arrays.toString(roleTypeIds) + ", roleTypeList=" + roleTypeList + ", createdBy=" + createdBy
                + ", createdTime=" + createdTime + ", excludedPaths=" + excludedPaths + ", includedPaths=" + includedPaths + ", name="
                + name + ", sortOrder=" + sortOrder + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", value=" + value
                + ", docMeta=" + docMeta + "]";
    }
}
