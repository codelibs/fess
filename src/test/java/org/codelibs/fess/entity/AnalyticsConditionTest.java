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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AnalyticsConditionTest extends UnitFessTestCase {
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-15T10:30:00Z"), JST);

    private AnalyticsCondition create(String range, String from, String to) {
        return AnalyticsCondition.create(range, from, to, null, null, null, 10, clock);
    }

    @Test
    public void test_default28d() {
        final AnalyticsCondition c = create(null, null, null);
        assertEquals("28d", c.getRange());
        assertEquals(ZonedDateTime.of(2026, 8, 19, 0, 0, 0, 0, JST), c.getStart());
        assertEquals(ZonedDateTime.of(2026, 9, 15, 19, 30, 0, 0, JST), c.getEnd());
        assertEquals(AnalyticsCondition.Interval.DAY, c.getInterval());
        assertFalse(c.isAdjusted());
        assertEquals("2026-08-19", c.getFrom());
        assertEquals("2026-09-15", c.getTo());
        assertEquals(10, c.getSize());
        assertEquals(ZonedDateTime.of(2026, 7, 22, 0, 0, 0, 0, JST), c.getPrevStart());
        assertEquals(c.getEnd().minusDays(28), c.getPrevEnd());
    }

    @Test
    public void test_today_and_yesterday() {
        final AnalyticsCondition today = create("today", null, null);
        assertEquals(ZonedDateTime.of(2026, 9, 15, 0, 0, 0, 0, JST), today.getStart());
        assertEquals(ZonedDateTime.of(2026, 9, 16, 0, 0, 0, 0, JST), today.getEnd());
        assertEquals(AnalyticsCondition.Interval.HOUR, today.getInterval());
        final AnalyticsCondition yesterday = create("yesterday", null, null);
        assertEquals(ZonedDateTime.of(2026, 9, 14, 0, 0, 0, 0, JST), yesterday.getStart());
        assertEquals(ZonedDateTime.of(2026, 9, 15, 0, 0, 0, 0, JST), yesterday.getEnd());
        assertEquals(ZonedDateTime.of(2026, 9, 13, 0, 0, 0, 0, JST), yesterday.getPrevStart());
        assertEquals(yesterday.getStart(), yesterday.getPrevEnd());
    }

    @Test
    public void test_7d_and_90d() {
        assertEquals(ZonedDateTime.of(2026, 9, 9, 0, 0, 0, 0, JST), create("7d", null, null).getStart());
        assertEquals(ZonedDateTime.of(2026, 6, 18, 0, 0, 0, 0, JST), create("90d", null, null).getStart());
    }

    @Test
    public void test_custom() {
        final AnalyticsCondition c = create("custom", "2026-09-01", "2026-09-02");
        assertEquals(ZonedDateTime.of(2026, 9, 1, 0, 0, 0, 0, JST), c.getStart());
        assertEquals(ZonedDateTime.of(2026, 9, 3, 0, 0, 0, 0, JST), c.getEnd());
        assertEquals("interval is HOUR at exactly 2 days", AnalyticsCondition.Interval.HOUR, c.getInterval());
        assertEquals(ZonedDateTime.of(2026, 8, 30, 0, 0, 0, 0, JST), c.getPrevStart());
        assertEquals("2026-09-02", c.getTo());
        assertEquals(AnalyticsCondition.Interval.DAY, create("custom", "2026-09-01", "2026-09-03").getInterval());
    }

    @Test
    public void test_invalidInputsFallBack() {
        for (final String[] in : new String[][] { { "abc", null, null }, { "custom", "2026-13-40", "2026-09-01" },
                { "custom", "2026-09-10", "2026-09-01" }, { "custom", "2025-01-01", "2026-09-01" }, { "custom", null, "2026-09-01" } }) {
            final AnalyticsCondition c = create(in[0], in[1], in[2]);
            assertEquals("28d", c.getRange()); // input: in[0]/in[1]/in[2]
            assertTrue(c.isAdjusted());
        }
        final AnalyticsCondition max = create("custom", "2025-09-15", "2026-09-15");
        assertEquals("custom", max.getRange()); // 366 days is allowed
        assertFalse(max.isAdjusted());

        final AnalyticsCondition over = create("custom", "2025-09-14", "2026-09-15");
        assertEquals("28d", over.getRange()); // 367 days exceeds MAX_DAYS
        assertTrue(over.isAdjusted());
    }

    @Test
    public void test_sizeCompareAccessType() {
        assertEquals(25, AnalyticsCondition.create(null, null, null, null, null, "25", 10, clock).getSize());
        final AnalyticsCondition bad = AnalyticsCondition.create(null, null, null, null, null, "7", 10, clock);
        assertEquals(10, bad.getSize());
        assertEquals(10, AnalyticsCondition.create(null, null, null, null, null, "x", 10, clock).getSize());
        assertTrue(AnalyticsCondition.create(null, null, null, "true", "  ", null, 10, clock).isCompare());
        assertNull(AnalyticsCondition.create(null, null, null, "true", "  ", null, 10, clock).getAccessType(), "blank is null");
        assertEquals("json", AnalyticsCondition.create(null, null, null, null, "json", null, 10, clock).getAccessType());
    }

    @Test
    public void test_toUtc() {
        assertEquals(LocalDateTime.of(2026, 9, 14, 15, 0), AnalyticsCondition.toUtc(ZonedDateTime.of(2026, 9, 15, 0, 0, 0, 0, JST)));
    }

    @Test
    public void test_custom_dst_fallback() {
        final ZoneId ny = ZoneId.of("America/New_York");
        final Clock nyClock = Clock.fixed(Instant.parse("2026-11-03T10:30:00Z"), ny);
        final AnalyticsCondition c = AnalyticsCondition.create("custom", "2026-11-01", "2026-11-02", null, null, null, 10, nyClock);
        assertEquals(ZonedDateTime.of(2026, 11, 1, 0, 0, 0, 0, ny), c.getStart());
        assertEquals(ZonedDateTime.of(2026, 11, 3, 0, 0, 0, 0, ny), c.getEnd());
        assertEquals("HOUR interval for 2 calendar days across DST", AnalyticsCondition.Interval.HOUR, c.getInterval());
        assertEquals(ZonedDateTime.of(2026, 10, 30, 0, 0, 0, 0, ny), c.getPrevStart());
    }

    @Test
    public void test_7d_dst_prevStart_is_midnight() {
        final ZoneId ny = ZoneId.of("America/New_York");
        final Clock nyClock = Clock.fixed(Instant.parse("2026-11-04T17:00:00Z"), ny);
        final AnalyticsCondition c = AnalyticsCondition.create("7d", null, null, null, null, null, 10, nyClock);
        assertEquals(AnalyticsCondition.Interval.DAY, c.getInterval());
        assertEquals(0, c.getPrevStart().getHour());
        assertEquals(0, c.getPrevStart().getMinute());
        assertEquals(c.getEnd().minusDays(7), c.getPrevEnd());
    }
}
