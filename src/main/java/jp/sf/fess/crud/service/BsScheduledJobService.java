/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.ScheduledJobCB;
import jp.sf.fess.db.exbhv.ScheduledJobBhv;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.pager.ScheduledJobPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsScheduledJobService {

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    public BsScheduledJobService() {
        super();
    }

    public List<ScheduledJob> getScheduledJobList(
            final ScheduledJobPager scheduledJobPager) {

        final ScheduledJobCB cb = new ScheduledJobCB();

        cb.fetchFirst(scheduledJobPager.getPageSize());
        cb.fetchPage(scheduledJobPager.getCurrentPageNumber());

        setupListCondition(cb, scheduledJobPager);

        final PagingResultBean<ScheduledJob> scheduledJobList = scheduledJobBhv
                .selectPage(cb);

        // update pager
        Beans.copy(scheduledJobList, scheduledJobPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        scheduledJobList.setPageRangeSize(5);
        scheduledJobPager.setPageNumberList(scheduledJobList.pageRange()
                .createPageNumberList());

        return scheduledJobList;
    }

    public ScheduledJob getScheduledJob(final Map<String, String> keys) {
        final ScheduledJobCB cb = new ScheduledJobCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final ScheduledJob scheduledJob = scheduledJobBhv.selectEntity(cb);
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