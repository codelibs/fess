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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.PluginException;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Helper class for managing Fess plugins and artifacts.
 * This class provides functionality to discover, install, and manage various types of plugins
 * including data stores, themes, ingest processors, scripts, web applications, thumbnails, and crawlers.
 */
public class PluginHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(PluginHelper.class);

    /**
     * Cache for storing available artifacts by type.
     * The cache expires after 5 minutes and has a maximum size of 10 entries.
     */
    protected LoadingCache<ArtifactType, Artifact[]> availableArtifacts = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<ArtifactType, Artifact[]>() {
                @Override
                public Artifact[] load(final ArtifactType key) {
                    final List<Artifact> list = new ArrayList<>();
                    for (final String url : getRepositories()) {
                        if (url.endsWith(".yaml")) {
                            if (key == ArtifactType.UNKNOWN) {
                                list.addAll(loadArtifactsFromRepository(url));
                            }
                        } else {
                            list.addAll(processRepository(key, url));
                        }
                    }
                    return list.toArray(new Artifact[list.size()]);
                }
            });

    /**
     * Default constructor for PluginHelper.
     * Initializes the plugin helper with default settings.
     */
    public PluginHelper() {
        // Default constructor
    }

    /**
     * Retrieves available artifacts of the specified type from configured repositories.
     *
     * @param artifactType the type of artifacts to retrieve
     * @return an array of available artifacts
     * @throws PluginException if failed to access the artifact repository
     */
    public Artifact[] getAvailableArtifacts(final ArtifactType artifactType) {
        try {
            return availableArtifacts.get(artifactType);
        } catch (final Exception e) {
            throw new PluginException("Failed to access " + artifactType, e);
        }
    }

    /**
     * Gets the list of configured plugin repositories.
     *
     * @return an array of repository URLs
     */
    protected String[] getRepositories() {
        return split(ComponentUtil.getFessConfig().getPluginRepositories(), ",")
                .get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
    }

    /**
     * Loads artifacts from a YAML-based repository.
     *
     * @param url the URL of the YAML repository
     * @return a list of artifacts loaded from the repository
     * @throws PluginException if failed to parse the repository content
     */
    protected List<Artifact> loadArtifactsFromRepository(final String url) {
        final String content = getRepositoryContent(url);
        final ObjectMapper objectMapper = new YAMLMapper();
        try {
            @SuppressWarnings("unchecked")
            final List<Map<?, ?>> result = objectMapper.readValue(content, List.class);
            if (result != null) {
                return result.stream()
                        .map(o -> new Artifact((String) o.get("name"), (String) o.get("version"), (String) o.get("url")))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (final Exception e) {
            throw new PluginException("Failed to access " + url, e);
        }
    }

    /**
     * Processes a Maven-style repository to extract artifacts of the specified type.
     *
     * @param artifactType the type of artifacts to process
     * @param url the URL of the repository
     * @return a list of artifacts found in the repository
     */
    protected List<Artifact> processRepository(final ArtifactType artifactType, final String url) {
        final List<Artifact> list = new ArrayList<>();
        final String repoContent = getRepositoryContent(url);
        final Matcher matcher = Pattern.compile("href=\"[^\"]*(" + artifactType.getId() + "[a-zA-Z0-9\\-]+)/?\"").matcher(repoContent);
        while (matcher.find()) {
            final String name = matcher.group(1);
            if (isExcludedName(artifactType, name)) {
                continue;
            }
            final String pluginUrl = url + (url.endsWith("/") ? name + "/" : "/" + name + "/");
            try {
                final String pluginMetaContent = getRepositoryContent(pluginUrl + "maven-metadata.xml");
                try (final InputStream is = new ByteArrayInputStream(pluginMetaContent.getBytes(Constants.UTF_8_CHARSET))) {
                    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature(Constants.FEATURE_SECURE_PROCESSING, true);
                    factory.setFeature(Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
                    factory.setFeature(Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
                    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtil.EMPTY);
                    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtil.EMPTY);
                    final DocumentBuilder builder = factory.newDocumentBuilder();
                    final Document document = builder.parse(is);
                    final NodeList nodeList = document.getElementsByTagName("version");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final String version = nodeList.item(i).getTextContent();
                        if (isTargetPluginVersion(version)) {
                            if (version.endsWith("SNAPSHOT")) {
                                final String snapshotVersion = getSnapshotActualVersion(builder, pluginUrl, version);
                                if (StringUtil.isNotBlank(snapshotVersion)) {
                                    final String actualVersion = version.replace("SNAPSHOT", snapshotVersion);
                                    list.add(new Artifact(name, actualVersion,
                                            pluginUrl + version + "/" + name + "-" + actualVersion + ".jar"));
                                } else if (logger.isDebugEnabled()) {
                                    logger.debug("Snapshot name not found: name={}, version={}", name, version);
                                }
                            } else {
                                list.add(new Artifact(name, version, pluginUrl + version + "/" + name + "-" + version + ".jar"));
                            }
                        } else if (logger.isDebugEnabled()) {
                            logger.debug("Artifact ignored: name={}, version={}", name, version);
                        }
                    }
                }
            } catch (final Exception e) {
                logger.warn("Failed to parse maven-metadata.xml: url={}", pluginUrl, e);
            }
        }
        return list;
    }

    /**
     * Checks if an artifact name should be excluded from the results.
     *
     * @param artifactType the type of the artifact
     * @param name the name of the artifact
     * @return true if the artifact should be excluded, false otherwise
     */
    protected boolean isExcludedName(final ArtifactType artifactType, final String name) {
        if (artifactType != ArtifactType.CRAWLER) {
            return false;
        }

        if ("fess-crawler".equals(name)//
                || "fess-crawler-db".equals(name)//
                || "fess-crawler-db-h2".equals(name)//
                || "fess-crawler-db-mysql".equals(name)//
                || "fess-crawler-es".equals(name)//
                || "fess-crawler-opensearch".equals(name)//
                || "fess-crawler-lasta".equals(name)//
                || "fess-crawler-parent".equals(name)//
                || "fess-crawler-playwright".equals(name)//
                || "fess-crawler-webdriver".equals(name)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a plugin version is a target version for the current Fess installation.
     *
     * @param version the version to check
     * @return true if the version is a target version, false otherwise
     */
    protected boolean isTargetPluginVersion(final String version) {
        return ComponentUtil.getFessConfig().isTargetPluginVersion(version);
    }

    /**
     * Gets the actual version string for a SNAPSHOT artifact by parsing the snapshot metadata.
     *
     * @param builder the document builder to use for parsing XML
     * @param pluginUrl the URL of the plugin
     * @param version the snapshot version
     * @return the actual version string with timestamp and build number, or null if not found
     * @throws SAXException if XML parsing fails
     * @throws IOException if I/O error occurs
     */
    protected String getSnapshotActualVersion(final DocumentBuilder builder, final String pluginUrl, final String version)
            throws SAXException, IOException {
        String timestamp = null;
        String buildNumber = null;
        final String versionMetaContent = getRepositoryContent(pluginUrl + version + "/maven-metadata.xml");
        try (final InputStream is = new ByteArrayInputStream(versionMetaContent.getBytes(Constants.UTF_8_CHARSET))) {
            final Document doc = builder.parse(is);
            final NodeList snapshotNodeList = doc.getElementsByTagName("snapshot");
            if (snapshotNodeList.getLength() > 0) {
                final NodeList nodeList = snapshotNodeList.item(0).getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    if ("timestamp".equalsIgnoreCase(node.getNodeName())) {
                        timestamp = node.getTextContent();
                    } else if ("buildNumber".equalsIgnoreCase(node.getNodeName())) {
                        buildNumber = node.getTextContent();
                    }
                }
            }
        }
        if (StringUtil.isNotBlank(timestamp) && StringUtil.isNotBlank(buildNumber)) {
            return timestamp + "-" + buildNumber;
        }
        return null;
    }

    /**
     * Retrieves the content of a repository URL.
     *
     * @param url the URL to retrieve content from
     * @return the content as a string
     * @throws IORuntimeException if I/O error occurs
     */
    protected String getRepositoryContent(final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading: url={}", url);
        }
        try (final CurlResponse response = createCurlRequest(url).execute()) {
            return response.getContentAsString();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Gets the list of installed artifacts of the specified type.
     *
     * @param artifactType the type of artifacts to retrieve
     * @return an array of installed artifacts
     */
    public Artifact[] getInstalledArtifacts(final ArtifactType artifactType) {
        if (artifactType == ArtifactType.UNKNOWN) {
            final File[] jarFiles = ResourceUtil.getPluginJarFiles((d, n) -> {
                for (final ArtifactType type : ArtifactType.values()) {
                    if (n.startsWith(type.getId())) {
                        return false;
                    }
                }
                return n.endsWith(".jar");
            });
            final List<Artifact> list = new ArrayList<>(jarFiles.length);
            for (final File file : jarFiles) {
                list.add(getArtifactFromFileName(artifactType, file.getName()));
            }
            list.sort(Comparator.comparing(Artifact::getName));
            return list.toArray(new Artifact[list.size()]);
        }

        final File[] jarFiles = ResourceUtil.getPluginJarFiles(artifactType.getId());
        final List<Artifact> list = new ArrayList<>(jarFiles.length);
        for (final File file : jarFiles) {
            list.add(getArtifactFromFileName(artifactType, file.getName()));
        }
        list.sort(Comparator.comparing(Artifact::getName));
        return list.toArray(new Artifact[list.size()]);
    }

    /**
     * Creates an artifact instance from a filename.
     *
     * @param artifactType the type of the artifact
     * @param filename the filename to parse
     * @return an artifact instance
     */
    protected Artifact getArtifactFromFileName(final ArtifactType artifactType, final String filename) {
        return getArtifactFromFileName(artifactType, filename, null);
    }

    /**
     * Creates an artifact instance from a filename with a specified URL.
     *
     * @param artifactType the type of the artifact
     * @param filename the filename to parse
     * @param url the URL of the artifact
     * @return an artifact instance
     */
    public Artifact getArtifactFromFileName(final ArtifactType artifactType, final String filename, final String url) {
        final String baseName = StringUtils.removeEndIgnoreCase(filename, ".jar");
        final List<String> nameList = new ArrayList<>();
        final List<String> versionList = new ArrayList<>();
        boolean isName = true;
        for (final String value : baseName.split("-")) {
            if (isName && value.length() > 0 && value.charAt(0) >= '0' && value.charAt(0) <= '9') {
                isName = false;
            }
            if (isName) {
                nameList.add(value);
            } else {
                versionList.add(value);
            }
        }
        return new Artifact(nameList.stream().collect(Collectors.joining("-")), versionList.stream().collect(Collectors.joining("-")), url);
    }

    /**
     * Installs an artifact based on its type.
     *
     * @param artifact the artifact to install
     */
    public void installArtifact(final Artifact artifact) {
        switch (artifact.getType()) {
        case THEME:
            install(artifact);
            ComponentUtil.getThemeHelper().install(artifact);
            break;
        default:
            install(artifact);
            break;
        }
    }

    /**
     * Installs an artifact by downloading it from its URL.
     *
     * @param artifact the artifact to install
     * @throws PluginException if installation fails
     */
    protected void install(final Artifact artifact) {
        final String fileName = artifact.getFileName();
        final String url = artifact.getUrl();
        if (StringUtil.isBlank(url)) {
            throw new PluginException("url is blank: " + artifact.getName());
        }
        if (url.startsWith("http:") || url.startsWith("https:")) {
            try (final CurlResponse response = createCurlRequest(url).execute()) {
                if (response.getHttpStatusCode() != 200) {
                    throw new PluginException("HTTP Status " + response.getHttpStatusCode() + " : failed to get the artifact from " + url);
                }
                try (final InputStream in = response.getContentAsStream()) {
                    CopyUtil.copy(in, ResourceUtil.getPluginPath(fileName).toFile());
                }
            } catch (final Exception e) {
                throw new PluginException("Failed to install the artifact " + artifact.getName(), e);
            }
        } else {
            try (final InputStream in = new FileInputStream(url)) {
                CopyUtil.copy(in, ResourceUtil.getPluginPath(fileName).toFile());
            } catch (final Exception e) {
                throw new PluginException("Failed to install the artifact " + artifact.getName(), e);
            }
        }
    }

    /**
     * Creates a CURL request for the specified URL with proxy configuration if available.
     *
     * @param url the URL to create a request for
     * @return a configured CURL request
     */
    protected CurlRequest createCurlRequest(final String url) {
        final CurlRequest request = Curl.get(url);
        final Proxy proxy = ComponentUtil.getFessConfig().getHttpProxy();
        if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
            request.proxy(proxy);
        }
        return request;
    }

    /**
     * Deletes an installed artifact.
     *
     * @param artifact the artifact to delete
     * @throws PluginException if the artifact does not exist or deletion fails
     */
    public void deleteInstalledArtifact(final Artifact artifact) {
        final String fileName = artifact.getFileName();
        final Path jarPath = Paths.get(ResourceUtil.getPluginPath().toString(), fileName);
        if (!Files.exists(jarPath)) {
            throw new PluginException(fileName + " does not exist.");
        }

        switch (artifact.getType()) {
        case THEME:
            ComponentUtil.getThemeHelper().uninstall(artifact);
            uninstall(fileName, jarPath);
            break;
        default:
            uninstall(fileName, jarPath);
            break;
        }

    }

    /**
     * Uninstalls an artifact by deleting its JAR file.
     *
     * @param fileName the name of the file to delete
     * @param jarPath the path to the JAR file
     * @throws PluginException if deletion fails
     */
    protected void uninstall(final String fileName, final Path jarPath) {
        try {
            Files.delete(jarPath);
        } catch (final IOException e) {
            throw new PluginException("Failed to delete the artifact " + fileName, e);
        }
    }

    /**
     * Gets an artifact by name and version from available artifacts.
     *
     * @param name the name of the artifact
     * @param version the version of the artifact
     * @return the artifact if found, null otherwise
     */
    public Artifact getArtifact(final String name, final String version) {
        if (StringUtil.isBlank(name) || StringUtil.isBlank(version)) {
            return null;
        }
        for (final Artifact artifact : getAvailableArtifacts(ArtifactType.getType(name))) {
            if (name.equals(artifact.getName()) && version.equals(artifact.getVersion())) {
                return artifact;
            }
        }
        return null;
    }

    /**
     * Represents a plugin artifact with name, version, and URL information.
     */
    public static class Artifact {
        /** The name of the artifact */
        protected final String name;
        /** The version of the artifact */
        protected final String version;
        /** The URL where the artifact can be downloaded */
        protected final String url;

        /**
         * Creates a new artifact with name, version, and URL.
         *
         * @param name the name of the artifact
         * @param version the version of the artifact
         * @param url the URL where the artifact can be downloaded
         */
        public Artifact(final String name, final String version, final String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        /**
         * Creates a new artifact with name and version, but no URL.
         *
         * @param name the name of the artifact
         * @param version the version of the artifact
         */
        public Artifact(final String name, final String version) {
            this(name, version, null);
        }

        /**
         * Gets the name of the artifact.
         *
         * @return the artifact name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the version of the artifact.
         *
         * @return the artifact version
         */
        public String getVersion() {
            return version;
        }

        /**
         * Gets the filename of the artifact JAR file.
         *
         * @return the filename in the format "name-version.jar"
         */
        public String getFileName() {
            return name + "-" + version + ".jar";
        }

        /**
         * Gets the URL where the artifact can be downloaded.
         *
         * @return the artifact URL
         */
        public String getUrl() {
            return url;
        }

        /**
         * Gets the type of the artifact based on its name.
         *
         * @return the artifact type
         */
        public ArtifactType getType() {
            return ArtifactType.getType(name);
        }

        /**
         * Returns a string representation of the artifact.
         *
         * @return a string in the format "name:version"
         */
        @Override
        public String toString() {
            return name + ":" + version;
        }
    }

    /**
     * Enumeration of different artifact types supported by Fess.
     * Each type has a specific ID prefix used to identify artifacts of that type.
     */
    public enum ArtifactType {
        /** Data store plugins */
        DATA_STORE("fess-ds"), //
        /** Theme plugins */
        THEME("fess-theme"), //
        /** Ingest processor plugins */
        INGEST("fess-ingest"), //
        /** Script plugins */
        SCRIPT("fess-script"), //
        /** Web application plugins */
        WEBAPP("fess-webapp"), //
        /** Thumbnail generator plugins */
        THUMBNAIL("fess-thumbnail"), //
        /** Crawler plugins */
        CRAWLER("fess-crawler"), //
        /** Unknown/generic JAR files */
        UNKNOWN("jar");

        /** The ID prefix for this artifact type */
        private final String id;

        /**
         * Creates a new artifact type with the specified ID.
         *
         * @param id the ID prefix for this artifact type
         */
        ArtifactType(final String id) {
            this.id = id;
        }

        /**
         * Gets the ID prefix for this artifact type.
         *
         * @return the ID prefix
         */
        public String getId() {
            return id;
        }

        /**
         * Determines the artifact type based on the artifact name.
         *
         * @param name the name of the artifact
         * @return the corresponding artifact type, or UNKNOWN if no match is found
         */
        public static ArtifactType getType(final String name) {
            if (name.startsWith(DATA_STORE.getId())) {
                return DATA_STORE;
            }
            if (name.startsWith(THEME.getId())) {
                return THEME;
            }
            if (name.startsWith(INGEST.getId())) {
                return INGEST;
            }
            if (name.startsWith(SCRIPT.getId())) {
                return SCRIPT;
            }
            if (name.startsWith(WEBAPP.getId())) {
                return WEBAPP;
            }
            if (name.startsWith(THUMBNAIL.getId())) {
                return THUMBNAIL;
            }
            if (name.startsWith(CRAWLER.getId())) {
                return CRAWLER;
            }
            return UNKNOWN;
        }
    }

}
