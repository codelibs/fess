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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code POST /api/v2/auth/logout}.
 *
 * <p>The endpoint is idempotent — calling it without a session still returns
 * {@code {"ok": true}} so SPAs can fire-and-forget on tab close. When a session
 * does exist it is invalidated; the CSRF token need not be rotated explicitly
 * because {@code session.invalidate()} discards the entire session including all
 * CSRF state.</p>
 *
 * <p>{@link IllegalStateException} from {@code session.invalidate()} is silently
 * swallowed: {@link FessLoginAssist#logout()} may already have invalidated the
 * session internally, and the contract is "idempotent ok".</p>
 */
public class LogoutHandler {

    private static final Logger logger = LogManager.getLogger(LogoutHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public LogoutHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/auth/logout} POST request.
     *
     * <p>Invokes {@link FessLoginAssist#logout()} (swallowing failures so the
     * call remains idempotent) and then invalidates the underlying
     * {@link HttpSession} when one exists. Rejects non-{@code POST} methods
     * with {@link V2ErrorCode#METHOD_NOT_ALLOWED}; otherwise always writes a
     * success envelope of {@code {"ok": true}}.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "POST");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            ComponentUtil.getComponent(FessLoginAssist.class).logout();
        } catch (final Exception e) {
            // logout is idempotent — no session or unavailable login subsystem; log WARN for
            // actual logout failures but still respond 200 ok (caller's intent is satisfied).
            logger.warn("[v2/logout] logout call failed", e);
        }
        final HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
            } catch (final IllegalStateException ignore) {
                // FessLoginAssist.logout() may have already invalidated the session.
            }
        }
        V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true));
    }
}
