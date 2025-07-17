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
package org.codelibs.fess.util;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.servlet.ServletContext;

/**
 * Utility class for accessing various resource paths and files in the Fess application.
 * This class provides methods to retrieve paths for configuration files, templates, dictionaries,
 * thumbnails, plugins, and other resources required by the Fess search engine.
 * It supports both regular deployment and Docker container environments.
 *
 */
public class ResourceUtil {
    /** Environment variable name for overriding the configuration path */
    private static final String FESS_OVERRIDE_CONF_PATH = "FESS_OVERRIDE_CONF_PATH";

    /** Environment variable name for specifying the application type */
    private static final String FESS_APP_TYPE = "FESS_APP_TYPE";

    /** Constant value representing Docker application type */
    private static final String FESS_APP_DOCKER = "docker";

    /**
     * Protected constructor to prevent instantiation of this utility class.
     * This class is designed to be used statically.
     */
    protected ResourceUtil() {
        // nothing
    }

    /**
     * Gets the HTTP URL for the OpenSearch (Fesen) server.
     * First checks for a system-configured search engine address,
     * then falls back to the URL configured in FessConfig.
     *
     * @return the HTTP URL for the OpenSearch server
     */
    public static String getFesenHttpUrl() {
        final String url = SystemUtil.getSearchEngineHttpAddress();
        if (url != null) {
            return url;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessConfig.getFesenHttpUrl();
    }

    /**
     * Gets the application type from the environment variable FESS_APP_TYPE.
     * This is used to determine the deployment environment (e.g., "docker").
     *
     * @return the application type string, or empty string if not set
     */
    public static String getAppType() {
        final String appType = System.getenv(FESS_APP_TYPE);
        if (StringUtil.isNotBlank(appType)) {
            return appType;
        }
        return StringUtil.EMPTY;
    }

    /**
     * Gets the override configuration path from environment variable when running in Docker.
     * This allows customization of the configuration directory location in containerized deployments.
     *
     * @return an OptionalEntity containing the override configuration path if set and running in Docker,
     *         or empty OptionalEntity otherwise
     */
    public static OptionalEntity<String> getOverrideConfPath() {
        if (FESS_APP_DOCKER.equalsIgnoreCase(getAppType())) {
            final String confPath = System.getenv(FESS_OVERRIDE_CONF_PATH);
            if (StringUtil.isNotBlank(confPath)) {
                return OptionalEntity.of(confPath);
            }
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets the path to configuration files. In Docker environments, checks /opt/fess first,
     * then falls back to system property FESS_CONF_PATH, and finally to WEB-INF/conf.
     *
     * @param names the path components to append to the configuration directory
     * @return the Path object pointing to the configuration file or directory
     */
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
        return getPath("WEB-INF/", "conf", names);
    }

    /**
     * Gets the path to configuration files, falling back to classpath resources if not found.
     * First attempts to find the file in the configuration directory, then searches the classpath.
     *
     * @param names the path components to append to the configuration directory
     * @return the Path object pointing to the configuration file, either in conf directory or classpath
     */
    public static Path getConfOrClassesPath(final String... names) {
        final Path confPath = getConfPath(names);
        if (Files.exists(confPath)) {
            return confPath;
        }
        return org.codelibs.core.io.ResourceUtil.getResourceAsFile(String.join("/", names)).toPath();
    }

    /**
     * Gets the path to compiled classes directory.
     *
     * @param names the path components to append to the classes directory
     * @return the Path object pointing to the classes directory
     */
    public static Path getClassesPath(final String... names) {
        return getPath("WEB-INF/", "classes", names);
    }

    /**
     * Gets the path to original files directory.
     *
     * @param names the path components to append to the orig directory
     * @return the Path object pointing to the original files directory
     */
    public static Path getOrigPath(final String... names) {
        return getPath("WEB-INF/", "orig", names);
    }

    /**
     * Gets the path to email template files directory.
     *
     * @param names the path components to append to the mail template directory
     * @return the Path object pointing to the mail template directory
     */
    public static Path getMailTemplatePath(final String... names) {
        return getPath("WEB-INF/", "mail", names);
    }

    /**
     * Gets the path to view template files directory.
     *
     * @param names the path components to append to the view template directory
     * @return the Path object pointing to the view template directory
     */
    public static Path getViewTemplatePath(final String... names) {
        return getPath("WEB-INF/", "view", names);
    }

    /**
     * Gets the path to dictionary files directory.
     *
     * @param names the path components to append to the dictionary directory
     * @return the Path object pointing to the dictionary directory
     */
    public static Path getDictionaryPath(final String... names) {
        return getPath("WEB-INF/", "dict", names);
    }

    /**
     * Gets the path to thumbnail files directory.
     *
     * @param names the path components to append to the thumbnails directory
     * @return the Path object pointing to the thumbnails directory
     */
    public static Path getThumbnailPath(final String... names) {
        return getPath("WEB-INF/", "thumbnails", names);
    }

    /**
     * Gets the path to site-specific files directory.
     *
     * @param names the path components to append to the site directory
     * @return the Path object pointing to the site directory
     */
    public static Path getSitePath(final String... names) {
        return getPath("WEB-INF/", "site", names);
    }

    /**
     * Gets the path to plugin files directory.
     *
     * @param names the path components to append to the plugin directory
     * @return the Path object pointing to the plugin directory
     */
    public static Path getPluginPath(final String... names) {
        return getPath("WEB-INF/", "plugin", names);
    }

    /**
     * Gets the path to the project properties file.
     *
     * @return the Path object pointing to the project.properties file
     */
    public static Path getProjectPropertiesFile() {
        return getPath("WEB-INF/", StringUtil.EMPTY, "project.properties");
    }

    /**
     * Gets the path to image files directory.
     *
     * @param names the path components to append to the images directory
     * @return the Path object pointing to the images directory
     */
    public static Path getImagePath(final String... names) {
        return getPath(StringUtil.EMPTY, "images", names);
    }

    /**
     * Gets the path to CSS files directory.
     *
     * @param names the path components to append to the CSS directory
     * @return the Path object pointing to the CSS directory
     */
    public static Path getCssPath(final String... names) {
        return getPath(StringUtil.EMPTY, "css", names);
    }

    /**
     * Gets the path to JavaScript files directory.
     *
     * @param names the path components to append to the JavaScript directory
     * @return the Path object pointing to the JavaScript directory
     */
    public static Path getJavaScriptPath(final String... names) {
        return getPath(StringUtil.EMPTY, "js", names);
    }

    /**
     * Gets the path to environment-specific files directory.
     *
     * @param envName the environment name (e.g., "python", "ruby")
     * @param names the path components to append to the environment directory
     * @return the Path object pointing to the environment-specific directory
     */
    public static Path getEnvPath(final String envName, final String... names) {
        return getPath("WEB-INF/", "env/" + envName, names);
    }

    /**
     * Gets the path by trying multiple locations in order of preference.
     * First tries to get the real path from servlet context, then checks various
     * fallback locations including source and target directories.
     *
     * @param root the root directory (e.g., "WEB-INF/")
     * @param base the base directory under root (e.g., "conf", "classes")
     * @param names the path components to append to the base directory
     * @return the Path object pointing to the requested resource
     */
    protected static Path getPath(final String root, final String base, final String... names) {

        try {
            final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
            final String webinfPath = servletContext.getRealPath("/" + root + base);
            if (webinfPath != null && Files.exists(Paths.get(webinfPath))) {
                return Paths.get(webinfPath, names);
            }
        } catch (final Throwable e) {
            // ignore
        }
        final String webinfBase = root + base;
        if (Files.exists(Paths.get(webinfBase))) {
            return Paths.get(webinfBase, names);
        }
        final String srcWebInfBase = "src/main/webapps" + root + base;
        if (Files.exists(Paths.get(srcWebInfBase))) {
            return Paths.get(srcWebInfBase, names);
        }
        final String targetWebInfBase = "target/fess/" + root + base;
        if (Files.exists(Paths.get(targetWebInfBase))) {
            return Paths.get(targetWebInfBase, names);
        }
        return Paths.get(webinfBase, names);
    }

    /**
     * Gets JAR files from the WEB-INF/lib directory that start with the specified prefix.
     *
     * @param namePrefix the prefix that JAR file names should start with
     * @return an array of File objects representing matching JAR files, or empty array if none found
     */
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
        if (!libDir.exists()) {
            return new File[0];
        }
        return libDir.listFiles((file, name) -> name.startsWith(namePrefix));
    }

    /**
     * Gets plugin JAR files from the plugin directory that start with the specified prefix.
     *
     * @param namePrefix the prefix that plugin JAR file names should start with
     * @return an array of File objects representing matching plugin JAR files, or empty array if none found
     */
    public static File[] getPluginJarFiles(final String namePrefix) {
        return getPluginJarFiles((file, name) -> name.startsWith(namePrefix));
    }

    /**
     * Gets plugin JAR files from the plugin directory that match the specified filter.
     *
     * @param filter the FilenameFilter to apply when selecting plugin JAR files
     * @return an array of File objects representing matching plugin JAR files, or empty array if none found
     */
    public static File[] getPluginJarFiles(final FilenameFilter filter) {
        final File libDir = getPluginPath().toFile();
        if (!libDir.exists()) {
            return new File[0];
        }
        return libDir.listFiles(filter);
    }

    /**
     * Resolves system properties in a given string.
     * @param value The string to resolve.
     * @return The resolved string.
     */
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
