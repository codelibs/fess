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
package org.codelibs.fess.app.web.thumbnail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.util.DocumentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Action class for handling thumbnail image requests.
 * Serves thumbnail images for documents in the search results.
 */
public class ThumbnailAction extends FessSearchAction {

    /**
     * Default constructor for ThumbnailAction.
     */
    public ThumbnailAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //
    /**
     * HTTP servlet response for streaming thumbnail images.
     */
    @Resource
    protected HttpServletResponse response;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Serves a thumbnail image for the specified document.
     *
     * @param form the thumbnail request form containing document ID and query parameters
     * @return ActionResponse containing the thumbnail image or 404 if not found
     */
    @Execute
    public ActionResponse index(final ThumbnailForm form) {
        validate(form, messages -> {}, () -> asHtml(virtualHost(path_Error_ErrorJsp)));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        final Map<String, Object> doc =
                searchHelper.getDocumentByDocId(form.docId, queryFieldConfig.getResponseFields(), getUserBean()).orElse(null);
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldThumbnail(), String.class);
        if (StringUtil.isBlank(form.queryId) || StringUtil.isBlank(url) || !thumbnailSupport) {
            // 404
            throw responseManager.new404("Thumbnail for " + form.docId + " is not found.");
        }

        final File thumbnailFile = thumbnailManager.getThumbnailFile(doc);
        if (thumbnailFile == null) {
            if (fessConfig.isThumbnailEnabled()) {
                thumbnailManager.offer(doc);
            }
            // 404
            throw responseManager.new404("Thumbnail for " + form.docId + " is under generating.");
        }

        return asStream(form.docId).contentType(getImageMimeType(thumbnailFile)).stream(out -> {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumbnailFile))) {
                out.write(in);
            }
        });
    }

    /**
     * Determines the MIME type of an image file based on its file extension.
     *
     * @param imageFile the image file
     * @return the MIME type string
     */
    protected String getImageMimeType(final File imageFile) {
        final String path = imageFile.getAbsolutePath();
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".gif")) {
            return "image/gif";
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}