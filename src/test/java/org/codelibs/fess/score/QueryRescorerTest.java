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
package org.codelibs.fess.score;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.search.rescore.QueryRescorerBuilder;
import org.opensearch.search.rescore.RescorerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class QueryRescorerTest extends UnitFessTestCase {

    private QueryRescorer queryRescorer;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    public void test_evaluate_withNullParams() {
        // Test implementation that returns null
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                return null;
            }
        };

        assertNull(queryRescorer.evaluate(null));
    }

    @Test
    public void test_evaluate_withEmptyParams() {
        // Test implementation that returns null for empty params
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params == null || params.isEmpty()) {
                    return null;
                }
                return createMockRescorerBuilder();
            }
        };

        assertNull(queryRescorer.evaluate(Collections.emptyMap()));
        assertNull(queryRescorer.evaluate(new HashMap<>()));
    }

    @Test
    public void test_evaluate_withParams() {
        // Test implementation that returns a RescorerBuilder
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params != null && params.containsKey("test")) {
                    return createMockRescorerBuilder();
                }
                return null;
            }
        };

        Map<String, Object> params = new HashMap<>();
        params.put("test", "value");

        RescorerBuilder<?> result = queryRescorer.evaluate(params);
        assertNotNull(result);
    }

    @Test
    public void test_evaluate_multipleImplementations() {
        // Test with first implementation
        QueryRescorer rescorer1 = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                return createMockRescorerBuilder();
            }
        };

        // Test with second implementation
        QueryRescorer rescorer2 = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                return null;
            }
        };

        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");

        assertNotNull(rescorer1.evaluate(params));
        assertNull(rescorer2.evaluate(params));
    }

    @Test
    public void test_evaluate_withDifferentParamTypes() {
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params == null) {
                    return null;
                }

                // Test different parameter types
                if (params.containsKey("string") && params.get("string") instanceof String) {
                    return createMockRescorerBuilder();
                }
                if (params.containsKey("number") && params.get("number") instanceof Number) {
                    return createMockRescorerBuilder();
                }
                if (params.containsKey("boolean") && params.get("boolean") instanceof Boolean) {
                    return createMockRescorerBuilder();
                }

                return null;
            }
        };

        // Test with string parameter
        Map<String, Object> stringParams = new HashMap<>();
        stringParams.put("string", "test");
        assertNotNull(queryRescorer.evaluate(stringParams));

        // Test with number parameter
        Map<String, Object> numberParams = new HashMap<>();
        numberParams.put("number", 123);
        assertNotNull(queryRescorer.evaluate(numberParams));

        // Test with boolean parameter
        Map<String, Object> booleanParams = new HashMap<>();
        booleanParams.put("boolean", true);
        assertNotNull(queryRescorer.evaluate(booleanParams));

        // Test with unknown parameter
        Map<String, Object> unknownParams = new HashMap<>();
        unknownParams.put("unknown", new Object());
        assertNull(queryRescorer.evaluate(unknownParams));
    }

    @Test
    public void test_evaluate_withNestedParams() {
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params == null) {
                    return null;
                }

                // Check for nested map
                if (params.containsKey("nested") && params.get("nested") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedMap = (Map<String, Object>) params.get("nested");
                    if (nestedMap.containsKey("innerKey")) {
                        return createMockRescorerBuilder();
                    }
                }

                return null;
            }
        };

        // Test with nested parameters
        Map<String, Object> nestedParams = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("innerKey", "innerValue");
        nestedParams.put("nested", innerMap);

        assertNotNull(queryRescorer.evaluate(nestedParams));

        // Test without nested parameters
        Map<String, Object> flatParams = new HashMap<>();
        flatParams.put("flat", "value");
        assertNull(queryRescorer.evaluate(flatParams));
    }

    @Test
    public void test_evaluate_withLargeParams() {
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params != null && params.size() > 100) {
                    return createMockRescorerBuilder();
                }
                return null;
            }
        };

        // Test with large number of parameters
        Map<String, Object> largeParams = new HashMap<>();
        for (int i = 0; i < 150; i++) {
            largeParams.put("key" + i, "value" + i);
        }

        assertNotNull(queryRescorer.evaluate(largeParams));

        // Test with small number of parameters
        Map<String, Object> smallParams = new HashMap<>();
        smallParams.put("key", "value");
        assertNull(queryRescorer.evaluate(smallParams));
    }

    @Test
    public void test_evaluate_exceptionHandling() {
        queryRescorer = new QueryRescorer() {
            @Override
            public RescorerBuilder<?> evaluate(Map<String, Object> params) {
                if (params != null && params.containsKey("throwException")) {
                    throw new RuntimeException("Test exception");
                }
                return createMockRescorerBuilder();
            }
        };

        // Test normal case
        Map<String, Object> normalParams = new HashMap<>();
        normalParams.put("normal", "value");
        assertNotNull(queryRescorer.evaluate(normalParams));

        // Test exception case
        Map<String, Object> exceptionParams = new HashMap<>();
        exceptionParams.put("throwException", true);

        try {
            queryRescorer.evaluate(exceptionParams);
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }
    }

    @Test
    public void test_evaluate_concurrency() {
        queryRescorer = new QueryRescorer() {
            private int callCount = 0;

            @Override
            public synchronized RescorerBuilder<?> evaluate(Map<String, Object> params) {
                callCount++;
                if (callCount % 2 == 0) {
                    return createMockRescorerBuilder();
                }
                return null;
            }
        };

        // Test multiple calls
        Map<String, Object> params = new HashMap<>();
        params.put("test", "value");

        assertNull(queryRescorer.evaluate(params)); // First call - odd
        assertNotNull(queryRescorer.evaluate(params)); // Second call - even
        assertNull(queryRescorer.evaluate(params)); // Third call - odd
        assertNotNull(queryRescorer.evaluate(params)); // Fourth call - even
    }

    /**
     * Creates a mock RescorerBuilder for testing purposes.
     * Uses QueryRescorerBuilder with a simple query.
     */
    private RescorerBuilder<?> createMockRescorerBuilder() {
        return new QueryRescorerBuilder(new MatchAllQueryBuilder());
    }
}