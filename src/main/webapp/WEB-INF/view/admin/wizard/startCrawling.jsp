<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.wizard_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="wizard" />
		</jsp:include>

		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.wizard_start_crawling_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/wizard/">
							<la:message key="labels.wizard_start_title" />
						</la:link></li>
					<li><la:message key="labels.wizard_crawling_config_title" /></li>
					<li class="active"><la:message key="labels.wizard_start_crawling_title" /></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<la:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<div class="box-header with-border">
									<h3 class="box-title">Start Crawling</h3>
								</div>
								<div class="box-body">
									<%-- Message: BEGIN --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert-message info">
												${msg}
											</div>
										</la:info>
										<la:errors />
									</div>
									<%-- Message: END --%>

									<p class="span8">
										<la:message key="labels.wizard_start_crawling_desc" />
									</p>
								</div>
								<div class="box-footer">
									<input type="submit" class="btn btn-primary" name="startCrawling"
										value="<la:message key="labels.wizard_button_start_crawling"/>"
									/> <input type="submit" class="btn" name="index" value="<la:message key="labels.wizard_button_finish"/>" />
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>

	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
