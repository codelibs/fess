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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.JobLogCB;
import jp.sf.fess.db.exbhv.JobLogBhv;
import jp.sf.fess.db.exentity.JobLog;
import jp.sf.fess.pager.JobLogPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsJobLogService {

    @Resource
    protected JobLogBhv jobLogBhv;

    public BsJobLogService() {
        super();
    }

    public List<JobLog> getJobLogList(final JobLogPager jobLogPager) {

        final JobLogCB cb = new JobLogCB();

        cb.fetchFirst(jobLogPager.getPageSize());
        cb.fetchPage(jobLogPager.getCurrentPageNumber());

        setupListCondition(cb, jobLogPager);

        final PagingResultBean<JobLog> jobLogList = jobLogBhv.selectPage(cb);

        // update pager
        Beans.copy(jobLogList, jobLogPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        jobLogList.setPageRangeSize(5);
        jobLogPager.setPageNumberList(jobLogList.pageRange()
                .createPageNumberList());

        return jobLogList;
    }

    public JobLog getJobLog(final Map<String, String> keys) {
        final JobLogCB cb = new JobLogCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final JobLog jobLog = jobLogBhv.selectEntity(cb);
        if (jobLog == null) {
            // TODO exception?
            return null;
        }

        return jobLog;
    }

    public void store(final JobLog jobLog) throws CrudMessageException {
        setupStoreCondition(jobLog);

        jobLogBhv.insertOrUpdate(jobLog);

    }

    public void delete(final JobLog jobLog) throws CrudMessageException {
        setupDeleteCondition(jobLog);

        jobLogBhv.delete(jobLog);

    }

    protected void setupListCondition(final JobLogCB cb,
            final JobLogPager jobLogPager) {

        if (jobLogPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(jobLogPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final JobLogCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final JobLog jobLog) {
    }

    protected void setupDeleteCondition(final JobLog jobLog) {
    }
}