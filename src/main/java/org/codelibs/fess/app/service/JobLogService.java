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

package org.codelibs.fess.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.es.cbean.JobLogCB;
import org.codelibs.fess.es.exbhv.JobLogBhv;
import org.codelibs.fess.es.exentity.JobLog;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;

public class JobLogService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected JobLogBhv jobLogBhv;

    public JobLogService() {
        super();
    }

    public List<JobLog> getJobLogList(final JobLogPager jobLogPager) {

        final PagingResultBean<JobLog> jobLogList = jobLogBhv.selectPage(cb -> {
            cb.paging(jobLogPager.getPageSize(), jobLogPager.getCurrentPageNumber());
            setupListCondition(cb, jobLogPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(jobLogList, jobLogPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        jobLogPager.setPageNumberList(jobLogList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return jobLogList;
    }

    public JobLog getJobLog(final Map<String, String> keys) {
        final JobLog jobLog = jobLogBhv.selectEntity(cb -> {
            cb.query().docMeta().setId_Equal(keys.get("id"));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (jobLog == null) {
            // TODO exception?
            return null;
        }

        return jobLog;
    }

    public void store(final JobLog jobLog) {
        setupStoreCondition(jobLog);

        jobLogBhv.insertOrUpdate(jobLog, op -> {
            op.setRefresh(true);
        });

    }

    public void delete(final JobLog jobLog) {
        setupDeleteCondition(jobLog);

        jobLogBhv.delete(jobLog, op -> {
            op.setRefresh(true);
        });

    }

    protected void setupListCondition(final JobLogCB cb, final JobLogPager jobLogPager) {
        if (jobLogPager.id != null) {
            cb.query().docMeta().setId_Equal(jobLogPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_StartTime_Desc();
        cb.query().addOrderBy_EndTime_Desc();

        // search

    }

    protected void setupEntityCondition(final JobLogCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final JobLog jobLog) {

        // setup condition

    }

    protected void setupDeleteCondition(final JobLog jobLog) {

        // setup condition

    }

    public void deleteBefore(final int days) {
        final long oneday = 24 * 60 * 60 * 1000;
        final long targetTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong() - days * oneday;
        jobLogBhv.queryDelete(cb -> {
            cb.query().setEndTime_LessThan(targetTime);
        });
    }

    public void deleteByJobStatus(final List<String> jobStatusList) {
        jobLogBhv.queryDelete(cb -> {
            cb.query().setJobStatus_InScope(jobStatusList);
        });
    }

}
