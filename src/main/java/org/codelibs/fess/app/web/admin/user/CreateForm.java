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
package org.codelibs.fess.app.web.admin.user;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.app.web.CrudMode;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * The create form for User.
 *
 */
public class CreateForm {

    /**
     * Default constructor.
     */
    public CreateForm() {
        // Empty constructor
    }

    /**
     * The CRUD mode for the form.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The username of the user.
     */
    @Required
    @Size(max = 100)
    public String name;

    /**
     * The password for the user.
     */
    @Size(max = 100)
    public String password;

    /**
     * The password confirmation field.
     */
    @Size(max = 100)
    public String confirmPassword;

    /**
     * The attributes map for the user.
     */
    public Map<String, String> attributes = new HashMap<>();

    /**
     * The roles assigned to the user.
     */
    public String[] roles;

    /**
     * The groups assigned to the user.
     */
    public String[] groups;

    /**
     * Initializes the form with default values for creating a new user.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
    }
}
