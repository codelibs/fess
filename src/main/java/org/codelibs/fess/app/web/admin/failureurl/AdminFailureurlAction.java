/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.failureurl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.FailureUrl;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminFailureurlAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FailureUrlService failureUrlService;
    @Resource
    private FailureUrlPager failureUrlPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("failureUrl"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final FailureUrlSearchForm form) {
        return asHtml(path_AdminFailureurl_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final FailureUrlSearchForm form) {
        failureUrlPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminFailureurl_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final FailureUrlSearchForm form) {
        copyBeanToBean(form.searchParams, failureUrlPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFailureurl_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final FailureUrlSearchForm form) {
        failureUrlPager.clear();
        return asHtml(path_AdminFailureurl_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final FailureUrlSearchForm form) {
        return asHtml(path_AdminFailureurl_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final FailureUrlSearchForm form) {
        data.register("failureUrlItems", failureUrlService.getFailureUrlList(failureUrlPager)); // page navi

        // restore from pager
        copyBeanToBean(failureUrlPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final FailureUrlEditForm form) {
        // TODO
        // form.crudMode = crudMode;
        // form.id = id;
        // verifyCrudMode(form, CrudMode.CONFIRM);
        loadFailureUrl(form);
        return asHtml(path_AdminFailureurl_DetailsJsp);
    }

    // -----------------------------------------------------
    //                            Actually Crud (only Delete)
    //                                         -------------

    @Execute
    public HtmlResponse delete(final FailureUrlEditForm form) {
        // TODO verifyCrudMode(form, CrudMode.DELETE);
        failureUrlService.delete(getFailureUrl(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse deleteall(final FailureUrlEditForm form) {
        failureUrlService.deleteAll(failureUrlPager);
        saveInfo(messages -> messages.addSuccessFailureUrlDeleteAll(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    protected void loadFailureUrl(final FailureUrlEditForm form) {
        copyBeanToBean(getFailureUrl(form), form, op -> op.exclude("crudMode"));
    }

    protected FailureUrl getFailureUrl(final FailureUrlEditForm form) {
        final FailureUrl failureUrl = failureUrlService.getFailureUrl(createKeyMap(form));
        if (failureUrl == null) {
            // TODO
        }
        return failureUrl;
    }

    protected Map<String, String> createKeyMap(final FailureUrlEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

}
