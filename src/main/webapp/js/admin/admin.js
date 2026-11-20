/*
 * Fess admin console behaviour. Plain JavaScript, no libraries.
 *
 * The page markup keeps the attribute hooks the admin screens have always used:
 * data-toggle="modal|collapse|tab", data-target, data-dismiss="modal" and
 * data-widget="pushmenu|treeview". Event handlers are delegated from the document, so they
 * are registered once; init() prepares the elements that exist when the page loads.
 */
(function() {
  "use strict";

  var NARROW = "(max-width: 991.98px)";
  var FOCUSABLE = 'a[href], button:not([disabled]), input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])';
  var NO_ENTER_SUBMIT = "button, [type=submit], [type=reset], [type=button], [type=image], [type=file]";

  function byHash(trigger) {
    var ref = trigger.getAttribute("data-target") || trigger.getAttribute("href") || "";
    return ref.charAt(0) === "#" && ref.length > 1 ? document.getElementById(ref.slice(1)) : null;
  }

  // ---- modal --------------------------------------------------------------------------------
  var openModal = null;
  var modalTrigger = null;
  var backdrop = null;

  // The delete confirmation of a search result reads the document from the button that opened it.
  function fillDeleteConfirmation(modal, trigger) {
    var title = modal.querySelector(".modal-body #delete-doc-title");
    var url = modal.querySelector(".modal-body #delete-doc-url");
    var docId = modal.querySelector(".modal-footer input#docId");
    if (title) {
      title.textContent = trigger.getAttribute("data-title") || "";
    }
    if (url) {
      url.textContent = trigger.getAttribute("data-url") || "";
    }
    if (docId) {
      docId.value = trigger.getAttribute("data-docid") || "";
    }
  }

  function showModal(modal, trigger) {
    if (openModal) {
      hideModal();
    }
    if (modal.id === "confirmToDelete" && trigger) {
      fillDeleteConfirmation(modal, trigger);
    }
    backdrop = document.createElement("div");
    backdrop.className = "modal-backdrop fade show";
    document.body.appendChild(backdrop);
    document.body.classList.add("modal-open");
    modal.classList.add("show");
    modal.removeAttribute("aria-hidden");
    modal.setAttribute("aria-modal", "true");
    openModal = modal;
    modalTrigger = trigger || null;
    var footer = modal.querySelector(".modal-footer");
    var first = modal.querySelector("[autofocus]") || (footer && footer.querySelector(FOCUSABLE)) || modal;
    first.focus();
  }

  function hideModal() {
    if (!openModal) {
      return;
    }
    openModal.classList.remove("show");
    openModal.setAttribute("aria-hidden", "true");
    openModal.removeAttribute("aria-modal");
    document.body.classList.remove("modal-open");
    if (backdrop && backdrop.parentNode) {
      backdrop.parentNode.removeChild(backdrop);
    }
    backdrop = null;
    var back = modalTrigger;
    openModal = null;
    modalTrigger = null;
    if (back && document.contains(back)) {
      back.focus();
    }
  }

  // Tab keeps the focus inside the open dialog.
  function trapFocus(event) {
    var items = Array.prototype.filter.call(openModal.querySelectorAll(FOCUSABLE), function(el) {
      return el.offsetParent !== null;
    });
    if (items.length === 0) {
      event.preventDefault();
      return;
    }
    var first = items[0];
    var last = items[items.length - 1];
    if (event.shiftKey && (document.activeElement === first || document.activeElement === openModal)) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  // ---- collapse, tabs, side menu -------------------------------------------------------------
  function toggleCollapse(trigger) {
    var target = byHash(trigger);
    if (!target) {
      return;
    }
    var shown = target.classList.toggle("show");
    trigger.setAttribute("aria-expanded", shown ? "true" : "false");
  }

  function activateTab(trigger) {
    var pane = byHash(trigger);
    if (!pane) {
      return;
    }
    var nav = trigger.closest(".nav");
    if (nav) {
      Array.prototype.forEach.call(nav.querySelectorAll('[data-toggle="tab"]'), function(link) {
        link.classList.remove("active");
        link.setAttribute("aria-selected", "false");
      });
    }
    trigger.classList.add("active");
    trigger.setAttribute("aria-selected", "true");
    if (pane.parentNode) {
      Array.prototype.forEach.call(pane.parentNode.children, function(sibling) {
        sibling.classList.remove("active");
      });
    }
    pane.classList.add("active");
  }

  // Arrow keys, Home and End move between the tabs of one tab list (WAI-ARIA tabs pattern); Space opens the focused tab.
  function moveTab(link, key) {
    var nav = link.closest(".nav");
    var tabs = nav ? Array.prototype.slice.call(nav.querySelectorAll('[data-toggle="tab"]')) : [];
    var at = tabs.indexOf(link);
    if (at < 0) {
      return false;
    }
    var next = key === "Home" ? 0 : key === "End" ? tabs.length - 1
      : key === "ArrowRight" ? (at + 1) % tabs.length : (at - 1 + tabs.length) % tabs.length;
    tabs[next].focus();
    activateTab(tabs[next]);
    return true;
  }

  function togglePushMenu(trigger) {
    var body = document.body;
    var narrow = window.matchMedia(NARROW).matches;
    var open = narrow ? body.classList.toggle("sidebar-open") : !body.classList.toggle("sidebar-collapse");
    trigger.setAttribute("aria-expanded", open ? "true" : "false");
  }

  function toggleTreeview(link) {
    var item = link.parentElement;
    var open = item.classList.toggle("menu-open");
    link.setAttribute("aria-expanded", open ? "true" : "false");
  }

  // ---- delegated handlers --------------------------------------------------------------------
  document.addEventListener("click", function(event) {
    var target = event.target;
    if (!(target instanceof Element)) {
      return;
    }
    var trigger = target.closest('[data-toggle="modal"]');
    if (trigger) {
      var modal = byHash(trigger);
      if (modal) {
        event.preventDefault();
        showModal(modal, trigger);
      }
      return;
    }
    if (openModal && (target.closest('[data-dismiss="modal"]') || target === openModal)) {
      hideModal();
      return;
    }
    trigger = target.closest('[data-toggle="collapse"]');
    if (trigger) {
      event.preventDefault();
      toggleCollapse(trigger);
      return;
    }
    trigger = target.closest('[data-toggle="tab"]');
    if (trigger) {
      event.preventDefault();
      activateTab(trigger);
      return;
    }
    trigger = target.closest('[data-widget="pushmenu"]');
    if (trigger) {
      event.preventDefault();
      togglePushMenu(trigger);
      return;
    }
    trigger = target.closest('[data-widget="treeview"] .has-treeview > .nav-link');
    if (trigger) {
      event.preventDefault();
      toggleTreeview(trigger);
      return;
    }
    // the dimmed page behind the opened side menu on a narrow screen
    if (target === document.body && document.body.classList.contains("sidebar-open")) {
      document.body.classList.remove("sidebar-open");
      return;
    }
    // a list row opens its details
    var row = target.closest(".table tr[data-href]");
    if (row) {
      document.location = row.getAttribute("data-href");
    }
  });

  document.addEventListener("keydown", function(event) {
    if (event.key === "Escape" && !openModal && document.body.classList.contains("sidebar-open")) {
      document.body.classList.remove("sidebar-open");
      var opener = document.querySelector('[data-widget="pushmenu"]');
      if (opener) {
        opener.setAttribute("aria-expanded", "false");
        opener.focus();
      }
      return;
    }
    if (openModal) {
      if (event.key === "Escape") {
        hideModal();
      } else if (event.key === "Tab") {
        trapFocus(event);
      }
      return;
    }
    var target = event.target;
    if (!(target instanceof Element)) {
      return;
    }

    var tab = target.closest('[data-toggle="tab"]');
    if (tab && (event.key === " " || /^(ArrowLeft|ArrowRight|Home|End)$/.test(event.key))) {
      if (event.key === " ") {
        activateTab(tab);
        event.preventDefault();
      } else if (moveTab(tab, event.key)) {
        event.preventDefault();
      }
      return;
    }

    // A row that is announced as a button opens with Enter or Space, like a button.
    if (target.matches(".table tr[data-href]")) {
      if (event.key === "Enter" || event.key === " ") {
        event.preventDefault();
        document.location = target.getAttribute("data-href");
      }
      return;
    }

    // Enter in a field presses the primary button of its form: Search on a list, Create or Update
    // on an edit form. Without this the browser would press the first submit button, which is Back.
    if (event.key === "Enter" && target.matches("section.content input") && !target.matches(NO_ENTER_SUBMIT)) {
      // Enter that confirms an IME composition
      if (event.isComposing || event.keyCode === 229) {
        return;
      }
      var form = target.closest("form");
      var submit = form && form.querySelector("input#submit:not(:disabled), button#submit:not(:disabled), .btn-primary[type=submit]:not(:disabled)");
      if (submit) {
        submit.click();
      }
      // ignore enter key down
      event.preventDefault();
    }
  });

  // ---- page preparation ----------------------------------------------------------------------
  function focusFirstField() {
    var first = document.querySelector(':is(.login-box, section.content) :is(input[type="text"], select, textarea)');
    var error = document.querySelector(".form-group .has-error");
    var errorField = error && error.nextElementSibling;
    if (errorField && errorField.matches("input, select, textarea")) {
      errorField.focus();
    } else if (first) {
      first.focus();
    }
  }

  // The date range is one text field on the server ("yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm"); it is
  // filled from two native date-time inputs.
  var RANGE_SEPARATOR = " - ";

  function toLocalValue(text) {
    var match = /^\s*(\d{4}-\d{2}-\d{2})[ T](\d{2}:\d{2})\s*$/.exec(text || "");
    return match ? match[1] + "T" + match[2] : "";
  }

  function toText(value) {
    return value ? value.replace("T", " ") : "";
  }

  function initRangePickers(root) {
    Array.prototype.forEach.call(root.querySelectorAll("input.datetimerange"), function(field) {
      if (field.getAttribute("data-range-ready")) {
        return;
      }
      field.setAttribute("data-range-ready", "true");
      var label = field.id ? document.querySelector('label[for="' + field.id + '"]') : null;
      var name = label ? label.textContent.trim() : "";
      var parts = (field.value || "").split(RANGE_SEPARATOR);

      var box = document.createElement("div");
      box.className = "range-picker";
      var inputs = [parts[0], parts[1]].map(function(value) {
        var input = document.createElement("input");
        input.type = "datetime-local";
        input.step = 60;
        input.className = "form-control";
        input.value = toLocalValue(value);
        if (name) {
          input.setAttribute("aria-label", name);
        }
        return input;
      });
      var separator = document.createElement("span");
      separator.className = "range-sep";
      separator.setAttribute("aria-hidden", "true");
      separator.textContent = "–";
      box.appendChild(inputs[0]);
      box.appendChild(separator);
      box.appendChild(inputs[1]);

      function sync() {
        var start = toText(inputs[0].value);
        var end = toText(inputs[1].value);
        field.value = start || end ? start + RANGE_SEPARATOR + end : "";
      }
      inputs[0].addEventListener("change", sync);
      inputs[1].addEventListener("change", sync);

      field.type = "hidden";
      field.parentNode.insertBefore(box, field);
    });
  }

  // ---- form layout: short fields side by side, long forms in tabs ------------------------------
  function fieldCell(row) {
    return Array.prototype.filter.call(row.children, function(child) {
      return /(^|\s)col-/.test(child.className) && !child.classList.contains("col-form-label");
    })[0];
  }

  // A row is compact when its field is short: an inline cell, a toggle or a lone select.
  function isCompactRow(row) {
    if (!row.matches(".form-group.row") || !row.querySelector(".col-form-label")) {
      return false;
    }
    var cell = fieldCell(row);
    if (!cell || cell.querySelector("textarea, table, .alert")) {
      return false;
    }
    if (cell.classList.contains("form-inline") || cell.querySelector(".form-check")) {
      return true;
    }
    return cell.querySelectorAll("select").length === 1 && !cell.querySelector("input:not([type=hidden])");
  }

  function groupCompactRows(root) {
    var parents = [];
    Array.prototype.forEach.call(root.querySelectorAll(".form-group.row"), function(row) {
      if (parents.indexOf(row.parentNode) < 0) {
        parents.push(row.parentNode);
      }
    });
    parents.forEach(function(parent) {
      if (parent.classList.contains("form-compact-grid")) {
        return;
      }
      var run = [];
      function flush() {
        if (run.length > 1) {
          var grid = document.createElement("div");
          grid.className = "form-compact-grid";
          parent.insertBefore(grid, run[0]);
          run.forEach(function(row) {
            grid.appendChild(row);
          });
        }
        run = [];
      }
      Array.prototype.slice.call(parent.children).forEach(function(child) {
        if (isCompactRow(child)) {
          run.push(child);
        } else {
          flush();
        }
      });
      flush();
    });
  }

  var sectionCount = 0;

  // A card body whose h4 headings split it into sections becomes tabs; the headings are the tab
  // titles. Anything before the first heading (messages) stays above the tabs.
  function buildSectionTabs(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".card-body"), function(body) {
      var heads = Array.prototype.filter.call(body.children, function(child) {
        return child.tagName === "H4";
      });
      if (heads.length < 2 || body.getAttribute("data-sections-ready")) {
        return;
      }
      body.setAttribute("data-sections-ready", "true");
      sectionCount += 1;
      var nav = document.createElement("ul");
      nav.className = "nav nav-tabs form-tabs";
      nav.setAttribute("role", "tablist");
      var content = document.createElement("div");
      content.className = "tab-content";
      body.insertBefore(nav, heads[0]);
      body.insertBefore(content, heads[0]);

      var firstWithError = null;
      heads.forEach(function(head, index) {
        var id = "section-" + sectionCount + "-" + index;
        var pane = document.createElement("div");
        pane.className = "tab-pane";
        pane.id = id;
        pane.setAttribute("role", "tabpanel");
        pane.setAttribute("aria-labelledby", id + "-tab");
        var nodes = [head];
        for (var node = head.nextElementSibling; node && node.tagName !== "H4"; node = node.nextElementSibling) {
          nodes.push(node);
        }
        nodes.forEach(function(node) {
          pane.appendChild(node);
        });
        head.classList.add("section-title");
        content.appendChild(pane);

        var item = document.createElement("li");
        item.className = "nav-item";
        item.setAttribute("role", "presentation");
        var link = document.createElement("a");
        link.className = "nav-link";
        link.id = id + "-tab";
        link.href = "#" + id;
        link.setAttribute("data-toggle", "tab");
        link.setAttribute("role", "tab");
        link.setAttribute("aria-controls", id);
        link.textContent = head.textContent.trim();
        item.appendChild(link);
        nav.appendChild(item);

        var problem = pane.querySelector(".has-error, .alert-danger");
        if (problem) {
          link.classList.add("has-error");
          // the red dot after the title is not enough for a screen reader or a colour-blind reader: the first message is the tab's description
          link.setAttribute("title", (problem.textContent || "").replace(/\s+/g, " ").trim());
          firstWithError = firstWithError || link;
        }
      });
      activateTab(firstWithError || nav.querySelector(".nav-link"));
    });
  }

  // A field the browser refuses to submit must not stay hidden in another tab.
  function revealInvalidField(event) {
    var pane = event.target.closest && event.target.closest(".tab-pane");
    if (pane && !pane.classList.contains("active")) {
      var link = document.getElementById(pane.id + "-tab");
      if (link) {
        activateTab(link);
      }
    }
  }

  // A card footer that holds no control and no text (a list without rows leaves its buttons out) would
  // be an empty bar under the card body.
  function hideEmptyFooters(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".card-footer"), function(footer) {
      if (!footer.querySelector("button, a[href], input:not([type=hidden]), select, textarea") && !footer.textContent.trim()) {
        footer.hidden = true;
      }
    });
  }

  function initFormLayout(root) {
    hideEmptyFooters(root);
    groupCompactRows(root);
    buildSectionTabs(root);
  }

  // A link to a tab (#section-1-3) opens it; the tabs are built by initFormLayout, so this runs after it.
  function openTabOfAddress() {
    var id = decodeURIComponent((location.hash || "").slice(1));
    var pane = id ? document.getElementById(id) : null;
    var link = pane && pane.classList.contains("tab-pane") ? document.getElementById(id + "-tab") : null;
    if (link) {
      activateTab(link);
    }
  }

  // The state of the side menu is told from the start, not only after the first click.
  function initMenuState() {
    Array.prototype.forEach.call(document.querySelectorAll('[data-widget="treeview"] .has-treeview > .nav-link'), function(link) {
      link.setAttribute("aria-expanded", link.parentElement.classList.contains("menu-open") ? "true" : "false");
    });
    Array.prototype.forEach.call(document.querySelectorAll('[data-widget="pushmenu"]'), function(button) {
      var narrow = window.matchMedia && window.matchMedia(NARROW).matches;
      var open = narrow ? document.body.classList.contains("sidebar-open") : !document.body.classList.contains("sidebar-collapse");
      button.setAttribute("aria-expanded", open ? "true" : "false");
    });
  }

  function init() {
    initFormLayout(document);
    initRangePickers(document);
    initMenuState();
    openTabOfAddress();
    focusFirstField();
  }

  document.addEventListener("invalid", revealInvalidField, true);

  window.FessAdmin = { init: init };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
