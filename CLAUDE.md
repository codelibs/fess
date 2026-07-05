# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Fess is an enterprise search server built on OpenSearch. It is a Java web application that crawls and indexes documents from web sites, file systems, and data stores, and provides full-text search through a web UI and a REST API. Licensed under Apache License 2.0.

Key capabilities: full-text search, multi-source crawling, multi-format document support (Office, PDF, etc.), an admin GUI, SSO (LDAP, OIDC, SAML, SPNEGO, Entra ID), and i18n (20+ languages).

## Tech Stack

| Component | Technology |
|-----------|------------|
| Web framework | LastaFlute (MVC) |
| DI container | Lasta Di |
| Data access | DBFlute (type-safe access to OpenSearch) |
| Search engine | OpenSearch |
| App server | Embedded Tomcat |
| Crawler | fess-crawler |
| Scheduler | Lasta Job |
| Testing | JUnit 4/5, UTFlute, REST Assured |

## Development Commands

```bash
# Setup
mvn antrun:run          # Download OpenSearch plugins (required before first build)
mvn dbflute:download    # One-time DBFlute setup
mvn dbflute:freegen     # Regenerate DBFlute source

# Build
mvn package             # Build (use `mvn clean package` for a clean build)
mvn rpm:rpm             # Build .rpm package
mvn jdeb:jdeb           # Build .deb package

# Test
mvn test                            # Unit tests (*Test.java)
mvn test -Dtest=ClassName           # Single unit test
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

# Format (run before committing)
mvn formatter:format
mvn license:format

# Run
./bin/fess              # From a built package, or run org.codelibs.fess.FessBoot from the IDE
# Access http://localhost:8080/ (admin UI: admin/admin)
```

Fess is packaged as a WAR. To install it as a jar so plugins can resolve it as a dependency, temporarily change `<packaging>war</packaging>` to `jar` in pom.xml, run `mvn clean install -DskipTests`, then revert.

## Architecture

Source lives under `src/main/java/org/codelibs/fess/`. `FessBoot` is the entry point. Notable packages: `app/web` (Actions and Forms, with `admin/` and `api/` controllers), `app/service` (business logic), `opensearch/` (DBFlute integration for the `config`, `log`, and `user` indices), `helper/` (stateless utilities), and `crawler/`, `sso/`, `auth/`, `ldap/`. Configuration and i18n resources live under `src/main/resources/` (`fess_config.properties`, `*.xml` DI files, `fess_label_*`/`fess_message_*` properties, `fess_indices/` mappings). JSP views are under `src/main/webapp/WEB-INF/view/`.

### Action (controller)
- Hierarchy: `TypicalAction` → `FessBaseAction` → `FessAdminAction` / `FessSearchAction`
- `@Execute` marks endpoints; `@Resource` for DI; `@Secured({ "role", "role-view" })` for authorization
- Return `HtmlResponse` for JSP, `JsonResponse` for APIs

### Service and Helper
- Services are plain classes; inject behaviors (Bhv) for data access; use `OptionalEntity<T>` for nullable returns
- Helpers are stateless utilities named with a `Helper` suffix, accessed via `ComponentUtil.getXyzHelper()`

### DBFlute (OpenSearch access)
- Under `opensearch/{index}/`: `bsentity`/`bsbhv` are generated base classes — do not edit; customize in `exentity`/`exbhv`; `cbean` holds condition beans (query builders)
- Use Bhv classes for data access rather than the OpenSearch client directly

### Form classes
- POJOs with public fields (no getters/setters)
- Validate with `@Required`, `@Size`, `@ValidateTypeFailure`, `@Pattern`; include a `crudMode` field for CRUD operations

## Security and Authentication

- `@Secured` with a role array; role-based result filtering via `RoleQueryHelper`
- Authentication: local (UserService), LDAP, OIDC, SAML, SPNEGO, Entra ID
- Password hashing: BCrypt (`{bcrypt}$2a$10$...`, Spring Security 5.8 compatible) via `PasswordHashHelper`, configured with `app.password.algorithm` / `app.password.bcrypt.cost`. Legacy unprefixed SHA-256/512/MD5 hashes are verified via `app.digest.algorithm` and re-hashed on next login when `app.password.upgrade.enabled=true` (default). Downgrading to a pre-BCrypt release invalidates `{bcrypt}` passwords — note this in release notes. New password-write paths must call `ComponentUtil.getPasswordHashHelper().encode(plain)`, not `FessLoginAssist.encryptPassword`.

## i18n

The project supports 20+ languages. When changing user-facing strings, labels, or error codes, propagate the change to all `fess_label_*.properties` and `fess_message_*.properties` files (and frontend i18n files).

## Conventions

- Use `@Resource` field injection (not constructor injection); access helpers via `ComponentUtil.getXyzHelper()`
- Use Bhv classes for data access; use `OptionalEntity<T>` for nullable entities
- Put user-facing strings in `fess_label_*.properties`; do not hardcode strings that should be internationalized
- Do not edit generated code (`bsentity`/`bsbhv`); do not use the OpenSearch client directly in business code
- Do not skip form validation; do not log secrets (passwords, tokens, credentials)
- Loggers: `LogManager.getLogger(ClassName.class)`; log as `key=value`, masking sensitive values
- Run `mvn formatter:format` and `mvn license:format` before committing

## External Resources

- Documentation: https://fess.codelibs.org/
- Repository: https://github.com/codelibs/fess
- Issues: https://github.com/codelibs/fess/issues
