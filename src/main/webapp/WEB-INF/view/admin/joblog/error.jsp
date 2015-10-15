<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.joblog_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="jobLog" />
		</jsp:include>

		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/jobLog/">
							<la:message key="labels.joblog_link_list" />
						</la:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="callout callout-danger lead">
					<h4>Error</h4>
					<p>
						<la:errors />
					</p>
					<p>
						<la:link href="index">
							<la:message key="labels.joblog_button_back" />
						</la:link>
					</p>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>

	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
