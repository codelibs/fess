// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the profile / password-change view. api.js and
// router.js are mocked (router.js carries module state; api.post is driven per
// test), while i18n.js stays real — its t() returns each key unchanged, so the
// error-mapping table asserts exact i18n keys. One isolated test seeds a real
// i18n bundle to prove positional {0} substitution of min_length.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom } from "../../helpers/dom.js";
import { installFetch, jsonResponse } from "../../helpers/net.js";

// vi.mock is hoisted, so the paths must be string literals (not the consts below).
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({ post: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({ navigate: vi.fn() }));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import * as router from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import {
  localizePasswordError,
  attach,
} from "../../../../main/webapp/themes/bootstrap/assets/profile.js";

const I18N = "../../../../main/webapp/themes/bootstrap/assets/i18n.js";
const PROFILE = "../../../../main/webapp/themes/bootstrap/assets/profile.js";

beforeEach(() => {
  resetDom();
  api.post.mockReset();
  router.navigate.mockReset();
});
afterEach(() => {
  vi.unstubAllGlobals();
  vi.useRealTimers();
});

// ---------------------------------------------------------------------------
// localizePasswordError: error-code / reason → localized message key.
// ---------------------------------------------------------------------------
describe("localizePasswordError", () => {
  it.each([
    [{ name: "NetworkError" }, "error.network"],
    [{ code: "RATE_LIMITED" }, "auth.error_rate_limited"],
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
    [{ code: "AUTH_REQUIRED" }, "profile.error_wrong_current"],
    [{ httpStatus: 401 }, "profile.error_wrong_current"],
    [{ code: "SOMETHING_UNMAPPED" }, "error.server"],
    [null, "error.server"],
  ])("%o → %s", (err, key) => {
    expect(localizePasswordError(err)).toBe(key);
  });
});

// ---------------------------------------------------------------------------
// attach(): DOM scaffold.
// ---------------------------------------------------------------------------

// Mount the #profile-view container and run attach(); shared by both attach blocks.
const mountProfile = () => {
  document.body.innerHTML = '<div id="profile-view"></div>';
  attach();
};

describe("attach (structure)", () => {
  it("does nothing (and does not throw) when #profile-view is absent", () => {
    expect(() => attach()).not.toThrow();
    expect(document.getElementById("password-form")).toBeNull();
  });

  it("renders the password-change form scaffold with the expected controls", () => {
    mountProfile();

    expect(document.getElementById("password-form")).not.toBeNull();

    const pwInputs = document.querySelectorAll('#password-form input[type="password"]');
    expect(pwInputs.length).toBe(3);
    expect(document.getElementById("old-password")).not.toBeNull();
    expect(document.getElementById("new-password").getAttribute("minlength")).toBe("8");
    expect(document.getElementById("confirm-password")).not.toBeNull();

    expect(document.querySelector('#password-form button[type="submit"]')).not.toBeNull();
    expect(document.querySelector("#password-form a.btn-secondary").getAttribute("href")).toBe("/");

    expect(document.getElementById("profile-error").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("profile-success").classList.contains("d-none")).toBe(true);
  });
});

// ---------------------------------------------------------------------------
// attach(): form-submit flows. The submit handler is async, so post-dispatch
// assertions flush via vi.waitFor / fake-timer advance.
// ---------------------------------------------------------------------------
describe("attach (submit)", () => {
  const setPasswords = (oldP, newP, confP) => {
    document.getElementById("old-password").value = oldP;
    document.getElementById("new-password").value = newP;
    document.getElementById("confirm-password").value = confP;
  };
  const submitForm = () =>
    document
      .getElementById("password-form")
      .dispatchEvent(new Event("submit", { cancelable: true, bubbles: true }));

  it("shows a mismatch error and never calls the API when new != confirm", () => {
    mountProfile();
    setPasswords("current", "aaaaaaaa", "bbbbbbbb");

    submitForm();

    const err = document.getElementById("profile-error");
    expect(err.classList.contains("d-none")).toBe(false);
    expect(err.textContent).toBe("profile.error_mismatch");
    expect(api.post).not.toHaveBeenCalled();
  });

  it("on success shows the success alert and resets the form", async () => {
    api.post.mockResolvedValue({});
    mountProfile();
    setPasswords("current", "newpass1", "newpass1");

    submitForm();

    const ok = document.getElementById("profile-success");
    await vi.waitFor(() => expect(ok.classList.contains("d-none")).toBe(false));
    expect(ok.textContent).toBe("profile.success");
    expect(api.post).toHaveBeenCalledTimes(1);
    expect(document.getElementById("new-password").value).toBe(""); // form.reset()
  });

  it("navigates home 2s after a re_login_required success", async () => {
    vi.useFakeTimers();
    api.post.mockResolvedValue({ re_login_required: true });
    mountProfile();
    setPasswords("current", "newpass1", "newpass1");

    submitForm();
    await vi.advanceTimersByTimeAsync(2000); // flush microtasks, then fire the 2s timer

    expect(router.navigate).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith("/");
  });

  it("shows the localized API error when the request rejects", async () => {
    api.post.mockRejectedValue({ code: "RATE_LIMITED" });
    mountProfile();
    setPasswords("current", "newpass1", "newpass1");

    submitForm();

    const err = document.getElementById("profile-error");
    await vi.waitFor(() => expect(err.classList.contains("d-none")).toBe(false));
    expect(err.textContent).toBe("auth.error_rate_limited");
    expect(router.navigate).not.toHaveBeenCalled();
  });
});

// ---------------------------------------------------------------------------
// Positional substitution: seed a real i18n bundle in an isolated module graph
// so the {0} placeholder actually renders min_length. Kept last so its
// resetModules never disturbs the statically-imported suites above.
// ---------------------------------------------------------------------------
describe("localizePasswordError (positional substitution)", () => {
  it("substitutes min_length into the localized password-length message", async () => {
    vi.resetModules(); // fresh, seedable i18n singleton (api/router stay mocked via vi.mock)
    const i18n = await import(I18N);
    installFetch(async () => jsonResponse({ "profile.error_password_length": "Minimum {0} characters" }));
    Object.defineProperty(navigator, "language", { value: "en", configurable: true });
    await i18n.init();
    const freshProfile = await import(PROFILE);

    const msg = freshProfile.localizePasswordError({
      details: { reason: "errors.password_length", min_length: 8 },
    });
    expect(msg).toContain("8");

    vi.unstubAllGlobals();
  });
});
