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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;

import com.google.common.base.Charsets;

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
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameLog()));
    }

    @Execute
    public HtmlResponse index() {
        return toIndexPage();
    }

    @Execute
    public ActionResponse download(final String id) {
        String filename = new String(Base64.getDecoder().decode(id), Charsets.UTF_8).replace("..", "").replaceAll("\\s", "");
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            Path path = Paths.get(logFilePath, filename);
            return asStream(filename).contentType("text/plain; charset=UTF-8").stream(out -> {
                try (InputStream in = Files.newInputStream(path)) {
                    out.write(in);
                }
            });
        }
        throwValidationError(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, filename), () -> {
            return toIndexPage();
        });
        return redirect(getClass());
    }

    public List<Map<String, Object>> getLogFileItems() {
        final List<Map<String, Object>> logFileItems = new ArrayList<Map<String, Object>>();
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            Path logDirPath = Paths.get(logFilePath);
            try (Stream<Path> stream = Files.list(logDirPath)) {
                stream.filter(entry -> entry.getFileName().toString().endsWith(".log")).forEach(filePath -> {
                    Map<String, Object> map = new HashMap<>();
                    String name = filePath.getFileName().toString();
                    map.put("id", Base64.getEncoder().encodeToString(name.getBytes(Charsets.UTF_8)));
                    map.put("name", name);
                    try {
                        map.put("lastModified", new Date(Files.getLastModifiedTime(filePath).toMillis()));
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                    logFileItems.add(map);
                });
            } catch (Exception e) {
                throw new FessSystemException("Failed to access log files.", e);
            }
        }
        return logFileItems;
    }

    private HtmlResponse toIndexPage() {
        return asHtml(path_AdminLog_AdminLogJsp).renderWith(data -> {
            data.register("logFileItems", getLogFileItems());
        });
    }

}
