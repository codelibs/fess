$(function() {
  var $result = $("#result"),
      $queryId = $("#queryId"),
      $favorites = $(".favorite", $result),
      $searchButton = $("#searchButton"),
      contextPath = $("#contextPath").val(),
      loadImage;

  $("#searchForm").on("submit", function(e) {
    $searchButton.attr("disabled", true);
    setTimeout(function() {
      $searchButton.attr("disabled", false);
    }, 3000);
    return true;
  });

  $("#content").on("click touchend", function(e) {
    if (
      !$(e.target).closest("#searchOptions, [data-toggle='control-options']")
        .length
    ) {
      $("#searchOptions").removeClass("active");
    }
  });

  $("[data-toggle='control-options']").click(function(e) {
    e.preventDefault();
    var target = $(this).attr("data-target") || $(this).attr("href");
    if (target) {
      $(target).toggleClass("active");
    }
  });

  $("#searchOptionsClearButton").on("click", function(e) {
    $("#labelTypeSearchOption").prop("selectedIndex", -1);
    $("#langSearchOption").prop("selectedIndex", 0);
    $("#sortSearchOption").prop("selectedIndex", 0);
    $("#numSearchOption").prop("selectedIndex", 0);
    return false;
  });

  $result.on("mousedown", "a.link", function(e) {
    var docId = $(this).attr("data-id"),
      rt = $("#rt").val(),
      queryId = $("#queryId").val(),
      order = $(this).attr("data-order"),
      url = $(this).attr("href"),
      buf = [],
      hashIndex,
      hashStr;
    buf.push(contextPath);
    buf.push("/go/?rt=");
    buf.push(rt);
    buf.push("&docId=");
    buf.push(docId);
    buf.push("&queryId=");
    buf.push(queryId);
    buf.push("&order=");
    buf.push(order);

    hashIndex = url.indexOf("#");
    if (hashIndex >= 0) {
      hashStr = url.substring(hashIndex);
      buf.push("&hash=");
      buf.push(encodeURIComponent(hashStr));
    }

    $(this).attr("href", buf.join(""));
  });

  $result.on("mouseover", "a.link", function(e) {
    var docId = $(this).attr("data-id"),
      rt = $("#rt").val(),
      url = $(this).attr("href"),
      buf = [],
      hashIndex,
      hashStr;
    buf.push(contextPath);
    buf.push("/go/?rt=");
    buf.push(rt);
    buf.push("&docId=");
    buf.push(docId);

    hashIndex = url.indexOf("#");
    if (hashIndex >= 0) {
      hashStr = url.substring(hashIndex);
      buf.push("&hash=");
      buf.push(encodeURIComponent(hashStr));
      buf.push(hashStr);
    }
  });

  $result.on("click", "a.favorite", function(e) {
    var $favorite = $(this),
        values = $favorite.attr("href").split("#"),
        actionUrl,
        docId;
    if (values.length === 2 && $queryId.length > 0) {
      actionUrl = contextPath + "/json";
      docId = values[1];
      $.ajax({
        dataType: "json",
        cache: false,
        type: "post",
        timeoutNumber: 10000,
        url: actionUrl,
        data: {
          type: "favorite",
          docId: docId,
          queryId: $queryId.val()
        }
      })
        .done(function(data) {
          var $favorited,
              $favoritedCount;
          if (
            data.response.status === 0 &&
            typeof data.response.result !== "undefined" &&
            data.response.result === "ok"
          ) {
            $favorited = $favorite.siblings(".favorited");
            $favoritedCount = $(".favorited-count", $favorited);
            $favoritedCount.css("display", "none");
            $favorite.fadeOut(1000, function() {
              $favorited.fadeIn(1000);
            });
          }
        })
        .fail(function(data) {
          $favorite.attr("href", "#" + docId);
          // alert(JSON.stringify(data));
        });
    }
    $(this).attr("href", "#");
    return false;
  });

  if ($favorites.length > 0) {
    $.ajax({
      dataType: "json",
      cache: false,
      type: "post",
      timeoutNumber: 10000,
      url: contextPath + "/json",
      data: {
        type: "favorites",
        queryId: $queryId.val()
      }
    })
      .done(function(data) {
        var docIds,
            i;
        if (
          data.response.status === 0 &&
          typeof data.response.num !== "undefined" &&
          data.response.num > 0
        ) {
          docIds = data.response.doc_ids;
          for (i = 0; i < docIds.length; i++) {
            docIds[i] = "#" + docIds[i];
          }
          $favorites.each(function(index) {
            var $favorite = $(this),
                url = $favorite.attr("href"),
                found = false,
                $favorited,
                i;
            for (i = 0; i < docIds.length; i++) {
              if (url === docIds[i]) {
                found = true;
                break;
              }
            }
            if (found) {
              $favorited = $favorite.siblings(".favorited");
              $favorite.fadeOut(1000, function() {
                $favorited.fadeIn(1000);
              });
            }
          });
        }
      })
      .fail(function(data) {
        // alert(JSON.stringify(data));
      });
  }

  $result.on("click", ".more a", function(e) {
    var $moreLink = $(this),
        value = $moreLink.attr("href"),
        $info;
    if (value !== "") {
      $info = $(value + " .info");
      if ($info.length > 0) {
        $moreLink.fadeOut(500, function() {
          $info.slideDown("slow");
        });
      }
    }
    return false;
  });

  if (typeof $.fn.suggestor === "function") {
    $("#query").suggestor({
      ajaxinfo: {
        url: contextPath + "/suggest",
        fn: "_default,content,title",
        num: 10,
        lang: $("#langSearchOption").val()
      },
      boxCssInfo: {
        border: "1px solid rgba(82, 168, 236, 0.5)",
        "-webkit-box-shadow":
          "0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)",
        "-moz-box-shadow":
          "0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)",
        "box-shadow":
          "0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)",
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
    });
  }

  IMG_LOADING_DELAY = 200;
  IMG_LOADING_MAX = 0;
  loadImage = function(img, url, limit) {
    var imgData = new Image();
    $(imgData).on("load", function() {
      $(img).css("background-image", "");
      $(img).attr("src", url);
    });
    $(imgData).on("error", function() {
      if (limit > 0) {
        setTimeout(function() {
          loadImage(img, url, --limit);
        }, IMG_LOADING_DELAY);
      } else {
        // $(img).attr('src', contextPath + "/images/noimage.png");
        $(img)
          .parent()
          .parent()
          .css("display", "none");
      }
      imgData = null;
    });
    imgData.src = url;
  };

  $("img.thumbnail").each(function() {
    $(this).css(
      "background-image",
      'url("' + contextPath + '/images/loading.gif")'
    );
    loadImage(this, $(this).attr("data-src"), IMG_LOADING_MAX);
  });
});
