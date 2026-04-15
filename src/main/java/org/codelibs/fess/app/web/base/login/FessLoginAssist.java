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
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.helper.PasswordHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.login.LoginHandlingResource;
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

    /**
     * Default constructor.
     */
    public FessLoginAssist() {
        super();
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

    /** The user behavior for database operations on user entities. */
    @Resource
    private UserBhv userBhv;

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
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
        }).map(user -> (FessUser) user);
    }

    // ===================================================================================
    //                                                                       Login Process
    //                                                                       =============
    /**
     * Creates a user bean from the given user entity.
     *
     * @param user the user entity to create a bean from
     * @return the created user bean
     */
    @Override
    protected FessUserBean createUserBean(final FessUser user) {
        return new FessUserBean(user);
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
                throw new UserRoleLoginException(RootAction.class);
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
            if (!fessConfig.isAdminUser(username)) {
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
     * {@link PasswordHelper} (BCrypt with legacy hex-digest fallback) and
     * performs best-effort lazy re-hashing for credentials stored in an older
     * format.
     *
     * <p>Timing-attack countermeasure: every failure path must pay
     * approximately one BCrypt verification worth of CPU, regardless of
     * whether the user exists and regardless of the stored hash format.
     * When {@link PasswordHelper#matches} already consumed a BCrypt cost
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
        final PasswordHelper passwordHelper = ComponentUtil.getPasswordHelper();
        final OptionalEntity<FessUser> userOpt = doFindLoginUser(username);
        if (userOpt.isPresent()) {
            final FessUser user = userOpt.get();
            final String stored = (user instanceof User) ? ((User) user).getPassword() : null;
            if (stored != null && passwordHelper.matches(plainPassword, stored)) {
                lazyUpgradePassword(username, plainPassword, stored, passwordHelper);
                return userOpt;
            }
            // Failure path: pad with dummy BCrypt UNLESS the matches() call
            // above already consumed a BCrypt cost (i.e., stored was in the
            // {bcrypt} form). Paying it twice would make this branch visibly
            // slower than the unknown-user branch and re-introduce the
            // enumeration oracle we are trying to close.
            if (!passwordHelper.isTimingSafeHash(stored)) {
                passwordHelper.applyTimingPadding();
            }
            return OptionalEntity.empty();
        }
        // User does not exist: pay one BCrypt pass to equalise timing.
        passwordHelper.applyTimingPadding();
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
     * @param passwordHelper the password manager to use
     */
    protected void lazyUpgradePassword(final String username, final String plainPassword, final String currentStored,
            final PasswordHelper passwordHelper) {
        if (!passwordHelper.upgradeEncoding(currentStored)) {
            return;
        }
        try {
            final String newEncoded = passwordHelper.encode(plainPassword);
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
     * {@link PasswordHelper#encode}. This override exists solely so that any
     * internal LastaFlute login path that still calls
     * {@code encryptPassword} produces a hash in the new
     * <code>{bcrypt}$2a$...</code> format. All Fess write paths (user
     * creation, password change, initial admin bootstrap) call
     * {@link PasswordHelper#encode(String)} directly via
     * {@link ComponentUtil#getPasswordHelper()}; do not add new callers of
     * this method from outside the login framework.
     *
     * @param plainText the plaintext password
     * @return the encoded (hashed) password with prefix
     */
    @Override
    public String encryptPassword(final String plainText) {
        return ComponentUtil.getPasswordHelper().encode(plainText);
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
        final String stored = (user instanceof User) ? ((User) user).getPassword() : null;
        if (stored != null && stored.equals(cipheredPassword)) {
            return userOpt;
        }
        return OptionalEntity.empty();
    }
}
