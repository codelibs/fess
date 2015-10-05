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

package org.codelibs.fess.app.web.admin.overlappinghost;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.OverlappingHostPager;
import org.codelibs.fess.app.service.OverlappingHostService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.OverlappingHost;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminOverlappinghostAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private OverlappingHostService overlappingHostService;
    @Resource
    private OverlappingHostPager overlappingHostPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("overlappingHost"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final OverlappingHostSearchForm form) {
        return asHtml(path_AdminOverlappinghost_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final OverlappingHostSearchForm form) {
        overlappingHostPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminOverlappinghost_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final OverlappingHostSearchForm form) {
        copyBeanToBean(form.searchParams, overlappingHostPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminOverlappinghost_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final OverlappingHostSearchForm form) {
        overlappingHostPager.clear();
        return asHtml(path_AdminOverlappinghost_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final OverlappingHostSearchForm form) {
        return asHtml(path_AdminOverlappinghost_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final OverlappingHostSearchForm form) {
        data.register("overlappingHostItems", overlappingHostService.getOverlappingHostList(overlappingHostPager)); // page navi

        // restore from pager
        copyBeanToBean(overlappingHostPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final OverlappingHostEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminOverlappinghost_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final OverlappingHostEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadOverlappingHost(form);
        return asHtml(path_AdminOverlappinghost_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final OverlappingHostEditForm form) {
        return asHtml(path_AdminOverlappinghost_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final OverlappingHostEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadOverlappingHost(form);
        return asHtml(path_AdminOverlappinghost_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final OverlappingHostEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadOverlappingHost(form);
        return asHtml(path_AdminOverlappinghost_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final OverlappingHostEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadOverlappingHost(form);
        return asHtml(path_AdminOverlappinghost_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final OverlappingHostEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadOverlappingHost(form);
        return asHtml(path_AdminOverlappinghost_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final OverlappingHostEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminOverlappinghost_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final OverlappingHostEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminOverlappinghost_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final OverlappingHostEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        overlappingHostService.store(createOverlappingHost(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final OverlappingHostEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        overlappingHostService.store(createOverlappingHost(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final OverlappingHostEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        overlappingHostService.delete(getOverlappingHost(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadOverlappingHost(final OverlappingHostEditForm form) {
        copyBeanToBean(getOverlappingHost(form), form, op -> op.exclude("crudMode"));
    }

    protected OverlappingHost getOverlappingHost(final OverlappingHostEditForm form) {
        final OverlappingHost overlappingHost = overlappingHostService.getOverlappingHost(createKeyMap(form));
        if (overlappingHost == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return overlappingHost;
    }

    protected OverlappingHost createOverlappingHost(final OverlappingHostEditForm form) {
        OverlappingHost overlappingHost;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            overlappingHost = getOverlappingHost(form);
        } else {
            overlappingHost = new OverlappingHost();
            overlappingHost.setCreatedBy(username);
            overlappingHost.setCreatedTime(currentTime);
        }
        overlappingHost.setUpdatedBy(username);
        overlappingHost.setUpdatedTime(currentTime);
        copyBeanToBean(form, overlappingHost, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return overlappingHost;
    }

    protected Map<String, String> createKeyMap(final OverlappingHostEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final OverlappingHostEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminOverlappinghost_EditJsp);
        };
    }
}
