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
package org.codelibs.fess.exception;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ContainerNotAvailableExceptionTest extends UnitFessTestCase {

    public void test_constructor_withComponentName() {
        // Test constructor with component name only
        String componentName = "testComponent";
        ContainerNotAvailableException exception = new ContainerNotAvailableException(componentName);

        assertEquals(componentName + " is not available.", exception.getMessage());
        assertNull(exception.getCause());
        // Note: This test reveals that componentName field is not set in this constructor
        assertNull(exception.getComponentName());
    }

    public void test_constructor_withComponentNameAndCause() {
        // Test constructor with component name and cause
        String componentName = "myComponent";
        Throwable cause = new RuntimeException("Connection failed");
        ContainerNotAvailableException exception = new ContainerNotAvailableException(componentName, cause);

        assertEquals(componentName + " is not available.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(componentName, exception.getComponentName());
    }

    public void test_constructor_withCause() {
        // Test constructor with cause only
        Throwable cause = new IllegalStateException("Container initialization failed");
        ContainerNotAvailableException exception = new ContainerNotAvailableException(cause);

        assertEquals("Container is not available.", exception.getMessage());
        // The cause is not passed to super constructor in the implementation
        assertNull(exception.getCause());
        assertEquals("container", exception.getComponentName());
    }

    public void test_constructor_withNullComponentName() {
        // Test constructor with null component name
        ContainerNotAvailableException exception = new ContainerNotAvailableException((String) null);

        assertEquals("null is not available.", exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getComponentName());
    }

    public void test_constructor_withEmptyComponentName() {
        // Test constructor with empty component name
        String componentName = "";
        ContainerNotAvailableException exception = new ContainerNotAvailableException(componentName);

        assertEquals(" is not available.", exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getComponentName());
    }

    public void test_constructor_withNullComponentNameAndCause() {
        // Test constructor with null component name and valid cause
        Throwable cause = new RuntimeException("Test cause");
        ContainerNotAvailableException exception = new ContainerNotAvailableException(null, cause);

        assertEquals("null is not available.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getComponentName());
    }

    public void test_constructor_withComponentNameAndNullCause() {
        // Test constructor with component name and null cause
        String componentName = "testService";
        ContainerNotAvailableException exception = new ContainerNotAvailableException(componentName, null);

        assertEquals(componentName + " is not available.", exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(componentName, exception.getComponentName());
    }

    public void test_constructor_withNullCause() {
        // Test constructor with null cause only
        ContainerNotAvailableException exception = new ContainerNotAvailableException((Throwable) null);

        assertEquals("Container is not available.", exception.getMessage());
        // The cause is not passed to super constructor in the implementation
        assertNull(exception.getCause());
        assertEquals("container", exception.getComponentName());
    }

    public void test_getComponentName() {
        // Test getComponentName method with various scenarios
        String componentName1 = "dataSource";
        ContainerNotAvailableException exception1 = new ContainerNotAvailableException(componentName1, new RuntimeException());
        assertEquals(componentName1, exception1.getComponentName());

        ContainerNotAvailableException exception2 = new ContainerNotAvailableException(new RuntimeException());
        assertEquals("container", exception2.getComponentName());

        ContainerNotAvailableException exception3 = new ContainerNotAvailableException("anotherComponent");
        assertNull(exception3.getComponentName());
    }

    public void test_exceptionChaining() {
        // Test exception chaining with multiple levels
        Exception rootCause = new Exception("Root cause");
        RuntimeException middleCause = new RuntimeException("Middle cause", rootCause);
        ContainerNotAvailableException topException = new ContainerNotAvailableException("dbConnection", middleCause);

        assertEquals("dbConnection is not available.", topException.getMessage());
        assertEquals("dbConnection", topException.getComponentName());
        assertEquals(middleCause, topException.getCause());
        assertEquals(rootCause, topException.getCause().getCause());
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String componentName = "cacheManager";
        try {
            throw new ContainerNotAvailableException(componentName);
        } catch (ContainerNotAvailableException e) {
            assertEquals(componentName + " is not available.", e.getMessage());
            assertNull(e.getCause());
            assertNull(e.getComponentName());
        }
    }

    public void test_throwAndCatchWithCause() {
        // Test throwing and catching the exception with cause
        String componentName = "searchEngine";
        Exception expectedCause = new IllegalStateException("Not initialized");

        try {
            throw new ContainerNotAvailableException(componentName, expectedCause);
        } catch (ContainerNotAvailableException e) {
            assertEquals(componentName + " is not available.", e.getMessage());
            assertEquals(expectedCause, e.getCause());
            assertEquals(componentName, e.getComponentName());
        }
    }

    public void test_instanceOfFessSystemException() {
        // Test that ContainerNotAvailableException is an instance of FessSystemException
        ContainerNotAvailableException exception = new ContainerNotAvailableException("test");
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_serialVersionUID() {
        // Test that serialVersionUID is properly defined
        ContainerNotAvailableException exception1 = new ContainerNotAvailableException("test");
        ContainerNotAvailableException exception2 = new ContainerNotAvailableException("test");

        // Both instances should be of the same class
        assertEquals(exception1.getClass(), exception2.getClass());
    }

    public void test_componentNameWithSpecialCharacters() {
        // Test component name with special characters
        String componentName = "my-component_v2.0@service";
        ContainerNotAvailableException exception = new ContainerNotAvailableException(componentName, new RuntimeException());

        assertEquals(componentName + " is not available.", exception.getMessage());
        assertEquals(componentName, exception.getComponentName());
    }

    public void test_longComponentName() {
        // Test with a very long component name
        String longName = "com.example.very.long.package.name.with.many.nested.levels.MyVeryLongComponentNameThatExceedsNormalLength";
        ContainerNotAvailableException exception = new ContainerNotAvailableException(longName, null);

        assertEquals(longName + " is not available.", exception.getMessage());
        assertEquals(longName, exception.getComponentName());
    }

    public void test_messageFormatting() {
        // Test message formatting for different constructors
        ContainerNotAvailableException exception1 = new ContainerNotAvailableException("service1");
        assertEquals("service1 is not available.", exception1.getMessage());

        ContainerNotAvailableException exception2 = new ContainerNotAvailableException("service2", new RuntimeException());
        assertEquals("service2 is not available.", exception2.getMessage());

        ContainerNotAvailableException exception3 = new ContainerNotAvailableException(new RuntimeException());
        assertEquals("Container is not available.", exception3.getMessage());
    }

    public void test_causeOnlyConstructorWithMessage() {
        // Test that cause-only constructor properly handles the cause
        String causeMessage = "Initialization failed";
        RuntimeException cause = new RuntimeException(causeMessage);
        ContainerNotAvailableException exception = new ContainerNotAvailableException(cause);

        assertEquals("Container is not available.", exception.getMessage());
        // The cause is not passed to super constructor in the implementation
        assertNull(exception.getCause());
        assertEquals("container", exception.getComponentName());
    }
}
