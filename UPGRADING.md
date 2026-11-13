# Upgrading

Notes for moving an existing Fess installation to a newer release. For moving to Fess from another
search system, see [MIGRATION.md](MIGRATION.md).

## Upgrading to 15.10

**A search result whose URL has a scheme outside `search.result.link.schemes` is no longer linked or
redirected.** The setting is new in 15.10 (fess#3777). Its default is the list the bootstrap theme
accepted until now:

```
search.result.link.schemes=http,https,ftp,ftps,file,smb,smb1,storage,s3,gcs
```

The theme reads the list from `result_link_schemes` of `/api/v2/ui/config`, the admin search list
and `/go/` check the same list, and `/go/` answers 404 for a document whose URL (after the "Displaying"
path mappings) uses another scheme. If a path mapping rewrites URLs to a custom scheme such as
`hammerspoon://`, add that scheme to the setting in `fess_config.properties` (comma-separated, without
the colon). Do not list `javascript`, `data` or `vbscript`. A blank value allows no scheme.

**A search word without quotes now also matches similar words, whatever its case.** Fess adds a fuzzy
clause for a plain word of `query.boost.fuzzy.min.length` (4) or more characters. Until 15.9 the clause
was built from the text exactly as typed, so `servr` matched `server` but `Servr` matched nothing. Since
15.10 (fess#3726) it is built from the analyzed terms, so the case no longer matters and a misspelling is
corrected for every plain word. The clause is still skipped for text with a digit (fess#3741, set
`query.boost.fuzzy.digit.enabled=true` to include it), for Han, Hiragana, Katakana and Hangul text
(`query.boost.fuzzy.cjk.enabled`), and for a word in double quotes, which is an exact match since fess#3670.

What changes in practice:

* A query written in capitals or mixed case can return more documents than before, because it now matches
  the same neighbours as the lowercase query. The total count, the facet counts and the number of pages
  include them, while the exact matches keep the top positions.
* A negated word excludes the similar words too: `-common` also removes the documents that only contain a
  similar word.

To get the previous result sizes, put the word in double quotes (`"common"`), or turn the clause off for
the whole installation with `query.boost.fuzzy.min.length=-1` in `fess_config.properties` (a negative
value never adds it).

**A document that the extractor takes for a zip bomb is now recorded as a failure URL.** Until 15.9 such a
document was indexed without its content and nothing was recorded (fess#3757). The check rejects a
document whose extracted text is longer than `maxUncompressionSize` of `crawler/extractor+tikaExtractor.xml`
(10,000,000 characters), so a genuine text file, archive or office document of that size is rejected as
well. Look under Admin > Failure URL for `Suspected zip bomb`. Raise `maxUncompressionSize` in that file if
you have to index such documents.
