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
package org.codelibs.fess.app.web.admin.searchlist;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Size;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
//public class SearchListForm implements Serializable {
public class ListForm implements SearchRequestParams, Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 1000)
    public String query;

    public String sort;

    @ValidateTypeFailure
    public Integer start;

    @ValidateTypeFailure
    public Integer pn;

    @ValidateTypeFailure
    public Integer num;

    public String[] lang;

    @Required
    public String docId;

    @Required
    public String url;

    @Override
    public String getQuery() {
        return query;
    }

    public Map<String, String[]> fields = new HashMap<>();

    public String additional[];

    @Size(max = 10)
    public String op;

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

    // geo

    public GeoInfo geo;

    // facet

    public FacetInfo facet;

    private int startPosition = -1;

    private int pageSize = -1;

    @Override
    public int getStartPosition() {
        if (startPosition != -1) {
            return startPosition;
        }
        startPosition = start;
        return startPosition;
    }

    @Override
    public int getPageSize() {
        if (pageSize != -1) {
            return pageSize;
        }
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        pageSize = num;
        if (pageSize > queryHelper.getMaxPageSize() || pageSize <= 0) {
            pageSize = queryHelper.getMaxPageSize();
        }
        num = pageSize;
        return pageSize;
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
    
    public void initialize() {
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        if (start == null) {
            start = queryHelper.getDefaultStart();
        }
        if (num == null) {
            num = queryHelper.getDefaultPageSize();
        } else if (num > queryHelper.getMaxPageSize()) {
            num = queryHelper.getMaxPageSize();
        }
    }
    
}
