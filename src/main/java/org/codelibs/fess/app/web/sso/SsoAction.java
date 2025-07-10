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
package org.codelibs.fess.app.web.sso;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.base.FessLoginAction;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.app.web.search.SearchAction;
import org.codelibs.fess.entity.RequestParameter;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.UrlChain;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;

public class SsoAction extends FessLoginAction {
    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(SsoAction.class);

    // ===================================================================================
    //                                                                       Login Execute
    //                                                                      ==============

    @Execute
    public ActionResponse index() {
        if (fessLoginAssist.getSavedUserBean().isPresent()) {
            return redirectToSearchPage().orElseGet(() -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("User is already logged in, redirecting to root.");
                }
                return redirect(RootAction.class);
            });
        }
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        final LoginCredential loginCredential = ssoManager.getLoginCredential();
        if (loginCredential == null) {
            if (ssoManager.available()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SSO is available but no user found.");
                }
                saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to login page.");
            }
            return redirect(LoginAction.class);
        }
        if (loginCredential instanceof ActionResponseCredential) {
            if (logger.isDebugEnabled()) {
                logger.debug("Login credential is an ActionResponseCredential, executing it.");
            }
            return ((ActionResponseCredential) loginCredential).execute();
        }
        try {
            return fessLoginAssist.loginRedirect(loginCredential, op -> {}, () -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Logging in user: {}", loginCredential);
                }
                activityHelper.login(getUserBean());
                userInfoHelper.deleteUserCodeFromCookie(request);
                return redirectToSearchPage().orElseGet(() -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No search parameters found, redirecting to root.");
                    }
                    return getHtmlResponse();
                });
            });
        } catch (final LoginFailureException lfe) {
            if (ssoManager.available()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SSO is available but login failed.", lfe);
                }
                saveError(messages -> messages.addErrorsSsoLoginError(GLOBAL));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to login page after failure.", lfe);
            }
            activityHelper.loginFailure(OptionalThing.of(loginCredential));
            return redirect(LoginAction.class);
        }
    }

    @Execute
    public ActionResponse metadata() {
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        try {
            final ActionResponse actionResponse = ssoManager.getResponse(SsoResponseType.METADATA);
            if (actionResponse == null) {
                throw responseManager.new400("Unsupported request type.");
            }
            return actionResponse;
        } catch (final SsoMessageException e) {
            if (e.getCause() == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Metadata response.", e);
                }
                saveInfo(e.getMessageCode());
            } else {
                logger.warn("Failed to process metadata.", e);
                saveError(e.getMessageCode());
            }
            return redirect(LoginAction.class);
        }
    }

    protected OptionalThing<HtmlResponse> redirectToSearchPage() {
        final RequestParameter[] searchParameters = searchHelper.getSearchParameters();
        if (searchParameters.length > 0) {
            final UrlChain chain = new UrlChain(this);
            for (final RequestParameter param : searchParameters) {
                for (final String value : param.getValues()) {
                    chain.params(param.getName(), value);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Redirecting to SearchAction with parameters: {}", chain);
            }
            return OptionalThing.of(redirectWith(SearchAction.class, chain));
        }
        return OptionalThing.empty();
    }

    @Execute
    public ActionResponse logout() {
        final SsoManager ssoManager = ComponentUtil.getSsoManager();
        try {
            final ActionResponse actionResponse = ssoManager.getResponse(SsoResponseType.LOGOUT);
            if (actionResponse == null) {
                throw responseManager.new400("Unsupported request type.");
            }
            return actionResponse;
        } catch (final SsoMessageException e) {
            if (e.getCause() == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Logout response.", e);
                }
                saveInfo(e.getMessageCode());
            } else {
                logger.warn("Failed to log out.", e);
                saveError(e.getMessageCode());
            }
            return redirect(LoginAction.class);
        }
    }
}