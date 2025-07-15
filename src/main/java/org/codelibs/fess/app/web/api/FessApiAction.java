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
package org.codelibs.fess.app.web.api;

import java.util.Locale;
import java.util.stream.Collectors;

import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaMessenger;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract base class for Fess API actions that provides common functionality
 * for API endpoints including authentication, message handling, and access control.
 *
 * This class extends FessBaseAction and provides specialized behavior for API requests,
 * including token-based authentication and JSON response handling.
 */
public abstract class FessApiAction extends FessBaseAction {

    /**
     * Default constructor.
     */
    public FessApiAction() {
        // Default constructor
    }

    /**
     * Message manager for handling internationalized messages and validation errors.
     * Used to convert validation messages to localized text for API responses.
     */
    @Resource
    protected MessageManager messageManager;

    /**
     * Service for managing API access tokens including validation and authentication.
     * Used to verify token-based authentication for API requests.
     */
    @Resource
    protected AccessTokenService accessTokenService;

    /**
     * HTTP servlet request object providing access to request parameters, headers,
     * and other request-specific information needed for API processing.
     */
    @Resource
    protected HttpServletRequest request;

    /**
     * Returns an empty OptionalThing for login manager since API actions
     * use token-based authentication instead of traditional session-based login.
     *
     * @return empty OptionalThing indicating no login manager is used
     */
    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty();
    }

    /**
     * Pre-processes API requests by checking access authorization before executing the action.
     * If access is not allowed, returns an unauthorized error response.
     *
     * @param runtime the action runtime context containing request information
     * @return ActionResponse with unauthorized error if access denied, otherwise delegates to parent
     */
    @Override
    public ActionResponse godHandPrologue(final ActionRuntime runtime) {
        if (!isAccessAllowed()) {
            return asJson(new ApiErrorResponse().message(getMessage(messages -> messages.addErrorsUnauthorizedRequest(GLOBAL)))
                    .status(Status.UNAUTHORIZED).result());
        }
        return super.godHandPrologue(runtime);
    }

    /**
     * Converts validation messages to a localized string representation for API responses.
     * Uses the request locale if available, otherwise defaults to English.
     *
     * @param validationMessagesLambda lambda function that adds validation messages
     * @return concatenated string of localized validation messages separated by spaces
     */
    protected String getMessage(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = new FessMessages();
        validationMessagesLambda.message(messages);
        return messageManager.toMessageList(request.getLocale() == null ? Locale.ENGLISH : request.getLocale(), messages).stream()
                .collect(Collectors.joining(" "));
    }

    /**
     * Determines whether the current request is authorized to access the API endpoint.
     * This default implementation returns false, requiring subclasses to override
     * and implement proper access control logic.
     *
     * @return true if access is allowed, false otherwise
     */
    protected boolean isAccessAllowed() {
        return false;
    }
}
