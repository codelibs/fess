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
import jp.sf.fess.crud.action.admin.BsDataCrawlingConfigAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.ds.DataStoreFactory;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.BrowserTypeService;
import jp.sf.fess.service.FailureUrlService;
import jp.sf.fess.service.LabelTypeService;
import jp.sf.fess.service.RoleTypeService;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class DataCrawlingConfigAction extends BsDataCrawlingConfigAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(DataCrawlingConfigAction.class);

    @Resource
    protected BrowserTypeService browserTypeService;

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected LabelTypeService labelTypeService;

    @Resource
    protected DataStoreFactory dataStoreFactory;

    @Resource
    protected FailureUrlService failureUrlService;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("dataCrawlingConfig");
    }

    @Override
    protected void loadDataCrawlingConfig() {

        final DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigService
                .getDataCrawlingConfig(createKeyMap());
        if (dataCrawlingConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { dataCrawlingConfigForm.id });
        }

        FessBeans.copy(dataCrawlingConfig, dataCrawlingConfigForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();

        // normalize boost
        if (dataCrawlingConfigForm.boost != null
                && dataCrawlingConfigForm.boost.indexOf('.') > 0) {
            dataCrawlingConfigForm.boost = dataCrawlingConfigForm.boost
                    .substring(0, dataCrawlingConfigForm.boost.indexOf('.'));
        }
    }

    @Override
    protected DataCrawlingConfig createDataCrawlingConfig() {
        DataCrawlingConfig dataCrawlingConfig;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (dataCrawlingConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            dataCrawlingConfig = dataCrawlingConfigService
                    .getDataCrawlingConfig(createKeyMap());
            if (dataCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { dataCrawlingConfigForm.id });
            }
        } else {
            dataCrawlingConfig = new DataCrawlingConfig();
            dataCrawlingConfig.setCreatedBy(username);
            dataCrawlingConfig.setCreatedTime(timestamp);
        }
        dataCrawlingConfig.setUpdatedBy(username);
        dataCrawlingConfig.setUpdatedTime(timestamp);
        FessBeans.copy(dataCrawlingConfigForm, dataCrawlingConfig)
                .excludesCommonColumns().execute();

        return dataCrawlingConfig;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (dataCrawlingConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            dataCrawlingConfigForm.crudMode });
        }

        try {
            final DataCrawlingConfig dataCrawlingConfig = dataCrawlingConfigService
                    .getDataCrawlingConfig(createKeyMap());
            if (dataCrawlingConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { dataCrawlingConfigForm.id });
            }

            failureUrlService
                    .deleteByConfigId(dataCrawlingConfig.getConfigId());

            //dataCrawlingConfigService.delete(dataCrawlingConfig);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            dataCrawlingConfig.setDeletedBy(username);
            dataCrawlingConfig.setDeletedTime(timestamp);
            dataCrawlingConfigService.store(dataCrawlingConfig);
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

    public List<BrowserType> getBrowserTypeItems() {
        return browserTypeService.getBrowserTypeList();
    }

    public List<RoleType> getRoleTypeItems() {
        return roleTypeService.getRoleTypeList();
    }

    public List<LabelType> getLabelTypeItems() {
        return labelTypeService.getLabelTypeList();
    }

    public List<Map<String, String>> getHandlerNameItems() {
        final List<String> dataStoreNameList = dataStoreFactory
                .getDataStoreNameList();
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        for (final String name : dataStoreNameList) {
            final Map<String, String> map = new HashMap<String, String>();
            map.put(Constants.ITEM_LABEL, name);
            map.put(Constants.ITEM_VALUE, name);
            itemList.add(map);
        }
        return itemList;
    }
}
