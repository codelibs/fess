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

package jp.sf.fess.action.admin;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsFileCrawlingConfigAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.FailureUrlService;
import jp.sf.fess.service.LabelTypeService;
import jp.sf.fess.service.RoleTypeService;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class FileCrawlingConfigAction extends BsFileCrawlingConfigAction {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(FileCrawlingConfigAction.class);

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

    @Override
    protected void loadFileCrawlingConfig() {

        final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigService
                .getFileCrawlingConfig(createKeyMap());
        if (fileCrawlingConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { fileCrawlingConfigForm.id });
        }

        FessBeans.copy(fileCrawlingConfig, fileCrawlingConfigForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();

        // normalize boost
        if (fileCrawlingConfigForm.boost != null
                && fileCrawlingConfigForm.boost.indexOf('.') > 0) {
            fileCrawlingConfigForm.boost = fileCrawlingConfigForm.boost
                    .substring(0, fileCrawlingConfigForm.boost.indexOf('.'));
        }
    }

    @Override
    protected FileCrawlingConfig createFileCrawlingConfig() {
        FileCrawlingConfig fileCrawlingConfig;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (fileCrawlingConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            fileCrawlingConfig = fileCrawlingConfigService
                    .getFileCrawlingConfig(createKeyMap());
            if (fileCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { fileCrawlingConfigForm.id });
            }
        } else {
            fileCrawlingConfig = new FileCrawlingConfig();
            fileCrawlingConfig.setCreatedBy(username);
            fileCrawlingConfig.setCreatedTime(timestamp);
        }
        fileCrawlingConfig.setUpdatedBy(username);
        fileCrawlingConfig.setUpdatedTime(timestamp);
        FessBeans.copy(fileCrawlingConfigForm, fileCrawlingConfig)
                .excludesCommonColumns().execute();

        return fileCrawlingConfig;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (fileCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            fileCrawlingConfigForm.crudMode });
        }

        try {
            final FileCrawlingConfig fileCrawlingConfig = fileCrawlingConfigService
                    .getFileCrawlingConfig(createKeyMap());
            if (fileCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { fileCrawlingConfigForm.id });
            }

            failureUrlService
                    .deleteByConfigId(fileCrawlingConfig.getConfigId());

            //fileCrawlingConfigService.delete(fileCrawlingConfig);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            fileCrawlingConfig.setDeletedBy(username);
            fileCrawlingConfig.setDeletedTime(timestamp);
            fileCrawlingConfigService.store(fileCrawlingConfig);
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

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    public List<LabelType> getLabelTypeItems() {
        return labelTypeService.getLabelTypeList();
    }

}
