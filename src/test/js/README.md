# Bundled bootstrap theme — JavaScript unit tests

Executable unit tests for the JavaScript that ships with the bundled `bootstrap`
static theme (`src/main/webapp/themes/bootstrap/assets/*.js`).

## Why this exists

`BundledBootstrapThemeTest.java` verifies theme JS by reading each file as text and
asserting on substrings (`js.contains("...")`). That cannot observe what the code
actually *does*: a heading regex can be present in source yet produce output that a
later sanitizer strips. These tests instead **import and run the real, unmodified
asset files** and assert on their behaviour.

The theme JS is authored as native ES modules (`import`/`export`) that call browser
APIs (`document`, `window`, `URL`, ...). JVM engines (Sai, Nashorn) cannot parse ES
modules, and headless approaches without a DOM cannot exercise the sanitizer. So
these tests run on Node with [vitest](https://vitest.dev) and a
[jsdom](https://github.com/jsdom/jsdom) DOM.

## Why here (`src/test/js/`) and not in the theme directory

`src/main/webapp/themes/bootstrap/` is packaged into the WAR (and the `.deb`/`.rpm`
distributions) and served to anonymous users by the servlet container. Placing
`package.json` / `node_modules/` there would ship them and expose them over HTTP.
`src/test/` is never packaged, so the tooling stays out of every artifact. This
mirrors the existing non-shipped fixture at
`src/test/resources/themes/fixture-minimal/`.

## Running

```bash
cd src/test/js
npm ci      # or: npm install
npm test
```

`npm test` runs the suite under coverage and enforces the thresholds declared
in `vitest.config.js` (it fails if coverage of the asset files under test
drops below them). The same command runs in CI, so a coverage regression breaks
the build.

These tests are **not** part of the Maven build. They run in their own GitHub
Actions workflow (`.github/workflows/theme-js.yml`). `mvn test` is unaffected.

## Layout

```
src/test/js/
├── package.json / package-lock.json / vitest.config.js
├── helpers/                # dom.js (jsdom fixtures), net.js (fetch/stream stubs)
└── themes/bootstrap/       # one *.test.js per shipped asset module
    ├── format.test.js  markdown.test.js  pipeline.test.js
    ├── api.test.js     router.test.js    i18n.test.js
    ├── error.test.js   auth.test.js      chat.test.js
    ├── advance.test.js cache.test.js     help.test.js
    ├── profile.test.js search.test.js    app.test.js
```

Every JavaScript module shipped in `assets/` is imported and executed by a test
here (the coverage gate uses `all: true`, so a new asset module with no test
drags coverage down and fails the build). Tests import the shipped files by
relative path; they are never copied.

Sibling modules are mocked where a test needs to control I/O or config
(`api.js`, `router.js`) and used for real where they work under jsdom
(`i18n.js`, `format.js`, `markdown.js`). `i18n.t(key)` returns the key unchanged
when no bundle is loaded, so tests assert on exact i18n keys.
