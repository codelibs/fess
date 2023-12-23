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
package org.codelibs.fess.app.web.admin.elevateword;

import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class CreateForm {

    public String[] labelTypeIds;

    @ValidateTypeFailure
    public Integer crudMode;

    @Required
    public String suggestWord;

    public String reading;

    public String targetLabel;

    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    @Required
    @ValidateTypeFailure
    public Float boost;

    @Size(max = 1000)
    public String createdBy;

    @ValidateTypeFailure
    public Long createdTime;

    public void initialize() {
        boost = 100.0f;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = ComponentUtil.getFessConfig().getSearchDefaultDisplayPermission();
    }
}
