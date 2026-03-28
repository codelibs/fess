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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Helper for inter-instance coordination via OpenSearch.
 * Provides heartbeat registration, distributed operation locking, and event notification
 * to prevent concurrent execution of maintenance operations across multiple Fess instances.
 */
public class CoordinatorHelper {

    private static final Logger logger = LogManager.getLogger(CoordinatorHelper.class);

    private static final String INDEX_NAME = "fess_config.coordinator";

    private static final String TYPE_HEARTBEAT = "heartbeat";

    private static final String TYPE_OPERATION = "operation";

    private static final String TYPE_EVENT = "event";

    private static final String STATUS_ACTIVE = "active";

    private static final String STATUS_RUNNING = "running";

    private static final String TARGET_ALL = "*";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String instanceId;

    private TimeoutTask pollTask;

    private long lastEventCheckTime;

    private final Map<String, List<Consumer<EventInfo>>> eventHandlers = new ConcurrentHashMap<>();

    /**
     * Default constructor.
     */
    public CoordinatorHelper() {
        // Default constructor
    }

    /**
     * Initializes the coordinator by sending an initial heartbeat and starting the poll loop.
     */
    @PostConstruct
    public void init() {
        instanceId = ComponentUtil.getSystemHelper().getInstanceId();
        lastEventCheckTime = System.currentTimeMillis();

        sendHeartbeat();

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int interval = fessConfig.getCoordinatorPollIntervalAsInteger();
        pollTask = TimeoutManager.getInstance().addTimeoutTarget(this::poll, interval, true);

        if (logger.isInfoEnabled()) {
            logger.info("CoordinatorHelper started: instanceId={}", instanceId);
        }
    }

    /**
     * Stops the poll loop and removes the heartbeat document on shutdown.
     */
    @PreDestroy
    public void destroy() {
        if (pollTask != null) {
            pollTask.cancel();
        }
        try {
            removeHeartbeat();
        } catch (final Exception e) {
            logger.debug("Failed to remove heartbeat on shutdown.", e);
        }
        if (logger.isInfoEnabled()) {
            logger.info("CoordinatorHelper stopped: instanceId={}", instanceId);
        }
    }

    // ===================================================================================
    //                                                                           Heartbeat
    //                                                                           =========

