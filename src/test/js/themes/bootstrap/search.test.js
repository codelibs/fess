// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bootstrap theme's search.js. api.js and router.js are
// mocked so getConfig/get/isAuthenticated and navigate() are controllable; i18n.js
// and format.js run for real. The real i18n.t() returns its key unchanged (messages
// are empty without init), so assertions match exact i18n keys.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom, mountBody, setLocation } from "../../helpers/dom.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => null),
  get: vi.fn(async () => ({})),
  post: vi.fn(async () => ({})),
  isAuthenticated: vi.fn(() => false),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  navigate: vi.fn(),
}));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import { navigate } from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import { formatFileSize } from "../../../../main/webapp/themes/bootstrap/assets/format.js";
import {
  safeHref,
  buildGoUrl,
  el,
  buildResultCard,
  renderPopularWords,
  disableSubmitBriefly,
  clearSearchState,
  runFromUrl,
  runSearch,
  renderSearchOptions,
  initSearchOptions,
  attachSuggest,
  syncSearchInputs,
  refresh,
  attach,
  _state,
  initialNum,
  forgetNum,
  ensureOsddLink,
} from "../../../../main/webapp/themes/bootstrap/assets/search.js";

beforeEach(() => {
  resetDom();
  vi.clearAllMocks();
  sessionStorage.clear();
  api.getConfig.mockReturnValue(null);
  clearSearchState();
});
afterEach(() => setLocation("/"));

describe("buildGoUrl", () => {
  it("builds a /go/ URL with rt, docId, queryId and order", () => {
    expect(buildGoUrl("https://ex.com/d", "d1", "q1", 3, 1700000000000))
      .toBe("go/?rt=1700000000000&docId=d1&queryId=q1&order=3");
  });

  it("appends an encoded &hash= for a #fragment URL", () => {
    expect(buildGoUrl("https://ex.com/d#sec-2", "d1", "q1", 3, 1700000000000))
      .toBe("go/?rt=1700000000000&docId=d1&queryId=q1&order=3&hash=%23sec-2");
  });

  it("returns # for javascript: and data: schemes", () => {
    expect(buildGoUrl("javascript:alert(1)", "d1", "q1", 3, 1)).toBe("#");
    expect(buildGoUrl("data:text/html,x", "d1", "q1", 3, 1)).toBe("#");
  });

  it("builds a /go/ URL for file:, smb: and s3: (file-system crawl results)", () => {
    expect(buildGoUrl("file:///data/report.pdf", "d1", "q1", 2, 1700000000000))
      .toBe("go/?rt=1700000000000&docId=d1&queryId=q1&order=2");
    expect(buildGoUrl("smb://host/share/file.docx", "d2", "q2", 1, 1700000000000))
      .toBe("go/?rt=1700000000000&docId=d2&queryId=q2&order=1");
    expect(buildGoUrl("s3://bucket/key.txt", "d3", "q3", 1, 1700000000000))
      .toBe("go/?rt=1700000000000&docId=d3&queryId=q3&order=1");
  });

  it("returns # for empty, null and non-string originalUrl", () => {
    expect(buildGoUrl("", "d1", "q1", 3, 1)).toBe("#");
    expect(buildGoUrl(null, "d1", "q1", 3, 1)).toBe("#");
    expect(buildGoUrl(42, "d1", "q1", 3, 1)).toBe("#");
  });

  it("percent-encodes docId and queryId with special characters", () => {
    expect(buildGoUrl("https://ex.com/d", "a b&c", "q 1", 3, 1))
      .toBe("go/?rt=1&docId=a%20b%26c&queryId=q%201&order=3");
  });

  it("defaults order to 0 when omitted", () => {
    expect(buildGoUrl("https://ex.com/d", "d1", "q1", undefined, 1))
      .toBe("go/?rt=1&docId=d1&queryId=q1&order=0");
  });
});

describe("ensureOsddLink", () => {
  const osddLinks = () => document.head.querySelectorAll('link[rel="search"]');

  it("adds the OpenSearch description link when features.osdd_link is on", () => {
    api.getConfig.mockReturnValue({ site_name: "Site", features: { osdd_link: true } });
    ensureOsddLink();
    ensureOsddLink();
    expect(osddLinks().length).toBe(1);
    const link = osddLinks()[0];
    expect(link.getAttribute("href")).toBe("osdd");
    expect(link.getAttribute("type")).toBe("application/opensearchdescription+xml");
    expect(link.getAttribute("title")).toBe("Site");
  });

  it("adds no link when the server does not serve the OSDD (JSP osddLink parity)", () => {
    api.getConfig.mockReturnValue({ features: { osdd_link: false } });
    ensureOsddLink();
    api.getConfig.mockReturnValue({ features: {} });
    ensureOsddLink();
    api.getConfig.mockReturnValue(null);
    ensureOsddLink();
    expect(osddLinks().length).toBe(0);
  });
});

describe("safeHref", () => {
  it("returns the URL for http/https/ftp/ftps schemes", () => {
    expect(safeHref("https://x.com")).toBe("https://x.com");
    expect(safeHref("http://x.com")).toBe("http://x.com");
    expect(safeHref("ftp://x.com")).toBe("ftp://x.com");
    expect(safeHref("ftps://x.com")).toBe("ftps://x.com");
  });

  it("returns the URL for file/smb/smb1/storage/s3/gcs schemes (file-system crawls)", () => {
    expect(safeHref("file:///data/report.pdf")).toBe("file:///data/report.pdf");
    expect(safeHref("smb://host/share/f.docx")).toBe("smb://host/share/f.docx");
    expect(safeHref("smb1://host/share/f.docx")).toBe("smb1://host/share/f.docx");
    expect(safeHref("storage://area/obj")).toBe("storage://area/obj");
    expect(safeHref("s3://bucket/key")).toBe("s3://bucket/key");
    expect(safeHref("gcs://bucket/key")).toBe("gcs://bucket/key");
  });

  it("returns relative and fragment URLs unchanged (resolve to the page scheme)", () => {
    expect(safeHref("/relative")).toBe("/relative");
    expect(safeHref("relative")).toBe("relative");
    expect(safeHref("#frag")).toBe("#frag");
  });

  it("returns # for mailto: and tel: (not in the search.js allowlist)", () => {
    expect(safeHref("mailto:a@b.com")).toBe("#");
    expect(safeHref("tel:12345")).toBe("#");
  });

  it("returns # for javascript:, data: and vbscript: schemes", () => {
    expect(safeHref("javascript:alert(1)")).toBe("#");
    expect(safeHref("data:text/html,x")).toBe("#");
    expect(safeHref("vbscript:msgbox")).toBe("#");
  });

  it("returns # for a malformed URL, empty/null and non-string input", () => {
    expect(safeHref("http://[bad")).toBe("#");
    expect(safeHref("")).toBe("#");
    expect(safeHref(null)).toBe("#");
    expect(safeHref(42)).toBe("#");
  });
});

describe("el", () => {
  it("creates a detached element with className, text, attrs and dataset", () => {
    const node = el("div", {
      className: "c1 c2",
      text: "hello",
      attrs: { id: "x", "data-uri": "u" },
      dataset: { docId: "d1" },
    });
    expect(node.tagName).toBe("DIV");
    expect(node.className).toBe("c1 c2");
    expect(node.textContent).toBe("hello");
    expect(node.getAttribute("id")).toBe("x");
    expect(node.getAttribute("data-uri")).toBe("u");
    expect(node.dataset.docId).toBe("d1");
    expect(node.parentNode).toBeNull();
  });

  it("returns a bare element when no opts are given", () => {
    const node = el("span");
    expect(node.tagName).toBe("SPAN");
    expect(node.className).toBe("");
    expect(node.textContent).toBe("");
  });
});

