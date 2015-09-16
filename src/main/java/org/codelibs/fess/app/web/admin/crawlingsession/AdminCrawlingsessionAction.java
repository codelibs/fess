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

package org.codelibs.fess.app.web.admin.crawlingsession;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.CrawlingSessionPager;
import org.codelibs.fess.app.service.CrawlingSessionService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.CrawlingSession;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminCrawlingsessionAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private CrawlingSessionService crawlingSessionService;
    @Resource
    private CrawlingSessionPager crawlingSessionPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected JobHelper jobHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("crawlingSession"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse deleteall(final CrawlingSessionEditForm form) {
        crawlingSessionService.deleteOldSessions(jobHelper.getRunningSessionIdSet());
        saveInfo(messages -> messages.addSuccessCrawlingSessionDeleteAll(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse index(final CrawlingSessionSearchForm form) {
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final CrawlingSessionSearchForm form) {
        crawlingSessionPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final CrawlingSessionSearchForm form) {
        copyBeanToBean(form.searchParams, crawlingSessionPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final CrawlingSessionSearchForm form) {
        crawlingSessionPager.clear();
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final CrawlingSessionSearchForm form) {
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final CrawlingSessionSearchForm form) {
        data.register("crawlingSessionItems", crawlingSessionService.getCrawlingSessionList(crawlingSessionPager)); // page navi

        // restore from pager
        copyBeanToBean(crawlingSessionPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final CrawlingSessionEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadCrawlingSession(form);
        return asHtml(path_AdminCrawlingsession_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final CrawlingSessionEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadCrawlingSession(form);
        return asHtml(path_AdminCrawlingsession_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final CrawlingSessionEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadCrawlingSession(form);
        return asHtml(path_AdminCrawlingsession_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse delete(final CrawlingSessionEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        crawlingSessionService.delete(getCrawlingSession(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadCrawlingSession(final CrawlingSessionEditForm form) {
        copyBeanToBean(getCrawlingSession(form), form, op -> op.exclude("crudMode"));
    }

    protected CrawlingSession getCrawlingSession(final CrawlingSessionEditForm form) {
        final CrawlingSession crawlingSession = crawlingSessionService.getCrawlingSession(createKeyMap(form));
        return crawlingSession;
    }

    protected Map<String, String> createKeyMap(final CrawlingSessionEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final CrawlingSessionEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toIndexHtml());
        }
    }

    protected VaErrorHook toIndexHtml() {
        return () -> {
            return asHtml(path_AdminCrawlingsession_IndexJsp);
        };
    }
}
