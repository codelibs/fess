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
package org.codelibs.fess.app.web.go;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.error.ErrorAction;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.opensearch.log.exentity.ClickLog;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * Action class for handling document redirection requests.
 * This action processes "go" requests that redirect users to specific documents
 * while tracking click events and handling various URL types including file system paths.
 */
public class GoAction extends FessSearchAction {

    /**
     * Default constructor for GoAction.
     */
    public GoAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                            Constant
    //
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(GoAction.class);

    /** Helper for URL path mapping and transformation. */
    @Resource
    protected PathMappingHelper pathMappingHelper;

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
     * Handles document redirection requests.
     * Validates the document ID, logs click events if enabled, and redirects
     * to the target URL or serves file content directly if configured.
     *
     * @param form the go form containing document ID and tracking parameters
     * @return action response for redirection or content streaming
     * @throws IOException if an I/O error occurs during content retrieval
     */
    @Execute
    public ActionResponse index(final GoForm form) throws IOException {
        validate(form, messages -> {}, () -> asHtml(virtualHost(path_Error_ErrorJsp)));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        Map<String, Object> doc = null;
        try {
            doc = searchHelper.getDocumentByDocId(form.docId,
                    new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldConfigId() }, getUserBean()).orElse(null);
        } catch (final Exception e) {
            logger.warn("Failed to request: {}", form.docId, e);
        }
        if (doc == null) {
            saveError(messages -> messages.addErrorsDocidNotFound(GLOBAL, form.docId));
            return redirect(ErrorAction.class);
        }
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (url == null) {
            saveError(messages -> messages.addErrorsDocumentNotFound(GLOBAL, form.docId));
            return redirect(ErrorAction.class);
        }

        if (fessConfig.isSearchLog()) {
            final String userSessionId = userInfoHelper.getUserCode();
            if (userSessionId != null) {
                final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
                final ClickLog clickLog = new ClickLog();
                clickLog.setUrlId((String) doc.get(fessConfig.getIndexFieldId()));
                clickLog.setUrl(url);
                clickLog.setRequestedAt(systemHelper.getCurrentTimeAsLocalDateTime());
                clickLog.setQueryRequestedAt(DfTypeUtil.toLocalDateTime(Long.parseLong(form.rt)));
                clickLog.setUserSessionId(userSessionId);
                clickLog.setDocId(form.docId);
                clickLog.setQueryId(form.queryId);
                if (form.order != null) {
                    clickLog.setOrder(form.order);
                }
                searchLogHelper.addClickLog(clickLog);
            }
        }

        final String targetUrl = pathMappingHelper.replaceUrl(url);

        String hash;
        if (StringUtil.isNotBlank(form.hash)) {
            final String value = URLUtil.decode(form.hash, Constants.UTF_8);
            if (targetUrl.indexOf('#') == -1) {
                final StringBuilder buf = new StringBuilder(value.length() + 100);
                for (final char c : value.toCharArray()) {
                    if (CharUtil.isUrlChar(c) || c == ' ') {
                        buf.append(c);
                    } else {
                        try {
                            buf.append(URLEncoder.encode(String.valueOf(c), Constants.UTF_8));
                        } catch (final UnsupportedEncodingException e) {
                            // NOP
                        }
                    }
                }
                hash = buf.toString();
            } else {
                hash = StringUtil.EMPTY;
            }
        } else {
            hash = StringUtil.EMPTY;
        }

        if (!isFileSystemPath(targetUrl)) {
            return HtmlResponse.fromRedirectPathAsIs(DocumentUtil.encodeUrl(targetUrl + hash));
        }
        if (!fessConfig.isSearchFileProxyEnabled()) {
            return HtmlResponse.fromRedirectPathAsIs(targetUrl + hash);
        }
        final ViewHelper viewHelper = ComponentUtil.getViewHelper();
        try {
            final StreamResponse response = viewHelper.asContentResponse(doc);
            if (response.getHttpStatus().orElse(200) == 404) {
                logger.debug("Not found: {}", targetUrl);
                saveError(messages -> messages.addErrorsNotFoundOnFileSystem(GLOBAL, targetUrl));
                return redirect(ErrorAction.class);
            }
            return response;
        } catch (final Exception e) {
            logger.warn("Failed to load: {}", doc, e);
            saveError(messages -> messages.addErrorsNotLoadFromServer(GLOBAL, targetUrl));
            return redirect(ErrorAction.class);
        }
    }

    /**
     * Checks if the given URL represents a file system path.
     * Determines if the URL uses file system protocols that may require
     * special handling for content serving.
     *
     * @param url the URL to check
     * @return true if the URL is a file system path, false otherwise
     */
    protected boolean isFileSystemPath(final String url) {
        return url.startsWith("file:") || url.startsWith("smb:") || url.startsWith("smb1:") || url.startsWith("ftp:")
                || url.startsWith("storage:");
    }
}
