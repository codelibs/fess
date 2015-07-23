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
package org.codelibs.fess.lasta.core.direction.sponsor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dbflute.util.DfTypeUtil;
import org.lastaflute.web.path.ActionAdjustmentProvider;

/**
 * @author jflute
 */
public class FessActionAdjustmentProvider implements ActionAdjustmentProvider {

    private static final int INDEXED_PROPERTY_SIZE_LIMIT = 200; // hard coding for now

    @Override
    public int provideIndexedPropertySizeLimit() {
        return INDEXED_PROPERTY_SIZE_LIMIT;
    }

    @Override
    public String decodeUrlParameterPropertyValue(Object bean, String name, String value) {
        return null;
    }

    @Override
    public String filterJspPath(String path, ActionMappingWrapper actionMappingWrapper) {
        return null;
    }

    @Override
    public List<String> prepareJspRetryWordList(String requestPath, List<String> wordList) {
        return null;
    }

    @Override
    public boolean isForcedRoutingTarget(HttpServletRequest request, String requestPath) {
        return false;
    }

    @Override
    public boolean isForcedSuppressRedirectWithSlash(HttpServletRequest request, String requestPath, S2ExecuteConfig executeConfig) {
        return false;
    }

    @Override
    public String customizeActionMappingRequestPath(String requestPath) {
        return null;
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{indexedLimit=" + INDEXED_PROPERTY_SIZE_LIMIT + "}";
    }
}
