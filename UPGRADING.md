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

**Fess creates a new index, `fess_user.passkey`, when it starts.** Passkey (WebAuthn) login for local
users is new in 15.10 (fess#3795) and is off by default, but the index is created and used whether or not it is
on: deleting a user, creating or renaming one, and opening a user's details on the admin screen all
query it. Where Fess is not allowed to create indices, create `fess_user.passkey` (with the configured
`index.user.index` prefix in place of `fess_user`) from `fess_indices/fess_user.passkey.json` and
`fess_indices/fess_user.passkey/passkey.json` before upgrading. The index does not join the
`fess_user` alias, so passkeys are not in the `fess_user.bulk` backup: after a restore, users register
their passkeys again.

To turn passkeys on, set the four new keys in `fess_config.properties`:

```
passkey.enabled=true
passkey.rp.id=example.com
passkey.rp.name=Fess
passkey.origins=https://fess.example.com
```

`passkey.rp.id` is the domain every passkey is bound to. Changing it later makes every registered
passkey unusable, so choose it once. `passkey.origins` lists, comma-separated, the exact origins
users open Fess at; a ceremony from any other origin is rejected, and the first such origin is logged
as a warning. Passkeys stay off, with a warning, while `passkey.rp.id` or `passkey.origins` is empty.

The challenge of a passkey ceremony is kept in the HTTP session, so behind a load balancer the
nodes must share sessions (`FESS_SESSION_STORE=opensearch`) or the balancer must pin each session
to one node. Only a user of the `fess_user` index can register a passkey, and only after entering the
local password again; a local user whose name and password are also accepted by the directory
(LDAP) cannot register one, since the password check cannot tell which account it verified.

Removing a passkey, by the user or by an administrator, ends the other login sessions of that user
the way a password change does: the sessions end when they next check the user index, within
`password.changed.check.interval` seconds (default 60).
