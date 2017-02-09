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
package org.codelibs.fess.app.service;

import javax.annotation.Resource;

import org.codelibs.fess.es.log.exbhv.SearchFieldLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.helper.SystemHelper;

public class SearchLogService {

    @Resource
    private SearchLogBhv searchLogBhv;

    @Resource
    private SearchFieldLogBhv searchFieldLogBhv;

    @Resource
    private SystemHelper systemHelper;

    public void deleteBefore(final int days) {
        searchLogBhv.selectCursor(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        }, entity -> {
            searchFieldLogBhv.queryDelete(subCb -> {
                subCb.query().setSearchLogId_Equal(entity.getId());
            });
        });
        searchLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

}
