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
import org.codelibs.fess.app.pager.GroupPager;
import org.codelibs.fess.es.user.cbean.GroupCB;
import org.codelibs.fess.es.user.exbhv.GroupBhv;
import org.codelibs.fess.es.user.exbhv.UserBhv;
import org.codelibs.fess.es.user.exentity.Group;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class GroupService {

    @Resource
    protected GroupBhv groupBhv;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected UserBhv userBhv;

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

    public OptionalEntity<Group> getGroup(final String id) {
        return groupBhv.selectByPK(id).map(g -> {
            ComponentUtil.getLdapManager().apply(g);
            return g;
        });
    }

    public void store(final Group group) {
        ComponentUtil.getLdapManager().insert(group);

        groupBhv.insertOrUpdate(group, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

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

    protected void setupListCondition(final GroupCB cb, final GroupPager groupPager) {
        if (groupPager.id != null) {
            cb.query().docMeta().setId_Equal(groupPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    public List<Group> getAvailableGroupList() {
        return groupBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Name_Asc();
            cb.paging(fessConfig.getPageGroupMaxFetchSizeAsInteger(), 1);
        });
    }

}
