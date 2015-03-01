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

package org.codelibs.fess.form.admin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.form.admin.BsRequestHeaderForm;

public class RequestHeaderForm extends BsRequestHeaderForm implements
        Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void initialize() {
        super.initialize();
        createdBy = "system";
        final SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.DEFAULT_DATETIME_FORMAT);
        createdTime = sdf.format(new Date());
    }
}
