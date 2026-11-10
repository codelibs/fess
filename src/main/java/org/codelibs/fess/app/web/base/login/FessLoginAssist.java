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

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.OptionalLong;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.login.LoginHandlingResource;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.login.PrimaryLoginManager;
import org.lastaflute.web.login.TypicalLoginAssist;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginRequiredException;
import org.lastaflute.web.login.option.LoginSpecifiedOption;
import org.lastaflute.web.servlet.session.SessionManager;

import jakarta.annotation.Resource;

/**
 * The assist for login handling in the Fess application.
 * This class extends TypicalLoginAssist to provide Fess-specific login functionality
 * including user authentication, permission checking, and login history management.
 *
 */
public class FessLoginAssist extends TypicalLoginAssist<String, FessUserBean, FessUser> // #change_it also UserBean
        implements PrimaryLoginManager {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(FessLoginAssist.class);

    /** The seconds used when {@code password.changed.check.interval} is not a number. */
    protected static final int DEFAULT_PASSWORD_CHECK_INTERVAL = 60;

    /**
     * Default constructor.
     */
    public FessLoginAssist() {
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The time manager for handling time-related operations. */
    @Resource
    private TimeManager timeManager;

    /** The async manager for handling asynchronous operations. */
    @Resource
    private AsyncManager asyncManager;

    /** The session manager for handling user sessions. */
    @Resource
    private SessionManager sessionManager;

    /** The Fess configuration providing application settings. */
    @Resource
    private FessConfig fessConfig;

    /**
     * Returns the behavior for user entities.
     *
     * <p>Resolved on demand rather than bound at construction. This component is the login
     * manager LastaFlute looks up whenever something asks who the current user is, and
     * answering that question reads the session, not the user index. Binding {@link UserBhv}
     * as a resource made the whole component unbuildable in any container that has not wired
     * the search engine client, because the behavior in turn requires that client. LastaFlute
     * answers "nobody is logged in" when it cannot look the login manager up, but only for a
     * manager it cannot find - one it finds and cannot build raised the binding failure at the
     * caller instead.</p>
     *
     * @return the behavior for user entities
     */
    protected UserBhv getUserBhv() {
        return ComponentUtil.getComponent(UserBhv.class);
    }

    // ===================================================================================
    //                                                                           Find User
    //                                                                           =========
    /**
     * Checks if a user can login with the given credential.
     * This method is not supported in the Fess implementation.
     *
     * @param credential the login credential to check
     * @return true if the user can login, false otherwise
     * @throws UnsupportedOperationException always thrown as this method is not supported
     */
    @Override
    public boolean checkUserLoginable(final LoginCredential credential) {
        throw new UnsupportedOperationException("checkUserLoginable is not supported.");
    }

    /**
     * Checks the credential using the provided credential checker.
     * This method is not supported in the Fess implementation.
     *
     * @param checker the credential checker to use
     * @throws UnsupportedOperationException always thrown as this method is not supported
     */
    @Override
    protected void checkCredential(final TypicalLoginAssist<String, FessUserBean, FessUser>.CredentialChecker checker) {
        throw new UnsupportedOperationException("checkCredential is not supported.");
    }

    /**
     * Finds a login user by username.
     *
     * @param username the username to search for
     * @return an optional entity containing the found user, or empty if not found
     */
    @Override
    protected OptionalEntity<FessUser> doFindLoginUser(final String username) {
        return getUserBhv().selectEntity(cb -> {
            cb.query().setName_Equal(username);
        }).map(user -> (FessUser) user);
    }

    // ===================================================================================
    //                                                                       Login Process
    //                                                                       =============
    /**
     * Creates a user bean from the given user entity. For a local user the bean remembers when the
     * password was last changed, see {@link #getSavedUserBean()}.
     *
     * @param user the user entity to create a bean from
     * @return the created user bean
     */
    @Override
    protected FessUserBean createUserBean(final FessUser user) {
        final FessUserBean userBean = new FessUserBean(user);
        userBean.setLoginTime(System.currentTimeMillis());
        if (user instanceof final User localUser) {
            // The session remembers the time of the document it was opened with, 0 when the
            // document has none, so that the first password change of a user is noticed too.
            final Long changedAt = localUser.getPasswordChangedAt();
            userBean.setPasswordChangedAt(changedAt != null ? changedAt : Long.valueOf(0L));
        }
        return userBean;
    }

    /**
     * Gets the cookie remember-me key for persistent login.
     * Currently returns empty as remember-me functionality is not enabled.
     *
     * @return an optional thing containing the remember-me key, or empty if not configured
     */
    @Override
    protected OptionalThing<String> getCookieRememberMeKey() {
        // example to use remember-me
        //return OptionalThing.of(fessConfig.getCookieRememberMeFessKey());
        return OptionalThing.empty();
    }

    /**
     * Saves the login history for the given user.
     * This operation is performed asynchronously.
     *
     * @param user the user entity
     * @param userBean the user bean
     * @param option the login specified option
     */
    @Override
    protected void saveLoginHistory(final FessUser user, final FessUserBean userBean, final LoginSpecifiedOption option) {
        asyncManager.async(() -> {
            insertLogin(user);
        });
    }

    /**
     * Inserts a login record for the given member.
     * Currently this method does nothing.
     *
     * @param member the member to insert a login record for
     */
    protected void insertLogin(final Object member) {
        // nothing
    }

    // ===================================================================================
    //                                                                         Login Check
    //                                                                         ===========
    // -----------------------------------------------------
    //                                LoginSession SyncCheck
    //                                ----------------------
    /**
     * Determines whether the login session of the given user bean should be sync-checked.
     *
     * <p>The sync check asks "can this user still log in?" and answers it with
     * {@link #doFindLoginUser(String)}, i.e. a lookup of {@link FessUserBean#getUserId()} in the
     * {@code fess_user.user} index; when that lookup finds nothing,
     * {@code syncCheckLoginSessionIfNeeds} logs the user out and invalidates the session. Only a
     * {@link User} entity lives in that index, so only for a {@link User} is the question
     * answerable, and the check is restricted to one here.</p>
     *
     * <p>A user authenticated by an external identity provider (LDAP, SAML, OpenID Connect, Entra
     * ID or SPNEGO) is built from the provider's response, and no login path writes a user
     * document for one. Without this restriction such a user is logged out at the first check
     * interval and sent to the local password form, which offers no way back to the identity
     * provider. For them the identity provider, not Fess, owns the account lifecycle.</p>
     *
     * <p>Local users are unaffected: deleting one in the admin UI still ends that user's admin
     * session within the check interval. What is given up is that same revocation for an
     * externally authenticated user who <em>does</em> have a local document, which is not merely
     * hypothetical - {@link #resolveCredential(CredentialResolver)} prefers LDAP over the local
     * index, so with LDAP configured a user present in both logs in as an
     * {@link org.codelibs.fess.ldap.LdapUser}, and with {@code ldap.admin.enabled} the admin UI
     * creates and deletes the LDAP entry and the user document together. Deleting such a user
     * still blocks further logins, but no longer ends an admin session that is already open.</p>
     *
     * @param userBean the logged-in user bean to be checked (NotNull)
     * @param checkDt the date-time of the latest sync check, empty if not checked yet (NotNull)
     * @param currentDt the current date-time (NotNull)
     * @return true if the sync check should be performed
     */
    @Override
    protected boolean needsLoginSessionSyncCheck(final FessUserBean userBean, final OptionalThing<LocalDateTime> checkDt,
            final LocalDateTime currentDt) {
        if (!(userBean.getFessUser() instanceof User)) {
            return false;
        }
        return super.needsLoginSessionSyncCheck(userBean, checkDt, currentDt);
    }

    // -----------------------------------------------------
    //                                  Password Change Check
    //                                  ---------------------
    /**
     * Gets the user bean of the login session, ending the session first when the password of the
     * user was changed after it was opened.
     *
     * <p>Every way to ask who is logged in ends here, the admin screens as well as the search
     * and the {@code /api/v2} requests, so a session cannot outlive a password change by taking a
     * path that does not look. The time of the change is read from the user document, not
     * recorded where the sessions are, so it works the same behind several nodes.</p>
     *
     * <p>A session of a local user remembers the time of the user document it was opened with (see
     * {@link #createUserBean(FessUser)}). A user authenticated by LDAP or an identity provider has no
     * such time, so the password of the identity provider, which Fess does not manage, is not looked
     * up. A session opened before the time was remembered has neither it nor the time it was opened
     * (it exists only where sessions are persisted or replicated), so it is judged by the time its HTTP session was created
     * instead: it ends if the password was changed after that.</p>
     *
     * @return the user bean, or the empty result when nobody is logged in or the session was ended
     */
    @Override
    public OptionalThing<FessUserBean> getSavedUserBean() {
        final OptionalThing<FessUserBean> saved = getSessionUserBeanUnchecked();
        if (saved.isPresent() && isPasswordChangedAfterLogin(saved.get())) {
            if (logger.isInfoEnabled()) {
                logger.info("Ended a login session opened before the password was changed. username={}", saved.get().getUserId());
            }
            logout();
            return getSessionUserBeanUnchecked();
        }
        return saved;
    }

    /**
     * Gets the user bean of the login session as it is, without the password change check.
     *
     * @return the user bean in the session
     */
    protected OptionalThing<FessUserBean> getSessionUserBeanUnchecked() {
        return super.getSavedUserBean();
    }

    /**
     * Stores the user bean in the session again, so that a session store that replicates a
     * session when an attribute is set learns of a change made to the bean.
     *
     * @param userBean the user bean to store
     */
    protected void saveSessionUserBean(final FessUserBean userBean) {
        sessionManager.setAttribute(getUserBeanKey(), userBean);
    }

    /**
     * Tests whether the password of the user was changed after the session was opened.
     *
     * <p>The user index is asked only when the session has not been checked for
     * {@code password.changed.check.interval} seconds (see {@link FessUserBean#getPasswordCheckTime()}),
     * so a request in between costs nothing. The session ends only when the stored time is later
     * than the remembered one, not when they merely differ: a node that has not yet seen a newer
     * change, or a document rewritten by a node that does not know the field, must not end a
     * session that is still valid.</p>
     *
     * @param userBean the user bean of the session
     * @return true if the session was opened before the latest password change
     */
    protected boolean isPasswordChangedAfterLogin(final FessUserBean userBean) {
        final Long remembered = userBean.getPasswordChangedAt();
        if (remembered == null && userBean.getLoginTime() != null) {
            return false; // a session of a user who is not a local user
        }
        final long now = currentTimeMillis();
        if (!isPasswordCheckDue(userBean, now)) {
            return false;
        }
        final long threshold;
        if (remembered != null) {
            threshold = remembered.longValue();
        } else {
            // Opened by a version that recorded neither. Nothing says when it logged in, so the creation of
            // the HTTP session stands in for it; a session that logged in after the change in a session
            // created before it is ended too, and the user logs in once more.
            final OptionalLong created = getSessionCreationTime();
            if (!created.isPresent()) {
                return false;
            }
            threshold = created.getAsLong();
        }
        userBean.setPasswordCheckTime(now);
        return findPasswordChangedAt(userBean.getUserId()) > threshold;
    }

    /**
     * Tests whether the session is due for a look at the stored time of the last password change.
     *
     * @param userBean the user bean of the session
     * @param now the current time in epoch milliseconds
     * @return true if the user index is to be asked
     */
    protected boolean isPasswordCheckDue(final FessUserBean userBean, final long now) {
        final Long checked = userBean.getPasswordCheckTime();
        if (checked == null) {
            return true;
        }
        return now - checked.longValue() >= getPasswordCheckInterval() * 1000L;
    }

    /**
     * Gets the seconds between two looks at the stored time of the last password change.
     *
     * @return the seconds; 0 or less looks on every request
     */
    protected int getPasswordCheckInterval() {
        try {
            return ComponentUtil.getFessConfig().getPasswordChangedCheckIntervalAsInteger().intValue();
        } catch (final RuntimeException e) {
            LogUtil.warnOnce(logger, "interval", "password.changed.check.interval is not a number, so {} seconds is used.",
                    DEFAULT_PASSWORD_CHECK_INTERVAL, e);
            return DEFAULT_PASSWORD_CHECK_INTERVAL;
        }
    }

    /**
     * Reads the time the password of a local user was last changed from the user index.
     *
     * <p>When the time cannot be read, the answer is 0, which is never later than what a session
     * remembers: a login session is not ended because the search engine is unreachable.</p>
     *
     * @param username the name of the user
     * @return epoch milliseconds, or 0 when there is none
     */
    protected long findPasswordChangedAt(final String username) {
        try {
            return ComponentUtil.getComponent(UserService.class).getPasswordChangedAt(username);
        } catch (final RuntimeException e) {
            LogUtil.warnOnce(logger, "read",
                    "Could not read the time of the last password change, so login sessions are not checked against it until it can be read.",
                    e);
            return 0L;
        }
    }

    /**
     * Gets the time the HTTP session was created.
     *
     * @return epoch milliseconds, or empty when there is no HTTP session
     */
    protected OptionalLong getSessionCreationTime() {
        return LaRequestUtil.getOptionalRequest().map(request -> request.getSession(false)).map(session -> {
            try {
                return OptionalLong.of(session.getCreationTime());
            } catch (final IllegalStateException e) {
                return OptionalLong.empty(); // invalidated meanwhile
            }
        }).orElse(OptionalLong.empty());
    }

    /**
     * Gets the current time.
     *
     * @return epoch milliseconds
     */
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Takes note that the password of a user was changed at the given time.
     *
     * <p>The login session of the request, when it belongs to that user, remembers the new time
     * and so stays open: it is the one that made the change. Every other session of the user
     * opened before it ends at its next look at the user index, within
     * {@code password.changed.check.interval} seconds, on any node.</p>
     *
     * @param username the name of the user whose password was changed
     * @param changedAt the time stored in the user document, in epoch milliseconds
     */
    public void acceptPasswordChange(final String username, final long changedAt) {
        getSessionUserBeanUnchecked().ifPresent(userBean -> {
            if (username.equals(userBean.getUserId()) && (userBean.getPasswordChangedAt() != null || userBean.getLoginTime() == null)) {
                userBean.setPasswordChangedAt(changedAt);
                userBean.setLoginTime(System.currentTimeMillis());
                saveSessionUserBean(userBean);
            }
        });
    }

    // -----------------------------------------------------
    //                                            Permission
    //                                            ----------
    /**
     * Checks if the current user has permission to access the given resource.
     * For admin actions, verifies that the user has appropriate admin roles or
     * meets the secured annotation requirements.
     *
     * @param resource the login handling resource to check permission for
     * @throws LoginRequiredException if login is required
     * @throws UserRoleLoginException if the user doesn't have required roles
     */
    @Override
    protected void checkPermission(final LoginHandlingResource resource) throws LoginRequiredException {
        if (FessAdminAction.class.isAssignableFrom(resource.getActionClass())) {
            getSavedUserBean().ifPresent(user -> {
                if (user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray())) {
                    return;
                }
                final Method executeMethod = resource.getExecuteMethod();
                final Secured secured = executeMethod.getAnnotation(Secured.class);
                if (secured != null && user.hasRoles(secured.value())) {
                    return;
                }
                throw new UserRoleLoginException();
            });
        }
    }

    // ===================================================================================
    //                                                                      Login Resource
    //                                                                      ==============
    /**
     * Gets the user bean type class.
     *
     * @return the FessUserBean class
     */
    @Override
    protected Class<FessUserBean> getUserBeanType() {
        return FessUserBean.class;
    }

    /**
     * Gets the login action type class.
     *
     * @return the LoginAction class
     */
    @Override
    protected Class<?> getLoginActionType() {
        return LoginAction.class;
    }

    /**
     * Converts a user key to a typed user ID.
     * In this implementation, returns the user key as-is.
     *
     * @param userKey the user key to convert
     * @return the typed user ID
     */
    @Override
    protected String toTypedUserId(final String userKey) {
        return userKey;
    }

    // ===================================================================================
    //                                                                     Login Extension
    //                                                                      ==============

    /**
     * Resolves login credentials using various authentication methods.
     * This method handles local user authentication, LDAP authentication,
     * and SSO authentication through configured authenticators.
     *
     * @param resolver the credential resolver to use
     */
    @Override
    protected void resolveCredential(final CredentialResolver resolver) {
        resolver.resolve(LocalUserCredential.class, credential -> {
            final LocalUserCredential userCredential = credential;
            final String username = userCredential.getUser();
            final String password = userCredential.getPassword();
            if (isLdapLoginCandidate(username)) {
                final OptionalEntity<FessUser> ldapUser = ComponentUtil.getLdapManager().login(username, password);
                if (ldapUser.isPresent()) {
                    return ldapUser;
                }
            }
            return doAuthenticateLocal(username, password);
        });
        final LoginCredentialResolver loginResolver = new LoginCredentialResolver(resolver);
        for (final SsoAuthenticator auth : ComponentUtil.getSsoManager().getAuthenticators()) {
            auth.resolveCredential(loginResolver);
        }
    }

    /**
     * Tells whether the typed name may be tried against the directory.
     *
     * <p>The name the directory is asked about is the one {@code LdapManager#normalizeLoginName} makes of
     * it, so a reserved name must be recognised in that form as well: padded, it would otherwise get past
     * the check and come back from the directory as the reserved account.</p>
     *
     * @param username the name as typed at the login form
     * @return false for a name authentication.admin.users reserves
     */
    protected boolean isLdapLoginCandidate(final String username) {
        return !fessConfig.isReservedLoginName(username);
    }

    /**
     * A resolver for login credentials that wraps the standard credential resolver
     * to provide SSO authentication support.
     */
    public static class LoginCredentialResolver {
        /** The wrapped credential resolver. */
        private final TypicalLoginAssist<String, FessUserBean, FessUser>.CredentialResolver resolver;

        /**
         * Creates a new login credential resolver.
         *
         * @param resolver the credential resolver to wrap
         */
        public LoginCredentialResolver(final CredentialResolver resolver) {
            this.resolver = resolver;
        }

        /**
         * Resolves credentials of the specified type using the provided function.
         *
         * @param <CREDENTIAL> the credential type
         * @param credentialType the class of the credential type
         * @param oneArgLambda the function to apply for credential resolution
         */
        public <CREDENTIAL extends LoginCredential> void resolve(final Class<CREDENTIAL> credentialType,
                final Function<CREDENTIAL, OptionalEntity<FessUser>> oneArgLambda) {
            resolver.resolve(credentialType, credential -> oneArgLambda.apply(credential));
        }
    }

    /**
     * Authenticates a local user by username and plaintext password using the
     * {@link PasswordHashHelper} (BCrypt with legacy hex-digest fallback) and
     * performs best-effort lazy re-hashing for credentials stored in an older
     * format.
     *
     * <p>Timing-attack countermeasure: every failure path must pay
     * approximately one BCrypt verification worth of CPU, regardless of
     * whether the user exists and regardless of the stored hash format.
     * When {@link PasswordHashHelper#matches} already consumed a BCrypt cost
     * (stored value carries a {@code {bcrypt}} prefix), no additional padding
     * is applied — otherwise the failure branch would pay <em>two</em>
     * BCrypt costs and become distinguishable from unknown-user failures.</p>
     *
     * @param username the login name to look up
     * @param plainPassword the raw, user-supplied password
     * @return an optional entity containing the found user on success, or empty
     *         otherwise
     */
    protected OptionalEntity<FessUser> doAuthenticateLocal(final String username, final String plainPassword) {
        final PasswordHashHelper passwordHashHelper = ComponentUtil.getPasswordHashHelper();
        final OptionalEntity<FessUser> userOpt = doFindLoginUser(username);
        if (userOpt.isPresent()) {
            final FessUser user = userOpt.get();
            final String stored = (user instanceof final User u) ? u.getPassword() : null;
            if (stored != null && passwordHashHelper.matches(plainPassword, stored)) {
                lazyUpgradePassword(username, plainPassword, stored, passwordHashHelper);
                return userOpt;
            }
            // Failure path: pad with dummy BCrypt UNLESS the matches() call
            // above already consumed a BCrypt cost (i.e., stored was in the
            // {bcrypt} form). Paying it twice would make this branch visibly
            // slower than the unknown-user branch and re-introduce the
            // enumeration oracle we are trying to close.
            if (!passwordHashHelper.isTimingSafeHash(stored)) {
                passwordHashHelper.applyTimingPadding();
            }
            return OptionalEntity.empty();
        }
        // User does not exist: pay one BCrypt pass to equalise timing.
        passwordHashHelper.applyTimingPadding();
        return OptionalEntity.empty();
    }

    /**
     * Best-effort upgrade of a legacy or obsolete-cost password hash to the
     * currently configured algorithm/parameters. A failure here never fails
     * the login and never propagates an exception to the caller.
     *
     * <p>Logs only the username (never the plaintext or hash values).</p>
     *
     * @param username the user whose stored hash is being upgraded
     * @param plainPassword the plaintext password (already verified to match)
     * @param currentStored the currently stored hash value
     * @param passwordHashHelper the password manager to use
     */
    protected void lazyUpgradePassword(final String username, final String plainPassword, final String currentStored,
            final PasswordHashHelper passwordHashHelper) {
        if (!passwordHashHelper.upgradeEncoding(currentStored)) {
            return;
        }
        try {
            final String newEncoded = passwordHashHelper.encode(plainPassword);
            final boolean updated =
                    ComponentUtil.getComponent(UserService.class).updateStoredPasswordHash(username, currentStored, newEncoded);
            if (updated) {
                if (logger.isInfoEnabled()) {
                    logger.info("Upgraded password hash. username={}", username);
                }
            } else if (logger.isWarnEnabled()) {
                logger.warn("Failed to upgrade password hash (update returned false). username={}", username);
            }
        } catch (final Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to upgrade password hash. username={}", username, e);
            }
        }
    }

    /**
     * Overrides the default cipher-based encryption to delegate to
     * {@link PasswordHashHelper#encode}. This override exists solely so that any
     * internal LastaFlute login path that still calls
     * {@code encryptPassword} produces a hash in the new
     * <code>{bcrypt}$2a$...</code> format. All Fess write paths (user
     * creation, password change, initial admin bootstrap) call
     * {@link PasswordHashHelper#encode(String)} directly via
     * {@link ComponentUtil#getPasswordHashHelper()}; do not add new callers of
     * this method from outside the login framework.
     *
     * @param plainText the plaintext password
     * @return the encoded (hashed) password with prefix
     */
    @Override
    public String encryptPassword(final String plainText) {
        return ComponentUtil.getPasswordHashHelper().encode(plainText);
    }

    /**
     * Finds a login user by username and encrypted password.
     *
     * @param username the username to search for
     * @param cipheredPassword ignored; retained only for source-level
     *        backward compatibility. Local authentication now goes through
     *        {@link #doAuthenticateLocal(String, String)}.
     * @return an optional entity containing the found user when the stored
     *         password exactly matches the supplied ciphered value, otherwise
     *         empty
     * @deprecated Use {@link #doAuthenticateLocal(String, String)} with the
     *             plaintext password. BCrypt uses per-record salts, so an
     *             exact-match DB lookup on a hashed value is no longer
     *             meaningful. Retained for any subclass or legacy caller.
     */
    @Deprecated
    protected OptionalEntity<FessUser> doFindLoginUser(final String username, final String cipheredPassword) {
        final OptionalEntity<FessUser> userOpt = doFindLoginUser(username);
        if (!userOpt.isPresent()) {
            return OptionalEntity.empty();
        }
        final FessUser user = userOpt.get();
        final String stored = (user instanceof final User u) ? u.getPassword() : null;
        if (stored != null && stored.equals(cipheredPassword)) {
            return userOpt;
        }
        return OptionalEntity.empty();
    }
}
