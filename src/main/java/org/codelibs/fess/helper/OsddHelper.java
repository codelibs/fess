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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaServletContextUtil;

/**
 * Helper class for Open Search Description Document.
 *
 * @author shinsuke
 *
 */
public class OsddHelper {
    private static final Logger logger = LogManager.getLogger(OsddHelper.class);

    protected String osddPath;

    protected String encoding = Constants.UTF_8;

    protected String contentType = "text/xml"; // "application/opensearchdescription+xml"

    protected File osddFile;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        osddFile = getOsddFile();
    }

    protected File getOsddFile() {
        if (!isOsddLinkEnabled()) {
            logger.debug("OSDD is disabled.");
            return null;
        }
        if (StringUtil.isBlank(osddPath)) {
            logger.info("OSDD file is not found.");
            return null;
        }
        final String path = LaServletContextUtil.getServletContext().getRealPath(osddPath);
        if (path == null) {
            logger.warn("{} was not found.", path);
            return null;
        }
        final File osddFile = new File(path);
        if (!osddFile.isFile()) {
            logger.warn("{} was not a file.", path);
            return null;
        }
        return osddFile;
    }

    protected boolean isOsddLinkEnabled() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String osddLinkEnabled = fessConfig.getOsddLinkEnabled();
        if (Constants.TRUE.equalsIgnoreCase(osddLinkEnabled)) {
            return true;
        }

        if (!Constants.AUTO.equalsIgnoreCase(osddLinkEnabled)) {
            return false;
        }

        final String ssoType = fessConfig.getSsoType();
        return StringUtil.isBlank(ssoType) || Constants.NONE.equalsIgnoreCase(ssoType);
    }

    public boolean hasOpenSearchFile() {
        return osddFile != null;
    }

    public StreamResponse asStream() {
        if (osddFile == null) {
            throw ComponentUtil.getResponseManager().new404("Unsupported Open Search Description Document response.");
        }

        return new StreamResponse(osddFile.getName()).contentType(contentType + "; charset=" + encoding).stream(out -> {
            try (InputStream ins = new FileInputStream(osddFile)) {
                out.write(ins);
            }
        });
    }

    public void setOsddPath(final String osddPath) {
        this.osddPath = osddPath;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
}
