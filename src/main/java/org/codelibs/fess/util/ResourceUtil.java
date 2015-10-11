/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.util;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.codelibs.core.lang.StringUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.web.util.LaServletContextUtil;

public class ResourceUtil {
    protected ResourceUtil() {
        // nothing
    }

    public static Path getConfPath(final String... names) {
        return getPath("conf", names);
    }

    public static Path getClassesPath(final String... names) {
        return getPath("classes", names);
    }

    public static Path getOrigPath(final String... names) {
        return getPath("orig", names);
    }

    public static Path getMailTemplatePath(final String... names) {
        return getPath("mail", names);
    }

    public static Path getViewTemplatePath(final String... names) {
        return getPath("view", names);
    }

    public static Path getDictionaryPath(final String... names) {
        return getPath("dict", names);
    }

    protected static Path getPath(final String base, String... names) {

        try {
            final ServletContext servletContext = SingletonLaContainer.getComponent(ServletContext.class);
            String webinfoPath = servletContext.getRealPath("/WEB-INF/" + base);
            if (webinfoPath != null) {
                Path path = Paths.get(webinfoPath, names);
                if (Files.exists(path)) {
                    return path;
                }
            }
        } catch (final Throwable e) { // NOSONAR
            // ignore
        }
        final Path defaultPath = Paths.get("WEB-INF/" + base, names);
        if (Files.exists(defaultPath)) {
            return defaultPath;
        }
        final Path srcBasePath = Paths.get("src/main/webapps/WEB-INF/" + base, names);
        if (Files.exists(srcBasePath)) {
            return srcBasePath;
        }
        final Path targetBasePath = Paths.get("target/fess/WEB-INF/" + base, names);
        if (Files.exists(targetBasePath)) {
            return targetBasePath;
        }
        return defaultPath;
    }

    public static File[] getJarFiles(final String namePrefix) {
        final ServletContext context = LaServletContextUtil.getServletContext();
        if (context == null) {
            return new File[0];
        }
        final String libPath = context.getRealPath("/WEB-INF/lib");
        if (StringUtil.isBlank(libPath)) {
            return new File[0];
        }
        final File libDir = new File(libPath);
        return libDir.listFiles((FilenameFilter) (file, name) -> name.startsWith(namePrefix));
    }

    public static String resolve(final String value) {
        if (value == null) {
            return null;
        }

        final StringBuffer tunedText = new StringBuffer(value.length());
        final Pattern pattern = Pattern.compile("(\\$\\{([\\w\\.]+)\\})");
        final Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            final String key = matcher.group(2);
            String replacement = System.getProperty(key);
            if (replacement == null) {
                replacement = matcher.group(1);
            }
            matcher.appendReplacement(tunedText, replacement.replace("\\", "\\\\").replace("$", "\\$"));

        }
        matcher.appendTail(tunedText);
        return tunedText.toString();
    }
}
