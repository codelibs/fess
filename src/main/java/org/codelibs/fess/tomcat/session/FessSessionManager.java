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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.session.ManagerBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

/**
 * A session manager that shares sessions between Fess nodes through a store that the web application provides.
 *
 * <p>This class is shipped to Tomcat's class loader, whereas the web application loader is parent-last, so it
 * must not reference any web application type. The store is reached through a bridge: a
 * {@code Map<String, Function<Map<String, Object>, Object>>} (JDK types only) that the web application stores in the
 * ServletContext attribute {@value #BRIDGE_ATTRIBUTE} once it is up. Session attribute values cross the bridge as
 * opaque objects. Until the bridge is installed, and whenever the store fails, sessions behave as plain in-memory
 * Tomcat sessions.</p>
 *
 * <p>Bridge operations:</p>
 * <ul>
 * <li>{@value #OP_LOAD}: {@code {id}} and optionally the {@code digest} the caller holds (the result has no {@code attributes} if it is unchanged) to {@code null} (not found) or {@code {creationTime, lastAccessedTime,
 * maxInactiveInterval, digest, attributes}}; throws a {@link RuntimeException} if the store is unreachable.</li>
 * <li>{@value #OP_SAVE}: {@code {id, creationTime, lastAccessedTime, maxInactiveInterval, attributes, digest,
 * persistedAt}} to {@code {digest, persistedAt, skippedNames}}, where {@code skippedNames}
 * are the attributes the store could not keep; they are never removed or overwritten by a state read from the store.
 * A session that was stored before is never created again: if its document is gone (deleted on another node), the result is
 * {@code {gone=true}} and the session is expired locally.</li>
 * <li>{@value #OP_DELETE}: {@code {id}} to {@code null}.</li>
 * <li>{@value #OP_PURGE}: {@code {}} to the number of deleted expired sessions.</li>
 * </ul>
 *
 * <p>The other direction is a {@code Consumer<Predicate<Object>>} that this manager stores in the ServletContext
 * attribute {@value #PERSIST_HOLDING_ATTRIBUTE} while it runs: it saves every session of this node that holds an
 * attribute value the predicate accepts, right away and on the calling thread. A background task that changed an
 * object a session holds, outside of any request, uses it so that the other nodes see the change before the next request
 * of that session reaches this node.</p>
 */
public class FessSessionManager extends ManagerBase {

    private static final Log log = LogFactory.getLog(FessSessionManager.class);

    /** The ServletContext attribute that holds the bridge. */
    public static final String BRIDGE_ATTRIBUTE = "fess.session.store";

    /** The ServletContext attribute that holds the function saving the sessions that hold a given value. */
    public static final String PERSIST_HOLDING_ATTRIBUTE = "fess.session.persistHolding";

    /** The bridge operation that reads a session. */
    public static final String OP_LOAD = "load";

    /** The bridge operation that writes a session. */
    public static final String OP_SAVE = "save";

    /** The bridge operation that deletes a session. */
    public static final String OP_DELETE = "delete";

    /** The bridge operation that deletes the expired sessions. */
    public static final String OP_PURGE = "purge";

    /** The number of session ids remembered as not to be written. */
    protected static final int DO_NOT_PERSIST_LIMIT = 1024;

    /** The minimum interval between two warnings about a failing store. */
    protected static final long WARN_INTERVAL_MS = 60_000L;

    private static final int LOCK_STRIPES = 16;

    /** The sessions that the current request thread touched, while a {@link FessSessionValve} is processing it. */
    protected final ThreadLocal<List<FessSession>> accessed = new ThreadLocal<>();

    /**
     * The result of {@link #findSession(String)} per session id for the current request, while a
     * {@link FessSessionValve} is processing it. Tomcat looks the session up again for every
     * {@code isRequestedSessionIdValid()} and for every {@code getSession(false)} that finds nothing, and each lookup
     * would be a read of the store. A {@code null} value is a session that the store does not have.
     */
    protected final ThreadLocal<Map<String, Session>> lookups = new ThreadLocal<>();

