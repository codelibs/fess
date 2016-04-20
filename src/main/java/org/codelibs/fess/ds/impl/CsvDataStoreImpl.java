/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.exception.DataStoreCrawlingException;
import org.codelibs.fess.exception.DataStoreException;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvReader;

public class CsvDataStoreImpl extends AbstractDataStoreImpl {
    private static final Logger logger = LoggerFactory.getLogger(CsvDataStoreImpl.class);

    protected static final String ESCAPE_CHARACTER_PARAM = "escapeCharacter";

    protected static final String QUOTE_CHARACTER_PARAM = "quoteCharacter";

    protected static final String SEPARATOR_CHARACTER_PARAM = "separatorCharacter";

    protected static final String SKIP_LINES_PARAM = "skipLines";

    protected static final String IGNORE_LINE_PATTERNS_PARAM = "ignoreLinePatterns";

    protected static final String IGNORE_EMPTY_LINES_PARAM = "ignoreEmptyLines";

    protected static final String IGNORE_TRAILING_WHITESPACES_PARAM = "ignoreTrailingWhitespaces";

    protected static final String IGNORE_LEADING_WHITESPACES_PARAM = "ignoreLeadingWhitespaces";

    protected static final String NULL_STRING_PARAM = "nullString";

    protected static final String BREAK_STRING_PARAM = "breakString";

    protected static final String ESCAPE_DISABLED_PARAM = "escapeDisabled";

    protected static final String QUOTE_DISABLED_PARAM = "quoteDisabled";

    protected static final String CSV_FILE_ENCODING_PARAM = "fileEncoding";

    protected static final String CSV_FILES_PARAM = "files";

    protected static final String CSV_DIRS_PARAM = "directories";

    protected static final String CELL_PREFIX = "cell";

    public String[] csvFileSuffixs = new String[] { ".csv", ".tsv" };

    protected List<File> getCsvFileList(final Map<String, String> paramMap) {
        String value = paramMap.get(CSV_FILES_PARAM);
        final List<File> fileList = new ArrayList<File>();
        if (StringUtil.isBlank(value)) {
            value = paramMap.get(CSV_DIRS_PARAM);
            if (StringUtil.isBlank(value)) {
                throw new DataStoreException(CSV_FILES_PARAM + " and " + CSV_DIRS_PARAM + " are blank.");
            }
            logger.info(CSV_DIRS_PARAM + "=" + value);
            final String[] values = value.split(",");
            for (final String path : values) {
                final File dir = new File(path);
                if (dir.isDirectory()) {
                    StreamUtil.of(dir.listFiles()).filter(f -> isCsvFile(f.getParentFile(), f.getName()))
                            .sorted((f1, f2) -> (int) (f1.lastModified() - f2.lastModified())).forEach(f -> fileList.add(f));
                } else {
                    logger.warn(path + " is not a directory.");
                }
            }
        } else {
            logger.info(CSV_FILES_PARAM + "=" + value);
            final String[] values = value.split(",");
            for (final String path : values) {
                final File file = new File(path);
                if (file.isFile() && isCsvFile(file.getParentFile(), file.getName())) {
                    fileList.add(file);
                } else {
                    logger.warn(path + " is not found.");
                }
            }
        }
        if (fileList.isEmpty() && logger.isDebugEnabled()) {
            logger.debug("No csv files in " + value);
        }
        return fileList;
    }

