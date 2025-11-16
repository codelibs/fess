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
package org.codelibs.fess.query;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for query field mappings in the Fess search engine.
 * This class manages field configurations for various query operations including
 * response fields, search fields, facet fields, sort fields, and highlighting.
 * It initializes field mappings from the FessConfig and provides methods to
 * query field properties and capabilities.
 */
public class QueryFieldConfig {

    /**
     * Default constructor.
     */
    public QueryFieldConfig() {
        // Default constructor
    }

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(QueryFieldConfig.class);

    /** Field name for document score in search results */
    public static final String SCORE_FIELD = "score";

    /** Field name for OpenSearch document score */
    public static final String DOC_SCORE_FIELD = "_score";

    /** Field name for site information in search results */
    public static final String SITE_FIELD = "site";

    /** Field name for URL-based search queries */
    public static final String INURL_FIELD = "inurl";

    /** Sort value for score-based sorting */
    protected static final String SCORE_SORT_VALUE = "score";

    /** Array of fields to be included in standard search response */
    protected String[] responseFields;

    /** Array of fields to be included in scroll search response */
    protected String[] scrollResponseFields;

    /** Array of fields to be included in cache search response */
    protected String[] cacheResponseFields;

    /** Array of fields that can be highlighted in search results */
    protected String[] highlightedFields;

    /** Array of fields that can be searched against */
    protected String[] searchFields;

    /** Set of fields that can be searched against for O(1) lookup */
    protected Set<String> searchFieldSet;

    /** Array of fields that can be used for faceted search */
    protected String[] facetFields;

    /** Set of fields that can be used for faceted search for O(1) lookup */
    protected Set<String> facetFieldSet;

    /** Array of fields that can be used for sorting search results */
    protected String[] sortFields;

    /** Set of fields that can be used for sorting for O(1) lookup */
    protected Set<String> sortFieldSet;

    /** Set of fields that are allowed in API responses */
    protected Set<String> apiResponseFieldSet;

    /** Set of fields that are not analyzed during indexing */
    protected Set<String> notAnalyzedFieldSet;

    /** List of additional default fields with their boost values */
    protected List<Pair<String, Float>> additionalDefaultList = new ArrayList<>();

    /**
     * Initializes the query field configuration by loading field mappings from FessConfig.
     * This method is called after dependency injection is complete.
     * It sets up response fields, search fields, facet fields, sort fields, and other
     * field configurations based on the application configuration.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (responseFields == null) {
            responseFields = fessConfig.getQueryAdditionalResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldThumbnail(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldHasCache());
        }
        if (scrollResponseFields == null) {
            scrollResponseFields = fessConfig.getQueryAdditionalScrollResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldThumbnail(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldHasCache());
        }
        if (cacheResponseFields == null) {
            cacheResponseFields = fessConfig.getQueryAdditionalCacheResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldCache());
        }
        if (highlightedFields == null) {
            highlightedFields = fessConfig.getQueryAdditionalHighlightedFields( //
                    fessConfig.getIndexFieldContent());
        }
        if (searchFields == null) {
            searchFields = fessConfig.getQueryAdditionalSearchFields(//
                    INURL_FIELD, //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldContent(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldSegment(), //
                    fessConfig.getIndexFieldAnchor(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldLang());
            // Initialize Set for O(1) lookup performance
            searchFieldSet = new HashSet<>();
            Collections.addAll(searchFieldSet, searchFields);
        }
        if (facetFields == null) {
            facetFields = fessConfig.getQueryAdditionalFacetFields(//
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldContent(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldSegment());
            // Initialize Set for O(1) lookup performance
            facetFieldSet = new HashSet<>();
            Collections.addAll(facetFieldSet, facetFields);
        }
        if (sortFields == null) {
            sortFields = fessConfig.getQueryAdditionalSortFields(//
                    SCORE_SORT_VALUE, //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount());
            // Initialize Set for O(1) lookup performance
            sortFieldSet = new HashSet<>();
            Collections.addAll(sortFieldSet, sortFields);
        }
        if (apiResponseFieldSet == null) {
            setApiResponseFields(fessConfig.getQueryAdditionalApiResponseFields(//
                    SCORE_SORT_VALUE, //
                    fessConfig.getResponseFieldContentDescription(), //
                    fessConfig.getResponseFieldContentTitle(), //
                    fessConfig.getResponseFieldSitePath(), //
                    fessConfig.getResponseFieldUrlLink(), //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl()));
        }
        if (notAnalyzedFieldSet == null) {
            setNotAnalyzedFields(fessConfig.getQueryAdditionalNotAnalyzedFields(//
                    fessConfig.getIndexFieldAnchor(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldExpires(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldHasCache(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldParentId(), //
                    fessConfig.getIndexFieldPrimaryTerm(), //
                    fessConfig.getIndexFieldRole(), //
                    fessConfig.getIndexFieldSegment(), //
                    fessConfig.getIndexFieldSeqNo(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldVersion()));
        }
        split(fessConfig.getQueryAdditionalAnalyzedFields(), ",")
                .of(stream -> stream.map(String::trim).filter(StringUtil::isNotBlank).forEach(s -> notAnalyzedFieldSet.remove(s)));
        split(fessConfig.getQueryAdditionalDefaultFields(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
            final Pair<String, Float> pair = new Pair<>();
            final String[] values = s.split(":");
            if (values.length == 1) {
                pair.setFirst(values[0].trim());
                pair.setSecond(1.0f);
            } else if (values.length > 1) {
                pair.setFirst(values[0]);
                pair.setSecond(Float.parseFloat(values[1]));
            } else {
                return null;
            }
            return pair;
        }).forEach(additionalDefaultList::add));
    }

    /**
     * Sets the fields that should not be analyzed during indexing.
     *
     * @param fields array of field names that should not be analyzed
     */
    public void setNotAnalyzedFields(final String[] fields) {
        notAnalyzedFieldSet = new HashSet<>();
        Collections.addAll(notAnalyzedFieldSet, fields);
    }

