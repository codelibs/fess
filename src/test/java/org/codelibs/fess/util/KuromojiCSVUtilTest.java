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
package org.codelibs.fess.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;

public class KuromojiCSVUtilTest extends UnitFessTestCase {
    public void test_parse() {
        String value;
        List<String> expected;
        List<String> actual;

        value = "フェス";
        expected = Arrays.asList("フェス");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "フェス,Fess";
        expected = Arrays.asList("フェス", "Fess");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "Fess,フェス";
        expected = Arrays.asList("Fess", "フェス");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "\"Fess,FESS\"";
        expected = Arrays.asList("\"Fess,FESS\"");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = " ";
        expected = Arrays.asList(" ");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "";
        expected = Arrays.asList("");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "\"Fess\"Fess\"";
        expected = Arrays.asList();
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));
    }
}
