import * as api from "./api.js";
import { t, languageLabel } from "./i18n.js";
import { escapeHtml, formatFileSize, formatDate, renderHighlightedSnippet, sanitizeHtml } from "./format.js";
import { navigate } from "./router.js";

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
 * Copy text to the clipboard with a fallback for non-secure contexts.
 * navigator.clipboard is undefined on plain-HTTP non-localhost origins
 * (e.g. http://host:8080), so fall back to a hidden textarea + execCommand.
 *
 * @param {string} text
 * @returns {Promise<void>}
 */
function copyToClipboard(text) {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    return navigator.clipboard.writeText(text);
  }
  return new Promise((resolve, reject) => {
    try {
      const ta = document.createElement("textarea");
      ta.value = text;
      ta.className = "chat-clipboard-fallback"; // off-screen, invisible
      document.body.appendChild(ta);
      ta.focus();
      ta.select();
      const ok = document.execCommand("copy");
      document.body.removeChild(ta);
      ok ? resolve() : reject(new Error("copy command rejected"));
    } catch (e) {
      reject(e);
    }
  });
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
  // Tag-parity with searchResults.jsp result item:
  //   li#result{n}
  //     h3.title.text-truncate > a.link[data-uri,data-id,data-order]
  //     div.body > (div.me-3 > a.link.d-none.d-sm-flex > img.thumbnail)? + div.description
  //     div.site.text-truncate > (i.far.fa-copy.url-copy)? + cite
  //     div.more > a
  //     div.info > date + (size) + (cache)
  const cfg = api.getConfig() || {};
  const features = cfg.features || {};
  const idx0 = order - 1;

  const li = el("li", {
    attrs: { id: "result" + idx0 },
    dataset: { docId: d.doc_id || "", queryId: queryId || "" }
  });

  // Build /go/ URL so click-logging + server-side redirect work for all click types.
  const originalUrl = d.url_link || d.url || "";
  const goHref = buildGoUrl(originalUrl, d.doc_id, queryId, order, state.requestedTime);

  // --- h3.title > a.link ---
  const h3 = el("h3", { className: "title text-truncate" });
  const a = el("a", {
    className: "link",
    attrs: {
      href: goHref,
      "data-uri": safeHref(originalUrl) !== "#" ? originalUrl : "",
      "data-id": d.doc_id || "",
      "data-order": String(idx0),
      title: safeHref(originalUrl) !== "#" ? originalUrl : ""
    }
  });
  if (d.content_title) {
    a.innerHTML = renderHighlightedSnippet(d.content_title);
  } else {
    a.textContent = d.title || d.url || "";
  }
  h3.appendChild(a);
  li.appendChild(h3);

  // --- div.body > (thumbnail)? + div.description ---
  const body = el("div", { className: "body" });
  if (d.thumbnail && features.thumbnail_enabled) {
    const thumbWrap = el("div", { className: "me-3" });
    const thumbA = el("a", {
      className: "link d-none d-sm-flex",
      attrs: { href: goHref, "aria-hidden": "true", tabindex: "-1" }
    });
    const img = document.createElement("img");
    img.className = "thumbnail";
    img.setAttribute("loading", "lazy");
    img.setAttribute("alt", "");
    img.setAttribute("src",
      "/thumbnail/?docId=" + encodeURIComponent(d.doc_id || "") +
      "&queryId=" + encodeURIComponent(queryId || ""));
    img.addEventListener("error", () => { thumbWrap.classList.add("d-none"); });
    thumbA.appendChild(img);
    thumbWrap.appendChild(thumbA);
    body.appendChild(thumbWrap);
  }
  const description = el("div", { className: "description" });
  // content_description is server-escaped HTML; digest is the raw index field,
  // so escape it here to meet the contract renderHighlightedSnippet() expects.
  // Without that, a digest like "Michael Froh <msfroh@example.com>" would have
  // the address parsed as a tag and dropped.
  description.innerHTML = renderHighlightedSnippet(
    d.content_description || escapeHtml(d.digest || ""));
  body.appendChild(description);
  li.appendChild(body);

  // --- div.site.text-truncate > (copy icon)? + cite ---
  const site = el("div", { className: "site text-truncate" });
  if (features.clipboard_copy_icon) {
    const rawUrl = d.url_link || d.url || "";
    const copyIcon = el("i", {
      className: "far fa-copy url-copy d-print-none",
      attrs: { "aria-hidden": "true", "data-clipboard-text": rawUrl, role: "button", "aria-label": t("result.copy_url") + ": " + rawUrl }
    });
    copyIcon.addEventListener("click", () => {
      copyToClipboard(rawUrl).then(() => {
        // JSP parity (js/search.js): swap the copy icon to a green checkmark for ~2s.
        copyIcon.classList.remove("url-copy", "far", "fa-copy");
        copyIcon.classList.add("url-copied", "fas", "fa-check");
        copyIcon.setAttribute("aria-label", t("result.copied"));
        setTimeout(() => {
          copyIcon.classList.remove("url-copied", "fas", "fa-check");
          copyIcon.classList.add("url-copy", "far", "fa-copy");
          copyIcon.setAttribute("aria-label", t("result.copy_url") + ": " + rawUrl);
        }, 2000);
      }).catch(() => { /* clipboard not available */ });
    });
    site.appendChild(copyIcon);
    // JSP has whitespace between the copy icon and the cite — keep them from touching.
    site.appendChild(document.createTextNode(" "));
  }
  site.appendChild(el("cite", { text: d.site_path || d.site || d.url || "" }));
  li.appendChild(site);

  // --- div.more > a (hidden on desktop via #result .more{display:none}) ---
  const more = el("div", { className: "more" });
  more.appendChild(el("a", {
    text: t("labels.search_result_more"),
    attrs: { href: "#result" + idx0, "aria-label": t("labels.search_result_more") + " - " + plainTitle(d) }
  }));
  li.appendChild(more);

  // --- div.info > date + (size) + (cache) + (similar) + (favorite) ---
  const info = el("div", { className: "info" });
  const dateStr = formatDate(d.last_modified || d.created);
  if (dateStr) info.appendChild(document.createTextNode(dateStr + " "));

  const appendNbspSpacer = () => {
    info.appendChild(el("div", { className: "d-sm-none" }));
    const sp = el("span", { className: "d-none d-sm-inline-block" });
    sp.appendChild(document.createTextNode(" "));
    info.appendChild(sp);
  };

  const sizeStr = formatFileSize(d.content_length);
  if (sizeStr) {
    appendNbspSpacer();
    info.appendChild(document.createTextNode(sizeStr + " "));
  }

  // cache link
  if (d.has_cache === "true" || d.has_cache === true) {
    appendNbspSpacer();
    const hlParam = state.highlightParams || ("&hq=" + encodeURIComponent(state.q || ""));
    info.appendChild(el("a", {
      className: "cache d-print-none",
      text: t("result.cache"),
      attrs: { href: `/cache/?docId=${encodeURIComponent(d.doc_id || "")}${hlParam}`, target: "_blank", rel: "noopener" }
    }));
  }

  // similar docs link (theme extra; same .info row)
  const simCount = Number(d.similar_docs_count);
  if (simCount > 1) {
    appendNbspSpacer();
    const simLink = el("a", {
      className: "similar d-print-none",
      text: t("result.similar", { count: simCount }),
      attrs: { href: "#" }
    });
    simLink.addEventListener("click", ev => {
      ev.preventDefault();
      state.sdh = d.similar_docs_hash || d.doc_id || "";
      state.start = 0;
      runSearch();
    });
    info.appendChild(simLink);
  }

  // favorite button (theme extra; same .info row). Config flag is features.user_favorite
  // (searchResults.jsp favoriteSupport / FessConfig.isUserFavorite), not features.favorite.
  // The star + favorite count are shown to EVERYONE (guests included) when the feature is on
  // — matching the legacy searchResults.jsp, which renders the star purely on ${favoriteSupport}
  // — so the count acts as a popularity / social-proof signal. Adding a favorite, however,
  // requires login: a guest click hits FavoritePostHandler's AUTH_REQUIRED gate and
  // toggleFavorite() opens the login modal. So gate display only on features.user_favorite; do
  // NOT add an api.isAuthenticated() check here (that would hide the count from guests).
  if (features.user_favorite) {
    // Spacer before the star (searchResults.jsp puts an &nbsp; before the favorite).
    appendNbspSpacer();
    const favBtn = el("button", {
      className: "btn btn-link btn-sm favorite-btn p-0",
      attrs: { type: "button", "aria-pressed": "false", "aria-label": t("result.favorite_add") }
    });
    const favIcon = el("i", { className: "far fa-star", attrs: { "aria-hidden": "true" } });
    favBtn.appendChild(favIcon);
    favBtn.dataset.count = String(d.favorite_count || 0);
    setFavoriteUi(favBtn, false, d.favorite_count || 0);
    info.appendChild(favBtn);
  }

  li.appendChild(info);
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
  const values = { b0: count.toLocaleString(), b1: String(start), b2: String(end), bq: q };
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
    badges.push({ name, targetId: "sortSearchOption" });
  }

  // Num (only show when non-default)
  const defaultNum = cfg.num_options && cfg.num_options.length > 0 ? cfg.num_options[0] : 10;
  if (state.num !== Number(defaultNum)) {
    badges.push({ name: t("search.num_format", { num: state.num }), targetId: "numSearchOption" });
  }

  // Lang
  const selected = Array.isArray(state.lang) ? state.lang : (state.lang ? [state.lang] : []);
  selected.forEach(langVal => {
    const opt = (cfg.lang_options || []).find(o => o.value === langVal);
    const name = opt
      ? (t(opt.label_key || "labels.lang_" + opt.value) || opt.label || langVal)
      : langVal;
    badges.push({ name, targetId: "langSearchOption" });
  });

  // Label field filter
  (state.fields.label || []).forEach(val => {
    const opt = (cfg.label_options || []).find(o => o.value === val);
    const name = opt ? (opt.label || opt.value) : val;
    badges.push({ name, targetId: "labelSearchOption" });
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

/**
 * JSP parity (search.jsp always-visible options bar): populate #options-bar with
 * list-inline items showing current sort / num / lang / label (when enabled).
 * Each value badge is an <a> that toggles the #searchOptions drawer collapse
 * (JSP parity: searchResults.jsp options links open header.jsp #searchOptions).
 */
function renderOptionsBar() {
  const bar = document.getElementById("options-bar");
  if (!bar) return;
  while (bar.firstChild) bar.removeChild(bar.firstChild);

  const cfg = api.getConfig() || {};

  const makeItem = (labelText, valueText) => {
    const li = el("li", { className: "list-inline-item" });
    li.appendChild(document.createTextNode(labelText + " "));
    const a = el("a", {
      className: "badge text-bg-primary text-decoration-none",
      text: valueText,
      attrs: { href: "#searchOptions", "data-bs-toggle": "collapse" }
    });
    li.appendChild(a);
    return li;
  };

  // Sort
  const sortOpts = cfg.sort_options || [];
  let sortLabel;
  if (state.sort) {
    const found = sortOpts.find(o => o.value === state.sort);
    sortLabel = found ? t(found.label_key || found.value || "") : state.sort;
  } else {
    sortLabel = t("search.menu_score");
  }
  bar.appendChild(makeItem(t("search.menu_sort"), sortLabel));

  // Num
  const numNums = cfg.num_options && cfg.num_options.length > 0 ? cfg.num_options : [10, 20, 50];
  const numText = t("search.menu_num_format", { num: state.num || (numNums[0] || 10) });
  bar.appendChild(makeItem(t("search.menu_num"), numText));

  // Lang
  const selected = Array.isArray(state.lang) ? state.lang : (state.lang ? [state.lang] : []);
  let langText;
  if (selected.length === 0) {
    langText = t("search.all");
  } else {
    langText = selected.map(langVal => {
      const opt = (cfg.lang_options || []).find(o => o.value === langVal);
      if (opt) {
        const key = opt.label_key || ("labels.lang_" + langVal);
        const resolved = t(key);
        return (resolved !== key) ? resolved : (opt.label || langVal);
      }
      return langVal;
    }).join(", ");
  }
  bar.appendChild(makeItem(t("search.menu_lang"), langText));

  // Label (only when display_label_type is enabled)
  if (cfg.features && cfg.features.display_label_type) {
    const activeLabels = state.fields.label || [];
    let labelText;
    if (activeLabels.length === 0) {
      labelText = t("search.all");
    } else {
      labelText = activeLabels.map(val => {
        const opt = (cfg.label_options || []).find(o => o.value === val);
        return opt ? (opt.label || opt.value) : val;
      }).join(", ");
    }
    bar.appendChild(makeItem(t("search.menu_labels"), labelText));
  }
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
  // #results-meta is intentionally left empty on success; status line (#results-status)
  // is the canonical count display. Error/network paths below still write to it.
  meta.textContent = "";
  // Reflect the server queryId / requestedTime into the hidden #queryId / #rt
  // fields (JSP parity: searchResults.jsp hidden inputs).
  const queryIdEl = document.getElementById("queryId");
  if (queryIdEl) queryIdEl.value = env.query_id || "";
  const rtEl = document.getElementById("rt");
  if (rtEl) rtEl.value = String(state.requestedTime || "");
  // Pass 1-based order so buildResultCard can embed it in the /go/ URL.
  data.forEach((d, idx) => list.appendChild(buildResultCard(d, env.query_id, idx + 1)));
  list.querySelectorAll("li[data-doc-id]").forEach(li => {
    const btn = li.querySelector(".favorite-btn");
    const docId = li.dataset.docId;
    if (!btn || !docId) return;
    btn.addEventListener("click", () => toggleFavorite(docId, btn, li.dataset.queryId || ""));
  });
  // Bulk-sync the *per-user* favorited state (solid vs outline star) for all result cards in
  // one request (Feature 5). Only logged-in users can own favorites (adding requires login),
  // so gate this on api.isAuthenticated() — a guest has nothing to sync. The star itself and
  // its count are still rendered for guests above; this only flips already-favorited stars to
  // the solid icon for the signed-in user.
  const favEnabled = !!(api.getConfig()?.features?.user_favorite) && api.isAuthenticated();
  if (favEnabled && env.query_id) syncFavorites(env.query_id);
}

/**
 * Toggle the in-flight search loading indicator (#search-loading).
 * Gives sighted users visible feedback during a /search request; cache and chat
 * already have loading states, search did not.
 */
function showSearchLoading(show) {
  const el = document.getElementById("search-loading");
  if (el) el.classList.toggle("d-none", !show);
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
  // Clear any stale error banner from a previous attempt and show the loading indicator.
  const prevErr = document.getElementById("search-error");
  if (prevErr) prevErr.classList.add("d-none");
  showSearchLoading(true);
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
    // Request the same facets the JSP sidebar renders: the "label" field facet plus
    // every configured facet-query view (timestamp / size / filetype ranges). Without
    // these the API returns no facet data, so the query-view groups render empty and
    // the sidebar is effectively dead (JSP parity: query.facet.fields + .queries).
    const cfgFacet = api.getConfig() || {};
    params["facet.field"] = ["label"];
    const facetQueryValues = [];
    (cfgFacet.facet_views || []).forEach(v =>
      (v.queries || []).forEach(qy => { if (qy && qy.value) facetQueryValues.push(qy.value); }));
    if (facetQueryValues.length > 0) params["facet.query"] = facetQueryValues;
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
    renderOptionsBar();
    // Hide inline validation error box on successful results.
    const eb = document.getElementById("search-error");
    if (eb) eb.classList.add("d-none");
    // Fetch related queries and content concurrently (abortable on next search).
    loadRelated(state.q, currentRelatedAbort.signal);
    document.dispatchEvent(new CustomEvent("fess:search:after", { detail: env }));
  } catch (e) {
    if (e && e.name === "AbortError") return; // request superseded — newer request owns the UI
    const errBox = document.getElementById("search-error");
    if (e && (e.code === "invalid_request" || e.code === "INVALID_REQUEST" || e.httpStatus === 400)) {
      if (errBox) { errBox.textContent = e.message || t("error.invalid_request"); errBox.classList.remove("d-none"); }
      else { document.getElementById("results-meta").textContent = e.message || t("error.invalid_request"); }
      return;
    }
    // Network/server/auth failures: surface in the VISIBLE banner too. Previously these wrote
    // only to the screen-reader-only #results-meta sink, so a sighted user saw nothing when a
    // search failed with a 500 or a dropped connection. Fall back to #results-meta when a
    // theme has no visible banner.
    const msg = (e && e.name === "NetworkError") ? t("error.network")
              : (e && e.code === "AUTH_REQUIRED") ? t("error.auth_required")
              : t("error.server");
    if (errBox) { errBox.textContent = msg; errBox.classList.remove("d-none"); }
    else { const meta = document.getElementById("results-meta"); if (meta) meta.textContent = msg; }
  } finally {
    // Only the latest request clears the spinner. If this request was superseded,
    // currentSearchAbort already points at a newer controller, so leave it running.
    if (currentSearchAbort && currentSearchAbort.signal === signal) showSearchLoading(false);
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
  const inp = document.getElementById("query");
  if (!q || q.length < 1) {
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
  const inp = document.getElementById("query");
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
 * @param {{ lang?: string[] | { length: number }, submitOnSelect?: boolean }} [opts] - options;
 *   opts.lang can be a getter; opts.submitOnSelect (opt-in) dispatches a submit event on the
 *   input's form after selection — matches default-JSP suggestor.js and the results-page header
 *   suggest (search.js mousedown handler).  Advanced search does NOT set this flag so it stays
 *   fill-only (other fields would be empty on premature submit).
 */
export function attachSuggest(input, dropdown, opts = {}) {
  if (!input || !dropdown) return;
  let timer = null;
  const clear = () => {
    while (dropdown.firstChild) dropdown.removeChild(dropdown.firstChild);
    dropdown.classList.add("d-none");
    input.setAttribute("aria-expanded", "false");
  };
  const choose = (text) => {
    input.value = text;
    clear();
    // submitOnSelect: opt-in flag — submit the form after filling the input, matching
    // default-JSP suggestor.js and the results-page header suggest behavior.
    // Advanced search does not pass this flag and keeps the fill-only path.
    if (opts.submitOnSelect) {
      const form = input.form || input.closest("form");
      if (form) { form.dispatchEvent(new Event("submit")); }
      return;
    }
    input.focus();
  };
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
  const sel = document.getElementById("sortSearchOption");
  if (!sel) return;
  while (sel.firstChild) sel.removeChild(sel.firstChild);
  const cfg = api.getConfig() || {};
  const rawOpts = cfg.sort_options && cfg.sort_options.length > 0
    ? cfg.sort_options
    : [{ value: "score.desc", label_key: "labels.search_result_sort_score_desc" }];
  // JSP parity (searchOptions.jsp): a single empty-value placeholder heads the
  // sort list, followed by the real sort options. The server's sort_options
  // already supplies a leading value="" entry (labelled "Score"); drop it before
  // prepending the placeholder so the list does not show a duplicate empty
  // option + "Score"/"スコア順" pair.
  const body = (rawOpts.length > 0 && (rawOpts[0].value == null || rawOpts[0].value === ""))
    ? rawOpts.slice(1)
    : rawOpts;
  const opts = [
    { value: "", label_key: "labels.advance_search_sort_default" },
    ...body,
  ];
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
  const sel = document.getElementById("numSearchOption");
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
  const sel = document.getElementById("langSearchOption");
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
      opt.textContent = languageLabel(rawVal, lang.label || rawVal || "");
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
  // Parity with searchOptions.jsp label fieldset: a multi-select #labelSearchOption
  // shown only when display_label_type is enabled and label options exist.
  const sel = document.getElementById("labelSearchOption");
  const fieldset = document.getElementById("labelSearchOptionFieldset");
  if (!sel) return;
  const cfg = api.getConfig() || {};
  const labelOpts = cfg.label_options || [];
  const show = !!(cfg.features && cfg.features.display_label_type) && labelOpts.length > 0;
  if (fieldset) fieldset.classList.toggle("d-none", !show);
  while (sel.firstChild) sel.removeChild(sel.firstChild);
  if (!show) return;

  const selected = state.fields.label || [];
  for (const lo of labelOpts) {
    const opt = document.createElement("option");
    opt.value = lo.value != null ? lo.value : "";
    opt.textContent = lo.label || lo.value || "";
    opt.selected = selected.includes(opt.value);
    sel.appendChild(opt);
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

/**
 * C.16: Keep header and home search inputs in sync.
 * Call with the new query value whenever a view transition changes the canonical query.
 */
function syncSearchInputs(q) {
  const header = document.getElementById("query");
  const home   = document.getElementById("contentQuery");
  if (header) header.value = q;
  if (home)   home.value   = q;
}

/**
 * A.8: Read URL search parameters into state and run a fresh search.
 * Called by app.js on every route dispatch (including popstate / back-forward)
 * so the results always reflect the current URL.
 */
export function runFromUrl() {
  const params = new URLSearchParams(location.search);
  const q = params.get("q");
  state.q = q || "";
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
  // Facet selections (sidebar label facets and facet query views) live only
  // in memory and are never written to the URL. Every navigation re-derives filter
  // state from the URL, so these in-memory stores must be cleared too; otherwise a
  // previously clicked facet survives a search-options submit (which navigates with
  // only fields.* in the URL) and gets merged back into the request, applying both
  // the old facet label and the new one.
  state.facets = {};
  state.facetQueries = [];
  // The similar-docs hash (sdh) is likewise memory-only and merged into the
  // request, so it must be cleared on navigation too.
  state.sdh = "";
  state.fields = {};
  for (const [key, value] of params.entries()) {
    if (key.startsWith("fields.") && value !== "") {
      const field = key.slice("fields.".length);
      (state.fields[field] = state.fields[field] || []).push(value);
    }
  }
  state.exQ = params.getAll("ex_q").filter(v => v !== "");
  // Re-sync the search-options drawer selects (sort / num / lang / label) to the
  // freshly hydrated state. attach() renders them only once, so without this a
  // navigation (link click, back/forward, facet submit) would leave the selects
  // showing stale values. That matters now that the selects are applied on the
  // Search button: a stale displayed value would otherwise be written back into the
  // URL on the next submit, silently reverting the user's actual sort/num/lang.
  // Guarded on config so the option lists exist before we re-render them.
  if (api.getConfig()) {
    renderSearchOptions();
  }
  // Run a search when a keyword OR any active filter is present in the URL (label /
  // other fields, geo, or ex_q). The classic JSP theme issues the request for
  // filter-only URLs such as /search?fields.label=foo, so mirror that here instead
  // of bailing on an empty keyword. sort/num/lang are query modifiers, not a search
  // on their own. Hydrating state above before this check also sets state.q to "" for
  // a filter-only URL, so a previous keyword can't leak into the next search:
  // runSearch() reads state.q, and the option-drawer Search button omits an empty q.
  const hasFields = Object.keys(state.fields).length > 0;
  const hasGeo = !!(state.geo.lat && state.geo.lon && state.geo.distance);
  const hasExQ = state.exQ.length > 0;
  if (!state.q && !hasFields && !hasGeo && !hasExQ) {
    // A blank query with no conditions (e.g. a sort/num/lang-only URL such as
    // /search?num=10) is not a search. JSP parity: SearchAction.doSearch() redirects
    // such requests to the top page via redirectToRoot(), so mirror that here. Without
    // this, the results view keeps showing the PREVIOUS query's results, because the
    // results DOM is re-rendered only when runSearch() runs and neither showView() nor
    // clearSearchState() clears it. Use replace: true so the empty /search entry does
    // not linger in history (matching the server-side redirect).
    navigate("/", { replace: true });
    return;
  }
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
  // Called from both main() (to wire the header form early) and the results route;
  // the second call is expected and idempotent, so return quietly (no warning noise).
  if (attached) {
    return;
  }
  attached = true;
  ensureOsddLink();
  ensureRouteListener(); // H.4: cancel suggest timer when navigating away
  const form = document.getElementById("search-form");
  const input = document.getElementById("query");
  const dropdown = document.getElementById("suggest-dropdown");
  if (form) {
    form.addEventListener("submit", ev => {
      ev.preventDefault();
      const q = input.value.trim();
      // A new query from the header search box starts a fresh search, so any facet
      // filters applied to the previous query are reset (they no longer apply).
      state.facets = {};
      state.fields = {};
      state.facetQueries = [];
      hideSuggest();
      // C.16: keep home-search-input in sync when submitting from the header
      syncSearchInputs(q);
      // Reflect the query in the address bar: build the /search URL from the new
      // query, carrying over current options (num/sort/lang/geo) while dropping the
      // previous query's page offset and facet/field filters, then navigate().
      // navigate() pushes history and runFromUrl() runs the search, so the URL's q=
      // param always matches what was searched (and back/forward works).
      const params = new URLSearchParams(location.search);
      if (q) params.set("q", q); else params.delete("q");
      params.delete("start");
      for (const key of [...params.keys()]) {
        if (key.startsWith("fields.") || key === "ex_q") params.delete(key);
      }
      // JSP parity: apply the current sort / num / lang drawer selections rather
      // than only carrying over the previous URL values, so a manual change to these
      // selects takes effect when the header Search button is pressed (the selects no
      // longer auto-run a search on change). Guarded on config so the selects are
      // populated before we read them.
      if (api.getConfig()) {
        const sortSel = document.getElementById("sortSearchOption");
        if (sortSel) { if (sortSel.value) params.set("sort", sortSel.value); else params.delete("sort"); }
        const numSel = document.getElementById("numSearchOption");
        // Unlike sort, the num select has no empty placeholder option, so its value is always
        // present; there is no empty case to delete here.
        if (numSel && numSel.value) params.set("num", numSel.value);
        const langSel = document.getElementById("langSearchOption");
        if (langSel) {
          params.delete("lang");
          Array.from(langSel.selectedOptions).map(o => o.value).filter(Boolean).forEach(v => params.append("lang", v));
        }
      }
      navigate("/search?" + params.toString());
      // JSP parity: disable the submit button for 3s after the search has been
      // triggered, to prevent rapid double-submits.
      disableSubmitBriefly(document.getElementById("searchButton"));
    });
  }
  // Search-options "Clear" button (searchOptions.jsp #searchOptionsClearButton):
  // reset the drawer option controls (num/sort/lang/label + geo) to their defaults.
  // JSP parity: this is a purely visual reset — it does NOT re-run the search.
  // The reset values are applied on the next Search-button press, matching searchOptions.jsp
  // whose Clear button only resets the select indices and never submits the form. (The geo
  // inputs are cleared visually here too; the geo filter is dropped only when the drawer's own
  // Search button is pressed and reads the now-empty inputs. A header-form submit instead
  // preserves the geo params already in the URL, matching the JSP theme whose header form
  // carries geo forward via hidden inputs.)
  const optClearBtn = document.getElementById("searchOptionsClearButton");
  if (optClearBtn) {
    optClearBtn.addEventListener("click", () => {
      resetOptionsDOM();
    });
  }
  // Search-options drawer "Search" button. It is form="search-form" (the header form),
  // but on the home view the header form is hidden and the query lives in #contentQuery,
  // so a plain submit does nothing. Build the search URL from the active query + drawer
  // options and navigate — works from both the home and results views.
  const optSearchBtn = document.querySelector('#searchOptions button[type="submit"]');
  if (optSearchBtn) {
    optSearchBtn.addEventListener("click", ev => {
      ev.preventDefault();
      const homeActive = !document.getElementById("home-view")?.hasAttribute("hidden");
      const qInput = homeActive ? document.getElementById("contentQuery") : document.getElementById("query");
      const q = ((qInput && qInput.value) || "").trim();
      const params = new URLSearchParams();
      if (q) params.set("q", q);
      const sortSel = document.getElementById("sortSearchOption");
      if (sortSel && sortSel.value) params.set("sort", sortSel.value);
      const numSel = document.getElementById("numSearchOption");
      if (numSel && numSel.value) params.set("num", numSel.value);
      const langSel = document.getElementById("langSearchOption");
      if (langSel) Array.from(langSel.selectedOptions).map(o => o.value).filter(Boolean).forEach(v => params.append("lang", v));
      const labelSel = document.getElementById("labelSearchOption");
      if (labelSel) Array.from(labelSel.selectedOptions).map(o => o.value).filter(Boolean).forEach(v => params.append("fields.label", v));
      // Geo filter (migrated into the drawer): include only when all three inputs
      // are set, matching the geo.location.point/distance pair runSearch() emits.
      const geoLat = (document.getElementById("geo-lat")?.value || "").trim();
      const geoLon = (document.getElementById("geo-lon")?.value || "").trim();
      const geoDist = (document.getElementById("geo-distance")?.value || "").trim();
      if (geoLat && geoLon && geoDist) {
        params.set("geo.location.point", geoLat + "," + geoLon);
        params.set("geo.location.distance", geoDist);
      }
      navigate("/search?" + params.toString());
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
    const sortSel = document.getElementById("sortSearchOption");
    if (sortSel) { sortSel.selectedIndex = 0; state.sort = sortSel.value || ""; }
    const numSel = document.getElementById("numSearchOption");
    if (numSel) { numSel.selectedIndex = 0; state.num = Number(numSel.value) || 10; }
    const langSel = document.getElementById("langSearchOption");
    if (langSel) { langSel.selectedIndex = -1; state.lang = []; }
    const labelSel = document.getElementById("labelSearchOption");
    if (labelSel) { Array.from(labelSel.options).forEach(o => { o.selected = false; }); }
    runSearch();
  });

  // Geo filter apply/clear is handled by the drawer's main Search / Clear buttons
  // (geo inputs were migrated into #searchOptions); no separate geo buttons.

  // JSP parity: the sort / num / lang drawer selects do NOT auto-run a search on
  // change. The default JSP search screen only applies these options when a Search button
  // is pressed, so changing a select here is a no-op until the user submits — either via
  // the header form (#search-form, which reads these selects in its submit handler above)
  // or the drawer's own Search button (#searchOptions button[type="submit"], wired above).
  // Re-introducing a `change -> runSearch()` handler here would resurrect the reported bug
  // where results changed before the Search button was pressed.

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
  // Tag-parity with searchResults.jsp facet group:
  //   ul.list-group.mb-2 > li.list-group-item.text-uppercase(title)
  //                      + li.list-group-item > a > span.badge.rounded-pill.text-bg-secondary.float-end
  const ul = el("ul", { className: "list-group mb-2" });
  ul.appendChild(el("li", { className: "list-group-item text-uppercase", text: title }));
  entries.forEach(entry => {
    const active = (state.facets[fieldKey] || []).includes(entry.value);
    const li = el("li", { className: "list-group-item" + (active ? " active" : "") });
    const a = el("a", { attrs: { href: "#" } });
    a.appendChild(document.createTextNode(entry.labelText + " "));
    if (entry.count != null) {
      a.appendChild(el("span", { className: "badge rounded-pill text-bg-secondary float-end", text: String(entry.count) }));
    }
    a.addEventListener("click", ev => {
      ev.preventDefault();
      state.facets[fieldKey] = state.facets[fieldKey] || [];
      const arr = state.facets[fieldKey];
      const idx = arr.indexOf(entry.value);
      if (idx >= 0) arr.splice(idx, 1); else arr.push(entry.value);
      state.start = 0;
      runSearch();
    });
    li.appendChild(a);
    ul.appendChild(li);
  });
  return ul;
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
    // ul.list-group.mb-2 > li.list-group-item.text-uppercase(title) + entries.
    // A group with no matching results still renders its title li (JSP parity:
    // the sidebar keeps the group header even when empty).
    const ul = el("ul", { className: "list-group mb-2" });
    ul.appendChild(el("li", { className: "list-group-item text-uppercase", text: title }));
    queries.forEach(qy => {
      const active = (state.facetQueries || []).includes(qy.value);
      const li = el("li", { className: "list-group-item" + (active ? " active" : "") });
      const a = el("a", { attrs: { href: "#" } });
      const label = qy.label_key && qy.label_key.startsWith("labels.") ? t(qy.label_key) : (qy.label_key || qy.value);
      a.appendChild(document.createTextNode(label + " "));
      a.appendChild(el("span", { className: "badge rounded-pill text-bg-secondary float-end", text: String(countByValue[qy.value]) }));
      a.addEventListener("click", ev => {
        ev.preventDefault();
        const arr = state.facetQueries ? [...state.facetQueries] : [];
        const i = arr.indexOf(qy.value);
        if (i >= 0) arr.splice(i, 1); else arr.push(qy.value);
        state.facetQueries = arr;
        state.start = 0;
        runSearch();
      });
      li.appendChild(a);
      ul.appendChild(li);
    });
    body.appendChild(ul);
  });
}

function renderFacets(env, labels) {
  const body = document.getElementById("facet-body");
  const clearBtn = document.getElementById("facet-clear");
  if (!body) return;
  // Clear existing children without innerHTML = ""
  while (body.firstChild) body.removeChild(body.firstChild);

  // ZERO-RESULT: hide the whole facet sidebar (desktop aside + mobile filter button)
  // when the search returned no documents — there is nothing to refine, so an empty
  // sidebar is just noise. #facet-body keeps its base "d-none" (mobile-hidden) class;
  // toggling "d-md-block" controls its desktop visibility. The mobile filter toggle
  // (#facet-toggle-wrap) is hidden outright with "d-none".
  const mobileBody = document.getElementById("facet-body-mobile");
  const toggleWrap = document.getElementById("facet-toggle-wrap");
  const hasResults = (env.data || []).length > 0;
  body.classList.toggle("d-md-block", hasResults);
  if (toggleWrap) toggleWrap.classList.toggle("d-none", !hasResults);
  if (!hasResults) {
    if (mobileBody) while (mobileBody.firstChild) mobileBody.removeChild(mobileBody.firstChild);
    if (clearBtn) clearBtn.classList.add("d-none");
    return;
  }

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

  // 3. Server-driven facet query views (timestamp ranges, size ranges, filetype
  //    ranges, etc.) (SRCH-4). The filetype group is one of these query views
  //    (filetype:html, filetype:word, …), matching the JSP sidebar — so there is
  //    no separate field-based filetype group (that produced a duplicate
  //    "ファイル種別" with no counts).
  renderFacetQueryViews(body, env);

  // Show clear button if any filter is active (optional control; may be absent).
  const anyActive =
    Object.values(state.facets).some(arr => Array.isArray(arr) && arr.length > 0) ||
    Object.values(state.fields).some(arr => Array.isArray(arr) && arr.length > 0) ||
    (Array.isArray(state.facetQueries) && state.facetQueries.length > 0);
  if (clearBtn) clearBtn.classList.toggle("d-none", !anyActive);

  // Mirror the rendered facet groups into the mobile offcanvas (#facet-body-mobile).
  // The desktop aside (#facet-body) is the source of truth; clone its <ul> groups and
  // forward clone-anchor clicks to the originals so the offcanvas drives the same
  // searches. Re-rendered after each search (idempotent).
  const mobile = document.getElementById("facet-body-mobile");
  if (mobile) {
    while (mobile.firstChild) mobile.removeChild(mobile.firstChild);
    body.querySelectorAll("ul.list-group").forEach(ul => {
      const clone = ul.cloneNode(true);
      const srcAnchors = ul.querySelectorAll("li.list-group-item a");
      clone.querySelectorAll("li.list-group-item a").forEach((ca, i) => {
        const src = srcAnchors[i];
        if (src) ca.addEventListener("click", ev => { ev.preventDefault(); src.click(); });
      });
      mobile.appendChild(clone);
    });
  }

  // JSP parity (searchResults.jsp): when any facet filter is applied, show a
  // "reset" link at the foot of the facet sidebar that clears all facet selections.
  if (anyActive) {
    const buildReset = () => {
      const wrap = el("div", { className: "d-flex justify-content-end" });
      const link = el("a", {
        className: "btn btn-link btn-sm facet-reset",
        text: t("labels.facet_label_reset"),
        attrs: { href: "#", role: "button" }
      });
      link.addEventListener("click", ev => {
        ev.preventDefault();
        state.facets = {};
        state.fields = {};
        state.facetQueries = [];
        state.start = 0;
        runSearch();
      });
      wrap.appendChild(link);
      return wrap;
    };
    body.appendChild(buildReset());
    if (mobile) mobile.appendChild(buildReset());
  }
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
      const input = document.getElementById("query");
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
    list.querySelectorAll("li[data-doc-id]").forEach(li => {
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
  // Tag-parity with searchResults.jsp pagination:
  //   nav#subfooter.mx-auto > ul.pagination.justify-content-center
  //     > li.page-item[.disabled] > a.page-link > span[aria-hidden] + span.visually-hidden (prev)
  //     > li.page-item[.active] > a.page-link (numbers; far pages get d-none d-sm-inline-block)
  //     > li.page-item > a.page-link > span.visually-hidden + span[aria-hidden] (next)
  const nav = document.getElementById("subfooter");
  const ul = document.getElementById("pagination");
  if (!nav || !ul) return;
  ul.innerHTML = "";
  // C.6: use prev_page / next_page flags — handles estimated counts correctly
  if (!env.prev_page && !env.next_page) { nav.classList.add("d-none"); return; }
  nav.classList.remove("d-none");

  const makeLi = (cls) => el("li", { className: cls });
  const makeLink = () => el("a", { className: "page-link", attrs: { href: "#" } });

  // Navigate to a page and scroll back to the top so the new results start in view.
  const goToPage = (start) => {
    state.start = Math.max(0, start);
    runSearch();
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  // Prev
  {
    const li = makeLi("page-item" + (env.prev_page ? "" : " disabled"));
    li.setAttribute("aria-label", t("pagination.prev"));
    const a = makeLink();
    const s1 = el("span", { attrs: { "aria-hidden": "true" } });
    s1.appendChild(document.createTextNode("«"));
    a.appendChild(s1);
    a.appendChild(document.createTextNode(" "));
    a.appendChild(el("span", { className: "visually-hidden", text: t("pagination.prev") }));
    a.addEventListener("click", ev => {
      ev.preventDefault();
      if (env.prev_page) goToPage(state.start - state.num);
    });
    li.appendChild(a);
    ul.appendChild(li);
  }

  // Page numbers
  (env.page_numbers || []).forEach(n => {
    // page_numbers come back as strings ("1") while page_number is a number (1),
    // so coerce before comparing — otherwise the current page never gets .active.
    const pageNum = Number(n);
    const isFar = Math.abs(pageNum - env.page_number) > 2;
    const li = makeLi("page-item" + (pageNum === env.page_number ? " active" : "") + (isFar ? " d-none d-sm-inline-block" : ""));
    const a = makeLink();
    a.textContent = String(pageNum);
    a.addEventListener("click", ev => { ev.preventDefault(); goToPage((pageNum - 1) * state.num); });
    li.appendChild(a);
    ul.appendChild(li);
  });

  // Next
  {
    const li = makeLi("page-item" + (env.next_page ? "" : " disabled"));
    li.setAttribute("aria-label", t("pagination.next"));
    const a = makeLink();
    a.appendChild(el("span", { className: "visually-hidden", text: t("pagination.next") }));
    a.appendChild(document.createTextNode(" "));
    const s2 = el("span", { attrs: { "aria-hidden": "true" } });
    s2.appendChild(document.createTextNode("»"));
    a.appendChild(s2);
    a.addEventListener("click", ev => {
      ev.preventDefault();
      if (env.next_page) goToPage(state.start + state.num);
    });
    li.appendChild(a);
    ul.appendChild(li);
  }
}

function setFavoriteUi(btn, on, count) {
  btn.setAttribute("aria-pressed", on ? "true" : "false");
  btn.setAttribute("aria-label", on ? t("result.favorite_remove") : t("result.favorite_add"));
  const icon = btn.querySelector("i");
  // Font Awesome 5+: solid star when favorited, regular (outline) when not.
  // (fa-star-o is FA4 syntax and renders nothing with this theme's FA build.)
  if (icon) icon.className = on ? "fas fa-star" : "far fa-star";
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

async function toggleFavorite(docId, btn, queryId) {
  try {
    // #3 (parity js/search.js:137): include query_id so the click is attributed to its query.
    const env = await api.post("/documents/" + encodeURIComponent(docId) + "/favorite", { query_id: queryId || "" });
    setFavoriteUi(btn, !!env.favorite, env.count || 0);
  } catch (e) {
    // 401/AUTH_REQUIRED: adding a favorite requires login (FavoritePostHandler gate).
    // 403: the CSRF gate rejects a stale session-bound token (e.g. the guest session
    // expired between page load and the click) *before* the auth check runs, so a
    // guest click can surface as 403 instead of 401. Logging in through the modal
    // issues a fresh session + token, so treat both the same.
    if (e.code === "AUTH_REQUIRED" || e.httpStatus === 401 || e.httpStatus === 403) {
      if (!window.bootstrap || !bootstrap.Modal) {
        console.warn("[fess] bootstrap not loaded; skipping modal show");
      } else {
        bootstrap.Modal.getOrCreateInstance(document.getElementById("login-modal")).show();
      }
    }
  }
}

/**
 * HOME-RESET: Silently reset drawer option controls (num/sort/lang/label + geo) to
 * their default DOM state without dispatching change events or triggering a re-search.
 * Shared by clearSearchState() and the #searchOptionsClearButton handler (SRCH-6681).
 */
function resetOptionsDOM() {
  // Geo inputs.
  ["geo-lat", "geo-lon", "geo-distance"].forEach(id => { const e = document.getElementById(id); if (e) e.value = ""; });
  // Drawer selects: single → selectedIndex 0; multiple → deselect all.
  ["numSearchOption", "sortSearchOption", "langSearchOption", "labelSearchOption"].forEach(id => {
    const sel = document.getElementById(id);
    if (!sel) return;
    if (sel.multiple) { Array.from(sel.options).forEach(o => { o.selected = false; }); }
    else { sel.selectedIndex = 0; }
  });
}

/**
 * HOME-RESET (SRCH-6681): Authoritatively reset all search state and option controls
 * when navigating back to the home view.  This is a *silent* reset — no change events
 * are dispatched and no re-search is triggered.  app.js calls this from the home route
 * handler so the SPA matches JSP parity (index.jsp is re-rendered on every request).
 *
 * Resets:
 *  - module-level `state` to initial values
 *  - geo inputs (#geo-lat, #geo-lon, #geo-distance)
 *  - drawer selects (#numSearchOption, #sortSearchOption, #langSearchOption, #labelSearchOption)
 *  - both query inputs via syncSearchInputs("") (#query + #contentQuery)
 */
export function clearSearchState() {
  // Reset module-level state to initial values.
  state.q             = "";
  state.start         = 0;
  state.num           = 10;
  state.sort          = "";
  state.lang          = [];
  state.sdh           = "";
  state.facets        = {};
  state.fields        = {};
  state.facetQueries  = [];
  state.exQ           = [];
  state.geo           = { lat: "", lon: "", distance: "" };
  state.requestedTime = 0;
  state.highlightParams = "";

  // Reset DOM controls silently (no change dispatch).
  resetOptionsDOM();

  // Clear both query inputs (header #query + home #contentQuery).
  syncSearchInputs("");
}

// Exported for later tasks (facets, pagination, etc.) to mutate state and re-run.
export const _state = state;
// renderPopularWords is exported inline at its declaration (line ~1515); do NOT
// re-export it here — a duplicate export is a module-level SyntaxError that aborts
// the entire SPA bootstrap (app.js never runs, so the home view never renders).
export { runSearch, el, buildResultCard, buildGoUrl, renderSearchOptions, syncSearchInputs };
