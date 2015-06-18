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

package org.codelibs.fess.web.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(LogAction.class);

    @ActionForm
    @Resource
    protected LogForm logForm;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("log");
    }

    @Execute(validator = false)
    public String index() {
        return "index.jsp";
    }

    @Execute(validator = true, input = "index", urlPattern = "download/{logFileName}")
    public String download() {
        final String logFilePath = ComponentUtil.getSystemHelper().getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            final File file = new File(logFilePath);
            final File parentDir = file.getParentFile();
            String fileName;
            try {
                fileName = new String(Base64.decodeBase64(logForm.logFileName.getBytes(Constants.UTF_8)), Constants.UTF_8);
            } catch (final UnsupportedEncodingException e1) {
                fileName =
                        new String(Base64.decodeBase64(logForm.logFileName.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
            }
            final File logFile = new File(parentDir, fileName);
            if (logFile.isFile()) {
                try {
                    ResponseUtil.download(fileName, new FileInputStream(logFile));
                    return null;
                } catch (final FileNotFoundException e) {
                    logger.warn("Could not find " + logFile.getAbsolutePath(), e);
                }
            }
        }
        throw new SSCActionMessagesException("errors.could_not_find_log_file", new Object[] { logForm.logFileName });
    }

    public List<Map<String, Object>> getLogFileItems() {
        final List<Map<String, Object>> logFileItems = new ArrayList<Map<String, Object>>();
        final String logFilePath = ComponentUtil.getSystemHelper().getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            try {
                final File file = new File(logFilePath);
                final File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    logger.warn("Log directory does not exist: " + parentDir.getAbsolutePath());
                    return logFileItems;
                }
                final File[] files = parentDir.listFiles((FilenameFilter) (dir, name) -> {
                    if (name.indexOf(".out") > 0) {
                        return true;
                    }
                    return false;
                });
                if (files == null) {
                    return logFileItems;
                }
                Arrays.sort(files, (o1, o2) -> {
                    if (o1.lastModified() < o2.lastModified()) {
                        return 1;
                    } else {
                        return -1;
                    }
                });
                for (final File logFile : files) {
                    logFileItems.add(createLogFileItem(logFile));
                }
            } catch (final Exception e) {
                logger.warn("Could not find log files.", e);
            }
        }
        return logFileItems;
    }

    protected Map<String, Object> createLogFileItem(final File file) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", file.getName());
        try {
            map.put("logFileName", new String(Base64.encodeBase64(file.getName().getBytes(Constants.UTF_8)), "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            map.put("logFileName",
                    new String(Base64.encodeBase64(file.getName().getBytes(Charset.defaultCharset())), Charset.defaultCharset()));
        }
        map.put("lastModified", new Date(file.lastModified()));
        return map;
    }
}