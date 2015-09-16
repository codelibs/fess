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

package org.codelibs.fess.app.web.admin.pathmapping;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.PathMappingPager;
import org.codelibs.fess.app.service.PathMappingService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.PathMapping;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminPathmappingAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private PathMappingService pathMappingService;
    @Resource
    private PathMappingPager pathMappingPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("pathMapping"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final PathMappingSearchForm form) {
        return asHtml(path_AdminPathmapping_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final PathMappingSearchForm form) {
        pathMappingPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminPathmapping_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final PathMappingSearchForm form) {
        copyBeanToBean(form.searchParams, pathMappingPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminPathmapping_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final PathMappingSearchForm form) {
        pathMappingPager.clear();
        return asHtml(path_AdminPathmapping_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final PathMappingSearchForm form) {
        return asHtml(path_AdminPathmapping_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final PathMappingSearchForm form) {
        data.register("pathMappingItems", pathMappingService.getPathMappingList(pathMappingPager)); // page navi

        // restore from pager
        copyBeanToBean(pathMappingPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final PathMappingEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminPathmapping_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final PathMappingEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadPathMapping(form);
        return asHtml(path_AdminPathmapping_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final PathMappingEditForm form) {
        return asHtml(path_AdminPathmapping_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final PathMappingEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadPathMapping(form);
        return asHtml(path_AdminPathmapping_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final PathMappingEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadPathMapping(form);
        return asHtml(path_AdminPathmapping_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final PathMappingEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadPathMapping(form);
        return asHtml(path_AdminPathmapping_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final PathMappingEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadPathMapping(form);
        return asHtml(path_AdminPathmapping_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final PathMappingEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminPathmapping_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final PathMappingEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminPathmapping_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final PathMappingEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        pathMappingService.store(createPathMapping(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final PathMappingEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        pathMappingService.store(createPathMapping(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final PathMappingEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        pathMappingService.delete(getPathMapping(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadPathMapping(final PathMappingEditForm form) {
        copyBeanToBean(getPathMapping(form), form, op -> op.exclude("crudMode"));
    }

    protected PathMapping getPathMapping(final PathMappingEditForm form) {
        final PathMapping pathMapping = pathMappingService.getPathMapping(createKeyMap(form));
        if (pathMapping == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return pathMapping;
    }

    protected PathMapping createPathMapping(final PathMappingEditForm form) {
        PathMapping pathMapping;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            pathMapping = getPathMapping(form);
        } else {
            pathMapping = new PathMapping();
            pathMapping.setCreatedBy(username);
            pathMapping.setCreatedTime(currentTime);
        }
        pathMapping.setUpdatedBy(username);
        pathMapping.setUpdatedTime(currentTime);
        copyBeanToBean(form, pathMapping, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return pathMapping;
    }

    protected Map<String, String> createKeyMap(final PathMappingEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final PathMappingEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminPathmapping_EditJsp);
        };
    }
}
