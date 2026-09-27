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
package org.codelibs.fess.setup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Downloads an artifact to a local file.
 *
 * <p>The body is streamed straight to disk rather than buffered: the OpenSearch bundle is
 * around 1 GB, and a buffering client would need that much heap, or a temporary file plus a
 * second copy, to deliver it.</p>
 *
 * <p>A {@code file:} URI is read from the local file system instead, so that a copy of a Maven
 * repository brought onto a machine with no route to the Internet can serve as the repository.
 * It behaves like the HTTP one: a missing file is what a 404 is, and a directory reads as the
 * index of its entries that a web server would list for it. Any other scheme is refused with a
 * {@link SetupException} rather than left to {@code HttpClient}, which throws an unchecked
 * exception for it.</p>
 */
public final class Downloader {

    private static final int BUFFER_SIZE = 1024 * 1024;

    private static final int CONNECT_TIMEOUT_SECONDS = 30;

    private static final int SHA1_HEX_LENGTH = 40;

    private static final int HTTP_NOT_FOUND = 404;

    private static final String FILE_SCHEME = "file";

    private static final List<String> HTTP_SCHEMES = List.of("http", "https");

    /** Receives download progress. */
    @FunctionalInterface
    public interface Progress {
        /**
         * Reports progress.
         *
         * @param bytes bytes written so far
         * @param total the total size, or -1 when the server sent no content length
         */
        void report(long bytes, long total);
    }

    private Downloader() {
    }

    /**
     * Returns the SHA-1 of a file, as lower-case hexadecimal.
     *
     * <p>SHA-1 because that is the digest these Maven repositories publish beside an artifact;
     * it is a transport check against a truncated or swapped file, not a signature.</p>
     *
     * @param file the file to hash
     * @return the digest
     * @throws SetupException if the file cannot be read
     */
    public static String sha1(final Path file) throws SetupException {
        try (InputStream in = Files.newInputStream(file)) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            final byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
            final StringBuilder hex = new StringBuilder();
            for (final byte b : digest.digest()) {
                hex.append(Character.forDigit(b >> 4 & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
            }
            return hex.toString();
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + file, e);
        } catch (final NoSuchAlgorithmException e) {
            throw new SetupException("SHA-1 is not available in this JVM", e);
        }
    }

    /**
     * Checks a downloaded file against a published SHA-1.
     *
     * <p>The published form is the bare digest, but a {@code sha1sum} line carries the file name
     * after it, so only the first token is read.</p>
     *
     * @param file the downloaded file
     * @param published the body of the {@code .sha1} file
     * @throws SetupException if the digests differ or {@code published} holds no digest
     */
    public static void verifySha1(final Path file, final String published) throws SetupException {
        final String expected = published == null ? "" : published.trim().split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        if (expected.length() != SHA1_HEX_LENGTH) {
            throw new SetupException("The checksum published for " + file.getFileName() + " is not a SHA-1: " + published);
        }
        final String actual = sha1(file);
        if (!expected.equals(actual)) {
            throw new SetupException(
                    "Checksum mismatch for " + file.getFileName() + ": expected " + expected + " but the download is " + actual);
        }
    }

    /**
     * Reads {@code uri} into a string.
     *
     * <p>For the small documents that describe what to download -- a {@code maven-metadata.xml},
     * a repository's directory index -- rather than the artifacts themselves.</p>
     *
     * @param uri the source
     * @return the body, decoded as UTF-8
     * @throws SetupException if the request fails or the server does not answer 200
     */
    public static String readString(final URI uri) throws SetupException {
        return read(uri, false);
    }

    /**
     * Reads {@code uri} into a string, treating a missing document as an answer rather than a
     * failure.
     *
     * <p>For a document whose absence is a normal state -- a checksum a repository does not
     * publish -- and only that. Every other status still fails, so that a proxy error or a
     * server fault cannot be mistaken for "there is nothing there".</p>
     *
     * @param uri the source
     * @return the body, or {@code null} when the server answered 404
     * @throws SetupException if the request fails for any other reason
     */
    public static String readStringIfPresent(final URI uri) throws SetupException {
        return read(uri, true);
    }

