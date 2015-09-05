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

package org.codelibs.fess.app.web.admin.webauthentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.WebAuthenticationPager;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.WebAuthentication;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminWebauthenticationAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private WebAuthenticationService webAuthenticationService;
    @Resource
    private WebAuthenticationPager webAuthenticationPager;
    @Resource
    private SystemHelper systemHelper;

    @Resource
    protected WebConfigService webConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("webAuthentication"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final WebAuthenticationSearchForm form) {
        return asHtml(path_AdminWebauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final WebAuthenticationSearchForm form) {
        webAuthenticationPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminWebauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final WebAuthenticationSearchForm form) {
        copyBeanToBean(form.searchParams, webAuthenticationPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final WebAuthenticationSearchForm form) {
        webAuthenticationPager.clear();
        return asHtml(path_AdminWebauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final WebAuthenticationSearchForm form) {
        return asHtml(path_AdminWebauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final WebAuthenticationSearchForm form) {
        data.register("webAuthenticationItems", webAuthenticationService.getWebAuthenticationList(webAuthenticationPager)); // page navi
        data.register("displayCreateLink", !webConfigService.getAllWebConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(webAuthenticationPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final WebAuthenticationEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminWebauthentication_EditJsp).renderWith(data -> {
            // data.register("webConfigItems", webConfigService.getAllWebConfigList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final WebAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadWebAuthentication(form);
        return asHtml(path_AdminWebauthentication_EditJsp).renderWith(data -> {
            // data.register("webConfigItems", webConfigService.getAllWebConfigList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final WebAuthenticationEditForm form) {
        return asHtml(path_AdminWebauthentication_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final WebAuthenticationEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadWebAuthentication(form);
        return asHtml(path_AdminWebauthentication_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final WebAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadWebAuthentication(form);
        return asHtml(path_AdminWebauthentication_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final WebAuthenticationEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadWebAuthentication(form);
        return asHtml(path_AdminWebauthentication_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final WebAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadWebAuthentication(form);
        return asHtml(path_AdminWebauthentication_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final WebAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebauthentication_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final WebAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminWebauthentication_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final WebAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webAuthenticationService.store(createWebAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final WebAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        webAuthenticationService.store(createWebAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final WebAuthenticationEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        webAuthenticationService.delete(getWebAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadWebAuthentication(final WebAuthenticationEditForm form) {
        copyBeanToBean(getWebAuthentication(form), form, op -> op.exclude("crudMode"));
    }

    protected WebAuthentication getWebAuthentication(final WebAuthenticationEditForm form) {
        final WebAuthentication webAuthentication = webAuthenticationService.getWebAuthentication(createKeyMap(form));
        if (webAuthentication == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return webAuthentication;
    }

    protected WebAuthentication createWebAuthentication(final WebAuthenticationEditForm form) {
        WebAuthentication webAuthentication;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            webAuthentication = getWebAuthentication(form);
        } else {
            webAuthentication = new WebAuthentication();
            webAuthentication.setCreatedBy(username);
            webAuthentication.setCreatedTime(currentTime);
        }
        webAuthentication.setUpdatedBy(username);
        webAuthentication.setUpdatedTime(currentTime);
        copyBeanToBean(form, webAuthentication, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return webAuthentication;
    }

    protected Map<String, String> createKeyMap(final WebAuthenticationEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final WebAuthenticationEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminWebauthentication_EditJsp);
        };
    }
}
