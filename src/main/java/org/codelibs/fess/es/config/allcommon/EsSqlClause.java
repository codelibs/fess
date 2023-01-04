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
package org.codelibs.fess.es.config.allcommon;

import org.dbflute.cbean.sqlclause.AbstractSqlClause;
import org.dbflute.dbway.DBWay;

/**
 * @author ESFlute (using FreeGen)
 */
public class EsSqlClause extends AbstractSqlClause {

    private static final long serialVersionUID = 1L;

    public EsSqlClause(String tableDbName) {
        super(tableDbName);
    }

    @Override
    public void lockForUpdate() {
    }

    @Override
    public DBWay dbway() {
        return null;
    }

    @Override
    protected void doFetchFirst() {
    }

    @Override
    protected void doFetchPage() {
    }

    @Override
    protected void doClearFetchPageClause() {
    }

    @Override
    protected String createSelectHint() {
        return null;
    }

    @Override
    protected String createFromBaseTableHint() {
        return null;
    }

    @Override
    protected String createFromHint() {
        return null;
    }

    @Override
    protected String createSqlSuffix() {
        return null;
    }

    @Override
    public void fetchFirst(int fetchSize) {
        _fetchScopeEffective = true;
        if (fetchSize < 0) {
            String msg = "Argument[fetchSize] should be plus: " + fetchSize;
            throw new IllegalArgumentException(msg);
        }
        _fetchStartIndex = 0;
        _fetchSize = fetchSize;
        _fetchPageNumber = 1;
        doClearFetchPageClause();
        doFetchFirst();
    }
}
