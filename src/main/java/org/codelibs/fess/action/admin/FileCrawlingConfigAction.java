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
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.action.base.FessAdminAction;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.db.exentity.LabelType;
import org.codelibs.fess.db.exentity.RoleType;
import org.codelibs.fess.form.admin.FileCrawlingConfigForm;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.FileCrawlingConfigPager;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.service.FileCrawlingConfigService;
import org.codelibs.fess.service.LabelTypeService;
import org.codelibs.fess.service.RoleTypeService;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCrawlingConfigAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(FileCrawlingConfigAction.class);
<<<<<<< HEAD

=======
    
>>>>>>> 54e5e6c69f136354f051f88cbbd9aa07a3648500
    // for list

    public List<FileCrawlingConfig> fileCrawlingConfigItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected FileCrawlingConfigForm fileCrawlingConfigForm;

    @Resource
    protected FileCrawlingConfigService fileCrawlingConfigService;

    @Resource
    protected FileCrawlingConfigPager fileCrawlingConfigPager;

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected LabelTypeService labelTypeService;

    @Resource
    protected FailureUrlService failureUrlService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("fileCrawlingConfig");
    }
    
    protected String displayList(final boolean redirect) {
        // page navi
        fileCrawlingConfigItems = fileCrawlingConfigService.getFileCrawlingConfigList(fileCrawlingConfigPager);

        // restore from pager
        Beans.copy(fileCrawlingConfigPager, fileCrawlingConfigForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(fileCrawlingConfigForm.pageNumber)) {
            try {
                fileCrawlingConfigPager.setCurrentPageNumber(Integer.parseInt(fileCrawlingConfigForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + fileCrawlingConfigForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(fileCrawlingConfigForm.searchParams, fileCrawlingConfigPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        fileCrawlingConfigPager.clear();

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
        if (fileCrawlingConfigForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

        loadFileCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        fileCrawlingConfigForm.initialize();
        fileCrawlingConfigForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (fileCrawlingConfigForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

<<<<<<< HEAD
    protected String displayList(final boolean redirect) {
        // page navi
        fileCrawlingConfigItems = fileCrawlingConfigService.getFileCrawlingConfigList(fileCrawlingConfigPager);

        // restore from pager
        Beans.copy(fileCrawlingConfigPager, fileCrawlingConfigForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(fileCrawlingConfigForm.pageNumber)) {
            try {
                fileCrawlingConfigPager.setCurrentPageNumber(Integer.parseInt(fileCrawlingConfigForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + fileCrawlingConfigForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(fileCrawlingConfigForm.searchParams, fileCrawlingConfigPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        fileCrawlingConfigPager.clear();

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
        if (fileCrawlingConfigForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

        loadFileCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        fileCrawlingConfigForm.initialize();
        fileCrawlingConfigForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (fileCrawlingConfigForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

=======
>>>>>>> 54e5e6c69f136354f051f88cbbd9aa07a3648500
        loadFileCrawlingConfig();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        fileCrawlingConfigForm.crudMode = CommonConstants.EDIT_MODE;

        loadFileCrawlingConfig();

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
        if (fileCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

        loadFileCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        fileCrawlingConfigForm.crudMode = CommonConstants.DELETE_MODE;

        loadFileCrawlingConfig();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final FileCrawlingConfig fileCrawlingConfig = createFileCrawlingConfig();
            fileCrawlingConfigService.store(fileCrawlingConfig);
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
            final FileCrawlingConfig fileCrawlingConfig = createFileCrawlingConfig();
            fileCrawlingConfigService.store(fileCrawlingConfig);
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

        keys.put("id", fileCrawlingConfigForm.id);

        return keys;
    }
<<<<<<< HEAD

=======
    
>>>>>>> 54e5e6c69f136354f051f88cbbd9aa07a3648500
    protected void loadFileCrawlingConfig() {

        final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigService.getFileCrawlingConfig(createKeyMap());
        if (fileCrawlingConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileCrawlingConfigForm.id });
        }

        FessBeans.copy(fileCrawlingConfig, fileCrawlingConfigForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();

        // normalize boost
        if (fileCrawlingConfigForm.boost != null && fileCrawlingConfigForm.boost.indexOf('.') > 0) {
            fileCrawlingConfigForm.boost = fileCrawlingConfigForm.boost.substring(0, fileCrawlingConfigForm.boost.indexOf('.'));
        }
    }

    protected FileCrawlingConfig createFileCrawlingConfig() {
        FileCrawlingConfig fileCrawlingConfig;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (fileCrawlingConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            fileCrawlingConfig = fileCrawlingConfigService.getFileCrawlingConfig(createKeyMap());
            if (fileCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileCrawlingConfigForm.id });
            }
        } else {
            fileCrawlingConfig = new FileCrawlingConfig();
            fileCrawlingConfig.setCreatedBy(username);
            fileCrawlingConfig.setCreatedTime(currentTime);
        }
        fileCrawlingConfig.setUpdatedBy(username);
        fileCrawlingConfig.setUpdatedTime(currentTime);
        FessBeans.copy(fileCrawlingConfigForm, fileCrawlingConfig).excludesCommonColumns().execute();

        return fileCrawlingConfig;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (fileCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    fileCrawlingConfigForm.crudMode });
        }

        try {
            final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigService.getFileCrawlingConfig(createKeyMap());
            if (fileCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileCrawlingConfigForm.id });
            }

            failureUrlService.deleteByConfigId(fileCrawlingConfig.getConfigId());

            //fileCrawlingConfigService.delete(fileCrawlingConfig);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            fileCrawlingConfig.setDeletedBy(username);
            fileCrawlingConfig.setDeletedTime(currentTime);
            fileCrawlingConfigService.store(fileCrawlingConfig);
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
