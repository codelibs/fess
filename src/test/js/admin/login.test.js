// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, beforeAll, beforeEach, afterEach, vi } from "vitest";
import jquerySource from "../../../main/webapp/js/admin/jquery-3.7.1.min.js?raw";
import loginSource from "../../../main/webapp/js/login.js?raw";
import { jsonResponse } from "../helpers/net.js";

// The shipped file is a plain script that uses the globals of the page; run it as the page does.
const run = (source) => (0, window.eval)(source);

const bytes = (...values) => new Uint8Array(values).buffer;

// What the passkey part of admin/login/index.jsp renders for a page that offers passkeys.
function page({ redirect = "", contextPath = "/fess" } = {}) {
  return `
<form id="login"></form>
<div id="passkey-error" class="d-none"><ul role="alert"><li>Invalid username or password.</li></ul></div>
<button type="button" id="passkey-login-btn" class="d-none" data-redirect="${redirect}"></button>
<input type="hidden" id="contextPath" value="${contextPath}" />`;
}

// login.js binds the button when the document is ready, to the elements that exist then.
async function mount(html) {
  document.body.innerHTML = html;
  run(loginSource);
  await new Promise((resolve) => window.jQuery(resolve));
  const navigate = vi.fn();
  window.FessPasskeyLogin.navigate = navigate;
  return navigate;
}

const options = (extra = {}) => ({ response: { status: 0, challenge: "AQID", rpId: "example.com", timeout: 300000, userVerification: "required", allowCredentials: [], ...extra } });
const accepted = { response: { status: 0, user: { username: "alice" }, csrf_token: "t" } };
const rejected = { response: { status: 1, error: { code: "invalid_credentials", message: "boom: raw server text" } } };

const assertion = () => ({
  id: "cred-id",
  rawId: bytes(251, 255, 254),
  type: "public-key",
  response: {
    clientDataJSON: bytes(1, 2),
    authenticatorData: bytes(3),
    signature: bytes(4, 5),
    userHandle: bytes(6),
    getTransports: () => ["internal"],
  },
  getClientExtensionResults: () => ({}),
});

function stubBrowser(get) {
  vi.stubGlobal("PublicKeyCredential", function PublicKeyCredential() {});
  Object.defineProperty(navigator, "credentials", { value: { get }, configurable: true });
}

const click = async () => {
  document.getElementById("passkey-login-btn").click();
  // let the promise chain of the click run to its end
  await vi.waitFor(() => expect(document.getElementById("passkey-login-btn").disabled).toBe(false));
};

beforeAll(() => {
  run(jquerySource);
});

afterEach(() => {
  vi.unstubAllGlobals();
  delete navigator.credentials;
  document.body.innerHTML = "";
});

describe("Base64url helpers", () => {
  beforeEach(async () => {
    await mount(page());
  });

  it("round-trips bytes through the URL-safe alphabet without padding", () => {
    const { toBase64url, toBuffer } = window.FessPasskeyLogin;
    expect(toBase64url(bytes(251, 255, 254))).toBe("-__-");
    expect(new Uint8Array(toBuffer("-__-"))).toEqual(new Uint8Array([251, 255, 254]));
    expect(toBase64url(bytes(1, 2))).toBe("AQI");
    expect(new Uint8Array(toBuffer("AQI"))).toEqual(new Uint8Array([1, 2]));
  });

  it("encodes a typed array view by its own bytes", () => {
    const view = new Uint8Array([9, 1, 2, 3]).subarray(1);
    expect(window.FessPasskeyLogin.toBase64url(view)).toBe("AQID");
  });
});

