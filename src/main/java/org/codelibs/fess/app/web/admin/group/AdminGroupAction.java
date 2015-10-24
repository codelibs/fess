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

package org.codelibs.fess.app.web.admin.group;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.GroupPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.Group;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminGroupAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private GroupService groupService;
    @Resource
    private GroupPager groupPager;
    @Resource
    private SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("group"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final GroupSearchForm form) {
        return asHtml(path_AdminGroup_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final GroupSearchForm form) {
        groupPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminGroup_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final GroupSearchForm form) {
        copyBeanToBean(form.searchParams, groupPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminGroup_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final GroupSearchForm form) {
        groupPager.clear();
        return asHtml(path_AdminGroup_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final GroupSearchForm form) {
        return asHtml(path_AdminGroup_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final GroupSearchForm form) {
        data.register("groupItems", groupService.getGroupList(groupPager)); // page navi

        // restore from pager
        copyBeanToBean(groupPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final GroupEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminGroup_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final int crudMode, final String id, final GroupEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadGroup(form);
        return asHtml(path_AdminGroup_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final GroupEditForm form) {
        return asHtml(path_AdminGroup_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final GroupEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadGroup(form);
        return asHtml(path_AdminGroup_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final int crudMode, final String id, final GroupEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadGroup(form);
        return asHtml(path_AdminGroup_ConfirmJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final GroupEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadGroup(form);
        return asHtml(path_AdminGroup_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final GroupEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadGroup(form);
        return asHtml(path_AdminGroup_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final GroupEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminGroup_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final GroupEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminGroup_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final GroupEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        groupService.store(createGroup(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final GroupEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        groupService.store(createGroup(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final GroupEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        groupService.delete(getGroup(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadGroup(final GroupEditForm form) {
        copyBeanToBean(getGroup(form), form, op -> op.exclude("crudMode"));
    }

    protected Group getGroup(final GroupEditForm form) {
        final Group group = groupService.getGroup(createKeyMap(form));
        if (group == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return group;
    }

    protected Group createGroup(final GroupEditForm form) {
        Group group;
        if (form.crudMode == CrudMode.EDIT) {
            group = getGroup(form);
        } else {
            group = new Group();
        }
        copyBeanToBean(form, group, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        group.setId(Base64.getEncoder().encodeToString(group.getName().getBytes(Constants.CHARSET_UTF_8)));
        return group;
    }

    protected Map<String, String> createKeyMap(final GroupEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final GroupEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminGroup_EditJsp);
        };
    }
}
