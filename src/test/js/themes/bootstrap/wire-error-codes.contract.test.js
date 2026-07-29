// SPDX-License-Identifier: Apache-2.0
// Source contract: a comparison against a v2 API error code must accept the
// LOWERCASE wire code the server actually sends.
//
// V2ErrorCode pairs a SCREAMING_SNAKE_CASE Java constant with a lowercase
// snake_case wire code (AUTH_REQUIRED -> "auth_required"). V2EnvelopeWriter puts
// code.code() — the wire code — onto the error envelope, api.js copies it onto the
// thrown ApiError verbatim, and nothing in the SPA normalises the case. So a branch
// that compares err.code only to the Java CONSTANT NAME is dead: it can never match
// a real server response, and the user silently gets the wrong message (or, where a
// `|| e.httpStatus === 4xx` fallback exists, the right message for the wrong reason
// — which is why the defect survived so long).
//
// Writing a behavioural test for each of the ~40 comparison sites is not practical,
// and behavioural tests only cover the sites someone remembered to write one for.
// This suite instead reads the shipped source and fails on ANY uppercase-only
// comparison, so a newly added branch cannot reintroduce the defect. The companion
// behavioural suites (auth.test.js / profile.test.js / search.test.js) stay: this
// contract proves a comparison is not misspelled, only they prove it produces the
// right message.
//
// The enum table is derived from V2ErrorCode.java rather than hardcoded, so adding a
// constant there automatically extends the contract instead of quietly escaping it.

import { describe, it, expect } from "vitest";
import { readFileSync, readdirSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

// fileURLToPath is given the URL as a STRING: the suite runs under the jsdom
// environment, whose global URL is whatwg-url rather than node's, and node's
// fileURLToPath rejects a foreign URL instance ("The URL must be of scheme file").
// src/test/js/themes/bootstrap/ -> repo root is five levels up.
const repoRoot = resolve(dirname(fileURLToPath(import.meta.url)), "../../../../..");
const ASSETS = join(repoRoot, "src/main/webapp/themes/bootstrap/assets");
const V2_ERROR_CODE_JAVA = join(repoRoot, "src/main/java/org/codelibs/fess/api/v2/V2ErrorCode.java");

/**
 * Uppercase string literals api.js mints CLIENT-SIDE that are deliberately NOT
 * subject to this contract:
 *
 *   NETWORK / PROTOCOL  — transport and envelope-shape failures detected before any
 *                         server error code exists (invalid JSON, missing envelope).
 *   UNKNOWN             — the fallback when the server omits error.code entirely
 *                         (`new ApiError(err.code || "UNKNOWN", ...)`).
 *   HTTP_ERROR          — a non-JSON HTTP failure with no v2 envelope at all.
 *   BUFFER_OVERFLOW     — a streaming-reader guard, purely local.
 *
 * These never travel over the wire, so there is no lowercase counterpart to compare
 * against and no server spelling they could disagree with. They are listed here (a)
 * to document why they are exempt and (b) so the disjointness assertion below fails
 * loudly if a future V2ErrorCode constant ever collides with one of these names —
 * at which point the same literal would mean two different things.
 */
const CLIENT_SENTINELS = ["NETWORK", "PROTOCOL", "UNKNOWN", "HTTP_ERROR", "BUFFER_OVERFLOW"];

/** Parse `CONSTANT_NAME("wire_code", 400),` out of the V2ErrorCode enum body. */
function readWireCodes() {
  const src = readFileSync(V2_ERROR_CODE_JAVA, "utf8");
  const map = new Map();
  for (const m of src.matchAll(/^\s{4}([A-Z][A-Z0-9_]*)\("([a-z0-9_]+)",\s*\d+\)/gm)) {
    map.set(m[1], m[2]);
  }
  return map;
}

/**
 * Every `<something>.code === "LITERAL"` / `code === "LITERAL"` comparison in a file.
 * `===` binds tighter than `||`/`&&`, so each match is one complete operand of the
 * surrounding condition.
 */
const COMPARISON = /([A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)*)\s*===\s*"([A-Z][A-Z0-9_]*)"/g;

const assetFiles = readdirSync(ASSETS).filter((f) => f.endsWith(".js")).sort();

describe("V2ErrorCode wire-code table", () => {
  const wireCodes = readWireCodes();

  it("parses every constant out of V2ErrorCode.java", () => {
    // A parse failure would make every assertion below vacuously green.
    expect(wireCodes.size).toBeGreaterThanOrEqual(12);
    expect(wireCodes.get("AUTH_REQUIRED")).toBe("auth_required");
    expect(wireCodes.get("RATE_LIMITED")).toBe("rate_limited");
    expect(wireCodes.get("INVALID_REQUEST")).toBe("invalid_request");
  });

  it("emits only lowercase snake_case wire codes", () => {
    for (const [name, code] of wireCodes) {
      expect(code, `${name} wire code`).toMatch(/^[a-z][a-z0-9_]*$/);
      expect(code, `${name} wire code must differ from the constant name`).not.toBe(name);
    }
  });

  it("keeps the client-side sentinels disjoint from the wire codes", () => {
    for (const sentinel of CLIENT_SENTINELS) {
      expect(wireCodes.has(sentinel), `${sentinel} is both a client sentinel and a V2ErrorCode`).toBe(false);
    }
  });
});

describe("theme JS compares the lowercase wire codes", () => {
  const wireCodes = readWireCodes();

  /**
   * Collect every uppercase wire-code comparison that does NOT also test the
   * lowercase spelling within the same condition. The convention every fixed site
   * follows is `x.code === "lower" || x.code === "UPPER"` on ONE line, so a
   * same-line check is both sufficient and deliberately strict: it keeps the two
   * spellings adjacent and reviewable instead of scattered across a condition.
   */
  function violations(file) {
    const lines = readFileSync(join(ASSETS, file), "utf8").split("\n");
    const found = [];
    let checked = 0;
    lines.forEach((line, i) => {
      for (const m of line.matchAll(COMPARISON)) {
        const [, lhs, upper] = m;
        if (!wireCodes.has(upper)) continue; // client sentinel or unrelated literal
        checked++;
        const lower = wireCodes.get(upper);
        if (!new RegExp(`===\\s*"${lower}"`).test(line)) {
          found.push(`${file}:${i + 1}  ${lhs} === "${upper}" without "${lower}"\n    ${line.trim()}`);
        }
      }
    });
    return { found, checked };
  }

  it("scans the real shipped assets (guards against a vacuous pass)", () => {
    expect(assetFiles.length).toBeGreaterThan(5);
    const total = assetFiles.reduce((n, f) => n + violations(f).checked, 0);
    expect(total, "no wire-code comparisons found at all — the scan is broken").toBeGreaterThan(5);
  });

  it.each(assetFiles)("%s pairs every uppercase wire code with its lowercase form", (file) => {
    const { found } = violations(file);
    expect(found, `uppercase-only v2 error-code comparison(s):\n${found.join("\n")}`).toEqual([]);
  });
});
