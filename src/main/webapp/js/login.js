$(function() {
  var $firstInput = $('input[type="text"],select,textarea', ".login-box,section.content").first();
  var $errorInput = $(".form-group .has-error").first().next("input,select,textarea");

  if ($errorInput.length) {
    $errorInput.focus();
  } else {
    $firstInput.focus();
  }

  $("section.content input").on("keypress", function(e) {
    if (e.which === 13) {
      var $submitButton = $("input#submit, button#submit");
      if ($submitButton.length > 0) {
        $submitButton.closest("form").trigger("submit");
      }
      return false;
    }
  });

  $(".table tr[data-href]").each(function() {
    var $row = $(this);
    $row.css("cursor", "pointer")
      .on("mouseenter", function() {
        $(this).addClass("active");
      })
      .on("mouseleave", function() {
        $(this).removeClass("active");
      })
      .on("click", function() {
        window.location.href = $(this).attr("data-href");
      });
  });

  $("#confirmToDelete").on("show.bs.modal", function(event) {
    var button = $(event.relatedTarget);
    var docId = button.data("docid");
    var title = button.data("title");
    var url = button.data("url");

    var $modal = $(this);
    $modal.find(".modal-body #delete-doc-title").text(title);
    $modal.find(".modal-body #delete-doc-url").text(url);
    $modal.find(".modal-footer input#docId").val(docId);
  });
});

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
    return fetch($("#contextPath").val() + "/api/v2" + path, {
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

  function signIn($button) {
    var $error = $("#passkey-error");
    $error.addClass("d-none");
    $button.prop("disabled", true);
    return post("/auth/passkey/login/options", {})
      .then(function(options) {
        return navigator.credentials.get({ publicKey: toRequestOptions(options) });
      })
      .then(function(credential) {
        return post("/auth/passkey/login/verify", { credential: credentialToJson(credential) });
      })
      .then(function() {
        var saved = $button.attr("data-redirect");
        api.navigate($("#contextPath").val() + (isSafePath(saved) ? saved : DEFAULT_PATH));
      })
      .catch(function(e) {
        // The user dismissed the browser's passkey prompt: nothing to report.
        if (!isCancel(e)) {
          $error.removeClass("d-none");
        }
      })
      .then(function() {
        $button.prop("disabled", false);
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

  $(function() {
    var $button = $("#passkey-login-btn");
    if ($button.length && isSupported()) {
      $button.removeClass("d-none").on("click", function() {
        signIn($button);
      });
    }
  });

  return api;
})();
