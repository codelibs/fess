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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.helper.NotificationHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.LogNotificationPostcard;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogNotificationBuffer.LogNotificationEvent;
import org.dbflute.mail.send.hook.SMailCallbackContext;
import org.lastaflute.core.mail.Postbox;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.sort.SortOrder;

/**
 * Job for sending log notifications.
 */
public class LogNotificationJob {

    /**
     * Default constructor.
     */
    public LogNotificationJob() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(LogNotificationJob.class);

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private static final int MAX_DETAILS_LENGTH = 4000;

    /**
     * Executes the log notification job.
     *
     * @return the execution result
     */
    public String execute() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (!fessConfig.isLogNotificationEnabled()) {
            return "Log notification disabled.";
        }

        if (!fessConfig.hasNotification()) {
            return "No notification targets configured.";
        }

        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        final String indexName = fessConfig.getIndexLogIndex() + ".notification_queue";

        if (!client.existsIndex(indexName)) {
            return "No log notifications.";
        }

        final String hostname = ComponentUtil.getSystemHelper().getHostname();
        final SearchResponse searchResponse = client.prepareSearch(indexName)
                .setQuery(QueryBuilders.termQuery("hostname", hostname))
                .setSize(1000)
                .addSort("timestamp", SortOrder.ASC)
                .execute()
                .actionGet(fessConfig.getIndexSearchTimeout());

        final SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return "No log notifications.";
        }

        final List<LogNotificationEvent> events = new ArrayList<>();
        final List<String> docIds = new ArrayList<>();
        for (final SearchHit hit : hits) {
            final Map<String, Object> source = hit.getSourceAsMap();
            events.add(new LogNotificationEvent(((Number) source.get("timestamp")).longValue(), (String) source.get("level"),
                    (String) source.get("loggerName"), (String) source.get("message"), (String) source.get("throwable")));
            docIds.add(hit.getId());
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Map<String, List<LogNotificationEvent>> eventsByLevel =
                events.stream().collect(Collectors.groupingBy(LogNotificationEvent::getLevel));

        for (final Map.Entry<String, List<LogNotificationEvent>> entry : eventsByLevel.entrySet()) {
            final String level = entry.getKey();
            final List<LogNotificationEvent> levelEvents = entry.getValue();
            final String details = formatDetails(levelEvents);

            final String toStrs = fessConfig.getNotificationTo();
            final String[] toAddresses;
            if (StringUtil.isNotBlank(toStrs)) {
                toAddresses = toStrs.split(",");
            } else {
                toAddresses = StringUtil.EMPTY_STRINGS;
            }

            final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
            final NotificationHelper notificationHelper = ComponentUtil.getNotificationHelper();
            try {
                SMailCallbackContext.setPreparedMessageHookOnThread(notificationHelper::send);
                LogNotificationPostcard.droppedInto(postbox, postcard -> {
                    postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                    postcard.addReplyTo(fessConfig.getMailReturnPath());
                    if (toAddresses.length > 0) {
                        stream(toAddresses).of(stream -> stream.map(String::trim).forEach(address -> {
                            postcard.addTo(address);
                        }));
                    } else {
                        postcard.addTo(fessConfig.getMailFromAddress());
                        postcard.dryrun();
                    }
                    postcard.setHostname(systemHelper.getHostname());
                    postcard.setLevel(level);
                    postcard.setCount(String.valueOf(levelEvents.size()));
                    postcard.setInterval(String.valueOf(fessConfig.getLogNotificationIntervalAsInteger()));
                    postcard.setDetails(details);
                });
            } catch (final Exception e) {
                logger.warn("Failed to send log notification.", e);
            } finally {
                SMailCallbackContext.clearPreparedMessageHookOnThread();
            }
        }

        final BulkRequestBuilder bulkDelete = client.prepareBulk();
        for (final String docId : docIds) {
            bulkDelete.add(client.prepareDelete().setIndex(indexName).setId(docId));
        }
        bulkDelete.execute().actionGet(fessConfig.getIndexSearchTimeout());

        return "Sent log notifications: " + events.size() + " events.";
    }

    /**
     * Formats a list of log notification events into a human-readable string.
     *
     * @param events the list of log notification events
     * @return the formatted details string, truncated to 4000 characters
     */
    protected String formatDetails(final List<LogNotificationEvent> events) {
        final StringBuilder sb = new StringBuilder();
        for (final LogNotificationEvent event : events) {
            final String timestamp = TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(event.getTimestamp()));
            sb.append('[')
                    .append(timestamp)
                    .append("] ")
                    .append(event.getLevel())
                    .append(' ')
                    .append(event.getLoggerName())
                    .append(" - ")
                    .append(event.getMessage())
                    .append('\n');
            if (StringUtil.isNotBlank(event.getThrowable())) {
                sb.append("  ").append(event.getThrowable()).append('\n');
            }
            if (sb.length() > MAX_DETAILS_LENGTH) {
                sb.setLength(MAX_DETAILS_LENGTH);
                break;
            }
        }
        return sb.toString();
    }
}
