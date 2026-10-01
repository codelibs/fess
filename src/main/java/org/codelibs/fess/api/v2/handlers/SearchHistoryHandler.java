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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.cbean.SearchLogCB;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Handles {@code GET /api/v2/search-history}.
 *
 * <p>Returns the most recent distinct searches of the logged-in user, so that a client can
 * offer to run one of them again. Each entry carries the search conditions recorded by
 * {@code SearchLogHelper} in the {@code searchParams} field of the search log (query, field
 * conditions, extra queries, sort and explicitly requested languages), restricted to first-page
 * searches made through {@code /api/v2/search} on the current virtual host.</p>
 *
 * <p>The response payload is:</p>
 *
 * <pre>{@code
 * { "record_count": <int>, "data": [{ "q": "<query>", "fields": {"<name>": ["<value>", ...]},
 *   "ex_q": ["<query>", ...], "sort": "<sort>", "lang": ["<lang>", ...],
 *   "requested_at": "<ISO-8601 instant>", "hit_count": <long> }, ...] }
 * }</pre>
 *
 * <p>Only {@code q}, {@code requested_at} and {@code hit_count} are always present; the other
 * condition keys are omitted when they were not part of the search, as in the stored value.
 * Entries are ordered newest first, de-duplicated by their conditions (the newest is kept) and
 * capped at {@code search.history.size}.</p>
 *
 * <p>Order of checks:</p>
 * <ol>
 *   <li>HTTP method must be {@code GET}.</li>
 *   <li>Both {@code search.history.enabled} and the search log must be enabled.</li>
 *   <li>A user must be logged in. The user id is taken from the login session only; the
 *       client-suppliable user code is never used as an identity here.</li>
 * </ol>
 *
 * <p>MJ-30 i18n contract: {@code error.message} values are developer-facing
 * English strings. Clients MUST use {@code error.code} for user-facing i18n.</p>
 */
public class SearchHistoryHandler {

    private static final Logger logger = LogManager.getLogger(SearchHistoryHandler.class);

    /** Upper bound of the search log entries read to build one response. */
    private static final int MAX_FETCH_SIZE = 200;

    /** Number of search log entries read per returned entry, to leave room for duplicates. */
    private static final int FETCH_SIZE_FACTOR = 10;

    private static final TypeReference<Map<String, Object>> CONDITIONS_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public SearchHistoryHandler() {
        // no-op
    }

    /**
     * Processes one {@code GET /api/v2/search-history} request.
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            if (!fessConfig.isSearchHistoryEnabled() || !fessConfig.isSearchLog()) {
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.INVALID_REQUEST, "search history is not available");
                return;
            }
            final String userId = getUserId();
            if (StringUtil.isBlank(userId)) {
                ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.AUTH_REQUIRED, "no user session");
                return;
            }
            final int size = fessConfig.getSearchHistorySizeAsInteger();
            final List<Map<String, Object>> data = new ArrayList<>();
            if (size > 0) {
                // The search log writer stores "" when no virtual host matched the request.
                final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
                final List<SearchLog> logs =
                        selectSearchLogs(userId, StringUtil.isNotBlank(virtualHostKey) ? virtualHostKey : StringUtil.EMPTY,
                                (int) Math.min((long) size * FETCH_SIZE_FACTOR, MAX_FETCH_SIZE));
                final Set<String> seen = new HashSet<>();
                for (final SearchLog searchLog : logs) {
                    if (data.size() >= size) {
                        break;
                    }
                    final String searchParams = searchLog.getSearchParams();
                    if (StringUtil.isBlank(searchParams) || !seen.add(searchParams)) {
                        continue;
                    }
                    final Map<String, Object> entry = toEntry(searchLog, searchParams);
                    if (entry != null) {
                        data.add(entry);
                    }
                }
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("record_count", data.size());
            payload.put("data", data);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/search-history GET");
        }
    }

    /**
     * Returns the id of the logged-in user, or {@code null} for an anonymous caller.
     * Exposed as a seam so unit tests can supply a user without a login session.
     *
     * @return the user id, or {@code null} when no user is logged in
     */
    protected String getUserId() {
        return ComponentUtil.getRequestManager().findUserBean(FessUserBean.class).map(FessUserBean::getUserId).orElse(null);
    }

    /**
     * Reads the newest search log entries of a user. Exposed as a seam so unit tests can
     * supply entries without a search engine.
     *
     * @param userId the logged-in user id
     * @param virtualHostKey the current virtual host key, {@code ""} when none matched
     * @param fetchSize the maximum number of entries to read
     * @return the entries, newest first
     */
    protected List<SearchLog> selectSearchLogs(final String userId, final String virtualHostKey, final int fetchSize) {
        return ComponentUtil.getComponent(SearchLogBhv.class).selectList(cb -> setUpConditionBean(cb, userId, virtualHostKey, fetchSize));
    }

    /**
     * Sets up the search log query: first-page {@code /api/v2/search} requests of the user on
     * the virtual host, newest first. Entries without recorded conditions cannot be filtered
     * out here because {@code searchParams} is neither indexed nor has doc values; the caller
     * skips them.
     *
     * @param cb the condition bean to set up
     * @param userId the logged-in user id
     * @param virtualHostKey the current virtual host key, {@code ""} when none matched
     * @param fetchSize the maximum number of entries to read
     */
    protected void setUpConditionBean(final SearchLogCB cb, final String userId, final String virtualHostKey, final int fetchSize) {
        cb.query().setUser_Equal(userId);
        cb.query().setAccessType_Equal(Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        cb.query().setQueryOffset_Equal(0);
        // setVirtualHost_Equal rejects an empty value, which is what the writer stores without a virtual host.
        cb.query().addQuery(QueryBuilders.termQuery("virtualHost", virtualHostKey));
        cb.query().addOrderBy_RequestedAt_Desc();
        // Skip the large "documents" array and the rest of the log: only these fields reach the response.
        cb.specify().columnRequestedAt();
        cb.specify().columnHitCount();
        cb.specify().doColumn("searchParams");
        cb.fetchFirst(fetchSize);
    }

    /**
     * Builds one response entry from a search log entry, or returns {@code null} when the
     * recorded conditions cannot be parsed or carry no query.
     */
    private Map<String, Object> toEntry(final SearchLog searchLog, final String searchParams) {
        final Map<String, Object> conditions;
        try {
            conditions = mapper.readValue(searchParams, CONDITIONS_TYPE);
        } catch (final JacksonException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipped unparseable search conditions: id={}", searchLog.getId(), e);
            }
            return null;
        }
        if (conditions == null || !(conditions.get("q") instanceof final String q) || StringUtil.isBlank(q)) {
            return null;
        }
        final Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("q", q);
        if (conditions.get("fields") instanceof final Map<?, ?> fields) {
            entry.put("fields", fields);
        }
        if (conditions.get("ex_q") instanceof final List<?> extraQueries) {
            entry.put("ex_q", extraQueries);
        }
        if (conditions.get("sort") instanceof final String sort) {
            entry.put("sort", sort);
        }
        if (conditions.get("lang") instanceof final List<?> languages) {
            entry.put("lang", languages);
        }
        if (searchLog.getRequestedAt() != null) {
            entry.put("requested_at",
                    DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.of(searchLog.getRequestedAt(), ZoneId.systemDefault())));
        }
        entry.put("hit_count", searchLog.getHitCount());
        return entry;
    }
}
