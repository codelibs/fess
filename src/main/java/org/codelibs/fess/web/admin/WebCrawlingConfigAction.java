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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.action.admin.BsWebCrawlingConfigAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.CrawlingConfig;
import org.codelibs.fess.db.exentity.LabelType;
import org.codelibs.fess.db.exentity.RoleType;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.service.LabelTypeService;
import org.codelibs.fess.service.RoleTypeService;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class WebCrawlingConfigAction extends BsWebCrawlingConfigAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(WebCrawlingConfigAction.class);

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected LabelTypeService labelTypeService;

    @Resource
    protected FailureUrlService failureUrlService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("webCrawlingConfig");
    }

    @Override
    protected void loadWebCrawlingConfig() {

        final CrawlingConfig webCrawlingConfig = webCrawlingConfigService.getWebCrawlingConfig(createKeyMap());
        if (webCrawlingConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webCrawlingConfigForm.id });
        }

        FessBeans.copy(webCrawlingConfig, webCrawlingConfigForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();

        // normalize boost
        if (webCrawlingConfigForm.boost != null && webCrawlingConfigForm.boost.indexOf('.') > 0) {
            webCrawlingConfigForm.boost = webCrawlingConfigForm.boost.substring(0, webCrawlingConfigForm.boost.indexOf('.'));
        }
    }

    @Override
    protected WebCrawlingConfig createWebCrawlingConfig() {
        WebCrawlingConfig webCrawlingConfig;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (webCrawlingConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            webCrawlingConfig = webCrawlingConfigService.getWebCrawlingConfig(createKeyMap());
            if (webCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webCrawlingConfigForm.id });
            }
        } else {
            webCrawlingConfig = new WebCrawlingConfig();
            webCrawlingConfig.setCreatedBy(username);
            webCrawlingConfig.setCreatedTime(currentTime);
        }
        webCrawlingConfig.setUpdatedBy(username);
        webCrawlingConfig.setUpdatedTime(currentTime);
        FessBeans.copy(webCrawlingConfigForm, webCrawlingConfig).excludesCommonColumns().execute();

        return webCrawlingConfig;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (webCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    webCrawlingConfigForm.crudMode });
        }

        try {
            final WebCrawlingConfig webCrawlingConfig = webCrawlingConfigService.getWebCrawlingConfig(createKeyMap());
            if (webCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webCrawlingConfigForm.id });
            }

            failureUrlService.deleteByConfigId(webCrawlingConfig.getConfigId());

            //     webCrawlingConfigService.delete(webCrawlingConfig);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            webCrawlingConfig.setDeletedBy(username);
            webCrawlingConfig.setDeletedTime(currentTime);
            webCrawlingConfigService.store(webCrawlingConfig);
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

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    public List<LabelType> getLabelTypeItems() {
        return labelTypeService.getLabelTypeList();
    }
}
