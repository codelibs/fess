// SPDX-License-Identifier: Apache-2.0
// Tests for the per-user tags of the bootstrap theme's search.js (features.user_tag): the tag
// chips and the inline tag editor on the result cards, the "tag" facet group, the fields.tag
// filter and its active-filter badge, and the "My tags" panel. A tag has an id (64 hex, used by
// the tag API) and a value (used by fields.tag); the theme never parses either. Tags of other
// users (mine: false) read as tag.shared_prefix + name. api.js and router.js are mocked so the
// config, the auth state and the API answers are controllable; i18n.js and format.js run for
// real, and i18n.t() returns its key unchanged (no messages loaded), so assertions match keys.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom, mountBody, setLocation } from "../../helpers/dom.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => null),
  get: vi.fn(async () => ({})),
  post: vi.fn(async () => ({})),
  put: vi.fn(async () => ({})),
  del: vi.fn(async () => ({})),
  isAuthenticated: vi.fn(() => false),
  url: vi.fn((path) => "api/v2" + path),
}));
vi.mock("../../../../main/webapp/themes/bootstrap/assets/router.js", () => ({
  navigate: vi.fn(),
}));

import * as api from "../../../../main/webapp/themes/bootstrap/assets/api.js";
import { navigate } from "../../../../main/webapp/themes/bootstrap/assets/router.js";
import { runSearch, runFromUrl, clearSearchState, attach, _state } from "../../../../main/webapp/themes/bootstrap/assets/search.js";

const FIXTURE = `
  <input id="query"><input id="contentQuery">
  <input id="queryId"><input id="rt">
  <div id="search-loading" class="d-none"></div>
  <div id="search-error" class="d-none"></div>
  <ul id="options-bar"></ul>
  <div id="results-status"></div>
  <ul id="results"></ul>
  <div id="results-meta"></div>
  <div id="empty-state" class="d-none"><span id="empty-did-not-match"></span></div>
  <div id="facet-body" class="d-none"></div>
  <div id="facet-body-mobile"></div>
  <div id="active-chips" class="d-none"></div>
  <div id="login-modal"></div>
  <div id="searchOptions"><button type="submit">Search</button></div>
`;

const PREFIX = "tag.shared_prefix";
const hex = (n) => n.toString(16).padStart(64, "0");
// Tag values as the server issues them: b64url(name):b64url(owner). Opaque to the theme.
const V = { a: "YQ:dXNlcg", b: "Yg:b3RoZXI", proposal: "cHJvcG9zYWw:dXNlcg", draft: "ZHJhZnQ:b3RoZXI", old: "b2xk:dXNlcg" };
const ID = { a: hex(0xa), b: hex(0xb), c: hex(0xc) };

function config({ userTag = true } = {}) {
  return { features: { user_tag: userTag } };
}

/** A search hit tag: { value, name, owner, mine, shared } (no id). */
const HIT_TAG = (value, name, mine = true, shared = false) => ({ value, name, owner: mine ? "user" : "other", mine, shared });
/** A DocumentTag of the document tag API: { id, value, name, owner, mine, shared }. */
const DOC_TAG = (id, value, name, mine = true, shared = false) => ({ id, value, name, owner: mine ? "user" : "other", mine, shared });

function searchEnv(docs, tagFacet) {
  return {
    query_id: "qid-1",
    record_count: docs.length,
    data: docs,
    facet_field: [
      { name: "label", result: [] },
      ...(tagFacet ? [{ name: "tag", result: tagFacet }] : []),
    ],
  };
}

let env;
let docTagsAnswer;
let myTagsAnswer;
function installApi() {
  api.get.mockImplementation(async (path) => {
    if (path === "/search") return env;
    if (path === "/labels") return { labels: [] };
    if (path === "/tags") return myTagsAnswer;
    if (path.endsWith("/tags")) return docTagsAnswer;
    return {};
  });
}

const settle = () => new Promise((r) => setTimeout(r));
const searchCalls = () => api.get.mock.calls.filter((c) => c[0] === "/search");
const lastSearchParams = () => searchCalls().at(-1)[1];
const tagGroup = () => [...document.querySelectorAll("#facet-body ul.list-group")]
  .find((ul) => ul.firstChild.textContent === "tag.title");
