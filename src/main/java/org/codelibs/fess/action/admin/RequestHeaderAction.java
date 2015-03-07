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

package org.codelibs.fess.action.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.action.admin.BsRequestHeaderAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.RequestHeader;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.service.WebCrawlingConfigService;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

public class RequestHeaderAction extends BsRequestHeaderAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(RequestHeaderAction.class);

    @Resource
    protected WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("requestHeader");
    }

    @Override
    protected void loadRequestHeader() {

        final RequestHeader requestHeader = requestHeaderService.getRequestHeader(createKeyMap());
        if (requestHeader == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { requestHeaderForm.id });
        }

        FessBeans.copy(requestHeader, requestHeaderForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    @Override
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

    @Override
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
