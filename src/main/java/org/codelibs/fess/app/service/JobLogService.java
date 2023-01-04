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

import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.es.config.cbean.JobLogCB;
import org.codelibs.fess.es.config.exbhv.JobLogBhv;
import org.codelibs.fess.es.config.exentity.JobLog;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class JobLogService {

    @Resource
    protected JobLogBhv jobLogBhv;

    @Resource
    protected FessConfig fessConfig;

    protected long expiredJobInterval = 2 * 60 * 60 * 1000L; // 2hours

    public List<JobLog> getJobLogList(final JobLogPager jobLogPager) {

        final PagingResultBean<JobLog> jobLogList = jobLogBhv.selectPage(cb -> {
            cb.paging(jobLogPager.getPageSize(), jobLogPager.getCurrentPageNumber());
            setupListCondition(cb, jobLogPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(jobLogList, jobLogPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        jobLogPager.setPageNumberList(jobLogList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return jobLogList;
    }

    public OptionalEntity<JobLog> getJobLog(final String id) {
        return jobLogBhv.selectByPK(id);
    }

    public void store(final JobLog jobLog) {

        jobLogBhv.insertOrUpdate(jobLog, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void delete(final JobLog jobLog) {

        jobLogBhv.delete(jobLog, op -> {
            op.setRefreshPolicy(Constants.TRUE);
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

    public void deleteBefore(final int days) {
        final long oneday = 24 * 60 * 60 * 1000L;
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

    public void updateStatus() {
        final long expiry = ComponentUtil.getSystemHelper().getCurrentTimeAsLong() - expiredJobInterval;
        final List<JobLog> list = jobLogBhv.selectList(cb -> {
            cb.query().bool((must, should, mustNot, filter) -> {
                must.setLastUpdated_LessEqual(expiry);
                mustNot.setEndTime_Exists();
            });
        });
        if (!list.isEmpty()) {
            list.forEach(jobLog -> {
                jobLog.setJobStatus(Constants.FAIL);
                jobLog.setScriptResult("No response from Job.");
                jobLog.setEndTime(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            });
            jobLogBhv.batchUpdate(list);
        }
    }

    public void setExpiredJobInterval(final long expiredJobInterval) {
        this.expiredJobInterval = expiredJobInterval;
    }

}
