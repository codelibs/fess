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
/*

 * Copyright 2014-2015 the original author or authors.
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
package org.codelibs.fess.web.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.codelibs.fess.mylasta.action.FessHtmlPath;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.helper.HandyDate;
import org.dbflute.hook.AccessContext;
import org.dbflute.optional.OptionalObject;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.db.dbflute.accesscontext.AccessContextArranger;
import org.lastaflute.db.dbflute.accesscontext.AccessContextResource;
import org.lastaflute.web.TypicalAction;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.login.UserBean;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.validation.ActionValidator;
import org.lastaflute.web.validation.LaValidatable;

/**
 * @author jflute
 */
public abstract class FessBaseAction extends TypicalAction // has several interfaces for direct use
        implements LaValidatable<FessMessages>, FessMessages.LabelKey, FessHtmlPath {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The application type for FESs, e.g. used by access context. */
    protected static final String APP_TYPE = "FES"; // #change_it_first

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RequestManager requestManager;
    @Resource
    private FessConfig fessConfig;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    // to suppress unexpected override by sub-class
    // you should remove the 'final' if you need to override this
    @Override
    public final ActionResponse godHandPrologue(ActionRuntime runtime) {
        return super.godHandPrologue(runtime);
    }

    @Override
    public final ActionResponse godHandMonologue(ActionRuntime runtime) {
        return super.godHandMonologue(runtime);
    }

    @Override
    public final void godHandEpilogue(ActionRuntime runtime) {
        super.godHandEpilogue(runtime);
    }

    // #app_customize you can customize the action hook
    @Override
    public ActionResponse hookBefore(ActionRuntime runtime) { // application may override
        return super.hookBefore(runtime);
    }

    @Override
    public void hookFinally(ActionRuntime runtime) {
        super.hookFinally(runtime);
    }

    // ===================================================================================
    //                                                                      Access Context
    //                                                                      ==============
    @Override
    protected AccessContextArranger newAccessContextArranger() { // for framework
        return resource -> {
            final AccessContext context = new AccessContext();
            context.setAccessLocalDateTimeProvider(() -> currentDateTime());
            context.setAccessUserProvider(() -> buildAccessUserTrace(resource));
            return context;
        };
    }

    private String buildAccessUserTrace(AccessContextResource resource) {
        // #app_customize you can customize the user trace for common column
        final StringBuilder sb = new StringBuilder();
        sb.append(myUserType().map(userType -> userType + ":").orElse(""));
        sb.append(getUserBean().map(bean -> bean.getUserId()).orElseGet(() -> -1L));
        sb.append(",").append(myAppType()).append(",").append(resource.getModuleName());
        final String trace = sb.toString();
        final int columnSize = 200;
        return trace.length() > columnSize ? trace.substring(0, columnSize) : trace;
    }

    // ===================================================================================
    //                                                                           User Info
    //                                                                           =========
    @Override
    protected OptionalThing<UserBean> getUserBean() { // to return as concrete class
        return OptionalThing.empty();// uses application server authentication so empty here
    }

    @Override
    protected String myAppType() { // for framework
        return APP_TYPE;
    }

    @Override
    protected OptionalThing<String> myUserType() { // for framework
        return OptionalObject.empty(); // same reason as getUserBean()
    }

    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty(); // same reason as getUserBean()
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @SuppressWarnings("unchecked")
    @Override
    public ActionValidator<FessMessages> createValidator() {
        return super.createValidator();
    }

    @Override
    public FessMessages createMessages() { // application may call
        return new FessMessages(); // overriding to change return type to concrete-class
    }

    // ===================================================================================
    //                                                                   Conversion Helper
    //                                                                   =================
    // #app_customize you can customize the conversion logic
    // -----------------------------------------------------
    //                                         to Local Date
    //                                         -------------
    protected OptionalThing<LocalDate> toDate(String exp) { // application may call
        if (isNotEmpty(exp)) {
            return OptionalThing.of(new HandyDate(exp, myConvZone()).getLocalDate());
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified expression for local date was null or empty: " + exp);
            });
        }
    }

    protected OptionalThing<LocalDateTime> toDateTime(String exp) { // application may call
        if (isNotEmpty(exp)) {
            return OptionalThing.of(new HandyDate(exp, myConvZone()).getLocalDateTime());
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified expression for local date was null or empty: " + exp);
            });
        }
    }

    // -----------------------------------------------------
    //                                       to Display Date
    //                                       ---------------
    protected OptionalThing<String> toDispDate(LocalDate date) { // application may call
        if (date != null) {
            return OptionalThing.of(new HandyDate(date, myConvZone()).toDisp(myDatePattern()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date was null.");
            });
        }
    }

    protected OptionalThing<String> toDispDate(LocalDateTime dateTime) { // application may call
        if (dateTime != null) {
            return OptionalThing.of(new HandyDate(dateTime, myConvZone()).toDisp(myDatePattern()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date-time was null.");
            });
        }
    }

    protected OptionalThing<String> toDispDateTime(LocalDateTime dateTime) { // application may call
        if (dateTime != null) {
            return OptionalThing.of(new HandyDate(dateTime, myConvZone()).toDisp(myDateTimePattern()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date-time was null.");
            });
        }
    }

    // -----------------------------------------------------
    //                                   Conversion Resource
    //                                   -------------------
    protected String myDatePattern() {
        return "yyyy/MM/dd";
    }

    protected String myDateTimePattern() {
        return "yyyy/MM/dd HH:mm:ss";
    }

    protected TimeZone myConvZone() {
        return requestManager.getUserTimeZone();
    }

    // ===================================================================================
    //                                                                            Document
    //                                                                            ========
    /**
     * {@inheritDoc} <br>
     * Application Origin Methods:
     * <pre>
     * <span style="font-size: 130%; color: #553000">[Conversion Helper]</span>
     * o toDate(exp) <span style="color: #3F7E5E">// convert expression to local date</span>
     * o toDateTime(exp) <span style="color: #3F7E5E">// convert expression to local date-time</span>
     * o toDispDate(date) <span style="color: #3F7E5E">// convert local date to display expression</span>
     * o toDispDateTime(date) <span style="color: #3F7E5E">// convert local date-time to display expression</span>
     * </pre>
     */
    @Override
    public void document1_CallableSuperMethod() {
        super.document1_CallableSuperMethod();
    }
}
