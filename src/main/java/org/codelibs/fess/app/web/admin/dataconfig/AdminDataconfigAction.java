/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.dataconfig;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.helper.PermissionHelper;
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
public class AdminDataconfigAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DataConfigService dataConfigService;
    @Resource
    private DataConfigPager dataConfigPager;
    @Resource
    private RoleTypeService roleTypeService;
    @Resource
    private LabelTypeService labelTypeService;
    @Resource
    protected DataStoreFactory dataStoreFactory;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDataconfig()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index() {
        return asListHtml();
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            dataConfigPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            dataConfigPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDataconfig_AdminDataconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, dataConfigPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDataconfig_AdminDataconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        dataConfigPager.clear();
        return asHtml(path_AdminDataconfig_AdminDataconfigJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "dataConfigItems", dataConfigService.getDataConfigList(dataConfigPager)); // page navi

        // restore from pager
        copyBeanToBean(dataConfigPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    public HtmlResponse createnew() {
        saveToken();
        return asEditHtml().useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    @Execute
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        final String id = form.id;
        dataConfigService
                .getDataConfig(id)
                .ifPresent(
                        entity -> {
                            copyBeanToBean(entity, form, copyOp -> {
                                copyOp.excludeNull();
                                copyOp.exclude(Constants.PERMISSIONS);
                            });
                            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                            form.permissions =
                                    stream(entity.getPermissions()).get(
                                            stream -> stream.map(permissionHelper::decode).filter(StringUtil::isNotBlank).distinct()
                                                    .collect(Collectors.joining("\n")));
                        }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
                });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        } else {
            form.crudMode = CrudMode.EDIT;
            return asEditHtml();
        }
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(
                EditForm.class,
                op -> {
                    op.setup(form -> {
                        dataConfigService
                                .getDataConfig(id)
                                .ifPresent(
                                        entity -> {
                                            copyBeanToBean(entity, form, copyOp -> {
                                                copyOp.excludeNull();
                                                copyOp.exclude(Constants.PERMISSIONS);
                                            });
                                            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                                            form.permissions =
                                                    stream(entity.getPermissions()).get(
                                                            stream -> stream.map(s -> permissionHelper.decode(s))
                                                                    .filter(StringUtil::isNotBlank).distinct()
                                                                    .collect(Collectors.joining("\n")));
                                            form.crudMode = crudMode;
                                        })
                                .orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id),
                                        () -> asListHtml()));
                    });
                });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getDataConfig(form).ifPresent(
                entity -> {
                    try {
                        dataConfigService.store(entity);
                        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getDataConfig(form).ifPresent(
                entity -> {
                    try {
                        dataConfigService.store(entity);
                        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        final String id = form.id;
        dataConfigService
                .getDataConfig(id)
                .ifPresent(
                        entity -> {
                            try {
                                dataConfigService.delete(entity);
                                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                            } catch (final Exception e) {
                                throwValidationError(
                                        messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                                        () -> asEditHtml());
                            }
                        })
                .orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id),
                        () -> asDetailsHtml()));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private OptionalEntity<DataConfig> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new DataConfig()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return dataConfigService.getDataConfig(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<DataConfig> getDataConfig(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(
                entity -> {
                    entity.setUpdatedBy(username);
                    entity.setUpdatedTime(currentTime);
                    copyBeanToBean(form, entity, op -> op.exclude(Stream.concat(Stream.of(Constants.COMMON_CONVERSION_RULE),
                            Stream.of(Constants.PERMISSIONS)).toArray(n -> new String[n])));
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    entity.setPermissions(split(form.permissions, "\n").get(
                            stream -> stream.map(s -> permissionHelper.encode(s)).filter(StringUtil::isNotBlank).distinct()
                                    .toArray(n -> new String[n])));
                    return entity;
                });
    }

    protected void registerRolesAndLabels(final RenderData data) {
        RenderDataUtil.register(data, "roleTypeItems", roleTypeService.getRoleTypeList());
        RenderDataUtil.register(data, "labelTypeItems", labelTypeService.getLabelTypeList());
    }

    protected void registerHandlerNames(final RenderData data) {
        final List<String> dataStoreNameList = dataStoreFactory.getDataStoreNameList();
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final String name : dataStoreNameList) {
            final Map<String, String> map = new HashMap<>();
            map.put(Constants.ITEM_LABEL, name);
            map.put(Constants.ITEM_VALUE, name);
            itemList.add(map);
        }
        RenderDataUtil.register(data, "handlerNameItems", itemList);
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, () -> asListHtml());
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminDataconfig_AdminDataconfigJsp).renderWith(data -> {
            RenderDataUtil.register(data, "dataConfigItems", dataConfigService.getDataConfigList(dataConfigPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(dataConfigPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDataconfig_AdminDataconfigEditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
            registerHandlerNames(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDataconfig_AdminDataconfigDetailsJsp).renderWith(data -> {
            registerRolesAndLabels(data);
            registerHandlerNames(data);
        });
    }

}
