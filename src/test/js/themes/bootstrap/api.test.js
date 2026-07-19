// SPDX-License-Identifier: Apache-2.0
// Behavioural tests for the /api/v2 client. Each test imports a fresh copy of
// the module (vi.resetModules) so its module-level state (config, csrfToken,
// authenticated) never leaks between cases, and drives it through a stubbed
// global fetch.

import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { jsonResponse, badJsonResponse, streamResponse, installFetch } from "../../helpers/net.js";

const API_PATH = "../../../../main/webapp/themes/bootstrap/assets/api.js";

/** Resolve `{ response: env }` from a stubbed fetch. */
const envelope = (env) => jsonResponse({ response: env });

let api;
beforeEach(async () => {
  vi.resetModules();
  api = await import(API_PATH);
});
afterEach(() => {
  vi.unstubAllGlobals();
});

describe("ApiError / NetworkError", () => {
  it("ApiError carries code/httpStatus/details and is an Error", () => {
    const e = new api.ApiError("BAD", "message", 404, { field: "x" });
    expect(e).toBeInstanceOf(Error);
    expect(e.name).toBe("ApiError");
    expect(e.code).toBe("BAD");
    expect(e.httpStatus).toBe(404);
    expect(e.details).toEqual({ field: "x" });
  });

  it("ApiError.details defaults to null", () => {
    expect(new api.ApiError("X", "m", 500).details).toBeNull();
  });

  it("NetworkError takes its message from the cause, or a default", () => {
    expect(new api.NetworkError(new Error("boom")).message).toBe("boom");
    expect(new api.NetworkError().message).toBe("Network error");
    expect(new api.NetworkError(new Error("x")).name).toBe("NetworkError");
  });
});

describe("get", () => {
  it("returns the unwrapped envelope on success", async () => {
    installFetch(async () => envelope({ status: 0, hits: [1, 2] }));
    await expect(api.get("/search")).resolves.toEqual({ status: 0, hits: [1, 2] });
  });

  it("builds the query string: skips null, repeats arrays", async () => {
    const fetchMock = installFetch(async () => envelope({ status: 0 }));
    await api.get("/search", { q: "x", skip: null, label: ["a", "b"] });
    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, opts] = fetchMock.mock.calls[0];
    expect(url).toBe("/api/v2/search?q=x&label=a&label=b");
    expect(opts.method).toBe("GET");
    expect(opts.headers.Accept).toBe("application/json");
  });

  it("throws ApiError from an error envelope", async () => {
    installFetch(async () => jsonResponse({ response: { status: 1, error: { code: "E", message: "bad" } } }, { status: 400 }));
    await expect(api.get("/x")).rejects.toMatchObject({ name: "ApiError", code: "E", httpStatus: 400 });
  });

  it("throws ApiError(PROTOCOL) when the envelope is missing", async () => {
    installFetch(async () => jsonResponse({ nope: true }));
    await expect(api.get("/x")).rejects.toMatchObject({ code: "PROTOCOL" });
  });

  it("throws ApiError(NETWORK) on invalid JSON", async () => {
    installFetch(async () => badJsonResponse());
    await expect(api.get("/x")).rejects.toMatchObject({ code: "NETWORK", message: "Invalid JSON" });
  });

  it("wraps a fetch rejection in NetworkError", async () => {
    installFetch(async () => { throw new Error("offline"); });
    await expect(api.get("/x")).rejects.toBeInstanceOf(api.NetworkError);
  });

  it("re-throws an AbortError unchanged (superseded request)", async () => {
    installFetch(async () => { const e = new Error("aborted"); e.name = "AbortError"; throw e; });
    await expect(api.get("/x")).rejects.toMatchObject({ name: "AbortError" });
  });
});

describe("post", () => {
  it("POSTs JSON with the CSRF header from module state", async () => {
    api.setCsrfToken("tok-123");
    const fetchMock = installFetch(async () => envelope({ status: 0, ok: true }));
    await api.post("/favorites", { id: "d1" });
    const [url, opts] = fetchMock.mock.calls[0];
    expect(url).toBe("/api/v2/favorites");
    expect(opts.method).toBe("POST");
    expect(opts.headers["X-Fess-CSRF-Token"]).toBe("tok-123");
    expect(opts.body).toBe(JSON.stringify({ id: "d1" }));
  });

  it("wraps any fetch rejection in NetworkError (no AbortError special case)", async () => {
    installFetch(async () => { const e = new Error("x"); e.name = "AbortError"; throw e; });
    await expect(api.post("/x", {})).rejects.toBeInstanceOf(api.NetworkError);
  });
});

describe("init + state accessors", () => {
  it("init stores the config envelope and csrf token", async () => {
    installFetch(async () => envelope({ status: 0, csrf_token: "abc", search: { enabled: true } }));
    await api.init();
    expect(api.getConfig()).toMatchObject({ csrf_token: "abc", search: { enabled: true } });
    expect(api.getCsrfToken()).toBe("abc");
  });

  it("has null config and empty csrf before init", () => {
    expect(api.getConfig()).toBeNull();
    expect(api.getCsrfToken()).toBe("");
  });

  it("setCsrfToken coerces nullish to empty string", () => {
    api.setCsrfToken("t");
    expect(api.getCsrfToken()).toBe("t");
    api.setCsrfToken(null);
    expect(api.getCsrfToken()).toBe("");
  });

  it("setAuthenticated coerces to boolean; defaults false", () => {
    expect(api.isAuthenticated()).toBe(false);
    api.setAuthenticated("yes");
    expect(api.isAuthenticated()).toBe(true);
    api.setAuthenticated(0);
    expect(api.isAuthenticated()).toBe(false);
  });
});

describe("sseStream", () => {
  /** Run a stream to completion, collecting events; resolves on eof or error. */
  const collect = (chunks, opts) =>
    new Promise((resolve) => {
      const events = [];
      installFetch(async () => streamResponse(chunks, opts));
      api.sseStream(
        "/chat/stream",
        { q: "hi" },
        (ev) => { events.push(ev); if (ev.type === "eof") resolve({ events }); },
        (err) => resolve({ events, err })
      );
    });

  it("parses framed events and emits a terminal eof", async () => {
    const { events } = await collect([
      "event: message\ndata: {\"text\":\"hi\"}\n\n",
      "event: done\ndata: bye\n\n",
    ]);
    expect(events[0]).toEqual({ type: "message", data: { text: "hi" } });
    expect(events[1]).toEqual({ type: "done", data: "bye" }); // non-JSON data → raw string
    expect(events[events.length - 1]).toEqual({ type: "eof", data: null });
  });

  it("reassembles a frame split across reads and concatenates multi-data lines", async () => {
    const { events } = await collect(["event: msg\nda", "ta: a\ndata: b\n\n"]);
    expect(events[0]).toEqual({ type: "msg", data: "a\nb" });
  });

  it("flushes a trailing frame with no terminating blank line", async () => {
    const { events } = await collect(["event: last\ndata: 7\n\n", "event: tail\ndata: 9"]);
    expect(events).toContainEqual({ type: "tail", data: 9 });
  });

  it("reports an ApiError when the response is not ok", async () => {
    const { err } = await collect([], { ok: false, status: 500 });
    expect(err).toMatchObject({ name: "ApiError", httpStatus: 500 });
  });

  it("aborts with BUFFER_OVERFLOW past the 1 MiB cap", async () => {
    const { err } = await collect(["x".repeat(1_048_577)]);
    expect(err).toMatchObject({ code: "BUFFER_OVERFLOW" });
  });
});
