$(function() {
  const $searchButton = $("#searchButton");
  const contextPath = $("#contextPath").val();
  const BUTTON_DISABLE_DURATION = 3000;

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

  if (typeof $.fn.suggestor === "function") {
    $("#query").suggestor(SUGGESTOR_CONFIG);
  }
});