const badgeTexts = () => [...document.querySelectorAll("#active-chips .active-chip > span")].map((s) => s.textContent);

async function search(docs, tagFacet) {
  env = searchEnv(docs, tagFacet);
  installApi();
  await runSearch();
  await settle();
}

async function searchFromUrl(url, docs, tagFacet) {
  env = searchEnv(docs, tagFacet);
  installApi();
  setLocation(url);
  runFromUrl();
  await settle();
  await settle();
}

let modalShow;
beforeEach(() => {
  resetDom();
  vi.clearAllMocks();
  api.getConfig.mockReturnValue(null);
  clearSearchState();
  mountBody(FIXTURE);
  setLocation("/search?q=foo");
  _state.q = "foo";
  api.getConfig.mockReturnValue(config());
  api.isAuthenticated.mockReturnValue(false);
  docTagsAnswer = { status: 0, doc_id: "d1", tags: [], addable: [] };
  myTagsAnswer = { status: 0, tags: [] };
  modalShow = vi.fn();
  window.bootstrap = { Modal: { getOrCreateInstance: vi.fn(() => ({ show: modalShow })) } };
});
afterEach(() => {
  delete window.bootstrap;
  setLocation("/");
});

describe("feature flag", () => {
  it("renders no tags, no editor, no tag facet and no My tags control when the feature is off", async () => {
    api.getConfig.mockReturnValue(config({ userTag: false }));
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T", tags: [HIT_TAG(V.a, "a")] }], [{ value: V.a, count: 2, label: "a", mine: true }]);

    expect(document.querySelector("#result0 .tags")).toBeNull();
    expect(document.querySelector(".tag-add-btn")).toBeNull();
    expect(document.getElementById("my-tags-toggle")).toBeNull();
    expect(lastSearchParams()["facet.field"]).toEqual(["label"]);
    expect(tagGroup()).toBeUndefined();
    const titles = [...document.querySelectorAll("#facet-body ul.list-group > li:first-child")].map((li) => li.textContent);
    expect(titles).not.toContain("tag");
  });

  it("asks for the tag facet as well when the feature is on", async () => {
    await search([{ doc_id: "d1", title: "T" }]);
    expect(lastSearchParams()["facet.field"]).toEqual(["label", "tag"]);
  });

  it("renders no tag row and no My tags control for a guest", async () => {
    await search([{ doc_id: "d1", title: "T", tags: [] }]);
    expect(document.querySelector("#result0 .tags")).toBeNull();
    expect(document.getElementById("my-tags-toggle")).toBeNull();
  });

  it("offers the add control to a logged-in user even when the document has no tags", async () => {
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T" }]);
    expect(document.querySelector("#result0 .tag-chip")).toBeNull();
    expect(document.querySelector("#result0 .tag-add-btn")).not.toBeNull();
  });
});

describe("result card chips", () => {
  it("shows own tags by name and other users' tags with the shared prefix, as plain text", async () => {
    const evil = '<img src=x onerror="alert(1)">';
    await search([{ doc_id: "d1", title: "T", tags: [HIT_TAG(V.proposal, "proposal", true, true), HIT_TAG(V.b, evil, false, true)] }]);

    const chips = [...document.querySelectorAll("#result0 .tags .tag-chip")];
    expect(chips.map((c) => c.textContent)).toEqual(["proposal", PREFIX + evil]);
    expect(document.querySelector("#result0 .tags img")).toBeNull();
    expect(chips[0].tagName).toBe("BUTTON");
    expect(chips[0].getAttribute("type")).toBe("button");
    expect(document.querySelector("#result0 .tag-add-btn")).toBeNull();
  });

  it("prefixes a tag that is not marked as the caller's own", async () => {
    await search([{ doc_id: "d1", title: "T", tags: [{ value: V.a, name: "x:y" }] }]);
    expect([...document.querySelectorAll("#result0 .tag-chip")].map((c) => c.textContent)).toEqual([PREFIX + "x:y"]);
  });

  it("filters the search by the clicked tag's value, keeping the query, and puts fields.tag in the URL", async () => {
    await search([{ doc_id: "d1", title: "T", tags: [HIT_TAG(V.a, "a b")] }]);
    _state.fields.label = ["lblA"];
    document.querySelector("#result0 .tag-chip").click();
    await settle();

    expect(searchCalls().length).toBe(2);
    const params = lastSearchParams();
    expect(params.q).toBe("foo");
    expect(params["fields.tag"]).toEqual([V.a]);
    expect(params["fields.label"]).toEqual(["lblA"]);
    expect(params.start).toBe(0);
    const qs = new URLSearchParams(location.search);
    expect(qs.getAll("fields.tag")).toEqual([V.a]);
    expect(qs.get("q")).toBe("foo");
  });
});

