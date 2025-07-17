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
 * SPNEGO authentication credential implementation.
 *
 * This class represents login credentials obtained through SPNEGO (Security Provider
 * Negotiation Protocol) authentication. It contains the username extracted from the
 * SPNEGO authentication process, typically from a Kerberos ticket.
 */
public class SpnegoCredential implements LoginCredential, FessCredential {

    /** The username extracted from SPNEGO authentication. */
    private final String username;

    /**
     * Constructs a new SpnegoCredential with the specified username.
     *
     * @param username The username obtained from SPNEGO authentication
     */
    public SpnegoCredential(final String username) {
        this.username = username;
    }

    /**
     * Gets the user identifier from this credential.
     *
     * @return The username from SPNEGO authentication
     */
    @Override
    public String getUserId() {
        return username;
    }

    /**
     * Returns a string representation of this credential.
     *
     * @return A string representation containing the username in braces
     */
    @Override
    public String toString() {
        return "{" + username + "}";
    }

}