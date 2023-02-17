<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="referrer" content="no-referrer">
<title>${f:h(displayQuery)}-<la:message
		key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${fe:url('/osdd')}"
		title="<la:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${fe:url('/css/bootstrap.min.css')}" rel="stylesheet"
	type="text/css" />
<link href="${fe:url('/css/style.css')}" rel="stylesheet" type="text/css" />
<link href="${fe:url('/css/font-awesome.min.css')}" rel="stylesheet"
	type="text/css" />
</head>
<body class="search">
	<jsp:include page="header.jsp" />
	<main id="content" class="container">
		<ul class="list-inline">
			<li class="list-inline-item"><la:message key="labels.searchoptions_menu_sort" /> <a
				href="#searchOptions" class="badge badge-primary"
				data-toggle="control-options"> <c:if test="${empty sort}">
						<la:message key="labels.searchoptions_score" />
					</c:if> <c:if test="${sort=='score.desc'}">
						<la:message key="labels.searchoptions_score" />
					</c:if> <c:if test="${sort=='filename.asc'}">
						<la:message key="labels.search_result_sort_filename_asc" />
					</c:if> <c:if test="${sort=='filename.desc'}">
						<la:message key="labels.search_result_sort_filename_desc" />
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
			<li class="list-inline-item"><la:message key="labels.searchoptions_menu_num" /> <a
				href="#searchOptions" class="badge badge-primary"
				data-toggle="control-options"> <la:message
						key="labels.searchoptions_num" arg0="${f:h(num)}" />
			</a></li>
			<li class="list-inline-item"><la:message key="labels.searchoptions_menu_lang" /> <a
				href="#searchOptions" class="badge badge-primary"
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
				<li class="list-inline-item"><la:message key="labels.searchoptions_menu_labels" /> <a
					href="#searchOptions" class="badge badge-primary"
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
		<c:if test="${!empty popularWords}">
			<div class="row">
				<div class="col">
					<p class="text-truncate">
						<la:message key="labels.search_popular_word_word" />
						<c:forEach var="item" varStatus="s" items="${popularWords}">
							<c:if test="${s.index < 3}">
								<la:link
									href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
							</c:if>
							<c:if test="${3 <= s.index}">
								<la:link styleClass="d-none d-sm-inline"
									href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
							</c:if>
						</c:forEach>
					</p>
				</div>
			</div>
		</c:if>
		<c:if test="${!empty relatedQueries}">
			<div class="row">
				<div class="col">
					<p class="text-truncate">
						<la:message key="labels.search_related_queries" />
						<c:forEach var="item" varStatus="s" items="${relatedQueries}">
							<c:if test="${s.index < 3}">
								<la:link
									href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
							</c:if>
							<c:if test="${3 <= s.index}">
								<la:link styleClass="d-none d-sm-inline"
									href="/search?q=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
							</c:if>
						</c:forEach>
					</p>
				</div>
			</div>
		</c:if>
		<c:forEach var="item" varStatus="s" items="${relatedContents}">
			<div class="row">
				<div class="col">
					${item}
				</div>
			</div>
		</c:forEach>
		<c:choose>
			<c:when test="${f:h(allRecordCount) != 0}">
				<jsp:include page="searchResults.jsp" />
				<div class="text-right">
					<a href="#"><la:message key="labels.footer_back_to_top" /></a>
				</div>
			</c:when>
			<c:otherwise>
				<jsp:include page="searchNoResult.jsp" />
			</c:otherwise>
		</c:choose>
	</main>
	<jsp:include page="footer.jsp" />
	<input type="hidden" id="contextPath" value="${contextPath}" />
	<script type="text/javascript"
		src="${fe:url('/js/jquery-3.6.3.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/clipboard.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${fe:url('/js/search.js')}"></script>
</body>
</html>
