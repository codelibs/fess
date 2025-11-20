$(function() {
  var $firstInput = $('input[type="text"],select,textarea', ".login-box,section.content").first();
  var $errorInput = $(".form-group .has-error").first().next("input,select,textarea");

  if ($errorInput.length) {
    $errorInput.focus();
  } else {
    $firstInput.focus();
  }

  $("section.content input").on("keypress", function(e) {
    if (e.which === 13) {
      var $submitButton = $("input#submit, button#submit");
      if ($submitButton.length > 0) {
        $submitButton.closest("form").trigger("submit");
      }
      return false;
    }
  });

  $(".table tr[data-href]").each(function() {
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

  $("#confirmToDelete").on("show.bs.modal", function(event) {
    var button = $(event.relatedTarget);
    var docId = button.data("docid");
    var title = button.data("title");
    var url = button.data("url");

    var $modal = $(this);
    $modal.find(".modal-body #delete-doc-title").text(title);
    $modal.find(".modal-body #delete-doc-url").text(url);
    $modal.find(".modal-footer input#docId").val(docId);
  });
});
