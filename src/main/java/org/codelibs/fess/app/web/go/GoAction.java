/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.error.ErrorAction;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(GoAction.class);

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
    @Execute
    public ActionResponse index(final GoForm form) throws IOException {
        validate(form, messages -> {}, () -> asHtml(path_Error_ErrorJsp));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        Map<String, Object> doc = null;
        try {
            doc =
                    searchService.getDocumentByDocId(form.docId,
                            new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldConfigId() }, getUserBean()).orElse(null);
        } catch (final Exception e) {
            logger.warn("Failed to request: " + form.docId, e);
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

        String hash;
        if (StringUtil.isNotBlank(form.hash)) {
            final String value = URLUtil.decode(form.hash, Constants.UTF_8);
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

        final String targetUrl = pathMappingHelper.replaceUrl(url);
        if (isFileSystemPath(targetUrl)) {
            if (fessConfig.isSearchFileProxyEnabled()) {
                final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                try {
                    final StreamResponse response = viewHelper.asContentResponse(doc);
                    if (response.getHttpStatus().orElse(200) == 404) {
                        logger.debug("Not found: " + targetUrl);
                        saveError(messages -> messages.addErrorsNotFoundOnFileSystem(GLOBAL, targetUrl));
                        return redirect(ErrorAction.class);
                    }
                    return response;
                } catch (final Exception e) {
                    logger.debug("Failed to load: " + doc, e);
                    saveError(messages -> messages.addErrorsNotLoadFromServer(GLOBAL, targetUrl));
                    return redirect(ErrorAction.class);
                }
            } else {
                return redirect(targetUrl + hash);
            }
        } else {
            return redirect(targetUrl + hash);
        }
    }

    protected ActionResponse redirect(final String url) {
        final HttpServletRequest request2 = LaRequestUtil.getRequest();
        final String enc = request2.getCharacterEncoding() == null ? Constants.UTF_8 : request2.getCharacterEncoding();
        final StringBuilder buf = new StringBuilder(url.length() + 100);
        for (final char c : url.toCharArray()) {
            if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c), enc));
                } catch (final UnsupportedEncodingException e) {
                    buf.append(c);
                }
            }
        }
        return HtmlResponse.fromRedirectPathAsIs(buf.toString());
    }

    protected boolean isFileSystemPath(final String url) {
        return url.startsWith("file:") || url.startsWith("smb:") || url.startsWith("ftp:");
    }
}
