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
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.core.CommonColumnAutoSetupper;

/**
 * The basic implementation of the auto set-upper of common column.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedCommonColumnAutoSetupper implements
        CommonColumnAutoSetupper {

    // =====================================================================================
    //                                                                            Definition
    //                                                                            ==========
    /** Log instance. */
    private static final Log _log = LogFactory
            .getLog(ImplementedCommonColumnAutoSetupper.class);

    // =====================================================================================
    //                                                                                Set up
    //                                                                                ======
    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCommonColumnOfInsertIfNeeds(final Entity targetEntity) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleCommonColumnOfUpdateIfNeeds(final Entity targetEntity) {
    }

    // =====================================================================================
    //                                                                               Logging
    //                                                                               =======
    protected boolean isInternalDebugEnabled() {
        return DBFluteConfig.getInstance().isInternalDebug()
                && _log.isDebugEnabled();
    }

    protected void logSettingUp(final EntityDefinedCommonColumn entity,
            final String keyword) {
        _log.debug("...Setting up column columns of " + entity.getTableDbName()
                + " before " + keyword);
    }
}
