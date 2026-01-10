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

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.path.ActionPathResolver;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessUserLocaleProcessProviderTest extends UnitFessTestCase {

    private FessUserLocaleProcessProvider provider;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        provider = new FessUserLocaleProcessProvider();
    }

    @Override
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test isAcceptCookieLocale method
    @Test
    public void test_isAcceptCookieLocale() {
        // Always returns false as per implementation
        assertFalse(provider.isAcceptCookieLocale());
    }

    // Test findBusinessLocale with valid locale parameter
    @Test
    public void test_findBusinessLocale_withValidLocale() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("en_US");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(Locale.US, result.get());
    }

    // Test findBusinessLocale with Japanese locale
    @Test
    public void test_findBusinessLocale_withJapaneseLocale() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("ja_JP");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(Locale.JAPAN, result.get());
    }

    // Test findBusinessLocale with language only (no country)
    @Test
    public void test_findBusinessLocale_withLanguageOnly() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("fr");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(Locale.FRENCH, result.get());
    }

    // Test findBusinessLocale with hyphen separator
    @Test
    public void test_findBusinessLocale_withHyphenSeparator() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("en-GB");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(Locale.UK, result.get());
    }

    // Test findBusinessLocale with blank parameter name
    @Test
    public void test_findBusinessLocale_withBlankParameterName() {
        // Setup mock config with blank parameter name
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("en_US");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify - should return empty when parameter name is blank
        assertFalse(result.isPresent());
    }

    // Test findBusinessLocale with null parameter name
    @Test
    public void test_findBusinessLocale_withNullParameterName() {
        // Setup mock config with null parameter name
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return null;
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("en_US");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify - should return empty when parameter name is null
        assertFalse(result.isPresent());
    }

    // Test findBusinessLocale with no parameter value
    @Test
    public void test_findBusinessLocale_withNoParameterValue() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager with no value
        RequestManager mockRequestManager = createMockRequestManager(null);

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify - should return empty when no parameter value
        assertFalse(result.isPresent());
    }

    // Test findBusinessLocale with invalid locale format
    @Test
    public void test_findBusinessLocale_withInvalidLocaleFormat() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager with invalid format
        RequestManager mockRequestManager = createMockRequestManager("invalid_locale_format");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify - invalid format should return empty due to exception in LocaleUtils.toLocale
        assertFalse(result.isPresent());
    }

    // Test findBusinessLocale with language and country and variant
    @Test
    public void test_findBusinessLocale_withLanguageCountryVariant() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("no_NO_NY");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify
        assertTrue(result.isPresent());
        Locale locale = result.get();
        assertEquals("no", locale.getLanguage());
        assertEquals("NO", locale.getCountry());
        assertEquals("NY", locale.getVariant());
    }

    // Test findBusinessLocale with case sensitivity
    @Test
    public void test_findBusinessLocale_withMixedCase() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager with mixed case
        // LocaleUtils.toLocale is strict about case - it will throw exception for invalid format
        RequestManager mockRequestManager = createMockRequestManager("En_Us");

        // Execute
        OptionalThing<Locale> result = provider.findBusinessLocale(null, mockRequestManager);

        // Verify - invalid case format should return empty due to exception
        assertFalse(result.isPresent());
    }

    // Test multiple calls to ensure consistency
    @Test
    public void test_findBusinessLocale_multipleCallsConsistency() {
        // Setup mock config
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryBrowserLangParameterName() {
                return "lang";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        // Setup mock request manager
        RequestManager mockRequestManager = createMockRequestManager("de_DE");

        // Execute multiple times
        OptionalThing<Locale> result1 = provider.findBusinessLocale(null, mockRequestManager);
        OptionalThing<Locale> result2 = provider.findBusinessLocale(null, mockRequestManager);
        OptionalThing<Locale> result3 = provider.findBusinessLocale(null, mockRequestManager);

        // Verify all return same result
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertTrue(result3.isPresent());
        assertEquals(Locale.GERMANY, result1.get());
        assertEquals(Locale.GERMANY, result2.get());
        assertEquals(Locale.GERMANY, result3.get());
    }

    // Helper method to create mock RequestManager
    private RequestManager createMockRequestManager(final String paramValue) {
        return new org.lastaflute.web.servlet.request.SimpleRequestManager() {
            @Override
            public OptionalThing<String> getParameter(String name) {
                if (paramValue != null) {
                    return OptionalThing.of(paramValue);
                }
                return OptionalThing.empty();
            }
        };
    }
}