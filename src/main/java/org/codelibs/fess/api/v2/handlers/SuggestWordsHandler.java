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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.request.suggest.SuggestRequestBuilder;
import org.codelibs.fess.suggest.request.suggest.SuggestResponse;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/suggest-words} endpoint.
 */
public class SuggestWordsHandler {

    private static final Logger logger = LogManager.getLogger(SuggestWordsHandler.class);

    /**
     * Default constructor used by the DI container.
     */
    public SuggestWordsHandler() {
        // default constructor
    }

    /**
     * Processes one suggest words request.
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
        try {
            final String q = request.getParameter("q");
            final String numStr = request.getParameter("num");
            final int num;
            if (StringUtil.isNotBlank(numStr) && StringUtils.isNumeric(numStr)) {
                num = Integer.parseInt(numStr);
            } else {
                num = 10;
            }
            final String[] fields = SearchRequestParams.getParamValueArray(request, "fn");
            final String[] langs = SearchRequestParams.getParamValueArray(request, "lang");
            final String[] tags = SearchRequestParams.getParamValueArray(request, "label");

            final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
            final SuggestRequestBuilder builder = suggestHelper.suggester().suggest();
            if (q != null) {
                builder.setQuery(q);
            }
            for (final String field : fields) {
                builder.addField(field);
            }
            ComponentUtil.getRoleQueryHelper().build(SearchRequestType.SUGGEST).forEach(builder::addRole);
            builder.setSize(num);
            for (final String lang : langs) {
                builder.addLang(lang);
            }
            for (final String tag : tags) {
                builder.addTag(tag);
            }
            final VirtualHostHelper virtualHostHelper = ComponentUtil.getVirtualHostHelper();
            final String virtualHostKey = virtualHostHelper.getVirtualHostKey();
            if (StringUtil.isNotBlank(virtualHostKey)) {
                builder.addTag(virtualHostKey);
            }
            builder.addKind(SuggestItem.Kind.USER.toString());
            if (ComponentUtil.getFessConfig().isSuggestSearchLog()) {
                builder.addKind(SuggestItem.Kind.QUERY.toString());
            }
            if (ComponentUtil.getFessConfig().isSuggestDocuments()) {
                builder.addKind(SuggestItem.Kind.DOCUMENT.toString());
            }

            final SuggestResponse suggestResponse = builder.execute().getResponse();

            final List<Map<String, Object>> items = new ArrayList<>(suggestResponse.getItems().size());
            for (final SuggestItem item : suggestResponse.getItems()) {
                final Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("text", item.getText());
                final String[] itemTags = item.getTags();
                entry.put("types", itemTags == null ? new ArrayList<String>() : List.of(itemTags));
                items.add(entry);
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("q", q == null ? "" : q);
            payload.put("page_size", suggestResponse.getNum());
            payload.put("record_count", suggestResponse.getTotal());
            payload.put("query_time", suggestResponse.getTookMs());
            payload.put("suggest_words", items);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/suggest-words");
        }
    }
}
