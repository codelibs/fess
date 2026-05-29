// SPDX-License-Identifier: Apache-2.0
// Chat module for the Fess bootstrap SPA.
// Provides attachInline() for the results-page sidebar panel and
// attachStandalone() for the full-width /chat route.

import * as api from "./api.js";
import { t } from "./i18n.js";
import { sanitizeHtml } from "./format.js";
import { parseMarkdown } from "./markdown.js";

// ---------------------------------------------------------------------------
// Shared state
// ---------------------------------------------------------------------------

/** AbortController for the active stream; null when idle. */
let currentStream = null;

/** Guard: prevent duplicate fess:route:change listener registration. */
let routeListenerAttached = false;

/** Current session id — shared by inline and standalone UIs. */
let sessionId = null;

/** Whether the standalone view has been mounted at least once. */
let standaloneMounted = false;

// ---------------------------------------------------------------------------
// Utility helpers
// ---------------------------------------------------------------------------

/**
 * Validate a URL string for safe use in an <a href>. Returns "#" for any
 * URL whose scheme is not http(s) or mailto, or for malformed input. Used
 * because source URLs in SSE events originate from the crawl index and
 * may contain attacker-controlled values from crawled documents.
 *
 * @param {string} u
 * @returns {string}
 */
function safeHref(u) {
  if (!u) return "#";
  try {
    const parsed = new URL(String(u), window.location.origin);
    const p = parsed.protocol;
    if (p === "http:" || p === "https:" || p === "mailto:") return parsed.href;
  } catch {
    /* fall through */
  }
  return "#";
}

/**
 * Create a DOM element with optional class, text content, and attributes.
 *
 * @param {string} tag
 * @param {{ className?: string, text?: string, attrs?: Record<string, string> }} [opts]
 * @returns {HTMLElement}
 */
function el(tag, opts) {
  const node = document.createElement(tag);
  if (!opts) return node;
  if (opts.className) node.className = opts.className;
  if (opts.text != null) node.textContent = opts.text;
  if (opts.attrs) {
    for (const [k, v] of Object.entries(opts.attrs)) {
      node.setAttribute(k, String(v));
    }
  }
  return node;
}

// ---------------------------------------------------------------------------
// SSE error-code allowlist (shared by both UIs)
// ---------------------------------------------------------------------------

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

// ---------------------------------------------------------------------------
// Phase strip (E.4)
// ---------------------------------------------------------------------------

const PHASE_ORDER = ["intent", "search", "evaluate", "fetch", "answer"];

/**
 * Build the 5-step phase progress strip.
 *
 * @returns {{ strip: HTMLElement, advanceTo: (phase: string, hitCount?: number) => void, reset: () => void }}
 */
function buildPhaseStrip() {
  const strip = el("div", { className: "chat-phase-strip d-flex gap-2 align-items-center mb-2 flex-wrap" });
  const badges = {};

  for (const phase of PHASE_ORDER) {
    const badge = el("span", {
      className: "badge bg-secondary chat-step",
      attrs: { "data-step": phase }
    });
    const icon = el("i", { attrs: { "aria-hidden": "true" } });
    // Map phase to icon class
    const iconMap = {
      intent: "fa fa-lightbulb-o",
      search: "fa fa-search",
      evaluate: "fa fa-check-circle-o",
      fetch: "fa fa-file-text-o",
      answer: "fa fa-pencil"
    };
    icon.className = iconMap[phase] || "fa fa-circle";
    badge.appendChild(icon);
    badge.appendChild(document.createTextNode(" " + t("labels.chat_step_" + phase)));
    strip.appendChild(badge);
    badges[phase] = badge;
  }

  function advanceTo(phase) {
    const idx = PHASE_ORDER.indexOf(phase);
    if (idx === -1) return;
    for (let i = 0; i < PHASE_ORDER.length; i++) {
      const b = badges[PHASE_ORDER[i]];
      b.classList.remove("bg-primary", "bg-success", "bg-secondary");
      if (i < idx) {
        b.classList.add("bg-success");
      } else if (i === idx) {
        b.classList.add("bg-primary");
      } else {
        b.classList.add("bg-secondary");
      }
    }
  }

  function complete(phase, hitCount) {
    const idx = PHASE_ORDER.indexOf(phase);
    if (idx === -1) return;
    const b = badges[phase];
    b.classList.remove("bg-primary", "bg-secondary");
    b.classList.add("bg-success");
    // Show hit_count on search phase complete
    if (phase === "search" && hitCount != null) {
      const countText = t("labels.chat_hit_count", { count: hitCount });
      const old = b.querySelector(".chat-hit-count");
      if (old) old.remove();
      const countSpan = el("span", { className: "chat-hit-count ms-1 small", text: " (" + countText + ")" });
      b.appendChild(countSpan);
    }
  }

  function reset() {
    for (const b of Object.values(badges)) {
      b.classList.remove("bg-primary", "bg-success");
      b.classList.add("bg-secondary");
      const old = b.querySelector(".chat-hit-count");
      if (old) old.remove();
    }
  }

  return { strip, advanceTo, complete, reset };
}

// ---------------------------------------------------------------------------
// Filter panel (E.6)
// ---------------------------------------------------------------------------

/**
 * Build the collapsible filter panel.
 *
 * @returns {{ panel: HTMLElement, getFilters: () => { fields: string[], extraQ: string[] } }}
 */
