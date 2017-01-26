/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.unit.UnitFessTestCase;

public class DocumentHelperTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void test_getContent() {
        DocumentHelper documentHelper = new DocumentHelper();

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("", documentHelper.getContent(responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t \t", dataMap));
        assertEquals("123 abc", documentHelper.getContent(responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(responseData, "　１２３　あいう　", dataMap));
        assertEquals("123 abc", documentHelper.getContent(responseData, " 123\nabc ", dataMap));
    }

    public void test_getContent_maxAlphanum() {
        DocumentHelper documentHelper = new DocumentHelper() {
            protected int getMaxAlphanumTermSize() {
                return 2;
            }
        };

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("", documentHelper.getContent(responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t \t", dataMap));
        assertEquals("12 ab", documentHelper.getContent(responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(responseData, "　１２３　あいう　", dataMap));
        assertEquals("12 ab", documentHelper.getContent(responseData, " 123\nabc ", dataMap));
        assertEquals("12", documentHelper.getContent(responseData, " 123abc ", dataMap));
    }

    public void test_getContent_maxSymbol() {
        DocumentHelper documentHelper = new DocumentHelper() {
            protected int getMaxSymbolTermSize() {
                return 2;
            }
        };

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("", documentHelper.getContent(responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(responseData, "\t \t", dataMap));
        assertEquals("123 abc", documentHelper.getContent(responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(responseData, "　１２３　あいう　", dataMap));
        assertEquals("123 abc", documentHelper.getContent(responseData, " 123\nabc ", dataMap));
        assertEquals("123abc", documentHelper.getContent(responseData, " 123abc ", dataMap));

        assertEquals("!!", documentHelper.getContent(responseData, "!!!", dataMap));
        assertEquals("//", documentHelper.getContent(responseData, "///", dataMap));
        assertEquals("::", documentHelper.getContent(responseData, ":::", dataMap));
        assertEquals("@@", documentHelper.getContent(responseData, "@@@", dataMap));
        assertEquals("[[", documentHelper.getContent(responseData, "[[[", dataMap));
        assertEquals("``", documentHelper.getContent(responseData, "```", dataMap));
        assertEquals("{{", documentHelper.getContent(responseData, "{{{", dataMap));
        assertEquals("~~", documentHelper.getContent(responseData, "~~~", dataMap));
        assertEquals("!\"", documentHelper.getContent(responseData, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", dataMap));
    }

    public void test_getDigest() {
        DocumentHelper documentHelper = new DocumentHelper();

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("1234567...", documentHelper.getDigest(responseData, " 1234567890  1234567890  1234567890 ", dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, "123456789012345678901234567890", dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, "123456789012345678901", dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, "12345678901234567890", dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, "1234567890123456789", dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, "12345678901", dataMap, 10));
        assertEquals("1234567890", documentHelper.getDigest(responseData, "1234567890", dataMap, 10));
        assertEquals("123456789", documentHelper.getDigest(responseData, "123456789", dataMap, 10));
        assertEquals("1234567", documentHelper.getDigest(responseData, "1234567", dataMap, 10));
        assertEquals("1", documentHelper.getDigest(responseData, "1", dataMap, 10));
        assertEquals("", documentHelper.getDigest(responseData, "", dataMap, 10));
        assertEquals("", documentHelper.getDigest(responseData, " ", dataMap, 10));
        assertEquals("", documentHelper.getDigest(responseData, null, dataMap, 10));
        assertEquals("1234567...", documentHelper.getDigest(responseData, " 1234567890  1234567890  1234567890 ", dataMap, 10));
        assertEquals("１２３４５６７...", documentHelper.getDigest(responseData, "１２３４５６７８９０１２３４５６７８９０", dataMap, 10));
    }
}
