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
import java.util.function.Function;
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
 */
public class FessSessionManager extends ManagerBase {

    private static final Log log = LogFactory.getLog(FessSessionManager.class);

    /** The ServletContext attribute that holds the bridge. */
    public static final String BRIDGE_ATTRIBUTE = "fess.session.store";

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
        if (save == null || session.isDoNotPersist() || !session.isValid()) {
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
        final String oldId = session.getIdInternal();
        super.changeSessionId(session, newId, notifySessionListeners, notifyContainerListeners);
        if (oldId != null && session instanceof FessSession) {
            deleteFromStore(oldId);
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
            warn("Failed to delete the session from the session store.", e);
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
        setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
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
