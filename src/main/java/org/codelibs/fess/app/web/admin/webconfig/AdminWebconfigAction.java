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
package org.codelibs.fess.app.web.admin.webconfig;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
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
 * Admin action for Web Config management.
 *
 */
public class AdminWebconfigAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminWebconfigAction() {
        super();
    }

    /** Role name for admin web config operations */
    public static final String ROLE = "admin-webconfig";

    private static final Logger logger = LogManager.getLogger(AdminWebconfigAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private WebConfigService webConfigService;
    @Resource
    private ScheduledJobService scheduledJobService;
    @Resource
    private WebConfigPager webConfigPager;
    @Resource
    private RoleTypeService roleTypeService;
    @Resource
    private LabelTypeService labelTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameWebconfig()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the web config management index page.
     *
     * @param form the search form for filtering
     * @return HTML response for the web config list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays a paginated list of web crawler configurations.
     *
     * @param pageNumber the page number to display (optional)
     * @param form the search form containing filter criteria
     * @return HTML response with the web config list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            webConfigPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            webConfigPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminWebconfig_AdminWebconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches for web crawler configurations based on the provided search criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with filtered web config results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, webConfigPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebconfig_AdminWebconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays all web crawler configurations.
     *
     * @param form the search form to reset
     * @return HTML response with the reset web config list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        webConfigPager.clear();
        return asHtml(path_AdminWebconfig_AdminWebconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up pagination data for the web config search results.
     * Registers web config items and restores search criteria from the pager.
     *
     * @param data the render data to populate with search results
     * @param form the search form containing filter criteria
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "webConfigItems", webConfigService.getWebConfigList(webConfigPager)); // page navi

        // restore from webConfigPager
        copyBeanToBean(webConfigPager, form, op -> op.include("id", "name", "urls", "description"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the form for creating a new web crawler configuration.
     *
     * @return HTML response for the web config creation form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asEditHtml().useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                ComponentUtil.getCrawlingConfigHelper().getDefaultConfig(ConfigType.WEB).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                        copyOp.exclude(Stream.concat(Stream.of(Constants.COMMON_CONVERSION_RULE),
                                Stream.of(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS)).toArray(n -> new String[n]));
                    });
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                            .filter(StringUtil::isNotBlank)
                            .distinct()
                            .collect(Collectors.joining("\n")));
                    form.virtualHosts = stream(entity.getVirtualHosts())
                            .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
                    form.name = null;
                });
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Displays the form for editing an existing web crawler configuration.
     *
     * @param form the edit form containing web config ID
     * @return HTML response for the web config edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        webConfigService.getWebConfig(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, copyOp -> {
                copyOp.excludeNull();
                copyOp.exclude(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS);
            });
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                    .filter(StringUtil::isNotBlank)
                    .distinct()
                    .collect(Collectors.joining("\n")));
            form.virtualHosts = stream(entity.getVirtualHosts())
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
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
     * Displays the details of a web crawler configuration.
     *
     * @param crudMode the CRUD mode for the operation
     * @param id the ID of the web config to display
     * @return HTML response for the web config details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, this::asListHtml);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                webConfigService.getWebConfig(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                        copyOp.exclude(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS);
                    });
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                            .filter(StringUtil::isNotBlank)
                            .distinct()
                            .collect(Collectors.joining("\n")));
                    form.virtualHosts = stream(entity.getVirtualHosts())
                            .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
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
     * Creates a new web crawler configuration.
     *
     * @param form the create form containing the new web config data
     * @return HTML response redirecting to the list page after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, this::asListHtml);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getWebConfig(form).ifPresent(entity -> {
            try {
                webConfigService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                logger.info("Created web config: {}", entity.getName());
            } catch (final Exception e) {
                logger.warn("Failed to create web config: {}", form.name, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Updates an existing web crawler configuration.
     *
     * @param form the edit form containing the updated web config data
     * @return HTML response redirecting to the list page after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, this::asListHtml);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getWebConfig(form).ifPresent(entity -> {
            try {
                webConfigService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                logger.info("Updated web config: {}", entity.getName());
            } catch (final Exception e) {
                logger.warn("Failed to update web config: {}", form.name, e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Deletes a web crawler configuration.
     *
     * @param form the edit form containing the ID of the web config to delete
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, this::asListHtml);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        webConfigService.getWebConfig(id).ifPresent(entity -> {
            try {
                webConfigService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                logger.info("Deleted web config: {}", entity.getName());
            } catch (final Exception e) {
                logger.warn("Failed to delete web config: {}", form.name, e);
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
     * Retrieves or creates a WebConfig entity based on the form's CRUD mode.
     *
     * @param form the form containing the web config data
     * @param username the username of the current user
     * @param currentTime the current timestamp
     * @return an optional WebConfig entity
     */
    public static OptionalEntity<WebConfig> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new WebConfig()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(WebConfigService.class).getWebConfig(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Converts a form to a WebConfig entity with proper user and timestamp information.
     * Also processes permissions and virtual hosts from form fields.
     *
     * @param form the form containing the web config data
     * @return an optional WebConfig entity with updated metadata
     */
    public static OptionalEntity<WebConfig> getWebConfig(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity,
                    op -> op.exclude(Stream
                            .concat(Stream.of(Constants.COMMON_CONVERSION_RULE), Stream.of(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS))
                            .toArray(n -> new String[n])));
            entity.setPermissions(encodePermissions(form.permissions));
            entity.setVirtualHosts(split(form.virtualHosts, "\n")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).distinct().map(String::trim).toArray(n -> new String[n])));
            return entity;
        });
    }

    /**
     * Registers available roles and labels for use in web config forms.
     * Includes role types, label types, and label setting configuration.
     *
     * @param data the render data to register the roles and labels with
     */
    protected void registerRolesAndLabels(final RenderData data) {
        RenderDataUtil.register(data, "labelSettingEnabled", fessConfig.isFormAdminLabelInConfigEnabled());
        RenderDataUtil.register(data, "roleTypeItems", roleTypeService.getRoleTypeList());
        RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminWebconfig_AdminWebconfigJsp).renderWith(data -> {
            RenderDataUtil.register(data, "webConfigItems", webConfigService.getWebConfigList(webConfigPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(webConfigPager, form, op -> op.include("id", "name", "urls", "description"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminWebconfig_AdminWebconfigEditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminWebconfig_AdminWebconfigDetailsJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }
}
