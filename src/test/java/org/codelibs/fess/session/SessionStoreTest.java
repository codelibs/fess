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
package org.codelibs.fess.session;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;

public class SessionStoreTest extends UnitFessTestCase {

    private static final String SESSION_ID = "0123456789ABCDEF0123456789ABCDEF";

    /** Serializes the attributes to their string form, which is enough to tell two contents apart. */
    private static class StubSerializer extends SessionDataSerializer {
        String formatVersion = "v1";

        @Override
        public byte[] toBytes(final Map<String, Object> attributes, final java.util.Set<String> skippedNames) {
            if (skippedNames != null) {
                attributes.keySet().stream().filter(n -> n.startsWith("skip")).forEach(skippedNames::add);
            }
            final Map<String, Object> kept = new java.util.TreeMap<>(attributes);
            kept.keySet().removeIf(n -> n.startsWith("skip"));
            return kept.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Map<String, Object> fromBytes(final byte[] bytes) {
            final Map<String, Object> map = new HashMap<>();
            map.put("raw", new String(bytes, StandardCharsets.UTF_8));
            return map;
        }

        @Override
        public String getFormatVersion() {
            return formatVersion;
        }
    }

    /** Keeps the documents in memory and records what would have been sent to the search engine. */
    private static class FakeSessionStore extends SessionStore {
        final Map<String, Map<String, Object>> docs = new LinkedHashMap<>();
        final List<String> calls = new ArrayList<>();
        final Map<String, String> env = new HashMap<>();
        final Map<String, Object> contextAttributes = new HashMap<>();
        long now = 1_000_000L;
        long purgedUntil = -1;
        RuntimeException getFailure;

        @Override
        protected String getEnv(final String name) {
            return env.get(name);
        }

        @Override
        protected long currentTimeMillis() {
            return now;
        }

        @Override
        protected ServletContext getServletContext() {
            return (ServletContext) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { ServletContext.class },
                    (proxy, method, args) -> {
                        if ("setAttribute".equals(method.getName())) {
                            contextAttributes.put((String) args[0], args[1]);
                        } else if ("removeAttribute".equals(method.getName())) {
                            contextAttributes.remove(args[0]);
                        } else if ("getAttribute".equals(method.getName())) {
                            return contextAttributes.get(args[0]);
                        }
                        return null;
                    });
        }

        @Override
        protected Map<String, Object> getSession(final String id) {
            calls.add("get");
            if (getFailure != null) {
                throw getFailure;
            }
            final Map<String, Object> doc = docs.get(id);
            return doc == null ? null : new HashMap<>(doc);
        }

        @Override
        protected boolean createSession(final String id, final Map<String, Object> source) {
            calls.add("create");
            if (docs.containsKey(id)) {
                return false;
            }
            docs.put(id, new HashMap<>(source));
            return true;
        }

        @Override
        protected boolean updateSession(final String id, final Map<String, Object> source) {
            calls.add("update");
            final Map<String, Object> doc = docs.get(id);
            if (doc == null) {
                return false;
            }
            doc.putAll(source);
            return true;
        }

        @Override
        protected boolean touchSession(final String id, final long lastAccessedTime, final long expiredTime) {
            calls.add("touch");
            final Map<String, Object> doc = docs.get(id);
            if (doc == null) {
                return false;
            }
            doc.put("lastAccessedTime", lastAccessedTime);
            doc.put("expiredTime", expiredTime);
            return true;
        }

        @Override
        protected void deleteSession(final String id) {
            calls.add("delete");
            docs.remove(id);
        }

        @Override
        protected long deleteExpiredSessions(final long time) {
            calls.add("purge");
            purgedUntil = time;
            return 3;
        }
    }

    private StubSerializer serializer;

    private FakeSessionStore store;

    @Override
    protected void setUp(final org.junit.jupiter.api.TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        serializer = new StubSerializer();
        store = new FakeSessionStore();
        store.sessionDataSerializer = serializer;
    }

    private Map<String, Object> saveArgs(final String digest, final Long persistedAt, final String value) {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", value);
        final Map<String, Object> args = new HashMap<>();
        args.put("id", SESSION_ID);
        args.put("creationTime", 500_000L);
        args.put("lastAccessedTime", store.now);
        args.put("maxInactiveInterval", 1800);
        args.put("attributes", attributes);
        args.put("digest", digest);
        args.put("persistedAt", persistedAt);
        return args;
    }

