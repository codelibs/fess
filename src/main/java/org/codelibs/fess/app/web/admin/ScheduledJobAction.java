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

package org.codelibs.fess.app.web.admin;

import java.beans.Beans;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.RoleType;
import org.codelibs.fess.es.exentity.ScheduledJob;
import org.codelibs.fess.exception.SSCActionMessagesException;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.app.pager.ScheduledJobPager;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledJobAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledJobAction.class);

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    // for list

    public List<ScheduledJob> scheduledJobItems;

    // for edit/confirm/delete

    //@ActionForm
    @Resource
    protected ScheduledJobForm scheduledJobForm;

    @Resource
    protected ScheduledJobService scheduledJobService;

    @Resource
    protected ScheduledJobPager scheduledJobPager;

    public boolean running = false;

    public String getHelpLink() {
        return systemHelper.getHelpLink("scheduledJob");
    }

    protected void loadScheduledJob() {

        final ScheduledJob scheduledJob = getScheduledJob();

        BeanUtil.copyBeanToBean(scheduledJob, scheduledJobForm, option -> option.exclude("searchParams", "mode", "jobLogging"));
        scheduledJobForm.jobLogging = scheduledJob.isLoggingEnabled() ? Constants.ON : null;
        scheduledJobForm.crawler = scheduledJob.isCrawlerJob() ? Constants.ON : null;
        scheduledJobForm.available = scheduledJob.isEnabled() ? Constants.ON : null;
        running = scheduledJob.isRunning();
    }

    protected ScheduledJob createScheduledJob() {
        ScheduledJob scheduledJob;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (scheduledJobForm.crudMode == CommonConstants.EDIT_MODE) {
            scheduledJob = scheduledJobService.getScheduledJob(createKeyMap());
            if (scheduledJob == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { scheduledJobForm.id });
            }
        } else {
            scheduledJob = new ScheduledJob();
            scheduledJob.setCreatedBy(username);
            scheduledJob.setCreatedTime(currentTime);
        }
        scheduledJob.setUpdatedBy(username);
        scheduledJob.setUpdatedTime(currentTime);
        BeanUtil.copyBeanToBean(scheduledJobForm, scheduledJob, option -> option.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        scheduledJob.setJobLogging(Constants.ON.equals(scheduledJobForm.jobLogging) ? Constants.T : Constants.F);
        scheduledJob.setCrawler(Constants.ON.equals(scheduledJobForm.crawler) ? Constants.T : Constants.F);
        scheduledJob.setAvailable(Constants.ON.equals(scheduledJobForm.available) ? Constants.T : Constants.F);

        return scheduledJob;
    }

    //@Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (scheduledJobForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    scheduledJobForm.crudMode });
        }

        try {
            final ScheduledJob scheduledJob = getScheduledJob();

            scheduledJobService.delete(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.crud_failed_to_delete_crud_table");
        }
    }

    //@Execute(validator = false, input = "error.jsp")
    public String start() {
        final ScheduledJob scheduledJob = getScheduledJob();
        try {
            scheduledJob.start();
            SAStrutsUtil.addSessionMessage("success.job_started", scheduledJob.getName());
            return displayList(true);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.failed_to_start_job", scheduledJob.getName());
        }

    }

    //@Execute(validator = false, input = "error.jsp")
    public String stop() {
        final ScheduledJob scheduledJob = getScheduledJob();
        try {
            final JobExecutor jobExecutoer = jobHelper.getJobExecutoer(scheduledJob.getId());
            jobExecutoer.shutdown();
            SAStrutsUtil.addSessionMessage("success.job_stopped", scheduledJob.getName());
            return displayList(true);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.failed_to_stop_job", scheduledJob.getName());
        }
    }

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    protected ScheduledJob getScheduledJob() {
        final ScheduledJob scheduledJob = scheduledJobService.getScheduledJob(createKeyMap());
        if (scheduledJob == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { scheduledJobForm.id });
        }
        return scheduledJob;
    }

    protected String displayList(final boolean redirect) {
        // page navi
        scheduledJobItems = scheduledJobService.getScheduledJobList(scheduledJobPager);

        // restore from pager
        BeanUtil.copyBeanToBean(scheduledJobPager, scheduledJobForm.searchParams,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    //@Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(scheduledJobForm.pageNumber)) {
            try {
                scheduledJobPager.setCurrentPageNumber(Integer.parseInt(scheduledJobForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + scheduledJobForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String search() {
        BeanUtil.copyBeanToBean(scheduledJobForm.searchParams, scheduledJobPager,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String reset() {
        scheduledJobPager.clear();

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (scheduledJobForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        scheduledJobForm.initialize();
        scheduledJobForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (scheduledJobForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        scheduledJobForm.crudMode = CommonConstants.EDIT_MODE;

        loadScheduledJob();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (scheduledJobForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        scheduledJobForm.crudMode = CommonConstants.DELETE_MODE;

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final ScheduledJob scheduledJob = createScheduledJob();
            scheduledJobService.store(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final ScheduledJob scheduledJob = createScheduledJob();
            scheduledJobService.store(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_update_crud_table");
        }
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", scheduledJobForm.id);

        return keys;
    }
}
