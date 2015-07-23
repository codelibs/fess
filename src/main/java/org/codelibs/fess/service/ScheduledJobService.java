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

package org.codelibs.fess.service;

import java.beans.Beans;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.es.cbean.ScheduledJobCB;
import org.codelibs.fess.es.exbhv.ScheduledJobBhv;
import org.codelibs.fess.es.exentity.ScheduledJob;
import org.codelibs.fess.job.JobScheduler;
import org.codelibs.fess.pager.ScheduledJobPager;
import org.dbflute.cbean.result.PagingResultBean;

public class ScheduledJobService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    public ScheduledJobService() {
        super();
    }

    public List<ScheduledJob> getScheduledJobList(final ScheduledJobPager scheduledJobPager) {

        final PagingResultBean<ScheduledJob> scheduledJobList = scheduledJobBhv.selectPage(cb -> {
            cb.paging(scheduledJobPager.getPageSize(), scheduledJobPager.getCurrentPageNumber());
            setupListCondition(cb, scheduledJobPager);
        });

        // update pager
        Beans.copy(scheduledJobList, scheduledJobPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        scheduledJobPager.setPageNumberList(scheduledJobList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return scheduledJobList;
    }

    public ScheduledJob getScheduledJob(final Map<String, String> keys) {
        final ScheduledJob scheduledJob = scheduledJobBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (scheduledJob == null) {
            // TODO exception?
            return null;
        }

        return scheduledJob;
    }

    public void delete(final ScheduledJob scheduledJob) throws CrudMessageException {
        setupDeleteCondition(scheduledJob);

        scheduledJobBhv.delete(scheduledJob, op -> {
            op.setRefresh(true);
        });

    }

    @Resource
    protected JobScheduler jobScheduler;

    protected void setupListCondition(final ScheduledJobCB cb, final ScheduledJobPager scheduledJobPager) {
        if (scheduledJobPager.id != null) {
            cb.query().docMeta().setId_Equal(scheduledJobPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    protected void setupEntityCondition(final ScheduledJobCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final ScheduledJob scheduledJob) {

        // setup condition

    }

    protected void setupDeleteCondition(final ScheduledJob scheduledJob) {

        // setup condition

    }

    public List<ScheduledJob> getScheduledJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

    public void store(final ScheduledJob scheduledJob) {
        final boolean isNew = scheduledJob.getId() == null;
        setupStoreCondition(scheduledJob);

        scheduledJobBhv.insertOrUpdate(scheduledJob, op -> {
            op.setRefresh(true);
        });
        if (!isNew) {
            jobScheduler.unregister(scheduledJob);
        }
        jobScheduler.register(scheduledJob);
    }

    public List<ScheduledJob> getCrawloerJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().setCrawler_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

}
