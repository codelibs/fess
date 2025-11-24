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
package org.codelibs.fess.dict;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.FileUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
import org.dbflute.optional.OptionalEntity;

import jakarta.annotation.PostConstruct;

/**
 * Manager class for handling dictionary files in the Fess search system.
 * This class provides functionality to retrieve, store, and manage various
 * dictionary files such as synonyms, kuromoji, protwords, and stopwords.
 * It coordinates with DictionaryCreator instances to handle different
 * dictionary types and manages file synchronization through ConfigSync.
 *
 */
public class DictionaryManager {
    private static final Logger logger = LogManager.getLogger(DictionaryManager.class);

    /** List of dictionary creators for handling different dictionary types */
    protected List<DictionaryCreator> creatorList = new ArrayList<>();

    /**
     * Default constructor for DictionaryManager.
     * Creates a new dictionary manager with an empty creator list.
     */
    public DictionaryManager() {
        // Default constructor
    }

    /**
     * Initializes the dictionary manager after construction.
     * Sets up the relationship between this manager and all registered creators.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        creatorList.forEach(creator -> {
            creator.setDictionaryManager(this);
        });
    }

    /**
     * Retrieves all available dictionary files from the ConfigSync storage.
     * This method queries the ConfigSync API to get file information and
     * uses registered DictionaryCreator instances to create appropriate
     * DictionaryFile objects.
     *
     * @return an array of dictionary files available in the system
     * @throws DictionaryException if there's an error accessing the dictionaries
     */
    public DictionaryFile<? extends DictionaryItem>[] getDictionaryFiles() {
        try (CurlResponse response = ComponentUtil.getCurlHelper()
                .get("/_configsync/file")
                .param("fields", "path,@timestamp")
                .param("size", ComponentUtil.getFessConfig().getPageDictionaryMaxFetchSize())
                .execute()) {
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> fileList = (List<Map<String, Object>>) contentMap.get("file");
            return fileList.stream().map(fileMap -> {
                try {
                    final String path = fileMap.get("path").toString();
                    final Date timestamp =
                            new SimpleDateFormat(Constants.DATE_FORMAT_ISO_8601_EXTEND_UTC).parse(fileMap.get("@timestamp").toString());
                    for (final DictionaryCreator creator : creatorList) {
                        final DictionaryFile<? extends DictionaryItem> file = creator.create(path, timestamp);
                        if (file != null) {
                            return file;
                        }
                    }
                } catch (final Exception e) {
                    final String filePath = fileMap.get("path") != null ? fileMap.get("path").toString() : "unknown";
                    final String fileTimestamp = fileMap.get("@timestamp") != null ? fileMap.get("@timestamp").toString() : "unknown";
                    logger.warn("Failed to load dictionary file: path={}, timestamp={}, error={}", filePath, fileTimestamp, e.getMessage(),
                            e);
                }
                return null;
            }).filter(file -> file != null).toArray(n -> new DictionaryFile<?>[n]);
        } catch (final IOException e) {
            throw new DictionaryException("Failed to access dictionaries", e);
        }
    }

    /**
     * Retrieves a specific dictionary file by its ID.
     *
     * @param id the unique identifier of the dictionary file to retrieve
     * @return an OptionalEntity containing the dictionary file if found, empty otherwise
     */
    public OptionalEntity<DictionaryFile<? extends DictionaryItem>> getDictionaryFile(final String id) {
        for (final DictionaryFile<? extends DictionaryItem> dictFile : getDictionaryFiles()) {
            if (dictFile.getId().equals(id)) {
                return OptionalEntity.of(dictFile);
            }
        }
        return OptionalEntity.empty();
    }

    /**
     * Stores or updates a dictionary file in the ConfigSync storage.
     * This method checks for concurrent modifications by comparing timestamps
     * and uploads the file content to the ConfigSync API.
     *
     * @param dictFile the dictionary file metadata to store
     * @param file the actual file containing the dictionary content
     * @throws DictionaryException if the file was updated by another process,
     *         if the file doesn't exist, or if there's an error during storage
     */
    public void store(final DictionaryFile<? extends DictionaryItem> dictFile, final File file) {
        getDictionaryFile(dictFile.getId()).ifPresent(currentFile -> {
            if (currentFile.getTimestamp().getTime() > dictFile.getTimestamp().getTime()) {
                throw new DictionaryException(dictFile.getPath() + " was updated.");
            }

            // TODO use stream
            try (CurlResponse response = ComponentUtil.getCurlHelper()
                    .post("/_configsync/file")
                    .param("path", dictFile.getPath())
                    .body(FileUtil.readUTF8(file))
                    .execute()) {
                final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
                if (!Constants.TRUE.equalsIgnoreCase(contentMap.get("acknowledged").toString())) {
                    throw new DictionaryException("Failed to update " + dictFile.getPath());
                }
            } catch (final IOException e) {
                throw new DictionaryException("Failed to update " + dictFile.getPath(), e);
            }

        }).orElse(() -> {
            throw new DictionaryException(dictFile.getPath() + " does not exist.");
        });
    }

    /**
     * Gets the HTTP response containing the content of a dictionary file.
     * This method retrieves the raw file content from ConfigSync storage.
     *
     * @param dictFile the dictionary file to retrieve content for
     * @return a CurlResponse containing the file content
     */
    public CurlResponse getContentResponse(final DictionaryFile<? extends DictionaryItem> dictFile) {
        return ComponentUtil.getCurlHelper().get("/_configsync/file").param("path", dictFile.getPath()).execute();
    }

    /**
     * Adds a new dictionary creator to this manager.
     * Dictionary creators are responsible for creating specific types
     * of dictionary files based on file paths and timestamps.
     *
     * @param creator the dictionary creator to add
     */
    public void addCreator(final DictionaryCreator creator) {
        creatorList.add(creator);
    }

}
