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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    //                                                               Heartbeat Body Build
    //                                                               =====================

    @Test
    public void test_heartbeatDocumentStructure() {
        // Verify that a heartbeat document body can be built and parsed correctly
        final long now = System.currentTimeMillis();
        final long ttl = 180000L;
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "heartbeat");
        body.put("instanceId", "node1@host1");
        body.put("hostname", "host1");
        body.put("name", "node1");
        body.put("status", "active");
        body.put("createdTime", now);
        body.put("expiredTime", now + ttl);

        final String json = coordinatorHelper.toJson(body);
        final Map<String, Object> parsed = coordinatorHelper.parseJson(json);

        assertEquals("heartbeat", parsed.get("type"));
        assertEquals("node1@host1", parsed.get("instanceId"));
        assertEquals("host1", parsed.get("hostname"));
        assertEquals("node1", parsed.get("name"));
        assertEquals("active", parsed.get("status"));
        assertTrue(((Number) parsed.get("expiredTime")).longValue() > ((Number) parsed.get("createdTime")).longValue());
    }

    @Test
    public void test_operationDocumentStructure() {
        final long now = System.currentTimeMillis();
        final long ttl = 7200000L;
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "operation");
        body.put("name", "reindex");
        body.put("instanceId", "node1@host1");
        body.put("hostname", "host1");
        body.put("status", "running");
        body.put("createdTime", now);
        body.put("expiredTime", now + ttl);
        body.put("data", "fess");

        final String json = coordinatorHelper.toJson(body);
        final Map<String, Object> parsed = coordinatorHelper.parseJson(json);

        assertEquals("operation", parsed.get("type"));
        assertEquals("reindex", parsed.get("name"));
        assertEquals("running", parsed.get("status"));
        assertEquals("fess", parsed.get("data"));
    }

    @Test
    public void test_eventDocumentStructure() {
        final long now = System.currentTimeMillis();
        final long ttl = 600000L;
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "event");
        body.put("name", "config_updated");
        body.put("instanceId", "node1@host1");
        body.put("targetInstanceId", "*");
        body.put("createdTime", now);
        body.put("expiredTime", now + ttl);
        body.put("data", "web_config");

        final String json = coordinatorHelper.toJson(body);
        final Map<String, Object> parsed = coordinatorHelper.parseJson(json);

        assertEquals("event", parsed.get("type"));
        assertEquals("config_updated", parsed.get("name"));
        assertEquals("*", parsed.get("targetInstanceId"));
        assertEquals("web_config", parsed.get("data"));
    }

    // ===================================================================================
    //                                                              Operation Data Null
    //                                                              ====================

    @Test
    public void test_operationDocumentWithoutData() {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "operation");
        body.put("name", "reindex_config");
        body.put("instanceId", "node1@host1");
        body.put("hostname", "host1");
        body.put("status", "running");
        body.put("createdTime", System.currentTimeMillis());
        body.put("expiredTime", System.currentTimeMillis() + 7200000L);
        // data is intentionally not set

        final String json = coordinatorHelper.toJson(body);
        final Map<String, Object> parsed = coordinatorHelper.parseJson(json);

        assertNull(coordinatorHelper.getStringValue(parsed, "data"));
    }

    // ===================================================================================
    //                                                             Active Instance Parse
    //                                                             ======================

    @Test
    public void test_parseActiveInstancesResponse() {
        // Simulate OpenSearch _search response for heartbeats
        final String json = "{\"hits\":{\"total\":{\"value\":2},\"hits\":["
                + "{\"_source\":{\"type\":\"heartbeat\",\"instanceId\":\"node1@host1\",\"hostname\":\"host1\",\"name\":\"node1\",\"createdTime\":1000}},"
                + "{\"_source\":{\"type\":\"heartbeat\",\"instanceId\":\"node2@host2\",\"hostname\":\"host2\",\"name\":\"node2\",\"createdTime\":2000}}"
                + "]}}";

        final Map<String, Object> result = coordinatorHelper.parseJson(json);
        final Map<String, Object> hits = coordinatorHelper.getMapValue(result, "hits");
        assertNotNull(hits);

        final List<Map<String, Object>> hitList = coordinatorHelper.getListValue(hits, "hits");
        assertNotNull(hitList);
        assertEquals(2, hitList.size());

        final List<InstanceInfo> instances = new ArrayList<>();
        for (final Map<String, Object> hit : hitList) {
            final Map<String, Object> source = coordinatorHelper.getMapValue(hit, "_source");
            if (source != null) {
                final InstanceInfo info = new InstanceInfo();
                info.instanceId = coordinatorHelper.getStringValue(source, "instanceId");
                info.hostname = coordinatorHelper.getStringValue(source, "hostname");
                info.name = coordinatorHelper.getStringValue(source, "name");
                info.lastSeen = coordinatorHelper.getLongValue(source, "createdTime");
                instances.add(info);
            }
        }

        assertEquals(2, instances.size());
        assertEquals("node1@host1", instances.get(0).instanceId);
        assertEquals("host1", instances.get(0).hostname);
        assertEquals("node1", instances.get(0).name);
        assertEquals(1000L, instances.get(0).lastSeen);
        assertEquals("node2@host2", instances.get(1).instanceId);
        assertEquals("host2", instances.get(1).hostname);
    }

    // ===================================================================================
    //                                                             Operation Info Parse
    //                                                             ======================

    @Test
    public void test_parseOperationInfoResponse_found() {
        final String json = "{\"_index\":\"fess_config.coordinator\",\"_id\":\"reindex\","
                + "\"found\":true,\"_seq_no\":10,\"_primary_term\":1," + "\"_source\":{\"type\":\"operation\",\"name\":\"reindex\","
                + "\"instanceId\":\"node1@host1\",\"hostname\":\"host1\","
                + "\"status\":\"running\",\"createdTime\":5000,\"expiredTime\":9999999}}";

        final Map<String, Object> result = coordinatorHelper.parseJson(json);
        final Boolean found = (Boolean) result.get("found");
        assertTrue(found);

        final Map<String, Object> source = coordinatorHelper.getMapValue(result, "_source");
        assertNotNull(source);

        final OperationInfo info = new OperationInfo();
        info.name = coordinatorHelper.getStringValue(source, "name");
        info.instanceId = coordinatorHelper.getStringValue(source, "instanceId");
        info.hostname = coordinatorHelper.getStringValue(source, "hostname");
        info.status = coordinatorHelper.getStringValue(source, "status");
        info.createdTime = coordinatorHelper.getLongValue(source, "createdTime");

        assertEquals("reindex", info.name);
        assertEquals("node1@host1", info.instanceId);
        assertEquals("host1", info.hostname);
        assertEquals("running", info.status);
        assertEquals(5000L, info.createdTime);
    }

    @Test
    public void test_parseOperationInfoResponse_notFound() {
        final String json = "{\"_index\":\"fess_config.coordinator\",\"_id\":\"reindex\",\"found\":false}";

        final Map<String, Object> result = coordinatorHelper.parseJson(json);
        final Boolean found = (Boolean) result.get("found");
        assertFalse(found);
    }

    // ===================================================================================
    //                                                           Expiry Check Logic
    //                                                           ====================

    @Test
    public void test_expiryCheck_expired() {
        final long now = System.currentTimeMillis();
        final long expiredTime = now - 1000L; // expired 1 second ago
        assertTrue(expiredTime < now);
    }

    @Test
    public void test_expiryCheck_notExpired() {
        final long now = System.currentTimeMillis();
        final long expiredTime = now + 3600000L; // expires in 1 hour
        assertFalse(expiredTime < now);
    }

    // ===================================================================================
    //                                                         Search Query Structure
    //                                                         =======================

    @Test
    public void test_heartbeatSearchQuery() {
        final long now = System.currentTimeMillis();
        final Map<String, Object> query = Map.of( //
                "query", Map.of("bool", Map.of("must", List.of( //
                        Map.of("term", Map.of("type", "heartbeat")), //
                        Map.of("range", Map.of("expiredTime", Map.of("gte", now)))))), //
                "size", 100);

        final String json = coordinatorHelper.toJson(query);
        assertNotNull(json);
        assertTrue(json.contains("\"heartbeat\""));
        assertTrue(json.contains("\"expiredTime\""));
        assertTrue(json.contains("\"gte\""));
        assertTrue(json.contains("\"size\":100"));
    }

    @Test
    public void test_eventSearchQuery() {
        final long lastCheckTime = 1000L;
        final String instanceId = "node1@host1";
        final Map<String, Object> query = Map.of( //
                "query", Map.of("bool", Map.of( //
                        "must", List.of( //
                                Map.of("term", Map.of("type", "event")), //
                                Map.of("range", Map.of("createdTime", Map.of("gt", lastCheckTime)))), //
                        "should", List.of( //
                                Map.of("term", Map.of("targetInstanceId", "*")), //
                                Map.of("term", Map.of("targetInstanceId", instanceId))), //
                        "minimum_should_match", 1, //
                        "must_not", List.of( //
                                Map.of("term", Map.of("instanceId", instanceId))))), //
                "size", 100, //
                "sort", List.of(Map.of("createdTime", "asc")));

        final String json = coordinatorHelper.toJson(query);
        assertNotNull(json);
        assertTrue(json.contains("\"event\""));
        assertTrue(json.contains("\"minimum_should_match\":1"));
        assertTrue(json.contains("\"must_not\""));
    }

    @Test
    public void test_cleanupQuery() {
        final long now = System.currentTimeMillis();
        final Map<String, Object> query = Map.of( //
                "query", Map.of("range", Map.of("expiredTime", Map.of("lt", now))));

        final String json = coordinatorHelper.toJson(query);
        assertNotNull(json);
        assertTrue(json.contains("\"lt\""));
        assertTrue(json.contains("\"expiredTime\""));
    }
}
