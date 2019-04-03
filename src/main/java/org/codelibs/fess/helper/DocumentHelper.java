/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import org.codelibs.core.io.ReaderUtil;
import org.codelibs.core.io.SerializeUtil;
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
import org.codelibs.fess.crawler.processor.ResponseProcessor;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.crawler.rule.Rule;
import org.codelibs.fess.crawler.rule.RuleManager;
import org.codelibs.fess.crawler.transformer.Transformer;
import org.codelibs.fess.crawler.util.TextUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentHelper {
    private static final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

    protected static final String SIMILAR_DOC_HASH_PREFIX = "$";

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

    public String getContent(final ResponseData responseData, final String content, final Map<String, Object> dataMap) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }

        final int maxAlphanumTermSize = getMaxAlphanumTermSize();
        final int maxSymbolTermSize = getMaxSymbolTermSize();
        final boolean duplicateTermRemoved = isDuplicateTermRemoved();
        final int[] spaceChars = getSpaceChars();
        try (final Reader reader = new StringReader(content)) {
            return TextUtil.normalizeText(reader).initialCapacity(content.length()).maxAlphanumTermSize(maxAlphanumTermSize)
                    .maxSymbolTermSize(maxSymbolTermSize).duplicateTermRemoved(duplicateTermRemoved).spaceChars(spaceChars).execute();
        } catch (final IOException e) {
            return StringUtil.EMPTY; // empty
        }
    }

    protected int getMaxAlphanumTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxAlphanumTermSizeAsInteger().intValue();
    }

    protected int getMaxSymbolTermSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentMaxSymbolTermSizeAsInteger().intValue();
    }

    protected boolean isDuplicateTermRemoved() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.isCrawlerDocumentDuplicateTermRemoved();
    }

    protected int[] getSpaceChars() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getCrawlerDocumentSpaceCharsAsArray();
    }

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

    public Map<String, Object> processRequest(final CrawlingConfig crawlingConfig, final String crawlingInfoId, final String url) {
        if (StringUtil.isBlank(crawlingInfoId)) {
            throw new CrawlingAccessException("sessionId is null.");
        }

        final CrawlerClientFactory crawlerClientFactory = ComponentUtil.getCrawlerClientFactory();
        crawlingConfig.initializeClientFactory(crawlerClientFactory);
        final CrawlerClient client = crawlerClientFactory.getClient(url);
        if (client == null) {
            throw new CrawlingAccessException("CrawlerClient is null for " + url);
        }

        final long startTime = System.currentTimeMillis();
        try (final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build())) {
            if (responseData.getRedirectLocation() != null) {
                final Set<RequestData> childUrlList = new HashSet<>();
                childUrlList.add(RequestDataBuilder.newRequestData().get().url(responseData.getRedirectLocation()).build());
                throw new ChildUrlsException(childUrlList, this.getClass().getName() + "#RedirectedFrom:" + url);
            }
            responseData.setExecutionTime(System.currentTimeMillis() - startTime);
            responseData.setSessionId(crawlingInfoId);

            final RuleManager ruleManager = SingletonLaContainer.getComponent(RuleManager.class);
            final Rule rule = ruleManager.getRule(responseData);
            if (rule == null) {
                throw new CrawlingAccessException("No url rule for " + url);
            } else {
                responseData.setRuleId(rule.getRuleId());
                final ResponseProcessor responseProcessor = rule.getResponseProcessor();
                if (responseProcessor instanceof DefaultResponseProcessor) {
                    final Transformer transformer = ((DefaultResponseProcessor) responseProcessor).getTransformer();
                    final ResultData resultData = transformer.transform(responseData);
                    final byte[] data = resultData.getData();
                    if (data != null) {
                        try {
                            @SuppressWarnings("unchecked")
                            final Map<String, Object> result = (Map<String, Object>) SerializeUtil.fromBinaryToObject(data);
                            return result;
                        } catch (final Exception e) {
                            throw new CrawlerSystemException("Could not create an instance from bytes.", e);
                        }
                    }
                } else {
                    throw new CrawlingAccessException("The response processor is not DefaultResponseProcessor. responseProcessor: "
                            + responseProcessor + ", url: " + url);
                }
            }
            return null;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to parse " + url, e);
        }
    }

    public String decodeSimilarDocHash(final String hash) {
        if (hash != null && hash.startsWith(SIMILAR_DOC_HASH_PREFIX) && hash.length() > SIMILAR_DOC_HASH_PREFIX.length()) {
            final byte[] decode = Base64.getUrlDecoder().decode(hash.substring(SIMILAR_DOC_HASH_PREFIX.length()));
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(decode)), Constants.UTF_8))) {
                return ReaderUtil.readText(reader);
            } catch (final IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to decode " + hash, e);
                }
            }
        }
        return hash;
    }

    public String encodeSimilarDocHash(final String hash) {
        if (hash != null && !hash.startsWith(SIMILAR_DOC_HASH_PREFIX)) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
                    gos.write(hash.getBytes(Constants.UTF_8));
                }
                return SIMILAR_DOC_HASH_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(baos.toByteArray());
            } catch (final IOException e) {
                logger.warn("Failed to encode " + hash, e);
            }
        }
        return hash;
    }

}
