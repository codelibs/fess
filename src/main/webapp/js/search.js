$(function() {
  var $result = $("#result");
  var $queryId = $("#queryId");
  var $favorites = $(".favorite", $result);
  var $searchButton = $("#searchButton");
  var contextPath = $("#contextPath").val();
  var IMG_LOADING_DELAY = 200;
  var IMG_LOADING_MAX = 0;
  var BUTTON_DISABLE_DURATION = 3000;
  var AJAX_TIMEOUT = 10000;
  var FADE_DURATION = 1000;

  // CSRF token cache for v2 state-changing endpoints. Populated lazily from
  // GET /api/v2/ui/config, refreshed on demand (e.g. after a 403). When the
  // server reports csrf_required=false, the token stays empty and the request
  // header below carries an empty string — which the server accepts because
  // the gate short-circuits on the global toggle.
  var csrfTokenCache = null;
  var csrfTokenPromise = null;
  function fetchCsrfToken(force) {
    if (!force && csrfTokenCache !== null) {
      return $.Deferred().resolve(csrfTokenCache).promise();
    }
    if (csrfTokenPromise) {
      return csrfTokenPromise;
    }
    csrfTokenPromise = $.ajax({
      dataType: "json",
      cache: false,
      type: "get",
      timeout: AJAX_TIMEOUT,
      url: contextPath + "/api/v2/ui/config"
    }).then(function(data) {
      var resp = (data && data.response) || {};
      csrfTokenCache = typeof resp.csrf_token === "string" ? resp.csrf_token : "";
      return csrfTokenCache;
    }, function() {
      // On failure, fall back to an empty token so the caller can still
      // attempt the POST; the server will reject with 403 if a token was
      // actually required, and the UI will surface the error.
      csrfTokenCache = "";
      return csrfTokenCache;
    }).always(function() {
      csrfTokenPromise = null;
    });
    return csrfTokenPromise;
  }

  var SUGGESTOR_CONFIG = {
    ajaxinfo: {
      url: contextPath + "/api/v2/suggest-words",
      fn: ["_default", "content", "title"],
      num: 10,
      lang: $("#langSearchOption").val()
    },
    boxCssInfo: {
      border: "1px solid rgba(82, 168, 236, 0.5)",
      "box-shadow": "0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)",
      "background-color": "#fff",
      "z-index": "10000"
    },
    listSelectedCssInfo: {
      "background-color": "rgba(82, 168, 236, 0.1)"
    },
    listDeselectedCssInfo: {
      "background-color": "#ffffff"
    },
    minterm: 1,
    adjustWidthVal: 11,
    searchForm: $("#searchForm")
  };

  $("#searchForm").on("submit", function() {
    $searchButton.prop("disabled", true);
    var $icon = $searchButton.find("i.fa-search");
    if ($icon.length > 0) {
      $icon.removeClass("fa fa-search").addClass("spinner-border spinner-border-sm");
    }
    setTimeout(function() {
      $searchButton.prop("disabled", false);
      var $spinner = $searchButton.find(".spinner-border");
      if ($spinner.length > 0) {
        $spinner.removeClass("spinner-border spinner-border-sm").addClass("fa fa-search");
      }
    }, BUTTON_DISABLE_DURATION);
    return true;
  });

  $(document).on("click touchend", function(e) {
    if (!$(e.target).closest("#searchOptions, [data-toggle='control-options']").length) {
      $("#searchOptions").removeClass("active");
    }
  });

  $("[data-toggle='control-options']").on("click", function(e) {
    e.preventDefault();
    var target = $(this).attr("data-target") || $(this).attr("href");
    if (target) {
      $(target).toggleClass("active");
    }
  });

  $("#searchOptionsClearButton").on("click", function(e) {
    e.preventDefault();
    $("#labelTypeSearchOption").prop("selectedIndex", -1);
    $("#langSearchOption").prop("selectedIndex", 0);
    $("#sortSearchOption").prop("selectedIndex", 0);
    $("#numSearchOption").prop("selectedIndex", 0);
  });

  $result.on("mousedown", "a.link", function(e) {
    var $link = $(this);
    var docId = $link.attr("data-id");
    var url = $link.attr("href");
    var queryId = $("#queryId").val();
    var order = $link.attr("data-order");
    var rt = $("#rt").val();
    var goUrl = contextPath + "/go/?rt=" + rt + "&docId=" + docId + "&queryId=" + queryId + "&order=" + order;

    var hashIndex = url.indexOf("#");
    if (hashIndex >= 0) {
      var hashStr = url.substring(hashIndex);
      goUrl += "&hash=" + encodeURIComponent(hashStr);
    }

    $link.attr("href", goUrl);
  });

  $result.on("click", "a.favorite", function(e) {
    e.preventDefault();
    var $favorite = $(this);
    var values = $favorite.attr("href").split("#");

    if (values.length === 2 && $queryId.length > 0) {
      var docId = values[1];
      var actionUrl = contextPath + "/api/v2/documents/" + docId + "/favorite";
      var requestBody = JSON.stringify({ query_id: $queryId.val() });

      var postFavorite = function(token, allowRetry) {
        return $.ajax({
          dataType: "json",
          cache: false,
          type: "post",
          timeout: AJAX_TIMEOUT,
          url: actionUrl,
          contentType: "application/json",
          headers: { "X-Fess-CSRF-Token": token || "" },
          data: requestBody
        })
          .done(function(data) {
            var resp = (data && data.response) || {};
            if (resp.status === 0 && resp.ok === true) {
              var $favorited = $favorite.siblings(".favorited");
              var $favoritedCount = $(".favorited-count", $favorited);
              $favoritedCount.hide();
              $favorite.fadeOut(FADE_DURATION, function() {
                $favorited.fadeIn(FADE_DURATION);
              });
            }
          })
          .fail(function(jqXHR, textStatus, errorThrown) {
            // 403 typically means a stale/missing CSRF token. Refresh once
            // and retry transparently before giving up.
            if (allowRetry && jqXHR && jqXHR.status === 403) {
              fetchCsrfToken(true).then(function(freshToken) {
                postFavorite(freshToken, false);
              });
              return;
            }
            $favorite.attr("href", "#" + docId);
            // 401: adding a favorite requires login (FavoritePostHandler's
            // AUTH_REQUIRED gate). 403 after the token refresh above: the CSRF
            // gate rejects a session-bound token before the auth check runs, so
            // a guest whose session rotated after page load surfaces as 403
            // instead of 401. Logging in issues a fresh session + token, so
            // send both to the login page — same spec as the static themes,
            // which open the login modal here.
            if (jqXHR && (jqXHR.status === 401 || jqXHR.status === 403)) {
              window.location.href = contextPath + "/login/";
              return;
            }
            console.error("Failed to add favorite:", textStatus, errorThrown);
          });
      };

      fetchCsrfToken(false).then(function(token) {
        postFavorite(token, true);
      });
    }
    $favorite.attr("href", "#");
    return false;
  });

  if ($favorites.length > 0) {
    $.ajax({
      dataType: "json",
      cache: false,
      type: "get",
      timeout: AJAX_TIMEOUT,
      url: contextPath + "/api/v2/favorites",
      data: {
        query_id: $queryId.val()
      }
    })
      .done(function(data) {
        var resp = (data && data.response) || {};
        var items = resp.data || [];
        if (resp.record_count > 0 && items.length > 0) {
          var docIdsLookup = {};
          var i;
          for (i = 0; i < items.length; i++) {
            docIdsLookup["#" + items[i].doc_id] = true;
          }

          $favorites.each(function() {
            var $favorite = $(this);
            var url = $favorite.attr("href");

            if (docIdsLookup[url]) {
              var $favorited = $favorite.siblings(".favorited");
              $favorite.fadeOut(FADE_DURATION, function() {
                $favorited.fadeIn(FADE_DURATION);
              });
            }
          });
        }
      })
      .fail(function(jqXHR, textStatus, errorThrown) {
        // 401: guest session — favorites are per-user, so a guest has nothing
        // to sync. The star itself stays visible (write requires login and
        // redirects to the login page on click), matching the static themes.
        if (jqXHR && jqXHR.status === 401) {
          return;
        }
        console.error("Failed to load favorites:", textStatus, errorThrown);
      });
  }

  $result.on("click", ".more a", function(e) {
    e.preventDefault();
    var $moreLink = $(this);
    var value = $moreLink.attr("href");

    if (value) {
      var $info = $(value + " .info");
      if ($info.length > 0) {
        $moreLink.fadeOut(500, function() {
          $info.slideDown("slow");
        });
      }
    }
    return false;
  });

  if (typeof $.fn.suggestor === "function") {
    $("#query").suggestor(SUGGESTOR_CONFIG);
  }

  var loadImage = function(img, url, limit) {
    var imgData = new Image();

    $(imgData).on("load", function() {
      $(img).css("background-image", "");
      $(img).attr("src", url);
    });

    $(imgData).on("error", function() {
      if (limit > 0) {
        setTimeout(function() {
          loadImage(img, url, limit - 1);
        }, IMG_LOADING_DELAY);
      } else {
        $(img).parent().parent().hide();
      }
    });

    imgData.src = url;
  };

  $("img.thumbnail").each(function() {
    var $img = $(this);
    $img.css("background-image", 'url("' + contextPath + '/images/loading.gif")');
    loadImage(this, $img.attr("data-src"), IMG_LOADING_MAX);
  });

  var clipboard = new ClipboardJS(".url-copy");
  clipboard.on("success", function(e) {
    var trigger = e.trigger;
    trigger.classList.remove("url-copy", "far", "fa-copy");
    trigger.classList.add("url-copied", "fas", "fa-check");

    setTimeout(function() {
      trigger.classList.remove("url-copied", "fas", "fa-check");
      trigger.classList.add("url-copy", "far", "fa-copy");
    }, 2000);

    e.clearSelection();
  });
});
