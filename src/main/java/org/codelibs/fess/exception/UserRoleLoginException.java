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
package org.codelibs.fess.exception;

import org.codelibs.fess.app.web.RootAction;

/**
 * Exception thrown when user role authentication fails during login attempts.
 * This exception is used to indicate that a user does not have the required role
 * to access a specific action or resource.
 *
 */
public class UserRoleLoginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The action class that requires specific user roles */
    private final Class<?> actionClass;

    /**
     * Constructs a new UserRoleLoginException with the specified action class.
     *
     * @param actionClass the action class that requires specific user roles
     */
    public UserRoleLoginException(final Class<RootAction> actionClass) {
        this.actionClass = actionClass;
    }

    /**
     * Gets the action class associated with this exception.
     *
     * @return the action class that requires specific user roles
     */
    public Class<?> getActionClass() {
        return actionClass;
    }

    /**
     * Overrides fillInStackTrace to return null for performance optimization.
     * This prevents stack trace generation for this exception type.
     *
     * @return null to skip stack trace generation
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
