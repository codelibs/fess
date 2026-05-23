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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Reads a JSON-encoded request body into a Map and returns it.
 *
 * <p>Rejects payloads larger than {@code maxBytes}, content types other than
 * {@code application/json}, and malformed JSON. The empty body is treated as
 * an empty map so callers do not need to null-check before lookups.</p>
 */
public final class V2JsonBody {

    /**
     * Shared Jackson mapper with tight {@link StreamReadConstraints} to defend against
     * deeply-nested JSON objects and oversized strings/numbers that could exhaust the JVM
     * stack or heap:
     * <ul>
     *   <li>{@code maxNestingDepth(32)} — rejects objects/arrays nested more than 32 levels deep.</li>
     *   <li>{@code maxNumberLength(1000)} — rejects multi-thousand-digit numeric literals.</li>
     *   <li>{@code maxStringLength(1 MiB)} — rejects individual string values exceeding 1 MiB.</li>
     * </ul>
     */
    private static final JsonMapper MAPPER = JsonMapper.builder(JsonFactory.builder()
            .streamReadConstraints(
                    StreamReadConstraints.builder().maxNestingDepth(32).maxNumberLength(1000).maxStringLength(1 << 20).build())
            .build()).build();

    private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>() {
    };

    /** UTF-8 BOM byte sequence (EF BB BF). Present in some editor-generated JSON files. */
    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    private V2JsonBody() {
    }

    /**
     * Reads a JSON-encoded request body into a {@code Map<String, Object>}.
     *
     * <p>Enforces the following rules before parsing:</p>
     * <ol>
     *   <li><strong>Content-Type required:</strong> a {@code null} or absent Content-Type
     *       header is rejected with {@link UnsupportedMediaTypeException}.</li>
     *   <li><strong>application/json only:</strong> any Content-Type whose base media type is
     *       not {@code application/json} is rejected with {@link UnsupportedMediaTypeException}.</li>
     *   <li><strong>UTF-8 charset only:</strong> if the Content-Type includes an explicit
     *       {@code charset} parameter, it must be {@code utf-8} (case-insensitive); any other
     *       charset is rejected with {@link UnsupportedMediaTypeException}.</li>
     *   <li><strong>Size limit:</strong> bodies longer than {@code maxBytes} are rejected
     *       with {@link PayloadTooLargeException}.</li>
     *   <li><strong>BOM stripping:</strong> a leading UTF-8 BOM (0xEF 0xBB 0xBF) is silently
     *       removed before parsing so that editor-generated JSON files are accepted.</li>
     * </ol>
     *
     * @param req the incoming HTTP request
     * @param maxBytes maximum accepted body length in bytes
     * @return parsed body as a map; an empty body returns an empty (immutable) map
     * @throws UnsupportedMediaTypeException if the Content-Type is absent, not application/json,
     *         or specifies a non-UTF-8 charset
     * @throws PayloadTooLargeException if the body exceeds {@code maxBytes}
     * @throws MalformedJsonException if the body is not valid JSON
     * @throws IOException if reading the request stream fails
     */
    public static Map<String, Object> read(final HttpServletRequest req, final int maxBytes) throws IOException {
        final String ct = req.getContentType();
        if (ct == null) {
            throw new UnsupportedMediaTypeException("content-type is required");
        }
        final String ctLower = ct.toLowerCase(Locale.ROOT);
        if (!ctLower.startsWith("application/json")) {
            throw new UnsupportedMediaTypeException("content-type must be application/json");
        }
        // Validate charset parameter if present; reject anything that is not utf-8.
        final int charsetIdx = ctLower.indexOf("charset=");
        if (charsetIdx >= 0) {
            final String charsetValue = ctLower.substring(charsetIdx + "charset=".length()).trim();
            // Strip optional surrounding quotes and trailing parameters (e.g. "; boundary=...")
            final String bareCharset = charsetValue.split("[;,\\s]")[0].replace("\"", "").trim();
            if (!"utf-8".equals(bareCharset)) {
                throw new UnsupportedMediaTypeException("charset must be utf-8; received: " + bareCharset);
            }
        }
        final byte[] buf = req.getInputStream().readNBytes(maxBytes + 1);
        if (buf.length > maxBytes) {
            throw new PayloadTooLargeException("body exceeds " + maxBytes + " bytes");
        }
        if (buf.length == 0) {
            return Collections.emptyMap();
        }
        // Strip leading UTF-8 BOM (EF BB BF) if present.
        int offset = 0;
        if (buf.length >= 3 && buf[0] == UTF8_BOM[0] && buf[1] == UTF8_BOM[1] && buf[2] == UTF8_BOM[2]) {
            offset = 3;
        }
        final byte[] jsonBytes = offset == 0 ? buf : java.util.Arrays.copyOfRange(buf, offset, buf.length);
        try {
            return MAPPER.readValue(new String(jsonBytes, StandardCharsets.UTF_8), TYPE);
        } catch (final JsonProcessingException e) {
            throw new MalformedJsonException(e.getMessage());
        }
    }

    public static class UnsupportedMediaTypeException extends IOException {
        private static final long serialVersionUID = 1L;

        public UnsupportedMediaTypeException(final String m) {
            super(m);
        }
    }

    public static class PayloadTooLargeException extends IOException {
        private static final long serialVersionUID = 1L;

        public PayloadTooLargeException(final String m) {
            super(m);
        }
    }

    public static class MalformedJsonException extends IOException {
        private static final long serialVersionUID = 1L;

        public MalformedJsonException(final String m) {
            super(m);
        }
    }
}