    private Map<String, Object> idArgs() {
        final Map<String, Object> args = new HashMap<>();
        args.put("id", SESSION_ID);
        return args;
    }

    @Test
    public void test_hashId() throws Exception {
        final String hash = store.hashId(SESSION_ID);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
        assertEquals(hash, store.hashId(SESSION_ID));
        assertFalse(hash.equals(store.hashId(SESSION_ID + "x")));
        // SHA-256 of "abc", a published test vector
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", store.hashId("abc"));
    }

    @Test
    public void test_save_localeOnlyNewSessionIsNotWritten() throws Exception {
        final Map<String, Object> args = saveArgs(null, null, "a");
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(org.lastaflute.web.LastaWebKey.USER_LOCALE_KEY, java.util.Locale.JAPAN);
        args.put("attributes", attributes);

        final Map<String, Object> result = store.save(args);

        assertTrue(store.calls.isEmpty());
        assertEquals(0L, result.get("persistedAt"));
    }

    @Test
    public void test_save_localeOnlyStoredSessionIsStillWritten() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        final Map<String, Object> args = saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "a");
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(org.lastaflute.web.LastaWebKey.USER_LOCALE_KEY, java.util.Locale.JAPAN);
        args.put("attributes", attributes);
        store.calls.clear();

        store.save(args);

        assertEquals(List.of("update"), store.calls);
    }

    @Test
    public void test_toKeyValues_isAcceptedByTheUpdateRequest() throws Exception {
        final Map<String, Object> fields = new HashMap<>();
        fields.put("lastAccessedTime", 1L);
        fields.put("expiredTime", 2L);

        final Object[] keyValues = SessionStore.toKeyValues(fields);

        assertEquals(4, keyValues.length);
        // The real request builder takes the pairs; a Map passed to doc(Object...) fails with an odd-argument error.
        final org.codelibs.fesen.opensearch.action.update.UpdateRequest request =
                new org.codelibs.fesen.opensearch.action.update.UpdateRequest("i", "id");
        request.doc(keyValues);
        assertTrue(request.doc().source().utf8ToString().contains("\"expiredTime\":2"));
    }

    @Test
    public void test_save_storesUnderHashedId() throws Exception {
        final Map<String, Object> result = store.save(saveArgs(null, null, "a"));

        assertEquals(List.of("create"), store.calls);
        assertEquals(1, store.docs.size());
        final String docId = store.docs.keySet().iterator().next();
        assertEquals(store.hashId(SESSION_ID), docId);
        assertFalse(docId.contains(SESSION_ID));
        final Map<String, Object> doc = store.docs.get(docId);
        assertEquals("v1", doc.get("formatVersion"));
        assertEquals(result.get("digest"), doc.get("digest"));
        assertEquals(500_000L, doc.get("creationTime"));
        assertEquals(store.now, doc.get("lastAccessedTime"));
        assertEquals(1800, doc.get("maxInactiveInterval"));
        assertEquals(store.now + 1800 * 1000L, doc.get("expiredTime"));
        assertEquals("{name=a}", new String(Base64.getDecoder().decode((String) doc.get("data")), StandardCharsets.UTF_8));
        assertEquals(store.now, result.get("persistedAt"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_save_reportsSkippedNamesAndKeepsDigestStable() throws Exception {
        final Map<String, Object> args = saveArgs(null, null, "a");
        ((Map<String, Object>) args.get("attributes")).put("skipPassword", "one");
        final Map<String, Object> first = store.save(args);
        assertEquals(java.util.Set.of("skipPassword"), first.get("skippedNames"));

        store.calls.clear();
        store.now += 1000L;
        final Map<String, Object> again = saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "a");
        ((Map<String, Object>) again.get("attributes")).put("skipPassword", "two");
        final Map<String, Object> second = store.save(again);
        assertEquals(List.of(), store.calls);
        assertEquals(first.get("digest"), second.get("digest"));
        assertEquals(java.util.Set.of("skipPassword"), second.get("skippedNames"));
    }

    @Test
    public void test_save_sameDigestWritesNothing() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.calls.clear();

        store.now += 59_999L;
        final Map<String, Object> second = store.save(saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "a"));

        assertEquals(List.of(), store.calls);
        assertEquals(first.get("digest"), second.get("digest"));
        assertEquals(first.get("persistedAt"), second.get("persistedAt"));
    }

    @Test
    public void test_save_sameDigestTouchesAfterInterval() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        final long firstPersistedAt = (Long) first.get("persistedAt");
        store.calls.clear();

        store.now += 60_000L;
        final Map<String, Object> second = store.save(saveArgs((String) first.get("digest"), firstPersistedAt, "a"));

        assertEquals(List.of("touch"), store.calls);
        assertEquals(store.now, second.get("persistedAt"));
        final Map<String, Object> doc = store.docs.get(store.hashId(SESSION_ID));
        assertEquals(store.now, doc.get("lastAccessedTime"));
        assertEquals(store.now + 1800 * 1000L, doc.get("expiredTime"));
    }

    @Test
    public void test_save_unknownPersistedAtCreatesAndUpdatesOnConflict() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.calls.clear();

        // not known to be stored: create, and as the document exists, update it
        final Map<String, Object> second = store.save(saveArgs((String) first.get("digest"), null, "a"));

        assertEquals(List.of("create", "update"), store.calls);
        assertNull(second.get("gone"));
    }

    @Test
    public void test_save_newSessionIsCreated_notUpdated() throws Exception {
        for (final Long persistedAt : new Long[] { null, 0L, -1L }) {
            store.docs.clear();
            store.calls.clear();

            final Map<String, Object> result = store.save(saveArgs(null, persistedAt, "a"));

            assertEquals(List.of("create"), store.calls);
            assertEquals(1, store.docs.size());
            assertNull(result.get("gone"));
            assertEquals(store.now, result.get("persistedAt"));
        }
    }

    @Test
    public void test_save_newSessionLosingTheCreateRaceDoesNotOverwriteButUpdates() throws Exception {
        store.save(saveArgs(null, null, "a"));
        store.calls.clear();

        final Map<String, Object> result = store.save(saveArgs(null, 0L, "b"));

        assertEquals(List.of("create", "update"), store.calls);
        assertNull(result.get("gone"));
        assertEquals(result.get("digest"), store.docs.get(store.hashId(SESSION_ID)).get("digest"));
    }

    @Test
    public void test_save_newSessionWhoseDocumentVanishesBetweenCreateAndUpdateIsGone() throws Exception {
        final FakeSessionStore racing = new FakeSessionStore() {
            @Override
            protected boolean createSession(final String id, final Map<String, Object> source) {
                calls.add("create");
                return false; // somebody created it ... and deleted it again before our update
            }
        };
        racing.sessionDataSerializer = serializer;

        final Map<String, Object> result = racing.save(saveArgs(null, 0L, "a"));

        assertEquals(Boolean.TRUE, result.get("gone"));
        assertTrue(racing.docs.isEmpty());
    }

    @Test
    public void test_save_changedDigestWrites() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.calls.clear();

        store.now += 1_000L;
        final Map<String, Object> second = store.save(saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "b"));

        assertEquals(List.of("update"), store.calls);
        assertFalse(first.get("digest").equals(second.get("digest")));
        assertEquals(second.get("digest"), store.docs.get(store.hashId(SESSION_ID)).get("digest"));
    }

    @Test
    public void test_save_touchOfMissingDocumentIsGone() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.docs.clear();
        store.calls.clear();

        store.now += 60_000L;
        final Map<String, Object> result = store.save(saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "a"));

        assertEquals(Boolean.TRUE, result.get("gone"));
        assertEquals(List.of("touch"), store.calls);
        assertTrue(store.docs.isEmpty(), "a deleted session must not be written again");
    }

    @Test
    public void test_save_changedDigestOfMissingDocumentIsGone() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.docs.clear();
        store.calls.clear();

        store.now += 1_000L;
        final Map<String, Object> result = store.save(saveArgs((String) first.get("digest"), (Long) first.get("persistedAt"), "b"));

        assertEquals(Boolean.TRUE, result.get("gone"));
        assertNull(result.get("digest"));
        assertEquals(List.of("update"), store.calls);
        assertTrue(store.docs.isEmpty(), "a deleted session must not be written again");
    }

    @Test
    public void test_save_storedSessionWithoutDigestIsUpdatedNotIndexed() throws Exception {
        final Map<String, Object> first = store.save(saveArgs(null, null, "a"));
        store.calls.clear();

        // a state the caller knows nothing of (digest null) but that was stored before
        store.save(saveArgs(null, (Long) first.get("persistedAt"), "a"));

        assertEquals(List.of("update"), store.calls);
    }

    @Test
    public void test_save_neverExpires() throws Exception {
        final Map<String, Object> args = saveArgs(null, null, "a");
        args.put("maxInactiveInterval", -1);
        store.save(args);
        assertEquals(Long.MAX_VALUE, store.docs.get(store.hashId(SESSION_ID)).get("expiredTime"));

        args.put("maxInactiveInterval", 0);
        store.save(args);
        assertEquals(Long.MAX_VALUE, store.docs.get(store.hashId(SESSION_ID)).get("expiredTime"));
    }

    @Test
    public void test_save_requiresId() throws Exception {
        final Map<String, Object> args = saveArgs(null, null, "a");
        args.remove("id");
        try {
            store.save(args);
            fail();
        } catch (final IllegalArgumentException e) {
            // ok
        }
        assertEquals(List.of(), store.calls);
    }

    @Test
    public void test_load_roundTrip() throws Exception {
        final Map<String, Object> saved = store.save(saveArgs(null, null, "a"));

        final Map<String, Object> session = store.load(idArgs());

        assertNotNull(session);
        assertEquals(500_000L, session.get("creationTime"));
        assertEquals(store.now, session.get("lastAccessedTime"));
        assertEquals(1800, session.get("maxInactiveInterval"));
        assertEquals(saved.get("digest"), session.get("digest"));
        assertEquals("{name=a}", ((Map<?, ?>) session.get("attributes")).get("raw"));
    }

    @Test
    public void test_load_unchangedDigestSkipsDeserialization() throws Exception {
        final Map<String, Object> saved = store.save(saveArgs(null, null, "a"));
        store.calls.clear();
        final SessionDataSerializer failing = new StubSerializer() {
            @Override
            public Map<String, Object> fromBytes(final byte[] bytes) {
                throw new AssertionError("the payload must not be deserialized");
            }
        };
        store.sessionDataSerializer = failing;
        final Map<String, Object> args = idArgs();
        args.put("digest", saved.get("digest"));

        final Map<String, Object> session = store.load(args);

        assertNotNull(session);
        assertEquals(saved.get("digest"), session.get("digest"));
        assertNull(session.get("attributes"));
        assertEquals(1800, session.get("maxInactiveInterval"));
        assertEquals(List.of("get"), store.calls);
    }

    @Test
    public void test_load_changedDigestDeserializes() throws Exception {
        final Map<String, Object> saved = store.save(saveArgs(null, null, "a"));
        store.calls.clear();
        final Map<String, Object> args = idArgs();
        args.put("digest", "stale");

        final Map<String, Object> session = store.load(args);

        assertNotNull(session);
        assertEquals(saved.get("digest"), session.get("digest"));
        assertEquals("{name=a}", ((Map<?, ?>) session.get("attributes")).get("raw"));
        assertEquals(List.of("get"), store.calls);
    }

    @Test
    public void test_load_unchangedDigestStillChecksExpiryAndFormat() throws Exception {
        final Map<String, Object> saved = store.save(saveArgs(null, null, "a"));
        final Map<String, Object> args = idArgs();
        args.put("digest", saved.get("digest"));

        serializer.formatVersion = "v2";
        assertNull(store.load(args));
        serializer.formatVersion = "v1";

        store.now += 1800 * 1000L;
        assertNull(store.load(args));
    }

    @Test
    public void test_load_notFound() throws Exception {
        assertNull(store.load(idArgs()));
        assertEquals(List.of("get"), store.calls);
    }

    @Test
    public void test_load_formatMismatch() throws Exception {
        store.save(saveArgs(null, null, "a"));
        serializer.formatVersion = "v2";

        assertNull(store.load(idArgs()));
    }

    @Test
    public void test_load_undecodableData() throws Exception {
        store.save(saveArgs(null, null, "a"));
        final SessionDataSerializer nullSerializer = new StubSerializer() {
            @Override
            public Map<String, Object> fromBytes(final byte[] bytes) {
                return null;
            }
        };
        store.sessionDataSerializer = nullSerializer;

        assertNull(store.load(idArgs()));
    }

    @Test
    public void test_load_expired() throws Exception {
        store.save(saveArgs(null, null, "a"));

        store.now += 1800 * 1000L - 1;
        assertNotNull(store.load(idArgs()));

        store.now += 1;
        assertNull(store.load(idArgs()));
    }

    @Test
    public void test_load_neverExpiringSession() throws Exception {
        final Map<String, Object> args = saveArgs(null, null, "a");
        args.put("maxInactiveInterval", -1);
        store.save(args);

        store.now += 365L * 24 * 3600 * 1000;
        assertNotNull(store.load(idArgs()));
    }

    @Test
    public void test_load_failureIsNotNull() throws Exception {
        store.getFailure = new IllegalStateException("down");
        try {
            store.load(idArgs());
            fail();
        } catch (final IllegalStateException e) {
            assertEquals("down", e.getMessage());
        }
    }

    @Test
    public void test_delete() throws Exception {
        store.save(saveArgs(null, null, "a"));
        assertEquals(1, store.docs.size());

        assertNull(store.delete(idArgs()));

        assertEquals(0, store.docs.size());
        assertEquals("delete", store.calls.get(store.calls.size() - 1));
        assertNull(store.load(idArgs()));
    }

    @Test
    public void test_purge() throws Exception {
        assertEquals(3L, store.purge(new HashMap<>()));
        assertEquals(store.now - SessionStore.PURGE_MARGIN_MS, store.purgedUntil);
    }

    @Test
    public void test_isDocumentMissing() throws Exception {
        assertTrue(store.isDocumentMissing(new org.codelibs.fesen.opensearch.OpenSearchStatusException(
                "OpenSearch exception [type=document_missing_exception, reason=[x]: document missing]",
                org.codelibs.fesen.opensearch.core.rest.RestStatus.NOT_FOUND, null)));
        assertFalse(store.isDocumentMissing(new org.codelibs.fesen.opensearch.OpenSearchStatusException(
                "OpenSearch exception [type=index_not_found_exception, reason=no such index [x]]",
                org.codelibs.fesen.opensearch.core.rest.RestStatus.NOT_FOUND, null)));
        assertFalse(store.isDocumentMissing(new org.codelibs.fesen.opensearch.OpenSearchStatusException("document_missing_exception",
                org.codelibs.fesen.opensearch.core.rest.RestStatus.INTERNAL_SERVER_ERROR, null)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_init_publishesOperationsWhenSelected() throws Exception {
        store.env.put(SessionStore.ENV_NAME, "opensearch");
        store.init();

        final Object operations = store.contextAttributes.get("fess.session.store");
        assertNotNull(operations);
        final Map<String, Function<Map<String, Object>, Object>> map = (Map<String, Function<Map<String, Object>, Object>>) operations;
        assertEquals(java.util.Set.of("load", "save", "delete", "purge"), map.keySet());

        // the operations are the working ones
        final Map<String, Object> saved = (Map<String, Object>) map.get("save").apply(saveArgs(null, null, "a"));
        assertNotNull(saved.get("digest"));
        assertNotNull(map.get("load").apply(idArgs()));
        assertNull(map.get("delete").apply(idArgs()));
        assertNull(map.get("load").apply(idArgs()));
        assertEquals(3L, map.get("purge").apply(new HashMap<>()));

        store.destroy();
        assertNull(store.contextAttributes.get("fess.session.store"));
    }

    @Test
    public void test_init_caseInsensitive() throws Exception {
        store.env.put(SessionStore.ENV_NAME, "OpenSearch");
        store.init();
        assertNotNull(store.contextAttributes.get(SessionStore.CONTEXT_ATTRIBUTE));
    }

    @Test
    public void test_init_doesNothingByDefault() throws Exception {
        store.init();
        assertTrue(store.contextAttributes.isEmpty());

        store.env.put(SessionStore.ENV_NAME, "");
        store.init();
        assertTrue(store.contextAttributes.isEmpty());

        store.env.put(SessionStore.ENV_NAME, "memory");
        store.init();
        assertTrue(store.contextAttributes.isEmpty());

        store.env.put(SessionStore.ENV_NAME, "opensearch2");
        store.init();
        assertTrue(store.contextAttributes.isEmpty());
        assertEquals(List.of(), store.calls);
    }

    @Test
    public void test_init_doesNothingInJobProcess() throws Exception {
        store.env.put(SessionStore.ENV_NAME, "opensearch");
        System.setProperty("fess.crawler.process", "true");
        try {
            store.init();
        } finally {
            System.clearProperty("fess.crawler.process");
        }
        assertTrue(store.contextAttributes.isEmpty());
    }

    @Test
    public void test_calculateExpiredTime() throws Exception {
        assertEquals(1_000L + 30_000L, store.calculateExpiredTime(1_000L, 30));
        assertEquals(Long.MAX_VALUE, store.calculateExpiredTime(1_000L, 0));
        assertEquals(Long.MAX_VALUE, store.calculateExpiredTime(1_000L, -1));
        assertEquals(1_000L + Integer.MAX_VALUE * 1000L, store.calculateExpiredTime(1_000L, Integer.MAX_VALUE));
    }
}
