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
package org.codelibs.fess.app.web.admin.fileauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FileAuthenticationPager;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.boostdoc.SearchForm;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.FileAuthentication;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminFileauthAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FileAuthenticationService fileAuthenticationService;
    @Resource
    private FileAuthenticationPager fileAuthenticationPager;
    @Resource
    private SystemHelper systemHelper;

    @Resource
    protected FileConfigService fileConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameFileauth()));
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
            fileAuthenticationPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            fileAuthenticationPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, fileAuthenticationPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        fileAuthenticationPager.clear();
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("fileAuthenticationItems", fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager)); // page navi
        data.register("displayCreateLink", !fileConfigService.getAllFileConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(fileAuthenticationPager, form, op -> op.include("id"));
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
        return asHtml(path_AdminFileauth_AdminFileauthEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        final String id = form.id;
        fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml().renderWith(data -> {
                registerProtocolSchemeItems(data);
                registerFileConfigItems(data);
            });
        } else {
            form.crudMode = CrudMode.EDIT;
            return asEditHtml().renderWith(data -> {
                registerProtocolSchemeItems(data);
                registerFileConfigItems(data);
            });
        }
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
                });
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
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
        getFileAuthentication(form).ifPresent(entity -> {
            fileAuthenticationService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getFileAuthentication(form).ifPresent(entity -> {
            fileAuthenticationService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
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
        fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
            fileAuthenticationService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asDetailsHtml());
        });
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private OptionalEntity<FileAuthentication> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                return OptionalEntity.of(new FileAuthentication()).map(entity -> {
                    entity.setCreatedBy(username);
                    entity.setCreatedTime(currentTime);
                    return entity;
                });
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return fileAuthenticationService.getFileAuthentication(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<FileAuthentication> getFileAuthentication(final CreateForm form) {
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
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final Locale locale = LaRequestUtil.getRequest().getLocale();
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.file_authentication_scheme_samba"),
                Constants.SAMBA));
        data.register("protocolSchemeItems", itemList);
    }

    protected void registerFileConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final List<FileConfig> fileConfigList = fileConfigService.getAllFileConfigList(false, false, false, null);
        for (final FileConfig fileConfig : fileConfigList) {
            itemList.add(createItem(fileConfig.getName(), fileConfig.getId().toString()));
        }
        data.register("fileConfigItems", itemList);
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
            }, () -> asListHtml());
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            data.register("fileAuthenticationItems", fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager)); // page navi
                data.register("displayCreateLink", !fileConfigService.getAllFileConfigList(false, false, false, null).isEmpty());
            }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(fileAuthenticationPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthDetailsJsp);
    }
}
