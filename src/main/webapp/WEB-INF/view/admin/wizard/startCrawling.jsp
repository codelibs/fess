<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.wizard_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="wizard" />
		</jsp:include>

		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<bean:message key="labels.wizard_start_crawling_title" />
				</h1>
				<ol class="breadcrumb">
					<li><s:link href="/admin/wizard/">Start</s:link></li>
					<li><bean:message key="labels.wizard_crawling_config_title" /></li>
					<li class="active"><bean:message key="labels.wizard_start_crawling_title" /></li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<s:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<div class="box-header with-border">
									<h3 class="box-title">Start Crawling</h3>
								</div>
								<div class="box-body">
									<div class="row">
										<div class="col-md-8">
											<%-- Message: BEGIN --%>
											<div>
												<html:messages id="msg" message="true">
													<div class="alert-message info">
														<bean:write name="msg" ignore="true" />
													</div>
												</html:messages>
												<html:errors />
											</div>
											<%-- Message: END --%>

											<p class="span8">
												<bean:message key="labels.wizard_start_crawling_desc" />
											</p>
										</div>
									</div>
								</div>
								<div class="box-footer">
									<input type="submit" class="btn btn-primary" name="startCrawling"
										value="<bean:message key="labels.wizard_button_start_crawling"/>"
									/> <input type="submit" class="btn" name="index" value="<bean:message key="labels.wizard_button_finish"/>" />
								</div>
							</div>
						</div>
					</div>
				</s:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>

	</div>

	<script src="${f:url('/js/admin2/jQuery-2.1.4.min.js')}" type="text/javascript"></script>
	<script src="${f:url('/js/admin2/bootstrap.min.js')}" type="text/javascript"></script>
	<script src="${f:url('/js/admin2/app.min.js')}" type="text/javascript"></script>
</body>
</html>
