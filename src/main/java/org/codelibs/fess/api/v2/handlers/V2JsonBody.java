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
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Reads a JSON-encoded request body into a Map and returns it.
 *
 * <p>Rejects payloads larger than {@code maxBytes}, content types other than
 * {@code application/json}, and malformed JSON. The empty body is treated as
 * an empty map so callers do not need to null-check before lookups.</p>
 */
public final class V2JsonBody {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TYPE = new TypeReference<>() {
    };

    private V2JsonBody() {
    }

    public static Map<String, Object> read(final HttpServletRequest req, final int maxBytes) throws IOException {
        final String ct = req.getContentType();
        if (ct != null && !ct.toLowerCase().startsWith("application/json")) {
            throw new UnsupportedMediaTypeException("content-type must be application/json");
        }
        final byte[] buf = req.getInputStream().readNBytes(maxBytes + 1);
        if (buf.length > maxBytes) {
            throw new PayloadTooLargeException("body exceeds " + maxBytes + " bytes");
        }
        if (buf.length == 0) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(new String(buf, StandardCharsets.UTF_8), TYPE);
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
