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
package org.codelibs.fess.auth.chain;

import org.codelibs.fess.opensearch.user.exentity.User;

/**
 * Interface for authentication chain operations.
 * Provides methods for user management and authentication operations.
 */
public interface AuthenticationChain {

    /**
     * Updates an existing user in the authentication chain.
     * @param user The user to update.
     */
    void update(User user);

    /**
     * Deletes a user from the authentication chain.
     * @param user The user to delete.
     */
    void delete(User user);

    /**
     * Changes the password for the specified user.
     * @param username The username for which to change the password.
     * @param password The new password.
     * @return True if the password was successfully changed, false otherwise.
     */
    boolean changePassword(String username, String password);

    /**
     * Loads user information from the authentication chain.
     * @param user The user template containing search criteria.
     * @return The loaded user, or null if not found.
     */
    User load(User user);

}
