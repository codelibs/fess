// @vitest-environment node
// SPDX-License-Identifier: Apache-2.0
// Runs format.js under the node environment (no jsdom) to prove that a missing
// DOM surfaces as a thrown error rather than being laundered into "unsafe URL".
// Importing format.js must succeed here — it uses window/document only when a
// function is called, never at module scope.

import { describe, it, expect } from "vitest";
import { isSafeHref } from "../../../../main/webapp/themes/bootstrap/assets/format.js";

describe("isSafeHref without a DOM", () => {
  it("throws (not returns false) when window is unavailable", () => {
    // In the node environment `window` is undefined, so `new URL(cleaned,
    // window.location.href)` raises a ReferenceError — NOT a TypeError. The
    // blanket catch used to swallow it and report the URL as unsafe; the fix
    // only swallows TypeError (malformed URL) and rethrows everything else, so
    // a broken environment can no longer masquerade as an unsafe link.
    expect(() => isSafeHref("https://example.com/")).toThrow();
  });
});
