$(function() {
  var $searchButton = $("#searchButton");
  var contextPath = $("#contextPath").val();
  var BUTTON_DISABLE_DURATION = 3000;

  var SUGGESTOR_CONFIG = {
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

  $("#searchForm").on("submit", function() {
    $searchButton.prop("disabled", true);
    setTimeout(function() {
      $searchButton.prop("disabled", false);
    }, BUTTON_DISABLE_DURATION);
    return true;
  });

  if (typeof $.fn.suggestor === "function") {
    $("#as_q").suggestor(SUGGESTOR_CONFIG);
  }
});
