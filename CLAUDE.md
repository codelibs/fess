# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Fess is an Enterprise Search Server built on OpenSearch. It's a Java-based web application that crawls and indexes documents from various sources and provides full-text search capabilities.

## Development Commands

### Setup
```bash
# Download OpenSearch plugins (required before first build)
mvn antrun:run

# Generate DBFlute source code (when schema changes)
mvn dbflute:download  # One-time setup
mvn dbflute:freegen
mvn license:format
```

### Build
```bash
# Standard build
mvn package

# Clean build
mvn clean package

# Build distribution packages
mvn rpm:rpm          # .rpm package
mvn jdeb:jdeb        # .deb package
```

### Testing
```bash
# Run unit tests (*Test.java)
mvn test

# Run single unit test
mvn test -Dtest=SearchEngineClientTest

# Run integration tests (*Tests.java in src/test/java/org/codelibs/fess/it/)
# Requires Fess server and OpenSearch running
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

# Run single integration test
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests
```

### Running
```bash
# From IDE: Run main method in org.codelibs.fess.FessBoot
# Access at: http://localhost:8080/
# Admin UI: http://localhost:8080/admin/ (admin/admin)

# From command line (after building)
./bin/fess
```

### Code Formatting
```bash
# Format code
mvn formatter:format

# Add license headers
mvn license:format
```

## Tech Stack

- **Web Framework**: LastaFlute (MVC framework)
- **DI Container**: Lasta Di
- **Data Access**: DBFlute (type-safe ORM for OpenSearch)
- **Search Engine**: OpenSearch
- **App Server**: Embedded Tomcat
- **Crawler**: fess-crawler library
- **Scheduler**: Lasta Job

## Key Directories

```
src/main/java/org/codelibs/fess/
├── FessBoot.java              # Application entry point
├── app/
│   ├── web/                   # User-facing controllers
│   ├── web/admin/             # Admin controllers
│   ├── service/               # Business logic
│   └── job/                   # Background jobs
├── api/                       # REST API endpoints
├── opensearch/                # OpenSearch integration
│   ├── config/                # Config index (crawl configs, schedules)
│   ├── log/                   # Log index
│   ├── user/                  # User index
│   ├── exbhv/                 # Extended behaviors (repositories)
│   └── exentity/              # Extended entities (domain models)
├── helper/                    # Cross-cutting utilities
├── crawler/                   # Crawling engine
├── sso/                       # SSO implementations
└── ds/                        # Data store connectors

src/main/resources/
├── fess_config.properties     # Main config
├── app.xml                    # DI configuration
├── fess_label_*.properties    # UI labels (i18n)
├── fess_message_*.properties  # Validation messages (i18n)
└── fess_indices/              # OpenSearch index mappings

src/main/webapp/WEB-INF/view/  # JSP templates
```

## DBFlute Code Generation

DBFlute generates type-safe data access code for OpenSearch indices:

```bash
mvn dbflute:freegen
```

Generated code:
- `opensearch/{index}/bsentity/` - Base entities (do not edit)
- `opensearch/{index}/exentity/` - Extended entities (customize here)
- `opensearch/{index}/bsbhv/` - Base behaviors (do not edit)
- `opensearch/{index}/exbhv/` - Extended behaviors (customize here)

## Key Patterns

### Actions (Controllers)
- Located in `app.web.*` or `app.web.admin.*`
- Methods with `@Execute` are web endpoints
- DI via `@Resource` annotation
- `HtmlResponse` for JSP, `JsonResponse` for APIs

### Services
- Business logic in `app.service.*`
- Injected into Actions
- Use behavior classes to access OpenSearch

### Helpers
- Cross-cutting utilities in `helper.*`
- Stateless, injected via DI
- Access via `ComponentUtil.getFooHelper()`

## Development Notes

- **Code Style**: Enforced by `mvn formatter:format`
- **License Headers**: Required (Apache 2.0) - use `mvn license:format`
- **Logging**: Log4j2 - `src/main/resources/log4j2.xml`
- **i18n**: All user-facing strings in `fess_label_*.properties`
- **Default Port**: 8080 (configurable via `fess.port` system property)
- **Log Location**: `target/fess-logs/` (dev), `/var/log/fess/` (prod)
- **Admin Login**: `admin/admin`

## Log Message Guidelines

- Format parameters as `key=value` (e.g., `sessionId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Use full words, not abbreviations (e.g., "documents" not "docs")
- Log only identifying fields, not entire objects
