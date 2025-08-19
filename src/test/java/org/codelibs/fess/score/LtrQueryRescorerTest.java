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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.search.rescore.QueryRescorerBuilder;
import org.opensearch.search.rescore.RescorerBuilder;

public class LtrQueryRescorerTest extends UnitFessTestCase {

    private LtrQueryRescorer ltrQueryRescorer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ltrQueryRescorer = new LtrQueryRescorer();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_constructor() {
        // Test that constructor works properly
        assertNotNull(new LtrQueryRescorer());
    }

    public void test_evaluate_withBlankModelName() {
        // Test when model name is blank
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return "";
            }

            @Override
            public int getLtrWindowSize() {
                return 50;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNull(result);
    }

    public void test_evaluate_withNullModelName() {
        // Test when model name is null
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return null;
            }

            @Override
            public int getLtrWindowSize() {
                return 50;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNull(result);
    }

    public void test_evaluate_withWhitespaceModelName() {
        // Test when model name contains only whitespace
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return "   ";
            }

            @Override
            public int getLtrWindowSize() {
                return 50;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNull(result);
    }

    public void test_evaluate_withValidModelName() {
        // Test with valid model name
        final String testModelName = "test_model";
        final int testWindowSize = 100;

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return testWindowSize;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", 123);

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertTrue(result instanceof QueryRescorerBuilder);

        QueryRescorerBuilder queryRescorerBuilder = (QueryRescorerBuilder) result;
        assertEquals(testWindowSize, queryRescorerBuilder.windowSize().intValue());
    }

    public void test_evaluate_withEmptyParams() {
        // Test with empty parameters map
        final String testModelName = "test_model";
        final int testWindowSize = 50;

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return testWindowSize;
            }
        });

        Map<String, Object> params = Collections.emptyMap();

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertTrue(result instanceof QueryRescorerBuilder);

        QueryRescorerBuilder queryRescorerBuilder = (QueryRescorerBuilder) result;
        assertEquals(testWindowSize, queryRescorerBuilder.windowSize().intValue());
    }

    public void test_evaluate_withNullParams() {
        // Test with null parameters - should throw NPE due to OpenSearch StoredLtrQueryBuilder requirements
        final String testModelName = "test_model";
        final int testWindowSize = 75;

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return testWindowSize;
            }
        });

        // The StoredLtrQueryBuilder.params() method requires non-null params
        // so this should throw a NullPointerException
        try {
            ltrQueryRescorer.evaluate(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected behavior - StoredLtrQueryBuilder requires non-null params
            assertNotNull(e);
        }
    }

    public void test_evaluate_withDifferentWindowSizes() {
        // Test with different window sizes
        final String testModelName = "test_model";

        // Test with small window size
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return 10;
            }
        });

        Map<String, Object> params = new HashMap<>();
        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertEquals(10, ((QueryRescorerBuilder) result).windowSize().intValue());

        // Test with large window size
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return 1000;
            }
        });

        result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertEquals(1000, ((QueryRescorerBuilder) result).windowSize().intValue());
    }

    public void test_evaluate_withZeroWindowSize() {
        // Test with zero window size
        final String testModelName = "test_model";

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return 0;
            }
        });

        Map<String, Object> params = new HashMap<>();
        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertEquals(0, ((QueryRescorerBuilder) result).windowSize().intValue());
    }

    public void test_evaluate_withNegativeWindowSize() {
        // Test with negative window size
        final String testModelName = "test_model";

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return -1;
            }
        });

        Map<String, Object> params = new HashMap<>();
        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertEquals(-1, ((QueryRescorerBuilder) result).windowSize().intValue());
    }

    public void test_evaluate_withComplexParams() {
        // Test with complex parameter map including nested structures
        final String testModelName = "complex_model";
        final int testWindowSize = 200;

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return testModelName;
            }

            @Override
            public int getLtrWindowSize() {
                return testWindowSize;
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("string_param", "test_value");
        params.put("int_param", 42);
        params.put("double_param", 3.14);
        params.put("boolean_param", true);

        Map<String, String> nestedMap = new HashMap<>();
        nestedMap.put("nested_key", "nested_value");
        params.put("nested_param", nestedMap);

        RescorerBuilder<?> result = ltrQueryRescorer.evaluate(params);
        assertNotNull(result);
        assertTrue(result instanceof QueryRescorerBuilder);

        QueryRescorerBuilder queryRescorerBuilder = (QueryRescorerBuilder) result;
        assertEquals(testWindowSize, queryRescorerBuilder.windowSize().intValue());
    }

    public void test_evaluate_multipleCallsWithDifferentConfigs() {
        // Test multiple calls with different configurations
        Map<String, Object> params = new HashMap<>();
        params.put("param", "value");

        // First call with model name
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return "model1";
            }

            @Override
            public int getLtrWindowSize() {
                return 50;
            }
        });

        RescorerBuilder<?> result1 = ltrQueryRescorer.evaluate(params);
        assertNotNull(result1);

        // Second call without model name
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return "";
            }

            @Override
            public int getLtrWindowSize() {
                return 100;
            }
        });

        RescorerBuilder<?> result2 = ltrQueryRescorer.evaluate(params);
        assertNull(result2);

        // Third call with different model name
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLtrModelName() {
                return "model2";
            }

            @Override
            public int getLtrWindowSize() {
                return 150;
            }
        });

        RescorerBuilder<?> result3 = ltrQueryRescorer.evaluate(params);
        assertNotNull(result3);
        assertEquals(150, ((QueryRescorerBuilder) result3).windowSize().intValue());
    }
}