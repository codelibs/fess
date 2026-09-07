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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/**
 * Downloads an artifact to a local file.
 *
 * <p>The body is streamed straight to disk rather than buffered: the OpenSearch bundle is
 * around 1 GB, and a buffering client would need that much heap, or a temporary file plus a
 * second copy, to deliver it.</p>
 */
public final class Downloader {

    private static final int BUFFER_SIZE = 1024 * 1024;

    private static final int CONNECT_TIMEOUT_SECONDS = 30;

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
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
            final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                if (response.statusCode() != 200) {
                    throw new SetupException("Failed to download " + uri + ": HTTP " + response.statusCode());
                }
                final long total = response.headers().firstValueAsLong("content-length").orElse(-1L);
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
        } catch (final IOException e) {
            throw new SetupException("Failed to download " + uri, e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SetupException("Interrupted while downloading " + uri, e);
        }
    }
}