    private static String read(final URI uri, final boolean absentIsNull) throws SetupException {
        final Path local = localFile(uri);
        if (local != null) {
            return readLocal(uri, local, absentIsNull);
        }
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (absentIsNull && response.statusCode() == HTTP_NOT_FOUND) {
                return null;
            }
            if (response.statusCode() != 200) {
                throw new SetupException("Failed to read " + uri + ": HTTP " + response.statusCode());
            }
            return response.body();
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + uri, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SetupException("Interrupted while reading " + uri, e);
        }
    }

    /**
     * Downloads {@code uri} to {@code dest} without reporting progress.
     *
     * @param uri the source
     * @param dest the destination file
     * @return {@code dest}
     * @throws SetupException if the download fails
     */
    public static Path download(final URI uri, final Path dest) throws SetupException {
        return download(uri, dest, (bytes, total) -> {
            // progress is not reported
        });
    }

    /**
     * Downloads {@code uri} to {@code dest}.
     *
     * <p>A destination file whose size already matches the server's content length is left
     * alone, so re-running setup does not re-fetch a gigabyte. The body is written to a
     * sibling {@code .part} file and renamed on completion, so an interrupted download never
     * leaves a truncated artifact that the size check would then accept.</p>
     *
     * @param uri the source
     * @param dest the destination file
     * @param progress receives progress updates
     * @return {@code dest}
     * @throws SetupException if the download fails
     */
    public static Path download(final URI uri, final Path dest, final Progress progress) throws SetupException {
        return fetch(uri, dest, progress, false);
    }

    /**
     * Downloads {@code uri} to {@code dest}, treating a missing file as an answer rather than a
     * failure.
     *
     * <p>For a source that is tried before another one: a plugin release that is not on GitHub
     * has to fall through to the Maven repository, and only an error that is not "no such file"
     * should stop the install.</p>
     *
     * @param uri the source
     * @param dest the destination file
     * @param progress receives progress updates
     * @return {@code dest}, or {@code null} when the server answered 404
     * @throws SetupException if the download fails for any other reason
     */
    public static Path downloadIfPresent(final URI uri, final Path dest, final Progress progress) throws SetupException {
        return fetch(uri, dest, progress, true);
    }

    private static Path fetch(final URI uri, final Path dest, final Progress progress, final boolean absentIsNull) throws SetupException {
        final Path local = localFile(uri);
        if (local != null) {
            return fetchLocal(uri, local, dest, progress, absentIsNull);
        }
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                if (absentIsNull && response.statusCode() == HTTP_NOT_FOUND) {
                    return null;
                }
                if (response.statusCode() != 200) {
                    throw new SetupException("Failed to download " + uri + ": HTTP " + response.statusCode());
                }
                return store(in, response.headers().firstValueAsLong("content-length").orElse(-1L), dest, progress);
            }
        } catch (final IOException e) {
            throw new SetupException("Failed to download " + uri, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SetupException("Interrupted while downloading " + uri, e);
        }
    }

    /**
     * Writes a body to {@code dest}, through a {@code .part} file, unless {@code dest} already
     * has its size.
     *
     * @param in the body
     * @param total the body's size, or -1 when it is not known
     * @param dest the destination file
     * @param progress receives progress updates
     * @return {@code dest}
     * @throws IOException if the body cannot be read or the file cannot be written
     */
    private static Path store(final InputStream in, final long total, final Path dest, final Progress progress) throws IOException {
        if (Files.exists(dest) && total >= 0 && Files.size(dest) == total) {
            progress.report(total, total);
            return dest;
        }
        final Path parent = dest.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        final Path part = dest.resolveSibling(dest.getFileName() + ".part");
        long written = 0L;
        try (OutputStream out = Files.newOutputStream(part)) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) >= 0) {
                out.write(buffer, 0, read);
                written += read;
                progress.report(written, total);
            }
        }
        Files.move(part, dest, StandardCopyOption.REPLACE_EXISTING);
        return dest;
    }

    /**
     * Returns the local file a {@code file:} URI names.
     *
     * @param uri the source
     * @return the file, or {@code null} when the URI is an HTTP one
     * @throws SetupException if the scheme is neither HTTP nor {@code file}, or the {@code file:}
     *             URI does not name an absolute local path
     */
    private static Path localFile(final URI uri) throws SetupException {
        final String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
        if (HTTP_SCHEMES.contains(scheme)) {
            return null;
        }
        if (!FILE_SCHEME.equals(scheme)) {
            throw new SetupException("Cannot read " + uri + ": only http, https and file URLs are supported");
        }
        try {
            return Path.of(uri);
        } catch (final IllegalArgumentException | FileSystemNotFoundException e) {
            throw new SetupException("Cannot read " + uri + ": a file URL names an absolute path, such as file:///opt/repository/", e);
        }
    }

    private static String readLocal(final URI uri, final Path file, final boolean absentIsNull) throws SetupException {
        try {
            if (Files.isDirectory(file)) {
                return listing(file);
            }
            if (!Files.isRegularFile(file)) {
                if (absentIsNull) {
                    return null;
                }
                throw new SetupException("Failed to read " + uri + ": no such file");
            }
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new SetupException("Failed to read " + uri, e);
        }
    }

    /**
     * Renders a directory the way a web server's directory index lists it, one link per entry
     * and a trailing slash on a directory, which is what a plugin listing is read from.
     *
     * @param directory the directory
     * @return the index
     * @throws IOException if the directory cannot be read
     */
    private static String listing(final Path directory) throws IOException {
        final StringBuilder html = new StringBuilder();
        try (Stream<Path> entries = Files.list(directory)) {
            for (final Path entry : entries.sorted().toList()) {
                final String name = entry.getFileName() + (Files.isDirectory(entry) ? "/" : "");
                html.append("<a href=\"").append(name).append("\">").append(name).append("</a>\n");
            }
        }
        return html.toString();
    }

    private static Path fetchLocal(final URI uri, final Path file, final Path dest, final Progress progress, final boolean absentIsNull)
            throws SetupException {
        if (!Files.isRegularFile(file)) {
            if (absentIsNull) {
                return null;
            }
            throw new SetupException("Failed to download " + uri + ": no such file");
        }
        try (InputStream in = Files.newInputStream(file)) {
            return store(in, Files.size(file), dest, progress);
        } catch (final IOException e) {
            throw new SetupException("Failed to download " + uri, e);
        }
    }
}
