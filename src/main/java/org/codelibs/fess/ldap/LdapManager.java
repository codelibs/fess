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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapManager {
    private static final Logger logger = LoggerFactory.getLogger(LdapManager.class);

    public OptionalEntity<FessUser> login(final String username, final String password) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String providerUrl = fessConfig.getLdapProviderUrl();

        if (StringUtil.isBlank(providerUrl)) {
            return OptionalEntity.empty();
        }

        DirContext ctx = null;
        try {
            final Hashtable<String, String> env = new Hashtable<>();
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
        } catch (final NamingException e) {
            logger.debug("Login failed.", e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (final NamingException e) {
                    // ignore
                }
            }
        }
        return OptionalEntity.empty();
    }

    protected LdapUser createLdapUser(final String username, final Hashtable<String, String> env) {
        return new LdapUser(env, username);
    }

    public String[] getRoles(final LdapUser ldapUser, final String bindDn, final String accountFilter) {
        final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<String> roleList = new ArrayList<String>();

        if (fessConfig.isSmbRoleAsUser()) {
            roleList.add(sambaHelper.getRoleByUser(ldapUser.getName()));
        }

        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(ldapUser.getEnvironment());

            // LDAP: cn=%s
            // AD: (&(objectClass=user)(sAMAccountName=%s))
            final String filter = String.format(accountFilter, ldapUser.getName());
            final SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //search
            final NamingEnumeration<SearchResult> rslt = ctx.search(bindDn, filter, controls);
            while (rslt.hasMoreElements()) {
                final SearchResult srcrslt = rslt.next();
                final Attributes attrs = srcrslt.getAttributes();

                //get group attr
                final Attribute attr = attrs.get("memberOf");
                if (attr == null) {
                    continue;
                }

                for (int i = 0; i < attr.size(); i++) {
                    final Object attrValue = attr.get(i);
                    if (attrValue != null) {
                        // TODO replace with regexp
                        String strTmp = attrValue.toString();

                        int strStart = 0;
                        int strEnd = 0;

                        strStart = strTmp.indexOf("CN=");
                        strStart += "CN=".length();
                        strEnd = strTmp.indexOf(',');

                        strTmp = strTmp.substring(strStart, strEnd);

                        if (fessConfig.isSmbRoleAsGroup()) {
                            roleList.add(sambaHelper.getRoleByDomainGroup(strTmp));
                        } else {
                            roleList.add(strTmp);
                        }
                    }
                }
            }

        } catch (final Exception e) {
            logger.warn("Failed to resolve roles: " + ldapUser.getName(), e);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (final NamingException e) {
                    // ignored
                }
            }
        }

        return roleList.toArray(new String[roleList.size()]);
    }
}
