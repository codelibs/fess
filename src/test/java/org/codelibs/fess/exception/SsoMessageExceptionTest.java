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

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.validation.VaMessenger;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

public class SsoMessageExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessageCodeMessageAndCause() {
        // Setup
        final String message = "Test SSO error message";
        final Exception cause = new RuntimeException("Test cause");
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message, cause);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withMessageCodeAndMessage() {
        // Setup
        final String message = "Test SSO error without cause";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullMessageCode() {
        // Setup
        final String message = "Test error with null message code";
        final Exception cause = new RuntimeException("Test cause");

        // Execute
        final SsoMessageException exception = new SsoMessageException(null, message, cause);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Setup
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new RuntimeException("Test cause");

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, null, cause);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullCause() {
        // Setup
        final String message = "Test error with null cause";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message, null);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withAllNullParameters() {
        // Execute
        final SsoMessageException exception = new SsoMessageException(null, null, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithNullMessageCode() {
        // Setup
        final String message = "Test message";

        // Execute
        final SsoMessageException exception = new SsoMessageException(null, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithNullMessage() {
        // Setup
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithBothNull() {
        // Execute
        final SsoMessageException exception = new SsoMessageException(null, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_inheritanceFromFessSystemException() {
        // Setup
        final String message = "Test inheritance";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message);

        // Verify that it's an instance of FessSystemException
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void test_stackTracePresence() {
        // Setup
        final String message = "Test stack trace";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new RuntimeException("Root cause");

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message, cause);

        // Verify stack trace is available
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    public void test_differentMessageCodes() {
        // Setup - test with different message codes
        final String message = "Test message";
        final VaMessenger<FessMessages> loginErrorCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);
        final VaMessenger<FessMessages> logoutErrorCode = messages -> messages.addSuccessSsoLogout(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException loginException = new SsoMessageException(loginErrorCode, message);
        final SsoMessageException logoutException = new SsoMessageException(logoutErrorCode, message);

        // Verify
        assertEquals(loginErrorCode, loginException.getMessageCode());
        assertEquals(logoutErrorCode, logoutException.getMessageCode());
        assertNotSame(loginException.getMessageCode(), logoutException.getMessageCode());
    }

    @Test
    public void test_complexMessageCode() {
        // Setup - test with a complex message code that has parameters
        final String errorDetail = "Authentication failed";
        final VaMessenger<FessMessages> complexMessageCode =
                messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, errorDetail);
        final String message = "SSO processing failed";

        // Execute
        final SsoMessageException exception = new SsoMessageException(complexMessageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(complexMessageCode, exception.getMessageCode());
    }

    @Test
    public void test_causeChainPropagation() {
        // Setup - create a chain of exceptions
        final Exception rootCause = new IllegalArgumentException("Root cause");
        final Exception middleCause = new IllegalStateException("Middle cause", rootCause);
        final String message = "Top level SSO error";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message, middleCause);

        // Verify cause chain
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_emptyMessage() {
        // Setup
        final String message = "";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_messageWithSpecialCharacters() {
        // Setup
        final String message = "Error: Failed to authenticate user <test@example.com> with special chars: #$%&*()!@";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new RuntimeException("Special cause: \n\t\r");

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message, cause);

        // Verify
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_verifySerialVersionUID() {
        // Setup
        final String message = "Test serial version UID";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsSsoLoginError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final SsoMessageException exception = new SsoMessageException(messageCode, message);

        // Verify that it has serialVersionUID (indirectly by checking it's serializable)
        assertTrue(exception instanceof java.io.Serializable);
    }
}