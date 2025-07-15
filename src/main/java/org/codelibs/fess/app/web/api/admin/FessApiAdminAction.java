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
package org.codelibs.fess.app.web.api.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.web.api.FessApiAction;
import org.codelibs.fess.exception.InvalidAccessTokenException;

/**
 * Abstract base class for admin API actions in Fess.
 * This class extends FessApiAction to provide admin-specific functionality
 * including enhanced access control for administrative operations.
 *
 * <p>Admin API actions require special permissions and access tokens
 * that are validated against the admin role configuration.</p>
 */
public abstract class FessApiAdminAction extends FessApiAction {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(FessApiAdminAction.class);

    /**
     * Default constructor.
     */
    public FessApiAdminAction() {
        // Default constructor
    }

    /**
     * Determines whether the current request is authorized to access admin API endpoints.
     * This method validates the access token and checks if the associated permissions
     * allow admin access according to the Fess configuration.
     *
     * @return true if admin access is allowed, false otherwise
     */
    @Override
    protected boolean isAccessAllowed() {
        try {
            return accessTokenService.getPermissions(request).map(permissions -> fessConfig.isApiAdminAccessAllowed(permissions))
                    .orElse(false);
        } catch (final InvalidAccessTokenException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid access token.", e);
            }
            return false;
        }
    }
}
