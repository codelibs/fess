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

/**
 * Thrown when a logged-in user opens an administration page their roles do not allow.
 * {@code FessAdminAction#godHandPrologue} records the denial and redirects the user to the
 * application root, where the static theme serves the search page.
 */
public class UserRoleLoginException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UserRoleLoginException.
     */
    public UserRoleLoginException() {
        // nothing
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
