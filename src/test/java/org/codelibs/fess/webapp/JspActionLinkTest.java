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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Guards {@code <la:link href="...">} and {@code <la:form action="...">} usage across every JSP.
 *
 * <p>Both attributes (and {@code <la:link>}'s lesser-used {@code action}/{@code page}
 * attributes) resolve an absolute path against LastaFlute's live action registry at
 * JSP-render time, not at JSP-compile time. A path with no matching action class only
 * fails when a browser actually opens the page -- exactly how the JSP search screen
 * removal broke both admin login screens: {@code <la:link href="/">} pointed at the
 * removed {@code RootAction}, and neither {@code mvn jspc:compile} (a plain Java-syntax
 * check) nor {@link FeTldUsageTest} (which only guards {@code fe:} EL functions) catch
 * this class of bug.</p>
 *
 * <p>This test re-implements the slice of {@code org.lastaflute.web.path.ActionPathResolver}'s
 * naming convention that Fess actually relies on: an absolute path's segments are consumed
 * left-to-right into a package ({@code org.codelibs.fess.app.web.<segments>}) and a class
 * name (the capitalized segments concatenated, plus {@code Action}); e.g.
 * {@code /admin/webconfig/} resolves to {@code org.codelibs.fess.app.web.admin.webconfig.AdminWebconfigAction}.
 * Any trailing segment left over once a class is found (e.g. {@code createnew} in
 * {@code /admin/searchlist/createnew}) must name a public method declared on that class,
 * mirroring how {@code ActionPathResolver} treats it as a routed method rather than more
 * package nesting. The empty path {@code /} is special-cased to {@code RootAction},
 * matching {@code ActionPathResolver.mappingActionPath}'s hardcoded {@code "root"} lookup.</p>
 *
 * <p>Both checks -- class existence and method existence -- are done by reading the
 * {@code .java} source under {@code src/main/java}, not by reflecting on compiled classes.
 * Maven's compiler plugin does not delete a {@code .class} file when its {@code .java}
 * source is removed, so a stale {@code target/classes/.../RootAction.class} left over from
 * a build predating its removal would let a reflection-based check pass vacuously; reading
 * source avoids that trap entirely.</p>
 *
 * <p>A {@code <la:form>} with no {@code action} attribute is not exempt: {@code
 * MappingHtmlFormTag.setupActionForNow()} then auto-derives the action path via {@code
 * ActionPathResolver.calculateActionPathByJspPath()}, using the JSP's own webapp-relative
 * path (view-root stripped) rather than any URL the JSP happens to be served under. For a
 * plain {@code word.jsp} (no underscore in the filename, e.g. {@code index.jsp}), that is
 * simply the JSP's containing directory as a URL; {@code word_word_..._word.jsp} names get
 * an extra rewrite (see {@link #resolveJspActionPath}). This is exactly how moving
 * {@code admin/login/index.jsp} out of {@code WEB-INF/view/login/} (while keeping
 * {@code LoginAction}'s URL at {@code /login/}) broke it: the bare form now auto-derives
 * {@code /admin/login/}, which no action class serves.</p>
 */
public class JspActionLinkTest extends UnitFessTestCase {

    /** Root of the JSP tree the application renders. */
    private static final Path VIEW_DIR = Paths.get("src/main/webapp/WEB-INF/view");

    /** Root package that every Fess action class lives under. */
    private static final Path ACTION_SOURCE_ROOT = Paths.get("src/main/java/org/codelibs/fess/app/web");

    /** Matches a whole {@code <la:link ...>} start tag, tolerating quoted attribute values and newlines. */
    private static final Pattern LINK_TAG = Pattern.compile("<la:link\\b((?:[^>\"]|\"[^\"]*\")*?)/?>", Pattern.DOTALL);

    /** Matches a whole {@code <la:form ...>} start tag, tolerating quoted attribute values and newlines. */
    private static final Pattern FORM_TAG = Pattern.compile("<la:form\\b((?:[^>\"]|\"[^\"]*\")*?)/?>", Pattern.DOTALL);

    /**
     * Matches one {@code href}, {@code action}, or {@code page} attribute -- the only
     * lasta-taglib attributes (on {@code la:link}/{@code la:form}) that resolve against
     * the action registry rather than being emitted as-is.
     */
    private static final Pattern ACTION_ATTRIBUTE = Pattern.compile("\\b(?:href|action|page)\\s*=\\s*\"([^\"]*)\"");

    /** Lower bound on the number of absolute paths checked, so a broken pattern cannot pass vacuously. */
    private static final int MINIMUM_EXPECTED_PATHS = 150;

    @Test
    public void test_everyAbsoluteActionPathResolvesToAnActionClass() throws Exception {
        final List<String> offenders = new ArrayList<>();
        int checked = 0;
        for (final Path jsp : listJspFiles()) {
            final String source = read(jsp);
            for (final String path : extractAbsolutePaths(LINK_TAG, source)) {
                checked++;
                if (!resolvesToActionClass(path)) {
                    offenders.add(jsp + " <la:link ...=\"" + path + "\">");
                }
            }
            checked += checkFormTags(jsp, source, offenders);
        }
        assertTrue(checked + " absolute action paths scanned, expected at least " + MINIMUM_EXPECTED_PATHS,
                checked >= MINIMUM_EXPECTED_PATHS);
        assertEquals("every absolute la:link/la:form path must resolve to an action class: " + offenders, 0, offenders.size());
    }

    /** @return every absolute (leading-slash) attribute value found on tags matching {@code tagPattern}. */
    private List<String> extractAbsolutePaths(final Pattern tagPattern, final String source) {
        final List<String> paths = new ArrayList<>();
        final Matcher tag = tagPattern.matcher(source);
        while (tag.find()) {
            final Matcher attr = ACTION_ATTRIBUTE.matcher(tag.group(1));
            while (attr.find()) {
                final String value = attr.group(1);
                if (value.startsWith("/")) { // absolute path only; relative hrefs are out of scope
                    paths.add(value);
                }
            }
        }
        return paths;
    }

    /**
     * Checks every {@code <la:form>} tag in {@code source}: a tag with an absolute
     * {@code action} is checked directly, and a tag with no {@code action} attribute at all
     * is checked against the path {@code MappingHtmlFormTag} would auto-derive from the
     * JSP's own location. A relative (non-absolute) {@code action} is out of scope, same as
     * for {@code la:link}.
     *
     * @return the number of checks performed, for the vacuous-pass guard
     */
    private int checkFormTags(final Path jsp, final String source, final List<String> offenders) throws IOException {
        int checked = 0;
        final Matcher tag = FORM_TAG.matcher(source);
        while (tag.find()) {
            final String tagAttributes = tag.group(1);
            final Matcher attr = ACTION_ATTRIBUTE.matcher(tagAttributes);
            boolean hasActionAttribute = false;
            while (attr.find()) {
                hasActionAttribute = true;
                final String value = attr.group(1);
                if (value.startsWith("/")) {
                    checked++;
                    if (!resolvesToActionClass(value)) {
                        offenders.add(jsp + " <la:form action=\"" + value + "\">");
                    }
                }
            }
            if (!hasActionAttribute) {
                checked++;
                final String derived = calculateActionPathByJspPath(jsp);
                if (!resolvesToActionClass(derived)) {
                    offenders.add(jsp + " <la:form> with no action attribute (derives \"" + derived + "\")");
                }
            }
        }
        return checked;
    }

    /**
     * Mirrors {@code ActionPathResolver.calculateActionPathByJspPath}: the JSP's own
     * webapp-relative path (view-root stripped) turned into its containing directory as a
     * URL, e.g. {@code admin/login/index.jsp} -> {@code /admin/login/}.
     */
    private String calculateActionPathByJspPath(final Path jsp) {
        final String requestPath = "/" + toUnixPath(VIEW_DIR.relativize(jsp));
        final int lastSlash = requestPath.lastIndexOf('/');
        final String frontPathElement = requestPath.substring(0, lastSlash); // e.g. /admin/login
        final String fileName = requestPath.substring(lastSlash + 1); // e.g. index.jsp
        final String pathBase = frontPathElement + "/";
        final String fileNameNoExt = fileName.substring(0, fileName.lastIndexOf('.'));
        if (!fileNameNoExt.contains("_")) { // e.g. index.jsp, newpassword.jsp
            return pathBase;
        }
        final List<String> wordList = Arrays.asList(fileNameNoExt.split("_"));
        if (wordList.size() < 2) {
            return pathBase;
        }
        final String resolved = resolveJspActionPath(frontPathElement, pathBase, wordList);
        return resolved != null ? resolved : pathBase;
    }

    /**
     * Mirrors {@code ActionPathResolver.resolveJspActionPath}: for a
     * {@code word_..._word_word.jsp} name whose containing directory ends with all but the
     * last word (e.g. {@code member/list/member_purchase_list.jsp} under {@code member/list/}),
     * the last word becomes a sub-path of the directory instead of the bare directory itself.
     */
    private String resolveJspActionPath(final String frontPathElement, final String pathBase, final List<String> wordList) {
        String previousSuffix = "";
        for (int i = 0; i < wordList.size(); i++) {
            final String pathSuffix = previousSuffix + "/" + wordList.get(i);
            final boolean nextLoopLast = wordList.size() == i + 2;
            if (nextLoopLast && frontPathElement.endsWith(pathSuffix)) {
                final String lastElement = wordList.get(i + 1);
                return "index".equals(lastElement) ? pathBase : pathBase + lastElement + "/";
            }
            previousSuffix = pathSuffix;
        }
        return null;
    }

    private String toUnixPath(final Path relative) {
        final List<String> parts = new ArrayList<>();
        relative.forEach(p -> parts.add(p.toString()));
        return String.join("/", parts);
    }

    /**
     * @return true if {@code absolutePath} resolves to a real action class by LastaFlute's
     *         naming convention, trying the longest possible class-owning prefix of path
     *         segments first and falling back to shorter ones (with the leftover segments
     *         required to name a public method) the way {@code ActionPathResolver} does.
     */
    private boolean resolvesToActionClass(final String absolutePath) throws IOException {
        final List<String> segments = splitSegments(stripQueryAndFragment(absolutePath));
        if (segments.isEmpty()) {
            return Files.isRegularFile(actionSourceFile(Collections.emptyList(), "RootAction"));
        }
        for (int splitIndex = segments.size(); splitIndex >= 1; splitIndex--) {
            final List<String> classSegments = segments.subList(0, splitIndex);
            final List<String> remaining = segments.subList(splitIndex, segments.size());
            final Path sourceFile = actionSourceFile(classSegments, buildClassName(classSegments));
            if (!Files.isRegularFile(sourceFile)) {
                continue;
            }
            if (remaining.isEmpty() || declaresPublicMethod(sourceFile, remaining.get(0))) {
                return true;
            }
        }
        return false;
    }

    private String stripQueryAndFragment(final String path) {
        int cut = path.length();
        final int question = path.indexOf('?');
        if (question >= 0) {
            cut = Math.min(cut, question);
        }
        final int hash = path.indexOf('#');
        if (hash >= 0) {
            cut = Math.min(cut, hash);
        }
        return path.substring(0, cut);
    }

    private List<String> splitSegments(final String path) {
        return Stream.of(path.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    /** @return e.g. "AdminWebconfigAction" for [admin, webconfig]. */
    private String buildClassName(final List<String> segments) {
        return segments.stream().map(this::capitalize).collect(Collectors.joining()) + "Action";
    }

    private String capitalize(final String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private Path actionSourceFile(final List<String> packageSegments, final String className) {
        Path dir = ACTION_SOURCE_ROOT;
        for (final String segment : packageSegments) {
            dir = dir.resolve(segment);
        }
        return dir.resolve(className + ".java");
    }

    private boolean declaresPublicMethod(final Path sourceFile, final String methodName) throws IOException {
        final Pattern methodDeclaration = Pattern.compile("\\bpublic\\s+\\S+\\s+" + Pattern.quote(methodName) + "\\s*\\(");
        return methodDeclaration.matcher(read(sourceFile)).find();
    }

    private List<Path> listJspFiles() throws IOException {
        assertTrue(VIEW_DIR + " should exist", Files.isDirectory(VIEW_DIR));
        try (Stream<Path> paths = Files.walk(VIEW_DIR)) {
            final List<Path> files = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".jsp")).sorted().toList();
            assertFalse("no JSP found under " + VIEW_DIR, files.isEmpty());
            return files;
        }
    }

    private String read(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
