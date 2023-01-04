/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminJoblogAction extends FessAdminAction {

    public static final String ROLE = "admin-joblog";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private JobLogService jobLogService;
    @Resource
    private JobLogPager jobLogPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameJoblog()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        saveToken();
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        saveToken();
        jobLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        saveToken();
        copyBeanToBean(form, jobLogPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        saveToken();
        jobLogPager.clear();
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        saveToken();
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "jobLogItems", jobLogService.getJobLogList(jobLogPager)); // page navi

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
    @Secured({ ROLE, ROLE + VIEW })
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
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        jobLogService.getJobLog(id).alwaysPresent(entity -> {
            jobLogService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse deleteall() {
        verifyToken(this::asListHtml);
        final List<String> jobStatusList = new ArrayList<>();
        jobStatusList.add(Constants.OK);
        jobStatusList.add(Constants.FAIL);
        jobLogService.deleteByJobStatus(jobStatusList);
        jobLogPager.clear();
        saveInfo(messages -> messages.addSuccessJobLogDeleteAll(GLOBAL));
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
            }, this::asListHtml);
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminJoblog_AdminJoblogJsp).renderWith(data -> {
            RenderDataUtil.register(data, "jobLogItems", jobLogService.getJobLogList(jobLogPager)); // page navi
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
