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

package org.codelibs.fess.app.web.admin.roletype;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.RoleTypePager;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.RoleType;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminRoletypeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RoleTypeService roleTypeService;
    @Resource
    private RoleTypePager roleTypePager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("roleType"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final RoleTypeSearchForm form) {
        return asHtml(path_AdminRoletype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final RoleTypeSearchForm form) {
        roleTypePager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminRoletype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final RoleTypeSearchForm form) {
        copyBeanToBean(form.searchParams, roleTypePager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminRoletype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final RoleTypeSearchForm form) {
        roleTypePager.clear();
        return asHtml(path_AdminRoletype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final RoleTypeSearchForm form) {
        return asHtml(path_AdminRoletype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final RoleTypeSearchForm form) {
        data.register("roleTypeItems", roleTypeService.getRoleTypeList(roleTypePager)); // page navi

        // restore from pager
        copyBeanToBean(roleTypePager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final RoleTypeEditForm form) {
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminRoletype_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final RoleTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadRoleType(form);
        return asHtml(path_AdminRoletype_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final RoleTypeEditForm form) {
        return asHtml(path_AdminRoletype_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final RoleTypeEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadRoleType(form);
        return asHtml(path_AdminRoletype_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final RoleTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadRoleType(form);
        return asHtml(path_AdminRoletype_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final RoleTypeEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadRoleType(form);
        return asHtml(path_AdminRoletype_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final RoleTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadRoleType(form);
        return asHtml(path_AdminRoletype_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final RoleTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRoletype_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final RoleTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRoletype_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final RoleTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        roleTypeService.store(createRoleType(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final RoleTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        roleTypeService.store(createRoleType(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final RoleTypeEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        roleTypeService.delete(getRoleType(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadRoleType(final RoleTypeEditForm form) {
        copyBeanToBean(getRoleType(form), form, op -> op.exclude("crudMode"));
    }

    protected RoleType getRoleType(final RoleTypeEditForm form) {
        final RoleType roleType = roleTypeService.getRoleType(createKeyMap(form));
        if (roleType == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return roleType;
    }

    protected RoleType createRoleType(final RoleTypeEditForm form) {
        RoleType roleType;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            roleType = getRoleType(form);
        } else {
            roleType = new RoleType();
            roleType.setCreatedBy(username);
            roleType.setCreatedTime(currentTime);
        }
        roleType.setUpdatedBy(username);
        roleType.setUpdatedTime(currentTime);
        copyBeanToBean(form, roleType, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return roleType;
    }

    protected Map<String, String> createKeyMap(final RoleTypeEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final RoleTypeEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminRoletype_EditJsp);
        };
    }
}
