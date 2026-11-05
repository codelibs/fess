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
package org.codelibs.fess.app.web.admin.failureurl;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class SearchFormTest extends UnitFessTestCase {

    private static int violations(final String min, final String max) {
        final SearchForm form = new SearchForm();
        form.errorCountMin = min;
        form.errorCountMax = max;
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = factory.getValidator();
            return validator.validate(form).size();
        }
    }

    @Test
    public void test_errorCount_acceptsBlankAndDigits() throws Exception {
        assertEquals(0, violations(null, null));
        assertEquals(0, violations("", ""));
        assertEquals(0, violations("0", "100000"));
    }

    @Test
    public void test_errorCount_rejectsWhatIntegerParseIntWouldThrowOn() throws Exception {
        assertEquals(1, violations("abc", null));
        assertEquals(1, violations(null, "-1"));
        assertEquals(1, violations(null, "99999999999"));
        assertEquals(2, violations("1.5", "1e3"));
    }
}
