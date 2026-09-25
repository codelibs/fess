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
package org.codelibs.fess.webapp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Keeps WEB-INF/fe.tld in step with the JSPs that call it.
 *
 * <p>A JSP is not compiled by the build, so a page calling an fe: function the TLD no longer
 * declares -- or a declaration whose Java method is gone -- is only found when a user opens the
 * page and the container fails to translate it. The TLD must declare exactly the functions the
 * application's JSPs call, and every declaration must resolve to a public static method.</p>
 */
public class FeTldUsageTest extends UnitFessTestCase {

    private static final Path VIEW_DIR = Paths.get("src/main/webapp/WEB-INF/view");

    private static final Path TLD = Paths.get("src/main/webapp/WEB-INF/fe.tld");

    private static final Pattern CALL = Pattern.compile("\\bfe:([A-Za-z][A-Za-z0-9]*)\\(");

    private static final Pattern FUNCTION = Pattern.compile("<function>(.*?)</function>", Pattern.DOTALL);

    private static final Pattern NAME = Pattern.compile("<name>\\s*([^<\\s]+)\\s*</name>");

    private static final Pattern SIGNATURE =
            Pattern.compile("<function-signature>\\s*[\\w.\\[\\]]+\\s+(\\w+)\\(([^)]*)\\)\\s*</function-signature>");

    @Test
    public void test_declaresExactlyTheFunctionsTheJspsCall() throws Exception {
        final Set<String> called = new TreeSet<>();
        try (Stream<Path> paths = Files.walk(VIEW_DIR)) {
            for (final Path jsp : paths.filter(p -> p.toString().endsWith(".jsp")).toList()) {
                final Matcher matcher = CALL.matcher(Files.readString(jsp, StandardCharsets.UTF_8));
                while (matcher.find()) {
                    called.add(matcher.group(1));
                }
            }
        }
        assertFalse("no fe: call found under " + VIEW_DIR, called.isEmpty());
        assertEquals("fe.tld must declare exactly the functions the JSPs call", called, declaredFunctions().keySet());
    }

    @Test
    public void test_everyDeclarationResolvesToAPublicStaticMethod() throws Exception {
        final List<String> unresolved = new ArrayList<>();
        for (final Map.Entry<String, String[]> entry : declaredFunctions().entrySet()) {
            final String methodName = entry.getValue()[0];
            final String params = entry.getValue()[1].trim();
            final int arity = params.isEmpty() ? 0 : params.split(",").length;
            boolean found = false;
            for (final Method method : FessFunctions.class.getMethods()) {
                if (method.getName().equals(methodName) && Modifier.isStatic(method.getModifiers())
                        && method.getParameterCount() == arity) {
                    found = true;
                }
            }
            if (!found) {
                unresolved.add(entry.getKey() + " -> " + methodName + "/" + arity);
            }
        }
        assertEquals("every fe.tld function must be a public static method of FessFunctions: " + unresolved, 0, unresolved.size());
    }

    /** @return function name -> { method name, parameter list } as declared in fe.tld */
    private Map<String, String[]> declaredFunctions() throws Exception {
        final Map<String, String[]> functions = new TreeMap<>();
        final Matcher block = FUNCTION.matcher(Files.readString(TLD, StandardCharsets.UTF_8));
        while (block.find()) {
            final Matcher name = NAME.matcher(block.group(1));
            final Matcher signature = SIGNATURE.matcher(block.group(1));
            assertTrue("a <function> without <name>: " + block.group(1), name.find());
            assertTrue("a <function> without a parsable <function-signature>: " + block.group(1), signature.find());
            functions.put(name.group(1), new String[] { signature.group(1), signature.group(2) });
        }
        return functions;
    }
}
