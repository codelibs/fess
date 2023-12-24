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
package org.codelibs.fess.app.web.admin.fileconfig;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminFileconfigAction extends FessAdminAction {

    public static final String ROLE = "admin-fileconfig";

    private static final Logger logger = LogManager.getLogger(AdminFileconfigAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FileConfigService fileConfigService;
    @Resource
    private FileConfigPager fileConfigPager;
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
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameFileconfig()));
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
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            fileConfigPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            fileConfigPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminFileconfig_AdminFileconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, fileConfigPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFileconfig_AdminFileconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        fileConfigPager.clear();
        return asHtml(path_AdminFileconfig_AdminFileconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "fileConfigItems", fileConfigService.getFileConfigList(fileConfigPager)); // page navi

        // restore from pager
        copyBeanToBean(fileConfigPager, form, op -> op.include("name", "paths", "description"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminFileconfig_AdminFileconfigEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                ComponentUtil.getCrawlingConfigHelper().getDefaultConfig(ConfigType.FILE).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                        copyOp.exclude(Stream.concat(Stream.of(Constants.COMMON_CONVERSION_RULE),
                                Stream.of(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS)).toArray(n -> new String[n]));
                    });
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                            .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
                    form.virtualHosts = stream(entity.getVirtualHosts())
                            .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
                    form.name = null;
                });
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        final String id = form.id;
        fileConfigService.getFileConfig(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, copyOp -> {
                copyOp.excludeNull();
                copyOp.exclude(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS);
            });
            form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(permissionHelper::decode)
                    .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
            form.virtualHosts = stream(entity.getVirtualHosts())
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
        }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml));
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
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminFileconfig_AdminFileconfigDetailsJsp).useForm(EditForm.class, op -> op.setup(form -> {
            fileConfigService.getFileConfig(id).ifPresent(entity -> {
                copyBeanToBean(entity, form, copyOp -> {
                    copyOp.excludeNull();
                    copyOp.exclude(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS);
                });
                final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                form.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                        .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
                form.virtualHosts = stream(entity.getVirtualHosts())
                        .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining("\n")));
                form.crudMode = crudMode;
            }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml));
        })).renderWith(this::registerRolesAndLabels);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getFileConfig(form).ifPresent(entity -> {
            try {
                fileConfigService.store(entity);
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

    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getFileConfig(form).ifPresent(entity -> {
            try {
                fileConfigService.store(entity);
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

    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        fileConfigService.getFileConfig(id).ifPresent(entity -> {
            try {
                fileConfigService.delete(entity);
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
    public static OptionalEntity<FileConfig> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new FileConfig()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(FileConfigService.class).getFileConfig(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    public static OptionalEntity<FileConfig> getFileConfig(final CreateForm form) {
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
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            entity.setPermissions(split(form.permissions, "\n").get(stream -> stream.map(s -> permissionHelper.encode(s))
                    .filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n])));
            entity.setVirtualHosts(split(form.virtualHosts, "\n")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).distinct().map(String::trim).toArray(n -> new String[n])));
            return entity;
        });
    }

    protected void registerRolesAndLabels(final RenderData data) {
        RenderDataUtil.register(data, "labelSettingEnabled", fessConfig.isFormAdminLabelInConfigEnabled());
        RenderDataUtil.register(data, "roleTypeItems", roleTypeService.getRoleTypeList());
        RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList());
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
        return asHtml(path_AdminFileconfig_AdminFileconfigJsp).renderWith(data -> {
            RenderDataUtil.register(data, "fileConfigItems", fileConfigService.getFileConfigList(fileConfigPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(fileConfigPager, form, op -> op.include("name", "paths", "description"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminFileconfig_AdminFileconfigEditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminFileconfig_AdminFileconfigDetailsJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }
}