describe("tag facet", () => {
  const FACET = [
    { value: V.proposal, count: 3, label: "proposal", owner: "user", mine: true, shared: false },
    { value: V.draft, count: 1, label: "draft", owner: "other", mine: false, shared: true },
    { value: V.old, count: 0, label: "old", owner: "user", mine: true, shared: false },
  ];

  it("renders one Tags group with the labels (others' tags prefixed) and counts, without zero counts", async () => {
    await search([{ doc_id: "d1", title: "T" }], FACET);

    const group = tagGroup();
    expect(group).toBeTruthy();
    const items = [...group.querySelectorAll("li.list-group-item a")];
    expect(items.map((a) => a.firstChild.textContent.trim())).toEqual(["proposal", PREFIX + "draft"]);
    expect(items.map((a) => a.querySelector(".badge").textContent)).toEqual(["3", "1"]);
    const titles = [...document.querySelectorAll("#facet-body ul.list-group > li:first-child")].map((li) => li.textContent);
    expect(titles.filter((x) => x === "tag.title" || x === "tag").length).toBe(1);
    expect(document.querySelectorAll("#facet-body-mobile ul.list-group").length).toBe(1);
  });

  it("falls back to the raw value without a label, and renders a label as plain text", async () => {
    const evil = "<b>x</b>";
    await search([{ doc_id: "d1", title: "T" }], [{ value: V.a, count: 2, mine: true }, { value: V.b, count: 1, label: evil, mine: true }]);
    const items = [...tagGroup().querySelectorAll("li.list-group-item a")];
    expect(items.map((a) => a.firstChild.textContent.trim())).toEqual([V.a, evil]);
    expect(tagGroup().querySelector("b")).toBeNull();
  });

  it("filters through fields.tag on click, names the badge with the same prefix rule, and clears on a second click", async () => {
    await search([{ doc_id: "d1", title: "T" }], FACET);
    tagGroup().querySelectorAll("li.list-group-item a")[1].click();
    await settle();

    expect(lastSearchParams()["fields.tag"]).toEqual([V.draft]);
    expect(lastSearchParams()["ex_q"]).toBeUndefined();
    expect(new URLSearchParams(location.search).getAll("fields.tag")).toEqual([V.draft]);
    const active = tagGroup().querySelector("li.list-group-item.active a");
    expect(active.firstChild.textContent.trim()).toBe(PREFIX + "draft");
    expect(badgeTexts()).toEqual([PREFIX + "draft"]);

    active.click();
    await settle();
    expect(lastSearchParams()["fields.tag"]).toBeUndefined();
    expect(new URLSearchParams(location.search).has("fields.tag")).toBe(false);
  });

  it("keeps a selected tag listed even when its count is zero", async () => {
    _state.fields.tag = [V.old];
    await search([{ doc_id: "d1", title: "T" }], FACET);
    const active = tagGroup().querySelector("li.list-group-item.active a");
    expect(active.firstChild.textContent.trim()).toBe("old");
  });
});

