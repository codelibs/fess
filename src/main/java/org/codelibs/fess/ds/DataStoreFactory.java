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
package org.codelibs.fess.ds;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.util.ResourceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataStoreFactory {
    private static final Logger logger = LogManager.getLogger(DataStoreFactory.class);

    protected Map<String, DataStore> dataStoreMap = new LinkedHashMap<>();

    protected String[] dataStoreNames = StringUtil.EMPTY_STRINGS;

    protected long lastLoadedTime = 0;

    public void add(final String name, final DataStore dataStore) {
        if (name == null || dataStore == null) {
            throw new IllegalArgumentException("name or dataStore is null.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", name);
        }
        dataStoreMap.put(name.toLowerCase(Locale.ROOT), dataStore);
        dataStoreMap.put(dataStore.getClass().getSimpleName().toLowerCase(Locale.ROOT), dataStore);
    }

    public DataStore getDataStore(final String name) {
        if (name == null) {
            return null;
        }
        return dataStoreMap.get(name.toLowerCase(Locale.ROOT));
    }

    public String[] getDataStoreNames() {
        final long now = System.currentTimeMillis();
        if (now - lastLoadedTime > 60000L) {
            final List<String> nameList = loadDataStoreNameList();
            dataStoreNames = nameList.toArray(n -> new String[nameList.size()]);
            lastLoadedTime = now;
        }
        return dataStoreNames;
    }

    protected List<String> loadDataStoreNameList() {
        final Set<String> nameSet = new HashSet<>();
        final File[] jarFiles = ResourceUtil.getPluginJarFiles(PluginHelper.ArtifactType.DATA_STORE.getId());
        for (final File jarFile : jarFiles) {
            try (FileSystem fs = FileSystems.newFileSystem(jarFile.toPath(), ClassLoader.getSystemClassLoader())) {
                final Path xmlPath = fs.getPath("fess_ds++.xml");
                try (InputStream is = Files.newInputStream(xmlPath)) {
                    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_SECURE_PROCESSING, true);
                    factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
                    factory.setFeature(org.codelibs.fess.crawler.Constants.FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
                    factory.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
                    final DocumentBuilder builder = factory.newDocumentBuilder();

                    final Document doc = builder.parse(is);
                    final NodeList nodeList = doc.getElementsByTagName("component");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        final NamedNodeMap attributes = node.getAttributes();
                        if (attributes != null) {
                            final Node classAttr = attributes.getNamedItem("class");
                            if (classAttr != null) {
                                final String value = classAttr.getNodeValue();
                                if (StringUtil.isNotBlank(value)) {
                                    final String[] values = value.split("\\.");
                                    nameSet.add(values[values.length - 1]);
                                }
                            }
                        }
                    }
                }
            } catch (final Exception e) {
                logger.warn("Failed to load {}", jarFile.getAbsolutePath(), e);
            }
        }
        return nameSet.stream().sorted().collect(Collectors.toList());
    }

}