describe("buildResultCard", () => {
  it("builds a detached li#result{order-1} with docId/queryId dataset and title/cite", () => {
    api.getConfig.mockReturnValue({ features: {} });
    _state.requestedTime = 1700000000000;
    const li = buildResultCard(
      { doc_id: "d1", title: "Hello", url: "https://ex.com/p" }, "q1", 1);

    expect(li.parentNode).toBeNull();
    expect(li.tagName).toBe("LI");
    expect(li.id).toBe("result0");
    expect(li.dataset.docId).toBe("d1");
    expect(li.dataset.queryId).toBe("q1");

    const a = li.querySelector("h3 a");
    expect(a.textContent).toBe("Hello");
    expect(a.getAttribute("href"))
      .toBe("go/?rt=1700000000000&docId=d1&queryId=q1&order=0");
    // JSP parity: /go/ order is the same 0-based value as data-order.
    expect(a.getAttribute("data-order")).toBe("0");
    expect(li.querySelector("cite").textContent).toBe("https://ex.com/p");
    // No thumbnail / cache / similar for a minimal doc.
    expect(li.querySelector("img.thumbnail")).toBeNull();
    expect(li.querySelector("a.cache")).toBeNull();
    expect(li.querySelector("a.similar")).toBeNull();
    // No content_title/description/digest → empty description.
    expect(li.querySelector(".description").textContent).toBe("");
  });

  it("tolerates a null config (features default to {})", () => {
    api.getConfig.mockReturnValue(null);
    expect(() => buildResultCard({ doc_id: "d", title: "T", url: "https://e.com" }, "q", 1))
      .not.toThrow();
  });

  it("renders content_title as highlighted markup via innerHTML", () => {
    api.getConfig.mockReturnValue({ features: {} });
    const li = buildResultCard(
      { doc_id: "d2", content_title: "A <strong>hi</strong> B", url: "https://e.com" }, "q", 2);
    const a = li.querySelector("h3 a");
    expect(a.querySelector("strong")).not.toBeNull();
    expect(a.innerHTML).toContain("<strong>hi</strong>");
    expect(a.textContent).toBe("A hi B");
  });

  it("renders a thumbnail only when features.thumbnail_enabled and d.thumbnail are set", () => {
    api.getConfig.mockReturnValue({ features: { thumbnail_enabled: true } });
    const li = buildResultCard(
      { doc_id: "d2", title: "T", url: "https://e.com", thumbnail: "y" }, "q2", 2);
    const img = li.querySelector("img.thumbnail");
    expect(img).not.toBeNull();
    expect(img.getAttribute("src")).toBe("thumbnail/?docId=d2&queryId=q2");
  });

  it("omits the thumbnail when the feature is off even if d.thumbnail is set", () => {
    api.getConfig.mockReturnValue({ features: {} });
    const li = buildResultCard(
      { doc_id: "d2", title: "T", url: "https://e.com", thumbnail: "y" }, "q2", 2);
    expect(li.querySelector("img.thumbnail")).toBeNull();
  });

  it("renders a cache link when has_cache is truthy", () => {
    api.getConfig.mockReturnValue({ features: {} });
    const li = buildResultCard(
      { doc_id: "d3", title: "T", url: "https://e.com", has_cache: "true" }, "q", 1);
    const cache = li.querySelector("a.cache");
    expect(cache).not.toBeNull();
    expect(cache.getAttribute("href")).toMatch(/^cache\/\?docId=d3/);
  });

  it("shows the view count between the size and the cache link while search logging is on", () => {
    api.getConfig.mockReturnValue({ features: { search_log_enabled: true } });
    const li = buildResultCard(
      { doc_id: "d5", title: "T", url: "https://e.com", content_length: 2048, click_count: 7, has_cache: "true" }, "q", 1);
    const text = li.querySelector(".info").textContent;
    const size = text.indexOf(formatFileSize(2048));
    const count = text.indexOf("result.click_count");
    const cache = text.indexOf("result.cache");
    expect(size).toBeGreaterThanOrEqual(0);
    expect(count).toBeGreaterThan(size);
    expect(cache).toBeGreaterThan(count);
  });

  it("omits the view count when search logging is off or nothing was clicked", () => {
    const infoText = (doc) => buildResultCard({ title: "T", url: "https://e.com", ...doc }, "q", 1)
      .querySelector(".info").textContent;
    api.getConfig.mockReturnValue({ features: { search_log_enabled: false } });
    expect(infoText({ doc_id: "a", click_count: 7 })).not.toContain("result.click_count");
    api.getConfig.mockReturnValue({ features: { search_log_enabled: true } });
    expect(infoText({ doc_id: "b", click_count: 0 })).not.toContain("result.click_count");
    expect(infoText({ doc_id: "c" })).not.toContain("result.click_count");
  });

  it("renders a similar link only when similar_docs_count > 1", () => {
    api.getConfig.mockReturnValue({ features: {} });
    const many = buildResultCard(
      { doc_id: "d4", title: "T", url: "https://e.com", similar_docs_count: 3 }, "q", 1);
    expect(many.querySelector("a.similar")).not.toBeNull();
    const one = buildResultCard(
      { doc_id: "d5", title: "T", url: "https://e.com", similar_docs_count: 1 }, "q", 1);
    expect(one.querySelector("a.similar")).toBeNull();
  });

  it("escapes a raw digest rather than dropping it (angle-bracket address preserved)", () => {
    api.getConfig.mockReturnValue({ features: {} });
    const li = buildResultCard(
      { doc_id: "d6", title: "T", url: "https://e.com", digest: "Michael <msfroh@example.com>" }, "q", 1);
    const desc = li.querySelector(".description");
    expect(desc.textContent).toBe("Michael <msfroh@example.com>");
    // Escaped, so the address never becomes a child element.
    expect(desc.children.length).toBe(0);
  });

  it("renders the copy-URL control as a real, accessible button", () => {
    api.getConfig.mockReturnValue({ features: { clipboard_copy_icon: true } });
    const li = buildResultCard(
      { doc_id: "d7", title: "T", url: "https://ex.com/p" }, "q1", 1);
    // The focusable element is a real <button>, in the a11y tree, carrying the
    // accessible name and the clipboard payload — not an <i> with role="button".
    const btn = li.querySelector("button.url-copy-btn");
    expect(btn).toBeTruthy();
    expect(btn.getAttribute("type")).toBe("button");
    expect(btn.getAttribute("aria-hidden")).toBeNull();
    expect(btn.getAttribute("aria-label")).toMatch(/./);
    expect(btn.getAttribute("data-clipboard-text")).toBeTruthy();
    expect(btn.classList.contains("d-print-none")).toBe(true);
    // The glyph inside is purely decorative: aria-hidden, no role.
    const glyph = btn.querySelector("i.fa-copy");
    expect(glyph).toBeTruthy();
    expect(glyph.getAttribute("aria-hidden")).toBe("true");
    expect(glyph.getAttribute("role")).toBeNull();
  });
});

describe("renderPopularWords", () => {
  it("renders a label span plus one data-spa anchor per word and removes d-none", () => {
    mountBody('<div id="pw" class="d-none"></div>');
    const target = document.getElementById("pw");
    renderPopularWords(["a", "b"], target);

    expect(target.classList.contains("d-none")).toBe(false);
    const spans = target.querySelectorAll("span");
    expect(spans.length).toBe(1);
    expect(spans[0].className).toBe("me-2");
    const anchors = target.querySelectorAll("a");
    expect(anchors.length).toBe(2);
    expect(anchors[0].getAttribute("href")).toBe("search?q=a");
    expect(anchors[0].hasAttribute("data-spa")).toBe(true);
    expect(anchors[1].getAttribute("href")).toBe("search?q=b");
  });

  it("percent-encodes a word containing a space", () => {
    mountBody('<div id="pw"></div>');
    const target = document.getElementById("pw");
    renderPopularWords(["c d"], target);
    expect(target.querySelector("a").getAttribute("href")).toBe("search?q=c%20d");
  });

  it("clears children and adds d-none for an empty or null word list", () => {
    mountBody('<div id="pw"></div>');
    const target = document.getElementById("pw");
    renderPopularWords(["seed"], target);
    renderPopularWords([], target);
    expect(target.classList.contains("d-none")).toBe(true);
    expect(target.children.length).toBe(0);
    renderPopularWords(["seed"], target);
    renderPopularWords(null, target);
    expect(target.classList.contains("d-none")).toBe(true);
    expect(target.children.length).toBe(0);
  });

  it("does not throw when targetEl is null", () => {
    expect(() => renderPopularWords(["a"], null)).not.toThrow();
  });
});

describe("disableSubmitBriefly", () => {
  afterEach(() => vi.useRealTimers());

  it("disables the button immediately and re-enables it after 3000ms", () => {
    vi.useFakeTimers();
    const btn = document.createElement("button");
    disableSubmitBriefly(btn);
    expect(btn.disabled).toBe(true);
    vi.advanceTimersByTime(2999);
    expect(btn.disabled).toBe(true);
    vi.advanceTimersByTime(1);
    expect(btn.disabled).toBe(false);
  });

  it("does not throw when btn is null", () => {
    expect(() => disableSubmitBriefly(null)).not.toThrow();
  });
});

describe("clearSearchState / _state", () => {
  it("resets every state field to its initial value and clears inputs", () => {
    mountBody(`
      <input id="query" value="stale">
      <input id="contentQuery" value="stale">
      <select id="numSearchOption"><option value="10">10</option><option value="50" selected>50</option></select>
      <select id="sortSearchOption"><option value="">def</option><option value="z" selected>z</option></select>
    `);
    const s = _state;
    s.q = "x"; s.start = 5; s.num = 50; s.sort = "z"; s.lang = ["ja"]; s.sdh = "h";
    s.as = { q: ["x"] };
    s.facets = { a: [1] }; s.fields = { label: ["x"] }; s.facetQueries = ["fq"]; s.exQ = ["e"];
    s.geo = { lat: "1", lon: "2", distance: "3" }; s.requestedTime = 99; s.highlightParams = "&hl";

    clearSearchState();

    expect(_state.q).toBe("");
    expect(_state.start).toBe(0);
    expect(_state.num).toBe(10);
    expect(_state.sort).toBe("");
    expect(_state.lang).toEqual([]);
    expect(_state.sdh).toBe("");
    expect(_state.as).toEqual({});
    expect(_state.facets).toEqual({});
    expect(_state.fields).toEqual({});
    expect(_state.facetQueries).toEqual([]);
    expect(_state.exQ).toEqual([]);
    expect(_state.geo).toEqual({ lat: "", lon: "", distance: "" });
    expect(_state.requestedTime).toBe(0);
    expect(_state.highlightParams).toBe("");

    // Inputs cleared; drawer selects reset to their default index.
    expect(document.getElementById("query").value).toBe("");
    expect(document.getElementById("contentQuery").value).toBe("");
    expect(document.getElementById("numSearchOption").selectedIndex).toBe(0);
    expect(document.getElementById("sortSearchOption").selectedIndex).toBe(0);
  });
});

