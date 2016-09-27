<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<script src="${f:url('/js/admin/jquery-2.2.4.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/admin/bootstrap.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/admin/app.min.js')}" type="text/javascript"></script>
<script src="${f:url('/js/admin/admin.js')}" type="text/javascript"></script>
<script src="${f:url('/js/admin/jquery.form-validator.min.js')}" type="text/javascript"></script>
<script>
$.validate({
    modules : 'japan, html5, location, date, security, file',
    onModulesLoaded : function() {
      $('#country').suggestCountry();
    }
  });

  // Restrict presentation length
  $('#presentation').restrictLength( $('#pres-max-length') );

</script>
