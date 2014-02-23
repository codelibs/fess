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

import java.text.SimpleDateFormat;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsSearchLog;

import org.codelibs.core.util.StringUtil;

/**
 * The entity of SEARCH_LOG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class SearchLog extends BsSearchLog {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public String getRequestedTimeForList() {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.DEFAULT_DATETIME_FORMAT);
        if (getRequestedTime() != null) {
            return sdf.format(getRequestedTime());
        }
        return null;
    }

    public void addSearchFieldLogValue(final String name, final String value) {
        if (StringUtil.isNotBlank(name) && StringUtil.isNotBlank(value)) {
            final SearchFieldLog fieldLog = new SearchFieldLog();
            fieldLog.setName(name);
            fieldLog.setValue(value);
            getSearchFieldLogList().add(fieldLog);
        }
    }

    public void setSearchQuery(final String query) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SEARCH_QUERY, query);
    }

    public void setSolrQuery(final String solrQuery) {
        addSearchFieldLogValue(Constants.SEARCH_FIELD_LOG_SOLR_QUERY, solrQuery);
    }

}
