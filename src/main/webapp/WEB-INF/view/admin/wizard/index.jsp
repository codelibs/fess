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
					<la:message key="labels.wizard_title_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/wizard/">
							<la:message key="labels.wizard_start_title" />
						</la:link></li>
				</ol>
			</section>

			<section class="content">

				<s:form>
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.wizard_start_title" />
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
									<p>
										<la:message key="labels.wizard_start_desc" />
									</p>
								</div>
								<div class="box-footer">
									<input type="submit" name="crawlingConfigForm" class="btn btn-primary"
										value="<la:message key="labels.wizard_start_button"/>"
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
