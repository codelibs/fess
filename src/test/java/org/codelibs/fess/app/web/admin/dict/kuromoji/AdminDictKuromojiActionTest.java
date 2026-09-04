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
package org.codelibs.fess.app.web.admin.dict.kuromoji;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * The user dictionary rules are checked before an entry is stored. An entry that breaks one of
 * them is otherwise only rejected when the search engine next loads the dictionary, and it then
 * refuses to open the index, which takes search down a long way from where the entry was added.
 */
public class AdminDictKuromojiActionTest extends UnitFessTestCase {

    /**
     * The segmentation and the reading have to be split into the same number of tokens. This is
     * the entry shape the engine rejects with "the number of segmentations does not the match
     * number of readings".
     */
    @Test
    public void test_verifyKuromojiEntry_rejectsMismatchedSegmentationAndReading() {
        assertTrue(errorOf("token", "one two", "ONE").contains("segmentation"));
    }

    /**
     * A token may not contain a space.
     */
    @Test
    public void test_verifyKuromojiEntry_rejectsATokenWithASpace() {
        assertTrue(errorOf("two tokens", "two tokens", "TWO TOKENS").contains("token"));
    }

    /**
     * A well formed entry reports nothing, whether it is one token or several.
     */
    @Test
    public void test_verifyKuromojiEntry_acceptsAWellFormedEntry() {
        assertEquals("", errorOf("token", "one two", "ONE TWO"));
        assertEquals("", errorOf("token", "token", "TOKEN"));
    }

    /**
     * Fields that were not filled in are left to the existing required-field validation.
     */
    @Test
    public void test_verifyKuromojiEntry_ignoresMissingFields() {
        assertEquals("", errorOf(null, null, null));
        assertEquals("", errorOf("token", "one two", null));
    }

    /**
     * Runs the shared rules over one entry and returns what they reported, or an empty string.
     */
    private String errorOf(final String token, final String segmentation, final String reading) {
        final CreateForm form = new CreateForm();
        form.token = token;
        form.segmentation = segmentation;
        form.reading = reading;
        final StringBuilder reported = new StringBuilder();
        AdminDictKuromojiAction.verifyKuromojiEntry(form, messenger -> {
            final FessMessages messages = new FessMessages();
            messenger.message(messages);
            reported.append(messages.toString());
        });
        return reported.toString();
    }
}
