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

    private long documentCount;

    private CrawlStateService crawlStateService;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        calls.clear();
        stopped = false;
        storeFails = false;
        commitFails = false;
        noDataStore = false;
        documentCount = 55;
        crawlStateService = new CrawlStateService() {
            @Override
            public void saveProgress(final String configId, final long count, final long ttl) {
                calls.add("progress:" + configId + ":" + count);
            }

            @Override
            public void saveCompleted(final String configId, final long count) {
                calls.add("completed:" + configId + ":" + count);
            }

            @Override
            public void endProgress(final String configId) {
                calls.add("end:" + configId);
            }

            @Override
            public long countDocuments(final String configId, final String segment) {
                calls.add("count:" + configId + ":" + segment);
                return documentCount;
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

            @Override
            protected void refreshIndex() {
                calls.add("refresh");
            }
        };
    }

    @Test
    public void test_process_savesTheNumberOfIndexedDocuments() {
        thread(dataConfig("1")).process();
        // the segment is the parent session ID that all the data configurations of the job share
        assertEquals(List.of("refresh", "count:D1:session1", "completed:D1:55"), calls);
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
    public void test_process_countFailureKeepsThePreviousCount() {
        documentCount = -1;
        thread(dataConfig("1")).process();
        assertEquals(List.of("refresh", "count:D1:session1", "end:D1"), calls);
    }

    @Test
    public void test_process_missingDataStoreEndsTheProgress() {
        noDataStore = true;
        thread(dataConfig("1")).process();
        assertEquals(List.of("end:D1"), calls);
    }

    @Test
    public void test_saveProgress_runningThreadsOnly() {
        final DataIndexHelper helper = new DataIndexHelper() {
            @Override
            protected CrawlStateService getCrawlStateService() {
                return crawlStateService;
            }
        };
        helper.dataCrawlingThreadList.add(thread(dataConfig("1")));
        helper.dataCrawlingThreadList.add(thread(dataConfig("2")));
        helper.dataCrawlingThreadList.add(thread(dataConfig("3")));

        helper.saveProgress("session1", List.of(Constants.RUNNING, Constants.DONE, Constants.READY));

        assertEquals(List.of("count:D1:session1", "progress:D1:55"), calls);
    }

    @Test
    public void test_saveProgress_writesOncePerIntervalAndSkipsFailedCounts() {
        final DataIndexHelper helper = new DataIndexHelper() {
            @Override
            protected CrawlStateService getCrawlStateService() {
                return crawlStateService;
            }
        };
        helper.dataCrawlingThreadList.add(thread(dataConfig("1")));
        final List<String> statuses = List.of(Constants.RUNNING);

        helper.setCrawlingExecutionInterval(60000L);
        helper.saveProgress("session1", statuses);
        helper.saveProgress("session1", statuses);
        assertEquals(2, calls.size());

        calls.clear();
        helper.setCrawlingExecutionInterval(0L);
        documentCount = -1;
        helper.saveProgress("session1", statuses);
        // a failed count is not written as zero progress
        assertEquals(List.of("count:D1:session1"), calls);
    }
}
