// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, beforeAll, beforeEach, afterEach } from "vitest";
import adminSource from "../../../main/webapp/js/admin/admin.js?raw";

// The shipped file is a plain script that uses the globals of the page; run it as the page does.
const run = (source) => (0, window.eval)(source);

// admin.js registers its (delegated) handlers once; init() prepares the elements the page has when it loads.
function mount(html) {
  document.body.innerHTML = html;
  document.body.className = "";
  window.FessAdmin.init();
}

function keyEvent(type, init) {
  const event = new KeyboardEvent(type, { bubbles: true, cancelable: true, ...init });
  return event;
}

function press(el, init = {}) {
  const event = keyEvent("keydown", { key: "Enter", keyCode: 13, ...init });
  el.dispatchEvent(event);
  return event;
}

// A click on a submit button would submit the form, which jsdom cannot do; record it instead.
function recordClicks(root) {
  const clicked = [];
  root.querySelectorAll("button, input[type=submit]").forEach((b) =>
    b.addEventListener("click", (e) => {
      e.preventDefault();
      clicked.push(b.name || b.id);
    })
  );
  return clicked;
}

const createForm = `
<section class="content"><form id="f">
  <input type="text" id="name" name="name">
  <input type="checkbox" id="flag" name="flag">
  <input type="file" id="upload" name="upload">
  <button type="submit" class="btn btn-outline-secondary" name="list">Back</button>
  <button type="submit" class="btn btn-primary" name="create">Create</button>
</form></section>`;

beforeAll(() => {
  run(adminSource);
});

beforeEach(() => {
  window.location.hash = "";
});

describe("Enter in a field of a form", () => {
  it("presses the primary button of the form, not the first submit button", () => {
    mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"));
    expect(clicked).toEqual(["create"]);
    expect(event.defaultPrevented).toBe(true);
  });

  it("presses the Search button of a list", () => {
    mount(`<section class="content"><form>
      <input type="text" id="q" name="q">
      <button type="submit" class="btn btn-primary" id="submit" name="search">Search</button>
      <button type="submit" class="btn btn-default" name="reset">Reset</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("q"));
    expect(clicked).toEqual(["search"]);
  });

  it("uses the buttons of the form the field is in", () => {
    mount(`<section class="content">
      <form><input type="text" id="a"><button type="submit" class="btn btn-primary" name="one">One</button></form>
      <form><input type="text" id="b"><button type="submit" class="btn btn-primary" name="two">Two</button></form>
    </section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("b"));
    expect(clicked).toEqual(["two"]);
  });

  it("does nothing, and still keeps the browser from pressing Back, when there is no primary button", () => {
    mount(`<section class="content"><form>
      <input type="text" id="name">
      <button type="submit" class="btn btn-outline-secondary" name="list">Back</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"));
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(true);
  });

  it("does not press a disabled button", () => {
    mount(`<section class="content"><form>
      <input type="text" id="name">
      <button type="submit" class="btn btn-primary" name="create" disabled>Create</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("name"));
    expect(clicked).toEqual([]);
  });

  it("leaves keys other than Enter alone", () => {
    mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"), { key: "a", keyCode: 65 });
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(false);
  });

  it("leaves the Enter that confirms an IME composition alone", () => {
    mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"), { isComposing: true });
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(false);
  });

  it("leaves file inputs and buttons to the browser", () => {
    mount(createForm);
    const clicked = recordClicks(document.body);
    expect(press(document.getElementById("upload")).defaultPrevented).toBe(false);
    expect(press(document.querySelector("button[name=list]")).defaultPrevented).toBe(false);
    expect(clicked).toEqual([]);
  });

  it("raises no script error", () => {
    mount(createForm);
    recordClicks(document.body);
    const errors = [];
    const onError = (e) => errors.push(e.message);
    window.addEventListener("error", onError);
    press(document.getElementById("name"));
    window.removeEventListener("error", onError);
    expect(errors).toEqual([]);
  });
});

