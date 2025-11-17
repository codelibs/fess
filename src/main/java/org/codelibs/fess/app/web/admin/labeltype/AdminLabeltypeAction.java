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
package org.codelibs.fess.app.web.admin.labeltype;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.LabelTypePager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
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
 * Admin action for Label Type management.
 *
 */
public class AdminLabeltypeAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminLabeltypeAction() {
        super();
    }

    /** The role name for label type administration. */
    public static final String ROLE = "admin-labeltype";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AdminLabeltypeAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for label type operations. */
    @Resource
    private LabelTypeService labelTypeService;

    /** Pager for label type list pagination. */
    @Resource
    private LabelTypePager labelTypePager;

    /** Service for role type operations. */
    @Resource
    private RoleTypeService roleTypeService;

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
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameLabeltype()));
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
     * Displays the label type list page.
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
     * Displays the label type list with pagination.
     *
     * @param pageNumber the page number
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            labelTypePager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            labelTypePager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminLabeltype_AdminLabeltypeJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches label types based on the form criteria.
     *
     * @param form the search form
     * @return HTML response for the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, labelTypePager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminLabeltype_AdminLabeltypeJsp).renderWith(data -> {
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
        labelTypePager.clear();
        return asHtml(path_AdminLabeltype_AdminLabeltypeJsp).renderWith(data -> {
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
        RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList(labelTypePager)); // page navi

        // restore from pager
        copyBeanToBean(labelTypePager, form, op -> op.include("name", "value"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the create new label type page.
     *
     * @return HTML response for the create page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminLabeltype_AdminLabeltypeEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    /**
     * Displays the edit label type page.
     *
     * @param form the edit form
     * @return HTML response for the edit page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        labelTypeService.getLabelType(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, copyOp -> {
                copyOp.excludeNull();
                copyOp.exclude(Constants.PERMISSIONS);
            });
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                    .filter(StringUtil::isNotBlank)
                    .distinct()
                    .collect(Collectors.joining("\n")));
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
     * Displays the label type details page.
     *
     * @param crudMode the CRUD mode
     * @param id the label type ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, this::asListHtml);
        saveToken();
        return asHtml(path_AdminLabeltype_AdminLabeltypeDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                labelTypeService.getLabelType(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                        copyOp.exclude(Constants.PERMISSIONS);
                    });
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                            .filter(StringUtil::isNotBlank)
                            .distinct()
                            .collect(Collectors.joining("\n")));
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                });
            });
        }).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Creates a new label type.
     *
     * @param form the create form
     * @return HTML response after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, this::asListHtml);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getLabelType(form).ifPresent(entity -> {
            try {
                labelTypeService.store(entity);
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
     * Updates an existing label type.
     *
     * @param form the edit form
     * @return HTML response after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, this::asListHtml);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getLabelType(form).ifPresent(entity -> {
            try {
                labelTypeService.store(entity);
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
     * Deletes a label type.
     *
     * @param form the edit form
     * @return HTML response after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, this::asListHtml);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        labelTypeService.getLabelType(id).ifPresent(entity -> {
            try {
                labelTypeService.delete(entity);
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
     * Gets a label type entity based on the form and current user info.
     *
     * @param form the create form
     * @param username the current username
     * @param currentTime the current time
     * @return optional label type entity
     */
    public static OptionalEntity<LabelType> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new LabelType()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(LabelTypeService.class).getLabelType(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets a label type entity from the form with system info.
     *
     * @param form the create form
     * @return optional label type entity
     */
    public static OptionalEntity<LabelType> getLabelType(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity,
                    op -> op.exclude(Stream.concat(Stream.of(Constants.COMMON_CONVERSION_RULE), Stream.of(Constants.PERMISSIONS))
                            .toArray(n -> new String[n])));
            entity.setPermissions(encodePermissions(form.permissions));
            return entity;
        });
    }

    /**
     * Registers role type items for the dropdown list.
     *
     * @param data the render data
     */
    protected void registerRoleTypeItems(final RenderData data) {
        RenderDataUtil.register(data, "roleTypeItems", roleTypeService.getRoleTypeList());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    //                                                                              JSP
    //                                                                           =========

    /**
     * Returns HTML response for the list page.
     *
     * @return HTML response for the list page
     */
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminLabeltype_AdminLabeltypeJsp).renderWith(data -> {
            RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList(labelTypePager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(labelTypePager, form, op -> op.include("name", "value"));
            });
        });
    }

    /**
     * Returns HTML response for the edit page.
     *
     * @return HTML response for the edit page
     */
    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminLabeltype_AdminLabeltypeEditJsp).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }

    /**
     * Returns HTML response for the details page.
     *
     * @return HTML response for the details page
     */
    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminLabeltype_AdminLabeltypeDetailsJsp).renderWith(data -> {
            registerRoleTypeItems(data);
        });
    }
}
