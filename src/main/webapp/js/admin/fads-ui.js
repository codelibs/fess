/**
 * Fess ADS UI - Admin Panel
 * Vanilla JS replacement for Bootstrap JS + AdminLTE JS
 * Handles: sidebar, dialogs, dropdowns, collapse, tooltips, theme toggle
 */
(function () {
  'use strict';

  // ========================================
  // Sidebar Toggle
  // ========================================
  function initSidebar() {
    var toggleBtn = document.querySelector('.fads-sidenav-toggle');
    var wrapper = document.querySelector('.fads-layout-wrapper');
    if (!toggleBtn || !wrapper) return;

    // Restore collapsed state from localStorage
    if (localStorage.getItem('fads_sidebar_collapsed') === 'true') {
      wrapper.classList.add('fads-sidenav-collapsed');
    }

    toggleBtn.addEventListener('click', function (e) {
      e.preventDefault();
      wrapper.classList.toggle('fads-sidenav-collapsed');
      localStorage.setItem(
        'fads_sidebar_collapsed',
        wrapper.classList.contains('fads-sidenav-collapsed')
      );
    });

    // Close sidebar on mobile when clicking outside
    document.addEventListener('click', function (e) {
      if (window.innerWidth < 992 &&
        !wrapper.classList.contains('fads-sidenav-collapsed') &&
        !e.target.closest('.fads-sidenav') &&
        !e.target.closest('.fads-sidenav-toggle')) {
        wrapper.classList.add('fads-sidenav-collapsed');
      }
    });
  }

  // ========================================
  // Sidebar Treeview (expand/collapse sections)
  // ========================================
  function initTreeview() {
    var sectionToggles = document.querySelectorAll('.fads-sidenav [aria-expanded]');
    sectionToggles.forEach(function (item) {
      var link = item.querySelector(':scope > .fads-sidenav-link');
      if (!link) return;
      link.addEventListener('click', function (e) {
        e.preventDefault();
        var expanded = item.getAttribute('aria-expanded') === 'true';
        item.setAttribute('aria-expanded', !expanded);
      });
    });
  }

  // ========================================
  // Dialog (Modal)
  // ========================================
  function openDialog(dialogId) {
    var overlay = document.getElementById(dialogId);
    if (!overlay) return;
    overlay.classList.add('show');
    overlay.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';

    // Focus first focusable element
    var focusable = overlay.querySelector('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
    if (focusable) focusable.focus();
  }

  function closeDialog(overlay) {
    if (!overlay) return;
    overlay.classList.remove('show');
    overlay.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
  }

  function initDialogs() {
    // Open dialog triggers
    document.addEventListener('click', function (e) {
      var trigger = e.target.closest('[data-fads-dialog]');
      if (trigger) {
        e.preventDefault();
        var dialogId = trigger.getAttribute('data-fads-dialog');
        openDialog(dialogId);

        // Pass data attributes to dialog (for delete confirmation)
        var overlay = document.getElementById(dialogId);
        if (overlay) {
          var docId = trigger.getAttribute('data-docid');
          var title = trigger.getAttribute('data-title');
          var url = trigger.getAttribute('data-url');
          if (docId) {
            var idInput = overlay.querySelector('input#docId');
            if (idInput) idInput.value = docId;
          }
          if (title) {
            var titleEl = overlay.querySelector('#delete-doc-title');
            if (titleEl) titleEl.textContent = title;
          }
          if (url) {
            var urlEl = overlay.querySelector('#delete-doc-url');
            if (urlEl) urlEl.textContent = url;
          }
        }
      }
    });

    // Close dialog triggers
    document.addEventListener('click', function (e) {
      var closeBtn = e.target.closest('[data-fads-dialog-close]');
      if (closeBtn) {
        e.preventDefault();
        var overlay = closeBtn.closest('.fads-dialog-overlay');
        closeDialog(overlay);
        return;
      }

      // Close on overlay background click
      if (e.target.classList.contains('fads-dialog-overlay') && e.target.classList.contains('show')) {
        closeDialog(e.target);
      }
    });

    // Close on Escape key
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') {
        var openOverlay = document.querySelector('.fads-dialog-overlay.show');
        if (openOverlay) closeDialog(openOverlay);
      }
    });
  }

  // ========================================
  // Dropdown
  // ========================================
  function initDropdowns() {
    document.addEventListener('click', function (e) {
      var toggle = e.target.closest('[data-fads-dropdown]');
      if (toggle) {
        e.preventDefault();
        e.stopPropagation();
        var parent = toggle.closest('.fads-dropdown') || toggle.parentElement;
        var menu = parent.querySelector('.fads-dropdown-menu');
        if (!menu) return;

        // Close all other dropdowns
        document.querySelectorAll('.fads-dropdown-menu.show').forEach(function (m) {
          if (m !== menu) m.classList.remove('show');
        });

        menu.classList.toggle('show');
        return;
      }

      // Close all dropdowns when clicking elsewhere
      document.querySelectorAll('.fads-dropdown-menu.show').forEach(function (m) {
        m.classList.remove('show');
      });
    });
  }

  // ========================================
  // Collapse
  // ========================================
  function initCollapse() {
    document.addEventListener('click', function (e) {
      var toggle = e.target.closest('[data-fads-collapse]');
      if (!toggle) return;
      e.preventDefault();
      var targetId = toggle.getAttribute('data-fads-collapse');
      var target = document.querySelector(targetId);
      if (!target) return;
      target.classList.toggle('show');
      toggle.setAttribute('aria-expanded', target.classList.contains('show'));
    });
  }

  // ========================================
  // Tooltip
  // ========================================
  var activeTooltip = null;

  function showTooltip(el) {
    removeTooltip();
    var text = el.getAttribute('data-fads-tooltip') || el.getAttribute('title');
    if (!text) return;

    // Prevent native title tooltip
    if (el.hasAttribute('title')) {
      el.setAttribute('data-fads-tooltip', text);
      el.removeAttribute('title');
    }

    var tip = document.createElement('div');
    tip.className = 'fads-tooltip';
    tip.textContent = text;
    tip.style.cssText = 'position:fixed;padding:4px 8px;background:var(--ds-surface-overlay);color:var(--ds-text);' +
      'border-radius:var(--ds-radius-100);font-size:var(--ds-font-size-075);box-shadow:var(--ds-shadow-overlay);' +
      'z-index:' + 'var(--ds-z-tooltip)' + ';pointer-events:none;max-width:240px;white-space:normal;';
    document.body.appendChild(tip);

    var rect = el.getBoundingClientRect();
    var placement = el.getAttribute('data-placement') || 'top';
    var tipRect = tip.getBoundingClientRect();

    if (placement === 'left') {
      tip.style.top = (rect.top + rect.height / 2 - tipRect.height / 2) + 'px';
      tip.style.left = (rect.left - tipRect.width - 8) + 'px';
    } else if (placement === 'bottom') {
      tip.style.top = (rect.bottom + 8) + 'px';
      tip.style.left = (rect.left + rect.width / 2 - tipRect.width / 2) + 'px';
    } else {
      // top (default)
      tip.style.top = (rect.top - tipRect.height - 8) + 'px';
      tip.style.left = (rect.left + rect.width / 2 - tipRect.width / 2) + 'px';
    }

    activeTooltip = tip;
  }

  function removeTooltip() {
    if (activeTooltip) {
      activeTooltip.remove();
      activeTooltip = null;
    }
  }

  function initTooltips() {
    document.addEventListener('mouseenter', function (e) {
      var el = e.target.closest('[data-fads-tooltip]') || e.target.closest('[title]');
      if (el && (el.hasAttribute('data-fads-tooltip') || el.hasAttribute('title'))) {
        showTooltip(el);
      }
    }, true);

    document.addEventListener('mouseleave', function (e) {
      var el = e.target.closest('[data-fads-tooltip]');
      if (el) removeTooltip();
    }, true);
  }

  // ========================================
  // Theme Toggle
  // ========================================
  function getTheme() {
    return document.documentElement.getAttribute('data-color-mode') || 'light';
  }

  function setTheme(theme) {
    document.documentElement.setAttribute('data-color-mode', theme);
    document.cookie = 'fess_theme=' + theme + ';path=/;max-age=31536000;SameSite=Lax';
    updateThemeIcons(theme);
  }

  function updateThemeIcons(theme) {
    document.querySelectorAll('.fads-theme-toggle i').forEach(function (icon) {
      if (theme === 'dark') {
        icon.className = 'fas fa-sun';
      } else {
        icon.className = 'fas fa-moon';
      }
    });
  }

  function initThemeToggle() {
    updateThemeIcons(getTheme());

    document.addEventListener('click', function (e) {
      var toggle = e.target.closest('.fads-theme-toggle');
      if (!toggle) return;
      e.preventDefault();
      var current = getTheme();
      setTheme(current === 'dark' ? 'light' : 'dark');
    });
  }

  // ========================================
  // Initialize all components on DOM ready
  // ========================================
  function init() {
    initSidebar();
    initTreeview();
    initDialogs();
    initDropdowns();
    initCollapse();
    initTooltips();
    initThemeToggle();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
