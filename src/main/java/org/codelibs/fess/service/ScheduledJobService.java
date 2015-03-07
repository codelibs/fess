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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.service.BsScheduledJobService;
import org.codelibs.fess.db.cbean.ScheduledJobCB;
import org.codelibs.fess.db.exentity.ScheduledJob;
import org.codelibs.fess.job.JobScheduler;
import org.codelibs.fess.pager.ScheduledJobPager;

public class ScheduledJobService extends BsScheduledJobService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected JobScheduler jobScheduler;

    @Override
    protected void setupListCondition(final ScheduledJobCB cb, final ScheduledJobPager scheduledJobPager) {
        super.setupListCondition(cb, scheduledJobPager);

        // setup condition
        cb.query().setDeletedBy_IsNull();
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    @Override
    protected void setupEntityCondition(final ScheduledJobCB cb, final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition
        cb.query().setDeletedBy_IsNull();

    }

    @Override
    protected void setupStoreCondition(final ScheduledJob scheduledJob) {
        super.setupStoreCondition(scheduledJob);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final ScheduledJob scheduledJob) {
        super.setupDeleteCondition(scheduledJob);

        // setup condition

    }

    public List<ScheduledJob> getScheduledJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

    @Override
    public void store(final ScheduledJob scheduledJob) {
        final boolean isNew = scheduledJob.getId() == null;
        final boolean isDelete = scheduledJob.getDeletedBy() != null;
        super.store(scheduledJob);
        if (!isNew) {
            jobScheduler.unregister(scheduledJob);
        }
        if (!isDelete) {
            jobScheduler.register(scheduledJob);
        }
    }

    public List<ScheduledJob> getCrawloerJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().setDeletedBy_IsNull();
            cb.query().setCrawler_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

}
