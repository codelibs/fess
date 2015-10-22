package org.codelibs.fess.entity;

import java.util.Map;

public interface SearchRequestParams {

    String getQuery();

    String getOperator();

    String[] getAdditional();

    Map<String, String[]> getFields();

    String[] getLanguages();

    GeoInfo getGeoInfo();

    FacetInfo getFacetInfo();

    String getSort();

    int getStartPosition();

    int getPageSize();

}
