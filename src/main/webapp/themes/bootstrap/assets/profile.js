// SPDX-License-Identifier: Apache-2.0
// Profile / password-change view for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent/setAttribute — no innerHTML.

import * as api from "./api.js";
import {
  endSession, getCurrentUser, isPasskeyCancel, isPasskeyEnabled, isSessionGone, localizePasswordError,
  passkeyCredentialToJson, promptLogin, toPasskeyCreationOptions
} from "./auth.js";
import { formatDate } from "./format.js";
import { t } from "./i18n.js";
import * as router from "./router.js";

/**
 * Helper: create an element with optional className and textContent.
 *
 * @param {string} tag
 * @param {object} [opts]
 * @param {string} [opts.className]
 * @param {string} [opts.textContent]
 * @param {string} [opts.id]
 * @returns {HTMLElement}
 */
function el(tag, opts = {}) {
  const node = document.createElement(tag);
  if (opts.id) node.id = opts.id;
  if (opts.className) node.className = opts.className;
  if (opts.textContent !== undefined) node.textContent = opts.textContent;
  return node;
}

/**
 * Helper: create an input-group password field with a trailing lock icon.
 * A visually-hidden <label> keeps accessibility; placeholder doubles as visible hint.
 *
 * @param {string} labelText  - used as placeholder AND aria/label text
 * @param {string} inputId
 * @param {object} [inputAttrs] - additional attributes (minlength, autocomplete, etc.)
 * @returns {HTMLDivElement}   - wrapper div.mb-3
 */
function makePasswordField(labelText, inputId, inputAttrs = {}) {
  const wrapper = el("div", { className: "mb-3" });

  // Visually-hidden label for screen readers.
  const label = el("label", { className: "visually-hidden", textContent: labelText });
  label.setAttribute("for", inputId);
  wrapper.appendChild(label);

  const group = el("div", { className: "input-group" });

  const input = el("input", { id: inputId, className: "form-control" });
  input.type = "password";
  input.name = inputId;
  input.required = true;
  input.placeholder = labelText;
  input.setAttribute("aria-label", labelText);
  input.setAttribute("autocomplete", inputAttrs.autocomplete || "current-password");
  if (inputAttrs.minlength) input.setAttribute("minlength", String(inputAttrs.minlength));
  group.appendChild(input);

  // Trailing lock icon span.
  const iconSpan = el("span", { className: "input-group-text" });
  const icon = el("i", { className: "fa fa-lock fa-fw" });
  icon.setAttribute("aria-hidden", "true");
  iconSpan.appendChild(icon);
  group.appendChild(iconSpan);

  wrapper.appendChild(group);
  return wrapper;
}

/** The localized message for a failed passkey registration or passkey list call. */
function passkeyErrorMessage(err) {
  if (err && err.name === "NetworkError") return t("error.network");
  const code = err && err.code;
  if (code === "rate_limited" || (err && err.httpStatus === 429)) return t("auth.error_rate_limited");
  const reason = err && err.details && err.details.reason;
  if (reason === "invalid_current_password") return t("profile.error_wrong_current");
  if (reason === "current_password_required") return t("profile.error_blank_password");
  if (reason === "registration_failed" || reason === "label_required" || reason === "label_too_long"
    || reason === "label_invalid_characters" || reason === "too_many_passkeys") {
    return t("profile.passkey_error_register");
  }
  return t("error.server");
}

/**
 * Build the "Passkeys" card: the user's passkeys with a delete button each, and, for a local
 * user, a form that registers a new one (name + current password, then navigator.credentials.create).
 * The card stays hidden for a user who has none and cannot register one (e.g. an LDAP or SSO user).
 * Every server-supplied text (the passkey name) is rendered with textContent.
 *
 * @returns {HTMLElement} the card, filled in once GET /auth/passkey answers
 */
