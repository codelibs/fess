/**
 * Fess ADS UI - Search Pages
 * Vanilla JS replacement for Bootstrap 5 JS
 * Handles: dropdowns, collapse, tooltips, theme toggle, navbar toggle
 * (No sidebar - search pages don't have one)
 */
(function () {
  'use strict';

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
      var targetSelector = toggle.getAttribute('data-fads-collapse');
      var target = document.querySelector(targetSelector);
      if (!target) return;
      target.classList.toggle('show');
      toggle.setAttribute('aria-expanded', target.classList.contains('show'));
    });
  }

  // ========================================
  // Navbar Toggle (mobile hamburger)
  // ========================================
  function initNavbarToggle() {
    document.addEventListener('click', function (e) {
      var toggle = e.target.closest('.fads-navbar-toggler');
      if (!toggle) return;
      e.preventDefault();
      var targetSelector = toggle.getAttribute('data-fads-target');
      var target = document.querySelector(targetSelector);
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
      'z-index:var(--ds-z-tooltip);pointer-events:none;max-width:240px;white-space:normal;';
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
    initDropdowns();
    initCollapse();
    initNavbarToggle();
    initTooltips();
    initThemeToggle();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
