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

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.FileAuthentication;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.FileAuthenticationPager;
import org.codelibs.fess.service.FileAuthenticationService;
import org.codelibs.fess.service.FileCrawlingConfigService;
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

public class FileAuthenticationAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(FileAuthenticationAction.class);

    // for list
    public List<FileAuthentication> fileAuthenticationItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected FileCrawlingConfigService fileCrawlingConfigService;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FileAuthenticationForm fileAuthenticationForm;

    @Resource
    protected FileAuthenticationService fileAuthenticationService;

    @Resource
    protected FileAuthenticationPager fileAuthenticationPager;

    public String getHelpLink() {
        return systemHelper.getHelpLink("fileAuthentication");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        fileAuthenticationItems = fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager);

        // restore from pager
        Beans.copy(fileAuthenticationPager, fileAuthenticationForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(fileAuthenticationForm.pageNumber)) {
            try {
                fileAuthenticationPager.setCurrentPageNumber(Integer.parseInt(fileAuthenticationForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + fileAuthenticationForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(fileAuthenticationForm.searchParams, fileAuthenticationPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        fileAuthenticationPager.clear();

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
        if (fileAuthenticationForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    fileAuthenticationForm.crudMode });
        }

        loadFileAuthentication();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        fileAuthenticationForm.initialize();
        fileAuthenticationForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (fileAuthenticationForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    fileAuthenticationForm.crudMode });
        }

        loadFileAuthentication();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        fileAuthenticationForm.crudMode = CommonConstants.EDIT_MODE;

        loadFileAuthentication();

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
        if (fileAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    fileAuthenticationForm.crudMode });
        }

        loadFileAuthentication();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        fileAuthenticationForm.crudMode = CommonConstants.DELETE_MODE;

        loadFileAuthentication();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final FileAuthentication fileAuthentication = createFileAuthentication();
            fileAuthenticationService.store(fileAuthentication);
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
            final FileAuthentication fileAuthentication = createFileAuthentication();
            fileAuthenticationService.store(fileAuthentication);
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

    protected void loadFileAuthentication() {

        final FileAuthentication fileAuthentication = fileAuthenticationService.getFileAuthentication(createKeyMap());
        if (fileAuthentication == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileAuthenticationForm.id });
        }

        FessBeans.copy(fileAuthentication, fileAuthenticationForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
        if ("-1".equals(fileAuthenticationForm.port)) {
            fileAuthenticationForm.port = StringUtil.EMPTY;
        }
    }

    protected FileAuthentication createFileAuthentication() {
        FileAuthentication fileAuthentication;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (fileAuthenticationForm.crudMode == CommonConstants.EDIT_MODE) {
            fileAuthentication = fileAuthenticationService.getFileAuthentication(createKeyMap());
            if (fileAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileAuthenticationForm.id });
            }
        } else {
            fileAuthentication = new FileAuthentication();
            fileAuthentication.setCreatedBy(username);
            fileAuthentication.setCreatedTime(currentTime);
        }
        fileAuthentication.setUpdatedBy(username);
        fileAuthentication.setUpdatedTime(currentTime);
        if (StringUtil.isBlank(fileAuthenticationForm.port)) {
            fileAuthenticationForm.port = "-1";
        }
        FessBeans.copy(fileAuthenticationForm, fileAuthentication).excludesCommonColumns().execute();

        return fileAuthentication;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (fileAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    fileAuthenticationForm.crudMode });
        }

        try {
            final FileAuthentication fileAuthentication = fileAuthenticationService.getFileAuthentication(createKeyMap());
            if (fileAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { fileAuthenticationForm.id });
            }

            //           fileAuthenticationService.delete(fileAuthentication);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            fileAuthentication.setDeletedBy(username);
            fileAuthentication.setDeletedTime(currentTime);
            fileAuthenticationService.store(fileAuthentication);
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

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", fileAuthenticationForm.id);

        return keys;
    }

    public boolean isDisplayCreateLink() {
        return !fileCrawlingConfigService.getAllFileCrawlingConfigList(false, false, false, null).isEmpty();
    }

    public List<Map<String, String>> getFileCrawlingConfigItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<FileCrawlingConfig> fileCrawlingConfigList =
                fileCrawlingConfigService.getAllFileCrawlingConfigList(false, false, false, null);
        for (final FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
            items.add(createItem(fileCrawlingConfig.getName(), fileCrawlingConfig.getId().toString()));
        }
        return items;
    }

    public List<Map<String, String>> getProtocolSchemeItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(createItem(
                MessageResourcesUtil.getMessage(RequestUtil.getRequest().getLocale(), "labels.file_authentication_scheme_samba"),
                Constants.SAMBA));
        return items;
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put("label", label);
        map.put("value", value);
        return map;
    }
}
