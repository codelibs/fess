/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class CreateForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String[] labelTypeIds;

    @ValidateTypeFailure
    public Integer crudMode;

    @Required
    public String suggestWord;

    public String reading;

    public String targetLabel;

    @Size(max = 4000)
    public String permissions;

    @Required
    @ValidateTypeFailure
    public Float boost;

    @Required
    @Size(max = 1000)
    public String createdBy;

    @Required
    @ValidateTypeFailure
    public Long createdTime;

    public void initialize() {
        boost = 100.0f;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = ComponentUtil.getFessConfig().getSearchDefaultDisplayPermission();
    }
}
