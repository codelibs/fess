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
package org.codelibs.fess.app.web.admin.failureurl;

import javax.annotation.Resource;

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

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminFailureurlAction extends FessAdminAction {

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
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        failureUrlPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, failureUrlPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        failureUrlPager.clear();
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse back(final SearchForm form) {
        return asHtml(path_AdminFailureurl_AdminFailureurlJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "failureUrlItems", failureUrlService.getFailureUrlList(failureUrlPager)); // page navi

        // restore from pager
        copyBeanToBean(failureUrlPager, form, op -> op.include("url", "errorCountMin", "errorCountMax", "errorName"));
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
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
