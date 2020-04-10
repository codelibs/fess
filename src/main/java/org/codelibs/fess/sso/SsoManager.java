/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
package org.codelibs.fess.sso;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

public class SsoManager {
    private static final Logger logger = LogManager.getLogger(SsoManager.class);

    protected static final String SSO_TYPE = "sso.type";

    protected static final String NONE = "none";

    protected final List<SsoAuthenticator> authenticatorList = new ArrayList<>();

    public boolean available() {
        final String ssoType = getSsoType();
        if (logger.isDebugEnabled()) {
            logger.debug("sso.type: {}", ssoType);
        }
        return !NONE.equals(ssoType);
    }

    public LoginCredential getLoginCredential() {
        if (available()) {
            final SsoAuthenticator authenticator = ComponentUtil.getComponent(getSsoType() + "Authenticator");
            return authenticator.getLoginCredential();
        }
        return null;
    }

    protected String getSsoType() {
        return ComponentUtil.getFessConfig().getSystemProperty(SSO_TYPE, NONE);
    }

    public SsoAuthenticator[] getAuthenticators() {
        return authenticatorList.toArray(new SsoAuthenticator[authenticatorList.size()]);
    }

    public void register(final SsoAuthenticator authenticator) {
        if (logger.isInfoEnabled()) {
            logger.info("Load {}", authenticator.getClass().getSimpleName());
        }
        authenticatorList.add(authenticator);
    }
}
