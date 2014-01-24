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

package jp.sf.fess.db.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DBDef;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBCurrent {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBCurrent.class);

    /** Singleton instance. */
    private static final DBCurrent _instance = new DBCurrent();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBDef _currentDBDef;
    {
        _currentDBDef = DBDef.codeOf("h2");
        if (_currentDBDef == null) {
            _currentDBDef = DBDef.Unknown;
        }
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    private DBCurrent() {
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    /**
     * Get singleton instance.
     * @return Singleton instance. (NotNull)
     */
    public static DBCurrent getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public void initializeCurrentDBDef(final DBDef currentDBDef) {
        if (_log.isInfoEnabled()) {
            _log.info("...Setting currentDBDef: " + currentDBDef);
        }
        if (currentDBDef == null) {
            final String msg = "The argument 'currentDBDef' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _currentDBDef = currentDBDef;
    }

    public DBDef currentDBDef() {
        return _currentDBDef;
    }

    public boolean isCurrentDBDef(final DBDef currentDBDef) {
        return _currentDBDef.equals(currentDBDef);
    }
}
