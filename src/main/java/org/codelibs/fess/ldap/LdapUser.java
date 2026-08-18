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

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.commons.lang3.ArrayUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

/**
 * An LDAP user.
 */
public class LdapUser implements FessUser {

    private static final long serialVersionUID = 1L;

    /** The environment for LDAP connection. */
    protected Hashtable<String, String> env;

    /** The name of the user. */
    protected String name;

    /**
     * Whether {@link #name} was asserted by an identity provider rather than typed at a login form.
     *
     * <p>Decides whether the user permission may be re-read as a NetBIOS-qualified {@code
     * DOMAIN\name}. A name typed at the login form may carry that qualifier and dropping it is what
     * makes the permission match the documents; a name an identity provider asserted carries the
     * account's own characters, so taking the tail of a backslash inside it names a different
     * account's permission.
     */
    protected final boolean nameFromProvider;

    /**
     * Whether the nested-group walk has already published its result.
     *
     * <p>Guards the synchronous write below. {@link LdapManager#getRoles} schedules that walk and
     * then returns, so the assignment of its return value happens after the walk was handed to the
     * timer -- and if the walk finished in between, assigning would overwrite the fuller set with
     * the direct-only one and leave the state reporting {@code RESOLVED} over it. The remaining
     * check-then-act window is a few instructions wide against an LDAP round trip.
     */
    protected volatile boolean nestedRolesPublished;

    /**
     * The permissions of the user.
     *
     * <p>Volatile because the nested-group resolution writes it from a {@code TimeoutManager}
     * thread while request threads read it. It is deliberately not guarded by a lock the way
     * {@code EntraIdUser} guards its own: the first read of this field performs the directory
     * search inline, so a lock here would be held across an LDAP round trip and the background
     * writer would block a pool thread behind it. The remaining race is two concurrent first
     * requests each running the search, which costs duplicate work but converges.
     */
    protected volatile String[] permissions = null;

    /**
     * How far this user's group and role permissions have got.
     *
     * <p>Starts {@link PermissionState#RESOLVED} rather than {@code PENDING}: unlike an Entra ID
     * user, this one resolves its permissions inline on first read, and the nested-group walk is
     * only scheduled when {@code ldap.group.filter} is configured. With the shipped defaults no
     * background work is ever scheduled, so the synchronous result is the whole answer and there
     * is no window to report. {@link LdapManager#getRoles} moves it to {@code PENDING} at the
     * moment it actually schedules that walk.
     *
     * <p>Volatile for the same reason as {@link #permissions}. {@code RESOLVED} and {@code FAILED}
     * are always written <em>after</em> the permissions they describe, so a reader that sees
     * {@code RESOLVED} cannot see stale ones. {@code PENDING} is written before the first
     * permissions exist at all, which is the state's whole point.
     */
    protected volatile PermissionState permissionState = PermissionState.RESOLVED;

    /**
     * Constructs a new LDAP user.
     *
     * @param env The environment for LDAP connection.
     * @param name The name of the user.
     */
    public LdapUser(final Hashtable<String, String> env, final String name) {
        this(env, name, false);
    }

    /**
     * Constructs a new LDAP user.
     *
     * @param env The environment for LDAP connection.
     * @param name The name of the user.
     * @param nameFromProvider Whether the name was asserted by an identity provider rather than
     *            typed at a login form.
     */
    public LdapUser(final Hashtable<String, String> env, final String name, final boolean nameFromProvider) {
        this.env = env;
        this.name = name;
        this.nameFromProvider = nameFromProvider;
    }