    /** The session ids whose store document could not be read and that Tomcat may re-create under the same id. */
    protected final Set<String> doNotPersistIds =
            Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<String, Boolean>() {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(final Map.Entry<String, Boolean> eldest) {
                    return size() > DO_NOT_PERSIST_LIMIT;
                }
            }));

    /**
     * The session ids that were invalidated here and whose store document could not be deleted. Without this, the next
     * request carrying the cookie would read the surviving document and bring the logged-out session back on this node.
     * Another node cannot know it and accepts the session until the document expires.
     */
    protected final Set<String> revokedIds = Collections.newSetFromMap(Collections.synchronizedMap(new LinkedHashMap<String, Boolean>() {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(final Map.Entry<String, Boolean> eldest) {
            return size() > DO_NOT_PERSIST_LIMIT;
        }
    }));

    private final Object[] locks = new Object[LOCK_STRIPES];

    private volatile Map<String, Function<Map<String, Object>, Object>> bridge;

    private volatile long lastWarnedAt;

    /**
     * Creates a manager.
     */
    public FessSessionManager() {
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
    }

    @Override
    public Session createEmptySession() {
        return new FessSession(this);
    }

    @Override
    public Session createSession(final String sessionId) {
        final Session session = super.createSession(sessionId);
        if (sessionId != null && doNotPersistIds.remove(sessionId) && session instanceof final FessSession fessSession) {
            fessSession.setDoNotPersist(true);
        }
        return session;
    }

    @Override
    public Session findSession(final String id) throws IOException {
        if (id == null) {
            return null;
        }
        if (revokedIds.contains(id)) {
            return null;
        }
        final Function<Map<String, Object>, Object> load = operation(OP_LOAD);
        if (load == null) {
            return sessions.get(id);
        }
        final Map<String, Session> memo = lookups.get();
        if (memo != null && memo.containsKey(id)) {
            final Session known = memo.get(id);
            if (known == null) {
                // Nothing in the store a moment ago; the session may have been created in this request since
                return sessions.get(id);
            }
            if (known.isValid() && id.equals(known.getIdInternal())) {
                return known;
            }
        }
        final Session found = lookup(id, load);
        if (memo != null) {
            memo.put(id, found);
        }
        return found;
    }

    private Session lookup(final String id, final Function<Map<String, Object>, Object> load) {
        Session local = sessions.get(id);
        if (local == null) {
            synchronized (locks[(id.hashCode() & Integer.MAX_VALUE) % LOCK_STRIPES]) {
                local = sessions.get(id);
                if (local == null) {
                    return loadAndCreate(id, load);
                }
            }
        }
        return local instanceof final FessSession fessSession ? refresh(fessSession, load) : local;
    }

    /**
     * Reads a session unknown to this node from the store and creates it locally.
     *
     * @param id the session id
     * @param load the load operation
     * @return the new local session, or {@code null} if the store has none or cannot be read
     */
    protected Session loadAndCreate(final String id, final Function<Map<String, Object>, Object> load) {
        final Map<String, Object> state;
        try {
            state = toMap(load.apply(idArgs(id)));
        } catch (final RuntimeException e) {
            doNotPersistIds.add(id);
            warn("Failed to load the session from the session store. The session is kept in memory only.", e);
            return null;
        }
        if (state == null) {
            return null;
        }
        final Session session = super.createSession(id);
        if (session instanceof final FessSession fessSession) {
            apply(fessSession, state, true);
            fessSession.setPersistedAt(System.currentTimeMillis());
        }
        session.setNew(false);
        return session;
    }

    /**
     * Brings a local session up to date with the store. The session object is never replaced.
     *
     * @param session the local session
     * @param load the load operation
     * @return the session, or {@code null} if the store dropped it (invalidated or expired on another node)
     */
    protected Session refresh(final FessSession session, final Function<Map<String, Object>, Object> load) {
        // The read and its application are one step with respect to the writes of this session, so that a state read
        // before a background write (persistHolding) cannot be applied after it and put the older attributes back.
        synchronized (session.getPersistLock()) {
            return refreshLocked(session, load);
        }
    }

    private Session refreshLocked(final FessSession session, final Function<Map<String, Object>, Object> load) {
        final Map<String, Object> state;
        try {
            final Map<String, Object> args = idArgs(session.getIdInternal());
            if (session.getDigest() != null) {
                // Lets the store skip the payload if it did not change.
                args.put("digest", session.getDigest());
            }
            state = toMap(load.apply(args));
        } catch (final RuntimeException e) {
            session.setDoNotPersist(true);
            warn("Failed to load the session from the session store. The session is kept in memory only.", e);
            return session;
        }
        session.setDoNotPersist(false);
        if (state == null) {
            if (session.getPersistedAt() > 0L) {
                // It was in the store, and is not any more: invalidated or expired elsewhere. Not an explicit
                // invalidate here, so the store is left alone.
                session.expire();
                return null;
            }
            return session;
        }
        final boolean changed = !String.valueOf(state.get("digest")).equals(session.getDigest());
        apply(session, state, changed);
        if (changed) {
            session.setPersistedAt(System.currentTimeMillis());
        }
        return session;
    }

    @SuppressWarnings("unchecked")
    private void apply(final FessSession session, final Map<String, Object> state, final boolean withAttributes) {
        session.applyStoreState(number(state.get("creationTime"), session.getCreationTimeInternal()),
                number(state.get("lastAccessedTime"), 0L), (int) number(state.get("maxInactiveInterval"), session.getMaxInactiveInterval()),
                (String) state.get("digest"), withAttributes ? (Map<String, Object>) state.get("attributes") : null);
    }

    /**
     * Writes a session to the store. Failures are logged and swallowed.
     *
     * @param session the session
     */
    protected void persist(final FessSession session) {
        final Function<Map<String, Object>, Object> save = operation(OP_SAVE);
        if (save == null) {
            return;
        }
        // The snapshot and the write of one session never interleave with another's, so that a write of an older
        // snapshot (the end of a request) cannot land after a newer one (a background change) and undo it.
        synchronized (session.getPersistLock()) {
            persist(session, save);
        }
    }

    private void persist(final FessSession session, final Function<Map<String, Object>, Object> save) {
        if (session.isDoNotPersist() || !session.isAlive()) {
            return;
        }
        final Map<String, Object> args = idArgs(session.getIdInternal());
        args.put("creationTime", session.getCreationTimeInternal());
        args.put("lastAccessedTime", session.getActivityTime());
        args.put("maxInactiveInterval", session.getMaxInactiveInterval());
        args.put("attributes", session.snapshotAttributes());
        args.put("digest", session.getDigest());
        args.put("persistedAt", session.getPersistedAt());
        try {
            final Map<String, Object> result = toMap(save.apply(args));
            if (result != null && Boolean.TRUE.equals(result.get("gone"))) {
                // Deleted on another node (logout, a new session id) while this request was running. Like in
                // refresh(), this is not an explicit invalidate here, so the store is left alone and the next
                // request with the cookie gets a new session.
                session.expire();
            } else if (result != null) {
                session.setDigest((String) result.get("digest"));
                final Object skipped = result.get("skippedNames");
                session.setSkippedNames(
                        skipped instanceof final Collection<?> names ? names.stream().map(String::valueOf).collect(Collectors.toSet())
                                : null);
                session.setPersistedAt(number(result.get("persistedAt"), session.getPersistedAt()));
            }
        } catch (final RuntimeException e) {
            warn("Failed to save the session to the session store.", e);
        }
    }

    @Override
    public void remove(final Session session, final boolean update) {
        super.remove(session, update);
        if (update && session instanceof final FessSession fessSession && fessSession.isInvalidated()) {
            deleteFromStore(fessSession.getIdInternal());
        }
    }

    @Override
    protected void changeSessionId(final Session session, final String newId, final boolean notifySessionListeners,
            final boolean notifyContainerListeners) {
        if (!(session instanceof final FessSession fessSession)) {
            super.changeSessionId(session, newId, notifySessionListeners, notifyContainerListeners);
            return;
        }
        // Under the lock of the writes of the session: a write (persistHolding runs on another thread) reads the id inside
        // that lock, so it happens wholly before this change, under the old id that is deleted below, or wholly after it,
        // under the new one. Otherwise it could create the old document again, or hand the result of the old id (digest,
        // persistedAt) to the renamed session, whose next write would then find no document and expire it.
        synchronized (fessSession.getPersistLock()) {
            final String oldId = session.getIdInternal();
            super.changeSessionId(session, newId, notifySessionListeners, notifyContainerListeners);
            if (oldId != null) {
                deleteFromStore(oldId);
            }
        }
    }

    /**
     * Deletes the expired sessions from the store, in addition to the local expiry. Any node may do it.
     */
    @Override
    public void processExpires() {
        super.processExpires();
        final Function<Map<String, Object>, Object> purge = operation(OP_PURGE);
        if (purge != null) {
            try {
                purge.apply(new HashMap<>());
            } catch (final RuntimeException e) {
                warn("Failed to purge the expired sessions from the session store.", e);
            }
        }
    }

    /**
     * Expiry by this node's timeout must not delete the store document, as another node may have renewed it.
     * Only an explicit invalidate does (see {@link #remove(Session, boolean)}).
     *
     * @param id the session id
     */
    protected void deleteFromStore(final String id) {
        final Function<Map<String, Object>, Object> delete = operation(OP_DELETE);
        if (delete == null) {
            return;
        }
        try {
            delete.apply(idArgs(id));
        } catch (final RuntimeException e) {
            revokedIds.add(id);
            warn("Failed to delete the session from the session store. This node refuses the session from now on, "
                    + "but other nodes accept it until it expires.", e);
        }
    }

    /**
     * Starts collecting the sessions the current thread touches.
     *
     * @return {@code true} if the caller must call {@link #endRequest()}; {@code false} if an outer caller already collects
     */
    protected boolean beginRequest() {
        if (accessed.get() != null) {
            return false;
        }
        accessed.set(new ArrayList<>());
        lookups.set(new HashMap<>());
        return true;
    }

    /**
     * Stops collecting and saves the sessions the current thread touched.
     */
    protected void endRequest() {
        final List<FessSession> touched = accessed.get();
        accessed.remove();
        lookups.remove();
        if (touched != null) {
            for (final FessSession session : touched) {
                persist(session);
            }
        }
    }

    /**
     * Saves the sessions the current thread touched so far and keeps collecting, so that {@link #endRequest()} saves
     * what changed afterwards. {@link FessSessionFilter} calls it before the response is committed: once the client has
     * the response, its next request may reach another node, which must find the session in the store.
     */
    protected void persistTouched() {
        final List<FessSession> touched = accessed.get();
        if (touched != null) {
            for (final FessSession session : new ArrayList<>(touched)) {
                persist(session);
            }
        }
    }

    /**
     * Saves every session of this node that holds an attribute value the predicate accepts. Unlike
     * {@link #persistTouched()} it needs no request: it is for a background thread that changed an object a session
     * holds. Failures are logged and swallowed.
     *
     * <p>The predicate may be asked about the same value more than once, and may bring a value up to date before it
     * accepts it: a session that this node refreshed from the store holds copies of the objects it held before, so the
     * object the background thread changed is not necessarily in the session. For a session that matches, the state of the
     * store is read again first (what other nodes wrote since this node last read it is kept) and the predicate is
     * applied to what the session holds then, so that a write of this node never undoes a change of another.</p>
     *
     * @param holds tells whether an attribute value is, or is a copy of, the one that changed
     */
    protected void persistHolding(final Predicate<Object> holds) {
        for (final Session session : findSessions()) {
            if (!(session instanceof final FessSession fessSession) || !fessSession.isAlive()) {
                continue;
            }
            try {
                if (!holdsAny(fessSession, holds)) {
                    continue;
                }
                synchronized (fessSession.getPersistLock()) {
                    final Function<Map<String, Object>, Object> load = operation(OP_LOAD);
                    if (load != null && refresh(fessSession, load) == null) {
                        continue;
                    }
                    if (holdsAny(fessSession, holds)) {
                        persist(fessSession);
                    }
                }
            } catch (final RuntimeException e) {
                warn("Failed to save the session after one of its attributes changed.", e);
            }
        }
    }

    private static boolean holdsAny(final FessSession session, final Predicate<Object> holds) {
        return session.snapshotAttributes().values().stream().anyMatch(holds);
    }

    /**
     * Records that the current thread touched a session.
     *
     * @param session the session
     */
    protected void markAccessed(final FessSession session) {
        final List<FessSession> touched = accessed.get();
        if (touched != null && !touched.contains(session)) {
            touched.add(session);
        }
    }

    @SuppressWarnings("unchecked")
    private Function<Map<String, Object>, Object> operation(final String name) {
        Map<String, Function<Map<String, Object>, Object>> current = bridge;
        if (current == null) {
            final Context context = getContext();
            final Object value = context != null ? context.getServletContext().getAttribute(BRIDGE_ATTRIBUTE) : null;
            if (!(value instanceof Map)) {
                return null;
            }
            current = (Map<String, Function<Map<String, Object>, Object>>) value;
            bridge = current;
        }
        return current.get(name);
    }

    private static Map<String, Object> idArgs(final String id) {
        final Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        return args;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(final Object value) {
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    private static long number(final Object value, final long defaultValue) {
        return value instanceof final Number n ? n.longValue() : defaultValue;
    }

    /**
     * Logs a warning, at most once per {@value #WARN_INTERVAL_MS} ms, so that a store outage does not flood the log.
     *
     * @param message the message
     * @param cause the cause
     */
    protected void warn(final String message, final Throwable cause) {
        final long now = System.currentTimeMillis();
        if (now - lastWarnedAt >= WARN_INTERVAL_MS) {
            lastWarnedAt = now;
            log.warn(message, cause);
        } else if (log.isDebugEnabled()) {
            log.debug(message, cause);
        }
    }

    @Override
    public void load() throws ClassNotFoundException, IOException {
        // The store replaces the Java serialization based persistence of StandardManager.
    }

    @Override
    public void unload() throws IOException {
        // The store replaces the Java serialization based persistence of StandardManager.
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        final Context context = getContext();
        if (context != null) {
            final Consumer<Predicate<Object>> persistHolding = this::persistHolding;
            context.getServletContext().setAttribute(PERSIST_HOLDING_ATTRIBUTE, persistHolding);
        }
        setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        final Context context = getContext();
        if (context != null) {
            context.getServletContext().removeAttribute(PERSIST_HOLDING_ATTRIBUTE);
        }
        // Local copies only: expiry does not delete from the store, so the sessions stay usable on other nodes.
        for (final Session session : findSessions()) {
            try {
                if (session.isValid()) {
                    session.expire();
                }
            } catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
            } finally {
                session.recycle();
            }
        }
        super.stopInternal();
    }
}
