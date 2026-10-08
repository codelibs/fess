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
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  navigate: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => ({})),
}));

import {
  quoteIfNeeded,
  quoteFieldValue,
  unquoteFieldValue,
  tokenize,
  compose,
  buildFiletypeOptions,
  TIME_RANGE_QUERY,
  attach,
} from "../../../../main/webapp/themes/bootstrap/assets/advance.js";
import { navigate } from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import { getConfig } from "../../../../main/webapp/themes/bootstrap/assets/api.js";
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

  it("owner / last_modifier → quoted field terms after filetype", () => {
    expect(compose({ owner: "alice" })).toBe('owner:"alice"');
    expect(compose({ last_modifier: " Taro Yamada " })).toBe('last_modifier:"Taro Yamada"');
    expect(compose({ all: "x", filetype: "word", owner: "CORP alice", last_modifier: "bob" }))
      .toBe('x filetype:"word" owner:"CORP alice" last_modifier:"bob"');
  });

  it("owner / last_modifier escape inner double quotes and backslashes", () => {
    // Actual characters: CORP\alice → owner:"CORP\\alice"; say "hi" → last_modifier:"say \"hi\""
    expect(compose({ owner: "CORP\\alice" })).toBe('owner:"CORP\\\\alice"');
    expect(compose({ last_modifier: 'say "hi"' })).toBe('last_modifier:"say \\"hi\\""');
  });

  it("whitespace-only owner / last_modifier → omitted", () => {
    expect(compose({ owner: "  ", last_modifier: "" })).toBe("");
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
// quoteFieldValue(s) / unquoteFieldValue(s)
// ---------------------------------------------------------------------------

describe("quoteFieldValue / unquoteFieldValue", () => {
  it("quotes and escapes backslash before quote", () => {
    expect(quoteFieldValue('a\\b "c"')).toBe('"a\\\\b \\"c\\""');
  });

  it("round-trips through unquoteFieldValue", () => {
    for (const v of ["alice", "CORP alice", 'say "hi"', "CORP\\alice", "trailing\\"]) {
      expect(unquoteFieldValue(quoteFieldValue(v))).toBe(v);
    }
  });

  it("returns an unquoted value unchanged", () => {
    expect(unquoteFieldValue("alice")).toBe("alice");
    expect(unquoteFieldValue('"open')).toBe('"open');
    expect(unquoteFieldValue("")).toBe("");
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

  it("keeps a field:\"quoted value\" whole, including spaces and escaped quotes", () => {
    expect(tokenize('a owner:"CORP alice" b')).toEqual(["a", 'owner:"CORP alice"', "b"]);
    expect(tokenize('last_modifier:"say \\"hi\\" now" x'))
      .toEqual(['last_modifier:"say \\"hi\\" now"', "x"]);
    expect(tokenize('owner:"open ended')).toEqual(['owner:"open ended']);
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

  it("labels each label checkbox with the label name, not its value", () => {
    getConfig.mockReturnValue({ label_options: [{ value: "dept1", name: "Planning" }, { value: "dept2" }] });
    try {
      attach();
    } finally {
      getConfig.mockReturnValue({});
    }
    expect(document.querySelector('label[for="adv-label-dept1"]').textContent).toBe("Planning");
    // A label without a name falls back to its value.
    expect(document.querySelector('label[for="adv-label-dept2"]').textContent).toBe("dept2");
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
    expect(target.startsWith("search?")).toBe(true);

    const p = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    // timestamp date-math appended to the composed q
    expect(p.get("q")).toBe("hello timestamp:[now-1M/d TO *]");
    // unrelated params survive
    expect(p.get("theme")).toBe("custom");
    expect(p.get("keepme")).toBe("1");
    // advance-owned param removed
    expect(p.has("start")).toBe(false);
    // the submit button is left enabled: it is the form's default button, and the form is rebuilt
    // (not reloaded) when the user comes back to it
    expect(form.querySelector('button[type="submit"]').disabled).toBe(false);
  });

  it("renders owner / last-modifier inputs and composes them into q", () => {
    attach();
    expect(document.querySelector('label[for="adv-owner"]').textContent).toBe("advance.owner");
    expect(document.querySelector('label[for="adv-last-modifier"]').textContent).toBe("advance.last_modifier");

    document.getElementById("adv-all").value = "report";
    document.getElementById("adv-owner").value = "CORP alice";
    document.getElementById("adv-last-modifier").value = 'Taro "T" Yamada';
    document.getElementById("advance-form")
      .dispatchEvent(new window.Event("submit", { bubbles: true, cancelable: true }));

    const target = navigate.mock.calls[0][0];
    const p = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    expect(p.get("q")).toBe('report owner:"CORP alice" last_modifier:"Taro \\"T\\" Yamada"');
  });

  it("re-populates owner / last-modifier from an existing q (round trip)", () => {
    const q = 'report filetype:"word" owner:"CORP alice" last_modifier:"Taro \\"T\\" Yamada"';
    setLocation("/advance?q=" + encodeURIComponent(q));
    attach();
    expect(document.getElementById("adv-owner").value).toBe("CORP alice");
    expect(document.getElementById("adv-last-modifier").value).toBe('Taro "T" Yamada');
    expect(document.getElementById("adv-all").value).toBe("report");

    document.getElementById("advance-form")
      .dispatchEvent(new window.Event("submit", { bubbles: true, cancelable: true }));
    const target = navigate.mock.calls[0][0];
    const p = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    expect(p.get("q")).toBe(q);
  });

  it("parses an unquoted owner:value and keeps a second owner term in all-words", () => {
    setLocation("/advance?q=" + encodeURIComponent('owner:alice owner:"bob"'));
    attach();
    expect(document.getElementById("adv-owner").value).toBe("alice");
    expect(document.getElementById("adv-all").value).toBe('owner:"bob"');
  });
});
