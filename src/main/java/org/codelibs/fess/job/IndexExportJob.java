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
package org.codelibs.fess.job;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Job for exporting indexed search documents to the filesystem.
 * Each document is exported as a single file with URL structure mapped to directory structure.
 */
public class IndexExportJob {

    private static final Logger logger = LogManager.getLogger(IndexExportJob.class);

    private static final int MAX_PATH_COMPONENT_LENGTH = 200;

    private QueryBuilder queryBuilder;

    private IndexExportFormatter formatter;

    /**
     * Creates a new IndexExportJob instance.
     */
    public IndexExportJob() {
        // default constructor
    }

    /**
     * Sets the query to filter which documents to export.
     *
     * @param queryBuilder the query to use for filtering documents
     * @return this instance for method chaining
     */
    public IndexExportJob query(final QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        return this;
    }

    /**
     * Sets the export format.
     *
     * @param format the format name (e.g. "html", "json")
     * @return this instance for method chaining
     */
    public IndexExportJob format(final String format) {
        this.formatter = createFormatter(format);
        return this;
    }

    /**
     * Creates a formatter for the given format name.
     *
     * @param format the format name
     * @return the formatter instance
     * @throws IllegalArgumentException if the format is null, empty, or not supported
     */
    protected IndexExportFormatter createFormatter(final String format) {
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Export format must not be null or empty");
        }
        switch (format.trim().toLowerCase()) {
        case "html":
            return new HtmlIndexExportFormatter();
        case "json":
            return new JsonIndexExportFormatter();
        default:
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }

    /**
     * Executes the export job, writing each matching document as a file.
     *
     * @return a string containing the execution result or error messages
     */
    public String execute() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final StringBuilder resultBuf = new StringBuilder();

        final String exportPath = fessConfig.getIndexExportPath();
        final Set<String> excludeFields = Arrays.stream(fessConfig.getIndexExportExcludeFields().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        final int scrollSize = fessConfig.getIndexExportScrollSizeAsInteger();

        final IndexExportFormatter resolvedFormatter =
                this.formatter != null ? this.formatter : createFormatter(fessConfig.getIndexExportFormat());

        final QueryBuilder query = queryBuilder != null ? queryBuilder : QueryBuilders.matchAllQuery();

        if (logger.isInfoEnabled()) {
            logger.info("[EXPORT] Starting index export: path={}, scrollSize={}, excludeFields={}, query={}", exportPath, scrollSize,
                    excludeFields, query);
        }

        final long startTime = System.currentTimeMillis();

        try {
            final AtomicLong processedCount = new AtomicLong(0);
            final long count = searchEngineClient.scrollSearch(fessConfig.getIndexDocumentSearchIndex(), requestBuilder -> {
                requestBuilder.setQuery(query).setSize(scrollSize);
                return true;
            }, source -> {
                exportDocument(source, exportPath, excludeFields, resolvedFormatter);
                final long currentCount = processedCount.incrementAndGet();
                if (logger.isDebugEnabled() && currentCount % scrollSize == 0) {
                    logger.debug("[EXPORT] Processing: count={}", currentCount);
                }
                return true;
            });
            resultBuf.append("Exported ").append(count).append(" documents.");
            if (logger.isInfoEnabled()) {
                logger.info("[EXPORT] Completed: exportedCount={}, elapsedTime={}ms", count, System.currentTimeMillis() - startTime);
            }
        } catch (final Exception e) {
            logger.warn("Failed to export documents.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

    /**
     * Exports a single document as a file.
     *
     * @param source the document source map
     * @param exportPath the base export directory path
     * @param excludeFields the set of field names to exclude from output
     * @param formatter the formatter to use for output
     */
    protected void exportDocument(final Map<String, Object> source, final String exportPath, final Set<String> excludeFields,
            final IndexExportFormatter formatter) {
        final Object urlObj = source.get("url");
        if (urlObj == null) {
            logger.debug("Skipping document without url field.");
            return;
        }

        final String url = urlObj.toString();
        final Path filePath = buildFilePath(exportPath, url, formatter);
        if (logger.isDebugEnabled()) {
            logger.debug("[EXPORT] Exporting document: url={}, path={}", url, filePath);
        }
        final String content = formatter.format(source, excludeFields);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            logger.warn("Failed to export document: url={}", url, e);
        }
    }

    /**
     * Builds a filesystem path from a document URL.
     *
     * @param exportPath the base export directory path
     * @param url the document URL
     * @param formatter the formatter to determine file extensions
     * @return the target file path
     */
    protected Path buildFilePath(final String exportPath, final String url, final IndexExportFormatter formatter) {
        try {
            final URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();

            if (host == null || host.isEmpty()) {
                host = "_local";
            }

            if (path == null || path.isEmpty()) {
                path = "/" + formatter.getIndexFileName();
            } else if (path.endsWith("/")) {
                path = path + formatter.getIndexFileName();
            } else if (!path.contains(".") || path.lastIndexOf('.') < path.lastIndexOf('/')) {
                path = path + formatter.getFileExtension();
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            final String[] components = (host + "/" + path).split("/");
            final StringBuilder sanitized = new StringBuilder();
            for (int i = 0; i < components.length; i++) {
                if (i > 0) {
                    sanitized.append('/');
                }
                String component = components[i].replaceAll("[<>:\"|?*\\\\]", "_");
                if (component.length() > MAX_PATH_COMPONENT_LENGTH) {
                    component = component.substring(0, MAX_PATH_COMPONENT_LENGTH);
                }
                sanitized.append(component);
            }

            return Paths.get(exportPath, sanitized.toString());
        } catch (final Exception e) {
            logger.debug("Failed to parse URL: {}", url, e);
            return Paths.get(exportPath, "_invalid", hashString(url) + formatter.getFileExtension());
        }
    }

    private String hashString(final String input) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            final StringBuilder sb = new StringBuilder();
            for (final byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (final NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }
}
