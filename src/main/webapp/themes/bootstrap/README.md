# Bootstrap — Fess Reference Static Theme

This directory is the **canonical reference implementation** for a Fess
static theme. It is bundled with the Fess WAR and ships in the RPM/DEB
packages. Activate it by setting `theme.default=bootstrap` in the admin
UI (`/admin/theme/`) or by binding it to a virtual host.

## Layout

```
bootstrap/
├── theme.yml             # manifest (apiVersion: fess.codelibs.org/v1)
├── index.html            # SPA shell — semantic HTML5 + Bootstrap 5
├── thumbnail.png         # shown in /admin/theme/ (≤512KB, ≤512x512)
├── assets/
│   ├── app.js            # entry point; loads modules in order
│   ├── api.js            # centralised fetch wrapper (CSRF, envelope)
│   ├── auth.js           # login, logout, /auth/me probe
│   ├── search.js         # search, suggest, facets, pagination, sort, favorite
│   ├── chat.js           # optional RAG chat (rag_chat_enabled feature flag)
│   ├── i18n.js           # JSON bundle loader; navigator.language → en/ja
│   └── styles.css        # overrides on top of /css/bootstrap.min.css
├── i18n/
│   ├── messages.en.json
│   └── messages.ja.json
└── README.md
```

## API endpoints consumed

All under `/api/v2`. See the Fess static-theme API reference doc for
the full schema. The wrapper in `assets/api.js` is the single source
of truth — third-party themes are encouraged to start by copying it.

| Module | Endpoints |
|---|---|
| `api.js` | `GET /ui/config` |
| `auth.js` | `GET /auth/me`, `POST /auth/login`, `POST /auth/logout` |
| `search.js` | `GET /search`, `GET /suggest-words`, `GET /labels`, `GET /popular-words`, `GET /documents/{id}/favorite`, `POST /documents/{id}/favorite`, `POST /click`, `GET /cache/{id}` (link target) |
| `chat.js` | `POST /chat/stream` (streaming SSE via fetch — see below) |

## Streaming chat (`/chat/stream`)

The `/api/v2/chat/stream` endpoint is **POST-only** and requires the
`X-Fess-CSRF-Token` header. The browser's native `EventSource` API only
supports GET requests and cannot attach custom headers, so it is
incompatible with this endpoint.

Use `api.sseStream(path, body, onEvent, onError)` instead:

```js
import * as api from "./api.js";

const ctrl = api.sseStream("/chat/stream", { q: "my question" }, (event) => {
  // event.type — e.g. "message", "done", "error", "phase"
  // event.data — JSON-parsed payload (or raw string if not valid JSON)
  if (event.type === "message") bubble.textContent += event.data.token ?? "";
  if (event.type === "done")    ctrl.abort(); // tidy up
}, (err) => {
  // err is ApiError (HTTP-level) or NetworkError (offline/DNS).
  console.error(err);
});

// Cancel the stream at any time:
ctrl.abort();
```

The function returns an `AbortController`. Call `.abort()` to cancel
the fetch before the server sends a final `done` event (e.g. on user
navigation or a new submission).

## Context path

Fess 15.9 and later insert `<base href="{context path}/">` right after `<head>` when they
serve `index.html`. Every URL in this theme is relative to that base (`css/bootstrap.min.css`,
`api/v2`, `search?q=…`, `./`), so the theme works when Fess runs under a context path such as
`/fess`. `router.js` matches routes against the path below the base, and `i18n.js` and `help.js`
load their JSON relative to the module URL. Keep new URLs relative: a root-absolute URL
(`/search`) skips the context path. Fragment-only links (`#id`) would resolve against the base,
so the router keeps them on the current page.

`index.html` refers to the theme's own files as `{{themePath}}/assets/…`. Fess replaces
`{{themePath}}` with `themes/<name>` (the `name` in `theme.yml`) when it serves the page, so the
same `index.html` loads the right files under whatever name the theme is installed. Use the
placeholder for any file of the theme that `index.html` references; files loaded from JavaScript
should stay relative to the module URL (`new URL("../i18n/…", import.meta.url)`).

## CSRF

All state-changing requests echo the token returned by `/ui/config` in
the `X-Fess-CSRF-Token` HTTP header. The token rotates on login and
logout; `auth.js` re-reads it from `/ui/config` after each change (or
directly from the logout response body if the server embeds it there).

## XSS-safety

No DOM construction in this theme uses `innerHTML` with dynamic data.
All result cards, facet items, suggest items, pagination, and chat
messages are built with `document.createElement` and `textContent`. The
only `innerHTML` writes are static empty-string clears (`el.innerHTML = ""`).
Theme authors who copy this code should preserve this pattern.

## Customising

1. Copy this directory and rename the copy, e.g. to `mytheme`.
2. Set `name: mytheme` in the copy's `theme.yml`, and bump its `version`.
3. Edit `assets/styles.css`, the logos in `assets/`, and `i18n/messages.*.json` as needed.
   Nothing in `index.html` names the theme directory: its references go through
   `{{themePath}}` (see [Context path](#context-path)), so the copy loads its own files.
4. ZIP the directory contents (with `theme.yml` at the top level) and upload the ZIP at
   `/admin/theme/`, then make it the default theme there or bind it to a virtual host.

The reserved name `bootstrap` must remain on this bundled directory.

## License

Apache-2.0 — same as Fess.
