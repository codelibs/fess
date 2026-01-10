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
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class InvalidQueryExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructor_withMessageCodeMessageAndCause() {
        // Setup
        final String message = "Invalid query syntax error";
        final Exception cause = new RuntimeException("Query parsing failed");
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message, cause);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withMessageCodeAndMessage() {
        // Setup
        final String message = "Query contains invalid characters";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullMessageCode() {
        // Setup
        final String message = "Invalid query with null message code";
        final Exception cause = new RuntimeException("Underlying error");

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(null, message, cause);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullMessage() {
        // Setup
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new RuntimeException("Query error");

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, null, cause);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withNullCause() {
        // Setup
        final String message = "Query validation failed";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message, null);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_withAllNullParameters() {
        // Execute
        final InvalidQueryException exception = new InvalidQueryException(null, null, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithNullMessageCode() {
        // Setup
        final String message = "Query error without message code";

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(null, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithNullMessage() {
        // Setup
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_constructor_twoParametersWithBothNull() {
        // Execute
        final InvalidQueryException exception = new InvalidQueryException(null, null);

        // Verify
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getMessageCode());
    }

    @Test
    public void test_inheritanceFromFessSystemException() {
        // Setup
        final String message = "Test inheritance hierarchy";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify inheritance hierarchy
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_stackTracePresence() {
        // Setup
        final String message = "Query error with stack trace";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new IllegalArgumentException("Invalid query argument");

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message, cause);

        // Verify stack trace is captured
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    public void test_differentMessageCodes() {
        // Setup - test with different message codes
        final String message = "Query validation error";
        final VaMessenger<FessMessages> unknownErrorCode =
                messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final VaMessenger<FessMessages> parseErrorCode =
                messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException unknownException = new InvalidQueryException(unknownErrorCode, message);
        final InvalidQueryException parseException = new InvalidQueryException(parseErrorCode, message);

        // Verify different message codes
        assertEquals(unknownErrorCode, unknownException.getMessageCode());
        assertEquals(parseErrorCode, parseException.getMessageCode());
        assertNotSame(unknownException.getMessageCode(), parseException.getMessageCode());
    }

    @Test
    public void test_complexMessageCode() {
        // Setup - test with a complex message code that includes parameters
        final String queryString = "field:value AND (";
        final VaMessenger<FessMessages> complexMessageCode =
                messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY);
        final String message = "Failed to parse query: " + queryString;

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(complexMessageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(complexMessageCode, exception.getMessageCode());
    }

    @Test
    public void test_causeChainPropagation() {
        // Setup - create a chain of exceptions
        final IllegalArgumentException rootCause = new IllegalArgumentException("Invalid character in query");
        final IllegalStateException middleCause = new IllegalStateException("Query parser state error", rootCause);
        final String message = "Query processing failed";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message, middleCause);

        // Verify cause chain is preserved
        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_emptyMessage() {
        // Setup
        final String message = "";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify empty message is preserved
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_messageWithSpecialCharacters() {
        // Setup
        final String message = "Query error: field:\"test value\" AND category:[A TO Z] OR tag:#hashtag @mention $special %wildcard";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final Exception cause = new RuntimeException("Special chars: \n\t\r\f\b");

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message, cause);

        // Verify special characters are preserved
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_verifySerialVersionUID() {
        // Setup
        final String message = "Test serialization capability";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify that exception is serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    public void test_longMessage() {
        // Setup - test with a very long message
        final StringBuilder longMessageBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessageBuilder.append("Query segment ").append(i).append("; ");
        }
        final String longMessage = longMessageBuilder.toString();
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, longMessage);

        // Verify long message is handled correctly
        assertNotNull(exception);
        assertEquals(longMessage, exception.getMessage());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_multipleExceptionTypes() {
        // Setup - test with different types of causes
        final String message = "Query processing error";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);

        final NullPointerException npeCause = new NullPointerException("Null query parameter");
        final IllegalStateException iseCause = new IllegalStateException("Invalid parser state");
        final OutOfMemoryError oomCause = new OutOfMemoryError("Query too large");

        // Execute
        final InvalidQueryException npeException = new InvalidQueryException(messageCode, message, npeCause);
        final InvalidQueryException iseException = new InvalidQueryException(messageCode, message, iseCause);
        final InvalidQueryException oomException = new InvalidQueryException(messageCode, message, oomCause);

        // Verify different cause types are handled
        assertEquals(npeCause, npeException.getCause());
        assertEquals(iseCause, iseException.getCause());
        assertEquals(oomCause, oomException.getCause());
    }

    @Test
    public void test_messageCodeWithPropertyKey() {
        // Setup - test message code with different property keys
        final String message = "Query field error";
        final String propertyKey = "searchQuery";
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(propertyKey);

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(messageCode, exception.getMessageCode());
    }

    @Test
    public void test_getMessageCodeConsistency() {
        // Setup
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final String message = "Consistent message code test";

        // Execute
        final InvalidQueryException exception = new InvalidQueryException(messageCode, message);

        // Verify getMessageCode returns the same instance
        final VaMessenger<FessMessages> retrievedCode1 = exception.getMessageCode();
        final VaMessenger<FessMessages> retrievedCode2 = exception.getMessageCode();

        assertNotNull(retrievedCode1);
        assertNotNull(retrievedCode2);
        assertSame(retrievedCode1, retrievedCode2);
        assertEquals(messageCode, retrievedCode1);
    }

    @Test
    public void test_constructorParameterOrder() {
        // Setup - verify parameter order is correct
        final VaMessenger<FessMessages> messageCode = messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY);
        final String message = "Parameter order test";
        final Exception cause = new RuntimeException("Test cause");

        // Execute with three parameters
        final InvalidQueryException exception3Params = new InvalidQueryException(messageCode, message, cause);

        // Execute with two parameters
        final InvalidQueryException exception2Params = new InvalidQueryException(messageCode, message);

        // Verify parameter order is preserved correctly
        assertEquals(messageCode, exception3Params.getMessageCode());
        assertEquals(message, exception3Params.getMessage());
        assertEquals(cause, exception3Params.getCause());

        assertEquals(messageCode, exception2Params.getMessageCode());
        assertEquals(message, exception2Params.getMessage());
        assertNull(exception2Params.getCause());
    }
}