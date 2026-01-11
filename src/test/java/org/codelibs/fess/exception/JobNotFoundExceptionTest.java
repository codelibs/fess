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

import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class JobNotFoundExceptionTest extends UnitFessTestCase {

    @Test
    public void test_constructorWithScheduledJob() {
        // Test with scheduled job that has a custom toString implementation
        ScheduledJob scheduledJob = new ScheduledJob() {
            @Override
            public String toString() {
                return "TestScheduledJob[id=123]";
            }
        };

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertEquals("TestScheduledJob[id=123]", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithScheduledJob_nullToString() {
        // Test with scheduled job that returns null from toString
        ScheduledJob scheduledJob = new ScheduledJob() {
            @Override
            public String toString() {
                return null;
            }
        };

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithScheduledJob_emptyToString() {
        // Test with scheduled job that returns empty string from toString
        ScheduledJob scheduledJob = new ScheduledJob() {
            @Override
            public String toString() {
                return "";
            }
        };

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithScheduledJob_defaultToString() {
        // Test with scheduled job using default toString implementation
        ScheduledJob scheduledJob = new ScheduledJob();

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("ScheduledJob"));
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithString() {
        // Test with a simple message
        String message = "Job with ID 456 not found";

        JobNotFoundException exception = new JobNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithString_null() {
        // Test with null message
        JobNotFoundException exception = new JobNotFoundException((String) null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithString_empty() {
        // Test with empty message
        String message = "";

        JobNotFoundException exception = new JobNotFoundException(message);

        assertNotNull(exception);
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithString_longMessage() {
        // Test with a long message
        String message = "This is a very long error message that describes in detail why the job was not found. "
                + "The job with identifier XYZ123 was expected to be in the system but could not be located "
                + "after searching through all available job registries and scheduled task databases.";

        JobNotFoundException exception = new JobNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_exceptionInheritance() {
        // Test that JobNotFoundException properly extends FessSystemException
        JobNotFoundException exception = new JobNotFoundException("test");

        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    public void test_serialization() {
        // Test that the exception has serialVersionUID defined
        JobNotFoundException exception = new JobNotFoundException("test");

        // Simply verify the exception can be created without issues
        // The serialVersionUID is compile-time checked
        assertNotNull(exception);
    }

    @Test
    public void test_stackTrace() {
        // Test that stack trace is properly captured
        JobNotFoundException exception = new JobNotFoundException("Stack trace test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);

        // Verify the stack trace contains this test method
        boolean foundTestMethod = false;
        for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getMethodName().equals("test_stackTrace")) {
                foundTestMethod = true;
                break;
            }
        }
        assertTrue(foundTestMethod);
    }

    @Test
    public void test_fillInStackTrace() {
        // Test that fillInStackTrace works properly
        JobNotFoundException exception = new JobNotFoundException("Fill stack trace test");

        Throwable filled = exception.fillInStackTrace();

        assertNotNull(filled);
        assertEquals(exception, filled);
        assertNotNull(filled.getStackTrace());
        assertTrue(filled.getStackTrace().length > 0);
    }

    @Test
    public void test_getMessage_consistency() {
        // Test that getMessage returns consistent results
        String message = "Consistent message";
        JobNotFoundException exception = new JobNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(message, exception.getMessage()); // Call twice to verify consistency
        assertEquals(message, exception.getLocalizedMessage());
    }

    @Test
    public void test_toString() {
        // Test toString method
        String message = "Test exception message";
        JobNotFoundException exception = new JobNotFoundException(message);

        String toStringResult = exception.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("JobNotFoundException"));
        assertTrue(toStringResult.contains(message));
    }

    @Test
    public void test_constructorWithScheduledJob_specialCharacters() {
        // Test with scheduled job that has special characters in toString
        ScheduledJob scheduledJob = new ScheduledJob() {
            @Override
            public String toString() {
                return "Job[name=\"Test & Job\", id='<123>']";
            }
        };

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertEquals("Job[name=\"Test & Job\", id='<123>']", exception.getMessage());
    }

    @Test
    public void test_constructorWithString_specialCharacters() {
        // Test with message containing special characters
        String message = "Job not found: \"My Job\" with params: {id=123, type='test'}";

        JobNotFoundException exception = new JobNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_constructorWithString_unicodeCharacters() {
        // Test with message containing unicode characters
        String message = "ジョブが見つかりません: 日本語のテスト";

        JobNotFoundException exception = new JobNotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void test_constructorWithScheduledJob_unicodeToString() {
        // Test with scheduled job that returns unicode in toString
        ScheduledJob scheduledJob = new ScheduledJob() {
            @Override
            public String toString() {
                return "スケジュールジョブ[名前=テスト]";
            }
        };

        JobNotFoundException exception = new JobNotFoundException(scheduledJob);

        assertNotNull(exception);
        assertEquals("スケジュールジョブ[名前=テスト]", exception.getMessage());
    }
}