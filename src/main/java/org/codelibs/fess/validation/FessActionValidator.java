/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.util.Locale;

import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.core.message.supplier.MessageLocaleProvider;
import org.lastaflute.core.message.supplier.UserMessagesCreator;
import org.lastaflute.web.validation.ActionValidator;
import org.lastaflute.web.validation.VaErrorHook;

public class FessActionValidator<MESSAGES extends UserMessages> extends ActionValidator<MESSAGES> {

    public FessActionValidator(final MessageManager messageManager, final MessageLocaleProvider messageLocaleProvider,
            final UserMessagesCreator<MESSAGES> userMessagesCreator, final VaErrorHook apiFailureHook, final Class<?>[] runtimeGroups) {
        super(messageManager, messageLocaleProvider, userMessagesCreator, apiFailureHook, runtimeGroups);
    }

    @Override
    protected ResourceBundleLocator newResourceBundleLocator() {
        return locale -> {
            final Locale userLocale = messageLocaleProvider.provide();
            return newHookedResourceBundle(userLocale == null ? locale : userLocale);
        };
    }
}
