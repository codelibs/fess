# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Fess is an Enterprise Search Server built on OpenSearch. It's a Java-based web application (version 15.5.0-SNAPSHOT) that crawls and indexes documents from various sources and provides full-text search capabilities. The project has been in development since 2009 and is licensed under Apache License 2.0.

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

## Directory Structure

```
src/main/java/org/codelibs/fess/
├── FessBoot.java              # Application entry point (extends TomcatBoot)
├── Constants.java             # Central application constants
├── app/
│   ├── web/                   # User-facing controllers (Actions)
│   │   ├── base/              # Base action classes
│   │   │   ├── FessBaseAction.java
│   │   │   ├── FessAdminAction.java
│   │   │   └── FessLoginAction.java
│   │   ├── admin/             # Admin controllers (one package per feature)
│   │   │   └── {feature}/     # Contains Action, CreateForm, EditForm, SearchForm
│   │   └── api/               # API controllers
│   ├── service/               # Business logic services
│   ├── pager/                 # Pagination handlers
│   └── job/                   # Background jobs (CrawlJob, SuggestJob, etc.)
├── api/                       # REST API infrastructure
│   ├── WebApiManager.java     # API interface
│   └── BaseApiManager.java    # Base implementation with FormatType
├── opensearch/                # DBFlute integration for OpenSearch
│   ├── config/                # Config index (crawl configs, schedules)
│   ├── log/                   # Log index (search logs, click logs)
│   ├── user/                  # User index (users, groups, roles)
│   └── common/                # Shared OpenSearch utilities
├── helper/                    # Cross-cutting utilities (~40+ helpers)
├── crawler/                   # Crawling engine
│   ├── processor/             # Response/document processing
│   ├── transformer/           # Document transformation
│   └── service/               # Crawler services
├── sso/                       # SSO implementations
│   ├── oic/                   # OpenID Connect
│   ├── saml/                  # SAML2
│   ├── spnego/                # Kerberos/SPNEGO
│   └── entraid/               # Microsoft Entra ID
├── auth/                      # Authentication management
├── ldap/                      # LDAP integration
├── filter/                    # Servlet filters (CORS, encoding, rate limiting)
├── validation/                # Custom validators
├── exception/                 # Custom exception classes
├── dict/                      # Dictionary management (synonyms, stop words)
├── annotation/                # Custom annotations (@Secured)
├── entity/                    # Domain entities
├── util/                      # Utility classes
├── script/                    # Script execution
├── thumbnail/                 # Thumbnail generation
├── storage/                   # Storage abstraction
└── ds/                        # Data store connectors

src/main/resources/
├── fess_config.properties     # Main configuration
├── fess_env.properties        # Environment configuration
├── app.xml                    # Main DI configuration
├── fess.xml                   # Fess-specific DI
├── fess_*.xml                 # Feature-specific DI (api, crawler, job, sso, etc.)
├── fess_label_*.properties    # UI labels (i18n - 20+ languages)
├── fess_message_*.properties  # Validation messages (i18n)
├── log4j2.xml                 # Logging configuration
├── tika.xml                   # Apache Tika configuration
└── fess_indices/              # OpenSearch index mappings

src/main/webapp/WEB-INF/view/  # JSP templates

src/test/
├── java/org/codelibs/fess/
│   ├── it/                    # Integration tests (*Tests.java)
│   └── ...                    # Unit tests (*Test.java)
└── resources/                 # Test data and fixtures
```

## Architecture Patterns

### Action (Controller) Hierarchy
```
TypicalAction (LastaFlute)
└── FessBaseAction (abstract)
    ├── FessAdminAction (abstract)
    │   └── Admin*Action classes
    ├── FessLoginAction (abstract)
    │   └── LoginAction
    └── FessSearchAction (abstract)
        └── Search-related actions
```

