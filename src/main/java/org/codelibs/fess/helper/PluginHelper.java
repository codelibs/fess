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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.lastaflute.di.exception.IORuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;

// TODO: refactoring, exception handling, improving codes
public class PluginHelper {
    private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

    protected static final String VERSION = "version";

    public Artifact[] getArtifacts(final ArtifactType artifactType) {
        final List<Artifact> list = new ArrayList<>();
        for (final String url : getRepositories()) {
            list.addAll(processRepository(artifactType, url));
        }
        return list.toArray(new Artifact[list.size()]);
    }

    protected String[] getRepositories() {
        return split(ComponentUtil.getFessConfig().getPluginRepositories(), ",").get(
                stream -> stream.map(s -> s.trim()).toArray(n -> new String[n]));
    }

    protected List<Artifact> processRepository(final ArtifactType artifactType, final String url) {
        final List<Artifact> list = new ArrayList<>();
        final String repoContent = getRepositoryContentAsString(url);
        final Matcher matcher = Pattern.compile("href=\"[^\"]*(" + artifactType.getId() + "[a-zA-Z0-9\\-]+)/?\"").matcher(repoContent);
        while (matcher.find()) {
            final String name = matcher.group(1);
            final String pluginUrl = url + (url.endsWith("/") ? name + "/" : "/" + name + "/");
            final String mavenMetadata = getRepositoryContentAsString(pluginUrl + "maven-metadata.xml");
            try (final StringReader reader = new StringReader(mavenMetadata)) {
                final DOMParser parser = new DOMParser();
                parser.parse(new InputSource(reader));
                final Document document = parser.getDocument();
                final NodeList nodeList = document.getElementsByTagName(VERSION);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final String version = nodeList.item(i).getTextContent();
                    list.add(new Artifact(name, version, pluginUrl + version + "/" + name + "-" + version + ".jar"));
                }
            } catch (final Exception e) {
                logger.warn("Failed to parse " + pluginUrl + "maven-metadata.xml.", e);
            }
        }
        return list;
    }

    protected String getRepositoryContentAsString(final String url) {
        try (final CurlResponse response = Curl.get(url).execute()) {
            return response.getContentAsString();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected InputStream getRepositoryContentAsStream(final String url) {
        try (final CurlResponse response = Curl.get(url).execute()) {
            return response.getContentAsStream();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public Artifact[] getInstalledArtifacts(final ArtifactType artifactType) {
        final File[] jarFiles = ResourceUtil.getJarFiles(artifactType.getId());
        final List<Artifact> list = new ArrayList<>(jarFiles.length);
        for (final File file : jarFiles) {
            list.add(getArtifactFromFileName(artifactType, file.getName()));
        }
        return list.toArray(new Artifact[list.size()]);
    }

    protected Artifact getArtifactFromFileName(final ArtifactType artifactType, final String fileName) {
        final String convertedFileName = fileName.substring(artifactType.getId().length() + 1, fileName.lastIndexOf('.'));
        final int firstIndexOfDash = convertedFileName.indexOf("-");
        final String artifactName = artifactType.getId() + "-" + convertedFileName.substring(0, firstIndexOfDash);
        final String artifactVersion = convertedFileName.substring(firstIndexOfDash + 1);
        return new Artifact(artifactName, artifactVersion, null);
    }

    public boolean installArtifact(Artifact artifact) {
        try (final InputStream in = getRepositoryContentAsStream(artifact.getUrl())) {
            final String fileName = artifact.getName() + "-" + artifact.getVersion() + ".jar";
            final File file = Paths.get(getLibPath().toString(), fileName).toFile();
            if (!file.exists()) {
                file.createNewFile();
                CopyUtil.copy(in, file);
            } else {
                final File tempFile = File.createTempFile(fileName, "bk");
                CopyUtil.copy(file, tempFile);
                CopyUtil.copy(in, file);
                tempFile.delete();
            }
            return file.exists();
        } catch (final Exception e) {
            logger.warn("Failed to install the artifact " + artifact.getName(), e);
            return false;
        }
    }

    public boolean deleteInstalledArtifact(Artifact artifact) {
        final String fileName = artifact.getName() + "-" + artifact.getVersion() + ".jar";
        final File file = Paths.get(getLibPath().toString(), fileName).toFile();
        if (file != null && file.exists() && file.delete()) {
            return true;
        } else {
            logger.warn("Failed to delete the artifact " + file.getAbsolutePath());
            return false;
        }
    }

    protected Path getLibPath() {
        return Paths.get(ComponentUtil.getComponent(ServletContext.class).getRealPath("/WEB-INF/lib/"));
    }

    public static class Artifact {
        protected final String name;
        protected final String version;
        protected final String url;

        public Artifact(String name, String version, String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getUrl() {
            return url;
        }
    }

    public enum ArtifactType {
        DATA_STORE("fess-ds"), UNKNOWN("unknown");

        private String id;

        private ArtifactType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public ArtifactType getType(String name) {
            if (name.startsWith(DATA_STORE.getId())) {
                return DATA_STORE;
            }
            return UNKNOWN;
        }
    }
}
