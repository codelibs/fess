/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.util.LaServletContextUtil;

public class ResourceUtil {
    private static final String FESS_OVERRIDE_CONF_PATH = "FESS_OVERRIDE_CONF_PATH";

    private static final String FESS_APP_TYPE = "FESS_APP_TYPE";

    private static final String FESS_APP_DOCKER = "docker";

    protected ResourceUtil() {
        // nothing
    }

    public static String getElasticsearchHttpUrl() {
        final String url = System.getProperty("fess.es.http_address");
        if (url != null) {
            return url;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getElasticsearchHttpUrl();
    }

    public static String getAppType() {
        final String appType = System.getenv(FESS_APP_TYPE);
        if (StringUtil.isNotBlank(appType)) {
            return appType;
        }
        return StringUtil.EMPTY;
    }

    public static OptionalEntity<String> getOverrideConfPath() {
        if (FESS_APP_DOCKER.equalsIgnoreCase(getAppType())) {
            final String confPath = System.getenv(FESS_OVERRIDE_CONF_PATH);
            if (StringUtil.isNotBlank(confPath)) {
                return OptionalEntity.of(confPath);
            }
        }
        return OptionalEntity.empty();
    }

    public static Path getConfPath(final String... names) {
        if (FESS_APP_DOCKER.equalsIgnoreCase(getAppType())) {
            final Path confPath = Paths.get("/opt/fess", names);
            if (Files.exists(confPath)) {
                return confPath;
            }
        }
        final String confPath = System.getProperty(Constants.FESS_CONF_PATH);
        if (StringUtil.isNotBlank(confPath)) {
            return Paths.get(confPath, names);
        }
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

    public static Path getThumbnailPath(final String... names) {
        return getPath("thumbnails", names);
    }

    public static Path getSitePath(final String... names) {
        return getPath("site", names);
    }

    public static Path getProjectPropertiesFile() {
        return getPath("", "project.properties");
    }

    protected static Path getPath(final String base, final String... names) {

        try {
            final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
            final String webinfPath = servletContext.getRealPath("/WEB-INF/" + base);
            if (webinfPath != null) {
                if (Files.exists(Paths.get(webinfPath))) {
                    return Paths.get(webinfPath, names);
                }
            }
        } catch (final Throwable e) {
            // ignore
        }
        final String webinfBase = "WEB-INF/" + base;
        if (Files.exists(Paths.get(webinfBase))) {
            return Paths.get(webinfBase, names);
        }
        final String srcWebInfBase = "src/main/webapps/WEB-INF/" + base;
        if (Files.exists(Paths.get(srcWebInfBase))) {
            return Paths.get(srcWebInfBase, names);
        }
        final String targetWebInfBase = "target/fess/WEB-INF/" + base;
        if (Files.exists(Paths.get(targetWebInfBase))) {
            return Paths.get(targetWebInfBase, names);
        }
        return Paths.get(webinfBase, names);
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
