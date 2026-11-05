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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fess.app.service.CrawlStateService.ConfigMetadata;
import org.codelibs.fess.entity.CrawlProgress;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class CrawlStateServiceTest extends UnitFessTestCase {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** A write the service sent. */
    private static class Stored {
        final String index;
        final String id;
        final String json;

        Stored(final String index, final String id, final String json) {
            this.index = index;
            this.id = id;
            this.json = json;
        }

        Map<String, Object> map() {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        }
    }

    private final List<Stored> stored = new ArrayList<>();

    private final List<String> fetched = new ArrayList<>();

    /** What the configuration holds in its metadata field before the crawler writes. */
    private String existingMetadata;

    private boolean fetchFails;

    private boolean storeFails;

    private String configIndex;

    private List<ConfigMetadata> configs;

    private boolean listFails;

    private long counted;

    private QueryBuilder countedQuery;

    private String countedIndex;

    private boolean countFails;

    private CrawlStateService service;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        LogUtil.resetWarnOnce();
        stored.clear();
        fetched.clear();
        existingMetadata = null;
        fetchFails = false;
        storeFails = false;
        configIndex = "fess_config";
        configs = new ArrayList<>();
        listFails = false;
        counted = 321;
        countedQuery = null;
        countedIndex = null;
        countFails = false;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return configIndex;
            }

            @Override
            public String getIndexFieldConfigId() {
                return "config_id";
            }

            @Override
            public String getIndexFieldSegment() {
                return "segment";
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "fess.update";
            }
        });
        service = new CrawlStateService() {
            @Override
            protected String fetchMetadata(final String index, final String id) {
                fetched.add(index + "/" + id);
                if (fetchFails) {
                    throw new IllegalStateException("search engine is down");
                }
                return existingMetadata;
            }

            @Override
            protected void storeMetadata(final String index, final String id, final String metadata) {
                if (storeFails) {
                    throw new IllegalStateException("search engine is down");
                }
                stored.add(new Stored(index, id, metadata));
            }

            @Override
            protected List<ConfigMetadata> listConfigMetadata() {
                if (listFails) {
                    throw new IllegalStateException("search engine is down");
                }
                return configs;
            }

            @Override
            protected long count(final String index, final QueryBuilder query) {
                if (countFails) {
                    throw new IllegalStateException("search engine is down");
                }
                countedIndex = index;
                countedQuery = query;
                return counted;
            }
        };
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        LogUtil.resetWarnOnce();
        super.tearDown(testInfo);
    }

    private static long number(final Map<String, Object> map, final String key) {
        return ((Number) map.get(key)).longValue();
    }

    @Test
    public void test_isValidConfigId() {
        assertTrue(CrawlStateService.isValidConfigId("W1"));
        assertTrue(CrawlStateService.isValidConfigId("Fabc-DEF_9"));
        assertTrue(CrawlStateService.isValidConfigId("DAbCdEfGh12345678901"));
        assertFalse(CrawlStateService.isValidConfigId(null));
        assertFalse(CrawlStateService.isValidConfigId(""));
        assertFalse(CrawlStateService.isValidConfigId("W"));
        assertFalse(CrawlStateService.isValidConfigId("X1"));
        assertFalse(CrawlStateService.isValidConfigId("1"));
        assertFalse(CrawlStateService.isValidConfigId("W1/../x"));
        assertFalse(CrawlStateService.isValidConfigId("W1?refresh=true"));
        assertFalse(CrawlStateService.isValidConfigId("W 1"));
        assertFalse(CrawlStateService.isValidConfigId("W1\n"));
        assertFalse(CrawlStateService.isValidConfigId("W" + "a".repeat(101)));
    }

    @Test
    public void test_getProgressTtl() {
        assertEquals(CrawlStateService.MIN_PROGRESS_TTL, CrawlStateService.getProgressTtl(1000L));
        assertEquals(CrawlStateService.MIN_PROGRESS_TTL, CrawlStateService.getProgressTtl(10000L));
        assertEquals(60000L, CrawlStateService.getProgressTtl(20000L));
    }

    @Test
    public void test_write_invalidIdTouchesNothing() {
        service.saveProgress("W1/../../_all", 1, 1000);
        service.saveCompleted("bad id", 1);
        service.endProgress(null);
        assertEquals(0, fetched.size());
        assertEquals(0, stored.size());
    }

    @Test
    public void test_write_choosesTheIndexOfTheConfigurationType() {
        service.endProgress("W1");
        service.endProgress("Fabc");
        service.endProgress("Dx-y");
        assertEquals(List.of("fess_config.web_config/1", "fess_config.file_config/abc", "fess_config.data_config/x-y"), fetched);
        assertEquals("fess_config.web_config", stored.get(0).index);
        assertEquals("1", stored.get(0).id);
        assertEquals("fess_config.data_config", stored.get(2).index);
    }

    @Test
    public void test_indexNameFollowsConfigIndexPrefix() {
        configIndex = "custom_config";
        service.endProgress("F7");
        assertEquals("custom_config.file_config", stored.get(0).index);
    }

    @Test
    public void test_saveProgress_keepsTheKeysItDoesNotSet() {
        existingMetadata = "{\"lastCount\":61,\"lastCompletedTime\":5,\"other\":\"kept\"}";
        final long before = System.currentTimeMillis();
        service.saveProgress("W1", 42, 30000L);
        final long after = System.currentTimeMillis();

        assertEquals(1, stored.size());
        final Map<String, Object> map = stored.get(0).map();
        // the previous count and a key that belongs to someone else are written back as they were
        assertEquals(61L, number(map, "lastCount"));
        assertEquals(5L, number(map, "lastCompletedTime"));
        assertEquals("kept", map.get("other"));
        assertEquals(42L, number(map, "progressCount"));
        final long updated = number(map, "progressUpdatedTime");
        assertTrue(updated >= before && updated <= after);
        assertEquals(30000L, number(map, "progressExpiredTime") - updated);
    }

    @Test
    public void test_write_readsTheStoredMetadataOnlyOncePerConfiguration() {
        existingMetadata = "{\"lastCount\":61}";
        service.saveProgress("W1", 1, 1000);
        service.saveProgress("W1", 2, 1000);
        service.endProgress("W1");
        assertEquals(1, fetched.size());
        assertEquals(3, stored.size());
        // each write carries every key, not only what changed
        final Map<String, Object> last = stored.get(2).map();
        assertEquals(61L, number(last, "lastCount"));
        assertEquals(2L, number(last, "progressCount"));
        assertEquals(0L, number(last, "progressExpiredTime"));
        service.saveProgress("F1", 1, 1000);
        assertEquals(2, fetched.size());
    }

    @Test
    public void test_saveCompleted_setsLastCountAndEndsProgress() {
        service.saveCompleted("F7", 1500);

        final Map<String, Object> map = stored.get(0).map();
        assertEquals(1500L, number(map, "lastCount"));
        assertEquals(1500L, number(map, "progressCount"));
        assertTrue(number(map, "lastCompletedTime") > 0);
        assertEquals(0L, number(map, "progressExpiredTime"));
    }

    @Test
    public void test_saveCompleted_zeroCountKeepsLastCount() {
        existingMetadata = "{\"lastCount\":61}";
        service.saveCompleted("W1", 0);

        final Map<String, Object> map = stored.get(0).map();
        assertEquals(61L, number(map, "lastCount"));
        assertEquals(0L, number(map, "progressExpiredTime"));
    }

    @Test
    public void test_endProgress_touchesNothingElse() {
        existingMetadata = "{\"lastCount\":61,\"progressCount\":7,\"progressExpiredTime\":99}";
        service.endProgress("D3");

        final Map<String, Object> map = stored.get(0).map();
        assertEquals(3, map.size());
        assertEquals(61L, number(map, "lastCount"));
        assertEquals(7L, number(map, "progressCount"));
        assertEquals(0L, number(map, "progressExpiredTime"));
    }

    @Test
    public void test_write_storedMetadataThatIsNotAJsonObjectIsReplaced() {
        existingMetadata = "not json";
        service.endProgress("W1");
        assertEquals(1, stored.get(0).map().size());

        existingMetadata = "[1,2]";
        service.endProgress("W2");
        assertEquals(1, stored.get(1).map().size());
    }

    @Test
    public void test_write_metadataIsCapped() {
        // a stored value that is within the cap, to which the progress keys do not fit
        existingMetadata = "{\"other\":\"" + "x".repeat(CrawlStateService.MAX_METADATA_LENGTH - 30) + "\"}";
        assertTrue(existingMetadata.length() < CrawlStateService.MAX_METADATA_LENGTH);

        service.saveProgress("W1", 1, 1000);

        assertEquals(0, stored.size());
    }

    @Test
    public void test_write_failedReadIsNotFollowedByAWrite() {
        existingMetadata = "{\"lastCount\":61}";
        fetchFails = true;
        service.saveProgress("W1", 1, 1000);
        assertEquals(0, stored.size());

        // the keys the failed read did not see are still there once it succeeds
        fetchFails = false;
        service.saveProgress("W1", 2, 1000);
        assertEquals(61L, number(stored.get(0).map(), "lastCount"));
    }

    @Test
    public void test_write_failedStoreIsRetriedFromWhatWasWritten() {
        existingMetadata = "{\"lastCount\":61}";
        storeFails = true;
        service.saveProgress("W1", 1, 1000);
        assertEquals(0, stored.size());

        storeFails = false;
        service.endProgress("W1");
        final Map<String, Object> map = stored.get(0).map();
        assertEquals(61L, number(map, "lastCount"));
        // the progress that was never written is not claimed to have been
        assertFalse(map.containsKey("progressCount"));
    }

    @Test
    public void test_write_failureIsWarnedOnceAndThenDebug() {
        storeFails = true;
        final LogCapturingAppender appender = LogCapturingAppender.attach(CrawlStateService.class);
        try {
            service.saveProgress("W1", 1, 1000);
            service.saveProgress("W1", 2, 1000);
            service.saveProgress("W2", 3, 1000);
            // not ERROR: an error is an operator notification, and this is only the progress display
            assertEquals(0, appender.eventsAt(Level.ERROR).size());
            assertEquals(1, appender.eventsAt(Level.WARN).size());
            assertEquals(2, appender.eventsAt(Level.DEBUG).size());
        } finally {
            appender.detach();
        }
    }

    private static String running(final long count, final Long lastCount, final long updated, final long expiredIn) {
        return "{\"progressCount\":" + count + (lastCount != null ? ",\"lastCount\":" + lastCount : "") + ",\"progressUpdatedTime\":"
                + updated + ",\"progressExpiredTime\":" + (System.currentTimeMillis() + expiredIn) + "}";
    }

    @Test
    public void test_getRunningProgress_listsUnexpiredProgressOnly() {
        configs.add(new ConfigMetadata("W1", "Zeta site", running(50, 200L, 111, 20000)));
        configs.add(new ConfigMetadata("F2", "Alpha files", running(10, null, 222, 20000)));
        configs.add(new ConfigMetadata("D3", null, running(900, 100L, 333, 20000)));
        // a progress that was not renewed in time: the crawler was terminated without clearing it
        configs.add(new ConfigMetadata("W4", "Stale", running(5, 10L, 1, -1)));
        // a crawl that ended
        configs.add(new ConfigMetadata("W5", "Ended", "{\"lastCount\":61,\"progressCount\":61,\"progressExpiredTime\":0}"));
        configs.add(new ConfigMetadata("W6", "Never crawled", null));

        final List<CrawlProgress> list = service.getRunningProgress();

        assertEquals(3, list.size());
        // ordered by name; a configuration without a name shows its ID
        assertEquals("Alpha files", list.get(0).getName());
        assertEquals("D3", list.get(1).getName());
        assertEquals("Zeta site", list.get(2).getName());

        final CrawlProgress files = list.get(0);
        assertEquals("F2", files.getConfigId());
        assertEquals(10L, files.getCount());
        assertEquals(0L, files.getLastCount());
        assertNull(files.getPercent());
        assertEquals(222L, files.getUpdatedTime());

        assertEquals(Integer.valueOf(99), list.get(1).getPercent());
        assertEquals(Integer.valueOf(25), list.get(2).getPercent());
    }

    @Test
    public void test_getRunningProgress_ignoresMetadataItCannotRead() {
        configs.add(new ConfigMetadata("W1", "Broken", "not json"));
        configs.add(new ConfigMetadata("W2", "Array", "[1,2]"));
        configs.add(new ConfigMetadata("W3", "Too long", "{\"x\":\"" + "y".repeat(CrawlStateService.MAX_METADATA_LENGTH) + "\"}"));
        configs.add(new ConfigMetadata("W4", "Wrong types", "{\"progressExpiredTime\":\"soon\",\"progressCount\":\"many\"}"));
        configs.add(new ConfigMetadata("W5", "Fine", running(1, 2L, 3, 20000)));

        final List<CrawlProgress> list = service.getRunningProgress();

        assertEquals(1, list.size());
        assertEquals("Fine", list.get(0).getName());
    }

    @Test
    public void test_getRunningProgress_failureIsEmpty() {
        listFails = true;
        assertEquals(0, service.getRunningProgress().size());
    }

    @Test
    public void test_countDocuments() {
        assertEquals(321L, service.countDocuments("D1", "session-1"));

        assertEquals("fess.update", countedIndex);
        // the segment is shared by all the data configurations of a job, so the config ID is part of the query
        final String query = countedQuery.toString();
        assertTrue(query.contains("config_id"));
        assertTrue(query.contains("D1"));
        assertTrue(query.contains("segment"));
        assertTrue(query.contains("session-1"));
    }

    @Test
    public void test_countDocuments_failureIsMinusOne() {
        countFails = true;
        assertEquals(-1L, service.countDocuments("D1", "s"));
    }
}
