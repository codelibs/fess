$(function(){
	var $searchButton = $('#searchButton');

	$('#searchForm').on('submit', function(e) {
		$searchButton.attr('disabled', true);
		setTimeout(function() {
			$searchButton.attr('disabled', false);
		}, 3000);
		return true;
	});

	$('#searchOptionsClearButton').on('click', function(e) {
		$('#labelTypeSearchOption option').removeProp('selected');
		$('#sortSearchOption option').removeProp('selected');
		$('#numSearchOption option').removeProp('selected');
		return false;
	});

});
