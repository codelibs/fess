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
| `chat.js` | `POST /chat`, `GET /chat/stream` (SSE) |

## CSRF

All state-changing requests echo the token returned by `/ui/config` in
the `X-Fess-CSRF-Token` HTTP header. The token rotates on login and
logout; `auth.js` re-reads it from `/ui/config` after each change.

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
