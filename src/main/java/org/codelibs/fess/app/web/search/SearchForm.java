/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Size;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

public class SearchForm implements SearchRequestParams, Serializable {

    private static final long serialVersionUID = 1L;

    public Map<String, String[]> fields = new HashMap<>();

    @Size(max = 1000)
    public String query;

    @Size(max = 1000)
    public String sort;

    @ValidateTypeFailure
    public Integer num;

    public String[] lang;

    public String additional[];

    @Size(max = 10)
    public String op;

    @ValidateTypeFailure
    public Integer start;

    @ValidateTypeFailure
    public Integer pn;

    // response redirect

    // geo

    public GeoInfo geo;

    // facet

    public FacetInfo facet;

    // advance

    public Map<String, String[]> options = new HashMap<>();

    @Override
    public int getStartPosition() {
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        if (start == null) {
            start = queryHelper.getDefaultStart();
        }
        return start;
    }

    @Override
    public int getPageSize() {
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        if (num == null) {
            num = queryHelper.getDefaultPageSize();
        } else {
            try {
                if (num.intValue() > queryHelper.getMaxPageSize() || num.intValue() <= 0) {
                    num = queryHelper.getMaxPageSize();
                }
            } catch (final NumberFormatException e) {
                num = queryHelper.getDefaultPageSize();
            }
        }
        return num;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getOperator() {
        return op;
    }

    @Override
    public String[] getAdditional() {
        return additional;
    }

    @Override
    public Map<String, String[]> getFields() {
        return fields;
    }

    @Override
    public String[] getLanguages() {
        return lang;
    }

    @Override
    public GeoInfo getGeoInfo() {
        return geo;
    }

    @Override
    public FacetInfo getFacetInfo() {
        return facet;
    }

    @Override
    public String getSort() {
        return sort;
    }
}
