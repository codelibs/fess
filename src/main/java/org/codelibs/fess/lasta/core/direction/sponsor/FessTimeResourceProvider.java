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
package org.codelibs.fess.lasta.core.direction.sponsor;

import java.util.Date;

import org.codelibs.fess.lasta.core.direction.FessConfig;
import org.lastaflute.core.time.BusinessTimeHandler;
import org.lastaflute.core.time.RelativeDateScript;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.core.time.TimeResourceProvider;
import org.lastaflute.core.time.TypicalBusinessTimeHandler;

/**
 * @author jflute
 */
public class FessTimeResourceProvider implements TimeResourceProvider {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final FessConfig maihamaConfig;

    protected final RelativeDateScript script = new RelativeDateScript();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FessTimeResourceProvider(FessConfig maihamaConfig) {
        this.maihamaConfig = maihamaConfig;
    }

    // ===================================================================================
    //                                                                      Basic Handling
    //                                                                      ==============
    @Override
    public BusinessTimeHandler provideBusinessTimeHandler(TimeManager timeManager) {
        return new TypicalBusinessTimeHandler(() -> {
            return timeManager.getCurrentMillis();
        }, () -> {
            return FessUserTimeZoneProcessProvider.centralTimeZone;
        });
    }

    @Override
    public boolean isCurrentIgnoreTransaction() {
        // this project uses transaction time for current date
        return false; // fixedly
    }

    // ===================================================================================
    //                                                                     Time Adjustment
    //                                                                     ===============
    @Override
    public boolean isAdjustAbsoluteMode() { // *1
        final String exp = maihamaConfig.getTimeAdjustTimeMillis();
        return exp.startsWith("$"); // means absolute e.g. $(2014/07/10)
    }

    @Override
    public long provideAdjustTimeMillis() { // *1
        final String exp = maihamaConfig.getTimeAdjustTimeMillis();
        try {
            return doProvideAdjustTimeMillis(exp);
        } catch (final RuntimeException e) {
            final String msg = "Illegal property for time.adjust.time.millis: " + exp;
            throw new IllegalStateException(msg);
        }
    }

    protected long doProvideAdjustTimeMillis(final String exp) {
        if (exp.startsWith("$")) { // absolute e.g. $(2014/07/10)
            return script.resolveHardCodingDate(exp).getTime();
        } else if (exp.contains("(")) { // relative e.g. addDay(3)
            final long current = System.currentTimeMillis();
            final Date resolved = script.resolveRelativeDate(exp, new Date(current));
            return resolved.getTime() - current;
        } else { // should be millisecond as relative
            return maihamaConfig.getTimeAdjustTimeMillisAsLong();
        }
    }
    // *1: called per called for dynamic change in development
}
