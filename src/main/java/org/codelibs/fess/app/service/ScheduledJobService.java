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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SchedulerPager;
import org.codelibs.fess.es.config.cbean.ScheduledJobCB;
import org.codelibs.fess.es.config.exbhv.ScheduledJobBhv;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.job.LaCron;

public class ScheduledJobService {

    private static final Logger logger = LogManager.getLogger(ScheduledJobService.class);

    @Resource
    protected ScheduledJobBhv scheduledJobBhv;

    @Resource
    protected FessConfig fessConfig;

    public List<ScheduledJob> getScheduledJobList(final SchedulerPager scheduledJobPager) {

        final PagingResultBean<ScheduledJob> scheduledJobList = scheduledJobBhv.selectPage(cb -> {
            cb.paging(scheduledJobPager.getPageSize(), scheduledJobPager.getCurrentPageNumber());
            setupListCondition(cb, scheduledJobPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(scheduledJobList, scheduledJobPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        scheduledJobPager.setPageNumberList(scheduledJobList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return scheduledJobList;
    }

    public List<ScheduledJob> getScheduledJobListAfter(final long updateTime) {
        return scheduledJobBhv.selectPage(cb -> {
            cb.fetchFirst(fessConfig.getPageScheduledJobMaxFetchSizeAsInteger());
            cb.query().setAvailable_Equal(Boolean.TRUE);
            cb.query().setUpdatedTime_GreaterThan(updateTime);
        });
    }

    public OptionalEntity<ScheduledJob> getScheduledJob(final String id) {
        return scheduledJobBhv.selectByPK(id);
    }

    public void delete(final ScheduledJob scheduledJob) {
        scheduledJobBhv.delete(scheduledJob, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
        ComponentUtil.getJobHelper().remove(scheduledJob);
    }

    protected void setupListCondition(final ScheduledJobCB cb, final SchedulerPager scheduledJobPager) {
        if (scheduledJobPager.id != null) {
            cb.query().docMeta().setId_Equal(scheduledJobPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_SortOrder_Asc();
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    public List<ScheduledJob> getScheduledJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(fessConfig.getPageScheduledJobMaxFetchSizeAsInteger());
        });
    }

    public void store(final ScheduledJob scheduledJob) {
        scheduledJobBhv.insertOrUpdate(scheduledJob, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });
    }

    public List<ScheduledJob> getCrawlerJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().setCrawler_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(fessConfig.getPageScheduledJobMaxFetchSizeAsInteger());
        });
    }

    public void start(final LaCron cron) {
        scheduledJobBhv.selectCursor(cb -> {
            cb.query().setAvailable_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        }, scheduledJob -> {
            try {
                ComponentUtil.getJobHelper().register(cron, scheduledJob);
            } catch (final Exception e) {
                logger.error("Failed to start Job {}", scheduledJob.getId(), e);
            }
        });
    }
}
