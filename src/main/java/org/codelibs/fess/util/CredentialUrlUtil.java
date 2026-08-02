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

import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Credential handling for operator-configured endpoint URLs: whether one embeds a credential, how
 * to keep one out of a log, and how to report a URL that will not parse without quoting it back.
 *
 * <p>Every provider integration - the built-in OpenSearch embedding client and each external
 * {@code fess-llm-*} / embedding plugin - accepts a user-configured {@code api.url} that may point
 * at a proxy or gateway, and each logs it. This class is the single definition of those rules, so
 * a masking rule added for one provider reaches all of them.</p>
 *
 * <p>What stays with the caller is the <em>remedy</em>: which configuration key to fix and which
 * authentication mechanism to use instead are provider-specific, so the rejection message is built
 * by the provider, not here.</p>
 */
public final class CredentialUrlUtil {

    /**
     * Matches a credential-bearing query parameter. Names cover the spellings the supported
     * providers document ({@code api_key}, {@code apikey}, {@code api-key}, {@code key},
     * {@code token}, {@code access_token}, {@code access-token}), case-insensitively.
     */
    private static final Pattern CREDENTIAL_QUERY_PARAM_PATTERN =
            Pattern.compile("(?i)([?&](?:api[-_]?key|key|token|access[-_]?token)=)[^&]*");

    /**
     * Matches RFC 3986 userinfo in a URL authority ({@code scheme://user:pass@host}). Neither
     * userinfo component may contain {@code /}, {@code @}, or whitespace, so the pattern cannot run
     * past the authority into the path and cannot mistake a {@code host:port} authority (no
     * {@code @}) or a later {@code @} in the path for a credential.
     *
     * <p>Excluding whitespace also means this pattern does <em>not</em> match a credential
     * containing a space, which is exactly the shape a mistyped one tends to have. That is why
     * masking must never be the thing a URL's safety rests on - use {@link #hasUserInfo(String)} to
     * refuse the value up front, and treat masking as a backstop.</p>
     */
    private static final Pattern CREDENTIAL_USER_INFO_PATTERN = Pattern.compile("(?i)(://)[^/@\\s]*:[^/@\\s]*@");

    /** Separator between the scheme and the authority of an absolute URL. */
    private static final String SCHEME_SEPARATOR = "://";

    private CredentialUrlUtil() {
        // utility class
    }

    /**
     * Masks credentials embedded in a URL, in both forms one can appear: a credential-bearing query
     * parameter (value replaced with {@code ***}) and RFC 3986 userinfo (replaced with
     * {@code ***:***}).
     *
     * <p>The query-parameter rule covers a credential a <em>working</em> configuration can genuinely
     * carry - several providers document passing the API key as {@code ?key=...} - while the
     * userinfo rule is a backstop for a value that should have been refused by
     * {@link #hasUserInfo(String)} before any log statement rendered it. See
     * {@link #CREDENTIAL_USER_INFO_PATTERN} for why the backstop is not something to rely on.</p>
     *
     * @param url the URL to mask (may be {@code null})
     * @return the URL with credential values replaced by {@code ***}, or {@code null} when the
     *         input is {@code null}
     */
    public static String maskCredentialInUrl(final String url) {
        if (url == null) {
            return null;
        }
        final String withoutUserInfo = CREDENTIAL_USER_INFO_PATTERN.matcher(url).replaceAll("$1***:***@");
        return CREDENTIAL_QUERY_PARAM_PATTERN.matcher(withoutUserInfo).replaceAll("$1***");
    }

