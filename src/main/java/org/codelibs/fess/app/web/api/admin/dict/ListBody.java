/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.dict;

import java.util.Date;

import org.codelibs.fess.app.web.admin.dict.ListForm;

/**
 * The request body for listing dictionaries.
 */
public class ListBody extends ListForm {
    /**
     * Default constructor.
     */
    public ListBody() {
        // do nothing
    }

    /** The ID of the dictionary. */
    public String id;
    /** The type of the dictionary. */
    public String type;
    /** The path of the dictionary. */
    public String path;
    /** The timestamp of the dictionary. */
    public Date timestamp;
}
