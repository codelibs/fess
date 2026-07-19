// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the predicate-driven SPA router.
//
// The module keeps a mutable `routes` array plus an `_attached` guard as
// module-level state. Two styles are used below:
//   - register / navigate / dispatch describes import a FRESH module per test
//     (vi.resetModules) so routes never leak between cases.
//   - the attach describe holds ONE dynamically-imported instance and calls
//     attach() exactly once (beforeAll). attach() registers real listeners on
//     the shared jsdom document/window that persist across the whole file even
//     through vi.resetModules, so re-attaching per test would accumulate
//     duplicate listeners. Its tests use DISTINCT paths so the routes that pile
//     up on that single instance never cross-match.

import { describe, it, expect, beforeAll, beforeEach, afterEach, vi } from "vitest";
import { resetDom, setLocation } from "../../helpers/dom.js";

const ROUTER = "../../../../main/webapp/themes/bootstrap/assets/router.js";

/** Dispatch a cancelable, bubbling click on `el` and return the event. */
function clickEvent(el) {
  const ev = new window.MouseEvent("click", { bubbles: true, cancelable: true });
  el.dispatchEvent(ev);
  return ev;
}

describe("register + dispatch", () => {
  let r;
  beforeEach(async () => {
    vi.resetModules();
    setLocation("/");
    r = await import(ROUTER);
  });
  afterEach(() => setLocation("/"));

  it("runs the handler whose predicate matches the current pathname", () => {
    setLocation("/search");
    let seen = null;
    r.register((p) => p === "/search", (p) => { seen = p; });
    r.dispatch();
    expect(seen).toBe("/search");
  });

  it("first matching route wins; later matches do not run", () => {
    setLocation("/x");
    const order = [];
    r.register((p) => p === "/x", () => order.push("a"));
    r.register((p) => p === "/x", () => order.push("b"));
    r.dispatch();
    expect(order).toEqual(["a"]);
  });

  it("runs nothing (and does not throw) when no predicate matches", () => {
    setLocation("/nomatch");
    let ran = false;
    r.register((p) => p === "/other", () => { ran = true; });
    expect(() => r.dispatch()).not.toThrow();
    expect(ran).toBe(false);
  });

  it("strips a trailing slash before matching (/search/ → /search)", () => {
    setLocation("/search/");
    let seen = null;
    r.register((p) => p === "/search", (p) => { seen = p; });
    r.dispatch();
    expect(seen).toBe("/search");
  });

  it("normalises the root pathname to '/' (empty-after-strip fallback)", () => {
    setLocation("/");
    let seen = null;
    r.register((p) => p === "/", (p) => { seen = p; });
    r.dispatch();
    expect(seen).toBe("/");
  });

  it("fires fess:route:change with detail.path BEFORE running the handler", () => {
    setLocation("/evt");
    const order = [];
    const onChange = (e) => order.push(["event", e.detail.path]);
    document.addEventListener("fess:route:change", onChange);
    r.register((p) => p === "/evt", (p) => order.push(["handler", p]));
    try {
      r.dispatch();
    } finally {
      document.removeEventListener("fess:route:change", onChange);
    }
    expect(order).toEqual([["event", "/evt"], ["handler", "/evt"]]);
  });

  it("still fires the route-change event when no route matches", () => {
    setLocation("/lonely");
    let detailPath = null;
    const onChange = (e) => { detailPath = e.detail.path; };
    document.addEventListener("fess:route:change", onChange);
    try {
      r.dispatch();
    } finally {
      document.removeEventListener("fess:route:change", onChange);
    }
    expect(detailPath).toBe("/lonely");
  });
});

describe("navigate", () => {
  let r;
  beforeEach(async () => {
    vi.resetModules();
    setLocation("/");
    r = await import(ROUTER);
  });
  afterEach(() => {
    vi.restoreAllMocks();
    setLocation("/");
  });

  it("uses pushState by default, updates the URL, then dispatches", () => {
    const push = vi.spyOn(history, "pushState");
    const replace = vi.spyOn(history, "replaceState");
    let seen = null;
    r.register((p) => p === "/go", (p) => { seen = p; });
    r.navigate("/go");
    expect(push).toHaveBeenCalledTimes(1);
    expect(push).toHaveBeenCalledWith(null, "", "/go");
    expect(replace).not.toHaveBeenCalled();
    expect(location.pathname).toBe("/go");
    expect(seen).toBe("/go");
  });

  it("uses replaceState (not pushState) when opts.replace is true", () => {
    const push = vi.spyOn(history, "pushState");
    const replace = vi.spyOn(history, "replaceState");
    let seen = null;
    r.register((p) => p === "/rep", (p) => { seen = p; });
    r.navigate("/rep", { replace: true });
    expect(replace).toHaveBeenCalledTimes(1);
    expect(replace).toHaveBeenCalledWith(null, "", "/rep");
    expect(push).not.toHaveBeenCalled();
    expect(location.pathname).toBe("/rep");
    expect(seen).toBe("/rep");
  });
});

describe("attach", () => {
  // One instance, attached once. Real listeners on document/window persist for
  // the whole file, so every test here targets a DISTINCT path.
  let r;
  beforeAll(async () => {
    vi.resetModules();
    r = await import(ROUTER);
    r.attach();
  });
  beforeEach(() => {
    resetDom();
    setLocation("/");
  });
  afterEach(() => setLocation("/"));

  it("intercepts an <a data-spa> click: preventDefault + navigate", () => {
    let seen = null;
    r.register((p) => p === "/spa-a", (p) => { seen = p; });
    document.body.innerHTML = '<a id="lnk" href="/spa-a" data-spa>go</a>';
    const ev = clickEvent(document.getElementById("lnk"));
    expect(ev.defaultPrevented).toBe(true);
    expect(location.pathname).toBe("/spa-a");
    expect(seen).toBe("/spa-a");
  });

  it("ignores external anchors (http/https/protocol-relative href)", () => {
    for (const href of ["https://x.example/y", "http://x.example/y", "//x.example/y"]) {
      setLocation("/");
      document.body.innerHTML = `<a id="ext" href="${href}" data-spa>ext</a>`;
      const ev = clickEvent(document.getElementById("ext"));
      expect(ev.defaultPrevented).toBe(false);
      expect(location.pathname).toBe("/");
    }
  });

  it("ignores clicks that are not on a data-spa anchor", () => {
    document.body.innerHTML = '<a id="plain" href="/plain">no-spa</a>';
    const ev = clickEvent(document.getElementById("plain"));
    expect(ev.defaultPrevented).toBe(false);
    expect(location.pathname).toBe("/");
  });

  it("re-dispatches on popstate (browser back/forward)", () => {
    let seen = null;
    r.register((p) => p === "/pop", (p) => { seen = p; });
    setLocation("/pop");
    window.dispatchEvent(new window.PopStateEvent("popstate"));
    expect(seen).toBe("/pop");
  });

  it("is idempotent: a second attach() adds no duplicate click listener", () => {
    r.attach(); // guarded no-op; must NOT add a second listener
    r.register((p) => p === "/idem", () => {});
    document.body.innerHTML = '<a id="i" href="/idem" data-spa>x</a>';
    let changeCount = 0;
    const onChange = () => { changeCount += 1; };
    document.addEventListener("fess:route:change", onChange);
    try {
      clickEvent(document.getElementById("i"));
    } finally {
      document.removeEventListener("fess:route:change", onChange);
    }
    // A single click → exactly one navigate → one dispatch → one route:change.
    expect(changeCount).toBe(1);
    expect(location.pathname).toBe("/idem");
  });
});
