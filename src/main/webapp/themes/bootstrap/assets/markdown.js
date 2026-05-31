// SPDX-License-Identifier: Apache-2.0
// Minimal in-repo Markdown renderer for the Fess bootstrap SPA.
// Produces HTML from a safe subset of Markdown. All input is HTML-escaped
// before pattern substitution — no raw user strings appear in output.
// The caller MUST pass the return value through sanitizeHtml() before
// appending to the live DOM (defense-in-depth).

import { escapeHtml, isSafeHref } from "./format.js";

/**
 * Parse a limited subset of Markdown to HTML.
 *
 * Supported constructs:
 *   - Fenced code blocks (``` ... ```)
 *   - ATX headings: # H1 / ## H2 / ### H3 / #### H4 / ##### H5 / ###### H6
 *   - Paragraphs (blank-line separated)
 *   - Line breaks within a paragraph
 *   - **bold** / *italic* / `inline code`
 *   - Links: [text](url) — external links get rel="nofollow noopener noreferrer"
 *   - Unordered lists: - item / * item (with nesting via indentation)
 *   - Ordered lists: 1. item (with nesting via indentation)
 *   - Blockquotes: > text (with nesting via >> / multiple > prefixes)
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

const HEADING_RE = /^(#{1,6}) (.+)$/;
const UL_LINE_RE = /^([ \t]*)[-*] (.+)$/;
const OL_LINE_RE = /^([ \t]*)(\d+)\. (.+)$/;
// D6: Blockquote line regex — strips leading '>' with optional space.
const BLOCKQUOTE_LINE_RE = /^(>+)\s?(.*)$/;
// Simple (no-indent) list line regexes for type-detection passes.
const UL_TOP_RE = /^[-*] (.+)$/;
const OL_TOP_RE = /^(\d+)\. (.+)$/;
// D6: GFM table delimiter row — cells of ':?-+:?' separated by '|'.
// Accepted limitation: column alignment and nested-block-in-cell are NOT rendered.
const TABLE_DELIM_RE = /^\s*\|?\s*:?-{1,}:?\s*(\|\s*:?-{1,}:?\s*)*\|?\s*$/;
// GAP D2: Horizontal rule — three or more -, *, or _ on a line by themselves.
const HR_RE = /^(?:-{3,}|\*{3,}|_{3,})$/;

// ---------------------------------------------------------------------------
// Nested list rendering
// ---------------------------------------------------------------------------

/**
 * Measure indentation depth of a line (count of leading spaces/tabs, normalised
 * to units of 2 — a tab counts as 2).
 *
 * @param {string} line
 * @returns {number}
 */
function indentDepth(line) {
  let count = 0;
  for (const ch of line) {
    if (ch === " ") count++;
    else if (ch === "\t") count += 2;
    else break;
  }
  return Math.floor(count / 2);
}

/**
 * Render a group of list lines (ul or ol) into nested HTML, supporting
 * indentation-based nesting.  Empty lines are skipped.
 * Nested-list marker: renderList handles indented sub-items.
 *
 * @param {string[]} lines
 * @param {"ul"|"ol"} tag
 * @returns {string}
 */
function renderList(lines, tag) {
  // Build a flat array of {depth, text} entries.
  const RE = tag === "ul" ? UL_LINE_RE : OL_LINE_RE;
  const textIdx = tag === "ul" ? 2 : 3; // capture group index for item text
  const items = lines
    .filter(l => RE.test(l))
    .map(l => {
      const m = l.match(RE);
      return { depth: indentDepth(l), text: m[textIdx] };
    });
  if (items.length === 0) return "";

  /**
   * Recursively render items starting at `start` up to `end` (exclusive)
   * whose depth equals `depth` (sub-items go one level deeper).
   *
   * @param {number} start
   * @param {number} end
   * @param {number} depth
   * @returns {string}
   */
  function renderItems(start, end, depth) {
    let html = "";
    let i = start;
    while (i < end) {
      if (items[i].depth === depth) {
        // Look ahead for indented sub-items.
        let j = i + 1;
        while (j < end && items[j].depth > depth) j++;
        const liContent = inlineMarkdown(items[i].text);
        if (j > i + 1) {
          // Has sub-items — render the nested list.
          const subTag = tag; // keep same type for simplicity
          html += "<li>" + liContent + renderItems(i + 1, j, depth + 1) + "</li>";
        } else {
          html += "<li>" + liContent + "</li>";
        }
        i = j;
      } else if (items[i].depth > depth) {
        // Sub-list block starting here — wrap in a new list.
        let j = i;
        while (j < end && items[j].depth > depth) j++;
        html += "<" + tag + ">" + renderItems(i, j, depth + 1) + "</" + tag + ">";
        i = j;
      } else {
        // Depth less than expected — stop.
        break;
      }
    }
    return "<" + tag + ">" + html + "</" + tag + ">";
  }

  return renderItems(0, items.length, 0);
}

// ---------------------------------------------------------------------------
// Nested blockquote rendering
// ---------------------------------------------------------------------------

/**
 * Render a group of blockquote lines into nested HTML.
 * Each line starts with one or more '>' characters; multiple '>' = nesting.
 * Nested-blockquote marker: renderBlockquote handles >> nesting.
 *
 * @param {string[]} lines
 * @returns {string}
 */
