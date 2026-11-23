# Upgrading

Notes for moving an existing Fess installation to a newer release. For moving to Fess from another
search system, see [MIGRATION.md](MIGRATION.md).

## Upgrading to 15.10

**The admin console no longer ships Bootstrap, AdminLTE, jQuery, Popper, moment.js, the date/time pickers or
the form validator.** Fess 15.9.0 loaded them from `css/admin/` and `js/admin/`. The console now uses its own
`css/admin/admin.css` and `js/admin/admin.js` (plain JavaScript), Font Awesome 7 Free under
`css/admin/fontawesome/`, and follows the operating system's light or dark setting. Nothing changes in the
data or the settings. If you added your own admin page, theme tweak or script that relies on the removed
libraries, change it:

* `$`, `jQuery`, `moment`, `$.fn.modal`, `.tooltip()`, `.daterangepicker()` and `.timepicker()` are not defined
  on admin pages. The `data-toggle="modal|collapse|tab"`, `data-target` and `data-dismiss="modal"` attributes
  of Bootstrap keep working; tooltips are the browser's own `title` text.
* The class names the admin JSPs use (`card`, `btn`, `form-group`, `row`, `col-sm-*`, `modal`, `table`, ...) are
  kept, but only the ones `admin.css` defines. A Bootstrap class it does not define has no effect.
* The search log screen takes its time range from two native date-time inputs; the value sent to the
  server (`yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm`) is unchanged.

**A secret can be given by an environment variable, and Fess warns about one that is a JVM option.** A
`-Dfess.config.<key>` or `-Dfess.system.<key>` option is part of the command line that `ps` and `docker top` show, and
Fess repeats those options on the command line of every crawler, suggest, thumbnail and chunk process it starts, so
a password or an API key given that way is readable by every user of the machine. Fess now also reads a setting from
the environment variable made from its key: `FESS_CONFIG_` (for a `fess_config.properties` key) or `FESS_SYSTEM_`
(for a `system.properties` key), then the key in upper case with each character other than a letter or a digit
replaced by `_`. For example `search_engine.password` is `FESS_CONFIG_SEARCH_ENGINE_PASSWORD`,
`rag.llm.gemini.api.key` is `FESS_CONFIG_RAG_LLM_GEMINI_API_KEY` and `storage.secretkey` is
`FESS_SYSTEM_STORAGE_SECRETKEY`. The order is the `-D` option, then the variable, then the file (for a `fess.system.`
setting, a value stored in `system.properties` still wins, as it does over the option); an empty variable counts as
not set, and an empty option does not hide a variable that is set. The child processes inherit the variable. A setting that
the code reads from `system.properties` directly (for example `oic.client.secret` of the SSO plugins) honours neither the option
nor the variable; the rule applies wherever the `-D` option of the key is honoured. The values of these variables are masked in System Info.
The web application writes a WARN at start for each `-Dfess.config.*` or `-Dfess.system.*` option that carries
a secret (it names the key and the variable, never the value; an empty option is not reported). A package
installation (deb, rpm) that starts Fess with systemd reads the environment file (`/etc/default/fess`, `/etc/sysconfig/fess`
for rpm) as `EnvironmentFile`, so add a plain `FESS_CONFIG_<KEY>=value` line; with the System V init script, which passes only the
variables it exports itself, write `export FESS_CONFIG_<KEY>=value` instead. Make the file readable by `root` only.
In a container, pass it with `-e` or
`environment:`; `docker inspect` shows it to whoever can use the Docker socket, as it did for `SEARCH_ENGINE_PASSWORD`.

**A search theme no longer gets Bootstrap, Popper or Font Awesome from the web application root.**
`css/bootstrap.min.css`, `css/font-awesome.min.css`, `css/fonts/`, `js/bootstrap.min.js` and `js/popper.min.js`
are removed, so a change to one of them can no longer change every theme. The bundled `bootstrap` theme
carries its own copies under `themes/bootstrap/assets/vendor/` (Bootstrap 5.3.9, was 5.3.0; Popper 2.11.8;
Font Awesome Free 7.3.1, was 5.12.0), and the themes of the `fess-themes` repository that show icons carry
Font Awesome the same way. **A theme that still links a removed file loses its styles, scripts or icons
(`css/font-awesome.min.css` is the common one): update the `fess-themes` themes to the versions released
for 15.10, and for a theme of your own copy the library into the theme and link it as
`{{themePath}}/assets/vendor/...`.** Font Awesome 7 keeps the version 5 and 6 icon names (`fa-search`,
`fa-pencil-alt`, ...) and, with `v4-shims.min.css`, the old `-o` names (`fa fa-star-o`, ...) that Font
Awesome 5.12 did not show. Only `woff2` fonts are shipped.

