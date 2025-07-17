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
package org.codelibs.fess.app.web.admin.searchlist;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * The list form for Search List.
 */
public class ListForm extends SearchRequestParams {

    /**
     * Default constructor.
     */
    public ListForm() {
        super();
    }

    /** The search query string. */
    @Size(max = 1000)
    public String q;

    /** The sort field and direction. */
    public String sort;

    /** The start position for search results. */
    @ValidateTypeFailure
    public Integer start;

    /** The offset for pagination. */
    @ValidateTypeFailure
    public Integer offset;

    /** The page number. */
    @ValidateTypeFailure
    public Integer pn;

    /** The number of results to display. */
    @ValidateTypeFailure
    public Integer num;

    /** The languages. */
    public String[] lang;

    /** The fields. */
    public Map<String, String[]> fields = new HashMap<>();

    /** The conditions. */
    public Map<String, String[]> as = new HashMap<>();

    /** The extra queries. */
    public String[] ex_q;

    /** The similar document hash. */
    public String sdh;

    @Override
    public String getQuery() {
        return q;
    }

    @Override
    public String[] getExtraQueries() {
        return stream(ex_q).get(stream -> stream.filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    @Override
    public Map<String, String[]> getFields() {
        return fields;
    }

    @Override
    public Map<String, String[]> getConditions() {
        return as;
    }

    @Override
    public int getStartPosition() {
        if (start == null) {
            start = ComponentUtil.getFessConfig().getPagingSearchPageStartAsInteger();
        }
        return start;
    }

    @Override
    public int getOffset() {
        if (offset == null) {
            offset = 0;
        }
        return offset;
    }

    @Override
    public int getPageSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (num == null) {
            num = fessConfig.getPagingSearchPageSizeAsInteger();
        }
        if (num > fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue() || num <= 0) {
            num = fessConfig.getPagingSearchPageMaxSizeAsInteger();
        }
        return num;
    }

    @Override
    public String[] getLanguages() {
        return stream(lang).get(stream -> stream.filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    @Override
    public GeoInfo getGeoInfo() {
        return null;
    }

    @Override
    public FacetInfo getFacetInfo() {
        return null;
    }

    @Override
    public HighlightInfo getHighlightInfo() {
        return new HighlightInfo();
    }

    @Override
    public String getSort() {
        return sort;
    }

    /**
     * Initializes the form with default values from configuration.
     */
    public void initialize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (start == null) {
            start = fessConfig.getPagingSearchPageStartAsInteger();
        }
        if (num == null) {
            num = fessConfig.getPagingSearchPageSizeAsInteger();
        } else if (num > fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue()) {
            num = fessConfig.getPagingSearchPageMaxSizeAsInteger();
        }
    }

    @Override
    public Object getAttribute(final String name) {
        return LaRequestUtil.getOptionalRequest().map(req -> req.getAttribute(name)).orElse(null);
    }

    @Override
    public Locale getLocale() {
        return ComponentUtil.getRequestManager().getUserLocale();
    }

    @Override
    public SearchRequestType getType() {
        return SearchRequestType.ADMIN_SEARCH;
    }

    @Override
    public String getSimilarDocHash() {
        return sdh;
    }

    @Override
    public String getTrackTotalHits() {
        final String value = ComponentUtil.getFessConfig().getPageSearchlistTrackTotalHits();
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return null;
    }
}
