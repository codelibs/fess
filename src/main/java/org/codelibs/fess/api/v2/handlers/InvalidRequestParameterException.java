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
package org.codelibs.fess.api.v2.handlers;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.lastaflute.web.validation.VaMessenger;

/**
 * Thrown when a v2 API request parameter violates a length, item-count, or
 * format constraint. Handlers map this to {@code V2ErrorCode.INVALID_REQUEST}.
 *
 * <p>{@link #getMessage()} is the English text meant for logs. A violation a visitor can cause
 * through the search page also carries a message code, which the handlers resolve in the
 * caller's language.</p>
 */
public class InvalidRequestParameterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /** The message code of the localized message, or {@code null} when the violation has none. */
    private final transient VaMessenger<FessMessages> messageCode;

    /**
     * Constructs the exception with a message describing the violated constraint.
     *
     * @param message the detail message describing the invalid request parameter
     */
    public InvalidRequestParameterException(final String message) {
        this(null, message);
    }

    /**
     * Constructs the exception with a message code for the caller's language besides the English message.
     *
     * @param messageCode the message code of the localized message, or {@code null}
     * @param message the detail message describing the invalid request parameter
     */
    public InvalidRequestParameterException(final VaMessenger<FessMessages> messageCode, final String message) {
        super(message);
        this.messageCode = messageCode;
    }

    /**
     * Returns the message code of the localized message.
     *
     * @return the message code, or {@code null} when the violation has none
     */
    public VaMessenger<FessMessages> getMessageCode() {
        return messageCode;
    }
}
