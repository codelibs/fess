/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.exentity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsClickLog;

/**
 * The entity of CLICK_LOG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class ClickLog extends BsClickLog {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private Timestamp queryRequestedTime;

    private String userSessionId;

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(final String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public Timestamp getQueryRequestedTime() {
        return queryRequestedTime;
    }

    public void setQueryRequestedTime(final Timestamp queryRequestedTime) {
        this.queryRequestedTime = queryRequestedTime;
    }

    public String getRequestedTimeForList() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.DEFAULT_DATETIME_FORMAT);
        if (getRequestedTime() != null) {
            return sdf.format(getRequestedTime());
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClickLog [queryRequestedTime=" + queryRequestedTime
                + ", userSessionId=" + userSessionId + ", _id=" + _id
                + ", _searchId=" + _searchId + ", _url=" + _url
                + ", _requestedTime=" + _requestedTime + "]";
    }
}
