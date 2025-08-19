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

public class ContentNotFoundExceptionTest extends UnitFessTestCase {

    public void test_constructor_withValidUrls() {
        // Test with normal URLs
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: http://example.com/child Parent: http://example.com/parent", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullParentUrl() {
        // Test with null parent URL
        String parentUrl = null;
        String url = "http://example.com/child";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: http://example.com/child Parent: null", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withNullUrl() {
        // Test with null URL
        String parentUrl = "http://example.com/parent";
        String url = null;
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: null Parent: http://example.com/parent", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withBothNullUrls() {
        // Test with both URLs as null
        String parentUrl = null;
        String url = null;
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: null Parent: null", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withEmptyUrls() {
        // Test with empty strings
        String parentUrl = "";
        String url = "";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found:  Parent: ", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withSpecialCharacters() {
        // Test with URLs containing special characters
        String parentUrl = "http://example.com/parent?query=test&id=123";
        String url = "http://example.com/child#fragment";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: http://example.com/child#fragment Parent: http://example.com/parent?query=test&id=123",
                exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_constructor_withLongUrls() {
        // Test with very long URLs
        StringBuilder longParentUrl = new StringBuilder("http://example.com/");
        StringBuilder longUrl = new StringBuilder("http://example.com/");
        for (int i = 0; i < 100; i++) {
            longParentUrl.append("parent/");
            longUrl.append("child/");
        }

        ContentNotFoundException exception = new ContentNotFoundException(longParentUrl.toString(), longUrl.toString());

        assertNotNull(exception);
        assertTrue(exception.getMessage().startsWith("Not Found: http://example.com/"));
        assertTrue(exception.getMessage().contains("Parent: http://example.com/"));
        assertNull(exception.getCause());
    }

    public void test_constructor_withUnicodeCharacters() {
        // Test with Unicode characters in URLs
        String parentUrl = "http://example.com/親/ディレクトリ";
        String url = "http://example.com/子/ファイル.html";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertNotNull(exception);
        assertEquals("Not Found: http://example.com/子/ファイル.html Parent: http://example.com/親/ディレクトリ", exception.getMessage());
        assertNull(exception.getCause());
    }

    public void test_instanceOfFessSystemException() {
        // Verify that ContentNotFoundException is an instance of FessSystemException
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    public void test_suppressionAndStackTrace() {
        // Test that suppression and stack trace are disabled (as per constructor)
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        // Verify that suppression is disabled
        try {
            exception.addSuppressed(new Exception("Test suppressed"));
            // If suppression is disabled, getSuppressed should return empty array
            assertEquals(0, exception.getSuppressed().length);
        } catch (Exception e) {
            // If an exception is thrown when trying to add suppressed, that's also valid
            // since suppression is disabled
        }

        // Stack trace should be empty since writableStackTrace is false
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertEquals(0, stackTrace.length);
    }

    public void test_serialization() {
        // Test that the exception has correct serialVersionUID
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";
        ContentNotFoundException exception = new ContentNotFoundException(parentUrl, url);

        // The exception should be serializable since it extends RuntimeException
        assertTrue(exception instanceof java.io.Serializable);
    }

    public void test_throwAndCatch() {
        // Test throwing and catching the exception
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";

        try {
            throw new ContentNotFoundException(parentUrl, url);
        } catch (ContentNotFoundException e) {
            assertEquals("Not Found: http://example.com/child Parent: http://example.com/parent", e.getMessage());
            assertNull(e.getCause());
        } catch (Exception e) {
            fail("Should have caught ContentNotFoundException");
        }
    }

    public void test_catchAsFessSystemException() {
        // Test catching as parent exception type
        String parentUrl = "http://example.com/parent";
        String url = "http://example.com/child";

        try {
            throw new ContentNotFoundException(parentUrl, url);
        } catch (FessSystemException e) {
            assertTrue(e instanceof ContentNotFoundException);
            assertEquals("Not Found: http://example.com/child Parent: http://example.com/parent", e.getMessage());
        } catch (Exception e) {
            fail("Should have caught as FessSystemException");
        }
    }
}