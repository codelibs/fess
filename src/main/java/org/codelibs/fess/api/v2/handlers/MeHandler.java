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
 */
public class MeHandler {

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "method not allowed");
            return;
        }
        OptionalThing<FessUserBean> userBean;
        try {
            userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        } catch (final Exception e) {
            // In environments where the login subsystem is not fully wired (e.g. unit test
            // harness without DBFlute behaviors), treat the caller as anonymous rather than
            // surfacing a 500. Production callers always have FessLoginAssist resolvable.
            userBean = OptionalThing.empty();
        }
        final Map<String, Object> payload = new LinkedHashMap<>();
        if (userBean.isPresent()) {
            final FessUserBean u = userBean.get();
            final Map<String, Object> user = new LinkedHashMap<>();
            user.put("user_id", u.getUserId());
            user.put("roles", u.getRoles());
            user.put("groups", u.getGroups());
            user.put("permissions", u.getPermissions());
            user.put("editable", u.isEditable());
            payload.put("authenticated", true);
            payload.put("user", user);
        } else {
            payload.put("authenticated", false);
        }
        V2EnvelopeWriter.writeSuccess(res, payload);
    }
}
