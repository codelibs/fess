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
package org.codelibs.fess.app.web.api.admin.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;

/**
 * Tests for the log file download of {@link ApiAdminLogAction}: a log file in the log directory is
 * served, and no id reaches a file outside of it.
 */
public class ApiAdminLogActionTest extends UnitFessTestCase {

    private static final String OUTSIDE = "outside the log directory";

    private Path logDir;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // <root>/etc/hosts.log is outside of the log directory <root>/a/b, two levels above it.
        final Path root = Files.createTempDirectory("fess_api_log");
        logDir = Files.createDirectories(root.resolve("a").resolve("b"));
        Files.createDirectories(root.resolve("etc"));
        Files.write(root.resolve("etc").resolve("hosts.log"), OUTSIDE.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void test_get$file_servesLogFilesOfTheLogDirectory() throws Exception {
        Files.write(logDir.resolve("fess.log"), "log line".getBytes(StandardCharsets.UTF_8));
        Files.write(logDir.resolve("fess.log.gz"), "gz bytes".getBytes(StandardCharsets.UTF_8));

        final StreamResponse log = action().get$file(id("fess.log"));
        assertEquals("fess.log", log.getFileName());
        assertEquals("log line", drain(log));
        assertEquals("gz bytes", drain(action().get$file(id("fess.log.gz"))));
    }

    /** Whitespace between the dots must not turn into ".." after the ".." removal. */
    @Test
    public void test_get$file_whitespaceBetweenDotsDoesNotEscapeTheLogDirectory() throws Exception {
        final StreamResponse response = action().get$file(id(". ./. ./etc/hosts.log"));

        assertFalse(response.getFileName().contains(".."), "the file name must not climb: " + response.getFileName());
        assertFalse(served(response).contains(OUTSIDE), "a file outside of the log directory was served");
    }

    @Test
    public void test_get$file_noTraversalSpellingReachesAFileOutsideTheLogDirectory() throws Exception {
        final String[] ids = { "../../etc/hosts.log", ". ./. ./etc/hosts.log", ".\t./.\t./etc/hosts.log", ".\n./.\n./etc/hosts.log",
                "..\\..\\etc\\hosts.log", ". .\\. .\\etc\\hosts.log", "....//....//etc/hosts.log", "/../../etc/hosts.log" };
        for (final String id : ids) {
            final StreamResponse response = action().get$file(id(id));
            assertFalse(served(response).contains(OUTSIDE), "a file outside of the log directory was served for: " + id);
        }
    }

    @Test
    public void test_get$file_absoluteLookingNameStaysInTheLogDirectory() throws Exception {
        final Path other = Files.createDirectories(logDir.resolve("etc"));
        Files.write(other.resolve("hosts.log"), "inside".getBytes(StandardCharsets.UTF_8));

        assertEquals("inside", drain(action().get$file(id("/etc/hosts.log"))));
    }

    @Test
    public void test_get$file_nonLogNamesAnswerAnEmptyBody() throws Exception {
        for (final String name : new String[] { "../../etc/passwd", "fess.txt", "fess.log.bak", "", ".." }) {
            assertNull(action().get$file(id(name)).getStreamCall(), "no stream must be served for: " + name);
        }
    }

    private ApiAdminLogAction action() {
        final String path = logDir.toString();
        return new ApiAdminLogAction() {
            {
                systemHelper = new SystemHelper() {
                    @Override
                    public String getLogFilePath() {
                        return path;
                    }
                };
            }
        };
    }

    /** The id the log list hands out. */
    private static String id(final String name) {
        return Base64.getUrlEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    /** What the client receives: the streamed text, or the failure that reached it instead. */
    private String served(final StreamResponse response) {
        if (response.getStreamCall() == null) {
            return "";
        }
        try {
            return drain(response);
        } catch (final Exception e) {
            return e.getClass().getSimpleName();
        }
    }

    private String drain(final StreamResponse response) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getStreamCall().callback(new WrittenStreamOut() {
            @Override
            public OutputStream stream() {
                return out;
            }

            @Override
            public void write(final InputStream ins) throws IOException {
                CopyUtil.copy(ins, out);
            }
        });
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
