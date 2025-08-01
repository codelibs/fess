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
package org.codelibs.fess.ldap;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.LdapConfigurationException;
import org.codelibs.fess.exception.LdapOperationException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.opensearch.user.exentity.Role;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.OptionalUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;

import jakarta.annotation.PostConstruct;

/**
 * Manages LDAP connections and operations.
 */
public class LdapManager {
    private static final Logger logger = LogManager.getLogger(LdapManager.class);

    /** A thread-local variable to hold the directory context. */
    protected ThreadLocal<DirContextHolder> contextLocal = new ThreadLocal<>();

    /** A flag to indicate if the LDAP connection is bound. */
    protected volatile boolean isBind = false;

    /** The Fess configuration. */
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public LdapManager() {
        // do nothing
    }

    /**
     * Initializes the LDAP manager.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
    }

    /**
     * Creates the environment for LDAP connection.
     *
     * @param initialContextFactory The initial context factory.
     * @param securityAuthentication The security authentication.
     * @param providerUrl The provider URL.
     * @param principal The principal.
     * @param credntials The credentials.
     * @return The environment for LDAP connection.
     */
    protected Hashtable<String, String> createEnvironment(final String initialContextFactory, final String securityAuthentication,
            final String providerUrl, final String principal, final String credntials) {
        final Hashtable<String, String> env = new Hashtable<>();
        putEnv(env, Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        putEnv(env, Context.SECURITY_AUTHENTICATION, securityAuthentication);
        putEnv(env, Context.PROVIDER_URL, providerUrl);
        putEnv(env, Context.SECURITY_PRINCIPAL, principal);
        putEnv(env, Context.SECURITY_CREDENTIALS, credntials);
        if (providerUrl != null && providerUrl.startsWith("ldaps://")) {
            putEnv(env, Context.SECURITY_PROTOCOL, "ssl");
        }
        return env;
    }

    /**
     * Puts a key-value pair to the environment.
     *
     * @param env The environment.
     * @param key The key.
     * @param value The value.
     */
    protected void putEnv(final Hashtable<String, String> env, final String key, final String value) {
        if (value == null) {
            throw new LdapConfigurationException(key + " is null.");
        }
        env.put(key, value);
    }

    /**
     * Creates the admin environment for LDAP connection.
     *
     * @return The admin environment for LDAP connection.
     */
    protected Hashtable<String, String> createAdminEnv() {
        return createEnvironment(//
                fessConfig.getLdapInitialContextFactory(), //
                fessConfig.getLdapSecurityAuthentication(), fessConfig.getLdapProviderUrl(), //
                fessConfig.getLdapAdminSecurityPrincipal(), //
                fessConfig.getLdapAdminSecurityCredentials());
    }

    /**
     * Creates the search environment for LDAP connection.
     *
     * @param username The username.
     * @param password The password.
     * @return The search environment for LDAP connection.
     */
    protected Hashtable<String, String> createSearchEnv(final String username, final String password) {
        return createEnvironment(//
                fessConfig.getLdapInitialContextFactory(), //
                fessConfig.getLdapSecurityAuthentication(), //
                fessConfig.getLdapProviderUrl(), //
                fessConfig.getLdapSecurityPrincipal(username), password);
    }

    /**
     * Creates the search environment for LDAP connection.
     *
     * @return The search environment for LDAP connection.
     */
    protected Hashtable<String, String> createSearchEnv() {
        return createEnvironment(//
                fessConfig.getLdapInitialContextFactory(), //
                fessConfig.getLdapSecurityAuthentication(), fessConfig.getLdapProviderUrl(), //
                fessConfig.getLdapAdminSecurityPrincipal(), //
                fessConfig.getLdapAdminSecurityCredentials());
    }

    /**
     * Updates the LDAP configuration.
     */
    public void updateConfig() {
        isBind = false;
    }

    /**
     * Validates the LDAP connection.
     *
     * @return True if the LDAP connection is valid, otherwise false.
     */
    protected boolean validate() {
        if (!isBind) {
            if (fessConfig.getLdapAdminSecurityPrincipal() == null || fessConfig.getLdapAdminSecurityCredentials() == null) {
                // no credentials
                return !fessConfig.isLdapAuthValidation();
            }
            final Hashtable<String, String> env = createAdminEnv();
            try (DirContextHolder holder = getDirContext(() -> env)) {
                final DirContext context = holder.get();
                if (logger.isDebugEnabled()) {
                    logger.debug("Logged in as Bind DN. {}", context);
                }
                isBind = true;
            } catch (final Exception e) {
                logger.warn("LDAP configuration is wrong.", e);
            }
        }
        return isBind;
    }

    /**
     * Authenticates a user with the specified username and password against LDAP.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return an optional containing the authenticated user if successful, empty otherwise
     */
    public OptionalEntity<FessUser> login(final String username, final String password) {
        if (StringUtil.isBlank(fessConfig.getLdapProviderUrl()) || !validate()) {
            return OptionalEntity.empty();
        }

        final Hashtable<String, String> env = createSearchEnv(username, password);
        try (DirContextHolder holder = getDirContext(() -> env)) {
            final DirContext context = holder.get();
            final LdapUser ldapUser = createLdapUser(username, env);
            if (!allowEmptyGroupAndRole(ldapUser)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Login failed. No permissions. {}", context);
                }
                return OptionalEntity.empty();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Logged in. {}", context);
            }
            return OptionalEntity.of(ldapUser);
        } catch (final Exception e) {
            logger.debug("Login failed.", e);
        }
        return OptionalEntity.empty();
    }

