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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.exception.IORuntimeException;

public class PluginHelper {

    public Artifact[] getArtifacts(PluginType pluginType) {
        List<Artifact> list = new ArrayList<>();
        for (String url : getRepositories()) {
            list.addAll(processRepository(pluginType, url));
        }
        return list.toArray(new Artifact[list.size()]);
    }

    protected String[] getRepositories() {
        return split(ComponentUtil.getFessConfig().getPluginRepositories(), ",").get(
                stream -> stream.map(s -> s.trim()).toArray(n -> new String[n]));
    }

    protected List<Artifact> processRepository(PluginType pluginType, String url) {
        List<Artifact> list = new ArrayList<>();
        String repoContent = getRepositoryContent(url);
        Matcher matcher = Pattern.compile("href=\"[^\"]*(" + pluginType.getId() + "[a-zA-Z0-9\\-]+)/?\"").matcher(repoContent);
        while (matcher.find()) {
            String name = matcher.group(1);
            // TODO parse maven-metadata.xml from url/name to set version and url
            list.add(new Artifact(name, "TODO", "TODO"));
        }
        return list;
    }

    protected String getRepositoryContent(String url) {
        try (CurlResponse response = Curl.get(url).execute()) {
            return response.getContentAsString();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