function buildPasskeySection() {
  const card = el("div", { className: "card mt-4 d-none", id: "passkey-card" });
  const body = el("div", { className: "card-body" });
  card.appendChild(body);
  body.appendChild(el("h2", { className: "h5 text-center mb-3", textContent: t("profile.passkey_title") }));

  const errorDiv = el("div", { id: "passkey-error", className: "alert alert-danger d-none" });
  errorDiv.setAttribute("role", "alert");
  body.appendChild(errorDiv);
  const successDiv = el("div", { id: "passkey-success", className: "alert alert-success d-none" });
  successDiv.setAttribute("role", "status");
  body.appendChild(successDiv);

  const list = el("ul", { id: "passkey-list", className: "list-group mb-3" });
  body.appendChild(list);

  const form = el("form", { id: "passkey-form", className: "d-none" });
  const labelWrap = el("div", { className: "mb-3" });
  const labelText = t("profile.passkey_label");
  const labelEl = el("label", { className: "visually-hidden", textContent: labelText });
  labelEl.setAttribute("for", "passkey-label");
  labelWrap.appendChild(labelEl);
  const labelInput = el("input", { id: "passkey-label", className: "form-control" });
  labelInput.type = "text";
  labelInput.name = "passkey-label";
  labelInput.required = true;
  labelInput.maxLength = 64;
  labelInput.placeholder = labelText;
  labelInput.setAttribute("autocomplete", "off");
  labelWrap.appendChild(labelInput);
  form.appendChild(labelWrap);
  form.appendChild(makePasswordField(t("profile.old_password"), "passkey-current-password", { autocomplete: "current-password" }));
  const addBtn = el("button", { className: "btn btn-success", textContent: " " + t("profile.passkey_register") });
  addBtn.type = "submit";
  const addIcon = el("i", { className: "fa fa-key" });
  addIcon.setAttribute("aria-hidden", "true");
  addBtn.insertBefore(addIcon, addBtn.firstChild);
  form.appendChild(el("div", { className: "text-center" })).appendChild(addBtn);
  body.appendChild(form);

  const showError = (message) => {
    successDiv.classList.add("d-none");
    errorDiv.textContent = message;
    errorDiv.classList.remove("d-none");
  };
  const showSuccess = (message) => {
    errorDiv.classList.add("d-none");
    successDiv.textContent = message;
    successDiv.classList.remove("d-none");
  };
  // The session ended (the server answers 401 with no reason): same as the password form.
  const handleSessionGone = async (err) => {
    if (!isSessionGone(err)) return false;
    await endSession();
    promptLogin();
    return true;
  };

  const renderList = (passkeys) => {
    while (list.firstChild) list.removeChild(list.firstChild);
    if (passkeys.length === 0) {
      list.appendChild(el("li", { className: "list-group-item text-muted", textContent: t("profile.passkey_none") }));
      return;
    }
    passkeys.forEach((passkey, index) => {
      const item = el("li", { className: "list-group-item d-flex justify-content-between align-items-center" });
      const text = el("div");
      const nameEl = el("div", { className: "fw-bold", id: "passkey-name-" + index, textContent: passkey.label || "" });
      text.appendChild(nameEl);
      const lastUsed = passkey.last_used_at ? formatDate(passkey.last_used_at) : t("profile.passkey_never");
      text.appendChild(el("small", {
        className: "text-muted",
        textContent: t("profile.passkey_created", [formatDate(passkey.created_at)]) + " / " + t("profile.passkey_last_used", [lastUsed])
      }));
      item.appendChild(text);
      const delBtn = el("button", { className: "btn btn-sm btn-outline-danger", textContent: t("profile.passkey_delete") });
      delBtn.type = "button";
      // "Delete <name>": with several passkeys a screen reader user must be told which one a button removes.
      delBtn.id = "passkey-delete-" + index;
      delBtn.setAttribute("aria-labelledby", delBtn.id + " " + nameEl.id);
      delBtn.addEventListener("click", () => deletePasskey(passkey, delBtn));
      item.appendChild(delBtn);
      list.appendChild(item);
    });
  };

  // canRegister: the session is a local user's and this browser can create passkeys.
  const loadList = async () => {
    try {
      const env = await api.get("/auth/passkey");
      const passkeys = Array.isArray(env.passkeys) ? env.passkeys : [];
      const canRegister = env.can_register === true && !!window.PublicKeyCredential && !!navigator.credentials;
      form.classList.toggle("d-none", !canRegister);
      renderList(passkeys);
      card.classList.toggle("d-none", passkeys.length === 0 && !canRegister);
    } catch (err) {
      if (await handleSessionGone(err)) return;
      showError(passkeyErrorMessage(err));
      card.classList.remove("d-none");
    }
  };

  async function deletePasskey(passkey, button) {
    if (!window.confirm(t("profile.passkey_delete_confirm", [passkey.label || ""]))) return;
    button.disabled = true;
    try {
      await api.del("/auth/passkey/" + encodeURIComponent(passkey.id));
      showSuccess(t("profile.passkey_deleted"));
    } catch (err) {
      if (await handleSessionGone(err)) return;
      // A passkey that is already gone (404) is the state the user wanted: show the list again.
      if (err && err.httpStatus !== 404) showError(passkeyErrorMessage(err));
    }
    await loadList();
  }

  form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    const label = labelInput.value.trim();
    const passwordInput = document.getElementById("passkey-current-password");
    if (!label) {
      // Spaces only pass "required": empty the field so that the browser tells, in its own words,
      // that the name is missing.
      labelInput.value = "";
      labelInput.reportValidity();
      labelInput.focus();
      return;
    }
    const currentPassword = passwordInput.value;
    addBtn.disabled = true;
    try {
      // The password is checked before any challenge is made; then the browser asks for the authenticator.
      const options = await api.post("/auth/passkey/register/options", { current_password: currentPassword });
      const credential = await navigator.credentials.create({ publicKey: toPasskeyCreationOptions(options) });
      await api.post("/auth/passkey/register/verify", { label, credential: passkeyCredentialToJson(credential) });
      form.reset();
      showSuccess(t("profile.passkey_registered"));
      await loadList();
    } catch (err) {
      // The user dismissed the browser's prompt: nothing to report.
      if (isPasskeyCancel(err)) return;
      if (await handleSessionGone(err)) return;
      // A server or network failure is told as it is; anything the browser threw (the
      // authenticator refused, a passkey of it is already registered) is a failed registration.
      const fromServer = err && (err.name === "ApiError" || err.name === "NetworkError");
      showError(fromServer ? passkeyErrorMessage(err) : t("profile.passkey_error_register"));
    } finally {
      // The password is asked for each registration and is not kept, whatever the outcome.
      passwordInput.value = "";
      addBtn.disabled = false;
    }
  });

  loadList();
  return card;
}