    /**
     * Sends a heartbeat document to OpenSearch to indicate this instance is active.
     */
    public void sendHeartbeat() {
        final long now = System.currentTimeMillis();
        final long ttl = ComponentUtil.getFessConfig().getCoordinatorHeartbeatTtlAsInteger().longValue();
        final String hostname = ComponentUtil.getSystemHelper().getHostname();
        final String targetName = ComponentUtil.getFessConfig().getSchedulerTargetName();

        final String body = toJson(Map.of( //
                "type", TYPE_HEARTBEAT, //
                "instanceId", instanceId, //
                "hostname", hostname, //
                "name", StringUtil.isNotBlank(targetName) ? targetName : hostname, //
                "status", STATUS_ACTIVE, //
                "createdTime", now, //
                "expiredTime", now + ttl));

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .put("/" + getIndexName() + "/_doc/" + instanceId + "?refresh=true") //
                .body(body)
                .execute()) {
            if (response.getHttpStatusCode() != 200 && response.getHttpStatusCode() != 201) {
                logger.warn("Failed to send heartbeat: status={}", response.getHttpStatusCode());
            }
        } catch (final Exception e) {
            logger.debug("Failed to send heartbeat.", e);
        }
    }

    /**
     * Removes the heartbeat document for this instance from OpenSearch.
     */
    protected void removeHeartbeat() {
        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .delete("/" + getIndexName() + "/_doc/" + instanceId + "?refresh=true") //
                .execute()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Removed heartbeat: status={}", response.getHttpStatusCode());
            }
        } catch (final Exception e) {
            logger.debug("Failed to remove heartbeat.", e);
        }
    }

    /**
     * Returns a list of currently active instances based on non-expired heartbeat documents.
     *
     * @return the list of active instances.
     */
    public List<InstanceInfo> getActiveInstances() {
        final List<InstanceInfo> instances = new ArrayList<>();
        final long now = System.currentTimeMillis();
        final String query = toJson(Map.of( //
                "query", Map.of("bool", Map.of("must", List.of( //
                        Map.of("term", Map.of("type", TYPE_HEARTBEAT)), //
                        Map.of("range", Map.of("expiredTime", Map.of("gte", now)))))), //
                "size", 100));

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .post("/" + getIndexName() + "/_search") //
                .body(query)
                .execute()) {
            if (response.getHttpStatusCode() == 200) {
                final Map<String, Object> result = parseJson(response.getContentAsString());
                final Map<String, Object> hits = getMapValue(result, "hits");
                if (hits != null) {
                    final List<Map<String, Object>> hitList = getListValue(hits, "hits");
                    if (hitList != null) {
                        for (final Map<String, Object> hit : hitList) {
                            final Map<String, Object> source = getMapValue(hit, "_source");
                            if (source != null) {
                                final InstanceInfo info = new InstanceInfo();
                                info.instanceId = getStringValue(source, "instanceId");
                                info.hostname = getStringValue(source, "hostname");
                                info.name = getStringValue(source, "name");
                                info.lastSeen = getLongValue(source, "createdTime");
                                instances.add(info);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to get active instances.", e);
        }
        return instances;
    }

    /**
     * Checks whether the specified instance is currently active.
     *
     * @param targetInstanceId the instance ID to check.
     * @return {@code true} if the instance is active.
     */
    public boolean isInstanceActive(final String targetInstanceId) {
        return getActiveInstances().stream().anyMatch(i -> i.instanceId.equals(targetInstanceId));
    }

    // ===================================================================================
    //                                                                    Operation State
    //                                                                    ================

    /**
     * Attempts to acquire a distributed lock for the specified operation.
     *
     * @param operationName the operation name used as the lock document ID.
     * @return {@code true} if the lock was acquired.
     */
    public boolean tryStartOperation(final String operationName) {
        return tryStartOperation(operationName, null);
    }

    /**
     * Attempts to acquire a distributed lock for the specified operation with optional data.
     *
     * @param operationName the operation name used as the lock document ID.
     * @param data optional data to store with the operation document.
     * @return {@code true} if the lock was acquired.
     */
    public boolean tryStartOperation(final String operationName, final String data) {
        final int maxRetry = ComponentUtil.getFessConfig().getCoordinatorOperationRetryAsInteger();
        return tryStartOperation(operationName, data, maxRetry);
    }

    /**
     * Internal method to acquire a distributed lock with retry control.
     *
     * @param operationName the operation name used as the lock document ID.
     * @param data optional data to store with the operation document.
     * @param remainingRetries the number of remaining retry attempts for stale lock cleanup.
     * @return {@code true} if the lock was acquired.
     */
    protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
        final long now = System.currentTimeMillis();
        final long ttl = ComponentUtil.getFessConfig().getCoordinatorOperationTtlAsInteger().longValue();
        final String hostname = ComponentUtil.getSystemHelper().getHostname();

        final Map<String, Object> bodyMap = new java.util.LinkedHashMap<>();
        bodyMap.put("type", TYPE_OPERATION);
        bodyMap.put("name", operationName);
        bodyMap.put("instanceId", instanceId);
        bodyMap.put("hostname", hostname);
        bodyMap.put("status", STATUS_RUNNING);
        bodyMap.put("createdTime", now);
        bodyMap.put("expiredTime", now + ttl);
        if (data != null) {
            bodyMap.put("data", data);
        }

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .put("/" + getIndexName() + "/_create/" + operationName + "?refresh=true") //
                .body(toJson(bodyMap))
                .execute()) {
            if (response.getHttpStatusCode() == 201) {
                if (logger.isInfoEnabled()) {
                    logger.info("Acquired operation lock: operation={}, instanceId={}", operationName, instanceId);
                }
                return true;
            }
        } catch (final Exception e) {
            logger.debug("Failed to create operation document: operation={}", operationName, e);
        }

        if (remainingRetries <= 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("No remaining retries for operation lock: operation={}", operationName);
            }
            return false;
        }

        // Document already exists - check if it's stale
        return tryCleanupAndRetry(operationName, data, remainingRetries);
    }

    /**
     * Checks if the existing operation lock is stale (expired or owner inactive) and retries acquisition.
     *
     * @param operationName the operation name.
     * @param data optional data for the operation.
     * @param remainingRetries the number of remaining retry attempts.
     * @return {@code true} if the lock was acquired after cleanup.
     */
    protected boolean tryCleanupAndRetry(final String operationName, final String data, final int remainingRetries) {
        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .get("/" + getIndexName() + "/_doc/" + operationName) //
                .execute()) {
            if (response.getHttpStatusCode() != 200) {
                return false;
            }

            final Map<String, Object> result = parseJson(response.getContentAsString());
            final Map<String, Object> source = getMapValue(result, "_source");
            if (source == null) {
                return false;
            }

            final long expiredTime = getLongValue(source, "expiredTime");
            final String ownerInstanceId = getStringValue(source, "instanceId");
            final long now = System.currentTimeMillis();

            // Check if expired or owner is no longer active
            if (expiredTime < now || !isInstanceActive(ownerInstanceId)) {
                final long seqNo = getLongValue(result, "_seq_no");
                final long primaryTerm = getLongValue(result, "_primary_term");

                // Try to delete with optimistic concurrency
                try (CurlResponse deleteResponse = ComponentUtil.getCurlHelper() //
                        .delete("/" + getIndexName() + "/_doc/" + operationName //
                                + "?refresh=true&if_seq_no=" + seqNo + "&if_primary_term=" + primaryTerm) //
                        .execute()) {
                    if (deleteResponse.getHttpStatusCode() == 200) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Cleaned up stale operation: operation={}, previousOwner={}", operationName, ownerInstanceId);
                        }
                        // Retry creation
                        return tryStartOperation(operationName, data, remainingRetries - 1);
                    }
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to check existing operation: operation={}", operationName, e);
        }
        return false;
    }

    /**
     * Releases the operation lock by deleting the operation document.
     * Safe to call multiple times; does nothing if the document is already deleted.
     *
     * @param operationName the operation name whose lock should be released.
     */
    public void completeOperation(final String operationName) {
        try (CurlResponse getResponse = ComponentUtil.getCurlHelper() //
                .get("/" + getIndexName() + "/_doc/" + operationName) //
                .execute()) {
            if (getResponse.getHttpStatusCode() != 200) {
                logger.debug("Operation document not found: operation={}", operationName);
                return;
            }
            final Map<String, Object> result = parseJson(getResponse.getContentAsString());
            final Map<String, Object> source = getMapValue(result, "_source");
            if (source == null) {
                return;
            }

            // Verify ownership before deleting
            final String ownerInstanceId = getStringValue(source, "instanceId");
            if (!instanceId.equals(ownerInstanceId)) {
                logger.warn("Cannot release operation lock owned by another instance: operation={}, owner={}", operationName,
                        ownerInstanceId);
                return;
            }

            // Delete with optimistic concurrency control
            final long seqNo = getLongValue(result, "_seq_no");
            final long primaryTerm = getLongValue(result, "_primary_term");
            try (CurlResponse deleteResponse = ComponentUtil.getCurlHelper() //
                    .delete("/" + getIndexName() + "/_doc/" + operationName //
                            + "?refresh=true&if_seq_no=" + seqNo + "&if_primary_term=" + primaryTerm) //
                    .execute()) {
                if (deleteResponse.getHttpStatusCode() == 200) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Released operation lock: operation={}, instanceId={}", operationName, instanceId);
                    }
                } else {
                    logger.warn("Failed to release operation lock: operation={}, status={}", operationName,
                            deleteResponse.getHttpStatusCode());
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to release operation lock: operation={}", operationName, e);
        }
    }

    /**
     * Checks whether the specified operation is currently running (lock held by an active instance).
     *
     * @param operationName the operation name to check.
     * @return {@code true} if the operation is running.
     */
    public boolean isOperationRunning(final String operationName) {
        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .get("/" + getIndexName() + "/_doc/" + operationName) //
                .execute()) {
            if (response.getHttpStatusCode() != 200) {
                return false;
            }

            final Map<String, Object> result = parseJson(response.getContentAsString());
            final Boolean found = (Boolean) result.get("found");
            if (found == null || !found) {
                return false;
            }

            final Map<String, Object> source = getMapValue(result, "_source");
            if (source == null) {
                return false;
            }

            final long expiredTime = getLongValue(source, "expiredTime");
            if (expiredTime < System.currentTimeMillis()) {
                return false;
            }

            final String ownerInstanceId = getStringValue(source, "instanceId");
            return isInstanceActive(ownerInstanceId);
        } catch (final Exception e) {
            logger.debug("Failed to check operation status: operation={}", operationName, e);
        }
        return false;
    }

    /**
     * Retrieves information about the specified operation.
     *
     * @param operationName the operation name to look up.
     * @return an {@link Optional} containing the operation info, or empty if not found.
     */
    public Optional<OperationInfo> getOperationInfo(final String operationName) {
        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .get("/" + getIndexName() + "/_doc/" + operationName) //
                .execute()) {
            if (response.getHttpStatusCode() == 200) {
                final Map<String, Object> result = parseJson(response.getContentAsString());
                final Boolean found = (Boolean) result.get("found");
                if (found != null && found) {
                    final Map<String, Object> source = getMapValue(result, "_source");
                    if (source != null) {
                        final OperationInfo info = new OperationInfo();
                        info.name = getStringValue(source, "name");
                        info.instanceId = getStringValue(source, "instanceId");
                        info.hostname = getStringValue(source, "hostname");
                        info.status = getStringValue(source, "status");
                        info.createdTime = getLongValue(source, "createdTime");
                        info.data = getStringValue(source, "data");
                        return Optional.of(info);
                    }
                }
            }
        } catch (final Exception e) {
            logger.debug("Failed to get operation info: operation={}", operationName, e);
        }
        return Optional.empty();
    }

    // ===================================================================================
    //                                                                              Event
    //                                                                              ======

    /**
     * Publishes an event to all instances.
     *
     * @param eventName the event name.
     * @param data optional event data.
     */
    public void publishEvent(final String eventName, final String data) {
        publishEvent(eventName, TARGET_ALL, data);
    }

    /**
     * Publishes an event to a specific instance or all instances.
     *
     * @param eventName the event name.
     * @param targetInstanceId the target instance ID, or {@code "*"} for all instances.
     * @param data optional event data.
     */
    public void publishEvent(final String eventName, final String targetInstanceId, final String data) {
        final long now = System.currentTimeMillis();
        final long ttl = ComponentUtil.getFessConfig().getCoordinatorEventTtlAsInteger().longValue();

        final Map<String, Object> bodyMap = new java.util.LinkedHashMap<>();
        bodyMap.put("type", TYPE_EVENT);
        bodyMap.put("name", eventName);
        bodyMap.put("instanceId", instanceId);
        bodyMap.put("targetInstanceId", targetInstanceId);
        bodyMap.put("createdTime", now);
        bodyMap.put("expiredTime", now + ttl);
        if (data != null) {
            bodyMap.put("data", data);
        }

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .post("/" + getIndexName() + "/_doc?refresh=true") //
                .body(toJson(bodyMap))
                .execute()) {
            if (response.getHttpStatusCode() != 201) {
                logger.warn("Failed to publish event: eventName={}, status={}", eventName, response.getHttpStatusCode());
            }
        } catch (final Exception e) {
            logger.warn("Failed to publish event: eventName={}", eventName, e);
        }
    }

    /**
     * Registers an event handler for the specified event name.
     *
     * @param eventName the event name to handle.
     * @param handler the consumer to invoke when the event is received.
     */
    public void addEventHandler(final String eventName, final Consumer<EventInfo> handler) {
        eventHandlers.computeIfAbsent(eventName, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Fetches new events from OpenSearch that were created after the last check time.
     *
     * @return the list of new events.
     */
    protected List<EventInfo> fetchNewEvents() {
        final List<EventInfo> events = new ArrayList<>();
        final String query = toJson(Map.of( //
                "query", Map.of("bool", Map.of( //
                        "must", List.of( //
                                Map.of("term", Map.of("type", TYPE_EVENT)), //
                                Map.of("range", Map.of("createdTime", Map.of("gt", lastEventCheckTime)))), //
                        "should", List.of( //
                                Map.of("term", Map.of("targetInstanceId", TARGET_ALL)), //
                                Map.of("term", Map.of("targetInstanceId", instanceId))), //
                        "minimum_should_match", 1, //
                        "must_not", List.of( //
                                Map.of("term", Map.of("instanceId", instanceId))))), //
                "size", 100, //
                "sort", List.of(Map.of("createdTime", "asc"))));

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .post("/" + getIndexName() + "/_search") //
                .body(query)
                .execute()) {
            if (response.getHttpStatusCode() == 200) {
                final Map<String, Object> result = parseJson(response.getContentAsString());
                final Map<String, Object> hits = getMapValue(result, "hits");
                if (hits != null) {
                    final List<Map<String, Object>> hitList = getListValue(hits, "hits");
                    if (hitList != null) {
                        for (final Map<String, Object> hit : hitList) {
                            final Map<String, Object> source = getMapValue(hit, "_source");
                            if (source != null) {
                                final EventInfo info = new EventInfo();
                                info.name = getStringValue(source, "name");
                                info.instanceId = getStringValue(source, "instanceId");
                                info.targetInstanceId = getStringValue(source, "targetInstanceId");
                                info.createdTime = getLongValue(source, "createdTime");
                                info.data = getStringValue(source, "data");
                                events.add(info);

                                if (info.createdTime >= lastEventCheckTime) {
                                    lastEventCheckTime = info.createdTime + 1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            logger.debug("Failed to fetch events.", e);
        }
        return events;
    }

    // ===================================================================================
    //                                                                         Poll Loop
    //                                                                         ===========

    /**
     * Periodic poll that sends a heartbeat, processes new events, and cleans up expired documents.
     */
    protected void poll() {
        try {
            sendHeartbeat();
        } catch (final Exception e) {
            logger.debug("Failed to send heartbeat in poll.", e);
        }

        try {
            final List<EventInfo> events = fetchNewEvents();
            for (final EventInfo event : events) {
                dispatchEvent(event);
            }
        } catch (final Exception e) {
            logger.debug("Failed to process events in poll.", e);
        }

        try {
            cleanupExpiredDocuments();
        } catch (final Exception e) {
            logger.debug("Failed to cleanup expired documents in poll.", e);
        }
    }

    /**
     * Dispatches an event to all registered handlers for the event name.
     *
     * @param event the event to dispatch.
     */
    protected void dispatchEvent(final EventInfo event) {
        final List<Consumer<EventInfo>> handlers = eventHandlers.get(event.name);
        if (handlers != null) {
            for (final Consumer<EventInfo> handler : handlers) {
                try {
                    handler.accept(event);
                } catch (final Exception e) {
                    logger.warn("Failed to handle event: eventName={}", event.name, e);
                }
            }
        }
    }

    /**
     * Deletes expired heartbeat, operation, and event documents from the coordinator index.
     */
    protected void cleanupExpiredDocuments() {
        final long now = System.currentTimeMillis();
        final String query = toJson(Map.of( //
                "query", Map.of("range", Map.of("expiredTime", Map.of("lt", now)))));

        try (CurlResponse response = ComponentUtil.getCurlHelper() //
                .post("/" + getIndexName() + "/_delete_by_query?refresh=true") //
                .body(query)
                .execute()) {
            if (response.getHttpStatusCode() == 200) {
                final Map<String, Object> result = parseJson(response.getContentAsString());
                final Number deleted = (Number) result.get("deleted");
                if (deleted != null && deleted.longValue() > 0 && logger.isDebugEnabled()) {
                    logger.debug("Cleaned up expired documents: count={}", deleted);
                }
            }
        } catch (final Exception e) {
            logger.debug("Failed to cleanup expired documents.", e);
        }
    }

    // ===================================================================================
    //                                                                        Index Name
    //                                                                        ============

    /**
     * Returns the coordinator index name, adjusted for the configured index prefix.
     *
     * @return the coordinator index name.
     */
    protected String getIndexName() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return INDEX_NAME.replaceFirst("fess_config", fessConfig.getIndexConfigIndex());
    }

    // ===================================================================================
    //                                                                         JSON Util
    //                                                                         ===========

    /**
     * Serializes a map to a JSON string.
     *
     * @param map the map to serialize.
     * @return the JSON string, or {@code "{}"} on failure.
     */
    protected String toJson(final Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (final Exception e) {
            logger.warn("Failed to serialize JSON.", e);
            return "{}";
        }
    }

    /**
     * Parses a JSON string into a map.
     *
     * @param json the JSON string to parse.
     * @return the parsed map, or an empty map on failure.
     */
    protected Map<String, Object> parseJson(final String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (final Exception e) {
            logger.warn("Failed to parse JSON.", e);
            return Map.of();
        }
    }

    /**
     * Gets a nested map value from the given map.
     *
     * @param map the source map.
     * @param key the key to look up.
     * @return the map value, or {@code null} if not found or not a map.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMapValue(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    /**
     * Gets a list value from the given map.
     *
     * @param map the source map.
     * @param key the key to look up.
     * @return the list value, or {@code null} if not found or not a list.
     */
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> getListValue(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return null;
    }

    /**
     * Gets a string value from the given map.
     *
     * @param map the source map.
     * @param key the key to look up.
     * @return the string value, or {@code null} if not found.
     */
    protected String getStringValue(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * Gets a long value from the given map.
     *
     * @param map the source map.
     * @param key the key to look up.
     * @return the long value, or {@code 0L} if not found or not a number.
     */
    protected long getLongValue(final Map<String, Object> map, final String key) {
        final Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    // ===================================================================================
    //                                                                         Data Class
    //                                                                         ===========

    /**
     * Represents an active Fess instance discovered via heartbeat.
     */
    public static class InstanceInfo {
        /** Default constructor. */
        public InstanceInfo() {
        }

        /** The unique instance ID. */
        public String instanceId;
        /** The hostname of the instance. */
        public String hostname;
        /** The display name of the instance. */
        public String name;
        /** The timestamp when the instance was last seen. */
        public long lastSeen;
    }

    /**
     * Represents the state of a distributed operation lock.
     */
    public static class OperationInfo {
        /** Default constructor. */
        public OperationInfo() {
        }

        /** The operation name. */
        public String name;
        /** The instance ID that owns the lock. */
        public String instanceId;
        /** The hostname of the lock owner. */
        public String hostname;
        /** The operation status. */
        public String status;
        /** The time when the operation was started. */
        public long createdTime;
        /** Optional data associated with the operation. */
        public String data;
    }

    /**
     * Represents an inter-instance event notification.
     */
    public static class EventInfo {
        /** Default constructor. */
        public EventInfo() {
        }

        /** The event name. */
        public String name;
        /** The instance ID that published the event. */
        public String instanceId;
        /** The target instance ID, or {@code "*"} for all instances. */
        public String targetInstanceId;
        /** The time when the event was created. */
        public long createdTime;
        /** Optional data associated with the event. */
        public String data;
    }
}
