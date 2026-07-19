// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bootstrap theme i18n loader.
//
// The pure describes (pickLocale / t / languageLabel / applyDom) use a single
// static import: that instance is never init()ed, so its `messages` stays {}
// and its `locale` stays "en". With empty messages the real t(key) returns the
// key string unchanged, which lets us assert exact i18n keys.
//
// The init-driven cases import a FRESH module (vi.resetModules) and drive a
// stubbed global fetch, because init() mutates module-level `messages`/`locale`
// and the shared document (<html lang>, <title>). resetDom() between tests
// keeps that document mutation from leaking.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { installFetch, jsonResponse } from "../../helpers/net.js";
import { resetDom } from "../../helpers/dom.js";
import {
  pickLocale,
  t,
  languageLabel,
  applyDom,
} from "../../../../main/webapp/themes/bootstrap/assets/i18n.js";

const I18N = "../../../../main/webapp/themes/bootstrap/assets/i18n.js";

/** navigator.language is read-only; redefine it for pickLocale/init. */
function setLanguage(value) {
  Object.defineProperty(navigator, "language", { value, configurable: true });
}

beforeEach(resetDom);
afterEach(() => vi.unstubAllGlobals());

describe("pickLocale", () => {
  it.each([
    ["ja-JP", "ja"],       // primary-subtag match
    ["de-AT", "de"],       // primary-subtag match
    ["pt-BR", "pt-BR"],    // case-insensitive exact match
    ["PT-br", "pt-BR"],    // exact match, differing case
    ["zh-CN", "zh-CN"],    // exact match
    ["en-US", "en"],       // primary-subtag match
    ["xx", "en"],          // no match → English fallback
    ["xx-YY", "en"],       // no primary match → English fallback
  ])("navigator.language %s → %s", (lang, expected) => {
    setLanguage(lang);
    expect(pickLocale()).toBe(expected);
  });

  it("falls back to en when navigator.language is empty", () => {
    setLanguage("");
    expect(pickLocale()).toBe("en");
  });
});

describe("t", () => {
  it("returns the key unchanged when the message is missing", () => {
    expect(t("some.unknown.key")).toBe("some.unknown.key");
  });

  it("substitutes a named param: {x}", () => {
    expect(t("a {x} b", { x: "Z" })).toBe("a Z b");
  });

  it("substitutes positional params from an array: {0}-{1}", () => {
    expect(t("{0}-{1}", ["p", "q"])).toBe("p-q");
  });

  it("renders a null or undefined value as an empty string", () => {
    expect(t("{v}", { v: null })).toBe("");
    expect(t("{v}", { v: undefined })).toBe("");
  });

  it("leaves placeholders untouched when params is omitted", () => {
    expect(t("a {x} b")).toBe("a {x} b");
  });
});

describe("languageLabel", () => {
  it("empty value → fallback, then value, then ''", () => {
    expect(languageLabel("", "Fallback")).toBe("Fallback");
    expect(languageLabel("")).toBe("");
  });

  it("uses Intl.DisplayNames for a code with no theme i18n key", () => {
    const name = languageLabel("ar");
    expect(name).not.toBe("ar");
    expect(name.length).toBeGreaterThan(0);
  });

  it("normalises underscore variants (pt_BR) before Intl lookup", () => {
    const name = languageLabel("pt_BR");
    expect(name).not.toBe("pt_BR");
    expect(name).not.toBe("pt-BR");
  });

  it("prefers a theme i18n key when the loaded messages define one", async () => {
    vi.resetModules();
    installFetch(async () => jsonResponse({ "labels.lang_ar": "Arabic (custom)" }));
    setLanguage("en");
    const m = await import(I18N);
    await m.init();
    expect(m.languageLabel("ar")).toBe("Arabic (custom)");
  });
});

describe("applyDom", () => {
  it("sets textContent / placeholder / aria-label / alt from data-i18n* keys", () => {
    const root = document.createElement("div");
    root.innerHTML =
      '<span data-i18n="k.text">old</span>' +
      '<input data-i18n-placeholder="k.ph">' +
      '<button data-i18n-aria-label="k.aria">b</button>' +
      '<img data-i18n-alt="k.alt">';
    applyDom(root);
    expect(root.querySelector("[data-i18n]").textContent).toBe("k.text");
    expect(root.querySelector("[data-i18n-placeholder]").getAttribute("placeholder")).toBe("k.ph");
    expect(root.querySelector("[data-i18n-aria-label]").getAttribute("aria-label")).toBe("k.aria");
    expect(root.querySelector("[data-i18n-alt]").getAttribute("alt")).toBe("k.alt");
  });

  it("leaves a subtree with no data-i18n* markers unchanged", () => {
    const root = document.createElement("div");
    root.innerHTML = "<p>plain</p>";
    expect(() => applyDom(root)).not.toThrow();
    expect(root.querySelector("p").textContent).toBe("plain");
  });
});

describe("init", () => {
  it("resolves the locale, loads its bundle, and sets <html lang>, title, and the DOM", async () => {
    vi.resetModules();
    const fetchMock = installFetch(async () =>
      jsonResponse({ "page.title": "My Fess", "k.hello": "Hello" })
    );
    setLanguage("ja-JP");
    document.body.innerHTML = '<span data-i18n="k.hello">x</span>';
    const m = await import(I18N);
    await m.init();
    expect(m.getLocale()).toBe("ja");
    expect(document.documentElement.lang).toBe("ja");
    expect(document.title).toBe("My Fess");
    expect(document.querySelector("[data-i18n]").textContent).toBe("Hello");
    expect(fetchMock).toHaveBeenCalledWith(
      "/themes/bootstrap/i18n/messages.ja.json",
      { credentials: "same-origin" }
    );
  });

  it("falls back to the English bundle when the primary locale bundle is not ok", async () => {
    vi.resetModules();
    const calls = [];
    installFetch(async (url) => {
      calls.push(url);
      if (url.includes("messages.de.json")) return jsonResponse({}, { ok: false, status: 404 });
      return jsonResponse({ "page.title": "EN Title" });
    });
    setLanguage("de-DE"); // → primary "de"
    const m = await import(I18N);
    await m.init();
    expect(m.getLocale()).toBe("en");
    expect(document.documentElement.lang).toBe("en");
    expect(document.title).toBe("EN Title");
    expect(calls[0]).toContain("messages.de.json");
    expect(calls[1]).toContain("messages.en.json");
  });

  it("leaves messages empty (no throw) when both primary and English fetches fail", async () => {
    vi.resetModules();
    installFetch(async () => { throw new Error("offline"); });
    setLanguage("fr-FR"); // → primary "fr"
    const m = await import(I18N);
    await expect(m.init()).resolves.toBeUndefined();
    // English fallback also failed → locale stays at the picked "fr", messages {}.
    expect(m.getLocale()).toBe("fr");
    expect(document.documentElement.lang).toBe("fr");
    expect(m.t("any.key")).toBe("any.key");
  });
});
