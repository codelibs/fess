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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.di.util.LdiFileUtil;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;

/**
 * Base action class for admin pages in Fess.
 * <p>
 * This abstract class provides common functionality for all admin actions,
 * including authentication, authorization, and HTML data setup.
 * </p>
 *
 */
public abstract class FessAdminAction extends FessBaseAction {

    /** Constant suffix for view names. */
    public static final String VIEW = "-view";

    /**
     * Default constructor.
     */
    public FessAdminAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Helper for crawling configuration management. */
    @Resource
    protected CrawlingConfigHelper crawlingConfigHelper;

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============

    /**
     * Sets up HTML data for admin pages.
     * <p>
     * This method configures common data needed for admin pages including
     * editable flags, user roles, and forum links.
     * </p>
     *
     * @param runtime the action runtime context
     */
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        systemHelper.setupAdminHtmlData(this, runtime);

        final Boolean editable = getUserBean()
                .map(user -> user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray()) || user.hasRole(getActionRole())).orElse(false);
        runtime.registerData("editable", editable);
        runtime.registerData("editableClass", editable ? StringUtil.EMPTY : "disabled");
        runtime.registerData("fesenType", fessConfig.getFesenType());
        final String forumLink = systemHelper.getForumLink();
        if (StringUtil.isNotBlank(forumLink)) {
            runtime.registerData("forumLink", forumLink);
        }
    }

    /**
     * Get the action role.
     * @return The action role.
     */
    protected abstract String getActionRole();

    /**
     * Writes data to the specified file path.
     *
     * @param path the file path to write to
     * @param data the data to write
     */
    protected void write(final String path, final byte[] data) {
        LdiFileUtil.write(path, data);
    }

    /**
     * Gets the servlet context.
     *
     * @return the servlet context
     */
    protected ServletContext getServletContext() {
        return LaServletContextUtil.getServletContext();
    }

    // ===================================================================================
    //                                                                            Document
    //                                                                            ========
    /**
     * {@inheritDoc} <br>
     * Application Origin Methods:
     * <pre>
     * <span style="font-size: 130%; color: #553000">[Small Helper]</span>
     * o saveInfo() <span style="color: #3F7E5E">// save messages to session</span>
     * o write() <span style="color: #3F7E5E">// write text to specified file</span>
     * o copyBeanToBean() <span style="color: #3F7E5E">// copy bean to bean by BeanUtil</span>
     * o getServletContext() <span style="color: #3F7E5E">// get servlet context</span>
     * </pre>
     */
    @Override
    public void document1_CallableSuperMethod() {
        super.document1_CallableSuperMethod();
    }

    // ===================================================================================
    //                                                                           User Info
    //                                                                           =========
    /**
     * Gets the login manager for this admin action.
     *
     * @return the login manager wrapped in OptionalThing
     */
    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.of(fessLoginAssist);
    }

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    /**
     * Handles the prologue phase of action execution.
     * <p>
     * This method catches UserRoleLoginException and redirects to the
     * appropriate action class.
     * </p>
     *
     * @param runtime the action runtime context
     * @return the action response, or redirect response if login exception occurs
     */
    @Override
    public ActionResponse godHandPrologue(final ActionRuntime runtime) {
        try {
            return super.godHandPrologue(runtime);
        } catch (final UserRoleLoginException e) {
            return redirect(e.getActionClass());
        }
    }

    /**
     * Hook method called before action execution.
     * <p>
     * This method logs user access activity for the current request.
     * </p>
     *
     * @param runtime the action runtime context
     * @return the action response from the parent hook
     */
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) {
        final String requestPath = runtime.getRequestPath();
        final String executeName = runtime.getExecuteMethod().getName();
        activityHelper.access(getUserBean(), requestPath, executeName);
        return super.hookBefore(runtime);
    }

    /**
     * Hook method called after action execution completes.
     * <p>
     * This method performs cleanup operations by calling the parent hook.
     * </p>
     *
     * @param runtime the action runtime context
     */
    @Override
    public void hookFinally(final ActionRuntime runtime) {
        super.hookFinally(runtime);
    }

}
