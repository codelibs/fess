import * as api from "./api.js";
import { sanitizeHtml } from "./format.js";
import { t } from "./i18n.js";

/**
 * Returns true when the login link should be shown.
 * The `login_link` feature flag (from /api/v2/ui/config) can be false to
 * hide login UI entirely. Defaults to true when missing.
 */
function isLoginLinkEnabled() {
  const features = api.getConfig()?.features || {};
  return features.login_link !== false;
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
    // 401 / AUTH_REQUIRED → not logged in; expected and silent.
    if (e.code === "AUTH_REQUIRED" || e.httpStatus === 401) {
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
function buildUserDropdown(user) {
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
    profileA.href = "/profile";
    profileA.setAttribute("data-spa", "");
    profileA.setAttribute("data-i18n", "nav.profile");
    profileA.textContent = t("nav.profile");
    menu.appendChild(profileA);
  }

  // Administration item — only when user.admin === true
  if (user.admin === true) {
    const adminA = document.createElement("a");
    adminA.className = "dropdown-item";
    adminA.href = "/admin/";
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
function buildLoginLink() {
  const cfg = api.getConfig() || {};
  const features = cfg.features || {};
  const a = document.createElement("a");
  a.className = "nav-link";
  a.id = "login-btn";
  a.setAttribute("role", "button");
  if (features.sso_enabled && features.login_link) {
    a.href = features.login_link;
  } else {
    a.href = "/login";
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
      document.dispatchEvent(new CustomEvent("fess:auth:logout"));
    });
  }
}

function setLoggedOut() {
  api.setAuthenticated(false);
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
async function rotateCsrf(logoutEnv) {
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

  const form = document.getElementById("login-form");
  const err = document.getElementById("login-error");
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
        if (!window.bootstrap || !bootstrap.Modal) {
          console.warn("[fess] bootstrap not loaded; skipping modal hide");
        } else {
          bootstrap.Modal.getOrCreateInstance(document.getElementById("login-modal")).hide();
        }
        document.dispatchEvent(new CustomEvent("fess:auth:login", { detail: env.user }));
      } catch (e) {
        if (e.code === "RATE_LIMITED" || e.httpStatus === 429) err.textContent = t("auth.error_rate_limited");
        else err.textContent = t("auth.error_invalid_credentials");
        err.classList.remove("d-none");
      }
    });
  }
  return probeMe();
}
