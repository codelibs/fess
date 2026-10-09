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
package org.codelibs.fess.app.web.admin.dict.synonym;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * A synonym entry is written to the synonym file as {@code in1,in2=>out1,out2}, so a term that
 * contains a comma or {@code =>} would be read back as several terms or as a different rule.
 * The inputs and the outputs are given one term per line; the terms themselves must not carry
 * the separators.
 */
public class AdminDictSynonymActionTest extends UnitFessTestCase {

    /**
     * A comma inside an input is refused, and the error names the property.
     */
    @Test
    public void test_verifySynonymEntry_rejectsACommaInAnInput() {
        final String reported = errorOf("検索,サーチ", "検索");
        assertTrue(reported.contains("inputs"), reported);
        assertTrue(reported.contains("invalid_str_is_included"), reported);
    }

    /**
     * A comma inside an output is refused, and the error names the property.
     */
    @Test
    public void test_verifySynonymEntry_rejectsACommaInAnOutput() {
        final String reported = errorOf("検索", "検索,サーチ,リサーチ");
        assertTrue(reported.contains("outputs"), reported);
        assertTrue(reported.contains("invalid_str_is_included"), reported);
    }

    /**
     * The rule separator is refused in both sides.
     */
    @Test
    public void test_verifySynonymEntry_rejectsTheRuleSeparator() {
        assertTrue(errorOf("a=>b", "c").contains("inputs"));
        assertTrue(errorOf("a", "b=>c").contains("outputs"));
    }

    /**
     * The same terms given one per line are accepted, and so are blank lines between them.
     */
    @Test
    public void test_verifySynonymEntry_acceptsOneTermPerLine() {
        assertEquals("", errorOf("検索\nサーチ", "検索\r\nサーチ\n\nリサーチ"));
    }

    /**
     * Runs the shared rules over one entry and returns what they reported.
     */
    private String errorOf(final String inputs, final String outputs) {
        final CreateForm form = new CreateForm();
        form.inputs = inputs;
        form.outputs = outputs;
        final StringBuilder reported = new StringBuilder();
        AdminDictSynonymAction.verifySynonymEntry(form, messenger -> {
            final FessMessages messages = new FessMessages();
            messenger.message(messages);
            reported.append(messages.toString());
        });
        return reported.toString();
    }
}
