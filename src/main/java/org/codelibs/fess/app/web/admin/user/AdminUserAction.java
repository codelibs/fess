/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.service.RoleService;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
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
    public HtmlResponse index(final SearchForm form) {
        clearStoredPassword();
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        clearStoredPassword();
        pageNumber.ifPresent(num -> {
            userPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            userPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        clearStoredPassword();
        copyBeanToBean(form, userPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        clearStoredPassword();
        userPager.clear();
        return asHtml(path_AdminUser_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("userItems", userService.getUserList(userPager)); // page navi
        // restore from pager
        copyBeanToBean(userPager, form, op -> op.include("id"));
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
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createnew() {
        clearStoredPassword();
        return asHtml(path_AdminUser_EditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerForms(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse edit(final EditForm form) {
        clearStoredPassword();
        HtmlNext next;
        switch (form.crudMode) {
        case CrudMode.EDIT: // back
            form.crudMode = CrudMode.DETAILS;
            next = path_AdminUser_DetailsJsp;
            break;
        default:
            form.crudMode = CrudMode.EDIT;
            next = path_AdminUser_EditJsp;
            break;
        }
        final String id = form.id;
        userService.getUser(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        resetPassword(form);
        validate(form, messages -> {}, toEditHtml());
        return asHtml(next).renderWith(data -> {
            registerForms(data);
        });
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        return asHtml(path_AdminUser_DetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                userService.getUser(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
                });
                resetPassword(form);
            });
        }).renderWith(data -> {
            registerForms(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createUser(form).ifPresent(entity -> {
            copyBeanToBean(form, entity, op -> op.exclude("crudMode", "password", "confirmPassword"));
            entity.setId(Base64.getEncoder().encodeToString(entity.getName().getBytes(Constants.CHARSET_UTF_8)));
            userService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        //verifyPassword(form);
        createUser(form).ifPresent(entity -> {
            copyBeanToBean(form, entity, op -> op.exclude("crudMode", "password", "confirmPassword"));
            entity.setId(Base64.getEncoder().encodeToString(entity.getName().getBytes(Constants.CHARSET_UTF_8)));
            userService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DELETE);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        userService.getUser(id).ifPresent(entity -> {
            userService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected OptionalEntity<User> createUser(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                final User entity = new User();
                sessionManager.getAttribute(TEMPORARY_PASSWORD, String.class).ifPresent(password -> {
                    entity.setPassword(password);
                });
                return OptionalEntity.of(entity);
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return userService.getUser(((EditForm) form).id).map(entity -> {
                    sessionManager.getAttribute(TEMPORARY_PASSWORD, String.class).ifPresent(password -> {
                        entity.setPassword(password);
                    });
                    return entity;
                });
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============

    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, toEditHtml());
        }
    }

    protected void verifyPassword(final CreateForm form) {
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

    protected void verifyStoredPassword(final CreateForm form) {
        if (!sessionManager.getAttribute(TEMPORARY_PASSWORD, String.class).isPresent()) {
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword(GLOBAL);
            }, toEditHtml());
        }
    }

    private void clearStoredPassword() {
        sessionManager.removeAttribute(TEMPORARY_PASSWORD);
    }

    private void storePassword(final CreateForm form) {
        final String encodedPassword = fessLoginAssist.encryptPassword(form.password);
        sessionManager.setAttribute(TEMPORARY_PASSWORD, encodedPassword);
        form.password = null;
        form.confirmPassword = null;
    }

    private void resetPassword(final CreateForm form) {
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
