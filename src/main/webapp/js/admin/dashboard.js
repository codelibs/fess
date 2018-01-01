$(function(){
    var $content = $('#content');
    var $contentFrame = $('#contentFrame');
    $contentFrame.on('load', function(){
        $(this).height($content.height());
    });
});
