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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for Open Search Description Document.
 */
public class OsddHelper {

    /**
     * Default constructor.
     */
    public OsddHelper() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(OsddHelper.class);

    /** The OSDD file path. */
    protected String osddPath;

    /** The encoding for OSDD file. */
    protected String encoding = Constants.UTF_8;

    /** The content type for OSDD response. */
    protected String contentType = "text/xml"; // "application/opensearchdescription+xml"

    /** The OSDD file. */
    protected File osddFile;

    /**
     * Initializes the OSDD helper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        osddFile = getOsddFile();
    }

    /**
     * Gets the OSDD file.
     *
     * @return the OSDD file
     */
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

    /**
     * Checks if OSDD link is enabled.
     *
     * @return true if OSDD link is enabled
     */
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

    /**
     * Checks if the OpenSearch file exists.
     *
     * @return true if the OpenSearch file exists
     */
    public boolean hasOpenSearchFile() {
        return osddFile != null;
    }

    /**
     * Returns the OSDD as a stream response.
     *
     * @return the stream response
     */
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

    /**
     * Sets the OSDD path.
     *
     * @param osddPath the OSDD path
     */
    public void setOsddPath(final String osddPath) {
        this.osddPath = osddPath;
    }

    /**
     * Sets the encoding.
     *
     * @param encoding the encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the content type
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
}
