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
package org.codelibs.fess.es.log.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.log.bsentity.BsSearchLog;
import org.codelibs.fess.es.log.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * @author FreeGen
 */
public class SearchLog extends BsSearchLog {

    private static final long serialVersionUID = 1L;

    private List<SearchFieldLog> searchFieldLogList;

    private OptionalEntity<UserInfo> userInfo;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public void setClickLogList(final List<ClickLog> clickLogList) {

    }

    public void addSearchFieldLogValue(final String name, final String value) {
        if (StringUtil.isNotBlank(name) && StringUtil.isNotBlank(value)) {
            final SearchFieldLog fieldLog = new SearchFieldLog();
            fieldLog.setName(name);
            fieldLog.setValue(value);
            if (searchFieldLogList == null) {
                searchFieldLogList = new ArrayList<>();
            }
            searchFieldLogList.add(fieldLog);
        }
    }

    public void setSearchQuery(final String query) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SEARCH_QUERY, query);
    }

    public OptionalEntity<UserInfo> getUserInfo() {
        if (getUserInfoId() == null) {
            return OptionalEntity.empty();
        } else if (userInfo == null) {
            final UserInfoBhv userInfoBhv = ComponentUtil.getComponent(UserInfoBhv.class);
            userInfo = userInfoBhv.selectByPK(getUserInfoId());
        }
        return userInfo;
    }

    public void setUserInfo(final OptionalEntity<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public List<SearchFieldLog> getSearchFieldLogList() {
        if (searchFieldLogList == null) {
            final SearchFieldLogBhv searchFieldLogBhv = ComponentUtil.getComponent(SearchFieldLogBhv.class);
            searchFieldLogList = searchFieldLogBhv.selectList(cb -> {
                cb.query().setSearchLogId_Equal(getId());
                cb.fetchFirst(ComponentUtil.getFessConfig().getPageSearchFieldLogMaxFetchSizeAsInteger());
            });
        }
        return searchFieldLogList;
    }

    @Override
    public void setUserInfoId(final String value) {
        userInfo = null;
        super.setUserInfoId(value);
    }

    @Override
    public Map<String, Object> toSource() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        Map<String, Object> sourceMap = new HashMap<>();
        if (accessType != null) {
            sourceMap.put("accessType", accessType);
        }
        if (user != null) {
            sourceMap.put("user", user);
        }
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        if (queryId != null) {
            sourceMap.put("queryId", queryId);
        }
        if (clientIp != null) {
            sourceMap.put("clientIp", clientIp);
        }
        if (hitCount != null) {
            sourceMap.put("hitCount", hitCount);
        }
        if (queryOffset != null) {
            sourceMap.put("queryOffset", queryOffset);
        }
        if (queryPageSize != null) {
            sourceMap.put("queryPageSize", queryPageSize);
        }
        if (referer != null) {
            sourceMap.put("referer", referer);
        }
        if (requestedAt != null) {
            sourceMap.put("requestedAt", systemHelper.convertDateTime(requestedAt));
        }
        if (responseTime != null) {
            sourceMap.put("responseTime", responseTime);
        }
        if (queryTime != null) {
            sourceMap.put("queryTime", queryTime);
        }
        if (searchWord != null) {
            sourceMap.put("searchWord", searchWord);
        }
        if (userAgent != null) {
            sourceMap.put("userAgent", userAgent);
        }
        if (userInfoId != null) {
            sourceMap.put("userInfoId", userInfoId);
        }
        if (userSessionId != null) {
            sourceMap.put("userSessionId", userSessionId);
        }
        if (languages != null) {
            sourceMap.put("languages", languages);
        }
        return sourceMap;
    }

    @Override
    public String toString() {
        return "SearchLog [searchFieldLogList=" + searchFieldLogList + ", userInfo=" + userInfo + ", accessType=" + accessType + ", user="
                + user + ", roles=" + Arrays.toString(roles) + ", queryId=" + queryId + ", clientIp=" + clientIp + ", hitCount=" + hitCount
                + ", queryOffset=" + queryOffset + ", queryPageSize=" + queryPageSize + ", referer=" + referer + ", requestedAt="
                + requestedAt + ", responseTime=" + responseTime + ", queryTime=" + queryTime + ", searchWord=" + searchWord
                + ", userAgent=" + userAgent + ", userInfoId=" + userInfoId + ", userSessionId=" + userSessionId + ", docMeta=" + docMeta
                + ", languages=" + languages + "]";
    }
}