describe("runFromUrl", () => {
  it("resets in-memory filter stores and navigates home for a filter-less URL", async () => {
    api.getConfig.mockReturnValue(null);
    api.get.mockResolvedValue({ data: [] });
    _state.facets = { label: ["x"] };
    _state.facetQueries = ["fq"];
    _state.sdh = "hash";
    _state.fields = { label: ["y"] };
    setLocation("/search?num=10");

    runFromUrl();
    await Promise.resolve();

    expect(_state.facets).toEqual({});
    expect(_state.facetQueries).toEqual([]);
    expect(_state.sdh).toBe("");
    expect(_state.fields).toEqual({});
    expect(navigate).toHaveBeenCalledWith("./", { replace: true });
    expect(api.get).not.toHaveBeenCalled();
  });

  it("hydrates state from a full search URL, syncs inputs, and issues the search", async () => {
    api.getConfig.mockReturnValue(null);
    api.get.mockResolvedValue({ data: [], query_id: "q" });
    mountBody('<input id="query"><input id="contentQuery"><ul id="results"></ul><div id="results-meta"></div><div id="empty-state"></div>');
    setLocation("/search?q=foo&start=20&num=50&sort=x&lang=ja&lang=en&fields.label=A&ex_q=cl");

    runFromUrl();
    await new Promise((r) => setTimeout(r));

    expect(_state.q).toBe("foo");
    expect(_state.start).toBe(20);
    expect(_state.num).toBe(50);
    expect(_state.sort).toBe("x");
    expect(_state.lang).toEqual(["ja", "en"]);
    expect(_state.fields).toEqual({ label: ["A"] });
    expect(_state.exQ).toEqual(["cl"]);

    expect(document.getElementById("query").value).toBe("foo");
    expect(document.getElementById("contentQuery").value).toBe("foo");

    expect(navigate).not.toHaveBeenCalled();
    expect(api.get).toHaveBeenCalled();
    const searchCall = api.get.mock.calls.find((c) => c[0] === "/search");
    expect(searchCall).toBeTruthy();
    expect(searchCall[1]).toMatchObject({
      q: "foo", start: 20, num: 50, sort: "x",
      lang: ["ja", "en"], "fields.label": ["A"], "ex_q": ["cl"],
    });
  });
});

// ─── Shared fixture + api dispatch for runSearch and its render machinery ─────

/**
 * Full DOM fixture with every id runSearch/renderResults/renderFacets/
 * renderPagination and the option renderers reach for.
 */
const SEARCH_FIXTURE = `
  <input id="query"><input id="contentQuery">
  <input id="queryId"><input id="rt">
  <div id="search-loading" class="d-none"></div>
  <div id="search-error" class="d-none"></div>
  <div id="results-warning" class="d-none"></div>
  <div id="results-status"></div>
  <div id="similar-doc-banner" class="d-none"></div>
  <ul id="results"></ul>
  <div id="results-meta"></div>
  <div id="empty-state" class="d-none"><span id="empty-did-not-match"></span></div>
  <div id="results-popular-words" class="d-none"></div>
  <nav id="subfooter" class="d-none"><ul id="pagination"></ul></nav>
  <div id="facet-body" class="d-none"></div>
  <div id="facet-body-mobile"></div>
  <div id="facet-toggle-wrap"></div>
  <div id="active-chips" class="d-none"></div>
  <ul id="current-filters"></ul>
  <ul id="options-bar"></ul>
  <div id="related-queries" class="d-none"></div>
  <div id="related-content" class="d-none"></div>
  <select id="sortSearchOption"></select>
  <select id="numSearchOption"></select>
  <select id="langSearchOption"></select>
  <fieldset id="labelSearchOptionFieldset"><select id="labelSearchOption"></select></fieldset>
`;

/** A representative api config the option renderers + facets consume. */
const FULL_CFG = {
  features: { popular_word: true, display_label_type: true },
  sort_options: [
    { value: "", label_key: "labels.search_result_sort_score_desc" },
    { value: "last_modified.desc", label_key: "labels.search_result_sort_last_modified_desc" },
  ],
  num_options: [10, 20, 50],
  lang_options: [{ value: "ja" }, { value: "en" }],
  label_options: [{ value: "lblA", label: "Label A" }, { value: "lblB", label: "Label B" }],
  facet_views: [
    {
      group_name: "labels.facet_filetype_title",
      queries: [
        { value: "filetype:html", label_key: "labels.facet_filetype_html" },
        { value: "filetype:pdf", label_key: "labels.facet_filetype_pdf" },
      ],
    },
  ],
};

/** Build a realistic /search envelope. `docs` become result cards. */
function makeSearchEnv(docs, extra = {}) {
  return {
    query_id: "qid-1",
    requested_time: 1700000000000,
    highlight_params: "&hl.q=foo",
    partial: false,
    record_count: docs.length ? 42 : 0,
    record_count_relation: "EQUAL_TO",
    start_record_number: 1,
    end_record_number: docs.length,
    exec_time: 0.05,
    page_number: 1,
    page_numbers: ["1", "2", "3", "4", "5"],
    prev_page: false,
    next_page: docs.length > 0,
    data: docs,
    facet_field: [
      {
        name: "label",
        result: [
          { value: "lblA", count: 5 },
          { value: "lblB", count: 3 },
          { value: "lblZ", count: 0 },
        ],
      },
    ],
    facet_query: [
      { value: "filetype:html", count: 7 },
      { value: "filetype:pdf", count: 0 },
    ],
    ...extra,
  };
}

const SAMPLE_DOCS = [
  { doc_id: "d1", title: "Doc One", url: "https://ex.com/1", last_modified: "2023-01-02T00:00:00Z", content_length: 1234, has_cache: "true" },
  { doc_id: "d2", content_title: "Doc <strong>Two</strong>", url: "https://ex.com/2", digest: "second digest" },
];

/**
 * Install an api.get implementation that dispatches per endpoint. Pass
 * overrides to replace the /search envelope or related payloads.
 */
function installApiDispatch(overrides = {}) {
  const searchEnv = "search" in overrides ? overrides.search : makeSearchEnv(SAMPLE_DOCS);
  api.get.mockImplementation(async (path) => {
    if (path === "/search") return searchEnv;
    if (path === "/labels") return { labels: overrides.labels || [{ value: "lblA", label: "Label A" }, { value: "lblB", label: "Label B" }] };
    if (path === "/popular-words") return { popular_words: overrides.popularWords || ["alpha", "beta", "gamma", "delta"] };
    if (path === "/related-queries") return { queries: overrides.relatedQueries || ["rq1", "rq2"] };
    if (path === "/related-content") return { content: "content" in overrides ? overrides.content : "<p>related</p>" };
    if (path === "/favorites") return { data: overrides.favorites || [] };
    if (path === "/suggest-words") return { suggest_words: overrides.suggestWords || [{ text: "sug1" }, { text: "sug2" }] };
    return {};
  });
}

/** Yield a macrotask so fire-and-forget helpers (popular words, related) settle. */
const settle = () => new Promise((r) => setTimeout(r));

/** How many "/search" API calls have been made so far. */
const searchCalls = () => api.get.mock.calls.filter((c) => c[0] === "/search").length;

