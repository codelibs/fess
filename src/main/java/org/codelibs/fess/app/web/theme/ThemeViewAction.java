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
package org.codelibs.fess.app.web.theme;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeRegistry;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * Action that serves the HTML entry file and asset files of a resolved static theme.
 *
 * <p>HTTP routing is performed by {@code StaticThemeFilter}, which inspects the request URI
 * and (when the request targets a theme) sets request attributes describing how this action
 * should respond before forwarding to {@code /theme-view}:
 * <ul>
 *   <li>{@link #REQ_ATTR_MODE} — {@code "INDEX"} (serve entry HTML) or {@code "ASSET"} (serve a static asset).</li>
 *   <li>{@link #REQ_ATTR_ASSET_PATH} — theme-relative asset path, used only when mode is {@code "ASSET"}.</li>
 * </ul>
 *
 * <p>The {@link #resolveAsset(Theme, String)} helper enforces path-traversal protection by rejecting
 * absolute paths and any path containing {@code ".."}, and by re-checking the canonicalized result
 * is still under the theme base directory.</p>
 */
public class ThemeViewAction extends FessSearchAction {

    private static final long serialVersionUID = 1L;

    /** Request attribute key for the view mode set by the filter: {@code "INDEX"} or {@code "ASSET"}. */
    public static final String REQ_ATTR_MODE = "fess.theme.view.mode";

    /** Request attribute key for the theme-relative asset path set by the filter when mode is {@code "ASSET"}. */
    public static final String REQ_ATTR_ASSET_PATH = "fess.theme.view.asset.path";

    /**
     * Content-Security-Policy for the SPA index HTML entry.
     * Restricts all resource origins to 'self', allows inline styles (required by most SPA themes),
     * and prevents the page from being embedded in frames.
     */
    static final String INDEX_CSP = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline';"
            + " img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'";

    /**
     * Content-Security-Policy for inline SVG assets.
     * Restricts resource loading to 'none' (SVG needs no external resources),
     * and permits inline styles that SVG documents often contain.
     */
    static final String SVG_CSP = "default-src 'none'; style-src 'unsafe-inline'";

    /** Registry providing the active static theme for the current virtual host. */
    @Resource
    protected ThemeRegistry themeRegistry;

    /** Helper used to derive the virtual-host key for theme resolution. */
    @Resource
    protected VirtualHostHelper virtualHostHelper;

    // requestManager is inherited from FessBaseAction (promoted to protected).
    // LastaFlute guarantees DI injection before any @Execute invocation,
    // so no defensive null checks are needed in @Execute methods.

    /**
     * Default constructor.
     */
    public ThemeViewAction() {
        super();
    }

    /**
     * Single entry point invoked by LastaFlute after {@code StaticThemeFilter} forwards the request.
     * The filter pre-populates {@link #REQ_ATTR_MODE} (and {@link #REQ_ATTR_ASSET_PATH} for assets)
     * so this method can dispatch to the right handler without route parameters.
     *
     * @return stream response containing the requested HTML entry or asset, or a 404 response
     */
    @Execute
    public ActionResponse index() {
        final String mode = requestManager.getAttribute(REQ_ATTR_MODE, String.class).orElse("INDEX");
        if ("ASSET".equals(mode)) {
            final String path = requestManager.getAttribute(REQ_ATTR_ASSET_PATH, String.class).orElse(null);
            return serveAsset(path);
        }
        return serveIndex();
    }

    /**
     * Streams the entry HTML file of the active theme with {@code no-cache} semantics.
     *
     * @return stream response for the entry file, or a 404 response if no theme/entry is available
     */
    ActionResponse serveIndex() {
        final Theme theme = resolveTheme();
        if (theme == null) {
            return notFound();
        }
        final String entry = theme.getManifest().map(m -> m.getEntry()).orElse("index.html");
        final Path indexFile = theme.getBasePath().resolve(entry).normalize();
        if (!indexFile.startsWith(theme.getBasePath()) || !Files.isRegularFile(indexFile)) {
            return notFound();
        }
        // TOCTOU note: isRegularFile() and Files.size() are separate syscalls.
        // A concurrent theme replacement (atomic move + REPLACE_EXISTING) between
        // these calls is theoretically possible but extremely unlikely in practice.
        // The worst outcome is a transient 404 or a Content-Length mismatch caught
        // by the HTTP client; no correctness invariant is violated. A fully atomic
        // approach would require opening the FileChannel first and deriving size
        // from the channel, but the complexity is not justified here.
        final long fileSize;
        try {
            fileSize = Files.size(indexFile);
        } catch (final java.io.IOException e) {
            return notFound();
        }
        // X-Content-Type-Options: nosniff is added by Tomcat HttpHeaderSecurityFilter (web.xml).
        return new StreamResponse("index.html").contentType("text/html; charset=UTF-8")
                .header("Cache-Control", "no-store")
                .header("Content-Security-Policy", INDEX_CSP)
                .header("Referrer-Policy", "same-origin")
                .header("Content-Length", String.valueOf(fileSize))
                .stream(out -> {
                    try (InputStream in = Files.newInputStream(indexFile)) {
                        in.transferTo(out.stream());
                    }
                });
    }

    /**
     * Streams a static asset from the active theme's base directory after validating the path.
     *
     * @param path theme-relative asset path supplied by the filter
     * @return stream response for the asset, or a 404 response if the path is rejected or missing
     */
    ActionResponse serveAsset(final String path) {
        final Theme theme = resolveTheme();
        if (theme == null) {
            return notFound();
        }
        final Path file = resolveAsset(theme, path);
        if (file == null) {
            return notFound();
        }
        return streamFile(file);
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
        if (!candidate.startsWith(theme.getBasePath())) {
            return null;
        }
        // Reject symlinks (NOFOLLOW_LINKS): a symlink could escape the theme sandbox.
        if (!Files.isRegularFile(candidate, LinkOption.NOFOLLOW_LINKS)) {
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
        if (filename == null || filename.isEmpty()) {
            return true;
        }
        if (filename.startsWith(".")) {
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

    private Theme resolveTheme() {
        final String key = virtualHostHelper == null ? null : virtualHostHelper.getVirtualHostKey();
        return themeRegistry.resolveActiveTheme(key).orElse(null);
    }

    private ActionResponse notFound() {
        final byte[] body = "Not Found".getBytes(StandardCharsets.UTF_8);
        return new StreamResponse("404").httpStatus(404).contentType("text/plain; charset=UTF-8").stream(out -> out.stream().write(body));
    }

    private ActionResponse streamFile(final Path file) {
        final String name = file.getFileName().toString();
        final String contentType = contentTypeFor(name);
        final boolean isSvg = name.toLowerCase(Locale.ROOT).endsWith(".svg");

        final BasicFileAttributes attrs;
        try {
            attrs = Files.readAttributes(file, BasicFileAttributes.class);
        } catch (final java.io.IOException e) {
            return notFound();
        }
        final long size = attrs.size();
        final long mtime = attrs.lastModifiedTime().toMillis();
        final String etag = "W/\"" + size + "-" + mtime + "\"";
        final String lastModified = java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
                .format(java.time.ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(mtime), java.time.ZoneOffset.UTC));

        // Honor If-None-Match
        final String ifNoneMatch = requestManager != null ? requestManager.getHeader("If-None-Match").orElse(null) : null;
        if (etag.equals(ifNoneMatch)) {
            return new StreamResponse(name).httpStatus(304).contentType(contentType).stream(out -> {
                // no body on 304
            });
        }

        // Honor If-Modified-Since (basic: compare epoch millis rounded to seconds)
        final String ifModifiedSince = requestManager != null ? requestManager.getHeader("If-Modified-Since").orElse(null) : null;
        if (ifModifiedSince != null) {
            try {
                final long clientMtime =
                        java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.parse(ifModifiedSince, java.time.ZonedDateTime::from)
                                .toInstant()
                                .toEpochMilli();
                // truncate to seconds for comparison
                if ((mtime / 1000) <= (clientMtime / 1000)) {
                    return new StreamResponse(name).httpStatus(304).contentType(contentType).stream(out -> {
                        // no body on 304
                    });
                }
            } catch (final Exception ignore) {
                // malformed header — serve normally
            }
        }

        // X-Content-Type-Options: nosniff is added by Tomcat HttpHeaderSecurityFilter (web.xml).
        final StreamResponse resp = new StreamResponse(name).contentType(contentType)
                .header("Cache-Control", "public, max-age=86400")
                .header("Vary", "Accept-Encoding")
                .header("Referrer-Policy", "same-origin")
                .header("ETag", etag)
                .header("Last-Modified", lastModified)
                .header("Content-Length", String.valueOf(size));
        if (isSvg) {
            resp.header("Content-Security-Policy", SVG_CSP);
        }
        return resp.stream(out -> {
            try (InputStream in = Files.newInputStream(file)) {
                in.transferTo(out.stream());
            }
        });
    }

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
}
