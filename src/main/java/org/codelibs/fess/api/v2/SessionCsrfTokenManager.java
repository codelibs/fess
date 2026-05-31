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
package org.codelibs.fess.api.v2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpSession;

/**
 * Helper that issues, verifies, and rotates per-session CSRF tokens.
 *
 * <p>Tokens are 256-bit random strings encoded with the URL-safe Base64
 * alphabet (no padding) and stored under {@link #SESSION_ATTR} on the
 * {@link HttpSession}. The verification path uses a constant-time string
 * comparison to avoid timing side-channels.</p>
 */
public class SessionCsrfTokenManager {

    /** Default constructor used by the DI container. */
    public SessionCsrfTokenManager() {
        // default constructor
    }

    private static final Logger logger = LogManager.getLogger(SessionCsrfTokenManager.class);

    /** Session attribute name under which the CSRF token is stored. */
    public static final String SESSION_ATTR = "fess.csrf.token";

    private static final SecureRandom RNG = new SecureRandom();

    /**
     * Returns the token bound to {@code session}, creating one if absent.
     *
     * @param session the HTTP session to read or update
     * @return the existing or newly issued token
     */
    public String issue(final HttpSession session) {
        final Object existing = session.getAttribute(SESSION_ATTR);
        if (existing instanceof String s && !s.isEmpty()) {
            if (logger.isDebugEnabled()) {
                // m-9: log token length only — NEVER log the token value itself.
                // m-19: log a truncated SHA-256 of the session id instead of the raw id
                // so the log line can correlate events without leaking a hijackable id.
                logger.debug("[csrf.issue] token already present: sessionIdHash={}, tokenLength={}", redactSessionId(session), s.length());
            }
            return s;
        }
        final byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        final String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTR, token);
        if (logger.isDebugEnabled()) {
            logger.debug("[csrf.issue] new token issued: sessionIdHash={}, tokenLength={}", redactSessionId(session), token.length());
        }
        return token;
    }

    /**
     * Verifies that {@code provided} matches the token bound to {@code session}.
     *
     * @param session the HTTP session holding the stored token
     * @param provided the candidate token to validate (typically from a request header or form field)
     * @return {@code true} when both tokens are non-empty and equal in constant time
     */
    public boolean verify(final HttpSession session, final String provided) {
        if (provided == null || provided.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[csrf.verify] rejected: provided token is null or empty; sessionIdHash={}",
                        session != null ? redactSessionId(session) : "null");
            }
            return false;
        }
        final Object stored = session.getAttribute(SESSION_ATTR);
        if (!(stored instanceof String s) || s.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[csrf.verify] rejected: no stored token in session; sessionIdHash={}", redactSessionId(session));
            }
            return false;
        }
        final boolean matched = constantTimeEquals(s, provided);
        if (logger.isDebugEnabled()) {
            // Log lengths only — never log token values. The matched flag is safe to log
            // because it carries no token material; it only tells ops why a 403 occurred.
            logger.debug("[csrf.verify] result: sessionIdHash={}, storedLength={}, providedLength={}, matched={}", redactSessionId(session),
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
            logger.debug("[csrf.rotate] token removed, issuing fresh token: sessionIdHash={}", redactSessionId(session));
        }
        return issue(session);
    }

    /**
     * Returns a redacted, log-safe identifier derived from the session id: the first
     * 8 hex characters of its SHA-256 digest. The raw session id is NEVER logged,
     * because anyone who reads it from a log can hijack the session.
     *
     * <p>If the session id cannot be retrieved (container threw {@code IllegalStateException}
     * because the session was invalidated, or another runtime failure) or SHA-256 is not
     * available on the JVM (should not happen on a standard Java runtime), this returns
     * a literal placeholder rather than leaking the raw id.</p>
     *
     * @param session the HTTP session whose id should be hashed; may be {@code null}
     * @return an 8-character lowercase hex prefix of {@code SHA-256(sessionId)}, or a literal placeholder
     */
    static String redactSessionId(final HttpSession session) {
        if (session == null) {
            return "(null)";
        }
        final String id;
        try {
            id = session.getId();
        } catch (final Exception e) {
            return "(unavailable)";
        }
        if (id == null || id.isEmpty()) {
            return "(empty)";
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digest = md.digest(id.getBytes(StandardCharsets.UTF_8));
            // First 8 hex chars (= 32 bits) is enough entropy to correlate log lines
            // for a single user session without being reversible to the raw id.
            return HexFormat.of().formatHex(digest, 0, 4);
        } catch (final NoSuchAlgorithmException e) {
            return "(no-sha256)";
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
