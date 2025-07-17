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
package org.codelibs.fess.app.web.error;

import org.codelibs.fess.app.web.base.FessSearchAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * Action class for handling general error pages.
 * This action displays error pages when unhandled exceptions occur
 * during search operations or page navigation.
 */
public class ErrorAction extends FessSearchAction {

    /**
     * Default constructor for ErrorAction.
     */
    public ErrorAction() {
        super();
    }

    // ===================================================================================
    //                                                                            Constant
    //

    // ===================================================================================
    //                                                                           Attribute
    //

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the general error page.
     *
     * @param form the error form containing error information
     * @return HTML response for the error page
     */
    @Execute
    public HtmlResponse index(final ErrorForm form) {
        return asHtml(virtualHost(path_Error_ErrorJsp));
    }
}