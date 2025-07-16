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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.opensearch.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing related content configurations.
 * This class provides functionality to load, cache, and retrieve related content
 * based on search queries and virtual host configurations. It supports both exact
 * term matching and regex pattern matching for flexible content association.
 */
public class RelatedContentHelper extends AbstractConfigHelper {

    /**
     * Default constructor for RelatedContentHelper.
     * The constructor does not perform any initialization logic as the actual
     * initialization is handled by the {@link #init()} method annotated with
     * {@code @PostConstruct}.
     */
    public RelatedContentHelper() {
        // Default constructor - initialization is handled by init() method
    }

    private static final Logger logger = LogManager.getLogger(RelatedContentHelper.class);

    /**
     * Cache map storing related content configurations organized by virtual host key.
     * The outer map key is the virtual host key, and the value is a pair containing:
     * - First: Map of exact term matches (term -> content)
     * - Second: List of regex pattern matches (Pattern -> content template)
     */
    protected Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = Collections.emptyMap();

    /**
     * Prefix used to identify regex patterns in related content terms.
     * When a term starts with this prefix, it is treated as a regular expression
     * pattern rather than an exact match term.
     */
    protected String regexPrefix = "regex:";

    /**
     * Placeholder string used in regex-based related content templates.
     * This placeholder is replaced with the actual search query when
     * a regex pattern matches the query.
     */
    protected String queryPlaceHolder = "__QUERY__";

    /**
     * Initializes the RelatedContentHelper by loading related content configurations
     * from the data store. This method is called automatically after dependency
     * injection is complete.
     *
     * PostConstruct annotation ensures this method is called after the bean
     * has been constructed and all dependencies have been injected.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    /**
     * Retrieves all available related content configurations from the data store.
     * The results are ordered by sort order ascending, then by term ascending.
     * The number of results is limited by the configured maximum fetch size.
     *
     * @return List of RelatedContent entities containing all available related content configurations
     */
    public List<RelatedContent> getAvailableRelatedContentList() {
        return ComponentUtil.getComponent(RelatedContentBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageRelatedcontentMaxFetchSizeAsInteger());
        });
    }

    @Override
    public int load() {
        final Map<String, Pair<Map<String, String>, List<Pair<Pattern, String>>>> relatedContentMap = new HashMap<>();
        getAvailableRelatedContentList().stream().forEach(entity -> {
            final String key = getHostKey(entity);
            Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
            if (pair == null) {
                pair = new Pair<>(new HashMap<>(), new ArrayList<>());
                relatedContentMap.put(key, pair);
            }
            if (entity.getTerm().startsWith(regexPrefix)) {
                final String regex = entity.getTerm().substring(regexPrefix.length());
                if (StringUtil.isBlank(regex)) {
                    logger.warn("Unknown regex pattern: {}", entity.getTerm());
                } else {
                    pair.getSecond().add(new Pair<>(Pattern.compile(regex), entity.getContent()));
                }
            } else {
                pair.getFirst().put(toLowerCase(entity.getTerm()), entity.getContent());
            }
        });
        this.relatedContentMap = relatedContentMap;
        return relatedContentMap.size();
    }

    /**
     * Extracts the virtual host key from a RelatedContent entity.
     * If the virtual host is blank or null, returns an empty string.
     * This key is used to organize related content by virtual host.
     *
     * @param entity the RelatedContent entity to extract the host key from
     * @return the virtual host key, or empty string if not specified
     */
    protected String getHostKey(final RelatedContent entity) {
        final String key = entity.getVirtualHost();
        return StringUtil.isBlank(key) ? StringUtil.EMPTY : key;
    }

    /**
     * Retrieves related content for a given search query.
     * First checks for exact term matches, then evaluates regex patterns.
     * For regex matches, the query placeholder is replaced with the actual query.
     *
     * @param query the search query to find related content for
     * @return array of related content strings, or empty array if no matches found
     */
    public String[] getRelatedContents(final String query) {
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final Pair<Map<String, String>, List<Pair<Pattern, String>>> pair = relatedContentMap.get(key);
        if (pair != null) {
            final List<String> contentList = new ArrayList<>();
            final String content = pair.getFirst().get(toLowerCase(query));
            if (StringUtil.isNotBlank(content)) {
                contentList.add(content);
            }
            for (final Pair<Pattern, String> regexData : pair.getSecond()) {
                if (regexData.getFirst().matcher(query).matches()) {
                    contentList.add(regexData.getSecond().replace(queryPlaceHolder, query));
                }
            }
            return contentList.toArray(new String[contentList.size()]);
        }
        return StringUtil.EMPTY_STRINGS;
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

    /**
     * Sets the prefix used to identify regex patterns in related content terms.
     * When a term starts with this prefix, it is treated as a regular expression
     * pattern rather than an exact match term.
     *
     * @param regexPrefix the prefix string to identify regex patterns (default: "regex:")
     */
    public void setRegexPrefix(final String regexPrefix) {
        this.regexPrefix = regexPrefix;
    }

    /**
     * Sets the placeholder string used in regex-based related content templates.
     * This placeholder is replaced with the actual search query when
     * a regex pattern matches the query.
     *
     * @param queryPlaceHolder the placeholder string to be replaced with the query (default: "__QUERY__")
     */
    public void setQueryPlaceHolder(final String queryPlaceHolder) {
        this.queryPlaceHolder = queryPlaceHolder;
    }

}
