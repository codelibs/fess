/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.action.admin;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsScheduledJobAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.helper.JobHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.RoleTypeService;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class ScheduledJobAction extends BsScheduledJobAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ScheduledJobAction.class);

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    public boolean running = false;

    public String getHelpLink() {
        return systemHelper.getHelpLink("scheduledJob");
    }

    @Override
    protected void loadScheduledJob() {

        final ScheduledJob scheduledJob = getScheduledJob();

        FessBeans.copy(scheduledJob, scheduledJobForm)
                .commonColumnDateConverter()
                .excludes("searchParams", "mode", "jobLogging").execute();
        scheduledJobForm.jobLogging = scheduledJob.isLoggingEnabled() ? Constants.ON
                : null;
        scheduledJobForm.crawler = scheduledJob.isCrawlerJob() ? Constants.ON
                : null;
        scheduledJobForm.available = scheduledJob.isEnabled() ? Constants.ON
                : null;
        running = scheduledJob.isRunning();
    }

    @Override
    protected ScheduledJob createScheduledJob() {
        ScheduledJob scheduledJob;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (scheduledJobForm.crudMode == CommonConstants.EDIT_MODE) {
            scheduledJob = scheduledJobService.getScheduledJob(createKeyMap());
            if (scheduledJob == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { scheduledJobForm.id });
            }
        } else {
            scheduledJob = new ScheduledJob();
            scheduledJob.setCreatedBy(username);
            scheduledJob.setCreatedTime(timestamp);
        }
        scheduledJob.setUpdatedBy(username);
        scheduledJob.setUpdatedTime(timestamp);
        FessBeans.copy(scheduledJobForm, scheduledJob).excludesCommonColumns()
                .execute();
        scheduledJob.setJobLogging(Constants.ON
                .equals(scheduledJobForm.jobLogging) ? Constants.T
                : Constants.F);
        scheduledJob
                .setCrawler(Constants.ON.equals(scheduledJobForm.crawler) ? Constants.T
                        : Constants.F);
        scheduledJob
                .setAvailable(Constants.ON.equals(scheduledJobForm.available) ? Constants.T
                        : Constants.F);

        return scheduledJob;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (scheduledJobForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            scheduledJobForm.crudMode });
        }

        try {
            final ScheduledJob scheduledJob = getScheduledJob();

            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            scheduledJob.setDeletedBy(username);
            scheduledJob.setDeletedTime(timestamp);
            scheduledJobService.store(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(),
                    e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String start() {
        final ScheduledJob scheduledJob = getScheduledJob();
        try {
            scheduledJob.start();
            SAStrutsUtil.addSessionMessage("success.job_started",
                    scheduledJob.getName());
            return displayList(true);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_start_job", scheduledJob.getName());
        }

    }

    @Execute(validator = false, input = "error.jsp")
    public String stop() {
        final ScheduledJob scheduledJob = getScheduledJob();
        try {
            jobHelper.getJobExecutoer(scheduledJob.getId());
            SAStrutsUtil.addSessionMessage("success.job_stopped",
                    scheduledJob.getName());
            return displayList(true);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_stop_job", scheduledJob.getName());
        }
    }

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    protected ScheduledJob getScheduledJob() {
        final ScheduledJob scheduledJob = scheduledJobService
                .getScheduledJob(createKeyMap());
        if (scheduledJob == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { scheduledJobForm.id });
        }
        return scheduledJob;
    }
}
