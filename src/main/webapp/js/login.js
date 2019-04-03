$(function() {
  $('input[type="text"],select,textarea', ".login-box,section.content")
    .first()
    .focus();
  $(".form-group .has-error")
    .first()
    .next("input,select,textarea")
    .focus();

  $("section.content input").keypress(function(e) {
    var $submitButton;
    if (e.which === 13) {
      $submitButton = $("input#submit, button#submit");
      if ($submitButton.length > 0) {
        $submitButton[0].submit();
      }
      // ignore enter key down
      return false;
    }
  });

  $(".table tr[data-href]").each(function() {
    $(this)
      .css("cursor", "pointer")
      .hover(
        function() {
          $(this).addClass("active");
        },
        function() {
          $(this).removeClass("active");
        }
      )
      .click(function() {
        document.location = $(this).attr("data-href");
      });
  });

  $("#confirmToDelete").on("show.bs.modal", function(event) {
    var button = $(event.relatedTarget),
        docId = button.data("docid"),
        title = button.data("title"),
        url = button.data("url");

    $(this)
      .find(".modal-body #delete-doc-title")
      .text(title);
    $(this)
      .find(".modal-body #delete-doc-url")
      .text(url);
    $(this)
      .find(".modal-footer input#docId")
      .val(docId);
  });
});
