/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

public abstract class FessApiAction extends FessBaseAction {

    @Resource
    protected MessageManager messageManager;

    @Resource
    protected AccessTokenService accessTokenService;

    @Resource
    protected HttpServletRequest request;

    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty();
    }

    @Override
    public ActionResponse godHandPrologue(final ActionRuntime runtime) {
        if (!isAccessAllowed()) {
            return asJson(new ApiErrorResponse().message(getMessage(messages -> messages.addErrorsUnauthorizedRequest(GLOBAL)))
                    .status(Status.UNAUTHORIZED).result());
        }
        return super.godHandPrologue(runtime);
    }

    protected String getMessage(final VaMessenger<FessMessages> validationMessagesLambda) {
        final FessMessages messages = new FessMessages();
        validationMessagesLambda.message(messages);
        return messageManager.toMessageList(request.getLocale() == null ? Locale.ENGLISH : request.getLocale(), messages).stream()
                .collect(Collectors.joining(" "));
    }

    protected boolean isAccessAllowed() {
        return false;
    }
}
