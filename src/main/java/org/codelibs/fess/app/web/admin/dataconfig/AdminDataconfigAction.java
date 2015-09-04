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

package org.codelibs.fess.app.web.admin.dataconfig;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.DataConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author codelibs
 * @author jflute
 */
public class AdminDataconfigAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DataConfigService dataConfigService;
    @Resource
    private DataConfigPager dataConfigPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected RoleTypeService roleTypeService;
    @Resource
    protected LabelTypeService labelTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("dataConfig"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final DataConfigSearchForm form) {
        return asHtml(path_AdminDataconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final DataConfigSearchForm form) {
        dataConfigPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminDataconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final DataConfigSearchForm form) {
        copyBeanToBean(form.searchParams, dataConfigPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDataconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final DataConfigSearchForm form) {
        dataConfigPager.clear();
        return asHtml(path_AdminDataconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final DataConfigSearchForm form) {
        return asHtml(path_AdminDataconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final DataConfigSearchForm form) {
        data.register("dataConfigItems", dataConfigService.getDataConfigList(dataConfigPager)); // page navi

        // restore from pager
        copyBeanToBean(dataConfigPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final DataConfigEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminDataconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final DataConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadDataConfig(form);
        return asHtml(path_AdminDataconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final DataConfigEditForm form) {
        return asHtml(path_AdminDataconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final DataConfigEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadDataConfig(form);
        return asHtml(path_AdminDataconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final DataConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadDataConfig(form);
        return asHtml(path_AdminDataconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final DataConfigEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadDataConfig(form);
        return asHtml(path_AdminDataconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final DataConfigEditForm form) {
        try {
            form.crudMode = crudMode;
            form.id = id;
            verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
            loadDataConfig(form);
            return asHtml(path_AdminDataconfig_ConfirmJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        } catch (final Exception e) {
            e.printStackTrace();
            return asHtml(path_AdminDataconfig_ConfirmJsp);
        }
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final DataConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDataconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final DataConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDataconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final DataConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        dataConfigService.store(createDataConfig(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final DataConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        dataConfigService.store(createDataConfig(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final DataConfigEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        dataConfigService.delete(getDataConfig(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadDataConfig(final DataConfigEditForm form) {
        copyBeanToBean(getDataConfig(form), form, op -> op.exclude("crudMode"));
    }

    protected DataConfig getDataConfig(final DataConfigEditForm form) {
        final DataConfig dataConfig = dataConfigService.getDataConfig(createKeyMap(form));
        if (dataConfig == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return dataConfig;
    }

    protected DataConfig createDataConfig(final DataConfigEditForm form) {
        DataConfig dataConfig;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            dataConfig = getDataConfig(form);
        } else {
            dataConfig = new DataConfig();
            dataConfig.setCreatedBy(username);
            dataConfig.setCreatedTime(currentTime);
        }
        dataConfig.setUpdatedBy(username);
        dataConfig.setUpdatedTime(currentTime);
        copyBeanToBean(form, dataConfig, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return dataConfig;
    }

    protected Map<String, String> createKeyMap(final DataConfigEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    protected void registerRolesAndLabels(final RenderData data) {
        data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        data.register("labelTypeItems", labelTypeService.getLabelTypeList());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final DataConfigEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminDataconfig_EditJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        };
    }
}
