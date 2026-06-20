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
package org.codelibs.fess.util;

import java.net.URI;
import java.util.Locale;

/**
 * Single source of truth for origin canonicalization used by the v2 CSRF Origin
 * check. Pure, side-effect-free static helper.
 *
 * <p>Normalizes an {@code Origin} header value or a full URL (e.g. {@code Referer})
 * into a canonical {@code "scheme://host[:port]"} string. The scheme and host are
 * lower-cased, the default port for the scheme is stripped ({@code https:443} /
 * {@code http:80}), and any path, query, or fragment is discarded.</p>
 *
 * <p>Invalid inputs all map to {@code null}: {@code null}, blank, the literal
 * string {@code "null"} (the value browsers send for opaque origins), values that
 * contain whitespace or control characters (CR/LF/TAB/etc.), values carrying
 * multiple space-separated tokens, values missing a scheme or host, and anything
 * that cannot be parsed. Parsing never throws — failures are caught and reported
 * as {@code null} so callers can treat {@code null} uniformly as "unusable".</p>
 */
public final class OriginUtil {

    private OriginUtil() {
    }

    /**
     * Canonicalizes an {@code Origin} header value or URL into
     * {@code "scheme://host[:port]"}.
     *
     * <p>Examples: {@code https://App.Example.com:443/path?x} →
     * {@code https://app.example.com}; {@code http://example.com:80} →
     * {@code http://example.com}.</p>
     *
     * <p>Note: hosts containing an underscore (e.g. {@code https://my_host}) canonicalize
     * to {@code null} because {@link java.net.URI#getHost()} returns {@code null} for them,
     * so such origins fail closed (are not treated as same-origin). Operators with such
     * internal hostnames should set {@code theme.api.csrf.server.origins} explicitly.</p>
     *
     * @param originOrUrl the raw Origin header value or URL (may be {@code null})
     * @return the canonical origin, or {@code null} if the input is missing,
     *         malformed, or otherwise unusable
     */
    public static String canonicalize(final String originOrUrl) {
        if (originOrUrl == null) {
            return null;
        }
        // Strip only surrounding ASCII spaces. Do NOT use String.trim() here: trim()
        // also removes trailing CR/LF/TAB, which would let an injected
        // "https://example.com\r\n..." slip through as a clean origin. Edge CR/LF/TAB
        // must be detected by the control-char scan below and rejected.
        final String value = stripSpaces(originOrUrl);
        if (value.isEmpty()) {
            return null;
        }
        // Browsers emit the literal "null" for opaque origins (e.g. sandboxed
        // iframes, data: URLs). It is never a trustworthy same-origin value.
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }
        // Reject any remaining whitespace or control character. A well-formed
        // Origin/URL is a single token without spaces; embedded space/CR/LF/TAB
        // indicate header injection or a multi-valued header and must not be
        // normalized away.
        for (int i = 0; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c <= ' ' || Character.isWhitespace(c) || Character.isISOControl(c)) {
                return null;
            }
        }
        try {
            final URI uri = new URI(value);
            final String scheme = uri.getScheme();
            if (scheme == null) {
                return null;
            }
            final String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                return null;
            }
            final String lowerScheme = scheme.toLowerCase(Locale.ROOT);
            final String lowerHost = host.toLowerCase(Locale.ROOT);
            final int port = uri.getPort();
            final StringBuilder buf = new StringBuilder();
            buf.append(lowerScheme).append("://").append(lowerHost);
            if (port != -1 && !isDefaultPort(lowerScheme, port)) {
                buf.append(':').append(port);
            }
            return buf.toString();
        } catch (final Exception e) {
            // URISyntaxException or any other parse failure — treat as unusable.
            return null;
        }
    }

    private static boolean isDefaultPort(final String scheme, final int port) {
        return ("https".equals(scheme) && port == 443) || ("http".equals(scheme) && port == 80);
    }

    /**
     * Removes only leading/trailing ASCII space (0x20) characters. Unlike
     * {@link String#trim()}, this deliberately preserves CR/LF/TAB and other
     * control characters so the caller's control-char scan can reject them.
     */
    private static String stripSpaces(final String value) {
        int start = 0;
        int end = value.length();
        while (start < end && value.charAt(start) == ' ') {
            start++;
        }
        while (end > start && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(start, end);
    }
}
