/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.job;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateLabelJob {

    private static final Logger logger = LoggerFactory.getLogger(UpdateLabelJob.class);

    protected QueryBuilder queryBuilder = null;

    public String execute() {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();

        final StringBuilder resultBuf = new StringBuilder();

        try {
            final long count =
                    fessEsClient.updateByQuery(
                            fessConfig.getIndexDocumentUpdateIndex(),
                            option -> {
                                if (queryBuilder != null) {
                                    option.setQuery(queryBuilder);
                                }
                                return option.setFetchSource(new String[] { fessConfig.getIndexFieldUrl() }, null);
                            },
                            (builder, hit) -> {
                                try {
                                    final Map<String, Object> doc = hit.getSourceAsMap();
                                    final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                                    if (StringUtil.isNotBlank(url)) {
                                        final Set<String> labelSet = labelTypeHelper.getMatchedLabelValueSet(url);
                                        return builder.setDoc(XContentFactory.jsonBuilder().startObject()
                                                .field(fessConfig.getIndexFieldLabel(), labelSet.toArray(n -> new String[n])).endObject());
                                    }
                                } catch (final IOException e) {
                                    logger.warn("Failed to process " + hit, e);
                                }
                                return null;
                            });
            resultBuf.append(count).append(" docs").append("\n");
        } catch (final Exception e) {
            logger.error("Could not update labels.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

    public UpdateLabelJob query(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }
}
