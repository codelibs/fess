/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.wizard;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

public class CrawlingConfigForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Required
    @Size(max = 200)
    public String crawlingConfigName;

    @Required
    @Size(max = 1000)
    public String crawlingConfigPath;

    // TODO
    //    @Min(0)
    //    @Max(Integer.MAX_VALUE)
    public String depth;

    // TODO
    @Size(max = 100)
    //    @Min(0)
    //    @Max(Long.MAX_VALUE)
    public String maxAccessCount;

}