describe("a list row announced as a button", () => {
  const rows = `<section class="content"><table class="table"><tbody>
    <tr id="row" data-href="#details-1" role="button" tabindex="0"><td>a</td><td><button id="inner" type="button">x</button></td></tr>
  </tbody></table></section>`;

  const key = (el, init) => {
    const event = keyEvent("keydown", init);
    el.dispatchEvent(event);
    return event;
  };

  it("opens with Enter", () => {
    mount(rows);
    const event = key(document.getElementById("row"), { key: "Enter", keyCode: 13 });
    expect(window.location.hash).toBe("#details-1");
    expect(event.defaultPrevented).toBe(true);
  });

  it("opens with Space, and the page does not scroll", () => {
    mount(rows);
    const event = key(document.getElementById("row"), { key: " ", keyCode: 32 });
    expect(window.location.hash).toBe("#details-1");
    expect(event.defaultPrevented).toBe(true);
  });

  it("ignores other keys", () => {
    mount(rows);
    key(document.getElementById("row"), { key: "a", keyCode: 65 });
    expect(window.location.hash).toBe("");
  });

  it("leaves Enter on a control inside the row to that control", () => {
    mount(rows);
    const event = key(document.getElementById("inner"), { key: "Enter", keyCode: 13 });
    expect(window.location.hash).toBe("");
    expect(event.defaultPrevented).toBe(false);
  });

  it("still opens on click", () => {
    mount(rows);
    document.getElementById("row").click();
    expect(window.location.hash).toBe("#details-1");
  });
});

describe("modal", () => {
  const modalPage = `<button id="open" type="button" data-toggle="modal" data-target="#confirmToDelete"
      data-docid="doc-1" data-title="A title" data-url="https://example.com/a">Delete</button>
    <div class="modal fade" id="confirmToDelete" tabindex="-1" role="alertdialog" aria-hidden="true">
      <div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><button type="button" class="close" data-dismiss="modal" id="x">x</button></div>
        <div class="modal-body"><span id="delete-doc-title"></span><span id="delete-doc-url"></span></div>
        <div class="modal-footer"><input type="hidden" id="docId"><button type="button" class="btn" data-dismiss="modal" id="cancel">Cancel</button><button type="submit" class="btn btn-danger" id="go">Delete</button></div>
      </div></div>
    </div>`;
  const modal = () => document.getElementById("confirmToDelete");

  it("opens from its trigger, with a backdrop, and fills the delete confirmation from the trigger", () => {
    mount(modalPage);
    document.getElementById("open").click();
    expect(modal().classList.contains("show")).toBe(true);
    expect(modal().getAttribute("aria-hidden")).toBeNull();
    expect(document.querySelector(".modal-backdrop")).not.toBeNull();
    expect(document.body.classList.contains("modal-open")).toBe(true);
    expect(document.getElementById("delete-doc-title").textContent).toBe("A title");
    expect(document.getElementById("delete-doc-url").textContent).toBe("https://example.com/a");
    expect(document.getElementById("docId").value).toBe("doc-1");
    // focus goes to a control in the footer, not to the close button
    expect(document.activeElement.id).toBe("cancel");
  });

  it("closes with a dismiss button, Escape and a click on the dimmed page, and gives the focus back", () => {
    mount(modalPage);
    for (const close of [
      () => document.getElementById("x").click(),
      () => document.dispatchEvent(keyEvent("keydown", { key: "Escape" })),
      () => modal().click(),
    ]) {
      document.getElementById("open").click();
      close();
      expect(modal().classList.contains("show")).toBe(false);
      expect(modal().getAttribute("aria-hidden")).toBe("true");
      expect(document.querySelector(".modal-backdrop")).toBeNull();
      expect(document.body.classList.contains("modal-open")).toBe(false);
      expect(document.activeElement.id).toBe("open");
    }
  });

  it("keeps Tab inside the dialog", () => {
    mount(modalPage);
    // jsdom has no layout, so every element reports no offset parent; give the controls one
    for (const el of modal().querySelectorAll("button, input")) Object.defineProperty(el, "offsetParent", { value: document.body });
    document.getElementById("open").click();
    document.getElementById("go").focus();
    const forward = keyEvent("keydown", { key: "Tab" });
    document.getElementById("go").dispatchEvent(forward);
    expect(forward.defaultPrevented).toBe(true);
    expect(document.activeElement.id).toBe("x");
    const backward = keyEvent("keydown", { key: "Tab", shiftKey: true });
    document.getElementById("x").dispatchEvent(backward);
    expect(backward.defaultPrevented).toBe(true);
    expect(document.activeElement.id).toBe("go");
  });
});

