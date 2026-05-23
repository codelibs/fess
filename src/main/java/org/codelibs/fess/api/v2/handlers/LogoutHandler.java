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
import org.codelibs.fess.helper.SessionCsrfTokenManager;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code POST /api/v2/auth/logout}.
 *
 * <p>The endpoint is idempotent — calling it without a session still returns
 * {@code {"ok": true}} so SPAs can fire-and-forget on tab close. When a session
 * does exist the CSRF token is rotated (defence-in-depth against fixation) and
 * the session is invalidated.</p>
 */
public class LogoutHandler {

    private static final Logger logger = LogManager.getLogger(LogoutHandler.class);

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            ComponentUtil.getComponent(FessLoginAssist.class).logout();
        } catch (final Exception e) {
            // logout is idempotent — no session or unavailable login subsystem; log WARN for
            // actual logout failures but still respond 200 ok (caller's intent is satisfied).
            logger.warn("logout call threw unexpectedly", e);
        }
        final HttpSession session = req.getSession(false);
        if (session != null) {
            ComponentUtil.getComponent(SessionCsrfTokenManager.class).rotate(session);
            session.invalidate();
        }
        V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true));
    }
}
