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

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.curl.io.ContentCache;
import org.codelibs.fess.helper.CoordinatorHelper.EventInfo;
import org.codelibs.fess.helper.CoordinatorHelper.InstanceInfo;
import org.codelibs.fess.helper.CoordinatorHelper.OperationInfo;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CoordinatorHelperTest extends UnitFessTestCase {

    private CoordinatorHelper coordinatorHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        coordinatorHelper = new CoordinatorHelper();
    }

    // ===================================================================================
    //                                                                     Test Helpers
    //                                                                     =============

    private CurlResponse createMockResponse(final int statusCode, final String body) {
        final CurlResponse response = new CurlResponse();
        response.setHttpStatusCode(statusCode);
        response.setEncoding("UTF-8");
        response.setContentCache(new ContentCache(body.getBytes(StandardCharsets.UTF_8)));
        return response;
    }

    private void setupMockFessConfig() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return "fess_config";
            }

            @Override
            public Integer getCoordinatorPollIntervalAsInteger() {
                return 60;
            }

            @Override
            public Integer getCoordinatorHeartbeatTtlAsInteger() {
                return 180000;
            }

            @Override
            public Integer getCoordinatorOperationTtlAsInteger() {
                return 7200000;
            }

            @Override
            public Integer getCoordinatorOperationRetryAsInteger() {
                return 3;
            }

            @Override
            public Integer getCoordinatorEventTtlAsInteger() {
                return 600000;
            }

            @Override
            public String getSchedulerTargetName() {
                return "";
            }
        });
    }

    /**
     * Creates a CoordinatorHelper that captures request bodies sent via CurlHelper.
     * The mock CurlHelper records the last request path and body for assertion.
     */
    private CoordinatorHelper createCapturingHelper(final String testInstanceId, final AtomicReference<String> capturedPath,
            final AtomicReference<String> capturedBody, final CurlResponse mockResponse) {
        final CurlHelper mockCurlHelper = new CurlHelper() {
            @Override
            public CurlRequest get(final String path) {
                return createCapturingRequest(path);
            }

            @Override
            public CurlRequest post(final String path) {
                return createCapturingRequest(path);
            }

            @Override
            public CurlRequest put(final String path) {
                return createCapturingRequest(path);
            }

            @Override
            public CurlRequest delete(final String path) {
                return createCapturingRequest(path);
            }

            private CurlRequest createCapturingRequest(final String path) {
                if (capturedPath != null) {
                    capturedPath.set(path);
                }
                return new CurlRequest(org.codelibs.curl.Curl.Method.GET, "http://localhost:9200") {
                    @Override
                    public CurlRequest body(final String body) {
                        if (capturedBody != null) {
                            capturedBody.set(body);
                        }
                        return this;
                    }

                    @Override
                    public CurlResponse execute() {
                        if (mockResponse != null) {
                            return mockResponse;
                        }
                        return createMockResponse(200, "{}");
                    }
                };
            }
        };
        ComponentUtil.register(mockCurlHelper, "curlHelper");
        ComponentUtil.register(new SystemHelper(), "systemHelper");

        final CoordinatorHelper helper = new CoordinatorHelper();
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("instanceId");
            field.setAccessible(true);
            field.set(helper, testInstanceId);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return helper;
    }

    private void setLastEventCheckTime(final CoordinatorHelper helper, final long time) {
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("lastEventCheckTime");
            field.setAccessible(true);
            field.set(helper, time);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long getLastEventCheckTime(final CoordinatorHelper helper) {
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("lastEventCheckTime");
            field.setAccessible(true);
            return (long) field.get(helper);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===================================================================================
    //                                                                         JSON Util
    //                                                                         ===========

    @Test
    public void test_toJson_simpleMap() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "heartbeat");
        map.put("instanceId", "test-instance");
        map.put("createdTime", 1000L);

        final String json = coordinatorHelper.toJson(map);
        assertNotNull(json);
        assertTrue(json.contains("\"type\":\"heartbeat\""));
        assertTrue(json.contains("\"instanceId\":\"test-instance\""));
        assertTrue(json.contains("\"createdTime\":1000"));
    }

    @Test
    public void test_toJson_emptyMap() {
        final String json = coordinatorHelper.toJson(Map.of());
        assertEquals("{}", json);
    }

    @Test
    public void test_toJson_nestedMap() {
        final Map<String, Object> map = Map.of("query", Map.of("term", Map.of("type", "heartbeat")));
        final String json = coordinatorHelper.toJson(map);
        assertNotNull(json);
        assertTrue(json.contains("\"query\""));
        assertTrue(json.contains("\"term\""));
        assertTrue(json.contains("\"type\":\"heartbeat\""));
    }

    @Test
    public void test_parseJson_valid() {
        final String json = "{\"type\":\"heartbeat\",\"instanceId\":\"test\",\"createdTime\":12345}";
        final Map<String, Object> result = coordinatorHelper.parseJson(json);
        assertEquals("heartbeat", result.get("type"));
        assertEquals("test", result.get("instanceId"));
        assertEquals(12345, result.get("createdTime"));
    }

    @Test
    public void test_parseJson_empty() {
        final Map<String, Object> result = coordinatorHelper.parseJson("{}");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_parseJson_invalid() {
        final Map<String, Object> result = coordinatorHelper.parseJson("invalid json");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_parseJson_null() {
        final Map<String, Object> result = coordinatorHelper.parseJson(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_toJson_and_parseJson_roundtrip() {
        final Map<String, Object> original = new LinkedHashMap<>();
        original.put("type", "operation");
        original.put("name", "reindex");
        original.put("status", "running");
        original.put("createdTime", 1234567890L);

        final String json = coordinatorHelper.toJson(original);
        final Map<String, Object> parsed = coordinatorHelper.parseJson(json);

        assertEquals("operation", parsed.get("type"));
        assertEquals("reindex", parsed.get("name"));
        assertEquals("running", parsed.get("status"));
        assertEquals(1234567890, ((Number) parsed.get("createdTime")).longValue());
    }

    // ===================================================================================
    //                                                                       Map Util
    //                                                                       ==========

    @Test
    public void test_getMapValue_exists() {
        final Map<String, Object> inner = Map.of("key", "value");
        final Map<String, Object> map = Map.of("nested", inner);
        final Map<String, Object> result = coordinatorHelper.getMapValue(map, "nested");
        assertNotNull(result);
        assertEquals("value", result.get("key"));
    }

    @Test
    public void test_getMapValue_notMap() {
        final Map<String, Object> map = Map.of("key", "string_value");
        assertNull(coordinatorHelper.getMapValue(map, "key"));
    }

    @Test
    public void test_getMapValue_missing() {
        final Map<String, Object> map = Map.of("key", "value");
        assertNull(coordinatorHelper.getMapValue(map, "nonexistent"));
    }

    @Test
    public void test_getListValue_exists() {
        final List<Map<String, Object>> list = List.of(Map.of("id", "1"), Map.of("id", "2"));
        final Map<String, Object> map = Map.of("items", list);
        final List<Map<String, Object>> result = coordinatorHelper.getListValue(map, "items");
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void test_getListValue_notList() {
        final Map<String, Object> map = Map.of("key", "not_a_list");
        assertNull(coordinatorHelper.getListValue(map, "key"));
    }

    @Test
    public void test_getListValue_missing() {
        final Map<String, Object> map = Map.of("key", "value");
        assertNull(coordinatorHelper.getListValue(map, "nonexistent"));
    }

    @Test
    public void test_getStringValue_exists() {
        final Map<String, Object> map = Map.of("name", "test-name");
        assertEquals("test-name", coordinatorHelper.getStringValue(map, "name"));
    }

    @Test
    public void test_getStringValue_numeric() {
        final Map<String, Object> map = Map.of("count", 42);
        assertEquals("42", coordinatorHelper.getStringValue(map, "count"));
    }

    @Test
    public void test_getStringValue_null() {
        final Map<String, Object> map = new HashMap<>();
        map.put("key", null);
        assertNull(coordinatorHelper.getStringValue(map, "key"));
    }

    @Test
    public void test_getStringValue_missing() {
        final Map<String, Object> map = Map.of("key", "value");
        assertNull(coordinatorHelper.getStringValue(map, "nonexistent"));
    }

    @Test
    public void test_getLongValue_integer() {
        final Map<String, Object> map = Map.of("time", 12345);
        assertEquals(12345L, coordinatorHelper.getLongValue(map, "time"));
    }

    @Test
    public void test_getLongValue_long() {
        final Map<String, Object> map = Map.of("time", 9876543210L);
        assertEquals(9876543210L, coordinatorHelper.getLongValue(map, "time"));
    }

    @Test
    public void test_getLongValue_double() {
        final Map<String, Object> map = Map.of("time", 123.45);
        assertEquals(123L, coordinatorHelper.getLongValue(map, "time"));
    }

    @Test
    public void test_getLongValue_notNumber() {
        final Map<String, Object> map = Map.of("time", "not_a_number");
        assertEquals(0L, coordinatorHelper.getLongValue(map, "time"));
    }

    @Test
    public void test_getLongValue_missing() {
        final Map<String, Object> map = Map.of("key", "value");
        assertEquals(0L, coordinatorHelper.getLongValue(map, "key"));
    }

    // ===================================================================================
    //                                                                       Index Name
    //                                                                       ============

    @Test
    public void test_getIndexName_default() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return "fess_config";
            }
        });
        assertEquals("fess_config.coordinator", coordinatorHelper.getIndexName());
    }

    @Test
    public void test_getIndexName_custom() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return "my_custom_config";
            }
        });
        assertEquals("my_custom_config.coordinator", coordinatorHelper.getIndexName());
    }

    // ===================================================================================
    //                                                                      Data Classes
    //                                                                      =============

    @Test
    public void test_InstanceInfo() {
        final InstanceInfo info = new InstanceInfo();
        info.instanceId = "node1@host1";
        info.hostname = "host1";
        info.name = "node1";
        info.lastSeen = 1000L;

        assertEquals("node1@host1", info.instanceId);
        assertEquals("host1", info.hostname);
        assertEquals("node1", info.name);
        assertEquals(1000L, info.lastSeen);
    }

    @Test
    public void test_OperationInfo() {
        final OperationInfo info = new OperationInfo();
        info.name = "reindex";
        info.instanceId = "node1@host1";
        info.hostname = "host1";
        info.status = "running";
        info.createdTime = 2000L;
        info.data = "{\"target\":\"fess\"}";

        assertEquals("reindex", info.name);
        assertEquals("node1@host1", info.instanceId);
        assertEquals("host1", info.hostname);
        assertEquals("running", info.status);
        assertEquals(2000L, info.createdTime);
        assertEquals("{\"target\":\"fess\"}", info.data);
    }

    @Test
    public void test_EventInfo() {
        final EventInfo info = new EventInfo();
        info.name = "config_updated";
        info.instanceId = "node1@host1";
        info.targetInstanceId = "*";
        info.createdTime = 3000L;
        info.data = "web_config";

        assertEquals("config_updated", info.name);
        assertEquals("node1@host1", info.instanceId);
        assertEquals("*", info.targetInstanceId);
        assertEquals(3000L, info.createdTime);
        assertEquals("web_config", info.data);
    }

    // ===================================================================================
    //                                                                    Event Handlers
    //                                                                    ================

    @Test
    public void test_addEventHandler_singleHandler() {
        final AtomicBoolean called = new AtomicBoolean(false);
        coordinatorHelper.addEventHandler("config_updated", event -> called.set(true));

        final EventInfo event = new EventInfo();
        event.name = "config_updated";
        coordinatorHelper.dispatchEvent(event);

        assertTrue(called.get());
    }

    @Test
    public void test_addEventHandler_multipleHandlers() {
        final AtomicInteger count = new AtomicInteger(0);
        coordinatorHelper.addEventHandler("config_updated", event -> count.incrementAndGet());
        coordinatorHelper.addEventHandler("config_updated", event -> count.incrementAndGet());

        final EventInfo event = new EventInfo();
        event.name = "config_updated";
        coordinatorHelper.dispatchEvent(event);

        assertEquals(2, count.get());
    }

    @Test
    public void test_addEventHandler_differentEvents() {
        final AtomicReference<String> received = new AtomicReference<>();
        coordinatorHelper.addEventHandler("config_updated", event -> received.set("config"));
        coordinatorHelper.addEventHandler("dict_updated", event -> received.set("dict"));

        final EventInfo configEvent = new EventInfo();
        configEvent.name = "config_updated";
        coordinatorHelper.dispatchEvent(configEvent);
        assertEquals("config", received.get());

        final EventInfo dictEvent = new EventInfo();
        dictEvent.name = "dict_updated";
        coordinatorHelper.dispatchEvent(dictEvent);
        assertEquals("dict", received.get());
    }

    @Test
    public void test_dispatchEvent_noHandler() {
        // Should not throw exception
        final EventInfo event = new EventInfo();
        event.name = "unknown_event";
        coordinatorHelper.dispatchEvent(event);
    }

    @Test
    public void test_dispatchEvent_handlerThrowsException() {
        final AtomicBoolean secondCalled = new AtomicBoolean(false);
        coordinatorHelper.addEventHandler("test_event", event -> {
            throw new RuntimeException("test error");
        });
        coordinatorHelper.addEventHandler("test_event", event -> secondCalled.set(true));

        final EventInfo event = new EventInfo();
        event.name = "test_event";
        coordinatorHelper.dispatchEvent(event);

        // Second handler should still be called even if first throws
        assertTrue(secondCalled.get());
    }

    @Test
    public void test_dispatchEvent_eventData() {
        final AtomicReference<String> receivedData = new AtomicReference<>();
        final AtomicReference<String> receivedInstanceId = new AtomicReference<>();
        coordinatorHelper.addEventHandler("config_updated", event -> {
            receivedData.set(event.data);
            receivedInstanceId.set(event.instanceId);
        });

        final EventInfo event = new EventInfo();
        event.name = "config_updated";
        event.data = "web_config";
        event.instanceId = "sender@host1";
        coordinatorHelper.dispatchEvent(event);

        assertEquals("web_config", receivedData.get());
        assertEquals("sender@host1", receivedInstanceId.get());
    }

    // ===================================================================================
    //                                                                  JSON with Complex
    //                                                                  ==================

    @Test
    public void test_toJson_withList() {
        final Map<String, Object> map = Map.of( //
                "query", Map.of("bool", Map.of("must", List.of( //
                        Map.of("term", Map.of("type", "heartbeat")), //
                        Map.of("range", Map.of("expiredTime", Map.of("gte", 1000L)))))));

        final String json = coordinatorHelper.toJson(map);
        assertNotNull(json);
        assertTrue(json.contains("\"must\""));
        assertTrue(json.contains("\"heartbeat\""));
        assertTrue(json.contains("\"gte\""));
    }

    @Test
    public void test_parseJson_nestedStructure() {
        final String json = "{\"hits\":{\"hits\":[{\"_source\":{\"instanceId\":\"test\",\"hostname\":\"host1\"}}]}}";
        final Map<String, Object> result = coordinatorHelper.parseJson(json);

        final Map<String, Object> hits = coordinatorHelper.getMapValue(result, "hits");
        assertNotNull(hits);

        final List<Map<String, Object>> hitList = coordinatorHelper.getListValue(hits, "hits");
        assertNotNull(hitList);
        assertEquals(1, hitList.size());

        final Map<String, Object> source = coordinatorHelper.getMapValue(hitList.get(0), "_source");
        assertNotNull(source);
        assertEquals("test", coordinatorHelper.getStringValue(source, "instanceId"));
        assertEquals("host1", coordinatorHelper.getStringValue(source, "hostname"));
    }

    @Test
    public void test_parseJson_operationDocument() {
        final String json = "{\"_index\":\"fess_config.coordinator\",\"_id\":\"reindex\","
                + "\"found\":true,\"_seq_no\":5,\"_primary_term\":1," + "\"_source\":{\"type\":\"operation\",\"name\":\"reindex\","
                + "\"instanceId\":\"node1@host1\",\"hostname\":\"host1\","
                + "\"status\":\"running\",\"createdTime\":1000,\"expiredTime\":9999999999999}}";

        final Map<String, Object> result = coordinatorHelper.parseJson(json);
        assertEquals(true, result.get("found"));
        assertEquals(5, ((Number) result.get("_seq_no")).longValue());
        assertEquals(1, ((Number) result.get("_primary_term")).longValue());

        final Map<String, Object> source = coordinatorHelper.getMapValue(result, "_source");
        assertNotNull(source);
        assertEquals("operation", coordinatorHelper.getStringValue(source, "type"));
        assertEquals("reindex", coordinatorHelper.getStringValue(source, "name"));
        assertEquals("node1@host1", coordinatorHelper.getStringValue(source, "instanceId"));
        assertEquals("host1", coordinatorHelper.getStringValue(source, "hostname"));
        assertEquals("running", coordinatorHelper.getStringValue(source, "status"));
    }

    // ===================================================================================
    //                                                       Actual Document Body Build
    //                                                       ===========================

    @Test
    public void test_sendHeartbeat_documentBody() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final AtomicReference<String> capturedPath = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", capturedPath, capturedBody, createMockResponse(200, "{}"));

        helper.sendHeartbeat();

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        assertEquals("heartbeat", body.get("type"));
        assertEquals("node1@host1", body.get("instanceId"));
        assertNotNull(body.get("hostname"));
        assertEquals("active", body.get("status"));
        assertNotNull(body.get("createdTime"));
        assertNotNull(body.get("expiredTime"));
        assertTrue(capturedPath.get().contains("/_doc/node1@host1"));
    }

    @Test
    public void test_tryStartOperation_documentBody() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final AtomicReference<String> capturedPath = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", capturedPath, capturedBody, createMockResponse(201, "{}"));

        helper.tryStartOperation("reindex", "fess");

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        assertEquals("operation", body.get("type"));
        assertEquals("reindex", body.get("name"));
        assertEquals("node1@host1", body.get("instanceId"));
        assertNotNull(body.get("hostname"));
        assertEquals("running", body.get("status"));
        assertEquals("fess", body.get("data"));
        assertTrue(capturedPath.get().contains("/_create/reindex"));
    }

    @Test
    public void test_publishEvent_documentBody() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(201, "{}"));

        helper.publishEvent("config_updated", "node2@host2", "web_config");

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        assertEquals("event", body.get("type"));
        assertEquals("config_updated", body.get("name"));
        assertEquals("node1@host1", body.get("instanceId"));
        assertEquals("node2@host2", body.get("targetInstanceId"));
        assertEquals("web_config", body.get("data"));
    }

    // ===================================================================================
    //                                                              Operation Data Null
    //                                                              ====================

    @Test
    public void test_tryStartOperation_withoutData() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(201, "{}"));

        helper.tryStartOperation("reindex_config", null);

        assertNotNull(capturedBody.get());
        assertFalse(capturedBody.get().contains("\"data\""));
    }

    // ===================================================================================
    //                                                             getActiveInstances
    //                                                             ====================

    @Test
    public void test_getActiveInstances_parsesResponse() {
        setupMockFessConfig();
        final String searchResponse = "{\"hits\":{\"total\":{\"value\":2},\"hits\":["
                + "{\"_source\":{\"type\":\"heartbeat\",\"instanceId\":\"node1@host1\",\"hostname\":\"host1\",\"name\":\"node1\",\"createdTime\":1000}},"
                + "{\"_source\":{\"type\":\"heartbeat\",\"instanceId\":\"node2@host2\",\"hostname\":\"host2\",\"name\":\"node2\",\"createdTime\":2000}}"
                + "]}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, searchResponse));

        final List<InstanceInfo> instances = helper.getActiveInstances();

        assertEquals(2, instances.size());
        assertEquals("node1@host1", instances.get(0).instanceId);
        assertEquals("host1", instances.get(0).hostname);
        assertEquals("node1", instances.get(0).name);
        assertEquals(1000L, instances.get(0).lastSeen);
        assertEquals("node2@host2", instances.get(1).instanceId);
        assertEquals("host2", instances.get(1).hostname);
    }

    // ===================================================================================
    //                                                             getOperationInfo
    //                                                             ==================

    @Test
    public void test_getOperationInfo_found() {
        setupMockFessConfig();
        final String json = "{\"_index\":\"fess_config.coordinator\",\"_id\":\"reindex\","
                + "\"found\":true,\"_seq_no\":10,\"_primary_term\":1," + "\"_source\":{\"type\":\"operation\",\"name\":\"reindex\","
                + "\"instanceId\":\"node1@host1\",\"hostname\":\"host1\","
                + "\"status\":\"running\",\"createdTime\":5000,\"expiredTime\":9999999,\"data\":\"fess\"}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, json));

        final Optional<OperationInfo> result = helper.getOperationInfo("reindex");

        assertTrue(result.isPresent());
        final OperationInfo info = result.get();
        assertEquals("reindex", info.name);
        assertEquals("node1@host1", info.instanceId);
        assertEquals("host1", info.hostname);
        assertEquals("running", info.status);
        assertEquals(5000L, info.createdTime);
        assertEquals("fess", info.data);
    }

    @Test
    public void test_getOperationInfo_notFound() {
        setupMockFessConfig();
        final String json = "{\"_index\":\"fess_config.coordinator\",\"_id\":\"reindex\",\"found\":false}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, json));

        final Optional<OperationInfo> result = helper.getOperationInfo("reindex");

        assertFalse(result.isPresent());
    }

    // ===================================================================================
    //                                                        isOperationRunning
    //                                                        ====================

    @Test
    public void test_isOperationRunning_expired() {
        setupMockFessConfig();
        final long now = System.currentTimeMillis();
        final String opJson = "{\"found\":true,\"_source\":{\"type\":\"operation\",\"name\":\"reindex\","
                + "\"instanceId\":\"node1@host1\",\"status\":\"running\"," + "\"expiredTime\":" + (now - 1000L) + "}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, opJson));

        assertFalse(helper.isOperationRunning("reindex"));
    }

    @Test
    public void test_isOperationRunning_notExpired_activeOwner() {
        setupMockFessConfig();
        final long now = System.currentTimeMillis();
        final String opJson = "{\"found\":true,\"_source\":{\"type\":\"operation\",\"name\":\"reindex\","
                + "\"instanceId\":\"node1@host1\",\"status\":\"running\"," + "\"expiredTime\":" + (now + 3600000L) + "}}";
        final CurlResponse mockResponse = createMockResponse(200, opJson);

        // Use a subclass to mock both CurlHelper access and getActiveInstances
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public List<InstanceInfo> getActiveInstances() {
                final InstanceInfo info = new InstanceInfo();
                info.instanceId = "node1@host1";
                return List.of(info);
            }
        };
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("instanceId");
            field.setAccessible(true);
            field.set(helper, "node1@host1");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        // Register mock CurlHelper that returns the operation document
        ComponentUtil.register(new CurlHelper() {
            @Override
            public CurlRequest get(final String path) {
                return new CurlRequest(org.codelibs.curl.Curl.Method.GET, "http://localhost:9200") {
                    @Override
                    public CurlResponse execute() {
                        return mockResponse;
                    }
                };
            }
        }, "curlHelper");

        assertTrue(helper.isOperationRunning("reindex"));
    }

    // ===================================================================================
    //                                                         Query Structure via Methods
    //                                                         ===========================

    @Test
    public void test_getActiveInstances_queryStructure() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final AtomicReference<String> capturedPath = new AtomicReference<>();
        final CoordinatorHelper helper =
                createCapturingHelper("node1@host1", capturedPath, capturedBody, createMockResponse(200, "{\"hits\":{\"hits\":[]}}"));

        helper.getActiveInstances();

        assertNotNull(capturedBody.get());
        assertTrue(capturedBody.get().contains("\"heartbeat\""));
        assertTrue(capturedBody.get().contains("\"expiredTime\""));
        assertTrue(capturedBody.get().contains("\"gte\""));
        assertTrue(capturedBody.get().contains("\"size\":100"));
        assertTrue(capturedPath.get().contains("/_search"));
    }

    @Test
    public void test_fetchNewEvents_queryStructure() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper =
                createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(200, "{\"hits\":{\"hits\":[]}}"));
        setLastEventCheckTime(helper, 1000L);

        helper.fetchNewEvents();

        assertNotNull(capturedBody.get());
        assertTrue(capturedBody.get().contains("\"event\""));
        assertTrue(capturedBody.get().contains("\"minimum_should_match\":1"));
        assertTrue(capturedBody.get().contains("\"must_not\""));
        assertTrue(capturedBody.get().contains("\"targetInstanceId\""));
        assertTrue(capturedBody.get().contains("\"node1@host1\""));
    }

    @Test
    public void test_cleanupExpiredDocuments_queryStructure() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final AtomicReference<String> capturedPath = new AtomicReference<>();
        final CoordinatorHelper helper =
                createCapturingHelper("node1@host1", capturedPath, capturedBody, createMockResponse(200, "{\"deleted\":0}"));

        helper.cleanupExpiredDocuments();

        assertNotNull(capturedBody.get());
        assertTrue(capturedBody.get().contains("\"lt\""));
        assertTrue(capturedBody.get().contains("\"expiredTime\""));
        assertTrue(capturedPath.get().contains("/_delete_by_query"));
    }

    // ===================================================================================
    //                                                             Retry Limit
    //                                                             ============

    @Test
    public void test_tryStartOperation_singleArg_delegatesToTwoArg() {
        // tryStartOperation(name) should delegate to tryStartOperation(name, null)
        final AtomicReference<String> passedData = new AtomicReference<>("NOT_NULL");
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public boolean tryStartOperation(final String operationName, final String data) {
                passedData.set(data);
                return false;
            }
        };

        helper.tryStartOperation("test_op");
        assertNull(passedData.get());
    }

    @Test
    public void test_tryStartOperation_twoArg_usesConfigRetry() {
        // tryStartOperation(name, data) should read maxRetry from FessConfig
        final AtomicInteger passedRetries = new AtomicInteger(-1);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
                passedRetries.set(remainingRetries);
                return false;
            }
        };

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getCoordinatorOperationRetryAsInteger() {
                return 5;
            }
        });

        helper.tryStartOperation("test_op", "test_data");
        assertEquals(5, passedRetries.get());
    }

    @Test
    public void test_tryStartOperation_twoArg_defaultRetry() {
        // Verify default retry value of 3 from config
        final AtomicInteger passedRetries = new AtomicInteger(-1);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
                passedRetries.set(remainingRetries);
                return false;
            }
        };

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getCoordinatorOperationRetryAsInteger() {
                return 3;
            }
        });

        helper.tryStartOperation("test_op", null);
        assertEquals(3, passedRetries.get());
    }

    /**
     * Creates a testable CoordinatorHelper that overrides tryStartOperation(name,data,retries)
     * to simulate "create always fails" (no OpenSearch), preserving the retry/cleanup dispatch logic.
     */
    private CoordinatorHelper createRetryTestHelper(final AtomicBoolean cleanupCalled, final AtomicInteger cleanupRetries,
            final AtomicReference<String> cleanupName, final AtomicReference<String> cleanupData, final boolean cleanupReturns) {
        return new CoordinatorHelper() {
            @Override
            protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
                // Simulate: create always fails (no OpenSearch)
                // Then apply the retry logic from the real implementation
                if (remainingRetries <= 0) {
                    return false;
                }
                return tryCleanupAndRetry(operationName, data, remainingRetries);
            }

            @Override
            protected boolean tryCleanupAndRetry(final String operationName, final String data, final int remainingRetries) {
                if (cleanupCalled != null) {
                    cleanupCalled.set(true);
                }
                if (cleanupRetries != null) {
                    cleanupRetries.set(remainingRetries);
                }
                if (cleanupName != null) {
                    cleanupName.set(operationName);
                }
                if (cleanupData != null) {
                    cleanupData.set(data);
                }
                if (cleanupReturns) {
                    // Simulate cleanup success: retry with decremented count
                    return tryStartOperation(operationName, data, remainingRetries - 1);
                }
                return false;
            }
        };
    }

    @Test
    public void test_tryStartOperation_retryZero_skipsCleanup() {
        final AtomicBoolean cleanupCalled = new AtomicBoolean(false);
        final CoordinatorHelper helper = createRetryTestHelper(cleanupCalled, null, null, null, false);

        final boolean result = helper.tryStartOperation("test_op", null, 0);
        assertFalse(result);
        assertFalse(cleanupCalled.get());
    }

    @Test
    public void test_tryStartOperation_retryNegative_skipsCleanup() {
        final AtomicBoolean cleanupCalled = new AtomicBoolean(false);
        final CoordinatorHelper helper = createRetryTestHelper(cleanupCalled, null, null, null, false);

        final boolean result = helper.tryStartOperation("test_op", null, -1);
        assertFalse(result);
        assertFalse(cleanupCalled.get());
    }

    @Test
    public void test_tryStartOperation_retryPositive_callsCleanup() {
        final AtomicInteger passedRetries = new AtomicInteger(-1);
        final AtomicReference<String> passedName = new AtomicReference<>();
        final AtomicReference<String> passedData = new AtomicReference<>();
        final CoordinatorHelper helper = createRetryTestHelper(null, passedRetries, passedName, passedData, false);

        helper.tryStartOperation("my_op", "my_data", 3);
        assertEquals("my_op", passedName.get());
        assertEquals("my_data", passedData.get());
        assertEquals(3, passedRetries.get());
    }

    @Test
    public void test_tryStartOperation_retryOne_callsCleanup() {
        final AtomicInteger passedRetries = new AtomicInteger(-1);
        final CoordinatorHelper helper = createRetryTestHelper(null, passedRetries, null, null, false);

        helper.tryStartOperation("test_op", null, 1);
        assertEquals(1, passedRetries.get());
    }

    @Test
    public void test_tryCleanupAndRetry_decrementsRetries() {
        // Verify that on retry chain, remainingRetries is decremented
        final List<Integer> retriesSeen = new ArrayList<>();
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
                retriesSeen.add(remainingRetries);
                if (remainingRetries <= 0) {
                    return false;
                }
                // Simulate: cleanup succeeds, retry with decremented retries
                return tryStartOperation(operationName, data, remainingRetries - 1);
            }
        };

        helper.tryStartOperation("test_op", null, 3);
        assertEquals(List.of(3, 2, 1, 0), retriesSeen);
    }

    @Test
    public void test_tryStartOperation_retryChain_exhausted() {
        // Simulate retry chain: cleanup always succeeds but create always fails
        final AtomicInteger cleanupCallCount = new AtomicInteger(0);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            protected boolean tryStartOperation(final String operationName, final String data, final int remainingRetries) {
                if (remainingRetries <= 0) {
                    return false;
                }
                return tryCleanupAndRetry(operationName, data, remainingRetries);
            }

            @Override
            protected boolean tryCleanupAndRetry(final String operationName, final String data, final int remainingRetries) {
                cleanupCallCount.incrementAndGet();
                return tryStartOperation(operationName, data, remainingRetries - 1);
            }
        };

        final boolean result = helper.tryStartOperation("test_op", null, 3);
        assertFalse(result);
        assertEquals(3, cleanupCallCount.get()); // Called with retries=3,2,1; stops when retries=0
    }

    // ===================================================================================
    //                                                         completeOperation Safety
    //                                                         =========================

    @Test
    public void test_completeOperation_noThrow_whenNoOpenSearch() {
        // completeOperation must not throw even when OpenSearch is unavailable
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            protected String getIndexName() {
                return "fess_config.coordinator";
            }
        };

        // CurlHelper not available → exception → caught internally → no throw
        try {
            helper.completeOperation("nonexistent_operation");
        } catch (final Exception e) {
            fail("completeOperation should not throw: " + e.getMessage());
        }
    }

    @Test
    public void test_completeOperation_doubleCall_safe() {
        // Calling completeOperation twice must not throw (idempotent release)
        final AtomicInteger callCount = new AtomicInteger(0);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public void completeOperation(final String operationName) {
                callCount.incrementAndGet();
                super.completeOperation(operationName);
            }

            @Override
            protected String getIndexName() {
                return "fess_config.coordinator";
            }
        };

        helper.completeOperation("test_op");
        helper.completeOperation("test_op");
        assertEquals(2, callCount.get());
    }

    // ===================================================================================
    //                                                         fetchNewEvents
    //                                                         ================

    @Test
    public void test_fetchNewEvents_parsesEventsCorrectly() {
        setupMockFessConfig();
        final String searchResponse =
                "{\"hits\":{\"hits\":[" + "{\"_source\":{\"type\":\"event\",\"name\":\"config_updated\",\"instanceId\":\"node2@host2\","
                        + "\"targetInstanceId\":\"*\",\"createdTime\":5000,\"data\":\"web_config\"}},"
                        + "{\"_source\":{\"type\":\"event\",\"name\":\"dict_updated\",\"instanceId\":\"node3@host3\","
                        + "\"targetInstanceId\":\"node1@host1\",\"createdTime\":6000}}" + "]}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, searchResponse));
        setLastEventCheckTime(helper, 4000L);

        final List<EventInfo> events = helper.fetchNewEvents();

        assertEquals(2, events.size());
        assertEquals("config_updated", events.get(0).name);
        assertEquals("node2@host2", events.get(0).instanceId);
        assertEquals("*", events.get(0).targetInstanceId);
        assertEquals(5000L, events.get(0).createdTime);
        assertEquals("web_config", events.get(0).data);
        assertEquals("dict_updated", events.get(1).name);
        assertEquals("node3@host3", events.get(1).instanceId);
        assertEquals(6000L, events.get(1).createdTime);
    }

    @Test
    public void test_fetchNewEvents_updatesLastEventCheckTime() {
        setupMockFessConfig();
        final String searchResponse = "{\"hits\":{\"hits\":[" + "{\"_source\":{\"type\":\"event\",\"name\":\"e1\",\"instanceId\":\"node2\","
                + "\"targetInstanceId\":\"*\",\"createdTime\":5000}},"
                + "{\"_source\":{\"type\":\"event\",\"name\":\"e2\",\"instanceId\":\"node3\","
                + "\"targetInstanceId\":\"*\",\"createdTime\":7000}},"
                + "{\"_source\":{\"type\":\"event\",\"name\":\"e3\",\"instanceId\":\"node4\","
                + "\"targetInstanceId\":\"*\",\"createdTime\":6000}}" + "]}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, searchResponse));
        setLastEventCheckTime(helper, 4000L);

        helper.fetchNewEvents();

        // lastEventCheckTime should be max(createdTime) + 1 = 7001
        assertEquals(7001L, getLastEventCheckTime(helper));
    }

    @Test
    public void test_fetchNewEvents_emptyResponse() {
        setupMockFessConfig();
        final String searchResponse = "{\"hits\":{\"hits\":[]}}";
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(200, searchResponse));
        setLastEventCheckTime(helper, 4000L);

        final List<EventInfo> events = helper.fetchNewEvents();

        assertTrue(events.isEmpty());
        assertEquals(4000L, getLastEventCheckTime(helper));
    }

    // ===================================================================================
    //                                                     Config Integration via Methods
    //                                                     ==============================

    @Test
    public void test_sendHeartbeat_usesConfigTtl() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(200, "{}"));

        final long before = System.currentTimeMillis();
        helper.sendHeartbeat();
        final long after = System.currentTimeMillis();

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        final long createdTime = ((Number) body.get("createdTime")).longValue();
        final long expiredTime = ((Number) body.get("expiredTime")).longValue();
        assertTrue(createdTime >= before);
        assertTrue(createdTime <= after);
        // heartbeat TTL = 180000
        assertEquals(180000L, expiredTime - createdTime);
    }

    @Test
    public void test_publishEvent_usesConfigTtl() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(201, "{}"));

        helper.publishEvent("config_updated", "web_config");

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        final long createdTime = ((Number) body.get("createdTime")).longValue();
        final long expiredTime = ((Number) body.get("expiredTime")).longValue();
        // event TTL = 600000
        assertEquals(600000L, expiredTime - createdTime);
    }

    @Test
    public void test_tryStartOperation_usesConfigTtl() {
        setupMockFessConfig();
        final AtomicReference<String> capturedBody = new AtomicReference<>();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, capturedBody, createMockResponse(201, "{}"));

        helper.tryStartOperation("reindex", "fess");

        assertNotNull(capturedBody.get());
        final Map<String, Object> body = coordinatorHelper.parseJson(capturedBody.get());
        final long createdTime = ((Number) body.get("createdTime")).longValue();
        final long expiredTime = ((Number) body.get("expiredTime")).longValue();
        // operation TTL = 7200000
        assertEquals(7200000L, expiredTime - createdTime);
    }

    @Test
    public void test_getIndexName_emptyPrefix() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return "fess_config";
            }
        });
        // Verify that the index name pattern uses config prefix correctly
        final String indexName = coordinatorHelper.getIndexName();
        assertTrue(indexName.endsWith(".coordinator"));
        assertTrue(indexName.startsWith("fess_config"));
    }

    // ===================================================================================
    //                                                   completeOperation Ownership
    //                                                   =============================

    @Test
    public void test_completeOperation_ownInstance_deletesDoc() {
        setupMockFessConfig();
        final String getResponse = "{\"found\":true,\"_seq_no\":5,\"_primary_term\":1,"
                + "\"_source\":{\"type\":\"operation\",\"name\":\"reindex\"," + "\"instanceId\":\"node1@host1\",\"status\":\"running\"}}";
        final AtomicBoolean deleteCalled = new AtomicBoolean(false);

        final CurlHelper mockCurlHelper = new CurlHelper() {
            @Override
            public CurlRequest get(final String path) {
                return new CurlRequest(org.codelibs.curl.Curl.Method.GET, "http://localhost:9200") {
                    @Override
                    public CurlResponse execute() {
                        return createMockResponse(200, getResponse);
                    }
                };
            }

            @Override
            public CurlRequest delete(final String path) {
                deleteCalled.set(true);
                return new CurlRequest(org.codelibs.curl.Curl.Method.DELETE, "http://localhost:9200") {
                    @Override
                    public CurlResponse execute() {
                        return createMockResponse(200, "{\"result\":\"deleted\"}");
                    }
                };
            }
        };
        ComponentUtil.register(mockCurlHelper, "curlHelper");

        final CoordinatorHelper helper = new CoordinatorHelper();
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("instanceId");
            field.setAccessible(true);
            field.set(helper, "node1@host1");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        helper.completeOperation("reindex");
        assertTrue(deleteCalled.get());
    }

    @Test
    public void test_completeOperation_otherInstance_doesNotDelete() {
        setupMockFessConfig();
        final String getResponse = "{\"found\":true,\"_seq_no\":5,\"_primary_term\":1,"
                + "\"_source\":{\"type\":\"operation\",\"name\":\"reindex\"," + "\"instanceId\":\"node2@host2\",\"status\":\"running\"}}";
        final AtomicBoolean deleteCalled = new AtomicBoolean(false);

        final CurlHelper mockCurlHelper = new CurlHelper() {
            @Override
            public CurlRequest get(final String path) {
                return new CurlRequest(org.codelibs.curl.Curl.Method.GET, "http://localhost:9200") {
                    @Override
                    public CurlResponse execute() {
                        return createMockResponse(200, getResponse);
                    }
                };
            }

            @Override
            public CurlRequest delete(final String path) {
                deleteCalled.set(true);
                return new CurlRequest(org.codelibs.curl.Curl.Method.DELETE, "http://localhost:9200") {
                    @Override
                    public CurlResponse execute() {
                        return createMockResponse(200, "{}");
                    }
                };
            }
        };
        ComponentUtil.register(mockCurlHelper, "curlHelper");

        final CoordinatorHelper helper = new CoordinatorHelper();
        try {
            final Field field = CoordinatorHelper.class.getDeclaredField("instanceId");
            field.setAccessible(true);
            field.set(helper, "node1@host1");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        helper.completeOperation("reindex");
        // Should NOT delete because owner is node2@host2, not node1@host1
        assertFalse(deleteCalled.get());
    }

    @Test
    public void test_completeOperation_notFound() {
        setupMockFessConfig();
        final CoordinatorHelper helper = createCapturingHelper("node1@host1", null, null, createMockResponse(404, "{}"));

        // Should complete safely without throwing
        helper.completeOperation("nonexistent_op");
    }

    // ===================================================================================
    //                                                         Poll Loop Safety
    //                                                         ==================

    @Test
    public void test_poll_callsHeartbeatEventsCleanup() {
        // Verify poll calls sendHeartbeat, fetchNewEvents+dispatch, cleanupExpiredDocuments
        final AtomicBoolean heartbeatCalled = new AtomicBoolean(false);
        final AtomicBoolean fetchCalled = new AtomicBoolean(false);
        final AtomicBoolean cleanupCalled = new AtomicBoolean(false);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public void sendHeartbeat() {
                heartbeatCalled.set(true);
            }

            @Override
            protected List<EventInfo> fetchNewEvents() {
                fetchCalled.set(true);
                return List.of();
            }

            @Override
            protected void cleanupExpiredDocuments() {
                cleanupCalled.set(true);
            }
        };

        helper.poll();
        assertTrue(heartbeatCalled.get());
        assertTrue(fetchCalled.get());
        assertTrue(cleanupCalled.get());
    }

    @Test
    public void test_poll_heartbeatException_doesNotStopEventProcessing() {
        // If sendHeartbeat throws, fetchNewEvents and cleanup should still be called
        final AtomicBoolean fetchCalled = new AtomicBoolean(false);
        final AtomicBoolean cleanupCalled = new AtomicBoolean(false);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public void sendHeartbeat() {
                throw new RuntimeException("heartbeat error");
            }

            @Override
            protected List<EventInfo> fetchNewEvents() {
                fetchCalled.set(true);
                return List.of();
            }

            @Override
            protected void cleanupExpiredDocuments() {
                cleanupCalled.set(true);
            }
        };

        helper.poll();
        assertTrue(fetchCalled.get());
        assertTrue(cleanupCalled.get());
    }

    @Test
    public void test_poll_eventException_doesNotStopCleanup() {
        // If fetchNewEvents throws, cleanup should still be called
        final AtomicBoolean heartbeatCalled = new AtomicBoolean(false);
        final AtomicBoolean cleanupCalled = new AtomicBoolean(false);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public void sendHeartbeat() {
                heartbeatCalled.set(true);
            }

            @Override
            protected List<EventInfo> fetchNewEvents() {
                throw new RuntimeException("event error");
            }

            @Override
            protected void cleanupExpiredDocuments() {
                cleanupCalled.set(true);
            }
        };

        helper.poll();
        assertTrue(heartbeatCalled.get());
        assertTrue(cleanupCalled.get());
    }

    @Test
    public void test_poll_dispatchesEvents() {
        // Verify that poll fetches events and dispatches them
        final AtomicInteger dispatchCount = new AtomicInteger(0);
        final CoordinatorHelper helper = new CoordinatorHelper() {
            @Override
            public void sendHeartbeat() {
                // no-op
            }

            @Override
            protected List<EventInfo> fetchNewEvents() {
                final EventInfo e1 = new EventInfo();
                e1.name = "event1";
                final EventInfo e2 = new EventInfo();
                e2.name = "event2";
                return List.of(e1, e2);
            }

            @Override
            protected void dispatchEvent(final EventInfo event) {
                dispatchCount.incrementAndGet();
            }

            @Override
            protected void cleanupExpiredDocuments() {
                // no-op
            }
        };

        helper.poll();
        assertEquals(2, dispatchCount.get());
    }
}
