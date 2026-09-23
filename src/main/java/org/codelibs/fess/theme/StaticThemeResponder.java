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
package org.codelibs.fess.theme;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Serves the HTML entry file and asset files of a resolved static theme directly to the
 * servlet response.
 *
 * <p>This component is invoked by {@code StaticThemeFilter}, which resolves the active theme
 * for the current virtual host and decides — based on the request URI — whether to serve the
 * SPA entry HTML ({@link #serveIndex}) or a theme-relative static asset ({@link #serveAsset}).
 * The filter writes the response in place (no {@code RequestDispatcher} forward), so the
 * browser address bar keeps the original request URI.</p>
 *
 * <p>Security: {@link #resolveAsset(Theme, String)} enforces path-traversal protection by
 * rejecting absolute paths and any path containing {@code ".."}, re-checking the canonicalized
 * result is still under the theme base directory, rejecting symlinks, and applying a filename
 * denylist (see {@link #isBlockedFilename(String)}). The entry file is subjected to the same
 * filename denylist so a malicious manifest entry cannot serve internal files (e.g.
 * {@code theme.yml} or {@code .env}) as HTML.</p>
 *
 * <p>{@link #serveErrorPage} is the counterpart used outside that filter: the container's error
 * dispatch (registered via {@code <error-page>}) invokes it for a request that already failed at
 * an arbitrary URL, with a status the servlet — not this class — derives from the original
 * request. It shares its entry-file validation and response-header logic with {@link #serveIndex}
 * through {@link #readEntryBytes} and {@link #writeIndexBytes} so the two entry points cannot
 * drift apart.</p>
 */
public class StaticThemeResponder {

    /**
     * Content-Security-Policy for the SPA index HTML entry.
     * Restricts all resource origins to 'self', allows inline styles (required by most SPA themes), allows
     * images from http(s) and data: URIs (administrators put external images in related content and
     * notifications, which rendered on the JSP pages),
     * permits blob: frames and child frames so the cache viewer can display cached documents in a
     * sandboxed blob: iframe, and prevents the page itself from being embedded in frames.
     */
    static final String INDEX_CSP = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';"
            + " img-src 'self' data: http: https:; connect-src 'self'; frame-src blob:; child-src blob:; frame-ancestors 'none'; base-uri 'self'";

    /**
     * Content-Security-Policy for inline SVG assets.
     * Restricts resource loading to 'none' (SVG needs no external resources),
     * and permits inline styles that SVG documents often contain.
     */
    static final String SVG_CSP = "default-src 'none'; style-src 'unsafe-inline'";

    /**
     * Allowlist backing {@link #isSafeMessageKey}: letters, digits, dots, underscores and
     * hyphens only. Applies both to the {@code message_key} request parameter read by
     * {@link #resolveMessageKey} and to a {@code detailKey} passed directly to
     * {@link #serveErrorPage}. Compiled once rather than per call.
     */
    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("[A-Za-z0-9._\\-]+");

    /** The start tag of {@code <head>}, with or without attributes; never {@code <header>}. */
    private static final Pattern HEAD_START_TAG = Pattern.compile("<head(?:\\s[^>]*)?>", Pattern.CASE_INSENSITIVE);

    /**
     * Placeholder in a theme's entry file for the theme's own asset directory, relative to the
     * inserted {@code <base>} element: {@code {{themePath}}/assets/app.js} is served as
     * {@code themes/<name>/assets/app.js}. A copy of a theme saved under another name therefore
     * loads its own assets without editing its entry file.
     */
    static final String THEME_PATH_PLACEHOLDER = "{{themePath}}";

    /**
     * Default constructor.
     */
    public StaticThemeResponder() {
        // Default constructor
    }

    /**
     * Streams the entry HTML file of the given theme with {@code no-cache} semantics.
     *
     * <p>When {@code requestPath} starts with {@code /error}, the response also carries:
     * <ul>
     *   <li>{@code X-Fess-Route: error} — signals to the SPA that this is an error route.</li>
     *   <li>{@code X-Fess-Error-Code: <status>} — the HTTP status code derived from the URI
     *       (e.g. {@code 404} for {@code /error/notFound}).</li>
     *   <li>{@code <meta name="x-fess-error-code" content="<status>">} — the same status
     *       code injected as a meta tag into the HTML {@code <head>} so client-side JS can
     *       read it directly without relying on the HTTP header.</li>
     *   <li>{@code <meta name="x-fess-error-detail-key" content="...">} — when a safe
     *       {@code message_key} query parameter is present.</li>
     * </ul>
     *
     * @param req the current request (used to read the {@code message_key} parameter)
     * @param res the response to write to
     * @param theme the active theme whose entry file is served
     * @param requestPath the context-path-stripped request path (used for {@code /error} detection)
     * @throws IOException if writing the response fails
     */
    public void serveIndex(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final String requestPath)
            throws IOException {
        // Read the file upfront so the base element (and, on error routes, the meta tags) can be
        // injected and the final byte length is known before Content-Length is set.
        final byte[] originalBytes = readEntryBytes(theme);
        if (originalBytes == null) {
            sendNotFound(res);
            return;
        }
        final byte[] indexBytes = injectBaseHref(originalBytes, req.getContextPath());

        // Attach error diagnostic headers when the request targets an /error route, and set the
        // HTTP status so proxies and CDNs see the correct status without parsing X-Fess-Error-Code.
        // When a ?message_key query parameter is present, inject an error detail meta tag so the
        // SPA can surface the specific error message.
        final boolean isErrorRoute = requestPath != null && ("/error".equals(requestPath) || requestPath.startsWith("/error/"));
        final String messageKey = resolveMessageKey(req);

        if (isErrorRoute) {
            final int status = computeErrorStatus(requestPath);
            byte[] modifiedBytes = injectErrorCodeMeta(indexBytes, status);
            if (messageKey != null && !messageKey.isEmpty()) {
                modifiedBytes = injectErrorDetailMeta(modifiedBytes, messageKey);
            }
            writeIndexBytes(res, modifiedBytes, status, status);
            return;
        }

        writeIndexBytes(res, indexBytes, HttpServletResponse.SC_OK, null);
    }

    /**
     * Serves the theme's entry file as an error page: the given HTTP status, the SPA's error meta
     * tags, and the same hardening headers as a normal index response.
     *
     * <p>Unlike {@link #serveIndex}, the status and the displayed error code are supplied by the
     * caller: the container's error dispatch knows the real status, and the displayed code may
     * differ from it (a 401 is shown as a 403, because the SPA has no 401 page and the browser
     * already handled the challenge).</p>
     *
     * @param req the request being answered (used for the context path)
     * @param res the response to write to
     * @param theme the resolved theme
     * @param httpStatus the HTTP status to send
     * @param displayCode the error code the page shows (meta tag and {@code X-Fess-Error-Code})
     * @param detailKey an i18n key for the detail line, or null; unsafe values are dropped
     * @return true when the page was written; false when the theme could not supply its entry file
     *         and nothing was written to the response
     * @throws IOException if writing the response fails
     */
    public boolean serveErrorPage(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final int httpStatus,
            final int displayCode, final String detailKey) throws IOException {
        // Nothing must be written before this point: the caller (the error-page servlet) falls
        // back to its own minimal HTML when the theme cannot answer, and that decision has to be
        // made before any bytes reach the response.
        final byte[] entryBytes = readEntryBytes(theme);
        if (entryBytes == null) {
            return false;
        }
        byte[] html = injectErrorCodeMeta(injectBaseHref(entryBytes, req == null ? null : req.getContextPath()), displayCode);
        if (isSafeMessageKey(detailKey)) {
            html = injectErrorDetailMeta(html, detailKey);
        }
        writeIndexBytes(res, html, httpStatus, displayCode);
        return true;
    }

    /**
     * Streams a static asset from the given theme's base directory after validating the path.
     *
     * @param req the current request (used for conditional-GET headers)
     * @param res the response to write to
     * @param theme the active theme whose base directory anchors the resolution
     * @param assetPath theme-relative asset path supplied by the filter
     * @throws IOException if writing the response fails
     */
    public void serveAsset(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final String assetPath)
            throws IOException {
        final Path file = resolveAsset(theme, assetPath);
        if (file == null) {
            sendNotFound(res);
            return;
        }
        streamFile(req, res, file);
    }

    /**
     * Derives the HTTP status code corresponding to an {@code /error/...} URI.
     *
     * <p>Mapping:
     * <ul>
     *   <li>{@code /error}, {@code /error/error}, {@code /error/system} → 500</li>
     *   <li>{@code /error/badRequest} or {@code /error/badrequest} → 400</li>
     *   <li>{@code /error/notFound} or {@code /error/notfound} → 404</li>
     *   <li>{@code /error/busy} → 429</li>
     *   <li>{@code /error/403} or {@code /error/forbidden} → 403</li>
     *   <li>{@code /error/503}, {@code /error/serviceUnavailable} or
     *       {@code /error/service_unavailable} → 503</li>
     *   <li>Any other {@code /error/*} path → 500</li>
     * </ul>
     *
     * @param uri the request path (context-path-stripped); must start with {@code /error}
     * @return the HTTP status code to report in the {@code X-Fess-Error-Code} header
     */
    static int computeErrorStatus(final String uri) {
        if (uri == null) {
            return 500;
        }
        // Extract the kind segment after "/error/"
        final String kind;
        if ("/error".equals(uri) || !uri.startsWith("/error/")) {
            kind = "";
        } else {
            // Take only the first path segment after /error/ (ignore trailing /...),
            // and normalise to lower-case for case-insensitive matching.
            final String rest = uri.substring("/error/".length());
            final int slash = rest.indexOf('/');
            kind = (slash < 0 ? rest : rest.substring(0, slash)).toLowerCase(Locale.ROOT);
        }
        return switch (kind) {
        case "", "error", "system" -> 500;
        case "badrequest" -> 400;
        case "notfound" -> 404;
        case "busy" -> 429;
        case "403", "forbidden" -> 403;
        case "503", "serviceunavailable", "service_unavailable" -> 503;
        default -> 500;
        };
    }

    /**
     * Resolves the {@code message_key} query parameter for the current request.
     *
     * <p>The value is sanitised: only alphanumeric characters, dots, underscores and
     * hyphens are allowed (matching the conventional key naming scheme such as
     * {@code errors.docid_not_found}). Any other value is rejected and {@code null} returned.
     *
     * @param req the current request
     * @return the sanitised message key, or {@code null} if absent/unsafe
     */
    String resolveMessageKey(final HttpServletRequest req) {
        if (req == null) {
            return null;
        }
        final String raw = req.getParameter("message_key");
        return isSafeMessageKey(raw) ? raw : null;
    }

    /**
     * Tests whether a candidate message key is safe to inject into HTML as a
     * {@code x-fess-error-detail-key} meta tag value.
     *
     * <p>This is the {@code message_key} allowlist — letters, digits, dots, underscores and
     * hyphens only, matching the conventional key naming scheme such as
     * {@code errors.docid_not_found} — extracted so both {@link #resolveMessageKey} (a query
     * parameter read from the request) and {@link #serveErrorPage} (a detail key supplied
     * directly by the caller, e.g. the error-page servlet) apply the same rule.</p>
     *
     * @param key the candidate key; {@code null} or empty is never safe
     * @return {@code true} when the key is non-empty and contains only allowlisted characters
     */
    public static boolean isSafeMessageKey(final String key) {
        return key != null && MESSAGE_KEY_PATTERN.matcher(key).matches();
    }

    /**
     * Inserts {@code <base href="{contextPath}/">} right after the {@code <head>} start tag, so a
     * theme's relative URLs resolve against the web application root whatever context path Fess is
     * deployed under. {@code INDEX_CSP}'s {@code base-uri 'self'} allows it: the href is always
     * on this origin. The bytes are returned unchanged when there is no {@code <head>} tag.
     *
     * @param htmlBytes UTF-8 encoded HTML bytes
     * @param contextPath the request's context path; null or empty for the root
     * @return the HTML with the base element inserted
     */
    static byte[] injectBaseHref(final byte[] htmlBytes, final String contextPath) {
        if (htmlBytes == null) {
            return htmlBytes;
        }
        final String html = new String(htmlBytes, StandardCharsets.UTF_8);
        final Matcher matcher = HEAD_START_TAG.matcher(html);
        if (!matcher.find()) {
            return htmlBytes;
        }
        final String href = ((contextPath == null ? "" : contextPath) + "/").replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        final String modified = html.substring(0, matcher.end()) + "<base href=\"" + href + "\">" + html.substring(matcher.end());
        return modified.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Replaces every {@link #THEME_PATH_PLACEHOLDER} in the entry HTML with {@code themes/<name>},
     * the path {@code StaticThemeFilter} serves the theme's assets under. Theme names match
     * {@link ThemeManifest#NAME_PATTERN}, so the result needs no escaping. The bytes are returned
     * unchanged when there is no placeholder.
     *
     * @param htmlBytes UTF-8 encoded HTML bytes
     * @param themeName the name of the theme the entry file belongs to
     * @return the HTML with the placeholder expanded
     */
    static byte[] expandThemePath(final byte[] htmlBytes, final String themeName) {
        if (htmlBytes == null || themeName == null) {
            return htmlBytes;
        }
        final String html = new String(htmlBytes, StandardCharsets.UTF_8);
        if (!html.contains(THEME_PATH_PLACEHOLDER)) {
            return htmlBytes;
        }
        return html.replace(THEME_PATH_PLACEHOLDER, "themes/" + themeName).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Injects a {@code <meta>} tag carrying the HTTP error code into the HTML bytes
     * immediately before the closing {@code </head>} tag.
     *
     * <p>The injected tag has the form:
     * {@code <meta name="x-fess-error-code" content="<status>">}, where {@code status}
     * is the numeric HTTP status code (e.g. {@code 404}, {@code 429}, {@code 500}).
     * If {@code </head>} is not found in the bytes the original bytes are returned unchanged.
     *
     * @param htmlBytes UTF-8 encoded HTML bytes
     * @param status HTTP status code (e.g. 404, 429, 500)
     * @return modified bytes with the error-code meta tag injected before {@code </head>}
     */
    static byte[] injectErrorCodeMeta(final byte[] htmlBytes, final int status) {
        if (htmlBytes == null) {
            return htmlBytes;
        }
        final String html = new String(htmlBytes, StandardCharsets.UTF_8);
        final int headEndIdx = html.indexOf("</head>");
        if (headEndIdx < 0) {
            return htmlBytes;
        }
        final String injection = "<meta name=\"x-fess-error-code\" content=\"" + status + "\">\n";
        final String modified = html.substring(0, headEndIdx) + injection + html.substring(headEndIdx);
        return modified.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Injects a {@code <meta>} tag carrying an error detail key into the HTML bytes
     * immediately before the closing {@code </head>} tag.
     *
     * <p>The injected tag has the form:
     * {@code <meta name="x-fess-error-detail-key" content="...">} — the i18n key.
     * If {@code </head>} is not found in the bytes the original bytes are returned unchanged.
     *
     * @param htmlBytes UTF-8 encoded HTML bytes
     * @param messageKey validated, safe message key
     * @return modified bytes with meta tag injected before {@code </head>}
     */
    static byte[] injectErrorDetailMeta(final byte[] htmlBytes, final String messageKey) {
        if (htmlBytes == null || messageKey == null || messageKey.isEmpty()) {
            return htmlBytes;
        }
        final String html = new String(htmlBytes, StandardCharsets.UTF_8);
        final int headEndIdx = html.indexOf("</head>");
        if (headEndIdx < 0) {
            return htmlBytes;
        }
        // Escape the key to prevent injection — allowlist already guarantees the key
        // contains only safe chars, but be explicit for defence in depth.
        final String safeKey = messageKey.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
        final String injection = "<meta name=\"x-fess-error-detail-key\" content=\"" + safeKey + "\">\n";
        final String modified = html.substring(0, headEndIdx) + injection + html.substring(headEndIdx);
        return modified.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Validates a theme-relative asset path and returns the resolved file path, or {@code null}
     * if the path is unsafe, escapes the theme base directory, or does not point at a regular file.
     *
     * @param theme active theme whose base directory anchors the resolution
     * @param path candidate theme-relative path; {@code null}, absolute, or {@code ".."}-containing
     *        values are rejected outright
     * @return resolved file path within the theme directory, or {@code null} if invalid
     */
    Path resolveAsset(final Theme theme, final String path) {
        if (path == null || path.startsWith("/") || path.contains("\\") || path.contains("\0")) {
            return null;
        }
        // Per-segment traversal check — avoid blocking 'foo..bar.txt' while rejecting '..'
        for (final String seg : path.split("/", -1)) {
            if ("..".equals(seg)) {
                return null;
            }
        }
        final Path candidate = theme.getBasePath().resolve(path).normalize();
        // Re-check containment after normalization.
        // Reject symlinks (NOFOLLOW_LINKS): a symlink could escape the theme sandbox.
        if (!candidate.startsWith(theme.getBasePath()) || !Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)) {
            return null;
        }
        final String fname = candidate.getFileName() != null ? candidate.getFileName().toString() : "";
        // Reject dotfiles and sensitive manifest/documentation files.
        if (isBlockedFilename(fname)) {
            return null;
        }
        return candidate;
    }

    /**
     * Returns {@code true} when a filename should never be served, regardless of
     * its position inside the theme directory.
     *
     * <p>Blocked:
     * <ul>
     *   <li>Any file whose name starts with {@code "."} (dotfile, e.g. {@code .env}).</li>
     *   <li>{@code theme.yml} — the manifest is internal metadata, not a servable asset.</li>
     *   <li>{@code README.md}, {@code CHANGELOG.md} — documentation leakage.</li>
     *   <li>Any file whose lowercase name starts with {@code "license"} (e.g.
     *       {@code LICENSE}, {@code LICENSE.txt}).</li>
     * </ul>
     *
     * @param filename the bare filename (not a path) to test
     * @return {@code true} if the file must not be served
     */
    static boolean isBlockedFilename(final String filename) {
        if (filename == null || filename.isEmpty() || filename.startsWith(".")) {
            return true;
        }
        final String lower = filename.toLowerCase(Locale.ROOT);
        if ("theme.yml".equals(lower) || "readme.md".equals(lower) || "changelog.md".equals(lower)) {
            return true;
        }
        if (lower.startsWith("license")) {
            return true;
        }
        return false;
    }

    /**
     * Maps a filename to its HTTP {@code Content-Type}.
     *
     * @param name the filename
     * @return the content type, or {@code application/octet-stream} when unknown
     */
    static String contentTypeFor(final String name) {
        final String lower = name == null ? "" : name.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".mjs") || lower.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml; charset=UTF-8";
        }
        if (lower.endsWith(".wasm")) {
            return "application/wasm";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".woff2")) {
            return "font/woff2";
        }
        if (lower.endsWith(".woff")) {
            return "font/woff";
        }
        if (lower.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }
        if (lower.endsWith(".webmanifest")) {
            return "application/manifest+json; charset=UTF-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        }
        return "application/octet-stream";
    }

    /**
     * Streams a validated asset file with caching, conditional-GET (304) and content-type headers.
     *
     * @param req the current request (for {@code If-None-Match}/{@code If-Modified-Since})
     * @param res the response to write to
     * @param file the validated asset file
     * @throws IOException if writing the response fails
     */
    private void streamFile(final HttpServletRequest req, final HttpServletResponse res, final Path file) throws IOException {
        final String name = file.getFileName().toString();
        final String contentType = contentTypeFor(name);
        final boolean isSvg = name.toLowerCase(Locale.ROOT).endsWith(".svg");

        final BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(file, BasicFileAttributes.class);
        } catch (final IOException e) {
            sendNotFound(res);
            return;
        }
        final long size = attrs.size();
        final long mtime = attrs.lastModifiedTime().toMillis();
        final String etag = "W/\"" + size + "-" + mtime + "\"";
        final String lastModified =
                DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(mtime), ZoneOffset.UTC));

        // Honor If-None-Match
        final String ifNoneMatch = req == null ? null : req.getHeader("If-None-Match");
        if (etag.equals(ifNoneMatch)) {
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            res.setContentType(contentType);
            return;
        }

        // Honor If-Modified-Since (basic: compare epoch millis rounded to seconds)
        final String ifModifiedSince = req == null ? null : req.getHeader("If-Modified-Since");
        if (ifModifiedSince != null) {
            try {
                final long clientMtime =
                        DateTimeFormatter.RFC_1123_DATE_TIME.parse(ifModifiedSince, ZonedDateTime::from).toInstant().toEpochMilli();
                // truncate to seconds for comparison
                if ((mtime / 1000) <= (clientMtime / 1000)) {
                    res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    res.setContentType(contentType);
                    return;
                }
            } catch (final Exception ignore) {
                // malformed header — serve normally
            }
        }

        // X-Content-Type-Options: nosniff is added by Tomcat HttpHeaderSecurityFilter (web.xml).
        // Assets load inline so <link>/<script>/<img> references in index.html behave normally.
        // HTML asset files are forced to "attachment" to prevent same-origin XSS via a malicious
        // theme dropping an executable HTML page next to the SPA entry — only index.html (served
        // by serveIndex() with INDEX_CSP) should render as HTML. SVG keeps inline because SVG_CSP
        // below blocks script execution.
        final boolean isHtml = "text/html; charset=UTF-8".equals(contentType);
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(contentType);
        res.setHeader("Cache-Control", "public, max-age=86400");
        res.setHeader("Vary", "Accept-Encoding");
        res.setHeader("Referrer-Policy", "same-origin");
        res.setHeader("ETag", etag);
        res.setHeader("Last-Modified", lastModified);
        res.setContentLengthLong(size);
        res.setHeader("Content-Disposition", (isHtml ? "attachment" : "inline") + "; filename=\"" + name + "\"");
        if (isSvg) {
            res.setHeader("Content-Security-Policy", SVG_CSP);
        }
        try (InputStream in = Files.newInputStream(file)) {
            in.transferTo(res.getOutputStream());
        }
    }

    /**
     * Reads the theme's entry file after the same validation {@link #serveIndex} applies.
     *
     * <p>Used by both {@link #serveIndex} and {@link #serveErrorPage} so the entry-file
     * resolution and validation rules (denylist, containment check, regular-file check) exist in
     * exactly one place.</p>
     *
     * @param theme the theme whose entry file is read
     * @return the file's bytes with {@link #THEME_PATH_PLACEHOLDER} expanded, or null when the
     *         entry is missing, blocked or unreadable
     */
    private byte[] readEntryBytes(final Theme theme) {
        final String entry = theme.getManifest().map(ThemeManifest::getEntry).orElse("index.html");
        final Path indexFile = theme.getBasePath().resolve(entry).normalize();
        // Apply the same filename denylist as resolveAsset: manifest validation rejects
        // traversal but not dotfiles/theme.yml/etc., so a malicious manifest entry must
        // not be able to serve internal files (e.g. entry: theme.yml or .env) as HTML.
        if (!indexFile.startsWith(theme.getBasePath()) || isBlockedFilename(indexFile.getFileName().toString())
                || !Files.isRegularFile(indexFile)) {
            return null;
        }
        // TOCTOU note: isRegularFile() and Files.size() are separate syscalls.
        // A concurrent theme replacement (atomic move + REPLACE_EXISTING) between
        // these calls is theoretically possible but extremely unlikely in practice.
        // The worst outcome is a transient 404 (serveIndex) or fallback HTML (serveErrorPage's
        // caller) or a Content-Length mismatch caught by the HTTP client; no correctness
        // invariant is violated.
        final long fileSize;
        try {
            fileSize = Files.size(indexFile);
        } catch (final IOException e) {
            return null;
        }
        try (InputStream in = Files.newInputStream(indexFile)) {
            final ByteArrayOutputStream buf = new ByteArrayOutputStream((int) fileSize + 256);
            in.transferTo(buf);
            return expandThemePath(buf.toByteArray(), theme.getName());
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Writes HTML bytes with the index response headers.
     *
     * <p>Used by both {@link #serveIndex} and {@link #serveErrorPage} so the header set for an
     * index-style HTML response — content type, caching, hardening headers, and the optional
     * error diagnostics — exists in exactly one place and cannot drift between the two entry
     * points.</p>
     *
     * @param res the response to write to
     * @param html the bytes to write
     * @param status the HTTP status
     * @param errorCode the error code for the diagnostic headers, or null for a normal page
     * @throws IOException if writing fails
     */
    private void writeIndexBytes(final HttpServletResponse res, final byte[] html, final int status, final Integer errorCode)
            throws IOException {
        // X-Content-Type-Options: nosniff is not set here. For serveIndex()'s caller
        // (StaticThemeFilter, a REQUEST dispatch) Tomcat's HttpHeaderSecurityFilter (web.xml)
        // adds it. That filter is REQUEST-dispatch only, so it never runs for serveErrorPage()'s
        // caller (the container's ERROR/FORWARD error dispatch); there, ErrorPageServlet sets the
        // header itself before calling serveErrorPage() -- the right place, since this method is
        // shared by both callers and cannot tell which dispatch type it is running under.
        // Content-Disposition must be inline; otherwise the browser would download index.html
        // instead of rendering it.
        res.setStatus(status);
        res.setContentType("text/html; charset=UTF-8");
        res.setHeader("Content-Disposition", "inline; filename=\"index.html\"");
        res.setHeader("Cache-Control", "no-store");
        res.setHeader("Content-Security-Policy", INDEX_CSP);
        // Clickjacking defense-in-depth: INDEX_CSP already sets frame-ancestors 'none'
        // (enforced via this HTTP header, unlike a <meta> CSP); X-Frame-Options covers
        // older browsers that don't honor frame-ancestors.
        res.setHeader("X-Frame-Options", "DENY");
        res.setHeader("Referrer-Policy", "same-origin");
        if (errorCode != null) {
            res.setHeader("X-Fess-Route", "error");
            res.setHeader("X-Fess-Error-Code", String.valueOf(errorCode));
        }
        res.setContentLength(html.length);
        res.getOutputStream().write(html);
    }

    /**
     * Writes a {@code 404 Not Found} plain-text response.
     *
     * @param res the response to write to
     * @throws IOException if writing the response fails
     */
    private void sendNotFound(final HttpServletResponse res) throws IOException {
        final byte[] body = "Not Found".getBytes(StandardCharsets.UTF_8);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.setContentType("text/plain; charset=UTF-8");
        res.setContentLength(body.length);
        res.getOutputStream().write(body);
    }
}