describe("collapse, tabs and the side menu", () => {
  // the modal tests leave a dialog open in the script's own state; Escape closes it
  beforeEach(() => document.dispatchEvent(keyEvent("keydown", { key: "Escape" })));

  it("collapses and expands the target and keeps aria-expanded in step", () => {
    mount(`<a id="t" role="button" data-toggle="collapse" href="#panel" aria-expanded="false">s</a><div class="collapse" id="panel"></div>`);
    document.getElementById("t").click();
    expect(document.getElementById("panel").classList.contains("show")).toBe(true);
    expect(document.getElementById("t").getAttribute("aria-expanded")).toBe("true");
    document.getElementById("t").click();
    expect(document.getElementById("panel").classList.contains("show")).toBe(false);
    expect(document.getElementById("t").getAttribute("aria-expanded")).toBe("false");
  });

  it("switches tabs", () => {
    mount(`<ul class="nav nav-tabs">
        <li><a id="a" class="nav-link active" data-toggle="tab" href="#one">1</a></li>
        <li><a id="b" class="nav-link" data-toggle="tab" href="#two">2</a></li></ul>
      <div class="tab-content"><div class="tab-pane active" id="one"></div><div class="tab-pane" id="two"></div></div>`);
    document.getElementById("b").click();
    expect(document.getElementById("a").classList.contains("active")).toBe(false);
    expect(document.getElementById("b").classList.contains("active")).toBe(true);
    expect(document.getElementById("one").classList.contains("active")).toBe(false);
    expect(document.getElementById("two").classList.contains("active")).toBe(true);
  });

  const tabList = `<ul class="nav nav-tabs" role="tablist">
      <li role="presentation"><a id="a" class="nav-link active" data-toggle="tab" href="#one" role="tab" aria-selected="true">1</a></li>
      <li role="presentation"><a id="b" class="nav-link" data-toggle="tab" href="#two" role="tab" aria-selected="false">2</a></li>
      <li role="presentation"><a id="c" class="nav-link" data-toggle="tab" href="#three" role="tab" aria-selected="false">3</a></li></ul>
    <div class="tab-content"><div class="tab-pane active" id="one"></div><div class="tab-pane" id="two"></div><div class="tab-pane" id="three"></div></div>`;

  it("moves between the tabs with the arrow keys, Home and End, and the focus follows", () => {
    mount(tabList);
    const a = document.getElementById("a");
    a.focus();
    const right = press(a, { key: "ArrowRight" });
    expect(right.defaultPrevented).toBe(true);
    expect(document.activeElement.id).toBe("b");
    expect(document.getElementById("b").getAttribute("aria-selected")).toBe("true");
    expect(document.getElementById("two").classList.contains("active")).toBe(true);
    press(document.getElementById("b"), { key: "End" });
    expect(document.activeElement.id).toBe("c");
    press(document.getElementById("c"), { key: "ArrowRight" });
    expect(document.activeElement.id).toBe("a");
    press(document.getElementById("a"), { key: "ArrowLeft" });
    expect(document.activeElement.id).toBe("c");
    press(document.getElementById("c"), { key: "Home" });
    expect(document.activeElement.id).toBe("a");
    expect(document.getElementById("one").classList.contains("active")).toBe(true);
  });

  it("opens the focused tab with Space and leaves other keys alone", () => {
    mount(tabList);
    const space = press(document.getElementById("b"), { key: " " });
    expect(space.defaultPrevented).toBe(true);
    expect(document.getElementById("two").classList.contains("active")).toBe(true);
    const other = press(document.getElementById("b"), { key: "a" });
    expect(other.defaultPrevented).toBe(false);
  });

  it("leaves the arrow keys, Home and End with a modifier to the browser (Alt+Left is Back)", () => {
    mount(tabList);
    const a = document.getElementById("a");
    a.focus();
    ["altKey", "ctrlKey", "metaKey", "shiftKey"].forEach((modifier) => {
      ["ArrowLeft", "ArrowRight", "Home", "End"].forEach((key) => {
        const event = press(a, { key, [modifier]: true });
        expect(event.defaultPrevented).toBe(false);
      });
    });
    expect(document.activeElement.id).toBe("a");
    expect(press(a, { key: " ", ctrlKey: true }).defaultPrevented).toBe(false);
    expect(document.getElementById("a").classList.contains("active")).toBe(true);
  });

  it("keeps only the selected tab in the tab order", () => {
    mount(tabList);
    const stops = () => ["a", "b", "c"].map((id) => document.getElementById(id).getAttribute("tabindex"));
    expect(stops()).toEqual(["0", "-1", "-1"]);
    press(document.getElementById("a"), { key: "End" });
    expect(stops()).toEqual(["-1", "-1", "0"]);
    document.getElementById("b").click();
    expect(stops()).toEqual(["-1", "0", "-1"]);
  });

  it("tells the state of the menu groups and of the menu button from the start", () => {
    window.matchMedia = (query) => ({ matches: false, media: query });
    mount(`<a id="p" data-widget="pushmenu" href="#">m</a>
      <ul data-widget="treeview"><li class="nav-item has-treeview menu-open"><a href="#" class="nav-link" id="open">a</a></li>
      <li class="nav-item has-treeview"><a href="#" class="nav-link" id="shut">b</a></li></ul>`);
    expect(document.getElementById("open").getAttribute("aria-expanded")).toBe("true");
    expect(document.getElementById("shut").getAttribute("aria-expanded")).toBe("false");
    expect(document.getElementById("p").getAttribute("aria-expanded")).toBe("true");
    window.matchMedia = (query) => ({ matches: true, media: query });
    window.FessAdmin.init();
    expect(document.getElementById("p").getAttribute("aria-expanded")).toBe("false");
  });

  it("closes the side menu that is open over a narrow page with Escape and puts the focus on the menu button", () => {
    window.matchMedia = (query) => ({ matches: true, media: query });
    mount(`<a id="p" data-widget="pushmenu" href="#">m</a>`);
    document.getElementById("p").click();
    expect(document.body.classList.contains("sidebar-open")).toBe(true);
    const event = press(document.body, { key: "Escape" });
    expect(event.defaultPrevented).toBe(false);
    expect(document.body.classList.contains("sidebar-open")).toBe(false);
    expect(document.getElementById("p").getAttribute("aria-expanded")).toBe("false");
    expect(document.activeElement.id).toBe("p");
  });

  it("leaves Escape to the page when the menu is not a drawer (a wide screen) or an IME composition is open", () => {
    window.matchMedia = (query) => ({ matches: true, media: query });
    mount(`<a id="p" data-widget="pushmenu" href="#">m</a><section class="content"><input type="text" id="q"></section>`);
    document.getElementById("p").click();
    expect(document.body.classList.contains("sidebar-open")).toBe(true);
    const q = document.getElementById("q");
    q.focus();
    press(q, { key: "Escape", isComposing: true });
    expect(document.body.classList.contains("sidebar-open")).toBe(true);
    // the window is widened: the class is still there, but Escape in a field must not move the focus to the menu button
    window.matchMedia = (query) => ({ matches: false, media: query });
    press(q, { key: "Escape" });
    expect(document.body.classList.contains("sidebar-open")).toBe(true);
    expect(document.activeElement.id).toBe("q");
    window.matchMedia = (query) => ({ matches: true, media: query });
    press(q, { key: "Escape" });
    expect(document.body.classList.contains("sidebar-open")).toBe(false);
  });

  it("opens and closes a menu group of the sidebar", () => {
    mount(`<ul data-widget="treeview"><li class="nav-item has-treeview" id="g"><a href="#" class="nav-link" id="l">g</a><ul class="nav-treeview"></ul></li></ul>`);
    const click = document.getElementById("l");
    click.click();
    expect(document.getElementById("g").classList.contains("menu-open")).toBe(true);
    expect(click.getAttribute("aria-expanded")).toBe("true");
    click.click();
    expect(document.getElementById("g").classList.contains("menu-open")).toBe(false);
  });

  it("collapses the sidebar on a wide screen and opens it over the page on a narrow one", () => {
    mount(`<a id="p" data-widget="pushmenu" href="#">m</a>`);
    window.matchMedia = (query) => ({ matches: false, media: query });
    document.getElementById("p").click();
    expect(document.body.classList.contains("sidebar-collapse")).toBe(true);
    document.getElementById("p").click();
    expect(document.body.classList.contains("sidebar-collapse")).toBe(false);

    window.matchMedia = (query) => ({ matches: true, media: query });
    document.getElementById("p").click();
    expect(document.body.classList.contains("sidebar-open")).toBe(true);
    document.body.click();
    expect(document.body.classList.contains("sidebar-open")).toBe(false);
  });
});

