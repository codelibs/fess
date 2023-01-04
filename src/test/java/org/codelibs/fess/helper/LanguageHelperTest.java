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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class LanguageHelperTest extends UnitFessTestCase {

    private LanguageHelper languageHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        languageHelper = new LanguageHelper();
        languageHelper.langFields = new String[] { "title", "content" };
    }

    public void test_createScript() {
        Map<String, Object> doc = new HashMap<>();
        assertEquals("aaa", languageHelper.createScript(doc, "aaa").getIdOrCode());

        doc.put("lang", "ja");
        assertEquals("aaa;ctx._source.title_ja=ctx._source.title;ctx._source.content_ja=ctx._source.content",
                languageHelper.createScript(doc, "aaa").getIdOrCode());
    }

    public void test_getReindexScriptSource() {
        assertEquals(
                "if(ctx._source.lang!=null){ctx._source['title_'+ctx._source.lang]=ctx._source.title;ctx._source['content_'+ctx._source.lang]=ctx._source.content}",
                languageHelper.getReindexScriptSource());
    }
}