describe("runSearch — successful render", () => {
  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
  });

  it("renders one result card per hit with correct ids and titles", async () => {
    await runSearch();
    await settle();

    const cards = document.querySelectorAll("#results > li");
    expect(cards.length).toBe(2);
    expect(document.getElementById("result0")).not.toBeNull();
    expect(document.getElementById("result1")).not.toBeNull();
    expect(document.querySelector("#result0 h3 a").textContent).toBe("Doc One");
    // content_title on the second doc keeps its highlight markup.
    expect(document.querySelector("#result1 h3 a strong")).not.toBeNull();
    expect(document.getElementById("result0").dataset.docId).toBe("d1");
  });

  it("toggles the loading indicator off and leaves results-meta empty on success", async () => {
    await runSearch();
    await settle();
    expect(document.getElementById("search-loading").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("empty-state").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("results-meta").textContent).toBe("");
  });

  it("populates the results-status banner and the hidden queryId/rt fields", async () => {
    await runSearch();
    await settle();
    const status = document.getElementById("results-status").textContent;
    expect(status).toContain("labels.search_result_status");
    expect(status).toContain("labels.search_result_time");
    expect(document.getElementById("queryId").value).toBe("qid-1");
    expect(document.getElementById("rt").value).toBe("1700000000000");
  });

  it("sets document.title from the query", async () => {
    await runSearch();
    await settle();
    expect(document.title).toBe("page.search_title");
  });

  it("requests /search with facet.field=label and configured facet.query values", async () => {
    await runSearch();
    await settle();
    const call = api.get.mock.calls.find((c) => c[0] === "/search");
    expect(call).toBeTruthy();
    expect(call[1]).toMatchObject({
      q: "foo",
      start: 0,
      num: 10,
      "facet.field": ["label"],
      "facet.query": ["filetype:html", "filetype:pdf"],
    });
    expect(call[2]).toHaveProperty("signal");
  });

  it("requests the auxiliary endpoints (popular-words, labels, related)", async () => {
    await runSearch();
    await settle();
    const paths = api.get.mock.calls.map((c) => c[0]);
    expect(paths).toContain("/popular-words");
    expect(paths).toContain("/labels");
    expect(paths).toContain("/related-queries");
    expect(paths).toContain("/related-content");
  });

  it("renders the label facet group and the filetype query view, suppressing zero counts", async () => {
    await runSearch();
    await settle();
    const body = document.getElementById("facet-body");
    expect(body.classList.contains("d-md-block")).toBe(true);
    const groups = body.querySelectorAll("ul.list-group");
    expect(groups.length).toBe(2);
    // Label group title + Label A / Label B (lblZ count 0 suppressed).
    expect(groups[0].querySelector("li.text-uppercase").textContent).toBe("labels.facet_label_title");
    const labelText = groups[0].textContent;
    expect(labelText).toContain("Label A");
    expect(labelText).toContain("Label B");
    expect(labelText).not.toContain("lblZ");
    // Filetype query view: html kept (count 7), pdf suppressed (count 0).
    expect(groups[1].textContent).toContain("labels.facet_filetype_html");
    expect(groups[1].textContent).not.toContain("labels.facet_filetype_pdf");
    // Counts ARE present here, so the shown option carries a count badge.
    expect(groups[1].querySelector(".badge")).not.toBeNull();
  });

  it("keeps every filetype query option (and drops badges) when the API omits facet_query", async () => {
    // Under rank fusion (and whenever facetResponse is null) the v2 API omits
    // facet_query entirely. The pre-fix code filtered options by `Number(count) > 0`,
    // so with no counts every option was dropped and the group collapsed to a bare
    // header. Recoverability fix: absent counts → keep the full clickable list, no badges.
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { facet_query: undefined }) });
    await runSearch();
    await settle();
    const body = document.getElementById("facet-body");
    const groups = body.querySelectorAll("ul.list-group");
    const filetype = Array.from(groups).find((g) => g.textContent.includes("labels.facet_filetype_html"));
    expect(filetype).toBeTruthy();
    // BOTH options survive (html AND pdf), unlike the counts-present zero-suppress path.
    expect(filetype.textContent).toContain("labels.facet_filetype_html");
    expect(filetype.textContent).toContain("labels.facet_filetype_pdf");
    // No counts were sent → the group renders no badges at all.
    expect(filetype.querySelector(".badge")).toBeNull();
  });

  it("mirrors the facet groups into the mobile offcanvas", async () => {
    await runSearch();
    await settle();
    const mobile = document.getElementById("facet-body-mobile");
    expect(mobile.querySelectorAll("ul.list-group").length).toBe(2);
  });

  it("renders pagination: disabled prev, five numbered pages, enabled next", async () => {
    await runSearch();
    await settle();
    expect(document.getElementById("subfooter").classList.contains("d-none")).toBe(false);
    const items = document.querySelectorAll("#pagination > li");
    expect(items.length).toBe(7); // prev + 5 + next
    expect(items[0].className).toContain("disabled"); // prev
    expect(items[1].className).toContain("active"); // page 1
    expect(items[items.length - 1].className).not.toContain("disabled"); // next
    // Far pages (4,5) carry the responsive-hide classes.
    expect(items[4].className).toContain("d-none");
  });

  it("renders related queries and sanitized related content", async () => {
    await runSearch();
    await settle();
    const rq = document.getElementById("related-queries");
    expect(rq.classList.contains("d-none")).toBe(false);
    expect(rq.textContent).toContain("rq1");
    expect(rq.textContent).toContain("rq2");
    const rc = document.getElementById("related-content");
    expect(rc.classList.contains("d-none")).toBe(false);
    expect(rc.textContent).toContain("related");
  });

  it("renders results popular words when the feature is enabled", async () => {
    await runSearch();
    await settle();
    const rpw = document.getElementById("results-popular-words");
    expect(rpw.classList.contains("d-none")).toBe(false);
    expect(rpw.querySelectorAll("a[data-spa]").length).toBe(4);
  });

  it("renders the options bar with sort/num/lang/label items", async () => {
    await runSearch();
    await settle();
    const items = document.querySelectorAll("#options-bar > li");
    expect(items.length).toBe(4);
    expect(document.getElementById("options-bar").textContent).toContain("search.menu_sort");
    expect(document.getElementById("options-bar").textContent).toContain("search.menu_labels");
  });

  it("shows the timeout warning when the search timed out", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { partial: true, timed_out: true }) });
    await runSearch();
    await settle();
    const warn = document.getElementById("results-warning");
    expect(warn.classList.contains("d-none")).toBe(false);
    expect(warn.textContent).toBe("labels.process_time_is_exceeded");
  });

  it("does not call a shard failure a timeout", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { partial: true, shard_failed: true }) });
    await runSearch();
    await settle();
    const warn = document.getElementById("results-warning");
    expect(warn.classList.contains("d-none")).toBe(false);
    expect(warn.textContent).toBe("labels.search_partially_failed");
  });

  it("shows both warnings when a timeout and a shard failure coincide", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { partial: true, timed_out: true, shard_failed: true }) });
    await runSearch();
    await settle();
    expect(document.getElementById("results-warning").textContent).toBe("labels.process_time_is_exceeded labels.search_partially_failed");
  });

  it("does not call a partial result a timeout when no cause is given", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { partial: true }) });
    await runSearch();
    await settle();
    expect(document.getElementById("results-warning").textContent).toBe("labels.search_partially_failed");
  });

  it.each([
    ["PENDING", "errors.user_permissions_loading"],
    ["FAILED", "errors.user_permissions_unavailable"],
  ])("tells the user when permissions are %s", async (permissionState, key) => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { permission_state: permissionState }) });
    await runSearch();
    await settle();
    const warn = document.getElementById("results-warning");
    expect(warn.classList.contains("d-none")).toBe(false);
    expect(warn.textContent).toBe(key);
  });

  it("shows the permission notice when nothing matched", async () => {
    installApiDispatch({ search: makeSearchEnv([], { permission_state: "PENDING" }) });
    await runSearch();
    await settle();
    const warn = document.getElementById("results-warning");
    expect(warn.classList.contains("d-none")).toBe(false);
    expect(warn.textContent).toBe("errors.user_permissions_loading");
  });

  it("puts the permission notice before a partial-result warning", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { permission_state: "FAILED", partial: true, timed_out: true }) });
    await runSearch();
    await settle();
    expect(document.getElementById("results-warning").textContent)
      .toBe("errors.user_permissions_unavailable labels.process_time_is_exceeded");
  });

  it("hides the banner for resolved permissions and a complete result", async () => {
    const warn = document.getElementById("results-warning");
    warn.classList.remove("d-none");
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { permission_state: "RESOLVED" }) });
    await runSearch();
    await settle();
    expect(warn.classList.contains("d-none")).toBe(true);
  });

  it("uses the _over status key when record_count_relation is not EQUAL_TO", async () => {
    installApiDispatch({ search: makeSearchEnv(SAMPLE_DOCS, { record_count_relation: "GREATER_THAN_OR_EQUAL_TO" }) });
    await runSearch();
    await settle();
    expect(document.getElementById("results-status").textContent).toContain("labels.search_result_status_over");
  });

  it("dispatches fess:search:after with the envelope", async () => {
    const spy = vi.fn();
    document.addEventListener("fess:search:after", spy);
    await runSearch();
    await settle();
    document.removeEventListener("fess:search:after", spy);
    expect(spy).toHaveBeenCalledTimes(1);
    expect(spy.mock.calls[0][0].detail.query_id).toBe("qid-1");
  });
});

