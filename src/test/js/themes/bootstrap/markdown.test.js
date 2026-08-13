// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bundled bootstrap theme's Markdown renderer.
// These import and execute the real, shipped markdown.js (which in turn imports
// format.js) rather than string-matching its source. parseMarkdown returns an
// HTML string, so no DOM serialisation is needed here — the pipeline through
// sanitizeHtml is exercised separately in pipeline.test.js.

import { describe, it, expect } from "vitest";
import { parseMarkdown } from "../../../../main/webapp/themes/bootstrap/assets/markdown.js";

describe("parseMarkdown: ATX headings h1-h6 (replaces test_markdownJs_h1ThroughH6)", () => {
  it.each([
    ["#", "h1"],
    ["##", "h2"],
    ["###", "h3"],
    ["####", "h4"],
    ["#####", "h5"],
    ["######", "h6"],
  ])("%s renders as <%s>", (hashes, tag) => {
    expect(parseMarkdown(`${hashes} Title`)).toBe(`<${tag}>Title</${tag}>`);
  });

  it("clamps to h6: seven hashes are not a heading", () => {
    // HEADING_RE only accepts 1-6 hashes followed by a space, so a 7-hash line
    // is a plain paragraph, never <h7>.
    const out = parseMarkdown("####### Title");
    expect(out).not.toMatch(/<h7/);
    expect(out).toBe("<p>####### Title</p>");
  });
});

describe("parseMarkdown: horizontal rules and autolinks (replaces test_markdownJs_horizontalRuleAndAutolink)", () => {
  it.each(["---", "***", "___"])("%s renders as <hr>", (rule) => {
    expect(parseMarkdown(rule)).toBe("<hr>");
  });

  it("wraps a bare <https://...> as an external anchor", () => {
    expect(parseMarkdown("<https://example.com/a>")).toBe(
      '<p><a href="https://example.com/a" target="_blank" rel="nofollow noopener noreferrer">https://example.com/a</a></p>'
    );
  });

  it("does not autolink a javascript: angle-bracket URL", () => {
    const out = parseMarkdown("<javascript:alert(1)>");
    expect(out).not.toContain("<a ");
  });
});

describe("parseMarkdown: tables and blockquotes (replaces test_markdownTablesBlockquote)", () => {
  it("renders a GFM table with thead and tbody", () => {
    const out = parseMarkdown("| a | b |\n|---|---|\n| 1 | 2 |");
    expect(out).toBe(
      '<table class="table"><thead><tr><th>a</th><th>b</th></tr></thead>' +
        "<tbody><tr><td>1</td><td>2</td></tr></tbody></table>"
    );
  });

  it("renders a simple blockquote", () => {
    expect(parseMarkdown("> quoted")).toBe("<blockquote>quoted</blockquote>");
  });
});

describe("parseMarkdown: nested lists and blockquotes (replaces test_markdownJs_nestedListAndBlockquoteHandling)", () => {
  it("nests an indented unordered sub-list inside its parent item", () => {
    expect(parseMarkdown("- a\n  - b")).toBe("<ul><li>a<ul><li>b</li></ul></li></ul>");
  });

  it("nests an ordered sub-list", () => {
    expect(parseMarkdown("1. a\n  1. b")).toBe("<ol><li>a<ol><li>b</li></ol></li></ol>");
  });

  it("nests a blockquote via a doubled marker", () => {
    expect(parseMarkdown("> outer\n>> inner")).toBe(
      "<blockquote>outer<br><blockquote>inner</blockquote></blockquote>"
    );
  });

  it("still nests when an item skips an indent level", () => {
    // A sub-item indented past the next level exercises the deep-jump branch of
    // the list renderer; b must still render nested under a.
    const out = parseMarkdown("- a\n    - b");
    expect(out.startsWith("<ul><li>a")).toBe(true);
    expect(out).toContain("<li>b</li>");
  });
});

