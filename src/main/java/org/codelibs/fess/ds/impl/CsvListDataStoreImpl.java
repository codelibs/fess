/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.io.File;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.exception.DataStoreException;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orangesignal.csv.CsvConfig;

public class CsvListDataStoreImpl extends CsvDataStoreImpl {

    private static final Logger logger = LoggerFactory.getLogger(CsvListDataStoreImpl.class);

    public boolean deleteProcessedFile = true;

    public long csvFileTimestampMargin = 60 * 1000L;// 1min

    public boolean ignoreDataStoreException = true;

    @Override
    protected boolean isCsvFile(final File parentFile, final String filename) {
        if (super.isCsvFile(parentFile, filename)) {
            final File file = new File(parentFile, filename);
            final long now = System.currentTimeMillis();
            return now - file.lastModified() > csvFileTimestampMargin;
        }
        return false;
    }

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {
        int nThreads = 1;
        if (paramMap.containsKey(Constants.NUM_OF_THREADS)) {
            try {
                nThreads = Integer.parseInt(paramMap.get(Constants.NUM_OF_THREADS));
            } catch (final NumberFormatException e) {
                logger.warn(Constants.NUM_OF_THREADS + " is not int value.", e);
            }
        }
        final CrawlerClientFactory crawlerClientFactory = ComponentUtil.getCrawlerClientFactory();
        dataConfig.initializeClientFactory(crawlerClientFactory);
        try {
            final FileListIndexUpdateCallbackImpl fileListIndexUpdateCallback =
                    new FileListIndexUpdateCallbackImpl(callback, crawlerClientFactory, nThreads);
            super.storeData(dataConfig, fileListIndexUpdateCallback, paramMap, scriptMap, defaultDataMap);
            fileListIndexUpdateCallback.commit();
        } catch (final Exception e) {
            throw new DataStoreException(e);
        }
    }

    @Override
    protected void processCsv(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap, final CsvConfig csvConfig, final File csvFile,
            final long readInterval, final String csvFileEncoding, final boolean hasHeaderLine) {
        try {
            super.processCsv(dataConfig, callback, paramMap, scriptMap, defaultDataMap, csvConfig, csvFile, readInterval, csvFileEncoding,
                    hasHeaderLine);

            // delete csv file
            if (deleteProcessedFile && !csvFile.delete()) {
                logger.warn("Failed to delete {}", csvFile.getAbsolutePath());
            }
        } catch (final DataStoreException e) {
            if (ignoreDataStoreException) {
                logger.error("Failed to process " + csvFile.getAbsolutePath(), e);
                // rename csv file, or delete it if failed
                if (!csvFile.renameTo(new File(csvFile.getParent(), csvFile.getName() + ".txt")) && !csvFile.delete()) {
                    logger.warn("Failed to delete {}", csvFile.getAbsolutePath());
                }
            } else {
                throw e;
            }
        }
    }

}
