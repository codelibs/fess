/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.app.web.api.FessApiAction;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FessApiAdminAction extends FessApiAction {

    private static final Logger logger = LoggerFactory.getLogger(FessApiAdminAction.class);

    @Override
    protected boolean isAccessAllowed() {
        try {
            return accessTokenService.getPermissions(request).map(permissions -> {
                return fessConfig.isApiAdminAccessAllowed(permissions);
            }).orElse(false);
        } catch (InvalidAccessTokenException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid access token.", e);
            }
            return false;
        }
    }
}
