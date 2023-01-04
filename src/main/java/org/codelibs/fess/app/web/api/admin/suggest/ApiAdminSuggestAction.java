/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.suggest;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.SuggestHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

public class ApiAdminSuggestAction extends FessApiAdminAction {

    @Resource
    protected SuggestHelper suggestHelper;

    // GET /api/admin/suggest
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final SuggestBody body = new SuggestBody();
        body.totalWordsNum = suggestHelper.getAllWordsNum();
        body.documentWordsNum = suggestHelper.getDocumentWordsNum();
        body.queryWordsNum = suggestHelper.getQueryWordsNum();
        return asJson(new ApiResult.ApiConfigResponse().setting(body).status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/suggest/all
    @Execute
    public JsonResponse<ApiResult> delete$all() {
        if (!suggestHelper.deleteAllWords()) {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/suggest/document
    @Execute
    public JsonResponse<ApiResult> delete$document() {
        if (!suggestHelper.deleteDocumentWords()) {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/suggest/query
    @Execute
    public JsonResponse<ApiResult> delete$query() {
        if (!suggestHelper.deleteQueryWords()) {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }
}
