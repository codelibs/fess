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

package org.codelibs.fess.app.web.admin;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.es.exentity.DataConfig;
import org.codelibs.fess.es.exentity.LabelType;
import org.codelibs.fess.es.exentity.RoleType;
import org.codelibs.fess.exception.SSCActionMessagesException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataConfigAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(DataConfigAction.class);

    // for list

    public List<DataConfig> dataConfigItems;

    // for edit/confirm/delete

    //@ActionForm
    @Resource
    protected DataConfigForm dataConfigForm;

    @Resource
    protected DataConfigService dataConfigService;

    @Resource
    protected DataConfigPager dataConfigPager;

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
        return systemHelper.getHelpLink("dataConfig");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        dataConfigItems = dataConfigService.getDataConfigList(dataConfigPager);

        // restore from pager
        BeanUtil.copyBeanToBean(dataConfigPager, dataConfigForm.searchParams,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    //@Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(dataConfigForm.pageNumber)) {
            try {
                dataConfigPager.setCurrentPageNumber(Integer.parseInt(dataConfigForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + dataConfigForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String search() {
        BeanUtil.copyBeanToBean(dataConfigForm.searchParams, dataConfigPager,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String reset() {
        dataConfigPager.clear();

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (dataConfigForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    dataConfigForm.crudMode });
        }

        loadDataConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        dataConfigForm.initialize();
        dataConfigForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (dataConfigForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE, dataConfigForm.crudMode });
        }

        loadDataConfig();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        dataConfigForm.crudMode = CommonConstants.EDIT_MODE;

        loadDataConfig();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (dataConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    dataConfigForm.crudMode });
        }

        loadDataConfig();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        dataConfigForm.crudMode = CommonConstants.DELETE_MODE;

        loadDataConfig();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final DataConfig dataConfig = createDataConfig();
            dataConfigService.store(dataConfig);
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
    //@Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final DataConfig dataConfig = createDataConfig();
            dataConfigService.store(dataConfig);
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

        keys.put("id", dataConfigForm.id);

        return keys;
    }

    protected void loadDataConfig() {

        final DataConfig dataConfig = dataConfigService.getDataConfig(createKeyMap());
        if (dataConfig == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { dataConfigForm.id });
        }

        BeanUtil.copyBeanToBean(dataConfig, dataConfigForm, option -> option.excludes("searchParams", "mode"));

        // normalize boost
        if (dataConfigForm.boost != null && dataConfigForm.boost.indexOf('.') > 0) {
            dataConfigForm.boost = dataConfigForm.boost.substring(0, dataConfigForm.boost.indexOf('.'));
        }
    }

    protected DataConfig createDataConfig() {
        DataConfig dataConfig;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (dataConfigForm.crudMode == CommonConstants.EDIT_MODE) {
            dataConfig = dataConfigService.getDataConfig(createKeyMap());
            if (dataConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { dataConfigForm.id });
            }
        } else {
            dataConfig = new DataConfig();
            dataConfig.setCreatedBy(username);
            dataConfig.setCreatedTime(currentTime);
        }
        dataConfig.setUpdatedBy(username);
        dataConfig.setUpdatedTime(currentTime);
        BeanUtil.copyBeanToBean(dataConfigForm, dataConfig, option -> option.exclude(CommonConstants.COMMON_CONVERSION_RULE));

        return dataConfig;
    }

    //@Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (dataConfigForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    dataConfigForm.crudMode });
        }

        try {
            final DataConfig dataConfig = dataConfigService.getDataConfig(createKeyMap());
            if (dataConfig == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { dataConfigForm.id });
            }

            failureUrlService.deleteByConfigId(dataConfig.getConfigId());

            dataConfigService.delete(dataConfig);
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

    public List<Map<String, String>> getHandlerNameItems() {
        final List<String> dataStoreNameList = dataStoreFactory.getDataStoreNameList();
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
