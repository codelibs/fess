$(function(){
	var $result = $('#result')
	var $queryId = $('#queryId');
	var $favorites = $('.favorite', $result);
	var $screenshot = $('#screenshot', $result);
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

	$result.on('mousedown', 'a.link', function(e){
		var docId = $(this).attr('data-id'),
			rt = $('#rt').val(),
			buf = [];
		buf.push('go?rt=');
		buf.push(rt);
		buf.push('&docId=');
		buf.push(docId);
		$(this).attr('href', buf.join(''));
	});

	$result.on('mouseover', 'a.link', function(e){
		if($screenshot.size() > 0) {
			var docId = $(this).attr('data-id'),
				rt = $('#rt').val(),
				queryId = $queryId.val(),
				buf = [];
			buf.push('go?rt=');
			buf.push(rt);
			buf.push('&docId=');
			buf.push(docId);

			$screenshot.children().remove();
			
			var content = '<a href="' + buf.join('') + '"><img src="screenshot?queryId='
				+ queryId + '&docId=' + docId + '"></a>'
			$screenshot.append(content);
			$('img', $screenshot).error(function() {
				$screenshot.children().remove();
			});
		}
	});

	$result.on('click', 'a.favorite', function(e){
		var $favorite = $(this);
		var values = $favorite.attr('href').split('#');
		if(values.length === 2 && $queryId.size() > 0){
			var contextPath = $('#contextPath').val();
			var actionUrl = contextPath + '/json';
			var docId = values[1];
			$.ajax({
				dataType: 'json',
				cache: false,
				type: 'post',
				timeoutNumber: 10000,
				url: actionUrl,
				data: {
					type: 'favorite',
					docId: docId,
					queryId: $queryId.val()
					}
			}).done(function ( data ) {
				if(data.response.status === 0 
					&& typeof data.response.result !== 'undefined'
					&& data.response.result === 'ok'){
					var $favorited = $favorite.siblings('.favorited');
					$favorite.fadeOut(1000, function(){$favorited.fadeIn(1000)});
				}
			}).fail(function ( data ) {
				$favorite.attr('href', '#' + docId);
//alert(JSON.stringify(data));
			});
		}
		$(this).attr('href', '#');
		return false;
	});

	if($favorites.size() > 0) {
		var contextPath = $('#contextPath').val();
		$.ajax({
			dataType: 'json',
			cache: false,
			type: 'post',
			timeoutNumber: 10000,
			url: contextPath + '/json',
			data: {
				type: 'favorites',
				queryId: $queryId.val()
			}
		}).done(function ( data ) {
			if(data.response.status === 0 
				&& typeof data.response.num !== 'undefined'
				&& data.response.num > 0){
				var docIds = data.response.docIds;
				for(var i = 0; i < docIds.length; i++) {
					docIds[i] = '#' + docIds[i];
				}
				$favorites.each(function(index) {
					var $favorite = $(this);
					var url = $favorite.attr('href');
					var found = false;
					for(var i = 0; i< docIds.length; i++) {
						if(url == docIds[i]) {
							found = true;
							break;
						}
					}
					if(found){
						var $favorited = $favorite.siblings('.favorited');
						$favorite.fadeOut(1000, function(){$favorited.fadeIn(1000)});
					}
				});
			}
		}).fail(function ( data ) {
//alert(JSON.stringify(data));
		});
	}

	$result.on('click', '.more a', function(e) {
		var $moreLink = $(this);
		var value = $moreLink.attr('href');
		if(value != '') {
			var $info = $(value + ' .info');
			if($info.size() > 0) {
				$moreLink.fadeOut(500, function(){
					$info.slideDown("slow");
				});
			}
		}
		return false;
	});
});
