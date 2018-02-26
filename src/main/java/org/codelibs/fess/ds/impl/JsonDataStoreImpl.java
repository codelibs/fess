/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.ds.impl;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.exception.DataStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDataStoreImpl extends AbstractDataStoreImpl {
    private static final Logger logger = LoggerFactory.getLogger(JsonDataStoreImpl.class);

    private static final String FILE_ENCODING_PARAM = "fileEncoding";

    private static final String FILES_PARAM = "files";

    private static final String DIRS_PARAM = "directories";

    private String[] fileSuffixes = new String[] { ".json", ".jsonl" };

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {
        final String fileEncoding = getFileEncoding(paramMap);
        final List<File> fileList = getFileList(paramMap);

        if (fileList.isEmpty()) {
            logger.warn("No files to process");
            return;
        }

        for (final File file : fileList) {
            processFile(dataConfig, callback, paramMap, scriptMap, defaultDataMap, file, fileEncoding);
        }
    }

    private List<File> getFileList(final Map<String, String> paramMap) {
        String value = paramMap.get(FILES_PARAM);
        final List<File> fileList = new ArrayList<>();
        if (StringUtil.isBlank(value)) {
            value = paramMap.get(DIRS_PARAM);
            if (StringUtil.isBlank(value)) {
                throw new DataStoreException(FILES_PARAM + " and " + DIRS_PARAM + " are blank.");
            }
            logger.info(DIRS_PARAM + "=" + value);
            final String[] values = value.split(",");
            for (final String path : values) {
                final File dir = new File(path);
                if (dir.isDirectory()) {
                    stream(dir.listFiles()).of(
                            stream -> stream.filter(f -> isDesiredFile(f.getParentFile(), f.getName()))
                                    .sorted((f1, f2) -> (int) (f1.lastModified() - f2.lastModified())).forEach(fileList::add));
                } else {
                    logger.warn(path + " is not a directory.");
                }
            }
        } else {
            logger.info(FILES_PARAM + "=" + value);
            final String[] values = value.split(",");
            for (final String path : values) {
                final File file = new File(path);
                if (file.isFile() && isDesiredFile(file.getParentFile(), file.getName())) {
                    fileList.add(file);
                } else {
                    logger.warn(path + " is not found.");
                }
            }
        }
        if (fileList.isEmpty() && logger.isDebugEnabled()) {
            logger.debug("No files in " + value);
        }
        return fileList;
    }

    private boolean isDesiredFile(final File parentFile, final String filename) {
        final String name = filename.toLowerCase(Locale.ROOT);
        for (final String suffix : fileSuffixes) {
            if (name.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private String getFileEncoding(final Map<String, String> paramMap) {
        final String value = paramMap.get(FILE_ENCODING_PARAM);
        if (StringUtil.isBlank(value)) {
            return Constants.UTF_8;
        }
        return value;
    }

    private void processFile(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap, final File file, final String fileEncoding) {
        final ObjectMapper objectMapper = new ObjectMapper();

        logger.info("Loading " + file.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), fileEncoding))) {
            for (String line; (line = br.readLine()) != null;) {
                final Map<String, Object> source = objectMapper.readValue(line, new TypeReference<Map<String, Object>>() {
                });
                final Map<String, Object> resultMap = new LinkedHashMap<>();
                final Map<String, Object> dataMap = new HashMap<>(defaultDataMap);

                resultMap.putAll(paramMap);
                resultMap.putAll(source);

                for (final Map.Entry<String, String> entry : scriptMap.entrySet()) {
                    final Object convertValue = convertValue(entry.getValue(), resultMap);
                    if (convertValue != null) {
                        dataMap.put(entry.getKey(), convertValue);
                    }
                }

                callback.store(paramMap, dataMap);
            }
        } catch (final FileNotFoundException e) {
            logger.warn("Source file " + file + " does not exist.", e);
        } catch (final IOException e) {
            logger.warn("IO Error occurred while reading source file.", e);
        }
    }

    public void setFileSuffixes(String[] fileSuffixes) {
        this.fileSuffixes = fileSuffixes;
    }
}