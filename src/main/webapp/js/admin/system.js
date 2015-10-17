$(function(){
	var $content = $('#content');
	var $contentFrame = $('#contentFrame');
    $contentFrame.load(function(){
        $(this).height($content.height());
    });
});
