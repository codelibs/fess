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

describe("styles.css target size", () => {
  it("keeps each link of the options bar at least 24 x 24 CSS px", () => {
    const rule = css.match(/#options-bar \.badge \{([^}]*)\}/);
    expect(rule).not.toBeNull();
    expect(rule[1]).toMatch(/min-height: 24px/);
    expect(rule[1]).toMatch(/min-width: 24px/);
  });
});

describe("styles.css dark colour scheme", () => {
  it("gives the outline secondary buttons a text colour that reads on the dark page", () => {
    // Bootstrap's own --bs-btn-color of .btn-outline-secondary is #6c757d in both schemes: 3.29:1 on #212529
    const rule = css.match(/\[data-bs-theme="dark"\] \.btn-outline-secondary \{([^}]*)\}/);
    expect(rule).not.toBeNull();
    expect(rule[1]).toMatch(/--bs-btn-color: var\(--bs-secondary-color\)/);
  });

  it("gives the outline danger buttons and the danger text the danger text colour of the dark scheme", () => {
    // Bootstrap's #dc3545 is 3.41:1 on #212529 (Delete of a passkey or a tag, the inline errors)
    expect(css).toMatch(/\[data-bs-theme="dark"\] \.btn-outline-danger \{ --bs-btn-color: var\(--bs-danger-text-emphasis\); \}/);
    expect(css).toMatch(/\[data-bs-theme="dark"\] \.text-danger \{ color: var\(--bs-danger-text-emphasis\) !important; \}/);
  });
});
