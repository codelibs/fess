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
package org.codelibs.fess.app.web.base.login;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.sso.entraid.EntraIdAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * Microsoft Entra ID credential implementation for Fess authentication.
 * Provides login credential functionality using Entra ID authentication results.
 */
public class EntraIdCredential implements LoginCredential, FessCredential {

    private static final Logger logger = LogManager.getLogger(EntraIdCredential.class);

    private final IAuthenticationResult authResult;

    /**
     * Constructs an Entra ID credential with the authentication result.
     * @param authResult The authentication result from Entra ID.
     */
    public EntraIdCredential(final IAuthenticationResult authResult) {
        this.authResult = authResult;
    }

    @Override
    public String getUserId() {
        return authResult.account().username();
    }

    @Override
    public String toString() {
        return "{" + authResult.account().username() + "}";
    }

    /**
     * Gets the Entra ID user associated with this credential.
     * @return The Entra ID user instance.
     */
    public EntraIdUser getUser() {
        return new EntraIdUser(authResult);
    }

    /**
     * Entra ID user implementation providing user information and permissions.
     */
    public static class EntraIdUser implements FessUser {
        private static final long serialVersionUID = 1L;

        /**
         * How long before the access token expires {@link #refresh()} starts asking MSAL4J for a
         * new one. It matches MSAL4J's own expiry buffer, so the token is renewed at the same
         * instant it always was; what the guard removes is the silent acquisition -- and the
         * Microsoft Graph call behind it -- on every other request.
         */
        protected static final long REFRESH_MARGIN = 5 * 60 * 1000L;

        /** User's group memberships. */
        protected volatile String[] groups;

        /** User's role assignments. */
        protected volatile String[] roles;

        /** User's computed permissions. */
        protected volatile String[] permissions;

        /**
         * Entra ID authentication result. Volatile because {@link #refresh()} replaces it from
         * whichever request thread wins the renewal while the other request threads sharing this
         * session-scoped instance keep reading it.
         */
        protected volatile IAuthenticationResult authResult;

        /**
         * How far this user's group and role permissions have got. Volatile because the resolution
         * runs on a TimeoutManager thread while request threads read it.
         *
         * <p>Starts PENDING: unlike every other {@code FessUser}, this one is handed out before its
         * memberships exist.
         */
        protected volatile PermissionState permissionState = PermissionState.PENDING;

        /**
         * Whether a membership resolution has ever run to completion for this user -- whether it
         * reached Microsoft Graph or fell back to the configured defaults. Volatile for the same
         * reason as {@link #permissionState}: written on a TimeoutManager thread, read on request
         * threads.
         *
         * <p>An explicit flag rather than {@code groups == null}, which is what it used to be
         * inferred from: the constructor now seeds the configured defaults, so the memberships are
         * never null and every resolution would look like a re-resolution -- keeping the seeded
         * defaults forever instead of writing the resolved groups.
         */
        protected volatile boolean resolutionCompleted;

        /**
         * Set for as long as one thread is inside the silent acquisition in {@link #refresh()}.
         * A plain flag rather than a lock: the losing threads must carry on with the token they
         * already hold instead of queueing behind an acquisition that can take tens of seconds.
         */
        private final AtomicBoolean refreshing = new AtomicBoolean();

        /**
         * Constructs an Entra ID user with the authentication result.
         * @param authResult The authentication result from Entra ID.
         */
        public EntraIdUser(final IAuthenticationResult authResult) {
            this.authResult = authResult;
            final EntraIdAuthenticator authenticator = ComponentUtil.getComponent(EntraIdAuthenticator.class);
            // The configured defaults are static -- no Graph call stands behind them -- so they
            // apply from the first request rather than only once the background resolution lands.
            // SsoAction redirects straight to the search page after login, so without this the
            // first results a user sees are those of someone holding no groups at all.
            authenticator.applyDefaultMemberships(this);
            authenticator.scheduleUpdateMemberOf(this);
        }

        @Override
        public String getName() {
            return authResult.account().username();
        }

        @Override
        public String[] getRoleNames() {
            return roles;
        }

        @Override
        public String[] getGroupNames() {
            return groups;
        }