describe("parseMarkdown: inline constructs", () => {
  it("renders **bold** and *italic*", () => {
    expect(parseMarkdown("**b** and *i*")).toBe("<p><strong>b</strong> and <em>i</em></p>");
  });

  it("renders __bold__ and _italic_ underscore variants", () => {
    expect(parseMarkdown("__b__ and _i_")).toBe("<p><strong>b</strong> and <em>i</em></p>");
  });

  it("renders inline `code` without applying emphasis inside it", () => {
    expect(parseMarkdown("use `a**b**c` here")).toBe("<p>use <code>a**b**c</code> here</p>");
  });

  it("renders a fenced code block, escaping its contents", () => {
    expect(parseMarkdown("```\n<b>&\n```")).toBe("<pre><code>&lt;b&gt;&amp;</code></pre>");
  });

  it("renders a heading line embedded in a multi-line paragraph block", () => {
    // A block whose lines are not uniformly headings/list/quote/table falls to
    // the mixed-paragraph path, which still renders an individual heading line.
    const out = parseMarkdown("intro\n## Mid\noutro");
    expect(out).toContain("<h2>Mid</h2>");
  });

  it("keeps a safe [text](https) link and marks it external", () => {
    expect(parseMarkdown("[t](https://example.com)")).toBe(
      '<p><a href="https://example.com" target="_blank" rel="nofollow noopener noreferrer">t</a></p>'
    );
  });

  it("drops a javascript: link, leaving only its text", () => {
    const out = parseMarkdown("[t](javascript:alert(1))");
    expect(out).not.toContain("<a ");
    expect(out).toContain("t");
  });

  it("escapes raw HTML so no live tag is emitted", () => {
    const out = parseMarkdown("<img src=x onerror=alert(1)>");
    expect(out).not.toContain("<img");
    expect(out).toContain("&lt;img");
  });

  it("returns empty string for empty or non-string input", () => {
    expect(parseMarkdown("")).toBe("");
    expect(parseMarkdown(null)).toBe("");
    expect(parseMarkdown(undefined)).toBe("");
  });
});

describe("parseMarkdown: code content is restored literally", () => {
  // String.prototype.replace() reads $&, $`, $' and $n in a *string* replacement
  // as substitution patterns. escapeHtml() rewrites ' to &#39;, so an apostrophe
  // after a '$' reaches the restore step as "$&#39;", where $& expands to the
  // matched text -- the internal placeholder itself. Passing a replacer function
  // instead is what keeps these verbatim, so each case below is a live check of
  // that, not of the escaping.
  it.each([
    ["a$&b", "a$&amp;b"],
    ["a$'b", "a$&#39;b"],
    ["a$`b", "a$`b"],
    ["a$1b", "a$1b"],
  ])("keeps %s verbatim inside a fenced block", (code, expected) => {
    expect(parseMarkdown("```\n" + code + "\n```")).toBe("<pre><code>" + expected + "</code></pre>");
  });

  // No backtick row here: a single-backtick span closes at the first inner
  // backtick, so "$`" is not expressible inline.
  it.each([
    ["a$&b", "a$&amp;b"],
    ["a$'b", "a$&#39;b"],
    ["a$1b", "a$1b"],
  ])("keeps %s verbatim inside an inline code span", (code, expected) => {
    expect(parseMarkdown("x `" + code + "` y")).toBe("<p>x <code>" + expected + "</code> y</p>");
  });
});

describe("parseMarkdown: a fence never carries its neighbours out unescaped", () => {
  // splitBlocks() splits on blank lines, so without the placeholder-boundary
  // split a fence not followed by a blank line shares a block with the text
  // after it, and processBlock() returned that whole block early -- reaching the
  // output without ever passing through escapeHtml().
  it("escapes text following a fence with no blank line between them", () => {
    expect(parseMarkdown("```\nc\n```\n<script>alert(1)</script>")).toBe(
      "<pre><code>c</code></pre>\n<p>&lt;script&gt;alert(1)&lt;/script&gt;</p>"
    );
  });

  it("escapes text preceding a fence with no blank line between them", () => {
    expect(parseMarkdown("<b>lead</b>\n```\nc\n```")).toBe(
      "<p>&lt;b&gt;lead&lt;/b&gt;</p>\n<pre><code>c</code></pre>"
    );
  });

  it("renders two fences with no blank line between them as sibling blocks", () => {
    expect(parseMarkdown("```\na\n```\n```\nb\n```")).toBe(
      "<pre><code>a</code></pre>\n<pre><code>b</code></pre>"
    );
  });

  it("strips NUL so input cannot forge a code-block placeholder", () => {
    // Placeholders are the only strings that bypass escaping; a forged one used
    // to reproduce the raw passthrough with no fence present at all.
    expect(parseMarkdown("\u0000CODEBLOCK0\u0000<script>alert(1)</script>")).toBe(
      "<p>CODEBLOCK0&lt;script&gt;alert(1)&lt;/script&gt;</p>"
    );
  });
});
