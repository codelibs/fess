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
package org.codelibs.fess.api.suggest;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.BaseJsonApiManager;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.request.suggest.SuggestRequestBuilder;
import org.codelibs.fess.suggest.request.suggest.SuggestResponse;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestApiManager extends BaseJsonApiManager {
    private static final Logger logger = LoggerFactory.getLogger(SuggestApiManager.class);

    public SuggestApiManager() {
        setPathPrefix("/suggest");
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        return servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        int status = 0;
        String errMsg = StringUtil.EMPTY;
        final StringBuilder buf = new StringBuilder(255); // TODO replace response stream
        final RoleQueryHelper roleQueryHelper = ComponentUtil.getRoleQueryHelper();
        final SearchService searchService = ComponentUtil.getComponent(SearchService.class);

        try {
            final RequestParameter parameter = RequestParameter.parse(request);
            final String[] langs = searchService.getLanguages(request, parameter);

            final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
            final SuggestRequestBuilder builder = suggestHelper.suggester().suggest();
            builder.setQuery(parameter.getQuery());
            stream(parameter.getSuggestFields()).of(stream -> stream.forEach(builder::addField));
            roleQueryHelper.build(SearchRequestType.SUGGEST).stream().forEach(builder::addRole);
            builder.setSize(parameter.getNum());
            stream(langs).of(stream -> stream.forEach(builder::addLang));

            builder.addKind(SuggestItem.Kind.USER.toString());
            if (ComponentUtil.getFessConfig().isSuggestSearchLog()) {
                builder.addKind(SuggestItem.Kind.QUERY.toString());
            }
            if (ComponentUtil.getFessConfig().isSuggestDocuments()) {
                builder.addKind(SuggestItem.Kind.DOCUMENT.toString());
            }

            final SuggestResponse suggestResponse = builder.execute().getResponse();

            buf.append("\"result\":{");
            buf.append("\"took\":\"").append(suggestResponse.getTookMs()).append('\"');

            buf.append(",\"total\":\"").append(suggestResponse.getTotal()).append('\"');

            buf.append(",\"num\":\"").append(suggestResponse.getNum()).append('\"');

            if (!suggestResponse.getItems().isEmpty()) {
                buf.append(",\"hits\":[");

                boolean first = true;
                for (final SuggestItem item : suggestResponse.getItems()) {
                    if (!first) {
                        buf.append(',');
                    }
                    first = false;

                    buf.append("{\"text\":\"").append(StringEscapeUtils.escapeJson(item.getText())).append('\"');
                    buf.append(",\"tags\":[");
                    for (int i = 0; i < item.getTags().length; i++) {
                        if (i > 0) {
                            buf.append(',');
                        }
                        buf.append('\"').append(StringEscapeUtils.escapeJson(item.getTags()[i])).append('\"');
                    }
                    buf.append(']');
                    buf.append('}');
                }
                buf.append(']');
            }

            buf.append('}');
        } catch (final Exception e) {
            status = 1;
            errMsg = e.getMessage();
            if (errMsg == null) {
                errMsg = e.getClass().getName();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process a suggest request.", e);
            }
            if (e instanceof InvalidAccessTokenException) {
                final InvalidAccessTokenException iate = (InvalidAccessTokenException) e;
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Bearer error=\"" + iate.getType() + "\"");
            }
        }

        writeJsonResponse(status, buf.toString(), errMsg);
    }

    protected static class RequestParameter implements SearchRequestParams {
        private final String query;

        private final String[] fields;

        private final int num;

        private final HttpServletRequest request;

        protected RequestParameter(final HttpServletRequest request, final String query, final String[] fields, final int num) {
            this.query = query;
            this.fields = fields;
            this.num = num;
            this.request = request;
        }

        protected static RequestParameter parse(final HttpServletRequest request) {
            final String query = request.getParameter("query");
            final String fieldsStr = request.getParameter("fields");
            final String[] fields;
            if (StringUtil.isNotBlank(fieldsStr)) {
                fields = fieldsStr.split(",");
            } else {
                fields = new String[0];
            }

            final String numStr = request.getParameter("num");
            final int num;
            if (StringUtil.isNotBlank(numStr) && StringUtils.isNumeric(numStr)) {
                num = Integer.parseInt(numStr);
            } else {
                num = 10;
            }

            return new RequestParameter(request, query, fields, num);
        }

        @Override
        public String getQuery() {
            return query;
        }

        protected String[] getSuggestFields() {
            return fields;
        }

        protected int getNum() {
            return num;
        }

        @Override
        public Map<String, String[]> getFields() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getLanguages() {
            return getParamValueArray(request, "lang");
        }

        @Override
        public GeoInfo getGeoInfo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FacetInfo getFacetInfo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getStartPosition() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPageSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String[] getExtraQueries() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getAttribute(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.SUGGEST;
        }
    }
}
