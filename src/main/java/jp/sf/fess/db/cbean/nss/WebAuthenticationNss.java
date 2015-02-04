/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import jp.sf.fess.db.cbean.cq.WebAuthenticationCQ;

import org.seasar.dbflute.cbean.ConditionQuery;

/**
 * The nest select set-upper of WEB_AUTHENTICATION.
 * @author DBFlute(AutoGenerator)
 */
public class WebAuthenticationNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebAuthenticationCQ _query;

    public WebAuthenticationNss(final WebAuthenticationCQ query) {
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
     * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
     */
    public void withWebCrawlingConfig() {
        _query.doNss(new WebAuthenticationCQ.NssCall() {
            @Override
            public ConditionQuery qf() {
                return _query.queryWebCrawlingConfig();
            }
        });
    }
}
