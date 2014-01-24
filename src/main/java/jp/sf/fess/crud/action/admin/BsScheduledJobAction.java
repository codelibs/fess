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

package jp.sf.fess.crud.action.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.form.admin.ScheduledJobForm;
import jp.sf.fess.pager.ScheduledJobPager;
import jp.sf.fess.service.ScheduledJobService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class BsScheduledJobAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(BsScheduledJobAction.class);

    // for list

    public List<ScheduledJob> scheduledJobItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected ScheduledJobForm scheduledJobForm;

    @Resource
    protected ScheduledJobService scheduledJobService;

    @Resource
    protected ScheduledJobPager scheduledJobPager;

    protected String displayList(final boolean redirect) {
        // page navi
        scheduledJobItems = scheduledJobService
                .getScheduledJobList(scheduledJobPager);

        // restore from pager
        Beans.copy(scheduledJobPager, scheduledJobForm.searchParams)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(scheduledJobForm.pageNumber)) {
            try {
                scheduledJobPager.setCurrentPageNumber(Integer
                        .parseInt(scheduledJobForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + scheduledJobForm.pageNumber,
                            e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(scheduledJobForm.searchParams, scheduledJobPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        scheduledJobPager.clear();

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
        if (scheduledJobForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        scheduledJobForm.initialize();
        scheduledJobForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (scheduledJobForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        scheduledJobForm.crudMode = CommonConstants.EDIT_MODE;

        loadScheduledJob();

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
        if (scheduledJobForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            scheduledJobForm.crudMode });
        }

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        scheduledJobForm.crudMode = CommonConstants.DELETE_MODE;

        loadScheduledJob();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final ScheduledJob scheduledJob = createScheduledJob();
            scheduledJobService.store(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final ScheduledJob scheduledJob = createScheduledJob();
            scheduledJobService.store(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (scheduledJobForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            scheduledJobForm.crudMode });
        }

        try {
            final ScheduledJob scheduledJob = scheduledJobService
                    .getScheduledJob(createKeyMap());
            if (scheduledJob == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { scheduledJobForm.id });

            }

            scheduledJobService.delete(scheduledJob);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadScheduledJob() {

        final ScheduledJob scheduledJob = scheduledJobService
                .getScheduledJob(createKeyMap());
        if (scheduledJob == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",

                    new Object[] { scheduledJobForm.id });

        }

        Beans.copy(scheduledJob, scheduledJobForm)
                .excludes("searchParams", "mode")

                .execute();
    }

    protected ScheduledJob createScheduledJob() {
        ScheduledJob scheduledJob;
        if (scheduledJobForm.crudMode == CommonConstants.EDIT_MODE) {
            scheduledJob = scheduledJobService.getScheduledJob(createKeyMap());
            if (scheduledJob == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { scheduledJobForm.id });

            }
        } else {
            scheduledJob = new ScheduledJob();
        }
        Beans.copy(scheduledJobForm, scheduledJob)
                .excludes("searchParams", "mode")

                .execute();

        return scheduledJob;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", scheduledJobForm.id);

        return keys;
    }
}
