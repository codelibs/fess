/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;

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
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.getLoginCredential();
            }
        }
        return null;
    }

    public ActionResponse getResponse(final SsoResponseType responseType) {
        if (available()) {
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.getResponse(responseType);
            }
        }
        return null;
    }

    public String logout(final FessUserBean user) {
        if (available()) {
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.logout(user);
            }
        }
        return null;
    }

    protected SsoAuthenticator getAuthenticator() {
        final String name = getSsoType() + "Authenticator";
        if (ComponentUtil.hasComponent(name)) {
            return ComponentUtil.getComponent(name);
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
