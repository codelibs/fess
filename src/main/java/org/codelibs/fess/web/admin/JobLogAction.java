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

package org.codelibs.fess.web.admin;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.JobLog;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.JobLogPager;
import org.codelibs.fess.service.JobLogService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.lastaflute.web.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(JobLogAction.class);

    // for list

    public List<JobLog> jobLogItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected JobLogForm jobLogForm;

    @Resource
    protected JobLogService jobLogService;

    @Resource
    protected JobLogPager jobLogPager;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("jobLog");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        jobLogItems = jobLogService.getJobLogList(jobLogPager);

        // restore from pager
        Beans.copy(jobLogPager, jobLogForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(jobLogForm.pageNumber)) {
            try {
                jobLogPager.setCurrentPageNumber(Integer.parseInt(jobLogForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + jobLogForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(jobLogForm.searchParams, jobLogPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        jobLogPager.clear();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (jobLogForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE, jobLogForm.crudMode });
        }

        loadJobLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        jobLogForm.initialize();
        jobLogForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (jobLogForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE, jobLogForm.crudMode });
        }

        loadJobLog();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        jobLogForm.crudMode = CommonConstants.EDIT_MODE;

        loadJobLog();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (jobLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE, jobLogForm.crudMode });
        }

        loadJobLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        jobLogForm.crudMode = CommonConstants.DELETE_MODE;

        loadJobLog();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final JobLog jobLog = createJobLog();
            jobLogService.store(jobLog);
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
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final JobLog jobLog = createJobLog();
            jobLogService.store(jobLog);
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

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (jobLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE, jobLogForm.crudMode });
        }

        try {
            final JobLog jobLog = jobLogService.getJobLog(createKeyMap());
            if (jobLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { jobLogForm.id });

            }

            jobLogService.delete(jobLog);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadJobLog() {

        final JobLog jobLog = jobLogService.getJobLog(createKeyMap());
        if (jobLog == null) {
            // throw an exception
            throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

            new Object[] { jobLogForm.id });

        }

        Beans.copy(jobLog, jobLogForm).excludes("searchParams", "mode")

        .execute();
    }

    protected JobLog createJobLog() {
        JobLog jobLog;
        if (jobLogForm.crudMode == CommonConstants.EDIT_MODE) {
            jobLog = jobLogService.getJobLog(createKeyMap());
            if (jobLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { jobLogForm.id });

            }
        } else {
            jobLog = new JobLog();
        }
        Beans.copy(jobLogForm, jobLog).excludes("searchParams", "mode")

        .execute();

        return jobLog;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", jobLogForm.id);

        return keys;
    }

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        final List<String> jobStatusList = new ArrayList<String>();
        jobStatusList.add(Constants.OK);
        jobStatusList.add(Constants.FAIL);
        jobLogService.deleteByJobStatus(jobStatusList);
        SAStrutsUtil.addSessionMessage("success.joblog_delete_all");
        return displayList(true);
    }
}
