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

import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.CrawlingConfig;
import org.codelibs.fess.es.exentity.LabelType;
import org.codelibs.fess.es.exentity.RoleType;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.WebConfigPager;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.service.LabelTypeService;
import org.codelibs.fess.service.RoleTypeService;
import org.codelibs.fess.service.WebConfigService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConfigAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(WebConfigAction.class);

    // for list

    public List<WebConfig> webConfigItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected WebConfigForm webConfigForm;

    @Resource
    protected WebConfigService webConfigService;

    @Resource
    protected WebConfigPager webConfigPager;

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected LabelTypeService labelTypeService;

    @Resource
    protected FailureUrlService failureUrlService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("webConfig");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        webConfigItems = webConfigService.getWebConfigList(webConfigPager);

        // restore from pager
        Beans.copy(webConfigPager, webConfigForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(webConfigForm.pageNumber)) {
            try {
                webConfigPager.setCurrentPageNumber(Integer.parseInt(webConfigForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + webConfigForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(webConfigForm.searchParams, webConfigPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        webConfigPager.clear();

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
        if (webConfigForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    webConfigForm.crudMode });
        }

        loadWebConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        webConfigForm.initialize();
        webConfigForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (webConfigForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE, webConfigForm.crudMode });
        }

        loadWebConfig();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        webConfigForm.crudMode = CommonConstants.EDIT_MODE;

        loadWebConfig();

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
        if (webConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    webConfigForm.crudMode });
        }

        loadWebConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        webConfigForm.crudMode = CommonConstants.DELETE_MODE;

        loadWebConfig();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final WebConfig webConfig = createWebConfig();
            webConfigService.store(webConfig);
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
            final WebConfig webConfig = createWebConfig();
            webConfigService.store(webConfig);
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

        keys.put("id", webConfigForm.id);

        return keys;
    }

    protected void loadWebConfig() {

        final CrawlingConfig webConfig = webConfigService.getWebConfig(createKeyMap());
        if (webConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webConfigForm.id });
        }

        FessBeans.copy(webConfig, webConfigForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();

        // normalize boost
        if (webConfigForm.boost != null && webConfigForm.boost.indexOf('.') > 0) {
            webConfigForm.boost = webConfigForm.boost.substring(0, webConfigForm.boost.indexOf('.'));
        }
    }

    protected WebConfig createWebConfig() {
        WebConfig webConfig;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (webConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            webConfig = webConfigService.getWebConfig(createKeyMap());
            if (webConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webConfigForm.id });
            }
        } else {
            webConfig = new WebConfig();
            webConfig.setCreatedBy(username);
            webConfig.setCreatedTime(currentTime);
        }
        webConfig.setUpdatedBy(username);
        webConfig.setUpdatedTime(currentTime);
        FessBeans.copy(webConfigForm, webConfig).excludesCommonColumns().execute();

        return webConfig;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (webConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    webConfigForm.crudMode });
        }

        try {
            final WebConfig webConfig = webConfigService.getWebConfig(createKeyMap());
            if (webConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { webConfigForm.id });
            }

            failureUrlService.deleteByConfigId(webConfig.getConfigId());

            webConfigService.delete(webConfig);
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

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    public List<LabelType> getLabelTypeItems() {
        return labelTypeService.getLabelTypeList();
    }
}
