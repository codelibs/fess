// SPDX-License-Identifier: Apache-2.0
// Tests for the shared user tags of the bootstrap theme's search.js (features.user_tag): the
// tag chips and the inline tag editor on the result cards, the "tag" facet group, the
// fields.tag filter and its active-filter badge. api.js and router.js are mocked so the
// config, the auth state and the /search and /documents/{id}/tags answers are controllable;
// i18n.js and format.js run for real, and i18n.t() returns its key unchanged (no messages
// loaded), so assertions match i18n keys.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { resetDom, mountBody, setLocation } from "../../helpers/dom.js";
import { jsonResponse, installFetch } from "../../helpers/net.js";

const API_PATH = "../../../../main/webapp/themes/bootstrap/assets/api.js";

vi.mock("../../../../main/webapp/themes/bootstrap/assets/api.js", () => ({
  getConfig: vi.fn(() => null),
  get: vi.fn(async () => ({})),
  post: vi.fn(async () => ({})),
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

const GENERAL = { value: "general", name: "General" };
const PROJECT = { value: "project", name: "Project" };

function config({ userTag = true, types = [GENERAL] } = {}) {
  return { features: { user_tag: userTag }, ...(userTag ? { tag_types: types } : {}) };
}

const TAG = (type, name) => ({ value: type + ":" + name, type, name });

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
let tagsAnswer;
function installApi() {
  api.get.mockImplementation(async (path) => {
    if (path === "/search") return env;
    if (path === "/labels") return { labels: [] };
    if (path.endsWith("/tags")) return tagsAnswer;
    return {};
  });
}

const settle = () => new Promise((r) => setTimeout(r));
const searchCalls = () => api.get.mock.calls.filter((c) => c[0] === "/search");
const lastSearchParams = () => searchCalls().at(-1)[1];
const tagGroup = () => [...document.querySelectorAll("#facet-body ul.list-group")]
  .find((ul) => ul.firstChild.textContent === "tag.title");

async function search(docs, tagFacet) {
  env = searchEnv(docs, tagFacet);
  installApi();
  await runSearch();
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
  tagsAnswer = { status: 0, doc_id: "d1", addable: true, tags: [] };
  modalShow = vi.fn();
  window.bootstrap = { Modal: { getOrCreateInstance: vi.fn(() => ({ show: modalShow })) } };
});
afterEach(() => {
  delete window.bootstrap;
  setLocation("/");
});

describe("feature flag", () => {
  it("renders no tags, no editor and no tag facet, and asks for the label facet only, when the feature is off", async () => {
    api.getConfig.mockReturnValue(config({ userTag: false }));
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T", tags: [TAG("general", "a")] }], [{ value: "general:a", count: 2 }]);

    expect(document.querySelector("#result0 .tags")).toBeNull();
    expect(document.querySelector(".tag-add-btn")).toBeNull();
    expect(lastSearchParams()["facet.field"]).toEqual(["label"]);
    // Neither the dedicated group nor the generic one shows the tag field.
    expect(tagGroup()).toBeUndefined();
    const titles = [...document.querySelectorAll("#facet-body ul.list-group > li:first-child")].map((li) => li.textContent);
    expect(titles).not.toContain("tag");
  });

  it("asks for the tag facet as well when the feature is on", async () => {
    await search([{ doc_id: "d1", title: "T" }]);
    expect(lastSearchParams()["facet.field"]).toEqual(["label", "tag"]);
  });

  it("renders no tag row for a guest when the document has no tags", async () => {
    await search([{ doc_id: "d1", title: "T", tags: [] }]);
    expect(document.querySelector("#result0 .tags")).toBeNull();
  });

  it("offers no add control when no tag type is visible", async () => {
    api.getConfig.mockReturnValue(config({ types: [] }));
    api.isAuthenticated.mockReturnValue(true);
    await search([{ doc_id: "d1", title: "T", tags: [TAG("general", "a")] }]);
    expect(document.querySelector("#result0 .tag-chip")).not.toBeNull();
    expect(document.querySelector("#result0 .tag-add-btn")).toBeNull();
  });
});

describe("result card chips", () => {
  it("renders each tag as a chip with its name as plain text", async () => {
    const evil = '<img src=x onerror="alert(1)">';
    await search([{ doc_id: "d1", title: "T", tags: [TAG("general", "proposal"), TAG("general", evil)], tag_count: 3 }]);

    const chips = [...document.querySelectorAll("#result0 .tags .tag-chip")];
    expect(chips.map((c) => c.textContent)).toEqual(["proposal", evil]);
    expect(document.querySelector("#result0 .tags img")).toBeNull();
    expect(chips[0].tagName).toBe("BUTTON");
    expect(chips[0].getAttribute("type")).toBe("button");
    expect(chips[0].getAttribute("title")).toBe("General: proposal");
    // A guest sees the tags but no add control.
    expect(document.querySelector("#result0 .tag-add-btn")).toBeNull();
  });

  it("prefixes the type name when several tag types exist", async () => {
    api.getConfig.mockReturnValue(config({ types: [GENERAL, PROJECT] }));
    await search([{ doc_id: "d1", title: "T", tags: [TAG("project", "x:y")] }]);
    expect(document.querySelector("#result0 .tag-chip").textContent).toBe("Project: x:y");
  });

  it("filters the search by the clicked tag, keeping the query, and puts fields.tag in the URL", async () => {
    await search([{ doc_id: "d1", title: "T", tags: [TAG("general", "a b")] }]);
    _state.fields.label = ["lblA"];
    document.querySelector("#result0 .tag-chip").click();
    await settle();

    expect(searchCalls().length).toBe(2);
    const params = lastSearchParams();
    expect(params.q).toBe("foo");
    expect(params["fields.tag"]).toEqual(["general:a b"]);
    expect(params["fields.label"]).toEqual(["lblA"]);
    expect(params.start).toBe(0);
    const qs = new URLSearchParams(location.search);
    expect(qs.getAll("fields.tag")).toEqual(["general:a b"]);
    expect(qs.get("q")).toBe("foo");
  });
});

describe("tag facet", () => {
  const FACET = [
    { value: "general:proposal", count: 3 },
    { value: "general:draft", count: 1 },
    { value: "general:old", count: 0 },
  ];

  it("renders one Tags group with the tag names and counts, without zero counts", async () => {
    await search([{ doc_id: "d1", title: "T" }], FACET);

    const group = tagGroup();
    expect(group).toBeTruthy();
    const items = [...group.querySelectorAll("li.list-group-item a")];
    expect(items.map((a) => a.firstChild.textContent.trim())).toEqual(["proposal", "draft"]);
    expect(items.map((a) => a.querySelector(".badge").textContent)).toEqual(["3", "1"]);
    // Not rendered a second time by the generic field facets.
    const titles = [...document.querySelectorAll("#facet-body ul.list-group > li:first-child")].map((li) => li.textContent);
    expect(titles.filter((x) => x === "tag.title" || x === "tag").length).toBe(1);
    // Mirrored into the mobile offcanvas.
    expect(document.querySelectorAll("#facet-body-mobile ul.list-group").length).toBe(1);
  });

  it("shows the type name when several tag types exist", async () => {
    api.getConfig.mockReturnValue(config({ types: [GENERAL, PROJECT] }));
    await search([{ doc_id: "d1", title: "T" }], [{ value: "project:x", count: 2 }, { value: "unknown:y", count: 1 }]);
    const items = [...tagGroup().querySelectorAll("li.list-group-item a")];
    expect(items.map((a) => a.firstChild.textContent.trim())).toEqual(["Project: x", "unknown:y"]);
  });

  it("filters through fields.tag on click and clears the filter on a second click", async () => {
    await search([{ doc_id: "d1", title: "T" }], FACET);
    tagGroup().querySelector("li.list-group-item a").click();
    await settle();

    expect(lastSearchParams()["fields.tag"]).toEqual(["general:proposal"]);
    expect(lastSearchParams()["ex_q"]).toBeUndefined();
    expect(new URLSearchParams(location.search).getAll("fields.tag")).toEqual(["general:proposal"]);
    const active = tagGroup().querySelector("li.list-group-item.active a");
    expect(active.firstChild.textContent.trim()).toBe("proposal");

    active.click();
    await settle();
    expect(lastSearchParams()["fields.tag"]).toBeUndefined();
    expect(new URLSearchParams(location.search).has("fields.tag")).toBe(false);
  });

  it("keeps a selected tag listed even when its count is zero", async () => {
    _state.fields.tag = ["general:old"];
    await search([{ doc_id: "d1", title: "T" }], FACET);
    const active = tagGroup().querySelector("li.list-group-item.active a");
    expect(active.firstChild.textContent.trim()).toBe("old");
  });
});

describe("fields.tag filter badge", () => {
  it("reads the tag from the URL and shows it as type name and tag name", async () => {
    env = searchEnv([{ doc_id: "d1", title: "T" }]);
    installApi();
    setLocation("/search?q=foo&fields.tag=general%3Aproposal&fields.tag=gone%3Ax");
    runFromUrl();
    await settle();
    await settle();

    expect(lastSearchParams()["fields.tag"]).toEqual(["general:proposal", "gone:x"]);
    const labels = [...document.querySelectorAll("#active-chips .active-chip > span")].map((s) => s.textContent);
    expect(labels).toEqual(["General: proposal", "gone:x"]);
  });

  it("drops the tag from the request and the URL when its badge is removed", async () => {
    env = searchEnv([{ doc_id: "d1", title: "T" }]);
    installApi();
    setLocation("/search?q=foo&fields.tag=general%3Aproposal");
    runFromUrl();
    await settle();
    await settle();
    document.querySelector("#active-chips .active-chip-remove").click();
    await settle();

    expect(lastSearchParams()["fields.tag"]).toBeUndefined();
    expect(new URLSearchParams(location.search).has("fields.tag")).toBe(false);
    expect(new URLSearchParams(location.search).get("q")).toBe("foo");
  });

  it("is kept by the search-options Search button", () => {
    setLocation("/search?q=foo&fields.tag=general%3Aproposal&ex_q=label%3Ax");
    attach();
    document.getElementById("query").value = "foo";
    document.querySelector("#searchOptions button[type=submit]").click();

    const target = navigate.mock.calls.at(-1)[0];
    const qs = new URLSearchParams(target.slice(target.indexOf("?") + 1));
    expect(qs.getAll("fields.tag")).toEqual(["general:proposal"]);
    expect(qs.getAll("ex_q")).toEqual(["label:x"]);
  });
});

describe("tag editor", () => {
  const card = () => document.getElementById("result0");
  const toggle = () => card().querySelector(".tag-add-btn");
  const editor = () => card().querySelector(".tag-editor");
  const editorItems = () => [...editor().querySelectorAll(".tag-editor-item")];
  const input = () => editor().querySelector("input");
  const errorBox = () => editor().querySelector(".tag-editor-error");
  const chipTexts = () => [...card().querySelectorAll(".tag-chips .tag-chip")].map((c) => c.textContent);

  async function openEditor(docs = [{ doc_id: "d1", title: "T", tags: [TAG("general", "a")] }]) {
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

  it("shows an add control to a logged-in user that opens the editor and loads the document's tags", async () => {
    tagsAnswer = {
      status: 0, doc_id: "d1", addable: true,
      tags: [
        { value: "general:a", type: "general", name: "a", count: 3, mine: true, removable: true },
        { value: "general:b", type: "general", name: "b", count: 1, mine: false, removable: false },
      ],
    };
    await openEditor();

    expect(toggle().getAttribute("aria-expanded")).toBe("true");
    expect(toggle().getAttribute("aria-controls")).toBe(editor().id);
    expect(toggle().textContent).toBe("tag.add");
    expect(api.get).toHaveBeenCalledWith("/documents/d1/tags");
    expect(editor().getAttribute("aria-label")).toBe("tag.title");
    const items = editorItems();
    expect(items.map((li) => li.firstChild.textContent)).toEqual(["a", "b"]);
    expect(items.map((li) => li.querySelector(".tag-count").textContent)).toEqual(["3", "1"]);
    expect(items[0].querySelector("button.tag-remove").getAttribute("aria-label")).toBe("tag.remove");
    expect(items[1].querySelector("button.tag-remove")).toBeNull();
    // The input is labelled, capped at 50 characters and takes the focus.
    expect(input().getAttribute("maxlength")).toBe("50");
    expect(editor().querySelector(`label[for="${input().id}"]`).textContent).toBe("tag.name");
    expect(document.activeElement).toBe(input());
    expect(editor().querySelector(".tag-editor-empty").classList.contains("d-none")).toBe(true);
    // The chips follow the answer.
    expect(chipTexts()).toEqual(["a", "b"]);
  });

  it("offers no type select with one tag type, and one listing every type with several", async () => {
    await openEditor();
    expect(editor().querySelector("select")).toBeNull();

    api.getConfig.mockReturnValue(config({ types: [GENERAL, PROJECT] }));
    await openEditor();
    const select = editor().querySelector("select");
    expect([...select.options].map((o) => [o.value, o.textContent])).toEqual([["general", "General"], ["project", "Project"]]);
    expect(editor().querySelector(`label[for="${select.id}"]`).textContent).toBe("tag.type");
  });

  it("adds a tag with the selected type and updates the chips without a new search", async () => {
    api.getConfig.mockReturnValue(config({ types: [GENERAL, PROJECT] }));
    await openEditor();
    editor().querySelector("select").value = "project";
    api.post.mockResolvedValue({
      status: 0, doc_id: "d1", addable: true, added: true,
      tags: [
        { value: "general:a", type: "general", name: "a", count: 1, removable: false },
        { value: "project:new one", type: "project", name: "new one", count: 1, removable: true },
      ],
    });
    const before = searchCalls().length;
    await add("  new one ");

    expect(api.post).toHaveBeenCalledWith("/documents/d1/tags", { type: "project", name: "new one" });
    expect(chipTexts()).toEqual(["General: a", "Project: new one"]);
    expect(editorItems().length).toBe(2);
    expect(input().value).toBe("");
    expect(document.activeElement).toBe(input());
    expect(searchCalls().length).toBe(before);
  });

  it("sends the only tag type and ignores an empty name", async () => {
    await openEditor();
    await add("   ");
    expect(api.post).not.toHaveBeenCalled();
    await add("x");
    expect(api.post).toHaveBeenCalledWith("/documents/d1/tags", { type: "general", name: "x" });
  });

  it("opens the login modal when adding answers 401 or 403", async () => {
    await openEditor();
    api.post.mockRejectedValueOnce({ code: "auth_required", httpStatus: 401, message: "login" });
    await add("x");
    expect(window.bootstrap.Modal.getOrCreateInstance).toHaveBeenCalledWith(document.getElementById("login-modal"));
    expect(modalShow).toHaveBeenCalledTimes(1);
    expect(errorBox().classList.contains("d-none")).toBe(true);

    api.post.mockRejectedValueOnce({ code: "forbidden", httpStatus: 403, message: "csrf" });
    await add("x");
    expect(modalShow).toHaveBeenCalledTimes(2);
  });

  it("shows the server's message for a 400 and a generic one for other failures", async () => {
    await openEditor();
    api.post.mockRejectedValueOnce({ code: "invalid_request", httpStatus: 400, message: "Too many tags." });
    await add("x");
    expect(errorBox().textContent).toBe("Too many tags.");
    expect(errorBox().classList.contains("d-none")).toBe(false);
    expect(errorBox().getAttribute("aria-live")).toBe("polite");
    expect(input().value).toBe("x");

    api.post.mockRejectedValueOnce({ name: "NetworkError", message: "down" });
    await add("x");
    expect(errorBox().textContent).toBe("tag.error");
    expect(modalShow).not.toHaveBeenCalled();

    // The next successful answer clears the message.
    api.post.mockResolvedValueOnce({ status: 0, addable: true, tags: [] });
    await add("x");
    expect(errorBox().classList.contains("d-none")).toBe(true);
  });

  it("removes a tag through DELETE with the tag value and updates the chips", async () => {
    tagsAnswer = {
      status: 0, doc_id: "d/1", addable: true,
      tags: [{ value: "general:a b&c", type: "general", name: "a b&c", count: 2, mine: true, removable: true }],
    };
    api.del.mockResolvedValue({ status: 0, doc_id: "d/1", addable: true, removed: 1, tags: [] });
    await openEditor([{ doc_id: "d/1", title: "T", tags: [TAG("general", "a b&c")] }]);
    expect(api.get).toHaveBeenCalledWith("/documents/d%2F1/tags");

    editorItems()[0].querySelector("button.tag-remove").click();
    await settle();

    expect(api.del).toHaveBeenCalledWith("/documents/d%2F1/tags", { value: "general:a b&c" });
    expect(chipTexts()).toEqual([]);
    expect(editorItems().length).toBe(0);
    expect(editor().querySelector(".tag-editor-empty").classList.contains("d-none")).toBe(false);
    expect(document.activeElement).toBe(input());
  });

  it("URL-encodes the tag value in the DELETE request", async () => {
    const actual = await vi.importActual(API_PATH);
    const fetchMock = installFetch(async () => jsonResponse({ response: { status: 0, addable: true, removed: 1, tags: [] } }));
    api.del.mockImplementation(actual.del);
    tagsAnswer = { status: 0, addable: true, tags: [{ value: "general:a b&c=d", type: "general", name: "a b&c=d", count: 1, removable: true }] };
    try {
      await openEditor();
      editorItems()[0].querySelector("button.tag-remove").click();
      await settle();
      const [url, opts] = fetchMock.mock.calls[0];
      expect(url).toBe("api/v2/documents/d1/tags?value=general%3Aa+b%26c%3Dd");
      expect(opts.method).toBe("DELETE");
      expect(new URLSearchParams(url.split("?")[1]).get("value")).toBe("general:a b&c=d");
    } finally {
      vi.unstubAllGlobals();
    }
  });

  it("re-enables the remove button and asks for login when the DELETE answers 401", async () => {
    tagsAnswer = { status: 0, addable: true, tags: [{ value: "general:a", type: "general", name: "a", count: 1, removable: true }] };
    api.del.mockRejectedValueOnce({ code: "auth_required", httpStatus: 401 });
    await openEditor();
    const btn = editorItems()[0].querySelector("button.tag-remove");
    btn.click();
    await settle();
    expect(modalShow).toHaveBeenCalledTimes(1);
    expect(btn.disabled).toBe(false);
  });

  it("hides the add form when the server says the user cannot add tags", async () => {
    tagsAnswer = { status: 0, addable: false, tags: [{ value: "general:a", type: "general", name: "a", count: 1, removable: false }] };
    await openEditor();
    expect(editor().querySelector("form").classList.contains("d-none")).toBe(true);
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

    // Reopening reuses the editor and reloads the tags.
    toggle().click();
    await settle();
    expect(card().querySelectorAll(".tag-editor").length).toBe(1);
    expect(editor().classList.contains("d-none")).toBe(false);
    expect(api.get.mock.calls.filter((c) => c[0] === "/documents/d1/tags").length).toBe(2);

    toggle().click();
    expect(editor().classList.contains("d-none")).toBe(true);
    expect(toggle().getAttribute("aria-expanded")).toBe("false");
  });

  it("filters by a chip after the chips were re-rendered from the editor", async () => {
    tagsAnswer = { status: 0, addable: true, tags: [{ value: "general:z", type: "general", name: "z", count: 1, removable: true }] };
    await openEditor();
    card().querySelector(".tag-chips .tag-chip").click();
    await settle();
    expect(lastSearchParams()["fields.tag"]).toEqual(["general:z"]);
  });
});