        @Override
        public synchronized String[] getPermissions() {
            // Synchronized on the same monitor as setGroups/setRoles/resetPermissions. Computing
            // the value is a check-then-act -- read `permissions == null`, read `groups`, write
            // `permissions` -- and the membership resolution scheduled at login runs on a
            // TimeoutManager thread while the user is already searching. Without the lock a reader
            // that started before it lands can finish after and overwrite the freshly reset value
            // with one computed from stale (or absent) groups; nothing resets it again, so those
            // permissions stay wrong for the rest of the session.
            if (permissions == null) {
                final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
                final Set<String> permissionSet = new HashSet<>();
                final IAccount account = authResult.account();
                final String homeAccountId = account.homeAccountId();
                final String username = account.username();
                if (logger.isDebugEnabled()) {
                    logger.debug("homeAccountId={}, username={}", homeAccountId, username);
                }
                permissionSet.add(systemHelper.getSearchRoleByUser(homeAccountId));
                permissionSet.add(systemHelper.getSearchRoleByUser(username));
                if (ComponentUtil.getFessConfig().isEntraIdUseDomainServices() && username.indexOf('@') >= 0) {
                    final String[] values = username.split("@");
                    if (values.length > 1) {
                        permissionSet.add(systemHelper.getSearchRoleByUser(values[0]));
                    }
                }
                stream(groups).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByGroup(s))));
                stream(roles).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByRole(s))));
                permissions = permissionSet.stream().filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]);
            }
            return permissions;
        }

        @Override
        public boolean refresh() {
            // MSAL4J handles token refresh internally through silent authentication
            // Check if token is still valid by comparing absolute timestamps
            final long tokenExpiryTime = authResult.expiresOnDate().getTime(); // milliseconds since epoch
            final long currentTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong(); // milliseconds since epoch
            if (tokenExpiryTime < currentTime) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Token expired: expiryTime={}, currentTime={}", tokenExpiryTime, currentTime);
                }
                return false;
            }
            if (tokenExpiryTime - currentTime > REFRESH_MARGIN) {
                // FessBaseAction.godHandPrologue calls this on every action request; a silent
                // acquisition is a network call, so it must not happen per request. Until the
                // token is close to expiring there is nothing to acquire, and the groups this
                // user was given at login are still the ones Entra ID issued them.
                if (logger.isDebugEnabled()) {
                    logger.debug("Token is still valid for {}ms. Skipping silent authentication.", tokenExpiryTime - currentTime);
                }
                return true;
            }
            // Lastaflute keeps one FessUserBean -- and therefore one EntraIdUser -- as a session
            // attribute, and FessBaseAction.godHandPrologue calls refresh() on every action
            // request, so all the requests a session has in flight arrive here together once the
            // token enters REFRESH_MARGIN. Each of them would see a renewed token and schedule its
            // own updateMemberOf task, and the last one to assign would decide which of the
            // results authResult ends up holding. One renewal per rollover is enough.
            // Not a lock, and deliberately not a synchronized method: the acquisition runs for up
            // to the authenticator's acquisition timeout, and getPermissions() takes this object's
            // monitor, so waiting here would stall every concurrent search of this user for that
            // whole time.
            if (!refreshing.compareAndSet(false, true)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Another request is already renewing the token. Skipping silent authentication.");
                }
                // The token this thread holds has not expired yet -- the check above proved it --
                // so the request may proceed while the winner renews.
                return true;
            }
            // Attempt to refresh token using MSAL4J silent authentication
            try {
                final EntraIdAuthenticator authenticator = ComponentUtil.getComponent(EntraIdAuthenticator.class);
                final IAuthenticationResult newResult = authenticator.refreshTokenSilently(this);
                if (newResult != null) {
                    // MSAL4J rounds its own buffer down to whole seconds, so for up to a second
                    // either side of REFRESH_MARGIN it hands back the token it already had.
                    // Re-reading the directory for a token that did not change would put the
                    // per-request Graph call straight back.
                    final boolean renewed = !newResult.accessToken().equals(authResult.accessToken());
                    authResult = newResult;
                    if (renewed) {
                        // Scheduled, not called: this runs on a request thread, and updateMemberOf
                        // reaches Microsoft Graph. It resets the permissions itself when it lands.
                        authenticator.scheduleUpdateMemberOf(this);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Silent authentication succeeded. renewed={}", renewed);
                    }
                    return true;
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Silent token refresh failed: {}", e.getMessage());
                }
            } finally {
                refreshing.set(false);
            }
            // For MSAL4J, if silent refresh fails, return true if token is still valid
            // Actual refresh will happen during next authentication request
            return true;
        }

        /**
         * Gets the Entra ID authentication result.
         * @return The authentication result.
         */
        public IAuthenticationResult getAuthenticationResult() {
            return authResult;
        }

        /**
         * Sets the user's group memberships.
         * @param groups Array of group names.
         */
        public synchronized void setGroups(final String[] groups) {
            this.groups = groups;
        }

        /**
         * Sets the user's role assignments.
         * @param roles Array of role names.
         */
        public synchronized void setRoles(final String[] roles) {
            this.roles = roles;
        }

        @Override
        public PermissionState getPermissionState() {
            return permissionState;
        }

        /**
         * Records how far the group and role resolution has got.
         * @param permissionState The state.
         */
        public void setPermissionState(final PermissionState permissionState) {
            this.permissionState = permissionState;
        }

        /**
         * Whether a membership resolution has ever run to completion for this user.
         * @return True once one has, so that a later one is a re-resolution.
         */
        public boolean isResolutionCompleted() {
            return resolutionCompleted;
        }

        /**
         * Records that a membership resolution has run to completion, so that the next one is a
         * re-resolution and must not overwrite what this one wrote with the defaults alone.
         */
        public void markResolutionCompleted() {
            this.resolutionCompleted = true;
        }

        /**
         * Resets permissions to force recalculation on next getPermissions() call.
         * Called from within {@code updateMemberOf}, before the permission state write, once the
         * asynchronous membership resolution has the new groups and roles in hand.
         */
        public synchronized void resetPermissions() {
            this.permissions = null;
        }
    }
}