function buildFilterPanel() {
  const cfg = api.getConfig() || {};
  const labelOptions = cfg.label_options || cfg.labels || [];
  const facetViews = cfg.facet_views || [];

  const wrapper = el("div", { className: "chat-filter-panel mb-2" });

  // If there are no configurable filters, return an empty placeholder.
  if (labelOptions.length === 0 && facetViews.length === 0) {
    wrapper.hidden = true;
    return {
      panel: wrapper,
      getFilters: () => ({ fields: [], extraQ: [] })
    };
  }

  const collapseId = "chat-filter-collapse";
  const header = el("div", { className: "d-flex gap-2 align-items-center mb-1" });

  const toggleBtn = el("button", {
    className: "btn btn-outline-secondary btn-sm",
    attrs: {
      type: "button",
      "data-bs-toggle": "collapse",
      "data-bs-target": "#" + collapseId,
      "aria-expanded": "false",
      "aria-controls": collapseId
    }
  });
  const filterIcon = el("i", { className: "fa fa-filter me-1", attrs: { "aria-hidden": "true" } });
  toggleBtn.appendChild(filterIcon);
  toggleBtn.appendChild(document.createTextNode(t("labels.chat_filter")));

  const filterBadge = el("span", { className: "badge rounded-pill bg-primary d-none ms-1", text: "0" });
  toggleBtn.appendChild(filterBadge);

  const clearBtn = el("button", {
    className: "btn btn-link btn-sm",
    text: t("labels.chat_clear"),
    attrs: { type: "button" }
  });

  header.appendChild(toggleBtn);
  header.appendChild(clearBtn);
  wrapper.appendChild(header);

  const collapse = el("div", { className: "collapse", attrs: { id: collapseId } });
  const filterBody = el("div", { className: "card card-body p-2 mb-2" });

  // Track all checkboxes.
  const checkboxes = [];

  function updateBadge() {
    const checked = checkboxes.filter(cb => cb.checked).length;
    filterBadge.textContent = String(checked);
    filterBadge.classList.toggle("d-none", checked === 0);
  }

  // Label options
  if (labelOptions.length > 0) {
    const groupLabel = el("div", { className: "fw-semibold small mb-1", text: t("labels.facet_label_title") });
    filterBody.appendChild(groupLabel);
    for (const opt of labelOptions) {
      const labelEl = el("label", { className: "d-flex align-items-center gap-1 small mb-1" });
      const cb = el("input", { attrs: { type: "checkbox", "data-filter-type": "label", "data-filter-value": opt.value || opt } });
      cb.addEventListener("change", updateBadge);
      checkboxes.push(cb);
      labelEl.appendChild(cb);
      labelEl.appendChild(document.createTextNode(opt.label || opt.value || opt));
      filterBody.appendChild(labelEl);
    }
  }

  // Facet views
  for (const facetView of facetViews) {
    const groupLabel = el("div", { className: "fw-semibold small mb-1 mt-2", text: facetView.title || "" });
    filterBody.appendChild(groupLabel);
    const queries = facetView.queryMap || facetView.queries || [];
    for (const [key, val] of (Array.isArray(queries) ? queries.map(q => [q.label || q, q.value || q]) : Object.entries(queries))) {
      const labelEl = el("label", { className: "d-flex align-items-center gap-1 small mb-1" });
      const cb = el("input", { attrs: { type: "checkbox", "data-filter-type": "ex_q", "data-filter-value": val } });
      cb.addEventListener("change", updateBadge);
      checkboxes.push(cb);
      labelEl.appendChild(cb);
      labelEl.appendChild(document.createTextNode(key));
      filterBody.appendChild(labelEl);
    }
  }

  clearBtn.addEventListener("click", () => {
    checkboxes.forEach(cb => { cb.checked = false; });
    updateBadge();
  });

  collapse.appendChild(filterBody);
  wrapper.appendChild(collapse);

  function getFilters() {
    const fields = [];
    const extraQ = [];
    for (const cb of checkboxes) {
      if (!cb.checked) continue;
      const type = cb.getAttribute("data-filter-type");
      const val = cb.getAttribute("data-filter-value");
      if (type === "label") fields.push(val);
      else if (type === "ex_q") extraQ.push(val);
    }
    return { fields, extraQ };
  }

  return { panel: wrapper, getFilters };
}

// ---------------------------------------------------------------------------
// Welcome state (E.8)
// ---------------------------------------------------------------------------

function buildWelcomeState() {
  const wrap = el("div", { className: "chat-welcome text-center py-5" });
  const icon = el("div", { className: "mb-3" });
  const iconI = el("i", { className: "fa fa-comments fa-3x text-muted", attrs: { "aria-hidden": "true" } });
  icon.appendChild(iconI);
  wrap.appendChild(icon);

  const title = el("h5", { className: "chat-welcome-title", text: t("labels.chat_welcome_title") });
  wrap.appendChild(title);

  const desc = el("p", { className: "text-muted small", text: t("labels.chat_welcome_description") });
  wrap.appendChild(desc);

  return wrap;
}

// ---------------------------------------------------------------------------
// Source card helpers (E.3)
// ---------------------------------------------------------------------------

/**
 * Return a Font Awesome icon class for a source URL / mimetype.
 *
 * @param {string} [url]
 * @param {string} [mimetype]
 * @returns {string}
 */
function sourceIcon(url, mimetype) {
  if (mimetype) {
    if (mimetype.indexOf("pdf") !== -1) return "fa-file-pdf-o";
    if (mimetype.indexOf("word") !== -1 || mimetype.indexOf("document") !== -1) return "fa-file-word-o";
    if (mimetype.indexOf("excel") !== -1 || mimetype.indexOf("spreadsheet") !== -1) return "fa-file-excel-o";
    if (mimetype.indexOf("powerpoint") !== -1 || mimetype.indexOf("presentation") !== -1) return "fa-file-powerpoint-o";
    if (mimetype.indexOf("image") !== -1) return "fa-file-image-o";
    if (mimetype.indexOf("text") !== -1) return "fa-file-text-o";
  }
  if (url) {
    const ext = url.split(".").pop().toLowerCase().split("?")[0];
    switch (ext) {
      case "pdf": return "fa-file-pdf-o";
      case "doc": case "docx": return "fa-file-word-o";
      case "xls": case "xlsx": return "fa-file-excel-o";
      case "ppt": case "pptx": return "fa-file-powerpoint-o";
      case "jpg": case "jpeg": case "png": case "gif": case "webp": case "svg": return "fa-file-image-o";
      case "txt": case "md": return "fa-file-text-o";
      case "html": case "htm": return "fa-globe";
      default: break;
    }
  }
  return "fa-file-o";
}

