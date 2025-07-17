/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryItem;
import org.codelibs.fess.dict.DictionaryManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin dictionary management.
 * Provides REST endpoints for managing dictionaries in the Fess search engine.
 */
public class ApiAdminDictAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminDictAction() {
        super();
    }

    /** Dictionary manager for handling dictionary file operations */
    @Resource
    protected DictionaryManager dictionaryManager;

    /**
     * Retrieves all available dictionary files.
     *
     * @return JSON response containing list of dictionary files
     */
    // GET /api/admin/dict
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final DictionaryFile<? extends DictionaryItem>[] dictFiles = dictionaryManager.getDictionaryFiles();
        return asJson(new ApiResult.ApiConfigsResponse<ListBody>()
                .settings(Stream.of(dictFiles).map(this::createListBody).collect(Collectors.toList())).status(ApiResult.Status.OK)
                .result());
    }

    /**
     * Creates a ListBody from a DictionaryFile for API responses.
     *
     * @param dictionaryFile the dictionary file to convert
     * @return the converted ListBody object
     */
    protected ListBody createListBody(final DictionaryFile<? extends DictionaryItem> dictionaryFile) {
        final ListBody body = new ListBody();
        body.id = dictionaryFile.getId();
        body.type = dictionaryFile.getType();
        body.path = dictionaryFile.getPath();
        body.timestamp = dictionaryFile.getTimestamp();
        return body;
    }
}
