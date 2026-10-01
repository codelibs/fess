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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.core.lang.StringUtil;

/**
 * Value object representing analytics period condition, including period selection,
 * comparison period, and chart interval calculation for search log analytics dashboard.
 */
public class AnalyticsCondition {
    /**
     * Enum representing the interval for aggregating analytics data.
     */
    public enum Interval {
        /** Hour-level aggregation */
        HOUR,
        /** Day-level aggregation */
        DAY
    }

    /** Preset range: today */
    public static final String RANGE_TODAY = "today";
    /** Preset range: yesterday */
    public static final String RANGE_YESTERDAY = "yesterday";
    /** Preset range: last 7 days */
    public static final String RANGE_7D = "7d";
    /** Preset range: last 28 days */
    public static final String RANGE_28D = "28d";
    /** Preset range: last 90 days */
    public static final String RANGE_90D = "90d";
    /** Custom range */
    public static final String RANGE_CUSTOM = "custom";

    /**
     * Ordered list of all valid range presets.
     */
    public static final List<String> RANGES =
            Collections.unmodifiableList(Arrays.asList(RANGE_TODAY, RANGE_YESTERDAY, RANGE_7D, RANGE_28D, RANGE_90D, RANGE_CUSTOM));

    /**
     * Valid table size options.
     */
    public static final List<Integer> SIZES = Collections.unmodifiableList(Arrays.asList(10, 25, 50, 100));

    /** Maximum number of days allowed for custom range */
    public static final int MAX_DAYS = 366;

    private String range;
    private String from;
    private String to;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime prevStart;
    private ZonedDateTime prevEnd;
    private Interval interval;
    private boolean compare;
    private String accessType;
    private int size;
    private boolean adjusted;
    private ZoneId zoneId;

    /**
     * Creates an AnalyticsCondition from the given parameters.
     * Resolves period (preset or custom), comparison period, and chart interval.
     *
     * @param range the range preset name or "custom" (null defaults to 28d)
     * @param from the start date for custom range in yyyy-MM-dd format (ignored for presets)
     * @param to the end date for custom range in yyyy-MM-dd format (ignored for presets)
     * @param compare "true" to enable comparison period (case-insensitive, null/blank = false)
     * @param accessType the access type filter (null when blank)
     * @param size the table size as string (must be in SIZES list, else defaultSize)
     * @param defaultSize the default size when size parameter is invalid
     * @param clock the clock for determining current time and zone
     * @return a new AnalyticsCondition instance
     */
    public static AnalyticsCondition create(final String range, final String from, final String to, final String compare,
            final String accessType, final String size, final int defaultSize, final Clock clock) {
        final AnalyticsCondition c = new AnalyticsCondition();
        c.zoneId = clock.getZone();
        final ZonedDateTime now = ZonedDateTime.now(clock);
        final ZonedDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        c.compare = "true".equalsIgnoreCase(StringUtil.isNotEmpty(compare) ? compare.trim() : null);
        c.accessType = StringUtil.isBlank(accessType) ? null : accessType.trim();
        c.size = parseSize(size, defaultSize);
        c.range = StringUtil.isBlank(range) ? RANGE_28D : range.trim();
        if (!c.applyRange(today, now, from, to)) {
            c.adjusted = true;
            c.range = RANGE_28D;
            c.applyRange(today, now, null, null);
        }
        final long dayCount = ChronoUnit.DAYS.between(c.start.toLocalDate(), c.end.minusNanos(1).toLocalDate()) + 1;
        c.interval = dayCount <= 2 ? Interval.HOUR : Interval.DAY;
        c.prevEnd = c.end.minusDays(dayCount);
        c.prevStart = c.start.minusDays(dayCount);
        c.from = c.start.toLocalDate().toString();
        c.to = c.end.minusNanos(1).toLocalDate().toString();
        return c;
    }

