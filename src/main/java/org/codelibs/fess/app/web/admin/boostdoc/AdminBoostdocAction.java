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
package org.codelibs.fess.app.web.admin.boostdoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.BoostDocPager;
import org.codelibs.fess.app.service.BoostDocumentRuleService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for Boost Document management.
 *
 */
public class AdminBoostdocAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminBoostdocAction() {
        super();
    }

    /** The role for this action. */
    public static final String ROLE = "admin-boostdoc";

    private static final Logger logger = LogManager.getLogger(AdminBoostdocAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private BoostDocumentRuleService boostDocumentRuleService;
    @Resource
    private BoostDocPager boostDocPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameBoostdoc()));
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
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            boostDocPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            boostDocPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminBoostdoc_AdminBoostdocJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Search for boost document rules.
     * @param form The search form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, boostDocPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminBoostdoc_AdminBoostdocJsp).renderWith(data -> {
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
        boostDocPager.clear();
        return asHtml(path_AdminBoostdoc_AdminBoostdocJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Search with paging.
     * @param data The render data.
     * @param form The search form.
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "boostDocumentRuleItems", boostDocumentRuleService.getBoostDocumentRuleList(boostDocPager)); // page navi

        // restore from pager
        copyBeanToBean(boostDocPager, form, op -> op.include("urlExpr", "boostExpr"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // ----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Show the create new page.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asEditHtml().useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Show the edit page.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        boostDocumentRuleService.getBoostDocumentRule(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        }
        form.crudMode = CrudMode.EDIT;
        return asEditHtml();
    }

    // ----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Show the details page.
     * @param crudMode The CRUD mode.
     * @param id The ID of the boost document rule.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                boostDocumentRuleService.getBoostDocumentRule(id).ifPresent(entity -> {
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

    // ----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Create a new boost document rule.
     * @param form The create form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getBoostDocumentRule(form).ifPresent(entity -> {
            try {
                boostDocumentRuleService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Update a boost document rule.
     * @param form The edit form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getBoostDocumentRule(form).ifPresent(entity -> {
            try {
                boostDocumentRuleService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Delete a boost document rule.
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
        boostDocumentRuleService.getBoostDocumentRule(id).ifPresent(entity -> {
            try {
                boostDocumentRuleService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asDetailsHtml);
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    private static OptionalEntity<BoostDocumentRule> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new BoostDocumentRule()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(BoostDocumentRuleService.class).getBoostDocumentRule(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Get a boost document rule from a form.
     * @param form The create form.
     * @return An optional entity of a boost document rule.
     */
    public static OptionalEntity<BoostDocumentRule> getBoostDocumentRule(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            BeanUtil.copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

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
        return asHtml(path_AdminBoostdoc_AdminBoostdocJsp).renderWith(data -> {
            RenderDataUtil.register(data, "boostDocumentRuleItems", boostDocumentRuleService.getBoostDocumentRuleList(boostDocPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(boostDocPager, form, op -> op.include("urlExpr", "boostExpr"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminBoostdoc_AdminBoostdocEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminBoostdoc_AdminBoostdocDetailsJsp);
    }

}
