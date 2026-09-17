import * as api from "./api.js";
import { sanitizeHtml } from "./format.js";
import { t } from "./i18n.js";
import * as router from "./router.js";

// What the last /auth/me probe, login or logout established: "in", "out", or "unknown"
// (before the probe, or when the probe failed on the network or the server).
let authState = "unknown";
// The logged-in user from /auth/me or /auth/login; null for a guest or an unknown state.
let currentUser = null;
// True while the login modal must stay open: login is required and nobody is logged in.
let loginLocked = false;
// Username and password of a login whose password must be changed
// (password_change_required). Held in memory only, until the change ends or the modal closes.
let pendingPasswordChange = null;

/**
 * Returns true when the login link should be shown.
 * The `login_link` feature flag (from /api/v2/ui/config) can be false to
 * hide login UI entirely. Defaults to true when missing.
 */
export function isLoginLinkEnabled() {
  const features = api.getConfig()?.features || {};
  return features.login_link !== false;
}

/** The logged-in user, or null for a guest or while the login state is unknown. */
export function getCurrentUser() {
  return currentUser;
}

/** True when Fess requires login for the search pages (login.required). */
export function isLoginRequired() {
  return api.getConfig()?.login_required === true;
}

/**
 * True when the search pages must wait for a login: login is required and the auth probe
 * confirmed a guest. An unknown state (the probe failed on the network or the server)
 * keeps the gate open, so a broken server cannot bounce the user through login in a loop.
 */
export function isLoginGateClosed() {
  return isLoginRequired() && authState === "out";
}

export async function probeMe() {
  try {
    const env = await api.get("/auth/me");
    // /auth/me always returns HTTP 200 with { authenticated, user? }. Only show the
    // user dropdown when actually authenticated; otherwise show the guest login link
    // (header.jsp parity: username!='guest' → dropdown, else → login link).
    if (env && env.authenticated && env.user) {
      setLoggedIn(env.user);
      return env.user;
    }
    setLoggedOut();
    return null;
  } catch (e) {
    // 401 / auth_required → not logged in; expected and silent.
    // V2ErrorCode emits lowercase snake_case wire codes ("auth_required"); the
    // uppercase spelling is kept alongside it so the branch survives either form.
    if (e.code === "auth_required" || e.code === "AUTH_REQUIRED" || e.httpStatus === 401) {
      setLoggedOut();
      return null;
    }
    // Network-class errors (fetch rejection → NetworkError, or HTTP 5xx/0) →
    // surface a diagnostic without treating the user as logged out.
    const isNetworkClass =
      e.name === "NetworkError" ||
      (e.httpStatus != null && (e.httpStatus === 0 || e.httpStatus >= 500));
    if (isNetworkClass) {
      console.warn("[fess] probeMe: network/server error", e);
      document.dispatchEvent(new CustomEvent("fess:auth:network-error", { detail: { error: e } }));
      const meta = document.getElementById("results-meta");
      if (meta) meta.textContent = t("error.network");
      // Do not call setLoggedOut() — auth state is unknown, not confirmed gone.
      authState = "unknown";
      return null;
    }
    // All other errors: log and treat as logged out (preserve safety net).
    console.warn("[fess] probeMe: unexpected error", e);
    setLoggedOut();
    return null;
  }
}

/**
 * Build and return a Bootstrap dropdown node for a logged-in user.
 * All DOM construction uses createElement (no innerHTML).
 *
 * @param {object} user - user payload from /api/v2/auth/me
 * @returns {HTMLElement} the dropdown wrapper div
 */
