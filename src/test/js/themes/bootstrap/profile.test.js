// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the profile / password-change view. api.js and
// router.js are mocked (router.js carries module state; api.post is driven per
// test). localizePasswordError moved to auth.js; its tests live in auth.test.js.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom } from "../../helpers/dom.js";

// vi.mock is hoisted, so the paths must be string literals (not the consts below).
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({ post: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({ navigate: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/auth.js", async (importOriginal) => ({
  ...(await importOriginal()),
  getCurrentUser: vi.fn(),
  promptLogin: vi.fn(),
  endSession: vi.fn(),
}));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import * as router from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import * as auth from "../../../../main/webapp/themes/bootstrap/assets/auth.js";
import { attach } from "../../../../main/webapp/themes/bootstrap/assets/profile.js";

beforeEach(() => {
  resetDom();
  api.post.mockReset();
  router.navigate.mockReset();
  auth.getCurrentUser.mockReset().mockReturnValue({ name: "Al", editable: true });
  auth.promptLogin.mockReset();
  auth.endSession.mockReset().mockResolvedValue(undefined);
});
afterEach(() => {
  vi.unstubAllGlobals();
  vi.useRealTimers();
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
    expect(document.querySelector("#password-form a.btn-secondary").getAttribute("href")).toBe("./");

    expect(document.getElementById("profile-error").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("profile-success").classList.contains("d-none")).toBe(true);
  });
});

describe("attach (who may change a password)", () => {
  it("asks a guest to log in instead of showing the form", () => {
    auth.getCurrentUser.mockReturnValue(null);
    mountProfile();
    expect(document.getElementById("password-form")).toBeNull();
    expect(document.querySelector("#profile-view .alert").textContent).toBe("flash.login_required");
    expect(auth.promptLogin).toHaveBeenCalledTimes(1);
  });

  it("sends a user whose password is managed elsewhere back home", () => {
    auth.getCurrentUser.mockReturnValue({ name: "ldap", editable: false });
    mountProfile();
    expect(document.getElementById("password-form")).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith("./", { replace: true });
    expect(auth.promptLogin).not.toHaveBeenCalled();
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

  it("after a re_login_required success ends the session and asks for a login, keeping the message", async () => {
    api.post.mockResolvedValue({ re_login_required: true });
    mountProfile();
    setPasswords("current", "newpass1", "newpass1");

    submitForm();

    await vi.waitFor(() => expect(auth.promptLogin).toHaveBeenCalledTimes(1));
    expect(auth.endSession).toHaveBeenCalledTimes(1);
    expect(document.getElementById("profile-success").textContent).toBe("profile.success");
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it("treats a 401 without a reason as an ended session", async () => {
    api.post.mockRejectedValue({ code: "auth_required" });
    mountProfile();
    setPasswords("current", "newpass1", "newpass1");

    submitForm();

    await vi.waitFor(() => expect(auth.promptLogin).toHaveBeenCalledTimes(1));
    expect(auth.endSession).toHaveBeenCalledTimes(1);
    expect(document.getElementById("profile-error").textContent).toBe("flash.login_required");
  });

  it("keeps the session when only the current password is wrong", async () => {
    api.post.mockRejectedValue({ code: "auth_required", details: { reason: "invalid_current_password" } });
    mountProfile();
    setPasswords("wrong", "newpass1", "newpass1");

    submitForm();

    const err = document.getElementById("profile-error");
    await vi.waitFor(() => expect(err.classList.contains("d-none")).toBe(false));
    expect(err.textContent).toBe("profile.error_wrong_current");
    expect(auth.endSession).not.toHaveBeenCalled();
    expect(auth.promptLogin).not.toHaveBeenCalled();
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
