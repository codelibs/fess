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

function setLoggedIn(user) {
  const chip = document.getElementById("user-chip");
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  if (chip) {
    chip.textContent = user.name || user.username || "";
    chip.classList.remove("d-none");
  }
  if (loginBtn) loginBtn.classList.add("d-none");
  if (logoutBtn) logoutBtn.classList.remove("d-none");
}

function setLoggedOut() {
  const chip = document.getElementById("user-chip");
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  if (chip) { chip.textContent = ""; chip.classList.add("d-none"); }
  if (loginBtn) loginBtn.classList.remove("d-none");
  if (logoutBtn) logoutBtn.classList.add("d-none");
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
  const logoutBtn = document.getElementById("logout-btn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      let logoutEnv = null;
      try { logoutEnv = await api.post("/auth/logout", {}); } catch { /* server may have already invalidated */ }
      // Pass logoutEnv so rotateCsrf can use an embedded csrf_token if present.
      await rotateCsrf(logoutEnv);
      setLoggedOut();
      document.dispatchEvent(new CustomEvent("fess:auth:logout"));
    });
  }
  return probeMe();
}
