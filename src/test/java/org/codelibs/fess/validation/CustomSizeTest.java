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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.unit.UnitFessTestCase;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CustomSizeTest extends UnitFessTestCase {

    // Test field with CustomSize annotation for testing
    @CustomSize(minKey = "test.min", maxKey = "test.max")
    private String testField;

    // Test field with only minKey
    @CustomSize(minKey = "test.min.only")
    private String testFieldMinOnly;

    // Test field with only maxKey
    @CustomSize(maxKey = "test.max.only")
    private String testFieldMaxOnly;

    // Test field with custom message
    @CustomSize(minKey = "test.min", maxKey = "test.max", message = "Custom error message")
    private String testFieldWithMessage;

    // Test field with groups
    @CustomSize(groups = { TestGroup.class })
    private String testFieldWithGroups;

    // Test field with payload
    @CustomSize(payload = { TestPayload.class })
    private String testFieldWithPayload;

    // Test method with CustomSize annotation
    @CustomSize(minKey = "method.min", maxKey = "method.max")
    public String testMethod() {
        return "test";
    }

    // Test groups for validation
    private interface TestGroup {
    }

    // Test payload for validation
    private static class TestPayload implements Payload {
    }

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    // Test annotation presence and structure
    @Test
    public void test_annotationPresence() {
        assertTrue("CustomSize should be an annotation", CustomSize.class.isAnnotation());
    }

    // Test annotation retention policy
    @Test
    public void test_retentionPolicy() {
        final Retention retention = CustomSize.class.getAnnotation(Retention.class);
        assertNotNull(retention, "Retention annotation should be present");
        assertEquals("Retention should be RUNTIME", RetentionPolicy.RUNTIME, retention.value());
    }

    // Test annotation target elements
    @Test
    public void test_targetElements() {
        final Target target = CustomSize.class.getAnnotation(Target.class);
        assertNotNull(target, "Target annotation should be present");

        final Set<ElementType> expectedTargets = new HashSet<>(Arrays.asList(METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER));
        final Set<ElementType> actualTargets = new HashSet<>(Arrays.asList(target.value()));

        assertEquals("Target elements should match", expectedTargets, actualTargets);
    }

    // Test annotation is documented
    @Test
    public void test_documented() {
        final Documented documented = CustomSize.class.getAnnotation(Documented.class);
        assertNotNull(documented, "Documented annotation should be present");
    }

    // Test Constraint annotation
    @Test
    public void test_constraintAnnotation() {
        final Constraint constraint = CustomSize.class.getAnnotation(Constraint.class);
        assertNotNull(constraint, "Constraint annotation should be present");
        assertEquals("Validator class should be CustomSizeValidator", CustomSizeValidator.class, constraint.validatedBy()[0]);
    }

    // Test default values
    @Test
    public void test_defaultValues() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testField");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Default message should match", "{jakarta.validation.constraints.Size.message}", getDefaultMessage());
        assertEquals("Default groups should be empty", 0, getDefaultGroups().length);
        assertEquals("Default payload should be empty", 0, getDefaultPayload().length);
        assertEquals("Default minKey should be empty", StringUtil.EMPTY, getDefaultMinKey());
        assertEquals("Default maxKey should be empty", StringUtil.EMPTY, getDefaultMaxKey());
    }

    // Test annotation on field
    @Test
    public void test_annotationOnField() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testField");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("minKey should match", "test.min", annotation.minKey());
        assertEquals("maxKey should match", "test.max", annotation.maxKey());
        assertEquals("message should be default", "{jakarta.validation.constraints.Size.message}", annotation.message());
    }

    // Test annotation with only minKey
    @Test
    public void test_annotationWithOnlyMinKey() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testFieldMinOnly");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("minKey should match", "test.min.only", annotation.minKey());
        assertEquals("maxKey should be empty", "", annotation.maxKey());
    }

    // Test annotation with only maxKey
    @Test
    public void test_annotationWithOnlyMaxKey() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testFieldMaxOnly");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("minKey should be empty", "", annotation.minKey());
        assertEquals("maxKey should match", "test.max.only", annotation.maxKey());
    }

    // Test annotation with custom message
    @Test
    public void test_annotationWithCustomMessage() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testFieldWithMessage");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Custom message should match", "Custom error message", annotation.message());
    }

    // Test annotation with groups
    @Test
    public void test_annotationWithGroups() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testFieldWithGroups");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Groups length should be 1", 1, annotation.groups().length);
        assertEquals("Group class should match", TestGroup.class, annotation.groups()[0]);
    }

    // Test annotation with payload
    @Test
    public void test_annotationWithPayload() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testFieldWithPayload");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Payload length should be 1", 1, annotation.payload().length);
        assertEquals("Payload class should match", TestPayload.class, annotation.payload()[0]);
    }

    // Test annotation on method
    @Test
    public void test_annotationOnMethod() throws Exception {
        final Method method = CustomSizeTest.class.getMethod("testMethod");
        final CustomSize annotation = method.getAnnotation(CustomSize.class);

        assertNotNull(annotation, "Annotation should be present on method");
        assertEquals("minKey should match", "method.min", annotation.minKey());
        assertEquals("maxKey should match", "method.max", annotation.maxKey());
    }

    // Test annotation type
    @Test
    public void test_annotationType() throws Exception {
        final Field field = CustomSizeTest.class.getDeclaredField("testField");
        final CustomSize annotation = field.getAnnotation(CustomSize.class);

        assertEquals("Annotation type should be CustomSize", CustomSize.class, annotation.annotationType());
    }

    // Test all annotation methods exist
    @Test
    public void test_annotationMethods() {
        try {
            final Method messageMethod = CustomSize.class.getMethod("message");
            assertNotNull(messageMethod, "message() method should exist");
            assertEquals("message() return type should be String", String.class, messageMethod.getReturnType());

            final Method groupsMethod = CustomSize.class.getMethod("groups");
            assertNotNull(groupsMethod, "groups() method should exist");
            assertEquals("groups() return type should be Class[]", Class[].class, groupsMethod.getReturnType());

            final Method payloadMethod = CustomSize.class.getMethod("payload");
            assertNotNull(payloadMethod, "payload() method should exist");
            assertEquals("payload() return type should be Class[]", Class[].class, payloadMethod.getReturnType());

            final Method minKeyMethod = CustomSize.class.getMethod("minKey");
            assertNotNull(minKeyMethod, "minKey() method should exist");
            assertEquals("minKey() return type should be String", String.class, minKeyMethod.getReturnType());

            final Method maxKeyMethod = CustomSize.class.getMethod("maxKey");
            assertNotNull(maxKeyMethod, "maxKey() method should exist");
            assertEquals("maxKey() return type should be String", String.class, maxKeyMethod.getReturnType());
        } catch (final NoSuchMethodException e) {
            fail("Required annotation method not found: " + e.getMessage());
        }
    }

    // Test creating custom implementation of annotation
    @Test
    public void test_customAnnotationImplementation() {
        final CustomSize customAnnotation = new CustomSize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomSize.class;
            }

            @Override
            public String message() {
                return "Test message";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[] { TestGroup.class };
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[] { TestPayload.class };
            }

            @Override
            public String minKey() {
                return "custom.min";
            }

            @Override
            public String maxKey() {
                return "custom.max";
            }
        };

        assertEquals("Custom message should match", "Test message", customAnnotation.message());
        assertEquals("Custom groups should have one element", 1, customAnnotation.groups().length);
        assertEquals("Custom payload should have one element", 1, customAnnotation.payload().length);
        assertEquals("Custom minKey should match", "custom.min", customAnnotation.minKey());
        assertEquals("Custom maxKey should match", "custom.max", customAnnotation.maxKey());
        assertEquals("Annotation type should be CustomSize", CustomSize.class, customAnnotation.annotationType());
    }

    // Test annotation with empty keys
    @Test
    public void test_annotationWithEmptyKeys() {
        final CustomSize emptyKeysAnnotation = new CustomSize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomSize.class;
            }

            @Override
            public String message() {
                return "{jakarta.validation.constraints.Size.message}";
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

        assertEquals("Empty minKey should be empty string", "", emptyKeysAnnotation.minKey());
        assertEquals("Empty maxKey should be empty string", "", emptyKeysAnnotation.maxKey());
        assertTrue("Empty minKey should match StringUtil.EMPTY", StringUtil.EMPTY.equals(emptyKeysAnnotation.minKey()));
        assertTrue("Empty maxKey should match StringUtil.EMPTY", StringUtil.EMPTY.equals(emptyKeysAnnotation.maxKey()));
    }

    // Test annotation with multiple groups and payloads
    @Test
    public void test_annotationWithMultipleGroupsAndPayloads() {
        final CustomSize multiAnnotation = new CustomSize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomSize.class;
            }

            @Override
            public String message() {
                return "Multi test";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[] { TestGroup.class, AnotherTestGroup.class };
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[] { TestPayload.class, AnotherTestPayload.class };
            }

            @Override
            public String minKey() {
                return "multi.min";
            }

            @Override
            public String maxKey() {
                return "multi.max";
            }
        };

        assertEquals("Should have 2 groups", 2, multiAnnotation.groups().length);
        assertEquals("First group should be TestGroup", TestGroup.class, multiAnnotation.groups()[0]);
        assertEquals("Second group should be AnotherTestGroup", AnotherTestGroup.class, multiAnnotation.groups()[1]);

        assertEquals("Should have 2 payloads", 2, multiAnnotation.payload().length);
        assertEquals("First payload should be TestPayload", TestPayload.class, multiAnnotation.payload()[0]);
        assertEquals("Second payload should be AnotherTestPayload", AnotherTestPayload.class, multiAnnotation.payload()[1]);
    }

    // Additional test interfaces for multiple groups/payloads test
    private interface AnotherTestGroup {
    }

    private static class AnotherTestPayload implements Payload {
    }

    // Helper methods to get default values
    private String getDefaultMessage() {
        try {
            final Method method = CustomSize.class.getMethod("message");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? defaultValue.toString() : null;
        } catch (final Exception e) {
            return "{jakarta.validation.constraints.Size.message}";
        }
    }

    private Class<?>[] getDefaultGroups() {
        try {
            final Method method = CustomSize.class.getMethod("groups");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? (Class<?>[]) defaultValue : new Class<?>[0];
        } catch (final Exception e) {
            return new Class<?>[0];
        }
    }

    private Class<? extends Payload>[] getDefaultPayload() {
        try {
            final Method method = CustomSize.class.getMethod("payload");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? (Class<? extends Payload>[]) defaultValue : new Class[0];
        } catch (final Exception e) {
            return new Class[0];
        }
    }

    private String getDefaultMinKey() {
        try {
            final Method method = CustomSize.class.getMethod("minKey");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? defaultValue.toString() : StringUtil.EMPTY;
        } catch (final Exception e) {
            return StringUtil.EMPTY;
        }
    }

    private String getDefaultMaxKey() {
        try {
            final Method method = CustomSize.class.getMethod("maxKey");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? defaultValue.toString() : StringUtil.EMPTY;
        } catch (final Exception e) {
            return StringUtil.EMPTY;
        }
    }
}