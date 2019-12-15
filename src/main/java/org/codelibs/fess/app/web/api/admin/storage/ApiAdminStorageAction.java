/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.storage;

import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.getFileItems;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

public class ApiAdminStorageAction extends FessApiAdminAction {

    // GET /api/admin/storage/list
    // POST /api/admin/storage/list
    @Execute
    public JsonResponse<ApiResult> list() {
        // TODO
        final List<Map<String, Object>> list = getFileItems(null);
        return asJson(new ApiResult.ApiStorageResponse().items(list).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/storage/download
//    @Execute
//    public JsonResponse<ApiResult> get$download() {
//        // TODO
//    }
//
//    // DELETE /api/admin/storage/delete
//    @Execute
//    public JsonResponse<ApiResult> delete$index() {
//        // TODO
//    }
//
//    // POST /api/admin/storage/upload
//    @Execute
//    public JsonResponse<ApiResult> post$upload() {
//        // TODO
//    }

}
