import * as api from "./api.js";
import { t } from "./i18n.js";
import { formatFileSize, formatDate, renderHighlightedSnippet, sanitizeHtml } from "./format.js";

/** Guard: prevent duplicate event-listener registration on hot-reload. */
let attached = false;

/** AbortController for the most-recent in-flight search; null when idle. */
let currentSearchAbort = null;

/** AbortController for in-flight related-queries/content requests; null when idle. */
let currentRelatedAbort = null;

const state = {
  q: "",
  start: 0,
  num: 10,
  sort: "",
  lang: [],             // string[] — zero or more language codes; serialised as repeated lang= params
  sdh: "",              // similar_docs_hash for similarity search
  facets: {},           // field -> [values]
  fields: {},           // extra field filters (e.g. label)
  facetQueries: [],     // string[] — active ex_q clause strings from facet_views (SRCH-4)
  exQ: [],             // string[] — extra ex_q clauses forwarded from advance search (ADV-2)
  geo: { lat: "", lon: "", distance: "" }, // GEO-1: geo search state
  requestedTime: 0,     // epoch ms of the most-recent search; used in /go/ click-log URL
  highlightParams: ""   // server-supplied highlight_params string (e.g. "&hl.q=...&hl.fragsize=...")
};

/** Filetype values shown in the static filetype facet group. Matches query.facet.queries defaults. */
const FILETYPE_VALUES = [
  "html", "word", "excel", "powerpoint",
  "odt", "ods", "odp", "pdf", "txt", "others"
];

// XSS-safety: this module builds every result-card DOM node with
// document.createElement + textContent. No untrusted string is ever
// passed to innerHTML.

/**
 * Return `url` only when its scheme is in the http/https/ftp/ftps allowlist.
 * Any other scheme (e.g. javascript:, data:, vbscript:) returns "#" so that
 * setAttribute("href", safeHref(u)) can never inject executable content.
 */
function safeHref(url) {
  if (!url || typeof url !== "string") return "#";
  try {
    const u = new URL(url, location.href);
    if (u.protocol === "https:" || u.protocol === "http:" ||
        u.protocol === "ftp:" || u.protocol === "ftps:") {
      return url;
    }
  } catch (e) {
    // URL constructor failed — treat as unsafe.
    return "#";
  }
  return "#";
}

function el(tag, opts) {
  const node = document.createElement(tag);
  if (!opts) return node;
  if (opts.className) node.className = opts.className;
  if (opts.text != null) node.textContent = opts.text;
  if (opts.attrs) for (const [k, v] of Object.entries(opts.attrs)) node.setAttribute(k, String(v));
  if (opts.dataset) for (const [k, v] of Object.entries(opts.dataset)) node.dataset[k] = String(v);
  return node;
}

/**
 * Build the /go/ click-tracking URL for a result link.
 *
 * Mirrors the JSP mousedown handler in src/main/webapp/js/search.js:111-127.
 * Returns "#" when the original URL is not in the http/https/ftp/ftps allowlist
 * so that safeHref semantics are preserved — the /go/ redirect would fail anyway
 * for unsafe schemes.
 *
 * @param {string} originalUrl - the document's url_link / url value
 * @param {string} docId       - document identifier
 * @param {string} queryId     - query identifier from the search response
 * @param {number} order       - 1-based rank of the result
 * @param {number} rt          - requestedTime in epoch ms
 * @returns {string} the /go/ redirect URL, or "#" for unsafe schemes
 */
function buildGoUrl(originalUrl, docId, queryId, order, rt) {
  // Validate scheme — same rules as safeHref; only build /go/ for safe schemes.
  if (!originalUrl || typeof originalUrl !== "string") return "#";
  try {
    const u = new URL(originalUrl, location.href);
    if (u.protocol !== "https:" && u.protocol !== "http:" &&
        u.protocol !== "ftp:" && u.protocol !== "ftps:") {
      return "#";
    }
  } catch (e) {
    return "#";
  }

  let goUrl = "/go/?rt=" + encodeURIComponent(rt) +
              "&docId=" + encodeURIComponent(docId || "") +
              "&queryId=" + encodeURIComponent(queryId || "") +
              "&order=" + encodeURIComponent(order || 0);

  // Preserve the fragment from the original URL (e.g. /doc.html#section-2)
  const hashIndex = originalUrl.indexOf("#");
  if (hashIndex >= 0) {
    goUrl += "&hash=" + encodeURIComponent(originalUrl.substring(hashIndex));
  }

  return goUrl;
}

/**
 * Return the plain-text title for a result document, stripping any
 * server-injected highlight markup (<strong>/<em>) from content_title.
 * Safe to use in aria-label and other text-only contexts.
 *
 * @param {Object} d - result document object
 * @returns {string}
 */
function plainTitle(d) {
  const raw = d.content_title || d.title || d.url || "";
  return String(raw).replace(/<\/?(?:strong|em)>/g, "");
}

function buildResultCard(d, queryId, order) {
  const cfg = api.getConfig() || {};
  const features = cfg.features || {};

  const li = el("li", {
    className: "result-card",
    attrs: { id: "result-" + (order - 1) },
    dataset: { docId: d.doc_id || "", queryId: queryId || "" }
  });

  // --- Task 2.2: thumbnail (left side) ---
  if (d.thumbnail && features.thumbnail_enabled) {
    const img = document.createElement("img");
    img.className = "result-thumbnail";
    img.setAttribute("loading", "lazy");
    img.setAttribute("alt", "");
    img.setAttribute("src",
      "/thumbnail/?docId=" + encodeURIComponent(d.doc_id || "") +
      "&queryId=" + encodeURIComponent(queryId || ""));
    img.addEventListener("error", () => { img.classList.add("d-none"); });
    li.appendChild(img);
  }

  // --- card body (right side) ---
  const body = el("div", { className: "result-card-body" });

  // Build /go/ URL so all click types (left, middle, right→open-in-tab) are
  // routed through GoAction for click-log recording and server-side redirect
  // (including file-proxy).  Falls back to "#" for unsafe schemes.
  const originalUrl = d.url_link || d.url || "";
  const goHref = buildGoUrl(originalUrl, d.doc_id, queryId, order, state.requestedTime);

  const h2 = el("h2");
  const a = el("a", {
    attrs: {
      href: goHref,
      // Keep the real URL visible on hover for usability / accessibility.
      title: safeHref(originalUrl) !== "#" ? originalUrl : ""
    },
    dataset: { resultLink: "1" }
  });
  if (d.content_title) {
    a.innerHTML = renderHighlightedSnippet(d.content_title);
  } else {
    a.textContent = d.title || d.url || "";
  }
  h2.appendChild(a);
  body.appendChild(h2);

  // C.5: use site_path when present; fall back to host/url
  if (d.site_path) {
    body.appendChild(el("cite", { className: "result-url result-site-path", text: d.site_path }));
  } else {
    body.appendChild(el("div", { className: "result-url", text: d.site || d.url || "" }));
  }

  // --- Task 2.3: info meta (size · date · click_count) ---
  const infoParts = [];
  const sizeStr = formatFileSize(d.content_length);
  if (sizeStr) infoParts.push(sizeStr);
  const dateStr = formatDate(d.last_modified || d.created);
  if (dateStr) infoParts.push(dateStr);
  if (features.search_log_enabled && d.click_count > 0) {
    infoParts.push(t("result.click_views", { count: d.click_count }));
  }
  if (infoParts.length > 0) {
    body.appendChild(el("div", { className: "result-info-meta text-muted small", text: infoParts.join(" · ") }));
  }

  // --- Task 2.6: highlighted snippet via renderHighlightedSnippet ---
  const snippetEl = el("div", { className: "result-snippet" });
  snippetEl.innerHTML = renderHighlightedSnippet(d.content_description || d.digest || "");
  body.appendChild(snippetEl);

  // C.4: "More.." anchor link — fragment targets the card's own id
  const moreLink = el("a", {
    className: "result-more",
    text: t("labels.search_result_more"),
    attrs: {
      href: "#result-" + (order - 1),
      "aria-label": t("labels.search_result_more") + " - " + plainTitle(d)
    }
  });
  body.appendChild(moreLink);

  // --- result-meta row: cache link, similar docs, host, favorite ---
  const meta = el("div", { className: "result-meta" });

  // --- site/URL row with optional Copy URL button ---
  const siteRow = el("div", { className: "result-site-row" });
  siteRow.appendChild(el("span", { className: "text-muted result-host", text: d.host || "" }));

  if (features.clipboard_copy_icon) {
    const rawUrl = d.url_link || d.url || "";
    const copyBtn = el("button", {
      className: "btn btn-link btn-sm copy-url-btn p-0 ms-1",
      attrs: {
        type: "button",
        "aria-label": t("result.copy_url") + ": " + rawUrl
      }
    });
    const copyIcon = el("i", { className: "far fa-copy", attrs: { "aria-hidden": "true" } });
    copyBtn.appendChild(copyIcon);
    copyBtn.addEventListener("click", () => {
      navigator.clipboard.writeText(rawUrl).then(() => {
        copyIcon.className = "fa fa-check";
        copyBtn.setAttribute("aria-label", t("result.copied"));
        setTimeout(() => {
          copyIcon.className = "far fa-copy";
          copyBtn.setAttribute("aria-label", t("result.copy_url") + ": " + rawUrl);
        }, 2000);
      }).catch(() => { /* clipboard not available */ });
    });
    siteRow.appendChild(copyBtn);
  }
  meta.appendChild(siteRow);

  // --- Task 2.4 / A.5: cache link — only when has_cache is truthy ---
  // Use server-supplied highlight_params when available (already formatted as "&hq=<term>&hq=…").
  // Fall back to a single &hq= param built from the current query term.
  // NOTE: the v2 CacheHandler only honours the "hq" parameter (not "hl.q").  [marker: cache-link-hq]
  if (d.has_cache === "true" || d.has_cache === true) {
    const hlParam = state.highlightParams || ("&hq=" + encodeURIComponent(state.q || ""));
    const cacheLink = el("a", {
      className: "text-muted",
      text: t("result.cache"),
      attrs: { href: `/cache/?docId=${encodeURIComponent(d.doc_id || "")}${hlParam}`, target: "_blank", rel: "noopener" }
    });
    meta.appendChild(cacheLink);
  }

  // --- Task 2.5: similar docs link ---
  const simCount = Number(d.similar_docs_count);
  if (simCount > 1) {
    const simLink = el("a", {
      className: "text-muted",
      text: t("result.similar", { count: simCount }),
      attrs: { href: "#" }
    });
    simLink.addEventListener("click", ev => {
      ev.preventDefault();
      state.sdh = d.similar_docs_hash || d.doc_id || "";
      state.start = 0;
      runSearch();
    });
    meta.appendChild(simLink);
  }

  const favBtn = el("button", {
    className: "btn btn-link btn-sm favorite-btn p-0",
    attrs: { type: "button", "aria-pressed": "false", "aria-label": t("result.favorite_add") }
  });
  const favIcon = el("i", { className: "fa fa-star-o", attrs: { "aria-hidden": "true" } });
  favBtn.appendChild(favIcon);
  // A.3: set initial count from server data so the badge shows before syncFavorites runs.
  favBtn.dataset.count = String(d.favorite_count || 0);
  setFavoriteUi(favBtn, false, d.favorite_count || 0);
  meta.appendChild(favBtn);

  body.appendChild(meta);
  li.appendChild(body);
  return li;
}

