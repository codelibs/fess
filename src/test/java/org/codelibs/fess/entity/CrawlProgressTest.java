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
package org.codelibs.fess.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CrawlProgressTest {

    @Test
    public void test_calculatePercent_withoutPreviousCount() {
        // the first crawl after an upgrade has nothing to compare with
        assertNull(CrawlProgress.calculatePercent(10, 0));
        assertNull(CrawlProgress.calculatePercent(0, 0));
        assertNull(CrawlProgress.calculatePercent(10, -1));
    }

    @Test
    public void test_calculatePercent_range() {
        assertEquals(Integer.valueOf(0), CrawlProgress.calculatePercent(0, 100));
        assertEquals(Integer.valueOf(0), CrawlProgress.calculatePercent(-5, 100));
        assertEquals(Integer.valueOf(0), CrawlProgress.calculatePercent(1, 1000));
        assertEquals(Integer.valueOf(33), CrawlProgress.calculatePercent(1, 3));
        assertEquals(Integer.valueOf(50), CrawlProgress.calculatePercent(50, 100));
        assertEquals(Integer.valueOf(98), CrawlProgress.calculatePercent(98, 100));
    }

    @Test
    public void test_calculatePercent_cappedUntilCompletion() {
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(99, 100));
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(999, 1000));
        // reaching or passing the previous count is not completion
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(100, 100));
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(5000, 100));
    }

    @Test
    public void test_getPercent() {
        assertEquals(Integer.valueOf(25), new CrawlProgress("W1", "site", 25, 100, 0L).getPercent());
        assertNull(new CrawlProgress("W1", "site", 25, 0, 0L).getPercent());
    }

    @Test
    public void test_getters() {
        final CrawlProgress progress = new CrawlProgress("F2", "files", 7, 70, 1234L);
        assertEquals("F2", progress.getConfigId());
        assertEquals("files", progress.getName());
        assertEquals(7L, progress.getCount());
        assertEquals(70L, progress.getLastCount());
        assertEquals(1234L, progress.getUpdatedTime());
    }

    @Test
    public void test_calculatePercent_doesNotOverflow() {
        // count * 100 wraps to a negative number here, and the page would render width:-N%
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(Long.MAX_VALUE, 1));
        assertEquals(Integer.valueOf(99), CrawlProgress.calculatePercent(Long.MAX_VALUE / 50, 1));
        assertEquals(Integer.valueOf(2), CrawlProgress.calculatePercent(Long.MAX_VALUE / 50, Long.MAX_VALUE));
        assertEquals(Integer.valueOf(50), CrawlProgress.calculatePercent(Long.MAX_VALUE / 2, Long.MAX_VALUE));
        for (final long count : new long[] { 0, 1, 99, 100, 1L << 40, Long.MAX_VALUE / 100, Long.MAX_VALUE / 99, Long.MAX_VALUE }) {
            for (final long last : new long[] { 1, 100, 1L << 50, Long.MAX_VALUE }) {
                final int percent = CrawlProgress.calculatePercent(count, last);
                assertTrue(percent >= 0 && percent <= CrawlProgress.MAX_RUNNING_PERCENT, count + "/" + last);
            }
        }
    }

    @Test
    public void test_negativeCountsAreTakenAsZero() {
        final CrawlProgress progress = new CrawlProgress("W1", "site", -5, -10, 0L);
        assertEquals(0L, progress.getCount());
        assertEquals(0L, progress.getLastCount());
        assertNull(progress.getPercent());
        assertEquals(Integer.valueOf(0), new CrawlProgress("W1", "site", -5, 100, 0L).getPercent());
    }
}
