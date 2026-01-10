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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.validation.UriTypeValidator.ProtocolType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class UriTypeTest extends UnitFessTestCase {

    // Test field with UriType annotation for WEB protocol
    @UriType(protocolType = ProtocolType.WEB)
    private String testFieldWeb;

    // Test field with UriType annotation for FILE protocol
    @UriType(protocolType = ProtocolType.FILE)
    private String testFieldFile;

    // Test field with custom message
    @UriType(protocolType = ProtocolType.WEB, message = "Custom URI validation error")
    private String testFieldWithMessage;

    // Test field with groups
    @UriType(protocolType = ProtocolType.WEB, groups = { TestGroup.class })
    private String testFieldWithGroups;

    // Test field with payload
    @UriType(protocolType = ProtocolType.WEB, payload = { TestPayload.class })
    private String testFieldWithPayload;

    // Test field with multiple groups
    @UriType(protocolType = ProtocolType.FILE, groups = { TestGroup.class, AnotherTestGroup.class })
    private String testFieldWithMultipleGroups;

    // Test field with multiple payloads
    @UriType(protocolType = ProtocolType.FILE, payload = { TestPayload.class, AnotherTestPayload.class })
    private String testFieldWithMultiplePayloads;

    // Test method with UriType annotation
    @UriType(protocolType = ProtocolType.WEB)
    public String testMethod() {
        return "http://example.com";
    }

    // Test parameter with UriType annotation
    public void testMethodWithParameter(@UriType(protocolType = ProtocolType.FILE) final String uri) {
        // Method for testing parameter annotation
    }

    // Test groups for validation
    private interface TestGroup {
    }

    private interface AnotherTestGroup {
    }

    // Test payload for validation
    private static class TestPayload implements Payload {
    }

    private static class AnotherTestPayload implements Payload {
    }

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    // Test annotation presence and structure
    @Test
    public void test_annotationPresence() {
        assertTrue("UriType should be an annotation", UriType.class.isAnnotation());
    }

    // Test annotation retention policy
    @Test
    public void test_retentionPolicy() {
        final Retention retention = UriType.class.getAnnotation(Retention.class);
        assertNotNull(retention, "Retention annotation should be present");
        assertEquals("Retention should be RUNTIME", RetentionPolicy.RUNTIME, retention.value());
    }

    // Test annotation target elements
    @Test
    public void test_targetElements() {
        final Target target = UriType.class.getAnnotation(Target.class);
        assertNotNull(target, "Target annotation should be present");

        final Set<ElementType> expectedTargets = new HashSet<>(Arrays.asList(METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER));
        final Set<ElementType> actualTargets = new HashSet<>(Arrays.asList(target.value()));

        assertEquals("Target elements should match", expectedTargets, actualTargets);
    }

    // Test annotation is documented
    @Test
    public void test_documented() {
        final Documented documented = UriType.class.getAnnotation(Documented.class);
        assertNotNull(documented, "Documented annotation should be present");
    }

    // Test Constraint annotation
    @Test
    public void test_constraintAnnotation() {
        final Constraint constraint = UriType.class.getAnnotation(Constraint.class);
        assertNotNull(constraint, "Constraint annotation should be present");
        assertEquals("Validator class should be UriTypeValidator", UriTypeValidator.class, constraint.validatedBy()[0]);
        assertEquals("Should have exactly one validator", 1, constraint.validatedBy().length);
    }

    // Test default values
    @Test
    public void test_defaultValues() throws Exception {
        assertEquals("Default message should match", "{org.lastaflute.validator.constraints.UriType.message}", getDefaultMessage());
        assertEquals("Default groups should be empty", 0, getDefaultGroups().length);
        assertEquals("Default payload should be empty", 0, getDefaultPayload().length);
    }

    // Test annotation on field with WEB protocol
    @Test
    public void test_annotationOnFieldWithWebProtocol() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWeb");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("protocolType should be WEB", ProtocolType.WEB, annotation.protocolType());
        assertEquals("message should be default", "{org.lastaflute.validator.constraints.UriType.message}", annotation.message());
        assertEquals("groups should be empty", 0, annotation.groups().length);
        assertEquals("payload should be empty", 0, annotation.payload().length);
    }

    // Test annotation on field with FILE protocol
    @Test
    public void test_annotationOnFieldWithFileProtocol() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldFile");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("protocolType should be FILE", ProtocolType.FILE, annotation.protocolType());
        assertEquals("message should be default", "{org.lastaflute.validator.constraints.UriType.message}", annotation.message());
    }

    // Test annotation with custom message
    @Test
    public void test_annotationWithCustomMessage() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWithMessage");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Custom message should match", "Custom URI validation error", annotation.message());
        assertEquals("protocolType should be WEB", ProtocolType.WEB, annotation.protocolType());
    }

    // Test annotation with groups
    @Test
    public void test_annotationWithGroups() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWithGroups");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Groups length should be 1", 1, annotation.groups().length);
        assertEquals("Group class should match", TestGroup.class, annotation.groups()[0]);
    }

    // Test annotation with payload
    @Test
    public void test_annotationWithPayload() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWithPayload");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Payload length should be 1", 1, annotation.payload().length);
        assertEquals("Payload class should match", TestPayload.class, annotation.payload()[0]);
    }

    // Test annotation with multiple groups
    @Test
    public void test_annotationWithMultipleGroups() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWithMultipleGroups");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Groups length should be 2", 2, annotation.groups().length);
        assertEquals("First group should be TestGroup", TestGroup.class, annotation.groups()[0]);
        assertEquals("Second group should be AnotherTestGroup", AnotherTestGroup.class, annotation.groups()[1]);
    }

    // Test annotation with multiple payloads
    @Test
    public void test_annotationWithMultiplePayloads() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWithMultiplePayloads");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on field");
        assertEquals("Payload length should be 2", 2, annotation.payload().length);
        assertEquals("First payload should be TestPayload", TestPayload.class, annotation.payload()[0]);
        assertEquals("Second payload should be AnotherTestPayload", AnotherTestPayload.class, annotation.payload()[1]);
    }

    // Test annotation on method
    @Test
    public void test_annotationOnMethod() throws Exception {
        final Method method = UriTypeTest.class.getMethod("testMethod");
        final UriType annotation = method.getAnnotation(UriType.class);

        assertNotNull(annotation, "Annotation should be present on method");
        assertEquals("protocolType should be WEB", ProtocolType.WEB, annotation.protocolType());
        assertEquals("message should be default", "{org.lastaflute.validator.constraints.UriType.message}", annotation.message());
    }

    // Test annotation on parameter
    @Test
    public void test_annotationOnParameter() throws Exception {
        final Method method = UriTypeTest.class.getMethod("testMethodWithParameter", String.class);
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();

        assertNotNull(paramAnnotations, "Parameter annotations should exist");
        assertEquals("Should have one parameter", 1, paramAnnotations.length);
        assertEquals("Parameter should have one annotation", 1, paramAnnotations[0].length);

        final UriType annotation = (UriType) paramAnnotations[0][0];
        assertNotNull(annotation, "UriType annotation should be present on parameter");
        assertEquals("protocolType should be FILE", ProtocolType.FILE, annotation.protocolType());
    }

    // Test annotation type
    @Test
    public void test_annotationType() throws Exception {
        final Field field = UriTypeTest.class.getDeclaredField("testFieldWeb");
        final UriType annotation = field.getAnnotation(UriType.class);

        assertEquals("Annotation type should be UriType", UriType.class, annotation.annotationType());
    }

    // Test all annotation methods exist
    @Test
    public void test_annotationMethods() {
        try {
            final Method protocolTypeMethod = UriType.class.getMethod("protocolType");
            assertNotNull(protocolTypeMethod, "protocolType() method should exist");
            assertEquals("protocolType() return type should be ProtocolType", ProtocolType.class, protocolTypeMethod.getReturnType());

            final Method messageMethod = UriType.class.getMethod("message");
            assertNotNull(messageMethod, "message() method should exist");
            assertEquals("message() return type should be String", String.class, messageMethod.getReturnType());

            final Method groupsMethod = UriType.class.getMethod("groups");
            assertNotNull(groupsMethod, "groups() method should exist");
            assertEquals("groups() return type should be Class[]", Class[].class, groupsMethod.getReturnType());

            final Method payloadMethod = UriType.class.getMethod("payload");
            assertNotNull(payloadMethod, "payload() method should exist");
            assertEquals("payload() return type should be Class[]", Class[].class, payloadMethod.getReturnType());
        } catch (final NoSuchMethodException e) {
            fail("Required annotation method not found: " + e.getMessage());
        }
    }

    // Test creating custom implementation of annotation
    @Test
    public void test_customAnnotationImplementation() {
        final UriType customAnnotation = new UriType() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UriType.class;
            }

            @Override
            public ProtocolType protocolType() {
                return ProtocolType.WEB;
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
        };

        assertEquals("Custom protocolType should be WEB", ProtocolType.WEB, customAnnotation.protocolType());
        assertEquals("Custom message should match", "Test message", customAnnotation.message());
        assertEquals("Custom groups should have one element", 1, customAnnotation.groups().length);
        assertEquals("Custom payload should have one element", 1, customAnnotation.payload().length);
        assertEquals("Annotation type should be UriType", UriType.class, customAnnotation.annotationType());
    }

    // Test annotation with FILE protocol type
    @Test
    public void test_annotationWithFileProtocolType() {
        final UriType fileAnnotation = new UriType() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UriType.class;
            }

            @Override
            public ProtocolType protocolType() {
                return ProtocolType.FILE;
            }

            @Override
            public String message() {
                return "{org.lastaflute.validator.constraints.UriType.message}";
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };

        assertEquals("Protocol type should be FILE", ProtocolType.FILE, fileAnnotation.protocolType());
        assertEquals("Message should be default", "{org.lastaflute.validator.constraints.UriType.message}", fileAnnotation.message());
        assertEquals("Groups should be empty", 0, fileAnnotation.groups().length);
        assertEquals("Payload should be empty", 0, fileAnnotation.payload().length);
    }

    // Test annotation with multiple groups and payloads
    @Test
    public void test_annotationWithMultipleGroupsAndPayloads() {
        final UriType multiAnnotation = new UriType() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return UriType.class;
            }

            @Override
            public ProtocolType protocolType() {
                return ProtocolType.WEB;
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
        };

        assertEquals("Protocol type should be WEB", ProtocolType.WEB, multiAnnotation.protocolType());
        assertEquals("Should have 2 groups", 2, multiAnnotation.groups().length);
        assertEquals("First group should be TestGroup", TestGroup.class, multiAnnotation.groups()[0]);
        assertEquals("Second group should be AnotherTestGroup", AnotherTestGroup.class, multiAnnotation.groups()[1]);

        assertEquals("Should have 2 payloads", 2, multiAnnotation.payload().length);
        assertEquals("First payload should be TestPayload", TestPayload.class, multiAnnotation.payload()[0]);
        assertEquals("Second payload should be AnotherTestPayload", AnotherTestPayload.class, multiAnnotation.payload()[1]);
    }

    // Test ProtocolType enum values
    @Test
    public void test_protocolTypeEnumValues() {
        final ProtocolType[] values = ProtocolType.values();
        assertEquals("Should have 2 protocol types", 2, values.length);

        boolean hasWeb = false;
        boolean hasFile = false;
        for (final ProtocolType type : values) {
            if (type == ProtocolType.WEB) {
                hasWeb = true;
            } else if (type == ProtocolType.FILE) {
                hasFile = true;
            }
        }

        assertTrue("Should have WEB protocol type", hasWeb);
        assertTrue("Should have FILE protocol type", hasFile);
    }

    // Test ProtocolType valueOf
    @Test
    public void test_protocolTypeValueOf() {
        assertEquals("valueOf(\"WEB\") should return WEB", ProtocolType.WEB, ProtocolType.valueOf("WEB"));
        assertEquals("valueOf(\"FILE\") should return FILE", ProtocolType.FILE, ProtocolType.valueOf("FILE"));
    }

    // Test ProtocolType name
    @Test
    public void test_protocolTypeName() {
        assertEquals("WEB.name() should return \"WEB\"", "WEB", ProtocolType.WEB.name());
        assertEquals("FILE.name() should return \"FILE\"", "FILE", ProtocolType.FILE.name());
    }

    // Test ProtocolType ordinal
    @Test
    public void test_protocolTypeOrdinal() {
        assertEquals("WEB.ordinal() should be 0", 0, ProtocolType.WEB.ordinal());
        assertEquals("FILE.ordinal() should be 1", 1, ProtocolType.FILE.ordinal());
    }

    // Test protocolType is required
    @Test
    public void test_protocolTypeIsRequired() {
        try {
            final Method method = UriType.class.getMethod("protocolType");
            assertNotNull(method, "protocolType method should exist");
            assertNull(method.getDefaultValue(), "protocolType should not have default value");
        } catch (final NoSuchMethodException e) {
            fail("protocolType method should exist");
        }
    }

    // Test annotation hashCode and equals behavior
    @Test
    public void test_annotationHashCodeAndEquals() throws Exception {
        final Field field1 = UriTypeTest.class.getDeclaredField("testFieldWeb");
        final UriType annotation1 = field1.getAnnotation(UriType.class);

        final Field field2 = UriTypeTest.class.getDeclaredField("testFieldFile");
        final UriType annotation2 = field2.getAnnotation(UriType.class);

        assertNotNull(annotation1, "First annotation should exist");
        assertNotNull(annotation2, "Second annotation should exist");

        // Annotations with different protocol types should not be equal
        assertFalse("Annotations with different protocol types should not be equal", annotation1.equals(annotation2));

        // Test with same field
        final UriType annotation1Copy = field1.getAnnotation(UriType.class);
        assertEquals("Same annotation should be equal", annotation1, annotation1Copy);
        assertEquals("Same annotation should have same hashCode", annotation1.hashCode(), annotation1Copy.hashCode());
    }

    // Helper methods to get default values
    private String getDefaultMessage() {
        try {
            final Method method = UriType.class.getMethod("message");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? defaultValue.toString() : null;
        } catch (final Exception e) {
            return "{org.lastaflute.validator.constraints.UriType.message}";
        }
    }

    private Class<?>[] getDefaultGroups() {
        try {
            final Method method = UriType.class.getMethod("groups");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? (Class<?>[]) defaultValue : new Class<?>[0];
        } catch (final Exception e) {
            return new Class<?>[0];
        }
    }

    private Class<? extends Payload>[] getDefaultPayload() {
        try {
            final Method method = UriType.class.getMethod("payload");
            final Object defaultValue = method.getDefaultValue();
            return defaultValue != null ? (Class<? extends Payload>[]) defaultValue : new Class[0];
        } catch (final Exception e) {
            return new Class[0];
        }
    }
}