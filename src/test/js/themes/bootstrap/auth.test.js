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

vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  redirect: vi.fn(),
  currentPath: vi.fn(() => "/"),
}));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import * as router from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import {
  isLoginLinkEnabled,
  buildUserDropdown,
  buildLoginLink,
  rotateCsrf,
  probeMe,
  attach,
  getCurrentUser,
  isLoginRequired,
  isLoginGateClosed,
  promptLogin,
  endSession,
  isSessionGone,
  localizePasswordError,
} from "../../../../main/webapp/themes/bootstrap/assets/auth.js";
import { resetDom } from "../../helpers/dom.js";
import { installFetch, jsonResponse } from "../../helpers/net.js";

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
    expect(buildUserDropdown({ name: "Al" }).querySelector('a[href="profile"]')).not.toBeNull();
    expect(buildUserDropdown({ name: "Al", editable: true }).querySelector('a[href="profile"]')).not.toBeNull();
    expect(buildUserDropdown({ name: "Al", editable: false }).querySelector('a[href="profile"]')).toBeNull();
  });

  it("includes the admin item only when admin === true", () => {
    expect(buildUserDropdown({ name: "Al", admin: true }).querySelector('a[href="admin/"]')).not.toBeNull();
    // Truthy-but-not-true values do not qualify (strict ===).
    expect(buildUserDropdown({ name: "Al", admin: 1 }).querySelector('a[href="admin/"]')).toBeNull();
    expect(buildUserDropdown({ name: "Al" }).querySelector('a[href="admin/"]')).toBeNull();
  });

  it("labels items with the exact i18n keys", () => {
    const w = buildUserDropdown({ name: "Al", admin: true });
    expect(w.querySelector('a[href="profile"]').textContent).toBe("nav.profile");
    expect(w.querySelector('a[href="admin/"]').textContent).toBe("nav.administration");
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
    expect(a.getAttribute("href")).toBe("login");
    expect(a.getAttribute("data-bs-toggle")).toBe("modal");
    expect(a.getAttribute("data-bs-target")).toBe("#login-modal");
    expect(a.querySelector("span").textContent).toBe("auth.login");
  });

  it("opens the modal when sso_enabled is set but login_link is missing", () => {
    api.getConfig.mockReturnValue({ features: { sso_enabled: true } });
    const a = buildLoginLink();
    expect(a.getAttribute("href")).toBe("login");
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
    expect(document.querySelector('#user-dropdown a[href="admin/"]')).not.toBeNull();
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

  // V2ErrorCode.AUTH_REQUIRED puts the wire code "auth_required" (lowercase
  // snake_case) on the envelope, and api.js copies err.code through verbatim —
  // nothing in the SPA normalises the case. No httpStatus is supplied here, so
  // the `|| e.httpStatus === 401` fallback cannot mask a failure to recognise
  // the code itself. Both the AUTH_REQUIRED branch and the generic fallback log
  // the user out, so the distinguishing observable is that the expected-and-silent
  // branch emits no console warning.
  it("treats the server's lowercase auth_required wire code as logged out, silently", async () => {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({});
    const warn = vi.spyOn(console, "warn").mockImplementation(() => {});
    api.get.mockRejectedValue({ code: "auth_required" });

    const result = await probeMe();

    expect(result).toBeNull();
    expect(api.setAuthenticated).toHaveBeenCalledWith(false);
    expect(document.getElementById("login-btn")).not.toBeNull();
    expect(warn).not.toHaveBeenCalled();
    warn.mockRestore();
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

  // The 429 case above is carried by the httpStatus fallback alone. This one pins
  // the code arm against the string the server actually sends
  // (V2ErrorCode.RATE_LIMITED → "rate_limited"), with no httpStatus to fall back on.
  it("shows the rate-limited message for the server's lowercase rate_limited wire code", async () => {
    document.body.innerHTML = LOGIN_FIXTURE;
    await attach();
    api.post.mockRejectedValue({ code: "rate_limited" });

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

describe("login gate", () => {
  beforeEach(() => {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
  });

  it("reads login_required from the config", () => {
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    expect(isLoginRequired()).toBe(true);
    api.getConfig.mockReturnValue({ features: { login_required: true } });
    expect(isLoginRequired()).toBe(false);
    api.getConfig.mockReturnValue(null);
    expect(isLoginRequired()).toBe(false);
  });

  it("closes only when login is required and the probe found a guest", async () => {
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.get.mockRejectedValue({ code: "auth_required" });
    await probeMe();
    expect(isLoginGateClosed()).toBe(true);
    expect(getCurrentUser()).toBeNull();
    api.getConfig.mockReturnValue({ login_required: false, features: {} });
    expect(isLoginGateClosed()).toBe(false);
  });

  it("stays open for a logged-in user, whose user object is kept", async () => {
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.get.mockResolvedValue({ authenticated: true, user: { name: "Al", editable: true } });
    await probeMe();
    expect(isLoginGateClosed()).toBe(false);
    expect(getCurrentUser()).toEqual({ name: "Al", editable: true });
  });

  it("stays open when the probe failed on the network, so a broken server cannot loop through login", async () => {
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.get.mockRejectedValue({ code: "auth_required" });
    await probeMe(); // a confirmed guest first
    const warn = vi.spyOn(console, "warn").mockImplementation(() => {});
    api.get.mockRejectedValue({ httpStatus: 503 });
    await probeMe();
    warn.mockRestore();
    expect(isLoginGateClosed()).toBe(false);
  });
});

describe("promptLogin", () => {
  const MODAL = `
    <div id="login-modal">
      <button type="button" id="login-close" data-bs-dismiss="modal"></button>
      <button type="button" id="login-cancel" data-bs-dismiss="modal"></button>
    </div>`;
  let show;
  beforeEach(() => {
    show = vi.fn();
    vi.stubGlobal("bootstrap", { Modal: { getOrCreateInstance: vi.fn(() => ({ show, hide: vi.fn() })) } });
  });

  it("sends the user to the SSO login when SSO is served", () => {
    document.body.innerHTML = MODAL;
    api.getConfig.mockReturnValue({ login_required: true, features: { sso_enabled: true, login_link: false } });
    promptLogin();
    expect(router.redirect).toHaveBeenCalledWith("sso/");
    expect(show).not.toHaveBeenCalled();
  });

  it("carries the search the user opened to the SSO login", () => {
    document.body.innerHTML = MODAL;
    api.getConfig.mockReturnValue({ login_required: true, features: { sso_enabled: true, login_link: "sso/" } });
    router.currentPath.mockReturnValue("/search");
    window.history.pushState({}, "", "/search/?q=kerberos&num=20&sort=score.desc");
    try {
      promptLogin();
      expect(router.redirect).toHaveBeenCalledWith("sso/?q=kerberos&num=20&sort=score.desc");
    } finally {
      window.history.pushState({}, "", "/");
    }
  });

  it("carries no query string from a page other than the search results", () => {
    document.body.innerHTML = MODAL;
    api.getConfig.mockReturnValue({ login_required: true, features: { sso_enabled: true, login_link: "sso/" } });
    router.currentPath.mockReturnValue("/cache");
    window.history.pushState({}, "", "/cache/?docId=abc");
    try {
      promptLogin();
      expect(router.redirect).toHaveBeenCalledWith("sso/");
    } finally {
      window.history.pushState({}, "", "/");
    }
  });

  it("opens a modal without close controls while login is required", () => {
    document.body.innerHTML = MODAL;
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    promptLogin();
    expect(show).toHaveBeenCalledTimes(1);
    expect(document.getElementById("login-close").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("login-cancel").classList.contains("d-none")).toBe(true);
  });

  it("opens a modal that can be closed when login is optional", () => {
    document.body.innerHTML = MODAL;
    api.getConfig.mockReturnValue({ features: {} });
    promptLogin();
    expect(show).toHaveBeenCalledTimes(1);
    expect(document.getElementById("login-cancel").classList.contains("d-none")).toBe(false);
    expect(router.redirect).not.toHaveBeenCalled();
  });
});

describe("attach — locked login modal", () => {
  const FIXTURE = `
    <ul id="auth-controls"></ul>
    <div id="login-modal">
      <form id="login-form">
        <button type="button" id="login-cancel" data-bs-dismiss="modal"></button>
        <input id="login-username" value="bob">
        <input id="login-password" value="pw">
        <div id="login-error" class="d-none"></div>
      </form>
    </div>`;
  let hide;
  const hideEvent = () => {
    const ev = new Event("hide.bs.modal", { cancelable: true });
    document.getElementById("login-modal").dispatchEvent(ev);
    return ev;
  };

  beforeEach(() => {
    hide = vi.fn();
    vi.stubGlobal("bootstrap", { Modal: { getOrCreateInstance: vi.fn(() => ({ show: vi.fn(), hide })) } });
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.get.mockRejectedValue({ code: "auth_required" });
  });

  it("refuses to close while login is required, and closes after a login", async () => {
    document.body.innerHTML = FIXTURE;
    await attach();
    promptLogin();
    expect(hideEvent().defaultPrevented).toBe(true);

    api.post.mockResolvedValue({ user: { name: "Bob" }, csrf_token: "Z" });
    document.getElementById("login-form").dispatchEvent(new Event("submit"));
    await flush();

    expect(hide).toHaveBeenCalled();
    expect(document.getElementById("login-cancel").classList.contains("d-none")).toBe(false);
    expect(hideEvent().defaultPrevented).toBe(false);
  });
});

describe("endSession", () => {
  it("shows the guest login link and takes a CSRF token for the new session", async () => {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.get.mockResolvedValueOnce({ authenticated: true, user: { name: "Al" } });
    await probeMe();
    api.get.mockResolvedValueOnce({ csrf_token: "fresh" });

    await endSession();

    expect(api.setAuthenticated).toHaveBeenLastCalledWith(false);
    expect(document.getElementById("login-btn")).not.toBeNull();
    expect(api.setCsrfToken).toHaveBeenCalledWith("fresh");
    expect(getCurrentUser()).toBeNull();
    expect(isLoginGateClosed()).toBe(true);
  });
});

describe("logout", () => {
  /**
   * Log in, click logout, and return the fess:auth:logout event (null when none was sent)
   * and the order in which the event and router.redirect happened.
   */
  async function logInThenLogOut(logoutEnv) {
    document.body.innerHTML = '<ul id="auth-controls"></ul>';
    api.getConfig.mockReturnValue({ features: {} });
    api.get.mockResolvedValueOnce({ authenticated: true, user: { name: "Al" } });
    await probeMe();
    api.post.mockResolvedValue(logoutEnv);
    const order = [];
    let event = null;
    const onLogout = (ev) => { event = ev; order.push("logout"); };
    router.redirect.mockImplementation(() => { order.push("redirect"); });
    document.addEventListener("fess:auth:logout", onLogout);
    try {
      document.getElementById("logout-btn").click();
      await flush();
    } finally {
      document.removeEventListener("fess:auth:logout", onLogout);
    }
    return { event, order };
  }

  it("continues to the identity provider's logout URL after announcing a logout that redirects", async () => {
    const { event, order } = await logInThenLogOut({ ok: true, csrf_token: "t", redirect_url: "https://idp.example.com/logout?id=1" });
    expect(event).not.toBeNull();
    expect(event.detail.redirecting).toBe(true);
    expect(router.redirect).toHaveBeenCalledTimes(1);
    expect(router.redirect).toHaveBeenCalledWith("https://idp.example.com/logout?id=1");
    expect(order).toEqual(["logout", "redirect"]);
  });

  it("does not follow a redirect_url that is not http(s)", async () => {
    const { event, order } = await logInThenLogOut({ ok: true, csrf_token: "t", redirect_url: "javascript:alert(1)" });
    expect(event).not.toBeNull();
    expect(event.detail.redirecting).toBe(false);
    expect(router.redirect).not.toHaveBeenCalled();
    expect(order).toEqual(["logout"]);
  });

  it("does not follow an http(s) redirect_url that is not a valid URL", async () => {
    const { event, order } = await logInThenLogOut({ ok: true, csrf_token: "t", redirect_url: "https://" });
    expect(event).not.toBeNull();
    expect(event.detail.redirecting).toBe(false);
    expect(router.redirect).not.toHaveBeenCalled();
    expect(order).toEqual(["logout"]);
  });

  it("stays on the page without a redirect_url", async () => {
    const { event, order } = await logInThenLogOut({ ok: true, csrf_token: "t" });
    expect(event).not.toBeNull();
    expect(event.detail.redirecting).toBe(false);
    expect(router.redirect).not.toHaveBeenCalled();
    expect(order).toEqual(["logout"]);
    expect(document.getElementById("login-btn")).not.toBeNull();
  });
});

// ---------------------------------------------------------------------------
// localizePasswordError: error-code / reason → localized message key.
// ---------------------------------------------------------------------------
describe("localizePasswordError", () => {
  it.each([
    [{ name: "NetworkError" }, "error.network"],
    [{ code: "RATE_LIMITED" }, "auth.error_rate_limited"],
    // The server's actual wire code is lowercase snake_case (V2ErrorCode
    // .RATE_LIMITED → "rate_limited"); api.js copies err.code through unchanged.
    // No httpStatus here, so the 429 fallback cannot cover for the code arm.
    [{ code: "rate_limited" }, "auth.error_rate_limited"],
    [{ httpStatus: 429 }, "auth.error_rate_limited"],
    [{ details: { reason: "invalid_current_password" } }, "profile.error_wrong_current"],
    [{ details: { reason: "errors.password_length", min_length: 8 } }, "profile.error_password_length"],
    [{ details: { reason: "errors.password_no_uppercase" } }, "profile.error_password_no_uppercase"],
    [{ details: { reason: "errors.password_no_lowercase" } }, "profile.error_password_no_lowercase"],
    [{ details: { reason: "errors.password_no_digit" } }, "profile.error_password_no_digit"],
    [{ details: { reason: "errors.password_no_special_char" } }, "profile.error_password_no_special_char"],
    [{ details: { reason: "errors.password_is_blacklisted" } }, "profile.error_password_blacklisted"],
    [{ details: { reason: "errors.blank_password" } }, "profile.error_blank_password"],
    [{ details: { reason: "new_password_required" } }, "profile.error_blank_password"],
    [{ details: { reason: "current_password_required" } }, "profile.error_blank_password"],
    [{ details: { reason: "password_mismatch" } }, "profile.error_mismatch"],
    // Without a reason, auth_required means the session is gone: the user must log in again.
    [{ code: "AUTH_REQUIRED" }, "flash.login_required"],
    // Same wire-code contract for V2ErrorCode.AUTH_REQUIRED → "auth_required".
    [{ code: "auth_required" }, "flash.login_required"],
    [{ httpStatus: 401 }, "flash.login_required"],
    [{ code: "auth_required", details: { reason: "invalid_current_password" } }, "profile.error_wrong_current"],
    [{ code: "SOMETHING_UNMAPPED" }, "error.server"],
    [null, "error.server"],
  ])("%o → %s", (err, key) => {
    expect(localizePasswordError(err)).toBe(key);
  });
});

describe("isSessionGone", () => {
  it.each([
    [{ code: "auth_required" }, true],
    [{ code: "AUTH_REQUIRED" }, true],
    [{ httpStatus: 401 }, true],
    [{ code: "auth_required", details: { reason: "invalid_current_password" } }, false],
    [{ code: "rate_limited" }, false],
    [{ name: "NetworkError" }, false],
    [null, false],
  ])("%o → %s", (err, expected) => {
    expect(isSessionGone(err)).toBe(expected);
  });
});

describe("attach — forced password change", () => {
  const FIXTURE = `
    <ul id="auth-controls"></ul>
    <div id="login-modal">
      <form id="login-form">
        <button type="button" id="login-cancel" data-bs-dismiss="modal"></button>
        <input id="login-username" value="admin">
        <input id="login-password" value="admin">
        <div id="login-error" class="d-none"></div>
      </form>
      <form id="password-change-form" class="d-none">
        <div id="password-change-error" class="d-none"></div>
        <input id="password-change-new">
        <input id="password-change-confirm">
        <button type="submit"></button>
      </form>
    </div>`;
  const ADMIN = { name: "admin", editable: true };
  let hide;
  let logins;
  const onLogin = () => { logins += 1; };
  const submit = (id) => document.getElementById(id).dispatchEvent(new Event("submit", { cancelable: true }));
  const setNewPassword = (value, confirm = value) => {
    document.getElementById("password-change-new").value = value;
    document.getElementById("password-change-confirm").value = confirm;
  };
  const isShown = (id) => !document.getElementById(id).classList.contains("d-none");

  beforeEach(async () => {
    hide = vi.fn();
    logins = 0;
    vi.stubGlobal("bootstrap", { Modal: { getOrCreateInstance: vi.fn(() => ({ show: vi.fn(), hide })) } });
    api.getConfig.mockReturnValue({ features: {} });
    api.get.mockRejectedValue({ code: "auth_required" });
    document.body.innerHTML = FIXTURE;
    document.addEventListener("fess:auth:login", onLogin);
    await attach();
  });
  afterEach(() => document.removeEventListener("fess:auth:login", onLogin));

  it("switches to the new-password form after a login whose password must change", async () => {
    api.post.mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true });
    submit("login-form");
    await flush();
    expect(isShown("password-change-form")).toBe(true);
    expect(isShown("login-form")).toBe(false);
    expect(hide).not.toHaveBeenCalled();
    expect(logins).toBe(1);
    expect(getCurrentUser()).toEqual(ADMIN);
  });

  it("closes the modal as usual when the user's password is not editable", async () => {
    api.post.mockResolvedValueOnce({ user: { name: "ldap", editable: false }, csrf_token: "A", password_change_required: true });
    submit("login-form");
    await flush();
    expect(isShown("password-change-form")).toBe(false);
    expect(hide).toHaveBeenCalled();
  });

  it("changes the password with the one just entered, then logs in with the new one", async () => {
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockResolvedValueOnce({ re_login_required: true })
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "B" });
    api.get.mockResolvedValue({ csrf_token: "R" });
    submit("login-form");
    await flush();
    setNewPassword("N3w-password!");
    submit("password-change-form");
    await flush();

    expect(api.post).toHaveBeenNthCalledWith(2, "/auth/password", {
      current_password: "admin",
      new_password: "N3w-password!",
      confirm_password: "N3w-password!",
    });
    expect(api.setCsrfToken).toHaveBeenCalledWith("R");
    expect(api.post).toHaveBeenNthCalledWith(3, "/auth/login", { username: "admin", password: "N3w-password!" });
    expect(api.setCsrfToken).toHaveBeenLastCalledWith("B");
    expect(hide).toHaveBeenCalled();
    expect(logins).toBe(2);
  });

  it("closes the modal when the change does not end the session", async () => {
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockResolvedValueOnce({});
    submit("login-form");
    await flush();
    setNewPassword("N3w-password!");
    submit("password-change-form");
    await flush();
    expect(api.post).toHaveBeenCalledTimes(2);
    expect(hide).toHaveBeenCalled();
  });

  it("does not send the new password again while the change is in flight", async () => {
    let finishChange;
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockImplementationOnce(() => new Promise((resolve) => { finishChange = resolve; }));
    submit("login-form");
    await flush();
    setNewPassword("N3w-password!");
    const button = document.querySelector('#password-change-form button[type="submit"]');

    submit("password-change-form");
    expect(button.disabled).toBe(true);
    submit("password-change-form");
    await flush();
    expect(api.post.mock.calls.filter((c) => c[0] === "/auth/password")).toHaveLength(1);

    finishChange({});
    await flush();
    expect(button.disabled).toBe(false);
    expect(hide).toHaveBeenCalled();
  });

  it("reports a mismatch without calling the API", async () => {
    api.post.mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true });
    submit("login-form");
    await flush();
    setNewPassword("one-password", "another-password");
    submit("password-change-form");
    await flush();
    expect(api.post).toHaveBeenCalledTimes(1);
    expect(document.getElementById("password-change-error").textContent).toBe("profile.error_mismatch");
    expect(isShown("password-change-error")).toBe(true);
  });

  it("says why the server refused the new password and keeps the form", async () => {
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockRejectedValueOnce({ code: "invalid_request", details: { reason: "errors.password_is_blacklisted" } });
    submit("login-form");
    await flush();
    setNewPassword("admin");
    submit("password-change-form");
    await flush();
    expect(document.getElementById("password-change-error").textContent).toBe("profile.error_password_blacklisted");
    expect(isShown("password-change-form")).toBe(true);
  });

  it("returns to the login form when the login with the new password fails", async () => {
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockResolvedValueOnce({ re_login_required: true })
      .mockRejectedValueOnce({ code: "rate_limited" });
    api.get.mockResolvedValue({ csrf_token: "R" });
    let loggedOut = false;
    const onLogout = () => { loggedOut = true; };
    document.addEventListener("fess:auth:logout", onLogout);
    try {
      submit("login-form");
      await flush();
      setNewPassword("N3w-password!");
      submit("password-change-form");
      await flush();
    } finally {
      document.removeEventListener("fess:auth:logout", onLogout);
    }
    expect(isShown("login-form")).toBe(true);
    expect(isShown("password-change-form")).toBe(false);
    expect(document.getElementById("login-username").value).toBe("admin");
    // Neither the old nor the new password stays in the forms.
    expect(document.getElementById("login-password").value).toBe("");
    expect(document.getElementById("password-change-new").value).toBe("");
    expect(document.getElementById("password-change-confirm").value).toBe("");
    expect(document.getElementById("login-error").textContent).toBe("auth.error_rate_limited");
    expect(api.setAuthenticated).toHaveBeenLastCalledWith(false);
    expect(loggedOut).toBe(true);
  });

  it("re-locks the modal when the re-login fails on a login-required site", async () => {
    api.getConfig.mockReturnValue({ login_required: true, features: {} });
    api.post
      .mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true })
      .mockResolvedValueOnce({ re_login_required: true })
      .mockRejectedValueOnce({ code: "rate_limited" });
    api.get.mockResolvedValue({ csrf_token: "R" });
    submit("login-form");
    await flush();
    setNewPassword("N3w-password!");
    submit("password-change-form");
    await flush();

    expect(document.getElementById("login-cancel").classList.contains("d-none")).toBe(true);
    const hideEvent = new Event("hide.bs.modal", { cancelable: true });
    document.getElementById("login-modal").dispatchEvent(hideEvent);
    expect(hideEvent.defaultPrevented).toBe(true);
  });

  it("forgets the password when the modal closes", async () => {
    api.post.mockResolvedValueOnce({ user: ADMIN, csrf_token: "A", password_change_required: true });
    submit("login-form");
    await flush();
    document.getElementById("login-modal").dispatchEvent(new Event("hidden.bs.modal"));
    expect(isShown("login-form")).toBe(true);
    expect(isShown("password-change-form")).toBe(false);
    setNewPassword("N3w-password!");
    submit("password-change-form");
    await flush();
    expect(api.post).toHaveBeenCalledTimes(1);
  });
});

// Kept last: vi.resetModules() below must not disturb the statically imported suites above.
describe("localizePasswordError (positional substitution)", () => {
  it("substitutes min_length into the localized password-length message", async () => {
    vi.resetModules(); // fresh, seedable i18n singleton (api/router stay mocked via vi.mock)
    const i18n = await import("../../../../main/webapp/themes/bootstrap/assets/i18n.js");
    installFetch(async () => jsonResponse({ "profile.error_password_length": "Minimum {0} characters" }));
    Object.defineProperty(navigator, "language", { value: "en", configurable: true });
    await i18n.init();
    const freshAuth = await import("../../../../main/webapp/themes/bootstrap/assets/auth.js");

    const msg = freshAuth.localizePasswordError({
      details: { reason: "errors.password_length", min_length: 8 },
    });
    expect(msg).toContain("8");

    vi.unstubAllGlobals();
  });
});
