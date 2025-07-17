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
package org.codelibs.fess.job;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.script.Script;

/**
 * Job class for updating label information in the search index.
 * This job processes documents and updates their label fields based on URL pattern matching.
 */
public class UpdateLabelJob {

    private static final Logger logger = LogManager.getLogger(UpdateLabelJob.class);

    /**
     * Query builder for filtering documents to be processed.
     */
    protected QueryBuilder queryBuilder = null;

    /**
     * Default constructor for UpdateLabelJob.
     */
    public UpdateLabelJob() {
        // Default constructor
    }

    /**
     * Executes the label update job.
     * Processes documents in the search index and updates their label fields.
     *
     * @return execution result message
     */
    public String execute() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        final LanguageHelper languageHelper = ComponentUtil.getLanguageHelper();

        final StringBuilder resultBuf = new StringBuilder();

        try {
            final long count = searchEngineClient.updateByQuery(fessConfig.getIndexDocumentUpdateIndex(), option -> {
                if (queryBuilder != null) {
                    option.setQuery(queryBuilder);
                }
                return option.setFetchSource(new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldLang() }, null);
            }, (builder, hit) -> {
                try {
                    final Map<String, Object> doc = hit.getSourceAsMap();
                    final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
                    if (StringUtil.isNotBlank(url)) {
                        final Set<String> labelSet = labelTypeHelper.getMatchedLabelValueSet(url);
                        final Script script = languageHelper.createScript(doc, "ctx._source." + fessConfig.getIndexFieldLabel()
                                + "=new String[]{" + labelSet.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")) + "}");
                        return builder.setScript(script);
                    }
                } catch (final Exception e) {
                    logger.warn("Failed to process {}", hit, e);
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

    /**
     * Sets the query builder for filtering documents.
     *
     * @param queryBuilder the query builder to filter documents
     * @return this UpdateLabelJob instance for method chaining
     */
    public UpdateLabelJob query(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }
}
