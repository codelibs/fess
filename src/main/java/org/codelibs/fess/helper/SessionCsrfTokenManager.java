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
package org.codelibs.fess.helper;

import java.security.SecureRandom;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpSession;

public class SessionCsrfTokenManager {

    private static final Logger logger = LogManager.getLogger(SessionCsrfTokenManager.class);

    public static final String SESSION_ATTR = "fess.csrf.token";

    private static final SecureRandom RNG = new SecureRandom();

    public String issue(final HttpSession session) {
        final Object existing = session.getAttribute(SESSION_ATTR);
        if (existing instanceof String s && !s.isEmpty()) {
            if (logger.isDebugEnabled()) {
                // m-9: log token length only — NEVER log the token value itself.
                logger.debug("[csrf.issue] token already present: sessionId={}, tokenLength={}", sessionId(session), s.length());
            }
            return s;
        }
        final byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        final String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTR, token);
        if (logger.isDebugEnabled()) {
            logger.debug("[csrf.issue] new token issued: sessionId={}, tokenLength={}", sessionId(session), token.length());
        }
        return token;
    }

    public boolean verify(final HttpSession session, final String provided) {
        if (provided == null || provided.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[csrf.verify] rejected: provided token is null or empty; sessionId={}",
                        session != null ? sessionId(session) : "null");
            }
            return false;
        }
        final Object stored = session.getAttribute(SESSION_ATTR);
        if (!(stored instanceof String s) || s.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[csrf.verify] rejected: no stored token in session; sessionId={}", sessionId(session));
            }
            return false;
        }
        final boolean matched = constantTimeEquals(s, provided);
        if (logger.isDebugEnabled()) {
            // Log lengths only — never log token values. The matched flag is safe to log
            // because it carries no token material; it only tells ops why a 403 occurred.
            logger.debug("[csrf.verify] result: sessionId={}, storedLength={}, providedLength={}, matched={}", sessionId(session),
                    s.length(), provided.length(), matched);
        }
        return matched;
    }

    /**
     * Rotates the session's CSRF token: invalidates any existing token and issues
     * a fresh one in a single atomic call from the session's perspective.
     *
     * <p>Callers that previously paired {@code rotate(); issue();} will continue to
     * work correctly because the second {@link #issue(HttpSession)} call is
     * idempotent — it returns the token already set by this method.</p>
     *
     * @param session the HTTP session to rotate the token in
     * @return the newly issued token, or {@code null} if {@code session} is {@code null}
     */
    public String rotate(final HttpSession session) {
        if (session == null) {
            return null;
        }
        session.removeAttribute(SESSION_ATTR);
        if (logger.isDebugEnabled()) {
            logger.debug("[csrf.rotate] token removed, issuing fresh token: sessionId={}", sessionId(session));
        }
        return issue(session);
    }

    /**
     * Safe accessor for session id — some container implementations throw on {@code getId()}
     * when the session has already been invalidated, so we catch and fall back to a literal.
     */
    private static String sessionId(final HttpSession session) {
        try {
            return session.getId();
        } catch (final Exception e) {
            return "(unavailable)";
        }
    }

    private static boolean constantTimeEquals(final String a, final String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
