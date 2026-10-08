// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the bootstrap theme help-page renderer. The real
// i18n.t() returns each key unchanged (messages are empty without init), so the
// error path asserts the exact i18n key; the real format.sanitizeHtml() runs so
// the section-body sanitation is exercised end to end. A stubbed global fetch
// drives fetchHelpContent's locale/fallback logic deterministically.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import {
  fetchHelpContent,
  renderSection,
  attach,
} from "../../../../main/webapp/themes/bootstrap/assets/help.js";
import { resetDom } from "../../helpers/dom.js";
import { installFetch, jsonResponse } from "../../helpers/net.js";

beforeEach(resetDom);
afterEach(() => vi.unstubAllGlobals());

describe("fetchHelpContent", () => {
  it("GETs {locale}.json with same-origin credentials and returns parsed JSON", async () => {
    const body = { sections: [{ id: "a", title: "A", html: "<p>a</p>" }] };
    const fetchMock = installFetch(async () => jsonResponse(body));

    await expect(fetchHelpContent("en")).resolves.toEqual(body);

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, opts] = fetchMock.mock.calls[0];
    expect(url).toMatch(/\/themes\/bootstrap\/help\/en\.json$/);
    expect(opts).toEqual({ credentials: "same-origin" });
  });

  it("falls back to en.json when the primary (ja) bundle is not ok", async () => {
    const en = { sections: [{ id: "en", title: "EN", html: "" }] };
    const fetchMock = installFetch(async (url) =>
      url.includes("/ja.json")
        ? jsonResponse(null, { ok: false, status: 404 })
        : jsonResponse(en)
    );

    await expect(fetchHelpContent("ja")).resolves.toEqual(en);

    expect(fetchMock).toHaveBeenCalledTimes(2);
    expect(fetchMock.mock.calls[0][0]).toMatch(/\/themes\/bootstrap\/help\/ja\.json$/);
    expect(fetchMock.mock.calls[1][0]).toMatch(/\/themes\/bootstrap\/help\/en\.json$/);
  });

  it("rejects without any fallback when the en bundle itself is not ok", async () => {
    const fetchMock = installFetch(async () => jsonResponse(null, { ok: false, status: 500 }));

    await expect(fetchHelpContent("en")).rejects.toThrow();

    expect(fetchMock).toHaveBeenCalledTimes(1); // no second (fallback) request for "en"
  });
});

describe("renderSection", () => {
  it("builds section#help-<id>.help-section with a text-only h2 and sanitized body", () => {
    const container = document.createElement("div");

    renderSection(container, {
      id: "intro",
      title: "<b>x</b>",
      html: "<p>ok</p><script>bad</script>",
    });

    const sec = container.querySelector("section");
    expect(sec.id).toBe("help-intro");
    expect(sec.className).toBe("help-section");

    // Title is inserted via textContent — the markup is literal text, not a node.
    const h2 = sec.querySelector("h2");
    expect(h2.textContent).toBe("<b>x</b>");
    expect(h2.querySelector("b")).toBeNull();

    // Body is sanitized: the <p> survives, the <script> is dropped whole.
    expect(sec.querySelector("p").textContent).toBe("ok");
    expect(sec.querySelector("script")).toBeNull();
  });
});

describe("renderSection: tables", () => {
  it("puts each table in a scroll container so a wide one does not widen the page", () => {
    const container = document.createElement("div");

    renderSection(container, {
      id: "ops",
      title: "Operators",
      html: '<p>before</p><table class="table table-sm"><thead><tr><th scope="col">A</th></tr></thead><tbody><tr><td>1</td></tr></tbody></table><p>after</p>',
    });

    const sec = container.querySelector("section");
    const table = sec.querySelector("table");
    expect(table.className).toBe("table table-sm");
    expect(table.parentElement.tagName).toBe("DIV");
    expect(table.parentElement.className).toBe("table-responsive");
    expect(table.parentElement.parentElement).toBe(sec);
    // The surrounding content keeps its order: h2, p, scroller(table), p.
    expect(Array.from(sec.children).map(e => e.tagName + (e.className ? "." + e.className : "")))
      .toEqual(["H2", "P", "DIV.table-responsive", "P"]);
    expect(table.querySelector("th").getAttribute("scope")).toBe("col");
  });

  it("wraps every table of a section, and leaves a section without one as it was", () => {
    const container = document.createElement("div");

    renderSection(container, { id: "two", title: "T", html: "<table><tr><td>a</td></tr></table><table><tr><td>b</td></tr></table>" });
    renderSection(container, { id: "none", title: "N", html: "<p>x</p>" });

    expect(container.querySelectorAll("#help-two .table-responsive > table").length).toBe(2);
    expect(container.querySelectorAll("#help-none .table-responsive").length).toBe(0);
  });
});

describe("attach", () => {
  it("does nothing (and does not throw) when #help-view is absent", async () => {
    await expect(attach()).resolves.toBeUndefined();
  });

  it("renders fetched sections and removes the loading indicator", async () => {
    document.body.innerHTML = '<div id="help-view"></div>';
    installFetch(async () =>
      jsonResponse({ sections: [{ id: "intro", title: "T", html: "<p>hello</p>" }] })
    );

    await attach();

    const view = document.getElementById("help-view");
    const sec = view.querySelector("#help-intro");
    expect(sec).not.toBeNull();
    expect(sec.querySelector("h2").textContent).toBe("T");
    expect(sec.querySelector("p").textContent).toBe("hello");
    expect(view.querySelector(".text-muted")).toBeNull(); // loading <p> removed
  });

  it("shows an error paragraph (t('error.server')) when the fetch fails", async () => {
    document.body.innerHTML = '<div id="help-view"></div>';
    installFetch(async () => jsonResponse(null, { ok: false, status: 500 }));

    await attach();

    const view = document.getElementById("help-view");
    const err = view.querySelector(".text-danger");
    expect(err).not.toBeNull();
    expect(err.textContent).toBe("error.server");
    expect(view.querySelector(".text-muted")).toBeNull(); // loading <p> removed
  });
});