describe("toRequestOptions", () => {
  beforeEach(async () => {
    await mount(page());
  });

  it("decodes the challenge and credential ids and leaves the envelope status out", () => {
    const out = window.FessPasskeyLogin.toRequestOptions({
      status: 0,
      challenge: "AQID",
      rpId: "example.com",
      allowCredentials: [{ type: "public-key", id: "BAU" }],
    });
    expect("status" in out).toBe(false);
    expect(out.rpId).toBe("example.com");
    expect(new Uint8Array(out.challenge)).toEqual(new Uint8Array([1, 2, 3]));
    expect(out.allowCredentials[0].type).toBe("public-key");
    expect(new Uint8Array(out.allowCredentials[0].id)).toEqual(new Uint8Array([4, 5]));
  });

  it("treats a missing allowCredentials as empty", () => {
    expect(window.FessPasskeyLogin.toRequestOptions({ challenge: "AQID" }).allowCredentials).toEqual([]);
  });
});

describe("credentialToJson", () => {
  beforeEach(async () => {
    await mount(page());
  });

  it("encodes the binary parts as Base64url and keeps what the server reads", () => {
    const json = window.FessPasskeyLogin.credentialToJson(assertion());
    expect(json).toEqual({
      id: "cred-id",
      rawId: "-__-",
      type: "public-key",
      response: { clientDataJSON: "AQI", authenticatorData: "Aw", signature: "BAU", userHandle: "Bg" },
      clientExtensionResults: {},
    });
  });

  it("leaves out a userHandle the authenticator did not return", () => {
    const credential = assertion();
    credential.response.userHandle = null;
    expect("userHandle" in window.FessPasskeyLogin.credentialToJson(credential).response).toBe(false);
  });
});

describe("isSafePath", () => {
  beforeEach(async () => {
    await mount(page());
  });

  it("accepts only a plain application path", () => {
    const { isSafePath } = window.FessPasskeyLogin;
    expect(isSafePath("/admin/user/?q=a")).toBe(true);
    for (const bad of ["", null, undefined, "admin/", "//evil.example/", "/\\evil.example", "https://evil.example/", "javascript:alert(1)", "/a\nb", "/a\u0000b"]) {
      expect(isSafePath(bad)).toBe(false);
    }
  });
});

describe("the passkey button", () => {
  it("stays hidden when the browser has no WebAuthn", async () => {
    vi.stubGlobal("PublicKeyCredential", undefined);
    await mount(page());
    expect(document.getElementById("passkey-login-btn").classList.contains("d-none")).toBe(true);
  });

  it("shows when the browser has WebAuthn", async () => {
    stubBrowser(vi.fn());
    await mount(page());
    expect(document.getElementById("passkey-login-btn").classList.contains("d-none")).toBe(false);
  });

  it("is absent on the other page that loads login.js, which must not fail", async () => {
    stubBrowser(vi.fn());
    await mount(`<input type="hidden" id="contextPath" value="" />`);
    expect(window.FessPasskeyLogin).toBeDefined();
  });
});

