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
package org.codelibs.fess.app.web.search;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.VaMore;
import org.lastaflute.web.validation.ValidationSuccess;

/**
 * SearchForm carries validator annotations, so the framework insists that every execute method
 * taking one calls validate() on every path out of the method. A path that returns before the
 * call is reported as a wiring mistake once the method has already returned, which turns the
 * response into a system error.
 */
public class SearchActionTest extends UnitFessTestCase {

    /**
     * The advance search screen validates before it consults the login gate, so the path that
     * sends an anonymous visitor to login has already called validate() by then.
     */
    @Test
    public void test_advance_validatesBeforeRedirectingToLogin() {
        final List<String> calls = new ArrayList<>();
        final SearchAction action = new SearchAction() {
            @Override
            public ValidationSuccess validate(final Object form, final VaMore<FessMessages> moreValidationLambda,
                    final VaErrorHook validationErrorLambda) {
                calls.add("validate");
                return new ValidationSuccess(new FessMessages());
            }

            @Override
            protected boolean isLoginRequired() {
                return true;
            }

            @Override
            protected HtmlResponse redirectToLogin() {
                calls.add("redirectToLogin");
                return null;
            }
        };

        assertNull(action.advance(new SearchForm()));
        assertEquals("validate redirectToLogin", String.join(" ", calls));
    }
}
