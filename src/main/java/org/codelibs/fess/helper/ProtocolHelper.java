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
import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.ClassNotFoundRuntimeException;
import org.codelibs.core.exception.NoSuchFieldRuntimeException;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing and validating URL protocols in Fess crawling system.
 * This class handles the initialization and validation of web and file protocols
 * used by the crawler to determine which URLs can be crawled.
 */
public class ProtocolHelper {
    private static final Logger logger = LogManager.getLogger(ProtocolHelper.class);

    /** Array of supported web protocols with colon suffix (e.g., "http:", "https:") */
    protected String[] webProtocols = StringUtil.EMPTY_STRINGS;

    /** Array of supported file protocols with colon suffix (e.g., "file:", "ftp:") */
    protected String[] fileProtocols = StringUtil.EMPTY_STRINGS;

    /**
     * Default constructor for ProtocolHelper.
     * Initializes the helper with empty protocol arrays that will be populated during init().
     */
    public ProtocolHelper() {
        // Default constructor
    }

    /**
     * Initializes the protocol helper by loading configured protocols from FessConfig
     * and scanning for available protocol handlers in the classpath.
     * This method is called automatically after bean construction.
     */
    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        webProtocols = split(fessConfig.getCrawlerWebProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));
        fileProtocols = split(fessConfig.getCrawlerFileProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));

        loadProtocols("org.codelibs.fess.net.protocol");

        if (logger.isDebugEnabled()) {
            logger.debug("Web protocols: protocols={}", Arrays.toString(webProtocols));
            logger.debug("File protocols: protocols={}", Arrays.toString(fileProtocols));
        }
    }

    /**
     * Loads protocol handlers from the specified base package by scanning for
     * Handler classes in subpackages and registering them as web or file protocols
     * based on their PROTOCOL_TYPE field.
     *
     * @param basePackage the base package to scan for protocol handlers
     */
    protected void loadProtocols(final String basePackage) {
        final List<String> subPackages = new ArrayList<>();
        final String path = basePackage.replace('.', '/');
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                logger.debug("Loading resource: url={}", resource);
                if ("file".equals(resource.getProtocol())) {
                    final File directory = new File(resource.getFile());
                    if (directory.exists() && directory.isDirectory()) {
                        final File[] files = directory.listFiles(File::isDirectory);
                        if (files != null) {
                            for (final File file : files) {
                                final String name = file.getName();
                                subPackages.add(name);
                                logger.debug("Found subpackage: name={}, resource={}", name, resource);
                            }
                        }
                    }
                } else if ("jar".equals(resource.getProtocol())) {
                    final JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                    try (JarFile jarFile = jarURLConnection.getJarFile()) {
                        final Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            final JarEntry entry = entries.nextElement();
                            final String entryName = entry.getName();
                            if (entryName.endsWith("/") && entryName.startsWith(path) && entryName.length() > path.length() + 1) {
                                final String name = entryName.substring(path.length() + 1, entryName.length() - 1);
                                if (name.indexOf('/') == -1) {
                                    subPackages.add(name);
                                    logger.debug("Found subpackage: name={}, resource={}", name, resource);
                                }
                            }
                        }
                    }
                }
            }
        } catch (final IOException e) {
            logger.warn("Cannot load subpackages: basePackage={}", basePackage, e);
        }

        subPackages.stream().forEach(protocol -> {
            try {
                final Class<Object> handlerClazz = ClassUtil.forName(basePackage + "." + protocol + ".Handler");
                final Field protocolTypeField = ClassUtil.getDeclaredField(handlerClazz, "PROTOCOL_TYPE");
                if (protocolTypeField.get(null) instanceof final String protocolType) {
                    if ("web".equalsIgnoreCase(protocolType)) {
                        addWebProtocol(protocol);
                    } else if ("file".equalsIgnoreCase(protocolType)) {
                        addFileProtocol(protocol);
                    } else {
                        logger.warn("Unknown protocol: protocol={}", protocol);
                    }
                }
            } catch (final ClassNotFoundRuntimeException e) {
                logger.debug("{}.{}.Handler does not exist.", basePackage, protocol, e);
            } catch (final NoSuchFieldRuntimeException e) {
                logger.debug("{}.{}.Handler does not contain PROTOCOL_TYPE.", basePackage, protocol, e);
            } catch (final Exception e) {
                logger.warn("Cannot load Handler from {}.{}", basePackage, protocol, e);
            }
        });
    }

    /**
     * Returns the array of supported web protocols.
     *
     * @return array of web protocol strings with colon suffix (e.g., "http:", "https:")
     */
    public String[] getWebProtocols() {
        return webProtocols;
    }

    /**
     * Returns the array of supported file protocols.
     *
     * @return array of file protocol strings with colon suffix (e.g., "file:", "ftp:")
     */
    public String[] getFileProtocols() {
        return fileProtocols;
    }

    /**
     * Checks if the given URL uses a valid web protocol.
     *
     * @param url the URL to validate
     * @return true if the URL starts with a supported web protocol, false otherwise
     */
    public boolean isValidWebProtocol(final String url) {
        return stream(webProtocols).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    /**
     * Checks if the given URL uses a valid file protocol.
     *
     * @param url the URL to validate
     * @return true if the URL starts with a supported file protocol, false otherwise
     */
    public boolean isValidFileProtocol(final String url) {
        return stream(fileProtocols).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    /**
     * Adds a new web protocol to the supported protocols list.
     * If the protocol already exists, it will not be added again.
     *
     * @param protocol the protocol name to add (without colon suffix)
     */
    public void addWebProtocol(final String protocol) {
        final String prefix = protocol + ":";
        if (stream(webProtocols).get(stream -> stream.anyMatch(s -> s.equals(prefix)))) {
            logger.debug("Web protocols already contains: protocol={}", protocol);
            return;
        }
        webProtocols = Arrays.copyOf(webProtocols, webProtocols.length + 1);
        webProtocols[webProtocols.length - 1] = prefix;
    }

    /**
     * Adds a new file protocol to the supported protocols list.
     * If the protocol already exists, it will not be added again.
     *
     * @param protocol the protocol name to add (without colon suffix)
     */
    public void addFileProtocol(final String protocol) {
        final String prefix = protocol + ":";
        if (stream(fileProtocols).get(stream -> stream.anyMatch(s -> s.equals(prefix)))) {
            logger.debug("File protocols already contains: protocol={}", protocol);
            return;
        }
        fileProtocols = Arrays.copyOf(fileProtocols, fileProtocols.length + 1);
        fileProtocols[fileProtocols.length - 1] = prefix;
    }

    /**
     * Checks if the given URL is a file path protocol that requires directory and permission handling.
     * Used for incremental crawling directory detection and file permission processing.
     *
     * @param url the URL to check
     * @return true if the URL uses a file path protocol (smb, smb1, file, ftp, s3, gcs)
     */
    public boolean isFilePathProtocol(final String url) {
        return url.startsWith("smb:") || url.startsWith("smb1:") || url.startsWith("file:") || url.startsWith("ftp:")
                || url.startsWith("s3:") || url.startsWith("gcs:");
    }

    /**
     * Checks if the given URL represents a file system path for content serving.
     * Used to determine if special handling is needed for file system URLs.
     *
     * @param url the URL to check
     * @return true if the URL is a file system path (file, smb, smb1, ftp, storage, s3, gcs)
     */
    public boolean isFileSystemPath(final String url) {
        return url.startsWith("file:") || url.startsWith("smb:") || url.startsWith("smb1:") || url.startsWith("ftp:")
                || url.startsWith("storage:") || url.startsWith("s3:") || url.startsWith("gcs:");
    }

    /**
     * Checks if the given URL should skip URL decoding when extracting file names.
     * Some protocols (like SMB, FTP, S3, GCS) should preserve the original URL encoding.
     *
     * @param url the URL to check
     * @return true if URL decoding should be skipped for this protocol
     */
    public boolean shouldSkipUrlDecode(final String url) {
        return url.startsWith("smb:") || url.startsWith("smb1:") || url.startsWith("ftp:") || url.startsWith("s3:")
                || url.startsWith("gcs:");
    }

    /**
     * Checks if the given path has a known protocol prefix that should not be converted.
     * Used to determine if path conversion is needed in the wizard.
     *
     * @param path the path to check
     * @return true if the path has a known protocol prefix
     */
    public boolean hasKnownProtocol(final String path) {
        return path.startsWith("http:") || path.startsWith("https:") || path.startsWith("smb:") || path.startsWith("smb1:")
                || path.startsWith("ftp:") || path.startsWith("storage:") || path.startsWith("s3:") || path.startsWith("gcs:");
    }
}
