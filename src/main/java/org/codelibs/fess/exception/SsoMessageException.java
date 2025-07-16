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
 * Exception thrown during SSO (Single Sign-On) processing with message code support.
 *
 * This exception is used to indicate errors that occur during SSO authentication
 * and authorization processes. It carries both a message code for internationalization
 * and localization purposes, as well as detailed error information. The message code
 * can be used by the UI layer to display appropriate error messages to users.
 */
public class SsoMessageException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /** The message code for internationalized error messages. */
    private final transient VaMessenger<FessMessages> messageCode;

    /**
     * Constructs a new SSO message exception with message code, detailed message, and cause.
     *
     * @param messageCode The message code for internationalized error display
     * @param message The detailed error message
     * @param cause The underlying cause of this exception
     */
    public SsoMessageException(final VaMessenger<FessMessages> messageCode, final String message, final Throwable cause) {
        super(message, cause);
        this.messageCode = messageCode;
    }

    /**
     * Constructs a new SSO message exception with message code and detailed message.
     *
     * @param messageCode The message code for internationalized error display
     * @param message The detailed error message
     */
    public SsoMessageException(final VaMessenger<FessMessages> messageCode, final String message) {
        super(message);
        this.messageCode = messageCode;
    }

    /**
     * Gets the message code for internationalized error display.
     *
     * The message code can be used by the presentation layer to retrieve
     * localized error messages appropriate for the user's language settings.
     *
     * @return The message code for error message localization
     */
    public VaMessenger<FessMessages> getMessageCode() {
        return messageCode;
    }

}
