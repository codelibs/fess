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
package org.codelibs.fess.tomcat.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Session;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.StandardSessionIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FessSessionManagerTest {

    private final Tomcat tomcat = new Tomcat();

    private File baseDir;

    private int nodes;

    @BeforeEach
    public void setUp() throws Exception {
        baseDir = Files.createTempDirectory("fess-session-test").toFile();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        baseDir.delete();
    }

    private FessSessionManager newManager(final FakeSessionStore store) {
        final StandardContext context = (StandardContext) tomcat.addContext("/node" + nodes++, baseDir.getAbsolutePath());
        final FessSessionManager manager = new FessSessionManager();
        manager.setSessionIdGenerator(new StandardSessionIdGenerator());
        context.setManager(manager);
        if (store != null) {
            context.getServletContext().setAttribute(FessSessionManager.BRIDGE_ATTRIBUTE, store.bridge());
        }
        return manager;
    }

    /** Runs the way the valve does around one request: touches the session, then saves. */
    private FessSession request(final FessSessionManager manager, final String id, final boolean create,
            final java.util.function.Consumer<FessSession> body) throws Exception {
        assertTrue(manager.beginRequest());
        try {
            Session session = id != null ? manager.findSession(id) : null;
            if (session != null && !session.isValid()) {
                session = null;
            }
            if (session == null && create) {
                session = manager.createSession(id);
            }
            if (session == null) {
                return null;
            }
            session.access();
            if (body != null) {
                body.accept((FessSession) session);
            }
            return (FessSession) session;
        } finally {
            manager.endRequest();
        }
    }

    @Test
    public void test_createEmptySession_isFessSession() {
        assertInstanceOf(FessSession.class, newManager(null).createEmptySession());
    }

    @Test
    public void test_withoutBridge_behavesAsMemory() throws Exception {
        final FessSessionManager manager = newManager(null);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        assertSame(session, manager.findSession(session.getId()));
        assertNull(manager.findSession("unknown"));
    }

    @Test
    public void test_newSession_isSavedAtEndOfRequest() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        assertEquals(1, store.docs.size());
        assertTrue(store.docs.containsKey(session.getId()));
        assertNotNull(session.getDigest());
        assertTrue(session.getPersistedAt() > 0);
    }

    @Test
    public void test_sessionNotTouched_isNotSaved() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, null);
        store.calls.clear();
        assertTrue(manager.beginRequest());
        manager.endRequest();
        assertTrue(store.calls.isEmpty());
        assertNotNull(session);
    }

    @Test
    public void test_unchangedSession_isNotWritten() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        assertEquals(1, store.fullWrites);
        request(manager, session.getId(), false, null);
        request(manager, session.getId(), false, null);
        assertEquals(1, store.fullWrites);
        assertEquals(0, store.touches);
    }

    @Test
    public void test_unchangedSession_isLoadedWithoutAttributes() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        request(manager, session.getId(), false, null);
        assertEquals(1, store.unchangedLoads);
        assertEquals("v", session.getAttribute("k"));
        // another node holds no copy, so it reads the attributes
        final FessSessionManager other = newManager(store);
        final FessSession onOther = request(other, session.getId(), false, null);
        assertEquals("v", onOther.getAttribute("k"));
        assertEquals(1, store.unchangedLoads);
        // a change on the other node reaches the first one with the attributes
        request(other, session.getId(), false, s -> s.setAttribute("k", "w"));
        assertEquals("w", request(manager, session.getId(), false, null).getAttribute("k"));
    }

    @Test
    public void test_lookupsInOneRequest_readTheStoreOnce() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        store.calls.clear();

        assertTrue(manager.beginRequest());
        try {
            assertSame(session, manager.findSession(session.getId()));
            assertSame(session, manager.findSession(session.getId()));
            assertNull(manager.findSession("unknown"));
            assertNull(manager.findSession("unknown"));
            // a session created under the id that was missing is found
            final Session created = manager.createSession("unknown");
            assertSame(created, manager.findSession("unknown"));
        } finally {
            manager.endRequest();
        }
        assertEquals(2, store.calls.stream().filter("load"::equals).count());

        // a lookup remembered under an id that the session no longer has is not answered with that session
        assertTrue(manager.beginRequest());
        try {
            final String oldId = session.getId();
            assertSame(session, manager.findSession(oldId));
            manager.rotateSessionId(session);
            assertNull(manager.findSession(oldId));
        } finally {
            manager.endRequest();
        }

        // the next request reads again
        store.calls.clear();
        assertNotNull(request(manager, session.getId(), false, null));
        assertEquals(1, store.calls.stream().filter("load"::equals).count());
    }

    @Test
    public void test_unchangedSession_isTouchedAfterInterval() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        store.touchIntervalMs = 0L;
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        request(manager, session.getId(), false, null);
        assertEquals(1, store.fullWrites);
        assertEquals(1, store.touches);
    }

    @Test
    public void test_changedSession_isWritten() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        request(manager, session.getId(), false, s -> s.setAttribute("k", "w"));
        assertEquals(2, store.fullWrites);
        assertEquals("w", ((java.util.Map<?, ?>) store.docs.get(session.getId()).get("attributes")).get("k"));
    }

    @Test
    public void test_otherNode_loadsSessionAndSharesChanges() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSessionManager nodeB = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));

        final FessSession onB = request(nodeB, onA.getId(), false, null);
        assertNotNull(onB);
        assertNotSame(onA, onB);
        assertEquals("alice", onB.getAttribute("user"));
        assertFalse(onB.isNew());
        assertEquals(onA.getCreationTimeInternal(), onB.getCreationTimeInternal());
        assertEquals(onA.getMaxInactiveInterval(), onB.getMaxInactiveInterval());

        request(nodeB, onA.getId(), false, s -> s.setAttribute("flash", "hello"));
        request(nodeB, onA.getId(), false, s -> s.removeAttribute("user"));

        final FessSession again = request(nodeA, onA.getId(), false, null);
        assertSame(onA, again, "the local session object must be updated in place, not replaced");
        assertEquals("hello", again.getAttribute("flash"));
        assertNull(again.getAttribute("user"));
    }

    @Test
    public void test_skippedAttribute_survivesApplyFromOtherNode() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSessionManager nodeB = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> {
            s.setAttribute("user", "alice");
            s.setAttribute("skipPassword", "plain");
        });
        assertEquals(java.util.Set.of("skipPassword"), onA.getSkippedNames());
        assertFalse(((java.util.Map<?, ?>) store.docs.get(onA.getId()).get("attributes")).containsKey("skipPassword"));

        // another node changes the shared content, which A applies in place
        request(nodeB, onA.getId(), false, s -> s.setAttribute("flash", "hello"));
        final FessSession again = request(nodeA, onA.getId(), false, null);
        assertSame(onA, again);
        assertEquals("hello", again.getAttribute("flash"));
        assertEquals("plain", again.getAttribute("skipPassword"), "a skipped attribute is not removed by the apply");
        assertNull(nodeB.findSession(onA.getId()).getSession().getAttribute("skipPassword"));
    }

    @Test
    public void test_skippedAttribute_isNotOverwrittenByStoreValue() {
        final FessSession session = (FessSession) newManager(null).createEmptySession();
        session.setValid(true);
        session.setAttribute("skipPassword", "local");
        session.setSkippedNames(java.util.Set.of("skipPassword"));
        session.applyStoreState(1L, 2L, 30, "d", java.util.Map.of("skipPassword", "remote", "x", "y"));
        assertEquals("local", session.getAttribute("skipPassword"));
        assertEquals("y", session.getAttribute("x"));
    }

    @Test
    public void test_syncedAttributeRemovedOnOtherNode_isRemovedLocally() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSessionManager nodeB = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> {
            s.setAttribute("user", "alice");
            s.setAttribute("flash", "hello");
            s.setAttribute("skipPassword", "plain");
        });
        request(nodeB, onA.getId(), false, s -> s.removeAttribute("flash"));
        final FessSession again = request(nodeA, onA.getId(), false, null);
        assertNull(again.getAttribute("flash"));
        assertEquals("alice", again.getAttribute("user"));
        assertEquals("plain", again.getAttribute("skipPassword"));
    }

    @Test
    public void test_regenerateSessionId_invalidatesOldAndSavesAllAttributesUnderNewId() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession old = request(manager, null, true, s -> s.setAttribute("user", "alice"));
        final String oldId = old.getId();
        final FessSession[] fresh = new FessSession[1];
        assertTrue(manager.beginRequest());
        try {
            // what SimpleSessionManager.regenerateSessionId does: invalidate, create a new session, copy the attributes back
            old.access();
            old.invalidate();
            fresh[0] = (FessSession) manager.createSession(null);
            fresh[0].access();
            fresh[0].setAttribute("user", "alice");
            fresh[0].setAttribute("flash", "hello");
        } finally {
            manager.endRequest();
        }
        assertNotEquals(oldId, fresh[0].getId());
        assertFalse(store.docs.containsKey(oldId));
        assertEquals(1, store.docs.size());
        assertEquals("hello", ((java.util.Map<?, ?>) store.docs.get(fresh[0].getId()).get("attributes")).get("flash"));
    }

    @Test
    public void test_unknownSession_isNull() throws Exception {
        assertNull(newManager(new FakeSessionStore()).findSession("nope"));
    }

    @Test
    public void test_invalidate_deletesFromStoreAndLogsOutOtherNode() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSessionManager nodeB = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));
        final FessSession onB = request(nodeB, onA.getId(), false, null);

        onB.invalidate();
        assertTrue(store.docs.isEmpty());

        // node A still has the session in memory but the store dropped it: it must not stay logged in
        assertNull(nodeA.findSession(onA.getId()));
        assertFalse(onA.isValid());
        assertNull(nodeA.findSession(onA.getId()));
    }

    @Test
    public void test_invalidateWhileStoreRejectsDelete_isNotBroughtBackOnThisNode() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("user", "alice"));
        final String id = session.getId();

        store.down = true;
        session.invalidate();
        store.down = false;
        // the document is still there, because the delete failed
        assertTrue(store.docs.containsKey(id));

        // the store is back, and the cookie of the logged-out session must not rebuild it
        assertNull(manager.findSession(id));
        assertNull(request(manager, id, false, null));
    }

    @Test
    public void test_invalidate_withWorkingStore_doesNotRefuseTheIdAfterwards() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("user", "alice"));
        session.invalidate();
        assertTrue(store.docs.isEmpty());
        assertTrue(manager.revokedIds.isEmpty());
    }

    @Test
    public void test_localExpiry_doesNotDeleteFromStore() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        session.expire();
        assertEquals(1, store.docs.size());
        assertEquals(0, store.count("delete"));
        assertEquals(0, manager.findSessions().length);
    }

    @Test
    public void test_staleLocalSession_isRenewedByStore() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("k", "v"));
        final long old = System.currentTimeMillis() - 10_000L;
        // the store says another node used the session just now
        store.docs.get(onA.getId()).put("lastAccessedTime", System.currentTimeMillis());
        onA.setMaxInactiveInterval(5);
        onA.setPersistedAt(old);
        final FessSession found = (FessSession) nodeA.findSession(onA.getId());
        assertSame(onA, found);
        assertTrue(onA.getLastAccessedTimeInternal() >= System.currentTimeMillis() - 1000L);
        assertEquals(1800, onA.getMaxInactiveInterval(), "maxInactiveInterval comes from the store");
    }

    @Test
    public void test_changeSessionId_deletesOldAndWritesNew() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        final String oldId = session.getId();
        assertTrue(manager.beginRequest());
        final String newId;
        try {
            session.access();
            newId = manager.rotateSessionId(session);
        } finally {
            manager.endRequest();
        }
        assertNotEquals(oldId, newId);
        assertFalse(store.docs.containsKey(oldId));
        assertTrue(store.docs.containsKey(newId));
        assertNotNull(session.getDigest());
        assertTrue(session.isValid(), "the new id is created in the store, not rejected as gone");
        assertEquals(0, store.gones);
    }

    @Test
    public void test_changeSessionId_resetsPersistedAt() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        assertTrue(session.getPersistedAt() > 0);

        manager.rotateSessionId(session);

        assertEquals(0L, session.getPersistedAt());
        assertNull(session.getDigest());
    }

    @Test
    public void test_sessionDeletedWhileInFlight_isExpiredLocallyAndNotRecreated() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));
        final String id = onA.getId();

        // the changed content is saved at the end of a request that outlives the logout on another node
        final FessSession inFlight = request(nodeA, id, false, s -> {
            store.docs.remove(id);
            s.setAttribute("user", "bob");
        });

        assertNotNull(inFlight);
        assertEquals(1, store.gones);
        assertTrue(store.docs.isEmpty(), "a deleted session must not be written again");
        assertFalse(inFlight.isValid());
        assertEquals(0, store.count("delete"), "the local expiry does not delete from the store");
        assertEquals(0, nodeA.findSessions().length);
        assertNull(nodeA.findSession(id));
    }

    @Test
    public void test_sessionDeletedWhileInFlight_touchPathIsExpiredLocallyAndNotRecreated() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        store.touchIntervalMs = 0L;
        final FessSessionManager nodeA = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));
        final String id = onA.getId();

        final FessSession inFlight = request(nodeA, id, false, s -> store.docs.remove(id));

        assertEquals(1, store.gones);
        assertTrue(store.docs.isEmpty());
        assertFalse(inFlight.isValid());
        assertNull(nodeA.findSession(id));
    }

    @Test
    public void test_goneSession_isNotSavedAgain() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));
        final String id = onA.getId();
        request(nodeA, id, false, s -> {
            store.docs.remove(id);
            s.setAttribute("user", "bob");
        });
        store.calls.clear();

        // an old request still holding the expired session ends: nothing is written
        assertTrue(nodeA.beginRequest());
        try {
            onA.access();
        } finally {
            nodeA.endRequest();
        }

        assertEquals(0, store.count("save"));
        assertTrue(store.docs.isEmpty());
    }

    @Test
    public void test_loadFailure_keepsLocalSessionAndStopsWriting() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        store.down = true;
        final int savesBefore = store.count("save");
        final FessSession again = request(manager, session.getId(), false, s -> s.setAttribute("k", "changed"));
        assertSame(session, again);
        assertTrue(again.isDoNotPersist());
        assertEquals("changed", again.getAttribute("k"));
        assertEquals(savesBefore, store.count("save"));

        store.down = false;
        request(manager, session.getId(), false, null);
        assertFalse(session.isDoNotPersist(), "a successful load re-enables writing");
    }

    @Test
    public void test_loadFailureWithoutLocalSession_reusedIdIsNotWritten() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager nodeA = newManager(store);
        final FessSessionManager nodeB = newManager(store);
        final FessSession onA = request(nodeA, null, true, s -> s.setAttribute("user", "alice"));
        final String savedDigest = (String) store.docs.get(onA.getId()).get("digest");

        store.down = true;
        assertNull(nodeB.findSession(onA.getId()));
        // Tomcat re-creates the session under the requested id; it must not overwrite the stored one
        final int savesBefore = store.count("save");
        final FessSession reused = request(nodeB, onA.getId(), true, s -> s.setAttribute("user", "nobody"));
        assertTrue(reused.isDoNotPersist());
        assertEquals(savesBefore, store.count("save"));
        assertEquals(savedDigest, store.docs.get(onA.getId()).get("digest"));

        // once the store is readable again the session adopts what is stored
        store.down = false;
        final FessSession recovered = request(nodeB, onA.getId(), false, null);
        assertSame(reused, recovered);
        assertFalse(recovered.isDoNotPersist());
        assertEquals("alice", recovered.getAttribute("user"));
    }

    @Test
    public void test_saveFailure_isSwallowed() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        store.down = true;
        final FessSession session = request(manager, null, true, s -> s.setAttribute("k", "v"));
        assertNotNull(session);
        assertNull(session.getDigest());
    }

    @Test
    public void test_processExpires_purgesStore() throws Exception {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        request(manager, null, true, s -> s.setAttribute("k", "v"));
        final java.util.Map<String, Object> expired = new java.util.HashMap<>(store.docs.values().iterator().next());
        expired.put("expiredTime", 1L);
        store.docs.put("old", expired);
        manager.processExpires();
        assertEquals(1, store.purges);
        assertFalse(store.docs.containsKey("old"));
        assertEquals(1, store.docs.size());
    }

    @Test
    public void test_purgeFailure_isSwallowed() {
        final FakeSessionStore store = new FakeSessionStore();
        final FessSessionManager manager = newManager(store);
        store.down = true;
        manager.processExpires();
    }

    @Test
    public void test_doNotPersistIds_isBounded() {
        final FessSessionManager manager = newManager(null);
        final List<String> ids = new ArrayList<>();
        for (int i = 0; i < FessSessionManager.DO_NOT_PERSIST_LIMIT + 10; i++) {
            ids.add("id" + i);
            manager.doNotPersistIds.add("id" + i);
        }
        assertEquals(FessSessionManager.DO_NOT_PERSIST_LIMIT, manager.doNotPersistIds.size());
        assertFalse(manager.doNotPersistIds.contains("id0"));
    }

    @Test
    public void test_loadAndUnload_areNoOps() throws Exception {
        final FessSessionManager manager = newManager(null);
        manager.load();
        manager.unload();
    }
}
