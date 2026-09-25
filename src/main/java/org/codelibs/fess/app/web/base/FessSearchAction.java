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

import org.codelibs.fess.app.web.sso.SsoAction;
import org.codelibs.fess.helper.OsddHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Base class of the non-admin actions Fess still serves itself: login, logout, SSO, the document
 * redirect, thumbnails and the OpenSearch description. The search screens are served by the
 * static theme; this class only holds what these actions share, such as the login requirement.
 */
public abstract class FessSearchAction extends FessBaseAction {

    /**
     * Default constructor.
     */
    public FessSearchAction() {
    }

    /** Helper for performing search operations and managing search requests. */
    @Resource
    protected SearchHelper searchHelper;

    /** Manager for handling thumbnail generation and display. */
    @Resource
    protected ThumbnailManager thumbnailManager;

    /** Configuration for query field mappings and processing. */
    @Resource
    protected QueryFieldConfig queryFieldConfig;

    /** Helper for managing user information and authentication. */
    @Resource
    protected UserInfoHelper userInfoHelper;

    /** Helper for OpenSearch Description Document (OSDD) functionality. */
    @Resource
    protected OsddHelper osddHelper;

    /** The HTTP servlet request object for the current request. */
    @Resource
    protected HttpServletRequest request;

    /** Flag indicating whether thumbnail generation is enabled. */
    protected boolean thumbnailSupport;

    /**
     * Hook method called before action execution. Reads the thumbnail flag.
     *
     * @param runtime the action runtime context
     * @return the action response, or null to continue with normal processing
     */
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) { // application may override
        thumbnailSupport = fessConfig.isThumbnailEnabled();
        return super.hookBefore(runtime);
    }

    /**
     * Returns the login manager for this action. These actions do not require
     * a login manager as they handle authentication differently.
     *
     * @return an empty OptionalThing as these actions don't use login managers
     */
    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty();
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                           =========

    /**
     * Checks if login is required for the current request based on configuration
     * and user authentication status.
     *
     * @return true if login is required, false otherwise
     */
    protected boolean isLoginRequired() {
        if (fessConfig.isLoginRequired() && !fessLoginAssist.getSavedUserBean().isPresent()) {
            return true;
        }
        return false;
    }

    /**
     * Redirects the user to the login page after storing current search parameters
     * for restoration after successful authentication.
     *
     * @return HTML response that redirects to the login page
     */
    protected HtmlResponse redirectToLogin() {
        searchHelper.storeSearchParameters();
        return systemHelper.getRedirectResponseToLogin(redirect(SsoAction.class));
    }
}
