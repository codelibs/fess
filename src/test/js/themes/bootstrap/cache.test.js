// SPDX-License-Identifier: Apache-2.0
// Executable tests for cache.js — the SPA cache viewer.
//
// cache.js carries module-level state (_currentBlobUrl, _routeListenerAttached),
// so every test does vi.resetModules() + a fresh dynamic import to start clean.
// api.js is mocked (get is a vi.fn resolved/rejected per test); i18n.t and
// format.formatDate are the real modules. The real i18n.t() returns its key
// unchanged (no init), so the rendered text is the exact i18n key.
//
// jsdom has no URL.createObjectURL/revokeObjectURL; both are stubbed. Without the
// createObjectURL stub the happy path throws inside the .then and is swallowed by
// the .catch — a false pass rendering the error view — so the stub is load-bearing.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom, setLocation } from "../../helpers/dom.js";

const API = "../../../../main/webapp/themes/bootstrap/assets/api.js";
const CACHE = "../../../../main/webapp/themes/bootstrap/assets/cache.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  get: vi.fn(),
  ApiError: class extends Error {},
}));

/** Import a fresh cache.js plus the mocked api after resetting module state. */
async function freshModules() {
  vi.resetModules();
  const api = await import(API);
  const cache = await import(CACHE);
  return { api, cache };
}

beforeEach(() => {
  document.body.innerHTML = '<section id="cache-view"></section>';
  URL.createObjectURL = vi.fn(() => "blob:mock");
  URL.revokeObjectURL = vi.fn();
});

afterEach(() => {
  vi.unstubAllGlobals();
  setLocation("/");
  resetDom();
});

describe("attach: guards", () => {
  it("does nothing when #cache-view is absent", async () => {
    document.body.innerHTML = "";
    const { cache } = await freshModules();
    setLocation("/cache?docId=abc");
    expect(() => cache.attach()).not.toThrow();
  });

  it("renders a not-found alert when docId is missing", async () => {
    const { api, cache } = await freshModules();
    setLocation("/cache");
    cache.attach();

    const alert = document.querySelector("#cache-view .alert.alert-warning");
    expect(alert).not.toBeNull();
    expect(alert.textContent).toBe("labels.cache_not_found");
    // No fetch attempted without a docId.
    expect(api.get).not.toHaveBeenCalled();
  });
});

describe("attach: happy path", () => {
  const env = {
    doc_id: "abc",
    mimetype: "text/html",
    content: "<html><head></head><body>x</body></html>",
    url: "https://site/p",
    created: "2024-01-01T00:00:00",
  };

  it("forwards non-empty hq terms and renders heading, metadata, and a sandboxed iframe", async () => {
    const { api, cache } = await freshModules();
    api.get.mockResolvedValue(env);

    setLocation("/cache?docId=abc&hq=foo&hq=");
    cache.attach();

    const host = document.getElementById("cache-view");
    await vi.waitFor(() => expect(host.querySelector("iframe.cache-frame")).not.toBeNull());

    // hq: empty values filtered out, non-empty forwarded to the cache API.
    expect(api.get).toHaveBeenCalledWith("/cache/abc", { hq: ["foo"] });

    // Heading.
    expect(host.querySelector("h2").textContent).toBe("labels.cache_title");

    // Metadata rows (dt=i18n key, dd=value).
    const dl = host.querySelector("dl.cache-meta");
    expect(dl).not.toBeNull();
    const dds = Array.from(dl.querySelectorAll("dd")).map((d) => d.textContent);
    expect(dds).toEqual(
      expect.arrayContaining([
        "https://site/p", // url
        "text/html;charset=utf-8", // charset-augmented mimetype
        "abc", // doc id
        "2024-01-01 00:00", // formatDate(created)
      ])
    );

    // Sandboxed iframe: exactly the two popup tokens, never scripts/same-origin.
    // jsdom stores an assigned iframe.sandbox as a plain string property (it does
    // not reflect to the attribute); a real browser reflects it into the sandbox
    // DOMTokenList. String() reads the value identically in both.
    const iframe = host.querySelector("iframe.cache-frame");
    expect(String(iframe.sandbox)).toBe("allow-popups allow-popups-to-escape-sandbox");
    expect(String(iframe.sandbox)).not.toContain("allow-scripts");
    expect(String(iframe.sandbox)).not.toContain("allow-same-origin");
    expect(iframe.getAttribute("src")).toBe("blob:mock");
  });

  it("injects a <base href> for the document URL into the blob content", async () => {
    const { api, cache } = await freshModules();
    api.get.mockResolvedValue(env);

    setLocation("/cache?docId=abc");
    cache.attach();

    const host = document.getElementById("cache-view");
    await vi.waitFor(() => expect(host.querySelector("iframe.cache-frame")).not.toBeNull());

    expect(URL.createObjectURL).toHaveBeenCalledTimes(1);
    const blobArg = URL.createObjectURL.mock.calls[0][0];
    const text = await blobArg.text();
    expect(text).toContain('<base href="https://site/p">');
  });
});

describe("attach: error path", () => {
  it("shows cache_not_found on a 404 rejection", async () => {
    const { api, cache } = await freshModules();
    api.get.mockRejectedValue({ httpStatus: 404 });

    setLocation("/cache?docId=missing");
    cache.attach();

    const host = document.getElementById("cache-view");
    await vi.waitFor(() =>
      expect(host.querySelector(".alert.alert-warning")).not.toBeNull()
    );
    expect(host.querySelector(".alert.alert-warning").textContent).toBe("labels.cache_not_found");
  });

  it("shows the generic server error for a non-404 rejection", async () => {
    const { api, cache } = await freshModules();
    api.get.mockRejectedValue({ code: "X" });

    setLocation("/cache?docId=boom");
    cache.attach();

    const host = document.getElementById("cache-view");
    await vi.waitFor(() =>
      expect(host.querySelector(".alert.alert-warning")).not.toBeNull()
    );
    expect(host.querySelector(".alert.alert-warning").textContent).toBe("error.server");
  });
});
