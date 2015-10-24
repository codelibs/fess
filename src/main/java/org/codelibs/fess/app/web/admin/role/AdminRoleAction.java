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

package org.codelibs.fess.app.web.admin.role;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RolePager;
import org.codelibs.fess.app.service.RoleService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.Role;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminRoleAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RoleService roleService;
    @Resource
    private RolePager rolePager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("role"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final RoleSearchForm form) {
        return asHtml(path_AdminRole_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final RoleSearchForm form) {
        rolePager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminRole_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final RoleSearchForm form) {
        copyBeanToBean(form.searchParams, rolePager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminRole_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final RoleSearchForm form) {
        rolePager.clear();
        return asHtml(path_AdminRole_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final RoleSearchForm form) {
        return asHtml(path_AdminRole_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final RoleSearchForm form) {
        data.register("roleItems", roleService.getRoleList(rolePager)); // page navi

        // restore from pager
        copyBeanToBean(rolePager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final RoleEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminRole_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final int crudMode, final String id, final RoleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadRole(form);
        return asHtml(path_AdminRole_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final RoleEditForm form) {
        return asHtml(path_AdminRole_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final RoleEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadRole(form);
        return asHtml(path_AdminRole_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final int crudMode, final String id, final RoleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadRole(form);
        return asHtml(path_AdminRole_ConfirmJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final RoleEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadRole(form);
        return asHtml(path_AdminRole_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final RoleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadRole(form);
        return asHtml(path_AdminRole_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final RoleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRole_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final RoleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRole_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final RoleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        roleService.store(createRole(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final RoleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        roleService.store(createRole(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final RoleEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        roleService.delete(getRole(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadRole(final RoleEditForm form) {
        copyBeanToBean(getRole(form), form, op -> op.exclude("crudMode"));
    }

    protected Role getRole(final RoleEditForm form) {
        final Role role = roleService.getRole(createKeyMap(form));
        if (role == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return role;
    }

    protected Role createRole(final RoleEditForm form) {
        Role role;
        if (form.crudMode == CrudMode.EDIT) {
            role = getRole(form);
        } else {
            role = new Role();
        }
        copyBeanToBean(form, role, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        role.setId(Base64.getEncoder().encodeToString(role.getName().getBytes(Constants.CHARSET_UTF_8)));
        return role;
    }

    protected Map<String, String> createKeyMap(final RoleEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final RoleEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminRole_EditJsp);
        };
    }
}
