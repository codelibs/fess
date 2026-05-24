import * as api from "./api.js";
import { t } from "./i18n.js";

let mounted = false;

/** AbortController for the active stream; null when idle. */
let currentStream = null;

function el(tag, opts) {
  const node = document.createElement(tag);
  if (!opts) return node;
  if (opts.className) node.className = opts.className;
  if (opts.text != null) node.textContent = opts.text;
  if (opts.attrs) for (const [k, v] of Object.entries(opts.attrs)) node.setAttribute(k, String(v));
  return node;
}

function appendMsg(log, who, text) {
  const div = el("div", { className: "chat-msg " + who, text });
  log.appendChild(div);
  log.scrollTop = log.scrollHeight;
  return div;
}

function buildPanel(column) {
  // Build chat panel via DOM construction — no dynamic data in markup strings.
  column.innerHTML = "";
  const card = el("div", { className: "card shadow-sm" });
  const header = el("div", { className: "card-header" });
  header.appendChild(el("strong", { text: t("chat.send") }));
  card.appendChild(header);
  const body = el("div", { className: "card-body p-2" });
  const log = el("div", { className: "chat-log mb-2", attrs: { id: "chat-log", role: "log", "aria-live": "polite" } });
  body.appendChild(log);
  const form = el("form", { className: "d-flex gap-2", attrs: { id: "chat-form" } });
  const input = el("input", {
    className: "form-control",
    attrs: { id: "chat-input", autocomplete: "off", placeholder: t("chat.placeholder"), required: "required" }
  });
  const submit = el("button", { className: "btn btn-primary", text: t("chat.send"), attrs: { type: "submit" } });
  form.appendChild(input);
  form.appendChild(submit);
  body.appendChild(form);
  card.appendChild(body);
  column.appendChild(card);
  return { form, input, log };
}

// Abort any active SSE stream when the user navigates away.  A single
// listener covers SPA hash changes, the back button, and full-page
// navigation.  Registered once at module load — not inside attach() — so
// it is always in place regardless of whether chat is enabled.
window.addEventListener("pagehide", () => {
  if (currentStream) {
    try { currentStream.abort(); } catch (e) { /* ignore */ }
    currentStream = null;
  }
});

export function attach() {
  const cfg = api.getConfig();
  const enabled = !!(cfg && cfg.features && cfg.features.rag_chat_enabled);
  const column = document.getElementById("chat-column");
  if (!column || !enabled || mounted) return;
  mounted = true;
  column.classList.remove("d-none");
  const { form, input, log } = buildPanel(column);
  form.addEventListener("submit", async ev => {
    ev.preventDefault();
    const q = input.value.trim();
    if (!q) return;
    input.value = "";
    appendMsg(log, "user", q);
    const bubble = appendMsg(log, "assistant", t("chat.thinking"));

    // Abort any in-flight stream before starting a new one.
    if (currentStream) {
      currentStream.abort();
      currentStream = null;
    }

    let cleared = false;

    /**
     * onEvent — receives parsed SSE frames from api.sseStream.
     * The v2 server emits typed events: "chunk", "done", "error", "phase",
     * "sources", "retry", "waiting", "fallback", "warning".
     * All user-visible text is set via textContent — no XSS risk.
     */
    function onEvent({ type, data }) {
      if (type === "done") {
        // data.session_id may be used for future session management
        currentStream = null;
        return;
      }
      if (type === "error") {
        // Map server-supplied error_code to i18n keys via an explicit allowlist.
        // Never surface raw server strings — they may contain stack traces or
        // internal paths.
        const KNOWN_ERROR_CODES = [
          "rate_limit",
          "auth_error",
          "service_unavailable",
          "timeout",
          "context_length_exceeded",
          "model_not_found",
          "invalid_response",
          "connection_error"
        ];
        const code = data && data.error_code;
        const key = KNOWN_ERROR_CODES.includes(code) ? "error." + code : "error.server";
        bubble.textContent = t(key);
        if (currentStream) { currentStream.abort(); currentStream = null; }
        return;
      }
      if (type === "chunk") {
        // Append streamed content chunk to bubble.
        if (!cleared) { bubble.textContent = ""; cleared = true; }
        const content = (data && data.content != null) ? String(data.content) : "";
        bubble.textContent += content;   // textContent — no XSS
        log.scrollTop = log.scrollHeight;
        return;
      }
      if (type === "phase" || type === "retry" || type === "waiting" ||
          type === "fallback" || type === "warning" || type === "sources") {
        // Progress/metadata events — no bubble update needed in the minimal UI.
        return;
      }
      // Unrecognised event type — ignore silently.
    }

    function onError(err) {
      currentStream = null;
      if (err && err.name === "NetworkError") {
        bubble.textContent = t("error.network");
      } else {
        bubble.textContent = t("error.server");
      }
    }

    currentStream = api.sseStream("/chat/stream", { q }, onEvent, onError);
  });
}
