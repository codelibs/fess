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

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

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
        if (logger.isDebugEnabled()) {
            logger.debug("web protocols: {}", Arrays.toString(webProtocols));
            logger.debug("file protocols: {}", Arrays.toString(fileProtocols));
        }
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
