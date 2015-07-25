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

package org.codelibs.fess.app.web.admin;

import java.io.Serializable;

public class WizardForm implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Maxbytelength(maxbytelength = 200, target = "crawlingConfig,crawlingConfigNext")
    public String crawlingConfigName;

    //@Required(target = "crawlingConfig,crawlingConfigNext")
    //@Maxbytelength(maxbytelength = 1000)
    public String crawlingConfigPath;

    //@LongRange(min = 0, max = Long.MAX_VALUE, target = "crawlingConfig,crawlingConfigNext")
    public String maxAccessCount;

    //@IntRange(min = 0, max = Integer.MAX_VALUE, target = "crawlingConfig,crawlingConfigNext")
    public String depth;

    public WizardForm() {
        maxAccessCount = "10000";
        depth = null;
    }
}