    /**
     * Applies the range configuration to set start and end times.
     *
     * @param today the current day at midnight in the target zone
     * @param now the current moment in the target zone
     * @param from the start date for custom range (yyyy-MM-dd format)
     * @param to the end date for custom range (yyyy-MM-dd format)
     * @return true if the range was successfully applied, false if invalid
     */
    private boolean applyRange(final ZonedDateTime today, final ZonedDateTime now, final String from, final String to) {
        switch (range) {
        case RANGE_TODAY -> {
            start = today;
            end = today.plusDays(1);
        }
        case RANGE_YESTERDAY -> {
            start = today.minusDays(1);
            end = today;
        }
        case RANGE_7D -> {
            start = today.minusDays(6);
            end = now;
        }
        case RANGE_28D -> {
            start = today.minusDays(27);
            end = now;
        }
        case RANGE_90D -> {
            start = today.minusDays(89);
            end = now;
        }
        case RANGE_CUSTOM -> {
            try {
                final LocalDate f = LocalDate.parse(from);
                final LocalDate t = LocalDate.parse(to);
                if (t.isBefore(f) || ChronoUnit.DAYS.between(f, t) + 1 > MAX_DAYS) {
                    return false;
                }
                start = f.atStartOfDay(zoneId);
                end = t.plusDays(1).atStartOfDay(zoneId);
            } catch (final Exception e) {
                return false;
            }
        }
        default -> {
            return false;
        }
        }
        return true;
    }

    /**
     * Converts a ZonedDateTime to UTC LocalDateTime.
     *
     * @param dateTime the ZonedDateTime to convert
     * @return the equivalent LocalDateTime in UTC
     */
    public static LocalDateTime toUtc(final ZonedDateTime dateTime) {
        return dateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Parses a size string and returns the value if it is in the SIZES list,
     * otherwise returns the default size.
     *
     * @param size the size as a string
     * @param defaultSize the default size to use if parsing fails or value is not in SIZES
     * @return the parsed size or defaultSize
     */
    private static int parseSize(final String size, final int defaultSize) {
        if (StringUtil.isBlank(size)) {
            return defaultSize;
        }
        try {
            final int parsed = Integer.parseInt(size.trim());
            return SIZES.contains(parsed) ? parsed : defaultSize;
        } catch (final NumberFormatException e) {
            return defaultSize;
        }
    }

    /**
     * Gets the normalized range preset name.
     *
     * @return the range preset (e.g., "28d", "custom")
     */
    public String getRange() {
        return range;
    }

    /**
     * Gets the start date as a string for re-rendering forms.
     *
     * @return the start date in yyyy-MM-dd format
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the last included day as a string for re-rendering forms.
     *
     * @return the end date in yyyy-MM-dd format
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the inclusive start time of the period.
     *
     * @return the start ZonedDateTime
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**
     * Gets the exclusive end time of the period.
     *
     * @return the end ZonedDateTime
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**
     * Gets the inclusive start time of the comparison period.
     * The comparison period spans the same number of calendar days as the main period,
     * aligned to local midnight boundaries.
     *
     * @return the previous period start ZonedDateTime at local midnight
     */
    public ZonedDateTime getPrevStart() {
        return prevStart;
    }

    /**
     * Gets the exclusive end time of the comparison period.
     * The comparison period spans the same number of calendar days as the main period,
     * aligned to local midnight boundaries.
     *
     * @return the previous period end ZonedDateTime at local midnight
     */
    public ZonedDateTime getPrevEnd() {
        return prevEnd;
    }

    /**
     * Gets the chart interval for aggregating data based on calendar day count.
     * Uses calendar day boundaries (not elapsed duration) to handle DST correctly.
     *
     * @return Interval.HOUR for periods spanning 2 calendar days or less, Interval.DAY otherwise
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Checks if comparison period is enabled.
     *
     * @return true if comparison period should be included
     */
    public boolean isCompare() {
        return compare;
    }

    /**
     * Gets the access type filter.
     *
     * @return the access type, or null when not specified
     */
    public String getAccessType() {
        return accessType;
    }

    /**
     * Gets the table size for pagination.
     *
     * @return the number of items per page
     */
    public int getSize() {
        return size;
    }

    /**
     * Checks if any input parameter was adjusted due to being invalid.
     *
     * @return true if range, from, or to were replaced with defaults
     */
    public boolean isAdjusted() {
        return adjusted;
    }

    /**
     * Gets the time zone used for this condition.
     *
     * @return the ZoneId
     */
    public ZoneId getZoneId() {
        return zoneId;
    }
}
