<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.path_mapping_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="pathMapping" />
		</jsp:include>
		
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<bean:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="/admin/pathMapping/">
							<bean:message key="labels.path_mapping_link_list" />
						</s:link></li>
				</ol>
			</section>

			<section class="content">
				<div class="callout callout-danger lead">
					<h4>Error</h4>
					<p>
						<html:errors />
					</p>
					<p>
						<s:link href="index">
							<bean:message key="labels.path_mapping_button_back" />
						</s:link>
					</p>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>

	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html> 
