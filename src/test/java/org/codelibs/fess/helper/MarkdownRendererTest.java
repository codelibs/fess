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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class MarkdownRendererTest extends UnitFessTestCase {

    private MarkdownRenderer markdownRenderer;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        markdownRenderer = new MarkdownRenderer();
        markdownRenderer.init();
    }

    @Test
    public void test_isInitialized() {
        assertTrue(markdownRenderer.isInitialized());
    }

    @Test
    public void test_render_null() {
        assertEquals("", markdownRenderer.render(null));
    }

    @Test
    public void test_render_empty() {
        assertEquals("", markdownRenderer.render(""));
    }

    @Test
    public void test_render_plainText() {
        String result = markdownRenderer.render("Hello World");
        assertTrue(result.contains("Hello World"));
        assertTrue(result.contains("<p>"));
    }

    @Test
    public void test_render_bold() {
        String result = markdownRenderer.render("**bold text**");
        assertTrue(result.contains("<strong>bold text</strong>"));
    }

    @Test
    public void test_render_italic() {
        String result = markdownRenderer.render("*italic text*");
        assertTrue(result.contains("<em>italic text</em>"));
    }

    @Test
    public void test_render_heading1() {
        String result = markdownRenderer.render("# Heading 1");
        assertTrue(result.contains("<h1>Heading 1</h1>"));
    }

    @Test
    public void test_render_heading2() {
        String result = markdownRenderer.render("## Heading 2");
        assertTrue(result.contains("<h2>Heading 2</h2>"));
    }

    @Test
    public void test_render_heading3() {
        String result = markdownRenderer.render("### Heading 3");
        assertTrue(result.contains("<h3>Heading 3</h3>"));
    }

    @Test
    public void test_render_unorderedList() {
        String markdown = "- Item 1\n- Item 2\n- Item 3";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li>"));
        assertTrue(result.contains("Item 1"));
        assertTrue(result.contains("Item 2"));
        assertTrue(result.contains("Item 3"));
    }

    @Test
    public void test_render_orderedList() {
        String markdown = "1. First\n2. Second\n3. Third";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("<ol>"));
        assertTrue(result.contains("<li>"));
        assertTrue(result.contains("First"));
        assertTrue(result.contains("Second"));
        assertTrue(result.contains("Third"));
    }

    @Test
    public void test_render_codeInline() {
        String result = markdownRenderer.render("Use `code` here");
        assertTrue(result.contains("<code>code</code>"));
    }

    @Test
    public void test_render_codeBlock() {
        String markdown = "```\ncode block\n```";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("<pre>"));
        assertTrue(result.contains("<code>"));
        assertTrue(result.contains("code block"));
    }

    @Test
    public void test_render_blockquote() {
        String result = markdownRenderer.render("> This is a quote");
        assertTrue(result.contains("<blockquote>"));
        assertTrue(result.contains("This is a quote"));
    }

    @Test
    public void test_render_link_http() {
        String result = markdownRenderer.render("[Link](http://example.com)");
        assertTrue(result.contains("<a"));
        assertTrue(result.contains("href=\"http://example.com\""));
        assertTrue(result.contains("Link"));
        assertTrue(result.contains("rel=\"nofollow\""));
    }

    @Test
    public void test_render_link_https() {
        String result = markdownRenderer.render("[Secure Link](https://example.com)");
        assertTrue(result.contains("<a"));
        assertTrue(result.contains("href=\"https://example.com\""));
        assertTrue(result.contains("Secure Link"));
    }

    @Test
    public void test_render_horizontalRule() {
        String result = markdownRenderer.render("---");
        assertTrue(result.contains("<hr"));
    }

    @Test
    public void test_render_table() {
        String markdown = "| Header 1 | Header 2 |\n|----------|----------|\n| Cell 1   | Cell 2   |";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("<table>"));
        assertTrue(result.contains("<th>"));
        assertTrue(result.contains("<td>"));
        assertTrue(result.contains("Header 1"));
        assertTrue(result.contains("Cell 1"));
    }

    @Test
    public void test_render_xss_scriptTag() {
        String malicious = "<script>alert('XSS')</script>";
        String result = markdownRenderer.render(malicious);
        // Script tags should be removed by sanitizer
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("</script>"));
    }

    @Test
    public void test_render_xss_onclickAttribute() {
        String malicious = "<a href=\"#\" onclick=\"alert('XSS')\">Click</a>";
        String result = markdownRenderer.render(malicious);
        // onclick attribute should be removed
        assertFalse(result.contains("onclick"));
    }

    @Test
    public void test_render_xss_javascriptProtocol() {
        String malicious = "[Click me](javascript:alert('XSS'))";
        String result = markdownRenderer.render(malicious);
        // javascript: protocol should be blocked
        assertFalse(result.contains("javascript:"));
    }

    @Test
    public void test_render_xss_dataProtocol() {
        String malicious = "[Click me](data:text/html,<script>alert('XSS')</script>)";
        String result = markdownRenderer.render(malicious);
        // data: protocol should be blocked
        assertFalse(result.contains("data:text/html"));
    }

    @Test
    public void test_render_xss_imgOnerror() {
        String malicious = "<img src=\"x\" onerror=\"alert('XSS')\">";
        String result = markdownRenderer.render(malicious);
        // onerror attribute should be removed
        assertFalse(result.contains("onerror"));
    }

    @Test
    public void test_render_xss_iframeTag() {
        String malicious = "<iframe src=\"http://evil.com\"></iframe>";
        String result = markdownRenderer.render(malicious);
        // iframe tags should be removed
        assertFalse(result.contains("<iframe"));
        assertFalse(result.contains("</iframe>"));
    }

    @Test
    public void test_render_xss_styleTag() {
        String malicious = "<style>body { background: url('javascript:alert(1)'); }</style>";
        String result = markdownRenderer.render(malicious);
        // style tags should be removed
        assertFalse(result.contains("<style>"));
    }

    @Test
    public void test_render_multipleNewlines() {
        String markdown = "Line 1\n\nLine 2\n\nLine 3";
        String result = markdownRenderer.render(markdown);
        // Should create multiple paragraphs
        assertTrue(result.contains("Line 1"));
        assertTrue(result.contains("Line 2"));
        assertTrue(result.contains("Line 3"));
    }

    @Test
    public void test_render_mixedContent() {
        String markdown = "# Title\n\nSome **bold** and *italic* text.\n\n- List item 1\n- List item 2\n\n> A quote\n\n`inline code`";
        String result = markdownRenderer.render(markdown);

        assertTrue(result.contains("<h1>"));
        assertTrue(result.contains("<strong>bold</strong>"));
        assertTrue(result.contains("<em>italic</em>"));
        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li>"));
        assertTrue(result.contains("<blockquote>"));
        assertTrue(result.contains("<code>inline code</code>"));
    }

    @Test
    public void test_render_escapedCharacters() {
        String markdown = "\\*not italic\\*";
        String result = markdownRenderer.render(markdown);
        // Escaped asterisks should not be converted to italics
        assertFalse(result.contains("<em>"));
    }

    @Test
    public void test_render_strikethrough() {
        // Note: This depends on whether the extension is enabled
        String result = markdownRenderer.render("~~strikethrough~~");
        // Just verify it doesn't break
        assertNotNull(result);
    }

    @Test
    public void test_render_nestedLists() {
        String markdown = "- Item 1\n  - Nested 1\n  - Nested 2\n- Item 2";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("<ul>"));
        assertTrue(result.contains("<li>"));
        assertTrue(result.contains("Item 1"));
        assertTrue(result.contains("Nested 1"));
    }

    @Test
    public void test_render_paragraphWithLineBreak() {
        String markdown = "Line 1  \nLine 2";
        String result = markdownRenderer.render(markdown);
        // Two spaces at end of line creates a line break
        assertTrue(result.contains("<br"));
    }

    @Test
    public void test_render_specialHtmlEntities() {
        String markdown = "Less than < and greater than > and ampersand &";
        String result = markdownRenderer.render(markdown);
        // These should be preserved or properly escaped
        assertNotNull(result);
        assertTrue(result.contains("Less than"));
    }

    @Test
    public void test_render_unicodeCharacters() {
        String markdown = "日本語テキスト **太字** *斜体*";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("日本語テキスト"));
        assertTrue(result.contains("<strong>太字</strong>"));
        assertTrue(result.contains("<em>斜体</em>"));
    }

    @Test
    public void test_render_longContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("Paragraph ").append(i).append("\n\n");
        }
        String result = markdownRenderer.render(sb.toString());
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    public void test_render_emptyParagraphs() {
        String markdown = "Text\n\n\n\nMore text";
        String result = markdownRenderer.render(markdown);
        assertTrue(result.contains("Text"));
        assertTrue(result.contains("More text"));
    }

    @Test
    public void test_notInitialized() {
        MarkdownRenderer uninitializedRenderer = new MarkdownRenderer();
        // Before init, isInitialized should return false
        assertFalse(uninitializedRenderer.isInitialized());
    }
}