    protected boolean isCsvFile(final File parentFile, final String filename) {
        final String name = filename.toLowerCase(Locale.ROOT);
        for (final String suffix : csvFileSuffixs) {
            if (name.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    protected String getCsvFileEncoding(final Map<String, String> paramMap) {
        final String value = paramMap.get(CSV_FILE_ENCODING_PARAM);
        if (StringUtil.isBlank(value)) {
            return Constants.UTF_8;
        }
        return value;
    }

    protected boolean hasHeaderLine(final Map<String, String> paramMap) {
        final String value = paramMap.get("hasHeaderLine");
        if (StringUtil.isBlank(value)) {
            return false;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        final long readInterval = getReadInterval(paramMap);

        final List<File> csvFileList = getCsvFileList(paramMap);
        if (csvFileList.isEmpty()) {
            logger.warn("No CSV file.");
            return;
        }

        final String csvFileEncoding = getCsvFileEncoding(paramMap);
        final boolean hasHeaderLine = hasHeaderLine(paramMap);
        final CsvConfig csvConfig = buildCsvConfig(paramMap);

        for (final File csvFile : csvFileList) {
            processCsv(dataConfig, callback, paramMap, scriptMap, defaultDataMap, csvConfig, csvFile, readInterval, csvFileEncoding,
                    hasHeaderLine);
        }
    }

    protected void processCsv(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap, final CsvConfig csvConfig, final File csvFile,
            final long readInterval, final String csvFileEncoding, final boolean hasHeaderLine) {
        logger.info("Loading " + csvFile.getAbsolutePath());
        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), csvFileEncoding)), csvConfig);
            List<String> headerList = null;
            if (hasHeaderLine) {
                headerList = csvReader.readValues();
            }
            List<String> list;
            boolean loop = true;
            while ((list = csvReader.readValues()) != null && loop && alive) {
                final Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.putAll(defaultDataMap);
                final Map<String, String> resultMap = new LinkedHashMap<String, String>();
                resultMap.putAll(paramMap);
                resultMap.put("csvfile", csvFile.getAbsolutePath());
                resultMap.put("csvfilename", csvFile.getName());
                boolean foundValues = false;
                for (int i = 0; i < list.size(); i++) {
                    String key = null;
                    String value = list.get(i);
                    if (value == null) {
                        value = StringUtil.EMPTY;
                    }
                    if (StringUtil.isNotBlank(value)) {
                        foundValues = true;
                    }
                    if (headerList != null && headerList.size() > i) {
                        key = headerList.get(i);
                        if (StringUtil.isNotBlank(key)) {
                            resultMap.put(key, value);
                        }
                    }
                    key = CELL_PREFIX + Integer.toString(i + 1);
                    resultMap.put(key, value);
                }
                if (!foundValues) {
                    logger.debug("No data in line: {}", resultMap);
                    continue;
                }

                if (logger.isDebugEnabled()) {
                    for (final Map.Entry<String, String> entry : resultMap.entrySet()) {
                        logger.debug(entry.getKey() + "=" + entry.getValue());
                    }
                }

                for (final Map.Entry<String, String> entry : scriptMap.entrySet()) {
                    final Object convertValue = convertValue(entry.getValue(), resultMap);
                    if (convertValue != null) {
                        dataMap.put(entry.getKey(), convertValue);
                    }
                }

                if (logger.isDebugEnabled()) {
                    for (final Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        logger.debug(entry.getKey() + "=" + entry.getValue());
                    }
                }

                try {
                    callback.store(paramMap, dataMap);
                } catch (final CrawlingAccessException e) {
                    logger.warn("Crawling Access Exception at : " + dataMap, e);

                    Throwable target = e;
                    if (target instanceof MultipleCrawlingAccessException) {
                        final Throwable[] causes = ((MultipleCrawlingAccessException) target).getCauses();
                        if (causes.length > 0) {
                            target = causes[causes.length - 1];
                        }
                    }

                    String errorName;
                    final Throwable cause = target.getCause();
                    if (cause != null) {
                        errorName = cause.getClass().getCanonicalName();
                    } else {
                        errorName = target.getClass().getCanonicalName();
                    }

                    String url;
                    if (target instanceof DataStoreCrawlingException) {
                        DataStoreCrawlingException dce = (DataStoreCrawlingException) target;
                        url = dce.getUrl();
                        if (dce.aborted()) {
                            loop = false;
                        }
                    } else {
                        url = csvFile.getAbsolutePath() + ":" + csvReader.getLineNumber();
                    }
                    final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
                    failureUrlService.store(dataConfig, errorName, url, target);
                } catch (final Throwable t) {
                    logger.warn("Crawling Access Exception at : " + dataMap, t);
                    final String url = csvFile.getAbsolutePath() + ":" + csvReader.getLineNumber();
                    final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
                    failureUrlService.store(dataConfig, t.getClass().getCanonicalName(), url, t);
                }

                if (readInterval > 0) {
                    sleep(readInterval);
                }
            }
        } catch (final Exception e) {
            throw new DataStoreException("Failed to crawl data when reading csv file.", e);
        } finally {
            IOUtils.closeQuietly(csvReader);
        }
    }

    protected CsvConfig buildCsvConfig(final Map<String, String> paramMap) {
        final CsvConfig csvConfig = new CsvConfig();

        if (paramMap.containsKey(SEPARATOR_CHARACTER_PARAM)) {
            final String value = paramMap.get(SEPARATOR_CHARACTER_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    csvConfig.setSeparator(StringEscapeUtils.unescapeJava(value).charAt(0));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + SEPARATOR_CHARACTER_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(QUOTE_CHARACTER_PARAM)) {
            final String value = paramMap.get(QUOTE_CHARACTER_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    csvConfig.setQuote(value.charAt(0));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + QUOTE_CHARACTER_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(ESCAPE_CHARACTER_PARAM)) {
            final String value = paramMap.get(ESCAPE_CHARACTER_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    csvConfig.setEscape(value.charAt(0));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + ESCAPE_CHARACTER_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(QUOTE_DISABLED_PARAM)) {
            final String value = paramMap.get(QUOTE_DISABLED_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // デフォルトでは無効となっている囲み文字を有効にします。
                    csvConfig.setQuoteDisabled(Boolean.parseBoolean(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + QUOTE_DISABLED_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(ESCAPE_DISABLED_PARAM)) {
            final String value = paramMap.get(ESCAPE_DISABLED_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // デフォルトでは無効となっているエスケープ文字を有効にします。
                    csvConfig.setEscapeDisabled(Boolean.parseBoolean(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + ESCAPE_DISABLED_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(BREAK_STRING_PARAM)) {
            final String value = paramMap.get(BREAK_STRING_PARAM);
            if (StringUtil.isNotBlank(value)) {
                // 項目値中の改行を \n で置換えます。
                csvConfig.setBreakString(value);
            }
        }

        if (paramMap.containsKey(NULL_STRING_PARAM)) {
            final String value = paramMap.get(NULL_STRING_PARAM);
            if (StringUtil.isNotBlank(value)) {
                // null 値扱いする文字列を指定します。
                csvConfig.setNullString(value);
            }
        }

        if (paramMap.containsKey(IGNORE_LEADING_WHITESPACES_PARAM)) {
            final String value = paramMap.get(IGNORE_LEADING_WHITESPACES_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // 項目値前のホワイトスペースを除去します。
                    csvConfig.setIgnoreLeadingWhitespaces(Boolean.parseBoolean(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + IGNORE_LEADING_WHITESPACES_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(IGNORE_TRAILING_WHITESPACES_PARAM)) {
            final String value = paramMap.get(IGNORE_TRAILING_WHITESPACES_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // 項目値後のホワイトスペースを除去します。
                    csvConfig.setIgnoreTrailingWhitespaces(Boolean.parseBoolean(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + IGNORE_TRAILING_WHITESPACES_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(IGNORE_EMPTY_LINES_PARAM)) {
            final String value = paramMap.get(IGNORE_EMPTY_LINES_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // 空行を無視するようにします。
                    csvConfig.setIgnoreEmptyLines(Boolean.parseBoolean(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + IGNORE_EMPTY_LINES_PARAM, e);
                }
            }
        }

        if (paramMap.containsKey(IGNORE_LINE_PATTERNS_PARAM)) {
            final String value = paramMap.get(IGNORE_LINE_PATTERNS_PARAM);
            if (StringUtil.isNotBlank(value)) {
                // 正規表現による無視する行パターンを設定します。(この例では # で始まる行)
                csvConfig.setIgnoreLinePatterns(Pattern.compile(value));
            }
        }

        if (paramMap.containsKey(SKIP_LINES_PARAM)) {
            final String value = paramMap.get(SKIP_LINES_PARAM);
            if (StringUtil.isNotBlank(value)) {
                try {
                    // 最初の1行目をスキップして読込みます。
                    csvConfig.setSkipLines(Integer.parseInt(value));
                } catch (final Exception e) {
                    logger.warn("Failed to load " + SKIP_LINES_PARAM, e);
                }
            }
        }

        return csvConfig;
    }
}
