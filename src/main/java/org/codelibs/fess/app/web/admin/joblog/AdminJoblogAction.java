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

package org.codelibs.fess.app.web.admin.joblog;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.JobLog;
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
        runtime.registerData("helpLink", systemHelper.getHelpLink("jobLog"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final JobLogSearchForm form) {
        return asHtml(path_AdminJoblog_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final JobLogSearchForm form) {
        jobLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminJoblog_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final JobLogSearchForm form) {
        copyBeanToBean(form.searchParams, jobLogPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminJoblog_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final JobLogSearchForm form) {
        jobLogPager.clear();
        return asHtml(path_AdminJoblog_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final JobLogSearchForm form) {
        return asHtml(path_AdminJoblog_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final JobLogSearchForm form) {
        data.register("jobLogItems", jobLogService.getJobLogList(jobLogPager)); // page navi

        // restore from pager
        copyBeanToBean(jobLogPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final JobLogEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadJobLog(form);
        return asHtml(path_AdminJoblog_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final JobLogEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadJobLog(form);
        return asHtml(path_AdminJoblog_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final JobLogEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadJobLog(form);
        return asHtml(path_AdminJoblog_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse delete(final JobLogEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        jobLogService.delete(getJobLog(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadJobLog(final JobLogEditForm form) {
        copyBeanToBean(getJobLog(form), form, op -> op.exclude("crudMode"));
    }

    protected JobLog getJobLog(final JobLogEditForm form) {
        final JobLog jobLog = jobLogService.getJobLog(createKeyMap(form));
        return jobLog;
    }

    protected Map<String, String> createKeyMap(final JobLogEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final JobLogEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toIndexHtml());
        }
    }

    protected VaErrorHook toIndexHtml() {
        return () -> {
            return asHtml(path_AdminJoblog_IndexJsp);
        };
    }
}
