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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.opensearch.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing related query configurations.
 * This class provides functionality to load, cache, and retrieve related queries
 * based on search terms and virtual hosts. Related queries are used to suggest
 * alternative or supplementary search terms to improve search results.
 */
public class RelatedQueryHelper extends AbstractConfigHelper {
    private static final Logger logger = LogManager.getLogger(RelatedQueryHelper.class);

    /**
     * Map storing related queries organized by virtual host key and search term.
     * The outer map key is the virtual host key, the inner map key is the search term
     * (in lowercase), and the value is an array of related query strings.
     */
    protected volatile Map<String, Map<String, String[]>> relatedQueryMap = Collections.emptyMap();

    /**
     * Default constructor for RelatedQueryHelper.
     * Initializes the helper with an empty related query map.
     */
    public RelatedQueryHelper() {
        super();
    }

    /**
     * Initializes the RelatedQueryHelper after dependency injection is complete.
     * This method is called automatically by the dependency injection framework
     * and loads the initial related query configurations.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    /**
     * Retrieves a list of all available related query entities from the data store.
     * The results are ordered by term and limited by the configured maximum fetch size.
     *
     * @return a list of RelatedQuery entities containing all available related queries
     */
    public List<RelatedQuery> getAvailableRelatedQueryList() {

        return ComponentUtil.getComponent(RelatedQueryBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Term_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageRelatedqueryMaxFetchSizeAsInteger());
        });
    }

    @Override
    public int load() {
        final Map<String, Map<String, String[]>> relatedQueryMap = new HashMap<>();
        getAvailableRelatedQueryList().stream().forEach(entity -> {
            final String key = getHostKey(entity);
            Map<String, String[]> map = relatedQueryMap.get(key);
            if (map == null) {
                map = new HashMap<>();
                relatedQueryMap.put(key, map);
            }
            map.put(toLowerCase(entity.getTerm()), entity.getQueries());
        });
        this.relatedQueryMap = relatedQueryMap;
        return relatedQueryMap.size();
    }

    /**
     * Extracts the virtual host key from a RelatedQuery entity.
     * If the virtual host is blank or null, returns an empty string.
     *
     * @param entity the RelatedQuery entity to extract the host key from
     * @return the virtual host key, or empty string if blank or null
     */
    protected String getHostKey(final RelatedQuery entity) {
        final String key = entity.getVirtualHost();
        return StringUtil.isBlank(key) ? StringUtil.EMPTY : key;
    }

    /**
     * Retrieves related queries for a given search term.
     * The search is performed using the current virtual host context and
     * the query term is converted to lowercase for case-insensitive matching.
     *
     * @param query the search term to find related queries for
     * @return an array of related query strings, or empty array if none found
     */
    public String[] getRelatedQueries(final String query) {
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        final Map<String, String[]> map = relatedQueryMap.get(key);
        if (map != null) {
            final String[] queries = map.get(toLowerCase(query));
            if (queries != null) {
                return queries;
            }
        }
        return StringUtil.EMPTY_STRINGS;
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

}