/**
 * C.1: Populate the results-status banner.
 * Uses _over variant when record_count_relation !== "EQUAL_TO".
 */
function renderResultsStatus(env) {
  const statusEl = document.getElementById("results-status");
  if (!statusEl) return;
  while (statusEl.firstChild) statusEl.removeChild(statusEl.firstChild);
  const count = env.record_count || 0;
  const start = env.start_record_number || 1;
  const end   = env.end_record_number   || 0;
  const q     = state.q || "";
  const isOver = env.record_count_relation && env.record_count_relation !== "EQUAL_TO";
  const statusKey = isOver ? "labels.search_result_status_over" : "labels.search_result_status";
  const values = { b0: String(count), b1: String(start), b2: String(end), bq: q };
  t(statusKey).split(/(\{b[012q]\})/).forEach(part => {
    const m = part.match(/^\{(b[012q])\}$/);
    if (m) {
      const b = document.createElement("b");
      b.textContent = values[m[1]] != null ? values[m[1]] : "";
      statusEl.appendChild(b);
    } else if (part) {
      statusEl.appendChild(document.createTextNode(part));
    }
  });
  if (env.exec_time != null) {
    const execSec = typeof env.exec_time === "number"
      ? env.exec_time.toFixed(2)
      : (typeof env.query_time === "number" ? (env.query_time / 1000).toFixed(2) : null);
    if (execSec !== null) {
      statusEl.appendChild(document.createTextNode(" " + t("labels.search_result_time").replace("{0}", execSec)));
    }
  }
}

/**
 * C.2: Show/hide the similar-doc banner.
 */
function renderSimilarDocBanner() {
  const banner = document.getElementById("similar-doc-banner");
  if (!banner) return;
  // Clear previous content
  while (banner.firstChild) banner.removeChild(banner.firstChild);
  if (!state.sdh) {
    banner.classList.add("d-none");
    banner.classList.remove("d-flex");
    return;
  }
  banner.classList.remove("d-none");
  banner.classList.add("d-flex");
  banner.appendChild(el("span", { text: t("labels.similar_doc_result_status") }));
  const closeBtn = el("button", {
    className: "btn-close ms-2",
    attrs: { type: "button", "aria-label": t("labels.similar_doc_result_status") }
  });
  closeBtn.addEventListener("click", () => {
    state.sdh = "";
    state.start = 0;
    runSearch();
  });
  banner.appendChild(closeBtn);
}

/**
 * C.3: Render current-selection badge row (sort / num / lang / label).
 */
function renderCurrentFilters() {
  const ul = document.getElementById("current-filters");
  if (!ul) return;
  while (ul.firstChild) ul.removeChild(ul.firstChild);

  const cfg = api.getConfig() || {};
  const badges = [];

  // Sort
  if (state.sort) {
    const opt = (cfg.sort_options || []).find(o => o.value === state.sort);
    const name = opt ? t(opt.label_key || opt.value) : state.sort;
    badges.push({ name, targetId: "sort-select" });
  }

  // Num (only show when non-default)
  const defaultNum = cfg.num_options && cfg.num_options.length > 0 ? cfg.num_options[0] : 10;
  if (state.num !== Number(defaultNum)) {
    badges.push({ name: t("search.num_format", { num: state.num }), targetId: "num-select" });
  }

  // Lang
  const selected = Array.isArray(state.lang) ? state.lang : (state.lang ? [state.lang] : []);
  selected.forEach(langVal => {
    const opt = (cfg.lang_options || []).find(o => o.value === langVal);
    const name = opt
      ? (t(opt.label_key || "labels.lang_" + opt.value) || opt.label || langVal)
      : langVal;
    badges.push({ name, targetId: "lang-select" });
  });

  // Label field filter
  (state.fields.label || []).forEach(val => {
    const opt = (cfg.label_options || []).find(o => o.value === val);
    const name = opt ? (opt.label || opt.value) : val;
    badges.push({ name, targetId: "label-dropdown-btn" });
  });

  if (badges.length === 0) return;

  badges.forEach(badge => {
    const li = el("li", { className: "list-inline-item" });
    const btn = el("button", {
      className: "badge bg-secondary border-0",
      text: badge.name,
      attrs: { type: "button" }
    });
    btn.classList.add("cursor-pointer");
    btn.addEventListener("click", () => {
      const target = document.getElementById(badge.targetId);
      if (target) target.focus();
    });
    li.appendChild(btn);
    ul.appendChild(li);
  });
}

function renderResults(env) {
  const list = document.getElementById("results");
  const meta = document.getElementById("results-meta");
  const empty = document.getElementById("empty-state");
  list.innerHTML = "";   // empty-string literal — clears children, no untrusted data
  const data = env.data || [];
  // C.2: always refresh similar-doc banner (hides when state.sdh is cleared)
  renderSimilarDocBanner();
  if (data.length === 0) {
    const dnm = document.getElementById("empty-did-not-match");
    if (dnm) {
      dnm.textContent = t("search.did_not_match", [state.q || ""]);
    }
    empty.classList.remove("d-none");
    meta.textContent = "";
    const statusEl = document.getElementById("results-status");
    if (statusEl) statusEl.textContent = "";
    // ZERO-RESULT: do NOT clear related queries/content here — loadRelated() (called
    // after renderResults in runSearch) will populate them. Clearing here would race
    // against the async loadRelated and wipe its output (parity-r3 A2).
    const rpw = document.getElementById("results-popular-words");
    if (rpw) rpw.classList.add("d-none");
    return;
  }
  empty.classList.add("d-none");
  // C.1: populate the result-status banner
  renderResultsStatus(env);
  // Load popular words for the results header slot (JSP parity: #1).
  loadResultsPopularWords();
  // exec_time legacy meta (keep for backward compat; status banner is primary)
  const execSec = typeof env.exec_time === "number"
    ? env.exec_time.toFixed(2)
    : (typeof env.query_time === "number" ? (env.query_time / 1000).toFixed(2) : null);
  meta.textContent = execSec !== null
    ? `${env.record_count} · ${t("search.exec_time", { n: execSec })}`
    : String(env.record_count);
  // Pass 1-based order so buildResultCard can embed it in the /go/ URL.
  data.forEach((d, idx) => list.appendChild(buildResultCard(d, env.query_id, idx + 1)));
  list.querySelectorAll("li.result-card").forEach(li => {
    const btn = li.querySelector(".favorite-btn");
    const docId = li.dataset.docId;
    if (!btn || !docId) return;
    btn.addEventListener("click", () => toggleFavorite(docId, btn));
  });
  // Bulk-sync favorites for all result cards in one request (Feature 5).
  if (env.query_id) syncFavorites(env.query_id);
}

