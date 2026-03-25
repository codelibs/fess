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
package org.codelibs.fess.timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutTarget;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogNotificationBuffer;
import org.codelibs.fess.util.LogNotificationBuffer.LogNotificationEvent;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;

/**
 * A timer target that periodically flushes buffered log notification events
 * to an OpenSearch index for downstream processing.
 */
public class LogNotificationTarget implements TimeoutTarget {

    /**
     * Default constructor.
     */
    public LogNotificationTarget() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(LogNotificationTarget.class);
    private static final String NOTIFICATION_QUEUE_INDEX = "fess_log.notification_queue";
    private static final int BATCH_SIZE = 100;
    private volatile boolean indexChecked = false;

    @Override
    public void expired() {
        final List<LogNotificationEvent> events = LogNotificationBuffer.getInstance().drainAll();
        if (events.isEmpty()) {
            return;
        }

        try {
            final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
            final String hostname = ComponentUtil.getSystemHelper().getHostname();
            final String indexName = resolveIndexName();

            ensureIndexExists(client, indexName);

            for (int i = 0; i < events.size(); i += BATCH_SIZE) {
                final List<LogNotificationEvent> batch = events.subList(i, Math.min(i + BATCH_SIZE, events.size()));
                final BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (final LogNotificationEvent event : batch) {
                    final Map<String, Object> source = new HashMap<>();
                    source.put("hostname", hostname);
                    source.put("level", event.getLevel());
                    source.put("loggerName", event.getLoggerName());
                    source.put("message", event.getMessage());
                    source.put("throwable", event.getThrowable() != null ? event.getThrowable() : "");
                    source.put("timestamp", event.getTimestamp());
                    bulkRequest.add(client.prepareIndex().setIndex(indexName).setSource(source));
                }
                final BulkResponse response = bulkRequest.execute().actionGet(30_000L);
                if (response.hasFailures()) {
                    logger.warn("Failed to write log notifications: {}", response.buildFailureMessage());
                }
            }
        } catch (final Exception e) {
            logger.debug("Failed to flush log notifications to OpenSearch.", e);
        }
    }

    /**
     * Flushes all buffered log notification events immediately.
     */
    public void flush() {
        expired();
    }

    private String resolveIndexName() {
        return ComponentUtil.getFessConfig().getIndexLogIndex() + ".notification_queue";
    }

    private void ensureIndexExists(final SearchEngineClient client, final String indexName) {
        if (!indexChecked) {
            if (!client.existsIndex(indexName)) {
                client.createIndex(NOTIFICATION_QUEUE_INDEX, indexName);
            }
            indexChecked = true;
        }
    }
}
