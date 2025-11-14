# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fess is an Enterprise Search Server built on OpenSearch. It's a Java-based web application that crawls and indexes documents from various sources (web, file systems, databases, cloud storage) and provides full-text search capabilities through both a user-facing search UI and an administrative interface.

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
# Standard build (compiles, runs unit tests, packages WAR)
mvn package

# Build distribution packages
mvn package           # Creates .zip in target/releases/
mvn rpm:rpm          # Creates .rpm package
mvn jdeb:jdeb        # Creates .deb package

# Clean build
mvn clean package
```

### Testing
```bash
# Run unit tests only (*Test.java files)
mvn test

# Run integration tests (*Tests.java files in src/test/java/org/codelibs/fess/it/)
# Note: Requires Fess server and OpenSearch to be running
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"

# Run a single integration test
mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201" -Dtest=SearchApiTests

# Run a single unit test
mvn test -Dtest=SearchEngineClientTest
```

### Running the Application
```bash
# Run from IDE: Execute main method in org.codelibs.fess.FessBoot
# Access at: http://localhost:8080/

# Run from command line (after building)
./bin/fess

# In IDE, set VM options if needed:
# -Dfess.log.path=/path/to/logs
# -Dfess.var.path=/path/to/var
```

### Code Formatting
```bash
# Format code according to project standards
mvn formatter:format

# Validate license headers
mvn license:check

# Add license headers
mvn license:format
```

## Architecture Overview

### Tech Stack
- **Web Framework**: LastaFlute (Japanese MVC framework, convention over configuration)
- **DI Container**: Lasta Di (lightweight dependency injection)
- **Data Access**: DBFlute (type-safe ORM-like framework, used with OpenSearch indices)
- **Search Engine**: OpenSearch (stores both search documents and application data)
- **App Server**: Embedded Tomcat (via tomcat-boot library)
- **Crawler**: Custom fess-crawler library
- **Scheduler**: Lasta Job (for crawl jobs and maintenance tasks)

### Application Layers

```
┌─────────────────────────────────────────┐
│   Presentation (app.web.* actions)      │  JSP views, REST APIs
├─────────────────────────────────────────┤
│   Service Layer (app.service.*)         │  Business logic
├─────────────────────────────────────────┤
│   Helper Layer (helper.*)               │  Cross-cutting utilities
├─────────────────────────────────────────┤
│   Data Access (opensearch.*.exbhv)      │  Repository pattern via DBFlute
├─────────────────────────────────────────┤
│   OpenSearch (3 indices)                │  config, log, user indices
└─────────────────────────────────────────┘
```

### Key Package Structure

**src/main/java/org/codelibs/fess/**

- **FessBoot.java** - Application entry point (extends TomcatBoot)
- **app/** - Application layer
  - **web/** - User-facing controllers (search, login, profile)
  - **web/admin/** - ~40 admin controllers (webconfig, fileconfig, scheduler, etc.)
  - **service/** - Business logic services (~50+ services)
  - **job/** - Background job definitions
  - **pager/** - Pagination utilities
- **api/** - REST API layer
  - **engine/** - Search engine API managers
  - **json/** - JSON API endpoints
- **opensearch/** - OpenSearch integration (DBFlute-generated + custom code)
  - **config/** - Configuration index (crawl configs, schedules, dictionaries, etc.)
  - **log/** - Log index (search logs, job logs)
  - **user/** - User index (users, groups, roles)
  - **client/** - Search client implementations
  - **query/** - Custom query builders
  - **exbhv/** - Extended behavior classes (repositories)
  - **exentity/** - Extended entity classes (domain models)
- **helper/** - Cross-cutting helpers (~30+ helpers)
  - IndexingHelper, CrawlingConfigHelper, DocumentHelper, etc.
- **crawler/** - Web crawling engine
  - **processor/** - Content processors
  - **transformer/** - Data transformers
  - **service/** - Crawler services
- **entity/** - Core domain entities
- **exception/** - Custom exception hierarchy
- **auth/** - Authentication framework
  - **chain/** - Authentication chain pattern
- **sso/** - Single Sign-On implementations (AAD, OIDC, SAML, SPNEGO)
- **ds/** - Pluggable Data Store connectors
- **dict/** - Dictionary management
- **mylasta/** - LastaFlute framework configuration
- **util/** - Utility classes

### OpenSearch Indices Architecture

Fess uses OpenSearch for **both** search document storage and application data storage. There are three main index types:

1. **config index** (`opensearch.config.*`) - Application configuration
   - Crawl configurations (web, file, data store)
   - Schedules, dictionaries, labels, roles, etc.

2. **log index** (`opensearch.log.*`) - Application logs
   - Search logs, job logs, user info logs

3. **user index** (`opensearch.user.*`) - User management
   - Users, groups, roles

These indices use DBFlute's code generation to create type-safe entity and behavior classes, similar to traditional ORM but for OpenSearch.

### LastaFlute Action Pattern

Controllers are called "Actions" and follow these conventions:

```java
public class ExampleAction extends FessSearchAction {
    @Resource
    private ExampleService exampleService;

    @Execute
    public HtmlResponse index(ExampleForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_IndexJsp);
        });

        exampleService.doSomething(form);

        return asHtml(path_IndexJsp)
            .renderWith(data -> {
                data.register("result", exampleService.getData());
            });
    }
}
```

- Actions are in `app.web.*` or `app.web.admin.*`
- Methods annotated with `@Execute` are web endpoints
- Forms are validated using LastaFlute's validation framework
- `HtmlResponse` for JSP rendering, `JsonResponse` for APIs
- DI via `@Resource` annotation

### Service Pattern

Services contain business logic and are injected into Actions:

```java
public class ExampleService {
    @Resource
    private ExampleBhv exampleBhv;  // DBFlute behavior (repository)

