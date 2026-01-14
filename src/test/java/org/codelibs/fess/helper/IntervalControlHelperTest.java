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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.IntervalControlHelper.IntervalRule;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class IntervalControlHelperTest extends UnitFessTestCase {

    private IntervalControlHelper intervalControlHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        intervalControlHelper = new IntervalControlHelper();
    }

    @Test
    public void test_noRule() {
        assertEquals(0, intervalControlHelper.getDelay());
    }

    @Test
    public void test_0000() throws ParseException {
        final IntervalControlHelper intervalControlHelper = createHelper("00:00", 1);
        intervalControlHelper.addIntervalRule("01:30", "15:15", "*", 1000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("16:30", "0:15", "*", 2000);
        assertEquals(2000, intervalControlHelper.getDelay());

        intervalControlHelper.ruleList.clear();

        intervalControlHelper.addIntervalRule("22:15", "2:45", "1", 3000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("22:15", "2:45", "2", 4000);
        assertEquals(4000, intervalControlHelper.getDelay());
    }

    @Test
    public void test_1215() throws ParseException {
        final IntervalControlHelper intervalControlHelper = createHelper("12:15", 1);
        intervalControlHelper.addIntervalRule("01:30", "12:05", "*", 1000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("12:30", "23:15", "*", 1000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("12:05", "12:15", "*", 2000);
        assertEquals(2000, intervalControlHelper.getDelay());

        intervalControlHelper.ruleList.clear();

        intervalControlHelper.addIntervalRule("12:15", "12:15", "*", 3000);
        assertEquals(3000, intervalControlHelper.getDelay());

        intervalControlHelper.ruleList.clear();

        intervalControlHelper.addIntervalRule("12:15", "12:15", "3,5", 4000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("12:10", "12:20", "1", 5000);
        assertEquals(5000, intervalControlHelper.getDelay());

    }

    @Test
    public void test_2250() throws ParseException {
        final IntervalControlHelper intervalControlHelper = createHelper("22:50", 1);
        intervalControlHelper.addIntervalRule("01:30", "15:15", "*", 1000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("16:30", "0:15", "*", 2000);
        assertEquals(2000, intervalControlHelper.getDelay());

        intervalControlHelper.ruleList.clear();

        intervalControlHelper.addIntervalRule("22:15", "2:45", "2", 3000);
        assertEquals(0, intervalControlHelper.getDelay());

        intervalControlHelper.addIntervalRule("22:15", "2:45", "1", 4000);
        assertEquals(4000, intervalControlHelper.getDelay());
    }

    private IntervalControlHelper createHelper(final String time, final int day) throws ParseException {
        final Date date = new SimpleDateFormat("HH:mm").parse(time);
        return new IntervalControlHelper() {
            @Override
            protected Calendar getCurrentCal() {
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date.getTime());
                cal.set(Calendar.DAY_OF_WEEK, day);
                return cal;
            }
        };
    }

    @Test
    public void test_checkCrawlerStatus() throws InterruptedException {
        IntervalControlHelper helper = new IntervalControlHelper();
        helper.setCrawlerWaitMillis(50);

        // Test when crawler is running
        assertTrue(helper.isCrawlerRunning());
        long start = System.currentTimeMillis();
        helper.checkCrawlerStatus();
        long end = System.currentTimeMillis();
        assertTrue(end - start < 100); // Should return quickly

        // Test when crawler is not running
        helper.setCrawlerRunning(false);
        assertFalse(helper.isCrawlerRunning());

        // Start a thread to set crawler running after a delay
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                helper.setCrawlerRunning(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();

        start = System.currentTimeMillis();
        helper.checkCrawlerStatus();
        end = System.currentTimeMillis();
        assertTrue(end - start >= 100); // Should wait until crawler is running
        thread.join();
    }

    @Test
    public void test_delayByRules() throws ParseException {
        IntervalControlHelper helper = createHelper("12:15", 1);

        // Test with no rules
        long start = System.currentTimeMillis();
        helper.delayByRules();
        long end = System.currentTimeMillis();
        assertTrue(end - start < 50); // Should return quickly

        // Test with rule that has no delay
        helper.addIntervalRule("01:30", "15:15", "*", 0);
        start = System.currentTimeMillis();
        helper.delayByRules();
        end = System.currentTimeMillis();
        assertTrue(end - start < 50); // Should return quickly

        // Test with rule that has delay (we'll use a very short delay for testing)
        helper.ruleList.clear();
        helper.addIntervalRule("12:00", "13:00", "*", 10);
        start = System.currentTimeMillis();
        helper.delayByRules();
        end = System.currentTimeMillis();
        assertTrue(end - start >= 10); // Should wait for delay
    }

    @Test
    public void test_setCrawlerRunning() {
        IntervalControlHelper helper = new IntervalControlHelper();

        // Test default state
        assertTrue(helper.isCrawlerRunning());

        // Test setting to false
        helper.setCrawlerRunning(false);
        assertFalse(helper.isCrawlerRunning());

        // Test setting back to true
        helper.setCrawlerRunning(true);
        assertTrue(helper.isCrawlerRunning());
    }

    @Test
    public void test_setCrawlerWaitMillis() {
        IntervalControlHelper helper = new IntervalControlHelper();

        // Test default wait time
        assertEquals(10000, helper.crawlerWaitMillis);

        // Test setting new wait time
        helper.setCrawlerWaitMillis(5000);
        assertEquals(5000, helper.crawlerWaitMillis);

        helper.setCrawlerWaitMillis(0);
        assertEquals(0, helper.crawlerWaitMillis);
    }

    @Test
    public void test_parseTime_validFormat() {
        int[] result = IntervalControlHelper.parseTime("12:30");
        assertEquals(12, result[0]);
        assertEquals(30, result[1]);

        result = IntervalControlHelper.parseTime("00:00");
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);

        result = IntervalControlHelper.parseTime("23:59");
        assertEquals(23, result[0]);
        assertEquals(59, result[1]);
    }

    @Test
    public void test_parseTime_invalidFormat() {
        try {
            IntervalControlHelper.parseTime("12");
            fail("Should throw FessSystemException");
        } catch (FessSystemException e) {
            assertEquals("Invalid time format: 12. Expected format: HH:MM", e.getMessage());
        }

        try {
            IntervalControlHelper.parseTime("12:30:45");
            fail("Should throw FessSystemException");
        } catch (FessSystemException e) {
            assertEquals("Invalid time format: 12:30:45. Expected format: HH:MM", e.getMessage());
        }

        try {
            IntervalControlHelper.parseTime("24:00");
            fail("Should throw FessSystemException");
        } catch (FessSystemException e) {
            assertEquals("Invalid hour value: 24 in time: 24:00. Hour must be between 0 and 23", e.getMessage());
        }

        try {
            IntervalControlHelper.parseTime("12:60");
            fail("Should throw FessSystemException");
        } catch (FessSystemException e) {
            assertEquals("Invalid minute value: 60 in time: 12:60. Minute must be between 0 and 59", e.getMessage());
        }

        try {
            IntervalControlHelper.parseTime("-1:30");
            fail("Should throw FessSystemException");
        } catch (FessSystemException e) {
            assertEquals("Invalid hour value: -1 in time: -1:30. Hour must be between 0 and 23", e.getMessage());
        }
    }

    @Test
    public void test_intervalRule_getDelay() {
        IntervalRule rule = new IntervalRule("10:00", "18:00", "*", 5000);
        assertEquals(5000, rule.getDelay());

        rule = new IntervalRule("10:00", "18:00", "*", 0);
        assertEquals(0, rule.getDelay());
    }

    @Test
    public void test_intervalRule_isTarget_sameDay() {
        IntervalRule rule = new IntervalRule("10:00", "18:00", "*", 1000);

        // Within range
        assertTrue(rule.isTarget(12, 30, 1)); // 12:30 on Sunday
        assertTrue(rule.isTarget(10, 0, 1)); // 10:00 on Sunday (start time)
        assertTrue(rule.isTarget(18, 0, 1)); // 18:00 on Sunday (end time)

        // Outside range
        assertFalse(rule.isTarget(9, 59, 1)); // 9:59 on Sunday
        assertFalse(rule.isTarget(18, 1, 1)); // 18:01 on Sunday
    }

    @Test
    public void test_intervalRule_isTarget_crossMidnight() {
        IntervalRule rule = new IntervalRule("22:00", "02:00", "*", 1000);

        // Within range (same day)
        assertTrue(rule.isTarget(23, 30, 1)); // 23:30 on Sunday
        assertTrue(rule.isTarget(22, 0, 1)); // 22:00 on Sunday (start time)

        // Within range (next day)
        assertTrue(rule.isTarget(1, 30, 1)); // 1:30 on Sunday (actually Monday morning)
        assertTrue(rule.isTarget(2, 0, 1)); // 2:00 on Sunday (actually Monday morning)

        // Outside range
        assertFalse(rule.isTarget(21, 59, 1)); // 21:59 on Sunday
        assertFalse(rule.isTarget(2, 1, 1)); // 2:01 on Sunday (actually Monday morning)
    }

    @Test
    public void test_intervalRule_isTarget_specificDays() {
        IntervalRule rule = new IntervalRule("10:00", "18:00", "1,3,5", 1000); // Sunday, Tuesday, Thursday

        // Within time range and matching day
        assertTrue(rule.isTarget(12, 30, 1)); // 12:30 on Sunday
        assertTrue(rule.isTarget(12, 30, 3)); // 12:30 on Tuesday
        assertTrue(rule.isTarget(12, 30, 5)); // 12:30 on Thursday

        // Within time range but not matching day
        assertFalse(rule.isTarget(12, 30, 2)); // 12:30 on Monday
        assertFalse(rule.isTarget(12, 30, 4)); // 12:30 on Wednesday
        assertFalse(rule.isTarget(12, 30, 6)); // 12:30 on Friday
        assertFalse(rule.isTarget(12, 30, 7)); // 12:30 on Saturday
    }

    @Test
    public void test_intervalRule_isTarget_invalidDays() {
        IntervalRule rule = new IntervalRule("10:00", "18:00", "1,invalid,3", 1000);

        // Should still work with valid days
        assertTrue(rule.isTarget(12, 30, 1)); // 12:30 on Sunday
        assertTrue(rule.isTarget(12, 30, 3)); // 12:30 on Tuesday
        assertFalse(rule.isTarget(12, 30, 2)); // 12:30 on Monday
    }

    @Test
    public void test_intervalRule_isTarget_dayOverflow() {
        IntervalRule rule = new IntervalRule("10:00", "18:00", "1", 1000); // Sunday only

        // Test day overflow (day 8 should be treated as day 1)
        // For same-day rules, it should check the day directly
        assertTrue(rule.isTarget(12, 30, 8)); // Should be treated as day 1 (Sunday)

        // Test with cross-midnight rule
        IntervalRule crossRule = new IntervalRule("22:00", "02:00", "1", 1000); // Sunday only
        // For cross-midnight rule, when checking early morning hours (1:30), it checks day+1
        // day=8 becomes day=1, so day+1=2, which is Monday (not Sunday)
        assertFalse(crossRule.isTarget(1, 30, 8)); // Should be treated as day 1 (Sunday), checking day+1=2 (Monday)
    }

    @Test
    public void test_addIntervalRule_multipleDays() {
        IntervalControlHelper helper = new IntervalControlHelper();
        helper.addIntervalRule("10:00", "18:00", "1,2,3,4,5", 1000);

        assertEquals(1, helper.ruleList.size());
        IntervalRule rule = helper.ruleList.get(0);
        assertEquals(1000, rule.getDelay());
    }

    @Test
    public void test_addIntervalRule_emptyDays() {
        IntervalControlHelper helper = new IntervalControlHelper();
        helper.addIntervalRule("10:00", "18:00", "", 1000);

        assertEquals(1, helper.ruleList.size());
        IntervalRule rule = helper.ruleList.get(0);
        // Should match any day when days is empty
        assertTrue(rule.isTarget(12, 30, 1));
        assertTrue(rule.isTarget(12, 30, 7));
    }
}
