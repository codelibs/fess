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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.RequestHeader;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.RequestHeaderPager;
import org.codelibs.fess.service.RequestHeaderService;
import org.codelibs.fess.service.WebCrawlingConfigService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaderAction extends FessAdminAction {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaderAction.class);

    // for list

    public List<RequestHeader> requestHeaderItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected RequestHeaderForm requestHeaderForm;

    @Resource
    protected RequestHeaderService requestHeaderService;

    @Resource
    protected RequestHeaderPager requestHeaderPager;

    @Resource
    protected WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected SystemHelper systemHelper;

    protected String displayList(final boolean redirect) {
        // page navi
        requestHeaderItems = requestHeaderService.getRequestHeaderList(requestHeaderPager);

        // restore from pager
        Beans.copy(requestHeaderPager, requestHeaderForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(requestHeaderForm.pageNumber)) {
            try {
                requestHeaderPager.setCurrentPageNumber(Integer.parseInt(requestHeaderForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + requestHeaderForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(requestHeaderForm.searchParams, requestHeaderPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        requestHeaderPager.clear();

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
        if (requestHeaderForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    requestHeaderForm.crudMode });
        }

        loadRequestHeader();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        requestHeaderForm.initialize();
        requestHeaderForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (requestHeaderForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    requestHeaderForm.crudMode });
        }

        loadRequestHeader();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        requestHeaderForm.crudMode = CommonConstants.EDIT_MODE;

        loadRequestHeader();

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
        if (requestHeaderForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    requestHeaderForm.crudMode });
        }

        loadRequestHeader();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        requestHeaderForm.crudMode = CommonConstants.DELETE_MODE;

        loadRequestHeader();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final RequestHeader requestHeader = createRequestHeader();
            requestHeaderService.store(requestHeader);
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
            throw new ActionMessagesException("errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final RequestHeader requestHeader = createRequestHeader();
            requestHeaderService.store(requestHeader);
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
            throw new ActionMessagesException("errors.crud_failed_to_update_crud_table");
        }
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", requestHeaderForm.id);

        return keys;
    }

    public String getHelpLink() {
        return systemHelper.getHelpLink("requestHeader");
    }

    protected void loadRequestHeader() {

        final RequestHeader requestHeader = requestHeaderService.getRequestHeader(createKeyMap());
        if (requestHeader == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { requestHeaderForm.id });
        }

        FessBeans.copy(requestHeader, requestHeaderForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    protected RequestHeader createRequestHeader() {
        RequestHeader requestHeader;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (requestHeaderForm.crudMode == CommonConstants.EDIT_MODE) {
            requestHeader = requestHeaderService.getRequestHeader(createKeyMap());
            if (requestHeader == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { requestHeaderForm.id });
            }
        } else {
            requestHeader = new RequestHeader();
            requestHeader.setCreatedBy(username);
            requestHeader.setCreatedTime(currentTime);
        }
        requestHeader.setUpdatedBy(username);
        requestHeader.setUpdatedTime(currentTime);
        FessBeans.copy(requestHeaderForm, requestHeader).excludesCommonColumns().execute();

        return requestHeader;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (requestHeaderForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    requestHeaderForm.crudMode });
        }

        try {
            final RequestHeader requestHeader = requestHeaderService.getRequestHeader(createKeyMap());
            if (requestHeader == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { requestHeaderForm.id });
            }

            //           requestHeaderService.delete(requestHeader);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            requestHeader.setDeletedBy(username);
            requestHeader.setDeletedTime(currentTime);
            requestHeaderService.store(requestHeader);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.crud_failed_to_delete_crud_table");
        }
    }

    public List<Map<String, String>> getWebCrawlingConfigItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<WebCrawlingConfig> webCrawlingConfigList =
                webCrawlingConfigService.getAllWebCrawlingConfigList(false, false, false, null);
        for (final WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
            items.add(createItem(webCrawlingConfig.getName(), webCrawlingConfig.getId().toString()));
        }
        return items;
    }

    public List<Map<String, String>> getProtocolSchemeItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.web_authentication_scheme_basic"), "BASIC"));
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.web_authentication_scheme_digest"), "DIGEST"));
        return items;
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put("label", label);
        map.put("value", value);
        return map;
    }

    public boolean isDisplayCreateLink() {
        return !webCrawlingConfigService.getAllWebCrawlingConfigList(false, false, false, null).isEmpty();
    }
}
