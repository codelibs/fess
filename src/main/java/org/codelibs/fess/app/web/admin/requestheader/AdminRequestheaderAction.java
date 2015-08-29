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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.RequestHeaderPager;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.RequestHeader;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
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

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("requestHeader"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(RequestHeaderSearchForm form) {
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(Integer pageNumber, RequestHeaderSearchForm form) {
        requestHeaderPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(RequestHeaderSearchForm form) {
        copyBeanToBean(form.searchParams, requestHeaderPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(RequestHeaderSearchForm form) {
        requestHeaderPager.clear();
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(RequestHeaderSearchForm form) {
        return asHtml(path_AdminRequestheader_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(RenderData data, RequestHeaderSearchForm form) {
        data.register("requestHeaderItems", requestHeaderService.getRequestHeaderList(requestHeaderPager)); // page navi

        // restore from pager
        copyBeanToBean(requestHeaderPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(RequestHeaderEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminRequestheader_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(int crudMode, String id, RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(RequestHeaderEditForm form) {
        return asHtml(path_AdminRequestheader_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(RequestHeaderEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(int crudMode, String id, RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(RequestHeaderEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(int crudMode, String id, RequestHeaderEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadRequestHeader(form);
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminRequestheader_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        requestHeaderService.store(createRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(RequestHeaderEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        requestHeaderService.store(createRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(RequestHeaderEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        requestHeaderService.delete(getRequestHeader(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadRequestHeader(RequestHeaderEditForm form) {
        copyBeanToBean(getRequestHeader(form), form, op -> op.exclude("crudMode"));
    }

    protected RequestHeader getRequestHeader(RequestHeaderEditForm form) {
        final RequestHeader requestHeader = requestHeaderService.getRequestHeader(createKeyMap(form));
        if (requestHeader == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return requestHeader;
    }

    protected RequestHeader createRequestHeader(RequestHeaderEditForm form) {
        RequestHeader requestHeader;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            requestHeader = getRequestHeader(form);
        } else {
            requestHeader = new RequestHeader();
            requestHeader.setCreatedBy(username);
            requestHeader.setCreatedTime(currentTime);
        }
        requestHeader.setUpdatedBy(username);
        requestHeader.setUpdatedTime(currentTime);
        copyBeanToBean(form, requestHeader, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return requestHeader;
    }

    protected Map<String, String> createKeyMap(RequestHeaderEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(RequestHeaderEditForm form, int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminRequestheader_EditJsp);
        };
    }
}
