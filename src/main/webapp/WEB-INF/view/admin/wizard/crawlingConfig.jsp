<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.wizard_title_configuration" /></title>
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
					<la:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/wizard/">
							<la:message key="labels.wizard_start_title" />
						</la:link></li>
					<li class="active"><la:message key="labels.wizard_crawling_config_title" /></li>
				</ol>
			</section>

			<section class="content">

				<la:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.wizard_crawling_config_title" />
									</h3>
								</div>
								<div class="box-body">

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

									<div class="form-group">
										<label for="crawlingConfigName"><la:message key="labels.wizard_crawling_config_name" /></label>
										<la:text property="crawlingConfigName" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="crawlingConfigPath"><la:message key="labels.wizard_crawling_config_path" /></label>
										<la:text property="crawlingConfigPath" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="maxAccessCount"><la:message key="labels.maxAccessCount" /></label>
										<la:text property="maxAccessCount" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="depth"><la:message key="labels.depth" /></label>
										<la:text property="depth" styleClass="form-control" />
									</div>
								</div>
								<div class="box-footer">
									<input type="submit" class="btn" name="index" value="<la:message key="labels.wizard_button_cancel"/>" /> <input
										type="submit" class="btn" name="startCrawlingForm" value="<la:message key="labels.wizard_button_skip"/>"
									/> <input type="submit" class="btn btn-primary" name="crawlingConfig"
										value="<la:message key="labels.wizard_button_register_again"/>"
									/> <input type="submit" class="btn btn-primary" name="crawlingConfigNext"
										value="<la:message key="labels.wizard_button_register_next"/>"
									/>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>

	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
