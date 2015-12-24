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
package org.codelibs.fess.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.filter.AdLoginInfoFilter;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapManager {
    private static final Logger logger = LoggerFactory.getLogger(AdLoginInfoFilter.class);

    public OptionalEntity<FessUser> login(String username, String password) {
        FessConfig fessConfig = ComponentUtil.getFessConfig();
        String providerUrl = fessConfig.getLdapProviderUrl();

        if (StringUtil.isBlank(providerUrl)) {
            return OptionalEntity.empty();
        }

        DirContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, fessConfig.getLdapInitialContextFactory());
            env.put(Context.SECURITY_AUTHENTICATION, fessConfig.getLdapSecurityAuthentication());
            env.put(Context.PROVIDER_URL, providerUrl);
            env.put(Context.SECURITY_PRINCIPAL, fessConfig.getLdapSecurityPrincipal(username));
            env.put(Context.SECURITY_CREDENTIALS, password);
            ctx = new InitialDirContext(env);
            if (logger.isDebugEnabled()) {
                logger.debug("Logged in.", ctx);
            }
            return OptionalEntity.of(createLdapUser(username, env));
        } catch (NamingException e) {
            logger.debug("Login failed.", e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    // ignore
                }
            }
        }
        return OptionalEntity.empty();
    }

    protected LdapUser createLdapUser(String username, Hashtable<String, String> env) {
        return new LdapUser(env, username);
    }
}