async function runSearch() {
  // Cancel any in-flight request before issuing a new one.
  if (currentSearchAbort) currentSearchAbort.abort();
  currentSearchAbort = new AbortController();
  const signal = currentSearchAbort.signal;
  // Cancel any in-flight related queries/content requests.
  if (currentRelatedAbort) currentRelatedAbort.abort();
  currentRelatedAbort = new AbortController();
  // Record the request time before the call so /go/ URLs embedded in result
  // cards carry the correct rt parameter (mirrors JSP #rt hidden field).
  state.requestedTime = Date.now();
  document.title = state.q ? t("page.search_title").replace("{0}", state.q) : "Fess";
  try {
    const params = { q: state.q, start: state.start, num: state.num };
    if (state.sort) params.sort = state.sort;
    // state.lang is string[] — send as repeated lang= params (empty array → omit).
    if (Array.isArray(state.lang) && state.lang.length > 0) {
      params.lang = state.lang;
    }
    if (state.sdh) params.sdh = state.sdh;
    // Merge facet-based field filters (state.facets) and explicit field filters
    // (state.fields) into a single deduplicated param per field.  The label field
    // can appear in both stores (sidebar facet group writes state.facets.label;
    // the label dropdown writes state.fields.label); emitting duplicates would
    // produce redundant fields.label params and duplicate active chips.
    const fieldSets = {};
    for (const [field, values] of Object.entries(state.facets)) {
      (values || []).forEach(v => { (fieldSets[field] = fieldSets[field] || new Set()).add(v); });
    }
    for (const [field, values] of Object.entries(state.fields)) {
      if (Array.isArray(values)) {
        values.forEach(v => { (fieldSets[field] = fieldSets[field] || new Set()).add(v); });
      }
    }
    for (const [field, valueSet] of Object.entries(fieldSets)) {
      valueSet.forEach(v => { (params["fields." + field] = params["fields." + field] || []).push(v); });
    }
    // facet query views — active ex_q clauses from server-driven facet_views (SRCH-4)
    if (Array.isArray(state.facetQueries) && state.facetQueries.length > 0) {
      params["ex_q"] = params["ex_q"] || [];
      if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
      state.facetQueries.forEach(v => params["ex_q"].push(v));
    }
    // ADV-2: extra ex_q clauses forwarded from advance search (e.g. time range)
    if (Array.isArray(state.exQ) && state.exQ.length > 0) {
      params["ex_q"] = params["ex_q"] || [];
      if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
      params["ex_q"].push(...state.exQ);
    }
    // GEO-1: emit geo params when all three are present
    if (state.geo && state.geo.lat !== "" && state.geo.lon !== "" && state.geo.distance !== "") {
      params["geo.location.point"] = state.geo.lat + "," + state.geo.lon;
      params["geo.location.distance"] = state.geo.distance;
    }
    const env = await api.get("/search", params, { signal });
    // Prefer the server-supplied requested_time when available (more accurate).
    if (env.requested_time) state.requestedTime = env.requested_time;
    // A.5: store server-supplied highlight params for cache link construction.
    state.highlightParams = (typeof env.highlight_params === "string" && env.highlight_params) ? env.highlight_params : "";
    // A.6: show/hide the partial-results warning banner.
    const warningEl = document.getElementById("results-warning");
    if (warningEl) {
      if (env.partial) {
        warningEl.textContent = t("labels.process_time_is_exceeded");
        warningEl.classList.remove("d-none");
      } else {
        warningEl.classList.add("d-none");
      }
    }
    renderResults(env);
    renderPagination(env);
    const labels = await loadLabels();
    renderFacets(env, labels);
    renderActiveChips();
    renderCurrentFilters();
    // Hide inline validation error box on successful results.
    const eb = document.getElementById("search-error");
    if (eb) eb.classList.add("d-none");
    // Fetch related queries and content concurrently (abortable on next search).
    loadRelated(state.q, currentRelatedAbort.signal);
    document.dispatchEvent(new CustomEvent("fess:search:after", { detail: env }));
  } catch (e) {
    if (e && e.name === "AbortError") return; // request superseded — silently ignore
    const errBox = document.getElementById("search-error");
    if (e && (e.code === "invalid_request" || e.code === "INVALID_REQUEST" || e.httpStatus === 400)) {
      if (errBox) { errBox.textContent = e.message || t("error.invalid_request"); errBox.classList.remove("d-none"); }
      else { document.getElementById("results-meta").textContent = e.message || t("error.invalid_request"); }
      return;
    }
    if (errBox) errBox.classList.add("d-none");
    const meta = document.getElementById("results-meta");
    if (e && e.name === "NetworkError") {
      meta.textContent = t("error.network");
    } else {
      meta.textContent = e.code === "AUTH_REQUIRED" ? t("error.auth_required") : t("error.server");
    }
  }
}

let suggestTimer = null;
let suggestIndex = -1;
/** Guard: prevent duplicate fess:route:change listener registration. */
let routeListenerAttached = false;

function renderSuggestItems(items) {
  const dropdown = document.getElementById("suggest-dropdown");
  // Clear by removing child nodes — avoids innerHTML with any dynamic string.
  while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
  items.forEach((it, i) => {
    const li = el("li", {
      className: "list-group-item",
      text: it.text || "",
      attrs: { role: "option", id: "suggest-item-" + i, "aria-selected": "false" },
      dataset: { idx: i, text: it.text || "" }
    });
    dropdown.appendChild(li);
  });
}

async function showSuggest(q) {
  const dropdown = document.getElementById("suggest-dropdown");
  const inp = document.getElementById("search-input");
  if (!q || q.length < 2) {
    dropdown.classList.add("d-none");
    while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
    if (inp) inp.setAttribute("aria-expanded", "false");
    return;
  }
  try {
    const suggestParams = { q, num: 10, fn: ["_default", "content", "title"] };
    if (Array.isArray(state.lang) && state.lang.length > 0) suggestParams.lang = state.lang;
    const labelFilters = (state.fields && state.fields.label) || [];
    if (labelFilters.length > 0) suggestParams.label = labelFilters;
    const env = await api.get("/suggest-words", suggestParams);
    const items = env.suggest_words || [];
    if (items.length === 0) {
      dropdown.classList.add("d-none");
      if (inp) inp.setAttribute("aria-expanded", "false");
      return;
    }
    renderSuggestItems(items);
    dropdown.classList.remove("d-none");
    if (inp) inp.setAttribute("aria-expanded", "true");
    suggestIndex = -1;
  } catch { /* swallow — suggest is best-effort */ }
}

function hideSuggest() {
  const dropdown = document.getElementById("suggest-dropdown");
  const inp = document.getElementById("search-input");
  dropdown.classList.add("d-none");
  if (inp) { inp.setAttribute("aria-expanded", "false"); inp.removeAttribute("aria-activedescendant"); }
  suggestIndex = -1;
}

/**
 * Disable a submit button briefly to guard against double-submits, then
 * re-enable it. Mirrors the JSP behaviour (BUTTON_DISABLE_DURATION=3000ms).
 * The SPA navigates client-side, so re-enabling on a timer is appropriate.
 * Call this AFTER the search/navigation has been triggered — it only touches
 * the button and never blocks or delays the actual search.
 *
 * @param {HTMLButtonElement|null} btn - the submit button to disable
 */
export function disableSubmitBriefly(btn) {
  if (!btn) return;
  btn.disabled = true;
  setTimeout(() => { btn.disabled = false; }, 3000);
}

/**
 * ADV-4: Reusable suggest wiring — attach autocomplete to any text input and dropdown list.
 * Calls /suggest-words with the same shape as showSuggest; renders with createElement/textContent only.
 *
 * @param {HTMLInputElement} input    - the text field to attach to
 * @param {HTMLElement}      dropdown - a <ul> that will be populated with <li> suggestions
 * @param {{ lang?: string[] | { length: number } }} [opts] - options; opts.lang can be a getter
 */
