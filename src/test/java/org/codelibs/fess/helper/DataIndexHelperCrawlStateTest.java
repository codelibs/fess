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
import java.util.function.LongSupplier;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.CrawlStateService;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.ds.DataStore;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class DataIndexHelperCrawlStateTest extends UnitFessTestCase {

    private final List<String> calls = new ArrayList<>();

    private boolean stopped;

    private boolean storeFails;

    private boolean commitFails;

    private boolean noDataStore;

    private long progressInterval;

    /** How many documents the data store stores through the callback it is given. */
    private int storedDocuments;

    /** False for a service that has only queued the write, as the real one does. */
    private boolean runSuppliers;

    private CrawlStateService crawlStateService;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        calls.clear();
        stopped = false;
        storeFails = false;
        commitFails = false;
        noDataStore = false;
        progressInterval = 10000L;
        storedDocuments = 0;
        runSuppliers = true;
        crawlStateService = new CrawlStateService() {
            @Override
            public void saveProgressAsync(final String configId, final LongSupplier count, final long ttl) {
                if (!runSuppliers) {
                    calls.add("queued:" + configId);
                    return;
                }
                final long value = count.getAsLong();
                if (value >= 0) {
                    calls.add("progress:" + configId + ":" + value);
                }
            }

            @Override
            public void saveCompleted(final String configId, final long count) {
                calls.add("completed:" + configId + ":" + count);
            }

            @Override
            public void endProgress(final String configId) {
                calls.add("end:" + configId);
            }
        };
        ComponentUtil.register(new FailureUrlService() {
            @Override
            public FailureUrl store(final CrawlingConfig crawlingConfig, final String errorName, final String url, final Throwable e) {
                return null;
            }
        }, FailureUrlService.class.getCanonicalName());
        ComponentUtil.register(new SystemHelper() {
            @Override
            public boolean isForceStop() {
                return stopped;
            }
        }, "systemHelper");
        ComponentUtil.register(new DataStoreFactory() {
            @Override
            public DataStore getDataStore(final String name) {
                if (noDataStore) {
                    return null;
                }
                return new DataStore() {
                    @Override
                    public void store(final DataConfig dataConfig, final IndexUpdateCallback callback, final DataStoreParams params) {
                        for (int i = 0; i < storedDocuments; i++) {
                            callback.store(params, new java.util.HashMap<>());
                        }
                        if (storeFails) {
                            throw new IllegalStateException("data source is down");
                        }
                    }

                    @Override
                    public void stop() {
                    }
                };
            }
        }, "dataStoreFactory");
    }

    private DataConfig dataConfig(final String id) {
        final DataConfig dataConfig = new DataConfig();
        dataConfig.setId(id);
        dataConfig.setName("test" + id);
        dataConfig.setHandlerName("handler");
        return dataConfig;
    }

    private DataIndexHelper.DataCrawlingThread thread(final DataConfig dataConfig) {
        final IndexUpdateCallback callback = new IndexUpdateCallback() {
            @Override
            public long getExecuteTime() {
                return 0L;
            }

            @Override
            public long getDocumentSize() {
                return 0L;
            }

            @Override
            public void commit() {
                if (commitFails) {
                    throw new IllegalStateException("bulk rejected");
                }
            }

            @Override
            public void store(final DataStoreParams params, final java.util.Map<String, Object> data) {
            }
        };
        final DataStoreParams params = new DataStoreParams();
        params.put(Constants.SESSION_ID, "session1");
        // the stale documents are not the subject here
        params.put("delete_old_docs", Constants.FALSE);
        return new DataIndexHelper.DataCrawlingThread(dataConfig, callback, params) {
            @Override
            protected CrawlStateService getCrawlStateService() {
                return crawlStateService;
            }
        };
    }

    @Test
    public void test_process_savesTheNumberOfStoredDocuments() {
        storedDocuments = 55;
        thread(dataConfig("1")).process();
        // the count of the progress, not a second measure taken from the index
        assertEquals(List.of("completed:D1:55"), calls);
    }

    @Test
    public void test_process_failedCrawlKeepsThePreviousCount() {
        storeFails = true;
        thread(dataConfig("1")).process();
        assertEquals(List.of("end:D1"), calls);
    }

    @Test
    public void test_process_failedCommitKeepsThePreviousCount() {
        commitFails = true;
        thread(dataConfig("1")).process();
        assertEquals(List.of("end:D1"), calls);
    }

    @Test
    public void test_process_stoppedCrawlKeepsThePreviousCount() {
        stopped = true;
        thread(dataConfig("1")).process();
        assertEquals(List.of("end:D1"), calls);
    }

    @Test
    public void test_process_missingDataStoreEndsTheProgress() {
        noDataStore = true;
        thread(dataConfig("1")).process();
        assertEquals(List.of("end:D1"), calls);
    }

    private DataIndexHelper helper() {
        return new DataIndexHelper() {
            @Override
            protected CrawlStateService getCrawlStateService() {
                return crawlStateService;
            }

            @Override
            protected long getProgressInterval() {
                return progressInterval;
            }
        };
    }

    @Test
    public void test_run_countsTheDocumentsTheDataStoreStores() {
        final DataConfig dataConfig = dataConfig("1");
        // what an earlier crawl of the same configuration counted is not carried over
        dataConfig.getProgressCounter().set(40);
        storedDocuments = 3;

        thread(dataConfig).run();

        assertEquals(3L, dataConfig.getProgressCounter().get());
        assertEquals(List.of("completed:D1:3"), calls);
    }

    @Test
    public void test_process_failedStoreKeepsWhatWasCounted() {
        final DataConfig dataConfig = dataConfig("1");
        storedDocuments = 2;
        storeFails = true;

        thread(dataConfig).process();

        assertEquals(2L, dataConfig.getProgressCounter().get());
    }

    @Test
    public void test_saveProgress_runningThreadsOnly() {
        final DataIndexHelper helper = helper();
        final DataConfig config1 = dataConfig("1");
        final DataConfig config2 = dataConfig("2");
        config1.getProgressCounter().set(7);
        config2.getProgressCounter().set(9);
        helper.dataCrawlingThreadList.add(thread(config1));
        helper.dataCrawlingThreadList.add(thread(config2));
        helper.dataCrawlingThreadList.add(thread(dataConfig("3")));

        helper.saveProgress(List.of(Constants.RUNNING, Constants.DONE, Constants.READY));

        // no query to the search engine: the number is the counter of the configuration
        assertEquals(List.of("progress:D1:7"), calls);
    }

    @Test
    public void test_saveProgress_doesNotWriteOnTheLoopThread() {
        final DataIndexHelper helper = helper();
        helper.dataCrawlingThreadList.add(thread(dataConfig("1")));
        runSuppliers = false;

        helper.saveProgress(List.of(Constants.RUNNING));

        assertEquals(List.of("queued:D1"), calls);
    }

    @Test
    public void test_saveProgress_writesOncePerProgressInterval() {
        final DataIndexHelper helper = helper();
        helper.dataCrawlingThreadList.add(thread(dataConfig("1")));
        final List<String> statuses = List.of(Constants.RUNNING);

        progressInterval = 60000L;
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        assertEquals(1, calls.size());

        // the crawling execution interval does not decide how often it is written
        helper.setCrawlingExecutionInterval(0L);
        helper.saveProgress(statuses);
        assertEquals(1, calls.size());

        progressInterval = 0L;
        helper.saveProgress(statuses);
        assertEquals(2, calls.size());
    }
}
