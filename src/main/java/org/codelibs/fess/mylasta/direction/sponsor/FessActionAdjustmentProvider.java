/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.mylasta.direction.sponsor;

import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.path.ActionAdjustmentProvider;
import org.lastaflute.web.path.FormMappingOption;

/**
 * @author jflute
 */
public class FessActionAdjustmentProvider implements ActionAdjustmentProvider {

    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // you can adjust your actions by overriding
    // default methods defined at the interface
    // _/_/_/_/_/_/_/_/_/_/

    @Override
    public FormMappingOption adjustFormMapping() {
        return new FormMappingOption().filterSimpleTextParameter((parameter, meta) -> {
            return parameter.trim();
        });
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{}";
    }
}
