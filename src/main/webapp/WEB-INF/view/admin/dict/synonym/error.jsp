<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.dict_synonym_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>

		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.wizard_start_title" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>

			<section class="content">

				<div class="callout callout-danger lead">
					<h4>Error</h4>
					<p>
						<la:errors />
					</p>
					<p>
						<la:link href="/admin/dict">
							<la:message key="labels.dict_synonym_button_back" />
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
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tilela:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tilela:put name="title"><la:message key="labels.dict_synonym_configuration" /></tilela:put>
	<tilela:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tilela:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tilela:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tilela:put name="menuType" value="dict" />
	<tilela:put name="headerScript" type="string"></tilela:put>
	<tilela:put name="body" type="string">

      <div id="main">

<la:errors/>
<br/>
<la:link href="/admin/dict"><la:message key="labels.dict_button_back"/></la:link>

      </div>

	</tilela:put>
</tilela:insert>
