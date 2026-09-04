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
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

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

        /**
         * How long a silent acquisition that failed is left alone before another one is attempted
         * for this session. A refresh token that has been revoked, an account that has been
         * disabled, and an account a {@code logout()} elsewhere evicted from the shared MSAL4J
         * cache all fail for good, and {@link #refresh()} runs on every action request, so
         * retrying one unconditionally would put back the per-request round trip
         * {@link #REFRESH_MARGIN} was introduced to remove. A minute matches the backoff
         * {@code EntraIdAuthenticator} applies to a throttled Microsoft Graph, holds a session
         * whose renewal cannot succeed to one acquisition a minute rather than one per request,
         * and is short enough that a failure early in {@link #REFRESH_MARGIN} still leaves four
         * more attempts before the token actually expires.
         */
        protected static final long RENEWAL_THROTTLE_INTERVAL = 60 * 1000L;

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
         * Point in time, as epoch milliseconds, until which {@link #refresh()} attempts no further
         * silent acquisition. Zero means none has failed yet. Written by whichever request thread
         * ran the failing acquisition while the other request threads sharing this session-scoped
         * instance keep reading it, hence volatile.
         */
        protected volatile long renewalThrottledUntil;

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
                final String objectId = getObjectId();
                final String username = account.username();
                if (logger.isDebugEnabled()) {
                    logger.debug("objectId={}, username={}", objectId, username);
                }
                if (StringUtil.isNotBlank(objectId)) {
                    permissionSet.add(systemHelper.getSearchRoleByDirectoryUser(objectId));
                }
                permissionSet.add(systemHelper.getSearchRoleByDirectoryUser(username));
                if (ComponentUtil.getFessConfig().isEntraIdUseDomainServices() && username.indexOf('@') >= 0) {
                    final String[] values = username.split("@");
                    if (values.length > 1) {
                        permissionSet.add(systemHelper.getSearchRoleByDirectoryUser(values[0]));
                    }
                }
                stream(groups).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByDirectoryGroup(s))));
                stream(roles).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByDirectoryRole(s))));
                permissions = permissionSet.stream().filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]);
            }
            return permissions;
        }

        /**
         * Reads the {@code oid} claim -- the user's object id in this tenant -- out of the ID
         * token.
         *
         * <p>Microsoft Graph names a user by that object id, so it is the value a crawler writes
         * into the {@code role} field of a document this user owns. {@code IAccount} exposes no
         * plain object id of its own: {@code homeAccountId()} is MSAL4J's own account key, and
         * {@code getTenantProfiles()} is null on the account an {@code IAuthenticationResult}
         * carries.
         *
         * @return The object id, or null when the ID token carries none.
         */
        protected String getObjectId() {
            final String idToken = authResult.idToken();
            if (StringUtil.isBlank(idToken)) {
                logger.warn("No ID token for {}. The object id permission is not granted.", getName());
                return null;
            }
            try {
                final JWTClaimsSet claimsSet = JWTParser.parse(idToken).getJWTClaimsSet();
                if (claimsSet != null) {
                    return claimsSet.getStringClaim("oid");
                }
                logger.warn("The ID token of {} carries no claims. The object id permission is not granted.", getName());
            } catch (final Exception e) {
                logger.warn("Failed to read the oid claim of {}. The object id permission is not granted.", getName(), e);
            }
            return null;
        }

        @Override
        public boolean refresh() {
            // MSAL4J handles token refresh internally through silent authentication
            // Check if token is still valid by comparing absolute timestamps
            final long tokenExpiryTime = authResult.expiresOnDate().getTime(); // milliseconds since epoch
            final long currentTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong(); // milliseconds since epoch
            final boolean expired = tokenExpiryTime < currentTime;
            if (!expired && tokenExpiryTime - currentTime > REFRESH_MARGIN) {
                // FessBaseAction.godHandPrologue calls this on every action request; a silent
                // acquisition is a network call, so it must not happen per request. Until the
                // token is close to expiring there is nothing to acquire, and the groups this
                // user was given at login are still the ones Entra ID issued them.
                if (logger.isDebugEnabled()) {
                    logger.debug("Token is still valid for {}ms. Skipping silent authentication.", tokenExpiryTime - currentTime);
                }
                return true;
            }
            // An expired access token still goes through the acquisition below rather than
            // straight out of here. MSAL4J's silent flow spends the cached refresh token, which
            // outlives the access token by hours, so a user who was idle across the expiry is
            // recoverable -- and giving up instead was permanent, because godHandPrologue
            // discards this result: nothing logged the user out, and every later request took the
            // same early exit, so the session kept a dead token and stopped re-reading its group
            // memberships for as long as it lasted.
            //
            // Attempting it is not free, though: an acquisition that cannot succeed would be
            // repeated on every request of a session that keeps searching, so one failure holds
            // the next attempt off for RENEWAL_THROTTLE_INTERVAL.
            if (isRenewalThrottled(currentTime)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("A silent authentication has just failed. Not retrying before {}. expired={}", renewalThrottledUntil,
                            expired);
                }
                return !expired;
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
                // A token that has not expired yet lets this request proceed while the winner
                // renews. An expired one does not, and whether the winner recovers it is not this
                // thread's to report.
                return !expired;
            }
            // Attempt to refresh token using MSAL4J silent authentication
            try {
                final EntraIdAuthenticator authenticator = ComponentUtil.getComponent(EntraIdAuthenticator.class);
                final IAuthenticationResult newResult = authenticator.refreshTokenSilently(this);
                if (newResult != null && newResult.expiresOnDate().getTime() >= currentTime) {
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
                // refreshTokenSilently answers null instead of throwing, so a revoked refresh
                // token, a disabled account, and an account evicted from the shared MSAL4J cache
                // all arrive here rather than in the catch below. A result that is itself already
                // expired is treated the same way: keeping it would leave nothing to renew from
                // and no record that the renewal has to be held off.
                applyRenewalThrottle(currentTime);
                logger.warn("Silent authentication returned no usable access token for {}. expired={}. Next attempt in {} seconds.",
                        getName(), expired, RENEWAL_THROTTLE_INTERVAL / 1000L);
            } catch (final Exception e) {
                // At WARN, not DEBUG: this is the same anti-pattern #3218 removed from
                // getLoginCredential, where a login that failed was invisible unless debug
                // logging happened to be on. The throttle applied first is what keeps a
                // persistent failure to one line per interval instead of one per request.
                applyRenewalThrottle(currentTime);
                logger.warn("Failed to renew the access token of {}. expired={}. Next attempt in {} seconds.", getName(), expired,
                        RENEWAL_THROTTLE_INTERVAL / 1000L, e);
            } finally {
                refreshing.set(false);
            }
            // The silent acquisition produced nothing. A token that has not expired yet still
            // authorises this request and MSAL4J is asked again once the throttle lapses, but an
            // expired one leaves nothing to carry on with.
            return !expired;
        }

        /**
         * Returns whether a silent acquisition failed recently enough that another one has to wait.
         *
         * @param currentTime The current time in epoch milliseconds.
         * @return True while the silent acquisition has to be skipped.
         */
        protected boolean isRenewalThrottled(final long currentTime) {
            final long until = renewalThrottledUntil;
            return until > 0L && currentTime < until;
        }

        /**
         * Records that a silent acquisition produced no usable token, so that the requests this
         * session makes over the next {@link #RENEWAL_THROTTLE_INTERVAL} do not repeat it.
         *
         * <p>Shaped after {@code EntraIdAuthenticator#applyGraphThrottle}, with a fixed interval
         * rather than a negotiated one: MSAL4J reports the failure as a null result, so there is
         * no {@code Retry-After} to read.
         *
         * @param currentTime The current time in epoch milliseconds.
         */
        protected void applyRenewalThrottle(final long currentTime) {
            renewalThrottledUntil = currentTime + RENEWAL_THROTTLE_INTERVAL;
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
