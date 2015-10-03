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

package org.codelibs.fess.app.web.admin.requestheader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.RequestHeaderPager;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.RequestHeader;
import org.codelibs.fess.es.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminRequestheaderAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RequestHeaderService requestHeaderService;
    @Resource
    private RequestHeaderPager requestHeaderPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected WebConfigService webConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("requestHeader"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final RequestHeaderSearchForm form) {
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final RequestHeaderSearchForm form) {
        requestHeaderPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final RequestHeaderSearchForm form) {
        copyBeanToBean(form.searchParams, requestHeaderPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final RequestHeaderSearchForm form) {
        requestHeaderPager.clear();
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final RequestHeaderSearchForm form) {
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final RequestHeaderSearchForm form) {
        data.register("requestHeaderItems", requestHeaderService.getRequestHeaderList(requestHeaderPager)); // page navi
        data.register("displayCreateLink", !webConfigService.getAllWebConfigList(false, false, false, null).isEmpty());

        // restore from pager
        copyBeanToBean(requestHeaderPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final RequestHeaderEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminRequestheader_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final RequestHeaderEditForm form) {
        return asHtml(path_AdminRequestheader_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final RequestHeaderEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_EditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final RequestHeaderEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        requestHeaderService.store(createRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        requestHeaderService.store(createRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final RequestHeaderEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        requestHeaderService.delete(getRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadRequestHeader(final RequestHeaderEditForm form) {
        copyBeanToBean(getRequestHeader(form), form, op -> op.exclude("crudMode"));
    }

    protected RequestHeader getRequestHeader(final RequestHeaderEditForm form) {
        final RequestHeader requestHeader = requestHeaderService.getRequestHeader(createKeyMap(form));
        if (requestHeader == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return requestHeader;
    }

    protected RequestHeader createRequestHeader(final RequestHeaderEditForm form) {
        RequestHeader requestHeader;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            requestHeader = getRequestHeader(form);
        } else {
            requestHeader = new RequestHeader();
            requestHeader.setCreatedBy(username);
            requestHeader.setCreatedTime(currentTime);
        }
        requestHeader.setUpdatedBy(username);
        requestHeader.setUpdatedTime(currentTime);
        copyBeanToBean(form, requestHeader, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return requestHeader;
    }

    protected Map<String, String> createKeyMap(final RequestHeaderEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    protected void registerProtocolSchemeItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final Locale locale = LaRequestUtil.getRequest().getLocale();
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_basic"),
                Constants.BASIC));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_digest"),
                Constants.DIGEST));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_ntlm"),
                Constants.NTLM));
        data.register("protocolSchemeItems", itemList);
    }

    protected void registerWebConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final List<WebConfig> webConfigList = webConfigService.getAllWebConfigList(false, false, false, null);
        for (final WebConfig webConfig : webConfigList) {
            itemList.add(createItem(webConfig.getName(), webConfig.getId().toString()));
        }
        data.register("webConfigItems", itemList);
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
    protected void verifyCrudMode(final RequestHeaderEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminRequestheader_EditJsp).renderWith(data -> {
                registerWebConfigItems(data);
            });
        };
    }
}
