$(function() {
  $('input[type="text"],select,textarea', ".fads-auth-card,section.content,.fads-card-body")
    .first()
    .focus();
  $(".has-error")
    .first()
    .next("input,select,textarea")
    .focus();

  $("section.content input,.fads-card-body input").keypress(function(e) {
    if (e.which === 13) {
      var $submitButton = $("input#submit, button#submit");
      if ($submitButton.size() > 0) {
        $submitButton[0].submit();
      }
      // ignore enter key down
      return false;
    }
  });

  $(".fads-table tr[data-href]").each(function() {
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

  // Date range picker
  var lang = (
    window.navigator.userLanguage ||
    window.navigator.language ||
    window.navigator.browserLanguage
  ).substr(0, 2);
  moment.locale(lang);
  $("input.fads-textfield.date")
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
  $("input.fads-textfield.daterange")
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
  $("input.fads-textfield.datetime")
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
  $("input.fads-textfield.datetimerange")
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
  $("input.fads-textfield.time").timepicker({
    showInputs: false
  });
});
