// SPDX-License-Identifier: Apache-2.0
// Tests for the recent-searches dropdown of the bootstrap theme (search.js
// attachSearchHistory, its wiring into the header search box by attach(), and the
// home view of app.js). api.js, router.js and app.js's other sibling modules are mocked so the config, the auth state, GET
// /search-history and navigate() are controllable; i18n.js runs for real and
// returns its key unchanged (no messages loaded), so assertions match i18n keys.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom, mountBody, setLocation } from "../../helpers/dom.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => null),
  get: vi.fn(async () => ({})),
  post: vi.fn(async () => ({})),
  isAuthenticated: vi.fn(() => false),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  navigate: vi.fn(),
  register: vi.fn(),
  attach: vi.fn(),
  dispatch: vi.fn(),
  currentPath: vi.fn(() => "/"),
  redirect: vi.fn(),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/auth.js", () => ({ attach: vi.fn(async () => null) }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/chat.js", () => ({ attach: vi.fn(), attachStandalone: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/error.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/profile.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/help.js", () => ({ attach: vi.fn(async () => {}) }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/advance.js", () => ({ attach: vi.fn() }));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/cache.js", () => ({ attach: vi.fn() }));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import { navigate } from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import { attachSearchHistory } from "../../../../main/webapp/themes/bootstrap/assets/search.js";

const CFG = {
  features: { search_history: true },
  sort_options: [{ value: "last_modified.desc", label_key: "labels.search_result_sort_last_modified_desc" }],
};

const ENTRIES = [
  {
    q: "fess",
    fields: { label: ["docs", "blog"] },
    ex_q: ["filetype:pdf"],
    sort: "last_modified.desc",
    lang: ["ja"],
    requested_at: "2026-10-01T03:00:00Z",
    hit_count: 42,
  },
  { q: "opensearch", requested_at: "2026-10-01T02:59:00Z", hit_count: 5 },
];

const historyCalls = () => api.get.mock.calls.filter((c) => c[0] === "/search-history");
const options = (dd) => [...dd.querySelectorAll('[role="option"]')];
const key = (input, k) => input.dispatchEvent(new KeyboardEvent("keydown", { key: k, bubbles: true, cancelable: true }));
const mousedown = (node) => node.dispatchEvent(new MouseEvent("mousedown", { bubbles: true, cancelable: true }));
const params = (target) => new URLSearchParams(target.slice(target.indexOf("?") + 1));

function mount() {
  mountBody('<form><input id="hq" type="search"><ul id="hqd" class="list-group d-none" role="listbox"></ul></form>');
  const input = document.getElementById("hq");
  const dd = document.getElementById("hqd");
  attachSearchHistory(input, dd);
  return { input, dd };
}

const click = (input) => input.dispatchEvent(new MouseEvent("click", { bubbles: true }));

/** Click into the box (focus + click, as a user does) and let GET /search-history settle. */
async function open(input) {
  input.focus();
  click(input);
  await vi.advanceTimersByTimeAsync(0);
}

beforeEach(() => {
  vi.useFakeTimers();
  resetDom();
  vi.clearAllMocks();
  api.getConfig.mockReturnValue(CFG);
  api.isAuthenticated.mockReturnValue(true);
  api.get.mockImplementation(async (path) => (path === "/search-history" ? { record_count: 2, data: ENTRIES } : {}));
});
afterEach(() => {
  vi.useRealTimers();
  setLocation("/");
});

describe("attachSearchHistory", () => {
  it("shows the recent searches under the empty box when the user clicks it", async () => {
    const { input, dd } = mount();
    await open(input);

    expect(historyCalls()).toEqual([["/search-history"]]);
    expect(dd.classList.contains("d-none")).toBe(false);
    expect(input.getAttribute("aria-expanded")).toBe("true");
    const heading = dd.querySelector(".search-history-heading");
    expect(heading.textContent).toBe("search_history.title");
    expect(heading.getAttribute("role")).toBe("presentation");
    // No list bullet, and not a choosable .list-group-item for the header suggest handlers.
    expect(heading.classList.contains("list-unstyled")).toBe(true);
    expect(heading.classList.contains("list-group-item")).toBe(false);
    const items = options(dd);
    expect(items.length).toBe(2);
    expect(items.map((li) => li.id)).toEqual(["hq-history-0", "hq-history-1"]);
    expect(items.every((li) => li.classList.contains("list-group-item") && li.getAttribute("aria-selected") === "false")).toBe(true);
  });

  it("renders the query and a muted summary of the conditions as plain text", async () => {
    api.get.mockResolvedValue({ data: [{ ...ENTRIES[0], q: '<img src=x onerror="alert(1)">' }, ENTRIES[1]] });
    const { input, dd } = mount();
    await open(input);

    expect(dd.querySelector("img")).toBeNull();
    const [first, second] = options(dd);
    expect(first.querySelector(".search-history-query").textContent).toBe('<img src=x onerror="alert(1)">');
    const summary = first.querySelector(".search-history-conditions").textContent;
    expect(summary).toContain("docs, blog");
    expect(summary).toContain("filetype:pdf");
    expect(summary).toContain("labels.search_result_sort_last_modified_desc");
    expect(first.querySelector(".search-history-conditions").classList.contains("text-body-secondary")).toBe(true);
    expect(second.querySelector(".search-history-query").textContent).toBe("opensearch");
    expect(second.querySelector(".search-history-conditions")).toBeNull();
  });

  it("re-runs the chosen search with its query and conditions through the router", async () => {
    const { input, dd } = mount();
    await open(input);
    mousedown(options(dd)[0]);

    expect(navigate).toHaveBeenCalledTimes(1);
    const target = navigate.mock.calls[0][0];
    expect(target.startsWith("search?")).toBe(true);
    const p = params(target);
    expect(p.get("q")).toBe("fess");
    expect(p.getAll("fields.label")).toEqual(["docs", "blog"]);
    expect(p.getAll("ex_q")).toEqual(["filetype:pdf"]);
    expect(p.get("sort")).toBe("last_modified.desc");
    expect(p.getAll("lang")).toEqual(["ja"]);
    expect([...p.keys()].sort()).toEqual(["ex_q", "fields.label", "fields.label", "lang", "q", "sort"]);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(dd.children.length).toBe(0);
    expect(input.getAttribute("aria-expanded")).toBe("false");
  });

  it("navigates with only q when the entry has no conditions", async () => {
    const { input, dd } = mount();
    await open(input);
    mousedown(options(dd)[1]);
    expect(navigate).toHaveBeenCalledWith("search?q=opensearch");
  });

  it("moves through the entries with ArrowDown/ArrowUp and runs the highlighted one on Enter", async () => {
    const { input, dd } = mount();
    await open(input);
    const items = options(dd);

    key(input, "ArrowDown");
    expect(items[0].classList.contains("active")).toBe(true);
    expect(items[0].getAttribute("aria-selected")).toBe("true");
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-0");
    key(input, "ArrowDown");
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-1");
    expect(items[0].classList.contains("active")).toBe(false);
    key(input, "ArrowDown"); // wraps to the first entry
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-0");
    key(input, "ArrowUp"); // wraps to the last entry
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-1");

    key(input, "Enter");
    expect(navigate).toHaveBeenCalledWith("search?q=opensearch");
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(input.hasAttribute("aria-activedescendant")).toBe(false);
  });

  it("leaves Enter alone when no entry is highlighted", async () => {
    const { input } = mount();
    await open(input);
    const ev = new KeyboardEvent("keydown", { key: "Enter", bubbles: true, cancelable: true });
    input.dispatchEvent(ev);
    expect(ev.defaultPrevented).toBe(false);
    expect(navigate).not.toHaveBeenCalled();
  });

  it("leaves the keys of an IME composition alone", async () => {
    const { input, dd } = mount();
    await open(input);
    key(input, "ArrowDown");
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-0");
    for (const init of [{ isComposing: true }, { keyCode: 229 }]) {
      for (const k of ["ArrowDown", "Enter", "Escape"]) {
        const ev = new KeyboardEvent("keydown", { key: k, bubbles: true, cancelable: true, ...init });
        input.dispatchEvent(ev);
        expect(ev.defaultPrevented).toBe(false);
      }
    }
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-0");
    expect(dd.classList.contains("d-none")).toBe(false);
    expect(navigate).not.toHaveBeenCalled();
  });

  it("does not open on focus alone (autofocus, router or skip-link focus)", async () => {
    const { input, dd } = mount();
    input.focus();
    await vi.advanceTimersByTimeAsync(0);
    expect(document.activeElement).toBe(input);
    expect(historyCalls().length).toBe(0);
    expect(dd.classList.contains("d-none")).toBe(true);
  });

  it("opens on ArrowDown in the empty box", async () => {
    const { input, dd } = mount();
    input.focus();
    const ev = new KeyboardEvent("keydown", { key: "ArrowDown", bubbles: true, cancelable: true });
    input.dispatchEvent(ev);
    expect(ev.defaultPrevented).toBe(true);
    await vi.advanceTimersByTimeAsync(0);
    expect(historyCalls().length).toBe(1);
    expect(options(dd).length).toBe(2);
    expect(input.hasAttribute("aria-activedescendant")).toBe(false);
    key(input, "ArrowDown"); // the next ArrowDown moves into the list
    expect(input.getAttribute("aria-activedescendant")).toBe("hq-history-0");
  });

  it("does not open on ArrowDown when the box holds text, or when the key was already taken", async () => {
    const { input } = mount();
    input.focus();
    input.value = "fess";
    key(input, "ArrowDown");
    input.value = "";
    const taken = new KeyboardEvent("keydown", { key: "ArrowDown", bubbles: true, cancelable: true });
    taken.preventDefault();
    input.dispatchEvent(taken);
    await vi.advanceTimersByTimeAsync(0);
    expect(historyCalls().length).toBe(0);
  });

  it("closes on Escape and opens again when the box is clicked", async () => {
    const { input, dd } = mount();
    await open(input);
    key(input, "Escape");
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(input.getAttribute("aria-expanded")).toBe("false");

    click(input);
    await vi.advanceTimersByTimeAsync(0);
    expect(dd.classList.contains("d-none")).toBe(false);
    expect(historyCalls().length).toBe(2);
  });

  it("does not fetch again while a request is in flight or the list is open", async () => {
    const { input } = mount();
    input.focus();
    click(input);
    key(input, "ArrowDown"); // request still in flight
    await vi.advanceTimersByTimeAsync(0);
    click(input); // already open
    await vi.advanceTimersByTimeAsync(0);
    expect(historyCalls().length).toBe(1);
  });

  it("does not throw on keys after a late suggest render replaced the entries", async () => {
    const { input, dd } = mount();
    await open(input);
    // A /suggest-words response that was still in flight renders over the history list.
    while (dd.firstChild) dd.removeChild(dd.firstChild);
    const li = document.createElement("li");
    li.className = "list-group-item";
    li.textContent = "stale";
    dd.appendChild(li);
    input.value = "x";
    expect(() => { key(input, "ArrowDown"); key(input, "ArrowUp"); key(input, "Enter"); key(input, "Escape"); }).not.toThrow();
    expect(navigate).not.toHaveBeenCalled();
    expect(dd.textContent).toBe("stale"); // the suggest owns the dropdown now
  });

  it("closes when the box loses focus (outside click)", async () => {
    const { input, dd } = mount();
    await open(input);
    input.blur();
    await vi.advanceTimersByTimeAsync(150);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(dd.children.length).toBe(0);
  });

  it("gives way to the normal suggest as soon as the user types", async () => {
    const { input, dd } = mount();
    await open(input);
    input.value = "f";
    input.dispatchEvent(new Event("input"));
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(dd.children.length).toBe(0);
    expect(dd.querySelector(".search-history-item")).toBeNull();
    key(input, "ArrowDown");
    expect(input.hasAttribute("aria-activedescendant")).toBe(false);
  });

  it("does not open when the user typed before the history arrived", async () => {
    let resolve;
    api.get.mockImplementation(() => new Promise((r) => { resolve = r; }));
    const { input, dd } = mount();
    input.focus();
    click(input);
    input.value = "x";
    resolve({ data: ENTRIES });
    await vi.advanceTimersByTimeAsync(0);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(dd.children.length).toBe(0);
  });

  it("does not open when the box lost focus before the history arrived", async () => {
    let resolve;
    api.get.mockImplementation(() => new Promise((r) => { resolve = r; }));
    const { input, dd } = mount();
    input.focus();
    click(input);
    input.blur();
    resolve({ data: ENTRIES });
    await vi.advanceTimersByTimeAsync(0);
    expect(dd.classList.contains("d-none")).toBe(true);
  });

  it("does not fetch when the box already holds a query", async () => {
    const { input } = mount();
    input.value = "fess";
    await open(input);
    expect(historyCalls().length).toBe(0);
  });

  it("does not fetch when the server turned search history off", async () => {
    api.getConfig.mockReturnValue({ features: { search_history: false } });
    const { input } = mount();
    await open(input);
    api.getConfig.mockReturnValue(null);
    click(input);
    await vi.advanceTimersByTimeAsync(0);
    expect(historyCalls().length).toBe(0);
  });

  it("does not fetch for a guest", async () => {
    api.isAuthenticated.mockReturnValue(false);
    const { input } = mount();
    await open(input);
    expect(historyCalls().length).toBe(0);
  });

  it("stays closed for an empty history", async () => {
    api.get.mockResolvedValue({ record_count: 0, data: [] });
    const { input, dd } = mount();
    await open(input);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(input.getAttribute("aria-expanded")).not.toBe("true");
  });

  it("stays closed and silent when the request fails", async () => {
    api.get.mockRejectedValue(Object.assign(new Error("no user session"), { code: "auth_required" }));
    const { input, dd } = mount();
    await open(input);
    expect(dd.classList.contains("d-none")).toBe(true);
    expect(dd.children.length).toBe(0);
    // The failure does not latch: the next click asks again.
    api.get.mockResolvedValue({ data: ENTRIES });
    input.blur();
    await open(input);
    expect(dd.classList.contains("d-none")).toBe(false);
  });

  it("is a no-op when the input or the dropdown is missing", () => {
    expect(() => attachSearchHistory(null, document.createElement("ul"))).not.toThrow();
    expect(() => attachSearchHistory(document.createElement("input"), null)).not.toThrow();
  });
});

// attach() wires the header box once per module instance, so this runs against a fresh one.
describe("header search box", () => {
  const HEADER_FIXTURE = `
    <form id="search-form">
      <input id="query" type="search" aria-expanded="false" aria-controls="suggest-dropdown">
      <button id="searchButton" type="submit"></button>
      <ul id="suggest-dropdown" class="list-group d-none" role="listbox"></ul>
    </form>`;

  async function attachHeader() {
    vi.resetModules();
    const freshApi = await import("../../../../main/webapp/themes/bootstrap/assets/api.js");
    const freshRouter = await import("../../../../main/webapp/themes/bootstrap/assets/router.js");
    const fresh = await import("../../../../main/webapp/themes/bootstrap/assets/search.js");
    freshApi.getConfig.mockReturnValue(CFG);
    freshApi.isAuthenticated.mockReturnValue(true);
    freshApi.get.mockImplementation(async (path) => {
      if (path === "/search-history") return { data: ENTRIES };
      if (path === "/suggest-words") return { suggest_words: [{ text: "sug1" }, { text: "sug2" }] };
      return {};
    });
    mountBody(HEADER_FIXTURE);
    fresh.attach();
    return {
      api: freshApi,
      navigate: freshRouter.navigate,
      input: document.getElementById("query"),
      dd: document.getElementById("suggest-dropdown"),
      button: document.getElementById("searchButton"),
    };
  }

  it("shows the history in the header suggest dropdown and keeps the header form out of the way", async () => {
    const h = await attachHeader();
    await open(h.input);
    expect(options(h.dd).length).toBe(2);
    expect(h.dd.querySelector(".search-history-heading")).not.toBeNull();

    // Enter on a highlighted entry runs that search, not the header form submit.
    key(h.input, "ArrowDown");
    expect(h.input.getAttribute("aria-activedescendant")).toBe("query-history-0");
    key(h.input, "Enter");
    expect(h.navigate).toHaveBeenCalledTimes(1);
    expect(params(h.navigate.mock.calls[0][0]).getAll("fields.label")).toEqual(["docs", "blog"]);
    expect(h.button.disabled).toBe(false);
    expect(h.input.value).toBe("");

    // A click on an entry does the same.
    click(h.input);
    await vi.advanceTimersByTimeAsync(0);
    mousedown(options(h.dd)[1]);
    expect(h.navigate).toHaveBeenCalledTimes(2);
    expect(h.navigate.mock.calls[1][0]).toBe("search?q=opensearch");
    expect(h.button.disabled).toBe(false);
  });

  it("opens on ArrowDown in the empty header box alongside the header suggest keys", async () => {
    const h = await attachHeader();
    h.input.focus();
    await vi.advanceTimersByTimeAsync(0);
    expect(h.api.get).not.toHaveBeenCalled(); // focus alone stays quiet
    key(h.input, "ArrowDown");
    await vi.advanceTimersByTimeAsync(0);
    expect(options(h.dd).length).toBe(2);
    key(h.input, "ArrowDown");
    expect(h.input.getAttribute("aria-activedescendant")).toBe("query-history-0");
  });

  it("lets the header suggest keys work again after a late suggest render replaced the history", async () => {
    const h = await attachHeader();
    let resolveSuggest;
    h.api.get.mockImplementation((path) => {
      if (path === "/search-history") return Promise.resolve({ data: ENTRIES });
      if (path === "/suggest-words") return new Promise((r) => { resolveSuggest = r; });
      return Promise.resolve({});
    });
    h.input.focus();
    h.input.value = "he";
    h.input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150); // suggest request now in flight
    h.input.value = "";
    click(h.input);
    await vi.advanceTimersByTimeAsync(0);
    expect(options(h.dd).length).toBe(2); // history shown
    resolveSuggest({ suggest_words: [{ text: "sug1" }] });
    await vi.advanceTimersByTimeAsync(0);
    expect(h.dd.querySelector(".search-history-item")).toBeNull();

    expect(() => key(h.input, "ArrowDown")).not.toThrow();
    expect(h.input.getAttribute("aria-activedescendant")).toBe("suggest-item-0");
    expect(h.api.get.mock.calls.filter((c) => c[0] === "/search-history").length).toBe(1);
  });

  it("switches back to the normal suggest when the user types", async () => {
    const h = await attachHeader();
    await open(h.input);
    h.input.value = "he";
    h.input.dispatchEvent(new Event("input"));
    await vi.advanceTimersByTimeAsync(150);

    expect(h.dd.querySelector(".search-history-heading")).toBeNull();
    expect([...h.dd.querySelectorAll(".list-group-item")].map((li) => li.textContent)).toEqual(["sug1", "sug2"]);
    key(h.input, "ArrowDown");
    expect(h.input.getAttribute("aria-activedescendant")).toBe("suggest-item-0");
  });
});

