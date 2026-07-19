// SPDX-License-Identifier: Apache-2.0
// error.js: pure path→code mapping plus a DOM smoke test of the rendered view.
// The real i18n.t() returns the requested key unchanged (messages are empty
// without init), which lets us assert exactly which i18n key each element uses.

import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { codeFromPath, attach } from "../../../../main/webapp/themes/bootstrap/assets/error.js";
import { resetDom, setLocation } from "../../helpers/dom.js";

beforeEach(resetDom);
afterEach(() => setLocation("/"));

describe("codeFromPath", () => {
  it.each([
    ["/error/400", "400"],
    ["/error/404", "404"],
    ["/error/not_found", "404"],
    ["/error/notFound", "404"], // camelCase collapses like snake_case
    ["/error/bad_request", "400"],
    ["/error/busy", "429"], // busy → 429, never 503
    ["/error/503", "503"],
    ["/error/service_unavailable", "503"],
    ["/error/system", "500"],
    ["/error/unknown", "500"], // no match → default 500
    ["/", "500"],
  ])("%s → %s", (path, code) => {
    expect(codeFromPath(path)).toBe(code);
  });

  it("scans segments in reverse (last match wins)", () => {
    expect(codeFromPath("/error/404/not_found")).toBe("404");
  });
});

describe("attach", () => {
  it("renders title/body from the code's i18n keys and a home link", () => {
    document.body.innerHTML = '<div id="error-view"></div>';
    setLocation("/error/404");
    attach();
    const view = document.getElementById("error-view");
    expect(view.querySelector(".error-title").textContent).toBe("error.title_404");
    expect(view.querySelector(".error-body").textContent).toBe("error.body_404");
    const home = view.querySelector(".error-actions a");
    expect(home.getAttribute("href")).toBe("/");
    expect(home.hasAttribute("data-spa")).toBe(true);
  });

  it("prefers the x-fess-error-code meta over the path", () => {
    document.head.innerHTML = '<meta name="x-fess-error-code" content="429">';
    document.body.innerHTML = '<div id="error-view"></div>';
    setLocation("/error/404");
    attach();
    expect(document.querySelector(".error-title").textContent).toBe("error.title_429");
  });

  it("puts the ?url value into the DOM as text, not markup", () => {
    document.body.innerHTML = '<div id="error-view"></div>';
    setLocation("/error/404?url=%3Cimg%20src%3Dx%3E");
    attach();
    const dd = document.querySelector(".error-detail dd");
    expect(dd.textContent).toBe("<img src=x>");
    expect(dd.querySelector("img")).toBeNull();
  });

  it("does nothing when #error-view is absent", () => {
    expect(() => attach()).not.toThrow();
  });
});
