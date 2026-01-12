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

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Renders markdown to sanitized HTML for safe display in the chat interface.
 * Uses commonmark for markdown parsing and OWASP HTML Sanitizer for XSS prevention.
 */
public class MarkdownRenderer {

    private static final Logger logger = LogManager.getLogger(MarkdownRenderer.class);

    private Parser markdownParser;
    private HtmlRenderer htmlRenderer;
    private PolicyFactory htmlSanitizer;

    /**
     * Default constructor.
     */
    public MarkdownRenderer() {
        // empty
    }

    /**
     * Initializes the markdown parser, HTML renderer, and sanitizer.
     */
    @PostConstruct
    public void init() {
        // Configure commonmark with table extension
        final Iterable<Extension> extensions = Arrays.asList(TablesExtension.create());

        markdownParser = Parser.builder().extensions(extensions).build();

        htmlRenderer = HtmlRenderer.builder().extensions(extensions).softbreak("<br/>").build();

        // Configure OWASP HTML Sanitizer with allowed tags
        htmlSanitizer = new HtmlPolicyBuilder()
                // Headings
                .allowElements("h1", "h2", "h3", "h4", "h5", "h6")
                // Text formatting
                .allowElements("p", "br", "hr")
                .allowElements("strong", "em", "b", "i", "u", "s", "del")
                // Lists
                .allowElements("ul", "ol", "li")
                // Code
                .allowElements("code", "pre")
                // Blockquote
                .allowElements("blockquote")
                // Tables
                .allowElements("table", "thead", "tbody", "tr", "th", "td")
                // Links - only allow http/https protocols
                .allowElements("a")
                .allowUrlProtocols("http", "https")
                .allowAttributes("href")
                .onElements("a")
                .requireRelNofollowOnLinks()
                // Images - only allow http/https protocols
                .allowElements("img")
                .allowUrlProtocols("http", "https")
                .allowAttributes("src", "alt", "title")
                .onElements("img")
                // Span and div for formatting
                .allowElements("span", "div")
                // Class attributes for styling code blocks
                .allowAttributes("class")
                .onElements("code", "pre", "span", "div")
                .toFactory();

        if (logger.isDebugEnabled()) {
            logger.debug("MarkdownRenderer initialized with commonmark and OWASP sanitizer");
        }
    }

    /**
     * Renders markdown text to sanitized HTML.
     *
     * @param markdown the markdown text to render
     * @return sanitized HTML string
     */
    public String render(final String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        try {
            // Parse markdown to AST
            final var document = markdownParser.parse(markdown);

            // Render AST to HTML
            final String html = htmlRenderer.render(document);

            // Sanitize HTML to prevent XSS
            final String sanitizedHtml = htmlSanitizer.sanitize(html);

            if (logger.isDebugEnabled()) {
                logger.debug("Rendered markdown. inputLength={}, outputLength={}", markdown.length(), sanitizedHtml.length());
            }

            return sanitizedHtml;
        } catch (final Exception e) {
            logger.warn("Failed to render markdown, returning escaped plain text. error={}", e.getMessage());
            // Fallback to escaped plain text
            return escapeHtml(markdown);
        }
    }

    /**
     * Escapes HTML special characters for safe display.
     *
     * @param text the text to escape
     * @return HTML-escaped text
     */
    private String escapeHtml(final String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }

    /**
     * Checks if the renderer is properly initialized.
     *
     * @return true if initialized
     */
    public boolean isInitialized() {
        return markdownParser != null && htmlRenderer != null && htmlSanitizer != null;
    }
}
