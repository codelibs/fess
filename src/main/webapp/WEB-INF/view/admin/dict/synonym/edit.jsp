<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_synonym_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>
		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_synonym_title" />
				</h1>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<la:hidden property="dictId" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div class="box box-primary">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.dict_synonym_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.dict_synonym_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"> <la:link
												href="../list">
												<la:message key="labels.dict_list_link" />
											</la:link>
										</span> <span class="label label-default"> <la:link
												href="list/1?dictId=${f:u(dictId)}">
												<la:message key="labels.dict_synonym_list_link" />
											</la:link>
										</span>
										<c:if test="${crudMode == 1}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_synonym_link_create" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 2}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_synonym_link_update" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 3}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_synonym_link_delete" />
											</a>
											</span>
										</c:if>
										<c:if test="${crudMode == 4}">
											<span class="label label-default"> <a href="#"> <la:message
														key="labels.dict_synonym_link_confirm" />
											</a>
											</span>
										</c:if>
										<span class="label label-default"> <la:link
												href="downloadpage/${f:u(dictId)}">
												<la:message key="labels.dict_synonym_link_download" />
											</la:link>
										</span> <span class="label label-default"> <la:link
												href="uploadpage/${f:u(dictId)}">
												<la:message key="labels.dict_synonym_link_upload" />
											</la:link>
										</span>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<div class="form-group">
										<label for="term"><la:message
												key="labels.dict_synonym_source" /></label>
										<la:textarea property="inputs" rows="5"
											styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="outputs"><la:message
												key="labels.dict_synonym_target" /></label>
										<la:textarea property="outputs" rows="5"
											styleClass="form-control" />
									</div>
								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_synonym_button_back" />">
											<la:message key="labels.dict_synonym_button_back" />
										</button>
										<button type="submit" class="btn btn-success"
											name="confirmfromcreate"
											value="<la:message key="labels.dict_synonym_button_create" />">
											<la:message key="labels.dict_synonym_button_create" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.dict_synonym_button_back" />">
											<la:message key="labels.dict_synonym_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="confirmfromupdate"
											value="<la:message key="labels.dict_synonym_button_confirm" />">
											<la:message key="labels.dict_synonym_button_confirm" />
										</button>
									</c:if>
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
