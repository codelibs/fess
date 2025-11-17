/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import jakarta.annotation.Resource;

/**
 * Admin action for Search Log.
 *
 */
public class AdminSearchlogAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminSearchlogAction() {
        super();
    }

    /** Role name for admin search log operations */
    public static final String ROLE = "admin-searchlog";

    private static final String[] CONDITION_FIELDS =
            { "logType", "queryId", "userSessionId", "accessType", "requestedTimeRange", "pageSize" };

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing search log data */
    @Resource
    private SearchLogService searchLogService;
    /** Pager for paginating search log results */
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
    /**
     * Displays the search log management index page.
     *
     * @return HTML response for the search log list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    /**
     * Displays a paginated list of search log entries.
     *
     * @param pageNumber the page number to display
     * @param form the search form containing filter criteria
     * @return HTML response with the search log list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        saveToken();
        searchLogPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches for search log entries based on the provided search criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with filtered search log results
     */
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

    /**
     * Resets the search criteria and displays all search log entries.
     *
     * @param form the search form to reset
     * @return HTML response with the reset search log list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        saveToken();
        searchLogPager.clear();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Returns to the search log list from a detail view.
     *
     * @param form the search form containing current state
     * @return HTML response for the search log list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        saveToken();
        return asHtml(path_AdminSearchlog_AdminSearchlogJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up search paging data for rendering the search log list.
     *
     * @param data the render data to populate
     * @param form the search form containing current search criteria
     */
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
    /**
     * Displays the details of a specific search log entry.
     *
     * @param crudMode the CRUD mode for the operation
     * @param logType the type of log entry
     * @param id the ID of the search log entry to display
     * @return HTML response for the search log details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String logType, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, this::asListHtml);
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
    /**
     * Deletes a specific search log entry.
     *
     * @param form the edit form containing the log entry information
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, this::asListHtml);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        searchLogService.getSearchLog(form.logType, form.id).alwaysPresent(e -> {
            searchLogService.deleteSearchLog(e);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    /**
     * Deletes all search log entries.
     *
     * @return HTML response redirecting to the list page after deletion
     */
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
