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
package org.codelibs.fess.chat;

import org.codelibs.fess.entity.ChatSession;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatSessionManagerLogTest extends UnitFessTestCase {

    @Test
    public void test_userIdWithLineBreakDoesNotReachTheLogRaw() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(ChatSessionManager.class);
        try {
            final ChatSessionManager manager = new ChatSessionManager();
            final ChatSession session = manager.createSession("alice");
            // A user id is the name an identity provider released, so it can carry what the provider put in it.
            manager.getOrCreateSession(session.getSessionId(), "bob\r\n2026-01-01 00:00:00,000 [main] ERROR forged");
            final String rendered = String.join("\n", appender.renderedEvents());
            assertTrue(rendered, rendered.contains("Session userId mismatch"));
            assertFalse(rendered, rendered.contains("\r"));
            assertFalse(rendered, rendered.contains("\nERROR") || rendered.contains("\n2026-01-01"));
        } finally {
            appender.detach();
        }
    }
}
