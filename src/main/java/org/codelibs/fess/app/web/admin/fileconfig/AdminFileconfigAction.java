/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.FileConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author codelibs
 * @author jflute
 */
public class AdminFileconfigAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FileConfigService fileConfigService;
    @Resource
    private FileConfigPager fileConfigPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected RoleTypeService roleTypeService;
    @Resource
    protected LabelTypeService labelTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("fileConfig"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final FileConfigSearchForm form) {
        return asHtml(path_AdminFileconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final FileConfigSearchForm form) {
        fileConfigPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminFileconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final FileConfigSearchForm form) {
        copyBeanToBean(form.searchParams, fileConfigPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFileconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final FileConfigSearchForm form) {
        fileConfigPager.clear();
        return asHtml(path_AdminFileconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final FileConfigSearchForm form) {
        return asHtml(path_AdminFileconfig_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final FileConfigSearchForm form) {
        data.register("fileConfigItems", fileConfigService.getFileConfigList(fileConfigPager)); // page navi

        // restore from pager
        copyBeanToBean(fileConfigPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final FileConfigEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminFileconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final FileConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadFileConfig(form);
        return asHtml(path_AdminFileconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final FileConfigEditForm form) {
        return asHtml(path_AdminFileconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final FileConfigEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadFileConfig(form);
        return asHtml(path_AdminFileconfig_EditJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final FileConfigEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadFileConfig(form);
        return asHtml(path_AdminFileconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final FileConfigEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadFileConfig(form);
        return asHtml(path_AdminFileconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final FileConfigEditForm form) {
        try {
            form.crudMode = crudMode;
            form.id = id;
            verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
            loadFileConfig(form);
            return asHtml(path_AdminFileconfig_ConfirmJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        } catch (final Exception e) {
            e.printStackTrace();
            return asHtml(path_AdminFileconfig_ConfirmJsp);
        }
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final FileConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminFileconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final FileConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminFileconfig_ConfirmJsp).renderWith(data -> {
            registerRolesAndLabels(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final FileConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        fileConfigService.store(createFileConfig(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final FileConfigEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        fileConfigService.store(createFileConfig(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final FileConfigEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        fileConfigService.delete(getFileConfig(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadFileConfig(final FileConfigEditForm form) {
        copyBeanToBean(getFileConfig(form), form, op -> op.exclude("crudMode"));
    }

    protected FileConfig getFileConfig(final FileConfigEditForm form) {
        final FileConfig fileConfig = fileConfigService.getFileConfig(createKeyMap(form));
        if (fileConfig == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return fileConfig;
    }

    protected FileConfig createFileConfig(final FileConfigEditForm form) {
        FileConfig fileConfig;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            fileConfig = getFileConfig(form);
        } else {
            fileConfig = new FileConfig();
            fileConfig.setCreatedBy(username);
            fileConfig.setCreatedTime(currentTime);
        }
        fileConfig.setUpdatedBy(username);
        fileConfig.setUpdatedTime(currentTime);
        copyBeanToBean(form, fileConfig, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return fileConfig;
    }

    protected Map<String, String> createKeyMap(final FileConfigEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    protected void registerRolesAndLabels(final RenderData data) {
        data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        data.register("labelTypeItems", labelTypeService.getLabelTypeList());
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final FileConfigEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminFileconfig_EditJsp).renderWith(data -> {
                registerRolesAndLabels(data);
            });
        };
    }
}
