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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.TimeZone;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.lastaflute.core.time.TypicalTimeResourceProvider;

/**
 * @author jflute
 */
public class FessTimeResourceProvider extends TypicalTimeResourceProvider {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final FessConfig fessConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessTimeResourceProvider(final FessConfig fessConfig) {
        this.fessConfig = fessConfig;
    }

    // ===================================================================================
    //                                                                      Basic Handling
    //                                                                      ==============
    @Override
    protected TimeZone getCentralTimeZone() {
        return FessUserTimeZoneProcessProvider.centralTimeZone;
    }

    // ===================================================================================
    //                                                                     Time Adjustment
    //                                                                     ===============
    @Override
    protected String getTimeAdjustTimeMillis() {
        return fessConfig.getTimeAdjustTimeMillis();
    }

    @Override
    protected Long getTimeAdjustTimeMillisAsLong() {
        return fessConfig.getTimeAdjustTimeMillisAsLong();
    }
}
