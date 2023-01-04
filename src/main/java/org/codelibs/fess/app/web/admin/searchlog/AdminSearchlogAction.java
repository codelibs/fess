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
package org.codelibs.fess.app.web.admin.searchlog;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.app.service.SearchLogService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author shinsuke
 */
public class AdminSearchlogAction extends FessAdminAction {

    public static final String ROLE = "admin-searchlog";

    private static final String[] CONDITION_FIELDS =
            { "logType", "queryId", "userSessionId", "accessType", "requestedTimeRange", "pageSize" };

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SearchLogService searchLogService;
    @Resource
    private SearchLogPager searchLogPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSearchlog()));
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
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        saveToken();
        searchLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        saveToken();
        searchLogPager.clear();
        copyBeanToBean(form, searchLogPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        searchLogPager.setPageSize(form.getPageSize());
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        saveToken();
        searchLogPager.clear();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        saveToken();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "searchLogItems", searchLogService.getSearchLogList(searchLogPager)); // page navi

        // restore from pager
        copyBeanToBean(searchLogPager, form, op -> op.include(CONDITION_FIELDS));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String logType, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                form.id = id;
                form.logType = logType;
                form.crudMode = crudMode;
            });
        }).renderWith(data -> {
            RenderDataUtil.register(data, "logParamItems", searchLogService.getSearchLogMap(logType, id));
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
        searchLogService.getSearchLog(form.logType, form.id).alwaysPresent(e -> {
            searchLogService.deleteSearchLog(e);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse deleteall() {
        verifyToken(this::asListHtml);
        searchLogPager.clear();
        // TODO delete logs
        saveInfo(messages -> messages.addSuccessCrawlingInfoDeleteAll(GLOBAL));
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
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            RenderDataUtil.register(data, "searchLogItems", searchLogService.getSearchLogList(searchLogPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(searchLogPager, form, op -> op.include(CONDITION_FIELDS));
            });
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminSearchlog_AdminSearchlogDetailsJsp);
    }
}
