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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.WebAuthentication;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.WebAuthenticationPager;
import org.codelibs.fess.service.WebAuthenticationService;
import org.codelibs.fess.service.WebConfigService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebAuthenticationAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(WebAuthenticationAction.class);

    // for list

    public List<WebAuthentication> webAuthenticationItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected WebAuthenticationForm webAuthenticationForm;

    @Resource
    protected WebAuthenticationService webAuthenticationService;

    @Resource
    protected WebAuthenticationPager webAuthenticationPager;

    @Resource
    protected WebConfigService webConfigService;

    @Resource
    protected SystemHelper systemHelper;

    protected String displayList(final boolean redirect) {
        // page navi
        webAuthenticationItems = webAuthenticationService.getWebAuthenticationList(webAuthenticationPager);

        // restore from pager
        Beans.copy(webAuthenticationPager, webAuthenticationForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(webAuthenticationForm.pageNumber)) {
            try {
                webAuthenticationPager.setCurrentPageNumber(Integer.parseInt(webAuthenticationForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + webAuthenticationForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(webAuthenticationForm.searchParams, webAuthenticationPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        webAuthenticationPager.clear();

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
        if (webAuthenticationForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    webAuthenticationForm.crudMode });
        }

        loadWebAuthentication();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        webAuthenticationForm.initialize();
        webAuthenticationForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (webAuthenticationForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    webAuthenticationForm.crudMode });
        }

        loadWebAuthentication();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        webAuthenticationForm.crudMode = CommonConstants.EDIT_MODE;

        loadWebAuthentication();

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
        if (webAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    webAuthenticationForm.crudMode });
        }

        loadWebAuthentication();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        webAuthenticationForm.crudMode = CommonConstants.DELETE_MODE;

        loadWebAuthentication();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final WebAuthentication webAuthentication = createWebAuthentication();
            webAuthenticationService.store(webAuthentication);
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
            final WebAuthentication webAuthentication = createWebAuthentication();
            webAuthenticationService.store(webAuthentication);
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

        keys.put("id", webAuthenticationForm.id);

        return keys;
    }

    public String getHelpLink() {
        return systemHelper.getHelpLink("webAuthentication");
    }

    protected void loadWebAuthentication() {

        final WebAuthentication webAuthentication = webAuthenticationService.getWebAuthentication(createKeyMap());
        if (webAuthentication == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webAuthenticationForm.id });
        }

        FessBeans.copy(webAuthentication, webAuthenticationForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
        if ("-1".equals(webAuthenticationForm.port)) {
            webAuthenticationForm.port = StringUtil.EMPTY;
        }
    }

    protected WebAuthentication createWebAuthentication() {
        WebAuthentication webAuthentication;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (webAuthenticationForm.crudMode == CommonConstants.EDIT_MODE) {
            webAuthentication = webAuthenticationService.getWebAuthentication(createKeyMap());
            if (webAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webAuthenticationForm.id });
            }
        } else {
            webAuthentication = new WebAuthentication();
            webAuthentication.setCreatedBy(username);
            webAuthentication.setCreatedTime(currentTime);
        }
        webAuthentication.setUpdatedBy(username);
        webAuthentication.setUpdatedTime(currentTime);
        if (StringUtil.isBlank(webAuthenticationForm.port)) {
            webAuthenticationForm.port = "-1";
        }
        FessBeans.copy(webAuthenticationForm, webAuthentication).excludesCommonColumns().execute();

        return webAuthentication;
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (webAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    webAuthenticationForm.crudMode });
        }

        try {
            final WebAuthentication webAuthentication = webAuthenticationService.getWebAuthentication(createKeyMap());
            if (webAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webAuthenticationForm.id });
            }

            webAuthenticationService.delete(webAuthentication);
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

    public boolean isDisplayCreateLink() {
        return !webConfigService.getAllWebConfigList(false, false, false, null).isEmpty();
    }

    public List<Map<String, String>> getWebConfigItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<WebConfig> webConfigList = webConfigService.getAllWebConfigList(false, false, false, null);
        for (final WebConfig webConfig : webConfigList) {
            items.add(createItem(webConfig.getName(), webConfig.getId().toString()));
        }
        return items;
    }

    public List<Map<String, String>> getProtocolSchemeItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.web_authentication_scheme_basic"),
                Constants.BASIC));
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.web_authentication_scheme_digest"),
                Constants.DIGEST));
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.web_authentication_scheme_ntlm"),
                Constants.NTLM));
        return items;
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put("label", label);
        map.put("value", value);
        return map;
    }
}
