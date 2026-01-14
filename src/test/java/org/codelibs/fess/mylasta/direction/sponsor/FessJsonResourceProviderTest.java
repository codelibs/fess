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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.core.json.JsonMappingOption;
import org.lastaflute.core.json.JsonMappingOption.JsonFieldNaming;
import org.dbflute.optional.OptionalThing;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessJsonResourceProviderTest extends UnitFessTestCase {

    private FessJsonResourceProvider provider;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        provider = new FessJsonResourceProvider();
    }

    @Test
    public void test_isNullsSuppressed() {
        // Test that nulls are suppressed
        assertTrue(provider.isNullsSuppressed());
    }

    @Test
    public void test_isPrettyPrintSuppressed() {
        // Test that pretty print is not suppressed
        assertFalse(provider.isPrettyPrintSuppressed());
    }

    @Test
    public void test_provideMappingOption() {
        // Test that mapping option is provided correctly
        JsonMappingOption option = provider.provideMappingOption();
        assertNotNull(option);

        // Verify the field naming is set to CAMEL_TO_LOWER_SNAKE
        OptionalThing<JsonFieldNaming> fieldNamingOpt = option.getFieldNaming();
        assertTrue("Field naming should be present", fieldNamingOpt.isPresent());
        assertEquals(JsonFieldNaming.CAMEL_TO_LOWER_SNAKE, fieldNamingOpt.get());
    }

    @Test
    public void test_provideMappingOption_notNull() {
        // Test that mapping option is never null
        JsonMappingOption option1 = provider.provideMappingOption();
        JsonMappingOption option2 = provider.provideMappingOption();

        assertNotNull(option1);
        assertNotNull(option2);

        // Each call should return a new instance
        assertNotSame(option1, option2);
    }

    @Test
    public void test_provideMappingOption_consistency() {
        // Test that multiple calls provide consistent configuration
        JsonMappingOption option1 = provider.provideMappingOption();
        JsonMappingOption option2 = provider.provideMappingOption();

        assertEquals(option1.getFieldNaming(), option2.getFieldNaming());
    }

    @Test
    public void test_allMethodsReturnExpectedValues() {
        // Comprehensive test to verify all methods return expected values
        FessJsonResourceProvider testProvider = new FessJsonResourceProvider();

        // Verify nulls suppression is enabled
        assertTrue("Nulls should be suppressed", testProvider.isNullsSuppressed());

        // Verify pretty print is enabled
        assertFalse("Pretty print should not be suppressed", testProvider.isPrettyPrintSuppressed());

        // Verify mapping option configuration
        JsonMappingOption mappingOption = testProvider.provideMappingOption();
        assertNotNull(mappingOption, "Mapping option should not be null");
        OptionalThing<JsonFieldNaming> fieldNamingOpt = mappingOption.getFieldNaming();
        assertTrue("Field naming should be present", fieldNamingOpt.isPresent());
        assertEquals("Field naming should be CAMEL_TO_LOWER_SNAKE", JsonFieldNaming.CAMEL_TO_LOWER_SNAKE, fieldNamingOpt.get());
    }

    @Test
    public void test_instanceCreation() {
        // Test that multiple instances can be created independently
        FessJsonResourceProvider provider1 = new FessJsonResourceProvider();
        FessJsonResourceProvider provider2 = new FessJsonResourceProvider();

        assertNotSame(provider1, provider2);

        // Both instances should have the same behavior
        assertEquals(provider1.isNullsSuppressed(), provider2.isNullsSuppressed());
        assertEquals(provider1.isPrettyPrintSuppressed(), provider2.isPrettyPrintSuppressed());
        assertEquals(provider1.provideMappingOption().getFieldNaming().get(), provider2.provideMappingOption().getFieldNaming().get());
    }

    @Test
    public void test_booleanMethodsReturnConsistentValues() {
        // Test that boolean methods return consistent values across multiple calls
        boolean firstNullsSuppressed = provider.isNullsSuppressed();
        boolean secondNullsSuppressed = provider.isNullsSuppressed();
        boolean thirdNullsSuppressed = provider.isNullsSuppressed();

        assertEquals(firstNullsSuppressed, secondNullsSuppressed);
        assertEquals(secondNullsSuppressed, thirdNullsSuppressed);
        assertTrue(firstNullsSuppressed);

        boolean firstPrettyPrintSuppressed = provider.isPrettyPrintSuppressed();
        boolean secondPrettyPrintSuppressed = provider.isPrettyPrintSuppressed();
        boolean thirdPrettyPrintSuppressed = provider.isPrettyPrintSuppressed();

        assertEquals(firstPrettyPrintSuppressed, secondPrettyPrintSuppressed);
        assertEquals(secondPrettyPrintSuppressed, thirdPrettyPrintSuppressed);
        assertFalse(firstPrettyPrintSuppressed);
    }

    @Test
    public void test_provideMappingOption_fieldNamingConfiguration() {
        // Test the specific field naming configuration
        JsonMappingOption option = provider.provideMappingOption();

        // The option should be configured with CAMEL_TO_LOWER_SNAKE
        // Field naming is now provided as OptionalThing
        OptionalThing<JsonFieldNaming> fieldNamingOpt = option.getFieldNaming();
        assertTrue("Field naming should be present", fieldNamingOpt.isPresent());
        JsonFieldNaming fieldNaming = fieldNamingOpt.get();
        assertNotNull(fieldNaming, "Field naming should not be null");
        assertEquals("Field naming should be CAMEL_TO_LOWER_SNAKE", JsonFieldNaming.CAMEL_TO_LOWER_SNAKE, fieldNaming);

        // Verify the naming convention
        assertEquals(JsonFieldNaming.CAMEL_TO_LOWER_SNAKE.name(), fieldNaming.name());
    }

    @Test
    public void test_provideMappingOption_newInstanceEachTime() {
        // Test that provideMappingOption creates a new instance each time
        JsonMappingOption option1 = provider.provideMappingOption();
        JsonMappingOption option2 = provider.provideMappingOption();
        JsonMappingOption option3 = provider.provideMappingOption();

        // All should be different instances
        assertNotSame(option1, option2);
        assertNotSame(option2, option3);
        assertNotSame(option1, option3);

        // But all should have the same configuration
        assertEquals(option1.getFieldNaming().get(), option2.getFieldNaming().get());
        assertEquals(option2.getFieldNaming().get(), option3.getFieldNaming().get());
    }
}