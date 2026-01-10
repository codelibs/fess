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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessMailDeliveryDepartmentCreatorTest extends UnitFessTestCase {

    private FessMailDeliveryDepartmentCreator creator;
    private FessConfig originalFessConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        originalFessConfig = ComponentUtil.getFessConfig();

        // Set up test configuration
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMailFromName() {
                return "Test Admin";
            }

            @Override
            public String getMailFromAddress() {
                return "test@example.com";
            }

            @Override
            public String getMailHostname() {
                return "localhost";
            }
        });

        creator = new FessMailDeliveryDepartmentCreator(ComponentUtil.getFessConfig());
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        if (originalFessConfig != null) {
            ComponentUtil.setFessConfig(originalFessConfig);
        }
        super.tearDown();
    }

    // Test creator initialization
    @Test
    public void test_creatorInitialization() {
        assertNotNull(creator);
    }

    // Test with different mail configurations
    @Test
    public void test_mailConfiguration() {
        FessConfig config = ComponentUtil.getFessConfig();

        assertEquals("Test Admin", config.getMailFromName());
        assertEquals("test@example.com", config.getMailFromAddress());
        assertEquals("localhost", config.getMailHostname());
    }

    // Test with different from names
    @Test
    public void test_differentFromNames() {
        String[] fromNames = { "Administrator", "Support Team", "No Reply", "Test User" };

        for (String name : fromNames) {
            ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getMailFromName() {
                    return name;
                }
            });

            assertEquals(name, ComponentUtil.getFessConfig().getMailFromName());
        }
    }

    // Test with different from addresses
    @Test
    public void test_differentFromAddresses() {
        String[] fromAddresses = { "noreply@example.com", "admin@test.org", "support@company.co.jp", "test.user@subdomain.example.com" };

        for (String from : fromAddresses) {
            ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getMailFromAddress() {
                    return from;
                }
            });

            assertEquals(from, ComponentUtil.getFessConfig().getMailFromAddress());
        }
    }

    // Test with different hostnames
    @Test
    public void test_differentHostnames() {
        String[] hostnames = { "smtp.gmail.com", "mail.example.com", "localhost", "192.168.1.1" };

        for (String hostname : hostnames) {
            ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getMailHostname() {
                    return hostname;
                }
            });

            assertEquals(hostname, ComponentUtil.getFessConfig().getMailHostname());
        }
    }

    // Test with null values
    @Test
    public void test_nullValues() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMailFromName() {
                return null;
            }

            @Override
            public String getMailFromAddress() {
                return null;
            }

            @Override
            public String getMailHostname() {
                return null;
            }
        });

        FessConfig config = ComponentUtil.getFessConfig();
        assertNull(config.getMailFromName());
        assertNull(config.getMailFromAddress());
        assertNull(config.getMailHostname());
    }

    // Test creator instance creation with valid config
    @Test
    public void test_multipleCreatorInstances() {
        FessConfig config = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;
        };

        FessMailDeliveryDepartmentCreator creator1 = new FessMailDeliveryDepartmentCreator(config);
        FessMailDeliveryDepartmentCreator creator2 = new FessMailDeliveryDepartmentCreator(config);

        assertNotNull(creator1);
        assertNotNull(creator2);
        assertNotSame(creator1, creator2);
    }

    // Test with empty strings
    @Test
    public void test_emptyStrings() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getMailFromName() {
                return "";
            }

            @Override
            public String getMailFromAddress() {
                return "";
            }

            @Override
            public String getMailHostname() {
                return "";
            }
        });

        FessConfig config = ComponentUtil.getFessConfig();
        assertEquals("", config.getMailFromName());
        assertEquals("", config.getMailFromAddress());
        assertEquals("", config.getMailHostname());
    }
}