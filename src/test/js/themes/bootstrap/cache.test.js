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
});

// The viewer shows the cached HTML in a blob: frame that inherits the page's CSP
// (base-uri 'self'), so a <base href> in the cached copy has no effect there.
// cache.js therefore resolves the relative URLs itself, against the address the
// server reports for the document (the same one cache.hbs puts in <base href>).
describe("attach: relative URLs of the cached copy", () => {
  const PAGE = "https://site.example/docs/guide/page.html?x=1";

  /** Run attach() for a cached document and return the HTML handed to the iframe's blob, parsed. */
  async function cachedDoc(content, url = PAGE) {
    const { api, cache } = await freshModules();
    api.get.mockResolvedValue({ doc_id: "abc", mimetype: "text/html", content, url, created: "2024-01-01T00:00:00" });
    setLocation("/cache?docId=abc");
    cache.attach();
    const host = document.getElementById("cache-view");
    await vi.waitFor(() => expect(host.querySelector("iframe.cache-frame")).not.toBeNull());
    const text = await URL.createObjectURL.mock.calls[0][0].text();
    return { text, doc: new DOMParser().parseFromString(text, "text/html") };
  }

  // What cache.hbs produces: the <base> first, then the banner, then the page itself.
  const hbs = (page) =>
    '<!DOCTYPE html>\n<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">\n' +
    '<base href="https://site.example/docs/guide/page.html?x=1">\n<div>banner</div>\n' + page;

  it("points relative links, images and form targets at the original site", async () => {
    const { doc } = await cachedDoc(hbs(
      '<html><head><title>t</title></head><body>' +
      '<a id="rel" href="next.html">n</a><a id="up" href="../top.html#s">u</a><a id="root" href="/r/x.html">r</a>' +
      '<a id="proto" href="//cdn.example/y">p</a><a id="q" href="?page=2">q</a>' +
      '<img id="img" src="img/a.png"><video id="vid" poster="p.png" src="v.mp4"></video>' +
      '<form id="frm" action="send.cgi"></form><object id="obj" data="o.swf"></object>' +
      '<iframe id="ifr" src="frame.html"></iframe>' +
      '</body></html>'));
    const attr = (id, name) => doc.getElementById(id).getAttribute(name);
    expect(attr("rel", "href")).toBe("https://site.example/docs/guide/next.html");
    expect(attr("up", "href")).toBe("https://site.example/docs/top.html#s");
    expect(attr("root", "href")).toBe("https://site.example/r/x.html");
    expect(attr("proto", "href")).toBe("https://cdn.example/y");
    expect(attr("q", "href")).toBe("https://site.example/docs/guide/page.html?page=2");
    expect(attr("img", "src")).toBe("https://site.example/docs/guide/img/a.png");
    expect(attr("vid", "poster")).toBe("https://site.example/docs/guide/p.png");
    expect(attr("vid", "src")).toBe("https://site.example/docs/guide/v.mp4");
    expect(attr("frm", "action")).toBe("https://site.example/docs/guide/send.cgi");
    expect(attr("obj", "data")).toBe("https://site.example/docs/guide/o.swf");
    expect(attr("ifr", "src")).toBe("https://site.example/docs/guide/frame.html");
  });

  it("resolves srcset candidates and keeps their descriptors", async () => {
    const { doc } = await cachedDoc(hbs(
      '<img id="a" srcset="s/a.png 1x, s/b.png 2x" src="s/a.png">' +
      '<img id="b" srcset="s/c.png 100w,/s/d.png 200w,https://o.example/e.png 300w">' +
      '<img id="c" srcset="s/f.png, s/g.png">' +
      '<img id="d" srcset="data:image/png;base64,AAAA 1x, s/h.png 2x">'));
    const srcset = (id) => doc.getElementById(id).getAttribute("srcset");
    expect(srcset("a")).toBe("https://site.example/docs/guide/s/a.png 1x, https://site.example/docs/guide/s/b.png 2x");
    expect(srcset("b")).toBe("https://site.example/docs/guide/s/c.png 100w,https://site.example/s/d.png 200w,https://o.example/e.png 300w");
    expect(srcset("c")).toBe("https://site.example/docs/guide/s/f.png, https://site.example/docs/guide/s/g.png");
    expect(srcset("d")).toBe("data:image/png;base64,AAAA 1x, https://site.example/docs/guide/s/h.png 2x");
  });

  it("resolves url() in style attributes and <style> elements", async () => {
    const { doc } = await cachedDoc(hbs(
      '<style>.h{background:url(img/h.png)} .g{background:url( "/g.png" ) no-repeat} .d{background:url(data:image/png;base64,AAAA)}</style>' +
      '<div id="x" style="background-image:url(\'img/x.png\');color:red">x</div>'));
    const css = doc.querySelector("style").textContent;
    expect(css).toContain('url("https://site.example/docs/guide/img/h.png")');
    expect(css).toContain('url("https://site.example/g.png") no-repeat');
    expect(css).toContain('url("data:image/png;base64,AAAA")');
    expect(doc.getElementById("x").getAttribute("style")).toBe('background-image:url("https://site.example/docs/guide/img/x.png");color:red');
  });

  it("leaves absolute URLs, fragments, other schemes and empty values alone", async () => {
    const { doc } = await cachedDoc(hbs(
      '<a id="abs" href="https://other.example/a">1</a><a id="frag" href="#top">2</a><a id="mail" href="mailto:a@b.example">3</a>' +
      '<a id="js" href="javascript:void(0)">4</a><a id="empty" href="">5</a><a id="tel" href="tel:+81">6</a>' +
      '<img id="data" src="data:image/gif;base64,R0lGODlhAQABAAAAACw=">'));
    const href = (id) => doc.getElementById(id).getAttribute("href");
    expect(href("abs")).toBe("https://other.example/a");
    expect(href("frag")).toBe("#top");
    expect(href("mail")).toBe("mailto:a@b.example");
    expect(href("js")).toBe("javascript:void(0)");
    expect(href("empty")).toBe("");
    expect(href("tel")).toBe("tel:+81");
    expect(doc.getElementById("data").getAttribute("src")).toBe("data:image/gif;base64,R0lGODlhAQABAAAAACw=");
  });

  it("drops every <base href> (the policy ignores it and reports it) and keeps the doctype", async () => {
    const { text, doc } = await cachedDoc(hbs(
      '<html><head><base href="/other/"><base target="_top" href=\'x>y\'></head><body><a href="z.html">z</a></body></html>'));
    expect(doc.querySelectorAll("base[href]").length).toBe(0);
    expect(text).not.toMatch(/<base\b[^>]*\bhref/i);
    expect(text.startsWith("<!DOCTYPE html>")).toBe(true);
    expect(doc.compatMode).toBe("CSS1Compat");
    // the first <base> was the server's: its address, not the page's own <base>, is the base
    expect(doc.querySelector("a").getAttribute("href")).toBe("https://site.example/docs/guide/z.html");
    // a stray fragment of a <base> tag must not leak into the page as text
    expect(doc.body.textContent).not.toContain("y'>");
  });

  it("keeps the rest of the document: banner, text, scripts stay as they were", async () => {
    const { doc } = await cachedDoc(hbs(
      '<html><head><title>T</title><script>window.__x=1</script></head><body><p id="p">Hello &amp; <em>world</em></p></body></html>'));
    expect(doc.getElementById("p").innerHTML).toBe("Hello &amp; <em>world</em>");
    expect(doc.querySelector("div").textContent).toBe("banner");
    expect(doc.querySelector("script").textContent).toBe("window.__x=1");
    expect(doc.title).toBe("T");
  });

  it("hands the content over untouched when the server reports no address", async () => {
    const content = '<base href="x/"><img src="a.png">';
    const { text } = await cachedDoc(content, null);
    expect(text).toBe(content);
  });

  it("does not touch the viewer's own DOM while resolving", async () => {
    const before = document.getElementById("cache-view");
    await cachedDoc(hbs('<img src="a.png">'));
    expect(document.getElementById("cache-view")).toBe(before);
    expect(document.querySelectorAll("img").length).toBe(0);
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
