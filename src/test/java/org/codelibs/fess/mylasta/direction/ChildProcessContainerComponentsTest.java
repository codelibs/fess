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
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Asserts that every component the crawler, suggest, thumbnail and chunk child processes can
 * reach through their own DI container loads, and answers {@code getMethods()}, without the
 * servlet API on the classpath.
 *
 * <p>Each of those four processes is launched ({@code CrawlJob}, {@code SuggestJob},
 * {@code GenerateThumbnailJob}, {@code ChunkVectorJob}) with a classpath of
 * {@code WEB-INF/env/<type>/resources} : {@code WEB-INF/classes} : {@code WEB-INF/lib/*.jar} :
 * {@code WEB-INF/env/<type>/lib/*.jar} -- there is no servlet API on it, because Tomcat supplies
 * that class to the webapp from the server's own {@code lib}, not from {@code WEB-INF/lib}. Each
 * process' entry point ({@code Crawler}, {@code SuggestCreator}, {@code ThumbnailGenerator},
 * {@code ChunkVectorIndexer}) calls {@code SingletonLaContainerFactory.setConfigPath("app.xml")},
 * and because {@code WEB-INF/env/<type>/resources} is first on that classpath, that name resolves
 * to {@code src/main/webapp/WEB-INF/env/<type>/resources/app.xml} -- a different, smaller file
 * than {@code src/main/resources/app.xml}, which only the webapp itself reads (where the servlet
 * API is present, so it is out of scope here).</p>
 *
 * <p>This test follows each of the four {@code app.xml} files' {@code <include>} graph -- plus
 * LastaDi's "+"-suffix convention, under which including {@code x.xml} auto-merges any
 * {@code x+*.xml} found on the classpath (e.g. including {@code fess_se.xml} also merges
 * {@code fess_se++.xml}, and {@code crawler_opensearch.xml} merges the three
 * {@code crawler_opensearch+*.xml} files) -- and, for every {@code <component class="...">} it
 * finds, loads the class through a loader that refuses {@code jakarta.servlet.*} and then calls
 * {@link Class#getMethods()} the way LastaDi's bean analysis does: that call itself resolves
 * every public method's parameter and return types and throws {@link NoClassDefFoundError} if one
 * of them is missing, which is exactly how this DI container startup fails in the child
 * process.</p>
 *
 * <p>An {@code <include>} that does not resolve to a file under {@code src/main/resources} (e.g.
 * {@code convention.xml}, {@code lastaflute_core.xml}, {@code crawler_opensearch.xml} -- all
 * packaged inside dependency jars, not this repository) is left unresolved: this test cannot see
 * the components those files declare and does not claim to guard them. Likewise, DI XMLs reachable
 * only from {@code src/main/resources/app.xml} (the webapp's own root -- e.g. {@code fess_api.xml},
 * {@code fess_cors.xml}, {@code fess_dict.xml}, {@code fess_job.xml}, {@code fess_ldap.xml},
 * {@code fess_query.xml}, {@code fess_rankfusion.xml}, {@code fess_score.xml}, {@code fess_sso.xml})
 * are out of scope: their components only ever run inside the webapp, which does have the servlet
 * API.</p>
 */
public class ChildProcessContainerComponentsTest extends UnitFessTestCase {

    /** Prefix of the API the child processes do not have; see this class' javadoc. */
    private static final String MISSING_PACKAGE_PREFIX = "jakarta.servlet.";

    /** The four child-process DI roots, as resolved by the job classes' classpath construction. */
    private static final List<String> ROOT_XML_PATHS =
            List.of("src/main/webapp/WEB-INF/env/crawler/resources/app.xml", "src/main/webapp/WEB-INF/env/suggest/resources/app.xml",
                    "src/main/webapp/WEB-INF/env/thumbnail/resources/app.xml", "src/main/webapp/WEB-INF/env/chunk/resources/app.xml");

    /** Where an {@code <include>} resolves when it is not a sibling of the including file (i.e. {@code WEB-INF/classes}). */
    private static final File RESOURCES_DIR = new File("src/main/resources");

    @Test
    public void test_childProcessComponentsLoadWithoutServletApi() throws Exception {
        final Set<String> componentClassNames = collectComponentClassNames();
        assertTrue(!componentClassNames.isEmpty(), "expected at least one component across " + ROOT_XML_PATHS);
        for (final String className : componentClassNames) {
            final ClassLoader loader = new ServletFreeClassLoader(getClass().getClassLoader());
            final Class<?> clazz;
            try {
                clazz = Class.forName(className, true, loader);
            } catch (final NoClassDefFoundError | ClassNotFoundException e) {
                fail(className + " is reachable from a child process' DI container, which has no servlet API on its classpath: " + e);
                return;
            }
            try {
                for (final Method method : clazz.getMethods()) {
                    method.getParameterTypes();
                    method.getReturnType();
                }
            } catch (final NoClassDefFoundError e) {
                fail(className + "'s public methods are reachable from a child process' DI container, which has no servlet API on "
                        + "its classpath: " + e);
            }
        }
    }

    /**
     * Follows the {@code <include>} graph from every root, plus the "+"-suffix convention, and
     * collects the {@code class} attribute of every {@code component} element found.
     */
    private Set<String> collectComponentClassNames() throws Exception {
        final Set<String> visitedFiles = new LinkedHashSet<>();
        final Deque<File> queue = new ArrayDeque<>();
        for (final String root : ROOT_XML_PATHS) {
            final File file = new File(root);
            assertTrue(file.isFile(), root + " should exist");
            queue.add(file);
        }
        final Set<String> classNames = new LinkedHashSet<>();
        while (!queue.isEmpty()) {
            final File file = queue.removeFirst();
            if (!visitedFiles.add(file.getCanonicalPath())) {
                continue;
            }
            final Document document = parse(file);
            classNames.addAll(readComponentClassNames(document));
            for (final String includePath : readIncludePaths(document)) {
                resolveInclude(file, includePath).ifPresent(queue::add);
                queue.addAll(resolvePlusSuffixSiblings(includePath));
            }
        }
        return classNames;
    }

    /** Resolves an {@code <include path="...">} against the including file's own directory, then against {@link #RESOURCES_DIR}. */
    private static java.util.Optional<File> resolveInclude(final File includingFile, final String includePath) {
        final File sibling = new File(includingFile.getParentFile(), includePath);
        if (sibling.isFile()) {
            return java.util.Optional.of(sibling);
        }
        final File fromResourcesRoot = new File(RESOURCES_DIR, includePath);
        if (fromResourcesRoot.isFile()) {
            return java.util.Optional.of(fromResourcesRoot);
        }
        // Not found in this repository -- packaged inside a dependency jar; see class javadoc.
        return java.util.Optional.empty();
    }

    /**
     * Finds every {@code <includePath-without-".xml">+*.xml} file under {@link #RESOURCES_DIR} --
     * LastaDi's convention for auto-merging plugin/extension component definitions into a
     * base file, whether or not that base file itself lives in this repository.
     */
    private static List<File> resolvePlusSuffixSiblings(final String includePath) {
        if (!includePath.endsWith(".xml")) {
            return List.of();
        }
        final String withoutExtension = includePath.substring(0, includePath.length() - ".xml".length());
        final int lastSlash = withoutExtension.lastIndexOf('/');
        final String dirPart = lastSlash >= 0 ? withoutExtension.substring(0, lastSlash) : "";
        final String baseName = lastSlash >= 0 ? withoutExtension.substring(lastSlash + 1) : withoutExtension;
        final File dir = dirPart.isEmpty() ? RESOURCES_DIR : new File(RESOURCES_DIR, dirPart);
        final File[] candidates = dir.listFiles((d, name) -> name.startsWith(baseName + "+") && name.endsWith(".xml"));
        if (candidates == null) {
            return List.of();
        }
        final List<File> siblings = new ArrayList<>();
        for (final File candidate : candidates) {
            siblings.add(candidate);
        }
        return siblings;
    }

    private static Document parse(final File file) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // the DOCTYPE points at dbflute.org; the DTD adds nothing this test needs
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return factory.newDocumentBuilder().parse(file);
    }

    /** Reads the {@code class} attribute of every {@code component} element in the document. */
    private static List<String> readComponentClassNames(final Document document) {
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

    /** Reads the {@code path} attribute of every {@code include} element in the document. */
    private static List<String> readIncludePaths(final Document document) {
        final NodeList includes = document.getElementsByTagName("include");
        final List<String> paths = new ArrayList<>();
        for (int i = 0; i < includes.getLength(); i++) {
            final String path = ((Element) includes.item(i)).getAttribute("path");
            if (!path.isEmpty()) {
                paths.add(path);
            }
        }
        return paths;
    }

    /**
     * Loads the application's own classes itself so that they resolve their dependencies through
     * this loader, and refuses the servlet API the way a child process' classpath refuses it.
     *
     * <p>Only the classes this loader defines see that refusal. Anything outside
     * {@link #APPLICATION_PACKAGE_PREFIX} -- LastaFlute, DBFlute, the libraries -- is defined by
     * the parent loader and keeps resolving its own references against the parent's classpath,
     * which does have the servlet API. So this catches a servlet type reachable from Fess' own
     * code, which is where the components declared in these DI XMLs live and where the regression
     * this guards against happened; it would not catch one introduced through a framework
     * class.</p>
     *
     * <p>Copied from {@code FessFwAssistantDirectorTest} rather than shared, to keep each guard
     * test self-contained.</p>
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
