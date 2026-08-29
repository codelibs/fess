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
package org.codelibs.fess.opensearch.config.exentity;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ScheduledJobScriptTypeTest extends UnitFessTestCase {

    @Test
    public void test_getScriptType_blankFallsBackToLegacy() {
        final ScheduledJob job = new ScheduledJob();
        assertEquals(Constants.LEGACY_SCRIPT, job.getScriptType());

        job.setScriptType("");
        assertEquals(Constants.LEGACY_SCRIPT, job.getScriptType());

        job.setScriptType("  ");
        assertEquals(Constants.LEGACY_SCRIPT, job.getScriptType());
    }

    @Test
    public void test_getScriptType_explicitWins() {
        final ScheduledJob job = new ScheduledJob();
        job.setScriptType("javascript");
        assertEquals("javascript", job.getScriptType());
    }

    @Test
    public void test_legacyIsGroovyAndDefaultIsJavaScript() {
        assertEquals("groovy", Constants.LEGACY_SCRIPT);
        assertEquals("javascript", Constants.DEFAULT_SCRIPT);
    }
}