describe("the date range of the search log", () => {
  const page = (value) => `<label for="range">Requested</label><input type="text" id="range" class="form-control datetimerange" value="${value}">`;
  const pickers = () => document.querySelectorAll(".range-picker input");
  const change = (el, value) => {
    el.value = value;
    el.dispatchEvent(new Event("change", { bubbles: true }));
  };

  it("shows two native date-time inputs filled from the text the server sent", () => {
    mount(page("2026-11-01 09:30 - 2026-11-19 18:05"));
    expect(document.getElementById("range").type).toBe("hidden");
    expect([...pickers()].map((i) => [i.type, i.value, i.getAttribute("aria-label")])).toEqual([
      ["datetime-local", "2026-11-01T09:30", "Requested"],
      ["datetime-local", "2026-11-19T18:05", "Requested"],
    ]);
  });

  it("names the two inputs apart when the screen names the two ends", () => {
    mount(page("2026-11-01 09:30 - 2026-11-19 18:05").replace('class="form-control datetimerange"', 'class="form-control datetimerange" data-label-from="From" data-label-to="To"'));
    expect([...pickers()].map((i) => i.getAttribute("aria-label"))).toEqual(["Requested From", "Requested To"]);
  });

  it("writes the text the server reads: start, a separator, end", () => {
    mount(page(""));
    const [from, to] = pickers();
    change(from, "2026-11-01T09:30");
    expect(document.getElementById("range").value).toBe("2026-11-01 09:30 - ");
    change(to, "2026-11-19T18:05");
    expect(document.getElementById("range").value).toBe("2026-11-01 09:30 - 2026-11-19 18:05");
    change(from, "");
    expect(document.getElementById("range").value).toBe(" - 2026-11-19 18:05");
    change(to, "");
    expect(document.getElementById("range").value).toBe("");
  });

  it("is prepared once however often the page is initialised", () => {
    mount(page(""));
    window.FessAdmin.init();
    expect(pickers().length).toBe(2);
  });

  it("leaves a value it cannot read empty", () => {
    mount(page("not a date"));
    expect([...pickers()].map((i) => i.value)).toEqual(["", ""]);
  });
});

