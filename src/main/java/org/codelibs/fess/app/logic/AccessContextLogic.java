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
package org.codelibs.fess.app.logic;

import javax.annotation.Resource;

import org.codelibs.fess.mylasta.action.FessUserBean;
import org.dbflute.hook.AccessContext;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.db.dbflute.accesscontext.AccessContextResource;

public class AccessContextLogic {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private TimeManager timeManager;

    // ===================================================================================
    //                                                                  Resource Interface
    //                                                                  ==================
    @FunctionalInterface
    public interface UserTypeSupplier {
        OptionalThing<String> supply();
    }

    @FunctionalInterface
    public interface UserBeanSupplier {
        OptionalThing<FessUserBean> supply();
    }

    @FunctionalInterface
    public interface AppTypeSupplier {
        String supply();
    }

    // ===================================================================================
    //                                                                      Create Context
    //                                                                      ==============
    public AccessContext create(final AccessContextResource resource, final UserTypeSupplier userTypeSupplier,
            final UserBeanSupplier userBeanSupplier, final AppTypeSupplier appTypeSupplier) {
        final AccessContext context = new AccessContext();
        context.setAccessLocalDateTimeProvider(() -> timeManager.currentDateTime());
        context.setAccessUserProvider(() -> buildAccessUserTrace(resource, userTypeSupplier, appTypeSupplier));
        return context;
    }

    private String buildAccessUserTrace(final AccessContextResource resource, final UserTypeSupplier userTypeSupplier,
            final AppTypeSupplier appTypeSupplier) {
        final StringBuilder sb = new StringBuilder();
        sb.append(userTypeSupplier.supply().orElse("_"));
        sb.append(",").append(appTypeSupplier.supply()).append(",").append(resource.getModuleName());
        final String trace = sb.toString();
        final int columnSize = 200;
        return trace.length() > columnSize ? trace.substring(0, columnSize) : trace;
    }
}
