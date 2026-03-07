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
package org.codelibs.fess.chat;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatSession;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Manager class for chat sessions.
 * Sessions are stored in memory with automatic expiration.
 *
 * <p><b>Note:</b> Sessions are stored in a local in-memory ConcurrentHashMap.
 * In multi-instance deployments (e.g., behind a load balancer), sessions are
 * not shared between instances. Use sticky sessions or an external session
 * store if session affinity across instances is required.</p>
 *
 * @author FessProject
 */
public class ChatSessionManager {

    private static final Logger logger = LogManager.getLogger(ChatSessionManager.class);

    private final Map<String, ChatSession> sessionCache = new ConcurrentHashMap<>();
    private TimeoutTask cleanupTask;

    /**
     * Default constructor.
     */
    public ChatSessionManager() {
        // Default constructor
    }

    /**
     * Initializes the session manager and starts the cleanup scheduler.
     */
    @PostConstruct
    public void init() {
        // 5 minutes = 300 seconds
        cleanupTask = TimeoutManager.getInstance().addTimeoutTarget(this::cleanupExpiredSessions, 300, true);
        if (logger.isDebugEnabled()) {
            logger.debug("Initialized ChatSessionManager");
        }
    }

    /**
     * Destroys the session manager and shuts down the cleanup scheduler.
     */
    @PreDestroy
    public void destroy() {
        if (cleanupTask != null && !cleanupTask.isCanceled()) {
            cleanupTask.cancel();
        }
    }

    /**
     * Creates a new chat session.
     *
     * @param userId the user ID (can be null for anonymous users)
     * @return the created session
     */
    public ChatSession createSession(final String userId) {
        final ChatSession session = new ChatSession(userId);
        sessionCache.put(session.getSessionId(), session);
        if (logger.isDebugEnabled()) {
            logger.debug("Created chat session: sessionId={}, userId={}", session.getSessionId(), userId);
        }
        enforceMaxSize();
        return session;
    }

