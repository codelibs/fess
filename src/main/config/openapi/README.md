# Fess OpenAPI definitions

This directory holds the OpenAPI 3.0 definitions for the user-facing Web API.

```
src/main/config/openapi/
├── README.md           — this file
├── v1/
│   └── openapi-user.yaml  — /api/v1/* (legacy flat responses)
└── v2/
    └── openapi-user.yaml  — /api/v2/* (uniform envelope: { response: { status, ... } })
```

Each version is a **self-contained document**. We deliberately do not share
`components/` between v1 and v2 because:

* v2 wraps every payload in a `response` envelope; v1 does not.
* The `Error` shape differs structurally (envelope `error` object vs. flat
  `error_code` / `message`).
* Even when a field name is identical, future v2 evolution must not be able
  to silently break v1.

Sharing fragments between independent API major versions consistently turns
out to be a foot-gun (small differences leak across; freezing v1 becomes
harder). GitHub's `rest-api-description`, Zalando's RESTful guidelines, and
the Redocly multi-file template all settle on the same rule: one spec per
audience+version.

## Recommended evolution: split components into separate files

The current layout (one YAML per version) is fine for the present size, but
once any of these get big it is worth splitting **inside each version
folder** while keeping the version folders independent.

Target layout when the size warrants it:

```
v2/
├── openapi-user.yaml          — entrypoint: info, servers, tags, paths
└── components/
    ├── schemas/
    │   ├── Envelope.yaml
    │   ├── ErrorEnvelope.yaml
    │   ├── SearchResponse.yaml
    │   ├── ChatRequest.yaml
    │   └── ...
    ├── parameters/
    │   ├── Q.yaml
    │   ├── DocId.yaml
    │   └── CsrfHeader.yaml
    └── responses/
        ├── BadRequest.yaml
        ├── Unauthorized.yaml
        └── ...
```

Inside paths, reference component files with relative `$ref`:

```yaml
parameters:
  - $ref: './components/parameters/Q.yaml'
responses:
  '200':
    content:
      application/json:
        schema: { $ref: './components/schemas/SearchResponse.yaml' }
```

This is supported natively by Redoc, Swagger UI (v5+), Spectral and
openapi-generator. For SDK builds, bundle to a single file first:

```bash
npx -y @redocly/cli bundle src/main/config/openapi/v2/openapi-user.yaml \
    --output target/openapi/v2/openapi-user.bundled.yaml
```

Larger split levels (one file per path, dereferenced output) buy
little for an API of Fess's current size and add review-time friction —
**don't** go there until paths counts exceed ~50.

## Conventions

* `info.version` is the **document's** SemVer, separate from `pom.xml`.
  Bump it only when the API contract changes, not on every Fess patch
  release.
* `operationId` follows `<verb><Resource>[V<n>]` (e.g. `searchV2`,
  `authLoginV2`).
* All JSON payload keys — request bodies, response bodies, and SSE event
  data — use **snake_case** consistently.
* Add new endpoints to existing tags before creating new ones.

## Future work

* Future admin API (when authored) should live as
  `v2/openapi-admin.yaml` (and eventually `v2/components/`) — admin vs.
  user audiences justify separate documents.
* When components are split, add a `redocly.yaml` at this directory's
  root to register each entrypoint, and wire `redocly lint` + a Spectral
  ruleset (`operationId-pattern`, `tag-defined`, `no-empty-servers`) into
  CI.
