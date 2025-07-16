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

import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
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

    /**
     * Default constructor.
     */
    public FessLoginAssist() {
        // Default constructor
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
            return doFindLoginUser(username, encryptPassword(password));
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
     * Finds a login user by username and encrypted password.
     *
     * @param username the username to search for
     * @param cipheredPassword the encrypted password to match
     * @return an optional entity containing the found user, or empty if not found
     */
    protected OptionalEntity<FessUser> doFindLoginUser(final String username, final String cipheredPassword) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
            cb.query().setPassword_Equal(cipheredPassword);
        }).map(user -> (FessUser) user);
    }
}
