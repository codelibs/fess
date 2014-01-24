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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsWebAuthenticationAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebCrawlingConfig;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.WebCrawlingConfigService;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

public class WebAuthenticationAction extends BsWebAuthenticationAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(WebAuthenticationAction.class);

    @Resource
    protected WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("webAuthentication");
    }

    @Override
    protected void loadWebAuthentication() {

        final WebAuthentication webAuthentication = webAuthenticationService
                .getWebAuthentication(createKeyMap());
        if (webAuthentication == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { webAuthenticationForm.id });
        }

        FessBeans.copy(webAuthentication, webAuthenticationForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
        if ("-1".equals(webAuthenticationForm.port)) {
            webAuthenticationForm.port = "";
        }
    }

    @Override
    protected WebAuthentication createWebAuthentication() {
        WebAuthentication webAuthentication;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (webAuthenticationForm.crudMode == CommonConstants.EDIT_MODE) {
            webAuthentication = webAuthenticationService
                    .getWebAuthentication(createKeyMap());
            if (webAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { webAuthenticationForm.id });
            }
        } else {
            webAuthentication = new WebAuthentication();
            webAuthentication.setCreatedBy(username);
            webAuthentication.setCreatedTime(timestamp);
        }
        webAuthentication.setUpdatedBy(username);
        webAuthentication.setUpdatedTime(timestamp);
        if (StringUtil.isBlank(webAuthenticationForm.port)) {
            webAuthenticationForm.port = "-1";
        }
        FessBeans.copy(webAuthenticationForm, webAuthentication)
                .excludesCommonColumns().execute();

        return webAuthentication;
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (webAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            webAuthenticationForm.crudMode });
        }

        try {
            final WebAuthentication webAuthentication = webAuthenticationService
                    .getWebAuthentication(createKeyMap());
            if (webAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { webAuthenticationForm.id });
            }

            //           webAuthenticationService.delete(webAuthentication);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            webAuthentication.setDeletedBy(username);
            webAuthentication.setDeletedTime(timestamp);
            webAuthenticationService.store(webAuthentication);
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

    public boolean isDisplayCreateLink() {
        return !webCrawlingConfigService.getAllWebCrawlingConfigList(false,
                false, false, false, null).isEmpty();
    }

    public List<Map<String, String>> getWebCrawlingConfigItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<WebCrawlingConfig> webCrawlingConfigList = webCrawlingConfigService
                .getAllWebCrawlingConfigList(false, false, false, false, null);
        for (final WebCrawlingConfig webCrawlingConfig : webCrawlingConfigList) {
            items.add(createItem(webCrawlingConfig.getName(), webCrawlingConfig
                    .getId().toString()));
        }
        return items;
    }

    public List<Map<String, String>> getProtocolSchemeItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(createItem(MessageResourcesUtil.getMessage(RequestUtil
                .getRequest().getLocale(),
                "labels.web_authentication_scheme_basic"), Constants.BASIC));
        items.add(createItem(MessageResourcesUtil.getMessage(RequestUtil
                .getRequest().getLocale(),
                "labels.web_authentication_scheme_digest"), Constants.DIGEST));
        items.add(createItem(MessageResourcesUtil.getMessage(RequestUtil
                .getRequest().getLocale(),
                "labels.web_authentication_scheme_ntlm"), Constants.NTLM));
        return items;
    }

    protected Map<String, String> createItem(final String label,
            final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put("label", label);
        map.put("value", value);
        return map;
    }
}
