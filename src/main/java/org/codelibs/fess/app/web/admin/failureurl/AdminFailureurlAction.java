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
package org.codelibs.fess.app.web.admin.failureurl;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for Failure URL management.
 *
 * @author shinsuke
 */
public class AdminFailureurlAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminFailureurlAction() {
        // default constructor
    }

    /**
     * Role name for failure URL administration.
     */
    public static final String ROLE = "admin-failureurl";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FailureUrlService failureUrlService;
    @Resource
    private FailureUrlPager failureUrlPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameFailureurl()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the main failure URL management page.
     *
     * @return HTML response for the failure URL list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    /**
     * Displays the failure URL list page with pagination support.
     *
     * @param pageNumber the page number to display
     * @param form the search form containing filter criteria
     * @return HTML response for the failure URL list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        failureUrlPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Performs a search for failure URLs based on the provided criteria.
     *
     * @param form the search form containing filter criteria
     * @return HTML response for the failure URL list page with search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, failureUrlPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and pagination state.
     *
     * @param form the search form to reset
     * @return HTML response for the failure URL list page with cleared search
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        failureUrlPager.clear();
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Returns to the failure URL list page with the current search form.
     *
     * @param form the search form containing current search parameters
     * @return HTML response for the failure URL list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up pagination data and restores search form values from the pager.
     *
     * @param data the render data to populate with failure URL items
     * @param form the search form to restore values into
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "failureUrlItems", failureUrlService.getFailureUrlList(failureUrlPager)); // page navi

        // restore from pager
        copyBeanToBean(failureUrlPager, form, op -> op.include("url", "errorCountMin", "errorCountMax", "errorName"));
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Displays the details of a specific failure URL record.
     *
     * @param crudMode the CRUD operation mode (should be DETAILS)
     * @param id the ID of the failure URL record to display
     * @return HTML response for the failure URL details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        return asHtml(path_AdminFailureurl_AdminFailureurlDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                failureUrlService.getFailureUrl(id).ifPresent(entity -> {
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
    //                            Actually Crud (only Delete)
    //                                         -------------

    /**
     * Deletes a specific failure URL record.
     *
     * @param form the edit form containing the ID of the failure URL to delete
     * @return HTML response redirecting to the failure URL list page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        failureUrlService.getFailureUrl(id).alwaysPresent(entity -> {
            failureUrlService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    /**
     * Deletes all failure URL records that match the current search criteria.
     *
     * @return HTML response redirecting to the failure URL list page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse deleteall() {
        verifyToken(this::asListHtml);
        failureUrlService.deleteAll(failureUrlPager);
        failureUrlPager.clear();
        saveInfo(messages -> messages.addSuccessFailureUrlDeleteAll(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verifies that the provided CRUD mode matches the expected mode.
     *
     * @param crudMode the actual CRUD mode provided
     * @param expectedMode the expected CRUD mode
     */
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
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            RenderDataUtil.register(data, "failureUrlItems", failureUrlService.getFailureUrlList(failureUrlPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(failureUrlPager, form, op -> op.include("url", "errorCountMin", "errorCountMax", "errorName"));
            });
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminFailureurl_AdminFailureurlDetailsJsp);
    }
}
