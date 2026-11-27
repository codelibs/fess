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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fesen.opensearch.action.index.IndexAction;
import org.codelibs.fesen.opensearch.action.index.IndexRequest;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.codelibs.fesen.opensearch.action.support.WriteRequest.RefreshPolicy;
import org.codelibs.fesen.opensearch.index.seqno.SequenceNumbers;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.SettingChangedConcurrentlyException;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessAppServiceTest extends UnitFessTestCase {

    private TestFessAppService service;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        service = new TestFessAppService();
    }

    @Test
    public void test_wrapQuery_withoutWildcards() {
        assertEquals("*test*", service.callWrapQuery("test"));
    }

    @Test
    public void test_wrapQuery_withLeadingWildcard() {
        assertEquals("*test*", service.callWrapQuery("*test"));
    }

    @Test
    public void test_wrapQuery_withTrailingWildcard() {
        assertEquals("*test*", service.callWrapQuery("test*"));
    }

    @Test
    public void test_wrapQuery_withBothWildcards() {
        assertEquals("*test*", service.callWrapQuery("*test*"));
    }

    @Test
    public void test_wrapQuery_emptyString() {
        assertEquals("**", service.callWrapQuery(""));
    }

    @Test
    public void test_wrapQuery_singleWildcard() {
        assertEquals("*", service.callWrapQuery("*"));
    }

    @Test
    public void test_wrapQuery_withSpaces() {
        assertEquals("*test query*", service.callWrapQuery("test query"));
    }

    @Test
    public void test_wrapQuery_withSpecialCharacters() {
        assertEquals("*test@example.com*", service.callWrapQuery("test@example.com"));
    }

    @Test
    public void test_wrapQuery_multipleInternalWildcards() {
        assertEquals("*test*query*search*", service.callWrapQuery("test*query*search"));
    }

    @Test
    public void test_wrapQuery_longRandomTextIsKept() {
        final StringBuilder sb = new StringBuilder();
        final java.util.Random random = new java.util.Random(1);
        for (int i = 0; i < 5000; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        assertEquals("*" + sb + "*", service.callWrapQuery(sb.toString()));
    }

    @Test
    public void test_wrapQuery_repeatedTextUpToTheLimit() {
        final String text = "x".repeat(314);
        assertEquals("*" + text + "*", service.callWrapQuery(text));
    }

    @Test
    public void test_wrapQuery_tooComplexIsInvalidQuery() {
        // the search engine refuses these with "Determinizing automaton ... would require more than 10000 effort" (HTTP 500 "all shards failed" before)
        for (final String text : new String[] { "x".repeat(315), "x".repeat(5000), "a?".repeat(50), "abcdefghij".repeat(100) }) {
            try {
                service.callWrapQuery(text);
                fail("expected InvalidQueryException for a pattern of " + text.length() + " characters");
            } catch (final InvalidQueryException e) {
                assertNotNull(e.getMessageCode());
            }
        }
    }

    @Test
    public void test_checkWildcard_prefixPatternIsKept() {
        // a pattern without a leading wildcard is a prefix: it stays cheap at any length
        final String pattern = "x".repeat(5000) + "*";
        assertEquals(pattern, FessAppService.checkWildcard(pattern));
    }

    // -----------------------------------------------------
    //                            insertOrUpdateIfUnchanged
    //                            -------------------------

    /** A behavior that records the request each write would send, or fails the write. */
    private static class RecordingBadWordBhv extends BadWordBhv {
        final List<IndexRequest> requests = new ArrayList<>();

        RuntimeException failure;

        @Override
        public void insertOrUpdate(final BadWord entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
            final IndexRequestBuilder builder = new IndexRequestBuilder(null, IndexAction.INSTANCE);
            opLambda.callback(builder);
            if (failure != null) {
                throw failure;
            }
            requests.add(builder.request());
        }
    }

    private static BadWordService badWordServiceOf(final RecordingBadWordBhv bhv) {
        final BadWordService badWordService = new BadWordService();
        badWordService.badWordBhv = bhv;
        return badWordService;
    }

    private static BadWord readBadWord(final long seqNo, final long primaryTerm) {
        final BadWord badWord = new BadWord();
        badWord.setId("badword1");
        badWord.setSuggestWord("word");
        badWord.asDocMeta().seqNo(seqNo).primaryTerm(primaryTerm);
        return badWord;
    }

    @Test
    public void test_store_settingThatWasReadIsWrittenOnTheConditionOfItsRead() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();

        badWordServiceOf(bhv).store(readBadWord(7L, 2L));

        assertEquals(1, bhv.requests.size());
        assertEquals(7L, bhv.requests.get(0).ifSeqNo());
        assertEquals(2L, bhv.requests.get(0).ifPrimaryTerm());
        assertEquals(RefreshPolicy.IMMEDIATE, bhv.requests.get(0).getRefreshPolicy());
    }

    @Test
    public void test_store_newSettingIsWrittenWithoutCondition() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();
        final BadWord badWord = new BadWord();
        badWord.setSuggestWord("word");

        badWordServiceOf(bhv).store(badWord);

        assertEquals(1, bhv.requests.size());
        assertEquals(SequenceNumbers.UNASSIGNED_SEQ_NO, bhv.requests.get(0).ifSeqNo());
        assertEquals(SequenceNumbers.UNASSIGNED_PRIMARY_TERM, bhv.requests.get(0).ifPrimaryTerm());
        assertEquals(RefreshPolicy.IMMEDIATE, bhv.requests.get(0).getRefreshPolicy());
    }

    @Test
    public void test_store_settingWithoutPrimaryTermIsWrittenWithoutCondition() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();
        final BadWord badWord = readBadWord(7L, 0L);

        badWordServiceOf(bhv).store(badWord);

        assertEquals(SequenceNumbers.UNASSIGNED_SEQ_NO, bhv.requests.get(0).ifSeqNo());
    }

    @Test
    public void test_store_versionConflictIsSettingChangedConcurrently() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();
        bhv.failure = new FakeVersionConflictEngineException("[badword1]: version conflict");

        try {
            badWordServiceOf(bhv).store(readBadWord(7L, 2L));
            fail("a version conflict must be refused");
        } catch (final SettingChangedConcurrentlyException e) {
            assertSame(bhv.failure, e.getCause());
        }
        assertTrue(bhv.requests.isEmpty());
    }

    @Test
    public void test_store_versionConflictOfTheHttpClientIsSettingChangedConcurrently() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();
        bhv.failure = new IllegalStateException("failed",
                new RuntimeException("OpenSearch exception [type=version_conflict_engine_exception, reason=[badword1]: version conflict]"));

        try {
            badWordServiceOf(bhv).store(readBadWord(7L, 2L));
            fail("a version conflict must be refused");
        } catch (final SettingChangedConcurrentlyException e) {
            assertSame(bhv.failure, e.getCause());
        }
    }

    @Test
    public void test_store_otherFailureIsNotAConflict() {
        final RecordingBadWordBhv bhv = new RecordingBadWordBhv();
        bhv.failure = new IllegalStateException("the search engine is not available");

        try {
            badWordServiceOf(bhv).store(readBadWord(7L, 2L));
            fail("the failure must be thrown");
        } catch (final SettingChangedConcurrentlyException e) {
            fail("a failure that is not a version conflict must not be reported as one");
        } catch (final IllegalStateException e) {
            assertSame(bhv.failure, e);
        }
    }

    /** Has the simple name of the exception the in-process client throws for a version conflict. */
    private static final class FakeVersionConflictEngineException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        FakeVersionConflictEngineException(final String message) {
            super(message);
        }
    }

    // Test subclass to access protected method
    private static class TestFessAppService extends FessAppService {
        public String callWrapQuery(final String query) {
            return wrapQuery(query);
        }
    }
}