describe("where the cursor starts", () => {
  const page = (extra = "", before = "") =>
    `${before}<section class="content"><form>${extra}<div class="form-group"><input type="text" id="name"><input type="text" id="other"></div></form></section>`;

  // earlier groups leave their own matchMedia behind: start from a mouse pointer
  beforeEach(() => {
    window.matchMedia = (query) => ({ matches: false, media: query });
  });
  afterEach(() => {
    document.activeElement?.blur();
  });

  it("is the first field of the form", () => {
    mount(page());
    expect(document.activeElement.id).toBe("name");
  });

  it("is not a field under a finger, where it would open the on-screen keyboard", () => {
    window.matchMedia = (query) => ({ matches: /pointer:\s*coarse/.test(query), media: query });
    mount(page());
    expect(document.activeElement).toBe(document.body);
  });

  it("is the message that says a form was saved, so the page that follows the redirect does not leave it on the document", () => {
    mount(page('<div class="alert alert-success" role="status" id="msg">Saved</div>'));
    expect(document.activeElement.id).toBe("msg");
    expect(document.activeElement.getAttribute("tabindex")).toBe("-1");
  });

  it("is the message under a finger too (it opens no keyboard)", () => {
    window.matchMedia = (query) => ({ matches: /pointer:\s*coarse/.test(query), media: query });
    mount(page('<div class="alert alert-success" role="status" id="msg">Saved</div>'));
    expect(document.activeElement.id).toBe("msg");
  });

  it("is the field the server refused, whatever else the page holds", () => {
    mount(
      `<section class="content"><form><div class="alert alert-success" id="msg">Saved</div>
        <div class="form-group"><input type="text" id="name"><ul class="has-error"><li>bad</li></ul><input type="text" id="bad"></div></form></section>`
    );
    expect(document.activeElement.id).toBe("bad");
  });
});

