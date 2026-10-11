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
package org.codelibs.fess.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FessPrepareFilterTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_duplicatedPlugins_namesThePluginAndBothJars() throws Exception {
        Files.writeString(tempDir.resolve("fess-script-ognl-15.8.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-script-ognl-15.9.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-ds-git-15.9.0.jar"), "x");

        final List<String> messages = FessPrepareFilter.duplicatedPlugins(tempDir);
        assertEquals(1, messages.size(), messages.toString());
        final String message = messages.get(0);
        assertTrue(message.contains("fess-script-ognl"), message);
        assertTrue(message.contains("fess-script-ognl-15.8.0.jar"), message);
        assertTrue(message.contains("fess-script-ognl-15.9.0.jar"), message);
        assertTrue(!message.contains("fess-ds-git"), message);
    }

    @Test
    public void test_duplicatedPlugins_isEmptyWhenEachPluginIsInstalledOnce() throws Exception {
        Files.writeString(tempDir.resolve("fess-webapp-mcp-15.9.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-webapp-mcp-client-15.9.0.jar"), "x");
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir));
    }

    @Test
    public void test_duplicatedPlugins_ignoresJarsThatAreNotPlugins() throws Exception {
        Files.writeString(tempDir.resolve("some-library-1.0.jar"), "x");
        Files.writeString(tempDir.resolve("some-library-2.0.jar"), "x");
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir));
    }

    @Test
    public void test_duplicatedPlugins_isEmptyWhenTheDirectoryIsAbsent() {
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir.resolve("none")));
    }
}
