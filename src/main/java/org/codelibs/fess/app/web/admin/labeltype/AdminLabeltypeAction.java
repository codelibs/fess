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

package org.codelibs.fess.app.web.admin.labeltype;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.LabelTypePager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.LabelType;
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
public class AdminLabeltypeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private LabelTypeService labelTypeService;
    @Resource
    private LabelTypePager labelTypePager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected RoleTypeService roleTypeService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("labelType"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final LabelTypeSearchForm form) {
        return asHtml(path_AdminLabeltype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final LabelTypeSearchForm form) {
        labelTypePager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminLabeltype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final LabelTypeSearchForm form) {
        copyBeanToBean(form.searchParams, labelTypePager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminLabeltype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final LabelTypeSearchForm form) {
        labelTypePager.clear();
        return asHtml(path_AdminLabeltype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final LabelTypeSearchForm form) {
        return asHtml(path_AdminLabeltype_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final LabelTypeSearchForm form) {
        data.register("labelTypeItems", labelTypeService.getLabelTypeList(labelTypePager)); // page navi

        // restore from pager
        copyBeanToBean(labelTypePager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final LabelTypeEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminLabeltype_EditJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final LabelTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadLabelType(form);
        return asHtml(path_AdminLabeltype_EditJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final LabelTypeEditForm form) {
        return asHtml(path_AdminLabeltype_EditJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final LabelTypeEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadLabelType(form);
        return asHtml(path_AdminLabeltype_EditJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final LabelTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadLabelType(form);
        return asHtml(path_AdminLabeltype_ConfirmJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final LabelTypeEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadLabelType(form);
        return asHtml(path_AdminLabeltype_ConfirmJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final LabelTypeEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadLabelType(form);
        return asHtml(path_AdminLabeltype_ConfirmJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final LabelTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminLabeltype_ConfirmJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final LabelTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminLabeltype_ConfirmJsp).renderWith(data -> {
            data.register("roleTypeItems", roleTypeService.getRoleTypeList());
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final LabelTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        labelTypeService.store(createLabelType(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final LabelTypeEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        labelTypeService.store(createLabelType(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final LabelTypeEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        labelTypeService.delete(getLabelType(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadLabelType(final LabelTypeEditForm form) {
        copyBeanToBean(getLabelType(form), form, op -> op.exclude("crudMode"));
    }

    protected LabelType getLabelType(final LabelTypeEditForm form) {
        final LabelType labelType = labelTypeService.getLabelType(createKeyMap(form));
        if (labelType == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return labelType;
    }

    protected LabelType createLabelType(final LabelTypeEditForm form) {
        LabelType labelType;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            labelType = getLabelType(form);
        } else {
            labelType = new LabelType();
            labelType.setCreatedBy(username);
            labelType.setCreatedTime(currentTime);
        }
        labelType.setUpdatedBy(username);
        labelType.setUpdatedTime(currentTime);
        copyBeanToBean(form, labelType, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return labelType;
    }

    protected Map<String, String> createKeyMap(final LabelTypeEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final LabelTypeEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminLabeltype_EditJsp);
        };
    }
}