    public void doSomething() {
        // Use behavior classes to access OpenSearch
        exampleBhv.selectEntity(cb -> {
            cb.query().setId_Equal(id);
        }).ifPresent(entity -> {
            // Process entity
        });
    }
}
```

### Helper Pattern

Helpers provide reusable, cross-cutting functionality:
- Stateless utility classes with complex logic
- Injected via DI
- Examples: `IndexingHelper`, `CrawlingConfigHelper`, `PermissionHelper`

### Dependency Injection

- Configuration files: `src/main/resources/*.xml` (app.xml, fess.xml, etc.)
- Components auto-discovered via package scanning
- Access via `@Resource` injection or `ComponentUtil.getComponent()`
- Static access pattern: `ComponentUtil.getFooHelper()` for framework components

## Important Configuration Files

### Application Configuration
- **src/main/resources/fess_config.properties** - Main application config (search engine URL, cipher keys, crawl settings)
- **src/main/resources/fess_env_*.properties** - Environment-specific configs (web, crawler, suggest, thumbnail)
- **src/main/resources/lasta_di.properties** - DI container settings

### DI Container (XML)
- **src/main/resources/app.xml** - Main DI configuration
- **src/main/resources/fess.xml** - Fess-specific components
- **src/main/resources/fess_*.xml** - Modular configs (api, cors, dict, job, ldap, sso)
- **src/main/resources/crawler_*.xml** - Crawler components
- **src/main/resources/esclient.xml** - OpenSearch client configuration

### Localization
- **src/main/resources/fess_label_*.properties** - UI labels (17 languages: en, ja, ar, cs, de, es, fr, hu, id, it, ko, nl, pl, pt, ro, ru, th, tr, zh_CN, zh_TW)
- **src/main/resources/fess_message_*.properties** - Validation/error messages

### Views
- **src/main/webapp/WEB-INF/view/** - JSP templates
  - **admin/** - Admin UI views
  - **search.jsp** - User search UI
  - **common/** - Shared templates

### OpenSearch
- **src/main/resources/fess_indices/** - Index mapping definitions (JSON)
- **src/main/resources/tika.xml** - Apache Tika configuration for content extraction

### Build & Deployment
- **pom.xml** - Maven dependencies and plugins
- **module.xml** - OpenSearch module management (Ant script)
- **plugin.xml** - OpenSearch plugin management (Ant script)
- **deps.xml** - Additional dependency management (Ant script)

## DBFlute Code Generation

DBFlute is used to generate type-safe data access code for OpenSearch indices:

```bash
# Generate entity and behavior classes
mvn dbflute:freegen
```

Generated code locations:
- **opensearch/{index}/bsentity/** - Base entities (do not edit)
- **opensearch/{index}/exentity/** - Extended entities (customize here)
- **opensearch/{index}/bsbhv/** - Base behaviors (do not edit)
- **opensearch/{index}/exbhv/** - Extended behaviors (customize here)
- **opensearch/{index}/cbean/** - Condition beans for queries

Configuration: `dbflute_fess/dfprop/`

## Testing Patterns

### Unit Tests
- File pattern: `*Test.java`
- Located in `src/test/java/` mirroring `src/main/java/`
- Run with: `mvn test`
- Use JUnit 5 (Jupiter) or JUnit 4
- May extend `UnitFessTestCase` for LastaFlute support

### Integration Tests
- File pattern: `*Tests.java` in `src/test/java/org/codelibs/fess/it/`
- Require running Fess and OpenSearch instances
- Use REST Assured for API testing
- Run with: `mvn test -P integrationTests -Dtest.fess.url="http://localhost:8080" -Dtest.search_engine.url="http://localhost:9201"`

## Common Development Patterns

### Adding a New Admin Feature

1. Create entity in appropriate OpenSearch index (`opensearch.config.exentity`)
2. Create behavior class if needed (`opensearch.config.exbhv`)
3. Create service in `app.service`
4. Create action in `app.web.admin`
5. Create form beans (request/response)
6. Create JSP view in `src/main/webapp/WEB-INF/view/admin/`
7. Add labels to `fess_label_en.properties` (and other languages)
8. Add messages to `fess_message_en.properties`

### Adding a New Crawler/Data Source

1. Implement data store connector in `ds` package
2. Register in `DataStoreFactory`
3. Add configuration entity/behavior
4. Create admin UI (action + view)

### Adding Authentication Method

1. Implement in `sso` package
2. Add to authentication chain in `auth.chain`
3. Configure in `fess_sso.xml`

## Development Notes

- **Code Style**: Maven formatter plugin enforces style (`mvn formatter:format`)
- **License Headers**: Apache 2.0 headers required (`mvn license:format`)
- **Logging**: Log4j2 (`src/main/resources/log4j2.xml`)
- **Internationalization**: All user-facing strings must be in `fess_label_*.properties`
- **Validation**: Use LastaFlute's validation annotations on form beans
- **Transaction Handling**: OpenSearch operations are generally non-transactional; use try-catch for error handling

## Debugging

- **Main Class**: `org.codelibs.fess.FessBoot`
- **Default Port**: 8080 (configurable in tomcat_config.properties or system property `fess.port`)
- **Log Location**: `target/fess-logs/` (development) or `/var/log/fess/` (production)
- **Data Location**: `target/fess-var/` (development)
- **Admin Login**: Default username/password is `admin/admin`

## External Dependencies

Fess integrates with external OpenSearch modules and plugins downloaded via `mvn antrun:run`:
- Modules stored in: `modules/`
- Plugins stored in: `plugins/`

These are managed by `module.xml` and `plugin.xml` Ant scripts.
