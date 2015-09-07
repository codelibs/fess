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

package org.codelibs.fess.app.web.admin.webconfig;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminWebconfigAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private WebConfigService webConfigService;
    @Resource
    private WebConfigPager webConfigPager;
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
        runtime.registerData("helpLink", systemHelper.getHelpLink("webConfig"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final WebConfigSearchForm form) {
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final WebConfigSearchForm form) {
        webConfigPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final WebConfigSearchForm form) {
        copyBeanToBean(form.searchParams, webConfigPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final WebConfigSearchForm form) {
        webConfigPager.clear();
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final WebConfigSearchForm form) {
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final WebConfigSearchForm form) {
        data.register("webConfigItems", webConfigService.getWebConfigList(webConfigPager)); // page navi

        // restore from pager
        copyBeanToBean(webConfigPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final WebConfigEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminWebconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final WebConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final WebConfigEditForm form) {
        return asHtml(path_AdminWebconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final WebConfigEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final WebConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final WebConfigEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final WebConfigEditForm form) {
        try {
            form.crudMode = crudMode;
            form.id = id;
            verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
            loadWebConfig(form);
            return asHtml(path_AdminWebconfig_ConfirmJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        } catch (final Exception e) {
            e.printStackTrace();
            return asHtml(path_AdminWebconfig_ConfirmJsp);
        }

    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webConfigService.store(createWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webConfigService.store(createWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final WebConfigEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        webConfigService.delete(getWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadWebConfig(final WebConfigEditForm form) {
        copyBeanToBean(getWebConfig(form), form, op -> op.exclude("crudMode"));
    }

    protected WebConfig getWebConfig(final WebConfigEditForm form) {
        final WebConfig webConfig = webConfigService.getWebConfig(createKeyMap(form));
        if (webConfig == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return webConfig;
    }

    protected WebConfig createWebConfig(final WebConfigEditForm form) {
        WebConfig webConfig;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            webConfig = getWebConfig(form);
        } else {
            webConfig = new WebConfig();
            webConfig.setCreatedBy(username);
            webConfig.setCreatedTime(currentTime);
        }
        webConfig.setUpdatedBy(username);
        webConfig.setUpdatedTime(currentTime);
        copyBeanToBean(form, webConfig, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return webConfig;
    }

    protected Map<String, String> createKeyMap(final WebConfigEditForm form) {
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
    protected void verifyCrudMode(final WebConfigEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminWebconfig_EditJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        };
    }
}
