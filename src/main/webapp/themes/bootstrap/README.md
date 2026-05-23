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

Copy this directory, rename it, edit `theme.yml#name` to match, and
upload as a ZIP via `/admin/theme/`. The reserved name `bootstrap`
must remain on this bundled directory.

## License

Apache-2.0 — same as Fess.
