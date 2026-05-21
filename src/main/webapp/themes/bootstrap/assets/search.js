import * as api from "./api.js";
import { t } from "./i18n.js";

const state = {
  q: "",
  start: 0,
  num: 10,
  sort: "",
  facets: {} // field -> [values]
};

// XSS-safety: this module builds every result-card DOM node with
// document.createElement + textContent. No untrusted string is ever
// passed to innerHTML.

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
  const li = el("li", { className: "result-card", dataset: { docId: d.doc_id || "", queryId: queryId || "" } });

  const h2 = el("h2");
  const a = el("a", { text: d.title || d.url || "", attrs: { href: d.url_link || d.url || "#" }, dataset: { resultLink: "1" } });
  h2.appendChild(a);
  li.appendChild(h2);

  li.appendChild(el("div", { className: "result-url", text: d.site || d.url || "" }));
  li.appendChild(el("div", { className: "result-snippet", text: d.content_description || d.digest || "" }));

  const meta = el("div", { className: "result-meta" });
  meta.appendChild(el("span", { className: "text-muted", text: d.host || "" }));
  meta.appendChild(el("span", { className: "text-muted", text: d.last_modified || "" }));
  const cacheLink = el("a", {
    className: "text-muted",
    text: t("result.cache"),
    attrs: { href: "/api/v2/cache/" + encodeURIComponent(d.doc_id || ""), target: "_blank", rel: "noopener" }
  });
  meta.appendChild(cacheLink);
  const favBtn = el("button", {
    className: "btn btn-link btn-sm favorite-btn p-0",
    attrs: { type: "button", "aria-pressed": "false", "aria-label": t("result.favorite_add") }
  });
  const favIcon = el("i", { className: "fa fa-star-o", attrs: { "aria-hidden": "true" } });
  favBtn.appendChild(favIcon);
  meta.appendChild(favBtn);

  li.appendChild(meta);
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
}

async function runSearch() {
  try {
    const params = { q: state.q, start: state.start, num: state.num };
    if (state.sort) params.sort = state.sort;
    for (const [field, values] of Object.entries(state.facets)) {
      values.forEach(v => { (params["fields." + field] = params["fields." + field] || []).push(v); });
    }
    const env = await api.get("/search", params);
    renderResults(env);
    document.dispatchEvent(new CustomEvent("fess:search:after", { detail: env }));
  } catch (e) {
    const meta = document.getElementById("results-meta");
    meta.textContent = e.code === "AUTH_REQUIRED" ? t("error.auth_required") : t("error.server");
  }
}

let suggestTimer = null;
let suggestIndex = -1;

function renderSuggestItems(items) {
  const dropdown = document.getElementById("suggest-dropdown");
  dropdown.innerHTML = "";
  items.forEach((it, i) => {
    const li = el("li", {
      className: "list-group-item",
      text: it.text || "",
      attrs: { role: "option" },
      dataset: { idx: i, text: it.text || "" }
    });
    dropdown.appendChild(li);
  });
}

async function showSuggest(q) {
  const dropdown = document.getElementById("suggest-dropdown");
  if (!q || q.length < 2) { dropdown.classList.add("d-none"); dropdown.innerHTML = ""; return; }
  try {
    const env = await api.get("/suggest-words", { q, num: 8 });
    const items = env.suggest_words || [];
    if (items.length === 0) { dropdown.classList.add("d-none"); return; }
    renderSuggestItems(items);
    dropdown.classList.remove("d-none");
    suggestIndex = -1;
  } catch { /* swallow — suggest is best-effort */ }
}

function hideSuggest() {
  const dropdown = document.getElementById("suggest-dropdown");
  dropdown.classList.add("d-none");
  suggestIndex = -1;
}

export function attach() {
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
      if (ev.key === "ArrowDown") { ev.preventDefault(); suggestIndex = Math.min(suggestIndex + 1, items.length - 1); }
      else if (ev.key === "ArrowUp") { ev.preventDefault(); suggestIndex = Math.max(suggestIndex - 1, 0); }
      else if (ev.key === "Enter" && suggestIndex >= 0) { ev.preventDefault(); input.value = items[suggestIndex].dataset.text; hideSuggest(); form.dispatchEvent(new Event("submit")); return; }
      else if (ev.key === "Escape") { hideSuggest(); return; }
      items.forEach((it, i) => it.classList.toggle("active", i === suggestIndex));
    });
    input.addEventListener("blur", () => setTimeout(hideSuggest, 150));
  }
  if (dropdown) {
    dropdown.addEventListener("mousedown", ev => {
      const li = ev.target.closest(".list-group-item");
      if (!li) return;
      ev.preventDefault();
      input.value = li.dataset.text;
      hideSuggest();
      form.dispatchEvent(new Event("submit"));
    });
  }
  const urlQ = new URLSearchParams(location.search).get("q");
  if (urlQ) { input.value = urlQ; state.q = urlQ; runSearch(); }
}

// Exported for later tasks (facets, pagination, etc.) to mutate state and re-run.
export const _state = state;
export { runSearch, el, buildResultCard };
