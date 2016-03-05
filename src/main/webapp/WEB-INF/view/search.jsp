<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="x-ua-compatible" content="ie=edge">
<title>${f:h(displayQuery)}-<la:message
		key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${f:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${f:url('/css/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body class="search">
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<ul class="searchOptionLabels">
					<li><la:message key="labels.searchoptions_menu_sort" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="control-options"> <c:if test="${empty sort}">
								<la:message key="labels.searchoptions_score" />
							</c:if> <c:if test="${sort=='score.desc'}">
								<la:message key="labels.searchoptions_score" />
							</c:if> <c:if test="${sort=='created.asc'}">
								<la:message key="labels.search_result_sort_created_asc" />
							</c:if> <c:if test="${sort=='created.desc'}">
								<la:message key="labels.search_result_sort_created_desc" />
							</c:if> <c:if test="${sort=='content_length.asc'}">
								<la:message key="labels.search_result_sort_content_length_asc" />
							</c:if> <c:if test="${sort=='content_length.desc'}">
								<la:message key="labels.search_result_sort_content_length_desc" />
							</c:if> <c:if test="${sort=='last_modified.asc'}">
								<la:message key="labels.search_result_sort_last_modified_asc" />
							</c:if> <c:if test="${sort=='last_modified.desc'}">
								<la:message key="labels.search_result_sort_last_modified_desc" />
							</c:if> <c:if test="${sort=='click_count.asc'}">
								<la:message key="labels.search_result_sort_click_count_asc" />
							</c:if> <c:if test="${sort=='click_count.desc'}">
								<la:message key="labels.search_result_sort_click_count_desc" />
							</c:if> <c:if test="${sort=='favorite_count.asc'}">
								<la:message key="labels.search_result_sort_favorite_count_asc" />
							</c:if> <c:if test="${sort=='favorite_count.desc'}">
								<la:message key="labels.search_result_sort_favorite_count_desc" />
							</c:if> <c:if test="${sort.indexOf(',') >= 0}">
								<la:message key="labels.search_result_sort_multiple" />
							</c:if>
					</a></li>
					<li><la:message key="labels.searchoptions_menu_num" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="control-options"> <la:message
								key="labels.searchoptions_num" arg0="${f:h(num)}" />
					</a></li>
					<li><la:message key="labels.searchoptions_menu_lang" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="control-options"> <c:if test="${empty lang}">
								<la:message key="labels.searchoptions_all" />
							</c:if> <c:if test="${!empty lang}">
								<c:forEach var="sLang" items="${lang}">
									<c:forEach var="item" items="${langItems}">
										<c:if test="${item.value==sLang}">${f:h(item.label)}</c:if>
									</c:forEach>
								</c:forEach>
							</c:if>
					</a></li>
					<c:if test="${displayLabelTypeItems}">
						<li><la:message key="labels.searchoptions_menu_labels" /> <a
							href="#searchOptions" class="label label-primary"
							data-toggle="control-options"> <c:if
									test="${empty fields.label}">
									<la:message key="labels.searchoptions_all" />
								</c:if> <c:if test="${!empty fields.label}">
									<c:forEach var="sLabel" items="${fields.label}">
										<c:forEach var="item" items="${labelTypeItems}">
											<c:if test="${item.value==sLabel}">${f:h(item.label)}</c:if>
										</c:forEach>
									</c:forEach>
								</c:if>
						</a></li>
					</c:if>
				</ul>
			</div>
		</div>
		<c:if test="${!empty popularWords}">
			<div class="row">
				<div class="col-md-12">
					<p class="popularWordBody ellipsis">
						<la:message key="labels.search_popular_word_word" />
						<c:forEach var="item" items="${popularWords}">
							<la:link
								href="/search/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
						</c:forEach>
					</p>
				</div>
			</div>
		</c:if>
		<c:choose>
			<c:when test="${f:h(allRecordCount) != 0}">
				<jsp:include page="searchResults.jsp" />
				<p class="pull-right move-to-top">
					<a href="#"><la:message key="labels.footer_back_to_top" /></a>
				</p>
			</c:when>
			<c:otherwise>
				<jsp:include page="searchNoResult.jsp" />
			</c:otherwise>
		</c:choose>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-2.1.4.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/search.js')}"></script>
</body>
</html>
