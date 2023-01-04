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

import java.util.Locale;

import org.codelibs.fess.unit.UnitFessTestCase;

public class LabelTypeHelperTest extends UnitFessTestCase {

    private LabelTypeHelper labelTypeHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        labelTypeHelper = new LabelTypeHelper();
    }

    public void test_matchLocale() {
        assertFalse(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.JAPANESE));
        assertFalse(labelTypeHelper.matchLocale(Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE));

        assertTrue(labelTypeHelper.matchLocale(null, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ROOT, Locale.ROOT));
        assertTrue(labelTypeHelper.matchLocale(Locale.ENGLISH, Locale.ENGLISH));
        assertTrue(labelTypeHelper.matchLocale(Locale.JAPAN, Locale.JAPANESE));
        assertTrue(labelTypeHelper.matchLocale(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE));
        assertTrue(labelTypeHelper.matchLocale(Locale.TRADITIONAL_CHINESE, Locale.CHINESE));
    }
}
