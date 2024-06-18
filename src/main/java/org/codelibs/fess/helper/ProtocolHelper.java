/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.ClassNotFoundRuntimeException;
import org.codelibs.core.exception.NoSuchFieldRuntimeException;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

public class ProtocolHelper {
    private static final Logger logger = LogManager.getLogger(ProtocolHelper.class);

    protected String[] webProtocols = StringUtil.EMPTY_STRINGS;

    protected String[] fileProtocols = StringUtil.EMPTY_STRINGS;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        webProtocols = split(fessConfig.getCrawlerWebProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));
        fileProtocols = split(fessConfig.getCrawlerFileProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));

        loadProtocols("org.codelibs.fess.net.protocol");

        if (logger.isDebugEnabled()) {
            logger.debug("web protocols: {}", Arrays.toString(webProtocols));
            logger.debug("file protocols: {}", Arrays.toString(fileProtocols));
        }
    }

    protected void loadProtocols(final String basePackage) {
        final List<String> subPackages = new ArrayList<>();
        final String path = basePackage.replace('.', '/');
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                final File directory = new File(resource.getFile());
                if (directory.exists() && directory.isDirectory()) {
                    final File[] files = directory.listFiles(File::isDirectory);
                    if (files != null) {
                        for (final File file : files) {
                            subPackages.add(file.getName());
                        }
                    }
                }
            }
        } catch (final IOException e) {
            logger.warn("Cannot load subpackages from {}", basePackage, e);
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
                        logger.warn("Unknown protocol: {}", protocol);
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

    public String[] getWebProtocols() {
        return webProtocols;
    }

    public String[] getFileProtocols() {
        return fileProtocols;
    }

    public boolean isValidWebProtocol(final String url) {
        return stream(webProtocols).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    public boolean isValidFileProtocol(final String url) {
        return stream(fileProtocols).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    public void addWebProtocol(final String protocol) {
        final String prefix = protocol + ":";
        if (stream(webProtocols).get(stream -> stream.anyMatch(s -> s.equals(prefix)))) {
            logger.debug("web protocols contains {}.", protocol);
            return;
        }
        webProtocols = Arrays.copyOf(webProtocols, webProtocols.length + 1);
        webProtocols[webProtocols.length - 1] = prefix;
    }

    public void addFileProtocol(final String protocol) {
        final String prefix = protocol + ":";
        if (stream(fileProtocols).get(stream -> stream.anyMatch(s -> s.equals(prefix)))) {
            logger.debug("file protocols contains {}.", protocol);
            return;
        }
        fileProtocols = Arrays.copyOf(fileProtocols, fileProtocols.length + 1);
        fileProtocols[fileProtocols.length - 1] = prefix;
    }
}
