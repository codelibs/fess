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
package org.codelibs.fess.app.web.help;

import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * Action class for handling help page requests in the Fess search application.
 * This class extends FessSearchAction to provide help functionality including
 * authentication checks and form parameter setup for the help interface.
 */
public class HelpAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //

    // ===================================================================================
    //                                                                           Attribute
    //

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========

    /**
     * Default constructor for HelpAction.
     */
    public HelpAction() {
        super();
    }

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Handles the help page request and renders the help interface.
     * This method performs authentication checks and sets up the necessary
     * form parameters and rendering data for the help page display.
     *
     * @return HtmlResponse containing the rendered help page or redirect to login if authentication is required
     */
    @Execute
    public HtmlResponse index() {
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        return asHtml(virtualHost(path_HelpJsp)).useForm(SearchForm.class, op -> {
            op.setup(form -> {
                buildFormParams(form);
            });
        }).renderWith(data -> {
            buildInitParams();
            RenderDataUtil.register(data, "helpPage", viewHelper.getPagePath("common/help"));
        });
    }

}