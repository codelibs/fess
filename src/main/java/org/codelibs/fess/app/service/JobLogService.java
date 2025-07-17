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

import java.util.List;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.JobLogCB;
import org.codelibs.fess.opensearch.config.exbhv.JobLogBhv;
import org.codelibs.fess.opensearch.config.exentity.JobLog;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.Resource;

/**
 * Service class for managing job logs in the Fess application.
 * Provides functionality to create, read, update, and delete job log entries,
 * as well as manage job status and perform cleanup operations.
 */
public class JobLogService {

    /**
     * Behavior class for job log database operations.
     */
    @Resource
    protected JobLogBhv jobLogBhv;

    /**
     * Default constructor.
     */
    public JobLogService() {
        // Default constructor
    }

    /**
     * Configuration settings for the Fess application.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Time interval in milliseconds after which jobs are considered expired.
     * Default is 2 hours (2 * 60 * 60 * 1000L).
     */
    protected long expiredJobInterval = 2 * 60 * 60 * 1000L; // 2hours

    /**
     * Retrieves a paginated list of job logs based on the provided pager configuration.
     *
     * @param jobLogPager the pager configuration for pagination and filtering
     * @return a list of job logs matching the criteria
     */
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

    /**
     * Retrieves a specific job log by its ID.
     *
     * @param id the unique identifier of the job log
     * @return an optional entity containing the job log if found
     */
    public OptionalEntity<JobLog> getJobLog(final String id) {
        return jobLogBhv.selectByPK(id);
    }

    /**
     * Stores a job log entry in the database.
     * Performs an insert or update operation based on whether the job log already exists.
     *
     * @param jobLog the job log to store
     */
    public void store(final JobLog jobLog) {

        jobLogBhv.insertOrUpdate(jobLog, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Deletes a specific job log from the database.
     *
     * @param jobLog the job log to delete
     */
    public void delete(final JobLog jobLog) {

        jobLogBhv.delete(jobLog, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    /**
     * Sets up the query conditions for retrieving job logs based on the pager configuration.
     * Configures filtering and ordering for the database query.
     *
     * @param cb the condition bean for building the query
     * @param jobLogPager the pager containing filter and search criteria
     */
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

    /**
     * Deletes job logs that ended before the specified number of days ago.
     * Used for cleaning up old log entries.
     *
     * @param days the number of days to look back from the current time
     */
    public void deleteBefore(final int days) {
        final long oneday = 24 * 60 * 60 * 1000L;
        final long targetTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong() - days * oneday;
        jobLogBhv.queryDelete(cb -> {
            cb.query().setEndTime_LessThan(targetTime);
        });
    }

    /**
     * Deletes job logs that have any of the specified job statuses.
     *
     * @param jobStatusList the list of job statuses to match for deletion
     */
    public void deleteByJobStatus(final List<String> jobStatusList) {
        jobLogBhv.queryDelete(cb -> {
            cb.query().setJobStatus_InScope(jobStatusList);
        });
    }

    /**
     * Updates the status of expired jobs that haven't finished.
     * Jobs that have been running longer than the expired job interval
     * without an end time are marked as failed.
     */
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

    /**
     * Sets the time interval after which jobs are considered expired.
     *
     * @param expiredJobInterval the time interval in milliseconds
     */
    public void setExpiredJobInterval(final long expiredJobInterval) {
        this.expiredJobInterval = expiredJobInterval;
    }

}
