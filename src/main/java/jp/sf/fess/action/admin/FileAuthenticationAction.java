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
import jp.sf.fess.crud.action.admin.BsFileAuthenticationAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.FileCrawlingConfigService;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;

public class FileAuthenticationAction extends BsFileAuthenticationAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(FileAuthenticationAction.class);

    @Resource
    protected FileCrawlingConfigService fileCrawlingConfigService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("fileAuthentication");
    }

    @Override
    protected void loadFileAuthentication() {

        final FileAuthentication fileAuthentication = fileAuthenticationService
                .getFileAuthentication(createKeyMap());
        if (fileAuthentication == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { fileAuthenticationForm.id });
        }

        FessBeans.copy(fileAuthentication, fileAuthenticationForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
        if ("-1".equals(fileAuthenticationForm.port)) {
            fileAuthenticationForm.port = StringUtil.EMPTY;
        }
    }

    @Override
    protected FileAuthentication createFileAuthentication() {
        FileAuthentication fileAuthentication;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (fileAuthenticationForm.crudMode == CommonConstants.EDIT_MODE) {
            fileAuthentication = fileAuthenticationService
                    .getFileAuthentication(createKeyMap());
            if (fileAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { fileAuthenticationForm.id });
            }
        } else {
            fileAuthentication = new FileAuthentication();
            fileAuthentication.setCreatedBy(username);
            fileAuthentication.setCreatedTime(timestamp);
        }
        fileAuthentication.setUpdatedBy(username);
        fileAuthentication.setUpdatedTime(timestamp);
        if (StringUtil.isBlank(fileAuthenticationForm.port)) {
            fileAuthenticationForm.port = "-1";
        }
        FessBeans.copy(fileAuthenticationForm, fileAuthentication)
                .excludesCommonColumns().execute();

        return fileAuthentication;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (fileAuthenticationForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            fileAuthenticationForm.crudMode });
        }

        try {
            final FileAuthentication fileAuthentication = fileAuthenticationService
                    .getFileAuthentication(createKeyMap());
            if (fileAuthentication == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { fileAuthenticationForm.id });
            }

            //           fileAuthenticationService.delete(fileAuthentication);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            fileAuthentication.setDeletedBy(username);
            fileAuthentication.setDeletedTime(timestamp);
            fileAuthenticationService.store(fileAuthentication);
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
        return !fileCrawlingConfigService.getAllFileCrawlingConfigList(false,
                false, false, false, null).isEmpty();
    }

    public List<Map<String, String>> getFileCrawlingConfigItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        final List<FileCrawlingConfig> fileCrawlingConfigList = fileCrawlingConfigService
                .getAllFileCrawlingConfigList(false, false, false, false, null);
        for (final FileCrawlingConfig fileCrawlingConfig : fileCrawlingConfigList) {
            items.add(createItem(fileCrawlingConfig.getName(),
                    fileCrawlingConfig.getId().toString()));
        }
        return items;
    }

    public List<Map<String, String>> getProtocolSchemeItems() {
        final List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        items.add(createItem(MessageResourcesUtil.getMessage(RequestUtil
                .getRequest().getLocale(),
                "labels.file_authentication_scheme_samba"), Constants.SAMBA));
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