**Fess now compresses the scripts, styles and SVG files of 1 KB or more itself.** The HTTP connector sends them
as gzip when the client accepts it (`bootstrap.min.css` goes from 232 KB to about 31 KB). Pages (HTML), JSON,
images, downloads, event streams and the NDJSON and CSV exports are not compressed, because the size of a
compressed page can reveal the session token it contains. If a reverse proxy in front of Fess already
compresses, or you want the previous behavior, set `tomcat.compression=off` in `tomcat_config.properties`;
`tomcat.compression.mime=text/html,application/json` there adds the types of pages and JSON.

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
document was indexed without its content and nothing was recorded (fess#3757). The extractor takes a document
for a zip bomb when its extracted text is longer than `maxUncompressionSize` (10,000,000 characters) and also
more than `maxCompressionRatio` times longer than the bytes it was read from. Both are set in
`crawler/extractor+tikaExtractor.xml`. The ratio is 100 (extracted characters per input byte, the default of
Tika): an ordinary archive or office document of text, which expands by a factor of a few to a few tens, is
indexed whatever its size, and only a document that expands by more than that is rejected. Look under Admin >
Failure URL for `Suspected zip bomb`. Raise `maxCompressionRatio` in that file if you have to index a more
compressible document, or lower it to be stricter.

**The static files of a theme are revalidated instead of being cached for a day.** Until 15.9 the files under
`/themes/<name>/` were sent with `Cache-Control: public, max-age=86400`, so a browser kept the scripts and
stylesheets of the version it had loaded for up to 24 hours after an upgrade. They are now sent with
`Cache-Control: no-cache` together with `ETag` and `Last-Modified`: the browser asks on every use and gets a
`304 Not Modified` (no body) while the file is unchanged. **A browser that loaded the theme of 15.9.0 still
holds those files under the old rule, so for up to 24 hours after the upgrade it can run the old scripts and
stylesheets of the bundled theme next to the new `index.html`. Reload the page without the cache (Ctrl+F5 /
Shift+reload) once, or wait for the day to pass.** A reverse proxy that caches `/themes/` by itself must
honour `no-cache` for the change to reach its clients.

**The label menu, the label choices of the admin forms and the matching of crawled documents now use every
label type.** Until 15.9 only the first `page.labeltype.max.fetch.size` (1,000) label types were read, so a
label type beyond them was missing from the menu and from the forms, and the documents it should label were
not labelled. The setting now only sizes the pages of the label type list in the admin console. An
installation with more than 1,000 label types sends a larger `/api/v2/ui/config` (about 45 bytes per label
type) and loads all of them on every change of a label type.

**An invalid regular expression is refused when a crawl configuration, a label type or a path mapping is
saved.** The included and excluded URLs and paths of a web or file configuration, the document filters of
both, the paths of a label type and the regular expression and user agent of a path mapping are compiled
when the form is saved (the admin API answers with the same error). Until 15.9 they were stored as typed: the
crawler skipped an invalid include or exclude pattern with a warning in the log, so it had no effect, and
the other fields failed when they were used. A configuration that was saved with such a pattern is not
changed, but cannot be saved again until the pattern is corrected.

**A permission entry written with full-width characters is refused.** On the permission field of the admin
screens and the admin API, `（deny）{user}name` or `(deny)｛user｝name` (full-width parentheses or braces, as a
Japanese keyboard types them) is rejected with a message asking for the half-width spelling, such as
`(deny){user}name`. Until 15.9 such an entry was stored as a plain value that matches nobody, or dropped, so a
deny that was meant to hide a document had no effect. The name after the marker is not changed, so a role
named in Japanese or with full-width letters is still accepted.

**A permission entry that starts with an invisible character is refused.** On the permission field of the
admin screens and the admin API, an entry that has a zero-width space, a byte order mark or a control
character where the `(allow)` / `(deny)` prefix or the name starts, such as `(deny)<U+200B>{user}name`, is
rejected with a message asking to delete the character. Such a character is usually pasted in with the text.
Until now such an entry was saved without a word, as a plain value that matches nobody, or dropped, so a deny
that was meant to hide a document had no effect. A character of this kind inside a name, or at its end, is not
refused.

**An admin user edit form that is out of date is refused.** The edit form of a user now carries the version of
the stored user. If the user was changed after the form was shown (by another administrator, by the user, or
by the deletion of a role or group the user had), saving the form shows "The user was changed by someone else.
Reload it and try again." and writes nothing. Until 15.9 the form was saved over the change. A script that
drives the admin screens with a saved form has to load the user again before it saves. The admin API
(`PUT /api/admin/user/setting`) compares the `version_no` of its body in the same way and refuses a stale one
with the same message (HTTP 400, `status` 1) before anything is written, so a client has to send the
`version_no` it has just read from `GET /api/admin/user/setting/{id}` (or the list) and read the user again
after a refusal. The version also changes when Fess itself writes the user (a login that upgrades the password
hash, ending the other sessions of the user when a passkey is removed, a password change), so a client that
reads a user and waits across such a write is refused as well. Until 15.9 the API applied a body with any `version_no`; it only refused a write that
collides with another one at the same moment.

**The other admin APIs refuse a stale `version_no` too.** `PUT /api/admin/<name>/setting` now compares the
`version_no` of its body with the stored one and refuses a stale one with "The setting was changed by someone
else. Reload it and try again." (HTTP 400, `status` 1) before anything is written. It covers `accesstoken`,
`badword`, `boostdoc`, `dataconfig`, `duplicatehost`, `elevateword`, `fileauth`, `fileconfig`, `group`,
`keymatch`, `labeltype`, `pathmap`, `relatedcontent`, `relatedquery`, `reqheader`, `role`, `scheduler`,
`webauth` and `webconfig`. Until 15.9 `version_no` was required but never compared (and the API returned `-1`
for it), so a script that sent a constant has to send the `version_no` it has just read from
`GET /api/admin/<name>/setting/{id}` (or the list) and read the setting again after a refusal. The admin screens
of these settings do not compare it. The APIs that have no `version_no` (for example `general`, `theme`, `dict`)
and `tagtype` (which has its own `seq_no` check) are unchanged.

**A user who is already signed in no longer gets the login form.** `GET /login/` now redirects the user to the
page a login would lead to: the dashboard for an administrator, the first admin screen the roles allow for a user
with some admin role, the search page for anyone else. An anonymous visitor still gets the form. To sign in as
another account, sign out first. The same applies where `/login/` was the landing page of a blocked cross-site
`/logout/` (fess#3815): the session still stays, and the browser now ends on the page above.

**While the search engine cannot be reached, Fess answers `503 Service Unavailable` instead of `500`.** A console
screen that reads the engine ends with the theme's 503 page (a client that does not take HTML gets
`Service Unavailable.`), as the v2 API already did with `503 service_unavailable`. A login attempt shows the
form again with a message instead of a system error, and it is not counted as a failed login or against the
rate limit. A monitor that treated `500` from these pages as "engine down" has to accept `503`.

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

**The password hash refuses a password longer than the algorithm reads.** BCrypt (`app.password.algorithm`,
the only built-in algorithm) takes into account only the first 72 bytes of a password in UTF-8, so a longer one
accepts any ending. The screens and the API already refuse to set such a password in 15.10; now hashing it refuses
it too (`IllegalArgumentException`), so no other code path can store one. The limit belongs to the algorithm,
while `password.max.length` (default 100 characters) is unchanged. The algorithm is now a component: the id in
`{id}` and in `app.password.algorithm` is resolved as the DI component `<id>PasswordEncoder`
(`bcryptPasswordEncoder` is registered by Fess), so a plugin can supply another one, with its own limit if it has
one. If `app.password.algorithm` names an id with no such component (for example the plugin jar was
removed), Fess still starts and users with a stored hash of another id still log in, but setting any password
fails at the first password hash with a message naming `app.password.algorithm`. Code outside this repository
that extended `PasswordHashHelper` must adapt: the public constants `BCRYPT_PREFIX` and `BCRYPT_MAX_PASSWORD_BYTES`
and the protected members `getEncoders`, `ID_BCRYPT`, `parseBcryptCost`, `resolveBcryptCost` and
`getDummyBcryptHash` are removed (BCrypt specifics moved to `BcryptPasswordEncoder`, which has
`MAX_PASSWORD_BYTES`, and to the `PasswordEncoder` interface), and the inner `PasswordEncoder` interface is now
the public `org.codelibs.fess.crypto.password.PasswordEncoder`. **An account whose password was set longer than 72 bytes keeps logging in:** BCrypt has been the hash since
15.6.0 and the password field accepted up to 100 characters with no byte rule until 15.10, so passwords longer
than 72 bytes exist. Verifying a password never applies the limit, only the first 72 bytes are compared, and the
stored hash is not upgraded to a newer cost while the password stays that long.
A password that long whose stored hash is a legacy one (the unsalted SHA-256, SHA-512 or MD5 hex digest of
`app.digest.algorithm`) also keeps that hash instead of moving to BCrypt at login, since BCrypt would refuse it;
the warning about an insecure legacy MD5 hash is still written at each login until the user sets a new password.
Set a new password (at most 72 bytes) to get the full protection. A fresh installation whose
`index.user.initial_password` is longer than 72 bytes fails to start before it creates the user index, with a
message naming the property; an installation whose `admin` user already exists only logs a warning.

**A password typed on a screen is no longer trimmed.** Until 15.9 the login form, the change-password form and the
admin user form removed the spaces at the start and the end of the password, while the JSON login
(`POST /api/v2/auth/login`) and the admin API did not, so a password such as `" lead and trail "` created through the
API logged in through JSON but not through the form. Every entrance now takes the password as typed (the password
fields of the crawl authentication screens and the SPNEGO pre-authentication password of the general settings too).
A password that was saved from a screen with such spaces is stored without them, so its owner now has to type it
without them at login; before, the form removed them.

**A login session of a local user ends when the roles or the groups of the user are changed.** Until 15.9 a session
kept the roles and the groups it was opened with, so removing a role from a user (or deleting the role) did not take
the permission away until the user logged in again. Now the session checks the stored user the way it checks a
password change (see the passkey note above): when the roles or the groups of the user document differ from the
ones the session was opened with, whoever changed them (the user edit screen, `PUT /api/admin/user/setting`, or
the deletion of a role or a group that the user had), the session ends within `password.changed.check.interval`
seconds (default 60) and the user logs in again. The same setting paces the check of the roles and the groups as
well as the one of the password; its name mentions only the password. The audit log records it as `SESSION_END` with
`reason=permissions_changed`. A session of a user that is not a local user (LDAP, SAML, SPNEGO, OpenID Connect,
Microsoft Entra ID) takes its roles from the directory or the identity provider at login and is not ended.

**A crawl configuration created through the admin API without a permission gets `{role}guest`.** The create screens
of the web, file and data store configurations prefill the permission field with
`role.search.default.display.permissions` (`{role}guest`), but `POST /api/admin/webconfig/setting`,
`/api/admin/fileconfig/setting` and `/api/admin/dataconfig/setting` took a missing or empty `permissions` as no
permission, so the crawled documents were visible to nobody, administrators included. Now a `POST` whose
`permissions` is missing or blank (only blank lines) stores `{role}guest`, and a permission in the request is kept as
is. `PUT` is unchanged in what it stores, like the edit screens: a `PUT` that omits `permissions` keeps the permission of
the stored setting, and any other value is stored as sent, so the body of a `GET` (whose `permissions` is empty for a
setting without one) can be sent back unchanged. A data store setting whose ACL-synchronizing connector must not
carry the guest role (the permission of the setting is added to the roles the connector computes, so `{role}guest` would
make the documents visible to anonymous users) is saved without a permission by a `PUT` that sends `permissions` empty
(a `POST` always gets the default). Settings already saved with no permission are not changed.

**BCrypt hashes written by other systems verify.** A stored `{bcrypt}` hash with the `$2b$` prefix (Python's bcrypt and many other systems) or the `$2y$` prefix (PHP, `htpasswd -B`) now verifies like a `$2a$` one; before, the login of such a user always failed. New hashes are still written as `$2a$`, and an imported hash is re-hashed at login only when its cost is below `app.password.bcrypt.cost`, as before.

**New setting `authentication.local.username.policy` (default `preserved`) to refuse local user names that differ
only in case or width.** Fess compares local user names exactly as typed, as it did in 15.9.0: `Alice`, `alice`
and `ＡＬＩＣＥ` (full-width letters) are three users, each with its own login. Set the key in `fess_config.properties`
to `mapped` to refuse such a name when a local user is created or renamed on the admin screen or through the admin API.
The names are compared after the full-width and half-width forms (such as half-width katakana) and the
ideographic space are replaced by their decomposition mapping, the case is lowered with the root locale, and
the result is put in Unicode normalization form C, as RFC 8265 `UsernameCaseMapped` does. A new
or renamed user is refused when its mapped name is the mapped name of another user, or of an entry of
`authentication.admin.users` without being that entry as written: `Admin` counts as an administrator when
`authentication.admin.users.ignore.case` is on, or `auto` with LDAP. So `admin` can still be created (again) while
`Admin`, `ADMIN` and the full-width spelling are refused. An unknown value is treated as `preserved` and logged once as a warning.

`mapped` only applies this comparison mapping (width, case, normalization) to refuse a collision when a user is
created or renamed. It does not enforce the character rules of the RFC 8265 profile: a name with spaces, symbols or
control characters is still accepted, and only the start-up warning lists the stored names that contain
whitespace or control characters. It does not change login, which still looks a user up by the name exactly as typed,
the user ID, the stored name, the permission strings made from a name (`{user}Alice` and `{user}alice` in a role
filter or a document's permissions) or the documents already indexed. A user that is saved under the name it already
has is never refused, so users that collide today stay editable, and Fess lists up to 20 such groups in one warning
at start.

The check runs where a user is created or renamed on the admin screen or through the admin API. It is bypassed by
a backup restore, by the initial `admin` load at first start, and by any direct write to the user index. Two
administrators creating `Alice` and `alice` at the same moment can both succeed, since no mapped name is stored to
enforce uniqueness. Folding names at login and in permissions would need a migration of the stored users, roles and
indexed documents, and is not part of this setting.
