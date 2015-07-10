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

		<!-- Content Wrapper. Contains page content -->
		<div class="content-wrapper">
			<!-- Content Header (Page header) -->
			<section class="content-header">
				<h1>
					<bean:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
					<li class="active">Here</li>
				</ol>
			</section>

			<!-- Main content -->
			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<div class="box-header with-border">
								<h3 class="box-title">Start Configuration Wizard</h3>
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
										<s:form>
											<div>
												<p class="span8">
													<bean:message key="labels.wizard_start_desc" />
												</p>
												<div class="span8">
													<input type="submit" name="crawlingConfigForm" class="btn small"
														value="<bean:message key="labels.wizard_start_button"/>" />
													</td>
												</div>
											</div>
										</s:form>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>

	</div>

	<script src="${f:url('/js/admin2/jQuery-2.1.4.min.js')}" type="text/javascript"></script>
	<script src="${f:url('/js/admin2/bootstrap.min.js')}" type="text/javascript"></script>
	<script src="${f:url('/js/admin2/app.min.js')}" type="text/javascript"></script>
</body>
</html>