export function buildUserDropdown(user) {
  // Tag-parity with header.jsp logged-in block:
  //   div.dropdown
  //     > a#userMenu.nav-link.dropdown-toggle > i.fa.fa-fw.fa-user + span(name)
  //     > div.dropdown-menu[aria-labelledby=userMenu]
  //         > a.dropdown-item (profile)?  + a.dropdown-item (admin)?  + a.dropdown-item (logout)
  const displayName = user.name || user.username || user.user_id || "";

  // Wrapper
  const wrapper = document.createElement("div");
  wrapper.className = "dropdown";

  // Toggle is an <a id="userMenu"> (JSP parity), not a button.
  const toggle = document.createElement("a");
  toggle.id = "userMenu";
  toggle.className = "nav-link dropdown-toggle";
  toggle.href = "#";
  toggle.setAttribute("role", "button");
  toggle.setAttribute("data-bs-toggle", "dropdown");
  toggle.setAttribute("aria-haspopup", "true");
  toggle.setAttribute("aria-expanded", "false");

  const icon = document.createElement("i");
  icon.className = "fa fa-fw fa-user";
  icon.setAttribute("aria-hidden", "true");
  toggle.appendChild(icon);
  toggle.appendChild(document.createTextNode(" "));

  const nameSpan = document.createElement("span");
  nameSpan.id = "user-name";
  nameSpan.textContent = displayName;
  toggle.appendChild(nameSpan);

  wrapper.appendChild(toggle);

  // Dropdown menu — direct a.dropdown-item children (no ul/li), JSP parity.
  const menu = document.createElement("div");
  menu.className = "dropdown-menu";
  menu.setAttribute("aria-labelledby", "userMenu");

  // Profile item — only when user.editable !== false
  if (user.editable !== false) {
    const profileA = document.createElement("a");
    profileA.className = "dropdown-item";
    profileA.href = "profile";
    profileA.setAttribute("data-spa", "");
    profileA.setAttribute("data-i18n", "nav.profile");
    profileA.textContent = t("nav.profile");
    menu.appendChild(profileA);
  }

  // Administration item — only when user.admin === true
  if (user.admin === true) {
    const adminA = document.createElement("a");
    adminA.className = "dropdown-item";
    adminA.href = "admin/";
    adminA.setAttribute("data-i18n", "nav.administration");
    adminA.textContent = t("nav.administration");
    menu.appendChild(adminA);
  }

  // Logout item — an a.dropdown-item (JSP uses la:link /logout/). Keeps id
  // #logout-btn so the existing click handler in setLoggedIn() wires to it.
  const logoutBtn = document.createElement("a");
  logoutBtn.className = "dropdown-item";
  logoutBtn.id = "logout-btn";
  logoutBtn.href = "#";
  logoutBtn.setAttribute("role", "button");
  logoutBtn.setAttribute("data-i18n", "auth.logout");
  logoutBtn.textContent = t("auth.logout");
  menu.appendChild(logoutBtn);

  wrapper.appendChild(menu);
  return wrapper;
}

/**
 * Build the guest login control inside #auth-controls (the li.nav-item).
 * header.jsp parity: a.nav-link[/login] > i.fa.fa-fw.fa-sign-in + span(Login).
 * Default action opens the SPA login modal; with SSO it is a direct link.
 */
export function buildLoginLink() {
  const cfg = api.getConfig() || {};
  const features = cfg.features || {};
  const a = document.createElement("a");
  a.className = "nav-link";
  a.id = "login-btn";
  a.setAttribute("role", "button");
  if (features.sso_enabled && features.login_link) {
    a.href = features.login_link;
  } else {
    a.href = "login";
    a.setAttribute("data-bs-toggle", "modal");
    a.setAttribute("data-bs-target", "#login-modal");
  }
  const icon = document.createElement("i");
  icon.className = "fa fa-fw fa-sign-in";
  icon.setAttribute("aria-hidden", "true");
  a.appendChild(icon);
  a.appendChild(document.createTextNode(" "));
  const span = document.createElement("span");
  span.setAttribute("data-i18n", "auth.login");
  span.textContent = t("auth.login");
  a.appendChild(span);
  return a;
}

function setLoggedIn(user) {
  api.setAuthenticated(true);
  authState = "in";
  currentUser = user;
  const controls = document.getElementById("auth-controls");
  if (!controls) return;

  // Remove the dynamic login link and any prior dropdown.
  const loginBtn = document.getElementById("login-btn");
  if (loginBtn) loginBtn.remove();
  const existingDropdown = document.getElementById("user-dropdown");
  if (existingDropdown) existingDropdown.remove();

  // Build and insert the user dropdown into the li.nav-item host.
  const dropdown = buildUserDropdown(user);
  dropdown.id = "user-dropdown";
  controls.appendChild(dropdown);

  // Wire the logout link inside the freshly-built dropdown.
  const newLogoutBtn = dropdown.querySelector("#logout-btn");
  if (newLogoutBtn) {
    newLogoutBtn.addEventListener("click", async (ev) => {
      ev.preventDefault();
      let logoutEnv = null;
      try { logoutEnv = await api.post("/auth/logout", {}); } catch { /* server may have already invalidated */ }
      await rotateCsrf(logoutEnv);
      setLoggedOut();
      // JSP parity (LogoutAction): with SSO single logout, continue to the identity
      // provider's logout URL. Only http(s) URLs are followed.
      const redirectUrl = ssoLogoutUrl(logoutEnv);
      document.dispatchEvent(new CustomEvent("fess:auth:logout", { detail: { redirecting: redirectUrl !== null } }));
      if (redirectUrl) router.redirect(redirectUrl);
    });
  }
}

