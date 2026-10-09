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
package org.codelibs.fess.app.web.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * Contract test for the API actions under {@code org.codelibs.fess.app.web.api}.
 *
 * <p>An API action has no HTML page. A route that only exists to say "not supported in API" must not
 * be an {@code @Execute} method: the framework would run it and answer 500, whereas a route that
 * does not exist is answered 404 like every other unknown API path.</p>
 */
public class ApiActionResponseTypeTest extends UnitFessTestCase {

    @Test
    public void test_noApiActionExposesAnHtmlRoute() throws Exception {
        final List<String> offenders = new ArrayList<>();
        for (final Class<?> actionType : findApiActions()) {
            for (final Method method : actionType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Execute.class) && HtmlResponse.class.isAssignableFrom(method.getReturnType())) {
                    offenders.add(actionType.getSimpleName() + "#" + method.getName());
                }
            }
        }
        assertTrue(offenders.isEmpty(), "API actions must not expose @Execute methods that return HtmlResponse: " + offenders);
    }

    @Test
    public void test_apiActionsAreFound() throws Exception {
        // guards the scan above against silently finding nothing
        assertTrue(findApiActions().size() > 30);
    }

    private List<Class<?>> findApiActions() throws Exception {
        final Path classesDir = Paths.get(ApiResult.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        final Path apiDir = classesDir.resolve(ApiResult.class.getPackageName().replace('.', '/'));
        final List<Class<?>> actions = new ArrayList<>();
        try (Stream<Path> files = Files.walk(apiDir)) {
            for (final Path file : (Iterable<Path>) files::iterator) {
                final String name = file.getFileName().toString();
                if (name.endsWith("Action.class") && !name.contains("$")) {
                    final String className = classesDir.relativize(file).toString().replace(file.getFileSystem().getSeparator(), ".");
                    actions.add(Class.forName(className.substring(0, className.length() - ".class".length()), false,
                            ApiResult.class.getClassLoader()));
                }
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to scan " + apiDir, e);
        }
        return actions;
    }
}
