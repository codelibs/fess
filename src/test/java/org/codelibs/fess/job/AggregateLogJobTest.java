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

import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AggregateLogJobTest extends UnitFessTestCase {

    private AggregateLogJob aggregateLogJob;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        aggregateLogJob = new AggregateLogJob();
    }

    @Test
    public void test_constructor() {
        // Test default constructor
        AggregateLogJob job = new AggregateLogJob();
        assertNotNull(job);
    }

    @Test
    public void test_execute_success() {
        // Setup mock SearchLogHelper that succeeds
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                // Successful execution - no exception thrown
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result is empty string on success
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    public void test_execute_withException() {
        // Setup mock SearchLogHelper that throws exception
        final String errorMessage = "Test exception occurred";
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new RuntimeException(errorMessage);
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result contains error message
        assertNotNull(result);
        assertTrue(result.contains(errorMessage));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withNullPointerException() {
        // Setup mock SearchLogHelper that throws NullPointerException
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new NullPointerException("Null value encountered");
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result contains error message
        assertNotNull(result);
        assertTrue(result.contains("Null value encountered"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withCustomException() {
        // Setup mock SearchLogHelper that throws custom exception
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new IllegalStateException("Illegal state in search log processing");
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result contains error message
        assertNotNull(result);
        assertTrue(result.contains("Illegal state in search log processing"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_multipleErrors() {
        // Test with multiple calls to execute() with errors
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            private int callCount = 0;

            @Override
            public void storeSearchLog() {
                callCount++;
                throw new RuntimeException("Error " + callCount);
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // First execution
        String result1 = aggregateLogJob.execute();
        assertNotNull(result1);
        assertTrue(result1.contains("Error 1"));

        // Second execution
        String result2 = aggregateLogJob.execute();
        assertNotNull(result2);
        assertTrue(result2.contains("Error 2"));

        // Results should be independent
        assertFalse(result1.equals(result2));
    }

    @Test
    public void test_execute_withEmptyStringException() {
        // Setup mock SearchLogHelper that throws exception with empty message
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new RuntimeException("");
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result ends with newline even with empty message
        assertNotNull(result);
        assertEquals("\n", result);
    }

    @Test
    public void test_execute_withNullMessageException() {
        // Setup mock SearchLogHelper that throws exception with null message
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new RuntimeException((String) null);
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result handles null message properly
        assertNotNull(result);
        assertTrue(result.contains("null"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withNestedExceptionCause() {
        // Setup mock SearchLogHelper that throws nested exception
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                Exception cause = new IllegalArgumentException("Root cause");
                throw new RuntimeException("Wrapper exception", cause);
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result contains the wrapper exception message
        assertNotNull(result);
        assertTrue(result.contains("Wrapper exception"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_concurrentCalls() {
        // Setup mock SearchLogHelper for concurrent testing
        final Object lock = new Object();
        final int[] callCount = { 0 };

        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                synchronized (lock) {
                    callCount[0]++;
                    if (callCount[0] % 2 == 0) {
                        throw new RuntimeException("Even call error");
                    }
                    // Odd calls succeed
                }
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // First call (odd) should succeed
        String result1 = aggregateLogJob.execute();
        assertEquals("", result1);

        // Second call (even) should fail
        String result2 = aggregateLogJob.execute();
        assertTrue(result2.contains("Even call error"));

        // Third call (odd) should succeed
        String result3 = aggregateLogJob.execute();
        assertEquals("", result3);
    }

    @Test
    public void test_execute_performanceWithLargeErrorMessage() {
        // Test with very large error message
        final StringBuilder largeMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeMessage.append("Error detail ").append(i).append(" ");
        }

        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new RuntimeException(largeMessage.toString());
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify large message is handled
        assertNotNull(result);
        assertTrue(result.length() > 1000);
        assertTrue(result.contains("Error detail 0"));
        assertTrue(result.contains("Error detail 999"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withCheckedExceptionWrapped() {
        // Setup mock SearchLogHelper that throws wrapped checked exception
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                try {
                    throw new Exception("Checked exception");
                } catch (Exception e) {
                    throw new RuntimeException("Wrapped: " + e.getMessage(), e);
                }
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job
        String result = aggregateLogJob.execute();

        // Verify result contains wrapped message
        assertNotNull(result);
        assertTrue(result.contains("Wrapped: Checked exception"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_withOutOfMemoryError() {
        // Test handling of Error (not Exception) - should NOT be caught by Exception handler
        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                throw new OutOfMemoryError("Simulated OOM");
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // Execute the job - Error should NOT be caught since implementation only catches Exception
        try {
            aggregateLogJob.execute();
            fail("OutOfMemoryError should not be caught by Exception handler");
        } catch (OutOfMemoryError e) {
            // Expected - OutOfMemoryError is not caught by Exception handler
            assertEquals("Simulated OOM", e.getMessage());
        }
    }

    @Test
    public void test_execute_multipleDifferentExceptions() {
        // Test job behavior with different exception types in sequence
        final int[] callCount = { 0 };

        SearchLogHelper mockSearchLogHelper = new SearchLogHelper() {
            @Override
            public void storeSearchLog() {
                callCount[0]++;
                switch (callCount[0]) {
                case 1:
                    // Success on first call
                    return;
                case 2:
                    throw new IllegalArgumentException("Invalid argument");
                case 3:
                    throw new IllegalStateException("Invalid state");
                case 4:
                    throw new NullPointerException("Null pointer");
                default:
                    throw new RuntimeException("Generic error");
                }
            }
        };
        ComponentUtil.register(mockSearchLogHelper, "searchLogHelper");

        // First execution - success
        assertEquals("", aggregateLogJob.execute());

        // Second execution - IllegalArgumentException
        assertTrue(aggregateLogJob.execute().contains("Invalid argument"));

        // Third execution - IllegalStateException
        assertTrue(aggregateLogJob.execute().contains("Invalid state"));

        // Fourth execution - NullPointerException
        assertTrue(aggregateLogJob.execute().contains("Null pointer"));

        // Fifth execution - RuntimeException
        assertTrue(aggregateLogJob.execute().contains("Generic error"));
    }
}