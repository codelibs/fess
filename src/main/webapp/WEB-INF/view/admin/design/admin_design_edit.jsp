<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.design_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="design" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.design_configuration" />
				</h1>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div>
							<la:info id="msg" message="true">
								<div class="alert-message info">${msg}</div>
							</la:info>
							<la:errors />
						</div>
						<div class="box box-primary">
							<c:if test="${editable}">
								<la:form action="/admin/design/">
									<%-- Box Header --%>
									<div class="box-header with-border">
										<h3>
											<la:message key="labels.design_title_edit_content" />
										</h3>
									</div>
									<%-- Box Body --%>
									<div class="box-body">
										<h4>${f:h(fileName)}</h4>
										<div>
											<la:textarea property="content" rows="20"
												styleClass="form-control"></la:textarea>
										</div>
									</div>
									<%-- Box Footer --%>
									<div class="box-footer">
										<button type="submit" class="btn btn-default" name="back"
											value="<la:message key="labels.design_button_back" />">
											<la:message key="labels.design_button_back" />
										</button>
										<button type="submit" class="btn btn-warning" name="update"
											value="<la:message key="labels.design_button_update" />">
											<la:message key="labels.design_button_update" />
										</button>
									</div>
									<la:hidden property="fileName" />
								</la:form>
							</c:if>
						</div>
					</div>
				</div>

			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
