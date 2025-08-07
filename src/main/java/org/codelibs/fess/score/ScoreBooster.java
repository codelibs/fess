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
package org.codelibs.fess.score;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.update.UpdateRequestBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.search.SearchHit;

/**
 * This class is a base class for score boosters.
 */
public abstract class ScoreBooster {
    /**
     * Constructor.
     */
    public ScoreBooster() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ScoreBooster.class);

    /**
     * The bulk request builder.
     */
    protected BulkRequestBuilder bulkRequestBuilder = null;

    /**
     * The priority of this score booster.
     */
    protected int priority = 1;

    /**
     * The request timeout.
     */
    protected String requestTimeout = "1m";

    /**
     * The request cache size.
     */
    protected int requestCacheSize = 1000;

    /**
     * The script language.
     */
    protected String scriptLang = "painless";

    /**
     * The script code.
     */
    protected String scriptCode = null;

    /**
     * A function to find document IDs.
     */
    protected Function<Map<String, Object>, String[]> idFinder = params -> {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        final Object url = params.get("url");
        if (url == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        final SearchResponse response = client.prepareSearch(index)
                .setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url))
                .setFetchSource(false)
                .setSize(fessConfig.getPageScoreBoosterMaxFetchSizeAsInteger())
                .execute()
                .actionGet(requestTimeout);
        return Arrays.stream(response.getHits().getHits()).map(SearchHit::getId).toArray(n -> new String[n]);
    };

    /**
     * A function to handle requests.
     */
    protected Function<Map<String, Object>, Long> requestHandler = params -> {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String[] ids = idFinder.apply(params);
        if (ids.length == 0) {
            return 0L;
        }
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        if (bulkRequestBuilder == null) {
            bulkRequestBuilder = client.prepareBulk();
        }
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        for (final String id : ids) {
            bulkRequestBuilder.add(client.prepareUpdate()
                    .setIndex(index)
                    .setId(id)
                    .setScript(new Script(ScriptType.INLINE, scriptLang, scriptCode, params)));
        }
        if (bulkRequestBuilder.numberOfActions() > requestCacheSize) {
            flush();
        }
        return (long) ids.length;
    };

    /**
     * Processes the score boosting.
     * @return The number of processed documents.
     */
    public abstract long process();

    /**
     * Enables this score booster.
     */
    protected void enable() {
        final ScoreUpdater scoreUpdater = ComponentUtil.getComponent("scoreUpdater");
        scoreUpdater.addScoreBooster(this);
    }

    /**
     * Updates the score of documents.
     * @param params The parameters for the update.
     * @return The number of updated documents.
     */
    protected long updateScore(final Map<String, Object> params) {
        return requestHandler.apply(params);
    }

    /**
     * Creates an update request builder.
     * @return The update request builder.
     */
    protected UpdateRequestBuilder createUpdateRequestBuilder() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient().prepareUpdate().setIndex(fessConfig.getIndexDocumentSearchIndex());
    }

    /**
     * Flushes the bulk request builder.
     */
    protected void flush() {
        if (bulkRequestBuilder != null) {
            final BulkResponse response = bulkRequestBuilder.execute().actionGet(requestTimeout);
            if (response.hasFailures()) {
                logger.warn("Failed to update scores: {}", response.buildFailureMessage());
            }
            bulkRequestBuilder = null;
        }
    }

    /**
     * Gets the priority of this score booster.
     * @return The priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this score booster.
     * @param priority The priority.
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Sets the request timeout.
     * @param bulkRequestTimeout The request timeout.
     */
    public void setRequestTimeout(final String bulkRequestTimeout) {
        requestTimeout = bulkRequestTimeout;
    }

    /**
     * Sets the request cache size.
     * @param requestCacheSize The request cache size.
     */
    public void setRequestCacheSize(final int requestCacheSize) {
        this.requestCacheSize = requestCacheSize;
    }

    /**
     * Sets the script language.
     * @param scriptLang The script language.
     */
    public void setScriptLang(final String scriptLang) {
        this.scriptLang = scriptLang;
    }

    /**
     * Sets the script code.
     * @param scriptCode The script code.
     */
    public void setScriptCode(final String scriptCode) {
        this.scriptCode = scriptCode;
    }
}