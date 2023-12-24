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
package org.codelibs.fess.app.web.admin.user;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.service.RoleService;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.VaMessenger;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminUserAction extends FessAdminAction {

    public static final String ROLE = "admin-user";

    private static final Logger logger = LogManager.getLogger(AdminUserAction.class);

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

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUser()));
        runtime.registerData("ldapAdminEnabled", fessConfig.isLdapAdminEnabled());
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
            userPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            userPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminUser_AdminUserJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, userPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminUser_AdminUserJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        userPager.clear();
        return asHtml(path_AdminUser_AdminUserJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "userItems", userService.getUserList(userPager)); // page navi
        // restore from pager
        copyBeanToBean(userPager, form, op -> op.include("id"));
    }

    private void registerForms(final RenderData data) {
        RenderDataUtil.register(data, "roleItems", roleService.getAvailableRoleList());
        RenderDataUtil.register(data, "groupItems", groupService.getAvailableGroupList());
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
        return asHtml(path_AdminUser_AdminUserEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerForms(data);
        });
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        userService.getUser(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
        });
        resetPassword(form);
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
        return asHtml(path_AdminUser_AdminUserDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                userService.getUser(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
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
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        validateAttributes(form.attributes, v -> throwValidationError(v, this::asEditHtml));
        verifyPassword(form, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getUser(form).ifPresent(entity -> {
            try {
                userService.store(entity);
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

    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        validateAttributes(form.attributes, v -> throwValidationError(v, this::asEditHtml));
        verifyPassword(form, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getUser(form).ifPresent(entity -> {
            try {
                userService.store(entity);
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

    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        getUserBean().ifPresent(u -> {
            if (u.getFessUser() instanceof User && form.name.equals(u.getUserId())) {
                throwValidationError(messages -> messages.addErrorsCouldNotDeleteLoggedInUser(GLOBAL), this::asDetailsHtml);
            }
        });
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        userService.getUser(id).ifPresent(entity -> {
            try {
                userService.delete(entity);
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

    //===================================================================================
    //                                                                       Assist Logic
    //                                                                       ============
    private static OptionalEntity<User> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new User()).map(entity -> {
                entity.setId(Base64.getUrlEncoder().encodeToString(form.name.getBytes(Constants.CHARSET_UTF_8)));
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(UserService.class).getUser(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    public static OptionalEntity<User> getUser(final CreateForm form) {
        return getEntity(form).map(entity -> {
            copyMapToBean(form.attributes, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            copyBeanToBean(form, entity, op -> op.exclude(ArrayUtils.addAll(Constants.COMMON_CONVERSION_RULE, "password")));
            if (form.crudMode.intValue() == CrudMode.CREATE || StringUtil.isNotBlank(form.password)) {
                final String encodedPassword = ComponentUtil.getComponent(FessLoginAssist.class).encryptPassword(form.password);
                entity.setOriginalPassword(form.password);
                entity.setPassword(encodedPassword);
            }
            return entity;
        });
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<>(2);
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
            }, this::asListHtml);
        }
    }

    protected void verifyPassword(final CreateForm form, final VaErrorHook validationErrorLambda) {
        if (form.crudMode == CrudMode.CREATE && StringUtil.isBlank(form.password)) {
            resetPassword(form);
            throwValidationError(messages -> {
                messages.addErrorsBlankPassword("password");
            }, validationErrorLambda);
        }
        if (form.password != null && !form.password.equals(form.confirmPassword)) {
            form.confirmPassword = null;
            throwValidationError(messages -> {
                messages.addErrorsInvalidConfirmPassword("confirmPassword");
            }, validationErrorLambda);
        }
    }

    public static void resetPassword(final CreateForm form) {
        form.password = null;
        form.confirmPassword = null;
    }

    public static void validateAttributes(final Map<String, String> attributes, final Consumer<VaMessenger<FessMessages>> throwError) {
        ComponentUtil.getLdapManager().validateUserAttributes(Long.class, attributes,
                s -> throwError.accept(messages -> messages.addErrorsPropertyTypeLong("attributes." + s, "attributes." + s)));
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminUser_AdminUserJsp).renderWith(data -> {
            RenderDataUtil.register(data, "userItems", userService.getUserList(userPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(userPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminUser_AdminUserEditJsp).renderWith(data -> {
            registerForms(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminUser_AdminUserDetailsJsp).renderWith(data -> {
            registerForms(data);
        });
    }
}
