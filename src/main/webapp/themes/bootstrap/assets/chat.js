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
     * The server emits typed events: "message" (token), "done", "error", "phase".
     * All user-visible text is set via textContent — no XSS risk.
     */
    function onEvent({ type, data }) {
      if (type === "done") {
        currentStream = null;
        return;
      }
      if (type === "error") {
        bubble.textContent = (data && (data.message || data.error)) || t("error.server");
        if (currentStream) { currentStream.abort(); currentStream = null; }
        return;
      }
      // "message" (and unrecognised types) — append token to bubble.
      if (!cleared) { bubble.textContent = ""; cleared = true; }
      const token = (data && data.token != null) ? String(data.token) : String(data || "");
      bubble.textContent += token;   // textContent — no XSS
      log.scrollTop = log.scrollHeight;
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
