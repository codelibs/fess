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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RolePager;
import org.codelibs.fess.es.user.cbean.RoleCB;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.es.user.exbhv.UserBhv;
import org.codelibs.fess.es.user.exentity.Role;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class RoleService {

    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected UserBhv userBhv;

    public List<Role> getRoleList(final RolePager rolePager) {

        final PagingResultBean<Role> roleList = roleBhv.selectPage(cb -> {
            cb.paging(rolePager.getPageSize(), rolePager.getCurrentPageNumber());
            setupListCondition(cb, rolePager);
        });

        // update pager
        BeanUtil.copyBeanToBean(roleList, rolePager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        rolePager.setPageNumberList(roleList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return roleList;
    }

    public OptionalEntity<Role> getRole(final String id) {
        return roleBhv.selectByPK(id);
    }

    public void store(final Role role) {
        ComponentUtil.getLdapManager().insert(role);

        roleBhv.insertOrUpdate(role, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final Role role) {
        ComponentUtil.getLdapManager().delete(role);

        roleBhv.delete(role, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        userBhv.selectCursor(cb -> cb.query().setRoles_Equal(role.getId()), entity -> {
            entity.setRoles(
                    stream(entity.getRoles()).get(stream -> stream.filter(s -> !s.equals(role.getId())).toArray(n -> new String[n])));
            userBhv.insertOrUpdate(entity);
        });
    }

    protected void setupListCondition(final RoleCB cb, final RolePager rolePager) {
        if (rolePager.id != null) {
            cb.query().docMeta().setId_Equal(rolePager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    public List<Role> getAvailableRoleList() {
        return roleBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageRoleMaxFetchSizeAsInteger(), 1);
        });
    }

}
