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
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Factory class responsible for managing and providing access to data store instances.
 * This factory maintains a registry of data store implementations and provides methods
 * to register, retrieve, and discover available data stores.
 *
 * <p>Data stores are registered by name and class name, allowing flexible lookup.
 * The factory also supports dynamic discovery of data store plugins by scanning
 * JAR files for data store configurations.</p>
 *
 * <p>Thread-safe operations are supported for registration and retrieval of data stores.
 * The factory caches data store names with a time-based refresh mechanism to improve
 * performance while ensuring up-to-date plugin discovery.</p>
 */
public class DataStoreFactory {
    /** Logger instance for this factory class. */
    private static final Logger logger = LogManager.getLogger(DataStoreFactory.class);

    /**
     * Map containing registered data store instances indexed by their names and class simple names.
     * All keys are stored in lowercase for case-insensitive lookup.
     */
    protected Map<String, DataStore> dataStoreMap = new LinkedHashMap<>();

    /**
     * Cached array of available data store names discovered from plugin JAR files.
     * This cache is refreshed periodically based on the lastLoadedTime.
     */
    protected volatile String[] dataStoreNames = StringUtil.EMPTY_STRINGS;

    /**
     * Timestamp of the last time data store names were loaded from plugin files.
     * Used to implement a time-based cache refresh mechanism.
     * Volatile to ensure visibility across threads.
     */
    protected volatile long lastLoadedTime = 0;

    /**
     * Creates a new instance of DataStoreFactory.
     * This constructor initializes the factory for managing data store instances
     * and provides methods for registration, retrieval, and plugin discovery.
     */
    public DataStoreFactory() {
        // Default constructor with explicit documentation
    }

    /**
     * Registers a data store instance with the factory using the specified name.
     * The data store will be accessible by both the provided name and its class simple name,
     * both converted to lowercase for case-insensitive lookup.
     *
     * @param name the name to register the data store under, must not be null
     * @param dataStore the data store instance to register, must not be null
     * @throws IllegalArgumentException if either name or dataStore is null
     */
    public void add(final String name, final DataStore dataStore) {
        if (name == null || dataStore == null) {
            throw new IllegalArgumentException(
                    "Both name and dataStore parameters are required. name: " + name + ", dataStore: " + dataStore);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded DataStore: name={}", name);
        }
        dataStoreMap.put(name.toLowerCase(Locale.ROOT), dataStore);
        dataStoreMap.put(dataStore.getClass().getSimpleName().toLowerCase(Locale.ROOT), dataStore);
    }

    /**
     * Retrieves a data store instance by name.
     * The lookup is case-insensitive and will match both registered names
     * and class simple names.
     *
     * @param name the name of the data store to retrieve, may be null
     * @return the data store instance if found, null if not found or name is null
     */
    public DataStore getDataStore(final String name) {
        if (name == null) {
            return null;
        }
        return dataStoreMap.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns an array of available data store names discovered from plugin JAR files.
     * This method implements a time-based caching mechanism that refreshes the list
     * every 60 seconds to balance performance with up-to-date plugin discovery.
     *
     * @return array of data store names sorted alphabetically, never null
     */
    public synchronized String[] getDataStoreNames() {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        if (now - lastLoadedTime > 60000L) {
            final List<String> nameList = loadDataStoreNameList();
            dataStoreNames = nameList.toArray(n -> new String[nameList.size()]);
            lastLoadedTime = now;
        }
        return dataStoreNames;
    }

    /**
     * Loads the list of available data store names by scanning plugin JAR files.
     * This method searches for 'fess_ds++.xml' configuration files within JAR files
     * in the data store plugin directory and extracts component class names.
     *
     * <p>The method uses secure XML parsing features to prevent XXE attacks and
     * other XML-based vulnerabilities. Component class names are extracted from
     * the 'class' attribute of 'component' elements in the XML files.</p>
     *
     * @return sorted list of data store class simple names discovered from plugins
     */
    protected List<String> loadDataStoreNameList() {
        final Set<String> nameSet = new HashSet<>();
        final File[] jarFiles = ResourceUtil.getPluginJarFiles(PluginHelper.ArtifactType.DATA_STORE.getId());
        if (jarFiles == null) {
            return nameSet.stream().sorted().collect(Collectors.toList());
        }
        for (final File jarFile : jarFiles) {
            try (FileSystem fs = FileSystems.newFileSystem(jarFile.toPath(), ClassLoader.getSystemClassLoader())) {
                final Path xmlPath = fs.getPath("fess_ds++.xml");
                if (!Files.exists(xmlPath)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Configuration file (fess_ds++.xml) not found: path={}", jarFile.getAbsolutePath());
                    }
                    continue;
                }
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
                logger.warn("Failed to load DataStore plugin: path={}", jarFile.getAbsolutePath(), e);
            }
        }
        return nameSet.stream().sorted().collect(Collectors.toList());
    }

}