    /**
     * Determines whether {@code url}'s authority carries an RFC 3986 userinfo component, i.e.
     * whether the URL embeds a credential as {@code scheme://user:password@host}.
     *
     * <p>Detection is structural rather than pattern-based, and deliberately not a
     * {@link java.net.URI} parse. The values most worth catching are the ones a parser rejects
     * outright - a raw space in the password, say - and rejecting them yields a
     * {@link URISyntaxException} whose message quotes the credential straight back. A textual scan
     * answers the question for any input at all.</p>
     *
     * <p>The authority is located by delimiter position: it begins after {@code "://"} when what
     * precedes that could be a scheme, after a leading {@code "//"} for a protocol-relative
     * reference, and otherwise at the start of the string; it ends at the first {@code /},
     * {@code ?} or {@code #}. Userinfo is present exactly when that span contains an {@code @}.</p>
     *
     * <p>Each clause exists because a narrower rule missed a real case:</p>
     * <ul>
     * <li>The <em>scheme guard</em> keeps a {@code "://"} that appears after a path character from
     * being read as a scheme delimiter, which would make the text after it look like an
     * authority.</li>
     * <li>The <em>protocol-relative</em> clause catches {@code //user:pw@host}, which a rule
     * requiring {@code "://"} misses entirely.</li>
     * <li>The <em>scheme-less</em> fallback catches a bare {@code user:pw@host}. That is not a
     * valid absolute URI, but it is a credential an operator typed into a URL setting, and the
     * useful answer is to refuse it rather than hand it to the HTTP client - which fails anyway,
     * with the credential in the message.</li>
     * </ul>
     *
     * <p>A plain {@code host:port} carries no {@code @}, and an {@code @} in a path, query or
     * fragment falls outside the authority, so neither is a false positive.</p>
     *
     * @param url the configured URL (may be {@code null})
     * @return {@code true} when the URL has an authority containing a userinfo component
     */
    public static boolean hasUserInfo(final String url) {
        if (url == null) {
            return false;
        }
        final String trimmed = url.trim();
        final int authorityStart;
        final int schemeEnd = trimmed.indexOf(SCHEME_SEPARATOR);
        if (schemeEnd >= 0 && couldBeScheme(trimmed, schemeEnd)) {
            authorityStart = schemeEnd + SCHEME_SEPARATOR.length();
        } else if (trimmed.startsWith("//")) {
            authorityStart = 2;
        } else {
            authorityStart = 0;
        }
        int authorityEnd = trimmed.length();
        for (int i = authorityStart; i < trimmed.length(); i++) {
            final char c = trimmed.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                authorityEnd = i;
                break;
            }
        }
        return authorityStart < authorityEnd && trimmed.lastIndexOf('@', authorityEnd - 1) >= authorityStart;
    }

    /**
     * Returns whether {@code [0, end)} of {@code url} could be a URI scheme, i.e. holds no
     * character that would place it in a path, query or fragment instead.
     *
     * <p>Deliberately permissive rather than the exact RFC 3986 scheme grammar: a value that only
     * <em>nearly</em> looks like a scheme should still have the text after {@code "://"} treated as
     * an authority and scanned for a credential. Being wrong in that direction costs a refused
     * configuration that could never have worked; being wrong the other way leaks one.</p>
     *
     * @param url the URL being inspected
     * @param end exclusive end index of the candidate scheme
     * @return true when nothing in the span rules out a scheme
     */
    private static boolean couldBeScheme(final String url, final int end) {
        for (int i = 0; i < end; i++) {
            final char c = url.charAt(i);
            if (c == '/' || c == '?' || c == '#' || c == '@') {
                return false;
            }
        }
        return true;
    }

    /**
     * Builds the replacement for an {@link IllegalArgumentException} raised while a request URI is
     * being constructed from a configured URL.
     *
     * <p>That exception quotes the offending URI verbatim and carries a {@link URISyntaxException}
     * that quotes it again, so it leaks a configured credential through every channel at once: the
     * message, the throwable attached to a log statement, and the cause chain every upstream handler
     * renders.</p>
     *
     * <p>The replacement carries no part of the URL, not even a masked one:
     * {@link #maskCredentialInUrl(String)} is at its least reliable here, because a URL reaches this
     * path only by containing a character the patterns exclude. What is left is the name of the
     * setting to look at plus the parser's own reason and index, which {@link URISyntaxException}
     * keeps separate from the offending input. There is no cause, so nothing downstream can recover
     * the raw URI.</p>
     *
     * @param configKey the configuration property the URL was read from, named so an operator knows
     *            what to inspect
     * @param e the exception raised while constructing the request URI (may be {@code null})
     * @return the replacement exception to throw in place of {@code e}
     */
    public static IllegalArgumentException invalidUrlException(final String configKey, final IllegalArgumentException e) {
        final StringBuilder buf = new StringBuilder("Invalid URL configured in ").append(configKey);
        if (e != null && e.getCause() instanceof final URISyntaxException syntaxException) {
            if (syntaxException.getReason() != null) {
                buf.append(": ").append(syntaxException.getReason());
            }
            if (syntaxException.getIndex() >= 0) {
                buf.append(" at index ").append(syntaxException.getIndex());
            }
        }
        return new IllegalArgumentException(buf.toString());
    }
}
