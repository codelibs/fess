/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

public interface LoginCredential {
    void validate();

    String getId();

    Object getResource();

    public default void assertLoginAccountRequired(final String account) {
        if (account == null || account.length() == 0) {
            final String msg = "The argument 'account' should not be null for login.";
            throw new IllegalArgumentException(msg);
        }
    }

    public default void assertLoginPasswordRequired(final String password) {
        if (password == null || password.length() == 0) {
            final String msg = "The argument 'password' should not be null for login.";
            throw new IllegalArgumentException(msg);
        }
    }
}