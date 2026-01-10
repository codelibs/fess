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
package org.codelibs.fess.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SecuredTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    // Test that Secured annotation has correct meta-annotations
    @Test
    public void test_annotationMetadata() {
        // Check if Secured annotation is present
        assertNotNull(Secured.class);

        // Check Target annotation
        Target target = Secured.class.getAnnotation(Target.class);
        assertNotNull(target);
        ElementType[] targetTypes = target.value();
        assertEquals(2, targetTypes.length);
        assertTrue(Arrays.asList(targetTypes).contains(ElementType.METHOD));
        assertTrue(Arrays.asList(targetTypes).contains(ElementType.TYPE));

        // Check Retention annotation
        Retention retention = Secured.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());

        // Check Inherited annotation
        assertNotNull(Secured.class.getAnnotation(Inherited.class));

        // Check Documented annotation
        assertNotNull(Secured.class.getAnnotation(Documented.class));
    }

    // Test annotation value method
    @Test
    public void test_valueMethod() throws NoSuchMethodException {
        Method valueMethod = Secured.class.getMethod("value");
        assertNotNull(valueMethod);
        assertEquals(String[].class, valueMethod.getReturnType());
        assertEquals(0, valueMethod.getParameterCount());
    }

    // Test class with single role
    @Secured({ "ROLE_USER" })
    static class SingleRoleClass {
        public void testMethod() {
        }
    }

    // Test class with multiple roles
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    static class MultipleRolesClass {
        public void testMethod() {
        }
    }

    // Test class with method annotations
    static class MethodAnnotatedClass {
        @Secured({ "ROLE_USER" })
        public void userMethod() {
        }

        @Secured({ "ROLE_ADMIN" })
        public void adminMethod() {
        }

        @Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_GUEST" })
        public void multiRoleMethod() {
        }

        public void nonAnnotatedMethod() {
        }
    }

    // Test class annotation with single role
    @Test
    public void test_classAnnotation_singleRole() {
        Secured secured = SingleRoleClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(1, roles.length);
        assertEquals("ROLE_USER", roles[0]);
    }

    // Test class annotation with multiple roles
    @Test
    public void test_classAnnotation_multipleRoles() {
        Secured secured = MultipleRolesClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(2, roles.length);
        assertEquals("ROLE_USER", roles[0]);
        assertEquals("ROLE_ADMIN", roles[1]);
    }

    // Test method annotation with single role
    @Test
    public void test_methodAnnotation_singleRole() throws NoSuchMethodException {
        Method method = MethodAnnotatedClass.class.getMethod("userMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(1, roles.length);
        assertEquals("ROLE_USER", roles[0]);
    }

    // Test method annotation with admin role
    @Test
    public void test_methodAnnotation_adminRole() throws NoSuchMethodException {
        Method method = MethodAnnotatedClass.class.getMethod("adminMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(1, roles.length);
        assertEquals("ROLE_ADMIN", roles[0]);
    }

    // Test method annotation with multiple roles
    @Test
    public void test_methodAnnotation_multipleRoles() throws NoSuchMethodException {
        Method method = MethodAnnotatedClass.class.getMethod("multiRoleMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(3, roles.length);
        assertEquals("ROLE_USER", roles[0]);
        assertEquals("ROLE_ADMIN", roles[1]);
        assertEquals("ROLE_GUEST", roles[2]);
    }

    // Test non-annotated method
    @Test
    public void test_methodWithoutAnnotation() throws NoSuchMethodException {
        Method method = MethodAnnotatedClass.class.getMethod("nonAnnotatedMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNull(secured);
    }

    // Test inheritance behavior
    @Secured({ "ROLE_PARENT" })
    static class ParentClass {
        @Secured({ "ROLE_PARENT_METHOD" })
        public void parentMethod() {
        }
    }

    static class ChildClass extends ParentClass {
        @Override
        public void parentMethod() {
        }

        @Secured({ "ROLE_CHILD_METHOD" })
        public void childMethod() {
        }
    }

    // Test that class annotation is inherited
    @Test
    public void test_inheritedClassAnnotation() {
        Secured parentSecured = ParentClass.class.getAnnotation(Secured.class);
        assertNotNull(parentSecured);
        assertEquals("ROLE_PARENT", parentSecured.value()[0]);

        // Child class should inherit the annotation
        Secured childSecured = ChildClass.class.getAnnotation(Secured.class);
        assertNotNull(childSecured);
        assertEquals("ROLE_PARENT", childSecured.value()[0]);
    }

    // Test method annotation inheritance
    @Test
    public void test_methodAnnotationInheritance() throws NoSuchMethodException {
        // Parent method has annotation
        Method parentMethod = ParentClass.class.getMethod("parentMethod");
        Secured parentSecured = parentMethod.getAnnotation(Secured.class);
        assertNotNull(parentSecured);
        assertEquals("ROLE_PARENT_METHOD", parentSecured.value()[0]);

        // Overridden method does not inherit annotation
        Method childMethod = ChildClass.class.getMethod("parentMethod");
        Secured childSecured = childMethod.getAnnotation(Secured.class);
        assertNull(childSecured);
    }

    // Test child's own method annotation
    @Test
    public void test_childMethodAnnotation() throws NoSuchMethodException {
        Method method = ChildClass.class.getMethod("childMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("ROLE_CHILD_METHOD", secured.value()[0]);
    }

    // Test with empty roles array
    @Secured({})
    static class EmptyRolesClass {
    }

    @Test
    public void test_emptyRolesArray() {
        Secured secured = EmptyRolesClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertNotNull(roles);
        assertEquals(0, roles.length);
    }

    // Test with special characters in roles
    @Secured({ "ROLE_USER-ADMIN", "ROLE_USER.VIEWER", "ROLE_USER_EDITOR", "ROLE$SPECIAL" })
    static class SpecialCharacterRolesClass {
    }

    @Test
    public void test_specialCharactersInRoles() {
        Secured secured = SpecialCharacterRolesClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(4, roles.length);
        assertEquals("ROLE_USER-ADMIN", roles[0]);
        assertEquals("ROLE_USER.VIEWER", roles[1]);
        assertEquals("ROLE_USER_EDITOR", roles[2]);
        assertEquals("ROLE$SPECIAL", roles[3]);
    }

    // Test both class and method annotations
    @Secured({ "CLASS_ROLE" })
    static class BothAnnotatedClass {
        @Secured({ "METHOD_ROLE" })
        public void annotatedMethod() {
        }
    }

    @Test
    public void test_classAndMethodAnnotations() throws NoSuchMethodException {
        // Check class annotation
        Secured classSecured = BothAnnotatedClass.class.getAnnotation(Secured.class);
        assertNotNull(classSecured);
        assertEquals("CLASS_ROLE", classSecured.value()[0]);

        // Check method annotation
        Method method = BothAnnotatedClass.class.getMethod("annotatedMethod");
        Secured methodSecured = method.getAnnotation(Secured.class);
        assertNotNull(methodSecured);
        assertEquals("METHOD_ROLE", methodSecured.value()[0]);
    }

    // Test annotation on interface
    @Secured({ "INTERFACE_ROLE" })
    interface SecuredInterface {
        @Secured({ "INTERFACE_METHOD_ROLE" })
        void securedMethod();
    }

    @Test
    public void test_interfaceAnnotation() {
        Secured secured = SecuredInterface.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("INTERFACE_ROLE", secured.value()[0]);
    }

    @Test
    public void test_interfaceMethodAnnotation() throws NoSuchMethodException {
        Method method = SecuredInterface.class.getMethod("securedMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("INTERFACE_METHOD_ROLE", secured.value()[0]);
    }

    // Test implementation of secured interface
    static class SecuredInterfaceImpl implements SecuredInterface {
        @Override
        public void securedMethod() {
        }
    }

    @Test
    public void test_interfaceImplementation() {
        // Implementation class inherits interface's class-level annotation
        Secured secured = SecuredInterfaceImpl.class.getAnnotation(Secured.class);
        assertNull(secured); // Interface annotations are not inherited by implementing classes
    }

    // Test annotation array immutability
    @Test
    public void test_annotationValueImmutability() {
        Secured secured = MultipleRolesClass.class.getAnnotation(Secured.class);
        String[] roles1 = secured.value();
        String[] roles2 = secured.value();

        // Different array instances should be returned
        assertNotSame(roles1, roles2);

        // But values should be the same
        assertEquals(roles1.length, roles2.length);
        for (int i = 0; i < roles1.length; i++) {
            assertEquals(roles1[i], roles2[i]);
        }
    }

    // Test annotation with duplicate roles
    @Secured({ "ROLE_USER", "ROLE_ADMIN", "ROLE_USER" })
    static class DuplicateRolesClass {
    }

    @Test
    public void test_duplicateRoles() {
        Secured secured = DuplicateRolesClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(3, roles.length);
        assertEquals("ROLE_USER", roles[0]);
        assertEquals("ROLE_ADMIN", roles[1]);
        assertEquals("ROLE_USER", roles[2]);
    }

    // Test annotation on private method
    static class PrivateMethodClass {
        @Secured({ "PRIVATE_ROLE" })
        private void privateMethod() {
        }

        @Secured({ "PROTECTED_ROLE" })
        protected void protectedMethod() {
        }

        @Secured({ "PACKAGE_ROLE" })
        void packageMethod() {
        }
    }

    @Test
    public void test_privateMethodAnnotation() throws NoSuchMethodException {
        Method method = PrivateMethodClass.class.getDeclaredMethod("privateMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("PRIVATE_ROLE", secured.value()[0]);
    }

    @Test
    public void test_protectedMethodAnnotation() throws NoSuchMethodException {
        Method method = PrivateMethodClass.class.getDeclaredMethod("protectedMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("PROTECTED_ROLE", secured.value()[0]);
    }

    @Test
    public void test_packageMethodAnnotation() throws NoSuchMethodException {
        Method method = PrivateMethodClass.class.getDeclaredMethod("packageMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("PACKAGE_ROLE", secured.value()[0]);
    }

    // Test annotation on static method
    static class StaticMethodClass {
        @Secured({ "STATIC_ROLE" })
        public static void staticMethod() {
        }
    }

    @Test
    public void test_staticMethodAnnotation() throws NoSuchMethodException {
        Method method = StaticMethodClass.class.getMethod("staticMethod");
        Secured secured = method.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("STATIC_ROLE", secured.value()[0]);
    }

    // Test annotation on constructor (should not compile, but test for completeness)
    static class ConstructorClass {
        // Note: @Secured cannot be applied to constructors due to @Target restrictions
        public ConstructorClass() {
        }
    }

    @Test
    public void test_constructorCannotBeAnnotated() throws NoSuchMethodException {
        // Verify that Secured annotation's target does not include CONSTRUCTOR
        Target target = Secured.class.getAnnotation(Target.class);
        ElementType[] targetTypes = target.value();
        assertFalse(Arrays.asList(targetTypes).contains(ElementType.CONSTRUCTOR));
    }

    // Test annotation on field (should not compile, but test for completeness)
    @Test
    public void test_fieldCannotBeAnnotated() {
        // Verify that Secured annotation's target does not include FIELD
        Target target = Secured.class.getAnnotation(Target.class);
        ElementType[] targetTypes = target.value();
        assertFalse(Arrays.asList(targetTypes).contains(ElementType.FIELD));
    }

    // Test with long role names
    @Secured({ "ROLE_SUPER_ULTRA_MEGA_ADMIN_WITH_VERY_LONG_NAME_FOR_TESTING_PURPOSES" })
    static class LongRoleNameClass {
    }

    @Test
    public void test_longRoleName() {
        Secured secured = LongRoleNameClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        assertEquals("ROLE_SUPER_ULTRA_MEGA_ADMIN_WITH_VERY_LONG_NAME_FOR_TESTING_PURPOSES", secured.value()[0]);
    }

    // Test with many roles
    @Secured({ "ROLE_1", "ROLE_2", "ROLE_3", "ROLE_4", "ROLE_5", "ROLE_6", "ROLE_7", "ROLE_8", "ROLE_9", "ROLE_10" })
    static class ManyRolesClass {
    }

    @Test
    public void test_manyRoles() {
        Secured secured = ManyRolesClass.class.getAnnotation(Secured.class);
        assertNotNull(secured);
        String[] roles = secured.value();
        assertEquals(10, roles.length);
        for (int i = 0; i < 10; i++) {
            assertEquals("ROLE_" + (i + 1), roles[i]);
        }
    }
}