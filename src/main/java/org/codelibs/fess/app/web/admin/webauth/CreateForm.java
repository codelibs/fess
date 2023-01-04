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
package org.codelibs.fess.app.web.admin.webauth;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author codelibs
 * @author Shunji Makino
 */
public class CreateForm {

    @ValidateTypeFailure
    public Integer crudMode;

    @Size(max = 100)
    public String hostname;

    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer port;

    @Size(max = 100)
    public String authRealm;

    @Size(max = 10)
    public String protocolScheme;

    @Required
    @Size(max = 100)
    public String username;

    @Size(max = 100)
    public String password;

    @Size(max = 1000)
    public String parameters;

    @Required
    @Size(max = 1000)
    public String webConfigId;

    @Size(max = 1000)
    public String createdBy;

    @ValidateTypeFailure
    public Long createdTime;

    public void initialize() {
        crudMode = CrudMode.CREATE;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}