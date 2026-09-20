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
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

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
        validate(form, messages -> {}, () -> {
            throw responseManager.new400("Invalid parameters: docId=" + form.docId);
        });
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
            saveErrorDetailKey("errors.docid_not_found");
            throw responseManager.new404("Doc ID is not found: " + form.docId);
        }
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (url == null) {
            saveErrorDetailKey("errors.document_not_found");
            throw responseManager.new404("The URL for the document ID is not found: " + form.docId);
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
        final String value = decodeHash(form.hash);
        if (value != null && targetUrl.indexOf('#') == -1) {
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

        if (!isFileSystemPath(targetUrl)) {
            if (isValidRedirectUrl(targetUrl)) {
                return HtmlResponse.fromRedirectPathAsIs(DocumentUtil.encodeUrl(targetUrl + hash));
            }
            logger.warn("Invalid redirect URL detected: {}", targetUrl);
            saveErrorDetailKey("errors.document_not_found");
            throw responseManager.new404("The URL for the document ID is not found: " + form.docId);
        }
        if (!fessConfig.isSearchFileProxyEnabled()) {
            return HtmlResponse.fromRedirectPathAsIs(targetUrl + hash);
        }
        final ViewHelper viewHelper = ComponentUtil.getViewHelper();
        final StreamResponse response;
        try {
            // Only this call may fail with an I/O-flavored exception (an unreachable file
            // server, say); the 404 check and its throw below must stay outside this try, or
            // the catch below would swallow that throw as "failed to load" too.
            response = viewHelper.asContentResponse(doc);
        } catch (final Exception e) {
            logger.warn("Failed to load: {}", doc, e);
            saveErrorDetailKey("errors.not_load_from_server");
            // sendError hands the response to the container's error page (ErrorPageServlet) while
            // keeping this failure at WARN: throwing would have LastaFlute log it at ERROR, which
            // Fess treats as an alert-worthy event. HtmlResponse.undefined() cannot be returned
            // here -- LastaFlute's RedCardableAssist#assertExecuteMethodResponseDefined forbids an
            // @Execute method from returning it (ExecuteMethodReturnUndefinedResponseException) --
            // so asEmptyBody() is used instead: the response the same guard recommends, which
            // likewise adds no body and leaves the sendError()-flagged response alone.
            LaResponseUtil.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return HtmlResponse.asEmptyBody();
        }
        if (response.getHttpStatus().orElse(200) == 404) {
            logger.debug("Document not found: url={}", targetUrl);
            saveErrorDetailKey("errors.not_found_on_file_system");
            throw responseManager.new404("Not found: " + targetUrl);
        }
        return response;
    }

    /**
     * Records the detail key the error page shows for this failure. {@code ErrorPageServlet} reads
     * it from the request on the container's error dispatch and passes it to the theme as the
     * {@code x-fess-error-detail-key} meta tag.
     *
     * @param messageKey a {@code fess_message} key, e.g. {@code errors.docid_not_found}
     */
    private void saveErrorDetailKey(final String messageKey) {
        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(Constants.ERROR_DETAIL_KEY, messageKey));
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
     * Resolves the URL fragment to append to the redirect target from the {@code hash} request parameter.
     *
     * <p>{@code hash} carries the fragment of the originating result URL in URL-encoded form, but it is
     * a plain request parameter and therefore arbitrary user input: {@link GoForm} declares no format
     * constraint on it. A value that is absent, blank or not decodable is treated as absent, so that a
     * malformed parameter drops the fragment instead of failing the user's navigation with an error.</p>
     *
     * @param hash the raw {@code hash} parameter, a URL-encoded fragment (NullAllowed)
     * @return the decoded fragment, or {@literal null} if {@code hash} is absent, blank or malformed
     */
    protected String decodeHash(final String hash) {
        if (StringUtil.isNotBlank(hash)) {
            try {
                return URLUtil.decode(hash, Constants.UTF_8);
            } catch (final IllegalArgumentException e) {
                // Attacker-controlled input: log at debug only so a malformed hash cannot flood logs.
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid hash parameter: {}", hash, e);
                }
            }
        }
        return null;
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
