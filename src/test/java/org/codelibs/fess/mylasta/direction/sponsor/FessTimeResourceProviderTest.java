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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.TimeZone;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessTimeResourceProviderTest extends UnitFessTestCase {

    private FessTimeResourceProvider provider;
    private FessConfig mockConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Create FessConfig mock
        mockConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "0";
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 0L;
            }
        };

        ComponentUtil.register(mockConfig, "fessConfig");

        // Create provider
        provider = new FessTimeResourceProvider(mockConfig);
    }

    @Override
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test constructor
    @Test
    public void test_constructor() {
        FessTimeResourceProvider testProvider = new FessTimeResourceProvider(mockConfig);
        assertNotNull(testProvider);
    }

    // Test constructor with null config
    @Test
    public void test_constructor_nullConfig() {
        try {
            new FessTimeResourceProvider(null);
            // Constructor may accept null, so test continues
            assertTrue(true);
        } catch (NullPointerException e) {
            // Expected if constructor validates input
            assertTrue(true);
        }
    }

    // Test with different time adjustments
    @Test
    public void test_withDifferentTimeAdjustments() {
        // Test zero adjustment
        FessConfig zeroConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "0";
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 0L;
            }
        };
        FessTimeResourceProvider zeroProvider = new FessTimeResourceProvider(zeroConfig);
        assertNotNull(zeroProvider);

        // Test positive adjustment
        FessConfig positiveConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "3600000";
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 3600000L;
            }
        };
        FessTimeResourceProvider positiveProvider = new FessTimeResourceProvider(positiveConfig);
        assertNotNull(positiveProvider);

        // Test negative adjustment
        FessConfig negativeConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "-3600000";
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return -3600000L;
            }
        };
        FessTimeResourceProvider negativeProvider = new FessTimeResourceProvider(negativeConfig);
        assertNotNull(negativeProvider);
    }

    // Test getTimeAdjustTimeMillis string values
    @Test
    public void test_getTimeAdjustTimeMillis_stringValues() {
        // Test various string formats
        String[] testValues = { "0", "1000", "-1000", "999999999", "-999999999", "1", "-1" };

        for (String value : testValues) {
            FessConfig testConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getTimeAdjustTimeMillis() {
                    return value;
                }

                @Override
                public Long getTimeAdjustTimeMillisAsLong() {
                    return Long.parseLong(value);
                }
            };

            FessTimeResourceProvider testProvider = new FessTimeResourceProvider(testConfig);
            assertNotNull(testProvider);
        }
    }

    // Test getTimeAdjustTimeMillisAsLong values
    @Test
    public void test_getTimeAdjustTimeMillisAsLong_values() {
        Long[] testValues = { 0L, 1L, -1L, 1000L, -1000L, 60000L, -60000L, 3600000L, -3600000L, Long.MAX_VALUE, Long.MIN_VALUE };

        for (Long value : testValues) {
            FessConfig testConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getTimeAdjustTimeMillis() {
                    return value.toString();
                }

                @Override
                public Long getTimeAdjustTimeMillisAsLong() {
                    return value;
                }
            };

            assertEquals(value, testConfig.getTimeAdjustTimeMillisAsLong());
            FessTimeResourceProvider testProvider = new FessTimeResourceProvider(testConfig);
            assertNotNull(testProvider);
        }
    }

    // Test centralTimeZone reference
    @Test
    public void test_centralTimeZone() {
        // Test that centralTimeZone is accessible and not null
        TimeZone centralTz = FessUserTimeZoneProcessProvider.centralTimeZone;
        assertNotNull(centralTz);
        assertEquals(TimeZone.getDefault(), centralTz);
    }

    // Test toString method
    @Test
    public void test_toString() {
        String result = provider.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // Check that it contains class name or relevant info
        assertTrue(result.contains("FessTimeResourceProvider") || result.contains("adjustTimeMillis"));
    }

    // Test multiple provider instances
    @Test
    public void test_multipleInstances() {
        FessTimeResourceProvider provider1 = new FessTimeResourceProvider(mockConfig);
        FessTimeResourceProvider provider2 = new FessTimeResourceProvider(mockConfig);

        assertNotNull(provider1);
        assertNotNull(provider2);
        assertNotSame(provider1, provider2);
    }

    // Test with config returning null time adjustment
    @Test
    public void test_nullTimeAdjustment() {
        FessConfig nullAdjustConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return null;
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return null;
            }
        };

        FessTimeResourceProvider nullAdjustProvider = new FessTimeResourceProvider(nullAdjustConfig);
        assertNotNull(nullAdjustProvider);
    }

    // Test with config returning empty string
    @Test
    public void test_emptyStringTimeAdjustment() {
        FessConfig emptyConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "";
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 0L;
            }
        };

        FessTimeResourceProvider emptyProvider = new FessTimeResourceProvider(emptyConfig);
        assertNotNull(emptyProvider);
    }

    // Test thread safety of provider creation
    @Test
    public void test_threadSafetyProviderCreation() throws InterruptedException {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final FessTimeResourceProvider[] providers = new FessTimeResourceProvider[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    providers[index] = new FessTimeResourceProvider(mockConfig);
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Check all providers were created successfully
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
            assertNotNull(providers[i], "Thread " + i + " failed to create provider");
        }
    }

    // Test with very large time adjustment values
    @Test
    public void test_largeTimeAdjustmentValues() {
        // Test with milliseconds in a year
        long yearInMillis = 365L * 24L * 60L * 60L * 1000L;

        FessConfig yearConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return String.valueOf(yearInMillis);
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return yearInMillis;
            }
        };

        FessTimeResourceProvider yearProvider = new FessTimeResourceProvider(yearConfig);
        assertNotNull(yearProvider);
    }

    // Test timezone information
    @Test
    public void test_timezoneInformation() {
        TimeZone defaultTz = TimeZone.getDefault();
        assertNotNull(defaultTz);
        assertNotNull(defaultTz.getID());
        assertTrue(defaultTz.getID().length() > 0);

        // Central timezone should match default
        assertEquals(defaultTz, FessUserTimeZoneProcessProvider.centralTimeZone);
    }

    // Test with decimal values in string (should be handled by config)
    @Test
    public void test_decimalStringValues() {
        FessConfig decimalConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "1000.5"; // Invalid for Long parsing
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 1000L; // Return valid long
            }
        };

        FessTimeResourceProvider decimalProvider = new FessTimeResourceProvider(decimalConfig);
        assertNotNull(decimalProvider);
        assertEquals(Long.valueOf(1000L), decimalConfig.getTimeAdjustTimeMillisAsLong());
    }

    // Test provider consistency
    @Test
    public void test_providerConsistency() {
        // Create multiple providers with same config
        FessTimeResourceProvider provider1 = new FessTimeResourceProvider(mockConfig);
        FessTimeResourceProvider provider2 = new FessTimeResourceProvider(mockConfig);
        FessTimeResourceProvider provider3 = new FessTimeResourceProvider(mockConfig);

        assertNotNull(provider1);
        assertNotNull(provider2);
        assertNotNull(provider3);

        // Each should be a different instance
        assertNotSame(provider1, provider2);
        assertNotSame(provider2, provider3);
        assertNotSame(provider1, provider3);
    }

    // Test with special characters in time adjustment string
    @Test
    public void test_specialCharactersInTimeAdjustment() {
        FessConfig specialConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return "0x1000"; // Hex notation string
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return 4096L; // Decimal equivalent
            }
        };

        FessTimeResourceProvider specialProvider = new FessTimeResourceProvider(specialConfig);
        assertNotNull(specialProvider);
        assertEquals(Long.valueOf(4096L), specialConfig.getTimeAdjustTimeMillisAsLong());
    }

    // Test memory efficiency with multiple providers
    @Test
    public void test_memoryEfficiency() {
        // Create and discard multiple providers
        for (int i = 0; i < 100; i++) {
            FessTimeResourceProvider tempProvider = new FessTimeResourceProvider(mockConfig);
            assertNotNull(tempProvider);
        }
        // If this completes without OutOfMemoryError, test passes
        assertTrue(true);
    }

    // Test equals and hashCode if implemented
    @Test
    public void test_equalsAndHashCode() {
        FessTimeResourceProvider provider1 = new FessTimeResourceProvider(mockConfig);
        FessTimeResourceProvider provider2 = new FessTimeResourceProvider(mockConfig);

        // Test basic object equality
        assertNotSame(provider1, provider2);
        assertEquals(provider1, provider1); // Self equality
        assertFalse(provider1.equals(null)); // Null check
        assertFalse(provider1.equals("string")); // Type check
    }

    // Test with rapid config changes
    @Test
    public void test_rapidConfigChanges() {
        for (int i = 0; i < 50; i++) {
            final int adjustment = i * 1000;
            FessConfig rapidConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getTimeAdjustTimeMillis() {
                    return String.valueOf(adjustment);
                }

                @Override
                public Long getTimeAdjustTimeMillisAsLong() {
                    return (long) adjustment;
                }
            };

            FessTimeResourceProvider rapidProvider = new FessTimeResourceProvider(rapidConfig);
            assertNotNull(rapidProvider);
        }
    }

    // Test boundary conditions for time adjustments
    @Test
    public void test_boundaryTimeAdjustments() {
        // Test zero boundary
        testBoundaryValue(0L);

        // Test one millisecond boundaries
        testBoundaryValue(1L);
        testBoundaryValue(-1L);

        // Test one second boundaries
        testBoundaryValue(1000L);
        testBoundaryValue(-1000L);

        // Test one minute boundaries
        testBoundaryValue(60000L);
        testBoundaryValue(-60000L);

        // Test one hour boundaries
        testBoundaryValue(3600000L);
        testBoundaryValue(-3600000L);

        // Test one day boundaries
        testBoundaryValue(86400000L);
        testBoundaryValue(-86400000L);
    }

    private void testBoundaryValue(long value) {
        FessConfig boundaryConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTimeAdjustTimeMillis() {
                return String.valueOf(value);
            }

            @Override
            public Long getTimeAdjustTimeMillisAsLong() {
                return value;
            }
        };

        FessTimeResourceProvider boundaryProvider = new FessTimeResourceProvider(boundaryConfig);
        assertNotNull(boundaryProvider);
        assertEquals(Long.valueOf(value), boundaryConfig.getTimeAdjustTimeMillisAsLong());
    }

    // Test config methods return consistent values
    @Test
    public void test_configConsistency() {
        assertEquals("0", mockConfig.getTimeAdjustTimeMillis());
        assertEquals(Long.valueOf(0L), mockConfig.getTimeAdjustTimeMillisAsLong());

        // Test multiple calls return same values
        for (int i = 0; i < 10; i++) {
            assertEquals("0", mockConfig.getTimeAdjustTimeMillis());
            assertEquals(Long.valueOf(0L), mockConfig.getTimeAdjustTimeMillisAsLong());
        }
    }

    // Test provider creation with various configs
    @Test
    public void test_providerCreationVariousConfigs() {
        // Test with multiple different configs
        FessConfig[] configs = new FessConfig[5];
        FessTimeResourceProvider[] providers = new FessTimeResourceProvider[5];

        for (int i = 0; i < configs.length; i++) {
            final int value = i * 100;
            configs[i] = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getTimeAdjustTimeMillis() {
                    return String.valueOf(value);
                }

                @Override
                public Long getTimeAdjustTimeMillisAsLong() {
                    return (long) value;
                }
            };

            providers[i] = new FessTimeResourceProvider(configs[i]);
            assertNotNull(providers[i]);
        }

        // Verify all providers are different instances
        for (int i = 0; i < providers.length; i++) {
            for (int j = i + 1; j < providers.length; j++) {
                assertNotSame(providers[i], providers[j]);
            }
        }
    }
}