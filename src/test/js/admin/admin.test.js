// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, beforeAll, beforeEach } from "vitest";
import jquerySource from "../../../main/webapp/js/admin/jquery-3.7.1.min.js?raw";
import adminSource from "../../../main/webapp/js/admin/admin.js?raw";

// The shipped files are plain scripts that use the globals of the page; run them as the page does.
const run = (source) => (0, window.eval)(source);

// admin.js binds its handlers when the document is ready, to the elements that exist then.
async function mount(html) {
  document.body.innerHTML = html;
  run(adminSource);
  // ready callbacks run in the order they were added, so this one runs after admin.js has bound its handlers
  await new Promise((resolve) => window.jQuery(resolve));
}

// jsdom leaves KeyboardEvent#which at 0, a browser sets it to the key code
function keyEvent(type, init) {
  const event = new KeyboardEvent(type, { bubbles: true, cancelable: true, ...init });
  Object.defineProperty(event, "which", { value: init.keyCode });
  return event;
}

function press(el, init = {}) {
  const event = keyEvent("keypress", { keyCode: 13, charCode: 13, ...init });
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

beforeAll(async () => {
  run(jquerySource);
  window.moment = { locale() {} };
  // plugins admin.js calls on load that the page ships separately
  for (const name of ["daterangepicker", "timepicker", "tooltip"]) window.jQuery.fn[name] = function () { return this; };
});

beforeEach(() => {
  window.location.hash = "";
});

describe("Enter in a field of a form", () => {
  it("presses the primary button of the form, not the first submit button", async () => {
    await mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"));
    expect(clicked).toEqual(["create"]);
    expect(event.defaultPrevented).toBe(true);
  });

  it("presses the Search button of a list", async () => {
    await mount(`<section class="content"><form>
      <input type="text" id="q" name="q">
      <button type="submit" class="btn btn-primary" id="submit" name="search">Search</button>
      <button type="submit" class="btn btn-default" name="reset">Reset</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("q"));
    expect(clicked).toEqual(["search"]);
  });

  it("uses the buttons of the form the field is in", async () => {
    await mount(`<section class="content">
      <form><input type="text" id="a"><button type="submit" class="btn btn-primary" name="one">One</button></form>
      <form><input type="text" id="b"><button type="submit" class="btn btn-primary" name="two">Two</button></form>
    </section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("b"));
    expect(clicked).toEqual(["two"]);
  });

  it("does nothing, and still keeps the browser from pressing Back, when there is no primary button", async () => {
    await mount(`<section class="content"><form>
      <input type="text" id="name">
      <button type="submit" class="btn btn-outline-secondary" name="list">Back</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"));
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(true);
  });

  it("does not press a disabled button", async () => {
    await mount(`<section class="content"><form>
      <input type="text" id="name">
      <button type="submit" class="btn btn-primary" name="create" disabled>Create</button>
    </form></section>`);
    const clicked = recordClicks(document.body);
    press(document.getElementById("name"));
    expect(clicked).toEqual([]);
  });

  it("leaves keys other than Enter alone", async () => {
    await mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"), { keyCode: 65, charCode: 97 });
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(false);
  });

  it("leaves the Enter that confirms an IME composition alone", async () => {
    await mount(createForm);
    const clicked = recordClicks(document.body);
    const event = press(document.getElementById("name"), { isComposing: true });
    expect(clicked).toEqual([]);
    expect(event.defaultPrevented).toBe(false);
  });

  it("leaves file inputs and buttons to the browser", async () => {
    await mount(createForm);
    const clicked = recordClicks(document.body);
    expect(press(document.getElementById("upload")).defaultPrevented).toBe(false);
    expect(press(document.querySelector("button[name=list]")).defaultPrevented).toBe(false);
    expect(clicked).toEqual([]);
  });

  it("raises no script error", async () => {
    await mount(createForm);
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

  it("opens with Enter", async () => {
    await mount(rows);
    const event = key(document.getElementById("row"), { key: "Enter", keyCode: 13 });
    expect(window.location.hash).toBe("#details-1");
    expect(event.defaultPrevented).toBe(true);
  });

  it("opens with Space, and the page does not scroll", async () => {
    await mount(rows);
    const event = key(document.getElementById("row"), { key: " ", keyCode: 32 });
    expect(window.location.hash).toBe("#details-1");
    expect(event.defaultPrevented).toBe(true);
  });

  it("ignores other keys", async () => {
    await mount(rows);
    key(document.getElementById("row"), { key: "a", keyCode: 65 });
    expect(window.location.hash).toBe("");
  });

  it("leaves Enter on a control inside the row to that control", async () => {
    await mount(rows);
    const event = key(document.getElementById("inner"), { key: "Enter", keyCode: 13 });
    expect(window.location.hash).toBe("");
    expect(event.defaultPrevented).toBe(false);
  });

  it("still opens on click", async () => {
    await mount(rows);
    document.getElementById("row").click();
    expect(window.location.hash).toBe("#details-1");
  });
});
