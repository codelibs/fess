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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/auth/me}.
 *
 * <p>Returns the current authentication state. When the session has a bound
 * {@link FessUserBean} the response includes the user's id, roles, groups,
 * permissions, and editable flag; otherwise just {@code authenticated:false}
 * so anonymous callers receive a stable, well-formed envelope rather than a
 * 401 (the v2 contract treats "anonymous" as a valid state, not an error).</p>
 *
 * <p>MJ-28: The user shape is produced by {@link UserPayloads#toJson(FessUserBean)}
 * to guarantee wire-identity with LoginHandler. All array fields are always JSON
 * arrays, never null.</p>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values in this handler are
 * developer-facing English strings. Clients MUST use {@code error.code}
 * (the V2ErrorCode token) for user-facing i18n.</p>
 */
public class MeHandler {

    private static final Logger logger = LogManager.getLogger(MeHandler.class);

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        OptionalThing<FessUserBean> userBean;
        try {
            userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        } catch (final Exception e) {
            // In environments where the login subsystem is not fully wired (e.g. null session
            // bean) treat the caller as anonymous. Log WARN for unexpected errors so ops can
            // diagnose misconfigured servers. Production callers always have FessLoginAssist resolvable.
            logger.warn("/api/v2/auth/me: login subsystem lookup failed; degrading to anonymous", e);
            userBean = OptionalThing.empty();
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        if (userBean.isPresent()) {
            final FessUserBean u = userBean.get();
            // MJ-28: use shared UserPayloads.toJson() to guarantee the same wire shape as
            // LoginHandler — roles/groups/permissions are always arrays, never null.
            payload.put("authenticated", true);
            payload.put("user", UserPayloads.toJson(u));
        } else {
            payload.put("authenticated", false);
        }
        V2EnvelopeWriter.writeSuccess(res, payload);
    }
}