function setLoggedOut() {
  api.setAuthenticated(false);
  authState = "out";
  currentUser = null;
  const controls = document.getElementById("auth-controls");
  const existingDropdown = document.getElementById("user-dropdown");
  if (existingDropdown) existingDropdown.remove();
  const existingLogin = document.getElementById("login-btn");
  if (existingLogin) existingLogin.remove();
  if (!controls) return;
  // Only show the login link when the login_link feature is enabled (D.3).
  if (isLoginLinkEnabled()) {
    controls.appendChild(buildLoginLink());
  }
}

/**
 * End the client side of a session the server already ended (a password change, an
 * expired session): show the guest header and take a CSRF token for the new session.
 */
export async function endSession() {
  setLoggedOut();
  await rotateCsrf();
}

/**
 * Ask the user to log in. When SSO is served, go to the SSO login (JSP parity:
 * FessSearchAction.redirectToLogin sends the user to /sso/). Otherwise open the login
 * modal; while login is required it cannot be closed.
 */
export function promptLogin() {
  if (api.getConfig()?.features?.sso_enabled) {
    router.redirect("sso/");
    return;
  }
  const modal = document.getElementById("login-modal");
  if (!modal) return;
  setLoginLocked(isLoginRequired());
  if (!window.bootstrap || !bootstrap.Modal) {
    console.warn("[fess] bootstrap not loaded; skipping modal show");
    return;
  }
  bootstrap.Modal.getOrCreateInstance(modal).show();
}

/** The identity provider's logout URL to follow after logout, or null unless it is an absolute http(s) URL. */
function ssoLogoutUrl(logoutEnv) {
  const value = logoutEnv && logoutEnv.redirect_url;
  if (typeof value !== "string") return null;
  try {
    const url = new URL(value);
    return url.protocol === "http:" || url.protocol === "https:" ? url.href : null;
  } catch {
    return null;
  }
}

/**
 * Lock the login modal (its close controls are hidden and attach() refuses hide.bs.modal)
 * or unlock it.
 */
function setLoginLocked(locked) {
  loginLocked = locked;
  document.querySelectorAll('#login-modal [data-bs-dismiss="modal"]').forEach(el => el.classList.toggle("d-none", locked));
}

function hideLoginModal() {
  if (!window.bootstrap || !bootstrap.Modal) {
    console.warn("[fess] bootstrap not loaded; skipping modal hide");
  } else {
    bootstrap.Modal.getOrCreateInstance(document.getElementById("login-modal")).hide();
  }
}

/** Show the login form ("login") or the new-password form ("password") in #login-modal. */
function showModalStep(step) {
  const loginForm = document.getElementById("login-form");
  const changeForm = document.getElementById("password-change-form");
  if (loginForm) loginForm.classList.toggle("d-none", step !== "login");
  if (changeForm) changeForm.classList.toggle("d-none", step !== "password");
}

/** The localized message for a failed POST /auth/login. */
function loginErrorMessage(e) {
  // V2ErrorCode emits lowercase snake_case wire codes ("rate_limited").
  if (e.code === "rate_limited" || e.code === "RATE_LIMITED" || e.httpStatus === 429) return t("auth.error_rate_limited");
  return t("auth.error_invalid_credentials");
}

/**
 * Attempt to obtain a fresh CSRF token after a session-boundary event (e.g. logout).
 *
 * Strategy:
 *   1. If the server embeds csrf_token in the logout response body (future backend
 *      enhancement), use that value directly — zero extra round-trip.
 *   2. Fall back to GET /ui/config to rotate the token, which is what older server
 *      versions return.
 *
 * @param {object|null} logoutEnv - the parsed response envelope from /auth/logout,
 *   or null when not available. May carry a csrf_token field if the server is new.
 */
