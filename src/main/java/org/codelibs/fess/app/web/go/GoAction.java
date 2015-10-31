/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(GoAction.class);

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
        searchAvailable();

        Map<String, Object> doc = null;
        try {
            doc = fessEsClient.getDocument(fieldHelper.docIndex, fieldHelper.docType, queryRequestBuilder -> {
                final TermQueryBuilder termQuery = QueryBuilders.termQuery(fieldHelper.docIdField, form.docId);
                queryRequestBuilder.setQuery(termQuery);
                queryRequestBuilder.addFields(queryHelper.getResponseFields());
                return true;
            }).get();
        } catch (final Exception e) {
            logger.warn("Failed to request: " + form.docId, e);
        }
        if (doc == null) {
            throwValidationError(messages -> {
                messages.addErrorsDocidNotFound(GLOBAL, form.docId);
            }, () -> asHtml(path_ErrorJsp));
        }
        final String url = DocumentUtil.getValue(doc, fieldHelper.urlField, String.class);
        if (url == null) {
            throwValidationError(messages -> {
                messages.addErrorsDocumentNotFound(GLOBAL, form.docId);
            }, () -> asHtml(path_ErrorJsp));
        }

        if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE))) {
            final String userSessionId = userInfoHelper.getUserCode();
            if (userSessionId != null) {
                final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
                final ClickLog clickLog = new ClickLog();
                clickLog.setUrl(url);
                final long now = systemHelper.getCurrentTimeAsLong();
                clickLog.setRequestedTime(now);
                clickLog.setQueryRequestedTime(Long.parseLong(form.rt));
                clickLog.setUserSessionId(userSessionId);
                clickLog.setDocId(form.docId);
                long clickCount = 0;
                final Integer count = DocumentUtil.getValue(doc, fieldHelper.clickCountField, Integer.class);
                if (count != null) {
                    clickCount = count.longValue();
                }
                clickLog.setClickCount(clickCount);
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

        if (isFileSystemPath(url)) {
            if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SEARCH_FILE_PROXY_PROPERTY, Constants.TRUE))) {
                final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                try {
                    return viewHelper.asContentResponse(doc);
                } catch (final Exception e) {
                    logger.error("Failed to load: " + doc, e);
                    throwValidationError(messages -> {
                        messages.addErrorsNotLoadFromServer(GLOBAL, url);
                    }, () -> asHtml(path_ErrorJsp));
                    return null; // workaround
                }
            } else if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SEARCH_DESKTOP_PROPERTY, Constants.FALSE))) {
                final String path = url.replaceFirst("file:/+", "//");
                final File file = new File(path);
                if (!file.exists()) {
                    throwValidationError(messages -> {
                        messages.addErrorsNotFoundOnFileSystem(GLOBAL, url);
                    }, () -> asHtml(path_ErrorJsp));
                }
                final Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (final Exception e) {
                    logger.warn("Could not open " + path, e);
                    throwValidationError(messages -> {
                        messages.addErrorsCouldNotOpenOnSystem(GLOBAL, url);
                    }, () -> asHtml(path_ErrorJsp));
                }

                return HtmlResponse.asEmptyBody().httpStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                return HtmlResponse.fromRedirectPathAsIs(url + hash);
            }
        } else {
            return HtmlResponse.fromRedirectPathAsIs(url + hash);
        }
    }

    protected boolean isFileSystemPath(final String url) {
        return url.startsWith("file:") || url.startsWith("smb:");
    }
}