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
package org.codelibs.fess.app.web.base;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.beans.util.CopyOptions;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.AccessTokenHelper;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessHtmlPath;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.hook.AccessContext;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.db.dbflute.accesscontext.AccessContextArranger;
import org.lastaflute.web.TypicalAction;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.ResponseManager;
import org.lastaflute.web.servlet.session.SessionManager;
import org.lastaflute.web.validation.ActionValidator;
import org.lastaflute.web.validation.LaValidatable;
import org.lastaflute.web.validation.VaMessenger;

/**
 * @author jflute
 */
public abstract class FessBaseAction extends TypicalAction // has several interfaces for direct use
        implements LaValidatable<FessMessages>, FessHtmlPath {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Logger logger = LogManager.getLogger(FessBaseAction.class);

    /** The application type for FESs, e.g. used by access context. */
    protected static final String APP_TYPE = "FES"; // #change_it_first

    /** The user type for Admin, e.g. used by access context. */
    protected static final String USER_TYPE = "A";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected FessLoginAssist fessLoginAssist;

    @Resource
    protected SessionManager sessionManager;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected ActivityHelper activityHelper;

    @Resource
    protected ResponseManager responseManager;

    @Resource
    protected TimeManager timeManager;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected AccessTokenHelper accessTokenHelper;

    @Resource
    protected ViewHelper viewHelper;

    @Resource
    private MessageManager messageManager;

    @Resource
    private RequestManager requestManager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    // to suppress unexpected override by sub-class
    // you should remove the 'final' if you need to override this
    @Override
    public ActionResponse godHandPrologue(final ActionRuntime runtime) {
        fessLoginAssist.getSavedUserBean().ifPresent(u -> {
            final boolean result = u.getFessUser().refresh();
            if (logger.isDebugEnabled()) {
                logger.debug("refresh user info: {}", result);
            }
        });
        return viewHelper.getActionHook().godHandPrologue(runtime, super::godHandPrologue);
    }

    @Override
    public final ActionResponse godHandMonologue(final ActionRuntime runtime) {
        return viewHelper.getActionHook().godHandMonologue(runtime, super::godHandMonologue);
    }

    @Override
    public final void godHandEpilogue(final ActionRuntime runtime) {
        viewHelper.getActionHook().godHandEpilogue(runtime, super::godHandEpilogue);
    }

    // #app_customize you can customize the action hook
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) { // application may override
        return viewHelper.getActionHook().hookBefore(runtime, super::hookBefore);
    }

    @Override
    public void hookFinally(final ActionRuntime runtime) {
        viewHelper.getActionHook().hookFinally(runtime, super::hookFinally);
    }

    // ===================================================================================
    //                                                                      Access Context
    //                                                                      ==============
    @Override
    protected AccessContextArranger newAccessContextArranger() { // for framework
        // fess does not use DBFlute, and this is unneeded so dummy
        return resource -> {
            final AccessContext context = new AccessContext();
            context.setAccessLocalDateTimeProvider(() -> timeManager.currentDateTime());
            context.setAccessUserProvider(() -> "unused");
            return context;
        };
    }

    // ===================================================================================
    //                                                                           User Info
    //                                                                           =========
    @Override
    protected OptionalThing<FessUserBean> getUserBean() { // to return as concrete class
        return fessLoginAssist.getSavedUserBean();
    }

    @Override
    protected String myAppType() { // for framework
        return APP_TYPE;
    }

    @Override
    protected OptionalThing<String> myUserType() { // for framework
        return OptionalThing.of(USER_TYPE); // same reason as getUserBean()
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @SuppressWarnings("unchecked")
    @Override
    public ActionValidator<FessMessages> createValidator() {
        return systemHelper.createValidator(requestManager, this::createMessages, myValidationGroups());
    }

    @Override
    public FessMessages createMessages() { // application may call
        return new FessMessages(); // overriding to change return type to concrete-class
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============

    protected void saveInfo(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = createMessages();
        validationMessagesLambda.message(messages);
        sessionManager.info().saveMessages(messages);
    }

    protected void saveError(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = createMessages();
        validationMessagesLambda.message(messages);
        sessionManager.errors().saveMessages(messages);
    }

    protected static void copyBeanToBean(final Object src, final Object dest, final Consumer<CopyOptions> option) {
        BeanUtil.copyBeanToBean(src, dest, option);
    }

    protected static void copyMapToBean(final Map<String, ? extends Object> src, final Object dest, final Consumer<CopyOptions> option) {
        BeanUtil.copyMapToBean(src, dest, option);
    }

    protected static <T> T copyBeanToNewBean(final Object src, final Class<T> destClass) {
        return BeanUtil.copyBeanToNewBean(src, destClass);
    }

    protected String buildThrowableMessage(final Throwable t) {
        final StringBuilder buf = new StringBuilder(100);
        Throwable current = t;
        while (current != null) {
            buf.append(current.getLocalizedMessage()).append(' ');
            current = current.getCause();
        }
        return buf.toString();
    }

    public static boolean isCheckboxEnabled(final String value) {
        if (value == null) {
            return false;
        }
        return Constants.ON.equalsIgnoreCase(value) || Constants.TRUE.equalsIgnoreCase(value);
    }
}
