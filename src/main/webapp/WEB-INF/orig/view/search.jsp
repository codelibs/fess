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
<link href="${f:url('/css/admin/font-awesome.min.css')}"
	rel="stylesheet" type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<ul class="searchOptionLabels">
					<li><la:message key="labels.searchoptions_menu_sort" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="modal"> <c:if test="${empty sort}">
								<la:message key="labels.searchoptions_score" />
							</c:if> <c:if test="${sort=='created.asc'}">
								<la:message key="labels.search_result_sort_created_asc" />
							</c:if> <c:if test="${sort=='created.desc'}">
								<la:message key="labels.search_result_sort_created_desc" />
							</c:if> <c:if test="${sort=='contentLength.asc'}">
								<la:message key="labels.search_result_sort_contentLength_asc" />
							</c:if> <c:if test="${sort=='contentLength.desc'}">
								<la:message key="labels.search_result_sort_contentLength_desc" />
							</c:if> <c:if test="${sort=='lastModified.asc'}">
								<la:message key="labels.search_result_sort_lastModified_asc" />
							</c:if> <c:if test="${sort=='lastModified.desc'}">
								<la:message key="labels.search_result_sort_lastModified_desc" />
							</c:if> <c:if test="${sort=='clickCount_l_x_dv.asc'}">
								<la:message key="labels.search_result_sort_clickCount_asc" />
							</c:if> <c:if test="${sort=='clickCount_l_x_dv.desc'}">
								<la:message key="labels.search_result_sort_clickCount_desc" />
							</c:if> <c:if test="${sort=='favoriteCount_l_x_dv.asc'}">
								<la:message key="labels.search_result_sort_favoriteCount_asc" />
							</c:if> <c:if test="${sort=='favoriteCount_l_x_dv.desc'}">
								<la:message key="labels.search_result_sort_favoriteCount_desc" />
							</c:if>
					</a></li>
					<li><la:message key="labels.searchoptions_menu_num" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="modal"> <la:message
								key="labels.searchoptions_num" arg0="${f:h(num)}" />
					</a></li>
					<li><la:message key="labels.searchoptions_menu_lang" /> <a
						href="#searchOptions" class="label label-primary"
						data-toggle="modal"> <c:if test="${empty lang}">
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
							data-toggle="modal"> <c:if test="${empty fields.label}">
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
		<c:if test="${fe:hswsize(null) != 0}">
			<div class="row">
				<div class="col-md-12">
					<p class="hotSearchWordBody ellipsis">
						<la:message key="labels.search_hot_search_word" />
						<c:forEach var="item" items="${fe:hsw(null, 5)}">
							<la:link
								href="/search/search?query=${f:u(item)}${fe:facetQuery()}${fe:geoQuery()}">${f:h(item)}</la:link>
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
	<input type="hidden" id="contextPath"
		value="<%=request.getContextPath()%>" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-2.1.4.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/search.js')}"></script>
</body>
</html>
