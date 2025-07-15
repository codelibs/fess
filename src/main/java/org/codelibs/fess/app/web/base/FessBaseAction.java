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
package org.codelibs.fess.app.web.base;

import java.util.Map;
import java.util.function.Consumer;

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

import jakarta.annotation.Resource;

/**
 * The base action class for Fess web application.
 * This abstract class provides common functionality for all Fess web actions,
 * including user authentication, validation, message handling, and access context management.
 * It extends LastaFlute's TypicalAction and implements validation and HTML path interfaces.
 *
 * @author FESs Project
 * @since 1.0
 */
public abstract class FessBaseAction extends TypicalAction // has several interfaces for direct use
        implements LaValidatable<FessMessages>, FessHtmlPath {

    /**
     * Default constructor.
     */
    public FessBaseAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(FessBaseAction.class);

    /** The application type for FESs, e.g. used by access context. */
    protected static final String APP_TYPE = "FES"; // #change_it_first

    /** The user type for Admin, e.g. used by access context. */
    protected static final String USER_TYPE = "A";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Login assistance helper for managing user authentication and session. */
    @Resource
    protected FessLoginAssist fessLoginAssist;

    /** Session manager for handling HTTP session operations. */
    @Resource
    protected SessionManager sessionManager;

    /** Configuration manager for Fess application settings. */
    @Resource
    protected FessConfig fessConfig;

    /** Helper for managing user activity logging and tracking. */
    @Resource
    protected ActivityHelper activityHelper;

    /** Manager for handling HTTP response operations. */
    @Resource
    protected ResponseManager responseManager;

    /** Time manager for handling date and time operations. */
    @Resource
    protected TimeManager timeManager;

    /** System helper for various system-level operations. */
    @Resource
    protected SystemHelper systemHelper;

    /** Helper for managing access tokens and API authentication. */
    @Resource
    protected AccessTokenHelper accessTokenHelper;

    /** Helper for view-related operations and rendering. */
    @Resource
    protected ViewHelper viewHelper;

    /** Manager for handling application messages and internationalization. */
    @Resource
    private MessageManager messageManager;

    /** Manager for handling HTTP request operations. */
    @Resource
    private RequestManager requestManager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    // to suppress unexpected override by sub-class
    // you should remove the 'final' if you need to override this
    /**
     * Hook method called before action execution.
     * This method refreshes the user information if a user is logged in
     * and delegates to the view helper's action hook.
     *
     * @param runtime the action runtime context
     * @return the action response, or null to continue with normal processing
     */
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

    /**
     * Hook method called during action execution.
     * This method delegates to the view helper's action hook for processing.
     *
     * @param runtime the action runtime context
     * @return the action response, or null to continue with normal processing
     */
    @Override
    public final ActionResponse godHandMonologue(final ActionRuntime runtime) {
        return viewHelper.getActionHook().godHandMonologue(runtime, super::godHandMonologue);
    }

    /**
     * Hook method called after action execution.
     * This method delegates to the view helper's action hook for cleanup.
     *
     * @param runtime the action runtime context
     */
    @Override
    public final void godHandEpilogue(final ActionRuntime runtime) {
        viewHelper.getActionHook().godHandEpilogue(runtime, super::godHandEpilogue);
    }

    // #app_customize you can customize the action hook
    /**
     * Hook method called before action processing.
     * This method can be overridden by subclasses to customize behavior.
     *
     * @param runtime the action runtime context
     * @return the action response, or null to continue with normal processing
     */
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) { // application may override
        return viewHelper.getActionHook().hookBefore(runtime, super::hookBefore);
    }

    /**
     * Hook method called in the finally block of action processing.
     * This method delegates to the view helper's action hook for final cleanup.
     *
     * @param runtime the action runtime context
     */
    @Override
    public void hookFinally(final ActionRuntime runtime) {
        viewHelper.getActionHook().hookFinally(runtime, super::hookFinally);
    }

    // ===================================================================================
    //                                                                      Access Context
    //                                                                      ==============
    /**
     * Creates a new access context arranger for database operations.
     * This method provides a dummy implementation as Fess does not use DBFlute extensively.
     *
     * @return a new access context arranger
     */
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
    /**
     * Gets the current user bean from the session.
     * This method returns the concrete FessUserBean class instead of the generic type.
     *
     * @return an optional containing the current user bean, or empty if not logged in
     */
    @Override
    protected OptionalThing<FessUserBean> getUserBean() { // to return as concrete class
        return fessLoginAssist.getSavedUserBean();
    }

    /**
     * Returns the application type identifier for this Fess application.
     *
     * @return the application type string "FES"
     */
    @Override
    protected String myAppType() { // for framework
        return APP_TYPE;
    }

    /**
     * Returns the user type identifier for this application.
     *
     * @return an optional containing the user type string "A" for Admin
     */
    @Override
    protected OptionalThing<String> myUserType() { // for framework
        return OptionalThing.of(USER_TYPE); // same reason as getUserBean()
    }

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    @SuppressWarnings("unchecked")
    /**
     * Creates a validator instance for form validation.
     * This method uses the system helper to create a validator with Fess-specific messages.
     *
     * @return a new action validator instance
     */
    @Override
    public ActionValidator<FessMessages> createValidator() {
        return systemHelper.createValidator(requestManager, this::createMessages, myValidationGroups());
    }

    /**
     * Creates a new messages instance for handling validation and user messages.
     * This method can be called by the application to create message containers.
     *
     * @return a new FessMessages instance
     */
    @Override
    public FessMessages createMessages() { // application may call
        return new FessMessages(); // overriding to change return type to concrete-class
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============

    /**
     * Saves informational messages to the session.
     * The messages will be displayed to the user on the next page load.
     *
     * @param validationMessagesLambda a lambda function to configure the messages
     */
    protected void saveInfo(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = createMessages();
        validationMessagesLambda.message(messages);
        sessionManager.info().saveMessages(messages);
    }

    /**
     * Saves error messages to the session.
     * The messages will be displayed to the user on the next page load.
     *
     * @param validationMessagesLambda a lambda function to configure the error messages
     */
    protected void saveError(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = createMessages();
        validationMessagesLambda.message(messages);
        sessionManager.errors().saveMessages(messages);
    }

    /**
     * Copies properties from source bean to destination bean.
     * This is a utility method that wraps BeanUtil.copyBeanToBean with custom options.
     *
     * @param src the source bean object
     * @param dest the destination bean object
     * @param option a consumer function to configure copy options
     */
    protected static void copyBeanToBean(final Object src, final Object dest, final Consumer<CopyOptions> option) {
        BeanUtil.copyBeanToBean(src, dest, option);
    }

    /**
     * Copies properties from a map to a bean object.
     * This is a utility method that wraps BeanUtil.copyMapToBean with custom options.
     *
     * @param src the source map containing property values
     * @param dest the destination bean object
     * @param option a consumer function to configure copy options
     */
    protected static void copyMapToBean(final Map<String, ? extends Object> src, final Object dest, final Consumer<CopyOptions> option) {
        BeanUtil.copyMapToBean(src, dest, option);
    }

    /**
     * Copies properties from source bean to a new instance of the destination class.
     * This is a utility method that wraps BeanUtil.copyBeanToNewBean.
     *
     * @param <T> the type of the destination class
     * @param src the source bean object
     * @param destClass the class of the destination bean
     * @return a new instance of the destination class with copied properties
     */
    protected static <T> T copyBeanToNewBean(final Object src, final Class<T> destClass) {
        return BeanUtil.copyBeanToNewBean(src, destClass);
    }

    /**
     * Builds a comprehensive error message from a throwable and its causes.
     * This method traverses the cause chain and concatenates all error messages.
     *
     * @param t the throwable to build message from
     * @return a string containing all error messages in the cause chain
     */
    protected String buildThrowableMessage(final Throwable t) {
        final StringBuilder buf = new StringBuilder(100);
        Throwable current = t;
        while (current != null) {
            buf.append(current.getLocalizedMessage()).append(' ');
            current = current.getCause();
        }
        return buf.toString();
    }

    /**
     * Checks if a checkbox value represents an enabled state.
     * This method considers "on" and "true" (case-insensitive) as enabled values.
     *
     * @param value the checkbox value to check
     * @return true if the value represents an enabled checkbox, false otherwise
     */
    public static boolean isCheckboxEnabled(final String value) {
        if (value == null) {
            return false;
        }
        return Constants.ON.equalsIgnoreCase(value) || Constants.TRUE.equalsIgnoreCase(value);
    }
}
