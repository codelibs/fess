$(function() {
  const $result = $("#result");
  const $queryId = $("#queryId");
  const $favorites = $(".favorite", $result);
  const $searchButton = $("#searchButton");
  const contextPath = $("#contextPath").val();
  const IMG_LOADING_DELAY = 200;
  const IMG_LOADING_MAX = 0;
  const BUTTON_DISABLE_DURATION = 3000;
  const AJAX_TIMEOUT = 10000;
  const FADE_DURATION = 1000;

  const SUGGESTOR_CONFIG = {
    ajaxinfo: {
      url: contextPath + "/api/v1/suggest-words",
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

  $("#searchForm").on("submit", () => {
    $searchButton.prop("disabled", true);
    setTimeout(() => {
      $searchButton.prop("disabled", false);
    }, BUTTON_DISABLE_DURATION);
    return true;
  });

  $(document).on("click touchend", (e) => {
    if (!$(e.target).closest("#searchOptions, [data-toggle='control-options']").length) {
      $("#searchOptions").removeClass("active");
    }
  });

  $("[data-toggle='control-options']").on("click", function(e) {
    e.preventDefault();
    const target = $(this).attr("data-target") || $(this).attr("href");
    if (target) {
      $(target).toggleClass("active");
    }
  });

  $("#searchOptionsClearButton").on("click", (e) => {
    e.preventDefault();
    $("#labelTypeSearchOption").prop("selectedIndex", -1);
    $("#langSearchOption").prop("selectedIndex", 0);
    $("#sortSearchOption").prop("selectedIndex", 0);
    $("#numSearchOption").prop("selectedIndex", 0);
  });

  const buildGoUrl = (docId, url, includeQueryIdAndOrder = true) => {
    const rt = $("#rt").val();
    const params = new URLSearchParams({
      rt: rt,
      docId: docId
    });

    if (includeQueryIdAndOrder) {
      const queryId = $("#queryId").val();
      const order = $(this).attr("data-order");
      params.append("queryId", queryId);
      params.append("order", order);
    }

    const hashIndex = url.indexOf("#");
    if (hashIndex >= 0) {
      const hashStr = url.substring(hashIndex);
      params.append("hash", hashStr);
      return `${contextPath}/go/?${params.toString()}${includeQueryIdAndOrder ? "" : hashStr}`;
    }

    return `${contextPath}/go/?${params.toString()}`;
  };

  $result.on("mousedown", "a.link", function(e) {
    const $link = $(this);
    const docId = $link.attr("data-id");
    const url = $link.attr("href");
    const queryId = $("#queryId").val();
    const order = $link.attr("data-order");

    const goUrl = `${contextPath}/go/?rt=${$("#rt").val()}&docId=${docId}&queryId=${queryId}&order=${order}${
      url.includes("#") ? `&hash=${encodeURIComponent(url.substring(url.indexOf("#")))}` : ""
    }`;

    $link.attr("href", goUrl);
  });

  $result.on("click", "a.favorite", function(e) {
    e.preventDefault();
    const $favorite = $(this);
    const values = $favorite.attr("href").split("#");

    if (values.length === 2 && $queryId.length > 0) {
      const docId = values[1];
      const actionUrl = `${contextPath}/api/v1/documents/${docId}/favorite`;

      $.ajax({
        dataType: "json",
        cache: false,
        type: "post",
        timeout: AJAX_TIMEOUT,
        url: actionUrl,
        data: {
          queryId: $queryId.val()
        }
      })
        .done((data) => {
          if (data.result === "created") {
            const $favorited = $favorite.siblings(".favorited");
            const $favoritedCount = $(".favorited-count", $favorited);
            $favoritedCount.hide();
            $favorite.fadeOut(FADE_DURATION, () => {
              $favorited.fadeIn(FADE_DURATION);
            });
          }
        })
        .fail((jqXHR, textStatus, errorThrown) => {
          $favorite.attr("href", `#${docId}`);
          console.error("Failed to add favorite:", textStatus, errorThrown);
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
      url: `${contextPath}/api/v1/favorites`,
      data: {
        queryId: $queryId.val()
      }
    })
      .done((data) => {
        if (data.record_count > 0) {
          const docIds = new Set(data.data.map(item => `#${item.doc_id}`));

          $favorites.each(function() {
            const $favorite = $(this);
            const url = $favorite.attr("href");

            if (docIds.has(url)) {
              const $favorited = $favorite.siblings(".favorited");
              $favorite.fadeOut(FADE_DURATION, () => {
                $favorited.fadeIn(FADE_DURATION);
              });
            }
          });
        }
      })
      .fail((jqXHR, textStatus, errorThrown) => {
        console.error("Failed to load favorites:", textStatus, errorThrown);
      });
  }

  $result.on("click", ".more a", function(e) {
    e.preventDefault();
    const $moreLink = $(this);
    const value = $moreLink.attr("href");

    if (value) {
      const $info = $(`${value} .info`);
      if ($info.length > 0) {
        $moreLink.fadeOut(500, () => {
          $info.slideDown("slow");
        });
      }
    }
    return false;
  });

  if (typeof $.fn.suggestor === "function") {
    $("#query").suggestor(SUGGESTOR_CONFIG);
  }

  const loadImage = (img, url, limit) => {
    const imgData = new Image();

    $(imgData).on("load", () => {
      $(img).css("background-image", "");
      $(img).attr("src", url);
    });

    $(imgData).on("error", () => {
      if (limit > 0) {
        setTimeout(() => {
          loadImage(img, url, limit - 1);
        }, IMG_LOADING_DELAY);
      } else {
        $(img).parent().parent().hide();
      }
    });

    imgData.src = url;
  };

  $("img.thumbnail").each(function() {
    const $img = $(this);
    $img.css("background-image", `url("${contextPath}/images/loading.gif")`);
    loadImage(this, $img.attr("data-src"), IMG_LOADING_MAX);
  });

  const clipboard = new ClipboardJS(".url-copy");
  clipboard.on("success", (e) => {
    const trigger = e.trigger;
    trigger.classList.remove("url-copy", "far", "fa-copy");
    trigger.classList.add("url-copied", "fas", "fa-check");

    setTimeout(() => {
      trigger.classList.remove("url-copied", "fas", "fa-check");
      trigger.classList.add("url-copy", "far", "fa-copy");
    }, 2000);

    e.clearSelection();
  });
});