export async function rotateCsrf(logoutEnv) {
  // Prefer a token returned directly in the logout response body (no extra round-trip).
  if (logoutEnv && logoutEnv.csrf_token) {
    api.setCsrfToken(logoutEnv.csrf_token);
    return;
  }
  // Fallback: re-fetch /ui/config to get a fresh token.
  try {
    const env = await api.get("/ui/config");
    api.setCsrfToken(env.csrf_token || "");
  } catch { /* leave previous token; subsequent calls will fail loudly */ }
}

/**
 * Map a password-change error from POST /api/v2/auth/password to a localized,
 * user-facing message. Per the V2 i18n contract the server's `error.message` is
 * developer-facing English; the client localizes using the stable `error.code`
 * and the structured `error.details.reason` token (mirrors auth.js login errors).
 *
 * @param {object} err - ApiError (code/httpStatus/details) or NetworkError
 * @returns {string} a localized message safe to render via textContent
 */
export function localizePasswordError(err) {
  if (err && err.name === "NetworkError") return t("error.network");
  const code = err && err.code;
  const httpStatus = err && err.httpStatus;
  // V2ErrorCode emits lowercase snake_case wire codes ("rate_limited"); the
  // uppercase spelling is kept alongside it so the branch survives either form.
  if (code === "rate_limited" || code === "RATE_LIMITED" || httpStatus === 429) return t("auth.error_rate_limited");
  const details = (err && err.details) || {};
  const reason = details.reason;
  if (reason === "invalid_current_password") return t("profile.error_wrong_current");
  switch (reason) {
    case "errors.password_length":
      return t("profile.error_password_length", [details.min_length]);
    case "errors.password_no_uppercase":
      return t("profile.error_password_no_uppercase");
    case "errors.password_no_lowercase":
      return t("profile.error_password_no_lowercase");
    case "errors.password_no_digit":
      return t("profile.error_password_no_digit");
    case "errors.password_no_special_char":
      return t("profile.error_password_no_special_char");
    case "errors.password_is_blacklisted":
      return t("profile.error_password_blacklisted");
    case "errors.blank_password":
    case "new_password_required":
    case "current_password_required":
      return t("profile.error_blank_password");
    case "password_mismatch":
      return t("profile.error_mismatch");
    default:
      break;
  }
  // Fallbacks by HTTP/code when no specific reason is present. auth_required without a
  // reason means the session is gone (a wrong current password carries a reason).
  if (code === "auth_required" || code === "AUTH_REQUIRED" || httpStatus === 401) return t("flash.login_required");
  return t("error.server");
}

/**
 * True when POST /auth/password failed because the session is gone: auth_required (401)
 * without a reason. A wrong current password is auth_required too, but carries the reason
 * invalid_current_password.
 */
export function isSessionGone(err) {
  if (!err || (err.details && err.details.reason)) return false;
  return err.code === "auth_required" || err.code === "AUTH_REQUIRED" || err.httpStatus === 401;
}

