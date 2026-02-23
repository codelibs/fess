<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
	<head>
	<meta charset="UTF-8">
	<title><la:message key="labels.admin_brand_title" /> | <la:message
			key="labels.sereq_configuration" /></title>
	<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="sereq" />
		</jsp:include>
		<main class="content-wrapper">
			<div class="content-header">
				<div class="container-fluid">
					<div class="row mb-2">
						<div class="col-sm-6">
							<h1>
								<la:message key="labels.sereq_configuration" />
							</h1>
						</div>
					</div>
				</div>
			</div>
			<section class="content">
				<la:form action="/admin/sereq/upload/" enctype="multipart/form-data">
					<div class="col-md-12">
						<la:info id="msg" message="true">
							<div class="alert alert-success">${msg}</div>
						</la:info>
						<la:errors />
					</div>
					<div class="col-md-12">
						<div class="card card-outline card-primary">
							<div class="card-header">
								<h3 class="card-title">
									<la:message key="labels.sereq_configuration" />
								</h3>
							</div>
							<div class="card-body">
								<div class="form-group row">
									<label for="requestFile" class="col-sm-3 text-sm-right col-form-label"><la:message
											key="labels.sereq_request_file"
										/></label>
									<div class="col-sm-9">
										<input id="requestFile" type="file" name="requestFile" class="form-control-file" />
									</div>
								</div>
							</div>
							<div class="card-footer">
								<c:if test="${editable}">
									<button type="submit" class="btn btn-primary" name="upload">
										<i class="fa fa-upload" aria-hidden="true"></i>
										<la:message key="labels.sereq_button_upload" />
									</button>
								</c:if>
							</div>
						</div>
					</div>
				</la:form>
			</section>
		</main>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}