export function attachSuggest(input, dropdown, opts = {}) {
  if (!input || !dropdown) return;
  let timer = null;
  const clear = () => {
    while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
    dropdown.classList.add("d-none");
    input.setAttribute("aria-expanded", "false");
  };
  const choose = (text) => { input.value = text; clear(); input.focus(); };
  const render = async (q) => {
    if (!q || q.length < 1) { clear(); return; }
    try {
      const params = { q, num: 10, fn: ["_default", "content", "title"] };
      const lang = typeof opts.lang === "function" ? opts.lang() : opts.lang;
      if (Array.isArray(lang) && lang.length > 0) params.lang = lang;
      const env = await api.get("/suggest-words", params);
      const items = env.suggest_words || [];
      while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
      if (items.length === 0) { clear(); return; }
      items.forEach((it, i) => {
        const li = document.createElement("li");
        li.className = "list-group-item";
        li.setAttribute("role", "option");
        li.id = input.id + "-suggest-" + i;
        li.textContent = it.text || "";
        li.addEventListener("mousedown", (e) => { e.preventDefault(); choose(it.text || ""); });
        dropdown.appendChild(li);
      });
      dropdown.classList.remove("d-none");
      input.setAttribute("aria-expanded", "true");
    } catch { /* best-effort */ }
  };
  input.addEventListener("input", () => {
    if (timer) clearTimeout(timer);
    const v = input.value.trim();
    timer = setTimeout(() => render(v), 150);
  });
  input.addEventListener("blur", () => setTimeout(clear, 120));
}

/**
 * H.4: Subscribe once to the fess:route:change event so that navigating away
 * from the search view (/, /search, /index) cancels any pending suggest timer
 * and collapses the dropdown.  The guard prevents double-registration if
 * attach() is ever called a second time (the `attached` flag above normally
 * stops that, but the guard is cheap insurance).
 */
function ensureRouteListener() {
  if (routeListenerAttached) return;
  routeListenerAttached = true;
  document.addEventListener("fess:route:change", (e) => {
    const path = (e.detail?.path || "").replace(/\/+$/, "") || "/";
    const inSearch = path === "/" || path === "/search" || path === "/index";
    if (!inSearch) {
      if (suggestTimer) { clearTimeout(suggestTimer); suggestTimer = null; }
      const dropdown = document.getElementById("suggest-dropdown");
      if (dropdown) dropdown.classList.add("d-none");
    }
  });
}

// ─── Phase 3: Search option selects ──────────────────────────────────────────

/**
 * Task 3.1 — Populate the sort <select> from api config sort_options.
 * Uses fess_label-compatible keys directly as i18n keys (no mapping needed).
 */
function renderSortOptions() {
  const sel = document.getElementById("sort-select");
  if (!sel) return;
  while (sel.firstChild) sel.removeChild(sel.firstChild);
  const cfg = api.getConfig() || {};
  const opts = cfg.sort_options && cfg.sort_options.length > 0
    ? cfg.sort_options
    : [{ value: "", label_key: "labels.search_result_sort_score_desc" }];
  for (const o of opts) {
    const opt = document.createElement("option");
    opt.value = o.value != null ? o.value : "";
    opt.textContent = t(o.label_key || o.value || "");
    sel.appendChild(opt);
  }
  sel.value = state.sort || "";
}

/**
 * Task 3.2 — Populate the num <select> from api config num_options.
 */
function renderNumOptions() {
  const sel = document.getElementById("num-select");
  if (!sel) return;
  while (sel.firstChild) sel.removeChild(sel.firstChild);
  const cfg = api.getConfig() || {};
  const nums = cfg.num_options && cfg.num_options.length > 0
    ? cfg.num_options
    : [10, 20, 50];
  for (const n of nums) {
    const opt = document.createElement("option");
    opt.value = String(n);
    opt.textContent = t("search.num_format", { num: n });
    sel.appendChild(opt);
  }
  sel.value = String(state.num || 10);
}

/**
 * Task 3.3 — Populate the lang <select multiple> from api config lang_options.
 * Parity with JSP: the lang select is multi-select so users can choose several
 * languages simultaneously. state.lang is string[] and is serialised as repeated
 * lang= query parameters.
 */
function renderLangOptions() {
  const sel = document.getElementById("lang-select");
  if (!sel) return;
  while (sel.firstChild) sel.removeChild(sel.firstChild);

  // Promote to multi-select with a visible size.
  sel.setAttribute("multiple", "");
  sel.setAttribute("size", "4");

  const cfg = api.getConfig() || {};
  const langs = cfg.lang_options || [];

  const selected = Array.isArray(state.lang) ? state.lang : (state.lang ? [state.lang] : []);

  // C.15: the server already prepends an "all" sentinel (value="all" or value="").
  // Add our own "All Languages" option only when the server does NOT provide one.
  const serverHasAll = langs.some(l => l.value === "all" || l.value === "" || l.value == null);
  if (!serverHasAll) {
    const allOpt = document.createElement("option");
    allOpt.value = "";
    allOpt.textContent = t("labels.searchoptions_all_langs");
    allOpt.selected = selected.length === 0;
    sel.appendChild(allOpt);
  }

  for (const lang of langs) {
    const rawVal = lang.value != null ? lang.value : "";
    // Treat server's "all" sentinel as the empty-state — map to value="" so it
    // behaves the same as our locally-added "All Languages" option.
    const optVal = rawVal === "all" ? "" : rawVal;
    const opt = document.createElement("option");
    opt.value = optVal;
    if (rawVal === "all" || rawVal === "") {
      opt.textContent = t("labels.searchoptions_all_langs");
      opt.selected = selected.length === 0;
    } else {
      // Try fess_label-compatible key first, fall back to raw value
      const labelKey = lang.label_key || ("labels.lang_" + rawVal);
      opt.textContent = t(labelKey) !== labelKey ? t(labelKey) : (lang.label || rawVal || "");
      opt.selected = selected.includes(optVal);
    }
    sel.appendChild(opt);
  }
}

/**
 * Task 3.4 — Build the label filter dropdown (checkbox list) using createElement only.
 * label_options: [{ value, label }] from api config.
 */
function renderLabelOptions() {
  const wrap = document.getElementById("label-dropdown-wrap");
  const menu = document.getElementById("label-dropdown-menu");
  if (!wrap || !menu) return;
  const cfg = api.getConfig() || {};
  const labelOpts = cfg.label_options || [];

  if (labelOpts.length === 0) {
    wrap.classList.add("d-none");
    return;
  }
  wrap.classList.remove("d-none");

  while (menu.firstChild) menu.removeChild(menu.firstChild);

  const selected = (state.fields.label || []);
  for (const lo of labelOpts) {
    const li = document.createElement("li");
    li.className = "dropdown-item";

    const label = document.createElement("label");
    label.className = "d-flex align-items-center gap-2 mb-0";
    label.classList.add("cursor-pointer");

    const cb = document.createElement("input");
    cb.type = "checkbox";
    cb.value = lo.value != null ? lo.value : "";
    cb.checked = selected.includes(cb.value);

    const span = document.createElement("span");
    span.textContent = lo.label || lo.value || "";

    label.appendChild(cb);
    label.appendChild(span);
    li.appendChild(label);

    cb.addEventListener("change", () => {
      const arr = state.fields.label ? [...state.fields.label] : [];
      if (cb.checked) {
        if (!arr.includes(cb.value)) arr.push(cb.value);
      } else {
        const i = arr.indexOf(cb.value);
        if (i >= 0) arr.splice(i, 1);
      }
      state.fields.label = arr;
      state.start = 0;
      runSearch();
    });

    menu.appendChild(li);
  }
}

/**
 * Parity with searchOptions.jsp — populate the up-front home-view option selects
 * (#home-sort-select / #home-num-select / #home-lang-select / #home-label-select)
 * from the same config that drives the results-view selects.
 *
 * home-lang: multi-select (searchOptions.jsp:75-84 uses multiple="true").
 * home-label: multi-select, shown only when features.display_label_type && label_options non-empty
 *             (parity with searchOptions.jsp:85-97, parity-r3 Task 2).
 */
