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
