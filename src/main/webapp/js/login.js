$(function() {
  var $firstInput = $('input[type="text"],select,textarea', ".fads-auth-card,section.content,.fads-main-content").first();
  var $errorInput = $(".fads-form-field .fads-textfield-error").first().next("input,select,textarea");

  if ($errorInput.length) {
    $errorInput.focus();
  } else {
    $firstInput.focus();
  }

  $("section.content input, .fads-main-content input").on("keypress", function(e) {
    if (e.which === 13) {
      var $submitButton = $("input#submit, button#submit");
      if ($submitButton.length > 0) {
        $submitButton.closest("form").trigger("submit");
      }
      return false;
    }
  });

  $(".fads-table tr[data-href]").each(function() {
    var $row = $(this);
    $row.css("cursor", "pointer")
      .on("mouseenter", function() {
        $(this).addClass("active");
      })
      .on("mouseleave", function() {
        $(this).removeClass("active");
      })
      .on("click", function() {
        window.location.href = $(this).attr("data-href");
      });
  });

  $("[data-fads-dialog='confirmToDelete']").on("click", function(event) {
    var button = $(event.currentTarget);
    var docId = button.data("docid");
    var title = button.data("title");
    var url = button.data("url");

    var $dialog = $("#confirmToDelete");
    $dialog.find("#delete-doc-title").text(title);
    $dialog.find("#delete-doc-url").text(url);
    $dialog.find("input#docId").val(docId);
  });
});
