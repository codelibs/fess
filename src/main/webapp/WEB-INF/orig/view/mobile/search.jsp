<%@page pageEncoding="UTF-8" %>
<html>
<head>
<jsp:include page="searchHtmlHead.jsp"/>
</head>
<body>
<jsp:include page="searchHeader.jsp"/>
<c:choose>
<c:when test="${f:h(allRecordCount) != 0}">
<jsp:include page="searchResults.jsp"/>
</c:when>
<c:otherwise>
<jsp:include page="searchNoResult.jsp"/>
</c:otherwise>
</c:choose>
<jsp:include page="searchFooter.jsp"/>
</body>
</html>
