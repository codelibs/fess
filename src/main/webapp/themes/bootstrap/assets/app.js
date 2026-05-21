import * as api from "./api.js";
import * as i18n from "./i18n.js";
import * as auth from "./auth.js";
import * as search from "./search.js";
import * as chat from "./chat.js";

async function main() {
  try {
    await api.init();
  } catch (e) {
    console.error("Fess /ui/config failed:", e);
  }
  await i18n.init();
  await auth.attach();
  search.attach();
  chat.attach(); // no-op when rag_chat_enabled is false
  document.addEventListener("fess:auth:login", () => search.attach());
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", main);
} else {
  main();
}
