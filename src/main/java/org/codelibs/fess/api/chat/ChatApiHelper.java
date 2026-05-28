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
package org.codelibs.fess.api.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Shared utilities for v2 chat API handlers.
 *
 * <p>Extracted common logic from {@link org.codelibs.fess.api.v2.handlers.ChatHandler}
 * and {@link org.codelibs.fess.api.v2.handlers.ChatStreamHandler} so it can be reused
 * across chat-related handlers without duplication. The v1
 * {@link ChatApiManager} is not modified by this class — the common utility
 * methods live here and only the v2 handlers call them. v1 refactoring is a
 * separate task.</p>
 *
 * <p>This is a DI-managed singleton helper (registered as {@code chatApiHelper}); obtain it via
 * {@link org.codelibs.fess.util.ComponentUtil#getChatApiHelper()} and call the instance methods.
 * Helpers in Fess are singletons accessed through {@code ComponentUtil}, not static utility
 * classes.</p>
 */
public class ChatApiHelper {

    private static final Logger logger = LogManager.getLogger(ChatApiHelper.class);

    /**
     * Default constructor for ChatApiHelper.
     */
    public ChatApiHelper() {
        // Default constructor
    }

    /**
     * Parses the {@code fields} object (nested structure) or legacy {@code fields.label}
     * dotted-key from a raw request map into the map shape accepted by ChatClient.
     *
     * <p>The v2 wire format accepts either:</p>
     * <ul>
     *   <li>{@code "fields": {"label": "value"}} — nested object (preferred v2 form)</li>
     *   <li>{@code "fields": {"label": ["v1", "v2"]}} — nested object with array</li>
     * </ul>
     *
     * <p>Values that are not present in the {@link org.codelibs.fess.helper.LabelTypeHelper}
     * allowlist are dropped silently; rejected values are appended to {@code warnings}
     * under the key {@code "fields.label"}.</p>
     *
     * @param raw the raw request body map
     * @param warnings mutable map to collect rejected values (keyed by field name)
     * @return a map of validated field filters, or an empty map when none are present
     */
    @SuppressWarnings("unchecked")
    public Map<String, String[]> parseFieldFilters(final Map<String, Object> raw, final Map<String, List<String>> warnings) {
        // Support nested fields object: {"fields": {"label": ... }}
        Object labelsRaw = null;
        final Object fieldsObj = raw.get("fields");
        if (fieldsObj instanceof Map<?, ?> fieldsMap) {
            labelsRaw = fieldsMap.get("label");
        }
        // Fallback: legacy dotted-key "fields.label" (kept for backward compat during transition)
        if (labelsRaw == null) {
            labelsRaw = raw.get("fields.label");
        }

        if (labelsRaw == null) {
            return Collections.emptyMap();
        }

        final List<String> labelValues = new ArrayList<>();
        if (labelsRaw instanceof List<?> l) {
            for (final Object o : l) {
                if (o != null) {
                    labelValues.add(o.toString());
                }
            }
        } else {
            labelValues.add(labelsRaw.toString());
        }

        if (labelValues.isEmpty()) {
            return Collections.emptyMap();
        }

        // Validate against the configured label type allowlist (same logic v1 uses).
        final Set<String> allowed = new HashSet<>();
        try {
            ComponentUtil.getLabelTypeHelper()
                    .getLabelTypeItemList(SearchRequestType.SEARCH, Locale.ROOT)
                    .stream()
                    .map(m -> m.get("value"))
                    .forEach(allowed::add);
        } catch (final Exception e) {
            // Helper unavailable in unit tests — degrade to empty allowlist.
            logger.warn("chat allowlist load failed for labels", e);
        }

        final List<String> valid = new ArrayList<>();
        final List<String> rejected = new ArrayList<>();
        for (final String v : labelValues) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            } else if (v != null) {
                rejected.add(v);
            }
        }
        if (!rejected.isEmpty()) {
            warnings.put("fields.label", Collections.unmodifiableList(rejected));
        }
        if (valid.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, String[]> out = new HashMap<>();
        out.put("label", valid.toArray(new String[0]));
        return out;
    }

    /**
     * Parses the {@code extra_queries} array (or scalar) from the raw request map.
     *
     * <p>Values are validated against the {@link org.codelibs.fess.helper.ViewHelper}
     * facet-query allowlist. Rejected values are appended to {@code warnings} under
     * the key {@code "extra_queries"}.</p>
     *
     * @param raw the raw request body map
     * @param warnings mutable map to collect rejected values (keyed by field name)
     * @return array of validated extra query strings; empty array when none present
     */
    @SuppressWarnings("unchecked")
    public String[] parseExtraQueries(final Map<String, Object> raw, final Map<String, List<String>> warnings) {
        final Object exqRaw = raw.get("extra_queries");
        if (exqRaw == null) {
            return new String[0];
        }

        final List<String> values = new ArrayList<>();
        if (exqRaw instanceof List<?> l) {
            for (final Object o : l) {
                if (o != null) {
                    values.add(o.toString());
                }
            }
        } else {
            values.add(exqRaw.toString());
        }

        if (values.isEmpty()) {
            return new String[0];
        }

        final Set<String> allowed = new HashSet<>();
        try {
            for (final FacetQueryView view : ComponentUtil.getViewHelper().getFacetQueryViewList()) {
                allowed.addAll(view.getQueryMap().values());
            }
        } catch (final Exception e) {
            // Helper unavailable in unit tests — degrade to empty allowlist.
            logger.warn("chat allowlist load failed for extra_queries", e);
        }

        final List<String> valid = new ArrayList<>();
        final List<String> rejected = new ArrayList<>();
        for (final String v : values) {
            if (v != null && allowed.contains(v)) {
                valid.add(v);
            } else if (v != null) {
                rejected.add(v);
            }
        }
        if (!rejected.isEmpty()) {
            warnings.put("extra_queries", Collections.unmodifiableList(rejected));
        }
        return valid.toArray(new String[0]);
    }

    /**
     * Resolves the effective user identifier for a chat request.
     *
     * <p>Mirrors the logic in v1 {@code ChatApiManager#getUserId}: prefer the
     * authenticated username, fall back to the cookie-bound userCode for guests.
     * This keeps chat usable for anonymous SPA visitors when login is not required.</p>
     *
     * @return the user identifier (never null)
     */
    public String getUserId() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        return resolveUserId(systemHelper.getUsername(), () -> ComponentUtil.getUserInfoHelper().getUserCode());
    }

    /**
     * Pure resolution logic behind {@link #getUserId()}, separated so the
     * branching can be unit-tested without the DI container. Prefers a non-guest username;
     * otherwise falls back to the guest user-code provided by the supplier (evaluated lazily so
     * the cookie-bound code is only resolved for guest visitors).
     *
     * @param username the authenticated username (may be {@link Constants#GUEST_USER})
     * @param guestUserCodeSupplier supplies the fallback user-code for guest visitors
     * @return the effective user identifier (never null)
     */
    String resolveUserId(final String username, final java.util.function.Supplier<String> guestUserCodeSupplier) {
        if (!Constants.GUEST_USER.equals(username)) {
            return username;
        }
        return guestUserCodeSupplier.get();
    }

    /**
     * Resolves {@code rag.chat.message.max.length} from fess_config system properties,
     * defaulting to {@code 4000} on parse failure.
     *
     * @param fessConfig active Fess config
     * @return max chat message length in characters
     */
    public int getMaxMessageLength(final FessConfig fessConfig) {
        try {
            return Integer.parseInt(fessConfig.getSystemProperty("rag.chat.message.max.length", "4000"));
        } catch (final NumberFormatException e) {
            return 4000;
        }
    }

}