/**
 * Return a human-readable type label for a source URL / mimetype.
 *
 * @param {string} [url]
 * @param {string} [mimetype]
 * @returns {string}
 */
function sourceTypeLabel(url, mimetype) {
  if (mimetype) {
    if (mimetype.indexOf("pdf") !== -1) return "PDF";
    if (mimetype.indexOf("word") !== -1 || mimetype.indexOf("document") !== -1) return "Word";
    if (mimetype.indexOf("excel") !== -1 || mimetype.indexOf("spreadsheet") !== -1) return "Excel";
    if (mimetype.indexOf("powerpoint") !== -1 || mimetype.indexOf("presentation") !== -1) return "PowerPoint";
    if (mimetype.indexOf("image") !== -1) return "Image";
  }
  if (url) {
    const ext = url.split(".").pop().toLowerCase().split("?")[0];
    switch (ext) {
      case "pdf": return "PDF";
      case "doc": case "docx": return "Word";
      case "xls": case "xlsx": return "Excel";
      case "ppt": case "pptx": return "PowerPoint";
      case "jpg": case "jpeg": case "png": case "gif": case "webp": case "svg": return "Image";
      case "txt": case "md": return "Text";
      case "html": case "htm": return "Web";
      default: break;
    }
  }
  return t("labels.chat_source_type_document");
}

// ---------------------------------------------------------------------------
// Bubble factory (E.11 copy-to-clipboard, E.3 sources, E.13 html_content)
// ---------------------------------------------------------------------------

/**
 * Append a user bubble to the log.
 *
 * @param {HTMLElement} log
 * @param {string} text
 * @returns {HTMLElement}
 */
function appendUserBubble(log, text) {
  const wrap = el("div", { className: "chat-msg-wrap user" });
  const bubble = el("div", { className: "chat-msg user" });
  bubble.textContent = text;
  wrap.appendChild(bubble);
  log.appendChild(wrap);
  log.scrollTop = log.scrollHeight;
  return bubble;
}

/**
 * Append an assistant bubble to the log with Markdown streaming support
 * and copy-to-clipboard button.
 *
 * @param {HTMLElement} log
 * @param {string} [initialText]
 * @returns {{ bubble: HTMLElement, wrap: HTMLElement, setBuffer: (md: string) => void, setHtmlContent: (html: string) => void, appendSources: (sources: any[]) => void }}
 */
function appendAssistantBubble(log, initialText) {
  const wrap = el("div", { className: "chat-msg-wrap assistant" });

  // Header row with copy button
  const header = el("div", { className: "chat-bubble-header d-flex justify-content-end mb-1" });
  const copyBtn = el("button", {
    className: "btn btn-link btn-sm p-0 chat-copy-btn",
    attrs: { type: "button", "aria-label": t("labels.chat_copied") }
  });
  const copyIcon = el("i", { className: "fa fa-copy", attrs: { "aria-hidden": "true" } });
  copyBtn.appendChild(copyIcon);
  copyBtn.appendChild(document.createTextNode(" "));
  const copyLabel = el("span", { text: "" });
  copyBtn.appendChild(copyLabel);
  header.appendChild(copyBtn);

  const bubble = el("div", {
    className: "chat-msg assistant",
    attrs: { role: "region", "aria-live": "off" }
  });
  if (initialText != null) bubble.textContent = initialText;

  // Status line for retry/waiting/fallback/warning (E.5)
  const statusLine = el("div", {
    className: "chat-status-line text-muted small mt-1 d-none",
    attrs: { "aria-live": "polite" }
  });

  wrap.appendChild(header);
  wrap.appendChild(bubble);
  wrap.appendChild(statusLine);
  log.appendChild(wrap);
  log.scrollTop = log.scrollHeight;

  // Copy-to-clipboard (E.11)
  let copyTimeout = null;
  copyBtn.addEventListener("click", () => {
    const plainText = bubble.textContent || "";
    const doFeedback = (success) => {
      copyLabel.textContent = success ? t("labels.chat_copied") : t("labels.chat_copy_failed");
      if (copyTimeout) clearTimeout(copyTimeout);
      copyTimeout = setTimeout(() => { copyLabel.textContent = ""; }, 2000);
    };
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(plainText).then(() => doFeedback(true), () => doFeedback(false));
    } else {
      // execCommand fallback
      try {
        const ta = document.createElement("textarea");
        ta.value = plainText;
        ta.className = "chat-clipboard-fallback";
        document.body.appendChild(ta);
        ta.select();
        document.execCommand("copy");
        document.body.removeChild(ta);
        doFeedback(true);
      } catch {
        doFeedback(false);
      }
    }
  });

  // Markdown buffer rendering
  let mdBuffer = "";

  function setBuffer(md) {
    mdBuffer = md;
    const html = parseMarkdown(md);
    while (bubble.firstChild) bubble.removeChild(bubble.firstChild);
    bubble.appendChild(sanitizeHtml(html));
    log.scrollTop = log.scrollHeight;
  }

  // E.13: Replace with sanitized html_content from done event
  function setHtmlContent(html) {
    while (bubble.firstChild) bubble.removeChild(bubble.firstChild);
    bubble.appendChild(sanitizeHtml(html));
    log.scrollTop = log.scrollHeight;
  }

  // E.3: Append sources details element — numbered cards with icon/type/index
  function appendSources(sources) {
    if (!sources || sources.length === 0) return;
    const details = el("details", { className: "chat-sources mt-2" });
    const summary = el("summary", { text: t("labels.chat_sources") });
    details.appendChild(summary);
    const ul = el("ul", { className: "list-unstyled mt-1 mb-0" });
    for (let i = 0; i < sources.length; i++) {
      const src = sources[i];
      // URL precedence per backend: go_url (redirect tracker) > url_link (canonical) > url (raw)
      const href = safeHref(src.go_url || src.url_link || src.url || "");
      const iconClass = sourceIcon(src.url, src.mimetype);
      const typeText = sourceTypeLabel(src.url, src.mimetype);
      const titleText = src.title || src.url || t("labels.chat_source_fallback");

      const li = el("li", { className: "mb-1" });
      const a = document.createElement("a");
      a.href = href;
      a.className = "source-card d-flex align-items-start gap-2 text-decoration-none";
      a.setAttribute("target", "_blank");
      a.setAttribute("rel", "noopener noreferrer");

      const indexSpan = el("span", { className: "source-index badge bg-secondary flex-shrink-0", text: String(i + 1) });

      const infoDiv = el("div", { className: "source-info" });
      const titleSpan = el("span", { className: "source-title d-block", text: titleText });
      const metaDiv = el("div", { className: "source-meta" });
      const typeSpan = el("span", { className: "source-type text-muted small" });
      const icon = el("i", { className: "fa " + iconClass + " me-1", attrs: { "aria-hidden": "true" } });
      typeSpan.appendChild(icon);
      typeSpan.appendChild(document.createTextNode(typeText));
      metaDiv.appendChild(typeSpan);
      infoDiv.appendChild(titleSpan);
      infoDiv.appendChild(metaDiv);

      a.appendChild(indexSpan);
      a.appendChild(infoDiv);
      li.appendChild(a);
      ul.appendChild(li);
    }
    details.appendChild(ul);
    wrap.appendChild(details);
  }

  // Status line updater
  function setStatusLine(text) {
    if (text) {
      statusLine.textContent = text;
      statusLine.classList.remove("d-none");
    } else {
      statusLine.classList.add("d-none");
      statusLine.textContent = "";
    }
  }

  return { bubble, wrap, setBuffer, setHtmlContent, appendSources, setStatusLine };
}