function renderBlockquote(lines) {
  const relevant = lines.filter(l => BLOCKQUOTE_LINE_RE.test(l));
  if (relevant.length === 0) return "";

  /**
   * Strip exactly one level of '>' from each line and recurse for groups
   * that still start with '>'.
   *
   * @param {string[]} bqLines - lines already matched by BLOCKQUOTE_LINE_RE
   * @returns {string}
   */
  function buildBq(bqLines) {
    let inner = "";
    const stripped = bqLines.map(l => {
      const m = l.match(BLOCKQUOTE_LINE_RE);
      // m[1] = one or more '>'; m[2] = rest of line
      const remaining = m[1].length > 1 ? m[1].slice(1) + " " + m[2] : m[2];
      return remaining;
    });
    // Group consecutive nested lines vs plain lines.
    let i = 0;
    while (i < stripped.length) {
      if (BLOCKQUOTE_LINE_RE.test(stripped[i])) {
        // Collect run of nested-blockquote lines.
        let j = i;
        while (j < stripped.length && BLOCKQUOTE_LINE_RE.test(stripped[j])) j++;
        inner += buildBq(stripped.slice(i, j));
        i = j;
      } else {
        inner += inlineMarkdown(stripped[i]) + "<br>";
        i++;
      }
    }
    // Remove trailing <br>.
    inner = inner.replace(/<br>$/, "");
    return "<blockquote>" + inner + "</blockquote>";
  }

  return buildBq(relevant);
}

// ---------------------------------------------------------------------------
// Block processing
// ---------------------------------------------------------------------------

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

  // GAP D2: Horizontal rule — a single-line block consisting solely of rule chars.
  if (lines.length === 1 && HR_RE.test(lines[0].trim())) return "<hr>";

  // Heading: all lines must be headings for it to render as headings
  // (or just first line for single-line block).
  if (lines.length === 1) {
    const hm = lines[0].match(HEADING_RE);
    if (hm) {
      const level = Math.min(hm[1].length, 6);
      const tag = "h" + level;
      return "<" + tag + ">" + inlineMarkdown(hm[2]) + "</" + tag + ">";
    }
  }

  // Check whether every non-empty line is an unordered list item (possibly indented).
  if (lines.every(l => UL_LINE_RE.test(l) || l.trim() === "")) {
    return renderList(lines, "ul");
  }

  // Check whether every non-empty line is an ordered list item (possibly indented).
  if (lines.every(l => OL_LINE_RE.test(l) || l.trim() === "")) {
    return renderList(lines, "ol");
  }

  // D6: Blockquote — every non-empty line starts with '>'.
  if (lines.every(l => BLOCKQUOTE_LINE_RE.test(l) || l.trim() === "")) {
    return renderBlockquote(lines);
  }

  // D6: GFM table — line 0 is a pipe-delimited header row, line 1 is a delimiter row.
  // Accepted limitation: column alignment and nested-block-in-cell are NOT rendered.
  if (lines.length >= 2 && lines[0].includes("|") && TABLE_DELIM_RE.test(lines[1])) {
    /**
     * Split a table row on '|', tolerating leading/trailing pipes.
     * @param {string} row
     * @returns {string[]}
     */
    function splitRow(row) {
      let s = row.trim();
      if (s.startsWith("|")) s = s.slice(1);
      if (s.endsWith("|")) s = s.slice(0, -1);
      return s.split("|").map(c => c.trim());
    }
    const headers = splitRow(lines[0]);
    const bodyLines = lines.slice(2);
    const thCells = headers.map(h => "<th>" + inlineMarkdown(h) + "</th>").join("");
    const bodyRows = bodyLines
      .filter(l => l.includes("|"))
      .map(l => {
        const cells = splitRow(l);
        return "<tr>" + cells.map(c => "<td>" + inlineMarkdown(c) + "</td>").join("") + "</tr>";
      }).join("");
    return "<table class=\"table\"><thead><tr>" + thCells + "</tr></thead><tbody>" + bodyRows + "</tbody></table>";
  }

  // Mixed block: treat as a paragraph, joining with <br> for soft line breaks.
  const rendered = lines
    .map(l => {
      // If the line itself is a heading, render it standalone.
      const hm = l.match(HEADING_RE);
      if (hm) {
        const level = Math.min(hm[1].length, 6);
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
    // Allowlist scheme check (shared with the sanitizer). A denylist regex here
    // is bypassable with obfuscated schemes such as "java\tscript:"; isSafeHref
    // normalises the URL before testing the scheme.
    if (!isSafeHref(rawUrl)) {
      // Reject unsafe schemes — render as plain text.
      return linkText;
    }
    // External links get target + rel (including nofollow to match legacy); keep url HTML-escaped.
    const isExternal = /^https?:\/\//i.test(rawUrl.trim());
    const extras = isExternal ? ' target="_blank" rel="nofollow noopener noreferrer"' : "";
    return '<a href="' + url + '"' + extras + ">" + linkText + "</a>";
  });

  // Angle-bracket autolinks: <https://…> (post-escape form is &lt;…&gt;).
  // Matches the legacy marked.js gfm autolink behaviour.
  s = s.replace(/&lt;(https?:\/\/[^\s]+?)&gt;/g, (_m, url) => {
    // url is already HTML-escaped (& -> &amp;); restore for the scheme check only.
    const raw = url.replace(/&amp;/g, "&").trim().toLowerCase();
    if (/^javascript\s*:/i.test(raw) || /^data\s*:/i.test(raw)) {
      return "&lt;" + url + "&gt;";
    }
    return '<a href="' + url + '" target="_blank" rel="nofollow noopener noreferrer">' + url + "</a>";
  });

  // Restore inline code placeholders.
  inlineCodes.forEach((ic, idx) => {
    s = s.replace("\x00IC" + idx + "\x00", ic);
  });

  return s;
}
