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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.FailureUrl;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.FailureUrlPager;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailureUrlAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(FailureUrlAction.class);

    // for list

    public List<FailureUrl> failureUrlItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected FailureUrlForm failureUrlForm;

    @Resource
    protected FailureUrlService failureUrlService;

    @Resource
    protected FailureUrlPager failureUrlPager;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("failureUrl");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        failureUrlItems = failureUrlService.getFailureUrlList(failureUrlPager);

        // restore from pager
        Beans.copy(failureUrlPager, failureUrlForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(failureUrlForm.pageNumber)) {
            try {
                failureUrlPager.setCurrentPageNumber(Integer.parseInt(failureUrlForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + failureUrlForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(failureUrlForm.searchParams, failureUrlPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        failureUrlPager.clear();

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
        if (failureUrlForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    failureUrlForm.crudMode });
        }

        loadFailureUrl();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        failureUrlForm.initialize();
        failureUrlForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (failureUrlForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE, failureUrlForm.crudMode });
        }

        loadFailureUrl();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        failureUrlForm.crudMode = CommonConstants.EDIT_MODE;

        loadFailureUrl();

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
        if (failureUrlForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    failureUrlForm.crudMode });
        }

        loadFailureUrl();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        failureUrlForm.crudMode = CommonConstants.DELETE_MODE;

        loadFailureUrl();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final FailureUrl failureUrl = createFailureUrl();
            failureUrlService.store(failureUrl);
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
            final FailureUrl failureUrl = createFailureUrl();
            failureUrlService.store(failureUrl);
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
        if (failureUrlForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    failureUrlForm.crudMode });
        }

        try {
            final FailureUrl failureUrl = failureUrlService.getFailureUrl(createKeyMap());
            if (failureUrl == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { failureUrlForm.id });

            }

            failureUrlService.delete(failureUrl);
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

    protected void loadFailureUrl() {

        final FailureUrl failureUrl = failureUrlService.getFailureUrl(createKeyMap());
        if (failureUrl == null) {
            // throw an exception
            throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

            new Object[] { failureUrlForm.id });

        }

        Beans.copy(failureUrl, failureUrlForm).excludes("searchParams", "mode")

        .execute();
    }

    protected FailureUrl createFailureUrl() {
        FailureUrl failureUrl;
        if (failureUrlForm.crudMode == CommonConstants.EDIT_MODE) {
            failureUrl = failureUrlService.getFailureUrl(createKeyMap());
            if (failureUrl == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { failureUrlForm.id });

            }
        } else {
            failureUrl = new FailureUrl();
        }
        Beans.copy(failureUrlForm, failureUrl).excludes("searchParams", "mode")

        .execute();

        return failureUrl;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", failureUrlForm.id);

        return keys;
    }

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        failureUrlService.deleteAll(failureUrlPager);
        SAStrutsUtil.addSessionMessage("success.failure_url_delete_all");
        return displayList(true);
    }
}
