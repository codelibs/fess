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
package org.codelibs.fess.app.web.admin.log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminLogAction extends FessAdminAction {

    public static final String ROLE = "admin-log";

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameLog()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asIndexHtml();
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final String id) {
        final String filename = new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).replace("..", "").replaceAll("\\s", "");
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath) && isLogFilename(filename)) {
            final Path path = Paths.get(logFilePath, filename);
            return asStream(filename).contentTypeOctetStream().stream(out -> {
                try (InputStream in = Files.newInputStream(path)) {
                    out.write(in);
                }
            });
        }
        throwValidationError(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, filename), this::asIndexHtml);
        return redirect(getClass()); // no-op
    }

    public static List<Map<String, Object>> getLogFileItems() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final List<Map<String, Object>> logFileItems = new ArrayList<>();
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            final Path logDirPath = Paths.get(logFilePath);
            try (Stream<Path> stream = Files.list(logDirPath)) {
                stream.filter(entry -> isLogFilename(entry.getFileName().toString())).sorted().forEach(filePath -> {
                    final Map<String, Object> map = new HashMap<>();
                    final String name = filePath.getFileName().toString();
                    map.put("id", Base64.getUrlEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8)));
                    map.put("name", name);
                    try {
                        map.put("lastModified", new Date(Files.getLastModifiedTime(filePath).toMillis()));
                    } catch (final IOException e) {
                        throw new IORuntimeException(e);
                    }
                    logFileItems.add(map);
                });
            } catch (final Exception e) {
                throw new FessSystemException("Failed to access log files.", e);
            }
        }
        return logFileItems;
    }

    public static boolean isLogFilename(final String name) {
        return name.endsWith(".log") || name.endsWith(".log.gz");
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminLog_AdminLogJsp).renderWith(data -> {
            RenderDataUtil.register(data, "logFileItems", getLogFileItems());
        });
    }

}
