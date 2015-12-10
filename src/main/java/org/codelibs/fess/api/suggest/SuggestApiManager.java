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
package org.codelibs.fess.api.suggest;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.api.json.JsonApiManager;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.request.suggest.SuggestRequestBuilder;
import org.codelibs.fess.suggest.request.suggest.SuggestResponse;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestApiManager extends BaseApiManager {
    private static final Logger logger = LoggerFactory.getLogger(SuggestApiManager.class);

    public SuggestApiManager() {
        setPathPrefix("/suggest");
    }

    @Resource
    protected DynamicProperties crawlerProperties;

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
        final StringBuilder buf = new StringBuilder(255);

        try {

            final RequestParameter parameter = RequestParameter.parse(request);

            final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
            final SuggestRequestBuilder builder = suggestHelper.suggester().suggest();
            builder.setQuery(parameter.getQuery());
            for (final String field : parameter.getFields()) {
                builder.addField(field);
            }
            builder.setSize(parameter.getNum());

            final SuggestResponse suggestResponse = builder.execute().getResponse();

            builder.addKind(SuggestItem.Kind.USER.toString());
            if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, Constants.TRUE))) {
                builder.addKind(SuggestItem.Kind.QUERY.toString());
            }
            if (Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SUGGEST_DOCUMENTS_PROPERTY, Constants.TRUE))) {
                builder.addKind(SuggestItem.Kind.DOCUMENT.toString());
            }

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

                    buf.append("{\"text\":\"").append(item.getText()).append('\"');
                    buf.append(",\"tags\":[");
                    for (int i = 0; i < item.getTags().length; i++) {
                        if (i > 0) {
                            buf.append(',');
                        }
                        buf.append('\"').append(item.getTags()[i]).append('\"');
                    }
                    buf.append(']');

                    buf.append(",\"roles\":[");
                    for (int i = 0; i < item.getRoles().length; i++) {
                        if (i > 0) {
                            buf.append(',');
                        }
                        buf.append('\"').append(item.getRoles()[i]).append('\"');
                    }
                    buf.append(']');

                    buf.append(",\"fields\":[");
                    for (int i = 0; i < item.getFields().length; i++) {
                        if (i > 0) {
                            buf.append(',');
                        }
                        buf.append('\"').append(item.getFields()[i]).append('\"');
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
        }

        JsonApiManager.writeJsonResponse(status, buf.toString(), errMsg);
    }

    protected static class RequestParameter {
        private final String query;

        private final String[] fields;

        private final int num;

        protected RequestParameter(final String query, final String[] fields, final int num) {
            this.query = query;
            this.fields = fields;
            this.num = num;
        }

        protected static RequestParameter parse(final HttpServletRequest request) {
            final String query = request.getParameter("query");
            final String fieldsStr = request.getParameter("fields");
            final String[] fields;
            if (StringUtils.isNotBlank(fieldsStr)) {
                fields = fieldsStr.split(",");
            } else {
                fields = new String[0];
            }

            final String numStr = request.getParameter("num");
            final int num;
            if (StringUtils.isNotBlank(numStr) && StringUtils.isNumeric(numStr)) {
                num = Integer.parseInt(numStr);
            } else {
                num = 10;
            }

            return new RequestParameter(query, fields, num);
        }

        protected String getQuery() {
            return query;
        }

        protected String[] getFields() {
            return fields;
        }

        protected int getNum() {
            return num;
        }
    }
}
