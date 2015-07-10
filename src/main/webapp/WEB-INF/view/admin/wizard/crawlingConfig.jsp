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
					<bean:message key="labels.wizard_start_title" />
				</h1>
				<ol class="breadcrumb">
					<li><s:link href="/admin/wizard/">
							<bean:message key="labels.wizard_start_title" />
						</s:link></li>
					<li class="active"><bean:message key="labels.wizard_crawling_config_title" /></li>
				</ol>
			</section>

			<section class="content">

				<s:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<div class="box-header with-border">
									<h3 class="box-title">
										<bean:message key="labels.wizard_crawling_config_title" />
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
										<label for="crawlingConfigName"><bean:message key="labels.wizard_crawling_config_name" /></label>
										<html:text property="crawlingConfigName" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="crawlingConfigPath"><bean:message key="labels.wizard_crawling_config_path" /></label>
										<html:text property="crawlingConfigPath" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="maxAccessCount"><bean:message key="labels.maxAccessCount" /></label>
										<html:text property="maxAccessCount" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="depth"><bean:message key="labels.depth" /></label>
										<html:text property="depth" styleClass="form-control" />
									</div>
								</div>
								<div class="box-footer">
									<input type="submit" class="btn" name="index" value="<bean:message key="labels.wizard_button_cancel"/>" /> <input
										type="submit" class="btn" name="startCrawlingForm" value="<bean:message key="labels.wizard_button_skip"/>"
									/> <input type="submit" class="btn btn-primary" name="crawlingConfig"
										value="<bean:message key="labels.wizard_button_register_again"/>"
									/> <input type="submit" class="btn btn-primary" name="crawlingConfigNext"
										value="<bean:message key="labels.wizard_button_register_next"/>"
									/>
								</div>
							</div>
						</div>
					</div>
				</s:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>

	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>
