import * as api from "./api.js";
import { t } from "./i18n.js";
import { formatFileSize, formatDate, renderHighlightedSnippet } from "./format.js";

/** Guard: prevent duplicate event-listener registration on hot-reload. */
let attached = false;

/** AbortController for the most-recent in-flight search; null when idle. */
let currentSearchAbort = null;

const state = {
  q: "",
  start: 0,
  num: 10,
  sort: "",
  lang: "",
  sdh: "",              // similar_docs_hash for similarity search
  facets: {},           // field -> [values]
  fields: {},           // extra field filters (e.g. label)
  timestampRange: "",   // one of: 1day, 1week, 1month, 3month, 6month, 1year, 2year, 3year
  sizeRange: ""         // one of: 0, 1, 2, 3, 4 (index into SIZE_RANGES)
};

// ─── Range facet definitions ──────────────────────────────────────────────────

/** Timestamp range buckets, matching JSP facet queries. */
const TIMESTAMP_RANGES = [
  { key: "1day",   query: "last_modified:[now/d-1d TO *]",  labelKey: "labels.facet_timestamp_1day" },
  { key: "1week",  query: "last_modified:[now/d-7d TO *]",  labelKey: "labels.facet_timestamp_1week" },
  { key: "1month", query: "last_modified:[now/d-1M TO *]",  labelKey: "labels.facet_timestamp_1month" },
  { key: "3month", query: "last_modified:[now/d-3M TO *]",  labelKey: "labels.facet_timestamp_3month" },
  { key: "6month", query: "last_modified:[now/d-6M TO *]",  labelKey: "labels.facet_timestamp_6month" },
  { key: "1year",  query: "last_modified:[now/d-1y TO *]",  labelKey: "labels.facet_timestamp_1year" },
  { key: "2year",  query: "last_modified:[now/d-2y TO *]",  labelKey: "labels.facet_timestamp_2year" },
  { key: "3year",  query: "last_modified:[now/d-3y TO *]",  labelKey: "labels.facet_timestamp_3year" }
];

/** Content-length (size) range buckets. */
const SIZE_RANGES = [
  { key: "0", query: "content_length:[* TO 10240]",           labelKey: "labels.facet_contentLength_0" },
  { key: "1", query: "content_length:[10240 TO 102400]",      labelKey: "labels.facet_contentLength_1" },
  { key: "2", query: "content_length:[102400 TO 1048576]",    labelKey: "labels.facet_contentLength_2" },
  { key: "3", query: "content_length:[1048576 TO 10485760]",  labelKey: "labels.facet_contentLength_3" },
  { key: "4", query: "content_length:[10485760 TO *]",        labelKey: "labels.facet_contentLength_4" }
];

/** Filetype values shown in the static filetype facet group. */
const FILETYPE_VALUES = [
  "html", "msword", "msexcel", "mspowerpoint",
  "odt", "ods", "odp", "pdf", "txt",
  "image", "audio", "video", "archive", "others"
];

// ─────────────────────────────────────────────────────────────────────────────

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

