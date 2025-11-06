# Adding a New Language to Fess

This guide explains how to add internationalization (i18n) support for a new language in Fess.

## Overview

Fess uses a two-tier language support system:

1. **Full UI Translation** (13 languages): Complete user interface translation with label and message files
2. **Search/Analysis Support** (40+ languages): Language-specific text analysis for search indexing

Currently supported UI languages: German, English, Spanish, French, Italian, Japanese, Korean, Dutch, Polish, Brazilian Portuguese, Russian, Simplified Chinese, and Traditional Chinese.

## File Structure

Language resource files are located in:
```
src/main/resources/
├── fess_label.properties           # Base English labels (fallback)
├── fess_label_[locale].properties  # Language-specific labels (~1,056 lines)
├── fess_message.properties         # Base English messages (fallback)
└── fess_message_[locale].properties # Language-specific messages (~200 lines)
```

### Naming Convention

- **Language only**: `fess_label_en.properties`, `fess_label_ja.properties`
- **Language + Region**: `fess_label_pt_BR.properties`, `fess_label_zh_CN.properties`, `fess_label_zh_TW.properties`
- **Special cases**: Kurdish uses `fess_label_ckb_IQ.properties`

## Step-by-Step Guide

### 1. Create Language Resource Files

Copy the base English files as templates:

```bash
cd src/main/resources

# Copy label file (~1,056 translation entries)
cp fess_label_en.properties fess_label_[your_locale].properties

# Copy message file (~200 translation entries)
cp fess_message_en.properties fess_message_[your_locale].properties
```

**Example for Swedish (`sv`):**
```bash
cp fess_label_en.properties fess_label_sv.properties
cp fess_message_en.properties fess_message_sv.properties
```

### 2. Translate Content

Edit the newly created files and translate all entries:

**`fess_label_[locale].properties`** contains UI labels such as:
```properties
labels.system_name=Fess
labels.search=Search
labels.login=Login
labels.logout=Logout
# ... approximately 1,056 more entries
```

**`fess_message_[locale].properties`** contains system messages such as:
```properties
errors.required={0} is required.
errors.minlength={0} cannot be less than {1}.
success.login=Logged in successfully.
# ... approximately 200 more entries
```

### 3. Update Configuration File

Edit `src/main/resources/fess_config.properties`:

Find the `supported.languages` property (around line 202) and ensure your language code is included:

```properties
supported.languages=ar,bg,bn,ca,ckb_IQ,cs,da,de,el,en_IE,en,es,et,eu,fa,fi,fr,gl,gu,he,hi,hr,hu,hy,id,it,ja,ko,lt,lv,mk,ml,nl,no,pa,pl,pt_BR,pt,ro,ru,si,sq,sv,ta,te,th,tl,tr,uk,ur,vi,zh_CN,zh_TW,zh
```

**Optional configurations:**

If you create online help documentation:
```properties
online.help.supported.langs=ja,sv  # Add your language
```

If you have forum support:
```properties
forum.supported.langs=en,ja,sv  # Add your language
```

### 4. Regenerate Auto-Generated Java Classes

Fess uses DBFlute's LastaFlute FreeGen to automatically generate Java constants from property files.

Run the regeneration command:

```bash
# Option 1: Using Maven from project root
mvn dbflute:freegen

# Option 2: Using DBFlute manage script
cd dbflute_fess
./manage.sh    # On Linux/Mac
manage.bat     # On Windows
# Select option 23 (generate)
```

This will regenerate:
- `src/main/java/org/codelibs/fess/mylasta/action/FessLabels.java`
- `src/main/java/org/codelibs/fess/mylasta/action/FessMessages.java`
- `src/main/java/org/codelibs/fess/mylasta/direction/FessProp.java`

### 5. Rebuild the Project

```bash
mvn clean package
```

### 6. Test the New Language

1. **Start Fess:**
   ```bash
   # Run the main class in your IDE
   org.codelibs.fess.FessBoot
   ```

2. **Access the Admin UI:**
   ```
   http://localhost:8080/admin/
   ```

3. **Test language selection:**
   - Check if your language appears in the language dropdown
   - Force your language by adding URL parameter: `?browser_lang=[locale]`
   - Example: `http://localhost:8080/admin/?browser_lang=sv`

4. **Verify translations:**
   - Navigate through different admin pages
   - Check that labels and messages appear in your language
   - Test error messages by submitting invalid forms

## Configuration Details

### Key Configuration Files

| File | Purpose |
|------|---------|
| `fess_config.properties` | Main configuration file containing `supported.languages` |
| `fess_label_[locale].properties` | UI labels (~1,056 entries) |
| `fess_message_[locale].properties` | System messages (~200 entries) |

### Java Classes Involved

| Class | Location | Purpose |
|-------|----------|---------|
| `FessProp` | `org.codelibs.fess.mylasta.direction` | Configuration interface |
| `FessLabels` | `org.codelibs.fess.mylasta.action` | Label constants (auto-generated) |
| `FessMessages` | `org.codelibs.fess.mylasta.action` | Message constants (auto-generated) |
| `LanguageHelper` | `org.codelibs.fess.helper` | Language detection and validation |
| `SystemHelper` | `org.codelibs.fess.helper` | UI language dropdown builder |
| `FessUserLocaleProcessProvider` | `org.codelibs.fess.mylasta.direction.sponsor` | User locale detection |

