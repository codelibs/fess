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

package org.codelibs.fess.app.web.admin.keymatch;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.KeyMatch;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.KeyMatchPager;
import org.codelibs.fess.service.KeyMatchService;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author codelibs
 * @author jflute
 */
public class AdminKeymatchTmpAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private KeyMatchService keyMatchService;
    @Resource
    private KeyMatchPager keyMatchPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("keyMatch"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(KeyMatchSearchForm form) {
        return asHtml(path_AdminKeyMatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(Integer pageNumber, KeyMatchSearchForm form) {
        keyMatchPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminKeyMatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(KeyMatchSearchForm form) {
        copyBeanToBean(form.searchParams, keyMatchPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminKeyMatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(KeyMatchSearchForm form) {
        keyMatchPager.clear();
        return asHtml(path_AdminKeyMatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(KeyMatchSearchForm form) {
        return asHtml(path_AdminKeyMatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(RenderData data, KeyMatchSearchForm form) {
        data.register("keyMatchItems", keyMatchService.getKeyMatchList(keyMatchPager)); // page navi

        // restore from pager
        copyBeanToBean(keyMatchPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(KeyMatchEditForm form) {
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminKeyMatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(int crudMode, String id, KeyMatchEditForm form) {
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadKeyMatch(form);
        return asHtml(path_AdminKeyMatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(KeyMatchEditForm form) {
        return asHtml(path_AdminKeyMatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(KeyMatchEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadKeyMatch(form);
        return asHtml(path_AdminKeyMatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(int crudMode, String id, KeyMatchEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadKeyMatch(form);
        return asHtml(path_AdminKeyMatch_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(KeyMatchEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadKeyMatch(form);
        return asHtml(path_AdminKeyMatch_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(int crudMode, String id, KeyMatchEditForm form) {
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadKeyMatch(form);
        return asHtml(path_AdminKeyMatch_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(KeyMatchEditForm form) {
        validate(form, messages -> {} , toEditHtml());
        return asHtml(path_AdminKeyMatch_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(KeyMatchEditForm form) {
        validate(form, messages -> {} , toEditHtml());
        return asHtml(path_AdminKeyMatch_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(KeyMatchEditForm form) {
        validate(form, messages -> {} , toEditHtml());
        keyMatchService.store(createKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(KeyMatchEditForm form) {
        validate(form, messages -> {} , toEditHtml());
        keyMatchService.store(createKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(KeyMatchEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        keyMatchService.delete(getKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadKeyMatch(KeyMatchEditForm form) {
        copyBeanToBean(getKeyMatch(form), form, op -> op.exclude("crudMode"));
    }

    protected KeyMatch getKeyMatch(KeyMatchEditForm form) {
        final KeyMatch keyMatch = keyMatchService.getKeyMatch(createKeyMap(form));
        if (keyMatch == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return keyMatch;
    }

    protected KeyMatch createKeyMatch(KeyMatchEditForm form) {
        KeyMatch keyMatch;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            keyMatch = getKeyMatch(form);
        } else {
            keyMatch = new KeyMatch();
            keyMatch.setCreatedBy(username);
            keyMatch.setCreatedTime(currentTime);
        }
        keyMatch.setUpdatedBy(username);
        keyMatch.setUpdatedTime(currentTime);
        copyBeanToBean(form, keyMatch, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return keyMatch;
    }

    protected Map<String, String> createKeyMap(KeyMatchEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(KeyMatchEditForm form, int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            } , toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminKeyMatch_EditJsp);
        };
    }
}
