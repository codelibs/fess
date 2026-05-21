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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code POST /api/v2/auth/password}.
 *
 * <p>Changes the current user's password. The handler requires an active
 * session ({@code 401 auth_required} otherwise), validates the new password
 * against the system's password policy ({@code SystemHelper#validatePassword}),
 * and delegates the actual store to {@link UserService#changePassword}.
 * Confirm-password mismatch and a blank new password both yield
 * {@code 400 invalid_request}.</p>
 */
public class PasswordChangeHandler {

    private static final int MAX_BODY_BYTES = 4 * 1024;

    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "method not allowed");
            return;
        }
        OptionalThing<FessUserBean> userBean;
        try {
            userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        } catch (final Exception e) {
            // Login subsystem not fully wired (e.g. unit test harness without DBFlute
            // behaviors). Treat as anonymous — production callers always have it resolvable.
            userBean = OptionalThing.empty();
        }
        if (!userBean.isPresent()) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
            return;
        }
        final Map<String, Object> body;
        try {
            body = V2JsonBody.read(req, MAX_BODY_BYTES);
        } catch (final V2JsonBody.PayloadTooLargeException | V2JsonBody.MalformedJsonException
                | V2JsonBody.UnsupportedMediaTypeException e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, e.getMessage());
            return;
        }
        final String newPw = stringOrNull(body.get("new_password"));
        final String confirm = stringOrNull(body.get("confirm_password"));
        if (StringUtil.isBlank(newPw) || !newPw.equals(confirm)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "passwords do not match");
            return;
        }
        final String validationError = ComponentUtil.getSystemHelper().validatePassword(newPw);
        if (StringUtil.isNotBlank(validationError)) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "weak password: " + validationError);
            return;
        }
        try {
            ComponentUtil.getComponent(UserService.class).changePassword(userBean.get().getUserId(), newPw);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "failed to change password");
            return;
        }
        V2EnvelopeWriter.writeSuccess(res, Map.of("ok", true));
    }

    private static String stringOrNull(final Object v) {
        return v == null ? null : v.toString();
    }
}
