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
package org.codelibs.fess.thumbnail.impl;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.exception.ThumbnailGenerationException;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.thumbnail.ThumbnailGenerator;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;

/**
 * Abstract base class for thumbnail generators.
 * Provides common functionality for thumbnail generation implementations.
 */
public abstract class BaseThumbnailGenerator implements ThumbnailGenerator {
    private static final Logger logger = LogManager.getLogger(BaseThumbnailGenerator.class);

    /** Map of conditions for thumbnail generation. */
    protected final Map<String, String> conditionMap = new HashMap<>();

    /** Length for directory name generation. */
    protected int directoryNameLength = 5;

    /** List of generator names. */
    protected List<String> generatorList;

    /** Map of file paths for thumbnail generation. */
    protected Map<String, String> filePathMap = new HashMap<>();

    /** The name of this thumbnail generator. */
    protected String name;

    /** Maximum number of redirects to follow. */
    protected int maxRedirectCount = 10;

    /** Availability status of this generator. */
    protected Boolean available = null;

    /**
     * Registers this thumbnail generator with the thumbnail manager.
     */
    public void register() {
        ComponentUtil.getThumbnailManager().add(this);
    }

    /**
     * Default constructor for BaseThumbnailGenerator.
     */
    public BaseThumbnailGenerator() {
        // Default constructor
    }

    /**
     * Adds a condition for thumbnail generation.
     * @param key The condition key.
     * @param regex The regex pattern for the condition.
     */
    public void addCondition(final String key, final String regex) {
        final String value = conditionMap.get(key);
        if (StringUtil.isBlank(value)) {
            conditionMap.put(key, regex);
        } else {
            conditionMap.put(key, value + "|" + regex);
        }
    }