export function attach() {
  // D.3: If login_link feature is disabled, immediately hide the login button
  // so it never flashes visible before probeMe() settles.
  if (!isLoginLinkEnabled()) {
    const loginBtn = document.getElementById("login-btn");
    if (loginBtn) loginBtn.classList.add("d-none");
  }

  // Login notification banner (parity login/index.jsp:25). The configured message
  // (config.notifications.login, set via admin General settings) is static, so fill
  // the slot once on attach; the modal is opened declaratively by Bootstrap.
  const notification = document.getElementById("login-notification");
  if (notification) {
    const html = (api.getConfig()?.notifications || {}).login || "";
    while (notification.firstChild) notification.removeChild(notification.firstChild);
    if (typeof html === "string" && html.trim() !== "") {
      notification.classList.remove("d-none");
      notification.appendChild(sanitizeHtml(html));
    } else {
      notification.classList.add("d-none");
    }
  }

  const modal = document.getElementById("login-modal");
  const form = document.getElementById("login-form");
  const err = document.getElementById("login-error");
  if (modal) {
    // A locked modal stays open. Bootstrap's hide() gives up when hide.bs.modal is
    // prevented, which covers the backdrop, Escape and the close controls.
    modal.addEventListener("hide.bs.modal", ev => {
      if (loginLocked) ev.preventDefault();
    });
  }
  const changeForm = document.getElementById("password-change-form");
  const changeErr = document.getElementById("password-change-error");
  if (modal && form) {
    modal.addEventListener("hidden.bs.modal", () => {
      form.reset();
      if (err) {
        err.classList.add("d-none");
        err.textContent = "";
      }
      // Closing the modal abandons a pending password change and forgets the password.
      pendingPasswordChange = null;
      if (changeForm) changeForm.reset();
      if (changeErr) {
        changeErr.classList.add("d-none");
        changeErr.textContent = "";
      }
      showModalStep("login");
    });
  }
  if (form) {
    form.addEventListener("submit", async ev => {
      ev.preventDefault();
      err.classList.add("d-none");
      err.textContent = "";
      const username = document.getElementById("login-username").value;
      const password = document.getElementById("login-password").value;
      try {
        const env = await api.post("/auth/login", { username, password });
        // Server rotates the token on login. Echo whichever it returned.
        if (env.csrf_token) api.setCsrfToken(env.csrf_token);
        else await rotateCsrf();
        setLoggedIn(env.user || { username });
        setLoginLocked(false);
        // JSP parity (LoginAction): a password listed in password.invalid.admin.passwords has
        // to be changed, so switch the modal to the new-password form. As on the JSP page the
        // user may leave it; the login itself already succeeded.
        if (env.password_change_required === true && env.user && env.user.editable) {
          pendingPasswordChange = { username, password };
          showModalStep("password");
        } else {
          hideLoginModal();
        }
        document.dispatchEvent(new CustomEvent("fess:auth:login", { detail: env.user }));
      } catch (e) {
        err.textContent = loginErrorMessage(e);
        err.classList.remove("d-none");
      }
    });
  }
  if (changeForm) {
    const changeSubmit = changeForm.querySelector('button[type="submit"]');
    changeForm.addEventListener("submit", async ev => {
      ev.preventDefault();
      if (!pendingPasswordChange) return;
      // The change and the login after it are still in flight: do not send them again.
      if (changeSubmit && changeSubmit.disabled) return;
      changeErr.classList.add("d-none");
      changeErr.textContent = "";
      const newPassword = document.getElementById("password-change-new").value;
      const confirmPassword = document.getElementById("password-change-confirm").value;
      if (newPassword !== confirmPassword) {
        changeErr.textContent = t("profile.error_mismatch");
        changeErr.classList.remove("d-none");
        return;
      }
      const { username, password } = pendingPasswordChange;
      if (changeSubmit) changeSubmit.disabled = true;
      try {
        let env;
        try {
          env = await api.post("/auth/password", {
            current_password: password,
            new_password: newPassword,
            confirm_password: confirmPassword
          });
        } catch (e) {
          changeErr.textContent = localizePasswordError(e);
          changeErr.classList.remove("d-none");
          return;
        }
        pendingPasswordChange = null;
        if (!env.re_login_required) {
          hideLoginModal();
          return;
        }
        // The server ended the session (M-3): take a token for the new session and log in
        // again with the new password.
        await rotateCsrf();
        try {
          const relogin = await api.post("/auth/login", { username, password: newPassword });
          if (relogin.csrf_token) api.setCsrfToken(relogin.csrf_token);
          else await rotateCsrf();
          setLoggedIn(relogin.user || { username });
          hideLoginModal();
          document.dispatchEvent(new CustomEvent("fess:auth:login", { detail: relogin.user }));
        } catch (e) {
          setLoggedOut();
          // A guest again: while login is required the modal must stay open.
          setLoginLocked(isLoginRequired());
          showModalStep("login");
          document.getElementById("login-username").value = username;
          // Neither the old nor the new password stays in the forms.
          document.getElementById("login-password").value = "";
          changeForm.reset();
          err.textContent = loginErrorMessage(e);
          err.classList.remove("d-none");
          document.dispatchEvent(new CustomEvent("fess:auth:logout"));
        }
      } finally {
        if (changeSubmit) changeSubmit.disabled = false;
      }
    });
  }
  return probeMe();
}
