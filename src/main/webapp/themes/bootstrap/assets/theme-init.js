/* Colour scheme of the theme: follows the operating system's light or dark setting.
   A classic script (no module, no defer) so it runs in <head> before the stylesheets and
   a dark visitor gets no light flash. The CSP forbids an inline script, hence the file.
   An explicit choice wins over the system: data-theme="light" or data-theme="dark" on <html>
   (the attribute the admin console uses too). Without it the system decides, and index.html's
   data-bs-theme="light" is what a browser without matchMedia or JavaScript keeps. */
(function () {
  var root = document.documentElement;
  var query = window.matchMedia ? window.matchMedia("(prefers-color-scheme: dark)") : null;
  function apply() {
    var forced = root.getAttribute("data-theme");
    var scheme = forced === "light" || forced === "dark" ? forced : (query && query.matches ? "dark" : "light");
    root.setAttribute("data-bs-theme", scheme);
  }
  apply();
  if (query && query.addEventListener) {
    query.addEventListener("change", apply);
  }
})();
