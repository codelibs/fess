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
package org.codelibs.fess.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
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
        if (Constants.TRUE.equalsIgnoreCase(ComponentUtil.getFessConfig().getOsddLinkEnabled())) {
            if (StringUtil.isNotBlank(osddPath)) {
                final String path = LaServletContextUtil.getServletContext().getRealPath(osddPath);
                osddFile = new File(path);
                if (!osddFile.isFile()) {
                    osddFile = null;
                    logger.warn("{} was not found.", path);
                }
            } else {
                logger.info("OSDD file is not found.");
            }
        } else {
            logger.debug("OSDD is disabled.");
        }
    }

    public boolean hasOpenSearchFile() {
        return osddFile != null;
    }

    public StreamResponse asStream() {
        if (osddFile == null) {
            throw ComponentUtil.getResponseManager().new404("Unsupported OpenSearch response.");
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
