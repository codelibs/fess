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
package org.codelibs.fess.app.web.admin.keymatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.KeyMatchPager;
import org.codelibs.fess.app.service.KeyMatchService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.KeyMatchHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.KeyMatch;
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
 * Admin action for Key Match management.
 *
 */
public class AdminKeymatchAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminKeymatchAction() {
        // Default constructor
    }

    /** The role name for key match administration. */
    public static final String ROLE = "admin-keymatch";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AdminKeymatchAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Helper for key match operations. */
    @Resource
    private KeyMatchHelper keyMatchHelper;

    /** Service for key match operations. */
    @Resource
    private KeyMatchService keyMatchService;

    /** Pager for key match list pagination. */
    @Resource
    private KeyMatchPager keyMatchPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    /**
     * Sets up HTML data for rendering, including help link.
     *
     * @param runtime the action runtime
     */
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameKeymatch()));
    }

    /**
     * Returns the action role for this admin action.
     *
     * @return the role name
     */
    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the key match list page.
     *
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays the key match list with pagination.
     *
     * @param pageNumber the page number
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            keyMatchPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            keyMatchPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminKeymatch_AdminKeymatchJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches key matches based on the form criteria.
     *
     * @param form the search form
     * @return HTML response for the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, keyMatchPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminKeymatch_AdminKeymatchJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays the default list.
     *
     * @param form the search form
     * @return HTML response for the reset list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        keyMatchPager.clear();
        return asHtml(path_AdminKeymatch_AdminKeymatchJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up data for search result pagination.
     *
     * @param data the render data
     * @param form the search form
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "keyMatchItems", keyMatchService.getKeyMatchList(keyMatchPager)); // page navi

        // restore from pager
        copyBeanToBean(keyMatchPager, form, op -> op.include("term", "query"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the create new key match page.
     *
     * @return HTML response for the create page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminKeymatch_AdminKeymatchEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Displays the edit key match page.
     *
     * @param form the edit form
     * @return HTML response for the edit page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        keyMatchService.getKeyMatch(id).ifPresent(entity -> {
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
     * Displays the key match details page with boosted documents.
     *
     * @param crudMode the CRUD mode
     * @param id the key match ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        final List<Map<String, Object>> docList = new ArrayList<>();
        return asHtml(path_AdminKeymatch_AdminKeymatchDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                keyMatchService.getKeyMatch(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                    docList.addAll(keyMatchHelper.getBoostedDocumentList(entity));
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                });
            });
        }).renderWith(data -> {
            data.register("docs", docList);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Creates a new key match.
     *
     * @param form the create form
     * @return HTML response after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getKeyMatch(form).ifPresent(entity -> {
            try {
                keyMatchService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                ComponentUtil.getKeyMatchHelper().update();
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
     * Updates an existing key match.
     *
     * @param form the edit form
     * @return HTML response after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getKeyMatch(form).ifPresent(entity -> {
            try {
                keyMatchService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                ComponentUtil.getKeyMatchHelper().update();
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
     * Deletes a key match.
     *
     * @param form the edit form
     * @return HTML response after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        keyMatchService.getKeyMatch(id).ifPresent(entity -> {
            try {
                keyMatchService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                ComponentUtil.getKeyMatchHelper().update();
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
     * Gets a key match entity based on the form and current user info.
     *
     * @param form the create form
     * @param username the current username
     * @param currentTime the current time
     * @return optional key match entity
     */
    public static OptionalEntity<KeyMatch> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new KeyMatch()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(KeyMatchService.class).getKeyMatch(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets a key match entity from the form with system info.
     *
     * @param form the create form
     * @return optional key match entity
     */
    public static OptionalEntity<KeyMatch> getKeyMatch(final CreateForm form) {
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

    /**
     * Returns HTML response for the list page.
     *
     * @return HTML response for the list page
     */
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminKeymatch_AdminKeymatchJsp).renderWith(data -> {
            RenderDataUtil.register(data, "keyMatchItems", keyMatchService.getKeyMatchList(keyMatchPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(keyMatchPager, form, op -> op.include("term", "query"));
            });
        });
    }

    /**
     * Returns HTML response for the edit page.
     *
     * @return HTML response for the edit page
     */
    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminKeymatch_AdminKeymatchEditJsp);
    }

    /**
     * Returns HTML response for the details page.
     *
     * @return HTML response for the details page
     */
    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminKeymatch_AdminKeymatchDetailsJsp);
    }
}
