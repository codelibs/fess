/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.db.exentity;

import java.util.Map;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.db.bsentity.BsWebAuthentication;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.client.http.Authentication;
import org.codelibs.robot.client.http.impl.AuthenticationImpl;
import org.codelibs.robot.client.http.ntlm.JcifsEngine;

/**
 * The entity of WEB_AUTHENTICATION.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class WebAuthentication extends BsWebAuthentication {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public Authentication getAuthentication() {
        return new AuthenticationImpl(getAuthScope(), getCredentials(),
                getAuthScheme());
    }

    private AuthScheme getAuthScheme() {
        if (Constants.BASIC.equals(getProtocolScheme())) {
            return new BasicScheme();
        } else if (Constants.DIGEST.equals(getProtocolScheme())) {
            return new DigestScheme();
        } else if (Constants.NTLM.equals(getProtocolScheme())) {
            return new NTLMScheme(new JcifsEngine());
        }
        return null;
    }

    private AuthScope getAuthScope() {
        if (StringUtil.isBlank(getHostname())) {
            return AuthScope.ANY;
        }

        int p;
        if (getPort() == null) {
            p = AuthScope.ANY_PORT;
        } else {
            p = getPort().intValue();
        }

        String r = getAuthRealm();
        if (StringUtil.isBlank(r)) {
            r = AuthScope.ANY_REALM;
        }

        String s = getProtocolScheme();
        if (StringUtil.isBlank(s) || Constants.NTLM.equals(s)) {
            s = AuthScope.ANY_SCHEME;
        }

        return new AuthScope(getHostname(), p, r, s);
    }

    private Credentials getCredentials() {
        if (StringUtil.isEmpty(getUsername())) {
            throw new RobotSystemException("username is empty.");
        }

        if (Constants.NTLM.equals(getProtocolScheme())) {
            final Map<String, String> parameterMap = ParameterUtil
                    .parse(getParameters());
            final String workstation = parameterMap.get("workstation");
            final String domain = parameterMap.get("domain");
            return new NTCredentials(getUsername(), getPassword(),
                    workstation == null ? StringUtil.EMPTY : workstation,
                    domain == null ? StringUtil.EMPTY : domain);
        }

        return new UsernamePasswordCredentials(getUsername(),
                getPassword() == null ? StringUtil.EMPTY : getPassword());
    }

}
