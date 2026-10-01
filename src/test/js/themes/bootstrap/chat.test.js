// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bootstrap theme chat module (chat.js).
//
// The pure/DOM-builder helpers (safeHref, sourceIcon, sourceTypeLabel,
// errorCodeToText, buildPhaseStrip, buildFilterPanel) are exported for test and
// exercised through the real, unmodified asset via a static import. The stateful
// attach* entry points hold module-level singletons (currentStream, sessionId,
// inlineMounted, standaloneMounted, routeListenerAttached) with no reset hook, so
// those tests use vi.resetModules() + a dynamic import to get a fresh instance.
//
// ./api.js is mocked so getConfig()/sseStream()/getCsrfToken() are controllable;
// i18n.js/format.js/markdown.js run for real. With empty i18n messages, t(key)
// returns the key unchanged, so assertions match exact i18n keys.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { resetDom, mountBody } from "../../helpers/dom.js";
import { jsonResponse } from "../../helpers/net.js";

const CHAT_PATH = "../../../../main/webapp/themes/bootstrap/assets/chat.js";
const I18N_PATH = "../../../../main/webapp/themes/bootstrap/assets/i18n.js";

// Controllable stand-in for the /api/v2 client. chat.js only reads
// api.getConfig(), api.sseStream() and api.getCsrfToken().
const apiMock = vi.hoisted(() => ({
  getConfig: vi.fn(),
  getCsrfToken: vi.fn(),
  sseStream: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => apiMock);

import {
  safeHref,
  sourceIcon,
  sourceTypeLabel,
  errorCodeToText,
  buildPhaseStrip,
  buildFilterPanel,
} from "../../../../main/webapp/themes/bootstrap/assets/chat.js";

beforeEach(() => {
  resetDom();
  apiMock.getConfig.mockReset().mockReturnValue(null);
  apiMock.getCsrfToken.mockReset().mockReturnValue("");
  apiMock.sseStream.mockReset().mockReturnValue({ abort() {} });
});
afterEach(() => {
  vi.unstubAllGlobals();
});

// jsdom's default document URL — the base safeHref resolves relative URLs against.
const ORIGIN = "http://localhost:3000";

// ---------------------------------------------------------------------------
// safeHref
// ---------------------------------------------------------------------------

describe("safeHref", () => {
  it("keeps an absolute http(s) URL", () => {
    expect(safeHref("https://x/y")).toBe("https://x/y");
    expect(safeHref("http://a.example/p")).toBe("http://a.example/p");
  });

  it("keeps a mailto: URL", () => {
    expect(safeHref("mailto:a@b")).toBe("mailto:a@b");
  });

  it("resolves a relative URL against window.location.origin", () => {
    expect(safeHref("/foo/bar")).toBe(ORIGIN + "/foo/bar");
    expect(safeHref("foo")).toBe(ORIGIN + "/foo");
  });

  it("rejects a javascript: scheme", () => {
    expect(safeHref("javascript:alert(1)")).toBe("#");
  });

  it("rejects other non-allowlisted schemes (ftp, data)", () => {
    expect(safeHref("ftp://host/file")).toBe("#");
    expect(safeHref("data:text/html,<b>x</b>")).toBe("#");
  });

  it("returns '#' for empty / nullish input", () => {
    expect(safeHref("")).toBe("#");
    expect(safeHref(null)).toBe("#");
    expect(safeHref(undefined)).toBe("#");
  });
});

// ---------------------------------------------------------------------------
// sourceIcon
// ---------------------------------------------------------------------------

describe("sourceIcon", () => {
  it("maps mimetype to a Font Awesome class", () => {
    expect(sourceIcon(undefined, "application/pdf")).toBe("fa-file-pdf-o");
    expect(sourceIcon(undefined, "application/msword")).toBe("fa-file-word-o");
    expect(sourceIcon(undefined, "text/x-document")).toBe("fa-file-word-o");
    expect(sourceIcon(undefined, "application/vnd.ms-excel")).toBe("fa-file-excel-o");
    expect(sourceIcon(undefined, "application/x-spreadsheet")).toBe("fa-file-excel-o");
    expect(sourceIcon(undefined, "application/vnd.ms-powerpoint")).toBe("fa-file-powerpoint-o");
    expect(sourceIcon(undefined, "application/x-presentation")).toBe("fa-file-powerpoint-o");
    expect(sourceIcon(undefined, "image/png")).toBe("fa-file-image-o");
    expect(sourceIcon(undefined, "text/plain")).toBe("fa-file-text-o");
  });

  it("matches word/document before excel/powerpoint: OpenDocument mimetypes hit the Word icon", () => {
    // Quirk: the substring "document" (in every *.opendocument.* /
    // *.officedocument.* mimetype) matches the word branch, which is tested
    // before the spreadsheet/presentation branches. So ODS/ODP resolve to Word.
    expect(sourceIcon(undefined, "application/vnd.oasis.opendocument.spreadsheet")).toBe("fa-file-word-o");
    expect(sourceIcon(undefined, "application/vnd.oasis.opendocument.presentation")).toBe("fa-file-word-o");
    // Extension-based routing is unaffected — it takes the .xlsx/.pptx path.
    expect(sourceIcon("book.ods")).toBe("fa-file-o"); // .ods not in the extension switch
  });

  it("maps url extension when no mimetype is given", () => {
    expect(sourceIcon("a/b.docx")).toBe("fa-file-word-o");
    expect(sourceIcon("doc.pdf")).toBe("fa-file-pdf-o");
    expect(sourceIcon("sheet.xlsx")).toBe("fa-file-excel-o");
    expect(sourceIcon("deck.pptx")).toBe("fa-file-powerpoint-o");
    expect(sourceIcon("pic.png")).toBe("fa-file-image-o");
    expect(sourceIcon("notes.md")).toBe("fa-file-text-o");
    expect(sourceIcon("x.html")).toBe("fa-globe");
    expect(sourceIcon("x.htm")).toBe("fa-globe");
  });

  it("mimetype takes precedence over the url extension", () => {
    // .html would map to fa-globe, but the pdf mimetype wins.
    expect(sourceIcon("page.html", "application/pdf")).toBe("fa-file-pdf-o");
  });

  it("strips a query string before matching the extension", () => {
    expect(sourceIcon("file.pdf?x=1")).toBe("fa-file-pdf-o");
  });

  it("falls back to fa-file-o for unknown extension / no input", () => {
    expect(sourceIcon("x.xyz")).toBe("fa-file-o");
    expect(sourceIcon(undefined, undefined)).toBe("fa-file-o");
    expect(sourceIcon(undefined, "application/octet-stream")).toBe("fa-file-o");
  });
});

// ---------------------------------------------------------------------------
// sourceTypeLabel
// ---------------------------------------------------------------------------

describe("sourceTypeLabel", () => {
  it("maps mimetype to a human label", () => {
    expect(sourceTypeLabel(undefined, "application/pdf")).toBe("PDF");
    expect(sourceTypeLabel(undefined, "application/msword")).toBe("Word");
    expect(sourceTypeLabel(undefined, "application/vnd.ms-excel")).toBe("Excel");
    expect(sourceTypeLabel(undefined, "application/vnd.ms-powerpoint")).toBe("PowerPoint");
    expect(sourceTypeLabel(undefined, "image/gif")).toBe("Image");
  });

  it("maps url extension when no mimetype is given", () => {
    expect(sourceTypeLabel("a.docx")).toBe("Word");
    expect(sourceTypeLabel("a.pdf")).toBe("PDF");
    expect(sourceTypeLabel("a.xls")).toBe("Excel");
    expect(sourceTypeLabel("a.ppt")).toBe("PowerPoint");
    expect(sourceTypeLabel("a.jpeg")).toBe("Image");
    expect(sourceTypeLabel("a.md")).toBe("Text");
    expect(sourceTypeLabel("a.html")).toBe("Web");
  });

  it("mimetype takes precedence over the url extension", () => {
    expect(sourceTypeLabel("page.html", "application/pdf")).toBe("PDF");
  });

  it("falls back to the i18n document label for unknown input", () => {
    expect(sourceTypeLabel("x.xyz")).toBe("labels.chat_source_type_document");
    expect(sourceTypeLabel(undefined, undefined)).toBe("labels.chat_source_type_document");
    // Note: unlike sourceIcon, sourceTypeLabel has no text/* mimetype branch,
    // so a text mimetype with no url falls through to the default label.
    expect(sourceTypeLabel(undefined, "text/plain")).toBe("labels.chat_source_type_document");
  });
});

// ---------------------------------------------------------------------------
// errorCodeToText
// ---------------------------------------------------------------------------

describe("errorCodeToText", () => {
  it("maps every known error code to error.<code>", () => {
    for (const code of [
      "rate_limit",
      "auth_error",
      "service_unavailable",
      "timeout",
      "context_length_exceeded",
      "model_not_found",
      "invalid_response",
      "connection_error",
    ]) {
      expect(errorCodeToText(code)).toBe("error." + code);
    }
  });

  it("falls back to error.server for unknown / missing codes", () => {
    expect(errorCodeToText("bogus")).toBe("error.server");
    expect(errorCodeToText(undefined)).toBe("error.server");
    expect(errorCodeToText("")).toBe("error.server");
  });
});

// ---------------------------------------------------------------------------
// buildPhaseStrip
// ---------------------------------------------------------------------------

describe("buildPhaseStrip", () => {
  const badge = (strip, phase) => strip.querySelector(`[data-step="${phase}"]`);

  it("builds one badge per phase in order", () => {
    const { strip, advanceTo, complete, reset } = buildPhaseStrip();
    expect(typeof advanceTo).toBe("function");
    expect(typeof complete).toBe("function");
    expect(typeof reset).toBe("function");
    expect(strip.classList.contains("chat-phase-strip")).toBe(true);
    const steps = [...strip.querySelectorAll(".chat-step")].map((b) => b.getAttribute("data-step"));
    expect(steps).toEqual(["intent", "search", "evaluate", "fetch", "answer"]);
    // Fresh strip: every badge is bg-secondary.
    for (const p of steps) expect(badge(strip, p).classList.contains("bg-secondary")).toBe(true);
  });

  it("advanceTo marks prior phases success, current primary, later secondary", () => {
    const { strip, advanceTo } = buildPhaseStrip();
    advanceTo("evaluate");
    expect(badge(strip, "intent").classList.contains("bg-success")).toBe(true);
    expect(badge(strip, "search").classList.contains("bg-success")).toBe(true);
    expect(badge(strip, "evaluate").classList.contains("bg-primary")).toBe(true);
    expect(badge(strip, "fetch").classList.contains("bg-secondary")).toBe(true);
    expect(badge(strip, "answer").classList.contains("bg-secondary")).toBe(true);
  });

  it("advanceTo ignores an unknown phase", () => {
    const { strip, advanceTo } = buildPhaseStrip();
    advanceTo("nope");
    // Unchanged: all still bg-secondary.
    for (const p of ["intent", "search", "evaluate", "fetch", "answer"]) {
      expect(badge(strip, p).classList.contains("bg-secondary")).toBe(true);
    }
  });

  it("complete('search', n) turns the badge success and adds one hit-count span", () => {
    const { strip, complete } = buildPhaseStrip();
    complete("search", 42);
    const b = badge(strip, "search");
    expect(b.classList.contains("bg-success")).toBe(true);
    expect(b.classList.contains("bg-primary")).toBe(false);
    const counts = b.querySelectorAll(".chat-hit-count");
    expect(counts.length).toBe(1);
    // countText comes from t("labels.chat_hit_count", {count}); with empty i18n
    // the key has no {count} placeholder, so it renders the key verbatim.
    expect(counts[0].textContent).toBe(" (labels.chat_hit_count)");
  });

  it("complete replaces (does not stack) the hit-count span", () => {
    const { strip, complete } = buildPhaseStrip();
    complete("search", 1);
    complete("search", 2);
    expect(badge(strip, "search").querySelectorAll(".chat-hit-count").length).toBe(1);
  });

  it("complete without a hit count adds no span", () => {
    const { strip, complete } = buildPhaseStrip();
    complete("evaluate");
    const b = badge(strip, "evaluate");
    expect(b.classList.contains("bg-success")).toBe(true);
    expect(b.querySelectorAll(".chat-hit-count").length).toBe(0);
  });

  it("reset returns all badges to secondary and removes hit counts", () => {
    const { strip, advanceTo, complete, reset } = buildPhaseStrip();
    advanceTo("answer");
    complete("search", 7);
    reset();
    for (const p of ["intent", "search", "evaluate", "fetch", "answer"]) {
      const b = badge(strip, p);
      expect(b.classList.contains("bg-secondary")).toBe(true);
      expect(b.classList.contains("bg-primary")).toBe(false);
      expect(b.classList.contains("bg-success")).toBe(false);
    }
    expect(badge(strip, "search").querySelectorAll(".chat-hit-count").length).toBe(0);
  });
});

// ---------------------------------------------------------------------------
// buildFilterPanel
// ---------------------------------------------------------------------------

describe("buildFilterPanel", () => {
  it("returns a hidden placeholder panel with empty filters when nothing is configured", () => {
    apiMock.getConfig.mockReturnValue(null);
    const { panel, getFilters, resetFilters } = buildFilterPanel();
    expect(panel.hidden).toBe(true);
    expect(getFilters()).toEqual({ fields: [], extraQ: [] });
    // resetFilters is a callable no-op so the New Chat handler can call it freely.
    expect(() => resetFilters()).not.toThrow();
  });

  it("also treats an empty-object config as no filters", () => {
    apiMock.getConfig.mockReturnValue({});
    const { panel, getFilters } = buildFilterPanel();
    expect(panel.hidden).toBe(true);
    expect(getFilters()).toEqual({ fields: [], extraQ: [] });
  });

  it("renders label options and facet-view queries as checkboxes", () => {
    apiMock.getConfig.mockReturnValue({
      label_options: [
        { value: "label1", name: "Label One" },
        { value: "label2", name: "Label Two" },
      ],
      facet_views: [
        { title: "Time", queries: [{ label: "Recent", value: "ts:[now-1M TO now]" }] },
      ],
    });
    const { panel } = buildFilterPanel();
    expect(panel.hidden).toBe(false);
    const labelCbs = panel.querySelectorAll('input[data-filter-type="label"]');
    const exqCbs = panel.querySelectorAll('input[data-filter-type="ex_q"]');
    expect(labelCbs.length).toBe(2);
    expect(exqCbs.length).toBe(1);
    expect(labelCbs[0].getAttribute("data-filter-value")).toBe("label1");
    expect(exqCbs[0].getAttribute("data-filter-value")).toBe("ts:[now-1M TO now]");
  });

  it("collects checked label/ex_q values into getFilters().fields / .extraQ", () => {
    apiMock.getConfig.mockReturnValue({
      label_options: [{ value: "label1", name: "One" }],
      facet_views: [{ title: "T", queries: [{ label: "R", value: "exq1" }] }],
    });
    const { panel, getFilters } = buildFilterPanel();
    expect(getFilters()).toEqual({ fields: [], extraQ: [] });

    const labelCb = panel.querySelector('input[data-filter-type="label"]');
    const exqCb = panel.querySelector('input[data-filter-type="ex_q"]');
    labelCb.checked = true;
    exqCb.checked = true;
    expect(getFilters()).toEqual({ fields: ["label1"], extraQ: ["exq1"] });
  });

  it("updates the count badge and toggles d-none as checkboxes change", () => {
    apiMock.getConfig.mockReturnValue({
      label_options: [{ value: "label1", name: "One" }],
      facet_views: [{ title: "T", queries: [{ label: "R", value: "exq1" }] }],
    });
    const { panel } = buildFilterPanel();
    const mainBadge = panel.querySelector(".badge.bg-primary");
    expect(mainBadge.textContent).toBe("0");
    expect(mainBadge.classList.contains("d-none")).toBe(true);

    const labelCb = panel.querySelector('input[data-filter-type="label"]');
    const exqCb = panel.querySelector('input[data-filter-type="ex_q"]');
    labelCb.checked = true;
    labelCb.dispatchEvent(new Event("change"));
    exqCb.checked = true;
    exqCb.dispatchEvent(new Event("change"));
    expect(mainBadge.textContent).toBe("2");
    expect(mainBadge.classList.contains("d-none")).toBe(false);
  });

  it("resetFilters unchecks everything and restores the badge", () => {
    apiMock.getConfig.mockReturnValue({
      label_options: [{ value: "label1", name: "One" }],
      facet_views: [{ title: "T", queries: [{ label: "R", value: "exq1" }] }],
    });
    const { panel, getFilters, resetFilters } = buildFilterPanel();
    const mainBadge = panel.querySelector(".badge.bg-primary");
    const labelCb = panel.querySelector('input[data-filter-type="label"]');
    const exqCb = panel.querySelector('input[data-filter-type="ex_q"]');
    labelCb.checked = true;
    labelCb.dispatchEvent(new Event("change"));
    exqCb.checked = true;
    exqCb.dispatchEvent(new Event("change"));

    resetFilters();
    expect(getFilters()).toEqual({ fields: [], extraQ: [] });
    expect(labelCb.checked).toBe(false);
    expect(exqCb.checked).toBe(false);
    expect(mainBadge.textContent).toBe("0");
    expect(mainBadge.classList.contains("d-none")).toBe(true);
  });

  it("shows the label name, not its value, next to each label checkbox", () => {
    apiMock.getConfig.mockReturnValue({ label_options: [{ value: "dept1", name: "Planning" }] });
    const { panel } = buildFilterPanel();
    const row = panel.querySelector('input[data-filter-type="label"]').parentElement;
    expect(row.textContent).toBe("Planning");
  });

  it("accepts label options given as plain strings", () => {
    apiMock.getConfig.mockReturnValue({ label_options: ["plainLabel"] });
    const { panel } = buildFilterPanel();
    const cb = panel.querySelector('input[data-filter-type="label"]');
    expect(cb.getAttribute("data-filter-value")).toBe("plainLabel");
  });

  it("accepts facet-view queries given as an object map", () => {
    apiMock.getConfig.mockReturnValue({
      facet_views: [{ title: "T", queries: { Recent: "q1", Old: "q2" } }],
    });
    const { panel, getFilters } = buildFilterPanel();
    const cbs = panel.querySelectorAll('input[data-filter-type="ex_q"]');
    expect([...cbs].map((c) => c.getAttribute("data-filter-value"))).toEqual(["q1", "q2"]);
    cbs[1].checked = true;
    expect(getFilters()).toEqual({ fields: [], extraQ: ["q2"] });
  });
});

// ---------------------------------------------------------------------------
// attach flow — request-body contract (stateful; fresh module per test)
// ---------------------------------------------------------------------------

describe("attachInline / attachStandalone submit", () => {
  const enterKey = (opts = {}) => new KeyboardEvent("keydown", { key: "Enter", bubbles: true, ...opts });

  it("attachInline: Enter posts {message} to /chat/stream via sseStream", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: true } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-column" class="d-none"></div>');
    chat.attachInline();

    const input = document.getElementById("chat-input-inline");
    expect(input).not.toBeNull();
    input.value = "hello world";
    input.dispatchEvent(enterKey());

    expect(apiMock.sseStream).toHaveBeenCalledTimes(1);
    const [url, body, onEvent, onError] = apiMock.sseStream.mock.calls[0];
    expect(url).toBe("/chat/stream");
    expect(body).toEqual({ message: "hello world" });
    expect(typeof onEvent).toBe("function");
    expect(typeof onError).toBe("function");
  });

  it("attachInline: Shift+Enter inserts a newline and does not submit", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: true } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-column"></div>');
    chat.attachInline();

    const input = document.getElementById("chat-input-inline");
    input.value = "line";
    input.dispatchEvent(enterKey({ shiftKey: true }));
    expect(apiMock.sseStream).not.toHaveBeenCalled();
  });

  it("attachInline: an active IME composition suppresses the Enter submit", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: true } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-column"></div>');
    chat.attachInline();

    const input = document.getElementById("chat-input-inline");
    input.value = "かな";
    input.dispatchEvent(new Event("compositionstart"));
    input.dispatchEvent(enterKey());
    expect(apiMock.sseStream).not.toHaveBeenCalled();

    // After composition ends, Enter submits normally.
    input.dispatchEvent(new Event("compositionend"));
    input.dispatchEvent(enterKey());
    expect(apiMock.sseStream).toHaveBeenCalledTimes(1);
  });

  it("attachInline: no-op when rag chat is disabled", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: false } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-column" class="d-none"></div>');
    chat.attachInline();
    expect(document.getElementById("chat-input-inline")).toBeNull();
  });

  it("attachInline: no-op when #chat-column is absent", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: true } });
    const chat = await import(CHAT_PATH);
    mountBody("<div></div>");
    expect(() => chat.attachInline()).not.toThrow();
    expect(apiMock.sseStream).not.toHaveBeenCalled();
  });

  it("attachStandalone: Enter posts {message, fields.label, extra_queries} from active filters", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({
      features: { rag_chat_enabled: true },
      label_options: [{ value: "lblA", name: "A" }],
      facet_views: [{ title: "T", queries: [{ label: "R", value: "exq1" }] }],
    });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-view" hidden></div>');
    chat.attachStandalone();

    document.querySelector('input[data-filter-type="label"]').checked = true;
    document.querySelector('input[data-filter-type="ex_q"]').checked = true;

    const ta = document.getElementById("standalone-chat-input");
    expect(ta).not.toBeNull();
    ta.value = "q text";
    ta.dispatchEvent(enterKey());

    expect(apiMock.sseStream).toHaveBeenCalledTimes(1);
    const [url, body] = apiMock.sseStream.mock.calls[0];
    expect(url).toBe("/chat/stream");
    expect(body).toEqual({
      message: "q text",
      fields: { label: ["lblA"] },
      extra_queries: ["exq1"],
    });
  });

  it("attachStandalone: send button with no active filters posts just {message}", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: true } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-view" hidden></div>');
    chat.attachStandalone();

    const ta = document.getElementById("standalone-chat-input");
    ta.value = "just a question";
    document.getElementById("standalone-send-btn").dispatchEvent(new Event("click"));

    expect(apiMock.sseStream).toHaveBeenCalledTimes(1);
    expect(apiMock.sseStream.mock.calls[0][1]).toEqual({ message: "just a question" });
  });

  it("attachStandalone: renders the disabled notice when rag chat is off", async () => {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue({ features: { rag_chat_enabled: false } });
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-view" hidden></div>');
    chat.attachStandalone();

    const view = document.getElementById("chat-view");
    const alert = view.querySelector(".alert.alert-warning");
    expect(alert).not.toBeNull();
    expect(alert.textContent).toBe("chat.disabled");
    expect(document.getElementById("standalone-chat-input")).toBeNull();
  });
});

