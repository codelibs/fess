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
package org.codelibs.fess.app.web.api.admin.badword;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.service.BadWordService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.exception.SettingChangedConcurrentlyException;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Covers the version check of {@code PUT /api/admin/badword/setting}: the body must carry the
 * {@code version_no} of the bad word it was read from.
 */
public class ApiAdminBadwordActionTest extends UnitFessTestCase {

    private static final String ID = "badword1";

    private final List<BadWord> stored = new ArrayList<>();

    /** The sequence number of the next read; every read sees a later one, as if the setting were written in between. */
    private long nextSeqNo = 5L;

    /** Whether the write is refused as the search engine refuses a write whose condition no longer holds. */
    private boolean conflictOnStore;

    private int suggestUpdates;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    private ApiAdminBadwordAction createAction(final Long storedVersion) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        suppressBindingOf(BadWordService.class);
        suppressBindingOf(SuggestHelper.class);
        final ApiAdminBadwordAction action = new ApiAdminBadwordAction();
        inject(action);
        setField(action, FessBaseAction.class, "systemHelper", ComponentUtil.getSystemHelper());
        setField(action, FessBaseAction.class, "fessConfig", ComponentUtil.getFessConfig());
        final BadWordService service = new BadWordService() {
            @Override
            public OptionalEntity<BadWord> getBadWord(final String id) {
                final BadWord badWord = new BadWord();
                badWord.setId(ID);
                badWord.setSuggestWord("word");
                badWord.setVersionNo(storedVersion);
                badWord.asDocMeta().seqNo(nextSeqNo++).primaryTerm(1L);
                return OptionalEntity.of(badWord);
            }

            @Override
            public void store(final BadWord badWord) {
                if (conflictOnStore) {
                    throw new SettingChangedConcurrentlyException("changed",
                            new IllegalStateException("version_conflict_engine_exception"));
                }
                stored.add(badWord);
            }
        };
        // the screen's getBadWord reads the stored bad word through the container
        ComponentUtil.register(service, BadWordService.class.getCanonicalName());
        setField(action, ApiAdminBadwordAction.class, "badWordService", service);
        setField(action, ApiAdminBadwordAction.class, "suggestHelper", new SuggestHelper() {
            @Override
            public void storeAllBadWords(final boolean doCommit) {
                suggestUpdates++;
            }
        });
        return action;
    }

    private static void setField(final Object target, final Class<?> owner, final String name, final Object value) throws Exception {
        final Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static EditBody bodyOfVersion(final Long version) {
        final EditBody body = new EditBody();
        body.id = ID;
        body.suggestWord = "word";
        body.versionNo = version;
        return body;
    }

    @Test
    public void test_put_staleVersionIsRefusedAndNothingIsStored() throws Exception {
        final ApiAdminBadwordAction action = createAction(4L);

        assertValidationError(() -> action.put$setting(bodyOfVersion(3L))).handle(data -> {
            data.requiredMessageOf("_global", "errors.setting_changed_concurrently");
        });
        assertTrue(stored.isEmpty());
    }

    @Test
    public void test_put_currentVersionIsStored() throws Exception {
        final ApiAdminBadwordAction action = createAction(4L);

        action.put$setting(bodyOfVersion(4L));

        assertEquals(1, stored.size());
        assertEquals("word", stored.get(0).getSuggestWord());
    }

    @Test
    public void test_put_versionThatWasNotReadCannotBeCompared() throws Exception {
        final ApiAdminBadwordAction action = createAction(-1L);

        action.put$setting(bodyOfVersion(3L));

        assertEquals(1, stored.size());
    }

    @Test
    public void test_put_writeIsConditionalOnTheReadWhoseVersionWasChecked() throws Exception {
        final ApiAdminBadwordAction action = createAction(4L);

        action.put$setting(bodyOfVersion(4L));

        // the version was checked on the first read (seqNo 5); the body is applied to a second one (seqNo 6), and a
        // write conditional on the second read would let a change made between the two reads be overwritten
        assertEquals(1, stored.size());
        assertEquals(Long.valueOf(5L), stored.get(0).asDocMeta().seqNo());
        assertEquals(Long.valueOf(1L), stored.get(0).asDocMeta().primaryTerm());
    }

    @Test
    public void test_put_conflictOnTheWriteIsRefusedLikeAStaleVersion() throws Exception {
        final ApiAdminBadwordAction action = createAction(4L);
        conflictOnStore = true;

        assertValidationError(() -> action.put$setting(bodyOfVersion(4L))).handle(data -> {
            data.requiredMessageOf("_global", "errors.setting_changed_concurrently");
        });
        assertTrue(stored.isEmpty());
        assertEquals(0, suggestUpdates);
    }

    @Test
    public void test_put_missingSettingIsNotFound() throws Exception {
        final ApiAdminBadwordAction action = createAction(4L);
        final BadWordService service = new BadWordService() {
            @Override
            public OptionalEntity<BadWord> getBadWord(final String id) {
                return OptionalEntity.empty();
            }
        };
        ComponentUtil.register(service, BadWordService.class.getCanonicalName());
        setField(action, ApiAdminBadwordAction.class, "badWordService", service);

        assertValidationError(() -> action.put$setting(bodyOfVersion(4L))).handle(data -> {
            data.requiredMessageOf("_global", "errors.crud_could_not_find_crud_table");
        });
        assertTrue(stored.isEmpty());
    }
}
