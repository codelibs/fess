/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SchedulerPager;
import org.codelibs.fess.es.config.cbean.ScheduledJobCB;
import org.codelibs.fess.es.config.exbhv.ScheduledJobBhv;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.job.ScheduledJobException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.LaCron;
import org.lastaflute.job.LaScheduledJob;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.job.subsidiary.CronParamsSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledJobService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobService.class);

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
            op.rangeSize(5);
        }).createPageNumberList());

        return scheduledJobList;
    }

    public OptionalEntity<ScheduledJob> getScheduledJob(final String id) {
        return scheduledJobBhv.selectByPK(id);
    }

    public void delete(final ScheduledJob scheduledJob) {

        scheduledJobBhv.delete(scheduledJob, op -> {
            op.setRefresh(true);
        });

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
        });
    }

    public void store(final ScheduledJob scheduledJob) {

        scheduledJobBhv.insertOrUpdate(scheduledJob, op -> {
            op.setRefresh(true);
        });
        JobManager jobManager = ComponentUtil.getJobManager();
        jobManager.schedule(cron -> register(cron, scheduledJob));
    }

    public List<ScheduledJob> getCrawlerJobList() {
        return scheduledJobBhv.selectList(cb -> {
            cb.query().setCrawler_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        });
    }

    protected void register(LaCron cron, final ScheduledJob scheduledJob) {
        if (scheduledJob == null) {
            throw new ScheduledJobException("No job.");
        }

        final String id = scheduledJob.getId();
        if (!Constants.T.equals(scheduledJob.getAvailable())) {
            logger.info("Inactive Job " + id + ":" + scheduledJob.getName());
            try {
                unregister(scheduledJob);
            } catch (final Exception e) {
                logger.debug("Failed to delete Job " + scheduledJob, e);
            }
            return;
        }

        final String target = scheduledJob.getTarget();
        if (!isTarget(target)) {
            logger.info("Ignore Job " + id + ":" + scheduledJob.getName() + " because of not target: " + scheduledJob.getTarget());
            return;
        }

        final CronParamsSupplier paramsOp = () -> {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.SCHEDULED_JOB, scheduledJob);
            return params;
        };
        findJobByUniqueOf(LaJobUnique.of(id)).ifPresent(job -> {
            if (!job.isUnscheduled()) {
                if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                    logger.info("Starting Job " + id + ":" + scheduledJob.getName());
                    final String cronExpression = scheduledJob.getCronExpression();
                    job.reschedule(cronExpression, op -> op.changeNoticeLogToDebug().params(paramsOp));
                } else {
                    logger.info("Inactive Job " + id + ":" + scheduledJob.getName());
                    job.becomeNonCron();
                }
            } else if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                logger.info("Starting Job " + id + ":" + scheduledJob.getName());
                String cronExpression = scheduledJob.getCronExpression();
                job.reschedule(cronExpression, op -> op.changeNoticeLogToDebug().params(paramsOp));
            }
        }).orElse(
                () -> {
                    if (StringUtil.isNotBlank(scheduledJob.getCronExpression())) {
                        logger.info("Starting Job " + id + ":" + scheduledJob.getName());
                        final String cronExpression = scheduledJob.getCronExpression();
                        cron.register(cronExpression, fessConfig.getSchedulerJobClassAsClass(),
                                fessConfig.getSchedulerConcurrentExecModeAsEnum(),
                                op -> op.uniqueBy(id).changeNoticeLogToDebug().params(paramsOp));
                    } else {
                        logger.info("Inactive Job " + id + ":" + scheduledJob.getName());
                        cron.registerNonCron(fessConfig.getSchedulerJobClassAsClass(), fessConfig.getSchedulerConcurrentExecModeAsEnum(),
                                op -> op.uniqueBy(id).changeNoticeLogToDebug().params(paramsOp));
                    }
                });
    }

    private OptionalThing<LaScheduledJob> findJobByUniqueOf(LaJobUnique jobUnique) {
        final JobManager jobManager = ComponentUtil.getJobManager();
        try {
            return jobManager.findJobByUniqueOf(jobUnique);
        } catch (Exception e) {
            return OptionalThing.empty();
        }
    }

    public void unregister(final ScheduledJob scheduledJob) {
        try {
            JobManager jobManager = ComponentUtil.getJobManager();
            if (jobManager.isSchedulingDone()) {
                jobManager.findJobByUniqueOf(LaJobUnique.of(scheduledJob.getId())).ifPresent(job -> {
                    job.unschedule();
                }).orElse(() -> logger.debug("Job {} is not scheduled.", scheduledJob.getId()));
            }
        } catch (final Exception e) {
            throw new ScheduledJobException("Failed to delete Job: " + scheduledJob, e);
        }
    }

    protected boolean isTarget(final String target) {
        if (StringUtil.isBlank(target)) {
            return true;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String myName = fessConfig.getSchedulerTargetName();

        final String[] targets = target.split(",");
        for (String name : targets) {
            name = name.trim();
            if (Constants.DEFAULT_JOB_TARGET.equalsIgnoreCase(name)) {
                return true;
            } else if (StringUtil.isNotBlank(myName) && myName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void start(LaCron cron) {
        scheduledJobBhv.selectCursor(cb -> {
            cb.query().setAvailable_Equal(Constants.T);
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
        }, scheduledJob -> {
            try {
                register(cron, scheduledJob);
            } catch (Exception e) {
                logger.error("Failed to start Job " + scheduledJob.getId(), e);
            }
        });
    }
}