    /**
     * Finds a session by ID without updating the last accessed time.
     *
     * @param sessionId the session ID
     * @return the session, or null if not found or expired
     */
    private ChatSession findSession(final String sessionId) {
        final ChatSession session = sessionCache.get(sessionId);
        if (session == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Session not found. sessionId={}", sessionId);
            }
            return null;
        }
        if (isExpired(session)) {
            sessionCache.remove(sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Session expired and removed. sessionId={}, lastAccessedAt={}", sessionId, session.getLastAccessedAt());
            }
            return null;
        }
        return session;
    }

    /**
     * Gets a session by ID.
     *
     * @param sessionId the session ID
     * @return the session, or null if not found or expired
     */
    public ChatSession getSession(final String sessionId) {
        final ChatSession session = findSession(sessionId);
        if (session == null) {
            return null;
        }
        session.touch();
        if (logger.isDebugEnabled()) {
            logger.debug("Session retrieved. sessionId={}, messageCount={}", sessionId, session.getMessages().size());
        }
        return session;
    }

    /**
     * Gets or creates a session.
     *
     * @param sessionId the session ID (can be null to create a new session)
     * @param userId the user ID
     * @return the existing or new session
     */
    public ChatSession getOrCreateSession(final String sessionId, final String userId) {
        if (sessionId != null) {
            final ChatSession session = findSession(sessionId);
            if (session != null) {
                final String sessionUserId = session.getUserId();
                // Validate userId matches - prevent cross-user session access
                if (userId != null && !userId.equals(sessionUserId)) {
                    logger.warn("Session userId mismatch. sessionId={}, requestUserId={}", sessionId, userId);
                } else if (userId == null && sessionUserId == null) {
                    // Both null (unauthenticated + no userCode) - allow by sessionId only
                    session.touch();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Reusing existing session (both userId null). sessionId={}", sessionId);
                    }
                    return session;
                } else if (userId != null && userId.equals(sessionUserId)) {
                    session.touch();
                    if (logger.isDebugEnabled()) {
                        logger.debug("Reusing existing session. sessionId={}, userId={}", sessionId, userId);
                    }
                    return session;
                } else {
                    // userId is null but sessionUserId is not - create new session
                    logger.warn("Session userId mismatch (null vs non-null). sessionId={}, sessionUserId={}", sessionId, sessionUserId);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating new session. requestedSessionId={}, userId={}", sessionId, userId);
        }
        return createSession(userId);
    }

    /**
     * Adds a message to a session.
     *
     * @param sessionId the session ID
     * @param message the message to add
     * @return true if the message was added, false if the session was not found
     */
    public boolean addMessage(final String sessionId, final ChatMessage message) {
        final ChatSession session = getSession(sessionId);
        if (session == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot add message, session not found. sessionId={}", sessionId);
            }
            return false;
        }
        session.addMessage(message);

        // Trim history if needed
        final int maxMessages = getMaxHistoryMessages();
        session.trimHistory(maxMessages);

        if (logger.isDebugEnabled()) {
            logger.debug("Message added to session. sessionId={}, role={}, messageCount={}", sessionId, message.getRole(),
                    session.getMessages().size());
        }
        return true;
    }

    /**
     * Clears the messages in a session with userId ownership check.
     *
     * @param sessionId the session ID
     * @param userId the user ID for ownership verification (can be null)
     * @return true if the session was found, owned by the user, and cleared; false otherwise
     */
    public boolean clearSession(final String sessionId, final String userId) {
        final ChatSession session = findSession(sessionId);
        if (session == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot clear session, not found. sessionId={}", sessionId);
            }
            return false;
        }
        // Verify ownership
        final String sessionUserId = session.getUserId();
        if (userId != null && !userId.equals(sessionUserId)) {
            logger.warn("Cannot clear session, userId mismatch. sessionId={}, requestUserId={}", sessionId, userId);
            return false;
        }
        if (userId == null && sessionUserId != null) {
            logger.warn("Cannot clear session, userId mismatch (null vs non-null). sessionId={}, sessionUserId={}", sessionId,
                    sessionUserId);
            return false;
        }
        session.touch();
        session.clearMessages();
        if (logger.isDebugEnabled()) {
            logger.debug("Session cleared. sessionId={}, userId={}", sessionId, userId);
        }
        return true;
    }

    /**
     * Clears the messages in a session without ownership check.
     * Used for internal/admin operations where ownership verification is not needed.
     *
     * @param sessionId the session ID
     * @return true if the session was found and cleared, false otherwise
     */
    public boolean clearSession(final String sessionId) {
        final ChatSession session = getSession(sessionId);
        if (session == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot clear session, not found. sessionId={}", sessionId);
            }
            return false;
        }
        session.clearMessages();
        if (logger.isDebugEnabled()) {
            logger.debug("Session cleared (no ownership check). sessionId={}", sessionId);
        }
        return true;
    }

    /**
     * Removes a session.
     *
     * @param sessionId the session ID
     * @return the removed session, or null if not found
     */
    public ChatSession removeSession(final String sessionId) {
        final ChatSession session = sessionCache.remove(sessionId);
        if (logger.isDebugEnabled()) {
            logger.debug("Session removed. sessionId={}, found={}", sessionId, session != null);
        }
        return session;
    }

    /**
     * Cleans up expired sessions.
     */
    protected void cleanupExpiredSessions() {
        if (logger.isDebugEnabled()) {
            logger.debug("Running chat session cleanup. currentSessionCount={}", sessionCache.size());
        }

        int removed = 0;
        final Iterator<Map.Entry<String, ChatSession>> iterator = sessionCache.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, ChatSession> entry = iterator.next();
            if (isExpired(entry.getValue())) {
                iterator.remove();
                removed++;
            }
        }

        if (removed > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Removed expired chat sessions. removedCount={}, remainingCount={}", removed, sessionCache.size());
            }
        }
    }

    /**
     * Enforces the maximum session cache size.
     */
    protected void enforceMaxSize() {
        final int maxSize = getMaxSessionSize();
        synchronized (sessionCache) {
            if (sessionCache.size() <= maxSize) {
                return;
            }

            final int toRemove = sessionCache.size() - maxSize;
            logger.warn("Session cache reached maximum size. Removing oldest sessions. currentSize={}, maxSize={}, removing={}",
                    sessionCache.size(), maxSize, toRemove);

            // Remove oldest sessions
            sessionCache.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e1.getValue().getLastAccessedAt().compareTo(e2.getValue().getLastAccessedAt()))
                    .limit(toRemove)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
                    .forEach(sessionCache::remove);
        }
    }

    /**
     * Checks if a session is expired.
     *
     * @param session the session to check
     * @return true if the session is expired
     */
    protected boolean isExpired(final ChatSession session) {
        final int timeoutMinutes = getSessionTimeoutMinutes();
        final LocalDateTime expirationTime = session.getLastAccessedAt().plusMinutes(timeoutMinutes);
        return LocalDateTime.now().isAfter(expirationTime);
    }

    /**
     * Gets the session timeout in minutes.
     *
     * @return the session timeout in minutes
     */
    protected int getSessionTimeoutMinutes() {
        final int value = ComponentUtil.getFessConfig().getRagChatSessionTimeoutMinutesAsInteger();
        if (value <= 0) {
            logger.warn("Invalid session timeout: {}. Using default: 30", value);
            return 30;
        }
        return value;
    }

    /**
     * Gets the maximum session cache size.
     *
     * @return the maximum session cache size
     */
    protected int getMaxSessionSize() {
        final int value = ComponentUtil.getFessConfig().getRagChatSessionMaxSizeAsInteger();
        if (value <= 0) {
            logger.warn("Invalid max session size: {}. Using default: 100", value);
            return 100;
        }
        return value;
    }

    /**
     * Gets the maximum number of history messages to retain.
     *
     * @return the maximum number of history messages
     */
    protected int getMaxHistoryMessages() {
        final int value = ComponentUtil.getFessConfig().getRagChatHistoryMaxMessagesAsInteger();
        if (value <= 0) {
            logger.warn("Invalid max history messages: {}. Using default: 20", value);
            return 20;
        }
        return value;
    }

    /**
     * Gets the current number of active sessions.
     *
     * @return the number of active sessions
     */
    public int getActiveSessionCount() {
        return sessionCache.size();
    }
}
