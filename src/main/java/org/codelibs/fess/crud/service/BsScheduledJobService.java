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
import org.codelibs.fess.db.cbean.ScheduledJobCB;
import org.codelibs.fess.db.exbhv.ScheduledJobBhv;
import org.codelibs.fess.db.exentity.ScheduledJob;
import org.codelibs.fess.pager.ScheduledJobPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsScheduledJobService {

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    public BsScheduledJobService() {
        super();
    }

    public List<ScheduledJob> getScheduledJobList(
            final ScheduledJobPager scheduledJobPager) {

        final PagingResultBean<ScheduledJob> scheduledJobList = scheduledJobBhv
                .selectPage(cb -> {
                    cb.paging(scheduledJobPager.getPageSize(),
                            scheduledJobPager.getCurrentPageNumber());
                    setupListCondition(cb, scheduledJobPager);
                });

        // update pager
        Beans.copy(scheduledJobList, scheduledJobPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        scheduledJobPager.setPageNumberList(scheduledJobList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return scheduledJobList;
    }

    public ScheduledJob getScheduledJob(final Map<String, String> keys) {
        final ScheduledJob scheduledJob = scheduledJobBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (scheduledJob == null) {
            // TODO exception?
            return null;
        }

        return scheduledJob;
    }

    public void store(final ScheduledJob scheduledJob)
            throws CrudMessageException {
        setupStoreCondition(scheduledJob);

        scheduledJobBhv.insertOrUpdate(scheduledJob);

    }

    public void delete(final ScheduledJob scheduledJob)
            throws CrudMessageException {
        setupDeleteCondition(scheduledJob);

        scheduledJobBhv.delete(scheduledJob);

    }

    protected void setupListCondition(final ScheduledJobCB cb,
            final ScheduledJobPager scheduledJobPager) {

        if (scheduledJobPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(scheduledJobPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final ScheduledJobCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final ScheduledJob scheduledJob) {
    }

    protected void setupDeleteCondition(final ScheduledJob scheduledJob) {
    }
}