describe("form layout", () => {
  const row = (id, cell) => `<div class="form-group row"><label for="${id}" class="col-sm-3 col-form-label">${id}</label>${cell}</div>`;
  const text = (id) => row(id, `<div class="col-sm-9"><input id="${id}" class="form-control"></div>`);
  const inline = (id) => row(id, `<div class="form-inline col-sm-9"><input id="${id}" type="number" class="form-control"></div>`);
  const toggle = (id) =>
    row(id, `<div class="form-inline col-sm-9"><div class="form-check"><input id="${id}" type="checkbox" class="form-check-input"></div></div>`);
  const area = (id) => row(id, `<div class="form-inline col-sm-9"><textarea id="${id}" class="form-control"></textarea></div>`);

  it("puts consecutive short fields into one grid and leaves long fields alone", () => {
    mount(`<div class="card-body" id="b">${text("name")}${inline("depth")}${toggle("on")}${inline("threads")}${text("url")}</div>`);
    const grids = document.querySelectorAll("#b > .form-compact-grid");
    expect(grids.length).toBe(1);
    expect(Array.from(grids[0].querySelectorAll("input")).map((i) => i.id)).toEqual(["depth", "on", "threads"]);
    expect(document.querySelector("#b > .form-group #name")).not.toBeNull();
    expect(document.querySelector("#b > .form-group #url")).not.toBeNull();
  });

  it("does not wrap a single short field, nor a textarea in an inline cell", () => {
    mount(`<div class="card-body" id="b">${text("name")}${inline("depth")}${text("url")}${area("a")}${area("b2")}</div>`);
    expect(document.querySelector(".form-compact-grid")).toBeNull();
  });

  it("is idempotent", () => {
    mount(`<div class="card-body" id="b">${inline("a")}${inline("b2")}</div>`);
    window.FessAdmin.init();
    expect(document.querySelectorAll(".form-compact-grid").length).toBe(1);
  });

  const sections = `<div class="card-body" id="b"><div id="msg" class="form-group row"><div class="alert">saved</div></div>
    <h4>Basic</h4>${text("name")}<h4>Crawl</h4>${text("urls")}<h4>Access</h4>${text("perm")}</div>`;

  it("turns h4 sections into tabs and keeps the messages above them", () => {
    mount(sections);
    const links = Array.from(document.querySelectorAll("#b > .form-tabs .nav-link"));
    expect(links.map((l) => l.textContent)).toEqual(["Basic", "Crawl", "Access"]);
    expect(links[0].classList.contains("active")).toBe(true);
    const panes = document.querySelectorAll("#b > .tab-content > .tab-pane");
    expect(panes.length).toBe(3);
    expect(panes[0].classList.contains("active")).toBe(true);
    expect(panes[1].classList.contains("active")).toBe(false);
    expect(panes[1].querySelector("#urls")).not.toBeNull();
    expect(document.querySelector("#b > #msg")).not.toBeNull();
  });

  it("switches the section with the tab", () => {
    mount(sections);
    document.querySelectorAll("#b .nav-link")[2].click();
    const panes = document.querySelectorAll("#b .tab-pane");
    expect(panes[2].classList.contains("active")).toBe(true);
    expect(panes[0].classList.contains("active")).toBe(false);
  });

  it("opens the section that holds an error, and marks its tab", () => {
    mount(sections.replace(text("urls"), `<ul class="has-error"><li>bad</li></ul>${text("urls")}`));
    const links = document.querySelectorAll("#b .nav-link");
    expect(links[1].classList.contains("active")).toBe(true);
    expect(links[1].classList.contains("has-error")).toBe(true);
    expect(links[0].classList.contains("has-error")).toBe(false);
  });

  it("keeps the tab list a tab list: the list items are presentation, an error tab carries the message as its title", () => {
    mount(sections.replace(text("urls"), `<ul class="has-error"><li>bad url</li></ul>${text("urls")}`));
    const items = Array.from(document.querySelectorAll("#b > .form-tabs > li"));
    expect(items.map((li) => li.getAttribute("role"))).toEqual(["presentation", "presentation", "presentation"]);
    const links = document.querySelectorAll("#b .nav-link");
    expect(links[1].getAttribute("title")).toBe("bad url");
    expect(links[0].hasAttribute("title")).toBe(false);
  });

  it("writes every message of an error tab into its title, one per message, and no empty title", () => {
    mount(
      sections.replace(
        text("urls"),
        `<ul class="has-error"><li><i></i> bad url</li><li><i></i> too long</li></ul><ul class="has-error"><li>  </li></ul>${text("urls")}`
      )
    );
    const links = document.querySelectorAll("#b .nav-link");
    expect(links[1].getAttribute("title")).toBe("bad url; too long");
    mount(sections.replace(text("perm"), `<ul class="has-error"><li><i></i> </li></ul>${text("perm")}`));
    const marked = document.querySelectorAll("#b .nav-link")[2];
    expect(marked.classList.contains("has-error")).toBe(true);
    expect(marked.hasAttribute("title")).toBe(false);
  });

  it("does not stop at an address that cannot be decoded", () => {
    window.location.hash = "#%";
    expect(() => mount(sections)).not.toThrow();
    expect(document.querySelectorAll("#b .nav-link")[0].classList.contains("active")).toBe(true);
    window.location.hash = "";
  });

  it("keeps the tab that holds an error open over a tab that the address names, and follows a later change of the address", () => {
    mount(sections.replace(text("urls"), `<ul class="has-error"><li>bad url</li></ul>${text("urls")}`));
    const links = document.querySelectorAll("#b .nav-link");
    window.location.hash = "#" + links[2].getAttribute("aria-controls");
    window.FessAdmin.init();
    expect(links[1].classList.contains("active")).toBe(true);
    window.dispatchEvent(new HashChangeEvent("hashchange"));
    expect(links[2].classList.contains("active")).toBe(true);
    window.location.hash = "";
  });

  it("opens the tab that the address names", () => {
    mount(sections);
    const links = document.querySelectorAll("#b .nav-link");
    expect(links[0].classList.contains("active")).toBe(true);
    window.location.hash = "#" + links[2].getAttribute("aria-controls");
    window.FessAdmin.init();
    expect(links[0].classList.contains("active")).toBe(false);
    expect(links[2].classList.contains("active")).toBe(true);
    expect(document.getElementById(links[2].getAttribute("aria-controls")).classList.contains("active")).toBe(true);
    window.location.hash = "";
  });

  it("shows the section of a field the browser refuses to submit", () => {
    mount(sections);
    document.getElementById("perm").dispatchEvent(new Event("invalid", { bubbles: false }));
    expect(document.querySelectorAll("#b .nav-link")[2].classList.contains("active")).toBe(true);
  });

  it("builds no tabs for a single heading", () => {
    mount(`<div class="card-body" id="b"><h4>Only</h4>${text("name")}</div>`);
    expect(document.querySelector(".form-tabs")).toBeNull();
  });

  it("hides a card footer that holds nothing, and keeps one with a button or text", () => {
    mount(`<div class="card"><div class="card-footer" id="a">
      <!-- nothing -->
    </div><div class="card-footer" id="b"><div class="row"></div></div>
    <div class="card-footer" id="c"><button type="submit">Go</button></div>
    <div class="card-footer" id="d">3 items</div>
    <div class="card-footer" id="e"><input type="hidden" name="x"></div></div>`);
    const hidden = (id) => document.getElementById(id).hidden;
    expect([hidden("a"), hidden("b"), hidden("c"), hidden("d"), hidden("e")]).toEqual([true, true, false, false, true]);
  });
});
