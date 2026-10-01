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
package org.codelibs.fess.app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class SearchLogServiceTest extends UnitFessTestCase {
    private static LocalDateTime utc(final LocalDateTime local) {
        return local.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    @Test
    public void test_parseRequestedTimeRange_bothEndsConverted() {
        // a non-UTC default zone makes the conversion assertions meaningful on any JVM
        final TimeZone original = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
        try {
            final LocalDateTime[] r = new SearchLogService().parseRequestedTimeRange("2026-09-01 00:00 - 2026-09-02 12:30");
            assertEquals(LocalDateTime.of(2026, 8, 31, 15, 0), r[0]);
            assertEquals(LocalDateTime.of(2026, 9, 2, 3, 30), r[1]);
            assertEquals(utc(LocalDateTime.of(2026, 9, 1, 0, 0)), r[0]);
            assertEquals(utc(LocalDateTime.of(2026, 9, 2, 12, 30)), r[1]);
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    public void test_parseRequestedTimeRange_partialAndInvalid() {
        final SearchLogService service = new SearchLogService();
        final LocalDateTime[] startOnly = service.parseRequestedTimeRange("2026-09-01 00:00");
        assertEquals(utc(LocalDateTime.of(2026, 9, 1, 0, 0)), startOnly[0]);
        assertNull(startOnly[1], "no end");
        final LocalDateTime[] bad = service.parseRequestedTimeRange("yesterday - tomorrow");
        assertNull(bad[0], "unparsable start");
        assertNull(bad[1], "unparsable end");
        final LocalDateTime[] blank = service.parseRequestedTimeRange("");
        assertNull(blank[0], "blank");
    }
}
