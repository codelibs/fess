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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.codelibs.core.lang.StringUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.struts.util.ServletContextUtil;

public class ResourceUtil {
    protected ResourceUtil() {
        // nothing
    }

    public static String getDbPath(final String name) {
        return getBasePath("WEB-INF/db/", name);
    }

    public static String getConfPath(final String name) {
        return getBasePath("WEB-INF/conf/", name);
    }

    public static String getClassesPath(final String name) {
        return getBasePath("WEB-INF/classes/", name);
    }

    public static String getOrigPath(final String name) {
        return getBasePath("WEB-INF/orig/", name);
    }

    public static String getMailTemplatePath(final String name) {
        return getBasePath("WEB-INF/mail/", name);
    }

    public static String getViewTemplatePath(final String name) {
        return getBasePath("WEB-INF/view/", name);
    }

    protected static String getBasePath(final String baseName, final String name) {

        String path = null;
        try {
            final ServletContext servletContext = SingletonS2Container.getComponent(ServletContext.class);
            if (servletContext != null) {
                path = servletContext.getRealPath("/" + baseName + name);
            }
        } catch (final Exception e) { // NOSONAR
            // ignore
        }
        if (path == null) {
            path = new File(baseName + name).getAbsolutePath();
        }
        return path;
    }

    public static File[] getJarFiles(final String namePrefix) {
        final ServletContext context = ServletContextUtil.getServletContext();
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
