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

import org.dbflute.system.DBFluteSystem;
import org.dbflute.system.provider.DfFinalTimeZoneProvider;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.core.direction.CurtainBeforeHook;
import org.lastaflute.core.direction.FwAssistantDirector;

/**
 * @author jflute
 */
public class FessCurtainBeforeHook implements CurtainBeforeHook {

    @Override
    public void hook(final FwAssistantDirector assistantDirector) {
        processDBFluteSystem();
    }

    protected void processDBFluteSystem() {
        DBFluteSystem.unlock();
        DBFluteSystem.setFinalTimeZoneProvider(createFinalTimeZoneProvider());
        DBFluteSystem.lock();
    }

    protected DfFinalTimeZoneProvider createFinalTimeZoneProvider() {
        return new DfFinalTimeZoneProvider() {
            protected final TimeZone provided = FessUserTimeZoneProcessProvider.centralTimeZone;

            @Override
            public TimeZone provide() {
                return provided;
            }

            @Override
            public String toString() {
                return DfTypeUtil.toClassTitle(this) + ":{" + provided.getID() + "}";
            }
        };
    }
}
