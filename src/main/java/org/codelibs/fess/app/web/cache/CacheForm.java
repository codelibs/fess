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
package org.codelibs.fess.app.web.cache;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

public class CacheForm {

    @Required
    @Size(max = 100)
    public String docId;

    public String[] hq;

    // for error page

    public String q;

    public String num;

    public String sort;

    public String lang;

    public Map<String, String[]> fields = new HashMap<>();
}
