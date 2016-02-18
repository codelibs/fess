/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.es.user.exentity.Group;
import org.codelibs.fess.es.user.exentity.Role;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.exception.LdapOperationException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.dbflute.optional.OptionalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapManager {
    private static final Logger logger = LoggerFactory.getLogger(LdapManager.class);

    protected ThreadLocal<DirContextHolder> contextLocal = new ThreadLocal<>();

    public OptionalEntity<FessUser> login(final String username, final String password) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (StringUtil.isBlank(fessConfig.getLdapProviderUrl())) {
            return OptionalEntity.empty();
        }

        DirContext ctx = null;
        try {
            final Hashtable<String, String> env =
                    createEnvironment(fessConfig.getLdapInitialContextFactory(), fessConfig.getLdapSecurityAuthentication(),
                            fessConfig.getLdapProviderUrl(), fessConfig.getLdapSecurityPrincipal(username), password);
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

    protected Hashtable<String, String> createEnvironment(final String initialContextFactory, final String securityAuthentication,
            String providerUrl, String principal, String credntials) {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credntials);
        return env;
    }

    protected LdapUser createLdapUser(final String username, final Hashtable<String, String> env) {
        return new LdapUser(env, username);
    }

    public String[] getRoles(final LdapUser ldapUser, final String bindDn, final String accountFilter) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<String> roleList = new ArrayList<String>();

        if (fessConfig.isLdapRoleSearchUserEnabled()) {
            roleList.add(systemHelper.getSearchRoleByUser(ldapUser.getName()));
        }

        // LDAP: cn=%s
        // AD: (&(objectClass=user)(sAMAccountName=%s))
        final String filter = String.format(accountFilter, ldapUser.getName());
        search(bindDn, filter, new String[] { fessConfig.getLdapMemberofAttribute() }, result -> {
            processSearchRoles(result, (entryDn, name) -> {
                final boolean isRole = entryDn.toLowerCase(Locale.ROOT).indexOf("ou=role") != -1;
                if (isRole) {
                    if (fessConfig.isLdapRoleSearchRoleEnabled()) {
                        roleList.add(systemHelper.getSearchRoleByRole(name));
                    }
                } else if (fessConfig.isLdapRoleSearchGroupEnabled()) {
                    roleList.add(systemHelper.getSearchRoleByGroup(name));
                }
            });
        });

        return roleList.toArray(new String[roleList.size()]);
    }

    protected void processSearchRoles(NamingEnumeration<SearchResult> result, BiConsumer<String, String> consumer) throws NamingException {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        while (result.hasMoreElements()) {
            final SearchResult srcrslt = result.next();
            final Attributes attrs = srcrslt.getAttributes();

            //get group attr
            final Attribute attr = attrs.get(fessConfig.getLdapMemberofAttribute());
            if (attr == null) {
                continue;
            }

            for (int i = 0; i < attr.size(); i++) {
                final Object attrValue = attr.get(i);
                if (attrValue != null) {
                    final String entryDn = attrValue.toString();

                    int start = 0;
                    int end = 0;

                    start = entryDn.indexOf("CN=");
                    if (start < 0) {
                        start = entryDn.indexOf("cn=");
                    }
                    if (start == -1) {
                        continue;
                    }
                    start += 3;
                    end = entryDn.indexOf(',');

                    String name;
                    if (end == -1) {
                        name = entryDn.substring(start);
                    } else {
                        name = entryDn.substring(start, end);
                    }

                    consumer.accept(entryDn, name);
                }
            }
        }
    }

    public void insert(User user) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(user.getName());
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()),
                new String[] { fessConfig.getLdapMemberofAttribute() }, result -> {
                    if (result.hasMore()) {
                        if (user.getOriginalPassword() != null) {
                            List<ModificationItem> modifyList = new ArrayList<>();
                            modifyReplaceEntry(modifyList, "userPassword", user.getOriginalPassword());
                            modify(userDN, modifyList);
                        }

                        List<String> oldGroupList = new ArrayList<>();
                        List<String> oldRoleList = new ArrayList<>();
                        String lowerGroupDn = fessConfig.getLdapAdminGroupBaseDn().toLowerCase(Locale.ROOT);
                        String lowerRoleDn = fessConfig.getLdapAdminRoleBaseDn().toLowerCase(Locale.ROOT);
                        processSearchRoles(result, (entryDn, name) -> {
                            String lowerEntryDn = entryDn.toLowerCase(Locale.ROOT);
                            if (lowerEntryDn.indexOf(lowerGroupDn) != -1) {
                                oldGroupList.add(name);
                            } else if (lowerEntryDn.indexOf(lowerRoleDn) != -1) {
                                oldRoleList.add(name);
                            }
                        });

                        List<String> newGroupList = StreamUtil.of(user.getGroupNames()).collect(Collectors.toList());
                        StreamUtil.of(user.getGroupNames()).forEach(name -> {
                            if (oldGroupList.contains(name)) {
                                oldGroupList.remove(name);
                                newGroupList.remove(name);
                            }
                        });
                        oldGroupList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, subResult -> {
                                if (subResult.hasMore()) {
                                    List<ModificationItem> modifyList = new ArrayList<>();
                                    modifyDeleteEntry(modifyList, "member", userDN);
                                    modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList);
                                }
                            });
                        });
                        newGroupList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, subResult -> {
                                if (!subResult.hasMore()) {
                                    Group group = new Group();
                                    group.setName(name);
                                    insert(group);
                                }
                                List<ModificationItem> modifyList = new ArrayList<>();
                                modifyAddEntry(modifyList, "member", userDN);
                                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList);
                            });
                        });

                        List<String> newRoleList = StreamUtil.of(user.getRoleNames()).collect(Collectors.toList());
                        StreamUtil.of(user.getRoleNames()).forEach(name -> {
                            if (oldRoleList.contains(name)) {
                                oldRoleList.remove(name);
                                newRoleList.remove(name);
                            }
                        });
                        oldRoleList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, subResult -> {
                                if (subResult.hasMore()) {
                                    List<ModificationItem> modifyList = new ArrayList<>();
                                    modifyDeleteEntry(modifyList, "member", userDN);
                                    modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList);
                                }
                            });
                        });
                        newRoleList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, subResult -> {
                                if (!subResult.hasMore()) {
                                    Role role = new Role();
                                    role.setName(name);
                                    insert(role);
                                }
                                List<ModificationItem> modifyList = new ArrayList<>();
                                modifyAddEntry(modifyList, "member", userDN);
                                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList);
                            });
                        });
                    } else {
                        BasicAttributes entry = new BasicAttributes();
                        addUserAttributes(entry, user, fessConfig);
                        Attribute oc = fessConfig.getLdapAdminUserObjectClassAttribute();
                        entry.put(oc);
                        insert(userDN, entry);

                        StreamUtil.of(user.getGroupNames()).forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, subResult -> {
                                if (!subResult.hasMore()) {
                                    Group group = new Group();
                                    group.setName(name);
                                    insert(group);
                                }
                                List<ModificationItem> modifyList = new ArrayList<>();
                                modifyAddEntry(modifyList, "member", userDN);
                                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList);
                            });
                        });

                        StreamUtil.of(user.getRoleNames()).forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, subResult -> {
                                if (!subResult.hasMore()) {
                                    Role role = new Role();
                                    role.setName(name);
                                    insert(role);
                                }
                                List<ModificationItem> modifyList = new ArrayList<>();
                                modifyAddEntry(modifyList, "member", userDN);
                                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList);
                            });
                        });
                    }
                });

    }

    protected void addUserAttributes(final BasicAttributes entry, final User user, final FessConfig fessConfig) {
        entry.put(new BasicAttribute("cn", user.getName()));
        entry.put(new BasicAttribute("sn", user.getName()));
        entry.put(new BasicAttribute("userPassword", user.getOriginalPassword()));
    }

    public void delete(User user) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()), null, result -> {
            if (result.hasMore()) {
                delete(fessConfig.getLdapAdminUserSecurityPrincipal(user.getName()));
            } else {
                logger.info("{} does not exist in LDAP server.", user.getName());
            }
        });

    }

    public void insert(Role role) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, result -> {
            if (result.hasMore()) {
                logger.info("{} exists in LDAP server.", role.getName());
            } else {
                String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                BasicAttributes entry = new BasicAttributes();
                addRoleAttributes(entry, role, fessConfig);
                Attribute oc = fessConfig.getLdapAdminRoleObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry);
            }
        });

    }

    protected void addRoleAttributes(final BasicAttributes entry, final Role user, final FessConfig fessConfig) {
        // nothing
    }

    public void delete(Role role) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, result -> {
            if (result.hasMore()) {
                String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                delete(entryDN);
            } else {
                logger.info("{} does not exist in LDAP server.", role.getName());
            }
        });

    }

    public void insert(Group group) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, result -> {
            if (result.hasMore()) {
                logger.info("{} exists in LDAP server.", group.getName());
            } else {
                String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
                BasicAttributes entry = new BasicAttributes();
                addGroupAttributes(entry, group.getName(), fessConfig);
                Attribute oc = fessConfig.getLdapAdminGroupObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry);
            }
        });
    }

    protected void addGroupAttributes(final BasicAttributes entry, final String name, final FessConfig fessConfig) {
        // nothing
    }

    public void delete(Group group) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, result -> {
            if (result.hasMore()) {
                String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
                delete(entryDN);
            } else {
                logger.info("{} does not exist in LDAP server.", group.getName());
            }
        });
    }

    protected void insert(String entryDN, Attributes entry) {
        try (DirContextHolder holder = getDirContext()) {
            logger.debug("Inserting {}", entryDN);
            holder.get().createSubcontext(entryDN, entry);
        } catch (NamingException e) {
            throw new LdapOperationException("Failed to add " + entryDN, e);
        }
    }

    protected void delete(String entryDN) {
        try (DirContextHolder holder = getDirContext()) {
            logger.debug("Deleting {}", entryDN);
            holder.get().destroySubcontext(entryDN);
        } catch (NamingException e) {
            throw new LdapOperationException("Failed to delete " + entryDN, e);
        }
    }

    protected void search(String baseDn, String filter, String[] returningAttrs, SearcConsumer consumer) {
        try (DirContextHolder holder = getDirContext()) {
            final SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            if (returningAttrs != null) {
                controls.setReturningAttributes(returningAttrs);
            }

            consumer.accept(holder.get().search(baseDn, filter, controls));
        } catch (NamingException e) {
            throw new LdapOperationException("Failed to search " + baseDn + " with " + filter, e);
        }
    }

    protected void modifyAddEntry(List<ModificationItem> modifyList, String name, String value) {
        Attribute attr = new BasicAttribute(name, value);
        ModificationItem mod = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modifyReplaceEntry(List<ModificationItem> modifyList, String name, String value) {
        Attribute attr = new BasicAttribute(name, value);
        ModificationItem mod = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modifyDeleteEntry(List<ModificationItem> modifyList, String name, String value) {
        Attribute attr = new BasicAttribute(name, value);
        ModificationItem mod = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modify(String dn, List<ModificationItem> modifyList) {
        try (DirContextHolder holder = getDirContext()) {
            holder.get().modifyAttributes(dn, modifyList.toArray(new ModificationItem[modifyList.size()]));
        } catch (NamingException e) {
            throw new LdapOperationException("Failed to search " + dn, e);
        }
    }

    interface SearcConsumer {
        void accept(NamingEnumeration<SearchResult> t) throws NamingException;
    }

    protected DirContextHolder getDirContext() {
        DirContextHolder holder = contextLocal.get();
        if (holder == null) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Hashtable<String, String> env =
                    createEnvironment(fessConfig.getLdapAdminInitialContextFactory(), fessConfig.getLdapAdminSecurityAuthentication(),
                            fessConfig.getLdapAdminProviderUrl(), fessConfig.getLdapAdminSecurityPrincipal(),
                            fessConfig.getLdapAdminSecurityCredentials());
            try {
                holder = new DirContextHolder(new InitialDirContext(env));
                contextLocal.set(holder);
                return holder;
            } catch (NamingException e) {
                throw new LdapOperationException("Failed to create DirContext.", e);
            }
        } else {
            holder.inc();
            return holder;
        }
    }

    protected class DirContextHolder implements AutoCloseable {
        private DirContext context;

        private int counter = 1;

        protected DirContextHolder(DirContext context) {
            this.context = context;
        }

        public DirContext get() {
            return context;
        }

        public void inc() {
            counter++;
        }

        @Override
        public void close() {
            if (counter > 1) {
                counter--;
            } else {
                contextLocal.remove();
                if (context != null) {
                    try {
                        context.close();
                    } catch (final NamingException e) {
                        // ignored
                    }
                }
            }
        }
    }
}
