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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.codelibs.fess.unit.UnitFessTestCase;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CronExpressionTest extends UnitFessTestCase {

    private Validator validator;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Test annotation attributes and defaults
    @Test
    public void test_annotationAttributes() throws Exception {
        Field field = TestBean.class.getDeclaredField("cronExpression");
        CronExpression annotation = field.getAnnotation(CronExpression.class);

        assertNotNull(annotation);
        assertEquals("{org.lastaflute.validator.constraints.CronExpression.message}", annotation.message());
        assertEquals(0, annotation.groups().length);
        assertEquals(0, annotation.payload().length);
    }

    // Test annotation target elements
    @Test
    public void test_annotationTargetElements() {
        CronExpression.class.isAnnotation();
        assertTrue(CronExpression.class.isAnnotation());

        // Check that annotation can be applied to METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER
        java.lang.annotation.Target target = CronExpression.class.getAnnotation(java.lang.annotation.Target.class);
        assertNotNull(target);

        java.lang.annotation.ElementType[] types = target.value();
        assertEquals(5, types.length);
    }

    // Test annotation retention policy
    @Test
    public void test_annotationRetention() {
        java.lang.annotation.Retention retention = CronExpression.class.getAnnotation(java.lang.annotation.Retention.class);
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());
    }

    // Test annotation is documented
    @Test
    public void test_annotationDocumented() {
        java.lang.annotation.Documented documented = CronExpression.class.getAnnotation(java.lang.annotation.Documented.class);
        assertNotNull(documented);
    }

    // Test annotation has Constraint meta-annotation
    @Test
    public void test_annotationConstraint() {
        jakarta.validation.Constraint constraint = CronExpression.class.getAnnotation(jakarta.validation.Constraint.class);
        assertNotNull(constraint);
        Class<?>[] validators = constraint.validatedBy();
        assertEquals(1, validators.length);
        assertEquals(CronExpressionValidator.class, validators[0]);
    }

    // Test validation with valid cron expressions
    @Test
    public void test_validCronExpressions() {
        TestBean bean = new TestBean();

        // Test null value (should be valid)
        bean.setCronExpression(null);
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertTrue(violations.isEmpty());

        // Test empty string (should be valid)
        bean.setCronExpression("");
        violations = validator.validate(bean);
        assertTrue(violations.isEmpty());

        // Test blank string (should be valid)
        bean.setCronExpression("   ");
        violations = validator.validate(bean);
        assertTrue(violations.isEmpty());
    }

    // Test validation with invalid cron expressions
    @Test
    public void test_invalidCronExpressions() {
        TestBean bean = new TestBean();

        // Test invalid cron expression
        bean.setCronExpression("invalid");
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        // Test too many fields
        bean.setCronExpression("* * * * * * * *");
        violations = validator.validate(bean);
        assertFalse(violations.isEmpty());

        // Test too few fields
        bean.setCronExpression("* * *");
        violations = validator.validate(bean);
        assertFalse(violations.isEmpty());
    }

    // Test validation with groups
    @Test
    public void test_validationGroups() {
        GroupTestBean bean = new GroupTestBean();
        bean.setDefaultCron("invalid");
        bean.setSpecialCron("invalid");

        // Validate default group
        Set<ConstraintViolation<GroupTestBean>> violations = validator.validate(bean, Default.class);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().contains("defaultCron"));

        // Validate special group
        violations = validator.validate(bean, SpecialGroup.class);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().contains("specialCron"));

        // Validate all groups
        violations = validator.validate(bean);
        assertEquals(1, violations.size()); // Only default group is validated
    }

    // Test custom message
    @Test
    public void test_customMessage() {
        CustomMessageBean bean = new CustomMessageBean();
        bean.setCronWithCustomMessage("invalid");

        Set<ConstraintViolation<CustomMessageBean>> violations = validator.validate(bean);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<CustomMessageBean> violation = violations.iterator().next();
        assertEquals("Invalid cron expression format", violation.getMessage());
    }

    // Test annotation on method parameter
    @Test
    public void test_annotationOnMethod() throws Exception {
        Method method = TestService.class.getDeclaredMethod("scheduledTask", String.class);
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        assertEquals(1, paramAnnotations.length);
        boolean hasCronExpression = false;
        for (Annotation annotation : paramAnnotations[0]) {
            if (annotation instanceof CronExpression) {
                hasCronExpression = true;
                break;
            }
        }
        assertTrue(hasCronExpression);
    }

    // Test annotation on constructor parameter
    @Test
    public void test_annotationOnConstructor() throws Exception {
        java.lang.reflect.Constructor<?> constructor = TestConstructor.class.getDeclaredConstructor(String.class);
        Annotation[][] paramAnnotations = constructor.getParameterAnnotations();

        assertEquals(1, paramAnnotations.length);
        boolean hasCronExpression = false;
        for (Annotation annotation : paramAnnotations[0]) {
            if (annotation instanceof CronExpression) {
                hasCronExpression = true;
                break;
            }
        }
        assertTrue(hasCronExpression);
    }

    // Test class hierarchy validation
    @Test
    public void test_inheritanceValidation() {
        ExtendedTestBean bean = new ExtendedTestBean();
        bean.setCronExpression("invalid");
        bean.setAdditionalCron("also invalid");

        Set<ConstraintViolation<ExtendedTestBean>> violations = validator.validate(bean);
        assertEquals(2, violations.size());
    }

    // Test multiple annotations on same field
    @Test
    public void test_multipleValidations() {
        MultiValidationBean bean = new MultiValidationBean();
        bean.setRequiredCron(null); // Violates NotNull

        Set<ConstraintViolation<MultiValidationBean>> violations = validator.validate(bean);
        assertEquals(1, violations.size()); // Only NotNull violation

        bean.setRequiredCron("invalid"); // Now violates CronExpression
        violations = validator.validate(bean);
        assertEquals(1, violations.size()); // Only CronExpression violation

        bean.setRequiredCron(""); // Empty string passes NotNull but should pass CronExpression too
        violations = validator.validate(bean);
        assertTrue(violations.isEmpty());
    }

    // Test annotation composition
    @Test
    public void test_composedAnnotation() {
        ComposedBean bean = new ComposedBean();
        bean.setComposedCron("invalid");

        Set<ConstraintViolation<ComposedBean>> violations = validator.validate(bean);
        assertFalse(violations.isEmpty());
    }

    // Inner test classes for validation testing

    private static class TestBean {
        @CronExpression
        private String cronExpression;

        public String getCronExpression() {
            return cronExpression;
        }

        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }
    }

    private interface SpecialGroup {
    }

    private static class GroupTestBean {
        @CronExpression(groups = Default.class)
        private String defaultCron;

        @CronExpression(groups = SpecialGroup.class)
        private String specialCron;

        public String getDefaultCron() {
            return defaultCron;
        }

        public void setDefaultCron(String defaultCron) {
            this.defaultCron = defaultCron;
        }

        public String getSpecialCron() {
            return specialCron;
        }

        public void setSpecialCron(String specialCron) {
            this.specialCron = specialCron;
        }
    }

    private static class CustomMessageBean {
        @CronExpression(message = "Invalid cron expression format")
        private String cronWithCustomMessage;

        public String getCronWithCustomMessage() {
            return cronWithCustomMessage;
        }

        public void setCronWithCustomMessage(String cronWithCustomMessage) {
            this.cronWithCustomMessage = cronWithCustomMessage;
        }
    }

    private static class TestService {
        public void scheduledTask(@CronExpression String schedule) {
            // Method for testing parameter annotation
        }
    }

    private static class TestConstructor {
        private final String schedule;

        public TestConstructor(@CronExpression String schedule) {
            this.schedule = schedule;
        }

        public String getSchedule() {
            return schedule;
        }
    }

    private static class ExtendedTestBean extends TestBean {
        @CronExpression
        private String additionalCron;

        public String getAdditionalCron() {
            return additionalCron;
        }

        public void setAdditionalCron(String additionalCron) {
            this.additionalCron = additionalCron;
        }
    }

    private static class MultiValidationBean {
        @jakarta.validation.constraints.NotNull
        @CronExpression
        private String requiredCron;

        public String getRequiredCron() {
            return requiredCron;
        }

        public void setRequiredCron(String requiredCron) {
            this.requiredCron = requiredCron;
        }
    }

    @CronExpression
    @jakarta.validation.constraints.NotBlank
    @java.lang.annotation.Target({ java.lang.annotation.ElementType.FIELD })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @jakarta.validation.Constraint(validatedBy = {})
    private @interface ValidCronRequired {
        String message() default "Must be a valid non-blank cron expression";

        Class<?>[] groups() default {};

        Class<? extends jakarta.validation.Payload>[] payload() default {};
    }

    private static class ComposedBean {
        @ValidCronRequired
        private String composedCron;

        public String getComposedCron() {
            return composedCron;
        }

        public void setComposedCron(String composedCron) {
            this.composedCron = composedCron;
        }
    }
}