describe("the passkey login", () => {
  it("runs options, get and verify, then goes to the saved path under the context path", async () => {
    const get = vi.fn(async () => assertion());
    stubBrowser(get);
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(jsonResponse(options()))
      .mockResolvedValueOnce(jsonResponse(accepted));
    vi.stubGlobal("fetch", fetchMock);
    const navigate = await mount(page({ redirect: "/admin/user/?q=a&amp;b=1" }));

    await click();

    expect(fetchMock).toHaveBeenCalledTimes(2);
    const [optionsUrl, optionsInit] = fetchMock.mock.calls[0];
    expect(optionsUrl).toBe("/fess/api/v2/auth/passkey/login/options");
    expect(optionsInit.method).toBe("POST");
    expect(optionsInit.credentials).toBe("same-origin");
    expect(optionsInit.headers["Content-Type"]).toBe("application/json");
    const publicKey = get.mock.calls[0][0].publicKey;
    expect(new Uint8Array(publicKey.challenge)).toEqual(new Uint8Array([1, 2, 3]));
    expect(publicKey.rpId).toBe("example.com");
    const [verifyUrl, verifyInit] = fetchMock.mock.calls[1];
    expect(verifyUrl).toBe("/fess/api/v2/auth/passkey/login/verify");
    expect(verifyInit.credentials).toBe("same-origin");
    expect(JSON.parse(verifyInit.body)).toEqual({ credential: window.FessPasskeyLogin.credentialToJson(assertion()) });
    expect(navigate).toHaveBeenCalledWith("/fess/admin/user/?q=a&b=1");
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(true);
  });

  it("goes to the admin top when no path was saved", async () => {
    stubBrowser(vi.fn(async () => assertion()));
    vi.stubGlobal("fetch", vi.fn().mockResolvedValueOnce(jsonResponse(options())).mockResolvedValueOnce(jsonResponse(accepted)));
    const navigate = await mount(page({ contextPath: "" }));
    await click();
    expect(navigate).toHaveBeenCalledWith("/admin/");
  });

  it("ignores a saved path that is not a plain application path", async () => {
    stubBrowser(vi.fn(async () => assertion()));
    vi.stubGlobal("fetch", vi.fn().mockResolvedValueOnce(jsonResponse(options())).mockResolvedValueOnce(jsonResponse(accepted)));
    const navigate = await mount(page({ redirect: "//evil.example/x", contextPath: "" }));
    await click();
    expect(navigate).toHaveBeenCalledWith("/admin/");
  });

  it("is silent when the user dismisses the prompt", async () => {
    stubBrowser(vi.fn(async () => { throw new DOMException("dismissed", "NotAllowedError"); }));
    const fetchMock = vi.fn().mockResolvedValueOnce(jsonResponse(options()));
    vi.stubGlobal("fetch", fetchMock);
    const navigate = await mount(page());
    await click();
    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(navigate).not.toHaveBeenCalled();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(true);
  });

  it("is silent when the prompt is aborted", async () => {
    stubBrowser(vi.fn(async () => { throw new DOMException("aborted", "AbortError"); }));
    vi.stubGlobal("fetch", vi.fn().mockResolvedValueOnce(jsonResponse(options())));
    await mount(page());
    await click();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(true);
  });

  it("shows the prepared login failure text when the server rejects the assertion, never the server text", async () => {
    stubBrowser(vi.fn(async () => assertion()));
    vi.stubGlobal("fetch", vi.fn().mockResolvedValueOnce(jsonResponse(options())).mockResolvedValueOnce(jsonResponse(rejected, { status: 401 })));
    const navigate = await mount(page());
    await click();
    const error = document.getElementById("passkey-error");
    expect(error.classList.contains("d-none")).toBe(false);
    expect(error.textContent).toContain("Invalid username or password.");
    expect(error.textContent).not.toContain("boom");
    expect(navigate).not.toHaveBeenCalled();
  });

  it("shows the failure text when the options request fails", async () => {
    stubBrowser(vi.fn());
    vi.stubGlobal("fetch", vi.fn().mockRejectedValueOnce(new TypeError("network down")));
    await mount(page());
    await click();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(false);
  });

  it("shows the failure text when the browser fails for another reason", async () => {
    stubBrowser(vi.fn(async () => { throw new DOMException("no authenticator", "NotSupportedError"); }));
    vi.stubGlobal("fetch", vi.fn().mockResolvedValueOnce(jsonResponse(options())));
    await mount(page());
    await click();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(false);
  });

  it("hides an earlier failure when it is tried again", async () => {
    stubBrowser(vi.fn(async () => assertion()));
    vi.stubGlobal("fetch", vi.fn()
      .mockResolvedValueOnce(jsonResponse(options()))
      .mockResolvedValueOnce(jsonResponse(rejected, { status: 401 }))
      .mockResolvedValueOnce(jsonResponse(options()))
      .mockResolvedValueOnce(jsonResponse(accepted)));
    const navigate = await mount(page());
    await click();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(false);
    await click();
    expect(document.getElementById("passkey-error").classList.contains("d-none")).toBe(true);
    expect(navigate).toHaveBeenCalledTimes(1);
  });
});
