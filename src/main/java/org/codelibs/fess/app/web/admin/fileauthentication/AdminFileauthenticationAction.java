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

package org.codelibs.fess.app.web.admin.fileauthentication;

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
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.FileAuthentication;
import org.codelibs.fess.es.exentity.FileConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author Keiichi Watanabe
 */
public class AdminFileauthenticationAction extends FessAdminAction {

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
        runtime.registerData("helpLink", systemHelper.getHelpLink("fileAuthentication"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final FileAuthenticationSearchForm form) {
        return asHtml(path_AdminFileauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final FileAuthenticationSearchForm form) {
        fileAuthenticationPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminFileauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final FileAuthenticationSearchForm form) {
        copyBeanToBean(form.searchParams, fileAuthenticationPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFileauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final FileAuthenticationSearchForm form) {
        fileAuthenticationPager.clear();
        return asHtml(path_AdminFileauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final FileAuthenticationSearchForm form) {
        return asHtml(path_AdminFileauthentication_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final FileAuthenticationSearchForm form) {
        data.register("fileAuthenticationItems", fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager)); // page navi
        data.register("displayCreateLink", !fileConfigService.getAllFileConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(fileAuthenticationPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final FileAuthenticationEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminFileauthentication_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final int crudMode, final String id, final FileAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadFileAuthentication(form);
        return asHtml(path_AdminFileauthentication_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final FileAuthenticationEditForm form) {
        return asHtml(path_AdminFileauthentication_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final FileAuthenticationEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadFileAuthentication(form);
        return asHtml(path_AdminFileauthentication_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final int crudMode, final String id, final FileAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadFileAuthentication(form);
        return asHtml(path_AdminFileauthentication_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final FileAuthenticationEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadFileAuthentication(form);
        return asHtml(path_AdminFileauthentication_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final FileAuthenticationEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadFileAuthentication(form);
        return asHtml(path_AdminFileauthentication_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final FileAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminFileauthentication_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final FileAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminFileauthentication_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final FileAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        fileAuthenticationService.store(createFileAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final FileAuthenticationEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        fileAuthenticationService.store(createFileAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final FileAuthenticationEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        fileAuthenticationService.delete(getFileAuthentication(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadFileAuthentication(final FileAuthenticationEditForm form) {
        copyBeanToBean(getFileAuthentication(form), form, op -> op.exclude("crudMode"));
    }

    protected FileAuthentication getFileAuthentication(final FileAuthenticationEditForm form) {
        final FileAuthentication fileAuthentication = fileAuthenticationService.getFileAuthentication(createKeyMap(form));
        if (fileAuthentication == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return fileAuthentication;
    }

    protected FileAuthentication createFileAuthentication(final FileAuthenticationEditForm form) {
        FileAuthentication fileAuthentication;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            fileAuthentication = getFileAuthentication(form);
        } else {
            fileAuthentication = new FileAuthentication();
            fileAuthentication.setCreatedBy(username);
            fileAuthentication.setCreatedTime(currentTime);
        }
        fileAuthentication.setUpdatedBy(username);
        fileAuthentication.setUpdatedTime(currentTime);
        copyBeanToBean(form, fileAuthentication, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return fileAuthentication;
    }

    protected Map<String, String> createKeyMap(final FileAuthenticationEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
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
    protected void verifyCrudMode(final FileAuthenticationEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminFileauthentication_EditJsp);
        };
    }
}