### Action Pattern
```java
public class AdminUserAction extends FessAdminAction {
    @Resource
    protected UserService userService;

    @Execute
    @Secured({ "admin-user", "admin-user-view" })
    public HtmlResponse index() {
        return asListHtml();
    }

    @Execute
    @Secured({ "admin-user" })
    public HtmlResponse create(CreateForm form) {
        validate(form, messages -> {}, this::asEditHtml);
        // ... business logic
        return redirect(getClass());
    }
}
```

**Key Conventions:**
- Methods with `@Execute` are web endpoints
- DI via `@Resource` annotation
- `@Secured({ "role", "role-view" })` for authorization
- Return `HtmlResponse` for JSP, `JsonResponse` for APIs
- Validation via `validate()` method with error handler

### Service Pattern
```java
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Resource
    protected UserBhv userBhv;

    public OptionalEntity<User> getUser(String id) {
        return userBhv.selectByPK(id);
    }

    public void store(User user) {
        // Business logic here
        userBhv.insertOrUpdate(user);
    }
}
```

**Key Conventions:**
- Inject behaviors (Bhv) for data access
- Use `OptionalEntity<T>` for nullable returns
- Static Logger via Log4j2
- No extends/implements (plain classes)

### Helper Pattern
```java
public class SearchHelper {
    @Resource
    protected QueryHelper queryHelper;

    public SearchResult search(SearchRequestParams params) {
        // Cross-cutting utility logic
    }
}
```

**Key Conventions:**
- Stateless utility classes
- Injected via DI with `@Resource`
- Access via `ComponentUtil.getSearchHelper()`
- Named with "Helper" suffix

## DBFlute Code Generation

DBFlute generates type-safe data access code for OpenSearch indices:

```bash
mvn dbflute:freegen
```

### Generated Code Structure
```
opensearch/{index}/
├── bsentity/              # Base entities (DO NOT EDIT)
│   └── Bs{Entity}.java
├── exentity/              # Extended entities (customize here)
│   └── {Entity}.java
├── bsbhv/                 # Base behaviors (DO NOT EDIT)
│   └── Bs{Entity}Bhv.java
├── exbhv/                 # Extended behaviors (customize here)
│   └── {Entity}Bhv.java
└── cbean/                 # Condition beans (query builders)
    └── {Entity}CB.java
```

### Behavior Usage
```java
// Query with ConditionBean
PagingResultBean<User> users = userBhv.selectPage(cb -> {
    cb.paging(pageSize, pageNumber);
    cb.query().setName_Equal(searchName);
});

// Single entity lookup
OptionalEntity<User> user = userBhv.selectByPK(id);
```

## Form Classes

Forms are POJOs with public fields and validation annotations:

```java
public class CreateForm {
    @Required
    @Size(max = 100)
    public String name;

    @ValidateTypeFailure
    public Integer crudMode;
}
```

**Key Conventions:**
- Public fields (no getters/setters)
- Validation annotations: `@Required`, `@Size`, `@ValidateTypeFailure`, `@Pattern`
- Include `crudMode` field using `CrudMode` enum for CRUD operations

## Security and Authentication

### Authorization
- `@Secured` annotation with role array
- Pattern: `"admin-user"` (write) and `"admin-user-view"` (read-only)
- Role-based query filtering via `RoleQueryHelper`
- Permission management via `PermissionHelper`

### Authentication Methods
| Method | Class | Use Case |
|--------|-------|----------|
| Local | UserService | Database users |
| LDAP | LdapManager | Directory services |
| OIDC | OpenIdConnectAuthenticator | OAuth2/OpenID Connect |
| SAML | SamlAuthenticator | SAML2 federation |
| SPNEGO | SpnegoAuthenticator | Windows/Kerberos |
| Entra ID | EntraIdAuthenticator | Microsoft Azure |

### Security Features
- Encryption: AES with configurable keys
- Digest: SHA256
- LDAP injection prevention
- Password policy validation
- Access token management
- Rate limiting filter

## Job Scheduler

