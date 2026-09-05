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
package org.codelibs.fess.app.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.codelibs.fess.dict.mapping.CharMappingItem;
import org.codelibs.fess.dict.protwords.ProtwordsItem;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideItem;
import org.codelibs.fess.dict.stopwords.StopwordsItem;
import org.codelibs.fess.dict.synonym.SynonymItem;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for what the six dictionary services do with a dictionary ID that resolves to no
 * dictionary file. They all shared one shape, so they are covered together rather than one
 * near-identical test class each.
 */
public class DictionaryServiceTest extends UnitFessTestCase {

    private static final String UNKNOWN_DICT_ID = "0";

    /**
     * Writing to a dictionary ID that resolves to nothing used to do nothing at all and report
     * nothing, so the caller was told the entry had been stored when it had not been.
     */
    @Test
    public void test_store_reportsAnUnknownDictionaryId() {
        final KuromojiService kuromojiService = new KuromojiService() {
            @Override
            public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> kuromojiService.store(UNKNOWN_DICT_ID, new KuromojiItem(0, "t", "s", "r", "p")));

        final SynonymService synonymService = new SynonymService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.synonym.SynonymFile> getSynonymFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> synonymService.store(UNKNOWN_DICT_ID, new SynonymItem(0, new String[] { "a" }, new String[] { "b" })));

        final StopwordsService stopwordsService = new StopwordsService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.stopwords.StopwordsFile> getStopwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> stopwordsService.store(UNKNOWN_DICT_ID, new StopwordsItem(0, "a")));

        final ProtwordsService protwordsService = new ProtwordsService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.protwords.ProtwordsFile> getProtwordsFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> protwordsService.store(UNKNOWN_DICT_ID, new ProtwordsItem(0, "a")));

        final StemmerOverrideService stemmerOverrideService = new StemmerOverrideService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.stemmeroverride.StemmerOverrideFile> getStemmerOverrideFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> stemmerOverrideService.store(UNKNOWN_DICT_ID, new StemmerOverrideItem(0, "a", "b")));

        final CharMappingService charMappingService = new CharMappingService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.mapping.CharMappingFile> getCharMappingFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> charMappingService.store(UNKNOWN_DICT_ID, new CharMappingItem(0, new String[] { "a" }, "b")));
    }

    /**
     * Deleting through the same services has the same shape, so it is refused the same way.
     */
    @Test
    public void test_delete_reportsAnUnknownDictionaryId() {
        final KuromojiService kuromojiService = new KuromojiService() {
            @Override
            public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> kuromojiService.delete(UNKNOWN_DICT_ID, new KuromojiItem(1, "t", "s", "r", "p")));

        final CharMappingService charMappingService = new CharMappingService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.mapping.CharMappingFile> getCharMappingFile(final String dictId) {
                return OptionalEntity.empty();
            }
        };
        assertReported(() -> charMappingService.delete(UNKNOWN_DICT_ID, new CharMappingItem(1, new String[] { "a" }, "b")));
    }

    /**
     * A dictionary ID that does resolve still stores the entry, so the check refuses only the case
     * that was silently discarded.
     */
    @Test
    public void test_store_writesToAKnownDictionaryId() {
        final AtomicReference<KuromojiItem> inserted = new AtomicReference<>();
        final KuromojiFile file = new KuromojiFile("1", "/dev/null", new Date()) {
            @Override
            public void insert(final KuromojiItem item) {
                inserted.set(item);
            }
        };
        final KuromojiService kuromojiService = new KuromojiService() {
            @Override
            public OptionalEntity<KuromojiFile> getKuromojiFile(final String dictId) {
                return OptionalEntity.of(file);
            }
        };

        final KuromojiItem item = new KuromojiItem(0, "t", "s", "r", "p");
        kuromojiService.store("1", item);

        assertSame(item, inserted.get());
    }

    /**
     * Asserts that the given write reports that it stored nothing.
     *
     * @param write the service call under test
     */
    private void assertReported(final Runnable write) {
        Assertions.assertThrows(DictionaryException.class, write::run);
    }
}
