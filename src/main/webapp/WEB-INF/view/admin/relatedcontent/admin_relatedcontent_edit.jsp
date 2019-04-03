<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.related_content_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="relatedContent" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.related_content_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/relatedcontent/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group">
										<label for="term" class="col-sm-3 control-label"><la:message
												key="labels.related_content_term" /></label>
										<div class="col-sm-9">
											<la:errors property="term" />
											<la:text styleId="term" property="term" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="content" class="col-sm-3 control-label"><la:message
												key="labels.related_content_content" /></label>
										<div class="col-sm-9">
											<la:errors property="content" />
											<la:textarea styleId="content" property="content" styleClass="form-control" rows="5"/>
										</div>
									</div>
									<div class="form-group">
										<label for="sortOrder" class="col-sm-3 control-label"><la:message
												key="labels.sortOrder" /></label>
										<div class="col-sm-9">
											<la:errors property="sortOrder" />
											<la:text styleId="sortOrder" property="sortOrder" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="virtualHost" class="col-sm-3 control-label"><la:message
												key="labels.virtual_host" /></label>
										<div class="col-sm-9">
											<la:errors property="virtualHost" />
											<la:text styleId="virtualHost" property="virtualHost" styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
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