describe("runSearch — zero results", () => {
  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch({ search: makeSearchEnv([], { prev_page: false, next_page: false, page_numbers: [] }) });
    mountBody(SEARCH_FIXTURE);
    _state.q = "zzz";
  });

  it("shows the empty state and clears the results list and status", async () => {
    await runSearch();
    await settle();
    expect(document.getElementById("empty-state").classList.contains("d-none")).toBe(false);
    expect(document.getElementById("empty-did-not-match").textContent).toBe("search.did_not_match");
    expect(document.querySelectorAll("#results > li").length).toBe(0);
    expect(document.getElementById("results-status").textContent).toBe("");
  });

  it("hides the facet sidebar, mobile toggle and pagination", async () => {
    await runSearch();
    await settle();
    expect(document.getElementById("facet-body").classList.contains("d-md-block")).toBe(false);
    expect(document.getElementById("facet-toggle-wrap").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("subfooter").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("results-popular-words").classList.contains("d-none")).toBe(true);
  });

  it("does NOT clear related queries — loadRelated still populates them", async () => {
    await runSearch();
    await settle();
    // The zero-result branch must leave related alone so the async loadRelated wins.
    const rq = document.getElementById("related-queries");
    expect(rq.classList.contains("d-none")).toBe(false);
    expect(rq.textContent).toContain("rq1");
  });
});

describe("runSearch — error handling", () => {
  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    api.get.mockResolvedValue({ data: [] });
  });

  const errBox = () => document.getElementById("search-error");

  it("shows the invalid_request message in the visible banner", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("bad query"), { code: "invalid_request" }));
    await runSearch();
    expect(errBox().textContent).toBe("bad query");
    expect(errBox().classList.contains("d-none")).toBe(false);
  });

  it("treats httpStatus 400 as an invalid request", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("http 400"), { httpStatus: 400 }));
    await runSearch();
    expect(errBox().textContent).toBe("http 400");
    expect(errBox().classList.contains("d-none")).toBe(false);
  });

  it("shows error.network for a NetworkError", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("net"), { name: "NetworkError" }));
    await runSearch();
    expect(errBox().textContent).toBe("error.network");
  });

  it("shows error.auth_required for AUTH_REQUIRED", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("auth"), { code: "AUTH_REQUIRED" }));
    await runSearch();
    expect(errBox().textContent).toBe("error.auth_required");
  });

  // This arm has NO httpStatus fallback in search.js, so the code string is the
  // only thing standing between an authentication failure and a generic
  // "error.server". The server sends V2ErrorCode.AUTH_REQUIRED's wire code
  // "auth_required" (lowercase snake_case) and api.js passes it through verbatim.
  it("shows error.auth_required for the server's lowercase auth_required wire code", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("auth"), { code: "auth_required" }));
    await runSearch();
    expect(errBox().textContent).toBe("error.auth_required");
  });

  it("announces fess:auth:required for auth_required only", async () => {
    let announced = 0;
    const onRequired = () => { announced += 1; };
    document.addEventListener("fess:auth:required", onRequired);
    try {
      api.get.mockRejectedValueOnce(Object.assign(new Error("auth"), { code: "auth_required" }));
      await runSearch();
      api.get.mockRejectedValueOnce(new Error("boom"));
      await runSearch();
    } finally {
      document.removeEventListener("fess:auth:required", onRequired);
    }
    expect(announced).toBe(1);
  });

  it("shows error.server for a generic failure and clears the spinner", async () => {
    api.get.mockRejectedValueOnce(new Error("boom"));
    await runSearch();
    expect(errBox().textContent).toBe("error.server");
    expect(document.getElementById("search-loading").classList.contains("d-none")).toBe(true);
  });

  it("swallows an AbortError without showing the error banner", async () => {
    api.get.mockRejectedValueOnce(Object.assign(new Error("aborted"), { name: "AbortError" }));
    await runSearch();
    expect(errBox().classList.contains("d-none")).toBe(true);
    expect(errBox().textContent).toBe("");
  });

  it("falls back to #results-meta when there is no #search-error element", async () => {
    errBox().remove();
    api.get.mockRejectedValueOnce(new Error("boom"));
    await runSearch();
    expect(document.getElementById("results-meta").textContent).toBe("error.server");
  });
});

describe("runSearch — active filters, chips and current filters", () => {
  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }]) });
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    _state.sort = "last_modified.desc";
    _state.num = 50;
    _state.lang = ["ja"];
    _state.fields = { label: ["lblB"], filetype: ["pdf"] };
    _state.facets = { label: ["lblA"] };
    _state.facetQueries = ["filetype:html"];
  });

  it("renders an active chip per filter (facet, filetype and facet-query)", async () => {
    await runSearch();
    await settle();
    const chips = document.getElementById("active-chips");
    expect(chips.classList.contains("d-none")).toBe(false);
    const text = chips.textContent;
    expect(text).toContain("facet.active_filters:");
    expect(text).toContain("label: lblA");
    expect(text).toContain("label: lblB");
    expect(text).toContain("labels.facet_filetype_title: labels.facet_filetype_pdf");
    // facet-query chip
    expect(text).toContain("labels.facet_filetype_title: labels.facet_filetype_html");
  });

  it("re-runs the search and drops the filter when a chip remove button is clicked", async () => {
    await runSearch();
    await settle();
    const before = searchCalls();
    // First chip is the label:lblA facet; its remove button clears it.
    const removeBtn = document.querySelector("#active-chips .active-chip-remove");
    removeBtn.click();
    await settle();
    const after = searchCalls();
    expect(after).toBe(before + 1);
  });

  it("renders current-filters badges for sort, non-default num, lang and label", async () => {
    await runSearch();
    await settle();
    // sort + non-default num + lang(ja) + label(lblB) → four badges.
    const badges = document.querySelectorAll("#current-filters > li");
    expect(badges.length).toBe(4);
    const text = document.getElementById("current-filters").textContent;
    expect(text).toContain("labels.search_result_sort_last_modified_desc");
    expect(text).toContain("search.num_format");
    expect(text).toContain("labels.lang_ja");
    expect(text).toContain("Label B");
  });

  it("focuses the drawer control when a current-filter badge is clicked", async () => {
    await runSearch();
    await settle();
    const btn = document.querySelector("#current-filters button");
    btn.click(); // sort badge → focuses #sortSearchOption; just assert no throw
    expect(document.activeElement).toBe(document.getElementById("sortSearchOption"));
  });

  it("shows a facet-reset link when filters are active", async () => {
    await runSearch();
    await settle();
    const reset = document.querySelector("#facet-body .facet-reset");
    expect(reset).not.toBeNull();
    const before = searchCalls();
    reset.click();
    await settle();
    expect(searchCalls()).toBe(before + 1);
    expect(_state.facets).toEqual({});
    expect(_state.facetQueries).toEqual([]);
  });

  it("marks the active facet entries with the active class", async () => {
    await runSearch();
    await settle();
    const active = document.querySelectorAll("#facet-body li.list-group-item.active");
    expect(active.length).toBeGreaterThanOrEqual(2); // lblA facet + filetype:html query view
  });
});

describe("runSearch — facet and pagination click handlers", () => {
  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    mountBody(SEARCH_FIXTURE);
    window.scrollTo = () => {};
    _state.q = "foo";
    _state.num = 10;
  });

  it("toggles a facet value and re-runs the search when a facet entry is clicked", async () => {
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }]) });
    await runSearch();
    await settle();
    const before = searchCalls();
    document.querySelector("#facet-body ul li.list-group-item a").click();
    await settle();
    expect(searchCalls()).toBe(before + 1);
    expect(_state.facets.label).toContain("lblA");
    expect(_state.start).toBe(0);
  });

  it("advances the page offset and re-runs when the next link is clicked", async () => {
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }], { prev_page: true, next_page: true, page_number: 2 }) });
    _state.start = 10;
    await runSearch();
    await settle();
    const before = searchCalls();
    document.querySelector("#pagination li:last-child a").click();
    await settle();
    expect(searchCalls()).toBe(before + 1);
    expect(_state.start).toBe(20);
  });

  it("pushes the page offset into the URL without re-dispatching the route", async () => {
    setLocation("/search?q=foo&start=10&num=10");
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }], { prev_page: true, next_page: true, page_number: 2 }) });
    _state.start = 10;
    await runSearch();
    await settle();
    const before = searchCalls();
    const historyLength = history.length;
    document.querySelector("#pagination li:last-child a").click();
    await settle();
    // JSP parity: paging links carry start=, so reload/back/share keep the page.
    expect(location.pathname).toBe("/search");
    expect(new URLSearchParams(location.search).get("start")).toBe("20");
    expect(new URLSearchParams(location.search).get("q")).toBe("foo");
    expect(history.length).toBe(historyLength + 1);
    // One fetch: the URL is pushed directly, not via navigate() -> runFromUrl().
    expect(navigate).not.toHaveBeenCalled();
    expect(searchCalls()).toBe(before + 1);
  });

  it("drops start from the URL when a facet click resets to the first page", async () => {
    setLocation("/search?q=foo&start=20");
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }]) });
    _state.start = 20;
    await runSearch();
    await settle();
    const historyLength = history.length;
    document.querySelector("#facet-body ul li.list-group-item a").click();
    await settle();
    expect(_state.start).toBe(0);
    expect(location.search).toBe("?q=foo&ex_q=label%3AlblA");
    // Corrected in place: a filter change is not a new page in history.
    expect(history.length).toBe(historyLength);
  });

  it("writes facet selections to the URL as ex_q and drops them again on deselect", async () => {
    setLocation("/search?q=foo");
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }]) });
    await runSearch();
    await settle();
    [...document.querySelectorAll("#facet-body li.list-group-item a")].find((a) => a.textContent.startsWith("Label B")).click();
    await settle();
    [...document.querySelectorAll("#facet-body li.list-group-item a")].find((a) => a.textContent.startsWith("labels.facet_filetype_html")).click();
    await settle();
    expect(new URLSearchParams(location.search).getAll("ex_q")).toEqual(["label:lblB", "filetype:html"]);
    document.querySelector("#facet-body li.list-group-item.active a").click();
    await settle();
    expect(new URLSearchParams(location.search).getAll("ex_q")).toEqual(["filetype:html"]);
  });

  it("jumps to a specific page when a numbered page link is clicked", async () => {
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }], { prev_page: true, next_page: true, page_number: 2 }) });
    _state.start = 10;
    await runSearch();
    await settle();
    // Third page number link → start = (3-1)*10 = 20.
    const pageLinks = document.querySelectorAll("#pagination li.page-item:not([aria-label]) a");
    pageLinks[2].click();
    await settle();
    expect(_state.start).toBe(20);
  });

  it("does nothing when the disabled prev link is clicked", async () => {
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }], { prev_page: false, next_page: true }) });
    _state.start = 0;
    await runSearch();
    await settle();
    const before = searchCalls();
    document.querySelector("#pagination li:first-child a").click();
    await settle();
    expect(searchCalls()).toBe(before);
  });
});