/**
 * Attach the profile view to the #profile-view container.
 * Clears previous content and rebuilds from scratch each time.
 * Safe to call on every navigation to /profile.
 */
export function attach() {
  const container = document.getElementById("profile-view");
  if (!container) return;

  // Clear previous content without innerHTML assignment.
  while (container.firstChild) container.removeChild(container.firstChild);

  // JSP parity (ProfileAction): the page is for logged-in users. Ask a guest to log in;
  // app.js runs this route again after the login.
  const user = getCurrentUser();
  if (!user) {
    const notice = el("div", { className: "alert alert-info mt-4", textContent: t("flash.login_required") });
    notice.setAttribute("role", "status");
    container.appendChild(notice);
    promptLogin();
    return;
  }
  // A password managed elsewhere (e.g. LDAP or SSO) cannot be changed here. The header
  // hides the link for such users, so send a direct visit home.
  if (user.editable === false) {
    router.navigate("./", { replace: true });
    return;
  }

  // ── Centered column layout ────────────────────────────────────────────────
  // #profile-view already has class="container my-4"; add top margin so the card
  // sits comfortably below the fixed header (no logo above it anymore).
  const row = el("div", { className: "row justify-content-center mt-4" });
  const col = el("div", { className: "col-12 col-sm-9 col-md-7 col-lg-5" });
  row.appendChild(col);
  container.appendChild(row);

  // ── Card ──────────────────────────────────────────────────────────────────
  const card = el("div", { className: "card" });
  const cardBody = el("div", { className: "card-body" });
  card.appendChild(cardBody);
  col.appendChild(card);
  if (isPasskeyEnabled()) col.appendChild(buildPasskeySection());

  // Card heading
  const heading = el("h1", { className: "h4 text-center mb-3", textContent: t("profile.title") });
  cardBody.appendChild(heading);

  // Password change form
  const form = el("form", { id: "password-form" });
  form.setAttribute("novalidate", "");
  cardBody.appendChild(form);

  // Current password
  form.appendChild(makePasswordField(
    t("profile.old_password"),
    "old-password",
    { autocomplete: "current-password" }
  ));

  // New password
  form.appendChild(makePasswordField(
    t("profile.new_password"),
    "new-password",
    { autocomplete: "new-password", minlength: 8 }
  ));

  // Confirm new password
  form.appendChild(makePasswordField(
    t("profile.confirm_password"),
    "confirm-password",
    { autocomplete: "new-password" }
  ));

  // Button row
  const btnRow = el("div", { className: "text-center" });

  // Back link (btn-secondary)
  const backLink = el("a", { className: "btn btn-secondary me-2" });
  backLink.href = "./";
  backLink.setAttribute("data-spa", "");
  const backIcon = el("i", { className: "fa fa-arrow-left" });
  backIcon.setAttribute("aria-hidden", "true");
  backLink.appendChild(backIcon);
  backLink.appendChild(document.createTextNode(" " + t("profile.back")));
  btnRow.appendChild(backLink);

  // Submit button (btn-success)
  const submitBtn = document.createElement("button");
  submitBtn.type = "submit";
  submitBtn.className = "btn btn-success";
  const updateIcon = el("i", { className: "fa fa-pencil-alt" });
  updateIcon.setAttribute("aria-hidden", "true");
  submitBtn.appendChild(updateIcon);
  submitBtn.appendChild(document.createTextNode(" " + t("profile.update")));
  btnRow.appendChild(submitBtn);

  form.appendChild(btnRow);

  // Error alert (hidden by default)
  const errorDiv = el("div", { id: "profile-error", className: "alert alert-danger d-none mt-3" });
  errorDiv.setAttribute("role", "alert");
  form.appendChild(errorDiv);

  // Success alert (hidden by default)
  const successDiv = el("div", { id: "profile-success", className: "alert alert-success d-none mt-3" });
  successDiv.setAttribute("role", "status");
  form.appendChild(successDiv);

  // Form submission handler
  form.addEventListener("submit", async (ev) => {
    ev.preventDefault();

    // Reset alerts
    errorDiv.textContent = "";
    errorDiv.classList.add("d-none");
    successDiv.textContent = "";
    successDiv.classList.add("d-none");

    const oldPw = document.getElementById("old-password").value;
    const newPw = document.getElementById("new-password").value;
    const confirmPw = document.getElementById("confirm-password").value;

    // Client-side mismatch check (server also validates, but early feedback is better UX)
    if (newPw !== confirmPw) {
      errorDiv.textContent = t("profile.error_mismatch");
      errorDiv.classList.remove("d-none");
      return;
    }

    submitBtn.disabled = true;
    try {
      const env = await api.post("/auth/password", {
        current_password: oldPw,
        new_password: newPw,
        confirm_password: confirmPw
      });

      successDiv.textContent = t("profile.success");
      successDiv.classList.remove("d-none");
      form.reset();
      // M-3: the server ended the session. Show the guest header, take a CSRF token for the
      // new session and ask for a login with the new password; the success message stays.
      if (env.re_login_required) {
        await endSession();
        promptLogin();
      }
    } catch (err) {
      errorDiv.textContent = localizePasswordError(err);
      errorDiv.classList.remove("d-none");
      if (isSessionGone(err)) {
        await endSession();
        promptLogin();
      }
    } finally {
      submitBtn.disabled = false;
    }
  });
}
