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
package org.codelibs.fess.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ReaderUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.serializer.DataSerializer;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.crawler.util.TextUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.exception.ComponentNotFoundException;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for document processing and manipulation in the Fess search system.
 * This class provides utilities for processing document content, titles, and digests,
 * handling text normalization, content extraction, and similar document hash encoding/decoding.
 * It also manages document processing requests and integrates with the crawler system.
 *
 */
public class DocumentHelper {
    private static final Logger logger = LogManager.getLogger(DocumentHelper.class);

    /** Prefix used for encoded similar document hashes */
    protected static final String SIMILAR_DOC_HASH_PREFIX = "$";

    /**
     * Default constructor for DocumentHelper.
     * Creates a new document helper instance.
     */
    public DocumentHelper() {
        // Default constructor
    }

    /**
     * Initializes the document helper after construction.
     * Sets up the TikaExtractor with configuration parameters for text processing.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        try {
            final TikaExtractor tikaExtractor = ComponentUtil.getComponent("tikaExtractor");
            if (tikaExtractor != null) {
                tikaExtractor.setMaxAlphanumTermSize(getMaxAlphanumTermSize());
                tikaExtractor.setMaxSymbolTermSize(getMaxSymbolTermSize());
                tikaExtractor.setReplaceDuplication(isDuplicateTermRemoved());
                tikaExtractor.setSpaceChars(getSpaceChars());
            }
        } catch (final ComponentNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("tikaExtractor is not found: {}", e.getMessage().replace('\n', ' '));
            }
        } catch (final Exception e) {
            logger.warn("Failed to initiaize TikaExtractor.", e);
        }
    }

    /**
     * Processes and normalizes a document title.
     * Applies text normalization using configured space characters and returns
     * a clean title suitable for indexing.
     *
     * @param responseData the response data from crawling (not currently used)
     * @param title the raw title text to process
     * @param dataMap additional data map (not currently used)
     * @return the normalized title, or empty string if title is null
     */
    public String getTitle(final ResponseData responseData, final String title, final Map<String, Object> dataMap) {
        if (title == null) {
            return StringUtil.EMPTY; // empty
        }

        final int[] spaceChars = getSpaceChars();
        try (final Reader reader = new StringReader(title)) {
            return TextUtil.normalizeText(reader).initialCapacity(title.length()).spaceChars(spaceChars).execute();
        } catch (final IOException e) {
            return StringUtil.EMPTY; // empty
        }
    }

    /**
     * Processes and normalizes document content.
     * Applies text normalization including duplicate term removal, size limits,
     * and space character handling. May preserve original content based on configuration.
     *
     * @param crawlingConfig the crawling configuration containing processing parameters
     * @param responseData the response data from crawling
     * @param content the raw content text to process
     * @param dataMap additional data map
     * @return the normalized content, or empty string if content is null
     */
    public String getContent(final CrawlingConfig crawlingConfig, final ResponseData responseData, final String content,
            final Map<String, Object> dataMap) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        if (crawlingConfig != null) {
            final Map<String, String> configParam = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
            if (configParam != null && Constants.TRUE.equalsIgnoreCase(configParam.get(Param.Config.KEEP_ORIGINAL_BODY))) {
                return content;
            }
        }

        if (responseData.getMetaDataMap().get(Extractor.class.getSimpleName()) instanceof TikaExtractor) {
            return content;
        }

