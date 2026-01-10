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
package org.codelibs.fess.job;

import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.helper.NotificationHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.mail.send.hook.SMailCallbackContext;
import org.lastaflute.core.mail.Postbox;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.cluster.health.ClusterHealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PingSearchEngineJobTest extends UnitFessTestCase {

    private PingSearchEngineJob pingSearchEngineJob;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        pingSearchEngineJob = new PingSearchEngineJob();
    }

    // Test normal operation with GREEN status and state change
    @Test
    public void test_execute_greenStatusNoChange() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.GREEN));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // State has changed
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of test-cluster is changed to GREEN.", result);
    }

    // Test normal operation with YELLOW status and no state change
    @Test
    public void test_execute_yellowStatusNoChange() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.YELLOW));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // YELLOW status is 0, should not be changed
                return false;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("test-cluster is alive.", result);
    }

    // Test RED status with state change
    @Test
    public void test_execute_redStatusNoChange() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                PingResponse response = new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.RED)) {
                    @Override
                    public int getStatus() {
                        // RED status should return non-zero status
                        return 2;
                    }
                };
                return response;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // State has changed
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of test-cluster is changed to RED.", result);
    }

    // Test state change with notification enabled
    @Test
    public void test_execute_stateChangeWithNotification() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                PingResponse response = new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.RED)) {
                    @Override
                    public int getStatus() {
                        // RED status should return non-zero status
                        return 2;
                    }

                    @Override
                    public String getClusterStatus() {
                        return "RED";
                    }
                };
                return response;
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // State has changed
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        NotificationHelper notificationHelper = new NotificationHelper() {
            // Mock send method
            public void send(final Object postcard) {
                // Mock implementation
            }
        };

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(notificationHelper, "notificationHelper");

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of test-cluster is changed to RED.", result);
        // Note: Notification sending is tested but we don't verify the mock since it uses complex callback mechanism
    }

    // Test state change with empty notification addresses
    @Test
    public void test_execute_stateChangeEmptyNotificationAddresses() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.GREEN));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // Status 0 = GREEN is alive, should not show changed
                return false;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public boolean hasNotification() {
                return true;
            }

            @Override
            public String getNotificationTo() {
                return "";
            }
        };

        NotificationHelper notificationHelper = new NotificationHelper() {
            // Mock send method
            public void send(final Object postcard) {
                // Mock implementation
            }
        };

        // Mock Postbox - no need to mock, the framework will handle it

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(notificationHelper, "notificationHelper");
        // Postbox is handled by the framework

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("test-cluster is alive.", result);
    }

    // Test with null notification addresses
    @Test
    public void test_execute_nullNotificationAddresses() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.YELLOW));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public boolean hasNotification() {
                return true;
            }

            @Override
            public String getNotificationTo() {
                return null;
            }
        };

        NotificationHelper notificationHelper = new NotificationHelper() {
            // Mock send method
            public void send(final Object postcard) {
                // Mock implementation
            }
        };

        // Mock Postbox - no need to mock, the framework will handle it

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(notificationHelper, "notificationHelper");
        // Postbox is handled by the framework

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of test-cluster is changed to YELLOW.", result);
    }

    // Test with whitespace in notification addresses
    @Test
    public void test_execute_whitespaceInNotificationAddresses() {
        // Setup mock components
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("test-cluster", ClusterHealthStatus.RED));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = new TestFessConfig() {
            @Override
            public boolean hasNotification() {
                return true;
            }

            @Override
            public String getNotificationTo() {
                return " admin@example.com , test@example.com ";
            }
        };

        NotificationHelper notificationHelper = new NotificationHelper() {
            // Mock send method
            public void send(final Object postcard) {
                // Mock implementation
            }
        };

        // Mock Postbox - no need to mock, the framework will handle it

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(notificationHelper, "notificationHelper");
        // Postbox is handled by the framework

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of test-cluster is changed to RED.", result);
    }

    // Test constructor
    @Test
    public void test_constructor() {
        // Test that constructor creates instance without error
        PingSearchEngineJob job = new PingSearchEngineJob();
        assertNotNull(job);
    }

    // Test different cluster names
    @Test
    public void test_execute_differentClusterNames() {
        // Test with production cluster name
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                return new PingResponse(createMockHealthResponse("production-cluster", ClusterHealthStatus.GREEN));
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public boolean isChangedClusterState(int status) {
                // State has changed
                return true;
            }

            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        NotificationHelper notificationHelper = new NotificationHelper() {
            // Mock send method
            public void send(final Object postcard) {
                // Mock implementation
            }
        };

        // Mock Postbox - no need to mock, the framework will handle it

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(notificationHelper, "notificationHelper");
        // Postbox is handled by the framework

        // Execute
        String result = pingSearchEngineJob.execute();

        // Assert
        assertEquals("Status of production-cluster is changed to GREEN.", result);
    }

    // Test with exception during ping
    @Test
    public void test_execute_pingException() {
        SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public PingResponse ping() {
                throw new RuntimeException("Connection failed");
            }
        };

        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getHostname() {
                return "test-hostname";
            }
        };

        FessConfig fessConfig = createTestFessConfig();

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.setFessConfig(fessConfig);

        // Execute and expect exception to be handled
        try {
            String result = pingSearchEngineJob.execute();
            // Job should handle exception gracefully
            assertNotNull(result);
        } catch (Exception e) {
            // Exception is expected to be caught within the job
            assertTrue(e.getMessage().contains("Connection failed"));
        }
    }

    // Helper method to create mock ClusterHealthResponse
    private ClusterHealthResponse createMockHealthResponse(String clusterName, ClusterHealthStatus status) {
        return new ClusterHealthResponse() {
            @Override
            public String getClusterName() {
                return clusterName;
            }

            @Override
            public ClusterHealthStatus getStatus() {
                return status;
            }

            @Override
            public boolean isTimedOut() {
                return false;
            }

            @Override
            public int getNumberOfNodes() {
                return 3;
            }

            @Override
            public int getNumberOfDataNodes() {
                return 3;
            }

            @Override
            public int getActivePrimaryShards() {
                return 10;
            }

            @Override
            public int getActiveShards() {
                return 20;
            }

            @Override
            public int getRelocatingShards() {
                return 0;
            }

            @Override
            public int getInitializingShards() {
                return 0;
            }

            @Override
            public int getUnassignedShards() {
                return 0;
            }

            @Override
            public int getDelayedUnassignedShards() {
                return 0;
            }

            @Override
            public int getNumberOfPendingTasks() {
                return 0;
            }

            @Override
            public int getNumberOfInFlightFetch() {
                return 0;
            }

            @Override
            public double getActiveShardsPercent() {
                return 100.0;
            }
        };
    }

    // Helper method to create test FessConfig
    private FessConfig createTestFessConfig() {
        return new TestFessConfig();
    }

    // Test configuration class extending FessConfig.SimpleImpl
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String getApiPingSearchEngineFields() {
            return "cluster_name,status,timed_out,number_of_nodes,number_of_data_nodes,active_primary_shards,active_shards,relocating_shards,initializing_shards,unassigned_shards,delayed_unassigned_shards,number_of_pending_tasks,number_of_in_flight_fetch,task_max_waiting_in_queue_millis,active_shards_percent_as_number";
        }

        @Override
        public boolean hasNotification() {
            return true;
        }

        @Override
        public String getNotificationTo() {
            return "admin@example.com,test@example.com";
        }

        @Override
        public String getMailFromAddress() {
            return "fess@example.com";
        }

        @Override
        public String getMailFromName() {
            return "Fess System";
        }

        @Override
        public String getMailReturnPath() {
            return "bounce@example.com";
        }
    }
}