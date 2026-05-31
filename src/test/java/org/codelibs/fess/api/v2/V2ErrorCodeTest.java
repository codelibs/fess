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
package org.codelibs.fess.api.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link V2ErrorCode}.
 *
 * <p>The enum is part of the {@code /api/v2} wire contract — its string codes
 * are emitted into the {@code error.code} field of every error envelope, and
 * its HTTP statuses drive {@code V2EnvelopeWriter.writeError}'s
 * {@code res.setStatus} call. Locking down both surfaces here prevents an
 * accidental rename or status flip from silently breaking SPA clients.</p>
 */
public class V2ErrorCodeTest {

    @Test
    public void test_eachCodeReportsExpectedHttpStatusAndKey() {
        // Table-driven: each enum constant pairs with its expected (wire code, http status).
        // Listing all six here also catches an accidentally added enum value (the size
        // assertion below would fail before the table loop reaches it).
        final Map<V2ErrorCode, int[]> expectedStatus = new LinkedHashMap<>();
        final Map<V2ErrorCode, String> expectedCode = new LinkedHashMap<>();
        expectedCode.put(V2ErrorCode.INVALID_REQUEST, "invalid_request");
        expectedStatus.put(V2ErrorCode.INVALID_REQUEST, new int[] { 400 });
        expectedCode.put(V2ErrorCode.AUTH_REQUIRED, "auth_required");
        expectedStatus.put(V2ErrorCode.AUTH_REQUIRED, new int[] { 401 });
        expectedCode.put(V2ErrorCode.FORBIDDEN, "forbidden");
        expectedStatus.put(V2ErrorCode.FORBIDDEN, new int[] { 403 });
        expectedCode.put(V2ErrorCode.NOT_FOUND, "not_found");
        expectedStatus.put(V2ErrorCode.NOT_FOUND, new int[] { 404 });
        expectedCode.put(V2ErrorCode.CONFLICT, "conflict");
        expectedStatus.put(V2ErrorCode.CONFLICT, new int[] { 409 });
        expectedCode.put(V2ErrorCode.METHOD_NOT_ALLOWED, "method_not_allowed");
        expectedStatus.put(V2ErrorCode.METHOD_NOT_ALLOWED, new int[] { 405 });
        expectedCode.put(V2ErrorCode.RATE_LIMITED, "rate_limited");
        expectedStatus.put(V2ErrorCode.RATE_LIMITED, new int[] { 429 });
        expectedCode.put(V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, "unsupported_media_type");
        expectedStatus.put(V2ErrorCode.UNSUPPORTED_MEDIA_TYPE, new int[] { 415 });
        expectedCode.put(V2ErrorCode.PAYLOAD_TOO_LARGE, "payload_too_large");
        expectedStatus.put(V2ErrorCode.PAYLOAD_TOO_LARGE, new int[] { 413 });
        expectedCode.put(V2ErrorCode.SERVICE_UNAVAILABLE, "service_unavailable");
        expectedStatus.put(V2ErrorCode.SERVICE_UNAVAILABLE, new int[] { 503 });
        expectedCode.put(V2ErrorCode.NOT_ACCEPTABLE, "not_acceptable");
        expectedStatus.put(V2ErrorCode.NOT_ACCEPTABLE, new int[] { 406 });
        expectedCode.put(V2ErrorCode.INTERNAL_ERROR, "internal_error");
        expectedStatus.put(V2ErrorCode.INTERNAL_ERROR, new int[] { 500 });

        // Guard: the table must cover the whole enum. If new codes are introduced the
        // wire contract should be updated and this test should be extended.
        assertEquals(V2ErrorCode.values().length, expectedCode.size(), "expected/code table out of sync with the enum");

        for (final V2ErrorCode v : V2ErrorCode.values()) {
            assertEquals(expectedCode.get(v), v.code(), "code mismatch for " + v.name());
            assertEquals(expectedStatus.get(v)[0], v.defaultHttpStatus(), "http status mismatch for " + v.name());
        }
    }

    @Test
    public void test_methodNotAllowed_mapsTo_405() {
        assertEquals(405, V2ErrorCode.METHOD_NOT_ALLOWED.defaultHttpStatus());
        assertEquals("method_not_allowed", V2ErrorCode.METHOD_NOT_ALLOWED.code());
    }

    @Test
    public void test_conflict_mapsTo_409() {
        assertEquals(409, V2ErrorCode.CONFLICT.defaultHttpStatus());
        assertEquals("conflict", V2ErrorCode.CONFLICT.code());
    }

    @Test
    public void test_unsupportedMediaType_mapsTo_415() {
        assertEquals(415, V2ErrorCode.UNSUPPORTED_MEDIA_TYPE.defaultHttpStatus());
        assertEquals("unsupported_media_type", V2ErrorCode.UNSUPPORTED_MEDIA_TYPE.code());
    }

    @Test
    public void test_payloadTooLarge_mapsTo_413() {
        assertEquals(413, V2ErrorCode.PAYLOAD_TOO_LARGE.defaultHttpStatus());
        assertEquals("payload_too_large", V2ErrorCode.PAYLOAD_TOO_LARGE.code());
    }

    @Test
    public void test_serviceUnavailable_mapsTo_503() {
        assertEquals(503, V2ErrorCode.SERVICE_UNAVAILABLE.defaultHttpStatus());
        assertEquals("service_unavailable", V2ErrorCode.SERVICE_UNAVAILABLE.code());
    }

    @Test
    public void test_notAcceptable_mapsTo_406() {
        assertEquals(406, V2ErrorCode.NOT_ACCEPTABLE.defaultHttpStatus());
        assertEquals("not_acceptable", V2ErrorCode.NOT_ACCEPTABLE.code());
    }

    @Test
    public void test_codeStringsAreLowerSnakeCase() {
        // SPA clients branch on the string code; the wire contract is lower_snake_case
        // (matching the rest of the v2 payload field names). Any drift to camelCase or
        // hyphenation would break those clients silently — assert the shape here.
        final Pattern p = Pattern.compile("^[a-z][a-z_]*[a-z]$");
        for (final V2ErrorCode v : V2ErrorCode.values()) {
            assertTrue(p.matcher(v.code()).matches(), "non-lower-snake-case code: " + v.name() + " -> " + v.code());
        }
    }
}
