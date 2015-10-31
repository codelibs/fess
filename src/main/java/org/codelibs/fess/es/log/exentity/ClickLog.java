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
package org.codelibs.fess.es.log.exentity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.codelibs.fess.Constants;
import org.codelibs.fess.es.log.bsentity.BsClickLog;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

/**
 * @author FreeGen
 */
public class ClickLog extends BsClickLog {

    private static final long serialVersionUID = 1L;

    private long queryRequestedTime;

    private String userSessionId;

    private String docId;

    private long clickCount;

    private OptionalEntity<SearchLog> searchLog;

    public OptionalEntity<SearchLog> getSearchLog() {
        if (searchLog == null) {
            final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);
            searchLog = searchLogBhv.selectEntity(cb -> {
                cb.query().docMeta().setId_Equal(getSearchLogId());
            });
        }
        return searchLog;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(final String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public long getQueryRequestedTime() {
        return queryRequestedTime;
    }

    public void setQueryRequestedTime(final long queryRequestedTime) {
        this.queryRequestedTime = queryRequestedTime;
    }

    public String getRequestedTimeForList() {
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATETIME_FORMAT);
        if (getRequestedTime() != null) {
            return sdf.format(new Date(getRequestedTime()));
        }
        return null;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(final String docId) {
        this.docId = docId;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(final long clickCount) {
        this.clickCount = clickCount;
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

}
