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
package org.codelibs.fess.job;

import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PurgeThumbnailJobTest extends UnitFessTestCase {

    private PurgeThumbnailJob purgeThumbnailJob;
    private MockThumbnailManager thumbnailManager;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        purgeThumbnailJob = new PurgeThumbnailJob();

        // Create a mock ThumbnailManager
        thumbnailManager = new MockThumbnailManager();

        ComponentUtil.register(thumbnailManager, "thumbnailManager");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    // Test default constructor
    @Test
    public void test_constructor() {
        PurgeThumbnailJob job = new PurgeThumbnailJob();
        assertNotNull(job);
        assertEquals(30L * 24 * 60 * 60 * 1000L, job.getExpiry());
    }

    // Test getExpiry with default value
    @Test
    public void test_getExpiry_default() {
        assertEquals(30L * 24 * 60 * 60 * 1000L, purgeThumbnailJob.getExpiry());
    }

    // Test expiry setter with valid value
    @Test
    public void test_expiry_validValue() {
        long newExpiry = 60L * 24 * 60 * 60 * 1000L; // 60 days
        PurgeThumbnailJob result = purgeThumbnailJob.expiry(newExpiry);

        // Test method chaining
        assertSame(purgeThumbnailJob, result);
        // Test value was set
        assertEquals(newExpiry, purgeThumbnailJob.getExpiry());
    }

    // Test expiry setter with zero value (should not change)
    @Test
    public void test_expiry_zeroValue() {
        long originalExpiry = purgeThumbnailJob.getExpiry();
        PurgeThumbnailJob result = purgeThumbnailJob.expiry(0);

        // Test method chaining
        assertSame(purgeThumbnailJob, result);
        // Test value was not changed
        assertEquals(originalExpiry, purgeThumbnailJob.getExpiry());
    }

    // Test expiry setter with negative value (should not change)
    @Test
    public void test_expiry_negativeValue() {
        long originalExpiry = purgeThumbnailJob.getExpiry();
        PurgeThumbnailJob result = purgeThumbnailJob.expiry(-1000L);

        // Test method chaining
        assertSame(purgeThumbnailJob, result);
        // Test value was not changed
        assertEquals(originalExpiry, purgeThumbnailJob.getExpiry());
    }

    // Test expiry setter with small positive value
    @Test
    public void test_expiry_smallPositiveValue() {
        PurgeThumbnailJob result = purgeThumbnailJob.expiry(1L);

        // Test method chaining
        assertSame(purgeThumbnailJob, result);
        // Test value was set
        assertEquals(1L, purgeThumbnailJob.getExpiry());
    }

    // Test execute with successful purge (no files deleted)
    @Test
    public void test_execute_noFilesDeleted() {
        thumbnailManager.setPurgeCallCount(0);

        String result = purgeThumbnailJob.execute();

        assertEquals("Deleted 0 thumbnail files.", result);
        assertEquals(30L * 24 * 60 * 60 * 1000L, thumbnailManager.getPurgeExpiry());
    }

    // Test execute with successful purge (single file deleted)
    @Test
    public void test_execute_singleFileDeleted() {
        thumbnailManager.setPurgeCallCount(1);

        String result = purgeThumbnailJob.execute();

        assertEquals("Deleted 1 thumbnail files.", result);
    }

    // Test execute with successful purge (multiple files deleted)
    @Test
    public void test_execute_multipleFilesDeleted() {
        thumbnailManager.setPurgeCallCount(100);

        String result = purgeThumbnailJob.execute();

        assertEquals("Deleted 100 thumbnail files.", result);
    }

    // Test execute with custom expiry
    @Test
    public void test_execute_customExpiry() {
        long customExpiry = 10L * 24 * 60 * 60 * 1000L; // 10 days
        purgeThumbnailJob.expiry(customExpiry);
        thumbnailManager.setPurgeCallCount(5);

        String result = purgeThumbnailJob.execute();

        assertEquals("Deleted 5 thumbnail files.", result);
        assertEquals(customExpiry, thumbnailManager.getPurgeExpiry());
    }

    // Test execute with exception thrown
    @Test
    public void test_execute_exceptionThrown() {
        thumbnailManager.setThrowException(true);
        thumbnailManager.setExceptionMessage("Purge failed");

        String result = purgeThumbnailJob.execute();

        assertTrue(result.contains("Purge failed"));
    }

    // Test execute with exception thrown (no message)
    @Test
    public void test_execute_exceptionThrownNoMessage() {
        thumbnailManager.setThrowException(true);
        thumbnailManager.setExceptionMessage(null);

        String result = purgeThumbnailJob.execute();

        assertTrue(result.contains("Test exception"));
    }

    // Test execute with large number of deleted files
    @Test
    public void test_execute_largeNumberOfFiles() {
        thumbnailManager.setPurgeCallCount(Long.MAX_VALUE);

        String result = purgeThumbnailJob.execute();

        assertEquals("Deleted " + Long.MAX_VALUE + " thumbnail files.", result);
    }

    // Test method chaining with multiple expiry calls
    @Test
    public void test_expiryChaining() {
        PurgeThumbnailJob result = purgeThumbnailJob.expiry(1000L)
                .expiry(2000L)
                .expiry(0) // Should not change
                .expiry(3000L);

        assertSame(purgeThumbnailJob, result);
        assertEquals(3000L, purgeThumbnailJob.getExpiry());
    }

    // Inner class for mock ThumbnailManager
    private static class MockThumbnailManager extends ThumbnailManager {
        private long purgeCallCount = 0;
        private long purgeExpiry = 0;
        private boolean throwException = false;
        private String exceptionMessage = null;

        @Override
        public long purge(final long expiry) {
            purgeExpiry = expiry;
            if (throwException) {
                if (exceptionMessage != null) {
                    throw new JobProcessingException(new RuntimeException(exceptionMessage));
                }
                throw new JobProcessingException(new RuntimeException("Test exception"));
            }
            return purgeCallCount;
        }

        public void setPurgeCallCount(long count) {
            this.purgeCallCount = count;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public void setExceptionMessage(String message) {
            this.exceptionMessage = message;
        }

        public long getPurgeExpiry() {
            return purgeExpiry;
        }
    }
}