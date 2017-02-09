/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.suggest;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SuggestHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yfujita
 */
public class AdminSuggestAction extends FessAdminAction {
    private static final Logger logger = LoggerFactory.getLogger(AdminSuggestAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected SuggestHelper suggestHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSuggest()));
        runtime.registerData("totalWordsNum", suggestHelper.getAllWordsNum());
        runtime.registerData("documentWordsNum", suggestHelper.getDocumentWordsNum());
        runtime.registerData("queryWordsNum", suggestHelper.getQueryWordsNum());
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                            ========
    @Execute
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class);
    }

    @Execute
    public HtmlResponse deleteAllWords() {
        if (!suggestHelper.deleteAllWords()) {
            throwValidationError(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL),
                    () -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        }
        saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        verifyToken(() -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse deleteDocumentWords() {
        if (!suggestHelper.deleteDocumentWords()) {
            throwValidationError(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL),
                    () -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        }
        saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        verifyToken(() -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse deleteQueryWords() {
        if (!suggestHelper.deleteQueryWords()) {
            throwValidationError(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL),
                    () -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        }
        saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        verifyToken(() -> asHtml(path_AdminSuggest_AdminSuggestJsp).useForm(SuggestForm.class));
        return redirect(getClass());
    }
}
