// SPDX-License-Identifier: Apache-2.0
// Profile / password-change view for the Fess bootstrap SPA.
// All DOM construction uses createElement/textContent/setAttribute — no innerHTML.

import * as api from "./api.js";
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
  backLink.href = "/";
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

      // M-3: session has been invalidated server-side. The SPA must re-authenticate
      // to obtain a fresh session and CSRF token.
      if (env.re_login_required) {
        successDiv.textContent = t("profile.success");
        successDiv.classList.remove("d-none");
        form.reset();
        // Redirect to login after a brief pause so the user sees the success message.
        setTimeout(() => router.navigate("/"), 2000);
      } else {
        successDiv.textContent = t("profile.success");
        successDiv.classList.remove("d-none");
        form.reset();
      }
    } catch (err) {
      // err.message is a developer-facing English string from the server — safe to
      // display as plain text via textContent (never innerHTML).
      errorDiv.textContent = err.message || t("error.server");
      errorDiv.classList.remove("d-none");
    } finally {
      submitBtn.disabled = false;
    }
  });
}