        final int maxAlphanumTermSize = getMaxAlphanumTermSize();
        final int maxSymbolTermSize = getMaxSymbolTermSize();
        final boolean duplicateTermRemoved = isDuplicateTermRemoved();
        final int[] spaceChars = getSpaceChars();
        try (final Reader reader = new StringReader(content)) {
            return TextUtil.normalizeText(reader)
                    .initialCapacity(content.length())
                    .maxAlphanumTermSize(maxAlphanumTermSize)
                    .maxSymbolTermSize(maxSymbolTermSize)
                    .duplicateTermRemoved(duplicateTermRemoved)
                    .spaceChars(spaceChars)
                    .execute();
        } catch (final IOException e) {
            return StringUtil.EMPTY; // empty
        }
    }

    /**
     * Gets the maximum size for alphanumeric terms from configuration.
     *
     * @return the maximum alphanumeric term size
     */
    protected int getMaxAlphanumTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxAlphanumTermSizeAsInteger();
    }

    /**
     * Gets the maximum size for symbol terms from configuration.
     *
     * @return the maximum symbol term size
     */
    protected int getMaxSymbolTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxSymbolTermSizeAsInteger();
    }

    /**
     * Checks if duplicate term removal is enabled in configuration.
     *
     * @return true if duplicate terms should be removed, false otherwise
     */
    protected boolean isDuplicateTermRemoved() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.isCrawlerDocumentDuplicateTermRemoved();
    }

    /**
     * Gets the array of space character codes from configuration.
     *
     * @return array of character codes to be treated as spaces
     */
    protected int[] getSpaceChars() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentSpaceCharsAsArray();
    }

    /**
     * Creates a digest (abbreviated summary) of document content.
     * Truncates and normalizes content to create a summary suitable for display.
     *
     * @param responseData the response data from crawling (not currently used)
     * @param content the content to create a digest from
     * @param dataMap additional data map (not currently used)
     * @param maxWidth the maximum width of the digest
     * @return the abbreviated and normalized digest, or empty string if content is null
     */
    public String getDigest(final ResponseData responseData, final String content, final Map<String, Object> dataMap, final int maxWidth) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        String subContent;
        if (content.length() < maxWidth * 2) {
            subContent = content;
        } else {
            subContent = content.substring(0, maxWidth * 2);
        }

        final int[] spaceChars = getSpaceChars();
        try (final Reader reader = new StringReader(subContent)) {
            final String originalStr = TextUtil.normalizeText(reader).initialCapacity(content.length()).spaceChars(spaceChars).execute();
            return StringUtils.abbreviate(originalStr, maxWidth);
        } catch (final IOException e) {
            return StringUtil.EMPTY; // empty
        }
    }

    /**
     * Processes a crawling request for a specific URL.
     * Executes the full crawling pipeline including client execution, rule processing,
     * transformation, and data extraction.
     *
     * @param crawlingConfig the crawling configuration to use
     * @param crawlingInfoId the crawling session ID
     * @param url the URL to process
     * @return a map containing the processed document data
     * @throws CrawlingAccessException if crawling fails or configuration is invalid
     * @throws ChildUrlsException if the URL redirects to another location
     * @throws CrawlerSystemException if data deserialization fails
     */
    public Map<String, Object> processRequest(final CrawlingConfig crawlingConfig, final String crawlingInfoId, final String url) {
        if (StringUtil.isBlank(crawlingInfoId)) {
            throw new CrawlingAccessException("sessionId is null. Cannot access document without a valid session ID.");
        }

        final CrawlerClientFactory crawlerClientFactory = crawlingConfig.initializeClientFactory(ComponentUtil::getCrawlerClientFactory);
        final CrawlerClient client = crawlerClientFactory.getClient(url);
        if (client == null) {
            throw new CrawlingAccessException("CrawlerClient is null for URL: " + url + ". Unable to access the document without a crawler client.");
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();
        try (final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build())) {
            if (responseData.getRedirectLocation() != null) {
                final Set<RequestData> childUrlList = new HashSet<>();
                childUrlList.add(RequestDataBuilder.newRequestData().get().url(responseData.getRedirectLocation()).build());
                throw new ChildUrlsException(childUrlList, this.getClass().getName() + "#RedirectedFrom:" + url);
            }
            responseData.setExecutionTime(systemHelper.getCurrentTimeAsLong() - startTime);
            responseData.setSessionId(crawlingInfoId);

            final RuleManager ruleManager = SingletonLaContainer.getComponent(RuleManager.class);
            final Rule rule = ruleManager.getRule(responseData);
            if (rule == null) {
                throw new CrawlingAccessException("No url rule for " + url);
            }
            responseData.setRuleId(rule.getRuleId());
            final ResponseProcessor responseProcessor = rule.getResponseProcessor();
            if (!(responseProcessor instanceof DefaultResponseProcessor)) {
                throw new CrawlingAccessException("The response processor is not DefaultResponseProcessor. responseProcessor: "
                        + responseProcessor + ", url: " + url);
            }
            final Transformer transformer = ((DefaultResponseProcessor) responseProcessor).getTransformer();
            final ResultData resultData = transformer.transform(responseData);
            final Object rawData = resultData.getRawData();
            if (rawData != null) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> responseDataMap = (Map<String, Object>) rawData;
                return responseDataMap;
            } else {
                final byte[] data = resultData.getData();
                if (data != null) {
                    try {
                        final DataSerializer dataSerializer = ComponentUtil.getComponent("dataSerializer");
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> responseDataMap = (Map<String, Object>) dataSerializer.fromBinaryToObject(data);
                        return responseDataMap;
                    } catch (final Exception e) {
                        throw new CrawlerSystemException("Could not create an instance from bytes.", e);
                    }
                }
            }
            return null;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to parse " + url, e);
        }
    }

    /**
     * Decodes a similar document hash from its compressed and encoded form.
     * Reverses the encoding process applied by encodeSimilarDocHash.
     *
     * @param hash the encoded hash string to decode
     * @return the decoded hash string, or the original hash if decoding fails
     */
    public String decodeSimilarDocHash(final String hash) {
        if (hash != null && hash.startsWith(SIMILAR_DOC_HASH_PREFIX) && hash.length() > SIMILAR_DOC_HASH_PREFIX.length()) {
            final byte[] decode = Base64.getUrlDecoder().decode(hash.substring(SIMILAR_DOC_HASH_PREFIX.length()));
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(decode)), Constants.UTF_8))) {
                return ReaderUtil.readText(reader);
            } catch (final IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to decode {}", hash, e);
                }
            }
        }
        return hash;
    }

    /**
     * Encodes a similar document hash using GZIP compression and Base64 encoding.
     * This reduces storage space for hash values while maintaining uniqueness.
     *
     * @param hash the hash string to encode
     * @return the encoded hash string with prefix, or the original hash if encoding fails
     */
    public String encodeSimilarDocHash(final String hash) {
        if (hash != null && !hash.startsWith(SIMILAR_DOC_HASH_PREFIX)) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
                    gos.write(hash.getBytes(Constants.UTF_8));
                }
                return SIMILAR_DOC_HASH_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(baos.toByteArray());
            } catch (final IOException e) {
                logger.warn("Failed to encode {}", hash, e);
            }
        }
        return hash;
    }

    /**
     * Appends line numbers to each line of content with a given prefix.
     * Useful for debugging and displaying content with line references.
     *
     * @param prefix the prefix to add before each line number
     * @param content the content to add line numbers to
     * @return the content with line numbers prepended, or empty string if content is blank
     */
    public String appendLineNumber(final String prefix, final String content) {
        if (StringUtil.isBlank(content)) {
            return StringUtil.EMPTY;
        }
        final String[] values = content.split("\n");
        final StringBuilder buf = new StringBuilder((int) (content.length() * 1.3));
        buf.append(prefix).append(1).append(':').append(values[0]);
        for (int i = 1; i < values.length; i++) {
            buf.append('\n').append(prefix).append(i + 1).append(':').append(values[i]);
        }
        return buf.toString();
    }
}
