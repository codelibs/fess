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

package org.codelibs.fess.crud.action.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.OverlappingHost;
import org.codelibs.fess.form.admin.OverlappingHostForm;
import org.codelibs.fess.pager.OverlappingHostPager;
import org.codelibs.fess.service.OverlappingHostService;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class BsOverlappingHostAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(BsOverlappingHostAction.class);

    // for list

    public List<OverlappingHost> overlappingHostItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected OverlappingHostForm overlappingHostForm;

    @Resource
    protected OverlappingHostService overlappingHostService;

    @Resource
    protected OverlappingHostPager overlappingHostPager;

    protected String displayList(final boolean redirect) {
        // page navi
        overlappingHostItems = overlappingHostService
                .getOverlappingHostList(overlappingHostPager);

        // restore from pager
        Beans.copy(overlappingHostPager, overlappingHostForm.searchParams)
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
        if (StringUtil.isNotBlank(overlappingHostForm.pageNumber)) {
            try {
                overlappingHostPager.setCurrentPageNumber(Integer
                        .parseInt(overlappingHostForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: "
                            + overlappingHostForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(overlappingHostForm.searchParams, overlappingHostPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        overlappingHostPager.clear();

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
        if (overlappingHostForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            overlappingHostForm.crudMode });
        }

        loadOverlappingHost();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        overlappingHostForm.initialize();
        overlappingHostForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (overlappingHostForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            overlappingHostForm.crudMode });
        }

        loadOverlappingHost();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        overlappingHostForm.crudMode = CommonConstants.EDIT_MODE;

        loadOverlappingHost();

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
        if (overlappingHostForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            overlappingHostForm.crudMode });
        }

        loadOverlappingHost();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        overlappingHostForm.crudMode = CommonConstants.DELETE_MODE;

        loadOverlappingHost();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final OverlappingHost overlappingHost = createOverlappingHost();
            overlappingHostService.store(overlappingHost);
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
            final OverlappingHost overlappingHost = createOverlappingHost();
            overlappingHostService.store(overlappingHost);
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
        if (overlappingHostForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            overlappingHostForm.crudMode });
        }

        try {
            final OverlappingHost overlappingHost = overlappingHostService
                    .getOverlappingHost(createKeyMap());
            if (overlappingHost == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { overlappingHostForm.id });

            }

            overlappingHostService.delete(overlappingHost);
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

    protected void loadOverlappingHost() {

        final OverlappingHost overlappingHost = overlappingHostService
                .getOverlappingHost(createKeyMap());
        if (overlappingHost == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",

                    new Object[] { overlappingHostForm.id });

        }

        Beans.copy(overlappingHost, overlappingHostForm)
                .excludes("searchParams", "mode")

                .execute();
    }

    protected OverlappingHost createOverlappingHost() {
        OverlappingHost overlappingHost;
        if (overlappingHostForm.crudMode == CommonConstants.EDIT_MODE) {
            overlappingHost = overlappingHostService
                    .getOverlappingHost(createKeyMap());
            if (overlappingHost == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { overlappingHostForm.id });

            }
        } else {
            overlappingHost = new OverlappingHost();
        }
        Beans.copy(overlappingHostForm, overlappingHost)
                .excludes("searchParams", "mode")

                .execute();

        return overlappingHost;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", overlappingHostForm.id);

        return keys;
    }
}
