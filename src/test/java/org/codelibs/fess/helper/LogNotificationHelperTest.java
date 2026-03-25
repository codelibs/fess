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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LogNotificationHelperTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    public void test_constructor() {
        LogNotificationHelper helper = new LogNotificationHelper();
        assertNotNull(helper);
    }

    @Test
    public void test_init_and_destroy() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // init() registers a TimeoutTarget with TimeoutManager
        helper.init();
        // destroy() cancels the task and flushes; should not throw
        helper.destroy();
    }

    @Test
    public void test_destroy_beforeInit() {
        LogNotificationHelper helper = new LogNotificationHelper();
        // destroy() without init() should not throw NPE because of null checks
        helper.destroy();
    }

    @Test
    public void test_init_destroy_multipleRounds() {
        LogNotificationHelper helper = new LogNotificationHelper();
        helper.init();
        helper.destroy();
        // Second round
        helper.init();
        helper.destroy();
    }
}