// ---------------------------------------------------------------------------
// Document mode ("Ask about this document")
// ---------------------------------------------------------------------------

describe("attachStandalone document mode", () => {
  // Document mode renders real sentences, so these tests load the shipped English bundle.
  // fileURLToPath gets the URL as a string: under jsdom the global URL is a foreign class.
  const en = JSON.parse(readFileSync(resolve(dirname(fileURLToPath(import.meta.url)),
    "../../../../main/webapp/themes/bootstrap/i18n/messages.en.json"), "utf-8"));
  const enterKey = () => new KeyboardEvent("keydown", { key: "Enter", bubbles: true });
  const CFG = {
    features: { rag_chat_enabled: true },
    label_options: [{ value: "lblA", name: "A" }],
    facet_views: [{ title: "T", queries: [{ label: "R", value: "exq1" }] }],
  };

  /** Point the location at /chat?doc_id=… with an optional title in the history state. */
  function openDocumentRoute(docId, title) {
    const qs = docId ? "?doc_id=" + encodeURIComponent(docId) : "";
    history.replaceState(title ? { docTitle: title } : null, "", "/chat" + qs);
  }

  async function mount(docId, title) {
    vi.resetModules();
    apiMock.getConfig.mockReturnValue(CFG);
    openDocumentRoute(docId, title);
    const i18n = await import(I18N_PATH);
    await i18n.init("en");
    fetch.mockClear();
    const chat = await import(CHAT_PATH);
    mountBody('<div id="chat-view" hidden></div>');
    chat.attachStandalone();
    return chat;
  }

  const input = () => document.getElementById("standalone-chat-input");
  const banner = () => document.querySelector(".chat-doc-banner");
  const isShown = (e) => !e.classList.contains("d-none");
  const ask = (q) => {
    input().value = q;
    input().dispatchEvent(enterKey());
  };
  const lastCall = () => apiMock.sseStream.mock.calls[apiMock.sseStream.mock.calls.length - 1];
  const emit = (event) => lastCall()[2](event);
  const fail = (err) => lastCall()[3](err);
  const errorText = () => document.querySelector(".chat-error-banner span.flex-grow-1").textContent;
  const clickClear = () => banner().querySelector("button.btn-close").dispatchEvent(new Event("click"));

  beforeEach(() => {
    vi.stubGlobal("fetch", vi.fn(async (url) =>
      String(url).includes("/i18n/messages.") ? jsonResponse(en) : { ok: true }));
  });
  afterEach(() => {
    history.replaceState(null, "", "/");
  });

  it("shows no banner and keeps the filters in normal chat", async () => {
    await mount("", "");
    expect(isShown(banner())).toBe(false);
    expect(document.querySelector('[data-bs-toggle="collapse"]').classList.contains("d-none")).toBe(false);
  });

  it("shows the banner with the title (as text, never HTML) and hides the filter controls", async () => {
    await mount("d1", "<img src=x onerror=alert(1)> Doc");
    expect(isShown(banner())).toBe(true);
    expect(banner().textContent).toBe("Asking about: <img src=x onerror=alert(1)> Doc");
    expect(banner().querySelector("img")).toBeNull();
    expect(document.querySelector('[data-bs-toggle="collapse"]').classList.contains("d-none")).toBe(true);
    expect(document.querySelector(".chat-filter-panel").classList.contains("d-none")).toBe(true);
  });

  it("falls back to a generic label and adopts the title of the first source", async () => {
    await mount("d1", "");
    expect(banner().textContent).toBe("Asking about: this document");
    ask("summarise");
    emit({ type: "sources", data: { sources: [{ title: "Server Title <b>", url: "https://e/1" }] } });
    expect(banner().textContent).toBe("Asking about: Server Title <b>");
    expect(banner().querySelector("b")).toBeNull();
    expect(banner().getAttribute("title")).toBe("Asking about: Server Title <b>");
  });

  it("keeps a title from the navigation over the one in a later source", async () => {
    await mount("d1", "From results");
    ask("q");
    emit({ type: "sources", data: { sources: [{ title: "Other", url: "https://e/1" }] } });
    expect(banner().textContent).toBe("Asking about: From results");
  });

  it("sends doc_id on every turn and never the label filters or extra_queries", async () => {
    await mount("doc-1", "My Doc");
    // Even a filter ticked before entering document mode must not leak into the request.
    document.querySelector('input[data-filter-type="label"]').checked = true;
    document.querySelector('input[data-filter-type="ex_q"]').checked = true;

    ask("first");
    expect(apiMock.sseStream).toHaveBeenCalledTimes(1);
    expect(lastCall()[0]).toBe("/chat/stream");
    expect(lastCall()[1]).toEqual({ message: "first", doc_id: "doc-1" });
    emit({ type: "done", data: { session_id: "sess-1" } });

    ask("second");
    expect(apiMock.sseStream).toHaveBeenCalledTimes(2);
    expect(lastCall()[1]).toEqual({ message: "second", session_id: "sess-1", doc_id: "doc-1" });
  });

  it("sends the filters (and no doc_id) in normal chat", async () => {
    await mount("", "");
    document.querySelector('input[data-filter-type="label"]').checked = true;
    ask("q");
    expect(lastCall()[1]).toEqual({ message: "q", fields: { label: ["lblA"] } });
  });

  it("clearing the banner returns to normal chat: new session, no doc_id, URL cleaned", async () => {
    await mount("doc-1", "My Doc");
    ask("first");
    emit({ type: "done", data: { session_id: "sess-1" } });

    clickClear();

    expect(isShown(banner())).toBe(false);
    expect(location.search).toBe("");
    expect(history.state).toBeNull();
    expect(document.querySelector('[data-bs-toggle="collapse"]').classList.contains("d-none")).toBe(false);
    // The old server session is deleted and the log is back to the welcome state.
    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch.mock.calls[0][0]).toBe("api/v2/chat/sessions/sess-1");
    expect(fetch.mock.calls[0][1].method).toBe("DELETE");
    expect(document.querySelector(".chat-msg-wrap")).toBeNull();

    ask("plain question");
    expect(lastCall()[1]).toEqual({ message: "plain question" });
  });

  it("New chat also leaves document mode", async () => {
    await mount("doc-1", "My Doc");
    ask("first");
    emit({ type: "done", data: { session_id: "sess-1" } });

    const newChat = [...document.querySelectorAll(".card-header button")]
      .find((b) => b.textContent.includes(en["labels.chat_new_chat"]));
    newChat.dispatchEvent(new Event("click"));

    expect(isShown(banner())).toBe(false);
    expect(location.search).toBe("");
    ask("again");
    expect(lastCall()[1]).toEqual({ message: "again" });
  });

  it("a 404 before streaming shows the document-unavailable message", async () => {
    const { ApiError } = await vi.importActual("../../../../main/webapp/themes/bootstrap/assets/api.js");
    await mount("gone", "Gone");
    ask("hello");
    fail(new ApiError("NOT_FOUND", "Document not found", 404));
    expect(errorText()).toBe("This document is not available or you do not have access to it.");
    // Input is usable again so the user can clear the banner and carry on.
    expect(input().disabled).toBe(false);
  });

  it("other failures in document mode keep the generic messages", async () => {
    const { ApiError, NetworkError } = await vi.importActual("../../../../main/webapp/themes/bootstrap/assets/api.js");
    await mount("d1", "T");
    ask("a");
    fail(new ApiError("INTERNAL", "boom", 500));
    expect(errorText()).toBe(en["error.server"]);
    ask("b");
    fail(new NetworkError(new Error("offline")));
    expect(errorText()).toBe(en["error.network"]);
  });

  it("a 404 in normal chat stays the generic server error", async () => {
    const { ApiError } = await vi.importActual("../../../../main/webapp/themes/bootstrap/assets/api.js");
    await mount("", "");
    ask("hello");
    fail(new ApiError("NOT_FOUND", "nope", 404));
    expect(errorText()).toBe(en["error.server"]);
  });

  it("shows localized part progress while a long document is summarized", async () => {
    await mount("d1", "T");
    ask("q");
    const progress = () => document.getElementById("chat-progress-message").textContent;
    emit({ type: "phase", data: { phase: "fetch", status: "start", message: "Retrieving document content..." } });
    expect(progress()).toBe(en["labels.chat_phase_fetch"]);
    emit({ type: "phase", data: { phase: "fetch", status: "complete", parts: 3 } });
    emit({ type: "phase", data: { phase: "fetch", status: "start", message: "Summarizing document part 1 of 3..." } });
    expect(progress()).toBe(en["labels.chat_phase_fetch_part"].replace("{0}", "1").replace("{1}", "3"));
    emit({ type: "phase", data: { phase: "fetch", status: "complete", part: 1, parts: 3 } });
    emit({ type: "phase", data: { phase: "fetch", status: "start", message: "Summarizing document part 2 of 3..." } });
    expect(progress()).toBe(en["labels.chat_phase_fetch_part"].replace("{0}", "2").replace("{1}", "3"));
    emit({ type: "phase", data: { phase: "answer", status: "start" } });
    expect(progress()).toBe(en["labels.chat_phase_answer"]);
  });

  it("ignores the server's English phase message for a short document", async () => {
    await mount("d1", "T");
    ask("q");
    emit({ type: "phase", data: { phase: "fetch", status: "start", message: "Retrieving document content..." } });
    emit({ type: "phase", data: { phase: "fetch", status: "complete" } });
    emit({ type: "phase", data: { phase: "fetch", status: "start", message: "Something else" } });
    expect(document.getElementById("chat-progress-message").textContent).toBe(en["labels.chat_phase_fetch"]);
  });

  it("shows warnings: truncation in document mode, a generic note otherwise", async () => {
    await mount("d1", "T");
    ask("q");
    emit({ type: "warning", data: { phase: "fetch", code: "document_truncated" } });
    const status = () => document.querySelector(".chat-status-line");
    expect(status().textContent).toBe(en["labels.chat_warning_document_truncated"]);
    expect(status().classList.contains("d-none")).toBe(false);
    emit({ type: "warning", data: { phase: "answer", code: "something_new" } });
    expect(status().textContent).toBe(en["labels.chat_warning_generic"]);
    emit({ type: "warning", data: { phase: "intent", code: "token_exhausted" } });
    expect(status().textContent).toBe(en["labels.chat_warning_token_exhausted"]);
  });

  it("shows the generic warning in normal chat too instead of dropping it", async () => {
    await mount("", "");
    ask("q");
    emit({ type: "warning", data: { phase: "answer", code: "document_truncated" } });
    expect(document.querySelector(".chat-status-line").textContent).toBe(en["labels.chat_warning_generic"]);
  });

  describe("re-entering the chat route", () => {
    it("with a different document starts a fresh conversation for it", async () => {
      const chat = await mount("doc-A", "A");
      ask("about A");
      emit({ type: "done", data: { session_id: "sess-A" } });
      expect(document.querySelector(".chat-msg-wrap")).not.toBeNull();

      openDocumentRoute("doc-B", "B");
      chat.attachStandalone();

      expect(document.querySelector(".chat-msg-wrap")).toBeNull();
      expect(fetch.mock.calls[0][0]).toBe("api/v2/chat/sessions/sess-A");
      expect(isShown(banner())).toBe(true);
      expect(banner().textContent).toBe("Asking about: B");
      ask("about B");
      expect(lastCall()[1]).toEqual({ message: "about B", doc_id: "doc-B" });
    });

    it("from normal chat into a document starts fresh in document mode", async () => {
      const chat = await mount("", "");
      ask("general");
      emit({ type: "done", data: { session_id: "sess-N" } });

      openDocumentRoute("doc-A", "A");
      chat.attachStandalone();

      expect(fetch.mock.calls[0][0]).toBe("api/v2/chat/sessions/sess-N");
      ask("about A");
      expect(lastCall()[1]).toEqual({ message: "about A", doc_id: "doc-A" });
    });

    it("from a document to the plain chat route returns to normal chat", async () => {
      const chat = await mount("doc-A", "A");
      ask("about A");
      emit({ type: "done", data: { session_id: "sess-A" } });

      openDocumentRoute("", "");
      chat.attachStandalone();

      expect(isShown(banner())).toBe(false);
      ask("general");
      expect(lastCall()[1]).toEqual({ message: "general" });
    });

    it("with the same document keeps the conversation", async () => {
      const chat = await mount("doc-A", "A");
      ask("about A");
      emit({ type: "done", data: { session_id: "sess-A" } });

      openDocumentRoute("doc-A", "A");
      chat.attachStandalone();

      expect(fetch).not.toHaveBeenCalled();
      expect(document.querySelector(".chat-msg-wrap")).not.toBeNull();
      ask("more");
      expect(lastCall()[1]).toEqual({ message: "more", session_id: "sess-A", doc_id: "doc-A" });
    });
  });
});
