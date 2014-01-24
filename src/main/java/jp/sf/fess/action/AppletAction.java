/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.action;

import java.io.IOException;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.form.AppletForm;
import jp.sf.fess.helper.SystemHelper;

import org.apache.commons.lang.StringEscapeUtils;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppletAction {

    private static final Logger logger = LoggerFactory
            .getLogger(AppletAction.class);

    @ActionForm
    @Resource
    protected AppletForm appletForm;

    @Resource
    protected SystemHelper systemHelper;

    public String path;

    public String referrer;

    public String launcherJarFile;

    public String launcherJnlpFile;

    @Execute(validator = true, input = "../index")
    public String index() {
        try {
            ResponseUtil.getResponse().sendRedirect(appletForm.uri);
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_redirect", appletForm.uri);
        }
        return null;
    }

    @Execute(validator = true, input = "../index")
    public String launcher() {
        launcherJarFile = systemHelper.getLauncherJarPath();
        if (StringUtil.isBlank(launcherJarFile)) {
            throw new SSCActionMessagesException(
                    "errors.no_launcher_applet_jar");
        }
        launcherJnlpFile = systemHelper.getLauncherJnlpPath();
        if (StringUtil.isBlank(launcherJnlpFile)) {
            throw new SSCActionMessagesException(
                    "errors.no_launcher_applet_jar");
        }

        String encoding = appletForm.encoding;
        if (StringUtil.isBlank(encoding)) {
            encoding = Constants.UTF_8;
        }

        path = StringEscapeUtils.escapeJavaScript(appletForm.uri);

        final String url = RequestUtil.getRequest().getHeader("REFERER");
        if (StringUtil.isBlank(url)) {
            referrer = "";
        } else {
            referrer = StringEscapeUtils.escapeJavaScript(url);
        }

        return "launcher.jsp";
    }

}