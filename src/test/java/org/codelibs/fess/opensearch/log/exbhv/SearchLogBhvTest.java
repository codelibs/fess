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
package org.codelibs.fess.opensearch.log.exbhv;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class SearchLogBhvTest extends UnitFessTestCase {

    private static final String SEARCH_PARAMS = "{\"q\":\"fess\",\"fields\":{\"label\":[\"docs\"]},\"sort\":\"score.desc\"}";

    @Test
    public void test_searchParams_roundTrip() {
        final SearchLog searchLog = new SearchLog();
        searchLog.setSearchWord("fess");
        searchLog.setSearchParams(SEARCH_PARAMS);

        final Map<String, Object> source = searchLog.toSource();
        assertEquals(SEARCH_PARAMS, source.get("searchParams"));

        final SearchLog restored = new SearchLogBhv().createEntity(source, SearchLog.class);
        assertEquals("fess", restored.getSearchWord());
        assertEquals(SEARCH_PARAMS, restored.getSearchParams());
    }

    @Test
    public void test_searchParams_absent() {
        final SearchLog searchLog = new SearchLog();
        searchLog.setSearchWord("fess");

        final Map<String, Object> source = searchLog.toSource();
        assertFalse(source.containsKey("searchParams"));

        final SearchLog restored = new SearchLogBhv().createEntity(source, SearchLog.class);
        assertNull(restored.getSearchParams());
    }

    @Test
    public void test_searchParams_nonStringIgnored() {
        final Map<String, Object> source = new HashMap<>();
        source.put("searchWord", "fess");
        source.put("searchParams", 123);

        final SearchLog restored = new SearchLogBhv().createEntity(source, SearchLog.class);
        assertNull(restored.getSearchParams());
    }
}
