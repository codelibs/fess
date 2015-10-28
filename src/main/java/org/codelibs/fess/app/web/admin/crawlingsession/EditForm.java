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

package org.codelibs.fess.app.web.admin.crawlingsession;

import java.io.Serializable;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

/**
 * @author codelibs
 * @author Shunji Makino
 */
public class EditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Digits(integer = 10, fraction = 0)
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

    @Required
    public Long createdTime;

    public void initialize() {

        id = null;
        sessionId = null;
        name = null;
        expiredTime = null;
        createdTime = null;

    }
}