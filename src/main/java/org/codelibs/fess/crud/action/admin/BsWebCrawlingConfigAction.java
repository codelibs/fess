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
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.form.admin.WebCrawlingConfigForm;
import org.codelibs.fess.pager.WebCrawlingConfigPager;
import org.codelibs.fess.service.WebCrawlingConfigService;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class BsWebCrawlingConfigAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(BsWebCrawlingConfigAction.class);

    // for list

    public List<WebCrawlingConfig> webCrawlingConfigItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected WebCrawlingConfigForm webCrawlingConfigForm;

    @Resource
    protected WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected WebCrawlingConfigPager webCrawlingConfigPager;

    protected String displayList(final boolean redirect) {
        // page navi
        webCrawlingConfigItems = webCrawlingConfigService
                .getWebCrawlingConfigList(webCrawlingConfigPager);

        // restore from pager
        Beans.copy(webCrawlingConfigPager, webCrawlingConfigForm.searchParams)
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
        if (StringUtil.isNotBlank(webCrawlingConfigForm.pageNumber)) {
            try {
                webCrawlingConfigPager.setCurrentPageNumber(Integer
                        .parseInt(webCrawlingConfigForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: "
                            + webCrawlingConfigForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(webCrawlingConfigForm.searchParams, webCrawlingConfigPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        webCrawlingConfigPager.clear();

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
        if (webCrawlingConfigForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            webCrawlingConfigForm.crudMode });
        }

        loadWebCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        webCrawlingConfigForm.initialize();
        webCrawlingConfigForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (webCrawlingConfigForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            webCrawlingConfigForm.crudMode });
        }

        loadWebCrawlingConfig();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        webCrawlingConfigForm.crudMode = CommonConstants.EDIT_MODE;

        loadWebCrawlingConfig();

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
        if (webCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            webCrawlingConfigForm.crudMode });
        }

        loadWebCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        webCrawlingConfigForm.crudMode = CommonConstants.DELETE_MODE;

        loadWebCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final WebCrawlingConfig webCrawlingConfig = createWebCrawlingConfig();
            webCrawlingConfigService.store(webCrawlingConfig);
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
            final WebCrawlingConfig webCrawlingConfig = createWebCrawlingConfig();
            webCrawlingConfigService.store(webCrawlingConfig);
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
        if (webCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            webCrawlingConfigForm.crudMode });
        }

        try {
            final WebCrawlingConfig webCrawlingConfig = webCrawlingConfigService
                    .getWebCrawlingConfig(createKeyMap());
            if (webCrawlingConfig == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { webCrawlingConfigForm.id });

            }

            webCrawlingConfigService.delete(webCrawlingConfig);
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

    protected void loadWebCrawlingConfig() {

        final WebCrawlingConfig webCrawlingConfig = webCrawlingConfigService
                .getWebCrawlingConfig(createKeyMap());
        if (webCrawlingConfig == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",

                    new Object[] { webCrawlingConfigForm.id });

        }

        Beans.copy(webCrawlingConfig, webCrawlingConfigForm)
                .excludes("searchParams", "mode")

                .execute();
    }

    protected WebCrawlingConfig createWebCrawlingConfig() {
        WebCrawlingConfig webCrawlingConfig;
        if (webCrawlingConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            webCrawlingConfig = webCrawlingConfigService
                    .getWebCrawlingConfig(createKeyMap());
            if (webCrawlingConfig == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { webCrawlingConfigForm.id });

            }
        } else {
            webCrawlingConfig = new WebCrawlingConfig();
        }
        Beans.copy(webCrawlingConfigForm, webCrawlingConfig)
                .excludes("searchParams", "mode")

                .execute();

        return webCrawlingConfig;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", webCrawlingConfigForm.id);

        return keys;
    }
}
