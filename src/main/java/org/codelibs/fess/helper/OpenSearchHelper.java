/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.FileUtil;
import org.seasar.struts.util.ServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for Open Search Description Document.
 *
 * @author shinsuke
 *
 */
public class OpenSearchHelper {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(OpenSearchHelper.class);

    public String osddPath;

    public String encoding = Constants.UTF_8;

    public String contentType = "text/xml"; // "application/opensearchdescription+xml";

    private File osddFile;

    @InitMethod
    public void init() {
        if (StringUtil.isNotBlank(osddPath)) {
            final String path = ServletContextUtil.getServletContext()
                    .getRealPath(osddPath);
            osddFile = new File(path);
            if (!osddFile.isFile()) {
                osddFile = null;
                logger.warn(path + " was not found.");
            }
        } else {
            logger.info("OSDD file is not found.");
        }
    }

    public boolean hasOpenSearchFile() {
        return osddFile != null;
    }

    public void write(final HttpServletResponse response) {
        if (osddFile == null) {
            throw new FessSystemException("Unsupported OpenSearch response.");
        }

        response.setContentType(contentType + "; charset=" + encoding);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(FileUtil.getBytes(osddFile));
        } catch (final IOException e) {
            throw new FessSystemException(
                    "Failed to write OpenSearch response.", e);
        } finally {
            IOUtils.closeQuietly(os);
        }

    }
}
