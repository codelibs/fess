/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.codelibs.fess.mylasta.action.FessUserBean;
import org.dbflute.hook.AccessContext;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.db.dbflute.accesscontext.AccessContextResource;

import jakarta.annotation.Resource;

/**
 * The logic for access context.
 */
public class AccessContextLogic {

    /**
     * Default constructor.
     */
    public AccessContextLogic() {
        // nothing
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private TimeManager timeManager;

    // ===================================================================================
    //                                                                  Resource Interface
    //                                                                  ==================
    /**
     * The supplier of user type.
     */
    @FunctionalInterface
    public interface UserTypeSupplier {
        /**
         * Supply the user type.
         * @return The user type.
         */
        OptionalThing<String> supply();
    }

    /**
     * The supplier of user bean.
     */
    @FunctionalInterface
    public interface UserBeanSupplier {
        /**
         * Supply the user bean.
         * @return The user bean.
         */
        OptionalThing<FessUserBean> supply();
    }

    /**
     * The supplier of application type.
     */
    @FunctionalInterface
    public interface AppTypeSupplier {
        /**
         * Supply the application type.
         * @return The application type.
         */
        String supply();
    }

    // ===================================================================================
    //                                                                      Create Context
    //                                                                      ==============
    /**
     * Create the access context.
     * @param resource The access context resource.
     * @param userTypeSupplier The supplier of user type.
     * @param userBeanSupplier The supplier of user bean.
     * @param appTypeSupplier The supplier of application type.
     * @return The access context.
     */
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
