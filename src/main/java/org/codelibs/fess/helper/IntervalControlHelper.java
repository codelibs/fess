/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class IntervalControlHelper {

    protected volatile boolean crawlerRunning = true;

    protected long crawlerWaitMillis = 10000;

    protected List<IntervalRule> ruleList = new ArrayList<>();

    public void checkCrawlerStatus() {
        while (!crawlerRunning) {
            ThreadUtil.sleepQuietly(crawlerWaitMillis);
        }
    }

    public void delayByRules() {
        final long delay = getDelay();
        if (delay > 0) {
            ThreadUtil.sleep(delay);
        }
    }

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

    protected Calendar getCurrentCal() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal;
    }

    public void addIntervalRule(final String from, final String to, final String days, final long delay) {
        ruleList.add(new IntervalRule(from, to, days, delay));
    }

    public boolean isCrawlerRunning() {
        return crawlerRunning;
    }

    public void setCrawlerRunning(final boolean crawlerRunning) {
        this.crawlerRunning = crawlerRunning;
    }

    public static class IntervalRule {
        protected int fromHours;

        protected int fromMinutes;

        protected int toHours;

        protected int toMinutes;

        protected long delay;

        protected int[] days;

        protected boolean reverse;

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

        public long getDelay() {
            return delay;
        }

        public boolean isTarget(final int hours, final int minutes, final int day) {
            if (!reverse) {
                return compareTime(fromHours, fromMinutes, hours, minutes) >= 0 && compareTime(hours, minutes, toHours, toMinutes) >= 0
                        && isInDays(day);
            }
            if ((compareTime(hours, minutes, toHours, toMinutes) >= 0 && isInDays(day + 1))
                    || (compareTime(fromHours, fromMinutes, hours, minutes) >= 0 && isInDays(day))) {
                return true;
            }
            return false;
        }

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

    public void setCrawlerWaitMillis(final long crawlerWaitMillis) {
        this.crawlerWaitMillis = crawlerWaitMillis;
    }

}
