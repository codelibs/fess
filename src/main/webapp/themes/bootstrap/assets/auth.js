import * as api from "./api.js";
import { t } from "./i18n.js";

export async function probeMe() {
  try {
    const env = await api.get("/auth/me");
    setLoggedIn(env.user || {});
    return env.user;
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
  const displayName = user.name || user.username || user.user_id || "";

  // Wrapper
  const wrapper = document.createElement("div");
  wrapper.className = "dropdown";

  // Toggle button
  const toggle = document.createElement("button");
  toggle.className = "btn btn-outline-light btn-sm dropdown-toggle d-flex align-items-center gap-1";
  toggle.type = "button";
  toggle.setAttribute("data-bs-toggle", "dropdown");
  toggle.setAttribute("aria-expanded", "false");
  toggle.setAttribute("aria-haspopup", "true");

  const icon = document.createElement("i");
  icon.className = "fa fa-user";
  icon.setAttribute("aria-hidden", "true");
  toggle.appendChild(icon);

  const nameSpan = document.createElement("span");
  nameSpan.id = "user-name";
  nameSpan.textContent = displayName;
  toggle.appendChild(nameSpan);

  wrapper.appendChild(toggle);

  // Dropdown menu
  const menu = document.createElement("ul");
  menu.className = "dropdown-menu dropdown-menu-end";

  // Profile item — only when user.editable !== false
  if (user.editable !== false) {
    const profileLi = document.createElement("li");
    const profileA = document.createElement("a");
    profileA.className = "dropdown-item";
    profileA.href = "/profile";
    profileA.setAttribute("data-spa", "");
    profileA.setAttribute("data-i18n", "nav.profile");
    profileA.textContent = t("nav.profile");
    profileLi.appendChild(profileA);
    menu.appendChild(profileLi);
  }

  // Administration item — only when user.admin === true
  if (user.admin === true) {
    const adminLi = document.createElement("li");
    const adminA = document.createElement("a");
    adminA.className = "dropdown-item";
    adminA.href = "/admin/";
    adminA.setAttribute("data-i18n", "nav.administration");
    adminA.textContent = t("nav.administration");
    adminLi.appendChild(adminA);
    menu.appendChild(adminLi);
  }

  // Divider
  const dividerLi = document.createElement("li");
  const divider = document.createElement("hr");
  divider.className = "dropdown-divider";
  dividerLi.appendChild(divider);
  menu.appendChild(dividerLi);

  // Logout item
  const logoutLi = document.createElement("li");
  const logoutBtn = document.createElement("button");
  logoutBtn.className = "dropdown-item";
  logoutBtn.id = "logout-btn";
  logoutBtn.type = "button";
  logoutBtn.setAttribute("data-i18n", "auth.logout");
  logoutBtn.textContent = t("auth.logout");
  logoutLi.appendChild(logoutBtn);
  menu.appendChild(logoutLi);

  wrapper.appendChild(menu);
  return wrapper;
}

function setLoggedIn(user) {
  const controls = document.getElementById("auth-controls");
  if (!controls) return;

  // Remove previous dynamic content (chip, old logout/login buttons),
  // leave the static login button as a reference point.
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  const chip = document.getElementById("user-chip");
  const existingDropdown = document.getElementById("user-dropdown");

  if (chip) chip.classList.add("d-none");
  if (logoutBtn) logoutBtn.classList.add("d-none");
  if (loginBtn) loginBtn.classList.add("d-none");
  if (existingDropdown) existingDropdown.remove();

  // Build and insert the dropdown before the login button (or append).
  const dropdown = buildUserDropdown(user);
  dropdown.id = "user-dropdown";
  if (loginBtn && loginBtn.parentNode === controls) {
    controls.insertBefore(dropdown, loginBtn);
  } else {
    controls.appendChild(dropdown);
  }

  // Wire the logout button inside the freshly-built dropdown.
  const newLogoutBtn = dropdown.querySelector("#logout-btn");
  if (newLogoutBtn) {
    newLogoutBtn.addEventListener("click", async () => {
      let logoutEnv = null;
      try { logoutEnv = await api.post("/auth/logout", {}); } catch { /* server may have already invalidated */ }
      await rotateCsrf(logoutEnv);
      setLoggedOut();
      document.dispatchEvent(new CustomEvent("fess:auth:logout"));
    });
  }
}

function setLoggedOut() {
  const chip = document.getElementById("user-chip");
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  const existingDropdown = document.getElementById("user-dropdown");

  if (chip) { chip.textContent = ""; chip.classList.add("d-none"); }
  if (logoutBtn) logoutBtn.classList.add("d-none");
  if (loginBtn) loginBtn.classList.remove("d-none");
  if (existingDropdown) existingDropdown.remove();
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
