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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.SessionCsrfTokenManager;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code GET /api/v2/ui/config}.
 *
 * <p>Returns the SPA bootstrap payload: site name, login requirement, supported
 * locales, the active theme descriptor, feature flags, paging defaults, and a
 * freshly-issued CSRF token. The endpoint is GET-only and CSRF-exempt because
 * it is the canonical way for an unauthenticated client to obtain a token in
 * the first place.</p>
 *
 * <p>The handler is defensive: a missing {@link ThemeRegistry} or any unexpected
 * exception during payload assembly is caught and emitted as a structured
 * envelope ({@code status:9, error.code:"internal_error"}) so the SPA always
 * sees a parseable response — never a leaked stack trace.</p>
 */
public class UiConfigHandler {

    private static final Logger logger = LogManager.getLogger(UiConfigHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public UiConfigHandler() {
        // no-op
    }

    /**
     * Builds the sort_options array. Entries for click_count are only included when
     * search logging is enabled; entries for favorite_count only when user favorites
     * are enabled. The caller controls both flags so this method stays pure / testable.
     *
     * @param searchLogEnabled whether search logging is on
     * @param userFavoriteEnabled whether user favorites are on
     * @return ordered list of sort option descriptor maps
     */
    List<Map<String, Object>> buildSortOptions(final boolean searchLogEnabled, final boolean userFavoriteEnabled) {
        final List<Map<String, Object>> list = new ArrayList<>();
        addSortOption(list, "", "labels.search_result_sort_score_desc");
        addSortOption(list, "score.desc", "labels.search_result_sort_score_desc");
        addSortOption(list, "filename.asc", "labels.search_result_sort_filename_asc");
        addSortOption(list, "filename.desc", "labels.search_result_sort_filename_desc");
        addSortOption(list, "created.asc", "labels.search_result_sort_created_asc");
        addSortOption(list, "created.desc", "labels.search_result_sort_created_desc");
        addSortOption(list, "content_length.asc", "labels.search_result_sort_content_length_asc");
        addSortOption(list, "content_length.desc", "labels.search_result_sort_content_length_desc");
        addSortOption(list, "last_modified.asc", "labels.search_result_sort_last_modified_asc");
        addSortOption(list, "last_modified.desc", "labels.search_result_sort_last_modified_desc");
        if (searchLogEnabled) {
            addSortOption(list, "click_count.asc", "labels.search_result_sort_click_count_asc");
            addSortOption(list, "click_count.desc", "labels.search_result_sort_click_count_desc");
        }
        if (userFavoriteEnabled) {
            addSortOption(list, "favorite_count.asc", "labels.search_result_sort_favorite_count_asc");
            addSortOption(list, "favorite_count.desc", "labels.search_result_sort_favorite_count_desc");
        }
        return list;
    }

    /**
     * Builds the filetype_options array. Values match the canonical {@code index.filetype}
     * field values and the {@code labels.facet_filetype_*} i18n keys used in the advance
     * search form (ADV-1 source).
     *
     * @return ordered list of filetype option descriptor maps, each with {@code value} and {@code label_key}
     */
    List<Map<String, Object>> buildFiletypeOptions() {
        final List<Map<String, Object>> list = new ArrayList<>();
        for (final String type : new String[] { "html", "word", "excel", "powerpoint", "odt", "ods", "odp", "pdf", "txt", "others" }) {
            final Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("value", type);
            entry.put("label_key", "labels.facet_filetype_" + type);
            list.add(entry);
        }
        return list;
    }

    private static void addSortOption(final List<Map<String, Object>> list, final String value, final String labelKey) {
        final Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("value", value);
        entry.put("label_key", labelKey);
        list.add(entry);
    }

    /**
     * Processes one {@code /api/v2/ui/config} GET request.
     *
     * <p>Rejects non-{@code GET} methods with {@link V2ErrorCode#METHOD_NOT_ALLOWED}.
     * Otherwise assembles the SPA bootstrap payload — site name, login
     * requirement, supported locales, active theme descriptor, feature flags,
     * paging defaults, and (when {@code theme.api.csrf.required=true}) a
     * freshly-issued CSRF token bound to the session — and writes it via the
     * standard v2 envelope. Any unexpected exception is caught and emitted as
     * a structured {@code internal_error} envelope.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final FessConfig cfg = ComponentUtil.getFessConfig();

            // Resolve the active theme via the registry; the registry may be unregistered
            // in some environments (notably stripped-down unit harnesses), so absence is
            // treated as "no theme" rather than an error.
            final Map<String, Object> themePayload = new LinkedHashMap<>();
            Optional<Theme> activeTheme = Optional.empty();
            try {
                final VirtualHostHelper vhost = ComponentUtil.getVirtualHostHelper();
                final String vhostKey = vhost == null ? null : vhost.getVirtualHostKey();
                final ThemeRegistry registry = ComponentUtil.getComponent(ThemeRegistry.class);
                if (registry != null) {
                    activeTheme = registry.resolveActiveTheme(vhostKey);
                    activeTheme.ifPresent(t -> {
                        themePayload.put("name", t.getName());
                        t.getManifest().ifPresent(m -> {
                            themePayload.put("display_name", m.getDisplayName());
                            themePayload.put("version", m.getVersion());
                            themePayload.put("supported_locales", m.getSupportedLocales());
                        });
                    });
                }
            } catch (final Exception themeEx) {
                logger.warn("Theme resolution failed; emitting empty theme payload", themeEx);
                // fall through with empty themePayload — the "theme" key is always present.
            }

            // Site name resolution: prefer the active theme manifest's display_name when
            // a theme is bound to the current request; otherwise fall back to the literal
            // "Fess". We intentionally do not introduce a new fess_config key here.
            final String siteName = activeTheme.flatMap(Theme::getManifest).map(ThemeManifest::getDisplayName).orElse("Fess");

            // Feature flags mirror what the legacy JSPs branch on so the SPA can light up
            // the same set of UI affordances without round-tripping more endpoints.
            final boolean searchLogEnabled = cfg.isSearchLog();
            final boolean userFavoriteEnabled = cfg.isUserFavorite();
            final boolean thumbnailEnabled = cfg.isThumbnailEnabled();

            // eoled / development_mode come from SystemHelper (same source as the JSP runtime data).
            boolean eoled = false;
            boolean developmentMode = false;
            try {
                final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
                if (systemHelper != null) {
                    eoled = systemHelper.isEoled();
                    developmentMode = ComponentUtil.getSearchEngineClient().isEmbedded();
                }
            } catch (final Exception ignored) {
                // SystemHelper or SearchEngineClient not wired in unit harness — default to false.
            }

            // label_options: built from LabelTypeHelper; empty list when no labels configured.
            final List<Map<String, Object>> labelOptions = new ArrayList<>();
            try {
                final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
                if (labelTypeHelper != null) {
                    final List<Map<String, String>> items = labelTypeHelper.getLabelTypeItemList(SearchRequestType.JSON, Locale.ROOT);
                    if (items != null) {
                        for (final Map<String, String> item : items) {
                            final Map<String, Object> entry = new LinkedHashMap<>();
                            entry.put("value", item.get(Constants.ITEM_VALUE));
                            entry.put("name", item.get(Constants.ITEM_LABEL));
                            labelOptions.add(entry);
                        }
                    }
                }
            } catch (final Exception ignored) {
                // LabelTypeHelper not wired — return empty list.
            }

            // eol_link / installation_link — surfaced as resolved URLs. Derivation mirrors
            // JSP runtime data registered by SystemHelper. Uses getHelpLink(name) for the
            // installation URL (same base-link pattern); for eol_link, uses the raw config
            // value since it is a full template URL rather than a guide-page name.
            // Falls back to empty string when SystemHelper / RequestManager are unavailable.
            String eolLink = "";
            String installationLink = "";
            try {
                final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
                if (systemHelper != null) {
                    try {
                        installationLink = systemHelper.getHelpLink("installation");
                    } catch (final Exception ignored) {
                        // RequestManager not bound in unit harness — leave empty.
                    }
                    if (eoled) {
                        try {
                            eolLink = systemHelper.getHelpLink("eol");
                        } catch (final Exception ignored) {
                            // RequestManager not bound in unit harness — leave empty.
                        }
                    }
                }
            } catch (final Exception ignored) {
                // SystemHelper not wired in unit harness — default to empty strings.
            }

            // login_link: mirrors fessConfig.isLoginLinkEnabled() used by JSP pageLoginLink.
            boolean loginLinkEnabled = true;
            try {
                loginLinkEnabled = cfg.isLoginLinkEnabled();
            } catch (final Exception ignored) {
                // FessProp system-property store not accessible — default to true.
            }

            final Map<String, Object> features = new LinkedHashMap<>();
            features.put("user_favorite", userFavoriteEnabled);
            features.put("popular_word", cfg.isWebApiPopularWord());
            features.put("suggest_search_log", cfg.isSuggestSearchLog());
            features.put("suggest_documents", cfg.isSuggestDocuments());
            features.put("login_required", cfg.isLoginRequired());
            features.put("eoled", eoled);
            features.put("development_mode", developmentMode);
            features.put("search_log_enabled", searchLogEnabled);
            features.put("thumbnail_enabled", thumbnailEnabled);
            features.put("display_label_type", !labelOptions.isEmpty());
            // A.4: clipboard copy icon toggle (mirrors view.copy.icon.enabled / clipboard.copy.icon.enabled).
            features.put("clipboard_copy_icon", cfg.isClipboardCopyIconEnabled());
            // B.1: EOL and installation links (may be empty strings when not applicable).
            features.put("eol_link", eolLink);
            features.put("installation_link", installationLink);
            // B.2: login link availability flag.
            features.put("login_link", loginLinkEnabled);

            // Server-wide supported language list, surfaced as plain JSON array of codes.
            final String[] langs = cfg.getSupportedLanguagesAsArray();

            // m-14: expose whether CSRF is required so the SPA can decide whether to
            // send the X-Fess-CSRF-Token header. When csrf_required=false the SPA MAY
            // omit the header; when true it MUST include it for all state-changing
            // requests. The csrf_token field is only populated when csrf_required=true
            // (empty string otherwise) to avoid issuing a session-bound token that the
            // SPA will never use.
            final boolean csrfRequired = cfg.isThemeApiCsrfRequired();
            String csrfToken = "";
            if (csrfRequired) {
                // Issue (or echo) a CSRF token so the SPA can immediately POST. Using
                // getSession(true) ensures the session exists even on the very first request.
                final HttpSession session = req.getSession(true);
                final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
                csrfToken = csrf == null ? "" : csrf.issue(session);
            }

            // sort_options: conditionally include click_count and favorite_count entries.
            final List<Map<String, Object>> sortOptions = buildSortOptions(searchLogEnabled, userFavoriteEnabled);

            // num_options: filter out values exceeding page_size_max.
            final int pageMax = cfg.getPagingSearchPageMaxSizeAsInteger();
            final List<Integer> numOptions = new ArrayList<>();
            for (final int n : new int[] { 10, 20, 30, 40, 50, 100 }) {
                if (n <= pageMax) {
                    numOptions.add(n);
                }
            }

            // lang_options: "all" sentinel + supported language codes.
            final List<Map<String, Object>> langOptions = new ArrayList<>();
            final Map<String, Object> allLang = new LinkedHashMap<>();
            allLang.put("value", "all");
            allLang.put("label_key", "labels.searchoptions_all_langs");
            langOptions.add(allLang);
            if (langs != null) {
                for (final String lang : langs) {
                    if (lang != null && !lang.isEmpty()) {
                        final Map<String, Object> langEntry = new LinkedHashMap<>();
                        langEntry.put("value", lang);
                        langEntry.put("label_key", "labels.lang_" + lang);
                        langOptions.add(langEntry);
                    }
                }
            }

            // notifications: HTML snippets shown at the top of specific views.
            // Values come from the system-property store (configurable via admin UI).
            // Empty string means "no notification for this view".
            final Map<String, Object> notifications = new LinkedHashMap<>();
            notifications.put("search_top", cfg.getNotificationSearchTop());
            notifications.put("advance_search", cfg.getNotificationAdvanceSearch());

            // B.3: facet_views — ordered list of FacetQueryView group descriptors from ViewHelper.
            // Each entry: {group_name: String, queries: [{label_key: String, value: String}]}
            // Falls back to an empty list when ViewHelper is not wired (unit harness / stripped env).
            final List<Map<String, Object>> facetViews = new ArrayList<>();
            try {
                final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                if (viewHelper != null) {
                    for (final FacetQueryView fqv : viewHelper.getFacetQueryViewList()) {
                        final Map<String, Object> group = new LinkedHashMap<>();
                        group.put("group_name", fqv.getTitle() != null ? fqv.getTitle() : "");
                        final List<Map<String, Object>> queries = new ArrayList<>();
                        if (fqv.getQueryMap() != null) {
                            fqv.getQueryMap().forEach((labelKey, queryValue) -> {
                                final Map<String, Object> q = new LinkedHashMap<>();
                                q.put("label_key", labelKey);
                                q.put("value", queryValue);
                                queries.add(q);
                            });
                        }
                        group.put("queries", queries);
                        facetViews.add(group);
                    }
                }
            } catch (final Exception ignored) {
                // ViewHelper not wired in stripped unit harness — emit empty list.
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("site_name", siteName);
            payload.put("login_required", cfg.isLoginRequired());
            payload.put("locales", langs == null ? java.util.List.of() : Arrays.asList(langs));
            payload.put("theme", themePayload);
            payload.put("features", features);
            payload.put("page_size_default", cfg.getPagingSearchPageSizeAsInteger());
            payload.put("page_size_max", pageMax);
            payload.put("sort_options", sortOptions);
            payload.put("num_options", numOptions);
            payload.put("lang_options", langOptions);
            payload.put("label_options", labelOptions);
            payload.put("notifications", notifications);
            payload.put("facet_views", facetViews);
            payload.put("filetype_options", buildFiletypeOptions());
            payload.put("csrf_required", csrfRequired);
            payload.put("csrf_token", csrfToken);
            V2EnvelopeWriter.writeSuccess(res, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/ui/config");
        }
    }
}
