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
package org.codelibs.fess.app.web;

/**
 * Constants for CRUD operation modes.
 */
public class CrudMode {
    /**
     * Mode for listing records.
     */
    public static final int LIST = 0;

    /**
     * Mode for creating a new record.
     */
    public static final int CREATE = 1;

    /**
     * Mode for editing an existing record.
     */
    public static final int EDIT = 2;

    /**
     * Mode for deleting a record.
     */
    public static final int DELETE = 3;

    /**
     * Mode for viewing record details.
     */
    public static final int DETAILS = 4;

    /**
     * Protected constructor to prevent instantiation.
     */
    protected CrudMode() {
        // nothing
    }
}
