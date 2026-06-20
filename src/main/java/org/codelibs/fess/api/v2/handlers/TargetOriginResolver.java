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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.OriginUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Builds the canonical self-origin set ({@code trustedSameOrigins}) for the v2
 * CSRF Origin check. The trust boundary for forwarded headers is
 * enforced here. Registered as a Lasta DI component
 * ({@code targetOriginResolver}) so the resolution strategy is replaceable per
 * deployment.
 *
 * <p>Resolution priority:</p>
 * <ol>
 * <li>If {@code theme.api.csrf.server.origins} is configured, canonicalize each
 * value and return that set. This is header-independent and the hardest option —
 * the recommended setting behind reverse proxies.</li>
 * <li>Otherwise, only when {@code request.getRemoteAddr()} is a trusted proxy
 * ({@code rate.limit.trusted.proxies}), reconstruct the origin from the
 * {@code X-Forwarded-Proto} / {@code X-Forwarded-Host} (+ optional
 * {@code X-Forwarded-Port}) headers. A comma-separated {@code X-Forwarded-Host}
 * uses the first value. Forwarded headers from an untrusted source are never
 * trusted (spoof prevention).</li>
 * <li>Otherwise, reconstruct from the servlet request
 * ({@code getScheme}/{@code getServerName}/{@code getServerPort}).</li>
 * </ol>
 *
 * <p>The reconstructed origin is always canonicalized so default ports are
 * normalized consistently with the source origin.</p>
 */
public class TargetOriginResolver {

    /**
     * Default constructor. The resolver is stateless and intended to be
     * instantiated once by the DI container and shared across concurrent requests.
     */
    public TargetOriginResolver() {
        // no-op
    }

    /**
     * Resolves the set of canonical origins that are considered same-origin for
     * this request.
     *
     * @param request the current request (forwarded headers consulted only for trusted proxies)
     * @return a non-null set of canonical origins (may be empty if every configured value was invalid)
     */
    public Set<String> resolve(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        // (1) Explicit, header-independent configuration takes precedence.
        final Set<String> configured = fessConfig.getThemeApiCsrfServerOriginsAsSet();
        if (configured != null && !configured.isEmpty()) {
            final Set<String> result = new LinkedHashSet<>();
            for (final String value : configured) {
                final String canonical = OriginUtil.canonicalize(value);
                if (canonical != null) {
                    result.add(canonical);
                }
            }
            return result;
        }

        // (2) Forwarded headers are trusted only when the immediate peer is a trusted proxy.
        final String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && fessConfig.getRateLimitTrustedProxiesAsSet().contains(remoteAddr)) {
            final String forwarded = reconstructFromForwardedHeaders(request);
            if (forwarded != null) {
                return Collections.singleton(forwarded);
            }
        }

        // (3) Fall back to the servlet-observed scheme/host/port.
        final String canonical =
                OriginUtil.canonicalize(buildOrigin(request.getScheme(), request.getServerName(), request.getServerPort()));
        if (canonical != null) {
            return Collections.singleton(canonical);
        }
        return Collections.emptySet();
    }

    /**
     * Reconstructs the canonical origin from {@code X-Forwarded-*} headers, or
     * {@code null} when the required proto/host are absent or unusable. Only call
     * after verifying the request came from a trusted proxy.
     */
    private static String reconstructFromForwardedHeaders(final HttpServletRequest request) {
        final String proto = firstValue(request.getHeader("X-Forwarded-Proto"));
        final String host = firstValue(request.getHeader("X-Forwarded-Host"));
        if (StringUtil.isBlank(proto) || StringUtil.isBlank(host)) {
            return null;
        }
        // X-Forwarded-Host may already include a port (host:port); if a separate
        // X-Forwarded-Port is supplied and the host lacks one, append it.
        final String trimmedHost = host.trim();
        String origin = proto.trim() + "://" + trimmedHost;
        if (!hasPort(trimmedHost)) {
            final String port = firstValue(request.getHeader("X-Forwarded-Port"));
            if (StringUtil.isNotBlank(port)) {
                origin = origin + ":" + port.trim();
            }
        }
        return OriginUtil.canonicalize(origin);
    }

    /**
     * Returns whether the host already carries a port. For a bracketed IPv6 literal
     * (e.g. {@code [::1]} or {@code [::1]:8443}) the port separator is the {@code ':'}
     * after the closing {@code ']'}; the inner colons of the address must not be
     * mistaken for a port separator. For a non-bracketed host any {@code ':'}
     * indicates a port.
     */
    private static boolean hasPort(final String host) {
        if (host.startsWith("[")) {
            final int closing = host.lastIndexOf(']');
            // Bracketed literal: a port exists only if a ':' follows the closing ']'.
            return closing >= 0 && host.indexOf(':', closing + 1) >= 0;
        }
        return host.indexOf(':') >= 0;
    }

    /**
     * Returns the first value of a possibly comma-separated header value, trimmed,
     * or {@code null} when the input is blank.
     */
    private static String firstValue(final String headerValue) {
        if (StringUtil.isBlank(headerValue)) {
            return null;
        }
        final int comma = headerValue.indexOf(',');
        final String first = comma >= 0 ? headerValue.substring(0, comma) : headerValue;
        final String trimmed = first.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String buildOrigin(final String scheme, final String host, final int port) {
        if (StringUtil.isBlank(scheme) || StringUtil.isBlank(host)) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(scheme).append("://").append(host);
        if (port > 0) {
            buf.append(':').append(port);
        }
        return buf.toString();
    }
}
