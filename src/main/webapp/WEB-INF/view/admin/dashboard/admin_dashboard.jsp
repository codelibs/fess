<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.dashboard_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="dashboard" />
			<jsp:param name="menuType" value="dashboard" />
		</jsp:include>

		<div id="content" class="content-wrapper">
			<iframe id="contentFrame" src="<%=request.getContextPath()%>${serverPath}<%= response.encodeURL("/_plugin/kopf/") %>" seamless></iframe>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
	<script src="${fe:url('/js/admin/dashboard.js')}" type="text/javascript"></script>
</body>
</html>
