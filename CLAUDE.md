# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Fess is an Enterprise Search Server built on OpenSearch. It's a Java-based web application that crawls and indexes documents from various sources and provides full-text search capabilities. Licensed under Apache License 2.0.

**Key Capabilities:**
- Full-text search with OpenSearch backend
- Web, file system, and data store crawling
- Multi-format document support (Office, PDF, etc.)
- Admin GUI for configuration
- REST API for programmatic access
- SSO integration (OIDC, SAML, SPNEGO, Entra ID)
- i18n support (20+ languages)

## Tech Stack

| Component | Technology |
|-----------|------------|
| Web Framework | LastaFlute (MVC framework) |
| DI Container | Lasta Di |
| Data Access | DBFlute (type-safe ORM for OpenSearch) |
| Search Engine | OpenSearch |
| App Server | Embedded Tomcat |
| Crawler | fess-crawler library |
| Scheduler | Lasta Job |
| Logging | Log4j2 |
| Testing | JUnit 4/5, UTFlute, REST Assured |

## Development Commands

### Setup
```bash
mvn antrun:run          # Download OpenSearch plugins (required before first build)
mvn dbflute:download    # One-time setup for DBFlute
mvn dbflute:freegen     # Generate DBFlute source code
mvn license:format      # Add license headers
```

### Build
```bash
mvn package             # Standard build
mvn clean package       # Clean build
mvn rpm:rpm             # Build .rpm package
mvn jdeb:jdeb           # Build .deb package
```

### Testing
```bash
mvn test                                    # Run unit tests (*Test.java)
mvn test -Dtest=ClassName                   # Run single unit test

# Integration tests (*Tests.java) - requires running Fess server
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"
```

### Running
```bash
./bin/fess              # From command line
# Or run org.codelibs.fess.FessBoot from IDE
# Access: http://localhost:8080/ (Admin: admin/admin)
```

### Code Formatting
```bash
mvn formatter:format    # Format code
mvn license:format      # Add license headers
```

## Directory Structure

```
src/main/java/org/codelibs/fess/
├── FessBoot.java              # Application entry point
├── Constants.java             # Central application constants
├── app/
│   ├── web/                   # Controllers (Actions)
│   │   ├── base/              # Base action classes (FessBaseAction, FessAdminAction)
│   │   ├── admin/             # Admin controllers ({feature}/ with Action, Forms)
│   │   └── api/               # API controllers
│   ├── service/               # Business logic services
│   ├── pager/                 # Pagination handlers
│   └── job/                   # Background jobs
├── api/                       # REST API infrastructure
├── opensearch/                # DBFlute integration for OpenSearch
│   ├── config/                # Config index (crawl configs, schedules)
│   ├── log/                   # Log index (search logs, click logs)
│   └── user/                  # User index (users, groups, roles)
├── helper/                    # Cross-cutting utilities (~40+ helpers)
├── crawler/                   # Crawling engine (processor, transformer, service)
├── sso/                       # SSO implementations (oic, saml, spnego, entraid)
├── auth/                      # Authentication management
├── ldap/                      # LDAP integration
├── filter/                    # Servlet filters
├── validation/                # Custom validators
├── dict/                      # Dictionary management
└── ds/                        # Data store connectors

src/main/resources/
├── fess_config.properties     # Main configuration
├── app.xml, fess.xml          # DI configuration
├── fess_*.xml                 # Feature-specific DI
├── fess_label_*.properties    # UI labels (i18n)
├── fess_message_*.properties  # Validation messages (i18n)
└── fess_indices/              # OpenSearch index mappings

src/main/webapp/WEB-INF/view/  # JSP templates
src/test/java/.../it/          # Integration tests (*Tests.java)
```

## Architecture Patterns

### Action (Controller)
- Hierarchy: `TypicalAction` → `FessBaseAction` → `FessAdminAction`/`FessSearchAction`
- `@Execute` marks web endpoints
- `@Resource` for DI
- `@Secured({ "role", "role-view" })` for authorization
- Return `HtmlResponse` for JSP, `JsonResponse` for APIs

### Service
- Inject behaviors (Bhv) for data access
- Use `OptionalEntity<T>` for nullable returns
- Plain classes (no extends/implements)

### Helper
- Stateless utility classes
- Access via `ComponentUtil.getXyzHelper()`
- Named with "Helper" suffix

### DBFlute Generated Code
```
opensearch/{index}/
├── bsentity/, bsbhv/    # Base classes (DO NOT EDIT)
├── exentity/, exbhv/    # Extended classes (customize here)
└── cbean/               # Condition beans (query builders)
```

### Form Classes
- POJOs with public fields (no getters/setters)
- Validation: `@Required`, `@Size`, `@ValidateTypeFailure`, `@Pattern`
- Include `crudMode` field for CRUD operations

## Security and Authentication

- `@Secured` annotation with role array (`"admin-user"`, `"admin-user-view"`)
- Role-based query filtering via `RoleQueryHelper`
- Authentication: Local (UserService), LDAP, OIDC, SAML, SPNEGO, Entra ID
- Security features: AES encryption, SHA256 digest, LDAP injection prevention, password policy, rate limiting

## Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `UserService`, `FessBaseAction` |
| Methods | camelCase + verb | `getUserList()`, `setupHtmlData()` |
| Constants | UPPER_SNAKE_CASE | `DEFAULT_PAGE_SIZE` |
| Fields | camelCase | `userPager`, `fessConfig` |

## Log Message Guidelines

- Logger: `LogManager.getLogger(ClassName.class)`
- Format: `key=value` (e.g., `userId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Mask sensitive values (passwords, tokens)

## Important Patterns for AI Assistants

### Do's
1. Use `@Resource` for field injection (not constructor injection)
2. Access helpers via `ComponentUtil.getXyzHelper()`
3. Use `OptionalEntity<T>` for nullable entity returns
4. Use DBFlute behavior classes (Bhv) for data access
5. Put user-facing strings in `fess_label_*.properties`
6. Run `mvn formatter:format` and `mvn license:format` before committing

### Don'ts
1. Don't edit files in `bsentity/` or `bsbhv/` (generated code)
2. Don't use direct OpenSearch client in business code (use Bhv classes)
3. Don't hardcode strings that should be internationalized
4. Don't skip validation in form processing
5. Don't log sensitive data (passwords, tokens, credentials)

### Common Files to Check
| Task | Files to Review |
|------|-----------------|
| Add admin feature | `app/web/admin/*/`, `app/service/`, `webapp/WEB-INF/view/admin/` |
| Add API endpoint | `api/`, `app/web/api/` |
| Modify search | `helper/SearchHelper.java`, `helper/QueryHelper.java` |
| Add crawl config | `opensearch/config/`, `crawler/` |
| Add SSO | `sso/`, `fess_sso.xml` |
| Add i18n | `fess_label_*.properties`, `fess_message_*.properties` |

## External Resources

- **Documentation**: https://fess.codelibs.org/
- **GitHub**: https://github.com/codelibs/fess
- **Issues**: https://github.com/codelibs/fess/issues
