$(function() {
	var $result = $('#result');
	var $queryId = $('#queryId');
	var $favorites = $('.favorite', $result);
	var $searchButton = $('#searchButton');
	var contextPath = $('#contextPath').val();

	$('#searchForm').on('submit', function(e) {
		$searchButton.attr('disabled', true);
		setTimeout(function() {
			$searchButton.attr('disabled', false);
		}, 3000);
		return true;
	});

	$("[data-toggle='control-options']").click(function(e) {
		e.preventDefault();
		var target = $(this).attr('data-target') || $(this).attr('href');
		if (target) {
			$(target).toggleClass("active");
		}
	});

	$('#searchOptionsClearButton').on('click', function(e) {
		$('#labelTypeSearchOption').prop('selectedIndex', -1);
		$('#langSearchOption').prop('selectedIndex', 0);
		$('#sortSearchOption').prop('selectedIndex', 0);
		$('#numSearchOption').prop('selectedIndex', 0);
		return false;
	});

	$result.on('mousedown', 'a.link', function(e) {
		var docId = $(this).attr('data-id'), rt = $('#rt').val(), queryId = $(
				'#queryId').val(), order = $(this).attr('data-order'), url = $(
				this).attr('href'), buf = [];
		buf.push(contextPath);
		buf.push('/go/?rt=');
		buf.push(rt);
		buf.push('&docId=');
		buf.push(docId);
		buf.push('&queryId=');
		buf.push(queryId);
		buf.push('&order=');
		buf.push(order);

		var hashIndex = url.indexOf('#');
		if (hashIndex >= 0) {
			var hashStr = url.substring(hashIndex);
			buf.push('&hash=');
			buf.push(encodeURIComponent(hashStr));
			buf.push(hashStr);
		}

		$(this).attr('href', buf.join(''));
	});

	$result.on('mouseover', 'a.link', function(e) {
		var docId = $(this).attr('data-id'), rt = $('#rt').val(), url = $(
				this).attr('href'), queryId = $queryId.val(), buf = [];
		buf.push(contextPath);
		buf.push('/go/?rt=');
		buf.push(rt);
		buf.push('&docId=');
		buf.push(docId);

		var hashIndex = url.indexOf('#');
		if (hashIndex >= 0) {
			var hashStr = url.substring(hashIndex);
			buf.push('&hash=');
			buf.push(encodeURIComponent(hashStr));
			buf.push(hashStr);
		}
	});

	$result.on('click', 'a.favorite', function(e) {
		var $favorite = $(this);
		var values = $favorite.attr('href').split('#');
		if (values.length === 2 && $queryId.size() > 0) {
			var actionUrl = contextPath + '/json';
			var docId = values[1];
			$.ajax({
				dataType : 'json',
				cache : false,
				type : 'post',
				timeoutNumber : 10000,
				url : actionUrl,
				data : {
					type : 'favorite',
					docId : docId,
					queryId : $queryId.val()
				}
			}).done(
					function(data) {
						if (data.response.status === 0
								&& typeof data.response.result !== 'undefined'
								&& data.response.result === 'ok') {
							var $favorited = $favorite.siblings('.favorited');
							var $favoritedCount = $('.favorited-count',
									$favorited);
							$favoritedCount.css('display', 'none');
							$favorite.fadeOut(1000, function() {
								$favorited.fadeIn(1000);
							});
						}
					}).fail(function(data) {
				$favorite.attr('href', '#' + docId);
				// alert(JSON.stringify(data));
			});
		}
		$(this).attr('href', '#');
		return false;
	});

	if ($favorites.size() > 0) {
		$.ajax({
			dataType : 'json',
			cache : false,
			type : 'post',
			timeoutNumber : 10000,
			url : contextPath + '/json',
			data : {
				type : 'favorites',
				queryId : $queryId.val()
			}
		}).done(
				function(data) {
					if (data.response.status === 0
							&& typeof data.response.num !== 'undefined'
							&& data.response.num > 0) {
						var docIds = data.response.doc_ids;
						for (var i = 0; i < docIds.length; i++) {
							docIds[i] = '#' + docIds[i];
						}
						$favorites.each(function(index) {
							var $favorite = $(this);
							var url = $favorite.attr('href');
							var found = false;
							for (var i = 0; i < docIds.length; i++) {
								if (url === docIds[i]) {
									found = true;
									break;
								}
							}
							if (found) {
								var $favorited = $favorite
										.siblings('.favorited');
								$favorite.fadeOut(1000, function() {
									$favorited.fadeIn(1000);
								});
							}
						});
					}
				}).fail(function(data) {
			// alert(JSON.stringify(data));
		});
	}

	$result.on('click', '.more a', function(e) {
		var $moreLink = $(this);
		var value = $moreLink.attr('href');
		if (value !== '') {
			var $info = $(value + ' .info');
			if ($info.size() > 0) {
				$moreLink.fadeOut(500, function() {
					$info.slideDown("slow");
				});
			}
		}
		return false;
	});

	$('#query')
			.suggestor(
					{
						ajaxinfo : {
							url : contextPath + '/suggest',
							fn : '_default,content,title',
							num : 10
						},
						boxCssInfo : {
							border : '1px solid rgba(82, 168, 236, 0.5)',
							'-webkit-box-shadow' : '0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)',
							'-moz-box-shadow' : '0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)',
							'box-shadow' : '0 1px 1px 0px rgba(0, 0, 0, 0.1), 0 3px 2px 0px rgba(82, 168, 236, 0.2)',
							'background-color' : '#fff',
							'z-index' : '10000'
						},
						listSelectedCssInfo : {
							'background-color' : 'rgba(82, 168, 236, 0.1)'
						},
						listDeselectedCssInfo : {
							'background-color' : '#ffffff'
						},
						minturm : 1,
						adjustWidthVal : 11,
						searchForm : $('#searchForm')
					});

});
