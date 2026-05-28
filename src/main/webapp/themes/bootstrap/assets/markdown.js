// SPDX-License-Identifier: Apache-2.0
// Minimal in-repo Markdown renderer for the Fess bootstrap SPA.
// Produces HTML from a safe subset of Markdown. All input is HTML-escaped
// before pattern substitution — no raw user strings appear in output.
// The caller MUST pass the return value through sanitizeHtml() before
// appending to the live DOM (defense-in-depth).

import { escapeHtml } from "./format.js";

/**
 * Parse a limited subset of Markdown to HTML.
 *
 * Supported constructs:
 *   - Fenced code blocks (``` ... ```)
 *   - ATX headings: ## H2 / ### H3 / #### H4
 *   - Paragraphs (blank-line separated)
 *   - Line breaks within a paragraph
 *   - **bold** / *italic* / `inline code`
 *   - Links: [text](url)
 *   - Unordered lists: - item / * item
 *   - Ordered lists: 1. item
 *
 * @param {string} text - Raw markdown string (may be untrusted).
 * @returns {string} HTML string safe to pass through sanitizeHtml().
 */
export function parseMarkdown(text) {
  if (!text || typeof text !== "string") return "";

  // Step 1: Normalise line endings.
  const normalised = text.replace(/\r\n/g, "\n").replace(/\r/g, "\n");

  // Step 2: Extract fenced code blocks so their contents are not processed by
  // inline rules.  Replace each block with a unique placeholder token.
  const codeBlocks = [];
  const withoutCode = normalised.replace(/```([^\n]*)\n([\s\S]*?)```/g, (_m, _lang, code) => {
    const idx = codeBlocks.length;
    // Escape the code block content immediately.
    codeBlocks.push("<pre><code>" + escapeHtml(code.replace(/\n$/, "")) + "</code></pre>");
    return "\x00CODEBLOCK" + idx + "\x00";
  });

  // Step 3: Split into block-level segments (paragraphs, headings, lists).
  // We process line-by-line within each blank-separated block.
  const blocks = splitBlocks(withoutCode);

  // Step 4: Process each block.
  const parts = blocks.map(block => processBlock(block));

  // Step 5: Restore code blocks.
  let html = parts.join("\n");
  codeBlocks.forEach((cb, idx) => {
    html = html.replace("\x00CODEBLOCK" + idx + "\x00", cb);
  });

  return html;
}

// ---------------------------------------------------------------------------
// Block splitting
// ---------------------------------------------------------------------------

/**
 * Split text into block-level units by one or more blank lines.
 * Preserves non-empty blocks only.
 *
 * @param {string} text
 * @returns {string[]}
 */
function splitBlocks(text) {
  return text
    .split(/\n{2,}/)
    .map(b => b.trim())
    .filter(b => b.length > 0);
}

// ---------------------------------------------------------------------------
// Block processing
// ---------------------------------------------------------------------------

const HEADING_RE = /^(#{2,4}) (.+)$/;
const UL_LINE_RE = /^[-*] (.+)$/;
const OL_LINE_RE = /^(\d+)\. (.+)$/;

/**
 * Determine the type of a block and render it.
 *
 * @param {string} block - One block of text (no blank lines within).
 * @returns {string} HTML string.
 */
function processBlock(block) {
  // Code block placeholder — pass through.
  if (block.startsWith("\x00CODEBLOCK")) return block;

  const lines = block.split("\n");

  // Heading: all lines must be headings for it to render as headings
  // (or just first line for single-line block).
  if (lines.length === 1) {
    const hm = lines[0].match(HEADING_RE);
    if (hm) {
      const level = Math.min(hm[1].length, 4);
      const tag = "h" + level;
      return "<" + tag + ">" + inlineMarkdown(hm[2]) + "</" + tag + ">";
    }
  }

  // Check whether every non-empty line is an unordered list item.
  if (lines.every(l => UL_LINE_RE.test(l) || l.trim() === "")) {
    const items = lines
      .filter(l => UL_LINE_RE.test(l))
      .map(l => "<li>" + inlineMarkdown(l.match(UL_LINE_RE)[1]) + "</li>");
    return "<ul>" + items.join("") + "</ul>";
  }

  // Check whether every non-empty line is an ordered list item.
  if (lines.every(l => OL_LINE_RE.test(l) || l.trim() === "")) {
    const items = lines
      .filter(l => OL_LINE_RE.test(l))
      .map(l => "<li>" + inlineMarkdown(l.match(OL_LINE_RE)[2]) + "</li>");
    return "<ol>" + items.join("") + "</ol>";
  }

  // Mixed block: treat as a paragraph, joining with <br> for soft line breaks.
  const rendered = lines
    .map(l => {
      // If the line itself is a heading, render it standalone.
      const hm = l.match(HEADING_RE);
      if (hm) {
        const level = Math.min(hm[1].length, 4);
        const tag = "h" + level;
        return "<" + tag + ">" + inlineMarkdown(hm[2]) + "</" + tag + ">";
      }
      return inlineMarkdown(l);
    })
    .join("<br>");
  return "<p>" + rendered + "</p>";
}

// ---------------------------------------------------------------------------
// Inline markdown
// ---------------------------------------------------------------------------

/**
 * Render inline Markdown within a single line of text.
 * Escapes HTML first, then applies pattern substitutions in order:
 *   `code`, **bold**, *italic*, [link](url)
 *
 * @param {string} text - Raw inline markdown text.
 * @returns {string} HTML-safe string.
 */
function inlineMarkdown(text) {
  // Escape all HTML first — no raw input reaches the output.
  let s = escapeHtml(text);

  // Inline code — match `...` (non-greedy, no newlines).
  // Done first so backtick content is not processed by bold/italic rules.
  const inlineCodes = [];
  s = s.replace(/`([^`\n]+)`/g, (_m, code) => {
    const idx = inlineCodes.length;
    // code is already HTML-escaped (from escapeHtml above).
    inlineCodes.push("<code>" + code + "</code>");
    return "\x00IC" + idx + "\x00";
  });

  // Bold: **text** or __text__ (escaped chars: &lt; etc. are fine inside bold)
  s = s.replace(/\*\*(.+?)\*\*/g, "<strong>$1</strong>");
  s = s.replace(/__(.+?)__/g, "<strong>$1</strong>");

  // Italic: *text* or _text_ (single markers, not already consumed by bold)
  s = s.replace(/\*(.+?)\*/g, "<em>$1</em>");
  s = s.replace(/_(.+?)_/g, "<em>$1</em>");

  // Links: [text](url) — url is already escaped; validate scheme.
  s = s.replace(/\[([^\]]*)\]\(([^)]*)\)/g, (_m, linkText, url) => {
    // url came through escapeHtml — restore &amp; for scheme check only.
    const rawUrl = url.replace(/&amp;/g, "&");
    const lower = rawUrl.trim().toLowerCase();
    if (/^javascript\s*:/i.test(lower) || /^data\s*:/i.test(lower)) {
      // Reject dangerous schemes — render as plain text.
      return linkText;
    }
    // External links get target + rel; keep url HTML-escaped for safety.
    const isExternal = /^https?:\/\//i.test(rawUrl.trim());
    const extras = isExternal ? ' target="_blank" rel="noopener noreferrer"' : "";
    return '<a href="' + url + '"' + extras + ">" + linkText + "</a>";
  });

  // Restore inline code placeholders.
  inlineCodes.forEach((ic, idx) => {
    s = s.replace("\x00IC" + idx + "\x00", ic);
  });

  return s;
}