describe("runSearch — favorites and similar docs", () => {
  it("renders the favorite star, syncs favorited state and toggles on click", async () => {
    api.getConfig.mockReturnValue({ ...FULL_CFG, features: { ...FULL_CFG.features, user_favorite: true } });
    api.isAuthenticated.mockReturnValue(true);
    installApiDispatch({
      search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1", favorite_count: 2 }]),
      favorites: ["d1"],
    });
    api.post.mockResolvedValue({ favorite: false, count: 1 });
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    await runSearch();
    await settle();

    const btn = document.querySelector(".favorite-btn");
    expect(btn).not.toBeNull();
    // syncFavorites flipped it to favorited (solid star + pressed).
    expect(btn.getAttribute("aria-pressed")).toBe("true");
    expect(btn.querySelector("i").className).toBe("fas fa-star");
    // Clicking posts to the favorite endpoint.
    btn.click();
    await settle();
    const call = api.post.mock.calls.find((c) => c[0].includes("/documents/d1/favorite"));
    expect(call).toBeTruthy();
    expect(call[1]).toMatchObject({ query_id: "qid-1" });
    expect(btn.getAttribute("aria-pressed")).toBe("false");
  });

  it("shows the similar-doc banner when state.sdh is set and clears it on close", async () => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1" }]) });
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    _state.sdh = "hash-1";
    await runSearch();
    await settle();
    const banner = document.getElementById("similar-doc-banner");
    expect(banner.classList.contains("d-flex")).toBe(true);
    expect(banner.textContent).toContain("labels.similar_doc_result_status");
    const before = searchCalls();
    banner.querySelector("button.btn-close").click();
    await settle();
    expect(_state.sdh).toBe("");
    expect(searchCalls()).toBe(before + 1);
  });

  it("renders a similar link that starts a similarity search on click", async () => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch({ search: makeSearchEnv([{ doc_id: "d1", title: "T", url: "https://e.com/1", similar_docs_count: 3, similar_docs_hash: "h1" }]) });
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    await runSearch();
    await settle();
    const link = document.querySelector("#result0 a.similar");
    expect(link).not.toBeNull();
    const before = searchCalls();
    link.click();
    await settle();
    expect(_state.sdh).toBe("h1");
    expect(searchCalls()).toBe(before + 1);
  });
});

describe("refresh", () => {
  it("re-runs the current search", async () => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    refresh();
    await settle();
    expect(api.get.mock.calls.some((c) => c[0] === "/search")).toBe(true);
    expect(document.querySelectorAll("#results > li").length).toBe(2);
  });
});

describe("syncSearchInputs", () => {
  it("writes the query into both the header and home inputs", () => {
    mountBody('<input id="query"><input id="contentQuery">');
    syncSearchInputs("hello world");
    expect(document.getElementById("query").value).toBe("hello world");
    expect(document.getElementById("contentQuery").value).toBe("hello world");
  });

  it("does not throw when the inputs are absent", () => {
    expect(() => syncSearchInputs("x")).not.toThrow();
  });
});

describe("renderSearchOptions / initSearchOptions", () => {
  beforeEach(() => {
    mountBody(SEARCH_FIXTURE);
  });

  it("populates sort, num, lang and label selects from config", () => {
    api.getConfig.mockReturnValue(FULL_CFG);
    _state.sort = "last_modified.desc";
    _state.num = 20;
    _state.lang = ["ja"];
    _state.fields = { label: ["lblB"] };
    renderSearchOptions();

    const sort = document.getElementById("sortSearchOption");
    expect(sort.options.length).toBe(2); // placeholder + last_modified.desc
    expect(sort.value).toBe("last_modified.desc");

    const num = document.getElementById("numSearchOption");
    expect(Array.from(num.options).map((o) => o.value)).toEqual(["10", "20", "50"]);
    expect(num.value).toBe("20");

    const lang = document.getElementById("langSearchOption");
    expect(lang.hasAttribute("multiple")).toBe(true);
    expect(lang.getAttribute("size")).toBe("4");
    // Intl.DisplayNames resolves the human names for ja/en.
    const langText = lang.textContent;
    expect(langText).toContain("Japanese");
    expect(langText).toContain("English");
    expect(Array.from(lang.selectedOptions).map((o) => o.value)).toContain("ja");

    const label = document.getElementById("labelSearchOption");
    expect(Array.from(label.options).map((o) => o.textContent)).toEqual(["Label A", "Label B"]);
    expect(Array.from(label.selectedOptions).map((o) => o.value)).toEqual(["lblB"]);
    expect(document.getElementById("labelSearchOptionFieldset").classList.contains("d-none")).toBe(false);
  });

  it("falls back to a single default sort option when config has none", () => {
    api.getConfig.mockReturnValue({ features: {} });
    _state.sort = "";
    renderSearchOptions();
    const sort = document.getElementById("sortSearchOption");
    // placeholder (value="") + the score.desc fallback.
    expect(Array.from(sort.options).map((o) => o.value)).toEqual(["", "score.desc"]);
  });

  it("falls back to [10,20,50] num options when config has none", () => {
    api.getConfig.mockReturnValue({ features: {} });
    renderSearchOptions();
    expect(Array.from(document.getElementById("numSearchOption").options).map((o) => o.value)).toEqual(["10", "20", "50"]);
  });

  it("does not add its own All-Languages option when the server supplies an all sentinel", () => {
    api.getConfig.mockReturnValue({ features: {}, lang_options: [{ value: "all" }, { value: "ja" }] });
    _state.lang = [];
    renderSearchOptions();
    const opts = Array.from(document.getElementById("langSearchOption").options);
    // server "all" is mapped to value="" — exactly one empty-value option, not two.
    expect(opts.filter((o) => o.value === "").length).toBe(1);
  });

  it("hides the label fieldset when display_label_type is disabled", () => {
    api.getConfig.mockReturnValue({ features: { display_label_type: false }, label_options: [{ value: "lblA", label: "A" }] });
    renderSearchOptions();
    expect(document.getElementById("labelSearchOptionFieldset").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("labelSearchOption").options.length).toBe(0);
  });

  it("initSearchOptions delegates to renderSearchOptions", () => {
    api.getConfig.mockReturnValue(FULL_CFG);
    initSearchOptions();
    expect(document.getElementById("numSearchOption").options.length).toBe(3);
  });
});

