<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message key="labels.search_list_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="searchList" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.search_list_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/searchlist/search?q=${f:u(q)}">
							<la:message key="labels.search_list_configuration" />
						</la:link></li>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/searchlist/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<la:hidden property="q" />
					<c:if test="${crudMode==2}">
						<la:hidden property="docId" />
						<la:hidden property="id" />
						<la:hidden property="version" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == null}">
											<la:message key="labels.crud_title_list" />
										</c:if>
										<c:if test="${crudMode == 1}">
											<la:message key="labels.crud_title_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.crud_title_edit" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.crud_title_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.crud_title_details" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<c:choose>
											<c:when test="${crudMode == null}">
												<la:link href="createnew" styleClass="btn btn-success btn-xs">
													<i class="fa fa-plus"></i>
													<la:message key="labels.crud_link_create" />
												</la:link>
											</c:when>
											<c:otherwise>
												<la:link href="/admin/searchlist/search?q=${f:u(q)}" styleClass="btn btn-primary btn-xs">
													<i class="fa fa-th-list"></i>
													<la:message key="labels.crud_link_list" />
												</la:link>
											</c:otherwise>
										</c:choose>
									</div>
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
										<label for="doc_id" class="col-sm-3 control-label">doc_id</label>
										<div class="col-sm-9">
											<la:errors property="doc.doc_id" />
											<la:text property="doc.doc_id" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="url" class="col-sm-3 control-label">url</label>
										<div class="col-sm-9">
											<la:errors property="doc.url" />
											<la:text property="doc.url" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="title" class="col-sm-3 control-label">title</label>
										<div class="col-sm-9">
											<la:errors property="doc.title" />
											<la:text property="doc.title" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="role" class="col-sm-3 control-label">role</label>
										<div class="col-sm-9">
											<la:errors property="doc.role" />
											<la:textarea property="doc.role" styleClass="form-control" />
										</div>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn btn-default" name="search" value="<la:message key="labels.crud_button_back" />">
											<i class="fa fa-arrow-circle-left"></i>
											<la:message key="labels.crud_button_back" />
										</button>
										<button type="submit" class="btn btn-success" name="create"
											value="<la:message key="labels.crud_button_create" />"
										>
											<i class="fa fa-plus"></i>
											<la:message key="labels.crud_button_create" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn btn-default" name="search" value="back">
											<i class="fa fa-arrow-circle-left"></i>
											<la:message key="labels.crud_button_back" />
										</button>
										<button type="submit" class="btn btn-warning" name="update"
											value="<la:message key="labels.crud_button_update" />"
										>
											<i class="fa fa-pencil"></i>
											<la:message key="labels.crud_button_update" />
										</button>
									</c:if>
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