    /**
     * Checks if the specified field can be used for sorting.
     * Uses O(1) Set lookup for improved performance.
     *
     * @param field the field name to check
     * @return true if the field can be used for sorting, false otherwise
     */
    protected boolean isSortField(final String field) {
        return sortFieldSet != null && sortFieldSet.contains(field);
    }

    /**
     * Checks if the specified field can be used for faceted search.
     * Uses O(1) Set lookup for improved performance.
     *
     * @param field the field name to check
     * @return true if the field can be used for faceted search, false otherwise
     */
    public boolean isFacetField(final String field) {
        if (StringUtil.isBlank(field)) {
            return false;
        }
        return facetFieldSet != null && facetFieldSet.contains(field);
    }

    /**
     * Checks if the specified sort value is valid for facet sorting.
     *
     * @param sort the sort value to check
     * @return true if the sort value is valid for facets ("count" or "index"), false otherwise
     */
    public boolean isFacetSortValue(final String sort) {
        return "count".equals(sort) || "index".equals(sort);
    }

    /**
     * Sets the fields that are allowed in API responses.
     *
     * @param fields array of field names that are allowed in API responses
     */
    public void setApiResponseFields(final String[] fields) {
        apiResponseFieldSet = new HashSet<>();
        Collections.addAll(apiResponseFieldSet, fields);
    }

    /**
     * Checks if the specified field is allowed in API responses.
     *
     * @param field the field name to check
     * @return true if the field is allowed in API responses, false otherwise
     */
    public boolean isApiResponseField(final String field) {
        return apiResponseFieldSet.contains(field);
    }

    /**
     * Gets the fields that are included in standard search responses.
     *
     * @return array of field names for standard search responses
     */
    public String[] getResponseFields() {
        return responseFields;
    }

    /**
     * Sets the fields that are included in standard search responses.
     *
     * @param responseFields array of field names for standard search responses
     */
    public void setResponseFields(final String[] responseFields) {
        this.responseFields = responseFields;
    }

    /**
     * Gets the fields that are included in scroll search responses.
     *
     * @return array of field names for scroll search responses
     */
    public String[] getScrollResponseFields() {
        return scrollResponseFields;
    }

    /**
     * Sets the fields that are included in scroll search responses.
     *
     * @param scrollResponseFields array of field names for scroll search responses
     */
    public void setScrollResponseFields(final String[] scrollResponseFields) {
        this.scrollResponseFields = scrollResponseFields;
    }

    /**
     * Gets the fields that are included in cache search responses.
     *
     * @return array of field names for cache search responses
     */
    public String[] getCacheResponseFields() {
        return cacheResponseFields;
    }

    /**
     * Sets the fields that are included in cache search responses.
     *
     * @param cacheResponseFields array of field names for cache search responses
     */
    public void setCacheResponseFields(final String[] cacheResponseFields) {
        this.cacheResponseFields = cacheResponseFields;
    }

    /**
     * Gets the fields that can be highlighted in search results.
     *
     * @return array of field names that can be highlighted
     */
    public String[] getHighlightedFields() {
        return highlightedFields;
    }

    /**
     * Sets the fields that can be highlighted in search results.
     *
     * @param highlightedFields array of field names that can be highlighted
     */
    public void setHighlightedFields(final String[] highlightedFields) {
        this.highlightedFields = highlightedFields;
    }

    /**
     * Processes the highlighted fields using the provided stream consumer.
     *
     * @param stream consumer that processes the stream of highlighted field names
     */
    public void highlightedFields(final Consumer<Stream<String>> stream) {
        stream(highlightedFields).of(stream);
    }

    /**
     * Gets the fields that can be searched against.
     *
     * @return array of field names that can be searched
     */
    public String[] getSearchFields() {
        return searchFields;
    }

    /**
     * Sets the fields that can be searched against.
     * Also updates the searchFieldSet for O(1) lookup performance.
     *
     * @param supportedFields array of field names that can be searched
     */
    public void setSearchFields(final String[] supportedFields) {
        searchFields = supportedFields;
        searchFieldSet = new HashSet<>();
        Collections.addAll(searchFieldSet, supportedFields);
    }

    /**
     * Gets the fields that can be used for faceted search.
     *
     * @return array of field names that can be used for faceted search
     */
    public String[] getFacetFields() {
        return facetFields;
    }

    /**
     * Sets the fields that can be used for faceted search.
     * Also updates the facetFieldSet for O(1) lookup performance.
     *
     * @param facetFields array of field names that can be used for faceted search
     */
    public void setFacetFields(final String[] facetFields) {
        this.facetFields = facetFields;
        facetFieldSet = new HashSet<>();
        Collections.addAll(facetFieldSet, facetFields);
    }

    /**
     * Gets the fields that can be used for sorting search results.
     *
     * @return array of field names that can be used for sorting
     */
    public String[] getSortFields() {
        return sortFields;
    }

    /**
     * Sets the fields that can be used for sorting search results.
     * Also updates the sortFieldSet for O(1) lookup performance.
     *
     * @param sortFields array of field names that can be used for sorting
     */
    public void setSortFields(final String[] sortFields) {
        this.sortFields = sortFields;
        sortFieldSet = new HashSet<>();
        Collections.addAll(sortFieldSet, sortFields);
    }

}
