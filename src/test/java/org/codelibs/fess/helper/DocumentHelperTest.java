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
        assertEquals("", documentHelper.getContent(null, responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t \t", dataMap));
        assertEquals("123 abc", documentHelper.getContent(null, responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(null, responseData, "　１２３　あいう　", dataMap));
        assertEquals("123 abc", documentHelper.getContent(null, responseData, " 123\nabc ", dataMap));
    }

    public void test_getContent_maxAlphanum() {
        DocumentHelper documentHelper = new DocumentHelper() {
            protected int getMaxAlphanumTermSize() {
                return 2;
            }
        };

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("", documentHelper.getContent(null, responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t \t", dataMap));
        assertEquals("12 ab", documentHelper.getContent(null, responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(null, responseData, "　１２３　あいう　", dataMap));
        assertEquals("12 ab", documentHelper.getContent(null, responseData, " 123\nabc ", dataMap));
        assertEquals("12", documentHelper.getContent(null, responseData, " 123abc ", dataMap));
    }

    public void test_getContent_maxSymbol() {
        DocumentHelper documentHelper = new DocumentHelper() {
            protected int getMaxSymbolTermSize() {
                return 2;
            }
        };

        ResponseData responseData = new ResponseData();
        Map<String, Object> dataMap = new HashMap<>();
        assertEquals("", documentHelper.getContent(null, responseData, null, dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, " ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "  ", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t\t", dataMap));
        assertEquals("", documentHelper.getContent(null, responseData, "\t \t", dataMap));
        assertEquals("123 abc", documentHelper.getContent(null, responseData, " 123 abc ", dataMap));
        assertEquals("１２３ あいう", documentHelper.getContent(null, responseData, "　１２３　あいう　", dataMap));
        assertEquals("123 abc", documentHelper.getContent(null, responseData, " 123\nabc ", dataMap));
        assertEquals("123abc", documentHelper.getContent(null, responseData, " 123abc ", dataMap));

        assertEquals("!!", documentHelper.getContent(null, responseData, "!!!", dataMap));
        assertEquals("//", documentHelper.getContent(null, responseData, "///", dataMap));
        assertEquals("::", documentHelper.getContent(null, responseData, ":::", dataMap));
        assertEquals("@@", documentHelper.getContent(null, responseData, "@@@", dataMap));
        assertEquals("[[", documentHelper.getContent(null, responseData, "[[[", dataMap));
        assertEquals("``", documentHelper.getContent(null, responseData, "```", dataMap));
        assertEquals("{{", documentHelper.getContent(null, responseData, "{{{", dataMap));
        assertEquals("~~", documentHelper.getContent(null, responseData, "~~~", dataMap));
        assertEquals("!\"", documentHelper.getContent(null, responseData, "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", dataMap));
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

    public void test_encodeSimilarDocHash() {
        DocumentHelper documentHelper = new DocumentHelper();

        String hash = "01010101010101010101010101010101";
        String value = "$H4sIAAAAAAAA_zMwNMALAXC7sg0gAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));
        hash = "00101010010010100100101010001010";
        value = "$H4sIAAAAAAAA_zMwMARDCELQQApMAgAi5-3LIAAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));
        hash = "0001010100100101001001010100010100101010010010100100101011000100";
        value = "$H4sIAAAAAAAA_zMwMDAEQwhC0EAKxscqDZE3AABZOHx2QAAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));
        hash = "00100101010001010100100101001001010100010100010101010010010101010010101100010101001000010101001001010001000001010010101001001010";
        value = "$H4sIAAAAAAAA_zMwMDQAIhCE01ARdBkYD1kGxkDVjWESRBBJPVwKAHL5QrqAAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));

        hash = "";
        value = "$H4sIAAAAAAAA_wMAAAAAAAAAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));
        hash = "$H4sIAAAAAAAAAAMAAAAAAAAAAAA";
        value = "$H4sIAAAAAAAAAAMAAAAAAAAAAAA";
        assertEquals(value, documentHelper.encodeSimilarDocHash(hash));

        assertNull(documentHelper.encodeSimilarDocHash(null));
    }

    public void test_decodeSimilarDocHash() {
        DocumentHelper documentHelper = new DocumentHelper();

        String hash = "01010101010101010101010101010101";
        String value = "$H4sIAAAAAAAAADMwNMALAXC7sg0gAAAA";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));
        hash = "00101010010010100100101010001010";
        value = "$H4sIAAAAAAAAADMwMARDCELQQApMAgAi5-3LIAAAAA";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));
        hash = "0001010100100101001001010100010100101010010010100100101011000100";
        value = "$H4sIAAAAAAAAADMwMDAEQwhC0EAKxscqDZE3AABZOHx2QAAAAA";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));
        hash = "00100101010001010100100101001001010100010100010101010010010101010010101100010101001000010101001001010001000001010010101001001010";
        value = "$H4sIAAAAAAAAADMwMDQAIhCE01ARdBkYD1kGxkDVjWESRBBJPVwKAHL5QrqAAAAA";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));

        hash = "01010101010101010101010101010101";
        value = "01010101010101010101010101010101";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));
        hash = "";
        value = "";
        assertEquals(hash, documentHelper.decodeSimilarDocHash(value));

        assertNull(documentHelper.decodeSimilarDocHash(null));
    }

    public void test_appendLineNumber() {
        DocumentHelper documentHelper = new DocumentHelper();

        assertEquals("", documentHelper.appendLineNumber("L", null));
        assertEquals("", documentHelper.appendLineNumber("L", ""));
        assertEquals("", documentHelper.appendLineNumber("L", " "));
        assertEquals("", documentHelper.appendLineNumber("L", "\n"));
        assertEquals("", documentHelper.appendLineNumber("L", "\n\n"));
        assertEquals("", documentHelper.appendLineNumber("L", "\n \n"));
        assertEquals("L1:aaa", documentHelper.appendLineNumber("L", "aaa"));
        assertEquals("L1:aaa", documentHelper.appendLineNumber("L", "aaa\n"));
        assertEquals("L1:aaa\nL2:bbb", documentHelper.appendLineNumber("L", "aaa\nbbb"));
        assertEquals("L1:aaa\nL2:bbb\nL3:ccc", documentHelper.appendLineNumber("L", "aaa\nbbb\nccc"));
    }
}
