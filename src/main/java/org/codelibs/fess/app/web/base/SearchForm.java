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
package org.codelibs.fess.app.web.base;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.Size;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

public class SearchForm implements SearchRequestParams {

    public Map<String, String[]> fields = new HashMap<>();

    @Size(max = 1000)
    public String q;

    @Size(max = 1000)
    public String sort;

    @ValidateTypeFailure
    public Integer num;

    public String[] lang;

    public String[] ex_q;

    @ValidateTypeFailure
    public Integer start;

    @ValidateTypeFailure
    public Integer pn;

    // advance

    @Override
    public int getStartPosition() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (start == null) {
            start = fessConfig.getPagingSearchPageStartAsInteger();
        }
        return start;
    }

    @Override
    public int getPageSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (num == null) {
            num = fessConfig.getPagingSearchPageSizeAsInteger();
        } else {
            try {
                if (num.intValue() > fessConfig.getPagingSearchPageMaxSizeAsInteger().intValue() || num.intValue() <= 0) {
                    num = fessConfig.getPagingSearchPageMaxSizeAsInteger();
                }
            } catch (final NumberFormatException e) {
                num = fessConfig.getPagingSearchPageSizeAsInteger();
            }
        }
        return num;
    }

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
    public String[] getLanguages() {
        return stream(lang).get(stream -> stream.filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    @Override
    public GeoInfo getGeoInfo() {
        final GeoInfo geoInfo = createGeoInfo(LaRequestUtil.getRequest());
        if (geoInfo != null) {
            return geoInfo;
        }
        return ComponentUtil.getQueryHelper().getDefaultGeoInfo();
    }

    @Override
    public FacetInfo getFacetInfo() {
        return ComponentUtil.getQueryHelper().getDefaultFacetInfo();
    }

    @Override
    public String getSort() {
        return sort;
    }

    @Override
    public Object getAttribute(final String name) {
        return LaRequestUtil.getRequest().getAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return ComponentUtil.getRequestManager().getUserLocale();
    }

    @Override
    public SearchRequestType getType() {
        return SearchRequestType.SEARCH;
    }
}
