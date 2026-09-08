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
package org.codelibs.fess.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.codelibs.curl.CurlResponse;
import org.junit.jupiter.api.Test;

public class SearchEngineCurlTest {

    private CurlResponse responseOf(final String json) {
        return new CurlResponse() {
            @Override
            public InputStream getContentAsStream() {
                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            }
        };
    }

    @Test
    public void test_jsonParser_readsScalarsAndNesting() {
        final Map<String, Object> content = SearchEngineCurl.jsonParser().apply(responseOf("{\"count\":42,\"nested\":{\"a\":\"b\"}}"));
        assertEquals(42, content.get("count"));
        assertEquals(Map.of("a", "b"), content.get("nested"));
    }

    @Test
    public void test_jsonParser_readsArrays() {
        final Map<String, Object> content = SearchEngineCurl.jsonParser().apply(responseOf("{\"file\":[\"a.txt\",\"b.txt\"]}"));
        assertEquals(List.of("a.txt", "b.txt"), content.get("file"));
    }

    @Test
    public void test_jsonParser_emptyObject() {
        assertTrue(SearchEngineCurl.jsonParser().apply(responseOf("{}")).isEmpty());
    }

    @Test
    public void test_jsonParser_malformedJsonFails() {
        assertThrows(Exception.class, () -> SearchEngineCurl.jsonParser().apply(responseOf("not json")));
    }

    @Test
    public void test_jsonParser_wrapsIoFailure() {
        final CurlResponse broken = new CurlResponse() {
            @Override
            public InputStream getContentAsStream() throws IOException {
                throw new IOException("stream unavailable");
            }
        };
        final UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> SearchEngineCurl.jsonParser().apply(broken));
        assertTrue(e.getMessage().contains("search engine response"), e.getMessage());
    }
}