    @Override
    public boolean isTarget(final Map<String, Object> docMap) {
        final String thumbnailFieldName = ComponentUtil.getFessConfig().getIndexFieldThumbnail();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] thumbnail: {}", name, docMap.get(thumbnailFieldName));
        }
        if (!docMap.containsKey(thumbnailFieldName)) {
            return false;
        }
        for (final Map.Entry<String, String> entry : conditionMap.entrySet()) {
            if (docMap.get(entry.getKey()) instanceof final String value && value.matches(entry.getValue())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[{}] match {}:{}", entry.getKey(), name, value);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable() {
        if (available != null) {
            return available;
        }
        if (generatorList != null && !generatorList.isEmpty()) {
            String path = System.getenv("PATH");
            if (path == null) {
                path = System.getenv("Path");
            }
            if (path == null) {
                path = System.getenv("path");
            }
            final List<String> pathList = new ArrayList<>();
            pathList.add("/usr/share/fess/bin");
            if (path != null) {
                stream(path.split(File.pathSeparator)).of(stream -> stream.map(String::trim).forEach(s -> pathList.add(s)));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("search paths: {}", pathList);
            }
            available = generatorList.stream().map(s -> {
                if (s.startsWith("${path}")) {
                    for (final String p : pathList) {
                        final File f = new File(s.replace("${path}", p));
                        if (f.exists()) {
                            final String filePath = f.getAbsolutePath();
                            filePathMap.put(s, filePath);
                            if (logger.isDebugEnabled()) {
                                logger.debug("generator path: {}", filePath);
                            }
                            return filePath;
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("generator path: {}", s);
                }
                return s;
            }).allMatch(s -> {
                final boolean found = new File(s).isFile();
                if (found && logger.isDebugEnabled()) {
                    logger.debug("{} is found.", s);
                }
                return found;
            });
        } else {
            available = true;
        }
        return available;
    }

    @Override
    public Tuple3<String, String, String> createTask(final String path, final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String thumbnailId = DocumentUtil.getValue(docMap, fessConfig.getIndexFieldId(), String.class);
        final Tuple3<String, String, String> task = new Tuple3<>(getName(), thumbnailId, path);
        if (logger.isDebugEnabled()) {
            logger.debug("Create thumbnail task: {}", task);
        }
        return task;
    }

    /**
     * Sets the directory name length for thumbnail storage.
     * @param directoryNameLength The directory name length.
     */
    public void setDirectoryNameLength(final int directoryNameLength) {
        this.directoryNameLength = directoryNameLength;
    }

    /**
     * Expands a file path using the file path mapping.
     * @param value The original path value.
     * @return The expanded path or the original value if no mapping exists.
     */
    protected String expandPath(final String value) {
        if (value != null && filePathMap.containsKey(value)) {
            return filePathMap.get(value);
        }
        return value;
    }

    /**
     * Updates the thumbnail field in the search index.
     * @param thumbnailId The thumbnail ID.
     * @param value The thumbnail value to update.
     */
    protected void updateThumbnailField(final String thumbnailId, final String value) {
        // TODO bulk
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            ComponentUtil.getIndexingHelper().updateDocument(ComponentUtil.getSearchEngineClient(), thumbnailId,
                    fessConfig.getIndexFieldThumbnail(), value);
        } catch (final Exception e) {
            logger.warn("Failed to update thumbnail field at {}", thumbnailId, e);
        }
    }

    /**
     * Processes thumbnail generation with a consumer function.
     * @param id The document ID.
     * @param consumer The consumer function to process thumbnail and config ID.
     * @return True if processing was successful, false otherwise.
     */
    protected boolean process(final String id, final BiPredicate<String, String> consumer) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        try {
            final Map<String, Object> doc = indexingHelper.getDocument(searchEngineClient, id,
                    new String[] { fessConfig.getIndexFieldThumbnail(), fessConfig.getIndexFieldConfigId() });
            if (doc == null) {
                throw new ThumbnailGenerationException("Document is not found: " + id);
            }
            final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldThumbnail(), String.class);
            if (StringUtil.isBlank(url)) {
                throw new ThumbnailGenerationException("Invalid thumbnail: " + url);
            }
            final String configId = DocumentUtil.getValue(doc, fessConfig.getIndexFieldConfigId(), String.class);
            if (configId == null || configId.length() < 2) {
                throw new ThumbnailGenerationException("Invalid configId: " + configId);
            }
            return consumer.test(configId, url);
        } catch (final ThumbnailGenerationException e) {
            if (e.getCause() == null) {
                logger.debug(e.getMessage());
            } else {
                logger.warn("Failed to process {}", id, e);
            }
        } catch (final Exception e) {
            logger.warn("Failed to process {}", id, e);
        }
        return false;
    }

    /**
     * Processes thumbnail generation with a response data consumer.
     * @param id The document ID.
     * @param consumer The consumer function to process response data.
     * @return True if processing was successful, false otherwise.
     */
    protected boolean process(final String id, final Predicate<ResponseData> consumer) {
        return process(id, (configId, url) -> {
            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            final CrawlingConfig config = crawlingConfigHelper.getCrawlingConfig(configId);
            if (config == null) {
                throw new ThumbnailGenerationException("No CrawlingConfig: " + configId);
            }

            if (logger.isInfoEnabled()) {
                logger.info("Generating Thumbnail: {}", url);
            }

            final CrawlerClientFactory crawlerClientFactory =
                    config.initializeClientFactory(() -> ComponentUtil.getComponent(CrawlerClientFactory.class));
            final CrawlerClient client = crawlerClientFactory.getClient(url);
            if (client == null) {
                throw new ThumbnailGenerationException("No CrawlerClient: " + configId + ", url: " + url);
            }
            String u = url;
            for (int i = 0; i < maxRedirectCount; i++) {
                try (final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(u).build())) {
                    if (StringUtil.isNotBlank(responseData.getRedirectLocation())) {
                        u = responseData.getRedirectLocation();
                        continue;
                    }
                    if (StringUtil.isBlank(responseData.getUrl())) {
                        throw new ThumbnailGenerationException(
                                "Failed to process a thumbnail content: " + url + " (Response URL is empty)");
                    }
                    return consumer.test(responseData);
                } catch (final CrawlingAccessException e) {
                    if (logger.isDebugEnabled()) {
                        throw new ThumbnailGenerationException("Failed to process a thumbnail content: " + url, e);
                    }
                    throw new ThumbnailGenerationException(e.getMessage());
                } catch (final Exception e) {
                    throw new ThumbnailGenerationException("Failed to process a thumbnail content: " + url, e);
                }
            }
            throw new ThumbnailGenerationException("Failed to process a thumbnail content: " + url + " (Redirect Loop)");
        });
    }

    /**
     * Sets the list of generator names.
     * @param generatorList The list of generator names.
     */
    public void setGeneratorList(final List<String> generatorList) {
        this.generatorList = generatorList;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this thumbnail generator.
     * @param name The generator name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the maximum number of redirects to follow.
     * @param maxRedirectCount The maximum redirect count.
     */
    public void setMaxRedirectCount(final int maxRedirectCount) {
        this.maxRedirectCount = maxRedirectCount;
    }

}