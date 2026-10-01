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
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.UserMessages;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.json.JsonMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/documents/export} endpoint — a CSV or JSON download of the
 * documents matching a search.
 *
 * <p>The query parameters are the ones {@code /api/v2/search} accepts, read through
 * {@link V2JsonRequestParams} as {@link ScrollSearchHandler} does, plus {@code format}
 * ({@code csv} by default, or {@code json}). The documents come from
 * {@link SearchHelper#scrollSearch}, so the role filter is the one {@code /search} and
 * {@code /documents/all} apply. The scroll is stopped once {@code api.search.export.max.size}
 * documents have been written.</p>
 *
 * <p>Only the fields listed in {@code api.search.export.fields} that are also
 * {@link QueryFieldConfig#isApiResponseField API response fields} are exported, so the export
 * never exposes a field the JSON API hides.</p>
 *
 * <p><strong>Wire contract:</strong> the success response is the file itself, not an envelope.
 * The headers and the first byte are written only once the search has produced its first
 * document (or finished without one), so a failure before that still comes back as the usual
 * error envelope. A failure after that point cannot be reported in-band without corrupting the
 * file, so it is logged and the download ends early; the client sees a truncated CSV, or a JSON
 * document that does not parse.</p>
 *
 * <p>The handler is stateless; the manager holds a single shared instance.</p>
 */
public class ExportSearchHandler {

    private static final Logger logger = LogManager.getLogger(ExportSearchHandler.class);

    private static final String FORMAT_CSV = "csv";

    private static final String FORMAT_JSON = "json";

    /**
     * Shared, thread-safe mapper whose generators do not close the servlet writer. Closing it would
     * make {@code PrintWriter} silently drop every later write (see {@link ScrollSearchHandler}).
     */
    private static final JsonMapper MAPPER = JsonMapper.builder().disable(StreamWriteFeature.AUTO_CLOSE_TARGET).build();

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public ExportSearchHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/documents/export} request.
     *
     * <p>Rejects non-{@code GET} methods, a disabled feature, an unknown {@code format} and an
     * exceeded rate limit before any search runs.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing an error envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isApiSearchExport()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, "search export is not available");
            return;
        }
        final String formatParam = request.getParameter("format");
        final String format = StringUtil.isBlank(formatParam) ? FORMAT_CSV : formatParam;
        if (!FORMAT_CSV.equals(format) && !FORMAT_JSON.equals(format)) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, "format must be csv or json");
            return;
        }
        if (isRateLimited(request, response, fessConfig)) {
            return;
        }
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        // Set once the headers and the first byte are out; from then on an error cannot be an envelope.
        final ExportWriter[] started = { null };
        try {
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final QueryFieldConfig queryFieldConfig = ComponentUtil.getQueryFieldConfig();
            final List<String> columns = Arrays.stream(fessConfig.getApiSearchExportFields().split(","))
                    .map(String::trim)
                    .filter(name -> StringUtil.isNotBlank(name) && queryFieldConfig.isApiResponseField(name))
                    .distinct()
                    .collect(Collectors.toList());
            if (columns.isEmpty()) {
                ComponentUtil.getV2EnvelopeWriter()
                        .writeError(response, V2ErrorCode.INVALID_REQUEST, "no exportable fields are configured");
                return;
            }
            final int maxSize = fessConfig.getApiSearchExportMaxSizeAsInteger();
            // Fetch in the largest batches a search page may have, so a full export takes
            // max.size / page.max.size round trips instead of one per default page; num is ignored.
            final int batchSize = Math.max(1, Math.min(maxSize, fessConfig.getPagingSearchPageMaxSizeAsInteger()));
            final V2JsonRequestParams params = new V2JsonRequestParams(request, fessConfig) {
                @Override
                public int getPageSize() {
                    return batchSize;
                }
            };
            final ExportWriter exportWriter = new ExportWriter(response, fessConfig, columns, FORMAT_JSON.equals(format));
            final long count = searchHelper.scrollSearch(params, doc -> {
                if (started[0] == null) {
                    exportWriter.begin();
                    started[0] = exportWriter;
                }
                exportWriter.write(doc);
                return exportWriter.rows < maxSize;
            }, OptionalThing.empty());
            if (started[0] == null) {
                // No document matched: still answer with a well-formed, empty file.
                exportWriter.begin();
                started[0] = exportWriter;
            }
            exportWriter.end();
            if (logger.isDebugEnabled()) {
                logger.debug("Exported {} documents", count);
            }
        } catch (final InvalidRequestParameterException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final InvalidQueryException e) {
            // The scroll reports a failure on a later page as an invalid query too.
            if (started[0] != null) {
                logger.warn("/api/v2/documents/export failed after partial write", e);
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/documents/export request", e);
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeUserMessageError(response, V2ErrorCode.INVALID_REQUEST, request.getLocale(), e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (started[0] != null) {
                logger.warn("/api/v2/documents/export failed after partial write", e);
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/documents/export request", e);
            }
            ComponentUtil.getV2EnvelopeWriter()
                    .writeUserMessageError(response, V2ErrorCode.INVALID_REQUEST, request.getLocale(),
                            messages -> messages.addErrorsResultSizeExceeded(UserMessages.GLOBAL_PROPERTY_KEY));
        } catch (final Exception e) {
            if (started[0] != null) {
                // The file is already partly written; an envelope would corrupt it.
                logger.warn("/api/v2/documents/export failed after partial write", e);
            } else {
                ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/documents/export");
            }
        }
    }

    /**
     * Applies the per-minute export limit and writes the {@code 429} envelope when it is exceeded.
     * The key is the one the chat endpoints throttle by (the logged-in user, else the client IP);
     * the {@link LoginRateLimiter.Scope#EXPORT} scope keeps the two buckets apart.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write the error envelope to
     * @param fessConfig the active Fess configuration
     * @return {@code true} when an error envelope was written and the request must stop
     * @throws IOException if the error envelope cannot be written
     */
    private boolean isRateLimited(final HttpServletRequest request, final HttpServletResponse response, final FessConfig fessConfig)
            throws IOException {
        final int limit = fessConfig.getApiSearchExportRateLimitPerMinuteAsInteger();
        if (limit <= 0) {
            return false;
        }
        final LoginRateLimiter limiter;
        try {
            limiter = ComponentUtil.getLoginRateLimiter();
        } catch (final RuntimeException e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/documents/export");
            return true;
        }
        if (!limiter.allow(LoginRateLimiter.Scope.EXPORT, getRateLimitKey(request), limit, 60)) {
            response.setHeader("Retry-After", "60");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.RATE_LIMITED, "too many export requests");
            return true;
        }
        return false;
    }

    /**
     * Resolves the rate-limit key: the authenticated user, else the proxy-aware client IP. Exposed as
     * a seam so unit tests can pin the key (same rationale as {@code ChatStreamHandler#getRateLimitKey}).
     *
     * @param req the incoming HTTP request
     * @return the rate-limit key (never null/blank)
     */
    protected String getRateLimitKey(final HttpServletRequest req) {
        final String username = ComponentUtil.getSystemHelper().getUsername();
        return ComponentUtil.getChatApiHelper()
                .resolveChatRateLimitKey(username, () -> ComponentUtil.getRateLimitHelper().getClientIp(req));
    }

    /**
     * Writes the export file for one request: the response headers and the CSV header row or the
     * JSON prologue in {@link #begin}, one record per {@link #write}, and the epilogue in {@link #end}.
     */
    private static final class ExportWriter {

        private final HttpServletResponse response;

        private final FessConfig fessConfig;

        private final List<String> columns;

        private final boolean json;

        private PrintWriter writer;

        private CsvWriter csvWriter;

        private JsonGenerator generator;

        /** Number of records written so far. */
        int rows;

        ExportWriter(final HttpServletResponse response, final FessConfig fessConfig, final List<String> columns, final boolean json) {
            this.response = response;
            this.fessConfig = fessConfig;
            this.columns = columns;
            this.json = json;
        }

        /**
         * Sets the headers and writes the CSV header row or the JSON prologue. Called right before
         * the first byte so a failure up to this point can still produce an error envelope.
         */
        void begin() {
            try {
                if (json) {
                    response.setContentType("application/json; charset=UTF-8");
                    response.setHeader("Content-Disposition", "attachment; filename=\"search_results.json\"");
                    writer = response.getWriter();
                    generator = MAPPER.createGenerator(writer);
                    generator.writeStartObject();
                    generator.writeName("data");
                    generator.writeStartArray();
                } else {
                    final String encoding = fessConfig.getCsvFileEncoding();
                    response.setContentType("text/csv; charset=" + encoding);
                    response.setHeader("Content-Disposition", "attachment; filename=\"search_results.csv\"");
                    writer = response.getWriter();
                    if (Constants.UTF_8.equalsIgnoreCase(encoding)) {
                        // Excel needs the BOM to read the file as UTF-8.
                        writer.write('\uFEFF');
                    }
                    final CsvConfig cfg = new CsvConfig(',', '"', '"');
                    cfg.setEscapeDisabled(false);
                    cfg.setQuoteDisabled(false);
                    // Never closed: closing it would close the servlet writer (see ScrollSearchHandler).
                    csvWriter = new CsvWriter(writer, cfg);
                    csvWriter.writeValues(columns);
                }
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        void write(final Map<String, Object> doc) {
            if (json) {
                generator.writeStartObject();
                for (final String name : columns) {
                    final Object value = doc.get(name);
                    if (value != null) {
                        generator.writeName(name);
                        generator.writePOJO(value);
                    }
                }
                generator.writeEndObject();
            } else {
                final List<String> values = new ArrayList<>(columns.size());
                for (final String name : columns) {
                    values.add(toCell(doc.get(name)));
                }
                try {
                    csvWriter.writeValues(values);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            rows++;
        }

        void end() throws IOException {
            if (json) {
                generator.writeEndArray();
                generator.writeEndObject();
                generator.close();
            } else {
                csvWriter.flush();
            }
            response.flushBuffer();
        }

        /**
         * Renders a value as a CSV cell. A multi-valued field is joined with a space. A cell a
         * spreadsheet would read as a formula gets a leading quote so it is shown as text.
         */
        private static String toCell(final Object value) {
            if (value == null) {
                return StringUtil.EMPTY;
            }
            final String text =
                    value instanceof Collection<?> c ? c.stream().map(String::valueOf).collect(Collectors.joining(" ")) : value.toString();
            if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0) {
                return "'" + text;
            }
            return text;
        }
    }
}
