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

import jp.sf.fess.db.cbean.cq.FileAuthenticationCQ;

import org.seasar.dbflute.cbean.ConditionQuery;

/**
 * The nest select set-upper of FILE_AUTHENTICATION.
 * @author DBFlute(AutoGenerator)
 */
public class FileAuthenticationNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileAuthenticationCQ _query;

    public FileAuthenticationNss(final FileAuthenticationCQ query) {
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
     * FILE_CRAWLING_CONFIG by my FILE_CRAWLING_CONFIG_ID, named 'fileCrawlingConfig'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public FileCrawlingConfigNss withFileCrawlingConfig() {
        _query.doNss(new FileAuthenticationCQ.NssCall() {
            @Override
            public ConditionQuery qf() {
                return _query.queryFileCrawlingConfig();
            }
        });
        return new FileCrawlingConfigNss(_query.queryFileCrawlingConfig());
    }

}
