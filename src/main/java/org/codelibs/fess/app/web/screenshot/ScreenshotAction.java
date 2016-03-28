/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.screenshot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.util.DocumentUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;

public class ScreenshotAction extends FessSearchAction {

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected HttpServletResponse response;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public ActionResponse index(final ScreenshotForm form) {
        validate(form, messages -> {}, () -> asHtml(path_Error_ErrorJsp));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        final Map<String, Object> doc =
                fessEsClient.getDocument(fessConfig.getIndexDocumentSearchIndex(), fessConfig.getIndexDocumentType(),
                        queryRequestBuilder -> {
                            final TermQueryBuilder termQuery = QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), form.docId);
                            queryRequestBuilder.setQuery(termQuery);
                            queryRequestBuilder.addFields(queryHelper.getResponseFields());
                            fessConfig.processSearchPreference(queryRequestBuilder, getUserBean());
                            return true;
                        }).orElse(null);
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (StringUtil.isBlank(form.queryId) || StringUtil.isBlank(url) || screenShotManager == null) {
            // 404
            throw404("Screenshot for " + form.docId + " is not found.");
            return null;
        }

        final File screenShotFile = screenShotManager.getScreenShotFile(form.queryId, form.docId);
        if (screenShotFile == null) {
            // 404
            throw404("Screenshot for " + form.docId + " is under generating.");
            screenShotManager.generate(doc);
            return null;
        }

        return asStream(form.docId).contentType(getImageMimeType(screenShotFile)).stream(out -> {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(screenShotFile))) {
                out.write(in);
            }
        });
    }

    protected String getImageMimeType(final File imageFile) {
        final String path = imageFile.getAbsolutePath();
        if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }
}