// ---------------------------------------------------------------------------
// Error banner (E.5)
// ---------------------------------------------------------------------------

/**
 * Build the error banner element (hidden by default).
 *
 * @param {() => void} onRetry
 * @returns {{ banner: HTMLElement, show: (msg: string) => void, hide: () => void }}
 */
function buildErrorBanner(onRetry) {
  const banner = el("div", {
    className: "chat-error-banner alert alert-danger d-none align-items-center mb-2",
    attrs: { role: "alert" }
  });

  const icon = el("i", { className: "fa fa-exclamation-triangle me-2", attrs: { "aria-hidden": "true" } });
  const msgSpan = el("span", { className: "flex-grow-1" });

  const retryBtn = el("button", {
    className: "btn btn-sm btn-outline-danger ms-2",
    text: t("labels.chat_retry"),
    attrs: { type: "button" }
  });
  retryBtn.addEventListener("click", () => { hide(); onRetry(); });

  const dismissBtn = el("button", {
    className: "btn-close ms-2",
    attrs: { type: "button", "aria-label": t("labels.chat_dismiss") }
  });
  dismissBtn.addEventListener("click", hide);

  banner.appendChild(icon);
  banner.appendChild(msgSpan);
  banner.appendChild(retryBtn);
  banner.appendChild(dismissBtn);

  function show(msg) {
    msgSpan.textContent = msg;
    banner.classList.remove("d-none");
    banner.classList.add("d-flex");
  }

  function hide() {
    banner.classList.add("d-none");
    banner.classList.remove("d-flex");
    msgSpan.textContent = "";
  }

  return { banner, show, hide };
}

// ---------------------------------------------------------------------------
// Status lozenge (E.10)
// ---------------------------------------------------------------------------

/**
 * Build the status lozenge badge.
 *
 * @returns {{ lozenge: HTMLElement, setStatus: (key: 'ready'|'thinking'|'error') => void }}
 */
function buildStatusLozenge() {
  const lozenge = el("span", {
    className: "badge bg-secondary chat-status-lozenge",
    attrs: { role: "status", "aria-live": "polite" }
  });
  const icon = el("i", { className: "fa fa-robot me-1", attrs: { "aria-hidden": "true" } });
  const text = el("span");
  lozenge.appendChild(icon);
  lozenge.appendChild(text);

  function setStatus(key) {
    lozenge.classList.remove("bg-secondary", "bg-primary", "bg-danger");
    if (key === "thinking") {
      lozenge.classList.add("bg-primary");
      text.textContent = t("labels.chat_status_thinking");
    } else if (key === "error") {
      lozenge.classList.add("bg-danger");
      text.textContent = t("labels.chat_status_error");
    } else {
      lozenge.classList.add("bg-secondary");
      text.textContent = t("labels.chat_status_ready");
    }
  }

  setStatus("ready");
  return { lozenge, setStatus };
}

// ---------------------------------------------------------------------------
// Map SSE error_code → i18n text
// ---------------------------------------------------------------------------

function errorCodeToText(code) {
  if (KNOWN_ERROR_CODES.includes(code)) {
    return t("error." + code);
  }
  return t("error.server");
}

// ---------------------------------------------------------------------------
// SSE event handler factory
// ---------------------------------------------------------------------------

/**
 * Build the SSE onEvent handler and attach it to a set of UI controls.
 *
 * @param {{ log, phaseStrip, statusLozenge, errorBanner, lastQuestion, inputEl, submitEl, emptyState }} ui
 * @returns {{ onEvent: Function, onError: Function }}
 */
