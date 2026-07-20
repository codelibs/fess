// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bundled bootstrap theme's formatting/sanitizing
// utilities. These import and execute the real, shipped format.js. sanitizeHtml
// returns a DocumentFragment (not a string), so it is serialised through a
// detached <div> to inspect the resulting markup — exactly how callers append it.

import { readFileSync } from "node:fs";
import { fileURLToPath } from "node:url";
import { describe, it, expect } from "vitest";
import {
  escapeHtml,
  formatFileSize,
  formatDate,
  isSafeHref,
  sanitizeHtml,
  renderHighlightedSnippet,
  renderSnippetText,
} from "../../../../main/webapp/themes/bootstrap/assets/format.js";

/** Serialise the DocumentFragment sanitizeHtml() returns into an HTML string. */
const clean = (html) => {
  const host = document.createElement("div");
  host.appendChild(sanitizeHtml(html));
  return host.innerHTML;
};

describe("escapeHtml", () => {
  it("escapes all five HTML metacharacters", () => {
    expect(escapeHtml("<a href=\"x\">&'</a>")).toBe(
      "&lt;a href=&quot;x&quot;&gt;&amp;&#39;&lt;/a&gt;"
    );
  });

  it("returns empty string for null/undefined", () => {
    expect(escapeHtml(null)).toBe("");
    expect(escapeHtml(undefined)).toBe("");
  });
});

describe("formatFileSize", () => {
  it.each([
    [0, "0 B"],
    [1023, "1023 B"],
    [1024, "1.0 KB"],
    [1048576, "1.0 MB"],
  ])("formats %d bytes as %s", (bytes, expected) => {
    expect(formatFileSize(bytes)).toBe(expected);
  });

  it("returns empty string for invalid or negative input", () => {
    expect(formatFileSize(null)).toBe("");
    expect(formatFileSize("")).toBe("");
    expect(formatFileSize("abc")).toBe("");
    expect(formatFileSize(-1)).toBe("");
  });
});

describe("formatDate", () => {
  it("formats an ISO string as YYYY-MM-DD HH:MM in local time", () => {
    // Construct the expectation from the same Date the code uses so the test is
    // independent of the runner's timezone.
    const d = new Date("2026-07-17T09:05:00");
    const pad = (x) => String(x).padStart(2, "0");
    const expected =
      `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ` +
      `${pad(d.getHours())}:${pad(d.getMinutes())}`;
    expect(formatDate("2026-07-17T09:05:00")).toBe(expected);
  });

  it("returns empty string for empty or invalid input", () => {
    expect(formatDate("")).toBe("");
    expect(formatDate(null)).toBe("");
    expect(formatDate("not-a-date")).toBe("");
  });
});

describe("isSafeHref", () => {
  it.each(["https://example.com", "http://example.com", "mailto:a@b.com", "/relative/path", "#frag"])(
    "accepts %s",
    (href) => expect(isSafeHref(href)).toBe(true)
  );

  it.each([
    "javascript:alert(1)",
    "java\tscript:alert(1)", // tab-obfuscated scheme must still be rejected
    "data:text/html,<script>alert(1)</script>",
    "vbscript:msgbox(1)",
    "http://%", // malformed: new URL() throws, so the catch branch returns false
    "",
  ])("rejects %s", (href) => expect(isSafeHref(href)).toBe(false));

  it("rejects non-string input", () => {
    expect(isSafeHref(null)).toBe(false);
    expect(isSafeHref(undefined)).toBe(false);
  });
});

describe("sanitizeHtml: allowed markup (replaces test_formatJs_sanitizerAllowsHr)", () => {
  it("keeps <hr>", () => {
    expect(clean("<hr>")).toBe("<hr>");
  });

  it("keeps safe structural markup", () => {
    expect(clean("<p>text</p>")).toBe("<p>text</p>");
    expect(clean("<ul><li>a</li></ul>")).toBe("<ul><li>a</li></ul>");
  });

  it("unwraps a disallowed inline tag but keeps its text", () => {
    // <b> is not in ALLOWED_TAGS (only STRONG/EM are), so it is unwrapped.
    expect(clean("<p><b>bold</b></p>")).toBe("<p>bold</p>");
  });
});

describe("sanitizeHtml: DROP_WITH_CONTENT drops the whole subtree (replaces test_formatJs_sanitizerDropsRawTextElementsWithContent)", () => {
  const DROPPED = [
    "IFRAME", "NOEMBED", "NOFRAMES", "PLAINTEXT", "SCRIPT",
    "STYLE", "TEXTAREA", "TITLE", "XMP", "NOSCRIPT", "TEMPLATE",
  ];
  it.each(DROPPED)("%s: the element and its content are both removed", (tag) => {
    const t = tag.toLowerCase();
    const out = clean(`<div>KEEP<${t}>EVIL</${t}></div>`);
    expect(out).toContain("KEEP");
    expect(out).not.toContain("EVIL");
    expect(out.toLowerCase()).not.toContain(`<${t}`);
  });
});

