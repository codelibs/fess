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
package org.codelibs.fess.app.web.admin.dashboard;

import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.api.engine.SearchEngineApiManager;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for Dashboard.
 *
 */
public class AdminDashboardAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminDashboardAction() {
        // nothing
    }

    /** The role for this action. */
    public static final String ROLE = "admin-dashboard";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** The search engine API manager. */
    @Resource
    protected SearchEngineApiManager searchEngineApiManager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDashboard()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                              Index
    //                                                                      ==============
    /**
     * Show the index page.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse index() {
        searchEngineApiManager.saveToken();
        return asHtml(path_AdminDashboard_AdminDashboardJsp).renderWith(data -> {
            RenderDataUtil.register(data, "serverPath", searchEngineApiManager.getServerPath());
        });
    }

}
