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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

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