## Language Detection and Selection

### User Interface Language

Fess determines the UI language in this order:

1. **Query parameter**: `?browser_lang=[locale]` (highest priority)
2. **Browser header**: `Accept-Language` header
3. **Fallback**: English (from `fess_label.properties` and `fess_message.properties`)

### Document Language Detection

During crawling and indexing, Fess:

1. Detects language from document content using Apache Tika
2. Validates against `supported.languages` list
3. Creates language-specific fields (e.g., `content_ja`, `title_en`, `content_sv`)
4. Applies language-specific analyzers for better search results

Configured in `fess_config.properties`:
```properties
indexer.language.fields=content,important_content,title
```

## Optional: Search Analysis Support

If your language requires special text analysis (stemming, stop words, etc.), you may need to configure OpenSearch analyzers.

### 1. Update OpenSearch Index Template

Edit `src/main/resources/fess_indices/fess.json` to add language-specific analyzers.

**Example for Swedish:**
```json
{
  "analysis": {
    "filter": {
      "swedish_stop": {
        "type": "stop",
        "stopwords": "_swedish_"
      },
      "swedish_stemmer": {
        "type": "stemmer",
        "language": "swedish"
      }
    },
    "analyzer": {
      "sv_analyzer": {
        "type": "custom",
        "tokenizer": "standard",
        "filter": ["lowercase", "swedish_stop", "swedish_stemmer"]
      }
    }
  }
}
```

### 2. Add Custom Dictionaries (Optional)

Create custom dictionary files in: `${fess.dictionary.path}/[lang]/`

Available dictionary types:
- `stopwords.txt` - Words to exclude from indexing
- `stemmer_override.txt` - Custom stemming rules
- `protwords.txt` - Protected words (no stemming)
- `mapping.txt` - Character/token mappings

**Example directory structure:**
```
dictionary/
└── sv/
    ├── stopwords.txt
    ├── stemmer_override.txt
    └── protwords.txt
```

## Important Notes

### No Database Changes Required

- Language configuration is entirely file-based
- No database or OpenSearch index updates needed
- Settings are loaded at application startup and cached

### Fallback Mechanism

If a translation is missing in `fess_label_[locale].properties`, Fess automatically falls back to the base `fess_label.properties` (English).

### Language Code Format

Use standard locale codes:
- ISO 639-1 language codes: `en`, `ja`, `de`, `fr`, `sv`, etc.
- With region when needed: `pt_BR`, `zh_CN`, `zh_TW`, `en_IE`

### Cache Behavior

- Language items are cached for 1 hour by `SystemHelper`
- Restart the application after adding new languages

### Auto-Generated Files

Never edit these files manually (marked with `@author FreeGen`):
- `FessLabels.java`
- `FessMessages.java`

Always regenerate them using `mvn dbflute:freegen`

## Complete Example: Adding Swedish (sv)

```bash
# 1. Navigate to resources directory
cd src/main/resources

# 2. Copy base files
cp fess_label_en.properties fess_label_sv.properties
cp fess_message_en.properties fess_message_sv.properties

# 3. Translate files (use your text editor)
# Edit fess_label_sv.properties - translate all ~1,056 entries
# Edit fess_message_sv.properties - translate all ~200 entries

# 4. Update configuration (ensure 'sv' is in supported.languages)
# Edit fess_config.properties line 202

# 5. Regenerate Java classes
cd ../../../..
mvn dbflute:freegen

# 6. Rebuild project
mvn clean package

# 7. Test
# Run org.codelibs.fess.FessBoot
# Access: http://localhost:8080/admin/?browser_lang=sv
```

## Troubleshooting

### Language doesn't appear in dropdown

- Verify both `fess_label_[locale].properties` and `fess_message_[locale].properties` exist
- Check that locale code is in `supported.languages` in `fess_config.properties`
- Regenerate Java classes: `mvn dbflute:freegen`
- Rebuild project: `mvn clean package`
- Restart Fess application

### Translations not showing

- Clear browser cache
- Force language with `?browser_lang=[locale]` parameter
- Check property file encoding (should be UTF-8 or use Unicode escapes `\uXXXX`)
- Verify property keys match exactly with base English files

### Build errors after regeneration

- Check property file syntax (no unescaped special characters)
- Ensure all property values are properly escaped
- Run `mvn clean` before rebuilding

## Additional Resources

- Fess Configuration: `src/main/resources/fess_config.properties`
- DBFlute FreeGen Configuration: `dbflute_fess/dfprop/lastafluteMap.dfprop`
- OpenSearch Analysis: `src/main/resources/fess_indices/fess.json`
- LastaFlute Documentation: https://lastaflute.org/

## Summary Checklist

- [ ] Create `fess_label_[locale].properties` with ~1,056 translated entries
- [ ] Create `fess_message_[locale].properties` with ~200 translated entries
- [ ] Add locale to `supported.languages` in `fess_config.properties`
- [ ] Run `mvn dbflute:freegen` to regenerate Java classes
- [ ] Run `mvn clean package` to rebuild project
- [ ] Test language selection in admin UI
- [ ] (Optional) Configure OpenSearch analyzers in `fess.json`
- [ ] (Optional) Add custom dictionaries for search analysis
- [ ] Verify all UI pages display correctly in new language
- [ ] Test error messages and form validation

Congratulations! You've successfully added a new language to Fess.
