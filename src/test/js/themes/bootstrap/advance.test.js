// SPDX-License-Identifier: Apache-2.0
// Executable tests for advance.js — the advanced-search query builder and view.
//
// advance.js imports search.js and router.js at module load; both are mocked to
// inert stubs so importing the real, unmodified asset stays side-effect-free and
// the pure query-composition logic can be exercised directly. api.js is mocked so
// getConfig() returns a controllable config for the attach() DOM flow.
//
// The real i18n.t() returns its key unchanged (messages are empty without init),
// so option labels are the raw i18n keys — irrelevant to these query tests.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/search.js", () => ({
  attachSuggest: () => {},
  disableSubmitBriefly: () => {},
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  navigate: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => ({})),
}));

import {
  quoteIfNeeded,
  tokenize,
  compose,
  buildFiletypeOptions,
  TIME_RANGE_QUERY,
  attach,
} from "../../../../main/webapp/themes/bootstrap/assets/advance.js";
import { navigate } from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import { resetDom, setLocation } from "../../helpers/dom.js";

// ---------------------------------------------------------------------------
// compose(parts) — the query builder
// ---------------------------------------------------------------------------

describe("compose", () => {
  it("none-words → one NOT per whitespace token", () => {
    expect(compose({ none: "foo bar" })).toBe("NOT foo NOT bar");
  });

  it("any-words with >1 token → parenthesised OR group", () => {
    expect(compose({ any: "a b c" })).toBe("(a OR b OR c)");
  });

  it("any-words with a single token → bare token, no parens/OR", () => {
    expect(compose({ any: "solo" })).toBe("solo");
  });

  it("exact phrase → double-quoted", () => {
    expect(compose({ exact: "fiscal year" })).toBe('"fiscal year"');
  });

  it("filetype → filetype:\"<canonical value>\" (word, never msword)", () => {
    expect(compose({ filetype: "word" })).toBe('filetype:"word"');
    expect(compose({ filetype: "excel" })).toBe('filetype:"excel"');
    expect(compose({ filetype: "powerpoint" })).toBe('filetype:"powerpoint"');
  });

  it("occt prefix with an empty query → empty string (no bare allintitle:)", () => {
    expect(compose({ occt: "allintitle" })).toBe("");
    expect(compose({ occt: "allinurl" })).toBe("");
  });

  it("occt prefix is applied only when there is a query body", () => {
    expect(compose({ all: "x", occt: "allintitle" })).toBe("allintitle:x");
    expect(compose({ all: "x", occt: "allinurl" })).toBe("allinurl:x");
  });

  it("assembles every part in server order with the allintitle: prefix", () => {
    expect(
      compose({
        all: "annual report",
        exact: "fiscal year",
        any: "2023 2024",
        none: "draft old",
        site: "example.com",
        filetype: "word",
        occt: "allintitle",
      })
    ).toBe(
      'allintitle:annual report "fiscal year" (2023 OR 2024) NOT draft NOT old site:example.com filetype:"word"'
    );
  });

  it("empty parts → empty string", () => {
    expect(compose({})).toBe("");
  });
});

// ---------------------------------------------------------------------------
// quoteIfNeeded(s)
// ---------------------------------------------------------------------------

describe("quoteIfNeeded", () => {
  it("escapes inner double-quotes with a backslash and wraps the whole", () => {
    // Actual characters: "he said \"hi\""  (each inner " prefixed by one backslash)
    expect(quoteIfNeeded('he said "hi"')).toBe('"he said \\"hi\\""');
  });

  it("passes an already-quoted phrase through unchanged", () => {
    expect(quoteIfNeeded('"already quoted"')).toBe('"already quoted"');
  });

  it("returns empty string for empty / whitespace-only input", () => {
    expect(quoteIfNeeded("")).toBe("");
    expect(quoteIfNeeded("   ")).toBe("");
  });

  it("quotes a plain phrase", () => {
    expect(quoteIfNeeded("plain")).toBe('"plain"');
  });
});

