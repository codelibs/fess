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
package org.codelibs.fess.es.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.log.bsentity.dbmeta.ClickLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsClickLog extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** urlId */
    protected String urlId;

    /** docId */
    protected String docId;

    /** order */
    protected Integer order;

    /** queryId */
    protected String queryId;

    /** queryRequestedAt */
    protected LocalDateTime queryRequestedAt;

    /** requestedAt */
    protected LocalDateTime requestedAt;

    /** url */
    protected String url;

    /** userSessionId */
    protected String userSessionId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "click_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (urlId != null) {
            addFieldToSource(sourceMap, "urlId", urlId);
        }
        if (docId != null) {
            addFieldToSource(sourceMap, "docId", docId);
        }
        if (order != null) {
            addFieldToSource(sourceMap, "order", order);
        }
        if (queryId != null) {
            addFieldToSource(sourceMap, "queryId", queryId);
        }
        if (queryRequestedAt != null) {
            addFieldToSource(sourceMap, "queryRequestedAt", queryRequestedAt);
        }
        if (requestedAt != null) {
            addFieldToSource(sourceMap, "requestedAt", requestedAt);
        }
        if (url != null) {
            addFieldToSource(sourceMap, "url", url);
        }
        if (userSessionId != null) {
            addFieldToSource(sourceMap, "userSessionId", userSessionId);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(urlId);
        sb.append(dm).append(docId);
        sb.append(dm).append(order);
        sb.append(dm).append(queryId);
        sb.append(dm).append(queryRequestedAt);
        sb.append(dm).append(requestedAt);
        sb.append(dm).append(url);
        sb.append(dm).append(userSessionId);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getUrlId() {
        checkSpecifiedProperty("urlId");
        return convertEmptyToNull(urlId);
    }

    public void setUrlId(String value) {
        registerModifiedProperty("urlId");
        this.urlId = value;
    }

    public String getDocId() {
        checkSpecifiedProperty("docId");
        return convertEmptyToNull(docId);
    }

    public void setDocId(String value) {
        registerModifiedProperty("docId");
        this.docId = value;
    }

    public Integer getOrder() {
        checkSpecifiedProperty("order");
        return order;
    }

    public void setOrder(Integer value) {
        registerModifiedProperty("order");
        this.order = value;
    }

    public String getQueryId() {
        checkSpecifiedProperty("queryId");
        return convertEmptyToNull(queryId);
    }

    public void setQueryId(String value) {
        registerModifiedProperty("queryId");
        this.queryId = value;
    }

    public LocalDateTime getQueryRequestedAt() {
        checkSpecifiedProperty("queryRequestedAt");
        return queryRequestedAt;
    }

    public void setQueryRequestedAt(LocalDateTime value) {
        registerModifiedProperty("queryRequestedAt");
        this.queryRequestedAt = value;
    }

    public LocalDateTime getRequestedAt() {
        checkSpecifiedProperty("requestedAt");
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime value) {
        registerModifiedProperty("requestedAt");
        this.requestedAt = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return convertEmptyToNull(url);
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    public String getUserSessionId() {
        checkSpecifiedProperty("userSessionId");
        return convertEmptyToNull(userSessionId);
    }

    public void setUserSessionId(String value) {
        registerModifiedProperty("userSessionId");
        this.userSessionId = value;
    }
}
