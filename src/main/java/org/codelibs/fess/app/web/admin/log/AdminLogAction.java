/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminLogAction extends FessAdminAction {

    @Resource
    private SystemHelper systemHelper;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("log"));
    }

    @Execute
    public HtmlResponse index(final LogForm form) {
        return asHtml(path_AdminLog_IndexJsp).renderWith(data -> {
            data.register("logFileItems", getLogFileItems());
        });
    }

    //@Execute(validator = true, input = "index", urlPattern = "download/{logFileName}")
    public HtmlResponse download(final LogForm form) {
        // TODO
        return redirect(getClass());
        /*
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
                    LaResponseUtil.download(fileName, new FileInputStream(logFile));
                    return null;
                } catch (final FileNotFoundException e) {
                    logger.warn("Could not find " + logFile.getAbsolutePath(), e);
                }
            }
        }
        throw new SSCActionMessagesException("errors.could_not_find_log_file", new Object[] { logForm.logFileName });
        */
    }

    public List<Map<String, Object>> getLogFileItems() {
        // TODO
        final List<Map<String, Object>> logFileItems = new ArrayList<Map<String, Object>>();
        return logFileItems;
    }

}
