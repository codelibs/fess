import * as api from "./api.js";
import { t } from "./i18n.js";

let mounted = false;

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
  // Build chat panel via DOM construction — no innerHTML with dynamic data.
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
  column.classList.remove("d-none");
  const { form, input, log } = buildPanel(column);
  form.addEventListener("submit", async ev => {
    ev.preventDefault();
    const q = input.value.trim();
    if (!q) return;
    input.value = "";
    appendMsg(log, "user", q);
    const bubble = appendMsg(log, "assistant", t("chat.thinking"));
    try {
      let cleared = false;
      // TODO Plan 6 follow-up: SSE over POST requires a custom fetch+ReadableStream impl.
      // EventSource only supports GET, but the v2 /chat/stream endpoint is POST-only;
      // a future batch must swap this EventSource call for a fetch+ReadableStream reader.
      const es = api.sse("/chat/stream", { q }, chunk => {
        if (chunk === "[[DONE]]") { es.close(); return; }
        if (!cleared) { bubble.textContent = ""; cleared = true; }
        bubble.textContent += chunk;       // textContent — no XSS
        log.scrollTop = log.scrollHeight;
      }, () => { bubble.textContent = t("error.server"); });
    } catch {
      bubble.textContent = t("error.server");
    }
  });
  mounted = true;
}
