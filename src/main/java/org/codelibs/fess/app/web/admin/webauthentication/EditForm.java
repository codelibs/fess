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

package org.codelibs.fess.app.web.admin.webauthentication;

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

/**
 * @author codelibs
 * @author Shunji Makino
 */
public class EditForm extends CreateForm {

    private static final long serialVersionUID = 1L;

    @Required
    @Size(max = 1000)
    public String id;

    @Size(max = 255)
    public String updatedBy;

    public Long updatedTime;

    @Required
    public Integer versionNo;

}