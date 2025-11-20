$(function() {
  const $firstInput = $('input[type="text"],select,textarea', ".login-box,section.content").first();
  const $errorInput = $(".form-group .has-error").first().next("input,select,textarea");

  if ($errorInput.length) {
    $errorInput.focus();
  } else {
    $firstInput.focus();
  }

  $("section.content input").on("keypress", function(e) {
    if (e.which === 13) {
      const $submitButton = $("input#submit, button#submit");
      if ($submitButton.length > 0) {
        $submitButton.closest("form").trigger("submit");
      }
      return false;
    }
  });

  $(".table tr[data-href]").each(function() {
    const $row = $(this);
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
    const button = $(event.relatedTarget);
    const docId = button.data("docid");
    const title = button.data("title");
    const url = button.data("url");

    const $modal = $(this);
    $modal.find(".modal-body #delete-doc-title").text(title);
    $modal.find(".modal-body #delete-doc-url").text(url);
    $modal.find(".modal-footer input#docId").val(docId);
  });
});
