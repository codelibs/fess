<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.dict_userdict_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>

		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<bean:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="/admin/dict/">
							<bean:message key="labels.dict_userdict_link_list" />
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
							<bean:message key="labels.dict_userdict_button_back" />
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
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp" flush="true">
	<tiles:put name="title"><bean:message key="labels.dict_userdict_configuration" /></tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

      <div id="main">

<html:errors/>
<br/>
<s:link href="../index"><bean:message key="labels.dict_button_back"/></s:link>

      </div>

	</tiles:put>
</tiles:insert>
