$(function() {
  var $searchButton = $("#searchButton"),
      contextPath = $("#contextPath").val();

  $("#searchForm").on("submit", function(e) {
    $searchButton.attr("disabled", true);
    setTimeout(function() {
      $searchButton.attr("disabled", false);
    }, 3000);
    return true;
  });

  if (typeof $.fn.suggestor === "function") {
    $("#as_q").suggestor({
      ajaxinfo: {
        url: contextPath + "/api/v1/suggest-words",
        fn: ["_default", "content", "title"],
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
});