describe("attachSuggest", () => {
  afterEach(() => vi.useRealTimers());

  function mountSuggest() {
    mountBody('<form><input id="sg"><ul id="sgd" class="d-none"></ul></form>');
    return { input: document.getElementById("sg"), dd: document.getElementById("sgd"), form: document.querySelector("form") };
  }

  it("renders suggestion items after the 150ms debounce and marks the input expanded", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "sug1" }, { text: "sug2" }] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd);
    input.value = "he";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    const items = dd.querySelectorAll("li.list-group-item");
    expect(items.length).toBe(2);
    expect(items[0].textContent).toBe("sug1");
    expect(items[0].id).toBe("sg-suggest-0");
    expect(dd.classList.contains("d-none")).toBe(false);
    expect(input.getAttribute("aria-expanded")).toBe("true");
  });

  it("requests /suggest-words with the debounced query and default fields", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "x" }] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd);
    input.value = "  foo  ";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    const call = api.get.mock.calls.find((c) => c[0] === "/suggest-words");
    expect(call[1]).toMatchObject({ q: "foo", num: 10, fn: ["_default", "content", "title"] });
  });

  it("includes lang params when opts.lang is a getter returning codes", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "x" }] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd, { lang: () => ["ja", "en"] });
    input.value = "foo";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    const call = api.get.mock.calls.find((c) => c[0] === "/suggest-words");
    expect(call[1].lang).toEqual(["ja", "en"]);
  });

  it("hides the dropdown when the query becomes empty", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "x" }] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd);
    input.value = "";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(api.get).not.toHaveBeenCalled();
  });

  it("hides the dropdown when the API returns no suggestions", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd);
    input.value = "he";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    expect(dd.classList.contains("d-none")).toBe(true);
  });

  it("fills the input and keeps focus on mousedown-select (no submit by default)", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "picked" }] });
    const { input, dd, form } = mountSuggest();
    const submitSpy = vi.fn();
    form.addEventListener("submit", (e) => { e.preventDefault(); submitSpy(); });
    attachSuggest(input, dd);
    input.value = "pi";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    dd.querySelector("li").dispatchEvent(new MouseEvent("mousedown", { bubbles: true, cancelable: true }));
    expect(input.value).toBe("picked");
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(submitSpy).not.toHaveBeenCalled();
  });

  it("submits the form on select when submitOnSelect is set", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "picked" }] });
    const { input, dd, form } = mountSuggest();
    const submitSpy = vi.fn();
    form.addEventListener("submit", (e) => { e.preventDefault(); submitSpy(); });
    attachSuggest(input, dd, { submitOnSelect: true });
    input.value = "pi";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    dd.querySelector("li").dispatchEvent(new MouseEvent("mousedown", { bubbles: true, cancelable: true }));
    expect(input.value).toBe("picked");
    expect(submitSpy).toHaveBeenCalledTimes(1);
  });

  it("clears the dropdown on blur after the grace delay", async () => {
    vi.useFakeTimers();
    api.get.mockResolvedValue({ suggest_words: [{ text: "x" }] });
    const { input, dd } = mountSuggest();
    attachSuggest(input, dd);
    input.value = "he";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    expect(dd.classList.contains("d-none")).toBe(false);
    input.dispatchEvent(new Event("blur"));
    await vi.advanceTimersByTimeAsync(120);
    expect(dd.classList.contains("d-none")).toBe(true);
  });

  it("is a no-op when input or dropdown is missing", () => {
    expect(() => attachSuggest(null, document.createElement("ul"))).not.toThrow();
    expect(() => attachSuggest(document.createElement("input"), null)).not.toThrow();
  });
});

// attach() has a module-level `attached` guard, so its body runs exactly once
// for the whole test module. All the wiring it installs is therefore exercised
// inside this single test (subsequent resetDom() calls drop the wired elements).
describe("attach — wiring", () => {
  afterEach(() => vi.useRealTimers());

  const ATTACH_FIXTURE = `
    <form id="search-form">
      <input id="query">
      <button id="searchButton" type="button"></button>
    </form>
    <ul id="suggest-dropdown" class="d-none"></ul>
    <div id="home-view"></div>
    <input id="contentQuery">
    <input id="queryId"><input id="rt">
    <button id="searchOptionsClearButton"></button>
    <div id="searchOptions"><button type="submit">go</button></div>
    <input id="geo-lat"><input id="geo-lon"><input id="geo-distance">
    <div id="popular-words" class="d-none"></div>
    <div id="search-loading" class="d-none"></div>
    <div id="search-error" class="d-none"></div>
    <div id="results-warning" class="d-none"></div>
    <div id="results-status"></div>
    <div id="similar-doc-banner" class="d-none"></div>
    <ul id="results"></ul>
    <div id="results-meta"></div>
    <div id="empty-state" class="d-none"><span id="empty-did-not-match"></span></div>
    <div id="results-popular-words" class="d-none"></div>
    <nav id="subfooter" class="d-none"><ul id="pagination"></ul></nav>
    <div id="facet-body" class="d-none"></div>
    <div id="facet-body-mobile"></div>
    <div id="facet-toggle-wrap"></div>
    <div id="active-chips" class="d-none"></div>
    <ul id="current-filters"></ul>
    <ul id="options-bar"></ul>
    <div id="related-queries" class="d-none"></div>
    <div id="related-content" class="d-none"></div>
    <select id="sortSearchOption"></select>
    <select id="numSearchOption"></select>
    <select id="langSearchOption"></select>
    <fieldset id="labelSearchOptionFieldset"><select id="labelSearchOption"></select></fieldset>
  `;

  it("wires the header form, drawer buttons, facet clear and header suggest", async () => {
    vi.useFakeTimers();
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch();
    setLocation("/"); // no q → attach() loads popular words and populates the input from nothing
    mountBody(ATTACH_FIXTURE);

    attach();
    await vi.advanceTimersByTimeAsync(0); // flush loadPopularWords / renderSearchOptions

    // attach() populated the option selects from config.
    expect(document.getElementById("numSearchOption").options.length).toBe(3);
    // and rendered popular words.
    expect(document.getElementById("popular-words").querySelectorAll("a[data-spa]").length).toBe(4);

    // 1. Header form submit → navigate to search?q=..., syncs inputs, disables the button.
    // The drawer's labels ride along; a legacy URL's sdh and as.* conditions do not.
    setLocation("/search?q=old&sdh=h1&as.q=legacy&fields.label=lblB");
    document.getElementById("labelSearchOption").value = "lblA";
    document.getElementById("query").value = "hello";
    document.getElementById("search-form").dispatchEvent(new Event("submit", { cancelable: true }));
    expect(navigate).toHaveBeenCalled();
    const headerTarget = navigate.mock.calls.at(-1)[0];
    expect(headerTarget).toContain("q=hello");
    const headerParams = new URLSearchParams(headerTarget.slice(headerTarget.indexOf("?") + 1));
    expect(headerParams.getAll("fields.label")).toEqual(["lblA"]);
    expect(headerParams.has("sdh")).toBe(false);
    expect(headerParams.has("as.q")).toBe(false);
    setLocation("/");
    expect(document.getElementById("contentQuery").value).toBe("hello");
    expect(document.getElementById("searchButton").disabled).toBe(true);

    // 2. Drawer "Clear" button → resets the option selects (no re-search).
    const numSel = document.getElementById("numSearchOption");
    numSel.selectedIndex = 2;
    document.getElementById("searchOptionsClearButton").click();
    expect(numSel.selectedIndex).toBe(0);

    // 3. Drawer "Search" button → navigate using the home query (home-view visible).
    navigate.mockClear();
    document.getElementById("contentQuery").value = "drawerq";
    document.querySelector('#searchOptions button[type="submit"]').click();
    expect(navigate).toHaveBeenCalled();
    expect(navigate.mock.calls.at(-1)[0]).toContain("q=drawerq");

    // 4. Header suggest: typing renders items after the 150ms debounce.
    const input = document.getElementById("query");
    input.value = "he";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    const dropdown = document.getElementById("suggest-dropdown");
    expect(dropdown.querySelectorAll(".list-group-item").length).toBe(2);
    expect(dropdown.classList.contains("d-none")).toBe(false);

    // 5. ArrowDown highlights the first item; Escape collapses the dropdown.
    input.dispatchEvent(new KeyboardEvent("keydown", { key: "ArrowDown", bubbles: true }));
    expect(dropdown.querySelector(".list-group-item.active")).not.toBeNull();
    expect(input.getAttribute("aria-activedescendant")).toBe("suggest-item-0");
    input.dispatchEvent(new KeyboardEvent("keydown", { key: "Escape", bubbles: true }));
    expect(dropdown.classList.contains("d-none")).toBe(true);

    // 6. Dropdown mousedown selects the item and submits the header form.
    input.value = "he";
    input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);
    navigate.mockClear();
    dropdown.querySelector(".list-group-item").dispatchEvent(new MouseEvent("mousedown", { bubbles: true, cancelable: true }));
    expect(input.value).toBe("sug1");
    expect(navigate).toHaveBeenCalled(); // submit handler navigated
  });

  it("is idempotent — a second attach() call is a quiet no-op", () => {
    expect(() => attach()).not.toThrow();
  });
});

