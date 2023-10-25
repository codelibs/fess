/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class PluginHelper {
    private static final Logger logger = LogManager.getLogger(PluginHelper.class);

    protected LoadingCache<ArtifactType, Artifact[]> availableArtifacts = CacheBuilder.newBuilder().maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<ArtifactType, Artifact[]>() {
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

    public Artifact[] getAvailableArtifacts(final ArtifactType artifactType) {
        try {
            return availableArtifacts.get(artifactType);
        } catch (final Exception e) {
            throw new PluginException("Failed to access " + artifactType, e);
        }
    }

    protected String[] getRepositories() {
        return split(ComponentUtil.getFessConfig().getPluginRepositories(), ",")
                .get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
    }

    protected List<Artifact> loadArtifactsFromRepository(final String url) {
        final String content = getRepositoryContent(url);
        final ObjectMapper objectMapper = new YAMLMapper();
        try {
            @SuppressWarnings("unchecked")
            final List<Map<?, ?>> result = objectMapper.readValue(content, List.class);
            if (result != null) {
                return result.stream().map(o -> new Artifact((String) o.get("name"), (String) o.get("version"), (String) o.get("url")))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (final Exception e) {
            throw new PluginException("Failed to access " + url, e);
        }
    }

    protected List<Artifact> processRepository(final ArtifactType artifactType, final String url) {
        final List<Artifact> list = new ArrayList<>();
        final String repoContent = getRepositoryContent(url);
        final Matcher matcher = Pattern.compile("href=\"[^\"]*(" + artifactType.getId() + "[a-zA-Z0-9\\-]+)/?\"").matcher(repoContent);
        while (matcher.find()) {
            final String name = matcher.group(1);
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
                                    logger.debug("Snapshot name is not found: {}/{}", name, version);
                                }
                            } else {
                                list.add(new Artifact(name, version, pluginUrl + version + "/" + name + "-" + version + ".jar"));
                            }
                        } else if (logger.isDebugEnabled()) {
                            logger.debug("{}:{} is ignored.", name, version);
                        }
                    }
                }
            } catch (final Exception e) {
                logger.warn("Failed to parse {}maven-metadata.xml.", pluginUrl, e);
            }
        }
        return list;
    }

    protected boolean isTargetPluginVersion(final String version) {
        return ComponentUtil.getFessConfig().isTargetPluginVersion(version);
    }

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

    protected String getRepositoryContent(final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading {}", url);
        }
        try (final CurlResponse response = createCurlRequest(url).execute()) {
            return response.getContentAsString();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public Artifact[] getInstalledArtifacts(final ArtifactType artifactType) {
        if (artifactType == ArtifactType.UNKNOWN) {
            final File[] jarFiles = ResourceUtil.getPluginJarFiles((d, n) -> {
                for (final ArtifactType type : ArtifactType.values()) {
                    if (n.startsWith(type.getId())) {
                        return false;
                    }
                }
                return true;
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

    protected Artifact getArtifactFromFileName(final ArtifactType artifactType, final String filename) {
        return getArtifactFromFileName(artifactType, filename, null);
    }

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

    protected CurlRequest createCurlRequest(final String url) {
        final CurlRequest request = Curl.get(url);
        final Proxy proxy = ComponentUtil.getFessConfig().getHttpProxy();
        if (proxy != null && !Proxy.NO_PROXY.equals(proxy)) {
            request.proxy(proxy);
        }
        return request;
    }

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

    protected void uninstall(final String fileName, final Path jarPath) {
        try {
            Files.delete(jarPath);
        } catch (final IOException e) {
            throw new PluginException("Failed to delete the artifact " + fileName, e);
        }
    }

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

    public static class Artifact {
        protected final String name;
        protected final String version;
        protected final String url;

        public Artifact(final String name, final String version, final String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        public Artifact(final String name, final String version) {
            this(name, version, null);
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getFileName() {
            return name + "-" + version + ".jar";
        }

        public String getUrl() {
            return url;
        }

        public ArtifactType getType() {
            return ArtifactType.getType(name);
        }

        @Override
        public String toString() {
            return name + ":" + version;
        }
    }

    public enum ArtifactType {
        DATA_STORE("fess-ds"), THEME("fess-theme"), INGEST("fess-ingest"), SCRIPT("fess-script"), WEBAPP("fess-webapp"), THUMBNAIL(
                "fess-thumbnail"), UNKNOWN("jar");

        private final String id;

        ArtifactType(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

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
            return UNKNOWN;
        }
    }

}
