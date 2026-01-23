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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Job for exporting indexed search documents as HTML files to the filesystem.
 * Each document is exported as a single HTML file with URL structure mapped to directory structure.
 */
public class IndexExportJob {

    private static final Logger logger = LogManager.getLogger(IndexExportJob.class);

    private static final int MAX_PATH_COMPONENT_LENGTH = 200;

    private QueryBuilder queryBuilder;

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
     * Executes the export job, writing each matching document as an HTML file.
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

        final QueryBuilder query = queryBuilder != null ? queryBuilder : QueryBuilders.matchAllQuery();

        try {
            final long count = searchEngineClient.scrollSearch(fessConfig.getIndexDocumentSearchIndex(), requestBuilder -> {
                requestBuilder.setQuery(query).setSize(scrollSize);
                return true;
            }, source -> {
                exportDocument(source, exportPath, excludeFields);
                return true;
            });
            resultBuf.append("Exported ").append(count).append(" documents.");
        } catch (final Exception e) {
            logger.error("Failed to export documents.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();
    }

    /**
     * Exports a single document as an HTML file.
     *
     * @param source the document source map
     * @param exportPath the base export directory path
     * @param excludeFields the set of field names to exclude from output
     */
    protected void exportDocument(final Map<String, Object> source, final String exportPath, final Set<String> excludeFields) {
        final Object urlObj = source.get("url");
        if (urlObj == null) {
            logger.debug("Skipping document without url field.");
            return;
        }

        final String url = urlObj.toString();
        final Path filePath = buildFilePath(exportPath, url);
        final String html = buildHtml(source, excludeFields);

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, html, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            logger.warn("Failed to export document: url={}", url, e);
        }
    }

    /**
     * Builds a filesystem path from a document URL.
     *
     * @param exportPath the base export directory path
     * @param url the document URL
     * @return the target file path
     */
    protected Path buildFilePath(final String exportPath, final String url) {
        try {
            final URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();

            if (host == null || host.isEmpty()) {
                host = "_local";
            }

            if (path == null || path.isEmpty()) {
                path = "/index.html";
            } else if (path.endsWith("/")) {
                path = path + "index.html";
            } else if (!path.contains(".") || path.lastIndexOf('.') < path.lastIndexOf('/')) {
                path = path + ".html";
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
            return Paths.get(exportPath, "_invalid", hashString(url) + ".html");
        }
    }

    /**
     * Builds an HTML string from a document source map.
     *
     * @param source the document source map
     * @param excludeFields the set of field names to exclude from meta tags
     * @return the generated HTML string
     */
    protected String buildHtml(final Map<String, Object> source, final Set<String> excludeFields) {
        final String title = escapeHtml(getStringValue(source, "title"));
        final String content = escapeHtml(getStringValue(source, "content"));
        final String lang = escapeHtml(getStringValue(source, "lang"));

        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"").append(lang).append("\">\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>").append(title).append("</title>\n");

        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String field = entry.getKey();
            if ("title".equals(field) || "content".equals(field) || "lang".equals(field)) {
                continue;
            }
            if (excludeFields.contains(field)) {
                continue;
            }

            final Object value = entry.getValue();
            if (value instanceof Collection) {
                for (final Object item : (Collection<?>) value) {
                    html.append("<meta name=\"fess:")
                            .append(escapeHtml(field))
                            .append("\" content=\"")
                            .append(escapeHtml(String.valueOf(item)))
                            .append("\">\n");
                }
            } else if (value != null) {
                html.append("<meta name=\"fess:")
                        .append(escapeHtml(field))
                        .append("\" content=\"")
                        .append(escapeHtml(String.valueOf(value)))
                        .append("\">\n");
            }
        }

        html.append("</head>\n");
        html.append("<body>\n");
        html.append(content).append("\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String getStringValue(final Map<String, Object> source, final String key) {
        final Object value = source.get(key);
        return value != null ? value.toString() : "";
    }

    private String escapeHtml(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
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
