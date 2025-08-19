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

import org.apache.lucene.queryparser.classic.ParseException;
import org.codelibs.fess.unit.UnitFessTestCase;

public class QueryParseExceptionTest extends UnitFessTestCase {

    public void test_constructor() {
        // Test constructor with ParseException cause
        ParseException parseException = new ParseException("Test parse error");
        QueryParseException queryParseException = new QueryParseException(parseException);

        assertNotNull(queryParseException);
        assertEquals(parseException, queryParseException.getCause());
        assertTrue(queryParseException instanceof FessSystemException);
        assertTrue(queryParseException instanceof RuntimeException);
    }

    public void test_getMessage() {
        // Test that message is inherited from the cause
        String errorMessage = "Invalid query syntax";
        ParseException parseException = new ParseException(errorMessage);
        QueryParseException queryParseException = new QueryParseException(parseException);

        assertEquals("org.apache.lucene.queryparser.classic.ParseException: " + errorMessage, queryParseException.getMessage());
    }

    public void test_getCause() {
        // Test that the cause is properly set and retrievable
        ParseException parseException = new ParseException("Query parsing failed");
        QueryParseException queryParseException = new QueryParseException(parseException);

        Throwable cause = queryParseException.getCause();
        assertNotNull(cause);
        assertTrue(cause instanceof ParseException);
        assertEquals(parseException, cause);
        assertEquals("Query parsing failed", cause.getMessage());
    }

    public void test_stackTrace() {
        // Test that stack trace is properly set
        ParseException parseException = new ParseException("Stack trace test");
        QueryParseException queryParseException = new QueryParseException(parseException);

        StackTraceElement[] stackTrace = queryParseException.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    public void test_serialVersionUID() {
        // Test that the exception is serializable
        ParseException parseException = new ParseException("Serialization test");
        QueryParseException queryParseException = new QueryParseException(parseException);

        // Verify that the exception can be created and has serialVersionUID
        assertNotNull(queryParseException);
        assertTrue(queryParseException instanceof java.io.Serializable);
    }

    public void test_inheritanceHierarchy() {
        // Test the inheritance hierarchy
        ParseException parseException = new ParseException("Hierarchy test");
        QueryParseException queryParseException = new QueryParseException(parseException);

        // Verify inheritance chain
        assertTrue(queryParseException instanceof QueryParseException);
        assertTrue(queryParseException instanceof FessSystemException);
        assertTrue(queryParseException instanceof RuntimeException);
        assertTrue(queryParseException instanceof Exception);
        assertTrue(queryParseException instanceof Throwable);
    }

    public void test_nullHandling() {
        // Test with null ParseException
        QueryParseException queryParseException = new QueryParseException(null);
        assertNotNull(queryParseException);
        assertNull(queryParseException.getCause());
        // getMessage() may return null when constructed with null
        // This is expected behavior for exceptions with null cause
    }

    public void test_nestedCause() {
        // Test with nested exceptions
        RuntimeException rootCause = new RuntimeException("Root cause");
        ParseException parseException = new ParseException("Parse error");
        parseException.initCause(rootCause);
        QueryParseException queryParseException = new QueryParseException(parseException);

        assertEquals(parseException, queryParseException.getCause());
        assertEquals(rootCause, queryParseException.getCause().getCause());
    }

    public void test_toString() {
        // Test toString method
        String errorMessage = "ToString test error";
        ParseException parseException = new ParseException(errorMessage);
        QueryParseException queryParseException = new QueryParseException(parseException);

        String toStringResult = queryParseException.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("QueryParseException"));
        assertTrue(toStringResult.contains("ParseException"));
    }
}