/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
 * @author Keiichi Watanabe
 */
package org.codelibs.fess.app.web.profile;

import javax.validation.constraints.NotBlank;

public class ProfileForm {

    @NotBlank
    public String oldPassword;

    @NotBlank
    public String newPassword;

    @NotBlank
    public String confirmNewPassword;

    public void clearSecurityInfo() {
        oldPassword = null;
        newPassword = null;
        confirmNewPassword = null;
    }

}
