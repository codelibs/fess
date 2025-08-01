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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.suggest.exception.SuggesterException;
import org.codelibs.fess.suggest.request.popularwords.PopularWordsRequestBuilder;
import org.codelibs.fess.util.ComponentUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing popular words and suggestions.
 * Provides functionality to retrieve popular words based on search parameters
 * and manages caching for improved performance.
 */
public class PopularWordHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PopularWordHelper.class);

    /** Character used to separate cache key components */
    protected static final char CACHE_KEY_SPLITTER = '\n';

    /** Cache for storing popular word lists */
    protected Cache<String, List<String>> cache;

    /** Fess configuration instance */
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public PopularWordHelper() {
        // Default constructor
    }

    /**
     * Initializes the PopularWordHelper after dependency injection.
     * Sets up the cache with configured size and expiration settings.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
        cache = CacheBuilder.newBuilder().maximumSize(fessConfig.getSuggestPopularWordCacheSizeAsInteger().longValue())
                .expireAfterWrite(fessConfig.getSuggestPopularWordCacheExpireAsInteger().longValue(), TimeUnit.MINUTES).build();
    }

    /**
     * Retrieves a list of popular words based on the specified search parameters.
     * Uses caching to improve performance for repeated requests.
     *
     * @param searchRequestType the type of search request
     * @param seed the seed value for popular word generation
     * @param tags array of tags to filter results
     * @param roles array of roles to filter results
     * @param fields array of fields to search in
     * @param excludes array of words to exclude from results
     * @return list of popular words matching the criteria
     */
    public List<String> getWordList(final SearchRequestType searchRequestType, final String seed, final String[] tags, final String[] roles,
            final String[] fields, final String[] excludes) {
        final String baseSeed = seed != null ? seed : fessConfig.getSuggestPopularWordSeed();
        final String[] baseTags = tags != null ? tags : fessConfig.getSuggestPopularWordTagsAsArray();
        final String[] baseRoles = roles != null ? roles
                : ComponentUtil.getRoleQueryHelper().build(searchRequestType).stream().filter(StringUtil::isNotBlank)
                        .toArray(n -> new String[n]);
        final String[] baseFields = fields != null ? fields : fessConfig.getSuggestPopularWordFieldsAsArray();
        final String[] baseExcludes = excludes != null ? excludes : fessConfig.getSuggestPopularWordExcludesAsArray();
        try {
            return cache.get(getCacheKey(baseSeed, baseTags, baseRoles, baseFields, baseExcludes), () -> {
                final List<String> wordList = new ArrayList<>();
                final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();
                final PopularWordsRequestBuilder popularWordsRequestBuilder =
                        suggestHelper.suggester().popularWords().setSize(fessConfig.getSuggestPopularWordSizeAsInteger())
                                .setWindowSize(fessConfig.getSuggestPopularWordWindowSizeAsInteger())
                                .setQueryFreqThreshold(fessConfig.getSuggestPopularWordQueryFreqAsInteger());
                popularWordsRequestBuilder.setSeed(baseSeed);
                stream(baseTags).of(stream -> stream.forEach(tag -> popularWordsRequestBuilder.addTag(tag)));
                stream(baseRoles).of(stream -> stream.forEach(role -> popularWordsRequestBuilder.addRole(role)));
                stream(baseFields).of(stream -> stream.forEach(field -> popularWordsRequestBuilder.addField(field)));
                stream(baseExcludes).of(stream -> stream.forEach(exclude -> popularWordsRequestBuilder.addExcludeWord(exclude)));
                try {
                    popularWordsRequestBuilder.execute().getResponse().getItems().stream().forEach(item -> wordList.add(item.getText()));
                } catch (final SuggesterException e) {
                    logger.warn("Failed to generate popular words.", e);
                }

                return wordList;
            });
        } catch (final ExecutionException e) {
            logger.warn("Failed to load popular words.", e);
        }
        return Collections.emptyList();
    }

    /**
     * Clears all cached popular word lists.
     */
    public void clearCache() {
        cache.invalidateAll();
    }

    /**
     * Generates a cache key based on the provided parameters.
     *
     * @param seed the seed value
     * @param tags array of tags
     * @param roles array of roles
     * @param fields array of fields
     * @param excludes array of excluded words
     * @return cache key string
     */
    protected String getCacheKey(final String seed, final String[] tags, final String[] roles, final String[] fields,
            final String[] excludes) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(seed).append(CACHE_KEY_SPLITTER);
        stream(tags).of(stream -> stream.sorted().reduce((l, r) -> l + r).ifPresent(v -> buf.append(v)));
        buf.append(CACHE_KEY_SPLITTER);
        stream(roles).of(stream -> stream.sorted().reduce((l, r) -> l + r).ifPresent(v -> buf.append(v)));
        buf.append(CACHE_KEY_SPLITTER);
        stream(fields).of(stream -> stream.sorted().reduce((l, r) -> l + r).ifPresent(v -> buf.append(v)));
        buf.append(CACHE_KEY_SPLITTER);
        stream(excludes).of(stream -> stream.sorted().reduce((l, r) -> l + r).ifPresent(v -> buf.append(v)));
        return buf.toString();
    }

}
