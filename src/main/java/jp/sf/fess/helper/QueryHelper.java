/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.helper;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.sf.fess.entity.FacetInfo;
import jp.sf.fess.entity.GeoInfo;
import jp.sf.fess.entity.MoreLikeThisInfo;
import jp.sf.fess.entity.SearchQuery;
import jp.sf.fess.entity.SearchQuery.SortField;
import jp.sf.fess.util.SearchParamMap;

public interface QueryHelper {

    SearchQuery build(String query, boolean envCondition);

    String[] getResponseFields();

    String[] getHighlightingFields();

    int getHighlightSnippetSize();

    String getShards();

    boolean isFacetField(String f);

    String buildFacetQuery(String query);

    boolean isFacetSortValue(String sort);

    String buildOptionQuery(SearchParamMap options);

    int getTimeAllowed();

    Set<Entry<String, String[]>> getRequestParameterSet();

    String getAdditionalGeoQuery();

    int getMaxSearchResultOffset();

    boolean hasDefaultSortFields();

    SortField[] getDefaultSortFields();

    String getHighlightingPrefix();

    String getMoreLikeThisField(String[] field);

    String getSuggestQueryType(String fieldName);

    boolean isAnalysisFieldName(String fieldName);

    FacetInfo getDefaultFacetInfo();

    MoreLikeThisInfo getDefaultMoreLikeThisInfo();

    GeoInfo getDefaultGeoInfo();

    Map<String, String[]> getQueryParamMap();

    boolean isApiResponseField(String field);
}