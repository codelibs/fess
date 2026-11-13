// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the profile / password-change view. api.js and
// router.js are mocked (router.js carries module state; api.post is driven per
// test). localizePasswordError moved to auth.js; its tests live in auth.test.js.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom } from "../../helpers/dom.js";

// vi.mock is hoisted, so the paths must be string literals (not the consts below).
vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  post: vi.fn(), get: vi.fn(), del: vi.fn(), getConfig: vi.fn(),
}));
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
  api.get.mockReset();
  api.del.mockReset();
  api.getConfig.mockReset();
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

// ---------------------------------------------------------------------------
// Passkeys section.
// ---------------------------------------------------------------------------
describe("passkey section", () => {
  const flush = () => new Promise((r) => setTimeout(r));
  const LIST = {
    can_register: true,
    passkeys: [
      { id: "AQ", label: "<img src=x onerror=alert(1)>", created_at: 1700000000000, last_used_at: null },
      { id: "Ag", label: "Work laptop", created_at: 1700000000000, last_used_at: 1700003600000 },
    ],
  };
  let create;

  beforeEach(() => {
    api.getConfig.mockReturnValue({ features: { passkey_enabled: true } });
    api.get.mockResolvedValue(LIST);
    create = vi.fn();
    vi.stubGlobal("PublicKeyCredential", function PublicKeyCredential() {});
    Object.defineProperty(navigator, "credentials", { value: { create }, configurable: true });
  });
  afterEach(() => {
    delete navigator.credentials;
  });

  const mountAndLoad = async () => {
    mountProfile();
    await flush();
  };
  const submitPasskeyForm = (label, password) => {
    document.getElementById("passkey-label").value = label;
    document.getElementById("passkey-current-password").value = password;
    document.getElementById("passkey-form").dispatchEvent(new Event("submit", { cancelable: true, bubbles: true }));
  };

  it("renders no section when passkeys are disabled", async () => {
    api.getConfig.mockReturnValue({ features: { passkey_enabled: false } });
    await mountAndLoad();
    expect(document.getElementById("passkey-card")).toBeNull();
    expect(api.get).not.toHaveBeenCalled();
  });

  it("lists passkeys with label as text, never as markup", async () => {
    await mountAndLoad();
    const items = document.querySelectorAll("#passkey-list li");
    expect(items.length).toBe(2);
    expect(document.getElementById("passkey-card").classList.contains("d-none")).toBe(false);
    expect(items[0].querySelector(".fw-bold").textContent).toBe("<img src=x onerror=alert(1)>");
    expect(document.querySelector("#passkey-list img")).toBeNull();
    // i18n is empty in the tests, so t() returns the keys: created and last-used are both shown.
    expect(items[0].querySelector("small").textContent).toBe("profile.passkey_created / profile.passkey_last_used");
    expect(document.getElementById("passkey-form").classList.contains("d-none")).toBe(false);
  });

  it("hides the registration form when the session cannot register", async () => {
    api.get.mockResolvedValue({ can_register: false, passkeys: LIST.passkeys });
    await mountAndLoad();
    expect(document.getElementById("passkey-form").classList.contains("d-none")).toBe(true);
    expect(document.querySelectorAll("#passkey-list li").length).toBe(2);
  });

  it("hides the whole section for a session with no passkeys that cannot register", async () => {
    api.get.mockResolvedValue({ can_register: false, passkeys: [] });
    await mountAndLoad();
    expect(document.getElementById("passkey-card").classList.contains("d-none")).toBe(true);
  });

  it("hides the registration form when the browser has no PublicKeyCredential", async () => {
    vi.stubGlobal("PublicKeyCredential", undefined);
    await mountAndLoad();
    expect(document.getElementById("passkey-form").classList.contains("d-none")).toBe(true);
  });

  it("registers: options with the password, credentials.create, verify with label and credential, then reloads", async () => {
    api.post.mockImplementation(async (path) =>
      path === "/auth/passkey/register/options"
        ? { status: 0, challenge: "AQI", rp: { id: "example.com", name: "Fess" }, user: { id: "AwQ", name: "al", displayName: "al" }, excludeCredentials: [] }
        : { passkey: { id: "AQ" } });
    create.mockResolvedValue({
      id: "AQ", rawId: new Uint8Array([1]).buffer, type: "public-key",
      response: { clientDataJSON: new Uint8Array([2]).buffer, attestationObject: new Uint8Array([3]).buffer, getTransports: () => ["internal"] },
      getClientExtensionResults: () => ({}),
    });
    await mountAndLoad();

    submitPasskeyForm("  My phone ", "secret");
    await flush();

    expect(api.post.mock.calls[0]).toEqual(["/auth/passkey/register/options", { current_password: "secret" }]);
    expect(Array.from(new Uint8Array(create.mock.calls[0][0].publicKey.user.id))).toEqual([3, 4]);
    expect(api.post.mock.calls[1][0]).toBe("/auth/passkey/register/verify");
    expect(api.post.mock.calls[1][1].label).toBe("My phone");
    expect(api.post.mock.calls[1][1].credential.response).toEqual({ clientDataJSON: "Ag", attestationObject: "Aw", transports: ["internal"] });
    expect(document.getElementById("passkey-success").textContent).toBe("profile.passkey_registered");
    expect(api.get).toHaveBeenCalledTimes(2); // reloaded
    expect(document.getElementById("passkey-current-password").value).toBe(""); // password not kept
  });

  it("tells a wrong current password without asking the browser or ending the session", async () => {
    api.post.mockRejectedValue({ name: "ApiError", code: "auth_required", httpStatus: 401, details: { reason: "invalid_current_password" } });
    await mountAndLoad();

    submitPasskeyForm("My phone", "wrong");
    await flush();

    expect(create).not.toHaveBeenCalled();
    expect(document.getElementById("passkey-error").textContent).toBe("profile.error_wrong_current");
    expect(auth.endSession).not.toHaveBeenCalled();
  });

  it("treats a 401 without a reason as an ended session", async () => {
    api.post.mockRejectedValue({ name: "ApiError", code: "auth_required", httpStatus: 401 });
    await mountAndLoad();

    submitPasskeyForm("My phone", "secret");
    await flush();

    expect(auth.endSession).toHaveBeenCalledTimes(1);
    expect(auth.promptLogin).toHaveBeenCalledTimes(1);
  });

  it("is silent when the user dismisses the browser prompt", async () => {
    api.post.mockResolvedValue({ challenge: "AQI", user: { id: "AwQ" } });
    create.mockRejectedValue(Object.assign(new Error("cancelled"), { name: "NotAllowedError" }));
    await mountAndLoad();

    submitPasskeyForm("My phone", "secret");
    await flush();

    expect(api.post).toHaveBeenCalledTimes(1);
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(true);
    expect(document.getElementById("passkey-success").classList.contains("d-none")).toBe(true);
  });

  it("clears the current password when the registration fails", async () => {
    api.post.mockRejectedValue({ name: "ApiError", code: "auth_required", httpStatus: 401, details: { reason: "invalid_current_password" } });
    await mountAndLoad();

    submitPasskeyForm("My phone", "wrong");
    await flush();

    expect(document.getElementById("passkey-current-password").value).toBe("");
  });

  it("clears the current password when the user dismisses the browser prompt", async () => {
    api.post.mockResolvedValue({ challenge: "AQI", user: { id: "AwQ" } });
    create.mockRejectedValue(Object.assign(new Error("cancelled"), { name: "NotAllowedError" }));
    await mountAndLoad();

    submitPasskeyForm("My phone", "secret");
    await flush();

    expect(document.getElementById("passkey-current-password").value).toBe("");
  });

  it("tells that a name of spaces only is missing, without calling the server", async () => {
    await mountAndLoad();
    const labelInput = document.getElementById("passkey-label");
    const invalid = vi.fn();
    labelInput.addEventListener("invalid", invalid);

    submitPasskeyForm("   ", "secret");
    await flush();

    expect(api.post).not.toHaveBeenCalled();
    expect(labelInput.value).toBe("");
    expect(invalid).toHaveBeenCalledTimes(1);
    expect(document.activeElement).toBe(labelInput);
    // the password is kept: nothing was sent yet
    expect(document.getElementById("passkey-current-password").value).toBe("secret");
  });

  it("tells a refused name or the passkey limit as a failed registration", async () => {
    for (const reason of ["label_invalid_characters", "too_many_passkeys"]) {
      api.post.mockReset();
      api.post.mockRejectedValue({ name: "ApiError", code: "invalid_request", httpStatus: 400, details: { reason } });
      await mountAndLoad();

      submitPasskeyForm("My phone", "secret");
      await flush();

      expect(document.getElementById("passkey-error").textContent).toBe("profile.passkey_error_register");
    }
  });

  it("shows a registration failure when the authenticator refuses", async () => {
    api.post.mockResolvedValue({ challenge: "AQI", user: { id: "AwQ" } });
    create.mockRejectedValue(Object.assign(new Error("already registered"), { name: "InvalidStateError" }));
    await mountAndLoad();

    submitPasskeyForm("My phone", "secret");
    await flush();

    expect(document.getElementById("passkey-error").textContent).toBe("profile.passkey_error_register");
  });

  it("deletes after a confirmation, and not when the confirmation is declined", async () => {
    api.del.mockResolvedValue({ ok: true });
    await mountAndLoad();
    const button = () => document.querySelectorAll("#passkey-list li button")[1];

    vi.stubGlobal("confirm", vi.fn().mockReturnValue(false));
    button().click();
    await flush();
    expect(api.del).not.toHaveBeenCalled();

    vi.stubGlobal("confirm", vi.fn().mockReturnValue(true));
    button().click();
    await flush();
    expect(window.confirm).toHaveBeenCalledWith("profile.passkey_delete_confirm");
    expect(api.del).toHaveBeenCalledWith("/auth/passkey/Ag");
    expect(document.getElementById("passkey-success").textContent).toBe("profile.passkey_deleted");
    expect(api.get).toHaveBeenCalledTimes(2);
  });
});
