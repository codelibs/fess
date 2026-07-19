import { defineConfig } from "vitest/config";
import { fileURLToPath } from "node:url";

// The bundled bootstrap theme JS is authored as native ES modules that call
// browser APIs (document, window, URL, ...). jsdom supplies a spec-compliant
// DOM so the real, unmodified asset files can be imported and executed here.
//
// Those asset files live in src/main/webapp/... — outside this directory — so
// the Vitest root is the repository root. That lets coverage instrument the
// real shipped files (v8 only instruments files under the root) while the tests
// keep importing them by relative path.
const repoRoot = fileURLToPath(new URL("../../../", import.meta.url));
const assets = "src/main/webapp/themes/bootstrap/assets";

export default defineConfig({
  root: repoRoot,
  // Keep Vite's cache inside the (git-ignored) test node_modules rather than
  // creating a node_modules/.vite at the repo root, which root:repoRoot would
  // otherwise do.
  cacheDir: fileURLToPath(new URL("./node_modules/.vitest", import.meta.url)),
  server: { fs: { allow: [repoRoot] } },
  test: {
    environment: "jsdom",
    include: ["src/test/js/themes/**/*.test.js"],
    coverage: {
      provider: "v8",
      // Instrument every shipped asset (all:true) so a new module without a
      // test drags coverage down and trips the gate, not just the loaded ones.
      all: true,
      include: [`${assets}/*.js`],
      reporter: ["text", "text-summary"],
      // Regression gate: kept a few points below the achieved numbers so honest
      // refactors don't trip it, while a real coverage drop fails the build.
      thresholds: {
        statements: 78,
        lines: 80,
        functions: 78,
        branches: 60,
      },
    },
  },
});
