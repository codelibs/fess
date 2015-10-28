<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.wizard_title_configuration" /></title>
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
					<la:message key="labels.wizard_crawling_config_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/wizard/">
							<la:message key="labels.wizard_start_title" />
						</la:link></li>
					<li class="active"><la:message
							key="labels.wizard_crawling_config_title" /></li>
				</ol>
			</section>

			<section class="content">

				<la:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.wizard_crawling_setting_title" />
									</h3>
								</div>
								<div class="box-body">

									<%-- Message: BEGIN --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert-message info">${msg}</div>
										</la:info>
										<la:errors />
									</div>
									<%-- Message: END --%>

									<div class="form-group">
										<label for="crawlingConfigName"><la:message
												key="labels.wizard_crawling_config_name" /></label>
										<la:text property="crawlingConfigName"
											styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="crawlingConfigPath"><la:message
												key="labels.wizard_crawling_config_path" /></label>
										<la:text property="crawlingConfigPath"
											styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="maxAccessCount"><la:message
												key="labels.maxAccessCount" /></label>
										<la:text property="maxAccessCount" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="depth"><la:message key="labels.depth" /></label>
										<la:text property="depth" styleClass="form-control" />
									</div>
								</div>
								<div class="box-footer">
									<button type="submit" class="btn" name="index"
										value="<la:message key="labels.wizard_button_cancel"/>">
										<la:message key="labels.wizard_button_cancel" />
									</button>
									<button type="submit" class="btn btn-primary"
										name="crawlingConfig"
										value="<la:message key="labels.wizard_button_register_again"/>">
										<la:message key="labels.wizard_button_register_again" />
									</button>
									<button type="submit" class="btn btn-success"
										name="crawlingConfigNext"
										value="<la:message key="labels.wizard_button_register_next"/>">
										<la:message key="labels.wizard_button_register_next" />
									</button>
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
