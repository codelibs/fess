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
package org.codelibs.fess.app.web.admin.log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
 * Admin action for Log.
 */
public class AdminLogAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminLogAction() {
    }

    /** The role name for log administration. */
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

    /**
     * Displays the log management index page.
     *
     * @return HTML response for the log list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asIndexHtml();
    }

    /**
     * Downloads a log file by its encoded ID.
     *
     * @param id the Base64 encoded filename of the log file to download
     * @return ActionResponse containing the log file stream
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final String id) {
        final String filename;
        try {
            filename = decodeLogFileId(id);
        } catch (final IllegalArgumentException e) {
            throwValidationError(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, id), this::asIndexHtml);
            return redirect(getClass()); // no-op
        }
        final Path path = findLogFile(systemHelper.getLogFilePath(), filename);
        if (path != null) {
            return asStream(path.getFileName().toString()).contentTypeOctetStream().stream(out -> {
                try (InputStream in = Files.newInputStream(path)) {
                    out.write(in);
                }
            });
        }
        throwValidationError(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, id), this::asIndexHtml);
        return redirect(getClass()); // no-op
    }

    /**
     * Decodes a log file ID, as handed out by {@link #getLogFileItems()}, back into the file name.
     * The ID is URL-safe Base64 ({@code -} and {@code _}); the standard alphabet ({@code +} and {@code /})
     * is accepted as well. A blank is read as {@code +}, which is what a {@code +} in a URL path
     * turns into when the container decodes it.
     *
     * @param id the Base64 encoded filename of the log file
     * @return the decoded filename
     * @throws IllegalArgumentException if the ID is not valid Base64
     */
    public static String decodeLogFileId(final String id) {
        return new String(Base64.getUrlDecoder().decode(id.replace('+', '-').replace(' ', '-').replace('/', '_')), StandardCharsets.UTF_8);
    }

    /**
     * Finds the log file that a decoded log file ID names. The name is looked up as it is, so any
     * file the log list shows is found by its own ID, white space and dots included. As before, a name
     * may also name a file below the log directory ({@code sub/x.log}, or {@code /sub/x.log}, which
     * stays inside it). What a name must not do is leave the log directory, so one that has a
     * {@code ..} path element, that is not a log file name, or that is not a regular file finds nothing.
     *
     * @param logFilePath the log directory
     * @param name the decoded log file ID
     * @return the log file, or null if the name does not name one
     */
    public static Path findLogFile(final String logFilePath, final String name) {
        if (StringUtil.isBlank(logFilePath) || name == null || !isLogFilename(name)) {
            return null;
        }
        final Path logDir;
        final Path path;
        try {
            logDir = Paths.get(logFilePath);
            path = Paths.get(logFilePath, name);
        } catch (final InvalidPathException e) {
            return null;
        }
        for (final Path element : logDir.relativize(path)) {
            if ("..".equals(element.toString())) {
                return null;
            }
        }
        return Files.isRegularFile(path) ? path : null;
    }

    /**
     * Sanitizes a filename by removing path traversal sequences and whitespace.
     *
     * @param filename the filename to sanitize
     * @return the sanitized filename
     */
    public static String sanitizeFilename(final String filename) {
        return filename.replaceAll("\\s", "").replace("\\", "/").replace("..", "").replaceAll("/+", "/");
    }

    /**
     * Gets a list of log file items for display in the admin interface.
     *
     * @return list of maps containing log file information (id, name, lastModified, size)
     */
    public static List<Map<String, Object>> getLogFileItems() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final List<Map<String, Object>> logFileItems = new ArrayList<>();
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            final Path logDirPath = Paths.get(logFilePath);
            try (Stream<Path> stream = Files.list(logDirPath)) {
                stream.filter(AdminLogAction::isLogFile).sorted().forEach(filePath -> {
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
                throw new FessSystemException("Failed to access log files: logFilePath=" + logFilePath, e);
            }
        }
        return logFileItems;
    }

    private static boolean isLogFile(final Path path) {
        return isLogFilename(path.getFileName().toString()) && Files.isRegularFile(path);
    }

    /**
     * Checks if the given filename is a log file.
     *
     * @param name the filename to check
     * @return true if the filename ends with .log or .log.gz, false otherwise
     */
    public static boolean isLogFilename(final String name) {
        return name.endsWith(".log") || name.endsWith(".log.gz");
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminLog_AdminLogJsp).renderWith(data -> {
            RenderDataUtil.register(data, "logFileItems", getLogFileItems());
        });
    }

}
