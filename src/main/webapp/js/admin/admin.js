$(function() {
  $('input[type="text"],select,textarea', ".login-box,section.content")
    .first()
    .focus();
  $(".form-group .has-error")
    .first()
    .next("input,select,textarea")
    .focus();

  $("section.content input").keypress(function(e) {
    if (e.which === 13) {
      var $submitButton = $("input#submit, button#submit");
      if ($submitButton.size() > 0) {
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
