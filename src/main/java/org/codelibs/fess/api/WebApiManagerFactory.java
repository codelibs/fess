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
package org.codelibs.fess.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class WebApiManagerFactory {

    protected WebApiManager[] webApiManagers = {};

    public void add(final WebApiManager webApiManager) {
        final List<WebApiManager> list = new ArrayList<>();
        Collections.addAll(list, webApiManagers);
        list.add(webApiManager);
        webApiManagers = list.toArray(new WebApiManager[list.size()]);
    }

    public WebApiManager get(final HttpServletRequest request) {
        for (final WebApiManager webApiManager : webApiManagers) {
            if (webApiManager.matches(request)) {
                return webApiManager;
            }
        }
        return null;
    }

}
