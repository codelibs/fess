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
    this.name = "ApiError";
  }
}

/**
 * Thrown when the network is unreachable or the fetch itself rejects
 * (e.g. DNS failure, offline). Consumers can check `e instanceof NetworkError`
 * to surface t("error.network") instead of t("error.server").
 */
export class NetworkError extends Error {
  constructor(cause) {
    super(cause && cause.message ? cause.message : "Network error");
    this.name = "NetworkError";
    this.cause = cause;
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

/**
 * GET /api/v2{path}
 * @param {string} path
 * @param {object} [params] - query parameters
 * @param {object} [options] - additional fetch options (e.g. { signal: AbortSignal })
 */
export async function get(path, params, options) {
  const fetchOpts = {
    method: "GET",
    credentials: "same-origin",
    headers: { "Accept": "application/json" }
  };
  if (options && options.signal) fetchOpts.signal = options.signal;
  let resp;
  try {
    resp = await fetch(BASE + path + qs(params), fetchOpts);
  } catch (e) {
    throw new NetworkError(e);
  }
  return unwrap(resp);
}

export async function post(path, body) {
  let resp;
  try {
    resp = await fetch(BASE + path, {
      method: "POST",
      credentials: "same-origin",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "X-Fess-CSRF-Token": csrfToken || ""
      },
      body: JSON.stringify(body || {})
    });
  } catch (e) {
    throw new NetworkError(e);
  }
  return unwrap(resp);
}

/**
 * @deprecated — use sseStream for POST-based streaming (EventSource cannot send custom headers).
 * Kept for backward compatibility; will be removed once all callers migrate to sseStream.
 */
export function sse(path, params, onMessage, onError) {
  const url = BASE + path + qs(params);
  const es = new EventSource(url, { withCredentials: true });
  es.onmessage = ev => onMessage(ev.data);
  es.onerror = err => { if (onError) onError(err); es.close(); };
  return es;
}

/**
 * Fetch-based SSE streaming over POST with CSRF.
 *
 * Required because EventSource only supports GET; the /chat/stream endpoint
 * is POST-only and requires the X-Fess-CSRF-Token header.
 *
 * The server must emit SSE-formatted text (lines of "event: ...\ndata: ...\n\n").
 * Each complete SSE frame (separated by a blank line) is parsed and dispatched
 * to onEvent as { type, data } where data is the JSON-parsed data line (or raw
 * string if not valid JSON).
 *
 * @param {string} path - path relative to /api/v2 (e.g. "/chat/stream")
 * @param {object} body - request body, JSON-serialised
 * @param {function} onEvent - called with { type: string, data: any } per SSE frame
 * @param {function} onError - called with ApiError or NetworkError on failure
 * @returns {AbortController} caller may call .abort() to cancel the stream
 */
export function sseStream(path, body, onEvent, onError) {
  const controller = new AbortController();
  const { signal } = controller;

  (async () => {
    let resp;
    try {
      resp = await fetch(BASE + path, {
        method: "POST",
        credentials: "same-origin",
        headers: {
          "Content-Type": "application/json",
          "X-Fess-CSRF-Token": csrfToken || ""
        },
        body: JSON.stringify(body || {}),
        signal
      });
    } catch (e) {
      if (signal.aborted) return; // caller aborted — not an error
      if (onError) onError(new NetworkError(e));
      return;
    }

    if (!resp.ok) {
      // Try to parse a JSON error envelope; fall back to HTTP status.
      let code = "HTTP_ERROR";
      let message = "HTTP " + resp.status;
      try {
        const errBody = await resp.json();
        const env = errBody && errBody.response;
        if (env && env.error) {
          code = env.error.code || code;
          message = env.error.message || message;
        }
      } catch { /* ignore parse failure */ }
      if (onError) onError(new ApiError(code, message, resp.status));
      return;
    }

    const reader = resp.body.getReader();
    const decoder = new TextDecoder("utf-8");
    let buffer = "";

    try {
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        // SSE frames are separated by blank lines (\n\n or \r\n\r\n).
        let sepIdx;
        while ((sepIdx = findFrameSep(buffer)) !== -1) {
          const frame = buffer.slice(0, sepIdx);
          buffer = buffer.slice(sepIdx + frameStepLen(buffer, sepIdx));
          if (frame.trim() === "") continue; // skip blank/comment-only frames
          dispatchFrame(frame, onEvent);
        }
      }
      // Flush any incomplete multi-byte sequence held by the TextDecoder after
      // stream:true mode.  Calling decode() with no arguments (stream:false)
      // releases any bytes buffered internally and appends them to buffer.
      buffer += decoder.decode();
      // Flush any remaining partial frame (no trailing blank line).
      if (buffer.trim() !== "") dispatchFrame(buffer, onEvent);
    } catch (e) {
      if (signal.aborted) return;
      if (onError) onError(new NetworkError(e));
    }
  })();

  return controller;
}

/** Find the byte-index of the first SSE frame separator (\n\n or \r\n\r\n). */
function findFrameSep(buf) {
  const idx2 = buf.indexOf("\n\n");
  const idx4 = buf.indexOf("\r\n\r\n");
  if (idx2 === -1 && idx4 === -1) return -1;
  if (idx2 === -1) return idx4;
  if (idx4 === -1) return idx2;
  return Math.min(idx2, idx4);
}

/** How many characters to skip over the separator itself. */
function frameStepLen(buf, sepIdx) {
  return buf.startsWith("\r\n\r\n", sepIdx) ? 4 : 2;
}

/** Parse a single SSE frame and call onEvent. */
function dispatchFrame(frame, onEvent) {
  let eventType = "message";
  let dataLine = "";
  for (const rawLine of frame.split(/\r?\n/)) {
    if (rawLine.startsWith("event:")) {
      eventType = rawLine.slice(6).trim();
    } else if (rawLine.startsWith("data:")) {
      // Multiple data lines within one frame are concatenated per SSE spec.
      dataLine += (dataLine ? "\n" : "") + rawLine.slice(5).trim();
    }
    // id: and retry: fields are ignored for now.
  }
  let parsedData;
  try {
    parsedData = JSON.parse(dataLine);
  } catch {
    parsedData = dataLine; // raw string fallback
  }
  if (onEvent) onEvent({ type: eventType, data: parsedData });
}

export async function init() {
  const env = await get("/ui/config");
  config = env;            // entire envelope, callers read fields directly
  csrfToken = env.csrf_token || "";
}

export function getConfig() { return config; }
export function getCsrfToken() { return csrfToken; }
export function setCsrfToken(t) { csrfToken = t || ""; }
