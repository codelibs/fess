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

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("webConfig"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(WebConfigSearchForm form) {
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(Integer pageNumber, WebConfigSearchForm form) {
        webConfigPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(WebConfigSearchForm form) {
        copyBeanToBean(form.searchParams, webConfigPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(WebConfigSearchForm form) {
        webConfigPager.clear();
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(WebConfigSearchForm form) {
        return asHtml(path_AdminWebconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(RenderData data, WebConfigSearchForm form) {
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
    public HtmlResponse createpage(WebConfigEditForm form) {
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminWebconfig_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(int crudMode, String id, WebConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(WebConfigEditForm form) {
        return asHtml(path_AdminWebconfig_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(WebConfigEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(int crudMode, String id, WebConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(WebConfigEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadWebConfig(form);
        return asHtml(path_AdminWebconfig_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(int crudMode, String id, WebConfigEditForm form) {
        try {
            form.crudMode = crudMode;
            form.id = id;
            verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
            loadWebConfig(form);
            return asHtml(path_AdminWebconfig_ConfirmJsp);
        } catch (Exception e) {
            e.printStackTrace();
            return asHtml(path_AdminWebconfig_ConfirmJsp);
        }

    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebconfig_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebconfig_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webConfigService.store(createWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(WebConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webConfigService.store(createWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(WebConfigEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        webConfigService.delete(getWebConfig(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadWebConfig(WebConfigEditForm form) {
        copyBeanToBean(getWebConfig(form), form, op -> op.exclude("crudMode"));
    }

    protected WebConfig getWebConfig(WebConfigEditForm form) {
        final WebConfig webConfig = webConfigService.getWebConfig(createKeyMap(form));
        if (webConfig == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return webConfig;
    }

    protected WebConfig createWebConfig(WebConfigEditForm form) {
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

    protected Map<String, String> createKeyMap(WebConfigEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(WebConfigEditForm form, int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminWebconfig_EditJsp);
        };
    }
}
