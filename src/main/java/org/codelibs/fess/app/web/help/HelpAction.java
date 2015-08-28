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

package org.codelibs.fess.app.web.help;

import org.codelibs.fess.app.web.base.FessSearchAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

public class HelpAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //     

    // ===================================================================================
    //                                                                           Attribute
    //     

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse help(HelpForm form) {
        return asHtml(path_HelpJsp).renderWith(data -> {
            buildLabelParams(form.fields);
            buildInitParams();
        });
    }

}