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
package org.codelibs.fess.entity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class SearchLogEventTest extends UnitFessTestCase {

    // Test implementation of SearchLogEvent interface
    private static class TestSearchLogEvent implements SearchLogEvent {
        private String id;
        private Long versionNo;
        private String eventType;
        private Map<String, Object> sourceMap;

        public TestSearchLogEvent(String id, Long versionNo, String eventType) {
            this.id = id;
            this.versionNo = versionNo;
            this.eventType = eventType;
            this.sourceMap = new HashMap<>();
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public Long getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(Long versionNo) {
            this.versionNo = versionNo;
        }

        @Override
        public Map<String, Object> toSource() {
            return sourceMap;
        }

        public void setSourceMap(Map<String, Object> sourceMap) {
            this.sourceMap = sourceMap;
        }

        @Override
        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }
    }

    // Test null values implementation
    private static class NullSearchLogEvent implements SearchLogEvent {
        @Override
        public String getId() {
            return null;
        }

        @Override
        public Long getVersionNo() {
            return null;
        }

        @Override
        public Map<String, Object> toSource() {
            return null;
        }

        @Override
        public String getEventType() {
            return null;
        }
    }

    // Test empty values implementation
    private static class EmptySearchLogEvent implements SearchLogEvent {
        @Override
        public String getId() {
            return "";
        }

        @Override
        public Long getVersionNo() {
            return 0L;
        }

        @Override
        public Map<String, Object> toSource() {
            return new HashMap<>();
        }

        @Override
        public String getEventType() {
            return "";
        }
    }

    // Test getId method
    @Test
    public void test_getId() {
        // Test normal ID
        TestSearchLogEvent event = new TestSearchLogEvent("test-id-123", 1L, "search");
        assertEquals("test-id-123", event.getId());

        // Test ID change
        event.setId("new-id-456");
        assertEquals("new-id-456", event.getId());

        // Test null ID
        NullSearchLogEvent nullEvent = new NullSearchLogEvent();
        assertNull(nullEvent.getId());

        // Test empty ID
        EmptySearchLogEvent emptyEvent = new EmptySearchLogEvent();
        assertEquals("", emptyEvent.getId());
    }

    // Test getVersionNo method
    @Test
    public void test_getVersionNo() {
        // Test normal version
        TestSearchLogEvent event = new TestSearchLogEvent("id", 5L, "search");
        assertEquals(Long.valueOf(5L), event.getVersionNo());

        // Test version update
        event.setVersionNo(10L);
        assertEquals(Long.valueOf(10L), event.getVersionNo());

        // Test null version
        NullSearchLogEvent nullEvent = new NullSearchLogEvent();
        assertNull(nullEvent.getVersionNo());

        // Test zero version
        EmptySearchLogEvent emptyEvent = new EmptySearchLogEvent();
        assertEquals(Long.valueOf(0L), emptyEvent.getVersionNo());

        // Test negative version
        event.setVersionNo(-1L);
        assertEquals(Long.valueOf(-1L), event.getVersionNo());

        // Test max version
        event.setVersionNo(Long.MAX_VALUE);
        assertEquals(Long.valueOf(Long.MAX_VALUE), event.getVersionNo());
    }

    // Test toSource method
    @Test
    public void test_toSource() {
        // Test with data
        TestSearchLogEvent event = new TestSearchLogEvent("id", 1L, "search");
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("key1", "value1");
        sourceMap.put("key2", 123);
        sourceMap.put("key3", true);
        event.setSourceMap(sourceMap);

        Map<String, Object> result = event.toSource();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals(123, result.get("key2"));
        assertEquals(true, result.get("key3"));

        // Test null source
        NullSearchLogEvent nullEvent = new NullSearchLogEvent();
        assertNull(nullEvent.toSource());

        // Test empty source
        EmptySearchLogEvent emptyEvent = new EmptySearchLogEvent();
        Map<String, Object> emptyResult = emptyEvent.toSource();
        assertNotNull(emptyResult);
        assertTrue(emptyResult.isEmpty());
    }

    // Test getEventType method
    @Test
    public void test_getEventType() {
        // Test search event type
        TestSearchLogEvent searchEvent = new TestSearchLogEvent("id", 1L, "search");
        assertEquals("search", searchEvent.getEventType());

        // Test click event type
        TestSearchLogEvent clickEvent = new TestSearchLogEvent("id", 1L, "click");
        assertEquals("click", clickEvent.getEventType());

        // Test favorite event type
        TestSearchLogEvent favoriteEvent = new TestSearchLogEvent("id", 1L, "favorite");
        assertEquals("favorite", favoriteEvent.getEventType());

        // Test user_info event type
        TestSearchLogEvent userInfoEvent = new TestSearchLogEvent("id", 1L, "user_info");
        assertEquals("user_info", userInfoEvent.getEventType());

        // Test event type change
        searchEvent.setEventType("updated_type");
        assertEquals("updated_type", searchEvent.getEventType());

        // Test null event type
        NullSearchLogEvent nullEvent = new NullSearchLogEvent();
        assertNull(nullEvent.getEventType());

        // Test empty event type
        EmptySearchLogEvent emptyEvent = new EmptySearchLogEvent();
        assertEquals("", emptyEvent.getEventType());
    }

    // Test multiple implementations
    @Test
    public void test_multipleImplementations() {
        // Create different implementations
        SearchLogEvent event1 = new TestSearchLogEvent("id1", 1L, "type1");
        SearchLogEvent event2 = new NullSearchLogEvent();
        SearchLogEvent event3 = new EmptySearchLogEvent();

        // Test polymorphic behavior
        SearchLogEvent[] events = { event1, event2, event3 };

        for (SearchLogEvent event : events) {
            // All methods should be callable without exceptions
            String id = event.getId();
            Long version = event.getVersionNo();
            Map<String, Object> source = event.toSource();
            String type = event.getEventType();

            // Just verify the methods are callable
            assertTrue(true);
        }
    }

    // Test with complex source map
    @Test
    public void test_complexSourceMap() {
        TestSearchLogEvent event = new TestSearchLogEvent("complex-id", 1L, "complex");

        // Create nested map structure
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");

        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("string", "test");
        sourceMap.put("integer", 42);
        sourceMap.put("long", 100L);
        sourceMap.put("double", 3.14);
        sourceMap.put("boolean", false);
        sourceMap.put("null_value", null);
        sourceMap.put("nested", nestedMap);

        event.setSourceMap(sourceMap);

        Map<String, Object> result = event.toSource();
        assertEquals("test", result.get("string"));
        assertEquals(42, result.get("integer"));
        assertEquals(100L, result.get("long"));
        assertEquals(3.14, result.get("double"));
        assertEquals(false, result.get("boolean"));
        assertNull(result.get("null_value"));
        assertNotNull(result.get("nested"));
        assertEquals("nested_value", ((Map<String, Object>) result.get("nested")).get("nested_key"));
    }

    // Test interface contract consistency
    @Test
    public void test_interfaceContract() {
        TestSearchLogEvent event = new TestSearchLogEvent("contract-id", 5L, "contract-type");

        // Test consistency of returned values
        String id1 = event.getId();
        String id2 = event.getId();
        assertEquals(id1, id2);

        Long version1 = event.getVersionNo();
        Long version2 = event.getVersionNo();
        assertEquals(version1, version2);

        String type1 = event.getEventType();
        String type2 = event.getEventType();
        assertEquals(type1, type2);

        Map<String, Object> source1 = event.toSource();
        Map<String, Object> source2 = event.toSource();
        assertSame(source1, source2);
    }

    // Test edge cases for version numbers
    @Test
    public void test_versionNumberEdgeCases() {
        TestSearchLogEvent event = new TestSearchLogEvent("id", 0L, "type");

        // Test minimum value
        event.setVersionNo(Long.MIN_VALUE);
        assertEquals(Long.valueOf(Long.MIN_VALUE), event.getVersionNo());

        // Test zero
        event.setVersionNo(0L);
        assertEquals(Long.valueOf(0L), event.getVersionNo());

        // Test maximum value
        event.setVersionNo(Long.MAX_VALUE);
        assertEquals(Long.valueOf(Long.MAX_VALUE), event.getVersionNo());
    }

    // Test special characters in strings
    @Test
    public void test_specialCharactersInStrings() {
        // Test special characters in ID
        TestSearchLogEvent event1 = new TestSearchLogEvent("id-with-special-!@#$%^&*()", 1L, "type");
        assertEquals("id-with-special-!@#$%^&*()", event1.getId());

        // Test unicode in event type
        TestSearchLogEvent event2 = new TestSearchLogEvent("id", 1L, "タイプ日本語");
        assertEquals("タイプ日本語", event2.getEventType());

        // Test empty strings
        TestSearchLogEvent event3 = new TestSearchLogEvent("", 1L, "");
        assertEquals("", event3.getId());
        assertEquals("", event3.getEventType());
    }

    // Test source map modifications
    @Test
    public void test_sourceMapModifications() {
        TestSearchLogEvent event = new TestSearchLogEvent("id", 1L, "type");

        // Initial empty map
        Map<String, Object> map1 = new HashMap<>();
        event.setSourceMap(map1);
        assertEquals(0, event.toSource().size());

        // Add elements to the map
        map1.put("key1", "value1");
        assertEquals(1, event.toSource().size());

        // Replace the map
        Map<String, Object> map2 = new HashMap<>();
        map2.put("key2", "value2");
        map2.put("key3", "value3");
        event.setSourceMap(map2);
        assertEquals(2, event.toSource().size());
        assertFalse(event.toSource().containsKey("key1"));
        assertTrue(event.toSource().containsKey("key2"));
        assertTrue(event.toSource().containsKey("key3"));
    }
}