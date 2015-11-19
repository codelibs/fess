$(function() {
	$('input[type="text"],select', '.login-box,section.content').first().focus();

	$("section.content input").keypress(function(e) {
		if (e.which == 13) {
			// ignore enter key down
			return false;
		}
	});

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
