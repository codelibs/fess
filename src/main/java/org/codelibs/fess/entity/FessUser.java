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
