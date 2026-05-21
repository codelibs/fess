import * as api from "./api.js";
import { t } from "./i18n.js";

export async function probeMe() {
  try {
    const env = await api.get("/auth/me");
    setLoggedIn(env.user || {});
    return env.user;
  } catch (e) {
    if (e.code === "AUTH_REQUIRED" || e.httpStatus === 401) {
      setLoggedOut();
      return null;
    }
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

async function rotateCsrf() {
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
        bootstrap.Modal.getOrCreateInstance(document.getElementById("login-modal")).hide();
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
      try { await api.post("/auth/logout", {}); } catch { /* server may have already invalidated */ }
      await rotateCsrf();
      setLoggedOut();
      document.dispatchEvent(new CustomEvent("fess:auth:logout"));
    });
  }
  return probeMe();
}
