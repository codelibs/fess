// SPDX-License-Identifier: Apache-2.0
// Executable tests for the bootstrap theme auth.js.
//
// api.js is mocked so config and network are driven per test; the real i18n.js
// and format.js are used. The real i18n.t() returns the requested key unchanged
// (messages are empty without init), so the tests assert exact i18n keys.
//
// Silent catches in the source mean behaviour is only observable through side
// effects (api.setAuthenticated / api.setCsrfToken calls, DOM mutations, and
// dispatched CustomEvents); the assertions target those.

import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  get: vi.fn(),
  post: vi.fn(),
  getConfig: vi.fn(),
  setAuthenticated: vi.fn(),
  setCsrfToken: vi.fn(),
  ApiError: class extends Error {},
  NetworkError: class extends Error {},
}));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import {
  isLoginLinkEnabled,
  buildUserDropdown,
  buildLoginLink,
  rotateCsrf,
  probeMe,
  attach,
} from "../../../../main/webapp/themes/bootstrap/assets/auth.js";
import { resetDom } from "../../helpers/dom.js";

/** Let queued microtasks/timers settle so async submit handlers complete. */
const flush = () => new Promise((r) => setTimeout(r));

beforeEach(() => {
  resetDom();
  vi.clearAllMocks();
  // Default: no config unless a test overrides it.
  api.getConfig.mockReturnValue(null);
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("isLoginLinkEnabled", () => {
  it("returns true when getConfig() is null (missing config)", () => {
    api.getConfig.mockReturnValue(null);
    expect(isLoginLinkEnabled()).toBe(true);
  });

  it("returns true when config has no features ({})", () => {
    api.getConfig.mockReturnValue({});
    expect(isLoginLinkEnabled()).toBe(true);
  });

  it("returns false only when login_link === false", () => {
    api.getConfig.mockReturnValue({ features: { login_link: false } });
    expect(isLoginLinkEnabled()).toBe(false);
  });

  it("returns true for a truthy login_link value (e.g. an SSO URL)", () => {
    api.getConfig.mockReturnValue({ features: { login_link: "/sso" } });
    expect(isLoginLinkEnabled()).toBe(true);
  });
});

describe("buildUserDropdown", () => {
  it("renders the toggle (#userMenu) and always a #logout-btn", () => {
    const w = buildUserDropdown({ name: "Al" });
    expect(w.querySelector("#userMenu")).not.toBeNull();
    expect(w.querySelector("#logout-btn")).not.toBeNull();
    // Detached node: not attached to the live document.
    expect(w.isConnected).toBe(false);
  });

  it("uses user.name for #user-name", () => {
    const w = buildUserDropdown({ name: "Al", username: "ignored" });
    expect(w.querySelector("#user-name").textContent).toBe("Al");
  });

  it("falls back name -> username -> user_id -> empty string", () => {
    expect(buildUserDropdown({ username: "u1" }).querySelector("#user-name").textContent).toBe("u1");
    expect(buildUserDropdown({ user_id: "uid7" }).querySelector("#user-name").textContent).toBe("uid7");
    expect(buildUserDropdown({}).querySelector("#user-name").textContent).toBe("");
  });

  it("includes the profile item unless editable === false", () => {
    expect(buildUserDropdown({ name: "Al" }).querySelector('a[href="/profile"]')).not.toBeNull();
    expect(buildUserDropdown({ name: "Al", editable: true }).querySelector('a[href="/profile"]')).not.toBeNull();
    expect(buildUserDropdown({ name: "Al", editable: false }).querySelector('a[href="/profile"]')).toBeNull();
  });

  it("includes the admin item only when admin === true", () => {
    expect(buildUserDropdown({ name: "Al", admin: true }).querySelector('a[href="/admin/"]')).not.toBeNull();
    // Truthy-but-not-true values do not qualify (strict ===).
    expect(buildUserDropdown({ name: "Al", admin: 1 }).querySelector('a[href="/admin/"]')).toBeNull();
    expect(buildUserDropdown({ name: "Al" }).querySelector('a[href="/admin/"]')).toBeNull();
  });

  it("labels items with the exact i18n keys", () => {
    const w = buildUserDropdown({ name: "Al", admin: true });
    expect(w.querySelector('a[href="/profile"]').textContent).toBe("nav.profile");
    expect(w.querySelector('a[href="/admin/"]').textContent).toBe("nav.administration");
    expect(w.querySelector("#logout-btn").textContent).toBe("auth.logout");
  });
});

describe("buildLoginLink", () => {
  it("links directly to features.login_link when SSO is enabled", () => {
    api.getConfig.mockReturnValue({ features: { sso_enabled: true, login_link: "/sso" } });
    const a = buildLoginLink();
    expect(a.getAttribute("href")).toBe("/sso");
    expect(a.id).toBe("login-btn");
    // Direct link: no modal wiring.
    expect(a.hasAttribute("data-bs-toggle")).toBe(false);
    expect(a.hasAttribute("data-bs-target")).toBe(false);
  });

  it("opens the login modal when SSO is not fully configured", () => {
    api.getConfig.mockReturnValue({});
    const a = buildLoginLink();
    expect(a.getAttribute("href")).toBe("/login");
    expect(a.getAttribute("data-bs-toggle")).toBe("modal");
    expect(a.getAttribute("data-bs-target")).toBe("#login-modal");
    expect(a.querySelector("span").textContent).toBe("auth.login");
  });

  it("opens the modal when sso_enabled is set but login_link is missing", () => {
    api.getConfig.mockReturnValue({ features: { sso_enabled: true } });
    const a = buildLoginLink();
    expect(a.getAttribute("href")).toBe("/login");
    expect(a.getAttribute("data-bs-target")).toBe("#login-modal");
  });
});

describe("rotateCsrf", () => {
  it("uses the token from the logout envelope without an extra request", async () => {
    await rotateCsrf({ csrf_token: "X" });
    expect(api.setCsrfToken).toHaveBeenCalledWith("X");
    expect(api.get).not.toHaveBeenCalled();
  });

  it("falls back to GET /ui/config when the envelope has no token", async () => {
    api.get.mockResolvedValue({ csrf_token: "Y" });
    await rotateCsrf(null);
    expect(api.get).toHaveBeenCalledWith("/ui/config");
    expect(api.setCsrfToken).toHaveBeenCalledWith("Y");
  });

  it("swallows a failed fallback fetch (no throw, token left as-is)", async () => {
    api.get.mockRejectedValue(new Error("boom"));
    await expect(rotateCsrf(null)).resolves.toBeUndefined();
    expect(api.setCsrfToken).not.toHaveBeenCalled();
  });
});

describe("probeMe", () => {
  it("renders the user dropdown and marks authenticated when logged in", async () => {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({});
    const user = { name: "Al", admin: true };
    api.get.mockResolvedValue({ authenticated: true, user });

    const result = await probeMe();

    expect(result).toEqual(user);
    expect(api.get).toHaveBeenCalledWith("/auth/me");
    expect(api.setAuthenticated).toHaveBeenCalledWith(true);
    expect(document.getElementById("user-dropdown")).not.toBeNull();
    expect(document.getElementById("userMenu")).not.toBeNull();
    expect(document.querySelector('#user-dropdown a[href="/admin/"]')).not.toBeNull();
  });

  it("treats AUTH_REQUIRED as logged out and shows the login link", async () => {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({});
    api.get.mockRejectedValue({ code: "AUTH_REQUIRED" });

    const result = await probeMe();

    expect(result).toBeNull();
    expect(api.setAuthenticated).toHaveBeenCalledWith(false);
    expect(document.getElementById("login-btn")).not.toBeNull();
    expect(document.getElementById("user-dropdown")).toBeNull();
  });

  it("surfaces a network/server (5xx) error without flipping auth state", async () => {
    document.body.innerHTML = '<div id="results-meta"></div><ul id="auth-controls"></ul>';
    let networkEvent = null;
    document.addEventListener("fess:auth:network-error", (e) => { networkEvent = e; });
    api.get.mockRejectedValue({ httpStatus: 503 });

    const result = await probeMe();

    expect(result).toBeNull();
    // Auth state is unknown, not confirmed gone — must NOT be set to false.
    expect(api.setAuthenticated).not.toHaveBeenCalledWith(false);
    expect(document.getElementById("results-meta").textContent).toBe("error.network");
    expect(networkEvent).not.toBeNull();
    expect(networkEvent.detail.error).toEqual({ httpStatus: 503 });
    // No login link rendered in the network-error branch.
    expect(document.getElementById("login-btn")).toBeNull();
  });
});

describe("attach — login form submit", () => {
  const LOGIN_FIXTURE = `
    <ul id="auth-controls"></ul>
    <form id="login-form">
      <input id="login-username" value="bob">
      <input id="login-password" value="pw">
    </form>
    <div id="login-error" class="d-none"></div>`;

  beforeEach(() => {
    api.getConfig.mockReturnValue({});
    // Initial probeMe() fired by attach() resolves to a clean logged-out state.
    api.get.mockRejectedValue({ code: "AUTH_REQUIRED" });
  });

  it("logs in: posts credentials, rotates CSRF, renders the dropdown", async () => {
    document.body.innerHTML = LOGIN_FIXTURE;
    await attach(); // attach() returns probeMe() — await the initial probe.
    api.post.mockResolvedValue({ user: { name: "Bob" }, csrf_token: "Z" });

    let loginEvent = null;
    document.addEventListener("fess:auth:login", (e) => { loginEvent = e; });

    document.getElementById("login-form").dispatchEvent(new Event("submit"));
    await flush();

    expect(api.post).toHaveBeenCalledWith("/auth/login", { username: "bob", password: "pw" });
    expect(api.setCsrfToken).toHaveBeenCalledWith("Z");
    expect(document.getElementById("user-dropdown")).not.toBeNull();
    expect(document.querySelector("#user-dropdown #user-name").textContent).toBe("Bob");
    expect(loginEvent).not.toBeNull();
  });

  it("shows the rate-limited message on HTTP 429", async () => {
    document.body.innerHTML = LOGIN_FIXTURE;
    await attach();
    api.post.mockRejectedValue({ httpStatus: 429 });

    document.getElementById("login-form").dispatchEvent(new Event("submit"));
    await flush();

    const err = document.getElementById("login-error");
    expect(err.textContent).toBe("auth.error_rate_limited");
    expect(err.classList.contains("d-none")).toBe(false);
  });

  it("shows the invalid-credentials message on a generic failure", async () => {
    document.body.innerHTML = LOGIN_FIXTURE;
    await attach();
    api.post.mockRejectedValue({ code: "BAD_CREDENTIALS" });

    document.getElementById("login-form").dispatchEvent(new Event("submit"));
    await flush();

    const err = document.getElementById("login-error");
    expect(err.textContent).toBe("auth.error_invalid_credentials");
    expect(err.classList.contains("d-none")).toBe(false);
  });
});
