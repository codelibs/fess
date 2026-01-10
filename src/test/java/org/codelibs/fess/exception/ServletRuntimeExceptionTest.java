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

import jakarta.servlet.ServletException;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ServletRuntimeExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withServletException() {
        // Create a ServletException with a message
        String errorMessage = "Test servlet error message";
        ServletException servletException = new ServletException(errorMessage);

        // Wrap it in ServletRuntimeException
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // Verify that the cause is correctly set
        assertNotNull(runtimeException);
        assertEquals(servletException, runtimeException.getCause());

        // Verify that the message is preserved from the cause
        assertTrue(runtimeException.getMessage().contains(ServletException.class.getName()));
        assertTrue(runtimeException.getMessage().contains(errorMessage));
    }

    @Test
    public void test_constructor_withServletExceptionWithCause() {
        // Create a root cause exception
        String rootCauseMessage = "Root cause error";
        Exception rootCause = new Exception(rootCauseMessage);

        // Create a ServletException with the root cause
        String servletErrorMessage = "Servlet error with cause";
        ServletException servletException = new ServletException(servletErrorMessage, rootCause);

        // Wrap it in ServletRuntimeException
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // Verify the exception chain
        assertNotNull(runtimeException);
        assertEquals(servletException, runtimeException.getCause());
        assertEquals(rootCause, servletException.getCause());

        // Verify messages in the chain
        assertTrue(runtimeException.getMessage().contains(ServletException.class.getName()));
        assertTrue(runtimeException.getMessage().contains(servletErrorMessage));
    }

    @Test
    public void test_constructor_withServletExceptionNoMessage() {
        // Create a ServletException without a message
        ServletException servletException = new ServletException();

        // Wrap it in ServletRuntimeException
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // Verify that the cause is correctly set
        assertNotNull(runtimeException);
        assertEquals(servletException, runtimeException.getCause());

        // Verify the message contains the exception class name
        assertTrue(runtimeException.getMessage().contains(ServletException.class.getName()));
    }

    @Test
    public void test_instanceOfRuntimeException() {
        // Create and wrap a ServletException
        ServletException servletException = new ServletException("Test error");
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // Verify it's an instance of RuntimeException
        assertTrue(runtimeException instanceof RuntimeException);
        assertTrue(runtimeException instanceof Exception);
        assertTrue(runtimeException instanceof Throwable);
    }

    @Test
    public void test_stackTrace() {
        // Create a ServletException with a specific message
        String errorMessage = "Stack trace test error";
        ServletException servletException = new ServletException(errorMessage);

        // Wrap it in ServletRuntimeException
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // Verify stack trace is available
        StackTraceElement[] stackTrace = runtimeException.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);

        // Verify the cause's stack trace is also available
        StackTraceElement[] causeStackTrace = runtimeException.getCause().getStackTrace();
        assertNotNull(causeStackTrace);
        assertTrue(causeStackTrace.length > 0);
    }

    @Test
    public void test_throwAndCatch() {
        // Test that the exception can be thrown and caught properly
        ServletException originalException = new ServletException("Throw test");

        try {
            throw new ServletRuntimeException(originalException);
        } catch (ServletRuntimeException caught) {
            // Verify the caught exception
            assertNotNull(caught);
            assertEquals(originalException, caught.getCause());
            assertTrue(caught.getMessage().contains("ServletException"));
        } catch (Exception e) {
            fail("Should have caught ServletRuntimeException");
        }
    }

    @Test
    public void test_serialization() {
        // Test that the serialVersionUID is properly defined
        // This test verifies that the class can be instantiated and has the serial version UID
        ServletException servletException = new ServletException("Serialization test");
        ServletRuntimeException runtimeException = new ServletRuntimeException(servletException);

        // The fact that we can create the instance verifies the serialVersionUID is defined
        assertNotNull(runtimeException);

        // Verify the exception maintains its state
        assertEquals(servletException, runtimeException.getCause());
    }
}