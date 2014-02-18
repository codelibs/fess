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
			<c:if test="${fe:hswsize(null) != 0}">
				<div class="span12">
					<p class="hotSearchWordBody ellipsis">
						<bean:message key="labels.search_hot_search_word" />
						<c:forEach var="item" items="${fe:hsw(null, 5)}">
							<html:link href="search?query=${f:u(item)}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">${f:h(item)}</html:link>
						</c:forEach>
					</p>
				</div>
			</c:if>
		</div>
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
