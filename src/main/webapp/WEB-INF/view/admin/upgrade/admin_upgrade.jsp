<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.upgrade_title_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="wizard" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.upgrade_title_configuration" />
				</h1>
			</section>
			<section class="content">
				<div class="row">
					<la:form action="/admin/upgrade/">
						<%-- Message: BEGIN --%>
						<div class="col-md-12">
							<la:info id="msg" message="true">
								<div class="alert alert-info">${msg}</div>
							</la:info>
							<la:errors />
						</div>
						<%-- Message: END --%>
						<div class="col-md-12">
							<div class="box box-primary">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.upgrade_data_migration" />
									</h3>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div class="form-group">
										<label for="targetVersion" class="col-sm-3 control-label"><la:message key="labels.target_version" /></label>
										<div class="col-sm-9">
											<la:errors property="targetVersion" />
											<la:select styleId="targetVersion" property="targetVersion" styleClass="form-control">
												<la:option value="12.0">12.0</la:option>
												<la:option value="12.1">12.1</la:option>
												<la:option value="12.2">12.2</la:option>
												<la:option value="12.3">12.3</la:option>
											</la:select>
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-primary"
										name="upgradeFrom"
										value="<la:message key="labels.upgrade_start_button"/>">
										<i class="fa fa-arrow-circle-right"></i>
										<la:message key="labels.upgrade_start_button" />
									</button>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
						</div>
					</la:form>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