Built-in jobs in `app.job.*`:
- `CrawlJob` - Main crawling job
- `SuggestJob` - Generate suggestions
- `AggregateLogJob` - Aggregate search logs
- `PurgeLogJob` - Clean old logs
- `PurgeDocJob` - Remove documents
- `GenerateThumbnailJob` - Create thumbnails
- `PingSearchEngineJob` - Health check
- `ScriptExecutor` - Script execution

Configuration via `fess_job.xml` with cron-like scheduling.

## Crawler Integration

**Key Classes:**
- `FessStandardTransformer` - Standard document transformation
- `FessFileTransformer` - File-specific transformation
- `FessUrlQueueService` - URL queue management
- `CrawlingConfigHelper` - Configuration management

**Data Sources:**
- Web (HTTP/HTTPS)
- File system
- Databases (via data config)
- Cloud storage (S3, GCS)
- SMB/CIFS shares

## Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `UserService`, `FessBaseAction` |
| Methods | camelCase + verb | `getUserList()`, `setupHtmlData()` |
| Constants | UPPER_SNAKE_CASE | `DEFAULT_PAGE_SIZE` |
| Fields | camelCase | `userPager`, `fessConfig` |
| Packages | lowercase.dotted | `org.codelibs.fess.app.service` |

## Log Message Guidelines

- Logger declaration: `private static final Logger logger = LogManager.getLogger(ClassName.class)`
- Format parameters as `key=value` (e.g., `sessionId={}`, `url={}`)
- Prefix with `[name]` when context identification is needed
- Use full words, not abbreviations (e.g., "documents" not "docs")
- Log only identifying fields, not entire objects
- Mask sensitive values (passwords, tokens)

```java
logger.info("User login successful: userId={}", userId);
logger.warn("[Crawler] Failed to fetch: url={}, status={}", url, statusCode);
```

## Development Notes

- **Code Style**: Enforced by `mvn formatter:format`
- **License Headers**: Required (Apache 2.0) - use `mvn license:format`
- **Logging**: Log4j2 - `src/main/resources/log4j2.xml`
- **i18n**: All user-facing strings in `fess_label_*.properties`
- **Default Port**: 8080 (configurable via `fess.port` system property)
- **Log Location**: `target/fess-logs/` (dev), `/var/log/fess/` (prod)
- **Admin Login**: `admin/admin`

## Important Patterns for AI Assistants

### Do's
1. Use `@Resource` for field injection (not constructor injection)
2. Access helpers via `ComponentUtil.getXyzHelper()`
3. Use `OptionalEntity<T>` for nullable entity returns
4. Access config via injected `FessConfig` instance
5. Use DBFlute behavior classes (Bhv) for data access
6. Put user-facing strings in `fess_label_*.properties`
7. Use configurable index names, not hardcoded strings
8. Check `@Secured` annotation requirements before accessing restricted actions
9. Run `mvn formatter:format` and `mvn license:format` before committing

### Don'ts
1. Don't edit files in `bsentity/` or `bsbhv/` (generated code)
2. Don't use direct OpenSearch client in business code (use Bhv classes)
3. Don't hardcode strings that should be internationalized
4. Don't skip validation in form processing
5. Don't log sensitive data (passwords, tokens, credentials)
6. Don't add new dependencies without checking existing ones in pom.xml

### Common Files to Check
| Task | Files to Review |
|------|-----------------|
| Add admin feature | `app/web/admin/*/`, `app/service/`, views in `webapp/WEB-INF/view/admin/` |
| Add API endpoint | `api/`, `app/web/api/` |
| Modify search | `helper/SearchHelper.java`, `helper/QueryHelper.java` |
| Add crawl config | `opensearch/config/`, `crawler/` |
| Add SSO | `sso/`, `fess_sso.xml` |
| Change index mapping | `src/main/resources/fess_indices/` |
| Add i18n | `fess_label_*.properties`, `fess_message_*.properties` |

## External Resources

- **Documentation**: https://fess.codelibs.org/
- **GitHub**: https://github.com/codelibs/fess
- **Issues**: https://github.com/codelibs/fess/issues
- **Discussion**: https://discuss.codelibs.org/c/FessEN/
