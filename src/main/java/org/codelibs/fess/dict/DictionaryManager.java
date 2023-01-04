/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.FileUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
import org.dbflute.optional.OptionalEntity;

public class DictionaryManager {
    private static final Logger logger = LogManager.getLogger(DictionaryManager.class);

    protected List<DictionaryCreator> creatorList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        creatorList.forEach(creator -> {
            creator.setDictionaryManager(this);
        });
    }

    public DictionaryFile<? extends DictionaryItem>[] getDictionaryFiles() {
        try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_configsync/file").param("fields", "path,@timestamp")
                .param("size", ComponentUtil.getFessConfig().getPageDictionaryMaxFetchSize()).execute()) {
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
                    logger.warn("Failed to load {}", fileMap, e);
                }
                return null;
            }).filter(file -> file != null).toArray(n -> new DictionaryFile<?>[n]);
        } catch (final IOException e) {
            throw new DictionaryException("Failed to access dictionaries", e);
        }
    }

    public OptionalEntity<DictionaryFile<? extends DictionaryItem>> getDictionaryFile(final String id) {
        for (final DictionaryFile<? extends DictionaryItem> dictFile : getDictionaryFiles()) {
            if (dictFile.getId().equals(id)) {
                return OptionalEntity.of(dictFile);
            }
        }
        return OptionalEntity.empty();
    }

    public void store(final DictionaryFile<? extends DictionaryItem> dictFile, final File file) {
        getDictionaryFile(dictFile.getId()).ifPresent(currentFile -> {
            if (currentFile.getTimestamp().getTime() > dictFile.getTimestamp().getTime()) {
                throw new DictionaryException(dictFile.getPath() + " was updated.");
            }

            // TODO use stream
            try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_configsync/file").param("path", dictFile.getPath())
                    .body(FileUtil.readUTF8(file)).execute()) {
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

    public CurlResponse getContentResponse(final DictionaryFile<? extends DictionaryItem> dictFile) {
        return ComponentUtil.getCurlHelper().get("/_configsync/file").param("path", dictFile.getPath()).execute();
    }

    public void addCreator(final DictionaryCreator creator) {
        creatorList.add(creator);
    }

}