function buildEventHandlers(ui) {
  const { log, phaseStrip, statusLozenge, errorBanner, getLastQuestion, inputEl, submitEl, emptyState } = ui;

  let activeBubble = null;
  let mdBuffer = "";
  let deltaCleared = false;

  function startNewAssistantBubble(initialText) {
    mdBuffer = "";
    deltaCleared = false;
    const bub = appendAssistantBubble(log, initialText);
    activeBubble = bub;
    return bub;
  }

  function setInputEnabled(enabled) {
    if (inputEl) inputEl.disabled = !enabled;
    if (submitEl) submitEl.disabled = !enabled;
  }

  function onEvent({ type, data }) {
    if (type === "phase") {
      const phase = data && data.phase;
      const hitCount = data && data.hit_count;
      if (phase) {
        phaseStrip.advanceTo(phase, hitCount);
        statusLozenge.setStatus("thinking");
      }
      return;
    }

    if (type === "chunk" || type === "delta") {
      // Hide empty state on first content
      if (emptyState) emptyState.hidden = true;

      const content = (data && data.content != null) ? String(data.content) : "";
      if (!activeBubble) {
        startNewAssistantBubble("");
      }
      if (!deltaCleared) {
        mdBuffer = "";
        deltaCleared = true;
        // Clear the initial "thinking" text
        while (activeBubble.bubble.firstChild) activeBubble.bubble.removeChild(activeBubble.bubble.firstChild);
      }
      mdBuffer += content;
      activeBubble.setBuffer(mdBuffer);
      return;
    }

    if (type === "sources") {
      const sources = data && (data.sources || data);
      if (activeBubble && Array.isArray(sources)) {
        activeBubble.appendSources(sources);
      }
      return;
    }

    if (type === "done") {
      currentStream = null;
      setInputEnabled(true);
      statusLozenge.setStatus("ready");
      // Save session id
      if (data && data.session_id) sessionId = data.session_id;
      // E.13: Replace with html_content if present
      if (activeBubble && data && data.html_content) {
        activeBubble.setHtmlContent(data.html_content);
      }
      activeBubble = null;
      return;
    }

    if (type === "error") {
      currentStream = null;
      setInputEnabled(true);
      statusLozenge.setStatus("error");
      const code = data && data.error_code;
      const msg = errorCodeToText(code);

      // E.15: auth_error — show login prompt
      if (code === "auth_error") {
        const authMsg = t("labels.chat_auth_required");
        // Try to open login modal
        const loginModal = document.getElementById("login-modal");
        if (loginModal && window.bootstrap && window.bootstrap.Modal) {
          window.bootstrap.Modal.getOrCreateInstance(loginModal).show();
        }
        errorBanner.show(authMsg);
        if (activeBubble) activeBubble.setStatusLine("");
        activeBubble = null;
        return;
      }

      errorBanner.show(msg);
      if (activeBubble) {
        activeBubble.bubble.textContent = msg;
        activeBubble.setStatusLine("");
      }
      activeBubble = null;
      return;
    }

    if (type === "retry") {
      if (activeBubble) activeBubble.setStatusLine(t("labels.chat_retrying"));
      return;
    }

    if (type === "waiting") {
      if (activeBubble) activeBubble.setStatusLine(t("labels.chat_waiting_queue"));
      return;
    }

    if (type === "fallback") {
      const reason = data && data.reason;
      const fallbackMsg = reason === "no_results" || reason === "no_relevant_results"
        ? t("labels.chat_fallback_model")
        : t("labels.chat_fallback_model");
      if (activeBubble) activeBubble.setStatusLine(fallbackMsg);
      return;
    }

    if (type === "warning") {
      const code = data && data.code;
      if (code === "reasoning_token_exhausted" || code === "token_exhausted") {
        if (activeBubble) activeBubble.setStatusLine(t("labels.chat_warning_token_exhausted"));
      }
      return;
    }
    // Unknown event types are ignored.
  }

  function onError(err) {
    currentStream = null;
    setInputEnabled(true);
    statusLozenge.setStatus("error");
    const msg = (err && err.name === "NetworkError")
      ? t("error.network")
      : t("error.server");
    errorBanner.show(msg);
    if (activeBubble) {
      activeBubble.bubble.textContent = msg;
    }
    activeBubble = null;
  }

  return {
    onEvent,
    onError,
    startNewAssistantBubble,
    setActiveBubble: (b) => { activeBubble = b; }
  };
}

// ---------------------------------------------------------------------------
// Submit logic (shared)
// ---------------------------------------------------------------------------

/**
 * Submit a question to the streaming chat API.
 *
 * @param {string} question
 * @param {{ log, phaseStrip, statusLozenge, errorBanner, getLastQuestion, inputEl, submitEl, emptyState, getFilters }} uiRefs
 */
