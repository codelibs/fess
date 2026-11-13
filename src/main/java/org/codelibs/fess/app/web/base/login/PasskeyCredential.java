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
package org.codelibs.fess.app.web.base.login;

import org.lastaflute.web.login.credential.LoginCredential;

/**
 * The credential of a user whose passkey assertion has been verified.
 *
 * <p>It carries no password: {@code FessLoginAssist} resolves it to the local user of that name and logs
 * that user in. Creating one is therefore equivalent to proving the user's identity, and the only way to
 * create one is {@link #verified(String)}.</p>
 */
public class PasskeyCredential implements LoginCredential, FessCredential {

    private final String userName;

    private PasskeyCredential(final String userName) {
        this.userName = userName;
    }

    /**
     * Creates the credential of a verified passkey login.
     *
     * <p>This may be called only with the name returned by {@code PasskeyService#verifyLogin}, from the
     * handler that has just called it. Any other caller logs a user in without a password.</p>
     *
     * @param userName the local user name {@code PasskeyService#verifyLogin} returned
     * @return the credential
     */
    public static PasskeyCredential verified(final String userName) {
        return new PasskeyCredential(userName);
    }

    @Override
    public String getUserId() {
        return userName;
    }

    @Override
    public String toString() {
        return "PasskeyCredential:{" + userName + "}";
    }
}
