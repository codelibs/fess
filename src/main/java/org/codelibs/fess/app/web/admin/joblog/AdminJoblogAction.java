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
package org.codelibs.fess.app.web.admin.joblog;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.boostdoc.SearchForm;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminJoblogAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private JobLogService jobLogService;
    @Resource
    private JobLogPager jobLogPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameJoblog()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        jobLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, jobLogPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        jobLogPager.clear();
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SearchForm form) {
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("jobLogItems", jobLogService.getJobLogList(jobLogPager)); // page navi

        // restore from pager
        copyBeanToBean(jobLogPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminJoblog_AdminJoblogDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                jobLogService.getJobLog(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        String id = form.id;
        jobLogService.getJobLog(id).alwaysPresent(entity -> {
            jobLogService.delete(entity);
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
            }, () -> asListHtml());
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            data.register("jobLogItems", jobLogService.getJobLogList(jobLogPager)); // page navi
            }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(jobLogPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminJoblog_AdminJoblogDetailsJsp);
    }
}
