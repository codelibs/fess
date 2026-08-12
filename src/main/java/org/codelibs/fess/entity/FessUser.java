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
package org.codelibs.fess.entity;

import java.io.Serializable;

/**
 * Interface representing a Fess user with authentication and authorization information.
 * Provides access to user name, roles, groups, and permissions.
 */
public interface FessUser extends Serializable {

    /**
     * Gets the user's display name.
     * @return The user's name.
     */
    String getName();

    /**
     * Gets the user's assigned role names.
     * @return Array of role names.
     */
    String[] getRoleNames();

    /**
     * Gets the user's assigned group names.
     * @return Array of group names.
     */
    String[] getGroupNames();

    /**
     * Gets the user's permissions.
     * @return Array of permission strings.
     */
    String[] getPermissions();

    /**
     * How far the user's group and role permissions have got.
     *
     * <p>Only an authenticator that resolves memberships after the user object is handed out has
     * anything but {@link PermissionState#RESOLVED} to report.
     */
    enum PermissionState {
        /** Group and role permissions are in place. */
        RESOLVED,
        /** They are still being resolved, so the user currently holds fewer than they should. */
        PENDING,
        /**
         * Resolving them failed outright, or completed only in part, so the user holds fewer
         * group and role permissions than they should. Not final: a later resolution -- an
         * authenticator that re-resolves on token renewal, say -- replaces this with
         * {@link #RESOLVED} once it succeeds.
         */
        FAILED
    }

    /**
     * Returns how far the user's group and role permissions have got.
     *
     * <p>Defaults to {@link PermissionState#RESOLVED}: every implementation that reads its groups
     * from an assertion, a token or a directory lookup finishes before the user object exists, so
     * there is never a window in which the user holds fewer permissions than they should.
     *
     * @return The state, never null.
     */
    default PermissionState getPermissionState() {
        return PermissionState.RESOLVED;
    }

    /**
     * Determines if the user's information can be edited.
     * @return True if the user's information is editable, false otherwise.
     */
    default boolean isEditable() {
        return false;
    }

    /**
     * Refreshes the user's information from the underlying data source.
     * @return True if refresh was successful, false otherwise.
     */
    default boolean refresh() {
        return false;
    }
}