function renderHomeOptions() {
  const copy = (srcId, destId) => {
    const src = document.getElementById(srcId);
    const dest = document.getElementById(destId);
    if (!src || !dest) return dest;
    // Clear then clone each option (no innerHTML — matches the clear idiom in
    // renderNumOptions/renderLangOptions and avoids the no-unsanitized rule).
    while (dest.firstChild) dest.removeChild(dest.firstChild);
    for (const o of src.options) dest.appendChild(o.cloneNode(true));
    return dest;
  };

  const homeSort = copy("sort-select", "home-sort-select");
  if (homeSort) homeSort.value = state.sort || "";

  const homeNum = copy("num-select", "home-num-select");
  if (homeNum) homeNum.value = String(state.num || 10);

  // Home lang is multi-select (parity with searchOptions.jsp multiple="true").
  // Copy options from the results lang select; restore selected state from state.lang.
  const homeLang = copy("lang-select", "home-lang-select");
  if (homeLang) {
    const selected = Array.isArray(state.lang) ? state.lang : (state.lang ? [state.lang] : []);
    for (const o of homeLang.options) {
      o.selected = selected.includes(o.value);
    }
    // Ensure multi-select attributes are present (the HTML already has them,
    // but guard against any copy-induced attribute loss).
    homeLang.setAttribute("multiple", "");
    // Match the size set by renderLangOptions for the results select.
    homeLang.setAttribute("size", "4");
  }

  // Home label multi-select — parity with searchOptions.jsp:85-97.
  // Show only when display_label_type feature is enabled and options are non-empty.
  const cfg = api.getConfig() || {};
  const labelOpts = cfg.label_options || [];
  const showLabel = !!(cfg.features && cfg.features.display_label_type) && labelOpts.length > 0;

  const homeLabelSel = document.getElementById("home-label-select");
  const homeLabelLbl = homeLabelSel && homeLabelSel.previousElementSibling;
  if (homeLabelSel) {
    if (!showLabel) {
      homeLabelSel.classList.add("d-none");
      if (homeLabelLbl && homeLabelLbl.tagName === "LABEL") homeLabelLbl.classList.add("d-none");
    } else {
      homeLabelSel.classList.remove("d-none");
      if (homeLabelLbl && homeLabelLbl.tagName === "LABEL") homeLabelLbl.classList.remove("d-none");

      // Rebuild options from cfg.label_options.
      while (homeLabelSel.firstChild) homeLabelSel.removeChild(homeLabelSel.firstChild);
      const selectedLabels = state.fields.label || [];
      for (const lo of labelOpts) {
        const opt = document.createElement("option");
        opt.value = lo.value != null ? lo.value : "";
        opt.textContent = lo.label || lo.value || "";
        opt.selected = selectedLabels.includes(opt.value);
        homeLabelSel.appendChild(opt);
      }
    }
  }
}

/**
 * (Re-)initialise all search option selects from config. Safe to call multiple times.
 * Listeners are attached once in attach(); this only repopulates the options.
 */
