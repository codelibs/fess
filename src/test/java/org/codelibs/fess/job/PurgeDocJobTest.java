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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PurgeDocJobTest extends UnitFessTestCase {

    private PurgeDocJob purgeDocJob;
    private SearchEngineClient searchEngineClient;
    private FessConfig fessConfig;

    private boolean deleteByQueryCalled;
    private String deleteIndex;
    private QueryBuilder deleteQuery;
    private String expiresFieldName;
    private String documentUpdateIndex;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        purgeDocJob = new PurgeDocJob();

        // Reset flags and variables
        deleteByQueryCalled = false;
        deleteIndex = null;
        deleteQuery = null;
        expiresFieldName = "expires";
        documentUpdateIndex = "fess.update";

        // Create mock SearchEngineClient
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                deleteByQueryCalled = true;
                deleteIndex = index;
                deleteQuery = query;
                return 1L;
            }
        };

        // Create mock FessConfig
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return documentUpdateIndex;
            }
        };

        // Register components
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void test_constructor() {
        // Test that constructor creates instance without error
        PurgeDocJob job = new PurgeDocJob();
        assertNotNull(job);
    }

    @Test
    public void test_execute_success() {
        // Execute the job
        String result = purgeDocJob.execute();

        // Assert deleteByQuery was called
        assertTrue(deleteByQueryCalled);

        // Assert correct index was used
        assertEquals("fess.update", deleteIndex);

        // Assert query builder is correct
        assertNotNull(deleteQuery);
        assertTrue(deleteQuery instanceof RangeQueryBuilder);
        RangeQueryBuilder rangeQuery = (RangeQueryBuilder) deleteQuery;
        assertEquals("expires", rangeQuery.fieldName());
        assertEquals("now", rangeQuery.to());

        // Assert result is empty when successful
        assertEquals("", result);
    }

    @Test
    public void test_execute_withException() {
        // Create mock SearchEngineClient that throws exception
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new RuntimeException("Delete operation failed");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert error message is in the result
        assertTrue(result.contains("Delete operation failed"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withCustomFieldName() {
        // Configure custom expires field name
        expiresFieldName = "custom_expires_field";

        // Re-register FessConfig with updated field name
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return documentUpdateIndex;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // Create new instance after registering the updated config
        purgeDocJob = new PurgeDocJob();

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert deleteByQuery was called
        assertTrue(deleteByQueryCalled);

        // Assert correct field name was used in query
        assertNotNull(deleteQuery);
        assertTrue(deleteQuery instanceof RangeQueryBuilder);
        RangeQueryBuilder rangeQuery = (RangeQueryBuilder) deleteQuery;
        assertEquals("custom_expires_field", rangeQuery.fieldName());

        // Assert result is empty when successful
        assertEquals("", result);
    }

    @Test
    public void test_execute_withCustomIndexName() {
        // Configure custom index name
        documentUpdateIndex = "custom.document.index";

        // Re-register FessConfig with updated index name
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "custom.document.index";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // Create new instance after registering the updated config
        purgeDocJob = new PurgeDocJob();

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert deleteByQuery was called
        assertTrue(deleteByQueryCalled);

        // Assert correct index was used
        assertEquals("custom.document.index", deleteIndex);

        // Assert result is empty when successful
        assertEquals("", result);
    }

    @Test
    public void test_execute_withNullPointerException() {
        // Create mock SearchEngineClient that throws NullPointerException
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new NullPointerException("Null value encountered");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert error message is in the result
        assertTrue(result.contains("Null value encountered"));
    }

    @Test
    public void test_execute_withIllegalArgumentException() {
        // Create mock SearchEngineClient that throws IllegalArgumentException
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new IllegalArgumentException("Invalid argument provided");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert error message is in the result
        assertTrue(result.contains("Invalid argument provided"));
    }

    @Test
    public void test_execute_queryBuilderCreation() {
        // Execute the job
        purgeDocJob.execute();

        // Verify the query builder was created correctly
        assertNotNull(deleteQuery);
        assertTrue(deleteQuery instanceof RangeQueryBuilder);

        // Verify the range query parameters
        RangeQueryBuilder rangeQuery = (RangeQueryBuilder) deleteQuery;
        assertEquals(expiresFieldName, rangeQuery.fieldName());
        assertEquals("now", rangeQuery.to());
        assertNull(rangeQuery.from());
    }

    @Test
    public void test_execute_multipleExecutions() {
        // Execute the job multiple times
        String result1 = purgeDocJob.execute();

        // Reset the flag
        deleteByQueryCalled = false;

        String result2 = purgeDocJob.execute();

        // Assert both executions were successful
        assertEquals("", result1);
        assertEquals("", result2);
        assertTrue(deleteByQueryCalled);
    }

    @Test
    public void test_execute_withEmptyIndexName() {
        // Configure empty index name
        documentUpdateIndex = "";
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return documentUpdateIndex;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert deleteByQuery was called with the configured empty index
        assertTrue(deleteByQueryCalled);
        assertEquals("", deleteIndex);

        // Assert result is empty when successful
        assertEquals("", result);
    }

    @Test
    public void test_execute_withEmptyFieldName() {
        // Configure empty field name
        expiresFieldName = "";

        // Re-register FessConfig with updated field name
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return documentUpdateIndex;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // Create new instance after registering the updated config
        purgeDocJob = new PurgeDocJob();

        // Execute the job - should handle empty field name gracefully
        try {
            String result = purgeDocJob.execute();
            // The job should handle the exception internally but since QueryBuilders.rangeQuery
            // throws immediately with empty field name, we need to catch it here
            fail("Expected an exception for empty field name");
        } catch (IllegalArgumentException e) {
            // Expected exception for empty field name
            assertTrue(e.getMessage().contains("field name is null or empty"));
        }

        // Assert deleteByQuery was NOT called due to exception in query building
        assertFalse(deleteByQueryCalled);
    }

    @Test
    public void test_execute_withOutOfMemoryError() {
        // Create mock SearchEngineClient that throws OutOfMemoryError
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new OutOfMemoryError("Out of memory");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // OutOfMemoryError is an Error, not Exception, so it will not be caught
        // The test should expect the error to propagate
        try {
            purgeDocJob.execute();
            fail("Expected OutOfMemoryError to be thrown");
        } catch (OutOfMemoryError e) {
            assertEquals("Out of memory", e.getMessage());
        }
    }

    @Test
    public void test_execute_verifyQueryToString() {
        // Execute the job
        purgeDocJob.execute();

        // Verify the query can be converted to string (for logging)
        assertNotNull(deleteQuery);
        String queryString = deleteQuery.toString();
        assertNotNull(queryString);
        assertTrue(queryString.contains("expires"));
    }

    @Test
    public void test_execute_withIOException() {
        // Create mock SearchEngineClient that throws IOException wrapped in RuntimeException
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new RuntimeException("IO error occurred during delete operation");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert error message is in the result
        assertTrue(result.contains("IO error occurred during delete operation"));
    }

    @Test
    public void test_execute_withTimeoutException() {
        // Create mock SearchEngineClient that throws timeout exception
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new RuntimeException("Operation timed out after 30 seconds");
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert error message is in the result
        assertTrue(result.contains("Operation timed out after 30 seconds"));
    }

    @Test
    public void test_execute_verifyComponentUtilCalls() {
        // Execute the job
        purgeDocJob.execute();

        // Verify that ComponentUtil was used to get components
        assertTrue(deleteByQueryCalled);
        assertEquals(documentUpdateIndex, deleteIndex);
        assertNotNull(deleteQuery);
    }

    @Test
    public void test_execute_withLongExceptionMessage() {
        // Create a very long exception message
        StringBuilder longMessage = new StringBuilder("Error: ");
        for (int i = 0; i < 100; i++) {
            longMessage.append("This is a very long error message part ").append(i).append(". ");
        }
        final String errorMessage = longMessage.toString();

        // Create mock SearchEngineClient that throws exception with long message
        searchEngineClient = new SearchEngineClient() {
            @Override
            public long deleteByQuery(String index, QueryBuilder query) {
                throw new RuntimeException(errorMessage);
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert the full error message is in the result
        assertTrue(result.contains(errorMessage));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withSpecialCharactersInConfig() {
        // Configure field and index names with special characters
        expiresFieldName = "expires-field.with@special#chars";
        documentUpdateIndex = "fess.update-index$special%chars";

        // Re-register FessConfig with updated values
        fessConfig = new TestFessConfig() {
            @Override
            public String getIndexFieldExpires() {
                return expiresFieldName;
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return documentUpdateIndex;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        // Create new instance after registering the updated config
        purgeDocJob = new PurgeDocJob();

        // Execute the job
        String result = purgeDocJob.execute();

        // Assert deleteByQuery was called with special characters
        assertTrue(deleteByQueryCalled);
        assertEquals("fess.update-index$special%chars", deleteIndex);

        // Assert field name with special characters was used
        assertNotNull(deleteQuery);
        assertTrue(deleteQuery instanceof RangeQueryBuilder);
        RangeQueryBuilder rangeQuery = (RangeQueryBuilder) deleteQuery;
        assertEquals("expires-field.with@special#chars", rangeQuery.fieldName());

        // Assert result is empty when successful
        assertEquals("", result);
    }

    // Test configuration class extending FessConfig.SimpleImpl
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
    }
}