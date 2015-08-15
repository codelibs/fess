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

package org.codelibs.fess.app.web.screenshot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.OutputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenshotAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //     
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotAction.class);

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
    public HtmlResponse index(final ScreenshotForm form) {
        searchAvailable();

        OutputStream out = null;
        BufferedInputStream in = null;
        try {
            final Map<String, Object> doc = fessEsClient.getDocument(fieldHelper.docIndex, fieldHelper.docType, queryRequestBuilder -> {
                final TermQueryBuilder termQuery = QueryBuilders.termQuery(fieldHelper.docIdField, form.docId);
                queryRequestBuilder.setQuery(termQuery);
                queryRequestBuilder.addFields(queryHelper.getResponseFields());
                return true;
            }).get();
            final String url = doc == null ? null : (String) doc.get(fieldHelper.urlField);
            if (StringUtil.isBlank(form.queryId) || StringUtil.isBlank(url) || screenShotManager == null) {
                // 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            final File screenShotFile = screenShotManager.getScreenShotFile(form.queryId, form.docId);
            if (screenShotFile == null) {
                // 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                screenShotManager.generate(doc);
                return null;
            }

            response.setContentType(getImageMimeType(screenShotFile));

            out = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(screenShotFile));
            CopyUtil.copy(in, out);
            OutputStreamUtil.flush(out);
        } catch (final Exception e) {
            logger.error("Failed to response: " + form.docId, e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return null;
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