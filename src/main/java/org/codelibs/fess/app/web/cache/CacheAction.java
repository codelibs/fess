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
package org.codelibs.fess.app.web.cache;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.error.ErrorAction;
import org.codelibs.fess.util.DocumentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;

/**
 * Action class for handling cached document content requests.
 * Provides functionality to retrieve and display cached versions of crawled documents.
 */
public class CacheAction extends FessSearchAction {

    /**
     * Creates a new instance of CacheAction.
     */
    public CacheAction() {
        super();
    }

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(CacheAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Retrieves and displays cached content for a specific document.
     *
     * @param form the cache form containing document ID and highlight query
     * @return ActionResponse containing the cached document content or error redirect
     */
    @Execute
    public ActionResponse index(final CacheForm form) {
        validate(form, messages -> {}, () -> asHtml(virtualHost(path_Error_ErrorJsp)));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        Map<String, Object> doc = null;
        try {
            doc = searchHelper.getDocumentByDocId(form.docId, queryFieldConfig.getCacheResponseFields(), getUserBean()).orElse(null);
        } catch (final Exception e) {
            logger.warn("Failed to request: {}", form.docId, e);
        }
        if (doc == null) {
            saveError(messages -> messages.addErrorsDocidNotFound(GLOBAL, form.docId));
            return redirect(ErrorAction.class);
        }

        final String content = viewHelper.createCacheContent(doc, form.hq);
        if (content == null) {
            saveError(messages -> messages.addErrorsDocidNotFound(GLOBAL, form.docId));
            return redirect(ErrorAction.class);
        }

        final StreamResponse response =
                asStream(DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class)).contentType("text/html; charset=UTF-8")
                        .data(content.getBytes(Constants.CHARSET_UTF_8));
        response.headerContentDispositionInline(); // TODO will be fixed in lastaflute
        return response;
    }

}