describe("fields.tag filter badge", () => {
  it("reads the tag from the URL and names it from the facet and the hits, else shows the raw value", async () => {
    const gone = "Z29uZQ:dXNlcg";
    await searchFromUrl(
      "/search?q=foo&fields.tag=" + V.proposal + "&fields.tag=" + V.b + "&fields.tag=" + gone,
      [{ doc_id: "d1", title: "T", tags: [HIT_TAG(V.b, "beta", false, true)] }],
      [{ value: V.proposal, count: 1, label: "proposal", mine: true }],
    );

    expect(lastSearchParams()["fields.tag"]).toEqual([V.proposal, V.b, gone]);
    expect(badgeTexts()).toEqual(["proposal", PREFIX + "beta", gone]);
  });

  it("drops the tag from the request and the URL when its badge is removed", async () => {
    await searchFromUrl("/search?q=foo&fields.tag=" + V.proposal, [{ doc_id: "d1", title: "T" }]);
    document.querySelector("#active-chips .active-chip-remove").click();
    await settle();

    expect(lastSearchParams()["fields.tag"]).toBeUndefined();
    expect(new URLSearchParams(location.search).has("fields.tag")).toBe(false);
    expect(new URLSearchParams(location.search).get("q")).toBe("foo");
  });

  it("is kept by the search-options Search button", () => {
    setLocation("/search?q=foo&fields.tag=" + V.proposal + "&ex_q=label%3Ax");
    attach();
    document.getElementById("query").value = "foo";
    document.querySelector("#searchOptions button[type=submit]").click();

    const target = navigate.mock.calls.at(-1)[0];
    const qs = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    expect(qs.getAll("fields.tag")).toEqual([V.proposal]);
    expect(qs.getAll("ex_q")).toEqual(["label:x"]);
  });
});