// ---------------------------------------------------------------------------
// tokenize(s)
// ---------------------------------------------------------------------------

describe("tokenize", () => {
  it("splits on whitespace and keeps quoted phrases whole (quotes retained)", () => {
    expect(tokenize('foo "bar baz" qux')).toEqual(["foo", '"bar baz"', "qux"]);
  });

  it("collapses runs of whitespace and trims", () => {
    expect(tokenize("  a   b  ")).toEqual(["a", "b"]);
  });

  it("keeps an unterminated quote as a single token", () => {
    expect(tokenize('"unterminated')).toEqual(['"unterminated']);
  });

  it("returns an empty array for an empty string", () => {
    expect(tokenize("")).toEqual([]);
  });
});

// ---------------------------------------------------------------------------
// buildFiletypeOptions(serverConfig)
// ---------------------------------------------------------------------------

describe("buildFiletypeOptions", () => {
  const canonicalValues = (opts) => opts.map((o) => o.value);

  it("falls back to the canonical list when config is null/empty/empty-array", () => {
    for (const cfg of [null, {}, { filetype_options: [] }]) {
      const opts = buildFiletypeOptions(cfg);
      expect(opts[0]).toEqual({ value: "", labelKey: "advance.any_filetype" });
      expect(canonicalValues(opts)).toEqual(
        expect.arrayContaining(["word", "excel", "powerpoint", "pdf", "html", "txt"])
      );
    }
  });

  it("prefixes a server-supplied list with the empty-value option", () => {
    const opts = buildFiletypeOptions({ filetype_options: [{ value: "csv", label: "CSV" }] });
    expect(opts).toHaveLength(2);
    expect(opts[0]).toEqual({ value: "", labelKey: "advance.any_filetype" });
    expect(opts[1].value).toBe("csv");
    expect(opts[1].label).toBe("CSV");
  });
});

// ---------------------------------------------------------------------------
// TIME_RANGE_QUERY constant
// ---------------------------------------------------------------------------

describe("TIME_RANGE_QUERY", () => {
  it("maps each range value to the server's date-math fragment", () => {
    expect(TIME_RANGE_QUERY).toEqual({
      "1day": "timestamp:[now-1d/d TO *]",
      "1week": "timestamp:[now-1w/d TO *]",
      "1month": "timestamp:[now-1M/d TO *]",
      "1year": "timestamp:[now-1y/d TO *]",
    });
  });
});

// ---------------------------------------------------------------------------
// attach() submit flow — timestamp date-math + unrelated-param preservation
// ---------------------------------------------------------------------------

describe("attach: submit builds the /search URL", () => {
  beforeEach(() => {
    document.body.innerHTML = '<div id="advance-view"></div>';
  });
  afterEach(() => {
    navigate.mockClear();
    setLocation("/");
    resetDom();
  });

  it("does nothing when #advance-view is absent", () => {
    document.body.innerHTML = "";
    expect(() => attach()).not.toThrow();
    expect(navigate).not.toHaveBeenCalled();
  });

  it("preserves unrelated params, drops advance-owned ones, appends timestamp date-math", () => {
    // theme/keepme are unrelated (preserved); start is advance-owned (dropped).
    setLocation("/advance?theme=custom&keepme=1&start=50");
    attach();

    document.getElementById("adv-all").value = "hello";
    document.getElementById("adv-time").value = "1month";

    const form = document.getElementById("advance-form");
    form.dispatchEvent(new window.Event("submit", { bubbles: true, cancelable: true }));

    expect(navigate).toHaveBeenCalledTimes(1);
    const target = navigate.mock.calls[0][0];
    expect(target.startsWith("/search?")).toBe(true);

    const p = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    // timestamp date-math appended to the composed q
    expect(p.get("q")).toBe("hello timestamp:[now-1M/d TO *]");
    // unrelated params survive
    expect(p.get("theme")).toBe("custom");
    expect(p.get("keepme")).toBe("1");
    // advance-owned param removed
    expect(p.has("start")).toBe(false);
  });
});
