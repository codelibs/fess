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

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.KeyMatchPager;
import org.codelibs.fess.app.service.KeyMatchService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.KeyMatch;
import org.codelibs.fess.helper.SystemHelper;
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
public class AdminKeymatchAction extends FessAdminAction {

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
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("keyMatch"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final KeyMatchSearchForm form) {
        return asHtml(path_AdminKeymatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final KeyMatchSearchForm form) {
        keyMatchPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminKeymatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final KeyMatchSearchForm form) {
        copyBeanToBean(form.searchParams, keyMatchPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminKeymatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final KeyMatchSearchForm form) {
        keyMatchPager.clear();
        return asHtml(path_AdminKeymatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final KeyMatchSearchForm form) {
        return asHtml(path_AdminKeymatch_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final KeyMatchSearchForm form) {
        data.register("keyMatchItems", keyMatchService.getKeyMatchList(keyMatchPager)); // page navi

        // restore from pager
        copyBeanToBean(keyMatchPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final KeyMatchEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminKeymatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final KeyMatchEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadKeyMatch(form);
        return asHtml(path_AdminKeymatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final KeyMatchEditForm form) {
        return asHtml(path_AdminKeymatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final KeyMatchEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadKeyMatch(form);
        return asHtml(path_AdminKeymatch_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final KeyMatchEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadKeyMatch(form);
        return asHtml(path_AdminKeymatch_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final KeyMatchEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadKeyMatch(form);
        return asHtml(path_AdminKeymatch_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final KeyMatchEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadKeyMatch(form);
        return asHtml(path_AdminKeymatch_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final KeyMatchEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminKeymatch_ConfirmJsp);
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final KeyMatchEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminKeymatch_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final KeyMatchEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        keyMatchService.store(createKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final KeyMatchEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        keyMatchService.store(createKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final KeyMatchEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        keyMatchService.delete(getKeyMatch(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        ComponentUtil.getKeyMatchHelper().update();
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadKeyMatch(final KeyMatchEditForm form) {
        copyBeanToBean(getKeyMatch(form), form, op -> op.exclude("crudMode"));
    }

    protected KeyMatch getKeyMatch(final KeyMatchEditForm form) {
        final KeyMatch keyMatch = keyMatchService.getKeyMatch(createKeyMap(form));
        if (keyMatch == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return keyMatch;
    }

    protected KeyMatch createKeyMatch(final KeyMatchEditForm form) {
        KeyMatch keyMatch;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            keyMatch = getKeyMatch(form);
        } else {
            keyMatch = new KeyMatch();
            keyMatch.setCreatedBy(username);
            keyMatch.setCreatedTime(currentTime);
        }
        keyMatch.setUpdatedBy(username);
        keyMatch.setUpdatedTime(currentTime);
        copyBeanToBean(form, keyMatch, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return keyMatch;
    }

    protected Map<String, String> createKeyMap(final KeyMatchEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final KeyMatchEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminKeymatch_EditJsp);
        };
    }
}
