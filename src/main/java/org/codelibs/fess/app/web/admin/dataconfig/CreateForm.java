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
package org.codelibs.fess.app.web.admin.dataconfig;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class CreateForm {

    @ValidateTypeFailure
    public Integer crudMode;

    @Required
    @Size(max = 200)
    public String name;

    @Size(max = 1000)
    public String description;

    @Required
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerName;

    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerParameter;

    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerScript;

    @Required
    @ValidateTypeFailure
    public Float boost;

    @Required
    @Size(max = 5)
    public String available;

    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    @CustomSize(maxKey = "form.admin.max.input.size")
    public String virtualHosts;

    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    @Size(max = 1000)
    public String createdBy;

    @ValidateTypeFailure
    public Long createdTime;

    public void initialize() {
        crudMode = CrudMode.CREATE;
        boost = 1.0f;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = ComponentUtil.getFessConfig().getSearchDefaultDisplayPermission();
    }
}
