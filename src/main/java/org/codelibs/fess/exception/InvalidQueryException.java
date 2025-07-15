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
package org.codelibs.fess.exception;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.lastaflute.web.validation.VaMessenger;

/**
 * Exception thrown when an invalid query is encountered.
 * This exception is typically used in search contexts where a provided
 * query is malformed, contains invalid syntax, or violates query constraints.
 */
public class InvalidQueryException extends FessSystemException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** Message code for localized error messages */
    private final transient VaMessenger<FessMessages> messageCode;

    /**
     * Creates a new InvalidQueryException with message code, message, and cause.
     *
     * @param messageCode the message code for localized error messages
     * @param message the detailed error message
     * @param cause the cause of the exception
     */
    public InvalidQueryException(final VaMessenger<FessMessages> messageCode, final String message, final Throwable cause) {
        super(message, cause);
        this.messageCode = messageCode;
    }

    /**
     * Creates a new InvalidQueryException with message code and message.
     *
     * @param messageCode the message code for localized error messages
     * @param message the detailed error message
     */
    public InvalidQueryException(final VaMessenger<FessMessages> messageCode, final String message) {
        super(message);
        this.messageCode = messageCode;
    }

    /**
     * Returns the message code for localized error messages.
     *
     * @return the message code
     */
    public VaMessenger<FessMessages> getMessageCode() {
        return messageCode;
    }

}