function submitQuestion(question, uiRefs) {
  if (!question) return;

  const { log, phaseStrip, statusLozenge, errorBanner, inputEl, submitEl, emptyState, getFilters } = uiRefs;

  // Hide welcome state
  if (emptyState) emptyState.hidden = true;

  errorBanner.hide();
  phaseStrip.reset();
  statusLozenge.setStatus("thinking");

  // Disable input while streaming
  if (inputEl) inputEl.disabled = true;
  if (submitEl) submitEl.disabled = true;

  // Append user bubble
  appendUserBubble(log, question);

  // Append thinking assistant bubble
  const assistantBubbleCtx = appendAssistantBubble(log, t("chat.thinking"));

  // Abort any in-flight stream
  if (currentStream) {
    currentStream.abort();
    currentStream = null;
  }

  // Build request body per v2 contract: message / session_id / fields.label / extra_queries
  const filters = getFilters ? getFilters() : { fields: [], extraQ: [] };
  const body = { message: question };
  if (sessionId) body.session_id = sessionId;
  if (filters.fields.length > 0) body.fields = { label: filters.fields.slice() };
  if (filters.extraQ.length > 0) body.extra_queries = filters.extraQ.slice();

  let mdBuffer = "";
  let deltaCleared = false;
  let activeBubble = assistantBubbleCtx;

  function onEvent({ type, data }) {
    if (type === "phase") {
      const phase = data && data.phase;
      const status = data && data.status;
      const hitCount = data && data.hit_count;
      if (phase) {
        if (status === "complete") phaseStrip.complete(phase, hitCount);
        else { phaseStrip.advanceTo(phase); statusLozenge.setStatus("thinking"); }
      }
      return;
    }

    if (type === "chunk" || type === "delta") {
      const content = (data && data.content != null) ? String(data.content) : "";
      if (!deltaCleared) {
        mdBuffer = "";
        deltaCleared = true;
        while (activeBubble.bubble.firstChild) activeBubble.bubble.removeChild(activeBubble.bubble.firstChild);
      }
      mdBuffer += content;
      activeBubble.setBuffer(mdBuffer);
      return;
    }

    if (type === "sources") {
      const sources = data && (data.sources || data);
      if (Array.isArray(sources)) activeBubble.appendSources(sources);
      return;
    }

    if (type === "done") {
      currentStream = null;
      if (inputEl) inputEl.disabled = false;
      if (submitEl) submitEl.disabled = false;
      statusLozenge.setStatus("ready");
      if (data && data.session_id) sessionId = data.session_id;
      if (data && data.html_content) activeBubble.setHtmlContent(data.html_content);
      return;
    }

    if (type === "error") {
      currentStream = null;
      if (inputEl) inputEl.disabled = false;
      if (submitEl) submitEl.disabled = false;
      statusLozenge.setStatus("error");
      const code = data && data.error_code;

      if (code === "auth_error") {
        const authMsg = t("labels.chat_auth_required");
        const loginModal = document.getElementById("login-modal");
        if (loginModal && window.bootstrap && window.bootstrap.Modal) {
          window.bootstrap.Modal.getOrCreateInstance(loginModal).show();
        }
        errorBanner.show(authMsg);
        activeBubble.bubble.textContent = authMsg;
        return;
      }

      const msg = errorCodeToText(code);
      errorBanner.show(msg);
      activeBubble.bubble.textContent = msg;
      return;
    }

    if (type === "retry") {
      activeBubble.setStatusLine(t("labels.chat_retrying"));
      return;
    }

    if (type === "waiting") {
      activeBubble.setStatusLine(t("labels.chat_waiting_queue"));
      return;
    }

    if (type === "fallback") {
      activeBubble.setStatusLine(t("labels.chat_fallback_model"));
      return;
    }

    if (type === "warning") {
      const code = data && data.code;
      if (code === "reasoning_token_exhausted" || code === "token_exhausted") {
        activeBubble.setStatusLine(t("labels.chat_warning_token_exhausted"));
      }
      return;
    }
  }

  function onError(err) {
    currentStream = null;
    if (inputEl) inputEl.disabled = false;
    if (submitEl) submitEl.disabled = false;
    statusLozenge.setStatus("error");
    const msg = (err && err.name === "NetworkError") ? t("error.network") : t("error.server");
    errorBanner.show(msg);
    activeBubble.bubble.textContent = msg;
  }

  currentStream = api.sseStream("/chat/stream", body, onEvent, onError);
}

// ---------------------------------------------------------------------------
// Inline panel (sidebar) — renamed from attach()
// ---------------------------------------------------------------------------

let inlineMounted = false;

/**
 * Attach the inline chat sidebar panel to #chat-column.
 * Previously named attach(); kept as attachInline() for results-page use.
 * No-op when rag_chat_enabled is false or already mounted.
 */
export function attachInline() {
  // Register route-change abort listener once.
  if (!routeListenerAttached) {
    routeListenerAttached = true;
    document.addEventListener("fess:route:change", () => {
      if (currentStream) {
        try { currentStream.abort(); } catch { /* ignore */ }
        currentStream = null;
      }
    });
  }

  const cfg = api.getConfig();
  const enabled = !!(cfg && cfg.features && cfg.features.rag_chat_enabled);
  const column = document.getElementById("chat-column");
  if (!column || !enabled || inlineMounted) return;
  inlineMounted = true;
  column.classList.remove("d-none");

  // Minimal inline panel (sidebar)
  while (column.firstChild) column.removeChild(column.firstChild);
  const card = el("div", { className: "card shadow-sm" });
  const header = el("div", { className: "card-header d-flex justify-content-between align-items-center" });

  const titleEl = el("strong", { text: t("labels.chat_title") });
  header.appendChild(titleEl);

  const { lozenge, setStatus } = buildStatusLozenge();
  header.appendChild(lozenge);

  card.appendChild(header);

  const body = el("div", { className: "card-body p-2" });

  const { strip: phaseStrip, advanceTo: phaseAdvanceTo, complete: phaseComplete, reset: phaseReset } = buildPhaseStrip();
  body.appendChild(phaseStrip);

  const log = el("div", {
    className: "chat-log mb-2",
    attrs: { id: "chat-log", role: "log", "aria-live": "polite", "aria-label": t("labels.chat_messages_area") }
  });
  body.appendChild(log);

  let lastQuestion = "";

  const { banner: errorBannerEl, show: showError, hide: hideError } = buildErrorBanner(() => {
    if (lastQuestion) submitQuestion(lastQuestion, refs);
  });
  body.appendChild(errorBannerEl);

  const form = el("form", { className: "d-flex gap-2", attrs: { id: "chat-form-inline" } });
  const input = el("textarea", {
    className: "form-control",
    attrs: {
      id: "chat-input-inline",
      autocomplete: "off",
      placeholder: t("labels.chat_input_placeholder"),
      rows: "2",
      maxlength: "4000"
    }
  });
  const submit = el("button", {
    className: "btn btn-primary",
    attrs: { type: "submit", "aria-label": t("labels.chat_send") }
  });
  const sendIcon = el("i", { className: "fa fa-paper-plane", attrs: { "aria-hidden": "true" } });
  submit.appendChild(sendIcon);

  form.appendChild(input);
  form.appendChild(submit);
  body.appendChild(form);

  const hint = el("div", { className: "text-muted small mt-1", text: t("labels.chat_input_hint") });
  body.appendChild(hint);

  card.appendChild(body);
  column.appendChild(card);

  const refs = {
    log,
    phaseStrip: { advanceTo: phaseAdvanceTo, complete: phaseComplete, reset: phaseReset },
    statusLozenge: { setStatus },
    errorBanner: { show: showError, hide: hideError },
    inputEl: input,
    submitEl: submit,
    emptyState: null,
    getFilters: () => ({ fields: [], extraQ: [] })
  };

  // IME composition guard for inline input (CJK and other composing keyboards)
  let inlineComposing = false;
  input.addEventListener("compositionstart", () => { inlineComposing = true; });
  input.addEventListener("compositionend", () => { inlineComposing = false; });

  // Shift+Enter for newline, plain Enter to submit (guarded against IME composition)
  input.addEventListener("keydown", ev => {
    if (ev.key === "Enter" && !ev.shiftKey && !ev.isComposing && !inlineComposing) {
      ev.preventDefault();
      const q = input.value.trim();
      if (q) {
        lastQuestion = q;
        input.value = "";
        submitQuestion(q, refs);
      }
    }
  });

  form.addEventListener("submit", ev => {
    ev.preventDefault();
    const q = input.value.trim();
    if (!q) return;
    lastQuestion = q;
    input.value = "";
    submitQuestion(q, refs);
  });
}

