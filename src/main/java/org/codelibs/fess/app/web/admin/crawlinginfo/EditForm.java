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
package org.codelibs.fess.app.web.admin.crawlinginfo;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class EditForm {

    @ValidateTypeFailure
    public int crudMode;

    @Required
    @Size(max = 1000)
    public String id;

    @Required
    @Size(max = 20)
    public String sessionId;

    @Size(max = 20)
    public String name;

    public String expiredTime;

    @ValidateTypeFailure
    public Long createdTime;

    public void initialize() {

        id = null;
        sessionId = null;
        name = null;
        expiredTime = null;
        createdTime = null;

    }
}