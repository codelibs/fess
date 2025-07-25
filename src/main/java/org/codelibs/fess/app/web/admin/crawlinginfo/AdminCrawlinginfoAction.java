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
package org.codelibs.fess.app.web.admin.crawlinginfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.CrawlingInfoPager;
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for Crawling Info management.
 *
 */
public class AdminCrawlinginfoAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminCrawlinginfoAction() {
        super();
    }

    /** The role for this action. */
    public static final String ROLE = "admin-crawlinginfo";

    private static final Logger logger = LogManager.getLogger(AdminCrawlinginfoAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private CrawlingInfoService crawlingInfoService;
    @Resource
    private CrawlingInfoPager crawlingInfoPager;
    /** The process helper. */
    @Resource
    protected ProcessHelper processHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameCrawlinginfo()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Show the index page.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    /**
     * Show the list page.
     * @param pageNumber The page number.
     * @param form The search form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        saveToken();
        crawlingInfoPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Search crawling info.
     * @param form The search form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        saveToken();
        copyBeanToBean(form, crawlingInfoPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Reset the search form.
     * @param form The search form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        saveToken();
        crawlingInfoPager.clear();
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Go back to the list page.
     * @param form The search form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        saveToken();
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Search with paging.
     * @param data The render data.
     * @param form The search form.
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "crawlingInfoItems", crawlingInfoService.getCrawlingInfoList(crawlingInfoPager)); // page navi

        // restore from pager
        copyBeanToBean(crawlingInfoPager, form, op -> op.include("sessionId"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Show the details page.
     * @param crudMode The CRUD mode.
     * @param id The crawling info ID.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return crawlingInfoService.getCrawlingInfo(id)
                .map(entity -> asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoDetailsJsp).useForm(EditForm.class, op -> {
                    op.setup(form -> {
                        copyBeanToBean(entity, form, copyOp -> {
                            copyOp.excludeNull();
                        });
                        form.crudMode = crudMode;
                    });
                }).renderWith(data -> {
                    RenderDataUtil.register(data, "crawlingInfoParamItems", crawlingInfoService.getCrawlingInfoParamList(id));
                    RenderDataUtil.register(data, "running", processHelper.isProcessRunning(entity.getSessionId()));
                })).orElseGet(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                    return null;
                });
    }

    /**
     * Print a thread dump.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse threaddump(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        crawlingInfoService.getCrawlingInfo(id).ifPresent(entity -> {
            try {
                processHelper.sendCommand(entity.getSessionId(), Constants.CRAWLER_PROCESS_COMMAND_THREAD_DUMP);
                saveInfo(messages -> messages.addSuccessPrintThreadDump(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to print a thread dump.", e);
                throwValidationError(messages -> messages.addErrorsFailedToPrintThreadDump(GLOBAL), this::asListHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
        });
        return redirect(getClass());
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Delete a crawling info.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        crawlingInfoService.getCrawlingInfo(id).alwaysPresent(entity -> {
            crawlingInfoService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        });
        return redirect(getClass());
    }

    /**
     * Delete all crawling info.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse deleteall() {
        verifyToken(this::asListHtml);
        crawlingInfoService.deleteOldSessions(processHelper.getRunningSessionIdSet());
        crawlingInfoPager.clear();
        saveInfo(messages -> messages.addSuccessCrawlingInfoDeleteAll(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verify the CRUD mode.
     * @param crudMode The CRUD mode.
     * @param expectedMode The expected mode.
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
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoJsp).renderWith(data -> {
            RenderDataUtil.register(data, "crawlingInfoItems", crawlingInfoService.getCrawlingInfoList(crawlingInfoPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(crawlingInfoPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminCrawlinginfo_AdminCrawlinginfoDetailsJsp);
    }
}