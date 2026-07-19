// SPDX-License-Identifier: Apache-2.0
// Shared jsdom test helpers for the bootstrap theme JS tests.
//
// Not a *.test.js file, so Vitest does not collect it as a suite; test files
// import from it.

/**
 * Reset the jsdom document to a known-empty body and restore the location to
 * the default origin. Call in beforeEach so DOM-mutating modules start clean.
 */
export function resetDom() {
  document.documentElement.lang = "";
  document.title = "";
  document.head.innerHTML = "";
  document.body.innerHTML = "";
}

/**
 * Set document.body.innerHTML to the given fixture and return the body, so a
 * test can build the exact DOM a module's attach()/render function expects.
 *
 * @param {string} html
 * @returns {HTMLElement} document.body
 */
export function mountBody(html) {
  document.body.innerHTML = html;
  return document.body;
}

/**
 * Navigate the jsdom window without a full reload (pushState keeps the same
 * document, which is what the theme's hash/pushState router relies on).
 *
 * @param {string} url - absolute or relative URL
 */
export function setLocation(url) {
  window.history.pushState({}, "", url);
}
