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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/popular-words} endpoint.
 */
public class PopularWordsHandler {

    private static final Logger logger = LogManager.getLogger(PopularWordsHandler.class);

    /**
     * Default constructor used by the DI container.
     */
    public PopularWordsHandler() {
        // default constructor
    }

    /**
     * Processes one popular words request.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        if (!ComponentUtil.getFessConfig().isWebApiPopularWord()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, "unsupported operation");
            return;
        }
        try {
            final String seed = request.getParameter("seed");
            String[] tags = SearchRequestParams.getParamValueArray(request, "label");
            final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(virtualHostKey)) {
                tags = ArrayUtils.addAll(tags, virtualHostKey);
            }
            final String[] fields = request.getParameterValues("field");
            final PopularWordHelper popularWordHelper = ComponentUtil.getPopularWordHelper();
            final List<String> words =
                    popularWordHelper.getWordList(SearchRequestType.JSON, seed, tags, null, fields, StringUtil.EMPTY_STRINGS);
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("record_count", words.size());
            payload.put("popular_words", words);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/popular-words");
        }
    }
}
