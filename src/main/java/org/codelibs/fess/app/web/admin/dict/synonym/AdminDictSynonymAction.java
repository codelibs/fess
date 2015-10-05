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

package org.codelibs.fess.app.web.admin.dict.synonym;

import javax.annotation.Resource;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.suggestelevateword.SuggestElevateWordEditForm;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author Keiichi Watanabe
 */
public class AdminDictSynonymAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SynonymService synonymService;
    @Resource
    private SynonymPager synonymPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected DynamicProperties crawlerProperties;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("synonym"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SynonymSearchForm form) {
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SynonymSearchForm form) {
        synonymPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SynonymSearchForm form) {
        copyBeanToBean(form.searchParams, synonymPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SynonymSearchForm form) {
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SynonymSearchForm form) {
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SynonymSearchForm form) {
        // TODO
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final SynonymEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final SynonymEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        // TODO loadSynonym(form);
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final SynonymEditForm form) {
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final SynonymEditForm form) {
        form.crudMode = CrudMode.EDIT;
        // TODO loadSynonym(form);
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final SynonymEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        // TODO loadSynonym(form);
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final SynonymEditForm form) {
        form.crudMode = CrudMode.DELETE;
        // TODO loadSynonym(form);
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final SynonymEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        // TODO loadSynonym(form);
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final SynonymEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final SynonymEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse downloadpage(final SynonymSearchForm form) {
        return asHtml(path_AdminDictSynonym_DownloadJsp);
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse download(final SynonymSearchForm form) {
        // TODO Download

        return asHtml(path_AdminDictSynonym_DownloadJsp);
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse uploadpage(final SynonymUploadForm form) {
        // TODO Upload

        return asHtml(path_AdminDictSynonym_UploadJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final SynonymEditForm form) {
        // TODO
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final SynonymEditForm form) {
        // TODO
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final SynonymEditForm form) {
        // TODO
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse upload(final SynonymUploadForm form) {
        // TODO
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadSynonym(final SuggestElevateWordEditForm form) {
        // TODO
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final SynonymEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminDictSynonym_EditJsp);
        };
    }
}
