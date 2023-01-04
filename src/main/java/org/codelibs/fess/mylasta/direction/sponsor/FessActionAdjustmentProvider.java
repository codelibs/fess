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
package org.codelibs.fess.mylasta.direction.sponsor;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;
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
        return new FormMappingOption()
                .filterSimpleTextParameter((parameter, meta) -> parameter.trim().replace("\r\n", "\n").replace('\r', '\n'));
    }

    @Override
    public String customizeActionMappingRequestPath(final String requestPath) {
        if (StringUtil.isBlank(requestPath)) {
            return null;
        }
        final String virtualHostKey = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        if (StringUtil.isBlank(virtualHostKey)) {
            return null;
        }
        final String prefix = "/" + virtualHostKey;
        if (requestPath.startsWith(prefix)) {
            return requestPath.substring(prefix.length());
        }
        return null;
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{}";
    }
}
