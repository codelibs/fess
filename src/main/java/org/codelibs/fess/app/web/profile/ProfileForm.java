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
/**
 */
package org.codelibs.fess.app.web.profile;

import jakarta.validation.constraints.NotBlank;

/**
 * Form for user profile operations.
 */
public class ProfileForm {

    /**
     * Default constructor.
     */
    public ProfileForm() {
        // Default constructor
    }

    /** The old password. */
    @NotBlank
    public String oldPassword;

    /** The new password. */
    @NotBlank
    public String newPassword;

    /** The confirm new password. */
    @NotBlank
    public String confirmNewPassword;

    /**
     * Clears security information.
     */
    public void clearSecurityInfo() {
        oldPassword = null;
        newPassword = null;
        confirmNewPassword = null;
    }

}
