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
package org.codelibs.fess.helper;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.mail.send.supplement.SMailPostingDiscloser;
import org.dbflute.optional.OptionalThing;

public class NotificationHelperTest extends UnitFessTestCase {

    private NotificationHelper notificationHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        notificationHelper = new NotificationHelper();
    }

    public void test_sendToSlack_withBlankWebhookUrls() {
        // Setup mock configuration with blank webhook URLs
        ComponentUtil.setFessConfig(new MockFessConfig() {
            @Override
            public String getSlackWebhookUrls() {
                return "";
            }
        });

        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        // Should return early without doing anything
        try {
            notificationHelper.sendToSlack(null, discloser);
        } catch (Exception e) {
            fail("sendToSlack() should not throw an exception with blank URLs: " + e.getMessage());
        }
    }

    public void test_sendToSlack_withNullWebhookUrls() {
        // Setup mock configuration with null webhook URLs
        ComponentUtil.setFessConfig(new MockFessConfig() {
            @Override
            public String getSlackWebhookUrls() {
                return null;
            }
        });

        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        // Should return early without doing anything
        try {
            notificationHelper.sendToSlack(null, discloser);
        } catch (Exception e) {
            fail("sendToSlack() should not throw an exception with null URLs: " + e.getMessage());
        }
    }

    public void test_sendToGoogleChat_withBlankWebhookUrls() {
        // Setup mock configuration with blank webhook URLs
        ComponentUtil.setFessConfig(new MockFessConfig() {
            @Override
            public String getGoogleChatWebhookUrls() {
                return "";
            }
        });

        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        // Should return early without doing anything
        try {
            notificationHelper.sendToGoogleChat(null, discloser);
        } catch (Exception e) {
            fail("sendToGoogleChat() should not throw an exception with blank URLs: " + e.getMessage());
        }
    }

    public void test_sendToGoogleChat_withNullWebhookUrls() {
        // Setup mock configuration with null webhook URLs
        ComponentUtil.setFessConfig(new MockFessConfig() {
            @Override
            public String getGoogleChatWebhookUrls() {
                return null;
            }
        });

        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        // Should return early without doing anything
        try {
            notificationHelper.sendToGoogleChat(null, discloser);
        } catch (Exception e) {
            fail("sendToGoogleChat() should not throw an exception with null URLs: " + e.getMessage());
        }
    }

    public void test_toSlackMessage_withBasicContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test Subject");
        discloser.setPlainText("Test Content");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test Subject"));
        assertTrue(result.contains("Test Content"));
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toSlackMessage_withEmptyContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("");
        discloser.setPlainText("");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toSlackMessage_withNullContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject(null);
        discloser.setPlainText(null);

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toSlackMessage_withSpecialCharacters() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test \"Subject\" with 'quotes'");
        discloser.setPlainText("Test content with\nnewlines and\ttabs");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test"));
        assertTrue(result.contains("Subject"));
        assertTrue(result.contains("quotes"));
        assertTrue(result.contains("content"));
        assertTrue(result.contains("newlines"));
        assertTrue(result.contains("tabs"));
        // JSON should be properly escaped
        assertTrue(result.contains("Test"));
    }

    public void test_toSlackMessage_withWhitespaceContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("  Test Subject  ");
        discloser.setPlainText("  Test Content  ");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test Subject"));
        assertTrue(result.contains("Test Content"));
        // Should be trimmed
        assertFalse(result.contains("  Test Subject  "));
        assertFalse(result.contains("  Test Content  "));
    }

    public void test_toGoogleChatMessage_withBasicContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test Subject");
        discloser.setPlainText("Test Content");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test Subject"));
        assertTrue(result.contains("Test Content"));
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toGoogleChatMessage_withEmptyContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("");
        discloser.setPlainText("");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toGoogleChatMessage_withNullContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject(null);
        discloser.setPlainText(null);

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

        assertTrue(result.contains("```"));
    }

    public void test_toGoogleChatMessage_withSpecialCharacters() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test \"Subject\" with 'quotes'");
        discloser.setPlainText("Test content with\nnewlines and\ttabs");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test"));
        assertTrue(result.contains("Subject"));
        assertTrue(result.contains("quotes"));
        assertTrue(result.contains("content"));
        assertTrue(result.contains("newlines"));
        assertTrue(result.contains("tabs"));
        // JSON should be properly escaped
        assertTrue(result.contains("Test"));
    }

    public void test_toGoogleChatMessage_withWhitespaceContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("  Test Subject  ");
        discloser.setPlainText("  Test Content  ");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Test Subject"));
        assertTrue(result.contains("Test Content"));
        // Should be trimmed
        assertFalse(result.contains("  Test Subject  "));
        assertFalse(result.contains("  Test Content  "));
    }

    public void test_slackAndGoogleChatMessage_identical() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test Subject");
        discloser.setPlainText("Test Content");

        String slackResult = notificationHelper.toSlackMessage(discloser);
        String googleChatResult = notificationHelper.toGoogleChatMessage(discloser);

        // Both methods should produce identical output
        assertEquals(slackResult, googleChatResult);
    }

    public void test_toSlackMessage_withLongContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        StringBuilder longSubject = new StringBuilder();
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longSubject.append("Subject Line ").append(i).append(" ");
            longContent.append("Content Line ").append(i).append(" with more text. ");
        }

        discloser.setSubject(longSubject.toString());
        discloser.setPlainText(longContent.toString());

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Subject Line"));
        assertTrue(result.contains("Content Line"));
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

    }

    public void test_toGoogleChatMessage_withLongContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();

        StringBuilder longSubject = new StringBuilder();
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longSubject.append("Subject Line ").append(i).append(" ");
            longContent.append("Content Line ").append(i).append(" with more text. ");
        }

        discloser.setSubject(longSubject.toString());
        discloser.setPlainText(longContent.toString());

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Subject Line"));
        assertTrue(result.contains("Content Line"));
        assertTrue(result.startsWith("{\"text\":\""));
        assertTrue(result.endsWith("\"}"));

    }

    public void test_toSlackMessage_withUnicodeContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("テスト件名 🚀");
        discloser.setPlainText("テスト内容です。\n改行もあります。");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        // Unicode characters may be escaped in JSON
        assertTrue(result.contains("テスト") || result.contains("\\u"));
    }

    public void test_toGoogleChatMessage_withUnicodeContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("テスト件名 🚀");
        discloser.setPlainText("テスト内容です。\n改行もあります。");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        // Unicode characters may be escaped in JSON
        assertTrue(result.contains("テスト") || result.contains("\\u"));
    }

    public void test_constantValue() {
        assertEquals('\n', NotificationHelper.LF);
    }

    public void test_send_withBothConfigurations() {
        // Setup mock configuration with both webhook URLs
        ComponentUtil.setFessConfig(new MockFessConfig());

        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Test Subject");
        discloser.setPlainText("Test Content");

        // Should call both sendToSlack and sendToGoogleChat
        try {
            notificationHelper.send(null, discloser);
        } catch (Exception e) {
            fail("send() should not throw an exception: " + e.getMessage());
        }
    }

    public void test_toSlackMessage_withOnlySubject() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Only Subject");
        discloser.setPlainText(null);

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Only Subject"));
        assertTrue(result.contains("```"));
    }

    public void test_toSlackMessage_withOnlyContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject(null);
        discloser.setPlainText("Only Content");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Only Content"));
        assertTrue(result.contains("```"));
    }

    public void test_toGoogleChatMessage_withOnlySubject() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Only Subject");
        discloser.setPlainText(null);

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Only Subject"));
        assertTrue(result.contains("```"));
    }

    public void test_toGoogleChatMessage_withOnlyContent() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject(null);
        discloser.setPlainText("Only Content");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Only Content"));
        assertTrue(result.contains("```"));
    }

    public void test_toSlackMessage_withBackslashCharacters() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Subject with \\ backslash");
        discloser.setPlainText("Content with \\ backslash");

        String result = notificationHelper.toSlackMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Subject with"));
        assertTrue(result.contains("Content with"));
        assertTrue(result.contains("backslash"));
    }

    public void test_toGoogleChatMessage_withBackslashCharacters() {
        MockSMailPostingDiscloser discloser = new MockSMailPostingDiscloser();
        discloser.setSubject("Subject with \\ backslash");
        discloser.setPlainText("Content with \\ backslash");

        String result = notificationHelper.toGoogleChatMessage(discloser);

        assertNotNull(result);
        assertTrue(result.contains("Subject with"));
        assertTrue(result.contains("Content with"));
        assertTrue(result.contains("backslash"));
    }

    // Mock classes
    private static class MockFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String getSlackWebhookUrls() {
            return "https://hooks.slack.com/services/test";
        }

        @Override
        public String getGoogleChatWebhookUrls() {
            return "https://chat.googleapis.com/v1/spaces/test/messages?key=test";
        }
    }

    private static class MockSMailPostingDiscloser implements SMailPostingDiscloser {
        private String subject;
        private String plainText;

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setPlainText(String plainText) {
            this.plainText = plainText;
        }

        @Override
        public OptionalThing<String> getSavedSubject() {
            return OptionalThing.ofNullable(subject, () -> {
                throw new IllegalStateException("Subject not found");
            });
        }

        @Override
        public OptionalThing<String> getSavedPlainText() {
            return OptionalThing.ofNullable(plainText, () -> {
                throw new IllegalStateException("Plain text not found");
            });
        }

        @Override
        public OptionalThing<String> getSavedHtmlText() {
            return OptionalThing.empty();
        }

        @Override
        public OptionalThing<String> getSavedReturnPath() {
            return OptionalThing.empty();
        }

        @Override
        public java.util.List<jakarta.mail.Address> getSavedReplyToList() {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.List<jakarta.mail.Address> getSavedBccList() {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.List<jakarta.mail.Address> getSavedCcList() {
            return java.util.Collections.emptyList();
        }

        @Override
        public OptionalThing<String> getLastServerResponse() {
            return OptionalThing.of("Mock response");
        }

        @Override
        public OptionalThing<Integer> getLastReturnCode() {
            return OptionalThing.of(200);
        }

        @Override
        public java.util.Map<String, org.dbflute.mail.send.supplement.attachment.SMailReadAttachedData> getSavedAttachmentMap() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public java.util.List<jakarta.mail.Address> getSavedToList() {
            return java.util.Collections.emptyList();
        }

        @Override

        public OptionalThing<jakarta.mail.Address> getSavedFrom() {

            return OptionalThing.empty();

        }

        @Override

        public void makeEmlFile(String fileName) {

        }

        @Override

        public String toHash() {

            return "mock-hash";

        }

        @Override
        public String toDisplay() {

            return "mock-display";

        }

        @Override
        public java.util.Map<String, java.util.Map<String, Object>> getOfficeManagedLoggingMap() {

            return java.util.Collections.emptyMap();

        }

        @Override
        public java.util.Map<String, Object> getPushedLoggingMap() {

            return java.util.Collections.emptyMap();

        }

        @Override
        public boolean isTraining() {

            return false;

        }

        @Override
        public jakarta.mail.internet.MimeMessage getMimeMessage() {

            return null;

        }

    }

}