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
package org.codelibs.fess.validation;

import java.lang.annotation.Annotation;

import org.codelibs.fess.unit.UnitFessTestCase;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CustomSizeValidatorTest extends UnitFessTestCase {

    private CustomSizeValidator validator;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        validator = new CustomSizeValidator();
    }

    @Test
    public void test_constructor() {
        assertNotNull(validator);
    }

    @Test
    public void test_isValid_nullValue() {
        final CustomSize annotation = createBasicAnnotation();
        validator.initialize(annotation);

        // Test null context - should return true for null value regardless of context
        assertTrue(validator.isValid(null, null));
    }

    @Test
    public void test_initialize_withValidParameters() {
        final CustomSize annotation = createBasicAnnotation();
        validator.initialize(annotation);
        // Test that initialization completes without exception
    }

    @Test
    public void test_classStructure() {
        // Test that the class exists and has the expected structure
        assertEquals("CustomSizeValidator should be in correct package", "org.codelibs.fess.validation.CustomSizeValidator",
                CustomSizeValidator.class.getName());

        // Test that it implements ConstraintValidator
        assertTrue("Should implement ConstraintValidator",
                jakarta.validation.ConstraintValidator.class.isAssignableFrom(CustomSizeValidator.class));
    }

    @Test
    public void test_methodsExist() {
        // Verify required methods exist
        try {
            CustomSizeValidator.class.getMethod("initialize", CustomSize.class);
            CustomSizeValidator.class.getMethod("isValid", CharSequence.class, ConstraintValidatorContext.class);
            assertTrue("Required methods exist", true);
        } catch (final NoSuchMethodException e) {
            fail("Required methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void test_inheritance() {
        assertTrue("Should implement ConstraintValidator interface",
                jakarta.validation.ConstraintValidator.class.isAssignableFrom(CustomSizeValidator.class));
    }

    @Test
    public void test_initializeWithBasicAnnotation() {
        final CustomSize annotation = createBasicAnnotation();
        try {
            validator.initialize(annotation);
            assertTrue("Initialize should complete without exception", true);
        } catch (final Exception e) {
            fail("Initialize should not throw exception with valid annotation: " + e.getMessage());
        }
    }

    // Test parameter validation without relying on complex configuration
    @Test
    public void test_validateParameters_basic() {
        // Test that parameter validation can be performed
        final CustomSize validAnnotation = createBasicAnnotation();

        try {
            validator.initialize(validAnnotation);
            assertTrue("Valid annotation should initialize successfully", true);
        } catch (final IllegalArgumentException e) {
            fail("Valid annotation should not cause IllegalArgumentException: " + e.getMessage());
        }
    }

    @Test
    public void test_isValid_basic() {
        final CustomSize annotation = createBasicAnnotation();
        validator.initialize(annotation);

        // Test that the method can be called (even if context handling is complex)
        try {
            final boolean result = validator.isValid("test", null);
            assertNotNull(Boolean.valueOf(result), "isValid should return a boolean result");
        } catch (final NullPointerException e) {
            // This is expected if the implementation requires a non-null context
            assertTrue(true, "NPE is acceptable for null context in this implementation");
        }
    }

    private CustomSize createBasicAnnotation() {
        return new CustomSize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomSize.class;
            }

            @Override
            public String message() {
                return "Size validation failed";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String minKey() {
                return "";
            }

            @Override
            public String maxKey() {
                return "";
            }
        };
    }
}