    /**
     * Authenticates a user with the specified username without password validation.
     *
     * @param username the username for authentication
     * @return an optional containing the authenticated user if successful, empty otherwise
     */
    public OptionalEntity<FessUser> login(final String username) {
        final Hashtable<String, String> env = createSearchEnv();
        try (DirContextHolder holder = getDirContext(() -> env)) {
            final DirContext context = holder.get();
            final LdapUser ldapUser = createLdapUser(username, env);
            if (!allowEmptyGroupAndRole(ldapUser)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Login failed. No permissions. {}", context);
                }
                return OptionalEntity.empty();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Logged in. {}", context);
            }
            return OptionalEntity.of(ldapUser);
        } catch (final Exception e) {
            logger.debug("Login failed.", e);
        }
        return OptionalEntity.empty();
    }

    /**
     * Checks if the specified LDAP user is allowed to have empty group and role permissions.
     *
     * @param ldapUser the LDAP user to check
     * @return true if empty permissions are allowed, false otherwise
     */
    protected boolean allowEmptyGroupAndRole(final LdapUser ldapUser) {
        if (fessConfig.isLdapAllowEmptyPermission()) {
            return true;
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        for (final String permission : ldapUser.getPermissions()) {
            if (!systemHelper.isUserPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new LDAP user instance with the specified username and environment.
     *
     * @param username the username for the LDAP user
     * @param env the environment configuration for LDAP connection
     * @return a new LdapUser instance
     */
    protected LdapUser createLdapUser(final String username, final Hashtable<String, String> env) {
        return new LdapUser(env, username);
    }

    /**
     * Retrieves roles for the specified LDAP user based on the provided filters.
     *
     * @param ldapUser the LDAP user to retrieve roles for
     * @param bindDn the bind DN for LDAP connection
     * @param accountFilter the account filter pattern
     * @param groupFilter the group filter pattern
     * @param lazyLoading the lazy loading consumer for roles
     * @return an array of role names
     */
    public String[] getRoles(final LdapUser ldapUser, final String bindDn, final String accountFilter, final String groupFilter,
            final Consumer<String[]> lazyLoading) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Set<String> roleSet = new HashSet<>();

        if (fessConfig.isLdapRoleSearchUserEnabled()) {
            roleSet.add(normalizePermissionName(systemHelper.getSearchRoleByUser(ldapUser.getName())));
        }

        // LDAP: cn=%s
        // AD: (&(objectClass=user)(sAMAccountName=%s))
        final String filter = String.format(accountFilter, escapeLDAPSearchFilter(ldapUser.getName()));
        if (logger.isDebugEnabled()) {
            logger.debug("Account Filter: {}", filter);
        }
        final Set<String> subRoleSet = new HashSet<>();
        final Set<String> sAMAccountGroupNameSet = new HashSet<>();
        search(bindDn, filter, new String[] { fessConfig.getLdapMemberofAttribute() }, () -> ldapUser.getEnvironment(), result -> {
            processSearchRoles(result, entryDn -> {
                final String roleName = getSearchRoleName(entryDn);
                final String roleType = updateSearchRoles(roleSet, entryDn, roleName);
                if (fessConfig.getRoleSearchGroupPrefix().equals(roleType) && fessConfig.isLdapSamaccountnameGroup()) {
                    sAMAccountGroupNameSet.add(roleName);
                }
                if (StringUtil.isNotBlank(groupFilter)) {
                    subRoleSet.add(entryDn);
                }
            });
        });

        if (logger.isDebugEnabled()) {
            logger.debug("role: {}", roleSet);
        }
        final String[] roles = roleSet.toArray(new String[roleSet.size()]);

        if (!subRoleSet.isEmpty()) {
            TimeoutManager.getInstance().addTimeoutTarget(() -> {
                sAMAccountGroupNameSet.stream().forEach(groupName -> {
                    getSAMAccountGroupName(bindDn, groupName).ifPresent(sAMAccountGroupName -> {
                        roleSet.add(systemHelper.getSearchRoleByGroup(normalizePermissionName(sAMAccountGroupName)));
                    });
                });
                processSubRoles(ldapUser, bindDn, subRoleSet, groupFilter, roleSet);
                if (logger.isDebugEnabled()) {
                    logger.debug("role(lazy loading): {}", roleSet);
                }
                lazyLoading.accept(roleSet.toArray(new String[roleSet.size()]));
            }, 0, false);
        }

        return roles;
    }

    /**
     * Gets the sAMAccountName for a group from the LDAP directory.
     *
     * @param bindDn the bind DN to search within
     * @param groupName the name of the group to search for
     * @return an optional containing the sAMAccountName if found, empty otherwise
     */
    protected OptionalEntity<String> getSAMAccountGroupName(final String bindDn, final String groupName) {
        final Hashtable<String, String> env = createSearchEnv();
        try (DirContextHolder holder = getDirContext(() -> env)) {
            final DirContext context = holder.get();
            final SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            if (logger.isDebugEnabled()) {
                logger.debug("Searching for sAMAccountName of group: {} on {}", groupName, bindDn);
            }
            final NamingEnumeration<SearchResult> results =
                    context.search(bindDn, "(name=" + escapeLDAPSearchFilter(groupName) + ")", searchControls);
            if (results.hasMore()) {
                final SearchResult searchResult = results.next();
                final Attribute attribute = searchResult.getAttributes().get("sAMAccountName");
                if (logger.isDebugEnabled()) {
                    logger.debug("sAMAccountName: {}", attribute);
                }
                if (attribute != null && attribute.get() instanceof String sAMAccountName) {
                    return OptionalEntity.of(sAMAccountName);
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to get sAMAccountName: {}", groupName, e);
        }
        return OptionalEntity.empty();
    }

    /**
     * Processes sub-roles for the specified LDAP user.
     *
     * @param ldapUser the LDAP user to process sub-roles for
     * @param bindDn the bind DN for LDAP connection
     * @param subRoleSet the set of sub-roles to process
     * @param groupFilter the group filter pattern
     * @param roleSet the set of roles to update
     */
    protected void processSubRoles(final LdapUser ldapUser, final String bindDn, final Set<String> subRoleSet, final String groupFilter,
            final Set<String> roleSet) {
        // (member:1.2.840.113556.1.4.1941:=%s)
        if (subRoleSet.isEmpty()) {
            return;
        }
        String filter = subRoleSet.stream().map(s -> String.format(groupFilter, s)).collect(Collectors.joining());
        if (subRoleSet.size() > 1) {
            filter = "(|" + filter + ")";
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Group Filter: {}", filter);
        }
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        search(bindDn, filter, null, () -> ldapUser.getEnvironment(), result -> {
            for (final SearchResult srcrslt : result) {
                final String groupDn = srcrslt.getNameInNamespace();
                if (logger.isDebugEnabled()) {
                    logger.debug("groupDn: {}", groupDn);
                }
                final String groupName = getSearchRoleName(groupDn);
                final String roleType = updateSearchRoles(roleSet, groupDn, groupName);
                if (fessConfig.getRoleSearchGroupPrefix().equals(roleType) && fessConfig.isLdapSamaccountnameGroup()) {
                    getSAMAccountGroupName(bindDn, groupName).ifPresent(sAMAccountGroupName -> {
                        roleSet.add(systemHelper.getSearchRoleByGroup(normalizePermissionName(sAMAccountGroupName)));
                    });
                }
            }
        });
    }

    /**
     * Updates the role set with search roles based on the entry DN and name.
     *
     * @param roleSet the set of roles to update
     * @param entryDn the entry DN to check
     * @param name the role name
     * @return the role type prefix if successful, null otherwise
     */
    protected String updateSearchRoles(final Set<String> roleSet, final String entryDn, final String name) {
        if (StringUtil.isNotBlank(name)) {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final boolean isRole = entryDn.toLowerCase(Locale.ROOT).indexOf("ou=role") != -1;
            if (isRole) {
                if (fessConfig.isLdapRoleSearchRoleEnabled()) {
                    roleSet.add(systemHelper.getSearchRoleByRole(normalizePermissionName(name)));
                    return fessConfig.getRoleSearchRolePrefix();
                }
            } else if (fessConfig.isLdapRoleSearchGroupEnabled()) {
                roleSet.add(systemHelper.getSearchRoleByGroup(normalizePermissionName(name)));
                return fessConfig.getRoleSearchGroupPrefix();
            }
        }
        return null;
    }

    /**
     * Escapes special characters in an LDAP search filter to prevent injection attacks.
     *
     * @param filter the LDAP search filter to escape
     * @return the escaped filter string
     */
    protected String escapeLDAPSearchFilter(String filter) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filter.length(); i++) {
            char curChar = filter.charAt(i);
            switch (curChar) {
            case '\\':
                sb.append("\\5c");
                break;
            case '*':
                sb.append("\\2a");
                break;
            case '(':
                sb.append("\\28");
                break;
            case ')':
                sb.append("\\29");
                break;
            case '\0':
                sb.append("\\00");
                break;
            default:
                sb.append(curChar);
            }
        }
        return sb.toString();
    }

    /**
     * Normalizes a permission name based on configuration settings.
     *
     * @param name the permission name to normalize
     * @return the normalized permission name
     */
    public String normalizePermissionName(final String name) {
        if (fessConfig.isLdapLowercasePermissionName()) {
            return name.toLowerCase(Locale.ROOT);
        }
        return name;
    }

    /**
     * Processes search results to extract roles using a BiConsumer.
     *
     * @param result the list of search results
     * @param consumer the BiConsumer to process entry DN and role name
     * @throws NamingException if LDAP naming exception occurs
     */
    protected void processSearchRoles(final List<SearchResult> result, final BiConsumer<String, String> consumer) throws NamingException {
        processSearchRoles(result, entryDn -> {
            final String name = getSearchRoleName(entryDn);
            if (name != null) {
                consumer.accept(entryDn, name);
            }
        });
    }

    /**
     * Processes search results to extract roles using a Consumer.
     *
     * @param result the list of search results
     * @param consumer the Consumer to process entry DN
     * @throws NamingException if LDAP naming exception occurs
     */
    protected void processSearchRoles(final List<SearchResult> result, final Consumer<String> consumer) throws NamingException {
        for (final SearchResult srcrslt : result) {
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

                    if (logger.isDebugEnabled()) {
                        logger.debug("entryDn: {}", entryDn);
                    }
                    consumer.accept(entryDn);
                }
            }
        }
    }

    /**
     * Extracts the role name from an LDAP entry DN.
     *
     * @param entryDn the LDAP entry DN
     * @return the extracted role name, or null if not found
     */
    protected String getSearchRoleName(final String entryDn) {
        if (entryDn == null) {
            return null;
        }
        int start = entryDn.toLowerCase(Locale.ROOT).indexOf("cn=");
        if (start == -1) {
            return null;
        }
        start += 3;

        final int end = entryDn.indexOf(',', start);
        final String value = end == -1 ? entryDn.substring(start) : entryDn.substring(start, end);
        if (fessConfig.isLdapGroupNameWithUnderscores()) {
            return replaceWithUnderscores(value);
        }
        return value;
    }

    /**
     * Replaces special characters in a string with underscores for group names.
     *
     * @param value the string to process
     * @return the string with special characters replaced by underscores
     */
    protected String replaceWithUnderscores(final String value) {
        return value.replaceAll("[/\\\\\\[\\]:;|=,+\\*?<>]", "_");
    }

    /**
     * Sets an attribute value from search results using a Consumer.
     *
     * @param result the list of search results
     * @param name the attribute name
     * @param consumer the Consumer to process the attribute value
     */
    protected void setAttributeValue(final List<SearchResult> result, final String name, final Consumer<Object> consumer) {
        final List<Object> attrList = getAttributeValueList(result, name);
        if (!attrList.isEmpty()) {
            consumer.accept(attrList.get(0));
        }
    }

    /**
     * Gets a list of attribute values from search results.
     *
     * @param result the list of search results
     * @param name the attribute name
     * @return a list of attribute values
     */
    protected List<Object> getAttributeValueList(final List<SearchResult> result, final String name) {
        try {
            for (final SearchResult srcrslt : result) {
                final Attributes attrs = srcrslt.getAttributes();

                final Attribute attr = attrs.get(name);
                if (attr == null) {
                    continue;
                }

                final List<Object> attrList = new ArrayList<>();
                for (int i = 0; i < attr.size(); i++) {
                    final Object attrValue = attr.get(i);
                    if (attrValue != null) {
                        attrList.add(attrValue);
                    }
                }
                return attrList;
            }
            return Collections.emptyList();
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to parse attribute values for " + name, e);
        }
    }

    /**
     * Applies LDAP attributes to a user object.
     *
     * @param user the user object to populate with LDAP attributes
     */
    public void apply(final User user) {
        if (!fessConfig.isLdapAdminEnabled(user.getName())) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                setAttributeValue(result, fessConfig.getLdapAttrSurname(), o -> user.setSurname(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrGivenName(), o -> user.setGivenName(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrMail(), o -> user.setMail(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrEmployeeNumber(), o -> user.setEmployeeNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrTelephoneNumber(), o -> user.setTelephoneNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrHomePhone(), o -> user.setHomePhone(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrHomePostalAddress(), o -> user.setHomePostalAddress(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrLabeledURI(), o -> user.setLabeledURI(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrRoomNumber(), o -> user.setRoomNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrDescription(), o -> user.setDescription(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrTitle(), o -> user.setTitle(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPager(), o -> user.setPager(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrStreet(), o -> user.setStreet(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPostalCode(), o -> user.setPostalCode(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPhysicalDeliveryOfficeName(),
                        o -> user.setPhysicalDeliveryOfficeName(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrDestinationIndicator(), o -> user.setDestinationIndicator(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrInternationaliSDNNumber(),
                        o -> user.setInternationaliSDNNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrState(), o -> user.setState(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrEmployeeType(), o -> user.setEmployeeType(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrFacsimileTelephoneNumber(),
                        o -> user.setFacsimileTelephoneNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPostOfficeBox(), o -> user.setPostOfficeBox(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrInitials(), o -> user.setInitials(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrCarLicense(), o -> user.setCarLicense(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrMobile(), o -> user.setMobile(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPostalAddress(), o -> user.setPostalAddress(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrCity(), o -> user.setCity(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrTeletexTerminalIdentifier(),
                        o -> user.setTeletexTerminalIdentifier(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrX121Address(), o -> user.setX121Address(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrBusinessCategory(), o -> user.setBusinessCategory(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrRegisteredAddress(), o -> user.setRegisteredAddress(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrDisplayName(), o -> user.setDisplayName(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrPreferredLanguage(), o -> user.setPreferredLanguage(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrDepartmentNumber(), o -> user.setDepartmentNumber(o.toString()));
                setAttributeValue(result, fessConfig.getLdapAttrUidNumber(), o -> user.setUidNumber(DfTypeUtil.toLong(o)));
                setAttributeValue(result, fessConfig.getLdapAttrGidNumber(), o -> user.setGidNumber(DfTypeUtil.toLong(o)));
                setAttributeValue(result, fessConfig.getLdapAttrHomeDirectory(), o -> user.setHomeDirectory(o.toString()));
            }
        });

        // groups and roles
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()),
                new String[] { fessConfig.getLdapMemberofAttribute() }, adminEnv, result -> {
                    if (!result.isEmpty()) {
                        final List<String> groupList = new ArrayList<>();
                        final List<String> roleList = new ArrayList<>();
                        final String lowerGroupDn = fessConfig.getLdapAdminGroupBaseDn().toLowerCase(Locale.ROOT);
                        final String lowerRoleDn = fessConfig.getLdapAdminRoleBaseDn().toLowerCase(Locale.ROOT);
                        processSearchRoles(result, (entryDn, name) -> {
                            final String lowerEntryDn = entryDn.toLowerCase(Locale.ROOT);
                            if (lowerEntryDn.indexOf(lowerGroupDn) != -1) {
                                groupList.add(Base64.getUrlEncoder().encodeToString(name.getBytes(Constants.CHARSET_UTF_8)));
                            } else if (lowerEntryDn.indexOf(lowerRoleDn) != -1) {
                                roleList.add(Base64.getUrlEncoder().encodeToString(name.getBytes(Constants.CHARSET_UTF_8)));
                            }
                        });
                        user.setGroups(groupList.toArray(new String[groupList.size()]));
                        user.setRoles(roleList.toArray(new String[roleList.size()]));
                    }
                });

    }

    /**
     * Inserts or updates a user in LDAP directory.
     *
     * @param user the user object to insert or update
     */
    public void insert(final User user) {
        if (!fessConfig.isLdapAdminEnabled(user.getName())) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(user.getName());
        // attributes
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                modifyUserAttributes(user, adminEnv, userDN, result);
            } else {
                final BasicAttributes entry = new BasicAttributes();
                addUserAttributes(entry, user);
                final Attribute oc = fessConfig.getLdapAdminUserObjectClassAttribute();
                entry.put(oc);
                insert(userDN, entry, adminEnv);
            }
        });

        // groups and roles
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()),
                new String[] { fessConfig.getLdapMemberofAttribute() }, adminEnv, result -> {
                    if (!result.isEmpty()) {
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
                        final List<String> newGroupList = stream(user.getGroupNames()).get(stream -> stream.collect(Collectors.toList()));
                        stream(user.getGroupNames()).of(stream -> stream.forEach(name -> {
                            if (oldGroupList.contains(name)) {
                                oldGroupList.remove(name);
                                newGroupList.remove(name);
                            }
                        }));
                        oldGroupList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (!subResult.isEmpty()) {
                                            final List<ModificationItem> modifyList = new ArrayList<>();
                                            modifyDeleteEntry(modifyList, "member", userDN);
                                            modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                        }
                                    });
                        });
                        newGroupList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (subResult.isEmpty()) {
                                            final Group group = new Group();
                                            group.setName(name);
                                            insert(group);
                                        }
                                        final List<ModificationItem> modifyList = new ArrayList<>();
                                        modifyAddEntry(modifyList, "member", userDN);
                                        modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                    });
                        });

                        final List<String> newRoleList = stream(user.getRoleNames()).get(stream -> stream.collect(Collectors.toList()));
                        stream(user.getRoleNames()).of(stream -> stream.forEach(name -> {
                            if (oldRoleList.contains(name)) {
                                oldRoleList.remove(name);
                                newRoleList.remove(name);
                            }
                        }));
                        oldRoleList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (!subResult.isEmpty()) {
                                            final List<ModificationItem> modifyList = new ArrayList<>();
                                            modifyDeleteEntry(modifyList, "member", userDN);
                                            modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
                                        }
                                    });
                        });
                        newRoleList.stream().forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (subResult.isEmpty()) {
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
                        stream(user.getGroupNames()).of(stream -> stream.forEach(name -> {
                            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (subResult.isEmpty()) {
                                            final Group group = new Group();
                                            group.setName(name);
                                            insert(group);
                                        }
                                        final List<ModificationItem> modifyList = new ArrayList<>();
                                        modifyAddEntry(modifyList, "member", userDN);
                                        modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
                                    });
                        }));

                        stream(user.getRoleNames()).of(stream -> stream.forEach(name -> {
                            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv,
                                    subResult -> {
                                        if (subResult.isEmpty()) {
                                            final Role role = new Role();
                                            role.setName(name);
                                            insert(role);
                                        }
                                        final List<ModificationItem> modifyList = new ArrayList<>();
                                        modifyAddEntry(modifyList, "member", userDN);
                                        modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
                                    });
                        }));
                    }
                });

    }

    /**
     * Modifies user attributes in the LDAP directory.
     *
     * @param user the user object with new attribute values
     * @param adminEnv the supplier for admin environment
     * @param userDN the DN of the user entry
     * @param result the search results containing current attributes
     */
    protected void modifyUserAttributes(final User user, final Supplier<Hashtable<String, String>> adminEnv, final String userDN,
            final List<SearchResult> result) {
        final List<ModificationItem> modifyList = new ArrayList<>();
        if (user.getOriginalPassword() != null) {
            modifyReplaceEntry(modifyList, "userPassword", user.getOriginalPassword());
        }

        final String attrSurname = fessConfig.getLdapAttrSurname();
        OptionalUtil.ofNullable(user.getSurname()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrSurname, s)).orElse(() -> getAttributeValueList(result, attrSurname)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrSurname, v)));
        final String attrGivenName = fessConfig.getLdapAttrGivenName();
        OptionalUtil.ofNullable(user.getGivenName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrGivenName, s)).orElse(() -> getAttributeValueList(result, attrGivenName)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrGivenName, v)));
        final String attrMail = fessConfig.getLdapAttrMail();
        OptionalUtil.ofNullable(user.getMail()).filter(StringUtil::isNotBlank).ifPresent(s -> modifyReplaceEntry(modifyList, attrMail, s))
                .orElse(() -> getAttributeValueList(result, attrMail).stream().forEach(v -> modifyDeleteEntry(modifyList, attrMail, v)));
        final String attrEmployeeNumber = fessConfig.getLdapAttrEmployeeNumber();
        OptionalUtil.ofNullable(user.getEmployeeNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrEmployeeNumber, s))
                .orElse(() -> getAttributeValueList(result, attrEmployeeNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrEmployeeNumber, v)));
        final String attrTelephoneNumber = fessConfig.getLdapAttrTelephoneNumber();
        OptionalUtil.ofNullable(user.getTelephoneNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrTelephoneNumber, s))
                .orElse(() -> getAttributeValueList(result, attrTelephoneNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrTelephoneNumber, v)));
        final String attrHomePhone = fessConfig.getLdapAttrHomePhone();
        OptionalUtil.ofNullable(user.getHomePhone()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrHomePhone, s)).orElse(() -> getAttributeValueList(result, attrHomePhone)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrHomePhone, v)));
        final String attrHomePostalAddress = fessConfig.getLdapAttrHomePostalAddress();
        OptionalUtil.ofNullable(user.getHomePostalAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrHomePostalAddress, s))
                .orElse(() -> getAttributeValueList(result, attrHomePostalAddress).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrHomePostalAddress, v)));
        final String attrLabeledURI = fessConfig.getLdapAttrLabeledURI();
        OptionalUtil.ofNullable(user.getLabeledURI()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrLabeledURI, s))
                .orElse(() -> getAttributeValueList(result, attrLabeledURI).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrLabeledURI, v)));
        final String attrRoomNumber = fessConfig.getLdapAttrRoomNumber();
        OptionalUtil.ofNullable(user.getRoomNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrRoomNumber, s))
                .orElse(() -> getAttributeValueList(result, attrRoomNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrRoomNumber, v)));
        final String attrDescription = fessConfig.getLdapAttrDescription();
        OptionalUtil.ofNullable(user.getDescription()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrDescription, s))
                .orElse(() -> getAttributeValueList(result, attrDescription).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrDescription, v)));
        final String attrTitle = fessConfig.getLdapAttrTitle();
        OptionalUtil.ofNullable(user.getTitle()).filter(StringUtil::isNotBlank).ifPresent(s -> modifyReplaceEntry(modifyList, attrTitle, s))
                .orElse(() -> getAttributeValueList(result, attrTitle).stream().forEach(v -> modifyDeleteEntry(modifyList, attrTitle, v)));
        final String attrPager = fessConfig.getLdapAttrPager();
        OptionalUtil.ofNullable(user.getPager()).filter(StringUtil::isNotBlank).ifPresent(s -> modifyReplaceEntry(modifyList, attrPager, s))
                .orElse(() -> getAttributeValueList(result, attrPager).stream().forEach(v -> modifyDeleteEntry(modifyList, attrPager, v)));
        final String attrStreet = fessConfig.getLdapAttrStreet();
        OptionalUtil.ofNullable(user.getStreet()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrStreet, s)).orElse(() -> getAttributeValueList(result, attrStreet)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrStreet, v)));
        final String attrPostalCode = fessConfig.getLdapAttrPostalCode();
        OptionalUtil.ofNullable(user.getPostalCode()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrPostalCode, s))
                .orElse(() -> getAttributeValueList(result, attrPostalCode).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrPostalCode, v)));
        final String attrPhysicalDeliveryOfficeName = fessConfig.getLdapAttrPhysicalDeliveryOfficeName();
        OptionalUtil.ofNullable(user.getPhysicalDeliveryOfficeName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrPhysicalDeliveryOfficeName, s))
                .orElse(() -> getAttributeValueList(result, attrPhysicalDeliveryOfficeName).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrPhysicalDeliveryOfficeName, v)));
        final String attrDestinationIndicator = fessConfig.getLdapAttrDestinationIndicator();
        OptionalUtil.ofNullable(user.getDestinationIndicator()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrDestinationIndicator, s))
                .orElse(() -> getAttributeValueList(result, attrDestinationIndicator).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrDestinationIndicator, v)));
        final String attrInternationaliSDNNumber = fessConfig.getLdapAttrInternationaliSDNNumber();
        OptionalUtil.ofNullable(user.getInternationaliSDNNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrInternationaliSDNNumber, s))
                .orElse(() -> getAttributeValueList(result, attrInternationaliSDNNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrInternationaliSDNNumber, v)));
        final String attrState = fessConfig.getLdapAttrState();
        OptionalUtil.ofNullable(user.getState()).filter(StringUtil::isNotBlank).ifPresent(s -> modifyReplaceEntry(modifyList, attrState, s))
                .orElse(() -> getAttributeValueList(result, attrState).stream().forEach(v -> modifyDeleteEntry(modifyList, attrState, v)));
        final String attrEmployeeType = fessConfig.getLdapAttrEmployeeType();
        OptionalUtil.ofNullable(user.getEmployeeType()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrEmployeeType, s))
                .orElse(() -> getAttributeValueList(result, attrEmployeeType).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrEmployeeType, v)));
        final String attrFacsimileTelephoneNumber = fessConfig.getLdapAttrFacsimileTelephoneNumber();
        OptionalUtil.ofNullable(user.getFacsimileTelephoneNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrFacsimileTelephoneNumber, s))
                .orElse(() -> getAttributeValueList(result, attrFacsimileTelephoneNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrFacsimileTelephoneNumber, v)));
        final String attrPostOfficeBox = fessConfig.getLdapAttrPostOfficeBox();
        OptionalUtil.ofNullable(user.getPostOfficeBox()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrPostOfficeBox, s))
                .orElse(() -> getAttributeValueList(result, attrPostOfficeBox).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrPostOfficeBox, v)));
        final String attrInitials = fessConfig.getLdapAttrInitials();
        OptionalUtil.ofNullable(user.getInitials()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrInitials, s)).orElse(() -> getAttributeValueList(result, attrInitials)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrInitials, v)));
        final String attrCarLicense = fessConfig.getLdapAttrCarLicense();
        OptionalUtil.ofNullable(user.getCarLicense()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrCarLicense, s))
                .orElse(() -> getAttributeValueList(result, attrCarLicense).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrCarLicense, v)));
        final String attrMobile = fessConfig.getLdapAttrMobile();
        OptionalUtil.ofNullable(user.getMobile()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrMobile, s)).orElse(() -> getAttributeValueList(result, attrMobile)
                        .stream().forEach(v -> modifyDeleteEntry(modifyList, attrMobile, v)));
        final String attrPostalAddress = fessConfig.getLdapAttrPostalAddress();
        OptionalUtil.ofNullable(user.getPostalAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrPostalAddress, s))
                .orElse(() -> getAttributeValueList(result, attrPostalAddress).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrPostalAddress, v)));
        final String attrCity = fessConfig.getLdapAttrCity();
        OptionalUtil.ofNullable(user.getCity()).filter(StringUtil::isNotBlank).ifPresent(s -> modifyReplaceEntry(modifyList, attrCity, s))
                .orElse(() -> getAttributeValueList(result, attrCity).stream().forEach(v -> modifyDeleteEntry(modifyList, attrCity, v)));
        final String attrTeletexTerminalIdentifier = fessConfig.getLdapAttrTeletexTerminalIdentifier();
        OptionalUtil.ofNullable(user.getTeletexTerminalIdentifier()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrTeletexTerminalIdentifier, s))
                .orElse(() -> getAttributeValueList(result, attrTeletexTerminalIdentifier).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrTeletexTerminalIdentifier, v)));
        final String attrX121Address = fessConfig.getLdapAttrX121Address();
        OptionalUtil.ofNullable(user.getX121Address()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrX121Address, s))
                .orElse(() -> getAttributeValueList(result, attrX121Address).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrX121Address, v)));
        final String attrBusinessCategory = fessConfig.getLdapAttrBusinessCategory();
        OptionalUtil.ofNullable(user.getBusinessCategory()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrBusinessCategory, s))
                .orElse(() -> getAttributeValueList(result, attrBusinessCategory).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrBusinessCategory, v)));
        final String attrRegisteredAddress = fessConfig.getLdapAttrRegisteredAddress();
        OptionalUtil.ofNullable(user.getRegisteredAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrRegisteredAddress, s))
                .orElse(() -> getAttributeValueList(result, attrRegisteredAddress).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrRegisteredAddress, v)));
        final String attrDisplayName = fessConfig.getLdapAttrDisplayName();
        OptionalUtil.ofNullable(user.getDisplayName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrDisplayName, s))
                .orElse(() -> getAttributeValueList(result, attrDisplayName).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrDisplayName, v)));
        final String attrPreferredLanguage = fessConfig.getLdapAttrPreferredLanguage();
        OptionalUtil.ofNullable(user.getPreferredLanguage()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrPreferredLanguage, s))
                .orElse(() -> getAttributeValueList(result, attrPreferredLanguage).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrPreferredLanguage, v)));
        final String attrDepartmentNumber = fessConfig.getLdapAttrDepartmentNumber();
        OptionalUtil.ofNullable(user.getDepartmentNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrDepartmentNumber, s))
                .orElse(() -> getAttributeValueList(result, attrDepartmentNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrDepartmentNumber, v)));
        final String attrUidNumber = fessConfig.getLdapAttrUidNumber();
        OptionalUtil.ofNullable(user.getUidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrUidNumber, s.toString()))
                .orElse(() -> getAttributeValueList(result, attrUidNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrUidNumber, v)));
        final String attrGidNumber = fessConfig.getLdapAttrGidNumber();
        OptionalUtil.ofNullable(user.getGidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrGidNumber, s.toString()))
                .orElse(() -> getAttributeValueList(result, attrGidNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrGidNumber, v)));
        final String attrHomeDirectory = fessConfig.getLdapAttrHomeDirectory();
        OptionalUtil.ofNullable(user.getHomeDirectory()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrHomeDirectory, s))
                .orElse(() -> getAttributeValueList(result, attrHomeDirectory).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrHomeDirectory, v)));

        modify(userDN, modifyList, adminEnv);
    }

    /**
     * Adds user attributes to the LDAP entry for user creation.
     *
     * @param entry the BasicAttributes to add user attributes to
     * @param user the user object containing attribute values
     */
    protected void addUserAttributes(final BasicAttributes entry, final User user) {
        entry.put(new BasicAttribute("cn", user.getName()));
        entry.put(new BasicAttribute("userPassword", user.getOriginalPassword()));

        OptionalUtil.ofNullable(user.getSurname()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrSurname(), s)));
        OptionalUtil.ofNullable(user.getGivenName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrGivenName(), s)));
        OptionalUtil.ofNullable(user.getMail()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrMail(), s)));
        OptionalUtil.ofNullable(user.getEmployeeNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrEmployeeNumber(), s)));
        OptionalUtil.ofNullable(user.getTelephoneNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrTelephoneNumber(), s)));
        OptionalUtil.ofNullable(user.getHomePhone()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrHomePhone(), s)));
        OptionalUtil.ofNullable(user.getHomePostalAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrHomePostalAddress(), s)));
        OptionalUtil.ofNullable(user.getLabeledURI()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrLabeledURI(), s)));
        OptionalUtil.ofNullable(user.getRoomNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrRoomNumber(), s)));
        OptionalUtil.ofNullable(user.getDescription()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrDescription(), s)));
        OptionalUtil.ofNullable(user.getTitle()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrTitle(), s)));
        OptionalUtil.ofNullable(user.getPager()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPager(), s)));
        OptionalUtil.ofNullable(user.getStreet()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrStreet(), s)));
        OptionalUtil.ofNullable(user.getPostalCode()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPostalCode(), s)));
        OptionalUtil.ofNullable(user.getPhysicalDeliveryOfficeName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPhysicalDeliveryOfficeName(), s)));
        OptionalUtil.ofNullable(user.getDestinationIndicator()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrDestinationIndicator(), s)));
        OptionalUtil.ofNullable(user.getInternationaliSDNNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrInternationaliSDNNumber(), s)));
        OptionalUtil.ofNullable(user.getState()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrState(), s)));
        OptionalUtil.ofNullable(user.getEmployeeType()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrEmployeeType(), s)));
        OptionalUtil.ofNullable(user.getFacsimileTelephoneNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrFacsimileTelephoneNumber(), s)));
        OptionalUtil.ofNullable(user.getPostOfficeBox()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPostOfficeBox(), s)));
        OptionalUtil.ofNullable(user.getInitials()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrInitials(), s)));
        OptionalUtil.ofNullable(user.getCarLicense()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrCarLicense(), s)));
        OptionalUtil.ofNullable(user.getMobile()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrMobile(), s)));
        OptionalUtil.ofNullable(user.getPostalAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPostalAddress(), s)));
        OptionalUtil.ofNullable(user.getCity()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrCity(), s)));
        OptionalUtil.ofNullable(user.getTeletexTerminalIdentifier()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrTeletexTerminalIdentifier(), s)));
        OptionalUtil.ofNullable(user.getX121Address()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrX121Address(), s)));
        OptionalUtil.ofNullable(user.getBusinessCategory()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrBusinessCategory(), s)));
        OptionalUtil.ofNullable(user.getRegisteredAddress()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrRegisteredAddress(), s)));
        OptionalUtil.ofNullable(user.getDisplayName()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrDisplayName(), s)));
        OptionalUtil.ofNullable(user.getPreferredLanguage()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrPreferredLanguage(), s)));
        OptionalUtil.ofNullable(user.getDepartmentNumber()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrDepartmentNumber(), s)));
        OptionalUtil.ofNullable(user.getUidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrUidNumber(), s)));
        OptionalUtil.ofNullable(user.getGidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrGidNumber(), s)));
        OptionalUtil.ofNullable(user.getHomeDirectory()).filter(StringUtil::isNotBlank)
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrHomeDirectory(), s)));
    }

    /**
     * Validates user attributes for the specified type.
     *
     * @param type the class type to validate for
     * @param attributes the map of attribute names to values
     * @param consumer the consumer to handle validation errors
     */
    public void validateUserAttributes(final Class<?> type, final Map<String, String> attributes, final Consumer<String> consumer) {
        if (type == Long.class) {
            // Long type attributes
            final String attrUidNumber = fessConfig.getLdapAttrUidNumber();
            final String attrGidNumber = fessConfig.getLdapAttrGidNumber();

            Stream.of(attrUidNumber, attrGidNumber)
                    .forEach(attrName -> OptionalUtil.ofNullable(attributes.get(attrName)).filter(StringUtil::isNotBlank).ifPresent(s -> {
                        try {
                            DfTypeUtil.toLong(s);
                        } catch (final NumberFormatException e) {
                            consumer.accept(attrName);
                        }
                    }));
        } else {
            // do nothing
        }
    }

    /**
     * Deletes a user from the LDAP directory.
     *
     * @param user the user object to delete
     */
    public void delete(final User user) {
        if (!fessConfig.isLdapAdminEnabled(user.getName())) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(user.getName());

        stream(user.getGroupNames()).of(stream -> stream.forEach(name -> {
            search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(name), null, adminEnv, subResult -> {
                if (subResult.isEmpty()) {
                    final Group group = new Group();
                    group.setName(name);
                    insert(group);
                }
                final List<ModificationItem> modifyList = new ArrayList<>();
                modifyDeleteEntry(modifyList, "member", userDN);
                modify(fessConfig.getLdapAdminGroupSecurityPrincipal(name), modifyList, adminEnv);
            });
        }));
        stream(user.getRoleNames()).of(stream -> stream.forEach(name -> {
            search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(name), null, adminEnv, subResult -> {
                if (subResult.isEmpty()) {
                    final Role role = new Role();
                    role.setName(name);
                    insert(role);
                }
                final List<ModificationItem> modifyList = new ArrayList<>();
                modifyDeleteEntry(modifyList, "member", userDN);
                modify(fessConfig.getLdapAdminRoleSecurityPrincipal(name), modifyList, adminEnv);
            });
        }));

        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(user.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                delete(userDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", user.getName());
            }
        });

    }

    /**
     * Inserts or updates a role in the LDAP directory.
     *
     * @param role the role object to insert or update
     */
    public void insert(final Role role) {
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                logger.info("{} exists in LDAP server.", role.getName());
            } else {
                final String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                final BasicAttributes entry = new BasicAttributes();
                addRoleAttributes(entry, role);
                final Attribute oc = fessConfig.getLdapAdminRoleObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry, adminEnv);
            }
        });

    }

    /**
     * Adds role attributes to the LDAP entry for role creation.
     *
     * @param entry the BasicAttributes to add role attributes to
     * @param user the role object containing attribute values
     */
    protected void addRoleAttributes(final BasicAttributes entry, final Role user) {
        // nothing
    }

    /**
     * Deletes a role from the LDAP directory.
     *
     * @param role the role object to delete
     */
    public void delete(final Role role) {
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        search(fessConfig.getLdapAdminRoleBaseDn(), fessConfig.getLdapAdminRoleFilter(role.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                final String entryDN = fessConfig.getLdapAdminRoleSecurityPrincipal(role.getName());
                delete(entryDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", role.getName());
            }
        });

    }

    /**
     * Applies LDAP attributes to a group object.
     *
     * @param group the group object to populate with LDAP attributes
     */
    public void apply(final Group group) {
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                setAttributeValue(result, fessConfig.getLdapAttrGidNumber(), o -> group.setGidNumber(DfTypeUtil.toLong(o)));
            }
        });
    }

    /**
     * Inserts or updates a group in the LDAP directory.
     *
     * @param group the group object to insert or update
     */
    public void insert(final Group group) {
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        final String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                logger.info("{} exists in LDAP server.", group.getName());
                modifyGroupAttributes(group, adminEnv, entryDN, result);
            } else {
                final BasicAttributes entry = new BasicAttributes();
                addGroupAttributes(entry, group);
                final Attribute oc = fessConfig.getLdapAdminGroupObjectClassAttribute();
                entry.put(oc);
                insert(entryDN, entry, adminEnv);
            }
        });
    }

    /**
     * Modifies group attributes in the LDAP directory.
     *
     * @param group the group object with new attribute values
     * @param adminEnv the supplier for admin environment
     * @param entryDN the DN of the group entry
     * @param result the search results containing current attributes
     */
    protected void modifyGroupAttributes(final Group group, final Supplier<Hashtable<String, String>> adminEnv, final String entryDN,
            final List<SearchResult> result) {
        final List<ModificationItem> modifyList = new ArrayList<>();

        final String attrGidNumber = fessConfig.getLdapAttrGidNumber();
        OptionalUtil.ofNullable(group.getGidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> modifyReplaceEntry(modifyList, attrGidNumber, s.toString()))
                .orElse(() -> getAttributeValueList(result, attrGidNumber).stream()
                        .forEach(v -> modifyDeleteEntry(modifyList, attrGidNumber, v)));

        modify(entryDN, modifyList, adminEnv);
    }

    /**
     * Adds group attributes to the LDAP entry for group creation.
     *
     * @param entry the BasicAttributes to add group attributes to
     * @param group the group object containing attribute values
     */
    protected void addGroupAttributes(final BasicAttributes entry, final Group group) {
        OptionalUtil.ofNullable(group.getGidNumber()).filter(s -> StringUtil.isNotBlank(s.toString()))
                .ifPresent(s -> entry.put(new BasicAttribute(fessConfig.getLdapAttrGidNumber(), s)));
    }

    /**
     * Validates group attributes for the specified type.
     *
     * @param type the class type to validate for
     * @param attributes the map of attribute names to values
     * @param consumer the consumer to handle validation errors
     */
    public void validateGroupAttributes(final Class<?> type, final Map<String, String> attributes, final Consumer<String> consumer) {
        if (type == Long.class) {
            // Long type attributes
            final String attrGidNumber = fessConfig.getLdapAttrGidNumber();

            Stream.of(attrGidNumber)
                    .forEach(attrName -> OptionalUtil.ofNullable(attributes.get(attrName)).filter(StringUtil::isNotBlank).ifPresent(s -> {
                        try {
                            DfTypeUtil.toLong(s);
                        } catch (final NumberFormatException e) {
                            consumer.accept(attrName);
                        }
                    }));
        } else {
            // do nothing
        }
    }

    /**
     * Deletes a group from the LDAP directory.
     *
     * @param group the group object to delete
     */
    public void delete(final Group group) {
        if (!fessConfig.isLdapAdminEnabled()) {
            return;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        search(fessConfig.getLdapAdminGroupBaseDn(), fessConfig.getLdapAdminGroupFilter(group.getName()), null, adminEnv, result -> {
            if (!result.isEmpty()) {
                final String entryDN = fessConfig.getLdapAdminGroupSecurityPrincipal(group.getName());
                delete(entryDN, adminEnv);
            } else {
                logger.info("{} does not exist in LDAP server.", group.getName());
            }
        });
    }

    /**
     * Changes the password for a user in the LDAP directory.
     *
     * @param username the username of the user
     * @param password the new password
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(final String username, final String password) {
        if (!fessConfig.isLdapAdminEnabled(username)) {
            return false;
        }

        final Supplier<Hashtable<String, String>> adminEnv = this::createAdminEnv;
        final String userDN = fessConfig.getLdapAdminUserSecurityPrincipal(username);
        search(fessConfig.getLdapAdminUserBaseDn(), fessConfig.getLdapAdminUserFilter(username), null, adminEnv, result -> {
            if (result.isEmpty()) {
                throw new LdapOperationException("User is not found: " + username);
            }
            final List<ModificationItem> modifyList = new ArrayList<>();
            modifyReplaceEntry(modifyList, "userPassword", password);
            modify(userDN, modifyList, adminEnv);
        });
        return true;
    }

    /**
     * Inserts a new entry into the LDAP directory.
     *
     * @param entryDN the DN of the entry to insert
     * @param entry the attributes of the entry
     * @param envSupplier the supplier for environment configuration
     */
    protected void insert(final String entryDN, final Attributes entry, final Supplier<Hashtable<String, String>> envSupplier) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            logger.debug("Inserting {}", entryDN);
            holder.get().createSubcontext(entryDN, entry);
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to add " + entryDN, e);
        }
    }

    /**
     * Deletes an entry from the LDAP directory.
     *
     * @param entryDN the DN of the entry to delete
     * @param envSupplier the supplier for environment configuration
     */
    protected void delete(final String entryDN, final Supplier<Hashtable<String, String>> envSupplier) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            logger.debug("Deleting {}", entryDN);
            holder.get().destroySubcontext(entryDN);
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to delete " + entryDN, e);
        }
    }

    /**
     * Searches the LDAP directory with the specified parameters.
     *
     * @param baseDn the base DN for the search
     * @param filter the search filter
     * @param returningAttrs the attributes to return from the search
     * @param envSupplier the supplier for environment configuration
     * @param consumer the consumer to handle search results
     */
    protected void search(final String baseDn, final String filter, final String[] returningAttrs,
            final Supplier<Hashtable<String, String>> envSupplier, final SearchConsumer consumer) {
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            final SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            if (returningAttrs != null) {
                controls.setReturningAttributes(returningAttrs);
            }

            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            final long startTime = systemHelper.getCurrentTimeAsLong();
            final List<SearchResult> list = Collections.list(holder.get().search(baseDn, filter, controls));
            if (logger.isDebugEnabled()) {
                logger.debug("LDAP search[{}ms]: {} - {}", systemHelper.getCurrentTimeAsLong() - startTime, baseDn, filter);
            }
            consumer.accept(list);
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to search " + baseDn + " with " + filter, e);
        }
    }

    /**
     * Modifies an entry by adding a new attribute.
     *
     * @param modifyList The list of modification items.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    protected void modifyAddEntry(final List<ModificationItem> modifyList, final String name, final String value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    /**
     * Modifies an entry by replacing an attribute.
     *
     * @param modifyList The list of modification items.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    protected void modifyReplaceEntry(final List<ModificationItem> modifyList, final String name, final String value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    /**
     * Modifies an entry by deleting an attribute.
     *
     * @param modifyList The list of modification items.
     * @param name The name of the attribute.
     * @param value The value of the attribute.
     */
    protected void modifyDeleteEntry(final List<ModificationItem> modifyList, final String name, final Object value) {
        final Attribute attr = new BasicAttribute(name, value);
        final ModificationItem mod = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr);
        modifyList.add(mod);
    }

    /**
     * Modifies an entry.
     *
     * @param dn The DN of the entry.
     * @param modifyList The list of modification items.
     * @param envSupplier The environment supplier.
     */
    protected void modify(final String dn, final List<ModificationItem> modifyList, final Supplier<Hashtable<String, String>> envSupplier) {
        if (modifyList.isEmpty()) {
            return;
        }
        try (DirContextHolder holder = getDirContext(envSupplier)) {
            holder.get().modifyAttributes(dn, modifyList.toArray(new ModificationItem[modifyList.size()]));
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to search " + dn, e);
        }
    }

    /**
     * An interface for consuming search results.
     */
    interface SearchConsumer {
        /**
         * Accepts a list of search results.
         *
         * @param t The list of search results.
         * @throws NamingException If a naming exception occurs.
         */
        void accept(List<SearchResult> t) throws NamingException;
    }

    /**
     * Gets the directory context.
     *
     * @param envSupplier The environment supplier.
     * @return The directory context holder.
     */
    protected DirContextHolder getDirContext(final Supplier<Hashtable<String, String>> envSupplier) {
        DirContextHolder holder = contextLocal.get();
        if (holder != null) {
            holder.inc();
            return holder;
        }
        final Hashtable<String, String> env = envSupplier.get();
        try {
            holder = new DirContextHolder(new InitialDirContext(env));
            contextLocal.set(holder);
            return holder;
        } catch (final NamingException e) {
            throw new LdapOperationException("Failed to create DirContext.", e);
        }
    }

    /**
     * A holder for the directory context.
     */
    protected class DirContextHolder implements AutoCloseable {
        private final DirContext context;

        private int counter = 1;

        /**
         * Constructs a new directory context holder.
         *
         * @param context The directory context.
         */
        protected DirContextHolder(final DirContext context) {
            this.context = context;
        }

        /**
         * Returns the directory context.
         *
         * @return The directory context.
         */
        public DirContext get() {
            return context;
        }

        /**
         * Increments the counter.
         */
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
