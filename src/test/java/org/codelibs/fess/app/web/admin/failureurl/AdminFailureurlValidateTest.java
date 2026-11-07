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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * {@link SearchForm} carries validator annotations, so LastaFlute throws
 * ExecuteMethodLonelyValidatorAnnotationException (HTTP 500) for an action that takes the form and
 * does not call validate(). The actions need a session and a search engine, so the check reads the source.
 */
public class AdminFailureurlValidateTest extends UnitFessTestCase {

    private static final Path ACTION = Paths.get("src/main/java/org/codelibs/fess/app/web/admin/failureurl/AdminFailureurlAction.java");

    @Test
    public void test_everyActionThatTakesTheSearchFormValidatesIt() throws Exception {
        final String source = Files.readString(ACTION, StandardCharsets.UTF_8);
        final Matcher m = Pattern.compile("public \\w+ (\\w+)\\([^)]*SearchForm form\\)\\s*\\{").matcher(source);
        final List<String> missing = new ArrayList<>();
        int actions = 0;
        while (m.find()) {
            actions++;
            final int end = source.indexOf("\n    }\n", m.end());
            final String body = source.substring(m.end(), end);
            if (!body.contains("validate(form")) {
                missing.add(m.group(1));
            }
        }
        assertTrue("no action with a SearchForm found", actions >= 5);
        assertEquals("actions that take the SearchForm without validate(): " + missing, 0, missing.size());
    }
}
