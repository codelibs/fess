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
package org.codelibs.fess.app.web.api.admin.dict;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;
import org.codelibs.fess.dict.DictionaryManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

public class ApiAdminDictAction extends FessApiAdminAction {
    @Resource
    protected DictionaryManager dictionaryManager;

    // GET /api/admin/dict
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final DictionaryFile<? extends DictionaryItem>[] dictFiles = dictionaryManager.getDictionaryFiles();
        return asJson(new ApiResult.ApiConfigsResponse<ListBody>()
                .settings(Stream.of(dictFiles).map(this::createListBody).collect(Collectors.toList())).status(ApiResult.Status.OK)
                .result());
    }

    protected ListBody createListBody(final DictionaryFile<? extends DictionaryItem> dictionaryFile) {
        final ListBody body = new ListBody();
        body.id = dictionaryFile.getId();
        body.type = dictionaryFile.getType();
        body.path = dictionaryFile.getPath();
        body.timestamp = dictionaryFile.getTimestamp();
        return body;
    }
}
