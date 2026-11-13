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

import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form class for resetting (deleting) one passkey of a user in the admin interface.
 */
public class DeletePasskeyForm {

    /**
     * Creates a new DeletePasskeyForm instance.
     */
    public DeletePasskeyForm() {
    }

    /**
     * The unique identifier of the user the passkey belongs to.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The credential ID of the passkey (unpadded Base64url, at most 1023 bytes).
     */
    @Required
    @Pattern(regexp = "[A-Za-z0-9_-]{1,1364}")
    public String credentialId;

}
