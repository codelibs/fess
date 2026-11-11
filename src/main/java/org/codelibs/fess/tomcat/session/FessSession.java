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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;

/**
 * A {@link StandardSession} that remembers what the shared session store knows about it.
 *
 * <p>This class lives in Tomcat's class loader, so it references only Tomcat, Servlet and JDK types.
 * Attribute values are opaque {@link Object}s to it.</p>
 */
public class FessSession extends StandardSession {

    private static final long serialVersionUID = 1L;

    /** The digest of the content last written to, or read from, the store. {@code null} if never stored. */
    protected transient volatile String digest;

    /** The time (epoch millis) the store was last written or touched for this session. {@code 0} if never. */
    protected transient volatile long persistedAt;

    /**
     * The names of the attributes that the store could not keep at the last save (for example a password or a type
     * the store cannot serialize). To the store they look the same as removed attributes, so they are never
     * removed or overwritten by a state read from the store.
     */
    protected transient volatile Set<String> skippedNames = Set.of();

    /** Whether this session must not be written, because the store could not be read for it. */
    protected transient volatile boolean doNotPersist;

    /** Whether {@link #invalidate()} was called, as opposed to an expiry by timeout. */
    protected transient volatile boolean invalidated;

    /**
     * Creates a session.
     *
     * @param manager the owning manager
     */
    public FessSession(final Manager manager) {
        super(manager);
    }

    /**
     * Records the access in the manager so that the valve saves the session at the end of the request.
     */
    @Override
    public void access() {
        super.access();
        if (manager instanceof final FessSessionManager fessManager) {
            fessManager.markAccessed(this);
        }
    }

    /**
     * Marks the session as explicitly invalidated, which makes the manager delete it from the store.
     */
    @Override
    public void invalidate() {
        if (isValidInternal()) {
            invalidated = true;
        }
        super.invalidate();
    }

    /**
     * A new session id means a new store document, so everything known about the old one is dropped.
     *
     * @param id the new id
     * @param notify whether to notify the listeners
     */
    @Override
    public void setId(final String id, final boolean notify) {
        if (this.id != null && !this.id.equals(id)) {
            digest = null;
            persistedAt = 0L;
        }
        super.setId(id, notify);
    }

    /**
     * Returns a copy of the attributes, safe to hand to the store while other requests keep using the session.
     *
     * @return the attribute name to value map
     */
    public Map<String, Object> snapshotAttributes() {
        return new HashMap<>(attributes);
    }

    /**
     * Returns the time of the last activity: the later of the current request's start and the previous request's end.
     *
     * @return epoch millis
     */
    public long getActivityTime() {
        return Math.max(getThisAccessedTimeInternal(), getLastAccessedTimeInternal());
    }

    /**
     * Applies a state read from the store to this session, in place. The session object is never replaced,
     * because other components synchronize on it.
     *
     * @param storeCreationTime the creation time stored
     * @param storeLastAccessedTime the last activity time stored
     * @param storeMaxInactiveInterval the max inactive interval stored, in seconds
     * @param storeDigest the digest stored
     * @param storeAttributes the attributes stored, or {@code null} to leave the attributes as they are
     */
    public void applyStoreState(final long storeCreationTime, final long storeLastAccessedTime, final int storeMaxInactiveInterval,
            final String storeDigest, final Map<String, Object> storeAttributes) {
        creationTime = storeCreationTime;
        maxInactiveInterval = storeMaxInactiveInterval;
        lastAccessedTime = Math.max(lastAccessedTime, storeLastAccessedTime);
        thisAccessedTime = Math.max(thisAccessedTime, storeLastAccessedTime);
        digest = storeDigest;
        if (storeAttributes != null) {
            final Set<String> local = skippedNames;
            synchronized (attributes) {
                attributes.keySet().removeIf(name -> !local.contains(name) && !storeAttributes.containsKey(name));
                storeAttributes.forEach((name, value) -> {
                    if (!local.contains(name)) {
                        attributes.put(name, value);
                    }
                });
            }
        }
    }

    /**
     * Returns the digest last written to, or read from, the store.
     *
     * @return the digest, or {@code null}
     */
    public String getDigest() {
        return digest;
    }

    /**
     * Sets the digest last written to, or read from, the store.
     *
     * @param digest the digest
     */
    public void setDigest(final String digest) {
        this.digest = digest;
    }

    /**
     * Returns the names of the attributes that the store did not keep at the last save.
     *
     * @return the names, never null
     */
    public Set<String> getSkippedNames() {
        return skippedNames;
    }

    /**
     * Sets the names of the attributes that the store did not keep at the last save.
     *
     * @param skippedNames the names, or null for none
     */
    public void setSkippedNames(final Set<String> skippedNames) {
        this.skippedNames = skippedNames != null ? Set.copyOf(skippedNames) : Set.of();
    }

    /**
     * Returns the time the store was last written or touched for this session.
     *
     * @return epoch millis, or {@code 0}
     */
    public long getPersistedAt() {
        return persistedAt;
    }

    /**
     * Sets the time the store was last written or touched for this session.
     *
     * @param persistedAt epoch millis
     */
    public void setPersistedAt(final long persistedAt) {
        this.persistedAt = persistedAt;
    }

    /**
     * Returns whether this session must not be written to the store.
     *
     * @return {@code true} if it must not be written
     */
    public boolean isDoNotPersist() {
        return doNotPersist;
    }

    /**
     * Sets whether this session must not be written to the store.
     *
     * @param doNotPersist {@code true} to stop writing it
     */
    public void setDoNotPersist(final boolean doNotPersist) {
        this.doNotPersist = doNotPersist;
    }

    /**
     * Returns whether the session was explicitly invalidated.
     *
     * @return {@code true} if {@link #invalidate()} was called while the session was valid
     */
    public boolean isInvalidated() {
        return invalidated;
    }
}
