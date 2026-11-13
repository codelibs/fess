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
package org.codelibs.fess.app.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.LogUtil;

import jakarta.annotation.Resource;

/**
 * Service for passkey (WebAuthn) login of local users.
 */
public class PasskeyService {

    private static final Logger logger = LogManager.getLogger(PasskeyService.class);

    /**
     * The Fess configuration.
     */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Constructor.
     */
    public PasskeyService() {
        // Default constructor
    }

    /**
     * Returns whether the passkey feature is available. It is when passkey.enabled is set and both
     * passkey.rp.id and passkey.origins are not blank.
     *
     * @return true if passkeys can be registered and used to log in
     */
    public boolean isEnabled() {
        if (!fessConfig.isPasskeyEnabled()) {
            return false;
        }
        if (StringUtil.isBlank(fessConfig.getPasskeyRpId()) || StringUtil.isBlank(fessConfig.getPasskeyOrigins())) {
            LogUtil.warnOnce(logger, "misconfigured",
                    "passkey.enabled is true but passkey.rp.id or passkey.origins is empty, so passkeys are disabled.");
            return false;
        }
        return true;
    }
}