describe("tag editor", () => {
  const card = () => document.getElementById("result0");
  const toggle = () => card().querySelector(".tag-add-btn");
  const editor = () => card().querySelector(".tag-editor");
  const editorItems = () => [...editor().querySelectorAll(".tag-editor-item")];
  const addableButtons = () => [...editor().querySelectorAll(".tag-addable")];
  const input = () => editor().querySelector("input");
  const errorBox = () => editor().querySelector(".tag-editor-error");
  const pending = () => editor().querySelector(".tag-pending");
  const chipTexts = () => [...card().querySelectorAll(".tag-chips .tag-chip")].map((c) => c.textContent);

  async function openEditor(docs = [{ doc_id: "d1", title: "T", tags: [HIT_TAG(V.a, "a")] }]) {
    api.isAuthenticated.mockReturnValue(true);
    await search(docs);
    toggle().click();
    await settle();
  }

  async function add(name) {
    input().value = name;
    editor().querySelector("button[type=submit]").click();
    await settle();
  }

  it("loads the document's tags: others' tags prefixed, a remove button on own ones, own tags offered to add", async () => {
    docTagsAnswer = {
      status: 0, doc_id: "d1",
      tags: [DOC_TAG(ID.a, V.a, "a"), DOC_TAG(ID.b, V.b, "b", false, true)],
      addable: [DOC_TAG(ID.c, "Yw:dXNlcg", "c")],
    };
    await openEditor();

    expect(toggle().getAttribute("aria-expanded")).toBe("true");
    expect(toggle().getAttribute("aria-controls")).toBe(editor().id);
    expect(api.get).toHaveBeenCalledWith("/documents/d1/tags");
    const items = editorItems();
    expect(items.map((li) => li.firstChild.textContent)).toEqual(["a", PREFIX + "b"]);
    expect(items[0].querySelector("button.tag-remove").getAttribute("aria-label")).toBe("tag.remove");
    expect(items[1].querySelector("button.tag-remove")).toBeNull();
    expect(addableButtons().map((b) => b.textContent)).toEqual(["c"]);
    expect(input().getAttribute("maxlength")).toBe("50");
    expect(editor().querySelector(`label[for="${input().id}"]`).textContent).toBe("tag.name");
    expect(document.activeElement).toBe(input());
    expect(pending().classList.contains("d-none")).toBe(true);
    expect(chipTexts()).toEqual(["a", PREFIX + "b"]);
  });

  it("adds one of the user's own tags by id and shows the pending note", async () => {
    docTagsAnswer = { status: 0, doc_id: "d1", tags: [], addable: [DOC_TAG(ID.c, "Yw:dXNlcg", "c")] };
    api.post.mockResolvedValue({ status: 0, doc_id: "d1", added: true, tag: DOC_TAG(ID.c, "Yw:dXNlcg", "c"),
      tags: [DOC_TAG(ID.c, "Yw:dXNlcg", "c")], addable: [] });
    await openEditor();
    const before = searchCalls().length;
    addableButtons()[0].click();
    await settle();

    expect(api.post).toHaveBeenCalledWith("/documents/d1/tags", { id: ID.c });
    expect(chipTexts()).toEqual(["c"]);
    expect(addableButtons().length).toBe(0);
    expect(pending().textContent).toBe("tag.pending");
    expect(pending().classList.contains("d-none")).toBe(false);
    expect(searchCalls().length).toBe(before);
  });

  it("adds a new tag by name, trimmed, and ignores an empty name", async () => {
    await openEditor();
    await add("   ");
    expect(api.post).not.toHaveBeenCalled();

    api.post.mockResolvedValue({ status: 0, doc_id: "d1", added: true, tag: DOC_TAG(ID.c, "x", "new one"),
      tags: [DOC_TAG(ID.c, "x", "new one")], addable: [] });
    await add("  new one ");
    expect(api.post).toHaveBeenCalledWith("/documents/d1/tags", { name: "new one" });
    expect(chipTexts()).toEqual(["new one"]);
    expect(input().value).toBe("");
    expect(document.activeElement).toBe(input());
    expect(pending().classList.contains("d-none")).toBe(false);
  });

  it("removes an own tag through DELETE /documents/{docId}/tags/{id}", async () => {
    docTagsAnswer = { status: 0, doc_id: "d/1", tags: [DOC_TAG(ID.a, V.a, "a b&c")], addable: [] };
    api.del.mockResolvedValue({ status: 0, doc_id: "d/1", removed: true, tags: [], addable: [DOC_TAG(ID.a, V.a, "a b&c")] });
    await openEditor([{ doc_id: "d/1", title: "T", tags: [HIT_TAG(V.a, "a b&c")] }]);
    expect(api.get).toHaveBeenCalledWith("/documents/d%2F1/tags");

    editorItems()[0].querySelector("button.tag-remove").click();
    await settle();

    expect(api.del).toHaveBeenCalledWith("/documents/d%2F1/tags/" + ID.a);
    expect(chipTexts()).toEqual([]);
    expect(editorItems().length).toBe(0);
    expect(addableButtons().map((b) => b.textContent)).toEqual(["a b&c"]);
    expect(editor().querySelector(".tag-editor-empty").classList.contains("d-none")).toBe(false);
    expect(pending().classList.contains("d-none")).toBe(false);
  });

  it("opens the login modal when a write answers 401 or 403", async () => {
    await openEditor();
    api.post.mockRejectedValueOnce({ code: "auth_required", httpStatus: 401, message: "login required" });
    await add("x");
    expect(window.bootstrap.Modal.getOrCreateInstance).toHaveBeenCalledWith(document.getElementById("login-modal"));
    expect(modalShow).toHaveBeenCalledTimes(1);
    expect(errorBox().classList.contains("d-none")).toBe(true);
    expect(pending().classList.contains("d-none")).toBe(true);

    api.post.mockRejectedValueOnce({ code: "forbidden", httpStatus: 403, message: "invalid csrf token" });
    await add("x");
    expect(modalShow).toHaveBeenCalledTimes(2);
  });

  it("shows the server's message for a 400 or 409 and a generic one for other failures", async () => {
    await openEditor();
    api.post.mockRejectedValueOnce({ code: "invalid_request", httpStatus: 400, message: "too many tags" });
    await add("x");
    expect(errorBox().textContent).toBe("too many tags");
    expect(errorBox().classList.contains("d-none")).toBe(false);
    expect(input().value).toBe("x");

    api.post.mockRejectedValueOnce({ code: "conflict", httpStatus: 409, message: "try again" });
    await add("x");
    expect(errorBox().textContent).toBe("try again");

    api.post.mockRejectedValueOnce({ name: "NetworkError", message: "down" });
    await add("x");
    expect(errorBox().textContent).toBe("tag.error");
    expect(modalShow).not.toHaveBeenCalled();
  });

  it("shows the generic error when the tags cannot be loaded", async () => {
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T" }]);
    api.get.mockImplementation(async (path) => {
      if (path.endsWith("/tags")) throw { code: "server_error", httpStatus: 500, message: "boom" };
      return {};
    });
    toggle().click();
    await settle();
    expect(errorBox().textContent).toBe("tag.error");
  });

  it("closes on Escape and on a second toggle click, returning the focus to the toggle", async () => {
    await openEditor();
    input().dispatchEvent(new KeyboardEvent("keydown", { key: "Escape", bubbles: true, cancelable: true }));
    expect(editor().classList.contains("d-none")).toBe(true);
    expect(toggle().getAttribute("aria-expanded")).toBe("false");
    expect(document.activeElement).toBe(toggle());

    toggle().click();
    await settle();
    expect(card().querySelectorAll(".tag-editor").length).toBe(1);
    expect(api.get.mock.calls.filter((c) => c[0] === "/documents/d1/tags").length).toBe(2);

    toggle().click();
    expect(editor().classList.contains("d-none")).toBe(true);
  });
});