describe("runFromUrl — facet selections in the URL", () => {
  const searchParams = () => api.get.mock.calls.filter((c) => c[0] === "/search").at(-1)[1];

  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
  });

  it("restores label and facet-query selections as active facets and keeps other clauses", async () => {
    setLocation("/search?q=foo&ex_q=label%3AlblB&ex_q=filetype%3Ahtml&ex_q=timestamp%3A%5Bnow%2Fd-1d+TO+*%5D");
    runFromUrl();
    await settle();
    expect(_state.facets).toEqual({ label: ["lblB"] });
    expect(_state.facetQueries).toEqual(["filetype:html"]);
    expect(_state.exQ).toEqual(["timestamp:[now/d-1d TO *]"]);
    expect(searchParams()["ex_q"]).toEqual(["label:lblB", "filetype:html", "timestamp:[now/d-1d TO *]"]);
    const active = [...document.querySelectorAll("#facet-body li.list-group-item.active")].map((li) => li.textContent);
    expect(active.some((txt) => txt.startsWith("Label B"))).toBe(true);
    expect(active.length).toBe(2);
  });

  it("removes a restored facet when it is clicked, in the request and in the URL", async () => {
    setLocation("/search?q=foo&ex_q=label%3AlblB");
    runFromUrl();
    await settle();
    [...document.querySelectorAll("#facet-body li.list-group-item.active a")].find((a) => a.textContent.startsWith("Label B")).click();
    await settle();
    expect(_state.facets.label).toEqual([]);
    expect(searchParams()).not.toHaveProperty("ex_q");
    expect(new URLSearchParams(location.search).has("ex_q")).toBe(false);
  });

  it("runs a search for a URL carrying only a facet selection", async () => {
    setLocation("/search?ex_q=label%3AlblA");
    runFromUrl();
    await settle();
    expect(navigate).not.toHaveBeenCalled();
    expect(searchParams()["ex_q"]).toEqual(["label:lblA"]);
  });

  it("sends a repeated clause once", async () => {
    setLocation("/search?q=foo&ex_q=label%3AlblA&ex_q=label%3AlblA");
    runFromUrl();
    await settle();
    expect(searchParams()["ex_q"]).toEqual(["label:lblA"]);
  });
});

describe("runFromUrl — default labels, sort and page size", () => {
  const DEFAULTS_CFG = {
    ...FULL_CFG,
    default_label_values: ["lblA"],
    default_sort: "last_modified.desc",
    page_size_default: 20,
    page_size_max: 100,
    num_options: [10, 20, 50],
  };
  const searchParams = () => api.get.mock.calls.find((c) => c[0] === "/search")[1];

  beforeEach(() => {
    api.getConfig.mockReturnValue(DEFAULTS_CFG);
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
  });

  it("applies the default labels and sort when the URL names none, and shows them in the drawer", async () => {
    setLocation("/search?q=foo");
    runFromUrl();
    await settle();
    expect(searchParams()["fields.label"]).toEqual(["lblA"]);
    expect(searchParams().sort).toBe("last_modified.desc");
    expect(document.getElementById("labelSearchOption").value).toBe("lblA");
    expect(document.getElementById("sortSearchOption").value).toBe("last_modified.desc");
  });

  it("keeps the labels and sort the URL names", async () => {
    setLocation("/search?q=foo&fields.label=lblB&sort=score.desc");
    runFromUrl();
    await settle();
    expect(searchParams()["fields.label"]).toEqual(["lblB"]);
    expect(searchParams().sort).toBe("score.desc");
  });

  it("adds no default label when the URL has an empty fields.label", async () => {
    setLocation("/search?q=foo&fields.label=");
    runFromUrl();
    await settle();
    expect(searchParams()).not.toHaveProperty(["fields.label"]);
  });

  it("narrows the search with a label facet click instead of adding the label to the defaults", async () => {
    setLocation("/search?q=foo");
    runFromUrl();
    await settle();
    api.get.mockClear();
    const facetLink = [...document.querySelectorAll("#facet-body li.list-group-item a")]
      .find((a) => a.textContent.startsWith("Label B"));
    facetLink.click();
    await settle();
    // JSP parity (searchResults.jsp): a facet link adds ex_q=label:<value>, which ANDs.
    expect(searchParams()["fields.label"]).toEqual(["lblA"]);
    expect(searchParams()["ex_q"]).toContain("label:lblB");
  });

  it("does not turn an empty search into a search by applying defaults", () => {
    setLocation("/search?sort=");
    runFromUrl();
    expect(navigate).toHaveBeenCalledWith("./", { replace: true });
    expect(api.get).not.toHaveBeenCalled();
  });

  it("remembers an explicit num for the tab and uses it when the URL has none", async () => {
    setLocation("/search?q=a&num=50");
    runFromUrl();
    await settle();
    expect(sessionStorage.getItem("fess.search.num")).toBe("50");
    api.get.mockClear();
    setLocation("/search?q=b");
    runFromUrl();
    await settle();
    expect(searchParams().num).toBe(50);
  });

  it("starts from page_size_default when nothing is remembered", async () => {
    setLocation("/search?q=a");
    runFromUrl();
    await settle();
    expect(searchParams().num).toBe(20);
  });
});

describe("initialNum / forgetNum", () => {
  const CFG = { page_size_default: 20, page_size_max: 100, num_options: [10, 20, 50] };

  it.each([
    ["50", 50],   // an offered size
    ["30", 20],   // rounded down to an offered size
    ["500", 50],  // capped at page_size_max, then rounded down
    ["5", 10],    // below every offered size → the smallest
    ["abc", 20],  // unreadable → page_size_default
    ["0", 20],    // not positive → page_size_default
  ])("turns a remembered %s into %i", (stored, expected) => {
    api.getConfig.mockReturnValue(CFG);
    sessionStorage.setItem("fess.search.num", stored);
    expect(initialNum()).toBe(expected);
  });

  it("caps a remembered size at page_size_max when no num options are offered", () => {
    api.getConfig.mockReturnValue({ page_size_default: 20, page_size_max: 100 });
    sessionStorage.setItem("fess.search.num", "300");
    expect(initialNum()).toBe(100);
  });

  it("falls back to 10 without config", () => {
    api.getConfig.mockReturnValue(null);
    expect(initialNum()).toBe(10);
  });

  it("forgetNum() drops the remembered size", () => {
    api.getConfig.mockReturnValue(CFG);
    sessionStorage.setItem("fess.search.num", "50");
    forgetNum();
    expect(sessionStorage.getItem("fess.search.num")).toBeNull();
    expect(initialNum()).toBe(20);
  });
});

describe("clearSearchState — home defaults", () => {
  it("pre-selects the default labels, sort and page size", () => {
    api.getConfig.mockReturnValue({
      ...FULL_CFG,
      default_label_values: ["lblA"],
      default_sort: "last_modified.desc",
      page_size_default: 20,
    });
    mountBody(SEARCH_FIXTURE);
    sessionStorage.setItem("fess.search.num", "50");

    clearSearchState();

    expect(_state.num).toBe(50);
    expect(_state.sort).toBe("last_modified.desc");
    expect(_state.fields).toEqual({ label: ["lblA"] });
    expect(document.getElementById("numSearchOption").value).toBe("50");
    expect(document.getElementById("sortSearchOption").value).toBe("last_modified.desc");
    expect(document.getElementById("labelSearchOption").value).toBe("lblA");
  });
});

describe("renderCurrentFilters — default page size", () => {
  it("shows no page-size badge for page_size_default", async () => {
    api.getConfig.mockReturnValue({ ...FULL_CFG, page_size_default: 20 });
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
    _state.q = "foo";
    _state.num = 20;
    await runSearch();
    await settle();
    expect(document.getElementById("current-filters").textContent).not.toContain("search.num_format");
  });
});

describe("runFromUrl — legacy JSP parameters", () => {
  const searchParams = () => api.get.mock.calls.find((c) => c[0] === "/search")[1];

  beforeEach(() => {
    api.getConfig.mockReturnValue(FULL_CFG);
    installApiDispatch();
    mountBody(SEARCH_FIXTURE);
  });

  it("passes sdh and as.* from the URL to the search API", async () => {
    setLocation("/search?q=foo&sdh=abc&as.q=bar&as.filetype=pdf&as.filetype=html&as.nq=");
    runFromUrl();
    await settle();
    expect(searchParams().sdh).toBe("abc");
    expect(searchParams()["as.q"]).toEqual(["bar"]);
    expect(searchParams()["as.filetype"]).toEqual(["pdf", "html"]);
    expect(Object.keys(searchParams())).not.toContain("as.nq");
  });

  it("runs a search for an advanced-search condition without a keyword", async () => {
    setLocation("/search?as.epq=exact%20phrase");
    runFromUrl();
    await settle();
    expect(navigate).not.toHaveBeenCalled();
    expect(searchParams()["as.epq"]).toEqual(["exact phrase"]);
  });

  it("goes home for as.occt alone, which only narrows a search", () => {
    setLocation("/search?as.occt=title");
    runFromUrl();
    expect(navigate).toHaveBeenCalledWith("./", { replace: true });
    expect(api.get).not.toHaveBeenCalled();
  });

  it("drops sdh and as.* once the URL has none", async () => {
    setLocation("/search?q=foo&sdh=abc&as.q=bar");
    runFromUrl();
    await settle();
    api.get.mockClear();
    setLocation("/search?q=next");
    runFromUrl();
    await settle();
    expect(Object.keys(searchParams())).not.toContain("sdh");
    expect(Object.keys(searchParams()).filter((k) => k.startsWith("as."))).toEqual([]);
  });
});
