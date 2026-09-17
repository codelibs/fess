// SPDX-License-Identifier: Apache-2.0
// Cross-module test for logging out with SSO single logout on a login.required site.
//
// The real app.js and auth.js (and format.js) run together; every other module app.js
// imports is mocked as in app.test.js. auth.js announces fess:auth:logout and then leaves
// for the identity provider's logout URL. If app.js answered that event by fetching the
// config and re-gating the page, the closed gate would call router.redirect("sso/") too,
// and in a browser that second navigation cancels the single logout.
//
// app.js runs main() at import time unless document.readyState is "loading", so the
// getter is forced before the dynamic import and main() is called once below. This file
// has its own module registry, so the listeners main() adds are registered exactly once.

import { describe, it, expect, vi } from "vitest";
import { resetDom, setLocation } from "../../helpers/dom.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  init: vi.fn(async () => {}),
  getConfig: vi.fn(() => ({ features: {} })),
  get: vi.fn(async () => ({ popular_words: [] })),
  post: vi.fn(async () => ({})),
  isAuthenticated: vi.fn(() => false),
  setAuthenticated: vi.fn(),
  getCsrfToken: vi.fn(() => ""),
  setCsrfToken: vi.fn(),
  ApiError: class extends Error {},
  NetworkError: class extends Error {},
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/i18n.js", () => ({
  init: vi.fn(async () => {}),
  t: (k) => k,
  applyDom: () => {},
  languageLabel: (v) => v,
  getLocale: () => "en",
  pickLocale: () => "en",
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/search.js", () => ({
  attach: vi.fn(),
  runFromUrl: vi.fn(),
  refresh: vi.fn(),
  clearSearchState: vi.fn(),
  attachSuggest: vi.fn(),
  disableSubmitBriefly: vi.fn(),
  renderPopularWords: vi.fn(),
  initSearchOptions: vi.fn(),
  forgetNum: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/chat.js", () => ({
  attach: vi.fn(),
  attachInline: vi.fn(),
  attachStandalone: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  register: vi.fn(),
  navigate: vi.fn(),
  attach: vi.fn(),
  dispatch: vi.fn(),
  currentPath: vi.fn(() => "/"),
  redirect: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/error.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/profile.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/help.js", () => ({ attach: vi.fn(async () => {}) }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/advance.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/cache.js", () => ({ attach: vi.fn() }));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import * as router from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import * as search from "../../../../main/webapp/themes/bootstrap/assets/search.js";

Object.defineProperty(document, "readyState", { configurable: true, get: () => "loading" });
const { main } = await import("../../../../main/webapp/themes/bootstrap/assets/app.js");

/** Let queued microtasks and timers settle so the async click handler completes. */
const flush = () => new Promise((r) => setTimeout(r));

describe("logout with SSO single logout on a login-required site", () => {
  it("leaves for the identity provider's logout URL without fetching the config or re-gating the page", async () => {
    resetDom();
    setLocation("/");
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({ login_required: true, features: { sso_enabled: true, login_link: "sso/" } });
    api.get.mockImplementation(async (path) =>
      path === "/auth/me" ? { authenticated: true, user: { name: "Al" } } : { popular_words: [] });
    api.post.mockResolvedValue({ ok: true, csrf_token: "t", redirect_url: "https://idp.example.com/slo?x=1" });

    await main();
    // A logged-in user passes the gate: the route runs and nothing redirects.
    expect(router.dispatch).toHaveBeenCalledTimes(1);
    expect(router.redirect).not.toHaveBeenCalled();

    document.getElementById("logout-btn").click();
    await flush();

    expect(api.post).toHaveBeenCalledWith("/auth/logout", {});
    expect(search.forgetNum).toHaveBeenCalledTimes(1);
    expect(router.redirect).toHaveBeenCalledTimes(1);
    expect(router.redirect).toHaveBeenCalledWith("https://idp.example.com/slo?x=1");
    // Only the boot fetched the config.
    expect(api.init).toHaveBeenCalledTimes(1);
  });
});
