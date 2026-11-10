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
package org.codelibs.fess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import org.apache.catalina.Context;
import org.apache.catalina.Valve;
import org.apache.catalina.startup.Tomcat;
import org.codelibs.fess.tomcat.session.FessSessionManager;
import org.codelibs.fess.tomcat.session.FessSessionValve;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FessBootSessionStoreTest {

    private File baseDir;

    private Context context;

    @BeforeEach
    public void setUp() throws Exception {
        baseDir = Files.createTempDirectory("fess-boot-session-test").toFile();
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        context = tomcat.addContext("", baseDir.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        baseDir.delete();
    }

    private boolean hasSessionValve() {
        return Arrays.stream(context.getPipeline().getValves()).map(Valve::getClass).anyMatch(FessSessionValve.class::equals);
    }

    @Test
    public void test_opensearch_installsManagerAndValve() {
        FessBoot.setupSessionStore(context, "opensearch");
        assertInstanceOf(FessSessionManager.class, context.getManager());
        assertEquals(true, hasSessionValve());
    }

    @Test
    public void test_opensearch_isCaseInsensitiveAndTrimmed() {
        FessBoot.setupSessionStore(context, " OpenSearch ");
        assertInstanceOf(FessSessionManager.class, context.getManager());
    }

    @Test
    public void test_unset_changesNothing() {
        FessBoot.setupSessionStore(context, null);
        FessBoot.setupSessionStore(context, "");
        FessBoot.setupSessionStore(context, "memory");
        assertNull(context.getManager());
        assertFalse(hasSessionValve());
    }

    @Test
    public void test_unknownValue_isTreatedAsMemory() {
        FessBoot.setupSessionStore(context, "redis");
        assertNull(context.getManager());
        assertFalse(hasSessionValve());
        assertNotNull(context);
    }
}