describe("sanitizeHtml: object/embed are NOT dropped whole (replaces test_formatJs_sanitizerDoesNotDropObjectOrEmbedContent)", () => {
  it("preserves <object> fallback prose by unwrapping it", () => {
    const out = clean("<div><object>fallback prose</object></div>");
    expect(out).toContain("fallback prose");
  });

  it("does not leave an <embed> element (void, unwrapped to nothing)", () => {
    const out = clean('<div>KEEP<embed src="x"></div>');
    expect(out).toContain("KEEP");
    expect(out.toLowerCase()).not.toContain("<embed");
  });
});

describe("sanitizeHtml: attribute and scheme filtering", () => {
  it("strips event-handler attributes", () => {
    const out = clean('<img src="x" onerror="alert(1)">');
    expect(out).not.toContain("onerror");
  });

  it("drops a javascript: href", () => {
    const out = clean('<a href="javascript:alert(1)">x</a>');
    expect(out).not.toContain("javascript:");
    expect(out).toContain("x");
  });

  it("keeps a safe href and marks external links", () => {
    const out = clean('<a href="https://example.com">x</a>');
    expect(out).toContain('href="https://example.com"');
    expect(out).toContain('rel="nofollow noopener noreferrer"');
    expect(out).toContain('target="_blank"');
  });

  it("keeps a safe non-external href without adding target/rel", () => {
    // A relative link is safe but not external, so it gets neither target
    // nor rel (exercises the non-external branch of the href handler).
    expect(clean('<a href="/local/path">x</a>')).toBe('<a href="/local/path">x</a>');
  });

  it("drops a droppable child while unwrapping its disallowed parent", () => {
    // <b> unwraps; its comment child is removed rather than surfaced.
    expect(clean("<b>keep<!-- secret --></b>")).toBe("keep");
  });

  it("removes comment nodes", () => {
    expect(clean("<p>a</p><!-- secret -->")).toBe("<p>a</p>");
  });

  it("unwraps a disallowed element at the top level (no wrapper)", () => {
    // Exercises the fragment-level replace path: the top-level node is itself
    // disallowed and unwraps to its sanitized children.
    expect(clean("<b>bold</b>")).toBe("bold");
  });
});

describe("renderHighlightedSnippet: server snippet is parsed, not re-escaped", () => {
  it("keeps the highlight tags <strong>/<em>", () => {
    expect(renderHighlightedSnippet("a <strong>b</strong> c")).toBe("a <strong>b</strong> c");
    expect(renderHighlightedSnippet("<em>x</em>")).toBe("<em>x</em>");
  });

  it("unwraps any tag outside SNIPPET_TAGS", () => {
    expect(renderHighlightedSnippet("<p>x</p>")).toBe("x");
  });

  it("drops a raw-text element whole (no source leak)", () => {
    expect(renderHighlightedSnippet("<script>alert(1)</script>tail")).toBe("tail");
  });

  it("decodes server entities without double-escaping", () => {
    // The server sends a literal quote as the entity &#034;; parsing decodes it
    // back to " rather than painting the literal text &#034;.
    expect(renderHighlightedSnippet("&#034;q&#034;")).toBe('"q"');
  });

  it("returns empty string for empty input", () => {
    expect(renderHighlightedSnippet("")).toBe("");
    expect(renderHighlightedSnippet(null)).toBe("");
  });
});

describe("renderSnippetText: server snippet reduced to plain text", () => {
  it("strips markup and keeps the text", () => {
    expect(renderSnippetText("a <strong>b</strong> c")).toBe("a b c");
  });

  it("drops raw-text element content entirely", () => {
    expect(renderSnippetText("<script>x</script>after")).toBe("after");
  });

  it("decodes entities to their characters", () => {
    expect(renderSnippetText("AT&amp;T")).toBe("AT&T");
  });

  it("returns empty string for empty input", () => {
    expect(renderSnippetText("")).toBe("");
    expect(renderSnippetText(null)).toBe("");
  });
});

describe("shared-asset header comment is theme-neutral", () => {
  const read = (rel) =>
    readFileSync(fileURLToPath(new URL(rel, import.meta.url)), "utf8").split("\n");
  it("format.js line 2 names no specific theme", () => {
    const line2 = read("../../../../main/webapp/themes/bootstrap/assets/format.js")[1];
    expect(line2).toContain("Fess static theme SPA");
    expect(line2).not.toContain("bootstrap");
  });
  it("markdown.js line 2 names no specific theme", () => {
    const line2 = read("../../../../main/webapp/themes/bootstrap/assets/markdown.js")[1];
    expect(line2).toContain("Fess static theme SPA");
    expect(line2).not.toContain("bootstrap");
  });
});
