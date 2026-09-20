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
package org.codelibs.fess.app.web.thumbnail;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter.RequestClientErrorException;
import org.lastaflute.web.servlet.request.ResponseManager;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.VaMore;
import org.lastaflute.web.validation.ValidationSuccess;

/**
 * Task 7 closes the {@code ThumbnailAction} form-validation branch: it used to
 * {@code saveError(...)} and redirect to {@code ErrorAction} (a 302 followed by a 200 page at a
 * different URL); it now reports the real 400 at the real URL, matching {@code GoAction}'s sibling
 * change. The other two branches ({@code thumbnail missing} / {@code under generating}) already
 * threw {@code responseManager.new404(...)} before this task and are unchanged, so they are not
 * retested here.
 */
public class ThumbnailActionTest extends UnitFessTestCase {

    private TestableThumbnailAction action;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        action = new TestableThumbnailAction();
        action.setResponseManager(ComponentUtil.getComponent(ResponseManager.class));
    }

    // Test class exposing a validate() seam so the production error hook can be exercised
    // without driving real Bean Validation, which would need requestManager wired up to reach --
    // the same seam GoActionTest and SearchActionTest use.
    private static class TestableThumbnailAction extends ThumbnailAction {

        void setResponseManager(final ResponseManager responseManager) {
            this.responseManager = responseManager;
        }

        boolean failValidation;

        @Override
        public ValidationSuccess validate(final Object form, final VaMore<FessMessages> moreValidationLambda,
                final VaErrorHook validationErrorLambda) {
            if (failValidation) {
                // The production hook's body always throws; it never returns a value.
                validationErrorLambda.hook();
                throw new IllegalStateException("validationErrorLambda should have thrown");
            }
            return new ValidationSuccess(new FessMessages());
        }
    }

    @Test
    public void test_index_validationFailure_is400() {
        action.failValidation = true;
        try {
            action.index(new ThumbnailForm());
            fail("expected a 400");
        } catch (final Exception e) {
            // The exception type and its getErrorStatus() are asserted directly, not the class
            // name: a class name merely containing "400" would also accept an unrelated class.
            assertTrue(e instanceof RequestClientErrorException, "expected a 400 RequestClientErrorException, got " + e.getClass());
            assertEquals(400, ((RequestClientErrorException) e).getErrorStatus());
        }
    }
}
