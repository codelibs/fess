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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Shared helper that produces the canonical v2 user JSON shape.
 *
 * <p>MJ-28: {@link LoginHandler} and {@link MeHandler} previously serialized the
 * same logical fields ({@code user_id}, {@code roles}, {@code groups},
 * {@code permissions}, {@code editable}) in subtly different ways — LoginHandler
 * used {@code arrayOrEmpty()} (always a JSON array), while MeHandler passed raw
 * {@code String[]} (null-capable). Centralising the mapping here guarantees the
 * two endpoints are wire-identical and any future endpoint returns the same shape.</p>
 *
 * <p>The fields are emitted in insertion order (LinkedHashMap) for deterministic
 * JSON output and readable debug logs.</p>
 *
 * <p>MJ-30 i18n note: error.message values in the callers of this class are
 * developer-facing English. Clients MUST use error.code (the V2ErrorCode token)
 * for user-facing i18n.</p>
 */
public final class UserPayloads {

    private UserPayloads() {
        // utility class — no instances
    }

    /**
     * Builds the canonical user JSON map for the given {@link FessUserBean}.
     * All array fields ({@code roles}, {@code groups}, {@code permissions}) are
     * guaranteed to be non-null JSON arrays — a null getter result is normalised
     * to an empty list so clients can always iterate without a null-check.
     *
     * @param u the authenticated user bean (must not be null)
     * @return a LinkedHashMap suitable for inclusion in a v2 response payload
     */
    public static Map<String, Object> toJson(final FessUserBean u) {
        final Map<String, Object> userMap = new LinkedHashMap<>();
        final String userId = u.getUserId();
        userMap.put("user_id", userId);
        // MJ-35: expose display-friendly name and username for the SPA dropdown.
        // user_id is the canonical identifier (= getName()); expose it as "username"
        // for UI use.  A separate "name" alias is the same value for now — future
        // backends can supply a display name independently without a wire break.
        userMap.put("username", userId);
        userMap.put("name", userId);
        userMap.put("roles", arrayOrEmpty(u.getRoles()));
        userMap.put("groups", arrayOrEmpty(u.getGroups()));
        userMap.put("permissions", arrayOrEmpty(u.getPermissions()));
        userMap.put("editable", u.isEditable());
        // MJ-35: derive admin flag the same way FessAdminAction does, honoring the
        // configured authentication.admin.roles list (defaults to "admin" but can be
        // customised per deployment). Ensures the SPA dropdown's "Administration"
        // visibility stays in lock-step with the actual authorization check.
        userMap.put("admin", u.hasRoles(ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray()));
        return userMap;
    }

    private static List<String> arrayOrEmpty(final String[] arr) {
        return arr == null ? List.of() : List.of(arr);
    }
}
