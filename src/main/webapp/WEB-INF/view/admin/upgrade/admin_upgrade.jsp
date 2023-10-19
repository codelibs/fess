<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title><la:message key="labels.admin_brand_title" /> | <la:message
			key="labels.upgrade_title_configuration" /></title>
	<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="upgrade" />
		</jsp:include>
		<div class="content-wrapper">
			<div class="content-header">
				<div class="container-fluid">
					<div class="row mb-2">
						<div class="col-sm-6">
							<h1>
								<la:message key="labels.upgrade_title_configuration" />
							</h1>
						</div>
					</div>
				</div>
			</div>
			<section class="content">
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
						<div class="card card-outline card-primary">
							<div class="card-header">
								<h3 class="card-title">
									<la:message key="labels.upgrade_data_migration" />
								</h3>
							</div>
							<div class="card-body">
								<div class="form-group row">
									<label for="targetVersion" class="col-sm-3 text-sm-right col-form-label"><la:message
											key="labels.target_version"
										/></label>
									<div class="col-sm-9">
										<la:errors property="targetVersion" />
										<la:select styleId="targetVersion" property="targetVersion" styleClass="form-control">
											<la:option value="13.0">13.0</la:option>
											<la:option value="13.1">13.1</la:option>
											<la:option value="13.2">13.2</la:option>
											<la:option value="13.3">13.3</la:option>
											<la:option value="13.4">13.4</la:option>
											<la:option value="13.5">13.5</la:option>
											<la:option value="13.6">13.6</la:option>
											<la:option value="13.7">13.7</la:option>
											<la:option value="13.8">13.8</la:option>
											<la:option value="13.9">13.9</la:option>
											<la:option value="13.10">13.10</la:option>
											<la:option value="13.11">13.11</la:option>
											<la:option value="13.12">13.12</la:option>
											<la:option value="13.13">13.13</la:option>
											<la:option value="13.14">13.14</la:option>
											<la:option value="13.15">13.15</la:option>
											<la:option value="13.16">13.16</la:option>
											<la:option value="13.17">13.17</la:option>
											<la:option value="14.0">14.0</la:option>
											<la:option value="14.1">14.1</la:option>
											<la:option value="14.2">14.2</la:option>
											<la:option value="14.3">14.3</la:option>
											<la:option value="14.4">14.4</la:option>
											<la:option value="14.5">14.5</la:option>
											<la:option value="14.6">14.6</la:option>
											<la:option value="14.7">14.7</la:option>
											<la:option value="14.8">14.8</la:option>
											<la:option value="14.9">14.9</la:option>
											<la:option value="14.10">14.10</la:option>
										</la:select>
									</div>
								</div>
							</div>
							<div class="card-footer">
								<button type="submit" class="btn btn-primary ${f:h(editableClass)}" name="upgradeFrom"
									value="<la:message key="labels.upgrade_start_button"/>"
								>
									<em class="fa fa-arrow-circle-right"></em>
									<la:message key="labels.upgrade_start_button" />
								</button>
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
