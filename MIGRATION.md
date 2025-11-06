# Migration Guide

This guide provides comprehensive instructions for migrating from other enterprise search systems to Fess. Fess offers robust compatibility layers, import tools, and APIs to facilitate smooth transitions from various search platforms.

## Table of Contents

- [Supported Migration Sources](#supported-migration-sources)
- [Pre-Migration Planning](#pre-migration-planning)
- [Migration Methods](#migration-methods)
  - [Google Search Appliance (GSA)](#google-search-appliance-gsa--google-mini)
  - [Google Site Search (GSS) / Google Custom Search](#google-site-search-gss--google-custom-search-gse)
  - [Elasticsearch / OpenSearch](#elasticsearch--opensearch)
  - [Apache Solr](#apache-solr)
  - [Other Search Systems](#other-search-systems)
- [Data Import Methods](#data-import-methods)
- [Configuration Migration](#configuration-migration)
- [API Reference](#api-reference)
- [Troubleshooting](#troubleshooting)
- [Migration Checklist](#migration-checklist)

---

## Supported Migration Sources

Fess provides built-in support and compatibility layers for:

- **Google Search Appliance (GSA)** - Configuration import and API compatibility
- **Google Site Search (GSS) / Google Custom Search (CSE)** - JavaScript replacement
- **Elasticsearch / OpenSearch** - Direct data migration via bulk APIs
- **Apache Solr** - Document import via REST API
- **Database Systems** - Direct connection via DataStore plugins
- **File Systems** - SMB, FTP, local file crawling
- **Custom Systems** - REST API for bulk document indexing

---

## Pre-Migration Planning

### 1. Assessment Phase

Before starting migration, assess your current system:

- **Document Count**: Total number of indexed documents
- **Index Size**: Storage requirements for search indices
- **Crawl Sources**: URLs, file shares, databases being indexed
- **Custom Configurations**: URL patterns, security rules, metadata fields
- **Search Features**: Facets, suggestions, security filtering, ranking rules
- **Integration Points**: Applications consuming search APIs
- **User Permissions**: Access control and label configurations

### 2. Infrastructure Requirements

Ensure your Fess environment meets these requirements:

- **Java**: JDK 17 or later
- **Memory**: Minimum 4GB RAM (8GB+ recommended for production)
- **Storage**: At least 2x your current index size
- **Network**: Access to crawl sources (web servers, file shares, databases)
- **Elasticsearch/OpenSearch**: Compatible version running

### 3. Backup Existing System

Before migration:

1. Export current search configurations
2. Backup existing indices and data
3. Document custom integrations and API endpoints
4. Save user access control settings

---

## Migration Methods

### Google Search Appliance (GSA) / Google Mini

Fess provides comprehensive GSA migration support through configuration import and API compatibility.

#### Configuration Import

Fess can directly import GSA XML configuration files, automatically converting them to native Fess configurations.

**Step 1: Export GSA Configuration**

Export your GSA configuration as XML. A typical GSA configuration includes:
- Collections with URL patterns
- Start URLs and crawl rules
- User agent settings
- Good/bad URL patterns

**Step 2: Import via Fess Admin UI**

1. Log in to Fess Admin Console: `http://<fess-server>:8080/admin`
2. Navigate to **System > Backup**
3. Click **Upload** and select your `gsaconfig.xml` file
4. Click **Import** - Fess will automatically:
   - Parse GSA collections
   - Create corresponding WebConfig or FileConfig entries
   - Convert URL patterns to Fess regex patterns
   - Set up LabelType (access control labels) if defined

**Step 3: Verify Imported Configurations**

After import, verify:
- **Crawler > Web**: Check web crawling configurations
- **Crawler > File**: Check file crawling configurations (SMB, FTP)
- **System > General**: Verify URL patterns and filters

**GSA Configuration Mapping**

| GSA Setting | Fess Equivalent |
|-------------|----------------|
| Collection | WebConfig or FileConfig |
| Start URLs | URLs field in crawl config |
| Good URL Pattern | Included URLs (regex) |
| Bad URL Pattern | Excluded URLs (regex) |
| Follow/Index Pattern | Config Depth and Filter settings |

**Implementation Details**

See the GSA parser implementation: [GsaConfigParser.java](/src/main/java/org/codelibs/fess/util/GsaConfigParser.java)

#### API Compatibility Mode

> **Note**: GSA-compatible search API support may vary by Fess version. The configuration import feature is fully supported, but for GSA-compatible search endpoints, please consult the official Fess documentation for your specific version.

For applications still sending queries to GSA endpoints, Fess provides a configuration option:

**Enable GSA API**

Add to `system.properties`:
```properties
web.api.gsa=true
```

After enabling this setting and restarting Fess, check your Fess version's documentation for the specific GSA-compatible endpoint configuration. The primary migration path is to:

1. **Import your GSA configuration** using the method described above
2. **Update client applications** to use Fess's JSON Search API (see [API Reference](#api-reference) section)
3. **Gradually migrate** from GSA query format to Fess's native API

**Alternative Approach**: Instead of relying on GSA API compatibility, we recommend migrating client applications to use Fess's modern JSON API (`/api/v1/documents`), which provides more features and better performance.

---

### Google Site Search (GSS) / Google Custom Search (GSE)

[Fess Site Search](https://github.com/codelibs/fess-site-search) provides a drop-in replacement for GSS/CSE JavaScript.

#### Migration Steps

**Step 1: Set Up Fess Crawling**

1. In Fess Admin, create a new Web crawling configuration
2. Add your website URL as the starting point
3. Configure crawl depth and URL patterns
4. Start crawling

**Step 2: Replace JavaScript Code**

Replace existing GSS/CSE code:

```javascript
<!-- Old GSS/CSE Code -->
<script>
  (function() {
    var cx = 'YOUR-CSE-ID';
    var gcse = document.createElement('script');
    gcse.type = 'text/javascript';
    gcse.async = true;
    gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(gcse, s);
  })();
</script>
<gcse:search></gcse:search>
```

With Fess Site Search:

```javascript
<!-- Fess Site Search -->
<script>
  (function() {
    var fess = document.createElement('script');
    fess.type = 'text/javascript';
    fess.async = true;
    fess.src = '//<Fess Server>/js/ss/fess-ss.min.js';
    fess.charset = 'utf-8';
    fess.setAttribute('id', 'fess-ss');
    fess.setAttribute('fess-url', '//<Fess Server>/json');
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fess, s);
  })();
</script>
<fess:search></fess:search>
```

**Step 3: Customize Search UI**

Fess Site Search provides extensive customization options via the [FSS Generator](https://fss-generator.codelibs.org/docs/manual):
- Result layout and styling
- Facet configuration
- Search box placement
- Suggestion behavior

**Example Result**:

![Fess Site Search](https://fess.codelibs.org/_images/fess-ss-1.png "Fess Site Search")

**Additional Resources**:
- [Fess Site Search Documentation](https://fss-generator.codelibs.org/docs/manual)
- [Customization Guide](https://github.com/codelibs/fess-site-search)

---

### Elasticsearch / OpenSearch

If you're migrating from a standalone Elasticsearch or OpenSearch deployment:

#### Document Migration

**Step 1: Export from Source**

Export documents from your existing Elasticsearch/OpenSearch cluster:

```bash
# Using elasticdump
elasticdump \
  --input=http://old-es-server:9200/my-index \
  --output=documents.json \
  --type=data
```

**Step 2: Convert to Fess Format**

Transform documents to include Fess required fields. Example transformation script:

```python
import json

with open('documents.json', 'r') as infile, \
     open('fess-documents.json', 'w') as outfile:
    for line in infile:
        doc = json.loads(line)

        # Transform to Fess format
        fess_doc = {
            "url": doc.get("url", ""),
            "title": doc.get("title", ""),
            "content": doc.get("content", ""),
            "mimetype": doc.get("mimetype", "text/html"),
            "filetype": doc.get("filetype", "html"),
            "created": doc.get("@timestamp", ""),
            "timestamp": doc.get("@timestamp", ""),
            # Add custom fields as needed
        }

        outfile.write(json.dumps(fess_doc) + '\n')
```

**Step 3: Import to Fess**

Option A: Via Admin UI
1. Go to **System > Backup**
2. Upload `fess-documents.json` (bulk format)
3. Click **Import**

Option B: Via Bulk API
```bash
curl -X PUT "http://localhost:8080/api/admin/documents/bulk" \
  -H "Content-Type: application/x-ndjson" \
  -u admin:admin \
  --data-binary @fess-documents.json
```

#### Index Settings Migration

Fess uses OpenSearch/Elasticsearch internally, so you can:

1. Export index mappings and settings from source
2. Apply custom analyzers in Fess's `fess_config.properties`
3. Create synonym dictionaries in Fess's dictionary directory

---

### Apache Solr

For migrating from Apache Solr:

#### Document Export and Import

**Step 1: Export from Solr**

```bash
# Export Solr documents as JSON
curl "http://solr-server:8983/solr/collection1/select?q=*:*&rows=10000&wt=json" \
  -o solr-export.json
```

**Step 2: Transform to Fess Format**

Create a transformation script to map Solr fields to Fess fields:

```python
import json

# Load Solr export
with open('solr-export.json', 'r') as f:
    solr_data = json.load(f)

# Transform documents
fess_docs = []
for doc in solr_data['response']['docs']:
    fess_doc = {
        "url": doc.get("id", ""),
        "title": doc.get("title", [""])[0] if isinstance(doc.get("title"), list) else doc.get("title", ""),
        "content": doc.get("content", [""])[0] if isinstance(doc.get("content"), list) else doc.get("content", ""),
        "mimetype": "text/html",
        "filetype": "html",
        # Map other fields
    }
    fess_docs.append(fess_doc)

# Write in NDJSON format for Fess bulk import
with open('fess-bulk-import.json', 'w') as f:
    for doc in fess_docs:
        f.write(json.dumps(doc) + '\n')
```

**Step 3: Import to Fess**

Use the Bulk API as described in the Elasticsearch section.

#### Configuration Migration

Map Solr configuration to Fess:

| Solr Feature | Fess Equivalent |
|--------------|----------------|
| Solr Core | Fess doesn't use cores; all documents in one index |
| Schema Fields | Define in crawl configuration or use default fields |
| RequestHandlers | Use Fess JSON API with parameters |
| Facet Fields | Configure in Fess search settings |
| Highlighting | Built-in in Fess search results |

---

### Other Search Systems

For proprietary or custom search systems:

#### API-Based Migration

**Step 1: Extract Documents**

Use your current system's API or export functionality to extract:
- Document URLs or identifiers
- Document content/text
- Metadata (title, author, date, etc.)
- Security/access control information

**Step 2: Format for Fess**

Create NDJSON file with required Fess fields:

```json
{"url":"http://example.com/doc1","title":"Document 1","content":"Full text content...","mimetype":"text/html"}
{"url":"http://example.com/doc2","title":"Document 2","content":"More content...","mimetype":"application/pdf"}
```

**Step 3: Bulk Import**

```bash
curl -X PUT "http://localhost:8080/api/admin/documents/bulk" \
  -H "Content-Type: application/x-ndjson" \
  -u admin:admin \
  --data-binary @documents.ndjson
```

#### Crawl-Based Migration

Alternatively, let Fess recrawl your content:

1. **Web Content**: Configure Web Crawler with starting URLs
2. **File Shares**: Configure File Crawler with SMB/FTP paths
3. **Databases**: Configure Data Store crawling with JDBC connections

---

## Data Import Methods

Fess provides multiple methods for importing data:

### 1. Admin UI Import

**Path**: System > Backup > Upload

**Supported Formats**:
- **GSA XML** (`gsa*.xml`) - GSA configuration files
- **Bulk NDJSON** (`*.bulk`) - Newline-delimited JSON documents
- **Fess JSON** (`fess*.json`) - Fess configuration export
- **Doc JSON** (`doc*.json`) - Document data export
- **System Properties** (`system.properties`) - System configuration

**Usage**:
1. Navigate to **System > Backup**
2. Click **Upload** button
3. Select file (auto-detects format)
4. Click **Import** to process

### 2. REST API Import

#### Bulk Document API

**Endpoint**: `PUT /api/admin/documents/bulk`

**Format**: NDJSON (Newline-Delimited JSON)

**Example**:
```bash
# Create NDJSON file
cat > documents.ndjson << 'EOF'
{"url":"http://example.com/page1","title":"Page 1","content":"Content here..."}
{"url":"http://example.com/page2","title":"Page 2","content":"More content..."}
EOF

# Import via API
curl -X PUT "http://localhost:8080/api/admin/documents/bulk" \
  -H "Content-Type: application/x-ndjson" \
  -u admin:admin \
  --data-binary @documents.ndjson
```

**Required Fields**:
- `url` - Unique document identifier/URL
- `title` - Document title
- `content` - Full text content

**Optional Fields**:
- `mimetype` - MIME type (default: text/html)
- `filetype` - File extension (default: html)
- `created` - Creation timestamp
- `timestamp` - Last modified timestamp
- `boost` - Document boost factor
- `label` - Access control labels
- Custom fields as defined in your configuration

**Response**:
```json
{
  "response": {
    "status": 0,
    "message": "Successfully imported 2 documents"
  }
}
```

#### Configuration API

**Endpoints** (all under `/api/admin/`):
- `webconfig` - Web crawling configurations
- `fileconfig` - File crawling configurations
- `dataconfig` - Data source configurations
- `labeltype` - Access control labels

**Example - Create Web Crawl Config**:
```bash
curl -X POST "http://localhost:8080/api/admin/webconfig" \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{
    "name": "Example Site",
    "urls": "http://example.com/",
    "depth": 3,
    "maxAccessCount": 1000,
    "userAgent": "Fess Crawler"
  }'
```

### 3. Command-Line Import

**Using curl with bulk files**:

```bash
# Import GSA configuration
curl -X POST "http://localhost:8080/api/admin/backup/upload" \
  -u admin:admin \
  -F "file=@gsaconfig.xml"

# Import bulk documents
curl -X POST "http://localhost:8080/api/admin/backup/upload" \
  -u admin:admin \
  -F "file=@documents.bulk"
```

### 4. Direct Crawler Configuration

Instead of importing documents, configure Fess to crawl your data sources:

**Web Crawling**:
- **Admin Path**: Crawler > Web
- **Supports**: HTTP/HTTPS websites
- **Features**: JavaScript rendering, authentication, custom headers

**File Crawling**:
- **Admin Path**: Crawler > File
- **Supports**: SMB, FTP, local file systems
- **Features**: Access control, file type filtering

**Data Store Crawling**:
- **Admin Path**: Crawler > Data Store
- **Supports**: Databases (via JDBC), CSV files, custom plugins
- **Features**: Incremental updates, custom field mapping

---

## Configuration Migration

### Exporting Current Configuration

**Via Admin UI**:
1. Navigate to **System > Backup**
2. Select configuration items to export:
   - Web Crawling Config
   - File Crawling Config
   - Data Store Config
   - Scheduled Jobs
   - Users and Roles
   - System Settings
3. Click **Export**
4. Download JSON file

**Via API**:
```bash
# Export all configurations
curl -X GET "http://localhost:8080/api/admin/backup/export" \
  -u admin:admin \
  -o fess-config-backup.json
```

### Importing Configuration

**Via Admin UI**:
1. **System > Backup > Upload**
2. Select `fess*.json` file
3. Click **Import**

**Via API**:
```bash
curl -X POST "http://localhost:8080/api/admin/backup/upload" \
  -u admin:admin \
  -F "file=@fess-config-backup.json"
```

### System Properties

Critical system settings in `system.properties`:

```properties
# GSA API compatibility
web.api.gsa=true

# JSON API
web.api.json=true

# Search page size
page.search.default.size=20

# Crawler settings
crawler.document.max.size=10000000
crawler.web.protocols=http,https

# Authentication
security.remember_me.enabled=true
```

After editing `system.properties`, restart Fess.

---

## API Reference

### Search APIs

#### JSON Search API

**Endpoints**:
- `GET /api/v1/documents?q={query}` (recommended)
- `GET /json/?q={query}` (legacy, for backward compatibility)

**Parameters**:
- `q` - Query string
- `num` - Results per page (default: 20)
- `start` - Starting offset (default: 0)
- `fields.label` - Label filter
- `lang` - Language preference

**Example**:
```bash
# Using the recommended endpoint
curl "http://localhost:8080/api/v1/documents?q=search&num=10"

# Or using the legacy endpoint
curl "http://localhost:8080/json/?q=search&num=10"
```

**Response**:
```json
{
  "response": {
    "version": 1,
    "status": 0,
    "q": "search",
    "exec_time": 0.123,
    "page_size": 10,
    "page_number": 1,
    "record_count": 234,
    "result": [
      {
        "url": "http://example.com/page1",
        "title": "Example Page",
        "content_description": "...search results...",
        "score": 1.543
      }
    ]
  }
}
```

**Documentation**: [JSON API Guide](https://fess.codelibs.org/12.5/user/json-response.html)

#### GSA-Compatible Search API

> **Note**: GSA-compatible search API availability may vary by Fess version. Please refer to the official Fess documentation for your specific version.

**Configuration**: Set `web.api.gsa=true` in `system.properties` to enable GSA compatibility features.

For most migration scenarios, we recommend using the modern JSON Search API (`/api/v1/documents`) instead.

### Admin APIs

All admin APIs require authentication (`-u admin:admin` or Bearer token).

**Base Path**: `/api/admin/`

**Available Endpoints**:
- `POST /backup/upload` - Upload backup/import files
- `GET /backup/export` - Export configurations
- `PUT /documents/bulk` - Bulk document import
- `GET /webconfig` - List web crawl configs
- `POST /webconfig` - Create web crawl config
- `PUT /webconfig/{id}` - Update web crawl config
- `DELETE /webconfig/{id}` - Delete web crawl config
- (Similar CRUD for `fileconfig`, `dataconfig`, `labeltype`, etc.)

**Example - List All Web Configs**:
```bash
curl -X GET "http://localhost:8080/api/admin/webconfig" \
  -u admin:admin
```

---

## Troubleshooting

### Common Migration Issues

#### Issue: Import fails with "Invalid format"

**Solution**:
- Ensure NDJSON files have one valid JSON object per line
- Check for UTF-8 encoding
- Validate JSON syntax with `jq`:
  ```bash
  cat documents.ndjson | jq empty
  ```

#### Issue: GSA XML import creates no configurations

**Solution**:
- Verify XML structure matches GSA export format
- Check Fess logs: `logs/fess.log`
- Look for `GsaConfigException` errors
- Ensure collections have valid URL patterns

#### Issue: Imported documents not appearing in search

**Solution**:
1. Check if documents were indexed:
   - Admin > System > Search
   - Try searching by exact URL
2. Verify required fields are present:
   - `url`, `title`, `content`
3. Check crawler logs: `logs/fess-crawler.log`
4. Manually trigger re-indexing:
   - Admin > Scheduler
   - Run "Default Crawler" job

#### Issue: Authentication errors on API calls

**Solution**:
- Verify credentials: default is `admin`/`admin`
- Check if API is enabled in `system.properties`
- Use proper authentication header:
  ```bash
  curl -u admin:admin ...
  # or
  curl -H "Authorization: Basic YWRtaW46YWRtaW4=" ...
  ```

#### Issue: Large bulk imports timing out

**Solution**:
- Split large files into smaller batches (e.g., 10,000 docs per file)
- Increase timeout in `system.properties`:
  ```properties
  crawler.document.request.timeout=600000
  ```
- Use asynchronous import via Admin UI instead of API

#### Issue: Special characters in documents not displaying correctly

**Solution**:
- Ensure source files are UTF-8 encoded
- Check `fess_config.properties`:
  ```properties
  crawler.document.encoding=UTF-8
  ```
- Verify HTML entity encoding in content

### Log Files

Check these logs for troubleshooting:

- **Main Application**: `logs/fess.log`
- **Crawler**: `logs/fess-crawler.log`
- **Search**: `logs/fess-search.log`
- **Thumbnail**: `logs/fess-thumbnail.log`

### Getting Help

- **Documentation**: https://fess.codelibs.org/
- **GitHub Issues**: https://github.com/codelibs/fess/issues
- **Discussions**: https://github.com/codelibs/fess/discussions

---

## Migration Checklist

Use this checklist to ensure a complete migration:

### Pre-Migration

- [ ] Assess current search system (document count, size, sources)
- [ ] Review infrastructure requirements
- [ ] Install and configure Fess (version: ______)
- [ ] Install compatible OpenSearch/Elasticsearch
- [ ] Backup current search system configuration
- [ ] Backup current search indices/documents
- [ ] Document current API endpoints and integrations
- [ ] Identify custom search features to replicate

### Configuration Migration

- [ ] Export current search configurations
- [ ] Import GSA XML config (if applicable)
- [ ] Create Web crawling configurations
- [ ] Create File crawling configurations
- [ ] Create Data Store configurations (if needed)
- [ ] Configure access control labels
- [ ] Set up scheduled crawling jobs
- [ ] Configure system properties
- [ ] Set up authentication and user roles

### Data Migration

- [ ] Export documents from current system
- [ ] Transform documents to Fess format
- [ ] Validate NDJSON format
- [ ] Import documents via Admin UI or API
- [ ] Verify document count after import
- [ ] Test search queries on migrated data
- [ ] Configure document boost/relevance (if needed)

### Search Integration

- [ ] Enable required APIs (GSA, JSON)
- [ ] Update client applications with new endpoints
- [ ] Replace GSS/CSE JavaScript (if applicable)
- [ ] Test all search features (facets, suggestions, etc.)
- [ ] Verify result formatting and highlighting
- [ ] Test pagination and sorting
- [ ] Validate security and access control

### Testing

- [ ] Functional testing of search queries
- [ ] Performance testing (response time, throughput)
- [ ] Security testing (authentication, authorization)
- [ ] Integration testing with client applications
- [ ] User acceptance testing
- [ ] Load testing for production capacity

### Go-Live

- [ ] Schedule maintenance window
- [ ] Final incremental data sync
- [ ] Switch DNS or endpoints to Fess
- [ ] Monitor logs for errors
- [ ] Monitor search performance metrics
- [ ] Have rollback plan ready

### Post-Migration

- [ ] Monitor search quality and relevance
- [ ] Tune ranking and boosting settings
- [ ] Set up regular crawl schedules
- [ ] Configure backup and disaster recovery
- [ ] Document new search architecture
- [ ] Train users on new admin interface
- [ ] Decommission old search system (after validation period)

---

## Additional Resources

- **Fess Documentation**: https://fess.codelibs.org/
- **Fess Site Search**: https://github.com/codelibs/fess-site-search
- **FSS Generator**: https://fss-generator.codelibs.org/
- **JSON API Reference**: https://fess.codelibs.org/12.5/user/json-response.html
- **GitHub Repository**: https://github.com/codelibs/fess
- **Docker Images**: https://hub.docker.com/r/codelibs/fess

---

*For specific questions or issues during migration, please consult the [Fess documentation](https://fess.codelibs.org/) or open an issue on [GitHub](https://github.com/codelibs/fess/issues).*