describe("My tags panel", () => {
  const panel = () => document.getElementById("my-tags-panel");
  const rows = () => [...panel().querySelectorAll(".my-tag")];
  const nameOf = (row) => row.querySelector(".my-tag-name").textContent;
  const pending = () => panel().querySelector(".tag-pending");
  const errorBox = () => panel().querySelector(".my-tags-error");

  const MINE = [
    { id: ID.a, value: V.a, name: "alpha", shared: false, sort_order: 0, path_count: 2 },
    { id: ID.b, value: V.proposal, name: "<b>beta</b>", shared: true, sort_order: 0, path_count: 0 },
  ];

  async function openPanel() {
    api.isAuthenticated.mockReturnValue(true);
    myTagsAnswer = { status: 0, tags: MINE };
    await search([{ doc_id: "d1", title: "T" }]);
    document.getElementById("my-tags-toggle").click();
    await settle();
  }

  it("lists the user's tags from GET /tags with their names as plain text and the shared state", async () => {
    await openPanel();
    expect(api.get).toHaveBeenCalledWith("/tags");
    expect(document.getElementById("my-tags-toggle").getAttribute("aria-expanded")).toBe("true");
    expect(rows().map(nameOf)).toEqual(["alpha", "<b>beta</b>"]);
    expect(panel().querySelector(".my-tag-name b")).toBeNull();
    expect(rows().map((r) => r.querySelector("input.my-tag-shared").checked)).toEqual([false, true]);
  });

  it("says so when the user has no tags", async () => {
    api.isAuthenticated.mockReturnValue(true);
    myTagsAnswer = { status: 0, tags: [] };
    await search([{ doc_id: "d1", title: "T" }]);
    document.getElementById("my-tags-toggle").click();
    await settle();
    expect(panel().querySelector(".my-tags-empty").textContent).toBe("tag.my_empty");
  });

  it("filters the search by a tag's value when its name is clicked", async () => {
    await openPanel();
    rows()[0].querySelector(".my-tag-name").click();
    await settle();
    expect(lastSearchParams()["fields.tag"]).toEqual([V.a]);
    expect(badgeTexts()).toEqual(["alpha"]);
  });

  it("renames a tag through PUT /tags/{id} with the new name, reloads the list and shows the pending note", async () => {
    await openPanel();
    api.put.mockResolvedValue({ status: 0, renamed: true, tag: { ...MINE[0], id: ID.c, name: "gamma" } });
    rows()[0].querySelector(".my-tag-rename").click();
    const input = rows()[0].querySelector("input.my-tag-name-input");
    expect(input.value).toBe("alpha");
    expect(document.activeElement).toBe(input);
    myTagsAnswer = { status: 0, tags: [{ ...MINE[0], id: ID.c, name: "gamma" }, MINE[1]] };
    input.value = "  gamma ";
    rows()[0].querySelector("form").dispatchEvent(new Event("submit", { cancelable: true }));
    await settle();

    expect(api.put).toHaveBeenCalledWith("/tags/" + ID.a, { name: "gamma" });
    expect(rows().map(nameOf)).toEqual(["gamma", "<b>beta</b>"]);
    expect(pending().classList.contains("d-none")).toBe(false);
  });

  it("cancels a rename without a request", async () => {
    await openPanel();
    rows()[0].querySelector(".my-tag-rename").click();
    rows()[0].querySelector(".my-tag-cancel").click();
    expect(api.put).not.toHaveBeenCalled();
    expect(rows()[0].querySelector("input.my-tag-name-input")).toBeNull();
    expect(nameOf(rows()[0])).toBe("alpha");
  });

  it("toggles sharing through PUT /tags/{id} with shared", async () => {
    await openPanel();
    api.put.mockResolvedValue({ status: 0, renamed: false, tag: { ...MINE[0], shared: true } });
    myTagsAnswer = { status: 0, tags: [{ ...MINE[0], shared: true }, MINE[1]] };
    const box = rows()[0].querySelector("input.my-tag-shared");
    box.click();
    await settle();
    expect(api.put).toHaveBeenCalledWith("/tags/" + ID.a, { shared: true });
    expect(rows()[0].querySelector("input.my-tag-shared").checked).toBe(true);
  });

  it("deletes a tag only after an in-page confirmation, through DELETE /tags/{id}", async () => {
    const confirmSpy = vi.fn(() => true);
    window.confirm = confirmSpy;
    await openPanel();
    rows()[1].querySelector(".my-tag-delete").click();
    expect(api.del).not.toHaveBeenCalled();
    const confirmBox = rows()[1].querySelector(".my-tag-confirm");
    expect(confirmBox.textContent).toContain("tag.delete_confirm");

    // Cancel keeps the tag.
    confirmBox.querySelector(".my-tag-cancel").click();
    expect(rows()[1].querySelector(".my-tag-confirm")).toBeNull();
    expect(api.del).not.toHaveBeenCalled();

    api.del.mockResolvedValue({ status: 0, id: ID.b, deleted: true });
    myTagsAnswer = { status: 0, tags: [MINE[0]] };
    rows()[1].querySelector(".my-tag-delete").click();
    rows()[1].querySelector(".my-tag-confirm .my-tag-confirm-delete").click();
    await settle();

    expect(api.del).toHaveBeenCalledWith("/tags/" + ID.b);
    expect(rows().map(nameOf)).toEqual(["alpha"]);
    expect(pending().classList.contains("d-none")).toBe(false);
    expect(confirmSpy).not.toHaveBeenCalled();
    delete window.confirm;
  });

  it("shows the server's message when a rename conflicts", async () => {
    await openPanel();
    api.put.mockRejectedValueOnce({ code: "conflict", httpStatus: 409, message: "a tag with the name already exists" });
    rows()[0].querySelector(".my-tag-rename").click();
    rows()[0].querySelector("input.my-tag-name-input").value = "beta";
    rows()[0].querySelector("form").dispatchEvent(new Event("submit", { cancelable: true }));
    await settle();
    expect(errorBox().textContent).toBe("a tag with the name already exists");
    expect(errorBox().classList.contains("d-none")).toBe(false);
    expect(modalShow).not.toHaveBeenCalled();
  });

  it("opens the login modal when GET /tags answers 401", async () => {
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T" }]);
    api.get.mockImplementation(async (path) => {
      if (path === "/tags") throw { code: "auth_required", httpStatus: 401, message: "login required" };
      return {};
    });
    document.getElementById("my-tags-toggle").click();
    await settle();
    expect(modalShow).toHaveBeenCalledTimes(1);
  });

  it("opens the login modal when a delete answers 403", async () => {
    await openPanel();
    api.del.mockRejectedValueOnce({ code: "forbidden", httpStatus: 403, message: "invalid csrf token" });
    rows()[0].querySelector(".my-tag-delete").click();
    rows()[0].querySelector(".my-tag-confirm-delete").click();
    await settle();
    expect(modalShow).toHaveBeenCalledTimes(1);
  });

  it("closes on a second toggle click and survives a new search", async () => {
    await openPanel();
    await search([{ doc_id: "d1", title: "T" }]);
    expect(document.getElementById("my-tags-toggle").getAttribute("aria-expanded")).toBe("true");
    expect(panel().classList.contains("d-none")).toBe(false);
    document.getElementById("my-tags-toggle").click();
    expect(panel().classList.contains("d-none")).toBe(true);
    expect(document.getElementById("my-tags-toggle").getAttribute("aria-expanded")).toBe("false");
  });
});
