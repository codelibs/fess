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
package org.codelibs.fess.mylasta.direction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codelibs.fess.servlet.ErrorPageServlet;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Asserts that every component {@code lastaflute_director.xml} names can be loaded without the
 * servlet API on the classpath.
 *
 * <p>That file is included by {@code lastaflute_assist.xml}, which the crawler, thumbnail,
 * suggest and chunk child processes read when they build their own DI container. Those
 * processes run with {@code WEB-INF/classes} plus {@code WEB-INF/lib} plus
 * {@code WEB-INF/env/<type>/lib} on the classpath -- the servlet API is not there, because
 * Tomcat hands it to the webapp from the server's own {@code lib} directory instead. So a
 * servlet type reachable from one of these components fails verification in the child process,
 * and every crawl, thumbnail and suggest job then dies at DI container startup with a
 * {@code NoClassDefFoundError} that the webapp itself never sees. Nothing else in the build
 * catches that: the classes compile, the webapp starts, and only a crawl reveals it.</p>
 */
public class FessFwAssistantDirectorTest extends UnitFessTestCase {

    private static final String DIRECTOR_XML_PATH = "src/main/resources/lastaflute_director.xml";

    /** Prefix of the API the child processes do not have; see this class' javadoc. */
    private static final String MISSING_PACKAGE_PREFIX = "jakarta.servlet.";

    @Test
    public void test_directorComponentsLoadWithoutServletApi() throws Exception {
        final List<String> componentClassNames = readComponentClassNames();
        assertTrue(DIRECTOR_XML_PATH + " should name at least one component", !componentClassNames.isEmpty());
        for (final String className : componentClassNames) {
            final ClassLoader loader = new ServletFreeClassLoader(getClass().getClassLoader());
            try {
                Class.forName(className, true, loader);
            } catch (final NoClassDefFoundError | ClassNotFoundException e) {
                fail(className + " is named by " + DIRECTOR_XML_PATH
                        + " and must load in a child process, which has no servlet API on its classpath: " + e);
            }
        }
    }

    /**
     * Pins the two show-errors forward paths {@code FessFwAssistantDirector} hands to
     * {@code JspHtmlRenderingProvider}: the search side against the servlet that actually answers
     * it -- both that the two constants agree, and that the path is actually wired into
     * {@code web.xml} as a {@code <servlet-mapping>}, which is what makes the container route a
     * forward to it -- and the admin side against a JSP that actually exists on disk.
     *
     * <p>The second assertion is a regression guard: the admin path used to read
     * {@code /admin/error/error.jsp}, a file that has never existed -- only {@code admin_error.jsp}
     * is there -- so a forward down that path fell through to the container's own default error
     * page instead of Fess' admin error screen.</p>
     */
    @Test
    public void test_showErrorsForwardPaths() throws Exception {
        // ErrorPageServlet.SERVLET_PATH and FessFwAssistantDirector.SEARCH_ERROR_FORWARD_PATH
        // are both `static final String` fields with constant initializers, so a plain
        // `ErrorPageServlet.SERVLET_PATH` expression here would be inlined by javac into this
        // class' own bytecode at compile time: a stale incremental build that recompiles this
        // test without recompiling ErrorPageServlet would then compare two frozen literals
        // instead of catching real drift between them. Reading it by reflection forces an
        // actual runtime field read of the loaded class.
        final String servletPath = (String) ErrorPageServlet.class.getField("SERVLET_PATH").get(null);
        assertEquals(servletPath, FessFwAssistantDirector.SEARCH_ERROR_FORWARD_PATH);

        final String webXml = Files.readString(Path.of("src/main/webapp/WEB-INF/web.xml"), StandardCharsets.UTF_8);
        final Matcher mappingMatcher = Pattern
                .compile("<servlet-mapping>\\s*<servlet-name>([^<]+)</servlet-name>\\s*<url-pattern>"
                        + Pattern.quote(FessFwAssistantDirector.SEARCH_ERROR_FORWARD_PATH) + "</url-pattern>\\s*</servlet-mapping>")
                .matcher(webXml);
        assertTrue(mappingMatcher.find(), "the " + FessFwAssistantDirector.SEARCH_ERROR_FORWARD_PATH
                + " url-pattern must be inside a <servlet-mapping> in web.xml, or a forward to it never reaches ErrorPageServlet");

        assertTrue(Files.isRegularFile(Path.of("src/main/webapp/WEB-INF/view" + FessFwAssistantDirector.ADMIN_ERROR_FORWARD_PATH)));
    }

    /** Reads the {@code class} attribute of every {@code component} element in the director XML. */
    private List<String> readComponentClassNames() throws Exception {
        final File file = new File(DIRECTOR_XML_PATH);
        assertTrue(DIRECTOR_XML_PATH + " should exist", file.exists());
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // the DOCTYPE points at dbflute.org; the DTD adds nothing this test needs
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        final Document document = factory.newDocumentBuilder().parse(file);
        final NodeList components = document.getElementsByTagName("component");
        final List<String> classNames = new ArrayList<>();
        for (int i = 0; i < components.getLength(); i++) {
            final String className = ((Element) components.item(i)).getAttribute("class");
            if (!className.isEmpty()) {
                classNames.add(className);
            }
        }
        return classNames;
    }

    /**
     * Loads the application's own classes itself so that they resolve their dependencies through
     * this loader, and refuses the servlet API the way a child process' classpath refuses it.
     *
     * <p>Only the classes this loader defines see that refusal. Anything outside
     * {@link #APPLICATION_PACKAGE_PREFIX} -- LastaFlute, DBFlute, the libraries -- is defined by
     * the parent loader and keeps resolving its own references against the parent's classpath,
     * which does have the servlet API. So this catches a servlet type reachable from Fess' own
     * code, which is where the components in {@code lastaflute_director.xml} live and where the
     * regression this guards against happened; it would not catch one introduced through a
     * framework class.</p>
     */
    private static class ServletFreeClassLoader extends ClassLoader {

        /** Loaded by this loader rather than delegated, so that they see the refusal below. */
        private static final String APPLICATION_PACKAGE_PREFIX = "org.codelibs.fess.";

        ServletFreeClassLoader(final ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            if (name.startsWith(MISSING_PACKAGE_PREFIX)) {
                throw new ClassNotFoundException(name + " is not on a child process' classpath");
            }
            if (!name.startsWith(APPLICATION_PACKAGE_PREFIX)) {
                return super.loadClass(name, resolve);
            }
            synchronized (getClassLoadingLock(name)) {
                Class<?> clazz = findLoadedClass(name);
                if (clazz == null) {
                    clazz = defineFromParentResource(name);
                }
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        }

        private Class<?> defineFromParentResource(final String name) throws ClassNotFoundException {
            try (InputStream in = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                final byte[] bytes = in.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (final IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }
}
