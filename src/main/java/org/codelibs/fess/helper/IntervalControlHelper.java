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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Helper class for controlling crawler execution intervals and timing.
 * This class manages crawler execution timing based on configurable rules
 * that can specify different delays for different time periods and days.
 */
public class IntervalControlHelper {

    /** Flag indicating whether the crawler is currently running */
    protected volatile boolean crawlerRunning = true;

    /**
     * Default constructor.
     */
    public IntervalControlHelper() {
        // Default constructor
    }

    /** Wait time in milliseconds when crawler is not running */
    protected long crawlerWaitMillis = 10000;

    /** List of interval rules for controlling crawler timing */
    protected List<IntervalRule> ruleList = new ArrayList<>();

    /**
     * Checks the crawler status and waits if the crawler is not running.
     * This method blocks until the crawler is running again.
     */
    public void checkCrawlerStatus() {
        while (!crawlerRunning) {
            ThreadUtil.sleepQuietly(crawlerWaitMillis);
        }
    }

    /**
     * Applies delay based on the configured interval rules.
     * This method calculates the appropriate delay for the current time
     * and applies it by sleeping the current thread.
     */
    public void delayByRules() {
        final long delay = getDelay();
        if (delay > 0) {
            ThreadUtil.sleep(delay);
        }
    }

    /**
     * Calculates the delay in milliseconds based on current time and configured rules.
     * The method checks each rule to see if it applies to the current time and day.
     *
     * @return the delay in milliseconds, or 0 if no rules apply
     */
    protected long getDelay() {
        if (ruleList.isEmpty()) {
            return 0;
        }
        final Calendar cal = getCurrentCal();
        final int h = cal.get(Calendar.HOUR_OF_DAY);
        final int m = cal.get(Calendar.MINUTE);
        final int d = cal.get(Calendar.DAY_OF_WEEK); // SUN(1) - SAT(7)
        for (final IntervalRule rule : ruleList) {
            if (rule.isTarget(h, m, d)) {
                return rule.getDelay();
            }
        }
        return 0;
    }

    /**
     * Gets the current calendar instance set to the system time.
     *
     * @return the current calendar instance
     */
    protected Calendar getCurrentCal() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        return cal;
    }

    /**
     * Adds a new interval rule to the rule list.
     *
     * @param from the start time in HH:MM format
     * @param to the end time in HH:MM format
     * @param days comma-separated list of days (1=Sunday, 7=Saturday)
     * @param delay the delay in milliseconds to apply during this interval
     */
    public void addIntervalRule(final String from, final String to, final String days, final long delay) {
        ruleList.add(new IntervalRule(from, to, days, delay));
    }

    /**
     * Checks if the crawler is currently running.
     *
     * @return true if the crawler is running, false otherwise
     */
    public boolean isCrawlerRunning() {
        return crawlerRunning;
    }

    /**
     * Sets the crawler running status.
     *
     * @param crawlerRunning true to indicate the crawler is running, false otherwise
     */
    public void setCrawlerRunning(final boolean crawlerRunning) {
        this.crawlerRunning = crawlerRunning;
    }

    /**
     * Represents a rule for controlling crawler intervals.
     * Each rule defines a time range, applicable days, and delay amount.
     */
    public static class IntervalRule {
        /** Starting hour of the interval */
        protected int fromHours;

        /** Starting minute of the interval */
        protected int fromMinutes;

        /** Ending hour of the interval */
        protected int toHours;

        /** Ending minute of the interval */
        protected int toMinutes;

        /** Delay in milliseconds to apply during this interval */
        protected long delay;

        /** Array of days when this rule applies (1=Sunday, 7=Saturday) */
        protected int[] days;

        /** Flag indicating if the interval spans across midnight */
        protected boolean reverse;

        /**
         * Creates a new interval rule.
         *
         * @param from the start time in HH:MM format
         * @param to the end time in HH:MM format
         * @param days comma-separated list of days (1=Sunday, 7=Saturday)
         * @param delay the delay in milliseconds to apply during this interval
         */
        public IntervalRule(final String from, final String to, final String days, final long delay) {
            final int[] fints = parseTime(from);
            fromHours = fints[0];
            fromMinutes = fints[1];
            final int[] tints = parseTime(to);
            toHours = tints[0];
            toMinutes = tints[1];
            final String[] values = days.split(",");
            final List<Integer> list = new ArrayList<>();
            for (final String value : values) {
                try {
                    list.add(Integer.parseInt(value.trim()));
                } catch (final NumberFormatException e) {}
            }
            this.days = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                this.days[i] = list.get(i);
            }
            this.delay = delay;
            reverse = compareTime(fromHours, fromMinutes, toHours, toMinutes) < 0;
        }

        /**
         * Gets the delay amount for this rule.
         *
         * @return the delay in milliseconds
         */
        public long getDelay() {
            return delay;
        }

        /**
         * Checks if this rule applies to the given time and day.
         *
         * @param hours the hour of the day (0-23)
         * @param minutes the minute of the hour (0-59)
         * @param day the day of the week (1=Sunday, 7=Saturday)
         * @return true if this rule applies, false otherwise
         */
        public boolean isTarget(final int hours, final int minutes, final int day) {
            if (!reverse) {
                return compareTime(fromHours, fromMinutes, hours, minutes) >= 0 && compareTime(hours, minutes, toHours, toMinutes) >= 0
                        && isInDays(day);
            }
            if (compareTime(hours, minutes, toHours, toMinutes) >= 0 && isInDays(day + 1)
                    || compareTime(fromHours, fromMinutes, hours, minutes) >= 0 && isInDays(day)) {
                return true;
            }
            return false;
        }

        /**
         * Checks if the given day is included in this rule's applicable days.
         *
         * @param day the day of the week (1=Sunday, 7=Saturday)
         * @return true if the day is included, false otherwise
         */
        private boolean isInDays(final int day) {
            if (days.length == 0) {
                return true;
            }
            int value;
            if (day == 8) {
                value = 1;
            } else {
                value = day;
            }
            for (final int d : days) {
                if (d == value) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Compares two times.
         *
         * @param h1 the first hour
         * @param m1 the first minute
         * @param h2 the second hour
         * @param m2 the second minute
         * @return positive if first time is earlier, 0 if equal, negative if later
         */
        protected int compareTime(final int h1, final int m1, final int h2, final int m2) {
            if (h1 < h2) {
                return 1;
            }
            if (h1 == h2) {
                if (m1 == m2) {
                    return 0;
                }
                if (m1 < m2) {
                    return 1;
                }
            }
            return -1;
        }
    }

    /**
     * Parses a time string in HH:MM format.
     *
     * @param time the time string to parse
     * @return an array containing [hour, minute]
     * @throws FessSystemException if the time format is invalid
     */
    protected static int[] parseTime(final String time) {
        final String[] froms = time.split(":");
        if (froms.length != 2) {
            throw new FessSystemException("Invalid format: " + time);
        }
        final int[] values = new int[2];
        values[0] = Integer.parseInt(froms[0]);
        if (values[0] < 0 || values[0] > 23) {
            throw new FessSystemException("Invalid format: " + time);
        }
        values[1] = Integer.parseInt(froms[1]);
        if (values[1] < 0 || values[1] > 59) {
            throw new FessSystemException("Invalid format: " + time);
        }
        return values;
    }

    /**
     * Sets the wait time in milliseconds when the crawler is not running.
     *
     * @param crawlerWaitMillis the wait time in milliseconds
     */
    public void setCrawlerWaitMillis(final long crawlerWaitMillis) {
        this.crawlerWaitMillis = crawlerWaitMillis;
    }

}
