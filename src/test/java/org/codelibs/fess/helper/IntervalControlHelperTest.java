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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codelibs.fess.unit.UnitFessTestCase;

public class IntervalControlHelperTest extends UnitFessTestCase {

    private IntervalControlHelper intervalControlHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        intervalControlHelper = new IntervalControlHelper();
    }

    public void test_noRule() {
        assertEquals(0, intervalControlHelper.getDelay());
    }

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
}
