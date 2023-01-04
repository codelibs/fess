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
package org.codelibs.fess.crawler;

import java.util.List;
import java.util.regex.Pattern;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.unit.UnitFessTestCase;

public class FessCrawlerThreadTest extends UnitFessTestCase {

    public void test_getClientRuleList() {
        FessCrawlerThread crawlerThread = new FessCrawlerThread();

        List<Pair<String, Pattern>> list = crawlerThread.getClientRuleList(null);
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList("");
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList(" ");
        assertEquals(0, list.size());

        list = crawlerThread.getClientRuleList("playwright:http://.*");
        assertEquals(1, list.size());
        assertEquals("playwright", list.get(0).getFirst());
        assertEquals("http://.*", list.get(0).getSecond().pattern());

        list = crawlerThread.getClientRuleList("playwright:http://.*,playwright:https://.*");
        assertEquals(2, list.size());
        assertEquals("playwright", list.get(0).getFirst());
        assertEquals("http://.*", list.get(0).getSecond().pattern());
        assertEquals("playwright", list.get(1).getFirst());
        assertEquals("https://.*", list.get(1).getSecond().pattern());
    }
}
