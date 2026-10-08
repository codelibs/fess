$(function() {
  $('input[type="text"],select,textarea', ".login-box,section.content")
    .first()
    .focus();
  $(".form-group .has-error")
    .first()
    .next("input,select,textarea")
    .focus();

  // Enter in a field presses the primary button of its form: Search on a list, Create or Update
  // on an edit form. Without this the browser would press the first submit button, which is Back.
  $("section.content input")
    .not("[type=button],[type=submit],[type=reset],[type=image],[type=file]")
    .keypress(function(e) {
      if (e.which === 13) {
        if (e.originalEvent && e.originalEvent.isComposing) {
          // Enter that confirms an IME composition
          return;
        }
        var $submitButton = $(this)
          .closest("form")
          .find("input#submit, button#submit, .btn-primary[type=submit]")
          .not(":disabled")
          .first();
        if ($submitButton.length > 0) {
          $submitButton[0].click();
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
      })
      .keydown(function(e) {
        // A row that is announced as a button opens with Enter or Space, like a button.
        if (e.target === this && (e.which === 13 || e.which === 32)) {
          e.preventDefault();
          document.location = $(this).attr("data-href");
        }
      });
  });

  $("#confirmToDelete").on("show.bs.modal", function(event) {
    var button = $(event.relatedTarget);
    var docId = button.data("docid");
    var title = button.data("title");
    var url = button.data("url");

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

  // Date range picker
  var lang = (
    window.navigator.userLanguage ||
    window.navigator.language ||
    window.navigator.browserLanguage
  ).substr(0, 2);
  moment.locale(lang);
  $("input.form-control.date")
    .daterangepicker({
      autoUpdateInput: false,
      timePicker: false,
      singleDatePicker: true,
      locale: {
        format: "YYYY-MM-DD"
      }
    })
    .on("apply.daterangepicker", function(ev, picker) {
      $(this).val(picker.startDate.format("YYYY-MM-DD"));
    });
  $("input.form-control.daterange")
    .daterangepicker({
      autoUpdateInput: false,
      timePicker: false,
      singleDatePicker: false,
      locale: {
        format: "YYYY-MM-DD"
      }
    })
    .on("apply.daterangepicker", function(ev, picker) {
      $(this).val(
        picker.startDate.format("YYYY-MM-DD") +
          " - " +
          picker.endDate.format("YYYY-MM-DD")
      );
    });
  $("input.form-control.datetime")
    .daterangepicker({
      autoUpdateInput: false,
      timePicker: true,
      timePickerIncrement: 10,
      singleDatePicker: true,
      locale: {
        format: "YYYY-MM-DD HH:mm"
      }
    })
    .on("apply.daterangepicker", function(ev, picker) {
      $(this).val(picker.startDate.format("YYYY-MM-DD HH:mm"));
    });
  $("input.form-control.datetimerange")
    .daterangepicker({
      autoUpdateInput: false,
      timePicker: true,
      timePickerIncrement: 10,
      singleDatePicker: false,
      locale: {
        format: "YYYY-MM-DD HH:mm"
      }
    })
    .on("apply.daterangepicker", function(ev, picker) {
      $(this).val(
        picker.startDate.format("YYYY-MM-DD HH:mm") +
          " - " +
          picker.endDate.format("YYYY-MM-DD HH:mm")
      );
    });

  // Time picker
  $("input.form-control.time").timepicker({
    showInputs: false
  });

  // tooltips
  $(function() {
    $('[data-toggle="tooltip"]').tooltip();
  });
});