function renderSearchOptions() {
  renderSortOptions();
  renderNumOptions();
  renderLangOptions();
  renderLabelOptions();
  // Mirror the rendered options into the home-view option panel.
  renderHomeOptions();
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * Trigger a fresh search with the current state without registering additional
 * event listeners. Safe to call from app.js after auth changes.
 */
export function refresh() {
  runSearch();
}

/**
 * C.16: Keep header and home search inputs in sync.
 * Call with the new query value whenever a view transition changes the canonical query.
 */
function syncSearchInputs(q) {
  const header = document.getElementById("search-input");
  const home   = document.getElementById("home-search-input");
  if (header) header.value = q;
  if (home)   home.value   = q;
}

/**
 * Parity with index.jsp — apply the up-front home-view option selections onto a
 * search query string. Reads #home-sort-select / #home-num-select /
 * #home-lang-select and appends the corresponding params (sort / num / lang) so
 * the executed search honours the home panel choices. Only non-empty values are
 * applied so an unset option never clobbers the server defaults. The resulting
 * params flow through runFromUrl(), which already parses sort / num / lang.
 *
 * @param {URLSearchParams} params query params to mutate in place
 * @returns {URLSearchParams} the same params object, for chaining
 */
export function applyHomeOptions(params) {
  const sortSel = document.getElementById("home-sort-select");
  if (sortSel && sortSel.value) params.set("sort", sortSel.value);

  const numSel = document.getElementById("home-num-select");
  if (numSel && numSel.value) params.set("num", numSel.value);

  // home-lang is multi-select (parity with searchOptions.jsp multiple="true");
  // collect all selected options and emit repeated lang= params.
  const langSel = document.getElementById("home-lang-select");
  if (langSel) {
    const selectedLangs = Array.from(langSel.selectedOptions).map(o => o.value).filter(v => v !== "");
    if (selectedLangs.length > 0) {
      params.delete("lang");
      selectedLangs.forEach(v => params.append("lang", v));
    }
  }

  // home-label multi-select — carry selected labels as fields.label params
  // (parity with searchOptions.jsp:85-97, parity-r3 Task 2).
  const labelSel = document.getElementById("home-label-select");
  if (labelSel && !labelSel.classList.contains("d-none")) {
    const selectedLabels = Array.from(labelSel.selectedOptions).map(o => o.value).filter(v => v !== "");
    if (selectedLabels.length > 0) {
      params.delete("fields.label");
      selectedLabels.forEach(v => params.append("fields.label", v));
    }
  }

  return params;
}

/**
 * A.8: Read URL search parameters into state and run a fresh search.
 * Called by app.js on every route dispatch (including popstate / back-forward)
 * so the results always reflect the current URL.
 */
export function runFromUrl() {
  const params = new URLSearchParams(location.search);
  const q = params.get("q");
  // Only run a search when a query is present in the URL.
  if (!q) return;
  state.q = q;
  state.start = Number(params.get("start")) || 0;
  const numVal = Number(params.get("num"));
  if (numVal > 0) state.num = numVal;
  state.sort = params.get("sort") || "";
  // C.16: sync both inputs to reflect the URL query.
  syncSearchInputs(state.q);
  // GEO-1: hydrate geo state from URL params
  const geoPoint = params.get("geo.location.point") || "";
  const geoDistance = params.get("geo.location.distance") || "";
  if (geoPoint && geoDistance) {
    const [lat, lon] = geoPoint.split(",");
    state.geo = { lat: (lat || "").trim(), lon: (lon || "").trim(), distance: geoDistance.trim() };
    const latEl = document.getElementById("geo-lat"); if (latEl) latEl.value = state.geo.lat;
    const lonEl = document.getElementById("geo-lon"); if (lonEl) lonEl.value = state.geo.lon;
    const distEl = document.getElementById("geo-distance"); if (distEl) distEl.value = state.geo.distance;
  } else { state.geo = { lat: "", lon: "", distance: "" }; }
  // ADV-2: hydrate lang / fields.* / ex_q from URL (forwarded by advance search submit)
  state.lang = params.getAll("lang").filter(v => v !== "");
  state.fields = {};
  for (const [key, value] of params.entries()) {
    if (key.startsWith("fields.") && value !== "") {
      const field = key.slice("fields.".length);
      (state.fields[field] = state.fields[field] || []).push(value);
    }
  }
  state.exQ = params.getAll("ex_q").filter(v => v !== "");
  runSearch();
}

function ensureOsddLink() {
  const cfg = api.getConfig();
  if (!cfg) return;
  if (document.querySelector('link[rel="search"]')) return;
  const link = document.createElement("link");
  link.setAttribute("rel", "search");
  link.setAttribute("type", "application/opensearchdescription+xml");
  link.setAttribute("title", cfg.site_name || "Fess");
  link.setAttribute("href", "/osdd");
  document.head.appendChild(link);
}

export function attach() {
  if (attached) {
    console.warn("search already attached — skipping duplicate attach()");
    return;
  }
  attached = true;
  ensureOsddLink();
  ensureRouteListener(); // H.4: cancel suggest timer when navigating away
  const form = document.getElementById("search-form");
  const input = document.getElementById("search-input");
  const dropdown = document.getElementById("suggest-dropdown");
  if (form) {
    form.addEventListener("submit", ev => {
      ev.preventDefault();
      state.q = input.value.trim();
      state.start = 0;
      hideSuggest();
      // C.16: keep home-search-input in sync when submitting from the header
      syncSearchInputs(state.q);
      runSearch();
      // JSP parity: disable the submit button for 3s after the search has been
      // triggered, to prevent rapid double-submits.
      disableSubmitBriefly(document.getElementById("search-submit"));
    });
  }
  if (input) {
    input.addEventListener("input", () => {
      if (suggestTimer) clearTimeout(suggestTimer);
      const v = input.value.trim();
      suggestTimer = setTimeout(() => showSuggest(v), 150);
    });
    input.addEventListener("keydown", ev => {
      const items = dropdown.querySelectorAll(".list-group-item");
      if (!items.length || dropdown.classList.contains("d-none")) return;
      if (ev.key === "ArrowDown") {
        ev.preventDefault();
        suggestIndex = suggestIndex >= items.length - 1 ? 0 : suggestIndex + 1;
      } else if (ev.key === "ArrowUp") {
        ev.preventDefault();
        suggestIndex = suggestIndex <= 0 ? items.length - 1 : suggestIndex - 1;
      } else if (ev.key === "Enter" && suggestIndex >= 0) {
        ev.preventDefault();
        input.value = items[suggestIndex].dataset.text;
        hideSuggest();
        form.dispatchEvent(new Event("submit"));
        return;
      } else if (ev.key === "Tab" && suggestIndex >= 0) {
        // H.3: Google-style Tab-to-accept — commit the highlighted suggestion
        // and prevent Tab from moving focus away.
        ev.preventDefault();
        input.value = items[suggestIndex].dataset.text;
        hideSuggest();
        form.dispatchEvent(new Event("submit"));
        return;
      } else if (ev.key === "Escape") { hideSuggest(); return; }
      items.forEach((it, i) => {
        const active = i === suggestIndex;
        it.classList.toggle("active", active);
        it.setAttribute("aria-selected", active ? "true" : "false");
      });
      if (suggestIndex >= 0 && items[suggestIndex]) {
        input.setAttribute("aria-activedescendant", items[suggestIndex].id);
      } else {
        input.removeAttribute("aria-activedescendant");
      }
    });
    input.addEventListener("blur", () => setTimeout(hideSuggest, 150));
  }
  if (dropdown) {
    dropdown.addEventListener("mousedown", ev => {
      const li = ev.target.closest(".list-group-item");
      if (!li) return;
      ev.preventDefault();
      if (input) input.value = li.dataset.text;
      hideSuggest();
      form.dispatchEvent(new Event("submit"));
    });
  }
  const clearBtn = document.getElementById("facet-clear");
  if (clearBtn) clearBtn.addEventListener("click", () => {
    state.facets = {};
    state.fields = {};
    state.facetQueries = [];
    // GEO-1: reset geo state and inputs
    state.geo = { lat: "", lon: "", distance: "" };
    ["geo-lat", "geo-lon", "geo-distance"].forEach(id => { const el = document.getElementById(id); if (el) el.value = ""; });
    // Reset selects: label multi-select deselects all (selectedIndex = -1);
    // sort and num return to their "all / default" first option (selectedIndex = 0).
    const sortSel = document.getElementById("sort-select");
    if (sortSel) { sortSel.selectedIndex = 0; state.sort = sortSel.value || ""; }
    const numSel = document.getElementById("num-select");
    if (numSel) { numSel.selectedIndex = 0; state.num = Number(numSel.value) || 10; }
    const langSel = document.getElementById("lang-select");
    if (langSel) { langSel.selectedIndex = -1; state.lang = []; }
    const labelMenu = document.getElementById("label-dropdown-menu");
    if (labelMenu) {
      labelMenu.querySelectorAll("input[type=checkbox]").forEach(cb => { cb.checked = false; });
    }
    runSearch();
  });

  // GEO-1: geo search apply/clear listeners
  const geoApply = document.getElementById("geo-apply");
  if (geoApply) geoApply.addEventListener("click", () => {
    state.geo = {
      lat: (document.getElementById("geo-lat").value || "").trim(),
      lon: (document.getElementById("geo-lon").value || "").trim(),
      distance: (document.getElementById("geo-distance").value || "").trim()
    };
    state.start = 0; runSearch();
  });
  const geoClear = document.getElementById("geo-clear");
  if (geoClear) geoClear.addEventListener("click", () => {
    state.geo = { lat: "", lon: "", distance: "" };
    ["geo-lat", "geo-lon", "geo-distance"].forEach(id => { const el = document.getElementById(id); if (el) el.value = ""; });
    state.start = 0; runSearch();
  });

  // Sort select — options are populated by renderSortOptions() after config loads
  const sortSelect = document.getElementById("sort-select");
  if (sortSelect) sortSelect.addEventListener("change", () => {
    state.sort = sortSelect.value || "";
    state.start = 0;
    runSearch();
  });

  // Num select
  const numSelect = document.getElementById("num-select");
  if (numSelect) numSelect.addEventListener("change", () => {
    state.num = Number(numSelect.value) || 10;
    state.start = 0;
    runSearch();
  });

  // Lang multi-select: collect all selected option values; when "All" (value="")
  // is selected or nothing is selected, reset to empty array.
  const langSelect = document.getElementById("lang-select");
  if (langSelect) langSelect.addEventListener("change", () => {
    const selected = Array.from(langSelect.selectedOptions).map(o => o.value).filter(v => v !== "");
    state.lang = selected;
    state.start = 0;
    runSearch();
  });

  // Populate search option selects once config is available.
  // api.init() is awaited by app.js before attach() is called, so getConfig()
  // should already be populated, but guard against timing edge cases.
  if (api.getConfig()) {
    renderSearchOptions();
  }

  // A.8: URL-driven search is handled by runFromUrl() called from app.js route
  // handlers on every dispatch (including popstate). attach() only wires DOM
  // listeners and populates the input once — it no longer triggers a search itself.
  const urlQ = new URLSearchParams(location.search).get("q");
  if (urlQ && input) { input.value = urlQ; state.q = urlQ; }
  if (!urlQ) loadPopularWords();
}

/**
 * (Re-)render search option dropdowns. Call this after api.init() completes
 * if attach() ran before config was available.
 */
export function initSearchOptions() {
  renderSearchOptions();
}

async function loadLabels() {
  try {
    const env = await api.get("/labels");
    return env.labels || [];
  } catch { return []; }
}

/**
 * Build a generic facet group for field-value facets (label, dynamic fields).
 * Each entry click toggles the value in state.facets[fieldKey].
 */
function buildFacetGroup(title, entries, fieldKey) {
  const group = el("div", { className: "facet-group" });
  group.appendChild(el("h3", { text: title }));
  entries.forEach(entry => {
    const active = (state.facets[fieldKey] || []).includes(entry.value);
    const item = el("button", {
      className: "facet-item btn btn-link p-0 text-start w-100" + (active ? " active" : ""),
      attrs: { type: "button", "aria-pressed": active ? "true" : "false" }
    });
    item.appendChild(el("span", { text: entry.labelText }));
    if (entry.count != null) {
      item.appendChild(el("span", { className: "badge bg-secondary", text: String(entry.count) }));
    }
    item.addEventListener("click", () => {
      state.facets[fieldKey] = state.facets[fieldKey] || [];
      const arr = state.facets[fieldKey];
      const idx = arr.indexOf(entry.value);
      if (idx >= 0) arr.splice(idx, 1); else arr.push(entry.value);
      state.start = 0;
      runSearch();
    });
    group.appendChild(item);
  });
  return group;
}

/**
 * Render server-driven facet query views (SRCH-4).
 * Consumes cfg.facet_views (group/query definitions) and env.facet_query (counts),
 * zero-suppresses entries with no results, and toggles selections in state.facetQueries.
 *
 * @param {Element} body - the facet-body container element
 * @param {Object} env  - the search response envelope
 */
function renderFacetQueryViews(body, env) {
  const cfg = api.getConfig() || {};
  const views = cfg.facet_views || [];
  const countByValue = {};
  (env.facet_query || []).forEach(fq => { countByValue[fq.value] = fq.count; });
  views.forEach(view => {
    const groupTitleKey = view.group_name || "";
    const title = groupTitleKey.startsWith("labels.") ? t(groupTitleKey) : groupTitleKey;
    const queries = (view.queries || []).filter(qy => Number(countByValue[qy.value]) > 0);
    const group = el("div", { className: "facet-group" });
    group.appendChild(el("h3", { text: title }));
    // JSP parity: a configured facet-query group with no matching results still
    // renders its title plus a muted "not found" line (labels.facet_is_not_found).
    if (queries.length === 0) {
      group.appendChild(el("div", { className: "facet-not-found text-muted", text: t("facet.not_found") }));
      body.appendChild(group);
      return;
    }
    queries.forEach(qy => {
      const active = (state.facetQueries || []).includes(qy.value);
      const item = el("button", {
        className: "facet-item btn btn-link p-0 text-start w-100" + (active ? " active" : ""),
        attrs: { type: "button", "aria-pressed": active ? "true" : "false" }
      });
      const label = qy.label_key && qy.label_key.startsWith("labels.") ? t(qy.label_key) : (qy.label_key || qy.value);
      item.appendChild(el("span", { text: label }));
      item.appendChild(el("span", { className: "badge bg-secondary", text: String(countByValue[qy.value]) }));
      item.addEventListener("click", () => {
        const arr = state.facetQueries ? [...state.facetQueries] : [];
        const i = arr.indexOf(qy.value);
        if (i >= 0) arr.splice(i, 1); else arr.push(qy.value);
        state.facetQueries = arr;
        state.start = 0;
        runSearch();
      });
      group.appendChild(item);
    });
    body.appendChild(group);
  });
}

/**
 * Build the filetype facet group.
 * - If the API response contains a filetype facet field, use its counts.
 * - Otherwise render the static list without counts.
 * Clicking toggles the value in state.fields.filetype (multi-select).
 */
function buildFiletypeFacetGroup(env) {
  const group = el("div", { className: "facet-group" });
  group.appendChild(el("h3", { text: t("labels.facet_filetype_title") }));

  // Try to find counts from API facet_field response
  const facetField = env.facet_field || [];
  const ftField = facetField.find(f => f.name === "filetype");
  const countMap = {};
  if (ftField) {
    (ftField.result || []).forEach(r => { countMap[r.value] = r.count; });
  }

  const selected = state.fields.filetype || [];
  FILETYPE_VALUES.forEach(ftValue => {
    // skip if API returned counts for this field but this value has zero results
    if (ftField && countMap[ftValue] == null) return;

    const active = selected.includes(ftValue);
    const item = el("button", {
      className: "facet-item btn btn-link p-0 text-start w-100" + (active ? " active" : ""),
      attrs: { type: "button", "aria-pressed": active ? "true" : "false" }
    });
    item.appendChild(el("span", { text: t("labels.facet_filetype_" + ftValue) }));
    if (countMap[ftValue] != null) {
      item.appendChild(el("span", { className: "badge bg-secondary", text: String(countMap[ftValue]) }));
    }
    item.addEventListener("click", () => {
      const arr = state.fields.filetype ? [...state.fields.filetype] : [];
      const idx = arr.indexOf(ftValue);
      if (idx >= 0) arr.splice(idx, 1); else arr.push(ftValue);
      state.fields.filetype = arr;
      state.start = 0;
      runSearch();
    });
    group.appendChild(item);
  });
  return group;
}

function renderFacets(env, labels) {
  const body = document.getElementById("facet-body");
  const clearBtn = document.getElementById("facet-clear");
  // Clear existing children without innerHTML = ""
  while (body.firstChild) body.removeChild(body.firstChild);

  // 1. Label facet — built from env.facet_field where name === "label" with per-label counts,
  //    zero-count suppressed, de-duplicated against the /labels list (SRCH-3).
  const facetField = env.facet_field || [];
  const labelValueSet = new Set((labels || []).map(l => l.value));
  const labelTitleByValue = new Map((labels || []).map(l => [l.value, l.label]));
  const labelField = facetField.find(f => f.name === "label");
  if (labelField) {
    const entries = (labelField.result || [])
      .filter(r => Number(r.count) > 0 && labelValueSet.has(r.value))
      .map(r => ({ labelText: labelTitleByValue.get(r.value) || r.value, value: r.value, count: r.count }));
    if (entries.length > 0) {
      body.appendChild(buildFacetGroup(t("labels.facet_label_title"), entries, "label"));
    }
  }

  // 2. Dynamic facet fields from API (excluding filetype and label — rendered separately)
  for (const field of facetField) {
    if (field.name === "filetype" || field.name === "label") continue;
    const entries = (field.result || []).map(r => ({ labelText: r.value, value: r.value, count: r.count }));
    if (entries.length > 0) {
      body.appendChild(buildFacetGroup(field.name, entries, field.name));
    }
  }

  // 3. Server-driven facet query views (timestamp ranges, size ranges, etc.) (SRCH-4)
  renderFacetQueryViews(body, env);

  // 4. Filetype facet
  body.appendChild(buildFiletypeFacetGroup(env));

  // Show clear button if any filter is active
  const anyActive =
    Object.values(state.facets).some(arr => Array.isArray(arr) && arr.length > 0) ||
    Object.values(state.fields).some(arr => Array.isArray(arr) && arr.length > 0) ||
    (Array.isArray(state.facetQueries) && state.facetQueries.length > 0);
  clearBtn.classList.toggle("d-none", !anyActive);
}

/**
 * Render the "Active filters" chip row above the results list.
 * Chips for: label facets, dynamic facets, filetype, facetQueries.
 * Each chip has a remove button that clears that specific filter.
 */
function renderActiveChips() {
  const container = document.getElementById("active-chips");
  if (!container) return;

  // Clear previous chips
  while (container.firstChild) container.removeChild(container.firstChild);

  const chips = [];

  // Build a deduplicated chip list for field-value filters.
  // state.facets and state.fields can both carry the same field (e.g. "label");
  // merging into a Set per field before building chips prevents duplicate entries.
  const chipFieldSets = {};
  for (const [field, values] of Object.entries(state.facets)) {
    (values || []).forEach(v => { (chipFieldSets[field] = chipFieldSets[field] || new Set()).add(v); });
  }
  for (const [field, values] of Object.entries(state.fields)) {
    if (field === "filetype") continue; // filetype handled separately below
    (Array.isArray(values) ? values : []).forEach(v => { (chipFieldSets[field] = chipFieldSets[field] || new Set()).add(v); });
  }

  for (const [field, valueSet] of Object.entries(chipFieldSets)) {
    valueSet.forEach(v => chips.push({
      label: field + ": " + v,
      remove: () => {
        // Remove from whichever store(s) hold this value.
        if (state.facets[field]) {
          const arr = state.facets[field];
          const idx = arr.indexOf(v);
          if (idx >= 0) arr.splice(idx, 1);
        }
        if (Array.isArray(state.fields[field])) {
          const arr = state.fields[field];
          const idx = arr.indexOf(v);
          if (idx >= 0) arr.splice(idx, 1);
        }
        state.start = 0;
        runSearch();
      }
    }));
  }

  // Filetype chips from state.fields.filetype
  (state.fields.filetype || []).forEach(v => chips.push({
    label: t("labels.facet_filetype_title") + ": " + t("labels.facet_filetype_" + v),
    remove: () => {
      const arr = state.fields.filetype ? [...state.fields.filetype] : [];
      const idx = arr.indexOf(v);
      if (idx >= 0) arr.splice(idx, 1);
      state.fields.filetype = arr;
      state.start = 0;
      runSearch();
    }
  }));

  // Facet query view chips (SRCH-4) — derive chip labels from cfg.facet_views value→label
  if (Array.isArray(state.facetQueries) && state.facetQueries.length > 0) {
    const cfg = api.getConfig() || {};
    const views = cfg.facet_views || [];
    // Build a flat value→label map from all facet_views queries
    const facetQueryLabelByValue = new Map();
    views.forEach(view => {
      const groupTitleKey = view.group_name || "";
      const groupTitle = groupTitleKey.startsWith("labels.") ? t(groupTitleKey) : groupTitleKey;
      (view.queries || []).forEach(qy => {
        const qLabel = qy.label_key && qy.label_key.startsWith("labels.") ? t(qy.label_key) : (qy.label_key || qy.value);
        facetQueryLabelByValue.set(qy.value, groupTitle ? groupTitle + ": " + qLabel : qLabel);
      });
    });
    state.facetQueries.forEach(v => chips.push({
      label: facetQueryLabelByValue.get(v) || v,
      remove: () => {
        const arr = state.facetQueries ? [...state.facetQueries] : [];
        const idx = arr.indexOf(v);
        if (idx >= 0) arr.splice(idx, 1);
        state.facetQueries = arr;
        state.start = 0;
        runSearch();
      }
    }));
  }

  if (chips.length === 0) {
    container.classList.add("d-none");
    return;
  }

  container.classList.remove("d-none");

  const label = el("span", { className: "active-chips-label text-muted small me-2", text: t("facet.active_filters") + ":" });
  container.appendChild(label);

  chips.forEach(chip => {
    const badge = el("span", { className: "badge rounded-pill active-chip me-1" });
    badge.appendChild(el("span", { text: chip.label }));

    const btn = el("button", {
      className: "btn-close btn-close-sm active-chip-remove",
      attrs: {
        type: "button",
        "aria-label": t("facet.remove") + " " + chip.label
      }
    });
    btn.addEventListener("click", chip.remove);
    badge.appendChild(btn);
    container.appendChild(badge);
  });
}

/**
 * Unified popular-words renderer — shared by home, empty-state, and results header slots.
 * JSP parity: first 3 words are always visible; index ≥ 3 carry class d-sm-inline-block
 * (hidden on xs, visible on sm+). No fixed upper-limit slice (was slice(0,5)).
 *
 * @param {string[]} words    - array of popular word strings
 * @param {Element}  targetEl - the container element to render into
 */
export function renderPopularWords(words, targetEl) {
  if (!targetEl) return;
  while (targetEl.firstChild) targetEl.removeChild(targetEl.firstChild);
  if (!words || words.length === 0) {
    targetEl.classList.add("d-none");
    return;
  }
  targetEl.classList.remove("d-none");
  const label = el("span", { className: "me-2", text: t("labels.search_popular_word_word") });
  targetEl.appendChild(label);
  words.forEach((w, i) => {
    // data-spa anchor: the router intercepts the click and navigates to
    // /search?q=w, which runFromUrl() turns into a full search (syncs inputs,
    // resets start, pushes history). No inline click handler — parity-r3 review:
    // an inline runSearch() here double-fired the search alongside the router.
    const a = el("a", {
      className: "me-1" + (i >= 3 ? " d-sm-inline-block d-none" : ""),
      text: w,
      attrs: {
        href: "/search?q=" + encodeURIComponent(w),
        "data-spa": ""
      }
    });
    targetEl.appendChild(a);
  });
}

async function loadPopularWords() {
  const target = document.getElementById("popular-words");
  if (!target) return;
  try {
    const env = await api.get("/popular-words");
    const words = env.popular_words || [];
    if (words.length === 0) return;
    target.classList.remove("d-none");
    renderPopularWords(words, target);
  } catch { /* best-effort */ }
}

/**
 * Load popular words and render them into #results-popular-words when
 * features.popular_word is enabled (JSP parity for non-empty results page).
 */
async function loadResultsPopularWords() {
  const target = document.getElementById("results-popular-words");
  if (!target) return;
  const cfg = api.getConfig() || {};
  if (!cfg.features || !cfg.features.popular_word) {
    target.classList.add("d-none");
    return;
  }
  try {
    const env = await api.get("/popular-words");
    const words = env.popular_words || [];
    renderPopularWords(words, target);
  } catch { /* best-effort */ }
}

/**
 * Render related query buttons above the results list (Feature 1).
 * Queries come from /api/v2/related-query (key: `queries`).
 *
 * @param {string[]} queries
 */
function renderRelatedQueries(queries) {
  const container = document.getElementById("related-queries");
  if (!container) return;
  while (container.firstChild) container.removeChild(container.firstChild);
  if (!queries || queries.length === 0) {
    container.classList.add("d-none");
    return;
  }
  container.classList.remove("d-none");
  const label = el("span", { className: "related-queries-label text-muted small me-2", text: t("search.related_queries") + ":" });
  container.appendChild(label);
  queries.forEach(q => {
    const btn = el("a", {
      className: "btn btn-sm btn-outline-secondary me-1 mb-1",
      text: q,
      attrs: { href: "?q=" + encodeURIComponent(q) }
    });
    btn.addEventListener("click", ev => {
      ev.preventDefault();
      const input = document.getElementById("search-input");
      if (input) input.value = q;
      state.q = q;
      state.start = 0;
      runSearch();
    });
    container.appendChild(btn);
  });
}

/**
 * Render related content HTML above the active-chips row (Feature 2).
 * Content comes from /api/v2/related-content (key: `content`).
 * The HTML string is passed through the whitelist sanitizer from format.js.
 *
 * @param {string} html
 */
function renderRelatedContent(html) {
  const container = document.getElementById("related-content");
  if (!container) return;
  while (container.firstChild) container.removeChild(container.firstChild);
  if (!html || !html.trim()) {
    container.classList.add("d-none");
    return;
  }
  container.classList.remove("d-none");
  container.appendChild(sanitizeHtml(html));
}

/**
 * Fetch related queries and content concurrently for the given query string.
 * Aborts on the provided signal so superseded requests are discarded cleanly.
 *
 * @param {string} q - current search query
 * @param {AbortSignal} signal
 */
async function loadRelated(q, signal) {
  if (!q) {
    renderRelatedQueries([]);
    renderRelatedContent("");
    return;
  }
  try {
    const [qEnv, cEnv] = await Promise.all([
      api.get("/related-queries", { q }, { signal }),
      api.get("/related-content", { q }, { signal })
    ]);
    renderRelatedQueries(qEnv.queries || []);
    renderRelatedContent(cEnv.content || "");
  } catch (e) {
    if (e && e.name === "AbortError") return; // superseded — ignore
    // best-effort: hide both sections on error
    renderRelatedQueries([]);
    renderRelatedContent("");
  }
}

/**
 * Bulk-sync favorite state for all result cards in a single request (Feature 5).
 * Calls GET /api/v2/favorites?query_id=<queryId> and updates each card's button.
 * On 401/AUTH_REQUIRED (unauthenticated) the function exits silently.
 *
 * @param {string} queryId
 */
async function syncFavorites(queryId) {
  if (!queryId) return;
  try {
    const env = await api.get("/favorites", { query_id: queryId });
    const favoriteSet = new Set((env.data || []).map(item => String(item.doc_id || item)));
    const list = document.getElementById("results");
    if (!list) return;
    list.querySelectorAll("li.result-card").forEach(li => {
      const btn = li.querySelector(".favorite-btn");
      if (!btn) return;
      const docId = li.dataset.docId;
      if (!docId) return;
      setFavoriteUi(btn, favoriteSet.has(docId), Number(btn.dataset.count) || 0);
    });
  } catch (e) {
    if (e && (e.code === "AUTH_REQUIRED" || e.httpStatus === 401)) return; // unauthenticated — silent
    // other errors: ignore, favorites are best-effort
  }
}

function renderPagination(env) {
  const nav = document.getElementById("pagination-nav");
  const ul = document.getElementById("pagination");
  ul.innerHTML = "";
  // C.6: use prev_page / next_page flags — handles estimated counts correctly
  if (!env.prev_page && !env.next_page) { nav.classList.add("d-none"); return; }
  nav.classList.remove("d-none");

  const buildPageItem = (label, opts) => {
    const extraClass = opts.extraClass ? " " + opts.extraClass : "";
    const li = el("li", { className: "page-item" + (opts.disabled ? " disabled" : "") + (opts.active ? " active" : "") + extraClass });
    const btn = el("button", { className: "page-link", text: label, attrs: { type: "button" } });
    if (opts.onClick) btn.addEventListener("click", opts.onClick);
    li.appendChild(btn);
    return li;
  };

  ul.appendChild(buildPageItem(t("pagination.prev"), {
    disabled: !env.prev_page,
    onClick: () => { if (env.prev_page) { state.start = Math.max(0, state.start - state.num); runSearch(); } }
  }));
  (env.page_numbers || []).forEach(n => {
    // C.6: hide page numbers more than 2 away from current on small screens
    const isFar = Math.abs(n - env.page_number) > 2;
    ul.appendChild(buildPageItem(String(n), {
      active: n === env.page_number,
      extraClass: isFar ? "d-none d-sm-inline-block" : "",
      onClick: () => { state.start = (n - 1) * state.num; runSearch(); }
    }));
  });
  ul.appendChild(buildPageItem(t("pagination.next"), {
    disabled: !env.next_page,
    onClick: () => { if (env.next_page) { state.start = state.start + state.num; runSearch(); } }
  }));
}

async function refreshFavorite(docId, btn) {
  try {
    const env = await api.get("/documents/" + encodeURIComponent(docId) + "/favorite");
    setFavoriteUi(btn, !!env.favorite, env.count || 0);
  } catch (e) {
    if (e.code === "AUTH_REQUIRED" || e.httpStatus === 401) btn.classList.add("d-none");
  }
}

function setFavoriteUi(btn, on, count) {
  btn.setAttribute("aria-pressed", on ? "true" : "false");
  btn.setAttribute("aria-label", on ? t("result.favorite_remove") : t("result.favorite_add"));
  const icon = btn.querySelector("i");
  if (icon) icon.className = on ? "fa fa-star" : "fa fa-star-o";
  btn.dataset.count = String(count);
  // Show or hide the count badge next to the star icon
  let countEl = btn.querySelector(".favorite-count");
  if (count > 0) {
    if (!countEl) {
      countEl = el("span", { className: "favorite-count" });
      btn.appendChild(countEl);
    }
    countEl.textContent = String(count);
  } else if (countEl) {
    btn.removeChild(countEl);
  }
}

async function toggleFavorite(docId, btn) {
  try {
    const env = await api.post("/documents/" + encodeURIComponent(docId) + "/favorite", {});
    setFavoriteUi(btn, !!env.favorite, env.count || 0);
  } catch (e) {
    if (e.code === "AUTH_REQUIRED" || e.httpStatus === 401) {
      if (!window.bootstrap || !bootstrap.Modal) {
        console.warn("[fess] bootstrap not loaded; skipping modal show");
      } else {
        bootstrap.Modal.getOrCreateInstance(document.getElementById("login-modal")).show();
      }
    }
  }
}

// Exported for later tasks (facets, pagination, etc.) to mutate state and re-run.
export const _state = state;
export { runSearch, el, buildResultCard, buildGoUrl, renderSearchOptions, syncSearchInputs, renderPopularWords };
