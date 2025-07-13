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
package org.codelibs.fess.app.web.admin.group;

import java.util.Base64;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.GroupPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaMessenger;

import jakarta.annotation.Resource;

/**
 * Admin action for Group management.
 *
 */
public class AdminGroupAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminGroupAction() {
        // Default constructor
    }

    /** The role name for group administration. */
    public static final String ROLE = "admin-group";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AdminGroupAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for group operations. */
    @Resource
    private GroupService groupService;

    /** Pager for group list pagination. */
    @Resource
    private GroupPager groupPager;

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
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameGroup()));
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
     * Displays the group list page.
     *
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asListHtml();
    }

    /**
     * Displays the group list with pagination.
     *
     * @param pageNumber the page number
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            groupPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            groupPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminGroup_AdminGroupJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches groups based on the form criteria.
     *
     * @param form the search form
     * @return HTML response for the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, groupPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminGroup_AdminGroupJsp).renderWith(data -> {
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
        groupPager.clear();
        return asHtml(path_AdminGroup_AdminGroupJsp).renderWith(data -> {
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
        RenderDataUtil.register(data, "groupItems", groupService.getGroupList(groupPager)); // page navi

        // restore from pager
        copyBeanToBean(groupPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the create new group page.
     *
     * @return HTML response for the create page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminGroup_AdminGroupEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Displays the edit group page.
     *
     * @param form the edit form
     * @return HTML response for the edit page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        groupService.getGroup(id).ifPresent(entity -> {
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
     * Displays the group details page.
     *
     * @param crudMode the CRUD mode
     * @param id the group ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminGroup_AdminGroupDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                groupService.getGroup(id).ifPresent(entity -> {
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
     * Creates a new group.
     *
     * @param form the create form
     * @return HTML response after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        validateAttributes(form.attributes, v -> throwValidationError(v, this::asEditHtml));
        verifyToken(this::asEditHtml);
        getGroup(form).ifPresent(entity -> {
            try {
                groupService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to add {}", entity, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Updates an existing group.
     *
     * @param form the edit form
     * @return HTML response after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        validateAttributes(form.attributes, v -> throwValidationError(v, this::asEditHtml));
        verifyToken(this::asEditHtml);
        getGroup(form).ifPresent(entity -> {
            try {
                groupService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to update {}", entity, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Deletes a group.
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
        groupService.getGroup(id).ifPresent(entity -> {
            try {
                groupService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to delete {}", entity, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asDetailsHtml);
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
     * Gets a group entity based on the form.
     *
     * @param form the create form
     * @return optional group entity
     */
    private static OptionalEntity<Group> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new Group()).map(entity -> {
                entity.setId(Base64.getUrlEncoder().encodeToString(form.name.getBytes(Constants.CHARSET_UTF_8)));
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(GroupService.class).getGroup(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets a group entity from the form with attributes.
     *
     * @param form the create form
     * @return optional group entity
     */
    public static OptionalEntity<Group> getGroup(final CreateForm form) {
        return getEntity(form).map(entity -> {
            copyMapToBean(form.attributes, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
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

    /**
     * Validates group attributes using LDAP manager.
     *
     * @param attributes the attributes to validate
     * @param throwError the error handler
     */
    public static void validateAttributes(final Map<String, String> attributes, final Consumer<VaMessenger<FessMessages>> throwError) {
        ComponentUtil.getLdapManager().validateGroupAttributes(Long.class, attributes,
                s -> throwError.accept(messages -> messages.addErrorsPropertyTypeLong("attributes." + s, "attributes." + s)));
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
        return asHtml(path_AdminGroup_AdminGroupJsp).renderWith(data -> {
            RenderDataUtil.register(data, "groupItems", groupService.getGroupList(groupPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(groupPager, form, op -> op.include("id"));
            });
        });
    }

    /**
     * Returns HTML response for the edit page.
     *
     * @return HTML response for the edit page
     */
    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminGroup_AdminGroupEditJsp);
    }

    /**
     * Returns HTML response for the details page.
     *
     * @return HTML response for the details page
     */
    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminGroup_AdminGroupDetailsJsp);
    }
}