// Backward-compatible alias — app.js calls chat.attach()
export function attach() {
  attachInline();
}

// ---------------------------------------------------------------------------
// Standalone full-width chat view (E.1)
// ---------------------------------------------------------------------------

/**
 * Mount or re-show the standalone chat UI in #chat-view.
 * Lazy-mounts on first call; subsequent calls re-show without rebuilding.
 */
export function attachStandalone() {
  // Register route-change abort listener once (shared with inline).
  if (!routeListenerAttached) {
    routeListenerAttached = true;
    document.addEventListener("fess:route:change", () => {
      if (currentStream) {
        try { currentStream.abort(); } catch { /* ignore */ }
        currentStream = null;
      }
    });
  }

  const container = document.getElementById("chat-view");
  if (!container) return;

  container.removeAttribute("hidden");

  if (standaloneMounted) return; // already built — just re-show
  standaloneMounted = true;

  const cfg = api.getConfig();
  const enabled = !!(cfg && cfg.features && cfg.features.rag_chat_enabled);

  // Clear and build
  while (container.firstChild) container.removeChild(container.firstChild);
  container.className = "container-fluid my-3";

  if (!enabled) {
    const msg = el("div", { className: "alert alert-warning", text: t("chat.disabled") });
    container.appendChild(msg);
    return;
  }

  const row = el("div", { className: "row justify-content-center" });
  const col = el("div", { className: "col-12 col-lg-10 col-xl-8" });

  // ---- Card ----
  const card = el("div", { className: "card shadow-sm" });

  // ---- Card header ----
  const cardHeader = el("div", { className: "card-header d-flex justify-content-between align-items-center flex-wrap gap-2" });

  const { lozenge, setStatus } = buildStatusLozenge();
  cardHeader.appendChild(lozenge);

  const btnGroup = el("div", { className: "d-flex gap-2" });

  // Filter toggle button (built later, referenced here)
  const filterCollapseId = "standalone-filter-collapse";
  const filterToggleBtn = el("button", {
    className: "btn btn-outline-secondary btn-sm",
    attrs: {
      type: "button",
      "data-bs-toggle": "collapse",
      "data-bs-target": "#" + filterCollapseId,
      "aria-expanded": "false",
      "aria-controls": filterCollapseId
    }
  });
  const filterIcon = el("i", { className: "fa fa-filter me-1", attrs: { "aria-hidden": "true" } });
  filterToggleBtn.appendChild(filterIcon);
  filterToggleBtn.appendChild(document.createTextNode(t("labels.chat_filter")));
  const filterBadge = el("span", { className: "badge rounded-pill bg-primary d-none ms-1", text: "0" });
  filterToggleBtn.appendChild(filterBadge);

  // New Chat button (E.7)
  const newChatBtn = el("button", {
    className: "btn btn-outline-secondary btn-sm",
    attrs: { type: "button" }
  });
  const newIcon = el("i", { className: "fa fa-plus me-1", attrs: { "aria-hidden": "true" } });
  newChatBtn.appendChild(newIcon);
  newChatBtn.appendChild(document.createTextNode(t("labels.chat_new_chat")));

  btnGroup.appendChild(filterToggleBtn);
  btnGroup.appendChild(newChatBtn);
  cardHeader.appendChild(btnGroup);
  card.appendChild(cardHeader);

  // ---- Filter panel ----
  const { panel: filterPanel, getFilters } = buildFilterPanel();
  // Override the collapse id to match the toggle button target
  const filterCollapse = filterPanel.querySelector(".collapse");
  if (filterCollapse) filterCollapse.id = filterCollapseId;
  card.appendChild(filterPanel);

  // ---- Card body ----
  const cardBody = el("div", { className: "card-body p-0" });

  // Messages area
  const chatMessages = el("div", {
    className: "chat-messages",
    attrs: {
      id: "standalone-chat-messages",
      role: "log",
      "aria-live": "polite",
      "aria-label": t("labels.chat_messages_area")
    }
  });

  // Welcome state (E.8)
  const emptyState = buildWelcomeState();
  chatMessages.appendChild(emptyState);

  // Phase progress strip (E.4)
  const { strip: phaseStripEl, advanceTo: phaseAdvanceTo, complete: phaseComplete, reset: phaseReset } = buildPhaseStrip();
  const progressWrap = el("div", {
    className: "chat-progress-wrap px-3 py-2 d-none",
    attrs: { role: "status", "aria-live": "polite" }
  });
  progressWrap.appendChild(phaseStripEl);

  cardBody.appendChild(chatMessages);
  cardBody.appendChild(progressWrap);
  card.appendChild(cardBody);

  // ---- Card footer ----
  const cardFooter = el("div", { className: "card-footer p-2" });

  let lastQuestion = "";

  const { banner: errorBannerEl, show: showError, hide: hideError } = buildErrorBanner(() => {
    if (lastQuestion) doSubmit(lastQuestion);
  });
  cardFooter.appendChild(errorBannerEl);

  // Input wrapper
  const inputWrapper = el("div", { className: "input-wrapper" });
  const inputGroup = el("div", { className: "input-group" });

  const textarea = el("textarea", {
    className: "form-control",
    attrs: {
      id: "standalone-chat-input",
      placeholder: t("labels.chat_input_placeholder"),
      rows: "2",
      maxlength: "4000",
      "aria-label": t("labels.chat_input_placeholder")
    }
  });

  const sendBtn = el("button", {
    className: "btn btn-primary",
    attrs: { type: "button", id: "standalone-send-btn", "aria-label": t("labels.chat_send") }
  });
  const sendIcon = el("i", { className: "fa fa-paper-plane", attrs: { "aria-hidden": "true" } });
  sendBtn.appendChild(sendIcon);

  inputGroup.appendChild(textarea);
  inputGroup.appendChild(sendBtn);
  inputWrapper.appendChild(inputGroup);

  // Input footer: hint + char counter (E.9)
  const inputFooter = el("div", { className: "input-footer d-flex justify-content-between mt-1" });
  const hintSpan = el("span", { className: "input-hint text-muted small", text: t("labels.chat_input_hint") });
  const charCountSpan = el("span", { className: "char-counter text-muted small" });
  const charNum = el("span", { text: "0", attrs: { id: "standalone-char-count" } });
  charCountSpan.appendChild(charNum);
  charCountSpan.appendChild(document.createTextNode(" / 4000"));
  inputFooter.appendChild(hintSpan);
  inputFooter.appendChild(charCountSpan);
  inputWrapper.appendChild(inputFooter);

  cardFooter.appendChild(inputWrapper);
  card.appendChild(cardFooter);

  col.appendChild(card);
  row.appendChild(col);
  container.appendChild(row);

  // ---- Wire events ----

  const refs = {
    log: chatMessages,
    phaseStrip: { advanceTo: phaseAdvanceTo, complete: phaseComplete, reset: phaseReset },
    statusLozenge: { setStatus },
    errorBanner: { show: showError, hide: hideError },
    inputEl: textarea,
    submitEl: sendBtn,
    emptyState,
    getFilters
  };

  function doSubmit(q) {
    if (!q) return;
    lastQuestion = q;
    // Show progress strip
    progressWrap.classList.remove("d-none");
    phaseReset();
    submitQuestion(q, refs);
  }

  // Char counter (E.9)
  textarea.addEventListener("input", () => {
    charNum.textContent = String(textarea.value.length);
  });

  // IME composition guard for standalone textarea (CJK and other composing keyboards)
  let standaloneComposing = false;
  textarea.addEventListener("compositionstart", () => { standaloneComposing = true; });
  textarea.addEventListener("compositionend", () => { standaloneComposing = false; });

  // Enter to submit, Shift+Enter for newline (E.9, guarded against IME composition)
  textarea.addEventListener("keydown", ev => {
    if (ev.key === "Enter" && !ev.shiftKey && !ev.isComposing && !standaloneComposing) {
      ev.preventDefault();
      const q = textarea.value.trim();
      if (q && !textarea.disabled) {
        textarea.value = "";
        charNum.textContent = "0";
        doSubmit(q);
      }
    }
  });

  sendBtn.addEventListener("click", () => {
    const q = textarea.value.trim();
    if (q && !textarea.disabled) {
      textarea.value = "";
      charNum.textContent = "0";
      doSubmit(q);
    }
  });

  // New Chat (E.7)
  newChatBtn.addEventListener("click", async () => {
    if (currentStream) {
      try { currentStream.abort(); } catch { /* ignore */ }
      currentStream = null;
    }
    // DELETE session
    if (sessionId) {
      try {
        await fetch("/api/v2/chat/sessions/" + encodeURIComponent(sessionId), {
          method: "DELETE",
          credentials: "same-origin",
          headers: { "X-Fess-CSRF-Token": api.getCsrfToken() || "" }
        });
      } catch { /* ignore network errors */ }
      sessionId = null;
    }
    // Reset UI
    while (chatMessages.firstChild) chatMessages.removeChild(chatMessages.firstChild);
    chatMessages.appendChild(emptyState);
    emptyState.hidden = false;
    hideError();
    phaseReset();
    progressWrap.classList.add("d-none");
    setStatus("ready");
    textarea.disabled = false;
    sendBtn.disabled = false;
    lastQuestion = "";
  });

  // Hide progress strip on done/error (done via polling setStatus)
  // We wrap setStatus to also control progressWrap visibility.
  const origSetStatus = setStatus;
  // Note: setStatus is a closure from buildStatusLozenge; we wrap refs
  refs.statusLozenge = {
    setStatus: (key) => {
      origSetStatus(key);
      if (key === "ready" || key === "error") {
        // Keep strip visible for user inspection after completion.
        // progressWrap stays shown until new chat.
      }
    }
  };
}
