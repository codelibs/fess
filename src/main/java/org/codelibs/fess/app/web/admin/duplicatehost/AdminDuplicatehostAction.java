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
package org.codelibs.fess.app.web.admin.duplicatehost;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.DuplicateHostPager;
import org.codelibs.fess.app.service.DuplicateHostService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.DuplicateHost;
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
 * Admin action for Duplicate Host management.
 *
 * @author codelibs
 */
public class AdminDuplicatehostAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminDuplicatehostAction() {
        // Default constructor
    }

    /**
     * Role name for duplicate host administration.
     */
    public static final String ROLE = "admin-duplicatehost";

    private static final Logger logger = LogManager.getLogger(AdminDuplicatehostAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DuplicateHostService duplicateHostService;
    @Resource
    private DuplicateHostPager duplicateHostPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDuplicatehost()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the initial duplicate host management page.
     *
     * @param form the search form
     * @return HTML response for the duplicate host list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays the duplicate host list with pagination.
     *
     * @param pageNumber the page number to display
     * @param form the search form
     * @return HTML response for the duplicate host list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            duplicateHostPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            duplicateHostPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Performs search for duplicate hosts based on form criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response for the duplicate host list page with search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, duplicateHostPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and pager state.
     *
     * @param form the search form
     * @return HTML response for the duplicate host list page with cleared search
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        duplicateHostPager.clear();
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up search pagination data for rendering.
     *
     * @param data the render data to populate
     * @param form the search form
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "duplicateHostItems", duplicateHostService.getDuplicateHostList(duplicateHostPager)); // page navi

        // restore from pager
        copyBeanToBean(duplicateHostPager, form, op -> op.include("regularName", "duplicateHostName"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the form for creating a new duplicate host configuration.
     *
     * @return HTML response for the duplicate host creation form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Displays the form for editing an existing duplicate host configuration.
     *
     * @param form the edit form containing the duplicate host ID
     * @return HTML response for the duplicate host edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        duplicateHostService.getDuplicateHost(id).ifPresent(entity -> {
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

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Displays the details of a duplicate host configuration.
     *
     * @param crudMode the CRUD mode
     * @param id the duplicate host ID
     * @return HTML response for the duplicate host details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                duplicateHostService.getDuplicateHost(id).ifPresent(entity -> {
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
    /**
     * Creates a new duplicate host configuration.
     *
     * @param form the creation form containing duplicate host data
     * @return HTML response redirecting to the duplicate host list
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getDuplicateHost(form).ifPresent(entity -> {
            try {
                duplicateHostService.store(entity);
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
     * Updates an existing duplicate host configuration.
     *
     * @param form the edit form containing updated duplicate host data
     * @return HTML response redirecting to the duplicate host list
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getDuplicateHost(form).ifPresent(entity -> {
            try {
                duplicateHostService.store(entity);
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
     * Deletes a duplicate host configuration.
     *
     * @param form the edit form containing the duplicate host ID to delete
     * @return HTML response redirecting to the duplicate host list
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        duplicateHostService.getDuplicateHost(id).ifPresent(entity -> {
            try {
                duplicateHostService.delete(entity);
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
    /**
     * Creates a duplicate host entity based on the form and current user context.
     *
     * @param form the form containing duplicate host data
     * @param username the current username
     * @param currentTime the current timestamp
     * @return optional duplicate host entity
     */
    public static OptionalEntity<DuplicateHost> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new DuplicateHost()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(DuplicateHostService.class).getDuplicateHost(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets a duplicate host entity from the form, setting audit fields.
     *
     * @param form the form containing duplicate host data
     * @return optional duplicate host entity with audit fields set
     */
    public static OptionalEntity<DuplicateHost> getDuplicateHost(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verifies that the CRUD mode matches the expected mode.
     *
     * @param crudMode the actual CRUD mode
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
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostJsp).renderWith(data -> {
            RenderDataUtil.register(data, "duplicateHostItems", duplicateHostService.getDuplicateHostList(duplicateHostPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(duplicateHostPager, form, op -> op.include("regularName", "duplicateHostName"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDuplicatehost_AdminDuplicatehostDetailsJsp);
    }
}
