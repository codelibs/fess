// SPDX-License-Identifier: Apache-2.0
import { describe, expect, it } from "vitest";
import { readFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

// The theme's stylesheet is plain CSS, so the check reads the shipped file as text. fileURLToPath is given the URL as a
// STRING: the suite runs under jsdom, whose global URL is not node's. src/test/js/themes/bootstrap/ -> repo root is five levels up.
const repoRoot = resolve(dirname(fileURLToPath(import.meta.url)), "../../../../..");
const css = readFileSync(join(repoRoot, "src/main/webapp/themes/bootstrap/assets/styles.css"), "utf8");
const reducedMotion = [...css.matchAll(/@media \(prefers-reduced-motion: reduce\) \{([\s\S]*?)\n\}/g)].map(m => m[1]).join("\n");

describe("styles.css under prefers-reduced-motion: reduce", () => {
  it("stops the slide-in of the search options drawer", () => {
    // the drawer animates `right` with `transition: all .4s`; a visitor who asked for less motion gets it at once
    expect(css).toMatch(/#searchOptions \.container \{[^}]*transition: all \.4s/);
    expect(reducedMotion).toMatch(/#searchOptions \.container \{ transition: none; \}/);
  });

  it("keeps the existing result card exemption", () => {
    expect(reducedMotion).toMatch(/\.result-card \{ transition: none; \}/);
  });
});
