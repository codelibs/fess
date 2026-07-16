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
import java.time.LocalDateTime;
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
        super();
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
            doc = searchHelper
                    .getDocumentByDocId(form.docId, new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldConfigId() },
                            getUserBean())
                    .orElse(null);
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
                clickLog.setQueryRequestedAt(parseQueryRequestedAt(form.rt));
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
            if (isValidRedirectUrl(targetUrl)) {
                return HtmlResponse.fromRedirectPathAsIs(DocumentUtil.encodeUrl(targetUrl + hash));
            } else {
                logger.warn("Invalid redirect URL detected: {}", targetUrl);
                saveError(messages -> messages.addErrorsDocumentNotFound(GLOBAL, form.docId));
                return redirect(ErrorAction.class);
            }
        }
        if (!fessConfig.isSearchFileProxyEnabled()) {
            return HtmlResponse.fromRedirectPathAsIs(targetUrl + hash);
        }
        final ViewHelper viewHelper = ComponentUtil.getViewHelper();
        try {
            final StreamResponse response = viewHelper.asContentResponse(doc);
            if (response.getHttpStatus().orElse(200) == 404) {
                logger.debug("Document not found: url={}", targetUrl);
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
     * Resolves the click log's query-requested timestamp from the {@code rt} request parameter.
     *
     * <p>{@code rt} carries the epoch millis of the originating search, but it is a plain
     * request parameter and therefore arbitrary user input: {@link GoForm} declares no numeric
     * constraint on it. A value that is absent or not a number is treated as absent and falls
     * back to the current time, so that a malformed parameter degrades click telemetry instead
     * of failing the user's navigation with an error.</p>
     *
     * <p>This matches the v2 API's {@code ClickHandler}, which likewise falls back to the
     * current time whenever {@code rt} is not a number.</p>
     *
     * @param rt the raw {@code rt} parameter, epoch millis in string form (NullAllowed)
     * @return the originating search time, or the current time if {@code rt} is absent or malformed
     */
    protected LocalDateTime parseQueryRequestedAt(final String rt) {
        if (rt != null) {
            try {
                return DfTypeUtil.toLocalDateTime(Long.parseLong(rt));
            } catch (final NumberFormatException e) {
                // Attacker-controlled input: log at debug only so a malformed rt cannot flood logs.
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid rt parameter: {}", rt, e);
                }
            }
        }
        return systemHelper.getCurrentTimeAsLocalDateTime();
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
        return ComponentUtil.getProtocolHelper().isFileSystemPath(url);
    }

    /**
     * Validates if the URL is safe for redirection.
     *
     * @param url the URL to validate
     * @return true if the URL is valid for redirection, false otherwise
     */
    protected boolean isValidRedirectUrl(final String url) {
        if (StringUtil.isBlank(url)) {
            return false;
        }
        final String lowerUrl = url.toLowerCase();
        if (lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://")) {
            return true;
        }
        if (lowerUrl.startsWith("javascript:") || lowerUrl.startsWith("data:") || lowerUrl.startsWith("vbscript:")) {
            return false;
        }
        return true;
    }
}
