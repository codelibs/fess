// SPDX-License-Identifier: Apache-2.0
// Network stubs for the bootstrap theme JS tests: minimal Response-like objects
// and a fetch installer. Node 22 provides global fetch; these let a test drive
// it deterministically without real I/O.

import { vi } from "vitest";

/** A Response-like object whose json() resolves to `body`. */
export function jsonResponse(body, { status = 200, ok } = {}) {
  return {
    ok: ok === undefined ? status >= 200 && status < 300 : ok,
    status,
    json: async () => body,
  };
}

/** A Response-like object whose json() rejects (invalid JSON body). */
export function badJsonResponse({ status = 200 } = {}) {
  return {
    ok: status >= 200 && status < 300,
    status,
    json: async () => {
      throw new SyntaxError("Unexpected token");
    },
  };
}

/**
 * A Response-like object exposing a readable body that yields `chunks`
 * (strings, UTF-8 encoded) one per read(), for testing fetch-based SSE.
 */
export function streamResponse(chunks, { status = 200, ok = true } = {}) {
  const encoder = new TextEncoder();
  const queue = chunks.map((c) => encoder.encode(c));
  let i = 0;
  let cancelled = false;
  return {
    ok,
    status,
    json: async () => ({}),
    body: {
      getReader() {
        return {
          async read() {
            if (cancelled || i >= queue.length) return { done: true, value: undefined };
            return { done: false, value: queue[i++] };
          },
          cancel() {
            cancelled = true;
          },
        };
      },
    },
  };
}

/**
 * Install a stub global fetch. `impl` receives (url, options) and returns a
 * Response-like object (or rejects). Returns the vi mock for assertions.
 * Pair with vi.unstubAllGlobals() in afterEach.
 */
export function installFetch(impl) {
  const fn = vi.fn(impl);
  vi.stubGlobal("fetch", fn);
  return fn;
}
