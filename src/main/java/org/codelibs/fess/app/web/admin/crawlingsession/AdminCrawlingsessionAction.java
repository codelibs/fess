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

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.CrawlingSessionPager;
import org.codelibs.fess.app.service.CrawlingSessionService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
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
    public HtmlResponse deleteall(final EditForm form) {
        validate(form, messages -> {}, toIndexHtml());
        crawlingSessionService.deleteOldSessions(jobHelper.getRunningSessionIdSet());
        saveInfo(messages -> messages.addSuccessCrawlingSessionDeleteAll(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse index(final SearchForm form) {
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        crawlingSessionPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, crawlingSessionPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        crawlingSessionPager.clear();
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SearchForm form) {
        return asHtml(path_AdminCrawlingsession_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("crawlingSessionItems", crawlingSessionService.getCrawlingSessionList(crawlingSessionPager)); // page navi

        // restore from pager
        copyBeanToBean(crawlingSessionPager, form, op -> op.include("sessionId"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final EditForm form) {
        form.crudMode = CrudMode.DELETE;
        validate(form, messages -> {}, toIndexHtml());
        String id = form.id;
        crawlingSessionService.getCrawlingSession(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toIndexHtml());
        });
        return asHtml(path_AdminCrawlingsession_ConfirmJsp).renderWith(data -> {
            data.register("crawlingSessionInfoItems", crawlingSessionService.getCrawlingSessionInfoList(id));
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.CONFIRM);
        return asHtml(path_AdminCrawlingsession_ConfirmJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                crawlingSessionService.getCrawlingSession(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toIndexHtml());
                });
            });
        }).renderWith(data -> {
            data.register("crawlingSessionInfoItems", crawlingSessionService.getCrawlingSessionInfoList(id));
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DELETE);
        validate(form, messages -> {}, toIndexHtml());
        String id = form.id;
        crawlingSessionService.getCrawlingSession(id).alwaysPresent(entity -> {
            crawlingSessionService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, toIndexHtml());
        }
    }

    protected VaErrorHook toIndexHtml() {
        return () -> {
            return asHtml(path_AdminCrawlingsession_IndexJsp);
        };
    }
}