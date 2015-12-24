/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.mylasta.direction;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;

public interface FessProp {
    public default String getProperty(String key) {
        return ComponentUtil.getCrawlerProperties().getProperty(key);
    }

    public default String getProperty(String key, String defaultValue) {
        return ComponentUtil.getCrawlerProperties().getProperty(key, defaultValue);
    }

    public default String getLdapInitialContextFactory() {
        return getProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    }

    public default String getLdapSecurityAuthentication() {
        return getProperty(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
    }

    public default String getLdapProviderUrl() {
        return getProperty(Constants.LDAP_PROVIDER_URL);
    }

    public default String getLdapSecurityPrincipal(String username) {
        return String.format(getProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY), username);
    }

    public default String getLdapBaseDn() {
        return getProperty(Constants.LDAP_BASE_DN);
    }
}