// The home view autofocuses its box on every visit; that focus must not open the list.
describe("home view (app.js)", () => {
  it("does not fetch the history when the home view autofocuses the box, but does on a click", async () => {
    vi.resetModules();
    const freshApi = await import("../../../../main/webapp/themes/bootstrap/assets/api.js");
    const freshRouter = await import("../../../../main/webapp/themes/bootstrap/assets/router.js");
    freshApi.getConfig.mockReturnValue(CFG);
    freshApi.isAuthenticated.mockReturnValue(true);
    freshApi.get.mockImplementation(async (path) => (path === "/search-history" ? { data: ENTRIES } : {}));
    // Keep app.js from running main() at import time (see app.test.js).
    const readyState = Object.getOwnPropertyDescriptor(Document.prototype, "readyState");
    Object.defineProperty(document, "readyState", { configurable: true, get: () => "loading" });
    let app;
    try {
      app = await import("../../../../main/webapp/themes/bootstrap/assets/app.js");
    } finally {
      delete document.readyState;
      expect(Object.getOwnPropertyDescriptor(Document.prototype, "readyState")).toEqual(readyState);
    }
    mountBody(`
      <section id="home-view" hidden>
        <form id="home-search-form">
          <input id="contentQuery" type="search">
          <ul id="home-suggest-dropdown" class="list-group d-none" role="listbox"></ul>
        </form>
        <div id="home-flash"></div>
      </section>`);
    app.registerRoutes();
    freshRouter.register.mock.calls[1][1](); // the home route (index 0 is the error-meta route)
    await vi.advanceTimersByTimeAsync(0);

    const input = document.getElementById("contentQuery");
    const dd = document.getElementById("home-suggest-dropdown");
    expect(document.activeElement).toBe(input); // autofocused
    expect(freshApi.get.mock.calls.filter((c) => c[0] === "/search-history").length).toBe(0);
    expect(dd.classList.contains("d-none")).toBe(true);

    click(input);
    await vi.advanceTimersByTimeAsync(0);
    expect(dd.querySelectorAll(".search-history-item").length).toBe(2);
  });
});
