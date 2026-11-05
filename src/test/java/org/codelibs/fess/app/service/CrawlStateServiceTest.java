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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.app.service.CrawlStateService.ConfigMetadata;
import org.codelibs.fess.entity.CrawlProgress;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
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

    /** The timeouts the requests were given, in order. */
    private final List<String> timeouts = new ArrayList<>();

    /** The tasks the service handed to its writer thread, not yet run. */
    private final List<Runnable> queued = new ArrayList<>();

    private List<WebConfig> webConfigs;

    private List<FileConfig> fileConfigs;

    private List<DataConfig> dataConfigs;

    private final List<Integer> selectedMax = new ArrayList<>();

    /** What the configuration holds in its metadata field before the crawler writes. */
    private String existingMetadata;

    private boolean fetchFails;

    private boolean storeFails;

    private String configIndex;

    private List<ConfigMetadata> configs;

    private boolean listFails;

    private boolean realList;

    private CrawlStateService service;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        LogUtil.resetWarnOnce();
        CrawlStateService.reset();
        stored.clear();
        fetched.clear();
        timeouts.clear();
        queued.clear();
        selectedMax.clear();
        webConfigs = new ArrayList<>();
        fileConfigs = new ArrayList<>();
        dataConfigs = new ArrayList<>();
        existingMetadata = null;
        fetchFails = false;
        storeFails = false;
        configIndex = "fess_config";
        configs = new ArrayList<>();
        listFails = false;
        realList = false;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return configIndex;
            }

            @Override
            public Integer getPageWebConfigMaxFetchSizeAsInteger() {
                return 2;
            }

            @Override
            public Integer getPageFileConfigMaxFetchSizeAsInteger() {
                return 2;
            }

            @Override
            public Integer getPageDataConfigMaxFetchSizeAsInteger() {
                return 2;
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
            protected String fetchMetadata(final String index, final String id, final String timeout) {
                fetched.add(index + "/" + id);
                timeouts.add(timeout);
                if (fetchFails) {
                    throw new IllegalStateException("search engine is down");
                }
                return existingMetadata;
            }

            @Override
            protected void storeMetadata(final String index, final String id, final String metadata, final String timeout) {
                timeouts.add(timeout);
                if (storeFails) {
                    throw new IllegalStateException("search engine is down");
                }
                stored.add(new Stored(index, id, metadata));
            }

            @Override
            protected List<ConfigMetadata> listConfigMetadata() {
                if (realList) {
                    return super.listConfigMetadata();
                }
                if (listFails) {
                    throw new IllegalStateException("search engine is down");
                }
                return configs;
            }

            @Override
            protected void execute(final Runnable task) {
                queued.add(task);
            }

            @Override
            protected List<WebConfig> selectWebConfigs(final int max) {
                selectedMax.add(max);
                return webConfigs;
            }

            @Override
            protected List<FileConfig> selectFileConfigs(final int max) {
                selectedMax.add(max);
                return fileConfigs;
            }

            @Override
            protected List<DataConfig> selectDataConfigs(final int max) {
                selectedMax.add(max);
                return dataConfigs;
            }
        };
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        CrawlStateService.reset();
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

    // ===================================================================================
    //                                                                      Hardening
    //                                                                      =========

    @Test
    public void test_timeouts_progressIsShortAndTheResultIsLonger() {
        service.saveProgress("W1", 1, 1000);
        // the read of the stored metadata and the write
        assertEquals(List.of(CrawlStateService.PROGRESS_TIMEOUT, CrawlStateService.PROGRESS_TIMEOUT), timeouts);

        timeouts.clear();
        service.saveCompleted("W1", 5);
        assertEquals(List.of(CrawlStateService.RESULT_TIMEOUT), timeouts);

        timeouts.clear();
        service.endProgress("F2");
        assertEquals(List.of(CrawlStateService.RESULT_TIMEOUT, CrawlStateService.RESULT_TIMEOUT), timeouts);
        assertTrue(CrawlStateService.PROGRESS_TIMEOUT.length() <= 2);
    }

    @Test
    public void test_saveProgressAsync_doesNothingOnTheCallingThread() {
        final AtomicInteger counted = new AtomicInteger();
        service.saveProgressAsync("W1", () -> {
            counted.incrementAndGet();
            return 7;
        }, 1000);

        // neither the count nor a request to the search engine has happened yet
        assertEquals(0, counted.get());
        assertEquals(0, fetched.size());
        assertEquals(0, stored.size());
        assertEquals(1, queued.size());

        queued.get(0).run();
        assertEquals(1, counted.get());
        assertEquals(7L, number(stored.get(0).map(), "progressCount"));
    }

    @Test
    public void test_saveProgressAsync_dropsAWriteWhileTheLastOneIsPending() {
        service.saveProgressAsync("W1", () -> 1, 1000);
        service.saveProgressAsync("W1", () -> 2, 1000);
        service.saveProgressAsync("W1", () -> 3, 1000);
        // another configuration has its own slot
        service.saveProgressAsync("F1", () -> 4, 1000);
        assertEquals(2, queued.size());

        queued.get(0).run();
        assertEquals(1L, number(stored.get(0).map(), "progressCount"));
        // once the write is done the next one is accepted
        service.saveProgressAsync("W1", () -> 5, 1000);
        assertEquals(3, queued.size());
    }

    @Test
    public void test_saveProgressAsync_aWriteQueuedBeforeTheEndDoesNotFollowIt() {
        service.saveProgressAsync("W1", () -> 3, 1000);
        service.saveCompleted("W1", 61);
        assertEquals(1, stored.size());

        queued.get(0).run();
        // the late progress would have set progressExpiredTime again and shown a finished crawl as running
        assertEquals(1, stored.size());
        assertEquals(0L, number(stored.get(0).map(), "progressExpiredTime"));

        // and nothing is queued for a crawl that has ended
        service.saveProgressAsync("W1", () -> 4, 1000);
        assertEquals(1, queued.size());
    }

    @Test
    public void test_saveProgressAsync_unknownOrFailingCountWritesNothingAndFreesTheSlot() {
        service.saveProgressAsync("W1", () -> -1, 1000);
        queued.get(0).run();
        assertEquals(0, stored.size());

        service.saveProgressAsync("W1", () -> {
            throw new IllegalStateException("search engine is down");
        }, 1000);
        queued.get(1).run();
        assertEquals(0, stored.size());

        service.saveProgressAsync("W1", () -> 2, 1000);
        assertEquals(3, queued.size());
        service.saveProgressAsync("bad id", () -> 2, 1000);
        assertEquals(3, queued.size());
    }

    @Test
    public void test_write_tooLongMetadataLosesTheKeysItDoesNotKnow() {
        // a number under a key of its own, so that only the key marks it as a stray
        existingMetadata = "{\"lastCount\":61,\"" + "k".repeat(CrawlStateService.MAX_METADATA_LENGTH - 40) + "\":1}";
        assertTrue(existingMetadata.length() < CrawlStateService.MAX_METADATA_LENGTH);

        service.saveProgress("W1", 1, 1000);

        // the write that does not fit is not given up: the stray key goes, the known keys stay
        assertEquals(1, stored.size());
        final Map<String, Object> map = stored.get(0).map();
        assertEquals(4, map.size());
        assertEquals(61L, number(map, "lastCount"));
        assertEquals(1L, number(map, "progressCount"));
        assertTrue(stored.get(0).json.length() <= CrawlStateService.MAX_METADATA_LENGTH);

        // and the next write does not bring it back
        service.endProgress("W1");
        assertEquals(4, stored.get(1).map().size());
    }

    @Test
    public void test_write_tooLongMetadataLosesAKnownKeyThatIsNotANumber() {
        existingMetadata = "{\"lastCount\":\"" + "x".repeat(CrawlStateService.MAX_METADATA_LENGTH - 40) + "\"}";

        service.saveProgress("W1", 1, 1000);

        assertEquals(1, stored.size());
        assertFalse(stored.get(0).map().containsKey("lastCount"));
        assertEquals(1L, number(stored.get(0).map(), "progressCount"));
    }

    @Test
    public void test_getRunningProgress_doesNotTrustAFarAwayExpiry() {
        final long now = System.currentTimeMillis();
        configs.add(new ConfigMetadata("W1", "Forever", "{\"progressCount\":5,\"progressExpiredTime\":" + Long.MAX_VALUE + "}"));
        configs.add(new ConfigMetadata("W2", "Two days",
                "{\"progressCount\":5,\"progressExpiredTime\":" + (now + 2 * 24 * 3600 * 1000L) + "}"));
        configs.add(new ConfigMetadata("W3", "Huge", "{\"progressCount\":5,\"progressExpiredTime\":99999999999999999999999}"));
        configs.add(new ConfigMetadata("W4", "Fraction", "{\"progressCount\":5,\"progressExpiredTime\":" + (now + 20000) + ".5}"));
        configs.add(new ConfigMetadata("W5", "Soon", "{\"progressCount\":5,\"progressExpiredTime\":" + (now + 20000) + "}"));

        final List<CrawlProgress> list = service.getRunningProgress();

        assertEquals(1, list.size());
        assertEquals("Soon", list.get(0).getName());
    }

    @Test
    public void test_getRunningProgress_negativeCountsAreIgnored() {
        configs.add(new ConfigMetadata("W1", "Negative", running(-5, -10L, 1, 20000)));
        configs.add(new ConfigMetadata("W2", "Overflow", running(Long.MAX_VALUE, 1L, 1, 20000)));

        final List<CrawlProgress> list = service.getRunningProgress();

        assertEquals(2, list.size());
        assertEquals(0L, list.get(0).getCount());
        assertNull(list.get(0).getPercent());
        assertEquals(Integer.valueOf(99), list.get(1).getPercent());
    }

    @Test
    public void test_listConfigMetadata_readsEachTypeUpToItsCap() {
        final WebConfig web = new WebConfig();
        web.setId("1");
        web.setName("web");
        web.setMetadata("{}");
        webConfigs.add(web);
        final FileConfig file = new FileConfig();
        file.setId("2");
        file.setName("file");
        fileConfigs.add(file);
        final DataConfig data = new DataConfig();
        data.setId("3");
        data.setName("data");
        dataConfigs.add(data);

        final LogCapturingAppender appender = LogCapturingAppender.attach(CrawlStateService.class);
        try {
            realList = true;
            final List<ConfigMetadata> list = service.listConfigMetadata();
            assertEquals(List.of("W1", "F2", "D3"), list.stream().map(ConfigMetadata::configId).toList());
            assertEquals(List.of(2, 2, 2), selectedMax);
            // under the cap, nothing to say
            assertEquals(0, appender.eventsAt(Level.WARN).size());

            // a type that reaches its cap is reported once, and not as a crawl that is skipped
            final WebConfig more = new WebConfig();
            more.setId("4");
            webConfigs.add(more);
            service.listConfigMetadata();
            service.listConfigMetadata();
            assertEquals(1, appender.eventsAt(Level.WARN).size());
            assertTrue(appender.messagesAt(Level.WARN).get(0).contains("page.web.config.max.fetch.size"));
            assertFalse(appender.messagesAt(Level.WARN).get(0).contains("not crawled"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_state_isSharedByEveryInstance() {
        // the container makes a new service for every lookup, and the crawl loop and the data store threads each get one
        final CrawlStateService other = new CrawlStateService() {
            @Override
            protected String fetchMetadata(final String index, final String id, final String timeout) {
                return existingMetadata;
            }

            @Override
            protected void storeMetadata(final String index, final String id, final String metadata, final String timeout) {
                stored.add(new Stored(index, id, metadata));
            }
        };

        service.saveProgressAsync("W1", () -> 1, 1000);
        // the same configuration is pending for the other instance too
        other.saveProgressAsync("W1", () -> 2, 1000);
        assertEquals(1, queued.size());

        // the end seen by one instance holds back the queued write of the other
        other.saveCompleted("W1", 61);
        assertEquals(1, stored.size());
        queued.get(0).run();
        assertEquals(1, stored.size());

        // a progress written directly is held back the same way
        other.endProgress("F9");
        service.saveProgress("F9", 3, 1000);
        assertEquals(2, stored.size());
    }
}
