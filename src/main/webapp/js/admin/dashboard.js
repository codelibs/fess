$(function() {
  var $content = $("#content"),
      $contentFrame = $("#contentFrame");
  $contentFrame.on("load", function() {
    $(this).height($content.height());
  });
});
