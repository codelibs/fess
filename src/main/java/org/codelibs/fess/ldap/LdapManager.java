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
import java.util.function.Supplier;
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

    protected Hashtable<String, String> createEnvironment(final String initialContextFactory, final String securityAuthentication,
            final String providerUrl, final String principal, final String credntials) {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credntials);
        return env;
    }

    protected Hashtable<String, String> createAdminEnv() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return createEnvironment(fessConfig.getLdapAdminInitialContextFactory(), fessConfig.getLdapAdminSecurityAuthentication(),
                fessConfig.getLdapAdminProviderUrl(), fessConfig.getLdapAdminSecurityPrincipal(),
                fessConfig.getLdapAdminSecurityCredentials());
    }

    protected Hashtable<String, String> createSearchEnv(final String username, final String password) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return createEnvironment(fessConfig.getLdapInitialContextFactory(), fessConfig.getLdapSecurityAuthentication(),
                fessConfig.getLdapProviderUrl(), fessConfig.getLdapSecurityPrincipal(username), password);
    }

    public OptionalEntity<FessUser> login(final String username, final String password) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (StringUtil.isBlank(fessConfig.getLdapProviderUrl())) {
            return OptionalEntity.empty();
        }

        final Hashtable<String, String> env = createSearchEnv(username, password);
        try (DirContextHolder holder = getDirContext(() -> env)) {
            final DirContext context = holder.get();
            if (logger.isDebugEnabled()) {
                logger.debug("Logged in.", context);
            }
            return OptionalEntity.of(createLdapUser(username, env));
        } catch (final Exception e) {
            logger.debug("Login failed.", e);
        }
        return OptionalEntity.empty();
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
        search(bindDn, filter, new String[] { fessConfig.getLdapMemberofAttribute() },
                () -> createSearchEnv(ldapUser.getName(), ldapUser.getPassword()), result -> {
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

    protected void processSearchRoles(final NamingEnumeration<SearchResult> result, final BiConsumer<String, String> consumer)
            throws NamingException {
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

    public void insert(final User user) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(user.getName());
        search(fessConfig.getLdapAdminUserBaseDn(),
                fessConfig.getLdapAdminUserFilter(user.getName()),
                new String[] { fessConfig.getLdapMemberofAttribute() },
                adminEnv,
                result -> {
                    if (result.hasMore()) {
                        if (user.getOriginalPassword() != null) {
                            final List<ModificationItem> modifyList = new ArrayList<>();
                            modifyReplaceEntry(modifyList, "userPassword", user.getOriginalPassword());
                            modify(userDN, modifyList, adminEnv);
                        }

                        final List<String> oldGroupList = new ArrayList<>();
                        final List<String> oldRoleList = new ArrayList<>();
                        final String lowerGroupDn = fessConfig.getLdapAdminGroupBaseDn().toLowerCase(Locale.ROOT);
                        final String lowerRoleDn = fessConfig.getLdapAdminRoleBaseDn().toLowerCase(Locale.ROOT);
                        processSearchRoles(result, (entryDn, name) -> {
                            final String lowerEntryDn = entryDn.toLowerCase(Locale.ROOT);
                            if (lowerEntryDn.indexOf(lowerGroupDn) != -1) {
                                oldGroupList.add(name);
                            } else if (lowerEntryDn.indexOf(lowerRoleDn) != -1) {
                                oldRoleList.add(name);
                            }
                        });

                        final List<String> newGroupList = StreamUtil.of(user.getGroupNames()).collect(Collectors.toList());
                        StreamUtil.of(user.getGroupNames()).forEach(name -> {
                            if (oldGroupList.contains(name)) {
                                oldGroupList.remove(name);
                                newGroupList.remove(name);
                            }
                        });
                        oldGroupList.stream().forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (subResult.hasMore()) {
                                                    final List<ModificationItem> modifyList = new ArrayList<>();
                                                    modifyDeleteEntry(modifyList, "member", userDN);
                                                    modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                                }
                                            });
                                });
                        newGroupList.stream().forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (!subResult.hasMore()) {
                                                    final Group group = new Group();
                                                    group.setName(name);
                                                    insert(group);
                                                }
                                                final List<ModificationItem> modifyList = new ArrayList<>();
                                                modifyAddEntry(modifyList, "member", userDN);
                                                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                            });
                                });

                        final List<String> newRoleList = StreamUtil.of(user.getRoleNames()).collect(Collectors.toList());
                        StreamUtil.of(user.getRoleNames()).forEach(name -> {
                            if (oldRoleList.contains(name)) {
                                oldRoleList.remove(name);
                                newRoleList.remove(name);
                            }
                        });
                        oldRoleList.stream().forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (subResult.hasMore()) {
                                                    final List<ModificationItem> modifyList = new ArrayList<>();
                                                    modifyDeleteEntry(modifyList, "member", userDN);
                                                    modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
                                                }
                                            });
                                });
                        newRoleList.stream().forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (!subResult.hasMore()) {
                                                    final Role role = new Role();
                                                    role.setName(name);
                                                    insert(role);
                                                }
                                                final List<ModificationItem> modifyList = new ArrayList<>();
                                                modifyAddEntry(modifyList, "member", userDN);
                                                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
                                            });
                                });
                    } else {
                        final BasicAttributes entry = new BasicAttributes();
                        addUserAttributes(entry, user, fessConfig);
                        final Attribute oc = fessConfig.getLdapAdminUserObjectClassAttribute();
                        entry.put(oc);
                        insert(userDN, entry, adminEnv);

                        StreamUtil.of(user.getGroupNames()).forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (!subResult.hasMore()) {
                                                    final Group group = new Group();
                                                    group.setName(name);
                                                    insert(group);
                                                }
                                                final List<ModificationItem> modifyList = new ArrayList<>();
                                                modifyAddEntry(modifyList, "member", userDN);
                                                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                            });
                                });

                        StreamUtil.of(user.getRoleNames()).forEach(
                                name -> {
                                    search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                            subResult -> {
                                                if (!subResult.hasMore()) {
                                                    final Role role = new Role();
                                                    role.setName(name);
                                                    insert(role);
                                                }
                                                final List<ModificationItem> modifyList = new ArrayList<>();
                                                modifyAddEntry(modifyList, "member", userDN);
                                                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
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

    public void delete(final User user) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(user.getName());

        StreamUtil.of(user.getGroupNames()).forEach(name -> {
            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv, subResult -> {
                if (!subResult.hasMore()) {
                    final Group group = new Group();
                    group.setName(name);
                    insert(group);
                }
                final List<ModificationItem> modifyList = new ArrayList<>();
                modifyDeleteEntry(modifyList, "member", userDN);
                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
            });
        });
        StreamUtil.of(user.getRoleNames()).forEach(name -> {
            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv, subResult -> {
                if (!subResult.hasMore()) {
                    final Role role = new Role();
                    role.setName(name);
                    insert(role);
                }
                final List<ModificationItem> modifyList = new ArrayList<>();
                modifyDeleteEntry(modifyList, "member", userDN);
                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
            });
        });

        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()), null, adminEnv, result -> {
            if (result.hasMore()) {
                delete(userDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", user.getName());
            }
        });

    }

    public void insert(final Role role) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, adminEnv, result -> {
            if (result.hasMore()) {
                logger.info("{} exists in LDAP server.", role.getName());
            } else {
                final String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                final BasicAttributes entry = new BasicAttributes();
                addRoleAttributes(entry, role, fessConfig);
                final Attribute oc = fessConfig.getLdapAdminRoleObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry, adminEnv);
            }
        });

    }

    protected void addRoleAttributes(final BasicAttributes entry, final Role user, final FessConfig fessConfig) {
        // nothing
    }

    public void delete(final Role role) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, adminEnv, result -> {
            if (result.hasMore()) {
                final String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                delete(entryDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", role.getName());
            }
        });

    }

    public void insert(final Group group) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, adminEnv, result -> {
            if (result.hasMore()) {
                logger.info("{} exists in LDAP server.", group.getName());
            } else {
                final String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
                final BasicAttributes entry = new BasicAttributes();
                addGroupAttributes(entry, group.getName(), fessConfig);
                final Attribute oc = fessConfig.getLdapAdminGroupObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry, adminEnv);
            }
        });
    }

    protected void addGroupAttributes(final BasicAttributes entry, final String name, final FessConfig fessConfig) {
        // nothing
    }

    public void delete(final Group group) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, adminEnv, result -> {
            if (result.hasMore()) {
                final String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
                delete(entryDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", group.getName());
            }
        });
    }

    public void changePassword(final String username, final String password) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = () -> createAdminEnv();
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(username);
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(username), null, adminEnv, result -> {
            if (result.hasMore()) {
                final List<ModificationItem> modifyList = new ArrayList<>();
                modifyReplaceEntry(modifyList, "userPassword", password);
                modify(userDN, modifyList, adminEnv);
            } else {
                throw new LdapOperationException("User is not found: " + username);
            }
        });

    }

    protected void insert(final String entryDN, final Attributes entry, final Supplier<Hashtable<String, String>> envSupplier) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            logger.debug("Inserting {}", entryDN);
            holder.get().createSubcontext(entryDN, entry);
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to add " + entryDN, e);
        }
    }

    protected void delete(final String entryDN, final Supplier<Hashtable<String, String>> envSupplier) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            logger.debug("Deleting {}", entryDN);
            holder.get().destroySubcontext(entryDN);
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to delete " + entryDN, e);
        }
    }

    protected void search(final String baseDn, final String filter, final String[] returningAttrs,
            final Supplier<Hashtable<String, String>> envSupplier, final SearcConsumer consumer) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            final SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            if (returningAttrs != null) {
                controls.setReturningAttributes(returningAttrs);
            }

            consumer.accept(holder.get().search(baseDn, filter, controls));
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to search " + baseDn + " with " + filter, e);
        }
    }

    protected void modifyAddEntry(final List<ModificationItem> modifyList, final String name, final String value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modifyReplaceEntry(final List<ModificationItem> modifyList, final String name, final String value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modifyDeleteEntry(final List<ModificationItem> modifyList, final String name, final String value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    protected void modify(final String dn, final List<ModificationItem> modifyList, final Supplier<Hashtable<String, String>> envSupplier) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            holder.get().modifyAttributes(dn, modifyList.toArray(new ModificationItem[modifyList.size()]));
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to search " + dn, e);
        }
    }

    interface SearcConsumer {
        void accept(NamingEnumeration<SearchResult> t) throws NamingException;
    }

    protected DirContextHolder getDirContext(final Supplier<Hashtable<String, String>> envSupplier) {
        DirContextHolder holder = contextLocal.get();
        if (holder == null) {
            final Hashtable<String, String> env = envSupplier.get();
            try {
                holder = new DirContextHolder(new InitialDirContext(env));
                contextLocal.set(holder);
                return holder;
            } catch (final NamingException e) {
                throw new LdapOperationException("Failed to create DirContext.", e);
            }
        } else {
            holder.inc();
            return holder;
        }
    }

    protected class DirContextHolder implements AutoCloseable {
        private final DirContext context;

        private int counter = 1;

        protected DirContextHolder(final DirContext context) {
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
