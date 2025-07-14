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
import org.codelibs.fess.app.pager.GroupPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.cbean.GroupCB;
import org.codelibs.fess.opensearch.user.exbhv.GroupBhv;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing group operations in the Fess application.
 * Provides CRUD operations for groups, including integration with LDAP manager
 * and user-group relationships. Handles group pagination, searching, and
 * maintaining data consistency between groups and associated users.
 */
public class GroupService {

    /** Behavior class for group database operations */
    @Resource
    protected GroupBhv groupBhv;

    /** Configuration settings for the Fess application */
    @Resource
    protected FessConfig fessConfig;

    /** Behavior class for user database operations */
    @Resource
    protected UserBhv userBhv;

    /**
     * Retrieves a paginated list of groups based on the provided pager criteria.
     * Updates the pager with pagination information including page numbers and ranges.
     *
     * @param groupPager the pager containing pagination and search criteria
     * @return a list of groups matching the criteria
     */
    public List<Group> getGroupList(final GroupPager groupPager) {

        final PagingResultBean<Group> groupList = groupBhv.selectPage(cb -> {
            cb.paging(groupPager.getPageSize(), groupPager.getCurrentPageNumber());
            setupListCondition(cb, groupPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(groupList, groupPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        groupPager.setPageNumberList(groupList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return groupList;
    }

    /**
     * Retrieves a specific group by its ID and applies LDAP manager settings.
     *
     * @param id the unique identifier of the group
     * @return an OptionalEntity containing the group if found, empty otherwise
     */
    public OptionalEntity<Group> getGroup(final String id) {
        return groupBhv.selectByPK(id).map(g -> {
            ComponentUtil.getLdapManager().apply(g);
            return g;
        });
    }

    /**
     * Stores a group by inserting or updating it in both LDAP and the database.
     * Uses refresh policy to ensure immediate availability of the stored data.
     *
     * @param group the group entity to store
     */
    public void store(final Group group) {
        ComponentUtil.getLdapManager().insert(group);

        groupBhv.insertOrUpdate(group, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a group from both LDAP and the database, and removes the group
     * association from all users that belong to this group.
     *
     * @param group the group entity to delete
     */
    public void delete(final Group group) {
        ComponentUtil.getLdapManager().delete(group);

        groupBhv.delete(group, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

        userBhv.selectCursor(cb -> cb.query().setGroups_Equal(group.getId()), entity -> {
            entity.setGroups(
                    stream(entity.getGroups()).get(stream -> stream.filter(s -> !s.equals(group.getId())).toArray(n -> new String[n])));
            userBhv.insertOrUpdate(entity);
        });

    }

    /**
     * Sets up the search conditions for group list queries based on pager criteria.
     * Configures the condition bean with ID filtering and ordering by name.
     *
     * @param cb the condition bean for building the query
     * @param groupPager the pager containing search and filter criteria
     */
    protected void setupListCondition(final GroupCB cb, final GroupPager groupPager) {
        if (groupPager.id != null) {
            cb.query().docMeta().setId_Equal(groupPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    /**
     * Retrieves all available groups ordered by name in ascending order.
     * Limited by the configured maximum fetch size for groups.
     *
     * @return a list of all available groups
     */
    public List<Group> getAvailableGroupList() {
        return groupBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageGroupMaxFetchSizeAsInteger(), 1);
        });
    }

}
