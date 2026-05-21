// Centralised /api/v2 client for the bootstrap theme. Owns the CSRF token
// (header name X-Fess-CSRF-Token; verified in SearchApiV2Manager.java:172),
// envelope unwrapping ({ response: { status, ...} }), and locale propagation.

const BASE = "/api/v2";
let config = null;
let csrfToken = "";

export class ApiError extends Error {
  constructor(code, message, httpStatus) {
    super(message);
    this.code = code;
    this.httpStatus = httpStatus;
  }
}

async function unwrap(resp) {
  let body;
  try { body = await resp.json(); } catch { throw new ApiError("NETWORK", "Invalid JSON", resp.status); }
  const env = body && body.response;
  if (!env) throw new ApiError("PROTOCOL", "Missing response envelope", resp.status);
  if (env.status !== 0) {
    const err = env.error || {};
    throw new ApiError(err.code || "UNKNOWN", err.message || "Unknown error", resp.status);
  }
  return env;
}

function qs(params) {
  if (!params) return "";
  const u = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v == null) continue;
    if (Array.isArray(v)) v.forEach(x => u.append(k, String(x)));
    else u.append(k, String(v));
  }
  const s = u.toString();
  return s ? "?" + s : "";
}

export async function get(path, params) {
  const resp = await fetch(BASE + path + qs(params), {
    method: "GET",
    credentials: "same-origin",
    headers: { "Accept": "application/json" }
  });
  return unwrap(resp);
}

export async function post(path, body) {
  const resp = await fetch(BASE + path, {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json",
      "X-Fess-CSRF-Token": csrfToken || ""
    },
    body: JSON.stringify(body || {})
  });
  return unwrap(resp);
}

export function sse(path, params, onMessage, onError) {
  const url = BASE + path + qs(params);
  const es = new EventSource(url, { withCredentials: true });
  es.onmessage = ev => onMessage(ev.data);
  es.onerror = err => { if (onError) onError(err); es.close(); };
  return es;
}

export async function init() {
  const env = await get("/ui/config");
  config = env;            // entire envelope, callers read fields directly
  csrfToken = env.csrf_token || "";
}

export function getConfig() { return config; }
export function getCsrfToken() { return csrfToken; }
export function setCsrfToken(t) { csrfToken = t || ""; }
