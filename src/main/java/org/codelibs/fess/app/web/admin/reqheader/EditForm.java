/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.reqheader;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author Keiichi Watanabe
 */
public class EditForm extends CreateForm {

    @Required
    @Size(max = 1000)
    public String id;

    @Size(max = 1000)
    public String updatedBy;

    @ValidateTypeFailure
    public Long updatedTime;

    @Required
    @ValidateTypeFailure
    public Integer versionNo;

}