    /**
     * Returns whether this user's name was asserted by an identity provider.
     *
     * @return true when the name came from a provider, false when a user typed it.
     */
    public boolean isNameFromProvider() {
        return nameFromProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getPermissions() {
        if (permissions == null) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final String baseDn = fessConfig.getLdapBaseDn();
            final String accountFilter = fessConfig.getLdapAccountFilter();
            final String groupFilter = fessConfig.getLdapGroupFilter();
            if (StringUtil.isNotBlank(baseDn) && StringUtil.isNotBlank(accountFilter)) {
                final LdapManager ldapManager = ComponentUtil.getLdapManager();
                // Appended to both writes, not just the synchronous one. LdapManager derives its own
                // user entry through getCanonicalLdapName, which drops a NetBIOS "DOMAIN\" prefix,
                // and adds it only when ldap.role.search.user.enabled is set -- so a lazy write that
                // carried only what LdapManager collected would hand back a strictly smaller set
                // than the synchronous result, and the user would silently lose their own
                // permission at the moment the nested-group walk succeeded.
                final String userPermission = fessConfig.getRoleSearchUserPrefix() + ldapManager.normalizePermissionName(getName());
                final String[] directPermissions =
                        distinct(ArrayUtils.addAll(ldapManager.getRoles(this, baseDn, accountFilter, groupFilter, roles -> {
                            permissions = distinct(ArrayUtils.addAll(roles, userPermission));
                            // Written after the permissions it describes, so a reader that sees the
                            // flag cannot see the set it replaced.
                            nestedRolesPublished = true;
                            ComponentUtil.getActivityHelper().permissionChanged(OptionalThing.of(new FessUserBean(this)));
                        }), userPermission));
                if (!nestedRolesPublished) {
                    permissions = directPermissions;
                }
            } else {
                permissions = StringUtil.EMPTY_STRINGS;
            }
        }
        return permissions;
    }

    /**
     * {@inheritDoc}
     *
     * <p>A bare field read. It must stay one: this is called on every request by
     * {@code FessSearchAction#hookBefore}, and {@link #getPermissions()} performs the directory
     * search inline, so anything that consulted the permissions here -- including
     * {@link #getGroupNames()} and {@link #getRoleNames()}, which derive from them -- would turn a
     * status check into an LDAP round trip.
     */
    @Override
    public PermissionState getPermissionState() {
        // Null-checked, not just returned. This class is Serializable and lives in the session, and
        // serialVersionUID is unchanged, so a session written by a release without this field
        // deserializes with it null -- field initializers do not run during deserialization. Tomcat's
        // default StandardManager persists sessions across a restart into a directory that survives
        // a package upgrade, so that stream is a real upgrade path, and the interface documents this
        // as never null.
        final PermissionState state = permissionState;
        return state == null ? PermissionState.RESOLVED : state;
    }

    /**
     * Records how far this user's group and role permissions have got.
     *
     * <p>Called by {@link LdapManager} around the nested-group walk. Always write the permissions
     * before the state that describes them.
     *
     * @param permissionState The state, never null.
     */
    public void setPermissionState(final PermissionState permissionState) {
        this.permissionState = permissionState;
    }

    @Override
    public String[] getRoleNames() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return stream(getPermissions()).get(stream -> stream.filter(s -> s.startsWith(fessConfig.getRoleSearchRolePrefix()))
                .map(s -> s.substring(1))
                .toArray(n -> new String[n]));
    }

    @Override
    public String[] getGroupNames() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return stream(getPermissions()).get(stream -> stream.filter(s -> s.startsWith(fessConfig.getRoleSearchGroupPrefix()))
                .map(s -> s.substring(1))
                .toArray(n -> new String[n]));
    }

    /**
     * Returns the environment for LDAP connection.
     *
     * @return The environment for LDAP connection.
     */
    public Hashtable<String, String> getEnvironment() {
        return env;
    }

    @Override
    public boolean isEditable() {
        return ComponentUtil.getFessConfig().isLdapAdminEnabled(name);
    }

    private static String[] distinct(final String[] values) {
        if (values == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        if (values.length < 2) {
            return values;
        }
        return Arrays.stream(values).distinct().toArray(n -> new String[n]);
    }

}
