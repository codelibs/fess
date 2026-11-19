// Admin login and new-password pages: focus the first field (or the one that failed), then the passkey button.
(function() {
  "use strict";

  function focusFirstField() {
    var error = document.querySelector(".form-group .has-error");
    var errorField = error && error.nextElementSibling;
    var first = document.querySelector(':is(.login-box, section.content) :is(input[type="text"], select, textarea)');
    if (errorField && errorField.matches("input, select, textarea")) {
      errorField.focus();
    } else if (first) {
      first.focus();
    }
  }

  function ready(fn) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else {
      fn();
    }
  }

  ready(focusFirstField);
})();

// Passkey (WebAuthn) login of the admin login page: POST options, navigator.credentials.get, POST verify,
// then on to where the password login would go. The page does not load the theme bundle, so this mirrors its
// toPasskeyRequestOptions / passkeyCredentialToJson (themes/bootstrap/assets/auth.js).
window.FessPasskeyLogin = (function() {
  var DEFAULT_PATH = "/admin/";

  // Base64url <-> ArrayBuffer: WebAuthn carries its binary values (challenge, ids, signatures) as Base64url in JSON.
  function toBuffer(value) {
    var base64 = String(value).replace(/-/g, "+").replace(/_/g, "/");
    var binary = atob(base64 + "=".repeat((4 - (base64.length % 4)) % 4));
    var bytes = new Uint8Array(binary.length);
    for (var i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
  }

  function toBase64url(buffer) {
    var bytes = ArrayBuffer.isView(buffer)
      ? new Uint8Array(buffer.buffer, buffer.byteOffset, buffer.byteLength)
      : new Uint8Array(buffer);
    var binary = "";
    for (var i = 0; i < bytes.length; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  }

  // The options envelope of /auth/passkey/login/options -> the publicKey argument of navigator.credentials.get.
  function toRequestOptions(envelope) {
    var options = {};
    Object.keys(envelope).forEach(function(key) {
      if (key !== "status") {
        options[key] = envelope[key];
      }
    });
    options.challenge = toBuffer(options.challenge);
    options.allowCredentials = (options.allowCredentials || []).map(function(c) {
      return Object.assign({}, c, { id: toBuffer(c.id) });
    });
    return options;
  }

  // The PublicKeyCredential navigator.credentials.get returned -> the JSON /auth/passkey/login/verify reads.
  function credentialToJson(credential) {
    var r = credential.response;
    var response = { clientDataJSON: toBase64url(r.clientDataJSON) };
    ["authenticatorData", "signature", "userHandle"].forEach(function(key) {
      if (r[key]) {
        response[key] = toBase64url(r[key]);
      }
    });
    return {
      id: credential.id,
      rawId: toBase64url(credential.rawId),
      type: credential.type,
      response: response,
      clientExtensionResults: typeof credential.getClientExtensionResults === "function" ? credential.getClientExtensionResults() : {}
    };
  }

  // The saved request path is relative to the context path: only a plain application path is followed, never
  // "//host", a backslash form or a control character.
  function isSafePath(path) {
    return typeof path === "string" && path.charAt(0) === "/" && path.charAt(1) !== "/" && !/[\\\u0000-\u001f\u007f]/.test(path);
  }

  function isCancel(e) {
    return !!e && (e.name === "NotAllowedError" || e.name === "AbortError");
  }

  function isSupported() {
    return !!window.PublicKeyCredential && !!navigator.credentials;
  }

  function post(path, body) {
    return fetch(document.getElementById("contextPath").value + "/api/v2" + path, {
      method: "POST",
      credentials: "same-origin",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify(body)
    }).then(function(resp) {
      return resp.json().then(function(json) {
        var envelope = json && json.response;
        if (!envelope || envelope.status !== 0) {
          throw new Error("passkey request failed");
        }
        return envelope;
      });
    });
  }

  function signIn(button) {
    var error = document.getElementById("passkey-error");
    if (error) {
      error.classList.add("d-none");
    }
    button.disabled = true;
    return post("/auth/passkey/login/options", {})
      .then(function(options) {
        return navigator.credentials.get({ publicKey: toRequestOptions(options) });
      })
      .then(function(credential) {
        return post("/auth/passkey/login/verify", { credential: credentialToJson(credential) });
      })
      .then(function() {
        var saved = button.getAttribute("data-redirect");
        api.navigate(document.getElementById("contextPath").value + (isSafePath(saved) ? saved : DEFAULT_PATH));
      })
      .catch(function(e) {
        // The user dismissed the browser's passkey prompt: nothing to report.
        if (!isCancel(e) && error) {
          error.classList.remove("d-none");
        }
      })
      .then(function() {
        button.disabled = false;
      });
  }

  var api = {
    toBuffer: toBuffer,
    toBase64url: toBase64url,
    toRequestOptions: toRequestOptions,
    credentialToJson: credentialToJson,
    isSafePath: isSafePath,
    navigate: function(url) {
      window.location.assign(url);
    }
  };

  function bindButton() {
    var button = document.getElementById("passkey-login-btn");
    if (button && isSupported()) {
      button.classList.remove("d-none");
      button.addEventListener("click", function() {
        signIn(button);
      });
    }
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", bindButton);
  } else {
    bindButton();
  }

  return api;
})();
