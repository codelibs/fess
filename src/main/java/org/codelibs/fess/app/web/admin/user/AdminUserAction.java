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

package org.codelibs.fess.app.web.admin.user;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.service.RoleService;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.User;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminUserAction extends FessAdminAction {

    private static final String TEMPORARY_PASSWORD = "fess.temporary_password";
    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private GroupService groupService;
    @Resource
    private UserPager userPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("user"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final UserSearchForm form) {
        clearStoredPassword();
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final UserSearchForm form) {
        clearStoredPassword();
        userPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final UserSearchForm form) {
        clearStoredPassword();
        copyBeanToBean(form.searchParams, userPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final UserSearchForm form) {
        clearStoredPassword();
        userPager.clear();
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final UserSearchForm form) {
        clearStoredPassword();
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final UserSearchForm form) {
        data.register("userItems", userService.getUserList(userPager)); // page navi

        // restore from pager
        copyBeanToBean(userPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    private void registerForms(final RenderData data) {
        data.register("roleItems", roleService.getAvailableRoleList());
        data.register("groupItems", groupService.getAvailableGroupList());
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final UserEditForm form) {
        clearStoredPassword();
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminUser_EditJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final UserEditForm form) {
        clearStoredPassword();
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadUser(form, false);
        return asHtml(path_AdminUser_EditJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final UserEditForm form) {
        clearStoredPassword();
        return asHtml(path_AdminUser_EditJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final UserEditForm form) {
        clearStoredPassword();
        form.crudMode = CrudMode.EDIT;
        loadUser(form, false);
        return asHtml(path_AdminUser_EditJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final UserEditForm form) {
        clearStoredPassword();
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadUser(form, false);
        return asHtml(path_AdminUser_ConfirmJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final UserEditForm form) {
        clearStoredPassword();
        form.crudMode = CrudMode.DELETE;
        loadUser(form, false);
        return asHtml(path_AdminUser_ConfirmJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final UserEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        verifyPassword(form);
        loadUser(form, false);
        return asHtml(path_AdminUser_ConfirmJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final UserEditForm form) {
        verifyPassword(form);
        storePassword(form);
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminUser_ConfirmJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final UserEditForm form) {
        verifyPassword(form);
        storePassword(form);
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminUser_ConfirmJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final UserEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        verifyPassword(form);
        userService.store(createUser(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final UserEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        verifyPassword(form);
        userService.store(createUser(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final UserEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        userService.delete(getUser(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadUser(final UserEditForm form, final boolean hasPassword) {
        copyBeanToBean(getUser(form), form, op -> op.exclude("crudMode"));
        if (!hasPassword) {
            form.password = null;
            form.confirmPassword = null;
        }
    }

    protected User getUser(final UserEditForm form) {
        final User user = userService.getUser(createKeyMap(form));
        if (user == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return user;
    }

    protected User createUser(final UserEditForm form) {
        User user;
        if (form.crudMode == CrudMode.EDIT) {
            user = getUser(form);
        } else {
            user = new User();
        }
        copyBeanToBean(form, user, op -> op.exclude("password", "confirmPassword"));
        sessionManager.getAttribute(TEMPORARY_PASSWORD, String.class).ifPresent(password -> {
            user.setPassword(password);
        });
        user.setId(Base64.getEncoder().encodeToString(user.getName().getBytes(Constants.CHARSET_UTF_8)));
        return user;
    }

    protected Map<String, String> createKeyMap(final UserEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final UserEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected void verifyPassword(final UserEditForm form) {
        if (form.crudMode == CrudMode.CREATE && StringUtil.isBlank(form.password)) {
            throwValidationError(messages -> {
                messages.addErrorsBlankPassword(GLOBAL);
            }, toEditHtml());
        }
        if (form.password != null && !form.password.equals(form.confirmPassword)) {
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, toEditHtml());
        }
    }

    protected void verifyStoredPassword(final UserEditForm form) {
        if (!sessionManager.getAttribute(TEMPORARY_PASSWORD, String.class).isPresent()) {
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, toEditHtml());
        }
    }

    private void clearStoredPassword() {
        sessionManager.removeAttribute(TEMPORARY_PASSWORD);
    }

    private void storePassword(final UserEditForm form) {
        final String encodedPassword = fessLoginAssist.encryptPassword(form.password);
        sessionManager.setAttribute(TEMPORARY_PASSWORD, encodedPassword);
        form.password = null;
        form.confirmPassword = null;
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminUser_EditJsp).renderWith(data -> {
                registerForms(data);
            });
        };
    }
}
