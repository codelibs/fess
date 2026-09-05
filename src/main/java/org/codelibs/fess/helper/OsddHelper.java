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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.OriginUtil;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaServletContextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

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

    /**
     * The placeholder in the OSDD file that is replaced with the URL this Fess
     * instance is being served from, as {@code scheme://host[:port][/contextPath]}.
     */
    protected static final String CONTEXT_URL_PLACEHOLDER = "${fess.context.url}";

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
            logger.debug("Initializing {}", this.getClass().getSimpleName());
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
            logger.warn("OSDD file path could not be resolved: {}", osddPath);
            return null;
        }
        final File osddFile = new File(path);
        if (!osddFile.isFile()) {
            logger.warn("OSDD path is not a file: {}", path);
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
     * Returns the OSDD as a stream response, with {@link #CONTEXT_URL_PLACEHOLDER}
     * resolved against the current request.
     *
     * @return the stream response
     */
    public StreamResponse asStream() {
        if (osddFile == null) {
            throw ComponentUtil.getResponseManager().new404("Unsupported Open Search Description Document response.");
        }

        final Charset charset = Charset.forName(StringUtil.isBlank(encoding) ? Constants.UTF_8 : encoding);
        final byte[] content =
                FileUtil.readText(osddFile, charset.name()).replace(CONTEXT_URL_PLACEHOLDER, getContextUrl()).getBytes(charset);
        return new StreamResponse(osddFile.getName()).contentType(contentType + "; charset=" + encoding)
                .stream(out -> out.write(new ByteArrayInputStream(content)));
    }

    /**
     * Returns the URL this Fess instance is being served from, as
     * {@code scheme://host[:port][/contextPath]}. The origin is taken from the
     * current request and canonicalized by {@link OriginUtil}, so the default port
     * for the scheme is omitted. Returns an empty string when no request is in
     * scope or the request carries no usable origin; the document then falls back
     * to context-relative URLs.
     *
     * @return the context URL, or an empty string when it cannot be resolved
     */
    protected String getContextUrl() {
        return LaRequestUtil.getOptionalRequest().map(this::buildContextUrl).orElse(StringUtil.EMPTY);
    }

    /**
     * Builds the context URL for the given request.
     *
     * @param request the current request
     * @return the context URL, or null when the request carries no usable origin
     */
    protected String buildContextUrl(final HttpServletRequest request) {
        final String origin = OriginUtil.canonicalize(request.getRequestURL().toString());
        if (origin == null) {
            logger.warn("Could not resolve the origin of {}. Open Search Description Document uses relative URLs.",
                    request.getRequestURL());
            return null;
        }
        final String contextPath = request.getContextPath();
        if (StringUtil.isBlank(contextPath) || "/".equals(contextPath)) {
            return origin;
        }
        return origin + contextPath;
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
