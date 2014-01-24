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

package jp.sf.fess.db.cbean.nss;

import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;

import org.seasar.dbflute.cbean.ConditionQuery;

/**
 * The nest select set-upper of CRAWLING_SESSION_INFO.
 * @author DBFlute(AutoGenerator)
 */
public class CrawlingSessionInfoNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected CrawlingSessionInfoCQ _query;

    public CrawlingSessionInfoNss(final CrawlingSessionInfoCQ query) {
        _query = query;
    }

    public boolean hasConditionQuery() {
        return _query != null;
    }

    // ===================================================================================
    //                                                                     Nested Relation
    //                                                                     ===============
    /**
     * With nested relation columns to select clause. <br />
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public CrawlingSessionNss withCrawlingSession() {
        _query.doNss(new CrawlingSessionInfoCQ.NssCall() {
            @Override
            public ConditionQuery qf() {
                return _query.queryCrawlingSession();
            }
        });
        return new CrawlingSessionNss(_query.queryCrawlingSession());
    }

}
