$(function() {
	$('input[type="text"]:first', '.login-box,section.content').focus();

	$('.table tr[data-href]').each(function() {
		$(this).css('cursor', 'pointer').hover(function() {
			$(this).addClass('active');
		}, function() {
			$(this).removeClass('active');
		}).click(function() {
			document.location = $(this).attr('data-href');
		});
	});
});
