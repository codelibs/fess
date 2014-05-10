<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://a9.com/-/spec/opensearch/1.1/">
<meta charset="utf-8">
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${f:h(displayQuery)} - <bean:message
		key="labels.search_title" /></title>
<c:if test="${osddLink}">
	<link rel="search" type="application/opensearchdescription+xml"
		href="${f:url('/osdd')}"
		title="<bean:message key="labels.index_osdd_title" />" />
</c:if>
<link href="${f:url('/css/style-base.css')}" rel="stylesheet"
	type="text/css" />
<link href="${f:url('/css/style.css')}" rel="stylesheet" type="text/css" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<div class="row">
			<div class="span9">
				<p id="searchOptionMenu">
					<span>
						<bean:message key="labels.searchoptions_menu_sort" />
						<a href="#searchOptions" data-toggle="modal">
						<c:if test="${empty sort}"><bean:message key="labels.searchoptions_score" /></c:if>
						<c:if test="${sort=='created.asc'}"><bean:message key="labels.search_result_sort_created_asc" /></c:if>
						<c:if test="${sort=='created.desc'}"><bean:message key="labels.search_result_sort_created_desc" /></c:if>
						<c:if test="${sort=='contentLength.asc'}"><bean:message key="labels.search_result_sort_contentLength_asc" /></c:if>
						<c:if test="${sort=='contentLength.desc'}"><bean:message key="labels.search_result_sort_contentLength_desc" /></c:if>
						<c:if test="${sort=='lastModified.asc'}"><bean:message key="labels.search_result_sort_lastModified_asc" /></c:if>
						<c:if test="${sort=='lastModified.desc'}"><bean:message key="labels.search_result_sort_lastModified_desc" /></c:if>
						<c:if test="${sort=='clickCount_l_x_dv.asc'}"><bean:message key="labels.search_result_sort_clickCount_asc" /></c:if>
						<c:if test="${sort=='clickCount_l_x_dv.desc'}"><bean:message key="labels.search_result_sort_clickCount_desc" /></c:if>
						<c:if test="${sort=='favoriteCount_l_x_dv.asc'}"><bean:message key="labels.search_result_sort_favoriteCount_asc" /></c:if>
						<c:if test="${sort=='favoriteCount_l_x_dv.desc'}"><bean:message key="labels.search_result_sort_favoriteCount_desc" /></c:if>
						</a>
					</span>
					<span>
						<bean:message key="labels.searchoptions_menu_num" />
						<a href="#searchOptions" data-toggle="modal">
						<bean:message key="labels.searchoptions_num" arg0="${f:h(num)}" />
						</a>
					</span>
					<span>
						<bean:message key="labels.searchoptions_menu_lang" />
						<a href="#searchOptions" data-toggle="modal">
						<c:if test="${empty lang}"><bean:message key="labels.searchoptions_all" /></c:if>
						<c:if test="${!empty lang}">
							<c:forEach var="sLang" items="${lang}">
								<c:forEach var="item" items="${langItems}">
									<c:if test="${item.value==sLang}">${f:h(item.label)}</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						</a>
					</span>
					<c:if test="${displayLabelTypeItems}">
					<span>
						<bean:message key="labels.searchoptions_menu_labels" />
						<a href="#searchOptions" data-toggle="modal">
						<c:if test="${empty fields.label}"><bean:message key="labels.searchoptions_all" /></c:if>
						<c:if test="${!empty fields.label}">
							<c:forEach var="sLabel" items="${fields.label}">
								<c:forEach var="item" items="${labelTypeItems}">
									<c:if test="${item.value==sLabel}">${f:h(item.label)}</c:if>
								</c:forEach>
							</c:forEach>
						</c:if>
						</a>
					</span>
					</c:if>
				</p>
			</div>
			<div class="span3">
				<c:if test="${!empty username}">
				<p class="username">
					<bean:message key="labels.searchheader_username" arg0="${f:h(username)}" />
					|
					<s:link href="/login/logout" styleClass="logout-link">
						<bean:message key="labels.logout" />
					</s:link>
				</p>
				</c:if>
			</div>
		</div>
		<c:if test="${fe:hswsize(null) != 0}">
			<div class="row">
				<div class="span12">
					<p class="hotSearchWordBody ellipsis">
						<bean:message key="labels.search_hot_search_word" />
						<c:forEach var="item" items="${fe:hsw(null, 5)}">
							<html:link href="search?query=${f:u(item)}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">${f:h(item)}</html:link>
						</c:forEach>
					</p>
				</div>
			</div>
		</c:if>
		<c:choose>
			<c:when test="${f:h(allRecordCount) != 0}">
				<jsp:include page="searchResults.jsp" />
				<p class="pull-right move-to-top">
					<a href="#"><bean:message key="labels.footer_back_to_top" /></a>
				</p>
			</c:when>
			<c:otherwise>
				<jsp:include page="searchNoResult.jsp" />
			</c:otherwise>
		</c:choose>
		<jsp:include page="footer.jsp" />
	</div>
	<input type="hidden" id="contextPath" value="<%=request.getContextPath()%>" />
	<script type="text/javascript"
		src="${f:url('/js/jquery-1.11.0.min.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/bootstrap.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/suggestor.js')}"></script>
	<script type="text/javascript" src="${f:url('/js/search.js')}"></script>
</body>
</html>
