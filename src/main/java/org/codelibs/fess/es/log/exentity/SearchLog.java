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
package org.codelibs.fess.es.log.exentity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchLogEvent;
import org.codelibs.fess.es.log.bsentity.BsSearchLog;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * @author FreeGen
 */
public class SearchLog extends BsSearchLog implements SearchLogEvent {

    private static final long serialVersionUID = 1L;

    private final List<Pair<String, String>> searchFieldLogList = new ArrayList<>();

    private final List<Pair<String, String>> headerList = new ArrayList<>();

    private OptionalEntity<UserInfo> userInfo;

    private Map<String, Object> fields;

    private final List<Map<String, Object>> documentList = new ArrayList<>();

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    @Override
    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public void addSearchFieldLogValue(final String name, final String value) {
        if (StringUtil.isNotBlank(name) && StringUtil.isNotBlank(value)) {
            searchFieldLogList.add(new Pair<>(name, value));
        }
    }

    public void addRequestHeaderValue(final String name, final String value) {
        if (StringUtil.isNotBlank(name) && StringUtil.isNotBlank(value)) {
            headerList.add(new Pair<>(name, value));
        }
    }

    public void addDocument(final Map<String, Object> doc) {
        documentList.add(doc);
    }

    public void setSearchQuery(final String query) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SEARCH_QUERY, query);
    }

    public OptionalEntity<UserInfo> getUserInfo() {
        if (getUserInfoId() == null) {
            return OptionalEntity.empty();
        }
        if (userInfo == null) {
            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);
            userInfo = userInfoBhv.selectByPK(getUserInfoId());
        }
        return userInfo;
    }

    public void setUserInfo(final OptionalEntity<UserInfo> userInfo) {
        this.userInfo = userInfo;
        userInfo.ifPresent(e -> {
            super.setUserInfoId(e.getId());
        });
    }

    public List<Pair<String, String>> getSearchFieldLogList() {
        return searchFieldLogList;
    }

    public List<Pair<String, String>> getRequestHeaderList() {
        return headerList;
    }

    public void addField(final String key, final Object value) {
        fields.put(key, value);
    }

    public String getLogMessage() {
        return getSearchWord();
    }

    @Override
    public Map<String, Object> toSource() {
        final Map<String, Object> sourceMap = super.toSource();
        if (fields != null) {
            sourceMap.putAll(fields);
        }
        final Map<String, List<String>> searchFieldMap = searchFieldLogList.stream()
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())));
        sourceMap.put("searchField", searchFieldMap);
        final Map<String, List<String>> headerMap = headerList.stream()
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())));
        sourceMap.put("headers", headerMap);
        sourceMap.put("documents", documentList);
        return sourceMap;
    }

    @Override
    protected void addFieldToSource(final Map<String, Object> sourceMap, final String field, final Object value) {
        if (value instanceof final LocalDateTime ldt) {
            final ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
            super.addFieldToSource(sourceMap, field, DateTimeFormatter.ISO_INSTANT.format(zdt));
        } else {
            super.addFieldToSource(sourceMap, field, value);
        }
    }

    @Override
    public void setUserInfoId(final String value) {
        userInfo = null;
        super.setUserInfoId(value);
    }

    @Override
    public String toString() {
        return "SearchLog [searchFieldLogList=" + searchFieldLogList + ", headerList=" + headerList + ", userInfo=" + userInfo + ", fields="
                + fields + ", accessType=" + accessType + ", clientIp=" + clientIp + ", hitCount=" + hitCount + ", languages=" + languages
                + ", queryId=" + queryId + ", queryOffset=" + queryOffset + ", queryPageSize=" + queryPageSize + ", queryTime=" + queryTime
                + ", referer=" + referer + ", requestedAt=" + requestedAt + ", responseTime=" + responseTime + ", roles="
                + Arrays.toString(roles) + ", searchWord=" + searchWord + ", user=" + user + ", userAgent=" + userAgent + ", userInfoId="
                + userInfoId + ", userSessionId=" + userSessionId + ", virtualHost=" + virtualHost + ", documents=" + documentList + "]";
    }

    @Override
    public String getEventType() {
        return "log";
    }
}
