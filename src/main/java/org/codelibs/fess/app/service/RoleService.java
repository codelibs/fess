/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RolePager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.cbean.RoleCB;
import org.codelibs.fess.opensearch.user.exbhv.RoleBhv;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.Role;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing roles.
 */
public class RoleService {

    /**
     * The behavior for roles.
     */
    @Resource
    protected RoleBhv roleBhv;

    /**
     * The Fess configuration.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * The behavior for users.
     */
    @Resource
    protected UserBhv userBhv;

    /**
     * Constructor.
     */
    public RoleService() {
        super();
    }

    /**
     * Gets a list of roles based on the pager.
     * @param rolePager The pager for roles.
     * @return A list of roles.
     */
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

    /**
     * Gets a role by its ID.
     * @param id The ID of the role.
     * @return An optional entity of the role.
     */
    public OptionalEntity<Role> getRole(final String id) {
        return roleBhv.selectByPK(id);
    }

    /**
     * Stores a role.
     * @param role The role to store.
     */
    public void store(final Role role) {
        ComponentUtil.getLdapManager().insert(role);

        roleBhv.insertOrUpdate(role, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a role.
     * @param role The role to delete.
     */
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

    /**
     * Sets up the list condition for the role query.
     * @param cb The role condition bean.
     * @param rolePager The role pager.
     */
    protected void setupListCondition(final RoleCB cb, final RolePager rolePager) {
        if (rolePager.id != null) {
            cb.query().docMeta().setId_Equal(rolePager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    /**
     * Gets a list of available roles.
     * @return A list of available roles.
     */
    public List<Role> getAvailableRoleList() {
        return roleBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageRoleMaxFetchSizeAsInteger(), 1);
        });
    }

}