function buildResultCard(d, queryId) {
  const cfg = api.getConfig() || {};
  const features = cfg.features || {};

  const li = el("li", { className: "result-card", dataset: { docId: d.doc_id || "", queryId: queryId || "" } });

  // --- Task 2.2: thumbnail (left side) ---
  if (d.thumbnail && features.thumbnail_enabled) {
    const img = document.createElement("img");
    img.className = "result-thumbnail";
    img.setAttribute("loading", "lazy");
    img.setAttribute("alt", "");
    img.setAttribute("src", safeHref(d.thumbnail));
    img.addEventListener("error", () => { img.style.display = "none"; });
    li.appendChild(img);
  }

  // --- card body (right side) ---
  const body = el("div", { className: "result-card-body" });

  const h2 = el("h2");
  const a = el("a", { text: d.title || d.url || "", attrs: { href: safeHref(d.url_link || d.url) }, dataset: { resultLink: "1" } });
  h2.appendChild(a);
  body.appendChild(h2);

  body.appendChild(el("div", { className: "result-url", text: d.site || d.url || "" }));

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

  // --- result-meta row: cache link, similar docs, host, favorite ---
  const meta = el("div", { className: "result-meta" });

  meta.appendChild(el("span", { className: "text-muted", text: d.host || "" }));

  // --- Task 2.4: cache link — only when has_cache is truthy ---
  if (d.has_cache === "true" || d.has_cache === true) {
    const cacheLink = el("a", {
      className: "text-muted",
      text: t("result.cache"),
      attrs: { href: "/api/v2/cache/" + encodeURIComponent(d.doc_id || ""), target: "_blank", rel: "noopener" }
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
  meta.appendChild(favBtn);

  body.appendChild(meta);
  li.appendChild(body);
  return li;
}

function renderResults(env) {
  const list = document.getElementById("results");
  const meta = document.getElementById("results-meta");
  const empty = document.getElementById("empty-state");
  list.innerHTML = "";   // empty-string literal — clears children, no untrusted data
  const data = env.data || [];
  if (data.length === 0) {
    empty.classList.remove("d-none");
    meta.textContent = "";
    return;
  }
  empty.classList.add("d-none");
  meta.textContent = `${env.record_count} (${env.exec_time}ms)`;
  for (const d of data) list.appendChild(buildResultCard(d, env.query_id));
  list.querySelectorAll("li.result-card").forEach((li, idx) => {
    const link = li.querySelector("a[data-result-link]");
    if (link) link.addEventListener("click", () => logClick(li.dataset.docId, li.dataset.queryId, idx + 1));
  });
  list.querySelectorAll("li.result-card").forEach(li => {
    const btn = li.querySelector(".favorite-btn");
    const docId = li.dataset.docId;
    if (!btn || !docId) return;
    refreshFavorite(docId, btn);
    btn.addEventListener("click", () => toggleFavorite(docId, btn));
  });
}

async function runSearch() {
  // Cancel any in-flight request before issuing a new one.
  if (currentSearchAbort) currentSearchAbort.abort();
  currentSearchAbort = new AbortController();
  const signal = currentSearchAbort.signal;
  try {
    const params = { q: state.q, start: state.start, num: state.num };
    if (state.sort) params.sort = state.sort;
    if (state.lang) params.lang = state.lang;
    if (state.sdh) params.sdh = state.sdh;
    // facet-based field filters
    for (const [field, values] of Object.entries(state.facets)) {
      values.forEach(v => { (params["fields." + field] = params["fields." + field] || []).push(v); });
    }
    // explicit field filters (e.g. label dropdown, filetype)
    for (const [field, values] of Object.entries(state.fields)) {
      if (Array.isArray(values)) {
        values.forEach(v => { (params["fields." + field] = params["fields." + field] || []).push(v); });
      }
    }
    // timestamp range filter — passed as ex_q (API v2 accepts ex_q array)
    if (state.timestampRange) {
      const ts = TIMESTAMP_RANGES.find(r => r.key === state.timestampRange);
      if (ts) {
        params["ex_q"] = params["ex_q"] || [];
        if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
        params["ex_q"].push(ts.query);
      }
    }
    // size range filter — passed as ex_q
    if (state.sizeRange !== "") {
      const sr = SIZE_RANGES.find(r => r.key === state.sizeRange);
      if (sr) {
        params["ex_q"] = params["ex_q"] || [];
        if (!Array.isArray(params["ex_q"])) params["ex_q"] = [params["ex_q"]];
        params["ex_q"].push(sr.query);
      }
    }
    const env = await api.get("/search", params, { signal });
    renderResults(env);
    renderPagination(env);
    const labels = await loadLabels();
    renderFacets(env, labels);
    renderActiveChips();
    document.dispatchEvent(new CustomEvent("fess:search:after", { detail: env }));
  } catch (e) {
    if (e && e.name === "AbortError") return; // request superseded — silently ignore
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
    const env = await api.get("/suggest-words", { q, num: 8 });
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
 * Task 3.3 — Populate the lang <select> from api config lang_options.
 */
function renderLangOptions() {
  const sel = document.getElementById("lang-select");
  if (!sel) return;
  while (sel.firstChild) sel.removeChild(sel.firstChild);
  const cfg = api.getConfig() || {};
  const langs = cfg.lang_options || [];

  // "All languages" option always first
  const allOpt = document.createElement("option");
  allOpt.value = "";
  allOpt.textContent = t("labels.searchoptions_all_langs");
  sel.appendChild(allOpt);

  for (const lang of langs) {
    const opt = document.createElement("option");
    opt.value = lang.value != null ? lang.value : "";
    // Try fess_label-compatible key first, fall back to raw value
    const labelKey = lang.label_key || ("labels.lang_" + lang.value);
    opt.textContent = t(labelKey) !== labelKey ? t(labelKey) : (lang.label || lang.value || "");
    sel.appendChild(opt);
  }
  sel.value = state.lang || "";
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
    label.style.cursor = "pointer";

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
 * (Re-)initialise all search option selects from config. Safe to call multiple times.
 * Listeners are attached once in attach(); this only repopulates the options.
 */
function renderSearchOptions() {
  renderSortOptions();
  renderNumOptions();
  renderLangOptions();
  renderLabelOptions();
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * Trigger a fresh search with the current state without registering additional
 * event listeners. Safe to call from app.js after auth changes.
 */
export function refresh() {
  runSearch();
}

export function attach() {
  if (attached) {
    console.warn("search already attached — skipping duplicate attach()");
    return;
  }
  attached = true;
  const form = document.getElementById("search-form");
  const input = document.getElementById("search-input");
  const dropdown = document.getElementById("suggest-dropdown");
  if (form) {
    form.addEventListener("submit", ev => {
      ev.preventDefault();
      state.q = input.value.trim();
      state.start = 0;
      hideSuggest();
      runSearch();
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
    state.timestampRange = "";
    state.sizeRange = "";
    runSearch();
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

  // Lang select
  const langSelect = document.getElementById("lang-select");
  if (langSelect) langSelect.addEventListener("change", () => {
    state.lang = langSelect.value || "";
    state.start = 0;
    runSearch();
  });

  // Populate search option selects once config is available.
  // api.init() is awaited by app.js before attach() is called, so getConfig()
  // should already be populated, but guard against timing edge cases.
  if (api.getConfig()) {
    renderSearchOptions();
  }

  const urlQ = new URLSearchParams(location.search).get("q");
  if (urlQ && input) { input.value = urlQ; state.q = urlQ; runSearch(); }
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
    const item = el("div", { className: "facet-item" });
    const active = (state.facets[fieldKey] || []).includes(entry.value);
    if (active) item.classList.add("active");
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
 * Build a single-select range facet group (timestamp or size).
 * Clicking the active item deselects; clicking another selects it.
 *
 * @param {string} title - group heading text
 * @param {{ key: string, labelKey: string }[]} ranges - range definitions
 * @param {string} stateKey - "timestampRange" or "sizeRange"
 */
function buildRangeFacetGroup(title, ranges, stateKey) {
  const group = el("div", { className: "facet-group" });
  group.appendChild(el("h3", { text: title }));
  ranges.forEach(range => {
    const item = el("div", { className: "facet-item" });
    if (state[stateKey] === range.key) item.classList.add("active");
    item.appendChild(el("span", { text: t(range.labelKey) }));
    item.addEventListener("click", () => {
      state[stateKey] = state[stateKey] === range.key ? "" : range.key;
      state.start = 0;
      runSearch();
    });
    group.appendChild(item);
  });
  return group;
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

    const item = el("div", { className: "facet-item" });
    if (selected.includes(ftValue)) item.classList.add("active");

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

  // 1. Label facet (from /labels endpoint)
  if (labels.length > 0) {
    const entries = labels.map(l => ({ labelText: l.label, value: l.value }));
    body.appendChild(buildFacetGroup(t("facet.title"), entries, "label"));
  }

  // 2. Dynamic facet fields from API (excluding filetype — handled separately below)
  const facetField = env.facet_field || [];
  for (const field of facetField) {
    if (field.name === "filetype") continue; // rendered in step 4
    const entries = (field.result || []).map(r => ({ labelText: r.value, value: r.value, count: r.count }));
    if (entries.length > 0) {
      body.appendChild(buildFacetGroup(field.name, entries, field.name));
    }
  }

  // 3. Timestamp range facet
  body.appendChild(buildRangeFacetGroup(t("labels.facet_timestamp_title"), TIMESTAMP_RANGES, "timestampRange"));

  // 4. Content-length (size) range facet
  body.appendChild(buildRangeFacetGroup(t("labels.facet_contentLength_title"), SIZE_RANGES, "sizeRange"));

  // 5. Filetype facet
  body.appendChild(buildFiletypeFacetGroup(env));

  // Show clear button if any filter is active
  const anyActive =
    Object.values(state.facets).some(arr => Array.isArray(arr) && arr.length > 0) ||
    Object.values(state.fields).some(arr => Array.isArray(arr) && arr.length > 0) ||
    state.timestampRange !== "" ||
    state.sizeRange !== "";
  clearBtn.classList.toggle("d-none", !anyActive);
}

/**
 * Render the "Active filters" chip row above the results list.
 * Chips for: label facets, dynamic facets, filetype, timestampRange, sizeRange.
 * Each chip has a remove button that clears that specific filter.
 */
function renderActiveChips() {
  const container = document.getElementById("active-chips");
  if (!container) return;

  // Clear previous chips
  while (container.firstChild) container.removeChild(container.firstChild);

  const chips = [];

  // Label / field-value facets from state.facets
  for (const [field, values] of Object.entries(state.facets)) {
    (values || []).forEach(v => chips.push({
      label: field + ": " + v,
      remove: () => {
        const arr = state.facets[field] || [];
        const idx = arr.indexOf(v);
        if (idx >= 0) arr.splice(idx, 1);
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

  // Other field filters (label dropdown from state.fields.label)
  for (const [field, values] of Object.entries(state.fields)) {
    if (field === "filetype") continue; // already handled above
    (values || []).forEach(v => chips.push({
      label: field + ": " + v,
      remove: () => {
        const arr = state.fields[field] ? [...state.fields[field]] : [];
        const idx = arr.indexOf(v);
        if (idx >= 0) arr.splice(idx, 1);
        state.fields[field] = arr;
        state.start = 0;
        runSearch();
      }
    }));
  }

  // Timestamp range chip
  if (state.timestampRange) {
    const ts = TIMESTAMP_RANGES.find(r => r.key === state.timestampRange);
    if (ts) chips.push({
      label: t("labels.facet_timestamp_title") + ": " + t(ts.labelKey),
      remove: () => { state.timestampRange = ""; state.start = 0; runSearch(); }
    });
  }

  // Size range chip
  if (state.sizeRange !== "") {
    const sr = SIZE_RANGES.find(r => r.key === state.sizeRange);
    if (sr) chips.push({
      label: t("labels.facet_contentLength_title") + ": " + t(sr.labelKey),
      remove: () => { state.sizeRange = ""; state.start = 0; runSearch(); }
    });
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

async function loadPopularWords() {
  const target = document.getElementById("popular-words");
  if (!target) return;
  try {
    const env = await api.get("/popular-words");
    const words = env.popular_words || [];
    if (words.length === 0) return;
    target.innerHTML = "";
    target.appendChild(el("p", { className: "mt-3", text: t("search.popular_searches") }));
    words.slice(0, 10).forEach(w => {
      const btn = el("button", {
        className: "btn btn-sm btn-outline-secondary me-1 mb-1",
        text: w,
        attrs: { type: "button" },
        dataset: { popular: w }
      });
      btn.addEventListener("click", () => {
        const input = document.getElementById("search-input");
        input.value = w;
        state.q = w;
        state.start = 0;
        runSearch();
      });
      target.appendChild(btn);
    });
  } catch { /* best-effort */ }
}

function renderPagination(env) {
  const nav = document.getElementById("pagination-nav");
  const ul = document.getElementById("pagination");
  ul.innerHTML = "";
  if (!env.record_count || env.record_count <= state.num) { nav.classList.add("d-none"); return; }
  nav.classList.remove("d-none");

  const buildPageItem = (label, opts) => {
    const li = el("li", { className: "page-item" + (opts.disabled ? " disabled" : "") + (opts.active ? " active" : "") });
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
    ul.appendChild(buildPageItem(String(n), {
      active: n === env.page_number,
      onClick: () => { state.start = (n - 1) * state.num; runSearch(); }
    }));
  });
  ul.appendChild(buildPageItem(t("pagination.next"), {
    disabled: !env.next_page,
    onClick: () => { if (env.next_page) { state.start = state.start + state.num; runSearch(); } }
  }));
}

async function logClick(docId, queryId, rank) {
  if (!docId) return;
  try { await api.post("/click", { doc_id: docId, query_id: queryId || "", rank: rank || 0 }); }
  catch { /* fire-and-forget */ }
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
export { runSearch, el, buildResultCard, refresh, renderSearchOptions };
