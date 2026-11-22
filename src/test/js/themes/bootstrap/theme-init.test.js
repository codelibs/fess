// SPDX-License-Identifier: Apache-2.0
// Executable tests for theme-init.js — the classic head script that makes the theme follow
// the system's light/dark setting. It is an IIFE with no exports, so every case stubs
// window.matchMedia, resets the module registry and imports the file again to run it.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";

const THEME_INIT = "../../../../main/webapp/themes/bootstrap/assets/theme-init.js";

/** Stub matchMedia with a controllable dark query; returns a setter that fires "change". */
function stubMatchMedia(initiallyDark) {
  const listeners = [];
  const mq = {
    matches: initiallyDark,
    addEventListener: (type, fn) => { if (type === "change") listeners.push(fn); },
  };
  vi.stubGlobal("matchMedia", vi.fn((q) => {
    expect(q).toBe("(prefers-color-scheme: dark)");
    return mq;
  }));
  return (dark) => { mq.matches = dark; listeners.forEach((fn) => fn({ matches: dark })); };
}

async function run() {
  vi.resetModules();
  await import(THEME_INIT);
}

const root = document.documentElement;

beforeEach(() => {
  root.setAttribute("data-bs-theme", "light");
  root.removeAttribute("data-theme");
});

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("theme-init.js", () => {
  it("keeps light when the system is light", async () => {
    stubMatchMedia(false);
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("light");
  });

  it("switches to dark when the system is dark", async () => {
    stubMatchMedia(true);
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("dark");
  });

  it("follows the system when it changes while the page is open", async () => {
    const set = stubMatchMedia(false);
    await run();
    set(true);
    expect(root.getAttribute("data-bs-theme")).toBe("dark");
    set(false);
    expect(root.getAttribute("data-bs-theme")).toBe("light");
  });

  it("an explicit data-theme wins over a dark system", async () => {
    stubMatchMedia(true);
    root.setAttribute("data-theme", "light");
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("light");
  });

  it("an explicit data-theme wins over a light system, also after a change", async () => {
    const set = stubMatchMedia(false);
    root.setAttribute("data-theme", "dark");
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("dark");
    set(false);
    expect(root.getAttribute("data-bs-theme")).toBe("dark");
  });

  it("ignores a data-theme value that is neither light nor dark", async () => {
    stubMatchMedia(true);
    root.setAttribute("data-theme", "sepia");
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("dark");
  });

  it("stays light when matchMedia does not exist", async () => {
    vi.stubGlobal("matchMedia", undefined);
    await run();
    expect(root.getAttribute("data-bs-theme")).toBe("light");
  });
});
