<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
	<head>
	<meta charset="UTF-8">
	<title><la:message key="labels.admin_brand_title" /> | <la:message
			key="labels.sereq_configuration" /></title>
	<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
	<div class="fads-layout-wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="sereq" />
		</jsp:include>
		<main class="fads-main-content">
			<div class="fads-page-header">
				<div >
					<div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
						<div class="fads-col-sm-6">
							<h1>
								<la:message key="labels.sereq_configuration" />
							</h1>
						</div>
					</div>
				</div>
			</div>
			<section class="content">
				<la:form action="/admin/sereq/upload/" enctype="multipart/form-data">
					<div class="fads-col-md-12">
						<la:info id="msg" message="true">
							<div class="fads-banner fads-banner-info">${msg}</div>
						</la:info>
						<la:errors />
					</div>
					<div class="fads-col-md-12">
						<div class="fads-card">
							<div class="fads-card-header">
								<h3 class="fads-card-title">
									<la:message key="labels.sereq_configuration" />
								</h3>
							</div>
							<div class="fads-card-body">
								<div class="fads-form-field">
									<label for="requestFile" class="fads-label"><la:message
											key="labels.sereq_request_file"
										/></label>
									<div class="fads-col-sm-9">
										<input id="requestFile" type="file" name="requestFile" class="fads-textfield" />
									</div>
								</div>
							</div>
							<div class="fads-card-footer">
								<c:if test="${editable}">
									<button type="submit" class="fads-btn fads-btn-primary" name="upload">
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

