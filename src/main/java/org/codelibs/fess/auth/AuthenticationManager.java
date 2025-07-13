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
package org.codelibs.fess.auth;

import static org.codelibs.core.stream.StreamUtil.stream;

import org.apache.commons.lang3.ArrayUtils;
import org.codelibs.core.stream.StreamUtil.StreamOf;
import org.codelibs.fess.auth.chain.AuthenticationChain;
import org.codelibs.fess.opensearch.user.exentity.User;

/**
 * Manages authentication operations across multiple authentication chains.
 * This class coordinates user operations across different authentication providers.
 */
public class AuthenticationManager {
    /** Array of authentication chains to process user operations. */
    protected AuthenticationChain[] chains = {};

    /**
     * Default constructor for AuthenticationManager.
     */
    public AuthenticationManager() {
        // Default constructor
    }

    /**
     * Inserts a new user across all authentication chains.
     * @param user The user to insert.
     */
    public void insert(final User user) {
        chains().of(stream -> stream.forEach(c -> c.update(user)));
    }

    /**
     * Changes the password for a user across all authentication chains.
     * @param username The username for which to change the password.
     * @param password The new password.
     * @return True if the password was successfully changed in all chains, false otherwise.
     */
    public boolean changePassword(final String username, final String password) {
        return chains().get(stream -> stream.allMatch(c -> c.changePassword(username, password)));
    }

    /**
     * Deletes a user from all authentication chains.
     * @param user The user to delete.
     */
    public void delete(final User user) {
        chains().of(stream -> stream.forEach(c -> c.delete(user)));
    }

    /**
     * Loads user information by processing through all authentication chains.
     * @param user The user template containing search criteria.
     * @return The loaded and enriched user information.
     */
    public User load(final User user) {
        User u = user;
        for (final AuthenticationChain chain : chains) {
            u = chain.load(u);
        }
        return u;
    }

    /**
     * Adds an authentication chain to the manager.
     * @param chain The authentication chain to add.
     */
    public void addChain(final AuthenticationChain chain) {
        chains = ArrayUtils.addAll(chains, chain);
    }

    /**
     * Returns a stream of authentication chains.
     * @return A stream wrapper of the authentication chains.
     */
    protected StreamOf<AuthenticationChain> chains() {
        return stream(chains);
    }

}
