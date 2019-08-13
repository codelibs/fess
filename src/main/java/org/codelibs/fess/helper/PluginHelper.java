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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

// TODO: refactoring
public class PluginHelper {
    private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

    protected static final String VERSION = "version";

    public Artifact[] getArtifacts(final PluginType pluginType) {
        final List<Artifact> list = new ArrayList<>();
        for (final String url : getRepositories()) {
            list.addAll(processRepository(pluginType, url, ""));
        }
        return list.toArray(new Artifact[list.size()]);
    }

    protected String[] getRepositories() {
        return split(ComponentUtil.getFessConfig().getPluginRepositories(), ",").get(
                stream -> stream.map(s -> s.trim()).toArray(n -> new String[n]));
    }

    protected List<Artifact> processRepository(final PluginType pluginType, final String url, final String index) {
        final List<Artifact> list = new ArrayList<>();
        final String repoContent = getRepositoryContent(url + "/" + index);
        final Matcher matcher = Pattern.compile("href=\"[^\"]*(" + pluginType.getId() + "[a-zA-Z0-9\\-]+)/?\"").matcher(repoContent);
        while (matcher.find()) {
            final String name = matcher.group(1);
            try {
                final String pluginUrl = url + "/" + name + "/";
                final String mavenMetadata = getRepositoryContent(pluginUrl + "maven-metadata.xml");
                final DOMParser parser = new DOMParser();
                parser.parse(new InputSource(new StringReader(mavenMetadata)));
                final Document document = parser.getDocument();
                final NodeList nodeList = document.getElementsByTagName(VERSION);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final String version = nodeList.item(i).getTextContent();
                    list.add(new Artifact(name, version, pluginUrl + version + "/" + name + "-" + version + ".jar"));
                }
            } catch (final Exception e) {
                logger.warn("Failed to parse maven-metadata.xml.", e);
            }
        }
        return list;
    }

    protected String getRepositoryContent(final String url) {
        try (final CurlResponse response = Curl.get(url).execute()) {
            return response.getContentAsString();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected Artifact[] getArtifactsAlreadyInstalled(final PluginType pluginType) {
        final List<Artifact> list = new ArrayList<>();
        File[] jarFiles = ResourceUtil.getJarFiles(pluginType.getId());
        for (final File file : jarFiles) {
            list.add(getArtifactFromFileName(pluginType, file.getName()));
        }
        return list.toArray(new Artifact[list.size()]);
    }

    protected Artifact getArtifactFromFileName(final PluginType pluginType, final String fileName) {
        final String convertedFileName = fileName.substring(pluginType.getId().length() + 1, fileName.lastIndexOf('.'));
        final int firstIndexOfDash = convertedFileName.indexOf("-");
        final String artifactName = pluginType.getId() + "-" + convertedFileName.substring(0, firstIndexOfDash);
        final String artifactVersion = convertedFileName.substring(firstIndexOfDash + 1);
        return new Artifact(artifactName, artifactVersion, "NONE");
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

    public enum PluginType {
        DATA_STORE("fess-ds");

        private String id;

        private PluginType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
