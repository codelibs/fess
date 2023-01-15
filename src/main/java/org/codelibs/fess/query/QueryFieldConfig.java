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

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class QueryFieldConfig {

    private static final Logger logger = LogManager.getLogger(QueryFieldConfig.class);

    public static final String SCORE_FIELD = "score";

    public static final String DOC_SCORE_FIELD = "_score";

    public static final String SITE_FIELD = "site";

    public static final String INURL_FIELD = "inurl";

    protected static final String SCORE_SORT_VALUE = "score";

    protected String[] responseFields;

    protected String[] scrollResponseFields;

    protected String[] cacheResponseFields;

    protected String[] highlightedFields;

    protected String[] searchFields;

    protected String[] facetFields;

    protected String[] sortFields;

    protected Set<String> apiResponseFieldSet;

    protected Set<String> notAnalyzedFieldSet;

    protected List<Pair<String, Float>> additionalDefaultList = new ArrayList<>();

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
        }
        if (apiResponseFieldSet == null) {
            setApiResponseFields(fessConfig.getQueryAdditionalApiResponseFields(//
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

    public void setNotAnalyzedFields(final String[] fields) {
        notAnalyzedFieldSet = new HashSet<>();
        Collections.addAll(notAnalyzedFieldSet, fields);
    }

    protected boolean isSortField(final String field) {
        for (final String f : sortFields) {
            if (f.equals(field)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFacetField(final String field) {
        if (StringUtil.isBlank(field)) {
            return false;
        }
        boolean flag = false;
        for (final String f : facetFields) {
            if (field.equals(f)) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isFacetSortValue(final String sort) {
        return "count".equals(sort) || "index".equals(sort);
    }

    public void setApiResponseFields(final String[] fields) {
        apiResponseFieldSet = new HashSet<>();
        Collections.addAll(apiResponseFieldSet, fields);
    }

    public boolean isApiResponseField(final String field) {
        return apiResponseFieldSet.contains(field);
    }

    /**
     * @return the responseFields
     */
    public String[] getResponseFields() {
        return responseFields;
    }

    /**
     * @param responseFields the responseFields to set
     */
    public void setResponseFields(final String[] responseFields) {
        this.responseFields = responseFields;
    }

    public String[] getScrollResponseFields() {
        return scrollResponseFields;
    }

    public void setScrollResponseFields(final String[] scrollResponseFields) {
        this.scrollResponseFields = scrollResponseFields;
    }

    public String[] getCacheResponseFields() {
        return cacheResponseFields;
    }

    public void setCacheResponseFields(final String[] cacheResponseFields) {
        this.cacheResponseFields = cacheResponseFields;
    }

    /**
     * @return the highlightedFields
     */
    public String[] getHighlightedFields() {
        return highlightedFields;
    }

    /**
     * @param highlightedFields the highlightedFields to set
     */
    public void setHighlightedFields(final String[] highlightedFields) {
        this.highlightedFields = highlightedFields;
    }

    public void highlightedFields(final Consumer<Stream<String>> stream) {
        stream(highlightedFields).of(stream);
    }

    /**
     * @return the supportedFields
     */
    public String[] getSearchFields() {
        return searchFields;
    }

    /**
     * @param supportedFields the supportedFields to set
     */
    public void setSearchFields(final String[] supportedFields) {
        searchFields = supportedFields;
    }

    /**
     * @return the facetFields
     */
    public String[] getFacetFields() {
        return facetFields;
    }

    /**
     * @param facetFields the facetFields to set
     */
    public void setFacetFields(final String[] facetFields) {
        this.facetFields = facetFields;
    }

    /**
     * @return the sortFields
     */
    public String[] getSortFields() {
        return sortFields;
    }

    /**
     * @param sortFields the sortFields to set
     */
    public void setSortFields(final String[] sortFields) {
        this.sortFields = sortFields;
    }

}
