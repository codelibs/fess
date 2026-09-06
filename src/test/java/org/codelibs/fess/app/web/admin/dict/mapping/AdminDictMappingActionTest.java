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
package org.codelibs.fess.app.web.admin.dict.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.fess.app.service.CharMappingService;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * The mapping dictionary is turned into a normalize char map when the search engine opens the
 * index, and that map refuses an input it has already seen ("match ... was already added"). The
 * open fails with it and every search then answers with index_closed_exception, so a repeated
 * input has to be caught where the entry is written instead.
 */
public class AdminDictMappingActionTest extends UnitFessTestCase {

    /**
     * An input another entry already maps is refused.
     */
    @Test
    public void test_verifyCharMappingEntry_rejectsAnInputTheDictionaryAlreadyMaps() {
        assertTrue(errorOf(dictionaryMapping(3L, "in"), null, "in").contains("duplicate_char_mapping_input"));
    }

    /**
     * An input repeated inside the entry being written is refused as well: the map is built from
     * every line at once, so the second occurrence fails the same way.
     */
    @Test
    public void test_verifyCharMappingEntry_rejectsAnInputRepeatedWithinTheEntry() {
        assertTrue(errorOf(emptyDictionary(), null, "in\nin").contains("duplicate_char_mapping_input"));
    }

    /**
     * An input no other entry maps is accepted.
     */
    @Test
    public void test_verifyCharMappingEntry_acceptsANewInput() {
        assertEquals("", errorOf(dictionaryMapping(3L, "in"), null, "other"));
        assertEquals("", errorOf(emptyDictionary(), null, "one\ntwo"));
    }

    /**
     * Editing an entry without changing its input is not a duplicate of itself.
     */
    @Test
    public void test_verifyCharMappingEntry_acceptsAnEditThatKeepsItsOwnInput() {
        assertEquals("", errorOf(dictionaryMapping(3L, "in"), 3L, "in"));
    }

    /**
     * Editing an entry onto an input another one already maps is still a duplicate.
     */
    @Test
    public void test_verifyCharMappingEntry_rejectsAnEditOntoAnotherEntrysInput() {
        assertTrue(errorOf(dictionaryMapping(3L, "in"), 4L, "in").contains("duplicate_char_mapping_input"));
    }

    /**
     * An entry with no inputs is left to the existing required-field validation.
     */
    @Test
    public void test_verifyCharMappingEntry_ignoresAnEmptyInput() {
        assertEquals("", errorOf(dictionaryMapping(3L, "in"), null, null));
    }

    /**
     * A service standing in for a dictionary that maps the given input.
     */
    private CharMappingService dictionaryMapping(final long id, final String input) {
        final Map<Long, String> entries = new LinkedHashMap<>();
        entries.put(id, input);
        return new CharMappingService() {
            @Override
            public boolean containsInput(final String dictId, final String candidate, final Long excludeId) {
                return entries.entrySet()
                        .stream()
                        .anyMatch(entry -> !entry.getKey().equals(excludeId) && entry.getValue().equals(candidate));
            }
        };
    }

    /**
     * A service standing in for a dictionary that maps nothing.
     */
    private CharMappingService emptyDictionary() {
        return new CharMappingService() {
            @Override
            public boolean containsInput(final String dictId, final String candidate, final Long excludeId) {
                return false;
            }
        };
    }

    /**
     * Runs the shared rules over one entry against the given dictionary contents.
     */
    private String errorOf(final CharMappingService charMappingService, final Long id, final String inputs) {
        final CreateForm form;
        if (id == null) {
            form = new CreateForm();
        } else {
            final EditForm editForm = new EditForm();
            editForm.id = id;
            form = editForm;
        }
        form.inputs = inputs;
        final StringBuilder reported = new StringBuilder();
        AdminDictMappingAction.verifyCharMappingEntry(charMappingService, form, messenger -> {
            final FessMessages messages = new FessMessages();
            messenger.message(messages);
            reported.append(messages.toString());
        });
        return reported.toString();
    }
}
