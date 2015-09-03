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

package org.codelibs.fess.app.web.admin.boostdocumentrule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.BoostDocumentRulePager;
import org.codelibs.fess.app.service.BoostDocumentRuleService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.BoostDocumentRule;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminBoostdocumentruleAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private BoostDocumentRuleService boostDocumentRuleService;
    @Resource
    private BoostDocumentRulePager boostDocumentRulePager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("boostDocumentRule"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final BoostDocumentRuleSearchForm form) {
        return asHtml(path_AdminBoostdocumentrule_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final BoostDocumentRuleSearchForm form) {
        boostDocumentRulePager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminBoostdocumentrule_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final BoostDocumentRuleSearchForm form) {
        copyBeanToBean(form.searchParams, boostDocumentRulePager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminBoostdocumentrule_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final BoostDocumentRuleSearchForm form) {
        boostDocumentRulePager.clear();
        return asHtml(path_AdminBoostdocumentrule_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final BoostDocumentRuleSearchForm form) {
        return asHtml(path_AdminBoostdocumentrule_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final BoostDocumentRuleSearchForm form) {
        data.register("boostDocumentRuleItems", boostDocumentRuleService.getBoostDocumentRuleList(boostDocumentRulePager)); // page navi

        // restore from pager
        copyBeanToBean(boostDocumentRulePager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final BoostDocumentRuleEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminBoostdocumentrule_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final BoostDocumentRuleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadBoostDocumentRule(form);
        return asHtml(path_AdminBoostdocumentrule_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final BoostDocumentRuleEditForm form) {
        return asHtml(path_AdminBoostdocumentrule_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final BoostDocumentRuleEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadBoostDocumentRule(form);
        return asHtml(path_AdminBoostdocumentrule_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final BoostDocumentRuleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadBoostDocumentRule(form);
        return asHtml(path_AdminBoostdocumentrule_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final BoostDocumentRuleEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadBoostDocumentRule(form);
        return asHtml(path_AdminBoostdocumentrule_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final BoostDocumentRuleEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadBoostDocumentRule(form);
        return asHtml(path_AdminBoostdocumentrule_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final BoostDocumentRuleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminBoostdocumentrule_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final BoostDocumentRuleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminBoostdocumentrule_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final BoostDocumentRuleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        boostDocumentRuleService.store(createBoostDocumentRule(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final BoostDocumentRuleEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        boostDocumentRuleService.store(createBoostDocumentRule(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final BoostDocumentRuleEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        boostDocumentRuleService.delete(getBoostDocumentRule(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadBoostDocumentRule(final BoostDocumentRuleEditForm form) {
        copyBeanToBean(getBoostDocumentRule(form), form, op -> op.exclude("crudMode"));
    }

    protected BoostDocumentRule getBoostDocumentRule(final BoostDocumentRuleEditForm form) {
        final BoostDocumentRule boostDocumentRule = boostDocumentRuleService.getBoostDocumentRule(createKeyMap(form));
        if (boostDocumentRule == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return boostDocumentRule;
    }

    protected BoostDocumentRule createBoostDocumentRule(final BoostDocumentRuleEditForm form) {
        BoostDocumentRule boostDocumentRule;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            boostDocumentRule = getBoostDocumentRule(form);
        } else {
            boostDocumentRule = new BoostDocumentRule();
            boostDocumentRule.setCreatedBy(username);
            boostDocumentRule.setCreatedTime(currentTime);
        }
        boostDocumentRule.setUpdatedBy(username);
        boostDocumentRule.setUpdatedTime(currentTime);
        copyBeanToBean(form, boostDocumentRule, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return boostDocumentRule;
    }

    protected Map<String, String> createKeyMap(final BoostDocumentRuleEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final BoostDocumentRuleEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminBoostdocumentrule_EditJsp);
        };
    }
}
