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
package org.codelibs.fess.app.web.admin.webauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.WebAuthPager;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.WebAuthentication;
import org.codelibs.fess.es.config.exentity.WebConfig;
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
 * @author Shunji Makino
 */
public class AdminWebauthAction extends FessAdminAction {

    public static final String ROLE = "admin-webauth";

    private static final Logger logger = LogManager.getLogger(AdminWebauthAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private WebAuthenticationService webAuthenticationService;
    @Resource
    private WebAuthPager webAuthPager;
    @Resource
    protected WebConfigService webConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameWebauth()));
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
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            webAuthPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            webAuthPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, webAuthPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        webAuthPager.clear();
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "webAuthenticationItems", webAuthenticationService.getWebAuthenticationList(webAuthPager)); // page navi
        RenderDataUtil.register(data, "displayCreateLink", !crawlingConfigHelper.getAllWebConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(webAuthPager, form, op -> op.include("id"));
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
        return asHtml(path_AdminWebauth_AdminWebauthEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
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
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminWebauth_AdminWebauthDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                });
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
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
        verifyToken(this::asEditHtml);
        getWebAuthentication(form).ifPresent(entity -> {
            try {
                webAuthenticationService.store(entity);
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
        getWebAuthentication(form).ifPresent(entity -> {
            try {
                webAuthenticationService.store(entity);
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
        webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
            try {
                webAuthenticationService.delete(entity);
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

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    public static OptionalEntity<WebAuthentication> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new WebAuthentication()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(WebAuthenticationService.class).getWebAuthentication(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    public static OptionalEntity<WebAuthentication> getWebAuthentication(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    protected void registerProtocolSchemeItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.webauth_scheme_basic"), Constants.BASIC));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.webauth_scheme_digest"), Constants.DIGEST));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.webauth_scheme_ntlm"), Constants.NTLM));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.webauth_scheme_form"), Constants.FORM));
        RenderDataUtil.register(data, "protocolSchemeItems", itemList);
    }

    protected void registerWebConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final List<WebConfig> webConfigList = crawlingConfigHelper.getAllWebConfigList(false, false, false, null);
        for (final WebConfig webConfig : webConfigList) {
            itemList.add(createItem(webConfig.getName(), webConfig.getId().toString()));
        }
        RenderDataUtil.register(data, "webConfigItems", itemList);
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

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            RenderDataUtil.register(data, "webAuthenticationItems", webAuthenticationService.getWebAuthenticationList(webAuthPager)); // page navi
            RenderDataUtil.register(data, "displayCreateLink",
                    !crawlingConfigHelper.getAllWebConfigList(false, false, false, null).isEmpty());
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(webAuthPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminWebauth_AdminWebauthEditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminWebauth_AdminWebauthDetailsJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }
}
