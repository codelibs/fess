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
package org.codelibs.fess.validation;

import org.lastaflute.core.message.UserMessages;
import org.lastaflute.core.message.supplier.UserMessagesCreator;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.validation.ActionValidator;

/**
 * Fess-specific action validator that extends the LastaFlute ActionValidator.
 * This validator provides validation functionality for Fess web actions with custom
 * message handling and runtime group validation.
 *
 * @param <MESSAGES> the type of user messages used by this validator
 */
public class FessActionValidator<MESSAGES extends UserMessages> extends ActionValidator<MESSAGES> {

    /**
     * Constructs a new FessActionValidator with the specified components.
     *
     * @param requestManager the request manager for handling HTTP requests
     * @param messagesCreator the creator for user messages
     * @param runtimeGroups the runtime validation groups
     */
    public FessActionValidator(final RequestManager requestManager, final UserMessagesCreator<MESSAGES> messagesCreator,
            final Class<?>[] runtimeGroups) {
        super(requestManager, messagesCreator, runtimeGroups);
    }

}
